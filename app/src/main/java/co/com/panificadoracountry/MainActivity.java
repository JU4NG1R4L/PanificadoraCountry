package co.com.panificadoracountry;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.TimerTask;

import co.com.BusinessObject.ConfigBO;
import co.com.BusinessObject.DataBaseBO;
import co.com.Conexion.Sync;
import co.com.woosim.printer.DownloadLogo;

public class MainActivity extends Activity implements Sincronizador {
  public static final String TAG = "MainActivity";
  ProgressDialog progressDialog;
  Dialog dialogMensaje;
  LocationManager locationManager;
  Location currentLocation = null;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    permisosApp();
    Util.closeTecladoStartActivity(this);
    CargarVersion();
    String txtCodigoDeUsuario = "";
    if (DataBaseBO.ExisteDataBase()) {
      txtCodigoDeUsuario = DataBaseBO.ObtenerCodigoDeUsuario();
      ((EditText) findViewById(R.id.txtUsuarioMain)).setText(txtCodigoDeUsuario);
      /* Usado para generar consecutivo de facturacion */
      DataBaseBO.crearTriggerFacturacion(txtCodigoDeUsuario);
    }
    /*
     * Descargar el logo para imprimir si no esta en su ubicacion en memoria
     */
    {
      DownloadLogo logo = new DownloadLogo(MainActivity.this);
      logo.execute();
    }
  }
  
  public void CargarVersion() {
    String versionApp;
    try {
      versionApp = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
      Log.i(TAG, "CargarVersion -> " + versionApp);
    } catch (NameNotFoundException e) {
      versionApp = "1.0";
      Log.e(TAG, "CargarVersion -> " + e.getMessage(), e);
    }
    setTitle(getTitle() + " " + versionApp + Const.TITULO);
  }
  
  public void OnClickLogin(View view) {
    final String usuario = ((EditText) findViewById(R.id.txtUsuarioMain)).getText().toString().trim();
    final String password = ((EditText) findViewById(R.id.txtClaveMain)).getText().toString().trim();
    if (usuario.equals("") || password.equals("")) {
      Util.MostrarAlertDialog(MainActivity.this, "No se pudo Iniciar Sesion.\nUsuario o Clave Incorrecto");
      ((EditText) findViewById(R.id.txtUsuarioMain)).requestFocus();
    } else {
      if (Util.checkSDCard()) {
        if (DataBaseBO.ExisteDataBase()) {
          boolean existe = DataBaseBO.LogIn(usuario, password);
          if (existe) {
            DataBaseBO.ValidarUsuario();
            MostrarFormPrincipal();
          } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false).setPositiveButton("Si", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                LogInServer(usuario, password);
              }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
              }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.setMessage("Usuario o Clave Incorrecto.\n\nDesea Iniciar Sesion con el Servidor?");
            alertDialog.show();
          }
        } else {
          // No existe Base de datos, Se loguea con el Servidor
          LogInServer(usuario, password);
        }
      } else {
        Util.MostrarAlertDialog(this, "Atencion no existe la Tarjeta de Memoria o no esta Accesible", 1);
      }
    }
  }
  
  public void OnClickSalir(View view) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage("Esta seguro que desea salir de la Aplicacion?").setCancelable(false).setPositiveButton("Si", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        finish();
        System.exit(0);
      }
    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        dialog.cancel();
      }
    });
    AlertDialog alert = builder.create();
    alert.show();
  }
  
  public void LogInServer(String usuario, String password) {
    if (IsOnline()) {
      progressDialog = ProgressDialog.show(MainActivity.this, "", "Iniciando Sesion...", true);
      progressDialog.show();
      progressDialog.setIndeterminate(true);
      progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_icon_drawable_animation));
      Sync sync = new Sync(MainActivity.this, Const.LOGIN);
      sync.usuario = usuario;
      sync.clave = password;
      sync.start();
    } else {
      Util.MostrarAlertDialog(this, "No hay conexion a la red.\n\nNo se puede iniciar Sesion con el Servidor debido a que no hay conexion a Internet.");
    }
  }
  
  public boolean IsOnline() {
    ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
    if (networkInfo != null && networkInfo.isConnectedOrConnecting()) return true;
    return false;
  }
  
  public void MostrarFormPrincipal() {
    Intent intent = new Intent(this, FormPrincipalActivity.class);
    startActivityForResult(intent, Const.RESP_FORM_LOGIN);
    startServiceGPS();
  }
  
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == Const.RESP_FORM_LOGIN) {
      ((EditText) findViewById(R.id.txtUsuarioMain)).setText("");
      ((EditText) findViewById(R.id.txtClaveMain)).setText("");
      ((EditText) findViewById(R.id.txtUsuarioMain)).requestFocus();
    }
  }
  
  @Override
  public void RespSync(boolean ok, final String respuestaServer, final String msg, int codeRequest) {
    switch (codeRequest) {
      case Const.LOGIN:
        try {
          if (progressDialog != null) progressDialog.cancel();
          if (ok) {
            boolean hayInfo = DataBaseBO.HayInformacionXEnviar();
            if (hayInfo) {
              final String usuarioActual = DataBaseBO.ObtenerUsuarioActual();
              if (usuarioActual == null) {
                ConfigBO.CrearConfigDB();
                String usuarioLogin = ((EditText) findViewById(R.id.txtUsuarioMain)).getText().toString().trim();
                String canalVenta = respuestaServer.split("@")[1];
                ConfigBO.GuardarConfigUsuario(usuarioLogin, "", 1, Integer.parseInt(canalVenta));
                // No Existe usuario Actual, Carga el Formulario
                // Principal
                MostrarFormPrincipal();
              } else {
                String usuarioLogin = ((EditText) findViewById(R.id.txtUsuarioMain)).getText().toString().trim();
                if (usuarioActual.equals(usuarioLogin)) {
                  ConfigBO.CrearConfigDB();
                  String canalVenta = respuestaServer.split("@")[1];
                  ConfigBO.GuardarConfigUsuario(usuarioLogin, "", 1, Integer.parseInt(canalVenta));
                  // El Usuario Actual es igual al Usuario que se
                  // Logueo, Carga el Formulario Principal
                  MostrarFormPrincipal();
                } else {
                  this.runOnUiThread(new Runnable() {
                    public void run() {
                      Util.MostrarAlertDialog(MainActivity.this, "No se pudo Iniciar Sesion.\n\nHay Informacion Pendiente por Enviar del Usuario " + usuarioActual + ".\n\n" + "Para Cambiar de Usuario, por favor envie primero la informacion Pendiente, ingresando con el Usuario " + usuarioActual);
                    }
                  });
                }
              }
            } else {
              // No hay Informacion Pendiente por enviar.
              // Verifica si el Usuario Actual es diferente del
              // Usuario que se esta Logueando, en Caso tal Borra la
              // Base de Datos
              String usuarioActual = DataBaseBO.ObtenerUsuarioActual();
              String usuarioLogin = ((EditText) findViewById(R.id.txtUsuarioMain)).getText().toString().trim();
              if (usuarioActual != null) {
                if (!usuarioActual.equals(usuarioLogin)) {
                  boolean borro = Util.BorrarDataBase();
                  ConfigBO.CrearConfigDB();
                  String canalVenta = respuestaServer.split("@")[1];
                  ConfigBO.GuardarConfigUsuario(usuarioLogin, "", 1, Integer.parseInt(canalVenta));
                  if (!borro) {
                    Util.MostrarAlertDialog(this, "No se pudo Iniciar Sesion.\n\nPor Favor Intente nuevamente.");
                    return;
                  }
                }
              }
              ConfigBO.CrearConfigDB();
              String canalVenta = respuestaServer.split("@")[1];
              ConfigBO.GuardarConfigUsuario(usuarioLogin, "", 1, Integer.parseInt(canalVenta));
              MostrarFormPrincipal();
            }
          } else {
            this.runOnUiThread(new Runnable() {
              public void run() {
                ((TextView) findViewById(R.id.txtClaveMain)).setText("");
                ((EditText) findViewById(R.id.txtUsuarioMain)).requestFocus();
                Util.MostrarAlertDialog(MainActivity.this, msg);
              }
            });
          }
        } catch (Exception e) {
          String mensaje = e.getMessage();
          Log.e(TAG, "RespSync -> " + mensaje, e);
        }
        break;
      case Const.DOWNLOAD_MESSAGE:
        break;
    }
  }
  
  private class TaskDownloadMessages extends TimerTask {
    @Override
    public void run() {
      if (handlerMensajes != null) handlerMensajes.sendEmptyMessage(0);
    }
  }
  
  private Handler handlerMensajes = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      buscarMensajes();
    }
  };
  private Handler handlerMostrarMensaje = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      Util.MostrarAlertDialog(getApplicationContext(), "Atencion, Tienes un Mensaje Nuevo de Marathon Distribuciones");
    }
  };
  
  public void buscarMensajes() {
    if (DataBaseBO.ExisteDataBase()) {
      String usuarioActual = DataBaseBO.ObtenerUsuarioActual();
      if (usuarioActual != null) {
        Sync sync = new Sync(MainActivity.this, Const.DOWNLOAD_MESSAGE);
        sync.usuario = usuarioActual;
        sync.start();
      }
    }
  }
  
  protected void startServiceGPS() {
    try {
      turnOnGPS();
      if (!isServiceGPSRunning()) {
        Intent intent = new Intent(this, ServiceGPS.class);
        startService(intent);
      }
    } catch (Exception e) {
      String msg = e.getMessage();
      Log.e(TAG, "startServiceGPS -> " + msg, e);
    }
  }
  
  /**
   Verifica si se puede Activar el GPS. En caso tal se activa.
   **/
  protected void turnOnGPS() {
    boolean turnOn = canToggleGPS();
    if (turnOn) {
      String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
      if (provider != null && !provider.contains("gps")) {
        final Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        intent.setData(Uri.parse("3"));
        sendBroadcast(intent);
      }
    }
  }
  
  private boolean canToggleGPS() {
    PackageInfo packageInfo = null;
    PackageManager packageManager = getPackageManager();
    try {
      packageInfo = packageManager.getPackageInfo("com.android.settings", PackageManager.GET_RECEIVERS);
    } catch (NameNotFoundException e) {
      // Paquete no encontrado
      return false;
    }
    if (packageInfo != null) {
      for (ActivityInfo actInfo : packageInfo.receivers) {
        String name = actInfo.name;
        boolean exported = actInfo.exported;
        // Verifica si el receiver es exported. En caso tal se puede
        // actiar el GPS.
        if (name.equals("com.android.settings.widget.SettingsAppWidgetProvider") && exported) {
          return true;
        }
      }
    }
    return false;
  }
  
  protected boolean isServiceGPSRunning() {
    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
      String nameSevice = service.service.getClassName();
      if (ServiceGPS.class.getName().equals(nameSevice)) {
        return true;
      }
    }
    return false;
  }
  
  protected void stopServiceGPS() {
    if (isServiceGPSRunning()) {
      Intent intent = new Intent(this, ServiceGPS.class);
      stopService(intent);
    }
  }


  private boolean permisosApp() {

    int PERMISSION_ALL = 1;
    boolean estado = false;

    String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.REQUEST_INSTALL_PACKAGES, Manifest.permission.CALL_PHONE};

    if (!hasPermissions(this, PERMISSIONS)) {
      ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);

      if (ContextCompat.checkSelfPermission(this, Manifest.permission.REQUEST_INSTALL_PACKAGES) != PackageManager.PERMISSION_GRANTED) {
        checkPermissionInstallPackages();
      }
      estado = false;
    } else {
      estado = true;
    }
    return estado;

  }

  public static boolean hasPermissions(Context context, String... permissions) {
    if (context != null && permissions != null) {
      for (String permission : permissions) {
        if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
          return false;
        }
      }

    }
    return true;
  }

  private void checkPermissionInstallPackages() {
    Log.e(TAG, "REQUEST_INSTALL_PACKAGES-> ");
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.REQUEST_INSTALL_PACKAGES) != PackageManager.PERMISSION_GRANTED) {
      if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.REQUEST_INSTALL_PACKAGES)) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, 1);
      } else {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, 1);
      }
    }
  }
}
