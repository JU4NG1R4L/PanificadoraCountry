package co.com.panificadoracountry;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import co.com.BusinessObject.DataBaseBO;
import co.com.BusinessObject.PrinterBO;
import co.com.Conexion.Sync;
import co.com.DataObject.Cliente;
import co.com.DataObject.ControlImpresion;
import co.com.DataObject.Detalle;
import co.com.DataObject.DetalleImprimir;
import co.com.DataObject.DetalleProducto;
import co.com.DataObject.Encabezado;
import co.com.DataObject.ItemListView;
import co.com.DataObject.Producto;
import co.com.DataObject.Usuario;
import co.com.woosim.printer.WoosimR240;

public class FormPedidosInformacion extends Activity implements OnClickListener{
	/*
	 * Usado para indicar que una tirilla de impresora es una copia de la
	 * original.
	 */
	private static final boolean COPIA = true;
	int tipoDeClienteSeleccionado = 1;
	Vector<Encabezado> listaPedidos;
	Dialog dialogResumen;
	public long tiempoClick1 = 0;
	Hashtable<String, Detalle> detallePedido;
	Encabezado encabezadoPedido;
	ProgressDialog progressDialog;
	String printerName = "";
	public final static String CONFIG_IMPRESORA = "PRINTER";
	public final static String MAC_IMPRESORA = "MAC";
	public final static String LABEL_IMPRESORA = "LABEL";
	private static String mensaje;
	private static Activity context;
	private static String TAG = FormEstadisticaPedidos.class.getName();
	/**
	 * Referencia a la impresora WSP-R240 WOOSIM.
	 */
	private WoosimR240 wR240 = null;
	Dialog dialogIngresarCodigoVerificacion;
	
