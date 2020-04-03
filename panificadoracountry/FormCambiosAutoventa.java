package co.com.panificadoracountry;


import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Vector;

import co.com.panificadoracountry.R;
import co.com.BusinessObject.DataBaseBO;
import co.com.BusinessObject.FileBO;
import co.com.BusinessObject.PrinterBO;
import co.com.Conexion.Sync;
import co.com.DataObject.Cliente;
import co.com.DataObject.Detalle;
import co.com.DataObject.Encabezado;
import co.com.DataObject.Inventario;
import co.com.DataObject.ItemListView;
import co.com.DataObject.MotivoCambio;
import co.com.DataObject.Producto;
import co.com.DataObject.Usuario;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.SQLException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SlidingDrawer;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;

public class FormCambiosAutoventa  extends Activity  implements OnClickListener,Sincronizador,OnDrawerOpenListener, OnDrawerCloseListener {



	/**
	 * Constantes para ingresar en las columnas numericas de la tabla emcabezado
	 * 	
	 */
	private final static int TIPO_TRANSACCION = 5;
	private final static int ENTREGADO = 0;
	private final static int FACTURA = 0;
	private final static int TIPO_VENTA_PEDIDO = 1;
	private final static int TIPO_DOCUMENTO = 1;
	private final static int ESTADO_PEDIDO = 1;
	private final static int ESTADO_PAGO = 1;
	
	/**
	 * identificador unico de un pedido. se forma por el siguiente formato.
	 * - AC aoo mes dia codVendedor 4digitos
	 * los 4 digitos conforman un consecutivo.
	 */
	private static String numeroDocPedidoAv = "";

	String printerName = "";

	public final static String CONFIG_IMPRESORA = "PRINTER";
	public final static String MAC_IMPRESORA    = "MAC";
	public final static String LABEL_IMPRESORA  = "LABEL";

	/**
	 * constante define el formato de las fechas usadas en el programa
	 */
	private static final String FORMATO_FECHA = "yyyy-MM-dd";
	
	private static Activity context = null;
	private static String mensaje = "";

	private static String TAG = FormCambiosAutoventa.class.getName();

	Vector<Producto> listaProductos;
	Vector<Integer> tiposPedido;

	boolean setListener = true;

	Cliente cliente;
	int tipoDeClienteSeleccionado = 1;
	Producto producto;
	Detalle detalleEdicion;
	Dialog dialogPedido;
	Dialog dialogEditar;
	Dialog dialogResumen;
	private static ProgressDialog progressDialog;
	Dialog dialogImprimirFacturaPedido;
	//Por defecto lo toma como pedido
	boolean cambio = false;
	String strOpcion;
	private TextView lblTotalItems;
	private TextView lblTotalItemsPedido;
	private TextView lblSubTotal;
	private TextView lblTotalIva;
	private TextView lblDescuento;
	private TextView lblValorNetoPedido;
	/**
	 * mostrar calendario para ingresar fecha de entrega de pedido
	 */
	private ImageButton fechaEntrega;
	/**
	 * fecha de entrega ingresada por el usuario
	 */
	private EditText editTextFechaEntregaPedidoAv;

