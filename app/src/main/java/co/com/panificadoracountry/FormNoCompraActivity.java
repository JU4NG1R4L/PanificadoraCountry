package co.com.panificadoracountry;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Vector;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import co.com.BusinessObject.DataBaseBO;
import co.com.BusinessObject.DataBaseBOJ;
import co.com.BusinessObject.FileBO;
import co.com.Conexion.Sync;
import co.com.DataObject.Cliente;
import co.com.DataObject.Encabezado;
import co.com.DataObject.MotivoCompra;
import co.com.DataObject.Usuario;

public class FormNoCompraActivity extends Activity implements Sincronizador {
	
	int anchoImg, altoImg;
	Cliente cliente;
	int tipoDeClienteSeleccionado = 1;
	ProgressDialog progressDialog;
	Vector<MotivoCompra> listaMotivosNoCompra;
	Encabezado encabezado;
	/*variable global de la clase*/
	private long mLastClickTime = 0;
	private String docNoCompra;


	//Manejo de coordenadas
	private double latitudActual, longitudActual;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_no_compra);
		CargarMotivosNoCompra();
		//Capturar las coordenadas cuando se ejecute el activity
		validarUbicacion();
	}


	
	protected void onResume(){
		
		super.onResume();
		DataBaseBO.ValidarUsuario();
		FileBO.validarCliente( this );
	}
	


	
	public void OnClickFormNoCompra(View view) {
		
		switch (view.getId()) {
		
			case R.id.btnAceptarFormNoCompra:
				GuardarMotivoNoCompra();
				break;
				
			case R.id.btnCancelarFormNoCompra:
				Finalizar();
				break;
		}
	}
	
	public void Finalizar() {
		
		finish();
	}
	
	public void OnClickTomarFoto(View view) {
		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		String filePath = Util.DirApp().getPath() + "/foto.jpg";
		
		Uri output = Uri.fromFile(new File(filePath));
		intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
		startActivityForResult(intent, Const.RESP_TOMAR_FOTO);
	}
	
	
	
	public void CargarMotivosNoCompra() {
   
		ArrayAdapter<String> adapter;
		Vector<String> listaItems = new Vector<String>();
		listaMotivosNoCompra = DataBaseBO.ListaMotivosNoCompra(listaItems);
		
		if (listaItems.size() > 0) {
			
			String[] items = new String[listaItems.size()];
			listaItems.copyInto(items);
			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
			
		} else {
			
			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[]{});
		}
		
		Spinner spinner = (Spinner) findViewById(R.id.cbNoCompra);        
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
    }
	
		
	
     public void GuardarMotivoNoCompra() {
		
    	 
    	 /*retardo de 1500ms para evitar eventos de doble click.
    	 * esto significa que solo se puede hacer click cada 1500ms.
    	 * es decir despues de presionar el boton, se debe esperar que transcurran
    	 * 1500ms para que se capture un nuevo evento.*/
    	 if (SystemClock.elapsedRealtime() - mLastClickTime < 1500){
    	  return;
    	 }

    	 mLastClickTime = SystemClock.elapsedRealtime();
    	 
    	 
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			String filePath = Util.DirApp().getPath() + "/foto.jpg";
			
			Uri output = Uri.fromFile(new File(filePath));
			intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
			startActivityForResult(intent, Const.RESP_TOMAR_FOTO);
    	    
	}
	
	
	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	
	    	Finalizar();
	    	return true;
	    }
	    
	    return super.onKeyDown(keyCode, event);
	}
	
	
	
	@Override
	public void RespSync(boolean ok, String respuestaServer, String msg, int codeRequest) {
		
		final String mensaje = ok ? "Novedad Registrada con Exito en el servidor" : msg;
		
		if (progressDialog != null)
			progressDialog.cancel();
		
		this.runOnUiThread(new Runnable() {
			
			public void run() {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(FormNoCompraActivity.this);
				builder.setMessage(mensaje)
						
				.setCancelable(false)
				.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int id) {
						
						dialog.cancel();
						FormNoCompraActivity.this.setResult(RESULT_OK);
						Finalizar();
					}
				});
				
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
	}
	
	public Drawable ResizedImage(int newWidth, int newHeight) {
		
		Matrix matrix;
		FileInputStream fd = null;
		Bitmap resizedBitmap = null;
		Bitmap bitmapOriginal = null;
		
		try {
			
			File fileImg = new File(Util.DirApp(), "foto.jpg");
			
			if (fileImg.exists()) {
				
				fd = new FileInputStream(fileImg.getPath());
				bitmapOriginal = BitmapFactory.decodeFileDescriptor(fd.getFD());

	            int width = bitmapOriginal.getWidth();
	            int height = bitmapOriginal.getHeight();
	            
	            //Matrix mtx = new Matrix();
	            
	            /**
	             * Si la Orientacion es 90 Grados, se rota la Imagen
	             **/
	            //int orientacion = ObtenerOrientacionImg();
	            //if (orientacion == 90)
	            //	mtx.postRotate(90);
	            
	            //Bitmap rotatedBMP = Bitmap.createBitmap(bitmapOriginal, 0, 0, width, height, mtx, true);
	            
	            //width = rotatedBMP.getWidth();
	            //height = rotatedBMP.getHeight();
	            
	            if (width == newWidth && height == newHeight) {
	            	
	            	//return new BitmapDrawable(rotatedBMP);
	            	return new BitmapDrawable(bitmapOriginal);
	            }
	            
	            // Reescala el Ancho y el Alto de la Imagen
	            float scaleWidth = ((float) newWidth) / width;
	            float scaleHeight = ((float) newHeight) / height;

	            matrix = new Matrix();
	            matrix.postScale(scaleWidth, scaleHeight);
	            
	            // Crea la Imagen con el nuevo Tamano
	            resizedBitmap = Bitmap.createBitmap(bitmapOriginal, 0, 0, width, height, matrix, true);
	            
	            //width = resizedBitmap.getWidth();
	            //height = resizedBitmap.getHeight();
	            
	            //Matrix mtx = new Matrix();
	            //int orientacion = ObtenerOrientacionImg();
	            //if (orientacion == 90)
	            //	mtx.postRotate(90);
	            
	            //Bitmap rotatedBMP = Bitmap.createBitmap(resizedBitmap, 0, 0, width, height, mtx, true);
	            
	            return new BitmapDrawable(resizedBitmap);
			}
			
			return null;

		} catch(Exception e) {
			
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG);
			return null;
			
		} finally {
			
			if (fd != null) {
				
				try {
					
					fd.close();
					
				} catch (IOException e) { }
			}
			
			fd = null;
			matrix = null;
			resizedBitmap = null;
			bitmapOriginal = null;
			System.gc();
		}
	}
	
	public int ObtenerOrientacionImg() {
		
		String[] projection = { MediaStore.Images.ImageColumns.ORIENTATION };

		int orientacion = 0;
		Cursor cursor = null;
		Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

		try {
			
			if (uri != null) {
				
				cursor = managedQuery(uri, projection, null, null, null);
			}

			if (cursor != null && cursor.moveToLast()) {
				
				orientacion = cursor.getInt(0);
				Log.i("ObtenerOrientacionImg", "ORIENTATION = " + orientacion);
			}
			
		} finally {
			
			if (cursor != null)
				cursor.close();
		}
		
		return orientacion;
	}
	
	public void BorrarFotoCapturada() {

		String[] projection = { MediaStore.Images.ImageColumns.SIZE, 
        						MediaStore.Images.ImageColumns.DISPLAY_NAME, 
        						MediaStore.Images.ImageColumns.DATA, 
        						BaseColumns._ID,
        						};

        Cursor cursor = null;
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        try {
        	
            if (uri != null) {
            	
            	cursor = managedQuery(uri, projection, null, null, null);
            }
            
            if (cursor != null && cursor.moveToLast()) {
            	
            	ContentResolver contentResolver = getContentResolver();
            	int rows = contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, BaseColumns._ID + "=" + cursor.getString(3), null);

                Log.i("BorrarFotoCapturada", "Numero de filas eliminadas : " + rows);
            }
            
        } finally {
        	
            if (cursor != null)
            	cursor.close();
        }
    }
	
	public String ObtenerVersion() {
		
		String version;
		
		try {
        	
        	version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			
		} catch (NameNotFoundException e) {
			
			version = "0.0";
			Log.e("FormNoCompraActivity", e.getMessage(), e);
		}
		
		return version;
	}
	
	public Drawable resizedImage(int newWidth, int newHeight) {
        
        Matrix matrix;
        FileInputStream fd = null;
        Bitmap resizedBitmap = null;
        Bitmap bitmapOriginal = null;
       
        try {
              
            File fileImg = new File(Util.DirApp(), "foto.jpg");
              
            if (fileImg.exists()) {
                    
               fd = new FileInputStream(fileImg.getPath());
               BitmapFactory.Options option = new BitmapFactory.Options();
               option.inDither = false;
               option.inPurgeable = true;
               option.inInputShareable = true;
               option.inTempStorage = new byte[32 * 1024];
               option.inPreferredConfig = Bitmap.Config.RGB_565;
                    
               bitmapOriginal = BitmapFactory.decodeFileDescriptor(fd.getFD(), null, option);
                    
               int width = bitmapOriginal.getWidth();
               int height = bitmapOriginal.getHeight();
             
              // Reescala el Ancho y el Alto de la Imagen
              float scaleWidth = ((float) newWidth) / width;
              float scaleHeight = ((float) newHeight) / height;

              matrix = new Matrix();
              matrix.postScale(scaleWidth, scaleHeight);
             
              // Crea la Imagen con el nuevo Tamano
              resizedBitmap = Bitmap.createBitmap(bitmapOriginal, 0, 0, width, height, matrix, true);
                     BitmapDrawable bmd = new BitmapDrawable(resizedBitmap);      
                    
                     bitmapOriginal.recycle();
                     bitmapOriginal = null;
                     return bmd.mutate();
               }
              
               return null;

        } catch(Exception e) {
              
               Log.e("", "resizedImage -> " + e.getMessage(), e);
               return null;
              
        } finally {
              
               if (fd != null) {
                    
                     try {
                           
                            fd.close();
                           
                     } catch (IOException e) { }
               }
              
               fd = null;
               matrix = null;
               resizedBitmap = null;
               bitmapOriginal = null;
               System.gc();
        }
  }
	
	public String ObtenerImei() {
		
		TelephonyManager manager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getDeviceId();
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == Const.RESP_TOMAR_FOTO && resultCode == RESULT_OK) {

			Drawable imgFoto = resizedImage(320, 480);

			byte[] byteArray = null;

			if (imgFoto == null) {
				Util.mostrarAlertDialogCerrarActivity(FormNoCompraActivity.this, "No se pudo guardar la imagen");

			} else {
				
				Usuario usuario = DataBaseBO.CargarUsuario();
				docNoCompra = DataBaseBO.ObtenterNumeroDoc(usuario.codigoVendedor);;

				Bitmap bitmap = ((BitmapDrawable) imgFoto).getBitmap();
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
				byteArray = stream.toByteArray();

				if (DataBaseBOJ.guardarImagenDeposito(docNoCompra, byteArray)) {

					Spinner spinner = (Spinner) findViewById(R.id.cbNoCompra);
					int position = spinner.getSelectedItemPosition();

					if (position != AdapterView.INVALID_POSITION && listaMotivosNoCompra.size() > 0) {


						MotivoCompra motivoCompra = listaMotivosNoCompra.elementAt(position);
						cliente = Main.cliente;
						Encabezado encabezado = new Encabezado();
						encabezado.codigo_cliente = cliente.codigo;
						encabezado.nombre_cliente = cliente.nombre;
						encabezado.razon_social = cliente.razonSocial;
						encabezado.codigo_novedad = motivoCompra.codigo;
						encabezado.valor_neto = 0;
						encabezado.hora_inicial = Util.FechaActual("yyyy-MM-dd HH:mm:ss");
						encabezado.hora_final = Util.FechaActual("yyyy-MM-dd HH:mm:ss");
						encabezado.tipo_cliente = "V";
						encabezado.observacion = "";
						encabezado.numero_doc = docNoCompra; // Numero
																										// Unico

						

						String version = ObtenerVersion();
						String imei = ObtenerImei();

						if (DataBaseBO.GuardarNoCompraSinFoto(encabezado, usuario, version, imei, latitudActual+"", longitudActual+"")) {

							AlertDialog.Builder builder = new AlertDialog.Builder(FormNoCompraActivity.this);
							builder.setMessage("Novedad Registrada con Exito " + "para el Cliente "
									+ Main.cliente.nombre +".")
									.setCancelable(false)
									.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

										public void onClick(DialogInterface dialog, int id) {

											progressDialog = ProgressDialog.show(FormNoCompraActivity.this, "",
													"Enviando Datos Novedad...", true);
											progressDialog.show();

											Sync sync = new Sync(FormNoCompraActivity.this, Const.ENVIAR_PEDIDO);
											sync.start();

										}
									});

							AlertDialog alert = builder.create();
							alert.show();
						}

					} else {

						Toast.makeText(getApplicationContext(), "No se pudo Guardar la Novedad", Toast.LENGTH_SHORT)
								.show();
					}

				} else {

					Toast.makeText(getApplicationContext(), "No se pudo Guardar la Novedad de la foto",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	private void validarUbicacion() {
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{
					Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
		} else {
			capturarCoordenadas();
		}
	}

	private void capturarCoordenadas() {
		LocationManager mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Localizacion local = new Localizacion();
		local.setMainActivity(this, mLocManager);
		final boolean gpsEnabled = mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!gpsEnabled) {
			Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(settingsIntent);
		}

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
			return;
		}

		mLocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) local);

		mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) local);
	}

	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == 1000) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				capturarCoordenadas();
				return;
			}
		}
	}

	public class Localizacion implements LocationListener {
		FormNoCompraActivity formNoCompraActivity;
		LocationManager lManager;

		public FormNoCompraActivity getMainActivity() {
			return formNoCompraActivity;
		}

		public void setMainActivity(FormNoCompraActivity mainActivity, LocationManager manager) {
			this.formNoCompraActivity = mainActivity;
			this.lManager = manager;
		}

		@Override
		public void onLocationChanged(Location loc) {
			// Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
			// debido a la deteccion de un cambio de ubicacion
			loc.getLatitude();
			loc.getLongitude();

			//Removemos para calcular solo la primera posicion
			lManager.removeUpdates(this);

			latitudActual = loc.getLatitude();
			longitudActual = loc.getLongitude();

		}

		@Override
		public void onProviderDisabled(String provider) {
			// Este metodo se ejecuta cuando el GPS es desactivado
			//Util.MostrarAlertDialog(FormSubMenu.this, "GPS Desactivado");
		}

		@Override
		public void onProviderEnabled(String provider) {
			// Este metodo se ejecuta cuando el GPS es activado
			//Util.MostrarAlertDialog(FormSubMenu.this, "GPS Activado");
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			switch (status) {
				case LocationProvider.AVAILABLE:
					break;
				case LocationProvider.OUT_OF_SERVICE:
					break;
				case LocationProvider.TEMPORARILY_UNAVAILABLE:
					break;
			}
		}
	}
}
