package co.com.panificadoracountry;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import co.com.BusinessObject.DataBaseBO;
import co.com.DataObject.ItemListView;
import co.com.DataObject.Producto;
import co.com.DataObject.Usuario;

import co.com.woosim.printer.WoosimR240;

@SuppressLint("InflateParams")
public class FormCargueInventarioSugerido extends Activity implements DatePickerDialog.OnDateSetListener {

	/**
	 * Objeto que permite la conexion a la impresora.
	 */
	private WoosimR240 wR240 = null;

	ProgressDialog progressDialog;

	/**
	 * variable global de la clase
	 */
	private long mLastClickTime = 0;

	/**
	 * Fimulario principal para solictar productos.
	 */
	private View formPrincipal;

	/**
	 * Formulario para mostrar los productos solicitados.
	 */
	private View formVerPedido;

	/**
	 * EditText que contiene el parametro de busqueda ingresado por el usuario.
	 */
	private EditText editTextParametroBusqueda;

	/**
	 * ListView que contiene la lista de productos para seleccionar y cargar en
	 * el inventario.
	 */
	private ListView listViewCargueInventarioSugerido;

	/**
	 * ListView que contiene la lista de producto que han sido pedidos al
	 * momento.
	 */
	private ListView listViewInventarioSugerido;

	/**
	 * lista de items que seran mostrados en el listView
	 */
	ArrayList<ItemListView> listaItems;
	{
		listaItems = new ArrayList<ItemListView>();
	}

	/**
	 * Lista de los productos disponibles.
	 */
	private ArrayList<Producto> listaProductos;
	{
		listaProductos = new ArrayList<Producto>();
	}

	/**
	 * lista de items que seran mostrados en el listView de pedidos
	 */
	ArrayList<ItemListView> listaItemsPedidos;
	{
		listaItemsPedidos = new ArrayList<ItemListView>();
	}

	/**
	 * Lista de los productos pedidos
	 */
	private ArrayList<Producto> listaProductosPedidos;
	{
		listaProductosPedidos = new ArrayList<Producto>();
	}

	/**
	 * Contiene la cantidad de producto a ser sugerido para el inventario.
	 */
	private EditText editTextCantidad;

	/**
	 * Contiene la fecha de asignacion definida para el inventario sugerido
	 */
	private EditText editTextFechaSugerido;

	/**
	 * Buton para evento de aceptar la cantidad ingresada
	 */
	private Button buttonAceptarCantidad;

	/**
	 * botoon para cancelar el dialog.
	 */
	private Button buttonCancelarCantidad;

	/**
	 * Dialog usado para pedir la cantidad de productos solicitados.
	 */
	private Dialog dialogPedirCantidad;

	/**
	 * Dialog para cargar el datepicker para capturar la fecha del sugerido
	 */
	private Dialog dialogTime = null;

	/**
	 * posicion del producto seleccionado
	 */
	private int posicion = 0;