	Button btnBusquedaProduc;
	Vector< MotivoCambio > listaMC;
	Detalle detalle;
	EditText txtCodigoProducto;
	Sync sync;
	Encabezado encabezado;
	Hashtable<String, Producto> productosPedido;
	Producto productoEdicion;
	
	
	/**
	 * conserva las obvservaciones ingresadas por el usuario.
	 */
	private String observaciones;
	boolean pVezCant1;
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.form_cambios_autoventa);
		encabezado = new Encabezado();
		productosPedido = new Hashtable<String, Producto>() ;		
		txtCodigoProducto = (EditText)findViewById( R.id.txtCodigoProducto );
		Inicializar();
		InicializarDatos();
		btnBusquedaProduc = (Button) findViewById(R.id.btnBusquedaProduc);
		SetListenerListView1();
		
	}

	protected void onResume(){

		super.onResume();
		DataBaseBO.ValidarUsuario();
		FileBO.validarCliente(this);
		DataBaseBO.setAppAutoventa();
		((EditText)findViewById(R.id.txtCodigoProducto)).requestFocus();
	}

	@Override
	public void onClick(View v) {


		// toggle TextView in first tab


	}





	public void Inicializar() {

		lblTotalItems       = ((TextView) findViewById(R.id.lblTotalItems));
		lblTotalItemsPedido = ((TextView) findViewById(R.id.lblTotalItemsPedido));
		lblSubTotal         = ((TextView) findViewById(R.id.lblSubTotal));
		lblTotalIva         = ((TextView) findViewById(R.id.lblTotalIva));
		lblDescuento        = ((TextView) findViewById(R.id.lblDescuento));
		lblValorNetoPedido  = ((TextView) findViewById(R.id.lblValorNetoPedido));
	}

	public void InicializarDatos() {

		Bundle bundle = getIntent().getExtras();
		if (bundle != null && bundle.containsKey("cambio"))
			cambio = bundle.getBoolean("cambio");
		strOpcion = cambio ? "Devolucion" : "Pedido";
		setTitle("Registrar " + strOpcion);
		((LinearLayout)findViewById(R.id.panelInfo)).setVisibility(View.VISIBLE);

		CargarListaPedido();

		if (setListener) {

			SetKeyListener();
			SetFocusListener();
			SetListenerListView1();
			setListener = false;
		}
		
		if(cambio)
			((Button)findViewById(R.id.btnUltimoPedido)).setVisibility(Button.GONE);
		
	}

	public void OnClickTerminarPedido(View view) {

		EditText txtCodigoProducto = (EditText)findViewById(R.id.txtCodigoProducto);
		txtCodigoProducto.setError(null);

		OcultarTeclado(txtCodigoProducto);
		ValidarPedido();
	}

	public void OnClickCancelarPedido(View view) {

		EditText txtCodigoProducto = (EditText)findViewById(R.id.txtCodigoProducto);
		OcultarTeclado(txtCodigoProducto);
		txtCodigoProducto.setError(null);
		CancelarPedido();
	}

	public void OnClickTomarFoto(View view) {

	}

	public void OnClickOpcionesPedido(View view) {

		int totalItems;
		EditText txtCodigoProducto;

		switch (view.getId()) {

		case R.id.btnAceptarOpcionesPedido:

			txtCodigoProducto = (EditText)findViewById(R.id.txtCodigoProducto);
			OcultarTeclado(txtCodigoProducto);

			String codigoProducto = txtCodigoProducto.getText().toString().trim();

			if (codigoProducto.equals("")) {

				txtCodigoProducto.setError("Ingrese el codigo");
				txtCodigoProducto.requestFocus();

			} else {


					if(existeProductoPedido(codigoProducto)){

						Util.MostrarAlertDialog(this,"El Producto ya fue Ingresado en el Cambio",1);

					}else{

						CargarProducto(codigoProducto);
						txtCodigoProducto.setError(null);

					}

				}
			
			break;

		case R.id.btnBuscarOpcionesPedido:



			  ((EditText)findViewById(R.id.txtCodigoProducto)).setError(null);
			   Intent formBuscarProducto = new Intent(this, FormBuscarProducto.class);
			   startActivityForResult(formBuscarProducto, Const.RESP_BUSQUEDA_PRODUCTO);
		
			break;


		}
	}

	public void OnClickUltimoPedido(View view) {

		Toast toast = Toast.makeText(this,"Ultimo Pedido No Habilitado por el Momento", Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
		
	}

	public void OnClickBuscarProducto( View view ){

		((EditText)findViewById(R.id.txtCodigoProducto)).setError(null);
		Intent formBuscarProducto = new Intent(this, FormBuscarProducto.class);
		startActivityForResult(formBuscarProducto, Const.RESP_BUSQUEDA_PRODUCTO);

	}


	private void AgregarProductoPedido(float cantidad, float descuento_autorizado, String codMotivo) {
		
		int cantidadTotal = DataBaseBO.obtenerCantTotalLotes(producto.codigo);
		
		producto.cantidadPedida = (int)cantidad;
		
		
		if (cantidadTotal >= producto.cantidadPedida) {

			Vector<Producto> productoAguardar = validarCantidadPorLotes(producto);

			int count = 0;
			for (Producto productox : productoAguardar) {
				
				productox.codMotivo      = codMotivo;
				/* agregar el producto al detalle detalle */
				boolean state = AgregarProducto(productox);

				if (state) {
					count++;
				}
			}
			
			if (count > 0) {

				EditText txtCantidadProc = (EditText)dialogPedido.findViewById(R.id.txtCantidadProc);
				txtCantidadProc.setText("");
				OcultarTeclado(txtCantidadProc);
				txtCodigoProducto.setText("");
				txtCodigoProducto.requestFocus();
				CargarListaPedido();
				dialogPedido.cancel();
				MostrarTeclado( txtCodigoProducto );

			} else {

				Util.MostrarAlertDialog(this, "Error registrando la informacion del Producto");
			}
			
		}else{
			mostrarToast("La cantidad ingresada supera el Inventario del Producto \n"
					+ "El inventario de "+producto.descripcion+ " es de "+producto.cantidadInv);
		}	

//		EditText txtCodigoProducto = (EditText)findViewById(R.id.txtCodigoProducto);
//		FileBO.validarCliente(this);
//		cliente = Main.cliente;
//		boolean agregoVenta = false;
//		Producto productoNuevo = new Producto();
//		productoNuevo.codigo          =  producto.codigo;
//		productoNuevo.descripcion     = producto.descripcion;
//		productoNuevo.precio          = producto.precio;
//		productoNuevo.iva             = producto.iva;
//		productoNuevo.cantidadPedida = (int)cantidad;
//		productoNuevo.codMotivo      = codMotivo;
//		productoNuevo.cantidadInv    = producto.cantidadInv;
//		productoNuevo.linea          = producto.linea;	
//		productoNuevo.linea 		 = producto.lote;
//		productoNuevo.fechaVencimiento = producto.fechaVencimiento;
//		productoNuevo.fechaFabricacion = producto.fechaFabricacion;
//		
//		agregoVenta = AgregarProducto(productoNuevo);
//
//		if (agregoVenta) {
//
//			EditText txtCantidadProc = (EditText)dialogPedido.findViewById(R.id.txtCantidadProc);
//			txtCantidadProc.setText("");
//			OcultarTeclado(txtCantidadProc);
//			txtCodigoProducto.setText("");
//			txtCodigoProducto.requestFocus();
//			CargarListaPedido();
//			dialogPedido.cancel();
//			MostrarTeclado( txtCodigoProducto );
//
//		} else {
//
//			Util.MostrarAlertDialog(this, "Error registrando la informacion del Producto");
//		}
	}
	
	private Vector<Producto> validarCantidadPorLotes(Producto producto) {
		
		Vector<Producto> listaProductoNew = new Vector<Producto>();

		int cantFaltante = 0;
		int cantVendida = producto.cantidadPedida;
		int cantAux = 0;

		System.out.println("Producto: " + producto.descripcion + " cod:" + producto.codigo + " N " + producto.cantidadPedida);

		Vector<Inventario> listaLote = DataBaseBO.obtenerLotesXCodigo(producto.codigo);

		Producto productoNew;

		for (int i = 0; i < listaLote.size(); i++) {

			productoNew = new Producto();
			productoNew.codigo = producto.codigo;
			productoNew.codLinea = producto.codLinea;
			productoNew.precio = producto.precio;
			productoNew.iva = producto.iva;
			productoNew.descripcion   = producto.descripcion;
			productoNew.descuento = producto.descuento;
			productoNew.cantidad = producto.cantidadPedida;
			productoNew.lote = listaLote.get(i).lote;
			productoNew.fechaFabricacion = listaLote.get(i).fechaFabricacion;
			productoNew.fechaVencimiento = listaLote.get(i).fechaVencimiento;

			cantAux = cantVendida + cantFaltante;
			System.out.println("LOTE: " + productoNew.lote);
			System.out.println("Cantidad AUXILIAR: " + cantAux + "Cantidad vendida " + cantVendida + " // " + cantFaltante);

			if (listaLote.get(i).cantidad >= cantAux) {
				restarLote(listaLote.get(i).cantidad, cantAux, listaLote.get(i).lote, producto.codigo);
				cantVendida = 0;

				System.out.println(listaLote.get(i).lote + " Cant: " + listaLote.get(i).cantidad);
				productoNew.cantidadPedida = cantAux;
				listaProductoNew.add(productoNew);
				System.out.println("Hay suficiente cantidad en el lote" + productoNew.cantidadPedida);
				break;
				// Hay cantidad suficiente en el lote

			} else {

				cantFaltante = producto.cantidadPedida - listaLote.get(i).cantidad;
				restarLote(listaLote.get(i).cantidad, cantAux - cantFaltante, listaLote.get(i).lote, producto.codigo);
				cantVendida = 0;
				productoNew.cantidadPedida = cantAux - cantFaltante;
				listaProductoNew.add(productoNew);
				System.out.println("Hace falta un total de " + cantFaltante + " inventario en el lote" + productoNew.cantidadPedida);

			}
		}
		return listaProductoNew;
	}
	
	private void restarLote(int cantidadLote, int cantVender, String lote, String codigo) {

		// System.out.println("Restar Lote");
		int cantidadSobrante = cantidadLote - cantVender;
		// System.out.println("Cantidad solicitada: "+ cantVender +" Cant total:
		// "+cantidadLote + " Cant sobrante: "+cantidadSobrante);
		DataBaseBO.restarLote(cantidadSobrante, lote, codigo);
	}

	private void ActualizarProductoPedido(Detalle detalle, float cantidad, float descuento_autorizado) {

		/*if (detalle == null) {

			Util.MostrarAlertDialog(this, "Error leyendo la informacion del Producto");
			return;
		}

		EditText txtCodigoProducto = (EditText)findViewById(R.id.txtCodigoProducto);
		String codProducto = txtCodigoProducto.getText().toString();

		boolean actualizo = DataBaseBO.ActualizarProductoPedido(encabezado, codProducto, cantidad);

		if (actualizo) {

			detalle.cantidad = cantidad;
			detalle.descuento_autorizado = descuento_autorizado;

			detallePedido .put(codProducto, detalle);

			EditText txtCantidadProc = (EditText)dialogPedido.findViewById(R.id.txtCantidadProc);
			txtCantidadProc.setText("");

			//((EditText)dialogPedido.findViewById(R.id.txtDescAutorizado)).setText("");

			OcultarTeclado(txtCantidadProc);

			txtCodigoProducto.setText("");
			txtCodigoProducto.requestFocus();

			CargarListaPedido();
			dialogPedido.cancel();

		} else {

			Util.MostrarAlertDialog(this, "Error Actualizando los datos del Producto");
		}*/
	}

	public boolean AgregarProducto(Producto producto) {

		boolean registro = false;
		productosPedido.put(producto.codigo, producto);
		
		
		try {

			Usuario usuario = DataBaseBO.ObtenerUsuario();
			DataBaseBO.agregarProductoDetalleAv2(producto, usuario.canalVenta);
			registro = true;

		} catch (SQLException e) {
			Util.MostrarAlertDialog(this.getBaseContext(),
					"Error guardando el producto!\nCausa: " + e.getMessage());
			registro = false;
		}

		return registro;
	}

	private boolean CargarProducto(String codigoProducto) {

		producto = new Producto();
		boolean cargo = DataBaseBO.ProductoXCodigo(codigoProducto, Main.cliente.listaPrecio, producto);

		if (cargo) {

			return MostrarDialogPedido(codigoProducto);

		} else {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("No se encontro el Producto")
			.setCancelable(false)
			.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int id) {

					dialog.cancel();
				}
			});

			AlertDialog alert = builder.create();
			alert.show();
			return false;
		}
	}

	public boolean MostrarDialogPedido(String codigoProducto) {

		try {

			final boolean editar;
			double descto = 0;

			if (dialogPedido == null) {

				dialogPedido = new Dialog(this);
				dialogPedido.setContentView(R.layout.dialog_pedido);
				dialogPedido.setTitle("Registrar Producto");


			} else {

				((EditText)dialogPedido.findViewById(R.id.txtCantidadProc)).requestFocus();
			}

			SetKeyListenerDialogProd();


			((EditText)dialogPedido.findViewById(R.id.txtCantidadProc)).requestFocus();
	
			MostrarTeclado( ((EditText)dialogPedido.findViewById(R.id.txtCantidadProc)) );

			if (cambio) {

				CargarMotivosCambio();
				((TableLayout)dialogPedido.findViewById(R.id.tblMotivoCambio)).setVisibility(TableLayout.VISIBLE);

			} else {

				((TableLayout)dialogPedido.findViewById(R.id.tblMotivoCambio)).setVisibility(TableLayout.GONE);
			}

			( ( TextView ) dialogPedido.findViewById( R.id.lblPromocion ) ).setVisibility     ( View.GONE );
		

			if (producto != null) { //Se carga la Informacion del Producto


				//descto = DataBaseBO.obtenerDescuento(producto, Main.cliente.codigo);
				//descto = DataBaseBO.obtenerDescuentoNuevo(producto, Main.cliente);

				((TextView)dialogPedido.findViewById(R.id.lblDescProducto)).setText(producto.descripcion);
				((TextView)dialogPedido.findViewById(R.id.lblPrecio)).setText("Precio: " + Util.SepararMiles("" + producto.precio) + " - Iva: " + producto.iva + "%");
				((TextView)dialogPedido.findViewById(R.id.lblInventario)).setText("Inventario: " + producto.cantidadInv);
				((TextView)dialogPedido.findViewById(R.id.lblDcto)).setText("Descuento: " + descto);



				editar = false;
				((EditText)dialogPedido.findViewById(R.id.txtCantidadProc)).setText("");
				
				int cantProm = 0;

				if( !cambio )
					cantProm = DataBaseBO.getCantidadPromocion( codigoProducto );

				if( cantProm > 0 ){

					( ( TextView ) dialogPedido.findViewById( R.id.lblPromocion ) ).setVisibility( View.GONE );
				
					( ( TextView ) dialogPedido.findViewById( R.id.lblPromocion ) ).setText( "Promocion: " + cantProm  );
					}

				((Button)dialogPedido.findViewById(R.id.btnVentaOpcionesPedido)).setOnClickListener(new OnClickListener() {

					public void onClick(View v) {

						agregarProducto();
					}
				});

				((Button)dialogPedido.findViewById(R.id.btnCancelarOpcionesPedido)).setOnClickListener(new OnClickListener() {

					public void onClick(View v) {

						dialogPedido.cancel();
					}
				});

				dialogPedido.setCancelable(false);
				dialogPedido.show();

				return true;

			} else { //No se Encontro el Producto

				//Main.descAct = "";
				((TextView)dialogPedido.findViewById(R.id.lblDescProducto)).setText("-");
				((TextView)dialogPedido.findViewById(R.id.lblPrecio)).setText("-");

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("No se encontro el Producto")
				.setCancelable(false)
				.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

				AlertDialog alert = builder.create();
				alert.show();

				return false;
			}

		} catch (Exception e) {

			//String msg = e.getMessage();
			return false;
		}
	}


	/**
	 * Verifica si el pedio tiene productos asignados, 
	 * en caso tal muestra el resumen del Pedido y pide al usuario las observaciones 
	 * Si el pedido esta Vacio, muestra un mensaje de advertencia al usuario
	 **/
	public void ValidarPedido() {

		if (productosPedido.size() > 0) {
				
			MostrarDialogResumen();

		} else {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("El " + strOpcion + " esta vacio. Por favor ingrese los productos.")
			.setCancelable(false)
			.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int id) {

					dialog.cancel();
				}
			});

			AlertDialog alert = builder.create();
			alert.show();
		}
	}


	public boolean MostrarDialogResumen() {

		try {

			if (dialogResumen == null) {

				dialogResumen = new Dialog(this);
				dialogResumen.setContentView(R.layout.dialog_resumen_pedido);

				/**
				 * Se registran los eventos de los Botones del Formulario Dialog Resumen
				 */
				((Button)dialogResumen.findViewById(R.id.btnAceptarResumenPedido)).setOnClickListener(new OnClickListener() {

					public void onClick(View v) {

						encabezado.fiado = 0;
						encabezado.observacion = ((EditText)dialogResumen.findViewById(R.id.txtObservacionPedido)).getText().toString().trim();
						finalizarCambio();
					}
				});

				((Button)dialogResumen.findViewById(R.id.btnCancelarResumenPedido)).setOnClickListener(new OnClickListener() {

					public void onClick(View v) {

						EditText txtObservacionPedido = ((EditText)dialogResumen.findViewById(R.id.txtObservacionPedido));
						OcultarTeclado(txtObservacionPedido);

						dialogResumen.cancel();
					}
				});

			} else {

				((EditText)dialogResumen.findViewById(R.id.txtObservacionPedido)).setText("");
				((EditText)dialogResumen.findViewById(R.id.txtObservacionPedido)).requestFocus();
				//((Spinner) dialogResumen.findViewById(R.id.cbMotivoCambio)).setSelection(0);
			}

;

				((TextView)dialogResumen.findViewById(R.id.lblSubTotalResumenPedido)).setText(lblSubTotal.getText().toString());
				((TextView)dialogResumen.findViewById(R.id.lblIVAResumenPedido)).setText(lblTotalIva.getText().toString());
				((TextView)dialogResumen.findViewById(R.id.lblDescuentoResumenPedido)).setText(lblDescuento.getText().toString());
				((TextView)dialogResumen.findViewById(R.id.lblTotalResumenPedido)).setText(lblValorNetoPedido.getText().toString());

				((TableLayout)dialogResumen.findViewById(R.id.tableLayoutResumenCliente)).setVisibility(TableLayout.VISIBLE);
				
				fechaEntrega = (ImageButton) dialogResumen.findViewById(R.id.buttonFechaEntregaPedidoAv);
				editTextFechaEntregaPedidoAv = ((EditText) dialogResumen.findViewById(R.id.editTextFechaEntregaPedidoAv));
				//((TableLayout)dialogResumen.findViewById(R.id.tblMotivoCambio)).setVisibility(TableLayout.GONE);
			//}


		

			cliente = Main.cliente;

			if(Main.tipoVenta.equals("T") || Main.tipoVenta.equals("C")){

				((TextView)dialogResumen.findViewById(R.id.lblClienteResumenPedido)).setText(cliente.nombre);
				dialogResumen.setTitle("Resumen " + strOpcion);

				///--dialogResumen.findViewById(R.id.trCreditoContado).setVisibility(TableRow.GONE);
				
				if(Main.tipoVenta.equals("C"))
					setListenerRadioButtons();
				
			}
			else{

				((TextView)dialogResumen.findViewById(R.id.lblClienteResumenPedido)).setText(cliente.nombre + "\n" + "Factura: " + DataBaseBO.getNumFacturaActual( !cambio ) );
				dialogResumen.setTitle("Resumen " + strOpcion);
				dialogResumen.findViewById(R.id.trCreditoContado).setVisibility(TableRow.VISIBLE);
				((RadioButton)(dialogResumen.findViewById(R.id.radioContado))).setChecked(true);
				((RadioButton)(dialogResumen.findViewById(R.id.radioCredito))).setChecked(false);

				((RadioButton)(dialogResumen.findViewById(R.id.radioContado))).setOnClickListener(new OnClickListener() {

					public void onClick(View v) {


						if(dialogResumen != null){
							if(dialogResumen.isShowing()){
								if(((RadioButton)dialogResumen.findViewById(R.id.radioContado)).isChecked()){


									((RadioButton)dialogResumen.findViewById(R.id.radioCredito)).setChecked(false);

									
								}
							}
						}


					}
				});



				((RadioButton)(dialogResumen.findViewById(R.id.radioCredito))).setOnClickListener(new OnClickListener() {

					public void onClick(View v) {

						if(dialogResumen != null){
							if(dialogResumen.isShowing()){
								if(((RadioButton)dialogResumen.findViewById(R.id.radioCredito)).isChecked()){


									((RadioButton)dialogResumen.findViewById(R.id.radioContado)).setChecked(false);
									
								}
							}
						}



					}
				});
				
				



			}


			//captura del evento del boton de definir la fecha de entrega del pedido
			fechaEntrega.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {			
					//mostrar el seleccionador de fecha para agregarla al edit del fecha de entrega
					mostrarDataPicker(editTextFechaEntregaPedidoAv);
				}
			});
			
			dialogResumen.setCancelable(false);
			dialogResumen.show();

			return true;

		} catch (Exception e) {

			Log.e("MostrarDialogResumen", e.getMessage(), e);
			return false;
		}
	}

	public void MostrarDialogEdicion() {

		if (dialogEditar == null) {

			dialogEditar = new Dialog(this);
			dialogEditar.setContentView(R.layout.dialog_editar);
			dialogEditar.setTitle("Opciones");



			((RadioButton)dialogEditar.findViewById(R.id.radioEliminar)).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					AlertDialog.Builder builder = new AlertDialog.Builder(FormCambiosAutoventa.this);
					builder.setMessage("Esta Seguro de eliminar el producto " + productoEdicion.codigo+" "+productoEdicion.descripcion)
					.setCancelable(false)
					.setPositiveButton("Si", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int id) {

							
							try {
							
								//eliminar producto de la lista de productos pedidos.
								Producto prod = productosPedido.remove(productoEdicion.codigo);
								
								devolverCantidadInventario(productoEdicion.codigo);

								//remover el producto de la lista de pedido.
								if(DataBaseBO.quitarProductoProductoPedidoAv(prod) && prod!=null) {
									mostrarToast("Producto codigo: " + prod.codigo + ".\nHa sido eliminado Correctamente!");
								
								}

								//actualizar la lista de pedido en pantalla.
								CargarListaPedido();
								dialogEditar.cancel();

							} catch (Exception e) {
								mostrarToast("ERROR: " + e.getMessage());
							}
							
							
						}
					})
					.setNegativeButton("No", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int id) {

							dialogEditar.cancel();
						}
					});

					AlertDialog alert = builder.create();
					alert.show();
				}
			});

			((RadioButton)dialogEditar.findViewById(R.id.radioCancelar)).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					dialogEditar.cancel();
				}
			});

		} else {

			((RadioButton)dialogEditar.findViewById(R.id.radioEditar)).requestFocus();		
		}


		((RadioButton)dialogEditar.findViewById(R.id.radioEditar)).setVisibility(RadioButton.GONE);
		dialogEditar.show();
	}
	
	private void devolverCantidadInventario(String codProducto) {
		
		Vector<Producto> listaDetallePdto = DataBaseBO.obtenerListaDetalleVenta(codProducto);			
		for (int j = 0; j < listaDetallePdto.size(); j++) {
			
			System.out.println("Codigo "+ listaDetallePdto.get(j).codigo);
			System.out.println("LOTE: "+ listaDetallePdto.get(j).lote);
			System.out.println("Cantidad : "+ listaDetallePdto.get(j).cantidadPedida);
			
			DataBaseBO.sumarLote(listaDetallePdto.get(j).cantidadPedida, listaDetallePdto.get(j).lote,  codProducto, listaDetallePdto.get(j).fechaFabricacion, listaDetallePdto.get(j).fechaVencimiento);
		}
	}

	public void CargarListaPedido() {

		encabezado = new Encabezado();
		encabezado.sub_total = 0;
		encabezado.total_iva = 0;
		encabezado.valor_descuento = 0;
		tiposPedido = new Vector<Integer>();
		ItemListView itemListView;
		Vector<ItemListView> datosPedido = new Vector<ItemListView>();
	
		
		Enumeration<Producto> e = productosPedido.elements();

		while (e.hasMoreElements()) {

			Producto prod = e.nextElement();
			itemListView = new ItemListView();
			float sub_total       = prod.cantidadPedida * prod.precio;
			float valor_descuento = sub_total * prod.descuento / 100;
			float valor_iva       = (sub_total - valor_descuento) * (prod.iva / 100);

			encabezado.sub_total += sub_total;
			encabezado.total_iva += valor_iva;
			encabezado.valor_descuento += valor_descuento;



			itemListView.titulo = prod.codigo + " - Cambio " + prod.descripcion;
		    itemListView.subTitulo = "Precio: $" + prod.precio + " - Iva: " + prod.iva +  "% - Cant: " + prod.cantidadPedida;

			if( !prod.codMotivo.equals( "" ) )
			itemListView.subTitulo += "\nMotivo: " + prod.codMotivo;

	

			datosPedido.addElement(itemListView);
		}

		ItemListView[] listaItems = new ItemListView[datosPedido.size()];
		datosPedido.copyInto(listaItems);

		encabezado.valor_neto     = encabezado.sub_total + encabezado.total_iva - encabezado.valor_descuento;
		encabezado.str_valor_neto = Util.SepararMilesSin(Util.RedondearFit(Util.QuitarE("" + encabezado.valor_neto), 0)); 


		lblTotalItemsPedido.setText("" + productosPedido.size());
		lblSubTotal.setText(Util.SepararMilesSin(Util.RedondearFit(Util.QuitarE("" + encabezado.sub_total), 0)));
		lblTotalIva.setText(Util.SepararMilesSin(Util.RedondearFit(Util.QuitarE("" + encabezado.total_iva), 0)));
		lblDescuento.setText(Util.SepararMilesSin(Util.RedondearFit(Util.QuitarE("" + encabezado.valor_descuento), 0)));
		lblValorNetoPedido.setText(encabezado.str_valor_neto);
		

		ListViewAdapter adapter = new ListViewAdapter(this, listaItems, R.drawable.prodconmed, 0);
		ListView listaPedido = (ListView)findViewById(R.id.listaPedido);
		listaPedido.setAdapter(adapter);

		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {

			if ( requestCode == Const.RESP_BUSQUEDA_PRODUCTO  ) {

				if (data != null) {

					Bundle bundle = data.getExtras();
					Object productoSel = bundle.get("producto"); 

					if (productoSel != null && productoSel instanceof Producto) {

						if(existeProductoPedido(((Producto)productoSel).codigo)){

							Util.MostrarAlertDialog(this,"El Producto ya fue Ingresado en el Cambio",1);

						}else{

						  producto = (Producto)productoSel;
						  ((EditText)findViewById(R.id.txtCodigoProducto)).setText(producto.codigo);

						  MostrarDialogPedido(producto.codigo);
						
						}
					}

				} else if (requestCode == Const.RESP_ULTIMO_PEDIDO) {

					CargarListaPedido();
				}

			} else if (requestCode == Const.RESP_FORMAS_DE_PAGO) {

				MostrarDialogResumen();
			}
		}
	}

	
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// replaces the default 'Back' button action
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			AlertDialog alertDialog;

			// ContextThemeWrapper ctw = new ContextThemeWrapper( this,
			// R.style.Theme_Dialog_Translucent );
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setCancelable(false)

					.setPositiveButton("Aceptar",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int id) {

									// limpiar listaProductos
									listaProductos = null;
									// limpiar productosPedido
									productosPedido = null;
									// limpiar cliente actual
									cliente = null;
									DataBaseBO.cancelarPedidoAv();
									dialog.cancel();
									finish();
								}
							})

					.setNegativeButton("Cancelar",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int id) {

									dialog.cancel();
								}
							});

			alertDialog = builder.create();
			alertDialog
					.setMessage("Desea Salir. Se eliminara la informacion del Cambio");
			alertDialog.show();

		}
		return true;
	}
	
	
	public void CancelarPedido() {

		if (productosPedido.size() > 0) {

			/**
			 * Ya ha tomado un Pedido, Se pregunta al Usuario si desea Cancelarlo
			 **/
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Esta Seguro de cancelar el " + strOpcion + "? La informacion se Borrara")
			.setCancelable(false)
			.setPositiveButton("Si", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int id) {

					
						//limpiar listaProductos
	     				listaProductos = null;
	     				//limpiar productosPedido
	     				productosPedido = null;		
	     				//limpiar cliente actual
	     				cliente = null;
	     				DataBaseBO.cancelarPedidoAv();
	     				dialog.cancel();
	     				finish();
	     				
				}

					
				
			})
			
			.setNegativeButton("No", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int id) {

					moveTaskToBack(false);
					dialog.cancel();
				}
			});

			AlertDialog alert = builder.create();
			alert.show();

		} else {

			
			   //limpiar listaProductos
				listaProductos = null;
				//limpiar productosPedido
				productosPedido = null;		
				//limpiar cliente actual
				cliente = null;
				DataBaseBO.cancelarPedidoAv();
				finish();
			
		}
	}



	/**
	 * metodo para confirmacion definitiva de un pedido.
	 * Se usan transacciones en bd para confirmar solo pedidos que terminan correctamente.
	 * si se produce un error el pedido no se realiza.
	 * @by JICZ
	 */
	protected void finalizarCambio() {
		
		//verificar que la lista de pedido no este vacia
		if(!productosPedido.isEmpty()){

			//cargar la informacion del usuario
			Usuario usuario = DataBaseBO.ObtenerUsuario();	

			//intentar inicializar numeroDocPedidoAv para este pedido, usando el codigo del vendedor.
			try {

				
				numeroDocPedidoAv = DataBaseBO.ObtenterNumeroDoc(usuario.codigoVendedor);
				FileBO.validarCliente(this);
				cliente = Main.cliente;		
				String resolucion = "";
				long consec = 0;
					
				encabezado.numero_doc = numeroDocPedidoAv;
				encabezado.resolucion = resolucion;
				encabezado.consecutivoResolucion = consec;
				encabezado.codigo_novedad = TIPO_TRANSACCION;
				encabezado.hora_inicial    = Util.FechaActual("yyyy-MM-dd HH:mm:ss");
					
				boolean pedidoCompleto = DataBaseBO.guardarEncabezadoCambioAv(	numeroDocPedidoAv,
							cliente, usuario, "", "",
							observaciones, TIPO_TRANSACCION ,ENTREGADO,
							FACTURA,TIPO_VENTA_PEDIDO,TIPO_DOCUMENTO,
							ESTADO_PEDIDO, ESTADO_PAGO, encabezado);
					
				//guardar la novedad
				String imei = obtenerImei();
				String version = ObtenerVersion();
				double latitud = 0;
				double longitud = 0;
				double lat = Main.cliente.latitud;
				double lon = Main.cliente.longitud;
				
				if(lat == 0.0 && lon ==0.0){
					latitud = DataBaseBO.verLatitud(Main.cliente.codigo);
					longitud = DataBaseBO.verLongitud(Main.cliente.codigo);
				}else{
					latitud = lat;
					longitud = lon;
				}
				
				encabezado.lat = latitud;
				encabezado.lon = longitud;
				boolean guardarNovedad = DataBaseBO.guardarNovedad(encabezado,numeroDocPedidoAv, cliente, usuario, "" ,observaciones, imei, version,1); // 6 y 32 quemado segun base de datos web, tablas tipo_documento y Nocompras.

				if(pedidoCompleto && guardarNovedad) {
					
					DataBaseBO.organizarInventario(this);
					
						
					mostrarToast("Se ha guardado el cambio correctamente!\n Numero: " + numeroDocPedidoAv);
					mostrarAlertParaConfirmarSiImprimir();						
					
				    }
					
				   else {
						mostrarToast("No se logro guardar el cambio!\n Numero: " + numeroDocPedidoAv);
					}					
				
			} catch (Exception e) {
				Util.MostrarAlertDialog(this.getBaseContext(), e.getMessage());
			}
		}
		else {
			//			--mostrar informacion de pedido vacio
			Util.MostrarAlertDialog(FormCambiosAutoventa.this, "No hay cambio para guardar!");
		}
	}



	public void SetListenerListView1() {

		ListView listaOpciones = (ListView)findViewById(R.id.listaPedido);
		listaOpciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				ListViewAdapter adapter = (ListViewAdapter)parent.getAdapter();
				ItemListView itemListView = adapter.listItems[position];

				String[] data = itemListView.titulo.split("-");
				String codigoProducto = data[0].trim();

				productoEdicion = productosPedido.get(codigoProducto+"");
				MostrarDialogEdicion();
			}
		});
	}

	/*public void SetKeyListener() {

		EditText txtCodigoProducto = (EditText)findViewById(R.id.txtCodigoProducto);
		txtCodigoProducto.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {

				if (keyCode == KeyEvent.KEYCODE_ENTER) {

					((Button)findViewById(R.id.btnAceptarOpcionesPedido)).requestFocus();
					return true;
				}

				return false;
			}
		});
	}*/

	public void SetFocusListener() {

		EditText txtCodigoProducto = (EditText)findViewById(R.id.txtCodigoProducto);
		txtCodigoProducto.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {

				if (!hasFocus) {

					/**
					 * Si NO tiene el foco se quita el mensaje de Error.
					 **/
					((EditText)findViewById(R.id.txtCodigoProducto)).setError(null);
				}
			}
		});
	}

	@Override
	public void RespSync(boolean ok, String respuestaServer, String msg, int codeRequest) {
		sync.activo = false;
		final String mensaje = ok ?  strOpcion + " Registrado con Exito en el servidor" : msg;

		if (progressDialog != null)
			progressDialog.cancel();

		this.runOnUiThread(new Runnable() {

			public void run() {

				AlertDialog.Builder builder = new AlertDialog.Builder(FormCambiosAutoventa.this);
				builder.setMessage(mensaje)

				.setCancelable(false)
				.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {

					
						//limpiar listaProductos
	     				listaProductos = null;
	     				//limpiar productosPedido
	     				productosPedido = null;
	     				//limpiar cliente actual
	     				cliente = null;
	     				//DataBaseBO.eliminarVista();
	     				DataBaseBO.cancelarPedidoAv();
	     				dialog.cancel();
	     				finish();
						
						
						
					}
				});

				AlertDialog alert = builder.create();
				alert.show();
			}
		});
	}

	public void OcultarTeclado(EditText editText) {

		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	public void MostrarTeclado(EditText editText) {

		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		//imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
	}

	public String ObtenerVersion() {

		String version;

		try {

			version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;

		} catch (NameNotFoundException e) {

			version = "0.0";
			Log.e("OpcionesPedidoActivity", e.getMessage(), e);
		}

		return version;
	}

	public String ObtenerImei() {

		TelephonyManager manager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		return manager.getDeviceId();
	}


	public void onDrawerClosed() {

		ListView listaOpciones = (ListView)findViewById(R.id.listaPedido);
		listaOpciones.setClickable(true);
		SetListenerListView1();

	}

	public void onDrawerOpened() {

		ListView listaOpciones = (ListView)findViewById(R.id.listaPedido);
		listaOpciones.setClickable(false);
		listaOpciones.setOnItemClickListener(null);
	}


	public void cerrarTeclado2(){

		try
		{

			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			EditText txtBusquedaCliente1 = (EditText)findViewById(R.id.txtCodigoProducto);
			imm.hideSoftInputFromWindow(txtBusquedaCliente1.getWindowToken(), 0);

		}catch(Exception e){}
	}



	public void SetListenerListView() {

		ListView listaOpciones = (ListView)findViewById(R.id.listaBusquedaProductos);

		listaOpciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				try {

					Producto producto = listaProductos.elementAt(position);


					if(existeProductoPedido(producto.codigo)){

						Util.MostrarAlertDialog(FormCambiosAutoventa.this,"El Producto ya fue Ingresado",1);	

					}else{


						((EditText)findViewById(R.id.txtCodigoProducto)).setText(producto.codigo);

						FormCambiosAutoventa.this.producto = producto;
						MostrarDialogPedido(producto.codigo);


						FormCambiosAutoventa.this.runOnUiThread(new Runnable() {

							public void run() {


								//slidingDrawerFiltros.animateClose();

								ItemListView[] items = new ItemListView[] {};

								if (listaProductos != null)
									listaProductos.removeAllElements();

								ListViewAdapter adapter1 = new ListViewAdapter(FormCambiosAutoventa.this, items, R.drawable.producto, 0);
								ListView listaBusquedaProductos = (ListView) findViewById(R.id.listaBusquedaProductos);
								listaBusquedaProductos.setAdapter(adapter1);



							}
						});


					}



				} catch (Exception e) {

					String msg = e.getMessage();
					Toast.makeText(getBaseContext(), "Error seleccionado Producto: " + msg, Toast.LENGTH_LONG).show();
				}
			}
		});
	}


	public void CargarOpcionesBusqueda() {

		String[] items = new String[] { Const.POR_CODIGO, Const.POR_NOMBRE };
		Spinner cbOpBusquedaProduc = (Spinner) findViewById(R.id.cbOpBusquedaProduc);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, items);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		cbOpBusquedaProduc.setAdapter(adapter);
		cbOpBusquedaProduc
		.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {

				String opSel = ((Spinner) findViewById(R.id.cbOpBusquedaProduc))
						.getItemAtPosition(position).toString();

				if (opSel.equals(Const.POR_NOMBRE)) {

					OpcionSeleccionada("Ingrese Parte del Nombre:",
							InputType.TYPE_CLASS_TEXT);

				} else if (opSel.equals(Const.POR_CODIGO)) {

					OpcionSeleccionada("Ingrese Parte del Codigo:",
							InputType.TYPE_CLASS_NUMBER);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}
		});
	}

	public void CargarMotivosCambio() {

		String[] items;
		Vector<String> listaItems = new Vector<String>();
		listaMC = DataBaseBO.ListaMotivosCambio( listaItems );

		if (listaItems.size() > 0) {

			items = new String[listaItems.size()];
			listaItems.copyInto(items);

		} else {

			items = new String[] {};

			if (listaMC != null)
				listaMC.removeAllElements();
		}

		Spinner cbMotivoCambio = (Spinner) dialogPedido.findViewById(R.id.cbMotivoCambio);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, items);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		cbMotivoCambio.setAdapter(adapter);

	}



	public void OpcionSeleccionada(String label, int inputType) {

		TextView lblOpBusquedaProduc = (TextView)findViewById(R.id.lblOpBusquedaProduc);
		EditText txtOpBusquedaProduc = (EditText)findViewById(R.id.txtOpBusquedaProduc);

		txtOpBusquedaProduc.setText("");
		lblOpBusquedaProduc.setText(label);
		txtOpBusquedaProduc.setInputType(inputType);

		ListViewAdapter adapter = new ListViewAdapter(this, new ItemListView[]{}, R.drawable.producto, 0);
		ListView listaBusquedaProductos = (ListView)findViewById(R.id.listaBusquedaProductos);
		listaBusquedaProductos.setAdapter(adapter);

		if (listaProductos != null)
			listaProductos.removeAllElements();
	}

	public void OnClickFormBuscarProducto(View view) {

		btnBusquedaProduc.setEnabled(false);
		OcultarTeclado((EditText)findViewById(R.id.txtOpBusquedaProduc));

		EditText txtOpBusquedaProduc = (EditText) findViewById(R.id.txtOpBusquedaProduc);
		String cadBusqueda = txtOpBusquedaProduc.getText().toString().trim();

		if (cadBusqueda.equals("")) {

			Toast.makeText(getApplicationContext(), "Debe ingresar la opcion de Busqueda", Toast.LENGTH_SHORT).show();
			txtOpBusquedaProduc.requestFocus();

		} else {

			boolean porCodigo = false;
			Spinner cbOpBusquedaProduc = (Spinner) findViewById(R.id.cbOpBusquedaProduc);
			String opBusqueda = cbOpBusquedaProduc.getSelectedItem().toString();

			if (opBusqueda.equals(Const.POR_NOMBRE)) {

				porCodigo = false;

			} else if (opBusqueda.equals(Const.POR_CODIGO)) {

				porCodigo = true;
			}

			//int index = ((Spinner) findViewById(R.id.cbLinerasProduc)).getSelectedItemPosition();
			//Linea linea =  listaLineas.elementAt(index);

			ItemListView[] items;
			Vector<ItemListView> listaItems = new Vector<ItemListView>();
			listaProductos = DataBaseBO.BuscarProductos(porCodigo, cadBusqueda, "", Main.cliente.listaPrecio, listaItems, true);

			if (listaItems.size() > 0) {

				items = new ItemListView[listaItems.size()];
				listaItems.copyInto(items);

			} else {

				items = new ItemListView[] {};

				if (listaProductos != null)
					listaProductos.removeAllElements();

				Toast.makeText(getApplicationContext(), "Busqueda sin resultados", Toast.LENGTH_SHORT).show();
			}

			ListViewAdapter adapter = new ListViewAdapter(this, items, R.drawable.prod, 0);
			ListView listaBusquedaProductos = (ListView) findViewById(R.id.listaBusquedaProductos);
			listaBusquedaProductos.setAdapter(adapter);
		}

		btnBusquedaProduc.setEnabled(true);
	}


	public void removeSetLisListener(){

		ListView listaOpciones = (ListView)findViewById(R.id.listaPedido);
		listaOpciones.setClickable(false);
		//listaOpciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {

	}




	@Override
	protected void onDestroy() {

		super.onDestroy();
	}



	public boolean existeProductoPedido(String codProd){

		boolean existe = false;

		if(productosPedido != null){

			if(productosPedido.containsKey(""+codProd+"")){

				existe = true;

			}	


		}

		return existe;

	}



	public void OnClickContado(View view){
		if(dialogResumen != null){
			if(dialogResumen.isShowing()){
				if(((RadioButton)dialogResumen.findViewById(R.id.radioContado)).isChecked()){


					((RadioButton)dialogResumen.findViewById(R.id.radioCredito)).setChecked(false);

					
				}
			}
		}


	}



	public void OnClickCredito(View view){
		if(dialogResumen != null){
			if(dialogResumen.isShowing()){
				if(((RadioButton)dialogResumen.findViewById(R.id.radioCredito)).isChecked()){


					((RadioButton)dialogResumen.findViewById(R.id.radioContado)).setChecked(false);
					
				}
			}
		}

	}



	public void MostrarDialogImprimirFactura( final boolean contado ) {


		dialogImprimirFacturaPedido = new Dialog(this);
		dialogImprimirFacturaPedido.setContentView(R.layout.dialog_imprimir_factura_pedido);
		dialogImprimirFacturaPedido.setTitle("Imprimir Factura");

		/*if( !contado ){

			((EditText)dialogImprimirFacturaPedido.findViewById(R.id.txtNumCopias)).setText( "2" );
			((EditText)dialogImprimirFacturaPedido.findViewById(R.id.txtNumCopias)).setFocusable( false );
			((EditText)dialogImprimirFacturaPedido.findViewById(R.id.txtNumCopias)).setFocusableInTouchMode( false );
		}else{
			((EditText)dialogImprimirFacturaPedido.findViewById(R.id.txtNumCopias)).setText( "1" );
			}*/
		
		((EditText)dialogImprimirFacturaPedido.findViewById(R.id.txtNumCopias)).setText( "1" );


		((Button)dialogImprimirFacturaPedido.findViewById(R.id.btnAceptarImprimirFactura)).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				String txt = ((EditText)dialogImprimirFacturaPedido.findViewById(R.id.txtNumCopias)).getText().toString();

				int numCopias = Util.ToInt( txt );

				progressDialog = ProgressDialog.show(FormCambiosAutoventa.this, "", "Por Favor Espere...\n\nProcesando Informacion!", true);
				progressDialog.show();

				SharedPreferences settings = getSharedPreferences(Const.CONFIG_IMPRESORA, MODE_PRIVATE);
				String macImpresora = settings.getString(Const.MAC_IMPRESORA, "-");

				if (macImpresora.equals("-")) {

					if( progressDialog != null ){

						progressDialog.dismiss();
					}

					Util.MostrarAlertDialog(FormCambiosAutoventa.this, "Aun no hay Impresora Establecida.\n\nPor Favor primero Configure la Impresora!");

				} else {

					imprimirTirillaExtech( macImpresora, encabezado.numero_doc, contado, numCopias );
				}
			}
		});

		((Button)dialogImprimirFacturaPedido.findViewById(R.id.btnCancelarImprimirFactura)).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				dialogImprimirFacturaPedido.cancel();

				progressDialog = ProgressDialog.show(FormCambiosAutoventa.this, "", "Enviando Informacion ...", true);
				progressDialog.show();

				//limpiar listaProductos
 				listaProductos = null;

 				//limpiar productosPedido
 				productosPedido = null;

 				
 				//limpiar cliente actual
 				cliente = null;

 				//DataBaseBO.eliminarVista();

 				DataBaseBO.cancelarPedidoAv();

 				
				sync = new Sync(FormCambiosAutoventa.this, Const.ENVIAR_PEDIDO);
				sync.start();
				
				lanzarTimerRespSync();

			}
		});

		dialogImprimirFacturaPedido.setCancelable(false);
		dialogImprimirFacturaPedido.show();
	}

	private Handler handlerFinish = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			if (progressDialog != null)
				progressDialog.cancel();

			dialogImprimirFacturaPedido.cancel();

			progressDialog = ProgressDialog.show(FormCambiosAutoventa.this, "", "Enviando Informacion ...", true);
			progressDialog.show();

			//limpiar listaProductos
				listaProductos = null;

				//limpiar productosPedido
				productosPedido = null;

				
				//limpiar cliente actual
				cliente = null;

				//DataBaseBO.eliminarVista();

				DataBaseBO.cancelarPedidoAv();

							

			sync = new Sync(FormCambiosAutoventa.this, Const.ENVIAR_PEDIDO);
			sync.start();
			
			
			lanzarTimerRespSync();
		}
	};

	private Handler handlerError = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			AlertDialog.Builder builder = new AlertDialog.Builder(FormCambiosAutoventa.this);
			builder.setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int id) {

					dialog.cancel();
				}
			});

			AlertDialog alertDialog = builder.create();
			alertDialog.setMessage(mensaje);

			//if (progressDialog != null)
			//	progressDialog.cancel();

			alertDialog.show();
		}
	};



	public void imprimirTirillaExtech(final String macPrinter, final String numeroDoc, final boolean contado, final int numCopias) {

		new Thread(new Runnable() {

			public void run() {

				mensaje = "";
				BluetoothSocket socket = null;

				try {

					Looper.prepare();

					BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

					if (bluetoothAdapter == null) {

						mensaje = "No hubo conexion con la impresora.\n\nPor Favor intente de nuevo.";

					} else if (!bluetoothAdapter.isEnabled()) {

						mensaje = "No hubo conexion con la impresora.\n\nPor Favor intente de nuevo.";

					} else {

						BluetoothDevice printer = null;
						Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

						for (BluetoothDevice device : pairedDevices) {

							String macAddress = device.getAddress();

							if (macAddress.equals(macPrinter)) {

								printer = device;
								break;
							}
						}

						if (printer == null) {

							mensaje = "No se pudo establecer la conexion con la Impresora.";

						} else {

							int state = printer.getBondState();

							if (state == BluetoothDevice.BOND_BONDED) {

								UUID uuid = UUID.fromString(FormConfigPrinter.UUID_BPP);
								socket = printer.createRfcommSocketToServiceRecord(uuid);

								if (socket != null) {            						                					           					                				

									socket.connect();

									//Thread.sleep(1000);

									synchronized ( this ) {

										wait( Const.timeWait );
									}
									

									if( contado ){

										for( int i = 0; i < ( 1 + numCopias ); i++ ){

											OutputStream stream = socket.getOutputStream();
											String strPrint = "";
											
											if( cambio ){
											
												strPrint = PrinterBO.formatoDevolucion( numeroDoc, i );
											}
											else{
												
												strPrint = PrinterBO.formatoVenta( numeroDoc, i );
											}

											//imprimir( socket, strPrint, i, ( 1 + numCopias ) );

											stream.write( strPrint.getBytes( "UTF-8" ) );                    						

											//Se asegura que los datos son enviados a la Impresora antes de cerrar la conexion.
											Thread.sleep(1500);
										}

									}
									else{

										if( !cambio ){
											
											for( int i = 0; i < 3; i++ ){

												OutputStream stream = socket.getOutputStream();
												String strPrint = PrinterBO.formatoVenta( numeroDoc, i );

												//imprimir( socket, strPrint, i, 3 );

												stream.write( strPrint.getBytes( "UTF-8" ) );

												//Se asegura que los datos son enviados a la Impresora antes de cerrar la conexion.
												Thread.sleep(1500);
											}
										}else{
											
											
											
											
											for( int i = 0; i < ( 1 + numCopias ); i++ ){

												OutputStream stream = socket.getOutputStream();
												String strPrint = "";
												
												if( cambio ){
												
													strPrint = PrinterBO.formatoDevolucion( numeroDoc, i );
												}
												else{
													
													strPrint = PrinterBO.formatoVenta( numeroDoc, i );
												}

												//imprimir( socket, strPrint, i, ( 1 + numCopias ) );

												stream.write( strPrint.getBytes( "UTF-8" ) );                    						

												//Se asegura que los datos son enviados a la Impresora antes de cerrar la conexion.
												Thread.sleep(1500);
											}
											
											
											
											
											
											
											
										}
									}

									handlerFinish.sendEmptyMessage(0);

								} else {

									mensaje = "No se pudo abrir la conexion con la Impresora.\n\nPor Favor intente de nuevo.";
								}

							} else {

								mensaje = "La Impresora: " + macPrinter + " Actualmente no esta Acoplada con el Dispositivo Movil.\n\nPor Favor configure primero la impresora.";
							}
						}
					}

					if (!mensaje.equals("")) {

						context = FormCambiosAutoventa.this;
						handlerMensaje.sendEmptyMessage(0);
					}

					Looper.myLooper().quit();

				} catch (Exception e) {

					String motivo = e.getMessage();
					Log.e(TAG, "imprimirPruebaExtech -> " + motivo, e);

					mensaje = "No se pudo ejecutar la Impresion.";

					if (motivo != null) {
						mensaje += "\n\n" + motivo;
					}

					context = FormCambiosAutoventa.this;
					handlerMensaje.sendEmptyMessage(0);

				} finally {

					try {

						if (socket != null) {
							socket.close();
						}

					} catch (Exception e) {

						Log.e(TAG, "imprimirPruebaExtech -> Cerrando socket", e);
					}
				}
			}

		}).start();
	}

	public void imprimir(final BluetoothSocket socket, final String strPrint, final int copia, final int numCopias ) {

		Thread printerThread = new Thread() {

			@Override
			public void run() {

				mensaje = "";

				try {

					if( copia == 0 ){

						synchronized(this) {

							wait( Const.timeWait );
						}
					}
					else{

						synchronized(this) {

							wait( 1500 );
						}
					}


					OutputStream stream = socket.getOutputStream();
					stream.write(strPrint.getBytes());

					if( ( copia + 1 ) == numCopias ){

						stream.flush();
						stream.close();
						socket.close();
					}

				} catch (Exception ex) {

					Log.e(TAG, "imprimir => " + ex.getMessage(), ex);
					mensaje = "No se pudo imprimir la Prueba.\n\nPor favor intente de nuevo.";
				}

				if (mensaje.equals("")) {

					if( ( copia + 1 ) == numCopias ){

						if (handlerFinish != null) {

							handlerFinish.sendEmptyMessage(0);

						} else {

							if (progressDialog != null) {
								progressDialog.cancel();
							}     
						}
					}

				} else {

					if (handlerMensaje != null) {

						//initContext();
						handlerMensaje.sendEmptyMessage(0);   

					} else {

						if (progressDialog != null) {
							progressDialog.cancel();
						}
					}
				}
			}
		};

		printerThread.start();
	}


	private static Handler handlerMensaje = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			if (progressDialog != null) {
				progressDialog.cancel();
			}

			if (context != null) {

				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {

						dialog.cancel();
					}
				});

				AlertDialog alertDialog = builder.create();
				alertDialog.setMessage(mensaje);
				alertDialog.show();
			}
		}
	};

	public void SetKeyListener() {

		//EditText txtCodigoProducto = (EditText)findViewById(R.id.txtCodigoProducto);
		txtCodigoProducto.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {

				if (keyCode == KeyEvent.KEYCODE_ENTER) {


					OcultarTeclado(txtCodigoProducto);

					
						String codigoProducto = txtCodigoProducto.getText().toString().trim();

						if (codigoProducto.equals("")) {

							txtCodigoProducto.setError("Ingrese el codigo");
							txtCodigoProducto.requestFocus();

						} else {


							if(existeProductoPedido(codigoProducto)){

								Util.MostrarAlertDialog(FormCambiosAutoventa.this,"El Producto ya fue Ingresado",1);

							}else{

								CargarProducto(codigoProducto);
								txtCodigoProducto.setError(null);
							}
						}
				

					return true;
				}

				return false;
			}
		});
	}

	public void SetKeyListenerDialogProd() {

		EditText txtCodigoProducto = (EditText) dialogPedido.findViewById(R.id.txtCantidadProc);
		txtCodigoProducto.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {

				if (keyCode == KeyEvent.KEYCODE_ENTER) {

					if( pVezCant1 ){

						agregarProducto();
						pVezCant1 = false;
					}

					return true;
				}

				return false;
			}
		});

	
	}

	private void agregarProducto() {

		String cant = ((EditText) dialogPedido
				.findViewById(R.id.txtCantidadProc)).getText().toString();

		int cantidad = Util.ToInt(cant);

		if (!(cantidad > 0)) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					dialogPedido.getContext());
			builder.setMessage("Debe ingresar la cantidad")
					.setCancelable(false)
					.setPositiveButton("Aceptar",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int id) {

									((EditText) dialogPedido
											.findViewById(R.id.txtCantidadProc))
											.requestFocus();
									dialog.cancel();
								}
							});

			AlertDialog alert = builder.create();
			alert.show();

		} else {

			if (cantidad > producto.cantidadInv) {

				String msg = "";
				if (producto.cantidadInv == 0)
					msg = "El producto: " + producto.codigo
							+ " no tiene Inventario";
				else
					msg = "La cantidad ingresada supera el Inventario del Producto: "
							+ producto.cantidadInv;

				AlertDialog.Builder builder = new AlertDialog.Builder(
						dialogPedido.getContext());
				builder.setMessage(msg)
						.setCancelable(false)
						.setPositiveButton("Aceptar",
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int id) {

										((EditText) dialogPedido
												.findViewById(R.id.txtCantidadProc))
												.requestFocus();
										dialog.cancel();
									}
								});

				AlertDialog alert = builder.create();
				alert.show();

			} else {

				String codMotivo = "";

				if (cambio) {

					int index = ((Spinner) dialogPedido
							.findViewById(R.id.cbMotivoCambio))
							.getSelectedItemPosition();

					if (index == AdapterView.INVALID_POSITION
							&& listaMC.size() > 0) {

						Util.MostrarAlertDialog(FormCambiosAutoventa.this,
								"Debe seleccionar un motivo valido para el cambio");
						return;
					} else {

						codMotivo = listaMC.elementAt(index).codigo;
					}
				}

				// float descuento =
				// Main.cliente.descuento;//Util.ToInt(strDescuento);
				// float descuento =
				// (float)DataBaseBO.obtenerDescuentoNuevo(producto,
				// Main.cliente);;
				AgregarProductoPedido(cantidad, 0, codMotivo);

			}

		}

	}

	  //Dos Minutos de Espera para Cerrar el progressDialog
  	public final static long time = 1 * 60 * 1000;

    
	public void lanzarTimerRespSync() {

		Timer timer = new Timer();
		TaskSync taskSync = new TaskSync();
		timer.schedule(taskSync, time);
	}

	private class TaskSync extends TimerTask {

		@Override
		public void run() {

			if (progressDialog != null) {
				progressDialog.cancel();
			}

			if (sync.activo) {

				cancelarSync();

				FormCambiosAutoventa.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

						try {

							AlertDialog.Builder builder = new AlertDialog.Builder(
									FormCambiosAutoventa.this);
							builder.setMessage(
									"No se Logro Registrar Informacion. Problema Temporal de Conectividad a Internet. Por Favor Intente mas Tarde")

									.setCancelable(false)
									.setPositiveButton(
											"Aceptar",
											new DialogInterface.OnClickListener() {

												public void onClick(
														DialogInterface dialog,
														int id) {

													
													//limpiar listaProductos
								     				listaProductos = null;

								     				//limpiar productosPedido
								     				productosPedido = null;

								     				
								     				//limpiar cliente actual
								     				cliente = null;

								     				//DataBaseBO.eliminarVista();

								     				DataBaseBO.cancelarPedidoAv();

								     				dialog.cancel();
								     				
								     				
								     				finish();
													
													

												}
											});

							AlertDialog alert = builder.create();
							alert.show();
							alert.setOnKeyListener(listener);

						} catch (Exception e) {
						}

					}
				});

			} else {

				cancelarSync();

			}

		}
	}

	public void cancelarSync() {

		try {

			if (sync != null) {

				sync.activo = false;

				if (sync.isAlive()) {
					sync.stop();
				}
			}

		} catch (Exception e) {

		} finally {

			sync = null;
		}
	}

	DialogInterface.OnKeyListener listener = new DialogInterface.OnKeyListener() {

		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_SEARCH) {
				return true; // Pretend we processed it
			}
			return false; // Any other keys are still processed as normal
		}
	};
	
	

