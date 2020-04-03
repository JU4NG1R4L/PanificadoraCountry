package co.com.panificadoracountry;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;
import co.com.BusinessObject.DataBaseBO;
import co.com.BusinessObject.DataBaseBOJ;
import co.com.BusinessObject.FileBO;
import co.com.DataObject.BotonesMenu;
import co.com.DataObject.Coordenada;
import co.com.woosim.printer.DownloadLogo;

@SuppressLint("NewApi")
public class FormInfoClienteActivity extends Activity implements OnClickListener {
	AlertDialog alertDialog;
	LocationManager locationManager;
	Location currentLocation = null;
	Vector<BotonesMenu> vBtnMenu = new Vector<BotonesMenu>();
	Vector<BotonesMenu> listadoOpciones;
	private boolean guardoCoordenada = false;
	private String provider;
	private GPSListener gpsListener;
	boolean esClienteDeCredito = false;
	float disponible = 0;
	long mLastClickTime = 0;
	boolean clienteCoordenadas;
	boolean clienteCertificado;
	double latitud = 0f;
	double longitud = 0f;
	private ProgressDialog progressBarDialog;
	private Handler updateBarHandler;
	String mensaje;
	private AsyncTask mMyTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			/*
			 * Descargar el logo para imprimir si no esta en su ubicacion en
			 * memoria
			 */
			{
				DownloadLogo logo = new DownloadLogo(FormInfoClienteActivity.this);
				logo.execute();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		clienteCertificado = DataBaseBO.existeCertificado(Main.cliente.codigo);
		clienteCoordenadas = DataBaseBO.existeCertificadoCoordenadas(Main.cliente.codigo);
		updateBarHandler = new Handler();

		DataBaseBO.setAppAutoventa();
		setContentView(R.layout.form_info_cliente);
		DataBaseBO.ValidarUsuario();
		FileBO.validarCliente(FormInfoClienteActivity.this);
		CargarDatosCliente();
		crearBotonesMenu();
		carpturarCoordenada();
	}

	public void onResume() {
		super.onResume();
		DataBaseBO.ValidarUsuario();
		FileBO.validarCliente(FormInfoClienteActivity.this);
		CargarDatosCliente();
		DataBaseBO.setAppAutoventa();

	}

	public void CargarDatosCliente() {
		TextView lblCodigo = (TextView) findViewById(R.id.lblCodigo);
		lblCodigo.setText(Main.cliente.codigo);
		TextView lblNombre = (TextView) findViewById(R.id.lblNombre);
		lblNombre.setText(Main.cliente.nombre);
		TextView lblRazonSocial = (TextView) findViewById(R.id.lblRazonSocial);
		lblRazonSocial.setText(Main.cliente.razonSocial);
		TextView lblDireccion = (TextView) findViewById(R.id.lblDireccion);
		lblDireccion.setText(Main.cliente.direccion);
		TextView lblCiudad = (TextView) findViewById(R.id.lblCiudad);
		lblCiudad.setText(Main.cliente.ciudad);
		TextView lblTelefono = (TextView) findViewById(R.id.lblTelefono);
		lblTelefono.setText(Main.cliente.telefono);
		TextView lblBarrio = (TextView) findViewById(R.id.lblBarrio);
		lblBarrio.setText(Main.cliente.barrio);

		float cupoCliente = DataBaseBO.obtenerCupoCliente(Main.cliente.codigo);

		TextView lblCupo = (TextView) findViewById(R.id.lblCupo);
		lblCupo.setText(Util.SepararMiles(Util.Redondear((long) cupoCliente + "", 0)));

		TextView lblTipoPago = (TextView) findViewById(R.id.lblTipoPago);

		if (cupoCliente > 0) {

			esClienteDeCredito = true;

			lblTipoPago.setText("CREDITO");

		} else {

			esClienteDeCredito = false;
			lblTipoPago.setText("CONTADO");
		}

		float saldoPendiente = DataBaseBO.obtenerSaldoPendienteCliente(Main.cliente.codigo);

		TextView lblSaldoPendiente = (TextView) findViewById(R.id.lblSaldoPendiente);
		lblSaldoPendiente.setText(Util.SepararMiles(Util.Redondear((long) saldoPendiente + "", 0)));

		disponible = cupoCliente - saldoPendiente;

		TextView lblDisponible = (TextView) findViewById(R.id.lblDisponible);
		lblDisponible.setText(Util.SepararMiles(Util.Redondear((long) disponible + "", 0)));

		if (esClienteDeCredito) {

			TableRow trDisponible = (TableRow) findViewById(R.id.trDisponible);
			trDisponible.setVisibility(TableRow.VISIBLE);

			TableRow trSaldoPendiente = (TableRow) findViewById(R.id.trSaldoPendiente);
			trSaldoPendiente.setVisibility(TableRow.VISIBLE);

			if (disponible <= 0)
				lblDisponible.setTextColor(0xff8B0000);
			else
				lblDisponible.setTextColor(0xff008000);

			lblDisponible.setTypeface(Typeface.DEFAULT_BOLD);

		} else {

			TableRow trDisponible = (TableRow) findViewById(R.id.trDisponible);
			trDisponible.setVisibility(TableRow.GONE);

			TableRow trSaldoPendiente = (TableRow) findViewById(R.id.trSaldoPendiente);
			trSaldoPendiente.setVisibility(TableRow.GONE);

		}

	}

