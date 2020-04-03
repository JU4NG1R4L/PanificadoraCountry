package co.com.panificadoracountry;

import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import co.com.panificadoracountry.R;
import co.com.woosim.printer.WoosimR240;
import co.com.BusinessObject.DataBaseBO;
import co.com.BusinessObject.PrinterBO;
import co.com.DataObject.DevolucionesRealizados;
import co.com.DataObject.Usuario;
import android.annotation.TargetApi;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN) public class FormDevolucionesRealizadas extends Activity {

	/**
	 * Objeto que permite la conexion a la impresora.
	 */
	private WoosimR240 wR240 = null;
	Dialog dialogInventario;
	Vector<DevolucionesRealizados> listaInformeDevoliciones;

	ProgressDialog progressDialog;

	private static String mensaje;
	private static Activity context;
	private static String TAG = FormDevolucionesRealizadas.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_productos);

		progressDialog = ProgressDialog.show(FormDevolucionesRealizadas.this, "", "Cargando Informacion...", true);
		progressDialog.show();

		new Thread() {

			public void run() {
				CargarInformeDev();
			}
		}.start();
	}

	/**
	 * use este -> {@link #onClickImprimir(View)} <br>  Nuevo metodo para uso con la impresora Woosim WSP-R240 
	 * @param view
	 */
	@Deprecated 
	public void OnClickImprimir( View view ){

		progressDialog = ProgressDialog.show(FormDevolucionesRealizadas.this, "", "Por Favor Espere...\n\nProcesando Informacion!", true);
		progressDialog.show();

		SharedPreferences settings = getSharedPreferences(Const.CONFIG_IMPRESORA, MODE_PRIVATE);
		String macImpresora = settings.getString(Const.MAC_IMPRESORA, "-");

		if (macImpresora.equals("-")) {

			if( progressDialog != null ){

				progressDialog.dismiss();
			}

			Util.MostrarAlertDialog(FormDevolucionesRealizadas.this, "Aun no hay Impresora Establecida.\n\nPor Favor primero Configure la Impresora!");
			return;
		} else {

			imprimirTirillaExtech( macImpresora );
		}
	}



	/**
	 * Metodo para iniciar una conexion a la impresora. 
	 * y generar la tirilla de inventario.
	 * @param view
	 */
	public void onClickImprimir(View view){

		progressDialog = ProgressDialog.show(FormDevolucionesRealizadas.this, "Imprimiendo", "Retire la tirilla cuando este lista.", true);
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
							Toast.makeText(FormDevolucionesRealizadas.this,
									"Aun no hay Impresora Predeterminada.\n\nPor Favor primero Configure la Impresora!", Toast.LENGTH_SHORT)
							.show();
						}
					});
				}
				else {

					if (wR240 == null) {
						wR240 = new WoosimR240(FormDevolucionesRealizadas.this);
					}
					int conect = wR240.conectarImpresora(macImpresora);

					switch (conect) {

					case 1:
						wR240.generarEncabezadoTirillaDevoluciones();
						wR240.imprimirBuffer(true);
						break;

					case -2:
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(FormDevolucionesRealizadas.this, "-2 fallo conexion", Toast.LENGTH_SHORT).show();
							}
						});
						break;

					case -8:
						progressDialog.dismiss();
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Util.MostrarAlertDialog(FormDevolucionesRealizadas.this,
										"Bluetooth apagado. Por favor habilite el bluetoth para imprimir.");
							}
						});
						break;

					default:
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(FormDevolucionesRealizadas.this, "Error desconocido, intente nuevamente.", Toast.LENGTH_SHORT).show();
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



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(wR240 != null){
			wR240.desconectarImpresora();
		}
	}


	public void CargarInformeDev() {

		//cargar la informacion del usuario
		Usuario usuario = DataBaseBO.ObtenerUsuario();	

		listaInformeDevoliciones = DataBaseBO.CargarInformeDevoluciones(usuario);

		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				TableLayout table = new TableLayout(FormDevolucionesRealizadas.this);
				table.setBackgroundColor(Color.WHITE);
				TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
				params.setMargins(0, 5, 0, 0);
				HorizontalScrollView scroll = (HorizontalScrollView) findViewById(R.id.scrollInventario);
				scroll.removeAllViews();
				scroll.addView(table);

				if (listaInformeDevoliciones.size() > 0) {

					String[] cabecera = {"Producto\n", "Cantidad\n", "Vr. Unitario\n", "Vr. Total\n"};

					Util.Headers(table, cabecera, FormDevolucionesRealizadas.this);

					for (DevolucionesRealizados devolucionesRealizados : listaInformeDevoliciones) {

						TableRow fila = new TableRow(FormDevolucionesRealizadas.this);

						final TextView textView6 = new TextView(FormDevolucionesRealizadas.this);
						textView6.setText(devolucionesRealizados.nombreProducto);
						textView6.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
						textView6.setTextColor(Color.argb(255, 0, 0, 0));
						textView6.setTextSize(14);
						textView6.setBackground(FormDevolucionesRealizadas.this.getResources().getDrawable(android.R.drawable.editbox_dropdown_light_frame));
						textView6.setLayoutParams(params);
						fila.addView(textView6);

						final TextView textView1 = new TextView(FormDevolucionesRealizadas.this);
						textView1.setText(devolucionesRealizados.cantidad);
						textView1.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
						textView1.setTextColor(Color.argb(255, 0, 0, 0));
						textView1.setTextSize(14);
						textView1.setBackground(FormDevolucionesRealizadas.this.getResources().getDrawable(android.R.drawable.editbox_dropdown_light_frame));
						textView1.setLayoutParams(params);
						fila.addView(textView1);


						final TextView textView3 = new TextView(FormDevolucionesRealizadas.this);
						textView3.setText(devolucionesRealizados.precio);
						textView3.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
						textView3.setTextColor(Color.argb(255, 0, 0, 0));
						textView3.setTextSize(14);
						textView3.setBackground(FormDevolucionesRealizadas.this.getResources().getDrawable(android.R.drawable.editbox_dropdown_light_frame));
						textView3.setLayoutParams(params);
						fila.addView(textView3);

						final TextView textView4 = new TextView(FormDevolucionesRealizadas.this);
						textView4.setText(devolucionesRealizados.valorTotal);
						textView4.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
						textView4.setTextColor(Color.argb(255, 0, 0, 0));
						textView4.setTextSize(14);
						textView4.setBackground(FormDevolucionesRealizadas.this.getResources().getDrawable(android.R.drawable.editbox_dropdown_light_frame));
						textView4.setLayoutParams(params);
						fila.addView(textView4);



						/*
						 * alinear la cuadricula para que se conserven del mismo
						 * tamaño que su par. el valor height de categoria es
						 * siempre mas grande que el de total, por esto se elige
						 * como referencia
						 */
						ViewTreeObserver viewTreeObserver = textView6.getViewTreeObserver();
						viewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

							@Override
							public void onGlobalLayout() {

								int height = textView6.getHeight();
								textView1.setHeight(height);
								textView6.setHeight(height);
								textView3.setHeight(height);
								textView4.setHeight(height);

							}
						});

						table.addView(fila);
					}

				}
				else {

					Toast.makeText(getApplicationContext(), "Busqueda sin resultados", Toast.LENGTH_SHORT).show();
				}

				if (progressDialog != null)
					progressDialog.dismiss();
			}
		});
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


									String strPrint = PrinterBO.formatoInventarioNuevo2();	
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

						context = FormDevolucionesRealizadas.this;
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

					context = FormDevolucionesRealizadas.this;
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

	protected void onResume(){

		super.onResume();
		if(Main.usuario == null || Main.usuario.codigoVendedor == null)
			DataBaseBO.CargarInfomacionUsuario();

	}
}