public void setListenerRadioButtons(){
	
	((RadioButton)(dialogResumen.findViewById(R.id.radioContado))).setOnClickListener(new OnClickListener() {

		public void onClick(View v) {


			if(dialogResumen != null){
				if(dialogResumen.isShowing()){
					if(((RadioButton)dialogResumen.findViewById(R.id.radioContado)).isChecked()){


						((RadioButton)dialogResumen.findViewById(R.id.radioCredito)).setChecked(false);

					}
				}
			}


		}
	});



	((RadioButton)(dialogResumen.findViewById(R.id.radioCredito))).setOnClickListener(new OnClickListener() {

		public void onClick(View v) {

			if(dialogResumen != null){
				if(dialogResumen.isShowing()){
					if(((RadioButton)dialogResumen.findViewById(R.id.radioCredito)).isChecked()){


						((RadioButton)dialogResumen.findViewById(R.id.radioContado)).setChecked(false);
						
					}
				}
			}



		}
	});
	
	
}



/**
 * metodo que crea un toast (mensaje informativo) en la pantalla para indicar diferentes situaciones
 * @param text, texto a mostrar
 * @by JICZ
 */
private void mostrarToast(CharSequence text) {

	/*captura del contexto de la aplicacion*/
	Context context = getApplicationContext();

	/*mostrar el mensaje en la parte inferior izquierda*/
	Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);	
	toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 2, 2);
	toast.show();
}




