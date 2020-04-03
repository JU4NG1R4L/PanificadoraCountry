package co.com.panificadoracountry;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import co.com.panificadoracountry.R;
import co.com.woosim.printer.WoosimR240;
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
import co.com.DataObject.Usuario;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;





/**
 * @author johnp
 *
 */
/**
 * @author johnp
 *
 */
public class FormEstadisticaPedidos extends Activity {

	Dialog dialogResumen;
	Vector<Encabezado> listaPedidos;
	int pedido = 1;
	public long tiempoClick1 = 0;
	ProgressDialog progressDialog;
	Encabezado encabezadoS;
	Hashtable<String, Detalle> detallePedido;
	
	private static String mensaje;
	private static Activity context;
	private static String TAG = FormEstadisticaPedidos.class.getName();
	
	
	private WoosimR240 wR240 = null;
	public final static String CONFIG_IMPRESORA = "PRINTER";
	public final static String MAC_IMPRESORA = "MAC";
	public final static String LABEL_IMPRESORA = "LABEL";
	Cliente clienteSel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_estadistica_pedidos);

		Inicializar();
		CargarResumenPedidos();
		SetListenerListView();

	
	}
	

	public void Inicializar() {

		Bundle bundle = getIntent().getExtras();

		if (bundle != null) {

			if (bundle.containsKey("pedido")) {

				pedido = bundle.getInt("pedido"); 
			}
		}
	}

	public void CargarResumenPedidos() {

		//int sync = pedido ? 1 : 0;
		ItemListView[] listaItems = null;

		Vector<ItemListView> listaItemsCliente = new Vector<ItemListView>(); 
		
		DataBaseBO.setAppAutoventa();
		
		listaPedidos = DataBaseBO.CargarPedidosRealizados(Main.usuario.fechaConsecutivo, listaItemsCliente, pedido);

		if (listaItemsCliente.size() > 0) {

			listaItems = new ItemListView[listaItemsCliente.size()];
			listaItemsCliente.copyInto(listaItems);

			ListViewAdapter adapter = new ListViewAdapter(this, listaItems, R.drawable.cliente, 0xff2E65AD);
			ListView listaPedidosRealizados = (ListView)findViewById(R.id.listaPedidosRealizados);
			listaPedidosRealizados.setAdapter(adapter);

		} else {

			ListViewAdapter adapter = new ListViewAdapter(this, new ItemListView[]{}, R.drawable.cliente, 0xff2E65AD);
			ListView listaPedidosRealizados = (ListView)findViewById(R.id.listaPedidosRealizados);
			listaPedidosRealizados.setAdapter(adapter);					
		}

		int size = listaPedidos.size();

		if (pedido==1) {

			String msg = "<b>Visitas Realizadas: " + size + "</b>";
			((TextView)findViewById(R.id.lblTitulo)).setText(Html.fromHtml(msg));
		} 
		else {


			if (pedido==0) {

				String msg = "<b>Pedidos Sin Sincronizar: " + size +"</b>";
				((TextView)findViewById(R.id.lblTitulo)).setText(Html.fromHtml(msg));
			}
			else if (pedido==-1) {

				String msg = "<b>Pedidos Clientes Ocasionales: " + size +"</b>";
				((TextView)findViewById(R.id.lblTitulo)).setText(Html.fromHtml(msg));
			}
			else if (pedido==2) {

				String msg = "<b>Facturas Anuladas: " + size +"</b>";
				((TextView)findViewById(R.id.lblTitulo)).setText(Html.fromHtml(msg));
			}
			else if (pedido==4) {

				String msg = "<b>Cambios: " + size +"</b>";
				((TextView)findViewById(R.id.lblTitulo)).setText(Html.fromHtml(msg));
			}
		}
	}

	public void SetListenerListView() {

		ListView listaPedidosRealizados = (ListView)findViewById(R.id.listaPedidosRealizados);
		listaPedidosRealizados.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Encabezado encabezado = listaPedidos.elementAt(position);


				Date date = new Date(); 

				long tiempoClick2 = date.getTime();

				long dif1 = tiempoClick2-tiempoClick1;

				if(dif1 < 0){

					dif1 = dif1*(-1);

				}


				if(dif1 > 2000){


					tiempoClick1 = 	tiempoClick2;


					//MostarDialogResumen(encabezado);
					MostarDialogResumenNuevo(encabezado);

				}


			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == Const.RESP_PEDIDO_EXITOSO) {

			CargarResumenPedidos();
		}
	}

	public void MostarDialogResumen(final Encabezado encabezado) {

	
		dialogResumen = new Dialog(this);
		dialogResumen.requestWindowFeature(Window.FEATURE_LEFT_ICON);
		dialogResumen.setContentView(R.layout.resumen_pedido2);
		dialogResumen.setTitle("Resumen");

		if( Const.tipoAplicacion == Const.PREVENTA ){

			( ( Button ) dialogResumen.findViewById( R.id.btnImprimirPedido ) ).setVisibility( View.GONE );
		}
		else{
			
			if(encabezado.tipoDoc.equals("2")){
				
				
				( ( Button ) dialogResumen.findViewById( R.id.btnImprimirPedido ) ).setVisibility( View.VISIBLE );
				
				
			}else{
				
				( ( Button ) dialogResumen.findViewById( R.id.btnImprimirPedido ) ).setVisibility( View.GONE );
				
				
			}

		}
		
		( ( Button ) dialogResumen.findViewById( R.id.btnCancelarPedidoResumenPedido ) ).setVisibility( View.GONE );
		
		
		
		
		
		


		encabezado.sub_total = 0;
		encabezado.total_iva = 0;
		encabezado.valor_descuento = 0;

		ItemListView itemListView;
		detallePedido = DataBaseBO.CargarDetallePedido(encabezado.numero_doc);

		//Enumeration<Detalle> e = detallePedido.elements();
		Vector<ItemListView> datosPedido = new Vector<ItemListView>();

		List< Detalle > tmp = Collections.list( detallePedido.elements() );
		Util.quickSortListaDetalle( tmp , 0, tmp.size() - 1 );

		int i = 0;

		//while (e.hasMoreElements()) {
		while( i < tmp.size() ){

			//Detalle detalle = e.nextElement();
			Detalle detalle = tmp.get( i );
			itemListView = new ItemListView();
			i++;

			float sub_total       = detalle.cantidad * detalle.precio;
			float valor_descuento = sub_total * detalle.descuento_autorizado / 100;
			float valor_iva       = (sub_total - valor_descuento) * (detalle.iva / 100);

			encabezado.sub_total += sub_total;
			encabezado.total_iva += valor_iva;
			encabezado.valor_descuento += valor_descuento;
	
			
			itemListView.titulo = detalle.codigo_producto + " - " + detalle.desc_producto;
			itemListView.subTitulo = "Precio: $" + detalle.precio + " - Iva: $" + detalle.iva +  " - Cant: " + detalle.cantidad + " - Desc: " + detalle.descuento_autorizado + "%";

			
			datosPedido.addElement(itemListView);
		}

		ItemListView[] listaItems = new ItemListView[datosPedido.size()];
		datosPedido.copyInto(listaItems);

		encabezado.valor_neto     = encabezado.sub_total + encabezado.total_iva - encabezado.valor_descuento;
		encabezado.str_valor_neto = Util.SepararMiles(Util.Redondear(Util.QuitarE("" + encabezado.valor_neto), 0)); 

		((TextView) dialogResumen.findViewById(R.id.lblCliente)).setText(encabezado.nombre_cliente);
		((TextView) dialogResumen.findViewById(R.id.lblValorNetoPedido)).setText(encabezado.str_valor_neto);

		ListViewAdapter adapter = new ListViewAdapter(this, listaItems, R.drawable.compra, 0);
		ListView listaResumenPedido = (ListView) dialogResumen.findViewById(R.id.listaResumenPedido);
		listaResumenPedido.setAdapter(adapter);

		((Button)dialogResumen.findViewById(R.id.btnAceptarResumenPedido)).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				dialogResumen.cancel();
			}
		});

		((Button)dialogResumen.findViewById(R.id.btnImprimirPedido)).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				

				progressDialog = ProgressDialog.show(FormEstadisticaPedidos.this, "", "Por Favor Espere...\n\nProcesando Informacion!", true);
				progressDialog.show();
				
				SharedPreferences settings = getSharedPreferences(Const.CONFIG_IMPRESORA, MODE_PRIVATE);
				String macImpresora = settings.getString(Const.MAC_IMPRESORA, "-");
				
				if (macImpresora.equals("-")) {
					
					if( progressDialog != null ){
						progressDialog.dismiss();
					}
					
					Util.MostrarAlertDialog(FormEstadisticaPedidos.this, "Aun no hay Impresora Establecida.\n\nPor Favor primero Configure la Impresora!");
					
				} else {
												
					//**imprimirTirilla( macImpresora, encabezado.numero_doc );
					
					
					Usuario usuario = DataBaseBO.CargarUsuario();
					Cliente cliente = DataBaseBO.buscarClientePorEncabezadoVenta(encabezado.numero_doc);
					clienteSel = cliente;
					imprimir_WSP_R240(macImpresora, encabezado.numero_doc, cliente, usuario, 1);
				
					
					
					
				}
			
				

			}
		});

		dialogResumen.setCancelable(false);
		dialogResumen.show();
		dialogResumen.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.cliente);
		
		
	}

	@Override
	protected void onResume() {

		super.onResume();
		
		DataBaseBO.ValidarUsuario();

		Inicializar();
		CargarResumenPedidos();
	}



	
	@Override
	protected void onDestroy() {

		super.onDestroy();
		}



	