	Encabezado encabezadoSeleccionado;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_pedidos_informacion);
		cargarInformacionDePedidos();
	}

	public void onResume(){
		super.onResume();
		if (Main.usuario == null || Main.usuario.codigoVendedor == null)
			DataBaseBO.CargarInfomacionUsuario();
		DataBaseBO.setAppAutoventa();
	}

	public void cargarInformacionDePedidos(){
		DataBaseBO.setAppAutoventa();
		listaPedidos = DataBaseBO.CargarPedidosRealizados(Main.usuario.fechaConsecutivo, 1);
		if (listaPedidos.size() > 0){
			TableLayout table = new TableLayout(this);
			table.setBackgroundColor(Color.WHITE);
			String[] cabecera = { "  ", "NroDoc", "Nombre", "Valor", "Fecha","Anulado","Sincronizado" };
			Util.Headers(table, cabecera, this);
			HorizontalScrollView scroll = (HorizontalScrollView) findViewById(R.id.scrollPedidosRealizados);
			scroll.removeAllViews();
			scroll.addView(table);
			int pos = 0;
			for (Encabezado encabezado : listaPedidos){
				
				
				if(encabezado.valor_neto == 0) {
					
					
				
				}else {
					
					
					
				}
				
				
				
				pos++;
				TextView textViewAux;
				TableRow fila = new TableRow(this);
				ImageView imgAux = new ImageView(this);
				// imgAux.setBackgroundDrawable(getResources().getDrawable(R.id.spDocumentos));
				imgAux.setImageResource(R.drawable.busqueda);
				imgAux.setOnClickListener(this);
				imgAux.setTag(pos);
				imgAux.setAdjustViewBounds(true);
				fila.addView(imgAux);
				textViewAux = new TextView(this);
				textViewAux.setText(encabezado.numero_doc + "\n");
				textViewAux.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
				
				
				
				if(encabezado.valor_neto == 0) {
					
					textViewAux.setTextColor(Color.argb(255, 0xb3, 0, 0));
					
				}else {
					
					textViewAux.setTextColor(Color.argb(255, 0, 0, 0));
					
				}
				
				// textViewAux.setTextSize(19);
				// textViewAux.setBackgroundDrawable(this.getResources().getDrawable(android.R.drawable.editbox_dropdown_light_frame));
				textViewAux.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.table_cell_row_1));
				fila.addView(textViewAux);
				textViewAux = new TextView(this);
				textViewAux.setText(encabezado.nombre_cliente + "\n");
				textViewAux.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
				if(encabezado.valor_neto == 0) {
					
					textViewAux.setTextColor(Color.argb(255, 0xb3, 0, 0));
					
				}else {
					
					textViewAux.setTextColor(Color.argb(255, 0, 0, 0));
					
				}
				// textViewAux.setTextSize(19);
				// textViewAux.setBackgroundDrawable(this.getResources().getDrawable(android.R.drawable.editbox_dropdown_light_frame));
				textViewAux.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.table_cell_row_1));
				fila.addView(textViewAux);
				textViewAux = new TextView(this);
				textViewAux.setText(Util.SepararMiles(Util.RedondearFit(Util.QuitarE("" + encabezado.valor_neto), 2)) + "\n");
				textViewAux.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
				if(encabezado.valor_neto == 0) {
					
					textViewAux.setTextColor(Color.argb(255, 0xb3, 0, 0));
					
				}else {
					
					textViewAux.setTextColor(Color.argb(255, 0, 0, 0));
					
				}
				// textViewAux.setTextSize(19);
				// textViewAux.setBackgroundDrawable(this.getResources().getDrawable(android.R.drawable.editbox_dropdown_light_frame));
				textViewAux.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.table_cell_row_1));
				fila.addView(textViewAux);
				textViewAux = new TextView(this);
				textViewAux.setText("" + encabezado.fecha + "\n");
				textViewAux.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
				if(encabezado.valor_neto == 0) {
					
					textViewAux.setTextColor(Color.argb(255, 0xb3, 0, 0));
					
				}else {
					
					textViewAux.setTextColor(Color.argb(255, 0, 0, 0));
					
				}
				// textViewAux.setTextSize(19);
				// textViewAux.setBackgroundDrawable(this.getResources().getDrawable(android.R.drawable.editbox_dropdown_light_frame));
				textViewAux.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.table_cell_row_1));
				fila.addView(textViewAux);
				
				
				
				
				textViewAux = new TextView(this);
				
				if(encabezado.valor_neto == 0) {
				
					textViewAux.setText("Si"+  "\n");
				
				}else {
					
					textViewAux.setText("No"+  "\n");
					
				}
				
				textViewAux.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
				if(encabezado.valor_neto == 0) {
					
					textViewAux.setTextColor(Color.argb(255, 0xb3, 0, 0));
					
				}else {
					
					textViewAux.setTextColor(Color.argb(255, 0, 0, 0));
					
				}
				// textViewAux.setTextSize(19);
				// textViewAux.setBackgroundDrawable(this.getResources().getDrawable(android.R.drawable.editbox_dropdown_light_frame));
				textViewAux.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.table_cell_row_1));
				
				fila.addView(textViewAux);
				
				
				textViewAux = new TextView(this);
				textViewAux.setText(encabezado.sync + "\n");
				textViewAux.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
				if(encabezado.valor_neto == 0) {
					
					textViewAux.setTextColor(Color.argb(255, 0xb3, 0, 0));
					
				}else {
					
					textViewAux.setTextColor(Color.argb(255, 0, 0, 0));
					
				}
				// textViewAux.setTextSize(19);
				// textViewAux.setBackgroundDrawable(this.getResources().getDrawable(android.R.drawable.editbox_dropdown_light_frame));
				textViewAux.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.table_cell_row_1));
				
				
				fila.addView(textViewAux);
				
				
				
				
				table.addView(fila);
			}
		}
	}

	public void onClick(View view){
		String cadena = view.getTag().toString();
		int pos = Integer.parseInt(cadena);
		encabezadoPedido = listaPedidos.elementAt(pos - 1);
		Date date = new Date();
		long tiempoClick2 = date.getTime();
		long dif1 = tiempoClick2 - tiempoClick1;
		if (dif1 < 0){
			dif1 = dif1 * (-1);
		}
		if (dif1 > 3000){
			tiempoClick1 = tiempoClick2;
			MostarDialogResumen(encabezadoPedido);
		}
	}

	public void MostarDialogResumen(final Encabezado encabezado){
		dialogResumen = new Dialog(this);
		dialogResumen.requestWindowFeature(Window.FEATURE_LEFT_ICON);
		dialogResumen.setContentView(R.layout.resumen_pedido2);
		dialogResumen.setTitle("Resumen Pedido");
		if (Const.tipoAplicacion == Const.PREVENTA){
			((Button) dialogResumen.findViewById(R.id.btnImprimirPedido)).setVisibility(View.GONE);
		}else{
			((Button) dialogResumen.findViewById(R.id.btnImprimirPedido)).setVisibility(View.VISIBLE);
		}
		encabezado.sub_total = 0;
		encabezado.total_iva = 0;
		encabezado.valor_descuento = 0;
		ItemListView itemListView;
		Cliente cliente = DataBaseBO.buscarClientePorEncabezadoVenta(encabezado.numero_doc);
		detallePedido = DataBaseBO.CargarDetallePedidoAv(encabezado.numero_doc, cliente.listaPrecio);
		// Enumeration<Detalle> e = detallePedido.elements();
		Vector<ItemListView> datosPedido = new Vector<ItemListView>();
		List<Detalle> tmp = Collections.list(detallePedido.elements());
		Util.quickSortListaDetalle(tmp, 0, tmp.size() - 1);
		int i = 0;
		// while (e.hasMoreElements()) {
		while (i < tmp.size()){
			// Detalle detalle = e.nextElement();
			Detalle detalle = tmp.get(i);
			itemListView = new ItemListView();
			i++;
			float sub_total = detalle.cantidad * detalle.precio;
			float valor_descuento = sub_total * detalle.descuento_autorizado ;
			float valor_iva = (sub_total - valor_descuento) * (detalle.iva / 100);
			encabezado.sub_total += sub_total;
			encabezado.total_iva += valor_iva;
			encabezado.valor_descuento += valor_descuento;
			
			if (detalle.tipo_pedido == 1){
				itemListView.titulo = detalle.codigo_producto + " -  " + detalle.desc_producto;
				itemListView.subTitulo = "Precio: $" + detalle.precio + " - Iva: $" + detalle.iva + " - Cant: " + detalle.cantidad + " - Desc: " + detalle.descuento_autorizado + "%";
			}else{
				if (detalle.tipo_pedido == 2){
					itemListView.titulo = detalle.codigo_producto + " - " + detalle.desc_producto;
					itemListView.subTitulo = "Precio: $" + detalle.precio + " - Iva: $" + detalle.iva + " - Cant: " + detalle.cantidad + " - Desc: " + detalle.descuento_autorizado + "%";
				}else{
					if (detalle.tipo_pedido == 3){
						itemListView.titulo = detalle.codigo_producto + " -  " + detalle.desc_producto;
						itemListView.subTitulo = "Precio: $" + detalle.precio + " - Iva: $" + detalle.iva + " - Cant: " + detalle.cantidad + " - Desc: " + detalle.descuento_autorizado + "%";
					}else{
						if (detalle.tipo_pedido == 4){
							itemListView.titulo = detalle.codigo_producto + " -  " + detalle.desc_producto;
							itemListView.subTitulo = "Precio: $" + detalle.precio + " - Iva: $" + detalle.iva + " - Cant: " + detalle.cantidad + " - Desc: " + detalle.descuento_autorizado + "%";
						}else{}
					}
				}
			}
			datosPedido.addElement(itemListView);
		}
		ItemListView[] listaItems = new ItemListView[datosPedido.size()];
		datosPedido.copyInto(listaItems);
		encabezado.valor_neto = encabezado.sub_total + encabezado.total_iva - encabezado.valor_descuento;
		encabezado.str_valor_neto = Util.SepararMiles(Util.Redondear(Util.QuitarE("" + encabezado.valor_neto), 0));
		if (encabezado.sync == 0){
			((Button) dialogResumen.findViewById(R.id.btnCancelarPedidoResumenPedido)).setVisibility(View.VISIBLE);
		}else{
			((Button) dialogResumen.findViewById(R.id.btnCancelarPedidoResumenPedido)).setVisibility(View.GONE);
		}
		
		
		if (encabezado.valor_neto == 0){
			((Button) dialogResumen.findViewById(R.id.btnCancelarPedidoResumenPedido)).setVisibility(View.GONE);
		}
		
		
		
		
		((TextView) dialogResumen.findViewById(R.id.lblCliente)).setText(encabezado.nombre_cliente);
		// ((TextView)
		// dialogResumen.findViewById(R.id.lblSubTotal)).setText(Util.SepararMiles(Util.Redondear(Util.QuitarE(""
		// + encabezado.sub_total), 2)));
		// ((TextView)
		// dialogResumen.findViewById(R.id.lblTotalIva)).setText(Util.SepararMiles(Util.Redondear(Util.QuitarE(""
		// + encabezado.total_iva), 2)));
		// ((TextView)
		// dialogResumen.findViewById(R.id.lblTotalDescuento)).setText(Util.SepararMiles(Util.Redondear(Util.QuitarE(""
		// + encabezado.valor_descuento), 2)));
		((TextView) dialogResumen.findViewById(R.id.lblValorNetoPedido)).setText(encabezado.str_valor_neto);
		ListViewAdapter adapter = new ListViewAdapter(this, listaItems, R.drawable.compra, 0);
		ListView listaResumenPedido = (ListView) dialogResumen.findViewById(R.id.listaResumenPedido);
		listaResumenPedido.setAdapter(adapter);
		((Button) dialogResumen.findViewById(R.id.btnCancelarPedidoResumenPedido)).setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				mensaje = "Esta seguro de Cancelar el Pedido?";
				AlertDialog.Builder builder = new AlertDialog.Builder(FormPedidosInformacion.this);
				builder.setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int id){
						// Se cancelar el pedido y colocar las cantidades
						// pedidas en 0
						/*if (DataBaseBO.CancelarPedidoEstadisticas(encabezado)){
							devolverTotalCantidadInventario(detallePedido);
							Util.MostrarAlertDialog(FormPedidosInformacion.this, "Pedido Cancelado correctamente.");
							cargarInformacionDePedidos();
						}else{
							Util.MostrarAlertDialog(FormPedidosInformacion.this, "No se puede cancelar el pedido, ya tiene relacionado un recaudo.");
						}*/
						
						encabezadoSeleccionado = encabezado;
						
						mostrarDialogoIngresarCodigoVerificacion();
						
						dialog.cancel();
						
					}
				}).setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int id){
						dialog.cancel();
						dialogResumen.cancel();
					}
				});
				AlertDialog alertDialog = builder.create();
				alertDialog.setMessage(mensaje);
				alertDialog.show();
			}
		});
		((Button) dialogResumen.findViewById(R.id.btnAceptarResumenPedido)).setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				dialogResumen.cancel();
			}
		});
		((Button) dialogResumen.findViewById(R.id.btnImprimirPedido)).setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				progressDialog = ProgressDialog.show(FormPedidosInformacion.this, "", "Por Favor Espere...\n\nProcesando Informacion!", true);
				progressDialog.show();
				SharedPreferences settings = getSharedPreferences(Const.CONFIG_IMPRESORA, MODE_PRIVATE);
				String macImpresora = settings.getString(Const.MAC_IMPRESORA, "-");
				if (macImpresora.equals("-")){
					if (progressDialog != null){
						progressDialog.dismiss();
					}
					Util.MostrarAlertDialog(FormPedidosInformacion.this, "Aun no hay Impresora Establecida.\n\nPor Favor primero Configure la Impresora!");
				}else{
					Usuario usuario = DataBaseBO.CargarUsuario();
					Cliente cliente = DataBaseBO.buscarClientePorEncabezadoVenta(encabezado.numero_doc);
					imprimir_WSP_R240(macImpresora, encabezado.numero_doc, cliente, usuario);
					// imprimirTirilla(macImpresora, encabezado.numero_doc);
				}
			}
		});
		dialogResumen.setCancelable(false);
		dialogResumen.show();
		dialogResumen.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.cliente);
	}

	/**
	 * metodo que devuelve las cantidades del inventario
	 * 
	 * @param detallePedidoTemp
	 */
	private void devolverTotalCantidadInventario(Hashtable<String, Detalle> detallePedidoTemp){
		List<Detalle> listaDetallePdto = Collections.list(detallePedidoTemp.elements());
		for (Detalle det : listaDetallePdto){
			DataBaseBO.sumarLote((int) det.cantidad, det.lote, det.codigo_producto, det.fechaFabricacion, det.fechaVencimiento);
		}
	}

	private Handler handlerError = new Handler(){
		@Override
		public void handleMessage(Message msg){
			AlertDialog.Builder builder = new AlertDialog.Builder(FormPedidosInformacion.this);
			builder.setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id){
					dialog.cancel();
				}
			});
			AlertDialog alertDialog = builder.create();
			alertDialog.setMessage(mensaje);
			if (progressDialog != null)
				progressDialog.cancel();
			alertDialog.show();
		}
	};

	public void imprimirTirilla(final String macPrinter, final String numeroDoc){
		new Thread(new Runnable(){
			public void run(){
				mensaje = "";
				BluetoothSocket socket = null;
				try{
					Looper.prepare();
					BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
					if (bluetoothAdapter == null){
						mensaje = "No hubo conexion con la impresora.\n\nPor Favor intente de nuevo.";
					}else if (!bluetoothAdapter.isEnabled()){
						mensaje = "No hubo conexion con la impresora.\n\nPor Favor intente de nuevo.";
					}else{
						BluetoothDevice printer = null;
						Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
						for (BluetoothDevice device : pairedDevices){
							String macAddress = device.getAddress();
							if (macAddress.equals(macPrinter)){
								printer = device;
								break;
							}
						}
						if (printer == null){
							mensaje = "No se pudo establecer la conexion con la Impresora.";
						}else{
							int state = printer.getBondState();
							if (state == BluetoothDevice.BOND_BONDED){
								UUID uuid = UUID.fromString(FormConfigPrinter.UUID_BPP);
								socket = printer.createRfcommSocketToServiceRecord(uuid);
								if (socket != null){
									socket.connect();
									// OutputStream stream =
									// socket.getOutputStream();
									String strPrint = PrinterBO.formatoVenta(numeroDoc, 1);
									imprimir(socket, strPrint);
								}else{
									mensaje = "No se pudo abrir la conexion con la Impresora.\n\nPor Favor intente de nuevo.";
								}
							}else{
								mensaje = "La Impresora: " + macPrinter + " Actualmente no esta Acoplada con el Dispositivo Movil.\n\nPor Favor configure primero la impresora.";
							}
						}
					}
					if (!mensaje.equals("")){
						context = FormPedidosInformacion.this;
						handlerMensaje.sendEmptyMessage(0);
					}
					Looper.myLooper().quit();
				}
				catch (Exception e){
					String motivo = e.getMessage();
					Log.e(TAG, "imprimirPruebaExtech -> " + motivo, e);
					mensaje = "No se pudo ejecutar la Impresion.";
					if (motivo != null){
						mensaje += "\n\n" + motivo;
					}
					context = FormPedidosInformacion.this;
					handlerMensaje.sendEmptyMessage(0);
				}
				finally{
					/*
					 * try {
					 * 
					 * if (socket != null) { socket.close(); }
					 * 
					 * } catch (Exception e) {
					 * 
					 * Log.e(TAG, "imprimirPruebaExtech -> Cerrando socket", e);
					 * }
					 */
				}
			}
		}).start();
	}

	public void imprimir(final BluetoothSocket socket, final String strPrint){
		Thread printerThread = new Thread(){
			@Override
			public void run(){
				mensaje = "";
				try{
					synchronized (this){
						wait(Const.timeWait);
					}
					OutputStream stream = socket.getOutputStream();
					stream.write(strPrint.getBytes());
					stream.flush();
					// wait(1500);
					Thread.sleep(1500);
					stream.close();
					socket.close();
				}
				catch (Exception ex){
					Log.e(TAG, "imprimir => " + ex.getMessage(), ex);
					mensaje = "No se pudo imprimir la Prueba.\n\nPor favor intente de nuevo.";
				}
				if (mensaje.equals("")){
					if (handlerFinish != null){
						handlerFinish.sendEmptyMessage(0);
					}else{
						if (progressDialog != null){
							progressDialog.cancel();
						}
					}
				}else{
					if (handlerMensaje != null){
						// initContext();
						handlerMensaje.sendEmptyMessage(0);
					}else{
						if (progressDialog != null){
							progressDialog.cancel();
						}
					}
				}
			}
		};
		printerThread.start();
	}

	private Handler handlerFinish = new Handler(){
		@Override
		public void handleMessage(Message msg){
			if (progressDialog != null)
				progressDialog.cancel();
		}
	};
	private Handler handlerMensaje = new Handler(){
		@Override
		public void handleMessage(Message msg){
			if (progressDialog != null){
				progressDialog.cancel();
			}
			if (context != null){
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int id){
						dialog.cancel();
					}
				});
				AlertDialog alertDialog = builder.create();
				alertDialog.setMessage(mensaje);
				alertDialog.show();
			}
		}
	};

	/**
	 * Imprimir en la impresora WR-240, debidamente establecida con
	 * anterioridad.
	 * 
	 * @param mac
	 * @param numeroDoc
	 * @param cliente
	 * @param usuario
	 */
	private void imprimir_WSP_R240(final String mac, final String numeroDoc, final Cliente cliente, final Usuario usuario){
		new Thread(new Runnable(){
			@Override
			public void run(){
				Looper.prepare();
				SharedPreferences settings = getSharedPreferences(CONFIG_IMPRESORA, MODE_PRIVATE);
				final String macImpresora = settings.getString(MAC_IMPRESORA, "-");
				if (macImpresora.equals("-")){
					progressDialog.dismiss();
					runOnUiThread(new Runnable(){
						@Override
						public void run(){
							Toast.makeText(FormPedidosInformacion.this, "Aun no hay Impresora Predeterminada.\n\nPor Favor primero Configure la Impresora!", Toast.LENGTH_SHORT).show();
						}
					});
				}else{
					if (wR240 == null){
						wR240 = new WoosimR240(FormPedidosInformacion.this);
					}
					int conect = wR240.conectarImpresora(macImpresora);
					switch (conect){
					case 1:
						ControlImpresion control = new ControlImpresion();
						/*
						 * lista que contendra los detalles que seran impresos
						 */
						
						DetalleImprimir detalleImprimir = new DetalleImprimir();
						ArrayList<DetalleProducto> listaDetalleProductos = new ArrayList<DetalleProducto>();
						String numeroFactura = DataBaseBO.cargarDetallesImprimir(numeroDoc, detalleImprimir, listaDetalleProductos, control, cliente.listaPrecio);
						if (detalleImprimir != null && detalleImprimir.getEncabezado() != null && listaDetalleProductos != null && !listaDetalleProductos.isEmpty()){
							wR240.generarEncabezadoTirilla(numeroFactura, cliente, control.fechaVenta, control.original);
							wR240.generarDetalleTirilla(detalleImprimir);
							int succes = wR240.imprimirBuffer(true);
							if ((control.original) && succes == 1){
								DataBaseBO.marcarComoCopiaProximaImpresion(numeroDoc, usuario);
							}
						}else{
							progressDialog.dismiss();
							runOnUiThread(new Runnable(){
								@Override
								public void run(){
									Toast.makeText(FormPedidosInformacion.this, "Aun no hay datos para imprimir, revise el pedido.", Toast.LENGTH_SHORT).show();
								}
							});
						}
						break;
					case -2:
						runOnUiThread(new Runnable(){
							@Override
							public void run(){
								Toast.makeText(FormPedidosInformacion.this, "-2 fallo conexion", Toast.LENGTH_SHORT).show();
							}
						});
						break;
					case -8:
						progressDialog.dismiss();
						runOnUiThread(new Runnable(){
							@Override
							public void run(){
								Util.MostrarAlertDialog(FormPedidosInformacion.this, "Bluetooth apagado. Por favor habilite el bluetoth para imprimir.");
							}
						});
						break;
					default:
						runOnUiThread(new Runnable(){
							@Override
							public void run(){
								Toast.makeText(FormPedidosInformacion.this, "error desconocido", Toast.LENGTH_SHORT).show();
							}
						});
						break;
					}
					try{
						Thread.sleep(Const.timeWait);
					}
					catch (InterruptedException e){
						e.printStackTrace();
					}
					if (wR240 != null){
						wR240.desconectarImpresora();
						progressDialog.dismiss();
						handlerFinish.sendEmptyMessage(0);
					}
				}
				Looper.myLooper().quit();
			}
		}).start();
	}
	
	
	
	
	public void mostrarDialogoIngresarCodigoVerificacion() {
		
		
		if(dialogIngresarCodigoVerificacion != null)
			if(dialogIngresarCodigoVerificacion.isShowing())
				dialogIngresarCodigoVerificacion.dismiss();
		
		
		dialogIngresarCodigoVerificacion = new Dialog(this);
		dialogIngresarCodigoVerificacion.requestWindowFeature(Window.FEATURE_LEFT_ICON);
		dialogIngresarCodigoVerificacion.setContentView(R.layout.dialog_ingresar_codigo_verificacion);
		dialogIngresarCodigoVerificacion.setTitle("Codigo de Autorizacion");
		
		EditText etCodigoVerificacion = (EditText)dialogIngresarCodigoVerificacion.findViewById(R.id.etCodigoVerificacion);
		etCodigoVerificacion.setText("");
		
		String mensaje = "<b>Ingrese un codigo de Autorizacion?</b><br /><br />";
		mensaje += "";
		((TextView) dialogIngresarCodigoVerificacion.findViewById(R.id.lblMsgTerminarDia)).setText(Html.fromHtml(mensaje));
		((Button) dialogIngresarCodigoVerificacion.findViewById(R.id.btnAceptar)).setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				
				EditText etCodigoVerificacion = (EditText)dialogIngresarCodigoVerificacion.findViewById(R.id.etCodigoVerificacion);
				String code = etCodigoVerificacion.getText().toString();
				
				if(code.equals("")) {
					
					Util.MostrarAlertDialog(FormPedidosInformacion.this, "Por favor ingrese el codigo de autorizacion");
					
				}else {
					
					
					DataBaseBO.ValidarUsuario();
					if(DataBaseBO.existeCodigoVerificacion(code,Main.usuario.codigoVendedor)) {
						
					if (DataBaseBO.CancelarPedidoEstadisticas(encabezadoSeleccionado)){
						
						devolverTotalCantidadInventario(detallePedido);
						Util.MostrarAlertDialog(FormPedidosInformacion.this, "Pedido Cancelado correctamente.");
						cargarInformacionDePedidos();
						
						
						String fechaMovil = Util.FechaActual("yyyy-MM-dd HH:mm:ss");
						
						DataBaseBO.GuardarCodigoAutorizacionIngresado(encabezadoSeleccionado.numero_doc, Main.usuario.codigoVendedor, code, fechaMovil);
						
						dialogResumen.cancel();
						dialogIngresarCodigoVerificacion.cancel();
						
					}else{
						Util.MostrarAlertDialog(FormPedidosInformacion.this, "No se puede cancelar el pedido, ya tiene relacionado un recaudo.");
					}
						
						
					}else {
					
						
						Util.MostrarAlertDialog(FormPedidosInformacion.this, "El Codigo de autorizacion ingresado no es valido");
						
					}
					
				}
				
				
			}
		});
		((Button) dialogIngresarCodigoVerificacion.findViewById(R.id.btnCancelar)).setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				dialogIngresarCodigoVerificacion.cancel();
			}
		});
		dialogIngresarCodigoVerificacion.setCancelable(false);
		dialogIngresarCodigoVerificacion.show();
		dialogIngresarCodigoVerificacion.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.op_terminar_dia);
		
		
	}
	
	
}