/**
 * metodo para vargar el imei del dispositivo.
 * @return
 */
public String obtenerImei() {

	String deviceId;
	TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
	deviceId = manager.getDeviceId();

	if (deviceId.equals("")) {
		WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);			
		if (wifiManager != null) {
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			if (wifiInfo != null) {
				String mac = wifiInfo.getMacAddress();
				if (mac != null) {
					deviceId = mac.replace(":", "").toUpperCase(Locale.getDefault());
				}
			}
		}
	}
	return deviceId;
}


/**
 * metodo para confirmar si el usuario desea imprimir o no la tirilla de reporte de un pedido
 */
private void mostrarAlertParaConfirmarSiImprimir() {

	//solicitar confirmacion para terminar el pedido.
	AlertDialog alertDialog;

	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	builder.setCancelable(false)

	.setPositiveButton("Aceptar",
			new DialogInterface.OnClickListener() {

		public void onClick(DialogInterface dialog, int id) {
		
			progressDialog = ProgressDialog.show(FormCambiosAutoventa.this, "","Enviando Informacion...", true);
			progressDialog.show();
			progressDialog.setIndeterminate(true);
			progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_icon_drawable_animation));

			
			sync = new Sync(FormCambiosAutoventa.this, Const.ENVIAR_PEDIDO);
			sync.start();
			
			dialog.cancel();
		}
	});

	alertDialog = builder.create();
	alertDialog.setMessage("Enviar Informacion");
	alertDialog.show();		

}


/**
 * Metodo para mostrar un DialogDatePicker, donde el usuario define las fechas de cobro o de entrega de un pedido.
 * @param editText, edit donde se mostrara la fecha ingresada por el usuario.
 * 
 * @by JICZ
 */
protected void mostrarDataPicker(final EditText editText) {

	/*Capturar la fecha actual que sera mostrada por primera vez en el datepicker*/
	final Calendar c = Calendar.getInstance();
	int year = c.get(Calendar.YEAR);
	int month = c.get(Calendar.MONTH);
	int day = c.get(Calendar.DAY_OF_MONTH);

	/* inicializar el dialog picker para que el usuario ingrese la fecha deseada, se define la fecha actual capturada*/
	DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			String fecha = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
			SimpleDateFormat formato = new SimpleDateFormat(FORMATO_FECHA, Locale.getDefault());
			Date fechas;
			try {
				fechas = formato.parse(fecha);
				fecha = formato.format(fechas);
				editText.setText(fecha);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}, year, month, day);

	//mostrar el el dialog datepicker.
	dpd.show();
}





	

}



