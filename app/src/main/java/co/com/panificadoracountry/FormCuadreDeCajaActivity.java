package co.com.panificadoracountry;
import co.com.panificadoracountry.R;
import co.com.woosim.printer.WoosimR240;
import co.com.BusinessObject.DataBaseBO;
import co.com.BusinessObject.PrinterBO;
import co.com.DataObject.EstadisticaRecorrido;

import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class FormCuadreDeCajaActivity extends Activity {

	ProgressDialog progressDialog;
	private WoosimR240 wR240 = null;
	String mensaje = "";
	private static Activity context;
	private static String TAG = FormCuadreDeCajaActivity.class.getName();
	
	float ventaDia = 0;
	float ventaDiaCredito = 0;
	float ventaDiaContado = 0;
	float saldosDepositados = 0;
	float recaudoEfectivo = 0;
	float recaudoEfectivoTotal = 0;
	float recaudoCheque = 0;
	float recaudoChequePostF = 0;
	float recaudoTransferencia = 0;
	float recaudo = 0;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_cuadre_de_caja);

		CargarCuadreDeCaja();
	

	}

	public void CargarCuadreDeCaja() {

		DataBaseBO.setAppAutoventa();
		
		ventaDia = DataBaseBO.obtenerVentaDeVendedor();

		((TextView) findViewById(R.id.lblTotalVenta)).setText("" + Util.SepararMiles("" + (long) ventaDia));
		
		saldosDepositados = DataBaseBO.obtenerSaldosDepositados();
		
		((TextView) findViewById(R.id.lblTotalSaldosDepositados)).setText("" + Util.SepararMiles("" +(long)  saldosDepositados));
		
		ventaDiaCredito = DataBaseBO.obtenerVentaDeVendedorClientesCredito();

		((TextView) findViewById(R.id.lblTotalVentaClientesCredito)).setText("" + Util.SepararMiles("" + (long) ventaDiaCredito));
		
		
		ventaDiaContado = DataBaseBO.obtenerVentaDeVendedorClientesContado();

		((TextView) findViewById(R.id.lblTotalVentaClientesContado)).setText("" + Util.SepararMiles("" + (long) ventaDiaContado));
	
		
		recaudoEfectivo = DataBaseBO.obtenerRecaudoVendedor(1);
		recaudoCheque = DataBaseBO.obtenerRecaudoVendedor(2);
		recaudoChequePostF = DataBaseBO.obtenerRecaudoVendedor(3);
		recaudoTransferencia= DataBaseBO.obtenerRecaudoVendedor(4);
		recaudo= DataBaseBO.obtenerRecaudoVendedor();
		recaudoEfectivoTotal = DataBaseBO.obtenerRecaudoVendedorTotal();
		
		
		((TextView) findViewById(R.id.lblTotalRecaudoEfectivoHistorico)).setText("" + Util.SepararMiles("" +(long)  recaudoEfectivoTotal));
		((TextView) findViewById(R.id.lblTotalRecaudoEfectivo)).setText("" + Util.SepararMiles("" +(long)  recaudoEfectivo));
		((TextView) findViewById(R.id.lblTotalRecaudoCheque)).setText("" + Util.SepararMiles("" + (long) recaudoCheque));
		((TextView) findViewById(R.id.lblTotalRecaudoChequePost)).setText("" + Util.SepararMiles("" + (long) recaudoChequePostF));
		((TextView) findViewById(R.id.lblTotalRecaudoTransferencia)).setText("" + Util.SepararMiles("" + (long) recaudoTransferencia));
		((TextView) findViewById(R.id.lblTotalRecaudo)).setText("" + Util.SepararMiles("" + (long) recaudo));

		
		String complemento;
		
		  String fechaActual = Util.FechaActual("yyyy-MM-dd HH:mm:ss");
			
		  complemento ="Fecha y Hora: "+fechaActual+ "\r\n"+ "\r\n";
			
			
			
			if(DataBaseBO.yaTerminoLabores()) {
				
				
				complemento = complemento +"El Vendedor Ya Termino Labores \r\n";
				
			}else {
				
				complemento = complemento +"El Vendedor No ha Terminado Labores \r\n";
				
			}
		
			((TextView) findViewById(R.id.txComplemento)).setText(complemento);

			
		
		
	}

	@Override
	protected void onResume() {

		super.onResume();
		if (Main.usuario == null || Main.usuario.codigoVendedor == null)
		DataBaseBO.CargarInfomacionUsuario();
		CargarCuadreDeCaja();
	}


	
	public void OnClickImprimir( View view ){

		if (DataBaseBO.HayInformacionXEnviar()) {
			
			Util.MostrarAlertDialog(this, "Por favor envie la informacion pendiente antes de terminar dia");
			return;
			
		}else {

				progressDialog = ProgressDialog.show(FormCuadreDeCajaActivity.this, "", "Por Favor Espere...\n\nProcesando Informacion!", true);
				progressDialog.show();
		
				SharedPreferences settings = getSharedPreferences(Const.CONFIG_IMPRESORA, MODE_PRIVATE);
				String macImpresora = settings.getString(Const.MAC_IMPRESORA, "-");
		
				if (macImpresora.equals("-")) {
		
					if( progressDialog != null ){
		
						progressDialog.dismiss();
					}
		
					Util.MostrarAlertDialog(FormCuadreDeCajaActivity.this, "Aun no hay Impresora Establecida.\n\nPor Favor primero Configure la Impresora!");
		
				} else {
		
					imprimirTirillaExtech( macImpresora );
				}
		}		
	}



	/**
	 * Metodo para iniciar una conexion a la impresora. 
	 * y generar la tirilla de inventario.
	 * @param view
	 */
	public void onClickImprimir(View view){
		if (DataBaseBO.HayInformacionXEnviar()) {
			
			Util.MostrarAlertDialog(this, "Por favor Termine Dia");
			return;
			
		}else {

			progressDialog = ProgressDialog.show(FormCuadreDeCajaActivity.this, "Imprimiendo", "Retire la tirilla cuando este lista.", true);
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
								Toast.makeText(FormCuadreDeCajaActivity.this,
										"Aun no hay Impresora Predeterminada.\n\nPor Favor primero Configure la Impresora!", Toast.LENGTH_SHORT)
								.show();
							}
						});
					}
					else {
	
						if (wR240 == null) {
							wR240 = new WoosimR240(FormCuadreDeCajaActivity.this);
						}
						int conect = wR240.conectarImpresora(macImpresora);
	
						switch (conect) {
	
						case 1:
							wR240.generarEncabezadoTirillaCuadreCaja((long)ventaDia,(long)saldosDepositados,(long)ventaDiaCredito,(long)ventaDiaContado,(long)recaudo,(long)recaudoEfectivo,(long)recaudoCheque,(long)recaudoChequePostF,(long)recaudoTransferencia);
							wR240.imprimirBuffer(true);
							break;
	
						case -2:
							runOnUiThread(new Runnable() {
	
								@Override
								public void run() {
									Toast.makeText(FormCuadreDeCajaActivity.this, "-2 fallo conexion", Toast.LENGTH_SHORT).show();
								}
							});
							break;
	
						case -8:
							progressDialog.dismiss();
							runOnUiThread(new Runnable() {
	
								@Override
								public void run() {
									Util.MostrarAlertDialog(FormCuadreDeCajaActivity.this,
											"Bluetooth apagado. Por favor habilite el bluetoth para imprimir.");
								}
							});
							break;
	
						default:
							runOnUiThread(new Runnable() {
	
								@Override
								public void run() {
									Toast.makeText(FormCuadreDeCajaActivity.this, "Error desconocido, intente nuevamente.", Toast.LENGTH_SHORT).show();
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
					}
					Looper.myLooper().quit();
				}
			}).start();
		}
	}



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(wR240 != null){
			wR240.desconectarImpresora();
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


									String strPrint ="FORMATO DE CUADRE DE CAJA\n\n\n\n";	
									imprimir(socket, strPrint);


								} else {

									mensaje = "No se pudo abrir la conexion con la Impresora.\n\nPor Favor intente de nuevo.";
								}

							} else {

								mensaje = "La Impresora: " + macPrinter + " Actualmente no esta Acoplada con el Dispositivo Movil.\n\nPor Favor configure primero la impresora.";
							}
						}
					}

					if (!mensaje.equals("")) {

						context = FormCuadreDeCajaActivity.this;
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

					context = FormCuadreDeCajaActivity.this;
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






}
