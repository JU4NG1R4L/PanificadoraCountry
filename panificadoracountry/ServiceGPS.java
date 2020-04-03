package co.com.panificadoracountry;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import co.com.BusinessObject.DataBaseBO;
import co.com.Conexion.Sync;
import co.com.DataObject.Coordenada;

public class ServiceGPS extends Service implements Sincronizador {
	
	private final IBinder binder = new LocalBinder();
	private boolean guardoCoordenada = false;

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	@Override
	public void onCreate() {
		
		Log.i(TAG, "Iniciando Servicio GPS...");
//		Toast.makeText(this, "Iniciando Servicio GPS...", Toast.LENGTH_SHORT).show();
		iniciarGPS();
	}

	@Override
	public void onDestroy() {
		
		Log.i(TAG, "Deteniendo Servicio GPS...");
		detenerGPS();
	}
	
	@Override
	public void onStart(Intent intent, int startid) { }
	
	@Override
	public void RespSync(boolean ok, String respuestaServer, String msg, int codeRequest) {
		 
		 Message msgObj = new Message();
		 Bundle bundle = new Bundle();

		if(ok)
			bundle.putString("message", "1");
		else
			bundle.putString("message", msg);
		
		msgObj.setData(bundle);
		handlerMensajeRespuesta.sendMessage(msgObj);
		
	}
	