//	public void imprimirTirilla(final String macPrinter, final String numeroDoc) {
//    	
//    	new Thread(new Runnable() {
//    		
//    		public void run() {
//    			
//    			mensaje = "";
//    			BluetoothSocket socket = null;
//    			
//    			try {
//    				
//    				Looper.prepare();
//    				
//    				BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
//    				if (bluetoothAdapter == null) {
//    					
//    					mensaje = "No hubo conexión con la impresora.\n\nPor Favor intente de nuevo.";
//    					
//    				} else if (!bluetoothAdapter.isEnabled()) {
//    					
//    					mensaje = "No hubo conexión con la impresora.\n\nPor Favor intente de nuevo.";
//    					
//    				} else {
//    					
//    					BluetoothDevice printer = null;
//        				Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
//
//        				for (BluetoothDevice device : pairedDevices) {
//        					
//        				    String macAddress = device.getAddress();
//
//        				    if (macAddress.equals(macPrinter)) {
//        				    	
//        				        printer = device;
//        				        break;
//        				    }
//        				}
//
//        				if (printer == null) {
//        					
//        					mensaje = "No se pudo establecer la conexion con la Impresora.";
//        					
//        				} else {
//        					
//        					int state = printer.getBondState();
//        					
//            				if (state == BluetoothDevice.BOND_BONDED) {
//            					
//            					UUID uuid = UUID.fromString(FormConfigPrinter.UUID_BPP);
//                				socket = printer.createRfcommSocketToServiceRecord(uuid);
//                				
//                				if (socket != null) {            						                					           					                				
//                					
//            						socket.connect();
//    								//OutputStream stream = socket.getOutputStream();
//            						String strPrint = PrinterBO.formatoVenta( numeroDoc, 1 );	
//            						imprimir(socket, strPrint );
//
//            						
//            					} else {
//            						
//            						mensaje = "No se pudo abrir la conexion con la Impresora.\n\nPor Favor intente de nuevo.";
//            					}
//                				
//            				} else {
//            					
//            					mensaje = "La Impresora: " + macPrinter + " Actualmente no esta Acoplada con el Dispositivo Movil.\n\nPor Favor configure primero la impresora.";
//            				}
//        				}
//    				}
//    				
//    				if (!mensaje.equals("")) {
//
//    					context = FormEstadisticaPedidos.this;
//        				handlerMensaje.sendEmptyMessage(0);
//    				}
//    				
//    				Looper.myLooper().quit();
//    				
//    			} catch (Exception e) {
//    				
//    				String motivo = e.getMessage();
//    				Log.e(TAG, "imprimirPruebaExtech -> " + motivo, e);
//    				
//    				mensaje = "No se pudo ejecutar la Impresion.";
//    				
//    				if (motivo != null) {
//    					mensaje += "\n\n" + motivo;
//    				}
//    				
//    				context = FormEstadisticaPedidos.this;
//    				handlerMensaje.sendEmptyMessage(0);
//    				
//    			} finally {
//    				
//    				/*try {
//    					
//    					if (socket != null) {
//    						socket.close();
//    					}
//    					
//    				} catch (Exception e) {
//    					
//    					Log.e(TAG, "imprimirPruebaExtech -> Cerrando socket", e);
//    				}*/
//    			}
//    		}
//    		
//    	 }).start();
//    }
	
	public void imprimir(final BluetoothSocket socket, final String strPrint) {

		Thread printerThread = new Thread() {

			@Override
			public void run() {

				mensaje = "";

				try {

					synchronized(this) {
						
						wait( Const.timeWait );
					}
					
					OutputStream stream = socket.getOutputStream();
					stream.write(strPrint.getBytes());

					stream.flush();
					
					//wait(1500);
					Thread.sleep(1500);
					
					stream.close();
					socket.close();

				} catch (Exception ex) {

					Log.e(TAG, "imprimir => " + ex.getMessage(), ex);
					mensaje = "No se pudo imprimir la Prueba.\n\nPor favor intente de nuevo.";
				}

				if (mensaje.equals("")) {

					if (handlerFinish != null) {

						handlerFinish.sendEmptyMessage(0);

					} else {

						if (progressDialog != null) {
							progressDialog.cancel();
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
	
	private Handler handlerFinish = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			if (progressDialog != null)
				progressDialog.cancel();			
		}
	};
	
	private Handler handlerMensaje = new Handler() {
		
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


    
    public void anularFactura(String numeroDoc,String vendedor){
    	
    	/*
    	if(DataBaseBO.insertarAnulacion(numeroDoc,vendedor)){
    		
    		//***jfmr
			DataBaseBO.reorganizarInventario(this);
			//***jfmr
			
    		
    		Util.MostrarAlertDialog(this, "Se ha Anulado la Factura de Forma Correcta");
    		
    		runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
				
					Inicializar();
					CargarResumenPedidos();
					SetListenerListView();
					
				}
			});    		
    	}
    	else {    		
    		Util.MostrarAlertDialog(this, "Fallo Anular Factura por Fvor Intente Nuevamente");
    	}*/
    }
    
    
    
    
    
    
	private void imprimir_WSP_R240(final String mac, final String numeroDoc, final Cliente clienteEncabezado, final Usuario usuario, final int numCopias){
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
							Toast.makeText(FormEstadisticaPedidos.this, "Aun no hay Impresora Predeterminada.\n\nPor Favor primero Configure la Impresora!", Toast.LENGTH_SHORT).show();
						}
					});
				}else{
					if (wR240 == null){
						wR240 = new WoosimR240(FormEstadisticaPedidos.this);
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
						String numeroFactura = DataBaseBO.cargarDetallesImprimir(numeroDoc, detalleImprimir, listaDetalleProductos, control, clienteSel.listaPrecio);
						if (detalleImprimir != null && detalleImprimir.getEncabezado() != null && listaDetalleProductos != null && !listaDetalleProductos.isEmpty()){
							for (int i = 0; i < numCopias; i++){
								if (i > 0){
									control.original = false;
								}
								wR240.generarEncabezadoTirilla(numeroFactura, clienteEncabezado, control.fechaVenta, control.original);
								wR240.generarDetalleTirilla(detalleImprimir);
								int succes = wR240.imprimirBuffer(true);
								if ((control.original) && succes == 1){
									DataBaseBO.marcarComoCopiaProximaImpresion(numeroDoc, usuario);
								}
								try{
									Thread.sleep(Const.timeWait);
								}
								catch (InterruptedException e){
									e.printStackTrace();
								}
							}
						}else{
							progressDialog.dismiss();
							runOnUiThread(new Runnable(){
								@Override
								public void run(){
									Toast.makeText(FormEstadisticaPedidos.this, "Aun no hay datos para imprimir, revise el pedido.", Toast.LENGTH_SHORT).show();
								}
							});
						}
						break;
					case -2:
						runOnUiThread(new Runnable(){
							@Override
							public void run(){
								Toast.makeText(FormEstadisticaPedidos.this, "-2 fallo conexion", Toast.LENGTH_SHORT).show();
							}
						});
						break;
					case -8:
						progressDialog.dismiss();
						runOnUiThread(new Runnable(){
							@Override
							public void run(){
								Util.MostrarAlertDialog(FormEstadisticaPedidos.this, "Bluetooth apagado. Por favor habilite el bluetoth para imprimir.");
							}
						});
						break;
					default:
						runOnUiThread(new Runnable(){
							@Override
							public void run(){
								Toast.makeText(FormEstadisticaPedidos.this, "error desconocido", Toast.LENGTH_SHORT).show();
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
	
	
	
	
	
	
	
	
	
	
	
	public void MostarDialogResumenNuevo(final Encabezado encabezado){
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
		
		
		((Button) dialogResumen.findViewById(R.id.btnCancelarPedidoResumenPedido)).setVisibility(View.GONE);
		
		((TextView) dialogResumen.findViewById(R.id.lblCliente)).setText(encabezado.nombre_cliente);
		
		((TextView) dialogResumen.findViewById(R.id.lblValorNetoPedido)).setText(encabezado.str_valor_neto);
		ListViewAdapter adapter = new ListViewAdapter(this, listaItems, R.drawable.compra, 0);
		ListView listaResumenPedido = (ListView) dialogResumen.findViewById(R.id.listaResumenPedido);
		listaResumenPedido.setAdapter(adapter);
		
		((Button) dialogResumen.findViewById(R.id.btnAceptarResumenPedido)).setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				dialogResumen.cancel();
			}
		});
		((Button) dialogResumen.findViewById(R.id.btnImprimirPedido)).setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				progressDialog = ProgressDialog.show(FormEstadisticaPedidos.this, "", "Por Favor Espere...\n\nProcesando Informacion!", true);
				progressDialog.show();
				SharedPreferences settings = getSharedPreferences(Const.CONFIG_IMPRESORA, MODE_PRIVATE);
				String macImpresora = settings.getString(Const.MAC_IMPRESORA, "-");
				if (macImpresora.equals("-")){
					if (progressDialog != null){
						progressDialog.dismiss();
					}
					Util.MostrarAlertDialog(FormEstadisticaPedidos.this, "Aun no hay Impresora Establecida.\n\nPor Favor primero Configure la Impresora!");
				}else{
					
					Usuario usuario = DataBaseBO.CargarUsuario();
					Cliente cliente = DataBaseBO.buscarClientePorEncabezadoVenta(encabezado.numero_doc);
					clienteSel = cliente;
					imprimir_WSP_R240(macImpresora, encabezado.numero_doc, cliente, usuario, 1);
					
				}
			}
		});
		dialogResumen.setCancelable(false);
		dialogResumen.show();
		dialogResumen.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.cliente);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
