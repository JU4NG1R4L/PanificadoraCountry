package co.com.panificadoracountry;

import java.io.File;
import java.util.Locale;

import co.com.panificadoracountry.R;
import co.com.BusinessObject.ConfigBO;
import co.com.BusinessObject.DataBaseBO;
import co.com.Conexion.Sync;
import co.com.DataObject.Config;
import co.com.DataObject.Usuario;
import co.com.woosim.printer.DownloadLogo;
import co.com.woosim.printer.WoosimR240;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FormPrincipalActivity extends Activity implements Sincronizador {
  
  Dialog dialogSalir;
  AlertDialog alert;
  ProgressDialog progressDialog;
  Criteria criteria;
  LocationManager locationManager;
  Location currentLocation = null;
  Dialog dialogEnviarInfo;
  Dialog dialogIniciarDia;
  Dialog dialogTerminarDia;
  Dialog dialogConfiguracion;
  private int tipoAplicacionAux = 0;
  WakeLock wakeLock;
  
  /**
   Objeto que permite la conexion a la impresora.
   */
  private WoosimR240 wR240 = null;
  
  private long mLastClickTime = 0;
  private long mLastClickTime2 = 0;
  private long mLastClickTime3 = 0;
  private long mLastClickTime4 = 0;
  private long mLastClickTime5 = 0;
  private long mLastClickTime7 = 0;
  private long mLastClickTime8 = 0;
  private long mLastClickTime9 = 0;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.form_principal);
    Inicializar();
    CargarInfoUsuario();
    acquirewakeLock();
  }
  
  public void onResume() {
    super.onResume();
    if (Main.usuario == null || Main.usuario.codigoVendedor == null) DataBaseBO.CargarInfomacionUsuario();
    DataBaseBO.setAppAutoventa();
    Spanned s;
    if (Const.tipoAplicacion == Const.AUTOVENTA) {
      setTitle("AUTOVENTA  - " + getApplicationName(this));
      s = Html.fromHtml("<font color=\"brown\">" + "<u>" + "AUTOVENTA" + "</u></font>" + "");
      ((TextView) findViewById(R.id.lblTipoAplicacion)).setText(s);
      ((Button) findViewById(R.id.btnIniciarDia)).setText("Sincronizar");
      
    } else if (Const.tipoAplicacion == Const.PREVENTA) {
      setTitle("PREVENTA - " + getApplicationName(this));
      s = Html.fromHtml("<font color=\"brown\">" + "<u>" + "PREVENTA" + "</u></font>" + "");
      ((TextView) findViewById(R.id.lblTipoAplicacion)).setText(s);
      ((Button) findViewById(R.id.btnIniciarDia)).setText("Sincronizar");
      
    } else {
      setTitle("CELUWEB - " + getApplicationName(this));
      ((TextView) findViewById(R.id.lblTipoAplicacion)).setText("");
    }
    CargarInfoUsuario();
    if (Main.usuario != null) {
      if (Main.usuario.gps == 1) {
        iniciarGPS();
        
      }
    }
    if (DataBaseBO.existeConteoFisicoInventario()) {
      if (DataBaseBO.HayInformacionXEnviar()) {
      }
      
    }
    
  }
  
  public void Inicializar() {
    ConfigBO.CrearConfigDB();
    try {
      Main.versionApp = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
      Log.i("FormPrincipalActivity", "Version = " + Main.versionApp);
      
    } catch (NameNotFoundException e) {
      Main.versionApp = "0.0.0";
      Log.e("FormPrincipalActivity", e.getMessage(), e);
    }
    setTitle("Aplicacion Estandar FV " + Main.versionApp + Const.TITULO);
    TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    Main.deviceId = manager.getDeviceId();
    Log.i("FormPrincipalActivity", "deviceId = " + Main.deviceId);
  }
  
  public void CargarInfoUsuario() {
    DataBaseBO.CargarInfomacionUsuario();
    Usuario usuario = DataBaseBO.ObtenerUsuario();
    if (usuario != null && usuario.codigoVendedor != null) {
      String msg = "<b>Bienvenido,</b> " + usuario.nombreVendedor;
      ((TextView) findViewById(R.id.lblUsuario)).setText(Html.fromHtml(msg));
      ((TextView) findViewById(R.id.lblFechaLabores)).setText(usuario.fechaLabores);
      
    } else {
      String msg = "<b>Sin Datos</b>";
      ((TextView) findViewById(R.id.lblUsuario)).setText(Html.fromHtml(msg));
      ((TextView) findViewById(R.id.lblFechaLabores)).setText("");
    }
  }
  
  public void OnClickClienteNuevo(View view) {
    if (Util.estaActualLaFecha()) {
      if (DataBaseBO.ExisteDataBase()) {
        if (DataBaseBO.yaTerminoLabores()) {
          Util.MostrarAlertDialog(this, "Ya termino labores");
          return;
          
        }
        Intent intent = new Intent(this, FormClienteNuevoActivity.class);
        startActivity(intent);
        
      } else {
        Util.MostrarAlertDialog(this, "Antes de ingresar clientes nuevos, por favor sincronice primero");
      }
      
    } else {
      Util.MostrarAlertDialog(this, "La fecha de la ultima sincronizacion no coincide con la fecha del celular, por favor organizar la fecha o sincronize iniciando dia");
      
    }
    
  }
  
  public void OnClikFormPrincipal(View view) {
    switch (view.getId()) {
      case R.id.btnRutero:
        if (DataBaseBO.ExisteDataBase()) {
          if (DataBaseBO.ExisteInformacion("Clientes")) {
            Config config = ConfigBO.ObtenerConfigUsuario();
            if (config == null) {
              MostrarAlert("Por favor Ingrese la Configuracion de Usuario e Inicie Dia");
              
            } else {
              if (Util.estaActualLaFecha()) {
                if (DataBaseBO.yaTerminoLabores()) {
                  Util.MostrarAlertDialog(this, "Ya termino labores");
                  return;
                  
                }
                this.runOnUiThread(new Runnable() {
                  
                  public void run() {
                    Main.pdialog = ProgressDialog.show(FormPrincipalActivity.this, "", "Cargando Informacion....", true);
                    Main.pdialog.setIndeterminate(true);
                    Main.pdialog.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_icon_drawable_animation));
                    Main.pdialog.show();
                    
                  }
                });
                Intent formRutero = new Intent(this, FormRuteroActivity.class);
                startActivity(formRutero);
                
              } else {
                Util.MostrarAlertDialog(this, "La fecha de la ultima sincronizacion no coincide con la fecha del celular, por favor organizar la fecha o sincronize iniciando dia");
                
              }
              
            }
            
          } else {
            Util.MostrarAlertDialog(this, "No hay Informacion de Rutero.\n\nVerifique con el Adminstrador si tiene Rutero Asignado", 1);
            
          }
          
        } else {
          Util.MostrarAlertDialog(this, "No hay Informacion de Rutero.\nPor Favor Sincronize", 1);
          
        }
        break;
      case R.id.btnBuscarCliente:
        if (DataBaseBO.ExisteInformacion("Clientes")) {
          if (Util.estaActualLaFecha()) {
            if (DataBaseBO.yaTerminoLabores()) {
              Util.MostrarAlertDialog(this, "Ya termino labores");
              return;
              
            }
            Intent formBuscarCliente = new Intent(this, FormBuscarClienteActivity.class);
            startActivity(formBuscarCliente);
            
          } else {
            Util.MostrarAlertDialog(this, "La fecha de la ultima sincronizacion no coincide con la fecha del celular, por favor organizar la fecha o sincronize iniciando dia");
            
          }
          
        } else {
          Util.MostrarAlertDialog(this, "No hay informacion de Clientes. Por Favor Sincronize.", 1);
          
        }
        break;
      
      /*
       * ***************************
       * case R.id.btnClienteNuevo:
       *
       * if(DataBaseBO.ExisteDataBase()){
       *
       * Intent intent = new Intent(this, FormClienteNuevoActivity.class);
       * startActivity(intent);
       *
       * ***************************** }
       */
      case R.id.btnEstadisticas:
        if (DataBaseBO.ExisteDataBase()) {
          Intent intent = new Intent(this, FormMenuEstadistica.class);
          startActivityForResult(intent, Const.RESP_FORM_INICIAR_DIA);
          
        } else {
          Util.MostrarAlertDialog(this, "No hay Informacion. Por favor Sincronize", 1);
          
        }
        break;
      case R.id.btnVerificarConectividad:
        VerificarSenal();
        break;
      case R.id.btnCerrarSesion:
        CerrarAplicacion();
        break;
      case R.id.btnConsultarPrecios:
        if (DataBaseBO.ExisteInformacion("productos")) {
          Intent intent1 = new Intent(this, FormConsultarPreciosActivity.class);
          startActivityForResult(intent1, Const.RESP_FORM_INICIAR_DIA);
          
        } else {
          Util.MostrarAlertDialog(this, "No hay Informacion. Por favor Sincronize", 1);
          
        }
        break;
      case R.id.btnDeposito:
        if (DataBaseBO.ExisteDataBase()) {
          if (DataBaseBO.yaTerminoLabores()) {
            Util.MostrarAlertDialog(this, "Ya termino labores");
            return;
            
          }
          Intent intent = new Intent(this, FormDepositoActivity.class);
          startActivity(intent);
          
        } else {
          Util.MostrarAlertDialog(this, "No hay Informacion. Por favor Sincronize", 1);
          
        }
        break;
      
    }
  }
  
  public void CerrarAplicacion() {
    setResult(RESULT_OK);
    finish();
  }
  
  public void MostrarAlert(String mensaje) {
    if (alert == null) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
        
        public void onClick(DialogInterface dialog, int id) {
          dialog.cancel();
        }
      });
      alert = builder.create();
    }
    alert.setMessage(mensaje);
    alert.show();
  }
  
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == Const.RESP_FORM_INICIAR_DIA && resultCode == RESULT_OK) {
      CargarInfoUsuario();
    }
  }
  
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      CerrarAplicacion();
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }
  
  public void VerificarSenal() {
    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = cm.getActiveNetworkInfo();
    if (netInfo != null && netInfo.isConnected()) {
      progressDialog = ProgressDialog.show(FormPrincipalActivity.this, "", "Verificando Conexion a Internet", true);
      progressDialog.show();
      Sync sync = new Sync(FormPrincipalActivity.this, Const.STATUS_SENAL);
      sync.start();
      
    } else {
      Util.MostrarAlertDialog2(FormPrincipalActivity.this, "No Hay conexion a Internet en el Momento", 1);
      
    }
    
  }
  
  public void RespSync(boolean ok, String respuestaServer, String msg, int codeRequest) {
    switch (codeRequest) {
      case Const.STATUS_SENAL:
        RespuestaTestDeConectividad(ok, respuestaServer, msg);
        break;
      case Const.DOWNLOAD_DATA_BASE:
        RespuestaDownloadInfo(ok, respuestaServer, msg);
        break;
      case Const.DOWNLOAD_VERSION_APP:
        RespuestaDownloadVersionApp(ok, respuestaServer, msg);
        break;
      case Const.ENVIAR_PEDIDO:
        RespuestaEnviarInfo(ok, respuestaServer, msg);
        break;
      case Const.TERMINAR_LABORES:
        RespuestaTerminarLabores(ok, respuestaServer, msg);
        break;
      
    }
  }
  
  public void RespuestaTestDeConectividad(boolean ok, String respuestaServer, String msg) {
    if (progressDialog != null) progressDialog.cancel();
    if (respuestaServer.startsWith("Ok")) {
      this.runOnUiThread(new Runnable() {
        
        public void run() {
          Util.MostrarAlertDialog(FormPrincipalActivity.this, "Existe Conectividad a Internet");
          
        }
      });
      
    } else {
      this.runOnUiThread(new Runnable() {
        
        public void run() {
          Util.MostrarAlertDialog(FormPrincipalActivity.this, "No Existe Conectividad a Internet en el Momento");
          
        }
      });
      
    }
    
  }
  
  @Override
  protected void onDestroy() {
    super.onDestroy();
    
  }
  
  public void iniciarDia() {
    if (DataBaseBO.HayInformacionXEnviar()) {
      if (Const.tipoAplicacion == Const.PREVENTA) Util.MostrarAlertDialog(this, "Hay informacion pendiente por Enviar. Antes de Sincronizar, por favor Envie Informacion");
      else if (Const.tipoAplicacion == Const.AUTOVENTA) Util.MostrarAlertDialog(this, "Hay informacion pendiente por Enviar. Antes de Sincronizar, por favor Envie Informacion");
      else Util.MostrarAlertDialog(this, "Hay informacion pendiente por Enviar. Antes de Sincronizar, por favor Envie Informacion");
      
    } else {
      Config config = ConfigBO.ObtenerConfigUsuario();
      if (config == null) {
        AlertDialog alertDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(FormPrincipalActivity.this);
        builder.setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
          
          public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
            // MostrarDialogConfiguracion();
          }
        });
        alertDialog = builder.create();
        alertDialog.setMessage("Para Iniciar Dia debe primero establecer la configuracion del usuario");
        alertDialog.show();
        
      } else {
        MostrarDialogIniciarDia(config);
      }
    }
  }
  
  public void MostrarDialogIniciarDia(Config config) {
    if (dialogIniciarDia == null) {
      dialogIniciarDia = new Dialog(this);
      dialogIniciarDia.requestWindowFeature(Window.FEATURE_LEFT_ICON);
      dialogIniciarDia.setContentView(R.layout.dialog_iniciar_dia);
      dialogIniciarDia.setTitle("Sincronizar");
    }
    String mensaje = "";
    mensaje = "<b>Usuario:</b> " + config.usuario + "<br />";
    // mensaje += "<b>Bodega:</b> " + config.bodega + "<br /><br />";
    mensaje += " " + "<br /><br />";
    mensaje += "Esta seguro de Sincronizar?<br /><br />";
    mensaje += "";
    ((TextView) dialogIniciarDia.findViewById(R.id.lblMsgIniciarDia)).setText(Html.fromHtml(mensaje));
    ((Button) dialogIniciarDia.findViewById(R.id.btnAceptarIniciarDia)).setOnClickListener(new OnClickListener() {
      
      public void onClick(View v) {
        tipoAplicacionAux = Const.tipoAplicacion;
        dialogIniciarDia.cancel();
        progressDialog = ProgressDialog.show(FormPrincipalActivity.this, "", "Descargando Informacion...", true);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_icon_drawable_animation));
        progressDialog.show();
        Sync sync = new Sync(FormPrincipalActivity.this, Const.DOWNLOAD_DATA_BASE);
        sync.version = ObtenerVersion();
        sync.imei = ObtenerImei();
        sync.start();
      }
    });
    ((Button) dialogIniciarDia.findViewById(R.id.btnCancelarIniciarDia)).setOnClickListener(new OnClickListener() {
      
      public void onClick(View v) {
        dialogIniciarDia.cancel();
      }
    });
    dialogIniciarDia.setCancelable(false);
    dialogIniciarDia.show();
    dialogIniciarDia.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.op_actualizar);
  }
  
  public String ObtenerVersion() {
    String versionApp;
    try {
      versionApp = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
      
    } catch (NameNotFoundException e) {
      versionApp = "0.0";
      Log.e("FormMenuEstadistica", "ObtenerVersion: " + e.getMessage(), e);
    }
    return versionApp;
  }
  
  public String ObtenerImei() {
    TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    Main.deviceId = manager.getDeviceId();
    if (Main.deviceId == null) {
      WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
      if (wifiManager != null) {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
          String mac = wifiInfo.getMacAddress();
          if (mac != null) {
            Main.deviceId = mac.replace(":", "").toUpperCase(Locale.getDefault());
          }
        }
      }
    }
    return Main.deviceId;
  }
  
  public void OnClickIniciarDia(View view) {
    
    /* Descargar el logo para imprimir si no esta en su ubicacion en memoria */
    {
      DownloadLogo logo = new DownloadLogo(FormPrincipalActivity.this);
      logo.execute();
    }
    iniciarDia();
    
  }
  
  public void RespuestaDownloadInfo(final boolean ok, final String respuestaServer, String msg) {
    final String mensaje = ok ? "Informacion Actualizada correctamente" : msg;
    if (progressDialog != null) progressDialog.cancel();
    this.runOnUiThread(new Runnable() {
      
      public void run() {
        if (ok) CargarInfoUsuario();
        if (DataBaseBO.CargarInfomacionUsuario()) {
          DataBaseBO.setAppAutoventa();
          if (tipoAplicacionAux == Const.tipoAplicacion) {
            if (ok) {
              ActualizarVersionApp();
              
            } else {
              Util.MostrarAlertDialog(FormPrincipalActivity.this, mensaje);
            }
          } else {
            tipoAplicacionAux = Const.tipoAplicacion;
            progressDialog = ProgressDialog.show(FormPrincipalActivity.this, "", "Descargando Informacion...", true);
            progressDialog.show();
            Sync sync = new Sync(FormPrincipalActivity.this, Const.DOWNLOAD_DATA_BASE);
            sync.version = ObtenerVersion();
            sync.imei = ObtenerImei();
            sync.start();
          }
          
        } else {
          Util.MostrarAlertDialog(FormPrincipalActivity.this, "No se pudo Sincronizar.\n\n" + respuestaServer);
        }
      }
    });
  }
  
  public void ActualizarVersionApp() {
    final String versionSvr = DataBaseBO.ObtenerVersionApp();
    String versionApp = ObtenerVersion();
    if (versionSvr != null && versionApp != null) {
      float versionServer = Util.ToFloat(Util.rpad(versionSvr.replace(".", ""), 4, "0"));
      float versionLocal = Util.ToFloat(Util.rpad(versionApp.replace(".", ""), 4, "0"));
      if (versionLocal < versionServer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FormPrincipalActivity.this);
        builder.setMessage("Hay una version de la aplicacion: " + versionSvr).setCancelable(false).setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
          
          public void onClick(DialogInterface dialog, int id) {
            progressDialog = ProgressDialog.show(FormPrincipalActivity.this, "", "Descargando Version " + versionSvr + "...", true);
            progressDialog.show();
            Sync sync = new Sync(FormPrincipalActivity.this, Const.DOWNLOAD_VERSION_APP);
            sync.start();
            
          }
          
        });
        AlertDialog alert = builder.create();
        alert.show();
        
      } else {
        Util.MostrarAlertDialog(FormPrincipalActivity.this, "Informacion Actualizada Correctamente");
      }
      
    } else {
      Util.MostrarAlertDialog(FormPrincipalActivity.this, "Informacion Actualizada Correctamente");
    }
    
  }
  
  public void RespuestaDownloadVersionApp(final boolean ok, final String respuestaServer, String msg) {
    if (progressDialog != null) progressDialog.cancel();
    this.runOnUiThread(new Runnable() {
      
      public void run() {
        if (ok) {
          File fileApp = new File(Util.DirApp(), Const.fileNameApk);
          if (fileApp.exists()) {
            Uri uri = Uri.fromFile(fileApp);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            startActivityForResult(intent, Const.RESP_ACTUALIZAR_VERSION);
            
          } else {
            Util.MostrarAlertDialog(FormPrincipalActivity.this, "No se pudo actualizar la version.");
          }
          
        } else {
          Util.MostrarAlertDialog(FormPrincipalActivity.this, respuestaServer);
        }
      }
    });
  }
  
  public void OnClickTerminarDia(View view) {
    if (DataBaseBO.verificarDeposito()) {
      Util.MostrarAlertDialog(FormPrincipalActivity.this, "Debe cancelar todos los depositos para poder terminar dia.");
    } else {
      terminaDia();
    }
  }
  
  public void OnClickEnviarInfo(View view) {
    enviarInformacion();
    
  }
  
  public void terminaDia() {

    	
    	/*if (DataBaseBO.HayInformacionXEnviar()) {
    	    progressDialog = ProgressDialog.show(FormPrincipalActivity.this, "", "Enviando Informacion...", true);
		    progressDialog.show();
		    
		    // DataBaseBO.ActualizarSnPedidosTmp();
		    
		    Sync sync = new Sync(FormPrincipalActivity.this, Const.ENVIAR_PEDIDO);
		    sync.start();
    	}*/
    terminarDia();
    
  }
  
  public void enviarInformacion() {
    if (DataBaseBO.HayInformacionXEnviar()) {
      if (dialogEnviarInfo == null) {
        dialogEnviarInfo = new Dialog(this);
        dialogEnviarInfo.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        dialogEnviarInfo.setContentView(R.layout.dialog_enviar_info);
        dialogEnviarInfo.setTitle("Enviar Informacion");
      }
      ((Button) dialogEnviarInfo.findViewById(R.id.btnAceptarEnviarInfo)).setOnClickListener(new OnClickListener() {
        
        public void onClick(View v) {
          dialogEnviarInfo.cancel();
          progressDialog = ProgressDialog.show(FormPrincipalActivity.this, "", "Enviando Informacion...", true);
          progressDialog.show();
          // DataBaseBO.ActualizarSnPedidosTmp();
          Sync sync = new Sync(FormPrincipalActivity.this, Const.ENVIAR_PEDIDO);
          sync.start();
        }
      });
      ((Button) dialogEnviarInfo.findViewById(R.id.btnCancelarEnviarInfo)).setOnClickListener(new OnClickListener() {
        
        public void onClick(View v) {
          dialogEnviarInfo.cancel();
        }
      });
      dialogEnviarInfo.setCancelable(false);
      dialogEnviarInfo.show();
      dialogEnviarInfo.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.op_enviar_info);
      
    } else {
      Util.MostrarAlertDialog(this, "No hay informacion pendiente por Enviar");
    }
  }
  
  public void RespuestaEnviarInfo(boolean ok, String respuestaServer, String msg) {
    final String mensaje = ok ? "Informacion Registrada con Exito en el servidor" : msg;
    if (progressDialog != null) progressDialog.cancel();
    this.runOnUiThread(new Runnable() {
      
      public void run() {
        Util.MostrarAlertDialog(FormPrincipalActivity.this, mensaje);
      }
    });
  }
  
  public static String getApplicationName(Context context) {
    int stringId = context.getApplicationInfo().labelRes;
    return context.getString(stringId);
  }
  
  public void iniciarGPS() {
    if (verificarGPS()) {
      iniciarServicioGPS();
      
    } else {
      // Util.MostrarAlertDialog(this,
      // "Por favor habilita el GPS del dispositivo.");
      AlertDialog alertDialog;
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
        
        public void onClick(DialogInterface dialog, int id) {
          dialog.cancel();
          Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
          startActivity(intent);
          
        }
      });
      alertDialog = builder.create();
      alertDialog.setMessage("Por favor habilita el GPS del dispositivo.");
      alertDialog.show();
      
    }
    
  }
  
  private boolean verificarGPS() {
    return isProviderEnabled(LocationManager.GPS_PROVIDER) || isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    
  }
  
  protected boolean isProviderEnabled(String provider) {
    LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    if (manager != null) {
      return manager.isProviderEnabled(provider);
    }
    return false;
  }
  
  public void iniciarServicioGPS() {
    turnOnGPS();
    try {
      startService(new Intent(this, ServiceGPS.class));
      
    } catch (Exception e) {
      String msg = e.getMessage();
      // Log.e(TAG, "iniciarGPS Service -> " + msg, e);
    }
    
  }
  
  protected void turnOnGPS() {
    // Se valida si se puede Activar el GPS
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
    PackageManager packageManager = getPackageManager();
    PackageInfo packageInfo = null;
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
  
  public void acquirewakeLock() {
    PowerManager pm = (PowerManager) getApplicationContext().getSystemService(getApplicationContext().POWER_SERVICE);
    wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TAG");
    wakeLock.acquire();
    // wakeLock.release();
  }
  
  public void OnClickCuadreDeCaja(View view) {
    if (SystemClock.elapsedRealtime() - mLastClickTime2 < 2000) {
      return;
    }
    mLastClickTime2 = SystemClock.elapsedRealtime();
    if (DataBaseBO.verificarDeposito()) {
      Util.MostrarAlertDialog(FormPrincipalActivity.this, "Debe Enviar Informacion.");
    } else {
      if (DataBaseBO.ExisteDataBase()) {
        Intent intent = new Intent(this, FormCuadreDeCajaActivity.class);
        startActivity(intent);
        
      } else {
        Util.MostrarAlertDialog(this, "No hay Informacion. Por favor Sincronize", 1);
        
      }
    }
    
  }
  
  public void terminarDia() {
    if (!DataBaseBO.ExisteDataBase()) {
      Util.MostrarAlertDialog(this, "Sin Datos");
      return;
      
    }
    if (DataBaseBO.yaTerminoLabores()) {
      Util.MostrarAlertDialog(this, "Ya termino labores");
      return;
      
    }
    if (DataBaseBO.HayInformacionXEnviar()) {
      Util.MostrarAlertDialog(this, "Por favor envie la informacion pendiente antes de terminar dia");
      return;
      
    }
    if (Util.estaActualLaFecha()) {
    } else {
      Util.MostrarAlertDialog(this, "La fecha de la ultima sincronizacion no coincide con la fecha del celular, por favor organizar la fecha o sincronize iniciando dia");
      return;
    }
    Intent intent = new Intent(this, FormCuadreDeCajaActivity2.class);
    startActivity(intent);
	
	
	/*
	if (dialogTerminarDia == null) {

		dialogTerminarDia = new Dialog(this);
		dialogTerminarDia.requestWindowFeature(Window.FEATURE_LEFT_ICON);
		dialogTerminarDia.setContentView(R.layout.dialog_terminar_labores);
		dialogTerminarDia.setTitle("Terminar Labores");
	}

	String mensaje = "<b>Esta seguro de Terminar Labores?</b><br /><br />"; 
	mensaje +=	"Una vez Finalice labores no podra Registrar mas pedidos.";

	((TextView) dialogTerminarDia.findViewById(R.id.lblMsgTerminarDia)).setText(Html.fromHtml(mensaje));

	((Button)dialogTerminarDia.findViewById(R.id.btnAceptarTerminarDia)).setOnClickListener(new OnClickListener() {

		public void onClick(View v) {

			String fechaMovil =  Util.FechaActual("yyyy-MM-dd HH:mm:ss");
			Usuario usuario = DataBaseBO.CargarUsuario();
			
			if(DataBaseBO.GuardarTerminoLabores(usuario.codigoVendedor, fechaMovil)) {
			
			if(DataBaseBO.HayInformacionXEnviar()) {
			 
				progressDialog = ProgressDialog.show(FormPrincipalActivity.this, "", "Enviando Informacion...", true);
			    progressDialog.show();

			    //DataBaseBO.ActualizarSnPedidosTmp();

			    Sync sync = new Sync(FormPrincipalActivity.this, Const.TERMINAR_LABORES);
			    sync.start();
			 
			}
			
			}
			
			
			dialogTerminarDia.cancel();
			
		}
	});

	((Button) dialogTerminarDia.findViewById(R.id.btnCancelarTerminarDia)).setOnClickListener(new OnClickListener() {

		public void onClick(View v) {

			dialogTerminarDia.cancel();
		}
	});

	dialogTerminarDia.setCancelable(false);
	dialogTerminarDia.show();
	dialogTerminarDia.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.op_terminar_dia);*/
  }
  
  public void RespuestaTerminarLabores(boolean ok, String respuestaServer, String msg) {
    final String mensaje = ok ? "Terminar Labores Satisfactorio" : msg + "\n\nPor Favor intente Nuevamente";
    if (progressDialog != null) progressDialog.cancel();
    this.runOnUiThread(new Runnable() {
      
      public void run() {
        Util.MostrarAlertDialog(FormPrincipalActivity.this, mensaje);
      }
    });
  }
  
  public void OnClikModuloAuditoria(View view) {
    if (SystemClock.elapsedRealtime() - mLastClickTime < 2500) {
      return;
    }
    mLastClickTime = SystemClock.elapsedRealtime();
    if (!DataBaseBO.ExisteDataBase()) {
      Util.MostrarAlertDialog(this, "Atencion: No Hay Informacion por favor sincronize");
      return;
      
    }
    if (DataBaseBO.existeConteoFisicoInventario()) {
      //Util.MostrarAlertDialog(this, "Ya Existe Liquidacion de inventario para el dia de hoy");
      AlertDialog alertDialog;
      ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.Theme_Dialog_Translucent);
      AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
      builder.setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
        
        public void onClick(DialogInterface dialog, int id) {
          dialog.cancel();
          imprimirTirillaConteoFisico();
        }
      }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
        
        public void onClick(DialogInterface dialog, int id) {
          dialog.cancel();
        }
      });
      alertDialog = builder.create();
      alertDialog.setMessage("Ya Existe Liquidacion de inventario para el dia de hoy\n\n\nDesea Imprimir Tirilla?");
      alertDialog.show();
      return;
      
    }
    if (!DataBaseBO.yaTerminoLabores()) {
      Util.MostrarAlertDialog(this, "Atencion: El Vendedor Debe terminar labores para realizar Auditoria");
      return;
      
    }
    if (DataBaseBO.HayInformacionXEnviar()) {
      Util.MostrarAlertDialog(this, "Por favor envie la informacion pendiente antes de hacer Auditoria");
      return;
      
    }
    if (Util.estaActualLaFecha()) {
    } else {
      Util.MostrarAlertDialog(this, "La fecha de la ultima sincronizacion no coincide con la fecha del celular, por favor organizar la fecha o sincronize iniciando dia");
      return;
    }
    mostrarDialogoIngresarClaveAuditoria();
    
  }
  
  public void mostrarDialogoIngresarClaveAuditoria() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Clave Auditoria");
    // Set up the input
    final EditText input = new EditText(this);
    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    builder.setView(input);
    // Set up the buttons
    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
        String claveIngresada = input.getText().toString();
        String claveAuditor = DataBaseBO.ObtenerClaveDeAuditor();
        if (claveIngresada.equals(claveAuditor)) {
          Intent intent = new Intent(FormPrincipalActivity.this, FormConteoFisicoInventario.class);
          startActivity(intent);
          
        } else {
          Util.MostrarAlertDialog(FormPrincipalActivity.this, "Atencion La Clave Ingresada no es Correcta");
          
        }
        
      }
    });
    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    });
    builder.show();
    
  }
  
  public void imprimirTirillaConteoFisico() {
    imprimirInventarioConteoFisico();
    
  }
  
  protected void imprimirInventarioConteoFisico() {
    progressDialog = ProgressDialog.show(FormPrincipalActivity.this, "Imprimiendo", "Retire la tirilla cuando este lista.", true);
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
              Toast.makeText(FormPrincipalActivity.this, "Aun no hay Impresora Predeterminada.\n\nPor Favor primero Configure la Impresora!", Toast.LENGTH_SHORT).show();
            }
          });
        } else {
          if (wR240 == null) {
            wR240 = new WoosimR240(FormPrincipalActivity.this);
          }
          int conect = wR240.conectarImpresora(macImpresora);
          switch (conect) {
            case 1:
              wR240.generarEncabezadoTirillaInventarioConteo();
              wR240.imprimirBuffer(true);
              break;
            case -2:
              runOnUiThread(new Runnable() {
                
                @Override
                public void run() {
                  Toast.makeText(FormPrincipalActivity.this, "-2 fallo conexion", Toast.LENGTH_SHORT).show();
                }
              });
              break;
            case -8:
              progressDialog.dismiss();
              runOnUiThread(new Runnable() {
                
                @Override
                public void run() {
                  Util.MostrarAlertDialog(FormPrincipalActivity.this, "Bluetooth apagado. Por favor habilite el bluetoth para imprimir.");
                }
              });
              break;
            default:
              runOnUiThread(new Runnable() {
                
                @Override
                public void run() {
                  Toast.makeText(FormPrincipalActivity.this, "Error desconocido, intente nuevamente.", Toast.LENGTH_SHORT).show();
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