	public void OnClickRegresar(View view) {
		setResult(RESULT_OK);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case Const.RESP_PEDIDO_EXITOSO:
			case Const.RESP_NO_COMPRA_EXITOSO:
				// setResult(RESULT_OK);
				// finish();
				break;
			case Const.RESP_RECAUDO_EXITOSO:
				// setResult(RESULT_OK);
				// finish();
				break;
			case Const.RESP_EDITAR_CLIENTE:
				break;
			}
		}
	}

	public void MostrarAlertDialog(String mensaje) {
		if (alertDialog == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			alertDialog = builder.create();
		}
		alertDialog.setMessage(mensaje);
		alertDialog.show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	OnClickListener clicksBtnMenu = new OnClickListener() {
		@Override
		public void onClick(View v) {
		}
	};

	public void crearBotonesMenu() {
		int numeroColumnas = DataBaseBO.ObtenerNumeroColumnas();
		listadoOpciones = DataBaseBO.CargarOpciones(2);
		int cantidadFilas = (listadoOpciones.size() / numeroColumnas);
		cantidadFilas++;
		int posBotonActual = 0;
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.lyMenu);
		for (int i = 0; i < cantidadFilas; i++) {
			if (posBotonActual >= listadoOpciones.size())
				break;
			LinearLayout inflatedView;
			LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (numeroColumnas == 2)
				inflatedView = (LinearLayout) inflater.inflate(R.layout.layout_menu2, null);
			else
				inflatedView = (LinearLayout) inflater.inflate(R.layout.layout_menu3, null);
			for (int j = 0; j < numeroColumnas; j++) {
				if (posBotonActual >= listadoOpciones.size())
					break;
				BotonesMenu botonMenu = listadoOpciones.elementAt(posBotonActual);
				int idImgTop;
				switch (botonMenu.id) {
				case Const.MOD_PEDIDOS:
					idImgTop = R.drawable.op_pedido;
					break;
				case Const.MOD_NO_COMPRA:
					idImgTop = R.drawable.op_no_compra;
					break;
				case Const.MOD_CAMBIOS:
					idImgTop = R.drawable.op_devolucion;
					break;
				case Const.MOD_CARTERA:
					idImgTop = R.drawable.op_cartera;
					break;
				case Const.MOD_RECAUDO:
					idImgTop = R.drawable.op_recaudo;
					break;
				case Const.MOD_EDITAR_CLIENTE:
					idImgTop = R.drawable.op_editar_cliente;
					break;
				case Const.MOD_MERCADEO:
					idImgTop = R.drawable.mercadeo;
					break;
				case Const.MOD_CERTIFICADO:
					idImgTop = R.drawable.gps_icon;
					break;
				case Const.MOD_INFO_CLIENTE_SALIR:
					idImgTop = R.drawable.op_regresar;
					break;
				default:
					continue;
				}
				LinearLayout lyAux = (LinearLayout) inflatedView.getChildAt(j);
				Button btn = (Button) lyAux.getChildAt(0);
				btn.setText(botonMenu.texto);
				btn.setTag("" + botonMenu.id);
				btn.setOnClickListener(this);
				if (idImgTop > 0) {
					btn.setCompoundDrawablesWithIntrinsicBounds(0, idImgTop, 0, 0);
				}
				posBotonActual++;
			}
			linearLayout.addView(inflatedView);
		}
	}

	@Override
	public void onClick(View view) {
		if (view instanceof Button) {
			String strTag = view.getTag().toString();
			int position = Util.ToInt(strTag);
			switch (position) {
			case Const.MOD_PEDIDOS:
				
				if (DataBaseBOJ.VerificarRecaudo(Main.cliente.codigo)) {
					float dias = DataBaseBOJ.diasDePago(Main.cliente.codigo);
					Util.mostrarToast(this, "Debe Pagar los Recaudos Pendientes por mas de " + dias
							+ " Dias, para hacer un Nuevo Pedido");
				} else {

					//clienteCertificado
					if (clienteCoordenadas == false && clienteCertificado == false) {


						Util.MostrarAlertDialog(this,
								"El cliente no tiene coordenadas certificadas, por favor certifique la coordenada!");
						return;
//
//					AlertDialog alertDialog;
//					
//					ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.Theme_Dialog_Translucent);
//					AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
//					builder.setCancelable(false).setPositiveButton("SI", new DialogInterface.OnClickListener() {
//					    
//					    public void onClick(DialogInterface dialog, int id) {
//					    	pedido();
//					    	dialog.cancel();
//					    }
//					});
//					builder.setCancelable(false).setNegativeButton("NO", new DialogInterface.OnClickListener() {
//						
//						
//						public void onClick(DialogInterface dialog, int which) {
//							dialog.cancel();
//							
//						}
//					});
//					alertDialog = builder.create();
//					alertDialog.setMessage("El cliente no tiene coordenadas certificadas, Desea continuar sin coordenada certificada?");
//					alertDialog.show();
					}else{
						pedido();
					}

					
				}
				break;
			case Const.MOD_NO_COMPRA:
				
				//clienteCertificado
				if (clienteCoordenadas == false && clienteCertificado == false) {

					Util.MostrarAlertDialog(this,
							"El cliente no tiene coordenadas certificadas, por favor certifique la coordenada!");
					return;

				}
				noCompra();
				break;
			case Const.MOD_CAMBIOS:
				
				
				//clienteCertificado
				if (clienteCoordenadas == false && clienteCertificado == false) {

					Util.MostrarAlertDialog(this,
							"El cliente no tiene coordenadas certificadas, por favor certifique la coordenada!");
					return;

				}
				cambios();
				break;
			case Const.MOD_CARTERA:
				

				//clienteCertificado
				if (clienteCoordenadas == false && clienteCertificado == false) {

						

						AlertDialog alertDialog;
						
						ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.Theme_Dialog_Translucent);
						AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
						builder.setCancelable(false).setPositiveButton("SI", new DialogInterface.OnClickListener() {
						    
						    public void onClick(DialogInterface dialog, int id) {
						    	cartera();
						    	dialog.cancel();
						    }
						});
						builder.setCancelable(false).setNegativeButton("NO", new DialogInterface.OnClickListener() {
							
							
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
								
							}
						});
						alertDialog = builder.create();
						alertDialog.setMessage("El cliente no tiene coordenadas certificadas, Desea continuar sin coordenada certificada?");
						alertDialog.show();
						}else{
							cartera();
						}
				
				
				break;
			case Const.MOD_RECAUDO:
				
				if (clienteCoordenadas == false && clienteCertificado == false) {

					Util.MostrarAlertDialog(this,
							"El cliente no tiene coordenadas certificadas, por favor certifique la coordenada!");
					return;

				}
				// Util.mostrarToast(this, "Modulo de recaudo en Construccion");
				recaudo();
				break;
			case Const.MOD_EDITAR_CLIENTE:
				editarCliente();
				break;
			case Const.MOD_INFO_CLIENTE_SALIR:
				finish();
				break;
			case Const.MOD_CERTIFICADO:
				certificarCoordenadas();
				break;
			case Const.MOD_MERCADEO:
				Util.mostrarToast(this, "Modulo No Activo");
				break;
			}
		}
	}

	public void pedido() {

		int estaEnElRango= 0;
		
		if (SystemClock.elapsedRealtime() - mLastClickTime < 2500) {
			return;
		}
		mLastClickTime = SystemClock.elapsedRealtime();
		
		
		if(validarGeoCerca(longitud, latitud)){
			estaEnElRango = 1; 
		}else{
			estaEnElRango = 2;
		}

		// el cliente es de credito
		if (esClienteDeCredito) {

			if (disponible <= 0) {

				Util.MostrarAlertDialog(FormInfoClienteActivity.this,
						"El Cliente de Credito no tiene cupo disponible para hacer la venta\n\nSe debe realizar el recaudo para liberar el cupo del cliente");
				return;

			}

		}

		DataBaseBO.setAppAutoventa();

		if (Const.tipoAplicacion == Const.AUTOVENTA) {
			long consecutivoActual = DataBaseBO.getConsecutivoActual();
			if (consecutivoActual == -1) {
				Util.MostrarAlertDialog(FormInfoClienteActivity.this,
						"No tiene consecutivos de facturacion, por tal motivo no puede tomar el pedido");
			} else {
				if (DataBaseBO.consecutivoEnElRango(consecutivoActual)) {
					// DataBaseBO.cancelarPedidoAv();
					Intent iPedido = new Intent(FormInfoClienteActivity.this, FormPedidosAutoventa.class);
					iPedido.putExtra("esClienteDeCredito", esClienteDeCredito);
					iPedido.putExtra("disponible", disponible);
					iPedido.putExtra("ESVALIDO", estaEnElRango);
					startActivity(iPedido);
				} else {
					Util.MostrarAlertDialog(FormInfoClienteActivity.this,
							"No tiene consecutivos de facturacion, por tal motivo no puede tomar el pedido");
				}
			}
		}
		if (Const.tipoAplicacion == Const.PREVENTA) {
			if (!DataBaseBO.ExistePedidoCliente(Main.cliente.codigo)) {
				long consecutivoActual = DataBaseBO.getConsecutivoActual();
				if (consecutivoActual == -1) {
					Util.MostrarAlertDialog(FormInfoClienteActivity.this,
							"No tiene consecutivos de facturacion, por tal motivo no puede tomar el pedido");
				} else {
					DataBaseBO.cancelarPedidoAv();
					Intent iPedido = new Intent(FormInfoClienteActivity.this, FormPedidosPreventa.class);
					iPedido.putExtra("esClienteDeCredito", esClienteDeCredito);
					iPedido.putExtra("disponible", disponible);
					iPedido.putExtra("ESVALIDO", estaEnElRango);
					startActivity(iPedido);
				}
			} else {
				mostrarMensajeYaTienePedido();
			}
		}
	}

	public void noCompra() {
		Intent intent = new Intent(this, FormNoCompraActivity.class);
		startActivity(intent);
	}

	public void cambios() {
		if (!Main.cliente.esClienteNuevo) {
			if (Const.tipoAplicacion == Const.AUTOVENTA) {
				DataBaseBO.cancelarPedidoAv();
				Intent iCambios = new Intent(this, FormCambiosAutoventa.class);
				iCambios.putExtra("cambio", true);
				startActivity(iCambios);
			}
			if (Const.tipoAplicacion == Const.PREVENTA) {
				DataBaseBO.cancelarPedidoAv();
				Intent iCambios = new Intent(this, FormCambiosPreventa.class);
				iCambios.putExtra("cambio", true);
				startActivity(iCambios);
			}
		} else {
			Util.MostrarAlertDialog(this, "Opcion No Habilitda Para Clientes Nuevos");
		}
	}

	public void cartera() {
		Intent intentCartera = new Intent(this, FormCarteraInformacion.class);
		startActivity(intentCartera);
	}

	private void recaudo() {
		Intent intentRecaudo = new Intent(this, FormRecaudoFacturasActivity.class);
		startActivity(intentRecaudo);
	}

	public void editarCliente() {
		Intent IntentEditarCliente = new Intent(this, FormEditarClienteActivity.class);
		startActivity(IntentEditarCliente);
	}

	public void mostrarMensajeYaTienePedido() {
		AlertDialog alertDialog;
		ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.Theme_Dialog_Translucent);
		AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
		builder.setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// dialog.cancel();
				Intent formInfoCliente = new Intent(FormInfoClienteActivity.this, FormPedidosAutoventa.class);
				formInfoCliente.putExtra("esClienteDeCredito", esClienteDeCredito);
				formInfoCliente.putExtra("disponible", disponible);
				startActivityForResult(formInfoCliente, Const.RESP_PEDIDO_EXITOSO);
			}
		}).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		alertDialog = builder.create();
		alertDialog.setMessage("Este Cliente ya tiene registrado un pedido. Desea Ingresar un nuevo pedido?");
		alertDialog.show();
	}

	private void carpturarCoordenada() {
		if (!guardoCoordenada)
			iniciarGPS();
	}

	private void iniciarGPS() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		boolean accesoCorrecto = false;
		accesoCorrecto = Util.validarAccesoGPS(locationManager);
		if (!accesoCorrecto) {
			Util.MostrarAlertDialog(this, "Active el GPS antes de Continuar");
			return;
		}
		try {
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setAltitudeRequired(false);
			criteria.setBearingRequired(false);
			criteria.setCostAllowed(true);
			criteria.setPowerRequirement(Criteria.POWER_LOW);
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			provider = locationManager.getBestProvider(criteria, true);
			if (isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				// Verifica si GPS_PROVIDER esta Activo. En caso tal se
				// lanza el Timer de Captura de Coordenadas
				provider = LocationManager.GPS_PROVIDER;
			}
			gpsListener = new GPSListener();
			locationManager.requestLocationUpdates(provider, 0, 1000, gpsListener);
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					handler2.sendEmptyMessage(0);
					try {
						this.finalize();
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			Timer timer = new Timer();
			timer.schedule(task, 15 * 1000);
		} catch (Exception e) {
			Log.e("FormInfoClienteActivity", "IniciarGPS -> " + e.getMessage(), e);
		}
	}

	public boolean isProviderEnabled(String provider) {
		LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (manager != null) {
			return manager.isProviderEnabled(provider);
		}
		return false;
	}

	public boolean IsEnableGPS() {
		LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (manager != null)
			return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
					&& manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		return false;
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				if (locationManager != null && gpsListener != null)
					locationManager.removeUpdates(gpsListener);
				if (currentLocation != null) {
					DataBaseBO.ValidarUsuario();
					Coordenada coordenada = new Coordenada();
					coordenada.codigoVendedor = Main.usuario.codigoVendedor;
					coordenada.codigoCliente = Main.cliente.codigo;
					coordenada.latitud = currentLocation.getLatitude();
					coordenada.longitud = currentLocation.getLongitude();
					// coordenada.sincronizado = 0;
					// coordenada.bandera = 0;
					coordenada.horaCoordenada = Util.FechaActual("HH:mm:ss");
					coordenada.id = "A" + Util.ObtenerFechaId();
					coordenada.imei = Util.obtenerImei(FormInfoClienteActivity.this);
					coordenada.fechaCoordenada = Util.FechaActual("yyyy-MM-dd HH:mm:ss");
					coordenada.tipoCaptura = provider.equals(LocationManager.NETWORK_PROVIDER) ? 1 : 0;
					coordenada.inicioCliente = 1;
					latitud = currentLocation.getLatitude();
					longitud = currentLocation.getLongitude();

					guardoCoordenada = DataBaseBO.GuardarCoordenadaCliente(coordenada);
					if (guardoCoordenada) {
						if (locationManager != null) {
							locationManager.removeUpdates(gpsListener);
						}
					}
				}
			} catch (Exception e) {
				Log.e("FormInfoClienteActivity", "GuardarCoordenadaCliente -> " + e.getMessage(), e);
			}
		}
	};
	private Handler handler2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (!guardoCoordenada) {
				if (locationManager != null) {
					locationManager.removeUpdates(gpsListener);
				}
				// Toast.makeText(FormInfoClienteActivity.this,"Lo encontro por
				// NETWORK_PROVIDER", Toast.LENGTH_SHORT).show();
				// Util.MostrarAlertDialog(FormInfoClienteActivity.this, "Lo
				// encontro por NETWORK_PROVIDER");
				// System.out.println("Lo encontro por NETWORK_PROVIDER");
				if (isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
					// Verifica si NETWORK_PROVIDER esta Activo. En
					// caso tal se
					// lanza el Timer de Captura de Coordenadas
					provider = LocationManager.NETWORK_PROVIDER;
				}
				if (gpsListener == null)
					gpsListener = new GPSListener();
				locationManager.requestLocationUpdates(provider, 0, 0, gpsListener);
			} else {
				// Toast.makeText(FormInfoClienteActivity.this,"lo encontro por
				// GPS_PROVIDER", Toast.LENGTH_SHORT).show();
				// Util.MostrarAlertDialog(FormInfoClienteActivity.this, "lo
				// encontro por GPS_PROVIDER");
				// System.out.println("lo encontro por GPS_PROVIDER");
			}
		}
	};

	private class GPSListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				currentLocation = location;
				handler.sendEmptyMessage(0);
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	public void certificarCoordenadas() {

		if (SystemClock.elapsedRealtime() - mLastClickTime < 2500) {
			return;
		}
		mLastClickTime = SystemClock.elapsedRealtime();
		
		if (clienteCertificado) {

			Util.MostrarAlertDialog(this,
					"El cliente ya tiene coordenadas certificadas para cambiar favor comunicarse con el administrador!");
			return;

		}

		mMyTask = new ObtenerCoordenadas(FormInfoClienteActivity.this).execute(mensaje);

	}

	public class ObtenerCoordenadas extends AsyncTask<String, Void, String> {

		private ProgressDialog progressDialog;
		private Activity activity;
		private Context context;

		public ObtenerCoordenadas(Activity activity) {
			this.activity = activity;
			context = activity;
			progressDialog = new ProgressDialog(context);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			this.progressDialog.setMessage("Progress start");
			this.progressDialog.show();

		}

		@Override
		protected String doInBackground(String... params) {
			try {

				Thread.sleep(20000);

				if (latitud == 0 && longitud == 0) {
					mensaje = "No es posible capturar coordenadas por favor volver a intentar";
					carpturarCoordenada();
					return mensaje;
				}

				Coordenada coordenada = new Coordenada();
				coordenada.codigoVendedor = Main.usuario.codigoVendedor;
				coordenada.codigoCliente = Main.cliente.codigo;
				coordenada.latitud = currentLocation.getLatitude();
				coordenada.longitud = currentLocation.getLongitude();
				coordenada.fechaCoordenada = Util.FechaActual("yyyy-MM-dd HH:mm:ss");

				if (DataBaseBO.GuardarCoordenadaCertificadas(coordenada)) {
					mensaje = "Coordenadas guardada de forma correcta!";
					clienteCertificado = true;

				} else {
					mensaje = "No fue posible guardar coordenadas";
					return mensaje;

				}

				return mensaje;
			} catch (Exception e) {
				Log.e("tag", "error", e);
				return e.getMessage();
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			progressDialog.dismiss();
			if (!result.isEmpty()) {
				Util.MostrarAlertDialog(context, result);
			}

		}

	}
		
	private boolean validarGeoCerca(double latitudActual, double longitudActual) {

		boolean estado ;
		double diff ;

		diff = Util.validarRangoGeoCerca(latitudActual, longitudActual, Main.cliente);
		estado = Util.validarDistanciaMaximaGeoCerca(diff);

		return estado;

	}

}
