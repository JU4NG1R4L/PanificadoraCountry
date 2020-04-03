/**
 * 
 */
package co.com.panificadoracountry;

import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import co.com.panificadoracountry.R;
import co.com.BusinessObject.DataBaseBO;
import co.com.BusinessObject.PrinterBO;
import co.com.DataObject.InformeInventario;
import co.com.DataObject.Kardex;
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
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author JohnFaber
 *
 */
public class FormKardexDetallado extends Activity{

	
	ProgressDialog progressDialog;

	private static String mensaje;
	private static Activity context;
	private static String TAG = FormKardexDetallado.class.getName();
	Dialog dialogInventario;
	Vector<Kardex> listaInformeKardexs;
	String codigoProducto = "";
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO Auto-generated method stub
        setContentView(R.layout.form_kardex_detallado);
        codigoProducto = getIntent().getExtras().getString("producto_mostrar");
        
		progressDialog = ProgressDialog.show(FormKardexDetallado.this, "", "Cargando Informacion...", true);
		progressDialog.show();

		new Thread(){

			public void run(){

				CargarProducto();
			}
		}.start();
        
	}
	


	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		// TODO Auto-generated method stub
		
		if(Main.usuario == null || Main.usuario.codigoVendedor == null)
			DataBaseBO.CargarInfomacionUsuario();
		

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		// TODO Auto-generated method stub

	}
	
	
	public void CargarProducto() {

		listaInformeKardexs = DataBaseBO.CargarKardex(codigoProducto);

		this.runOnUiThread( new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				TableLayout table = new TableLayout(FormKardexDetallado.this);
				table.setBackgroundColor(Color.WHITE);

				HorizontalScrollView scroll = (HorizontalScrollView)findViewById(R.id.scrollInventario);
				scroll.removeAllViews();
				scroll.addView(table);

				if (listaInformeKardexs.size() > 0) {

					//String[] cabecera = {"CODIGO\n", "INV\nINICIAL", "INV\nACTUAL", "CANT\nVENTAS", "CANT CAMBIO\nVENTAS", "CANT\nDEVOLUCIONES", "CANT CAMBIO\nDEVOLUCIONES",  "NOMBRE\n"};
					
					String[] cabecera = {"FECHA\n", "TIPO\nDOC.", "CANT\n","TIPO\nREG","NUM DOC.\n"};
					
					
					Util.Headers(table, cabecera, FormKardexDetallado.this);

					TextView textViewAux;
					ImageView imageView;

					for (Kardex kardex : listaInformeKardexs) {

						TableRow fila = new TableRow(FormKardexDetallado.this);

						textViewAux = new TextView(FormKardexDetallado.this);
						textViewAux.setText(kardex.fecha+"");			
						textViewAux.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
						textViewAux.setTextColor(Color.argb(255,0,0,0));
						textViewAux.setTextSize(16);
						textViewAux.setBackgroundDrawable(FormKardexDetallado.this.getResources().getDrawable(android.R.drawable.editbox_dropdown_light_frame));
						fila.addView(textViewAux);
						
						
						textViewAux = new TextView(FormKardexDetallado.this);
						textViewAux.setText("  "+kardex.tipoDocumento+"");			
						textViewAux.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
						textViewAux.setTextColor(Color.argb(255,0,0,0));
						textViewAux.setTextSize(16);
						textViewAux.setBackgroundDrawable(FormKardexDetallado.this.getResources().getDrawable(android.R.drawable.editbox_dropdown_light_frame));
						fila.addView(textViewAux);
						
						
						textViewAux = new TextView(FormKardexDetallado.this);
						textViewAux.setText("  "+Util.Redondear(kardex.cantidad+"",1));			
						textViewAux.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
						textViewAux.setTextColor(Color.argb(255,0,0,0));	
						textViewAux.setTextSize(14);
						textViewAux.setBackgroundDrawable(FormKardexDetallado.this.getResources().getDrawable(android.R.drawable.editbox_dropdown_light_frame));
						fila.addView(textViewAux);
						
						textViewAux = new TextView(FormKardexDetallado.this);
						textViewAux.setText(kardex.tipoDocumento+"");			
						textViewAux.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
						textViewAux.setTextColor(Color.argb(255,0,0,0));	
						textViewAux.setTextSize(14);
						textViewAux.setBackgroundDrawable(FormKardexDetallado.this.getResources().getDrawable(android.R.drawable.editbox_dropdown_light_frame));
						fila.addView(textViewAux);


						
						textViewAux = new TextView(FormKardexDetallado.this);
						textViewAux.setText(kardex.numeroDocumento);			
						textViewAux.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
						textViewAux.setTextColor(Color.argb(255,0,0,0));	
						textViewAux.setTextSize(14);
						textViewAux.setBackgroundDrawable(FormKardexDetallado.this.getResources().getDrawable(android.R.drawable.editbox_dropdown_light_frame));
						fila.addView(textViewAux);
		

						table.addView(fila);
					}

				} else {

					Toast.makeText(getApplicationContext(), "Busqueda sin resultados", Toast.LENGTH_SHORT).show();
				}

				if( progressDialog != null )
					progressDialog.dismiss();
			}
		} );
	}
	

	
	
	public void OnClickImprimir(View view){
		
	
		progressDialog = ProgressDialog.show(this, "", "Por Favor Espere...\n\nProcesando Informacion!", true);
		progressDialog.show();

		SharedPreferences settings = getSharedPreferences(Const.CONFIG_IMPRESORA, MODE_PRIVATE);
		String macImpresora = settings.getString(Const.MAC_IMPRESORA, "-");

		if (macImpresora.equals("-")) {

			if( progressDialog != null ){

				progressDialog.dismiss();
			}

			Util.MostrarAlertDialog(this, "Aun no hay Impresora Establecida.\n\nPor Favor primero Configure la Impresora!");

		} else {

			imprimirTirillaExtech( macImpresora );
		}
		
		
		
	}
	
	
	
	public void imprimirTirillaExtech(final String macPrinter) {

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

									//OutputStream stream = socket.getOutputStream();
									String strPrint = PrinterBO.formatoKardex(codigoProducto,listaInformeKardexs);
									
									imprimir(socket, strPrint);
									
									//stream.write( strPrint.getBytes( "UTF-8" ) );                    						

									//Se asegura que los datos son enviados a la Impresora antes de cerrar la conexion.
									//Thread.sleep(1500);

									//handlerFinish.sendEmptyMessage(0);

								} else {

									mensaje = "No se pudo abrir la conexion con la Impresora.\n\nPor Favor intente de nuevo.";
								}

							} else {

								mensaje = "La Impresora: " + macPrinter + " Actualmente no esta Acoplada con el Dispositivo Movil.\n\nPor Favor configure primero la impresora.";
							}
						}
					}

					if (!mensaje.equals("")) {

						context = FormKardexDetallado.this;
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

					context = FormKardexDetallado.this;
					handlerMensaje.sendEmptyMessage(0);

				} finally {
					
				}
			}

		}).start();
	}

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
					
					sleep(1500);
					
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

    
	public void OnClickAtras(View view){
		
		finish();
		
	}
	

}