	private Handler handlerMensajeRespuesta = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			
//			if(msg.getData().getString("message").equals("1"))
//				Toast.makeText(ServiceGPS.this,"Envio Coordenada Correctamente ", Toast.LENGTH_SHORT).show();
//			else
//				Toast.makeText(ServiceGPS.this,"No Pudo Enviar Coordenada por : " + msg.getData().getString("message"), Toast.LENGTH_SHORT).show();
			
		}
	};
	
	public class LocalBinder extends Binder {
		 
		 ServiceGPS getService() {
			 return ServiceGPS.this;
		 }
	}

	/**********************************************************************************************************
	 ************************************** Definicion del Modulo de GPS **************************************
	 *********************************************************************************************************/
	public void iniciarGPS() {
		
		Coordenada.crearTablaCoordenadas();
		
		
		if (isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			
			//Verifica si GPS_PROVIDER esta Activo. En caso tal se lanza el Timer de Captura de Coordenadas
			lanzarHandlerGPS(LocationManager.GPS_PROVIDER);
    	    
    	} /*else if (isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
    		
    		//Verifica si NETWORK_PROVIDER esta Activo. En caso tal se lanza el Timer de Captura de Coordenadas
    		lanzarHandlerGPS(LocationManager.NETWORK_PROVIDER);
    	    
    	}*/ else {
    		
    		/**
    		 * Se remueve de los Settings del Movil el nombre del proveedor de GPS.
    		 * Esto es porque el GPS esta Apagado.
    		 **/
    		removeGPSProvider();
    		
    		DataBaseBO.ValidarUsuario();
			
			if (Main.usuario != null && Main.usuario.codigoVendedor != null) {
				
				/**
				 * Se envia la coordenada 0,0 con estado de que el GPS esta Apagado 
				 **/
				
				Coordenada coordenada = new Coordenada();
				coordenada.codigoVendedor = Main.usuario.codigoVendedor;
				coordenada.codigoCliente  = "0";
				coordenada.latitud        = 0;
				coordenada.longitud       = 0; 
				coordenada.horaCoordenada = Util.FechaActual("HH:mm:ss");
				coordenada.estado         = Coordenada.ESTADO_GPS_APAGADO;
				coordenada.id             = Coordenada.obtenerId(Main.usuario.codigoVendedor);
				coordenada.imei            = Util.obtenerImei(this);
				coordenada.fechaCoordenada = Util.FechaActual("yyyy-MM-dd HH:mm:ss");
				coordenada.tipoCaptura	   = 0;   
				
				Sync sync = new Sync(ServiceGPS.this, Const.ENVIAR_COORDENADAS);
				sync.coordenada = coordenada;
				sync.start();
			}
    	}
	}
	
	public boolean isProviderEnabled(String provider) {
		
	    LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

	    if (manager != null) {
	    	return manager.isProviderEnabled(provider);
	    }
	    
	    return false;
	}
	
	public void lanzarHandlerGPS(String provider) {
		
		/**
		 * Se guarda el Provider para Captura de Coordenadas.
		 * Y Se remueve el listener en caso de que haya uno en ejecucion. 
		 **/
		setGPSProvider(provider);
		detenerGPS();
		
		if (gpsHandler == null) {
			gpsHandler = new Handler();
		}
		
		gpsHandler.postDelayed(runnable, 0);
	}
	
	public void detenerGPS() {
		
		if (gpsHandler != null && runnable != null) {
			gpsHandler.removeCallbacks(runnable);
		}
		
		if (locationManager != null && gpsListener != null) {
			locationManager.removeUpdates(gpsListener);
		}
	}
	
	public void enviarCoordenada(Location currentLocation) {
		
//		Toast.makeText(ServiceGPS.this,"Entro A Enviar Coordenada ", Toast.LENGTH_SHORT).show();
		
		if (currentLocation != null) {
			
			if (locationManager != null && gpsListener != null) {
				locationManager.removeUpdates(gpsListener);
			}
			
			DataBaseBO.ValidarUsuario();
			
			if (Main.usuario != null && Main.usuario.codigoVendedor != null) {
				
				Coordenada coordenada = new Coordenada();
				coordenada.codigoVendedor  = Main.usuario.codigoVendedor;
				coordenada.codigoCliente   = "0";
				coordenada.latitud         = currentLocation.getLatitude();
				coordenada.longitud        = currentLocation.getLongitude(); 
				coordenada.estado          = Coordenada.ESTADO_GPS_CAPTURO;
				coordenada.id              = Coordenada.obtenerId(Main.usuario.codigoVendedor);
				coordenada.imei            = Util.obtenerImei(this);
				coordenada.fechaCoordenada = Util.FechaActual("yyyy-MM-dd HH:mm:ss");
				coordenada.tipoCaptura	   = currentLocation.getProvider().equals(LocationManager.NETWORK_PROVIDER) ? 1 : 0;    
				
				Sync sync = new Sync(ServiceGPS.this, Const.ENVIAR_COORDENADAS);
				sync.coordenada = coordenada;
				sync.start();
				
				guardoCoordenada = true;
				
//				Toast.makeText(ServiceGPS.this,"Envio Coordenada ", Toast.LENGTH_SHORT).show();
			}
    	}
	}
	
	public void registrarListenerGPS() {
    	
    	try {
    		
    		guardoCoordenada = false;
    		enviar = true;
    		String provider = getGPSProvider();
    		
    		if (provider != null && !provider.equals("")) {
    			
    			if (locationManager == null) {
        			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    			}
        		
        		if (gpsListener == null) {
    				gpsListener = new GPSListener();
        		}
    			
    			//Registra el Listener de Coordenadas.
    			locationManager.requestLocationUpdates(provider, minTime, 0, gpsListener);
    			
    			
    			//Lanza un timer para que en un determinado tiempo si no ha tomado coordenada
    			//la tome por network
    			
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
					
					Timer timer = new Timer (); 
					//espera 4 minutos
					timer.schedule(task, 1 * 60 * 1000);
    			
    		} else {
    			
    			Log.e(TAG, "registrarListenerGPS -> No se encontro el Provider");
    		}
			
		} catch (Exception e) {
			
			Log.e(TAG, "registrarListenerGPS -> " + e.getMessage(), e);
		}
	}
	
	private Handler handler2 = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			
			if (!guardoCoordenada) {
				
				if (locationManager != null) {
					locationManager.removeUpdates(gpsListener);
				}
				
//				Toast.makeText(ServiceGPS.this,"Lo encontro por NETWORK_PROVIDER", Toast.LENGTH_SHORT).show();
//				Util.MostrarAlertDialog(FormInfoClienteActivity.this, "Lo encontro por NETWORK_PROVIDER");
				
//				System.out.println("Lo encontro por NETWORK_PROVIDER");

				if (isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

					// Verifica si NETWORK_PROVIDER esta Activo. En
					// caso tal se
					// lanza el Timer de Captura de Coordenadas
					String provider = LocationManager.NETWORK_PROVIDER;
					
					if(gpsListener == null)
						gpsListener = new GPSListener();
						
					locationManager.requestLocationUpdates(provider, 0, 0, gpsListener);
				}
			}
		
		}
	};
	
	public String getGPSProvider() {
		
		SharedPreferences settings = getSharedPreferences(Coordenada.SETTINGS_GPS, MODE_PRIVATE);
		return settings.getString(Coordenada.PROVIDER, "");
	}
	
	public void setGPSProvider(String provider) {
		
		SharedPreferences settings = getSharedPreferences(Coordenada.SETTINGS_GPS, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Coordenada.PROVIDER, provider);
        editor.commit();
	}
	
	public void removeGPSProvider() {
		
		SharedPreferences settings = getSharedPreferences(Coordenada.SETTINGS_GPS, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(Coordenada.PROVIDER);
        editor.commit();
	}
	
	private Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			
//			Toast.makeText(ServiceGPS.this,"entro al Runnable", Toast.LENGTH_SHORT).show();
			registrarListenerGPS();
	    	gpsHandler.postDelayed(this, time);
		}
	};
	
	private class GPSListener implements LocationListener {
		
		@Override
		public void onLocationChanged(Location location) {
			
			if (location != null && enviar) {
				
				enviar = false;
				enviarCoordenada(location);
			}
		}

		@Override
		public void onProviderDisabled(String provider) { }

		@Override
		public void onProviderEnabled(String provider) { }

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) { }
	}
	
	static Handler gpsHandler;
	static GPSListener gpsListener;
	static LocationManager locationManager;
	
	static boolean enviar = true;
	
	//3 Segundos para que el listener GPS espere entre una captura y otra. 
	public long minTime = 3 * 1000;
	
	//Registra Coordenadas Cada 30 Minutos
	public long time = 30 * 60 * 1000;
//	public long time = 60 * 10a00;
	
	public static final String TAG = ServiceGPS.class.getName();
}