	/**
	 * boton para imprimir sugerido
	 */
	private ImageButton buttonImprSugerido;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_cargue_inventario_sugerido);

		/* instanciar formularios disponibles. (Merge) */
		formPrincipal = FormCargueInventarioSugerido.this.findViewById(R.id.relativeLayoutPrincipal);
		formVerPedido = FormCargueInventarioSugerido.this.findViewById(R.id.relativeLayoutVerCargueSugerido);
		formPrincipal.setVisibility(View.VISIBLE);
		formVerPedido.setVisibility(View.GONE);

		/* verificar si ya ha hecho un sugerido del dia. */
		verificarSugeridoDelDia();

		/* Instanciar views */
		editTextFechaSugerido = (EditText) FormCargueInventarioSugerido.this.findViewById(R.id.editTextFechaSugerido);
		editTextParametroBusqueda = (EditText) FormCargueInventarioSugerido.this
				.findViewById(R.id.editTextParametroBusqueda);
		editTextParametroBusqueda.setOnEditorActionListener(listenerImeOPtion);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		listViewCargueInventarioSugerido = (ListView) FormCargueInventarioSugerido.this
				.findViewById(R.id.listViewCargueInventarioSugerido);
		listViewInventarioSugerido = (ListView) FormCargueInventarioSugerido.this
				.findViewById(R.id.listViewInventarioSugerido);

		/* Llenar la lista inicial de productos disponibles */
		cargarListaDeProductosParaCargue("");
	}

	/**
	 * Verificar si el usuario ya ha realizado un inventario sugerido el dia de
	 * hoy
	 */
	private void verificarSugeridoDelDia() {
		boolean sugerido = DataBaseBO.verificarSugeridoDelDia();
		if (sugerido) {
			mostrarInformandoSugeridoRealizado();
		}
	}

	/**
	 * Listener para detectar si el usuario presiona el boton de busqueda del
	 * teclado e iniciar la busqueda. visitar:
	 * https://developer.android.com/training/keyboard-input/style.html
	 * (imeOptions de EditText)
	 */
	private OnEditorActionListener listenerImeOPtion = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			boolean handle = false;
			if (actionId == EditorInfo.IME_ACTION_SEARCH) {
				handle = true;
				onClickBuscarProducto(null);
			}
			return handle;
		}
	};

	/**
	 * Listener para detectar que usuario a elegido un producto para agregar a
	 * la lista de pedidos.
	 */
	private OnItemClickListener listenerAgregar = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			boolean existe = verificarSiYaEstaPedido(position);
			if (!existe) {
				mostrarDialogPedirCantidad(position, 0);
			} else {
				/* informar si desea modificar el producto */
				mostrarAlertInformandoCambio(position);
			}
		}
	};

	/**
	 * Cargar la lista de productos que estan disponibles para llenar el cargue
	 * sugerido.
	 */
	private void cargarListaDeProductosParaCargue(String parametroBusqueda) {

		/* Llenar las listas con los datos a mostrar. */
		boolean existen = DataBaseBO.cargarProductosParaCargueSugerido(listaItems, listaProductos, parametroBusqueda);

		if (!existen) {
			Toast.makeText(this, "No hay productos con: " + parametroBusqueda, Toast.LENGTH_SHORT).show();
		}

		ListViewAdapter adapter = new ListViewAdapter(this, listaItems, R.drawable.prod, 0);
		listViewCargueInventarioSugerido.setAdapter(adapter);
		listViewCargueInventarioSugerido.setOnItemClickListener(listenerAgregar);
	}

	/**
	 * Informar al usuario si desea cambiar la cantidad del producto.
	 * 
	 * @param position
	 */
	protected void mostrarAlertInformandoCambio(final int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(FormCargueInventarioSugerido.this);
		builder.setTitle("¿Modificar?");
		builder.setMessage("Puede modificar la cantidad o eliminar el producto ingresado.");
		builder.setIcon(R.drawable.alert);
		builder.setPositiveButton("Modificar", new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Producto p = listaProductos.get(position);
				Producto pedido = listaProductosPedidos.get(listaProductosPedidos.indexOf(p));
				int candidad = pedido.cantidad;
				mostrarDialogPedirCantidad(position, candidad);
			}
		});

		builder.setNegativeButton("Cancelar", new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});

		builder.setNeutralButton("Eliminar", new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Producto p = listaProductos.get(position);
				listaProductosPedidos.remove(p);
				dialog.dismiss();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Recorrer la lista de productos pedidos para verificar si el producto
	 * actual ya esta en la lista.
	 * 
	 * @param position
	 * @return
	 */
	protected boolean verificarSiYaEstaPedido(int position) {
		boolean existe = false;
		Producto producto = listaProductos.get(position);
		for (Producto p : listaProductosPedidos) {
			if (p.codigo.equals(producto.codigo)) {
				existe = true;
				break;
			}
		}
		return existe;
	}

	/**
	 * Listener para captura de eventos de botones del dialog para ingresar la
	 * cantidad.
	 */
	private android.view.View.OnClickListener listenerButtonsDialog = new android.view.View.OnClickListener() {

		@Override
		public void onClick(View view) {

			int id = view.getId();
			switch (id) {
			case R.id.buttonCancelarCantidad:
				dialogPedirCantidad.dismiss();
				break;

			case R.id.buttonAceptarCantidad:
				botonAgregar();
				break;

			default:
				break;
			}
		}
	};

	/**
	 * Mostrar un dialog para pedir la cantida de producto solicitado por el
	 * usuario.
	 * 
	 * @param position
	 */
	protected void mostrarDialogPedirCantidad(int position, int cantidadInicial) {

		if (dialogPedirCantidad == null) {
			dialogPedirCantidad = new Dialog(FormCargueInventarioSugerido.this);
			LayoutInflater inflater = FormCargueInventarioSugerido.this.getLayoutInflater();
			final View view = inflater.inflate(R.layout.dialog_cargue_inventario_sugerido, null);
			dialogPedirCantidad.setContentView(view);
			dialogPedirCantidad.setTitle("Registrar Cantidad Inventario Sugerido");
			dialogPedirCantidad.setCancelable(false);
			editTextCantidad = (EditText) dialogPedirCantidad.findViewById(R.id.editTextCantidadProductoSugerido);
			buttonAceptarCantidad = (Button) dialogPedirCantidad.findViewById(R.id.buttonAceptarCantidad);
			buttonAceptarCantidad.setOnClickListener(listenerButtonsDialog);
			buttonCancelarCantidad = (Button) dialogPedirCantidad.findViewById(R.id.buttonCancelarCantidad);
			buttonCancelarCantidad.setOnClickListener(listenerButtonsDialog);
		}

		/* Mostrar la cantidad inicialmente pedida */
		if (cantidadInicial > 0) {
			editTextCantidad.setText(String.valueOf(cantidadInicial));
		} else {
			editTextCantidad.setText("");
		}
		/* Asignar posision seleccionada */
		posicion = position;

		/* mostrar el dialog. */
		dialogPedirCantidad.show();
	}

	/**
	 * Permite agregar un producto a la lista de inventario sugerido
	 * 
	 * @param position
	 * @param index
	 * @param cantidad
	 */
	protected void agregarProductoSugerido(int position, int index, String cantidad) {
		Producto p = listaProductos.get(position);
		p.cantidad = Integer.parseInt(cantidad);

		if (index == -1) {
			listaProductosPedidos.add(p);
		} else {
			listaProductosPedidos.add(index, p);
		}
		cargarProductosPedidos();
	}

	/**
	 * Agregar el producto seleccionado a la lista de pedidos.
	 */
	private void botonAgregar() {

		/* calcular cantidad y guardar */
		String cantidad = editTextCantidad.getText().toString().trim();
		if (cantidad.equals("") || cantidad.equals("0")) {
			Toast.makeText(FormCargueInventarioSugerido.this, "Ingrese una cantidad valida.", Toast.LENGTH_SHORT)
					.show();
		} else {
			dialogPedirCantidad.cancel();

			/* Eliminar producto anterior si existe */
			Producto p = listaProductos.get(posicion);
			int index = listaProductosPedidos.indexOf(p);
			listaProductosPedidos.remove(p);

			/* Agregar a la lista de pedidos */
			agregarProductoSugerido(posicion, index, cantidad);
		}
	}

	/**
	 * metodo para buscar un produtos por el parametro de busqueda.
	 * 
	 * @param view
	 */
	public void onClickBuscarProducto(View view) {
		ocultarTeclado(editTextParametroBusqueda);
		String parametroBusqueda = editTextParametroBusqueda.getText().toString().trim();
		cargarListaDeProductosParaCargue(parametroBusqueda);
	}

	/**
	 * Evento para mostrar los productos pedidos al momento.
	 * 
	 * @param view
	 */
	public void onClickVerCargueSugerido(View view) {

		if (listaProductosPedidos != null && !listaProductosPedidos.isEmpty()) {
			formPrincipal.setVisibility(View.GONE);
			formVerPedido.setVisibility(View.VISIBLE);
		} else {
			Toast.makeText(FormCargueInventarioSugerido.this, "No se han solicitado productos para el inventario.",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Cargar la lista de productos pedidos en el listView.
	 */
	private void cargarProductosPedidos() {
		/* limpiar lista de intems */
		listaItemsPedidos.clear();

		/* calcular de nuevo la lista de items pedidos */
		for (Producto p : listaProductosPedidos) {
			ItemListView item = new ItemListView();
			item.titulo = p.codigo + " - " + p.descripcion;
			item.subTitulo = "Cantidad: " + p.cantidad + " -Precio: " + Util.SepararMiles(String.valueOf(p.precio))
					+ " -Med: " + p.unidadMedida;
			listaItemsPedidos.add(item);
		}
		ListViewAdapter adapter = new ListViewAdapter(this, listaItemsPedidos, R.drawable.prod, 0);
		listViewInventarioSugerido.setAdapter(adapter);
		listViewInventarioSugerido.setOnItemClickListener(listenerEditar);
	}

	/**
	 * Listener para detectar que usuario a elegido un producto de la lista de
	 * pedidos para modificar.
	 */
	private OnItemClickListener listenerEditar = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			mostrarAlertInformandoEdicion(position);
		}
	};

	/**
	 * Informar al usuario si desea cambiar la cantidad del producto.
	 * 
	 * @param position
	 */
	protected void mostrarAlertInformandoEdicion(final int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(FormCargueInventarioSugerido.this);
		builder.setTitle("¿Modificar?");
		builder.setMessage("Puede modificar la cantidad o eliminar el producto.");
		builder.setIcon(R.drawable.alert);
		builder.setPositiveButton("Modificar", new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Producto pedido = listaProductosPedidos.get(position);
				int pos = listaProductos.indexOf(pedido);
				int candidad = pedido.cantidad;
				mostrarDialogPedirCantidad(pos, candidad);
			}
		});

		builder.setNegativeButton("Cancelar", new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});

		builder.setNeutralButton("Eliminar", new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				listaProductosPedidos.remove(position);
				dialog.dismiss();
				cargarProductosPedidos();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Evento para terminar el cargue sugerido definido por el usuario.
	 * 
	 * @param view
	 */
	public void onClickTerminarCargue(View view) {
		/* retardo de 2000ms para evitar eventos de doble click. */
		if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
			return;
		}
		mLastClickTime = SystemClock.elapsedRealtime();

		/* Verificar fecha de sugerido */
		String fecha = editTextFechaSugerido.getText().toString().trim();
		if (fecha.equals("")) {
			Toast.makeText(FormCargueInventarioSugerido.this, "Ingrese la fecha del sugerido.", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		/* Verificar la lista de pedidos */
		if (listaProductosPedidos != null && !listaProductosPedidos.isEmpty()) {
			formPrincipal.setVisibility(View.GONE);
			formVerPedido.setVisibility(View.VISIBLE);
			confirmarInventarioSugerido();
		} else {
			Toast.makeText(FormCargueInventarioSugerido.this, "No se han solicitado productos para el inventario.",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * INformar al usuario que ya tiene un inventario sugerido del dia.
	 */
	private void mostrarInformandoSugeridoRealizado() {

		AlertDialog.Builder builder = new AlertDialog.Builder(FormCargueInventarioSugerido.this);
		builder.setCancelable(false);
		builder.setIcon(R.drawable.alert);
		builder.setTitle("¿Eliminar Sugerido?");
		builder.setMessage("Ya tiene un sugerido del dia.\nSe elimina el anterior para ingresar un nuevo sugerido.");
		builder.setPositiveButton("Eliminar", new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				eliminarAnteriorSugerido();
			}
		});

		builder.setNegativeButton("Cancelar", new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				FormCargueInventarioSugerido.this.finish();
			}
		});
		builder.setNeutralButton("Imprimir", new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				mostrarAlertImprimirInvenatrioSugerido();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();

	}

	/**
	 * Eliminar el sugerido que esta en la base de datos.
	 */
	protected void eliminarAnteriorSugerido() {
		DataBaseBO.eliminarInventarioSugerido();
	}

	/**
	 * Permite mostrar un dialog para pedir confirmacion al vendedor si desea
	 * enviar este inventario sugerido
	 */
	private void confirmarInventarioSugerido( ) {
		AlertDialog.Builder builder = new AlertDialog.Builder(FormCargueInventarioSugerido.this);
		builder.setCancelable(false);
		builder.setIcon(R.drawable.alert);
		builder.setTitle("¿Confirmar Sugerido?");
		builder.setMessage("Terminar el inventario sugerido");
		builder.setPositiveButton("Aceptar", new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				guardarInventarioSugerido();
			}
		});

		builder.setNegativeButton("Cancelar", new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Guardar el inventario sugerido en la base de datos.
	 */
	protected void guardarInventarioSugerido() {

		Usuario usuario = DataBaseBO.ObtenerUsuario();
		

		/* fecha del inventario sugerido */
		String fecha = validarFecha(editTextFechaSugerido.getText().toString().trim());

		if (fecha == null) {
			Util.MostrarAlertDialog(this, "La fecha no es valida.");
			return;
		}
		

		 NumeroAleatorios na = new NumeroAleatorios(1, 1000);

String idMovil =  Util.FechaActual("yyyyMMddHHmmssSSS") + na.generar();

		boolean insertado = DataBaseBO.insertarInventarioSugerido(listaProductosPedidos, usuario,idMovil, fecha);

		if (insertado) {
			Toast.makeText(FormCargueInventarioSugerido.this, "¡Guardado!", Toast.LENGTH_SHORT).show();
			mostrarAlertImprimirInvenatrioSugerido();
		} else {
			Toast.makeText(FormCargueInventarioSugerido.this, "¡Fallo!\nIntene nuevamente.", Toast.LENGTH_SHORT)
					.show();
		}
	}

	/**
	 * Informar al usuario si desea imprimir el reporte de inventario sugerido
	 * seleccionado.
	 */
	private void mostrarAlertImprimirInvenatrioSugerido() {

		AlertDialog.Builder builder = new AlertDialog.Builder(FormCargueInventarioSugerido.this);
		builder.setCancelable(false);
		builder.setIcon(R.drawable.alert);
		builder.setTitle("¿Imprimir?");
		builder.setMessage("Puede imprimir el reporte de inventario sugerido.");
		builder.setPositiveButton("Aceptar", new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				imprimirInventarioSugerido();
			}
		});

		builder.setNegativeButton("Salir", new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				FormCargueInventarioSugerido.this.finish();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Validar que la fecha sea correcta y retornar en formato valido para
	 * sqlite.
	 * 
	 * @param timeToFormat
	 * @return finalDateTime, fecha en formato valido para sqlite.
	 */
	private String validarFecha(String timeToFormat) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		String finalDateTime = null;
		Date date = null;
		if (timeToFormat != null) {
			try {
				format.setLenient(false);
				date = format.parse(timeToFormat);
			} catch (ParseException e) {
				date = null;
			}
			// dar formato valido.
			if (date != null) {
				finalDateTime = format.format(date);
			}
		}
		return finalDateTime;
	}

	/**
	 * Mostrar el formulario principal
	 * 
	 * @param view
	 */
	public void onClickAtras(View view) {
		formPrincipal.setVisibility(View.VISIBLE);
		formVerPedido.setVisibility(View.GONE);
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(FormCargueInventarioSugerido.this);
		builder.setCancelable(false);
		builder.setIcon(R.drawable.alert);
		builder.setTitle("¿Salir Sin Registrar?");
		builder.setMessage("Se perdera el inventario sugerido ingresado hasta el momento.");
		builder.setPositiveButton("Aceptar", new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				FormCargueInventarioSugerido.this.finish();
			}
		});

		builder.setNegativeButton("Cancelar", new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Metodo para ocultar el teclado despues de un evento onClick
	 */
	private void ocultarTeclado(EditText editText) {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	/**
	 * Inciar la captura de la fecha para el sugerido.
	 * 
	 * @param view
	 */
	public void cargarFechaSugerido(View view) {
		// Use the current date as the default date in the picker
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int monthOfYear = c.get(Calendar.MONTH);
		int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);

		/* verificar si no se ha iniciado un dialog antes */
		if (dialogTime == null) {
			dialogTime = new DatePickerDialog(this, this, year, monthOfYear, dayOfMonth);
		}
		dialogTime.show();
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		/* retardo de 1500ms para evitar eventos de doble click. */
		if (SystemClock.elapsedRealtime() - mLastClickTime < 1500) {
			return;
		}
		mLastClickTime = SystemClock.elapsedRealtime();

		/* Cargar fecha seleccionada */
		Calendar fechaSelecionada = Calendar.getInstance();
		fechaSelecionada.set(year, monthOfYear, dayOfMonth);

		/* Cargar fecha actual */
		Calendar fechaActual = Calendar.getInstance();

		/* Validar que la fecha sea mayor a la actual. */
		if (fechaSelecionada.before(fechaActual) || fechaSelecionada.equals(fechaActual)) {
			String titulo = "Error Fecha";
			String mensaje = "Intente ingresar la fecha de mañana";
			mostrarAlertDialog(titulo, mensaje);
			dialogTime = new DatePickerDialog(this, this, fechaActual.get(Calendar.YEAR),
					fechaActual.get(Calendar.MONTH), fechaActual.get(Calendar.DAY_OF_MONTH));
			return;
		}
		/* Validar el maximo de 5 dias . */
		fechaActual.add(Calendar.DATE, 5);

		/* No puede ser asignado un dia domingo para fecha de sugerido */
		if (fechaSelecionada.after(fechaActual)) {
			String titulo = "Error Fecha";
			String mensaje = "No ingresar fecha mayor a 5 dias.";
			mostrarAlertDialog(titulo, mensaje);
			fechaActual.add(Calendar.DATE, -5);
			dialogTime = new DatePickerDialog(this, this, fechaActual.get(Calendar.YEAR),
					fechaActual.get(Calendar.MONTH), fechaActual.get(Calendar.DAY_OF_MONTH));
			return;
		} 
//		else if (fechaSelecionada.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
//			String titulo = "Domingo";
//			String mensaje = "Intente ingresar la fecha del lunes.";
//			mostrarAlertDialog(titulo, mensaje);
//			fechaActual.add(Calendar.DATE, -5);
//			dialogTime = new DatePickerDialog(this, this, fechaActual.get(Calendar.YEAR),
//					fechaActual.get(Calendar.MONTH), fechaActual.get(Calendar.DAY_OF_MONTH));
//			return;
//		} 
		else {
			String fecha = String.valueOf(year) + "-"
					+ (((monthOfYear + 1) < 10) ? ("0" + String.valueOf(monthOfYear + 1))
							: String.valueOf(monthOfYear + 1))
					+ "-" + ((dayOfMonth < 10) ? ("0" + String.valueOf(dayOfMonth)) : String.valueOf(dayOfMonth));
			editTextFechaSugerido.setText(fecha);
		}
	}

	/**
	 * Alert Dialog para mostrar diferentes tipos de mensaje al usuario.
	 * 
	 * @param titulo
	 * @param mensaje
	 */
	private void mostrarAlertDialog(String titulo, String mensaje) {

		AlertDialog.Builder builder = new AlertDialog.Builder(FormCargueInventarioSugerido.this);
		builder.setCancelable(false);
		builder.setIcon(R.drawable.alert);
		builder.setTitle(titulo);
		builder.setMessage(mensaje);
		builder.setPositiveButton("Aceptar", new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Imprimir la tirilla de reporte
	 */
	protected void imprimirInventarioSugerido() {

		progressDialog = ProgressDialog.show(FormCargueInventarioSugerido.this, "Imprimiendo",
				"Retire la tirilla cuando este lista.", true);
		progressDialog.show();

		new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				SharedPreferences settings = getSharedPreferences(Const.CONFIG_IMPRESORA, MODE_PRIVATE);
				final String macImpresora = settings.getString(Const.MAC_IMPRESORA, "-");

				if (macImpresora.equals("-")) {
					progressDialog.dismiss();
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(FormCargueInventarioSugerido.this,
									"Aun no hay Impresora Predeterminada.\n\nPor Favor primero Configure la Impresora!",
									Toast.LENGTH_SHORT).show();
						}
					});
				} else {

					if (wR240 == null) {
						wR240 = new WoosimR240(FormCargueInventarioSugerido.this);
					}
					int conect = wR240.conectarImpresora(macImpresora);

					switch (conect) {

					case 1:
						wR240.generarEncabezadoTirillaInventarioSugerido();
						wR240.imprimirBuffer(true);
						break;

					case -2:
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(FormCargueInventarioSugerido.this, "-2 fallo conexion",
										Toast.LENGTH_SHORT).show();
							}
						});
						break;

					case -8:
						progressDialog.dismiss();
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Util.MostrarAlertDialog(FormCargueInventarioSugerido.this,
										"Bluetooth apagado. Por favor habilite el bluetoth para imprimir.");
							}
						});
						break;

					default:
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(FormCargueInventarioSugerido.this,
										"Error desconocido, intente nuevamente.", Toast.LENGTH_SHORT).show();
							}
						});
						break;
					}

					try {
						Thread.sleep(Const.timeWait);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					if (wR240 != null) {
						wR240.desconectarImpresora();
						progressDialog.dismiss();
					}
					FormCargueInventarioSugerido.this.finish();
				}
				Looper.myLooper().quit();
			}
		}).start();

	}

}// final de la clase