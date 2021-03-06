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
import co.com.BusinessObject.DataBaseBOJ;
import co.com.BusinessObject.PrinterBO;
import co.com.DataObject.Cliente;
import co.com.DataObject.ControlImpresion;
import co.com.DataObject.Deposito;
import co.com.DataObject.Detalle;
import co.com.DataObject.DetalleDeposito;
import co.com.DataObject.DetalleImprimir;
import co.com.DataObject.DetalleProducto;
import co.com.DataObject.Encabezado;
import co.com.DataObject.ItemListView;
import co.com.DataObject.Producto;
import co.com.DataObject.SaldoCancelado;
import co.com.DataObject.Usuario;
import co.com.panificadoracountry.R;
import co.com.woosim.printer.WoosimR240;


public class FormRecaudoInformacion extends Activity implements OnClickListener {
      
    ArrayList<SaldoCancelado> listaRacaudo;
    SaldoCancelado saldoCancelado;
    public long tiempoClick1 = 0;
    private Dialog dialogImprimirFacturaDeposito;
    private ProgressDialog progressDialog;
	public final static String CONFIG_IMPRESORA = "PRINTER";
	public final static String MAC_IMPRESORA    = "MAC";
	public final static String LABEL_IMPRESORA  = "LABEL";
    

    
    /**
     * Referencia a la impresora WSP-R240 WOOSIM.
     */
    private WoosimR240 wR240 = null;
    
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	
	super.onCreate(savedInstanceState);
	setContentView(R.layout.form_recaudo_informacion);
	cargarInformacionDeRecaudo();
    }
    
    
    
    public void onResume() {
	
	super.onResume();
	if (Main.usuario == null || Main.usuario.codigoVendedor == null)
	    DataBaseBO.CargarInfomacionUsuario();
	    DataBaseBO.setAppAutoventa();
    }
    
    
    
    public void cargarInformacionDeRecaudo() {
	
	    listaRacaudo = DataBaseBO.cargarRecaudoRealizado();
	    
	    if(listaRacaudo.size() > 0){
	    	TableLayout table = new TableLayout(this);
		    table.setBackgroundColor(Color.WHITE);
		    
		    String[] cabecera = { "  ", "factura", "Saldo", "Cliente"};
		    Util.Headers(table, cabecera, this);
		    
		    HorizontalScrollView scroll = (HorizontalScrollView) findViewById(R.id.scrollPedidosRealizados);
		    scroll.removeAllViews();
		    scroll.addView(table);
		    int pos = 0;
		    
		    for(SaldoCancelado saldoCan: listaRacaudo){
		    	pos++;
				
				TextView textViewAux;
				TableRow fila = new TableRow(this);
				
				ImageView imgAux = new ImageView(this);
				imgAux.setImageResource(R.drawable.imprimir_inventario);
				imgAux.setOnClickListener(this);
				imgAux.setTag(pos);
				imgAux.setAdjustViewBounds(true);
				fila.addView(imgAux);
				
				textViewAux = new TextView(this);
				textViewAux.setText(saldoCan.factura + "\n");
				textViewAux.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
				textViewAux.setTextColor(Color.argb(255, 0, 0, 0));
				textViewAux.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.table_cell_row_1));
				fila.addView(textViewAux);
				
				
				textViewAux = new TextView(this);
				textViewAux.setText(Util.SepararMiles(""+saldoCan.saldo_recibido) + "\n");
				textViewAux.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
				textViewAux.setTextColor(Color.argb(255, 0, 0, 0));
				textViewAux.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.table_cell_row_1));
				fila.addView(textViewAux);
				
				textViewAux = new TextView(this);
				textViewAux.setText(saldoCan.razon_social + "\n");
				textViewAux.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
				textViewAux.setTextColor(Color.argb(255, 0, 0, 0));
				textViewAux.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.table_cell_row_1));
				fila.addView(textViewAux);
				
	
				
				
				table.addView(fila);
		    }
	    }

    	}
    
    
    
	    public void onClick(View view) {
		
		String cadena = view.getTag().toString();
		int pos = Integer.parseInt(cadena);
		saldoCancelado = listaRacaudo.get(pos - 1);
		Date date = new Date();
		long tiempoClick2 = date.getTime();
		long dif1 = tiempoClick2 - tiempoClick1;
		
		if (dif1 < 0) {
		    
		    dif1 = dif1 * (-1);
		    
		}
		
		if (dif1 > 3000) {
		    
		    tiempoClick1 = tiempoClick2;
		    mostrarDialogImprimirFactura();
		    
		}
	
    }
	    
    private void mostrarDialogImprimirFactura(){

		dialogImprimirFacturaDeposito = new Dialog(FormRecaudoInformacion.this);
		dialogImprimirFacturaDeposito.setContentView(R.layout.dialog_imprimir_factura_pedido);
		dialogImprimirFacturaDeposito.setTitle("Imprimir Factura");


		((EditText)dialogImprimirFacturaDeposito.findViewById(R.id.txtNumCopias)).setText( "1" );

		((Button)dialogImprimirFacturaDeposito.findViewById(R.id.btnAceptarImprimirFactura)).setOnClickListener(new OnClickListener() {

			

			public void onClick(View v) {

				String txt = ((EditText)dialogImprimirFacturaDeposito.findViewById(R.id.txtNumCopias)).getText().toString();

				int numCopias = Util.ToInt( txt );
				
				/*Si se presenta un error con el numero asignar una copia para imprimir.*/
				if(numCopias == 0){
					numCopias = 1;
				}

				progressDialog = ProgressDialog.show(FormRecaudoInformacion.this, "", "Por Favor Espere...\n\nProcesando Informacion!", true);
				progressDialog.show();

				SharedPreferences settings = getSharedPreferences(Const.CONFIG_IMPRESORA, MODE_PRIVATE);
				String macImpresora = settings.getString(Const.MAC_IMPRESORA, "-");

				if (macImpresora.equals("-")) {

					if( progressDialog != null ){

						progressDialog.dismiss();
					}

					Util.MostrarAlertDialog(FormRecaudoInformacion.this, "Aun no hay Impresora Establecida.\n\nPor Favor primero Configure la Impresora!");

				} else {
					Usuario usuario = DataBaseBO.CargarUsuario();
					imprimir_WSP_R240(macImpresora, usuario, numCopias);
					//		    imprimirTirilla( macImpresora, encabezado.numero_doc, numCopias );
				}
			}
		});

		((Button)dialogImprimirFacturaDeposito.findViewById(R.id.btnCancelarImprimirFactura)).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				dialogImprimirFacturaDeposito.cancel();
			}
		});

		dialogImprimirFacturaDeposito.setCancelable(false);
		dialogImprimirFacturaDeposito.show();
	}
    
    private void imprimir_WSP_R240(final String mac, final Usuario usuario, final int numCopias){
		new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				SharedPreferences settings = getSharedPreferences(CONFIG_IMPRESORA, MODE_PRIVATE);
				final String macImpresora = settings.getString(MAC_IMPRESORA, "-");

				if (macImpresora.equals("-")) {
					progressDialog.dismiss();
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(FormRecaudoInformacion.this,
									"Aun no hay Impresora Predeterminada.\n\nPor Favor primero Configure la Impresora!", Toast.LENGTH_SHORT)
							.show();
						}
					});
				}
				else {

					if (wR240 == null) {
						wR240 = new WoosimR240(FormRecaudoInformacion.this);
					}
					int conect = wR240.conectarImpresora(macImpresora);

					switch (conect) {
					case 1:
						ControlImpresion control = new ControlImpresion();
						/*lista que contendr� los detalles que seran impresos*/
//						DetalleImprimir detalleImprimir = new DetalleImprimir();
						 
						//ArrayList<DetalleDeposito> depositoSel = new ArrayList<DetalleDeposito>();
						//depositoSel.add(detalleDeposito);
						
						
						if (saldoCancelado != null) {

							for (int i = 0; i < numCopias; i++) {
								if(i > 0){
									control.original = false;
								}
								wR240.generarEncabezadoTirillaRecaudo(saldoCancelado,DataBaseBOJ.getCliente(saldoCancelado.cod_cliente) , Main.usuario,control.original);
								int succes = wR240.imprimirBuffer(true);
								if( (control.original) && succes == 1 ){
//									DataBaseBO.marcarComoCopiaProximaImpresion(numeroDoc, usuario);
								}
								try {
									Thread.sleep(Const.timeWait);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
						else {
							progressDialog.dismiss();
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(FormRecaudoInformacion.this,
											"Aun no hay datos para imprimir, revise el deposito.", Toast.LENGTH_SHORT)
									.show();
								}
							});
						}
						break;

					case -2:
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(FormRecaudoInformacion.this, "-2 fallo conexion", Toast.LENGTH_SHORT).show();
							}
						});
						break;

					case -8:
						progressDialog.dismiss();
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Util.MostrarAlertDialog(FormRecaudoInformacion.this,
										"Bluetooth apagado. Por favor habilite el bluetoth para imprimir.");
							}
						});
						break;

					default:
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(FormRecaudoInformacion.this, "error desconocido", Toast.LENGTH_SHORT).show();
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
						handlerFinish.sendEmptyMessage(0);
					}
				}
				Looper.myLooper().quit();
			}
		}).start();
	}
    
    private Handler handlerFinish = new Handler() {

		@Override
		public void handleMessage(Message msg) {


			AlertDialog.Builder builder = new AlertDialog.Builder(FormRecaudoInformacion.this);
			builder.setMessage("Solicitud Almacenada Exitosamente")
			.setCancelable(false)
			.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int id) {

//					progressDialog = ProgressDialog.show(FormPedidosAutoventa.this, "","Enviando Informacion...", true);
//					progressDialog.show();
//					progressDialog.setIndeterminate(true);
//					progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_icon_drawable_animation));
//
//
//					Sync sync = new Sync(FormPedidosAutoventa.this, Const.ENVIAR_PEDIDO);
//					sync.start();
					
					dialog.cancel();					
				}
			});

			AlertDialog alert = builder.create();
			alert.show();
		}
	};
   
}
