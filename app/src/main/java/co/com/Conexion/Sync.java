package co.com.Conexion;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import co.com.BusinessObject.ConfigBO;
import co.com.BusinessObject.DataBaseBO;
import co.com.DataObject.Config;
import co.com.DataObject.Coordenada;
import co.com.DataObject.Usuario;
import co.com.panificadoracountry.Const;
import co.com.panificadoracountry.Main;
import co.com.panificadoracountry.Sincronizador;
import co.com.panificadoracountry.Util;
import co.com.preferences.PreferenceNumeroFactura;

public class Sync extends Thread {
  
  private final static String TAG = "Conexion.Sync";
  
  boolean ok; // Indica si la Respuesta del Servidor fue OK o ERROR
  int codeRequest; // Indica el tipo de codeRequest a Sincronizar (Login,
  // DowloadDataBase, ....)
  Sincronizador sincronizador; // Clase que se encarga de procesar la
  // Respuesta del Servidor cuando finaliza el
  // proceso de Sincronizacion.
  
  public int timeout = 2 * 60 * 1000; // 2 Minutos de Timeout para descargar y
  // enviar informacion!
  public int timeoutOne = 60 * 1000; // 1 Minuto de Timeout para Iniciar
  // Sesion con el Servidor y Terminar
  // labores
  
  public String usuario;
  public String clave;
  public String version;
  public String imei;
  public String numeroDoc;
  
  public String nit;
  
  /**
   Guarda el mensaje de la ejecucion de la sincrionizacion Puede ser un
   mensaje de Ok o de Error
   **/
  String mensaje;
  
  public String url;
  public int cantTramas = 15;
  public String termino = "0";
  
  /**
   Guarda la respuesta del servidor, Puede ser un mensaje de Ok o de Error
   **/
  String respuestaServer = "";
  
  public boolean activo = true;
  
  /**
   Guarda la Coordenada que de la Ruta del Vendedor
   **/
  public Coordenada coordenada;
  
  /**
   Contructor de la clase. Sincronizador es una interfaz que deben
   implementar las Actividades que sincronizan datos Cuando se termina la
   sincronizacion, se llama el metodo RespSync de la actividad que invoco el
   Sync con el estatus de la Sincronizacion
   **/
  public Sync(Sincronizador sincronizador, int codeRequest) {
    this.sincronizador = sincronizador;
    this.codeRequest = codeRequest;
  }
  
  public Sync(int codeRequest) {
    this.codeRequest = codeRequest;
  }
  
  public void run() {
    switch (codeRequest) {
      case Const.LOGIN:
        LogIn();
        break;
      case Const.DOWNLOAD_DATA_BASE:
        DownloadDataBase();
        break;
      case Const.ENVIAR_PEDIDO:
      case Const.ENVIAR_PEDIDO_TERMINAR:
        EnviarPedido();
        break;
      case Const.DOWNLOAD_VERSION_APP:
        DownloadVersionApp();
        break;
      case Const.TERMINAR_LABORES:
        //**TerminarLabores();
        EnviarPedido();
        break;
      case Const.VALIDAR_NIT:
        validarNitOnLine();
        break;
      case Const.DOWNLOAD_MESSAGE:
        downloadMessages();
        break;
      case Const.STATUS_SENAL:
        senalStatus();
        break;
      case Const.VERIFICAR_EDICION_PEDIDO:
      case Const.VERIFICAR_ELIMINACION_PEDIDO:
        verificarEdicionPedido();
        break;
      case Const.ENVIAR_COORDENADAS:
        enviarCoordenadas();
        break;
    }
  }
  
  public void LogIn() {
    boolean ok = false;
    respuestaServer = "";
    HttpURLConnection urlConnection = null;
    try {
      // Se hace es ajuste para el nuevo sincronizador
      String strURL = Const.URL_SYNC + "LogIn.aspx?un=" + this.usuario + "&pw=" + this.clave;
      // String strURL = "http://66.33.69.75/AX/sync/syncpw.asp?un=" +
      // this.usuario + "&pw=" + this.clave;
      Log.i("LogIn", "URL Login: " + strURL);
      URL url = new URL(strURL);
      urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.setRequestMethod("GET");
      urlConnection.setRequestProperty("Cache-Control", "no-cache");
      urlConnection.setRequestProperty("Pragma", "no-cache");
      urlConnection.setRequestProperty("Expires", "-1");
      urlConnection.setDoInput(true);
      /**
       * SE DEFINE EL TIEMPO DE ESPERA, MAXIMO ESPERA 1 MINUTO
       **/
      urlConnection.setConnectTimeout(timeoutOne);
      urlConnection.setReadTimeout(timeoutOne);
      BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
      int statusCode = urlConnection.getResponseCode();
      if (statusCode == HttpURLConnection.HTTP_OK) {
        /*****************************************************************
         * Si la Solicitud al Servidor fue Satisfactoria, lee la
         * Respuesta
         *****************************************************************/
        String line = null;
        while ((line = reader.readLine()) != null) {
          respuestaServer += line;
        }
        if (respuestaServer.startsWith("ok")) {
          ok = true;
          mensaje = "Login realizado con exito";
          
        } else {
          mensaje = "No se pudo Iniciar Sesion.\n" + respuestaServer;
        }
        
      } else if (statusCode == -1) {
        mensaje = "No se pudo Iniciar Sesion. Pagina No Encontrada: LogIn.aspx";
        
      } else {
        mensaje = MensajeHttpError(statusCode, "LogIn.aspx");
      }
      
    } catch (Exception e) {
      String motivo = e.getMessage();
      Log.e(TAG, "logIn -> " + motivo, e);
      if (motivo != null && motivo.startsWith("http://")) motivo = "Pagina no Encontrada: LogIn.aspx";
      mensaje = "No se pudo Iniciar Sesion";
      if (motivo != null) mensaje += "\n\nMotivo: " + motivo;
      
    } finally {
      if (urlConnection != null) urlConnection.disconnect();
    }
    sincronizador.RespSync(ok, respuestaServer, mensaje, codeRequest);
  }
  
  public void DownloadDataBase() {
    boolean ok = false;
    InputStream inputStream = null;
    FileOutputStream fileOutput = null;
    HttpURLConnection urlConnection = null;
    try {
      /************************************
       * Carga la Configuracion del Usuario.
       ************************************/
      Config config = ConfigBO.ObtenerConfigUsuario();
      if (config != null) {
        Locale locale = Locale.getDefault();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd@HH_mm_ss", locale);
        String fechaMovil = dateFormat.format(new Date());
        String tipo = "";
        if (Const.tipoAplicacion == 0 || Const.tipoAplicacion == Const.PREVENTA) {
          tipo = Const.PREVENTA + "";
        } else {
          tipo = Const.AUTOVENTA + "";
        }
        // String urlDataBase = Const.URL_SYNC + "CrearDB.aspx?un=" +
        // config.usuario + "&fe=" + fechaMovil + "&lo=" + locale +
        // "&vr=" + version + "&i=" + imei;
        // String urlDataBase = Const.URL_SYNC + "CrearDB.aspx?un=" +
        // config.usuario + "&fe=" + fechaMovil + "&lo=" + locale +
        // "&vr=" + version + "&i=" + imei + "&sr=" + "1" + "&aux=" +
        // "1" + "&na=" + "1" + "&tipo=" + tipo;
        String urlDataBase = Const.URL_SYNC + "CrearDB.aspx?un=" + config.usuario + "&fe=" + fechaMovil + "&lo=" + locale + "&vr=" + version + "&i=" + imei + "&cv=" + tipo;
        Log.i(TAG, "URL DB = " + urlDataBase);
        URL url = new URL(urlDataBase);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Cache-Control", "no-cache");
        urlConnection.setRequestProperty("Pragma", "no-cache");
        urlConnection.setRequestProperty("Expires", "-1");
        urlConnection.setDoInput(true);
        /**
         * SE DEFINE EL TIEMPO DE ESPERA, MAXIMO ESPERA 2 MINUTOS
         **/
        urlConnection.setConnectTimeout(timeout);
        urlConnection.setReadTimeout(timeout);
        urlConnection.connect();
        inputStream = urlConnection.getInputStream();
        String contentDisposition = urlConnection.getHeaderField("Content-Disposition");
        if (contentDisposition != null) { // Viene Archivo Adjunto
          /**
           * Se obtiene la ruta del SD Card, para guardar la Base de
           * Datos. Y se crea el Archivo de la BD
           **/
          String fileName = "Temporal.zip";
          File file = new File(Util.DirApp(), fileName);
          if (file.exists()) file.delete();
          if (file.createNewFile()) {
            fileOutput = new FileOutputStream(file);
            long downloadedSize = 0;
            int bufferLength = 0;
            byte[] buffer = new byte[1024];
            /**
             * SE LEE LA INFORMACION DEL BUFFER Y SE ESCRIBE EL
             * CONTENIDO EN EL ARCHIVO DE SALIDA
             **/
            while ((bufferLength = inputStream.read(buffer)) > 0) {
              fileOutput.write(buffer, 0, bufferLength);
              downloadedSize += bufferLength;
            }
            fileOutput.flush();
            fileOutput.close();
            inputStream.close();
            long content_length = Util.ToLong(urlConnection.getHeaderField("content-length"));
            if (content_length == 0) {
              mensaje = "No hay conexion, por favor intente de nuevo";
              
            } else if (content_length != downloadedSize) { // La
              // longitud
              // de
              // descarga
              // no es
              // igual al
              // Content
              // Length
              // del
              // Archivo
              mensaje = "No se pudo descargar la base de datos, por favor intente de nuevo";
              
            } else {
              Descomprimir(file);
              ok = true;
              mensaje = "Descargo correctamente la Base de Datos";
              /*Quitar el conteo local de numero de factura para sincronizar con el servidor.*/
              PreferenceNumeroFactura.vaciarPreferences((Context) this.sincronizador);
            }
            
          } else {
            mensaje = "No se pudo crear el archivo de la Base de Datos";
          }
          
        } else { // No hay archivo adjunto, se procesa el Mensaje de
          // respuesta del Servidor
          BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
          String line = null;
          while ((line = reader.readLine()) != null) {
            respuestaServer += line;
          }
          if (respuestaServer.equals("")) mensaje = "No se pudo descargar la base de datos, por favor intente de nuevo.";
          else mensaje = respuestaServer;
        }
        
      } else {
        Log.i("DownloadDataBase", "Falta establecer la configuracion del Usuario");
        mensaje = "Por favor, primero ingrese la configuracion del usuario";
      }
      
    } catch (Exception e) {
      String motivo = e.getMessage();
      Log.e(TAG, "downloadDataBase -> " + motivo, e);
      if (motivo != null && motivo.startsWith("http://")) motivo = "Pagina no Encontrada: CrearDB.aspx";
      mensaje = "No se pudo descargar la Base de Datos";
      if (motivo != null) mensaje += "\n\nMotivo: " + motivo;
      
    } finally {
      try {
        if (fileOutput != null) fileOutput.close();
        if (inputStream != null) inputStream.close();
        
      } catch (IOException e) {
      }
      if (urlConnection != null) urlConnection.disconnect();
    }
    sincronizador.RespSync(ok, mensaje, mensaje, codeRequest);
  }
  
  public void TerminarLabores() {
    /*
     * ok = false; mensaje = ""; HttpURLConnection urlConnection = null;
     *
     *
     * Config config = ConfigBO.ObtenerConfigUsuario();
     *
     * if (config != null) {
     *
     * try {
     *
     * Locale locale = Locale.getDefault(); SimpleDateFormat dateFormat =
     * new SimpleDateFormat("yyyy-MM-dd@HH_mm_ss", locale); String
     * fechaMovil = dateFormat.format(new Date());
     *
     * String strURL = Const.URL_SYNC + "TerminarLabores.aspx?un=" +
     * config.usuario + "&fe=" + fechaMovil + "&i=" + imei; Log.i(TAG,
     * "URL TerminarLabores = " + strURL);
     *
     * URL url = new URL(strURL); urlConnection = (HttpURLConnection)
     * url.openConnection();
     *
     *
     * urlConnection.setRequestMethod("GET");
     * urlConnection.setRequestProperty("Cache-Control", "no-cache");
     * urlConnection.setRequestProperty("Pragma", "no-cache");
     * urlConnection.setRequestProperty("Expires", "-1");
     * urlConnection.setDoInput(true);
     *
     *
     * urlConnection.setConnectTimeout(timeoutOne);
     * urlConnection.setReadTimeout(timeoutOne);
     *
     *
     *
     * BufferedReader reader = new BufferedReader(new
     * InputStreamReader(urlConnection.getInputStream())); int statusCode =
     * urlConnection.getResponseCode();
     *
     * if (statusCode == HttpURLConnection.HTTP_OK) {
     *
     *
     * String line = null; while ((line = reader.readLine()) != null) {
     *
     * respuestaServer += line; }
     *
     * if (respuestaServer.startsWith("ok")) {
     *
     * ok = true; DataBaseBO.ActulizarTerminoLabores(); mensaje =
     * "Terminar Labores Satisfactorio";
     *
     * } else {
     *
     * mensaje = respuestaServer; }
     *
     * } else if (statusCode == -1) {
     *
     * mensaje =
     * "No se pudo Terminar Labores. Pagina No Encontrada: TerminarLabores.aspx"
     * ;
     *
     * } else {
     *
     * mensaje = MensajeHttpError(statusCode, "TerminarLabores.aspx"); }
     *
     * } catch (Exception e) {
     *
     * String motivo = e.getMessage(); Log.e(TAG, "terminarLabores -> " +
     * motivo, e);
     *
     * if (motivo != null && motivo.startsWith("http://")) motivo =
     * "Pagina no Encontrada: TerminarLabores.aspx";
     *
     * mensaje = "No se pudo Terminar Labores.";
     *
     * if (motivo != null) mensaje += "\n\nMotivo: " + motivo;
     *
     * }
     *
     * } else {
     *
     * Log.i(TAG,
     * "TerminarLabores: Falta establecer la configuracion del Usuario");
     * mensaje = "Por favor, primero ingrese la configuracion del usuario";
     * }
     *
     * sincronizador.RespSync(ok, respuestaServer, mensaje, codeRequest);
     */
  }
  
  public void Descomprimir(File fileZip) {
    try {
      if (fileZip.exists()) {
        String nameFile = fileZip.getName().replace(".zip", "");
        File fileZipAux = new File(Util.DirApp(), nameFile);
        if (!fileZipAux.exists()) fileZipAux.delete();
        FileInputStream fin = new FileInputStream(fileZip);
        ZipInputStream zin = new ZipInputStream(fin);
        ZipEntry ze = null;
        while ((ze = zin.getNextEntry()) != null) {
          Log.v("Descomprimir", "Unzipping " + ze.getName());
          if (ze.isDirectory()) {
            dirChecker(ze.getName());
            
          } else {
            String pathFile = Util.DirApp() + "/" + ze.getName();
            File file = new File(pathFile);
            FileOutputStream fout = new FileOutputStream(file);
            // long bytes = 0;
            int bufferLength = 0;
            byte[] buffer = new byte[1024];
            while ((bufferLength = zin.read(buffer)) > 0) {
              fout.write(buffer, 0, bufferLength);
              // bytes += bufferLength;
            }
            zin.closeEntry();
            fout.flush();
            fout.close();
          }
        }
        zin.close();
      }
      
    } catch (Exception e) {
      Log.e("Descomprimir", e.getMessage(), e);
    }
  }
  
  public void EnviarPedido() {
    ok = false;
    String msg = "";
    if (ComprimirArchivo()) {
      File zipPedido = new File(Util.DirApp(), "Temp.zip");
      if (!zipPedido.exists()) {
        Log.i("EnviarPedido", "El archivo Temp.zip no Existe");
        sincronizador.RespSync(ok, "", "El archivo Temp.zip no Existe", codeRequest);
        return;
      }
      DataOutputStream dos = null;
      HttpURLConnection conexion = null;
      BufferedReader bufferedReader = null;
      FileInputStream fileInputStream = null;
      byte[] buffer;
      int maxBufferSize = 1 * 1024 * 1024;
      int bytesRead, bytesAvailable, bufferSize;
      Usuario usuario = DataBaseBO.CargarUsuario();
      if (usuario != null) {
        int consecutivo = DataBaseBO.ObtenerConsecutivoVend();
        // String urlUpLoad = Const.URL_SYNC +
        // "RegistrarPedido.aspx?un=" + config.usuario +
        // "&termino=0&ext=zip&fechaLabor=" + Main.usuario.fechaLabores
        // + "&co=" + consecutivo;
        String urlUpLoad = Const.URL_SYNC + "RegistrarInformacion.aspx?un=" + usuario.codigoVendedor + "&termino=0&ext=zip&fechaLabor=" + Main.usuario.fechaLabores + "&co=" + consecutivo + "&fa=" + "1" + "&sr=" + "1" + "&na=" + "1";
        Log.i("EnviarPedido", "URL Enviar Info = " + urlUpLoad);
        try {
          URL url = new URL(urlUpLoad);
          conexion = (HttpURLConnection) url.openConnection();
          conexion.setDoInput(true); // Permite Entradas
          conexion.setDoOutput(true); // Permite Salidas
          conexion.setUseCaches(false); // No usar cache
          conexion.setRequestMethod("POST");
          conexion.setRequestProperty("Cache-Control", "no-cache");
          conexion.setRequestProperty("Pragma", "no-cache");
          conexion.setRequestProperty("Expires", "-1");
          conexion.setRequestProperty("Connection", "Keep-Alive");
          conexion.setRequestProperty("Content-Type", "multipart/form-data; boundary=---------------------------4664151417711");
          conexion.setConnectTimeout(timeout);
          conexion.setReadTimeout(timeout);
          fileInputStream = new FileInputStream(zipPedido);
          bytesAvailable = fileInputStream.available();
          bufferSize = Math.min(bytesAvailable, maxBufferSize);
          buffer = new byte[bufferSize];
          dos = new DataOutputStream(conexion.getOutputStream());
          bytesRead = fileInputStream.read(buffer, 0, bufferSize);
          while (bytesRead > 0) {
            dos.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
          }
          dos.flush();
          Log.i("EnviarPedido", "Enviando informacion del Archivo");
          bufferedReader = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
          String line;
          respuestaServer = "";
          while ((line = bufferedReader.readLine()) != null) {
            respuestaServer += line;
          }
          if (respuestaServer.toUpperCase().startsWith("OK") || respuestaServer.toUpperCase().indexOf("LISTO") != -1) {
            ok = true;
            if (activo) {
              DataBaseBO.ActualizarSyncPedidos();
              DataBaseBO.BorrarInfoTemp();
              
            }
            
          } else {
            if (respuestaServer.equals("")) msg = "Sin respuesta del servidor";
            else msg = respuestaServer;
          }
          Log.i("EnviarPedido", "respuesta: " + respuestaServer);
          
        } catch (Exception ex) {
          String motivo = ex.getMessage();
          if (motivo != null && motivo.startsWith("http://")) motivo = "Pagina no Encontrada: Registrar Informacion";
          msg = "No se pudo Registrar Informacion";
          if (motivo != null) msg += "\n\nMotivo: " + motivo;
          Log.e(TAG, "enviarPedido -> " + msg, ex);
          
        } finally {
          try {
            if (bufferedReader != null) bufferedReader.close();
            if (fileInputStream != null) fileInputStream.close();
            if (dos != null) dos.close();
            if (conexion != null) conexion.disconnect();
            
          } catch (IOException e) {
            Log.e("FileUpLoad", "Error cerrando conexion: " + e.getMessage(), e);
          }
        }
        
      } else {
        Log.i("EnviarPedido", "Falta establecer la configuracion del Usuario");
        mensaje = "Por favor, primero ingrese la configuracion del usuario";
      }
      
    } else {
      msg = "Error comprimiendo la Base de datos Pedido";
      Log.e("FileUpLoad", msg);
    }
    if (respuestaServer.equals("")) respuestaServer = "error, Sin respuesta del servidor";
    // sincronizador.RespSync(ok, respuestaServer, msg, codeRequest);
    if (activo) {
      sincronizador.RespSync(ok, respuestaServer, msg, codeRequest);
    } else {
      Log.w(TAG, "Sync Cancelado por Timer");
    }
    
  }
  
  public void DownloadVersionApp() {
    boolean ok = false;
    InputStream inputStream = null;
    FileOutputStream fileOutput = null;
    try {
      URL url = new URL(Const.URL_DOWNLOAD_NEW_VERSION);
      HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
      Log.i(TAG, "downloadVersionApp -> URL App = " + Const.URL_DOWNLOAD_NEW_VERSION);
      urlConnection.setRequestMethod("GET");
      urlConnection.setRequestProperty("Cache-Control", "no-cache");
      urlConnection.setRequestProperty("Pragma", "no-cache");
      urlConnection.setRequestProperty("Expires", "-1");
      urlConnection.setDoInput(true);
      /**
       * SE DEFINE EL TIEMPO DE ESPERA, MAXIMO ESPERA 2 MINUTOS
       **/
      urlConnection.setConnectTimeout(timeout);
      urlConnection.setReadTimeout(timeout);
      urlConnection.connect();
      inputStream = urlConnection.getInputStream();
      File file = new File(Util.DirApp(), Const.fileNameApk);
      if (file.exists()) file.delete();
      if (file.createNewFile()) {
        fileOutput = new FileOutputStream(file);
        long downloadedSize = 0;
        int bufferLength = 0;
        byte[] buffer = new byte[1024];
        /**
         * SE LEE LA INFORMACION DEL BUFFER Y SE ESCRIBE EL CONTENIDO EN
         * EL ARCHIVO DE SALIDA
         **/
        while ((bufferLength = inputStream.read(buffer)) > 0) {
          fileOutput.write(buffer, 0, bufferLength);
          downloadedSize += bufferLength;
        }
        fileOutput.flush();
        fileOutput.close();
        inputStream.close();
        long content_length = Util.ToLong(urlConnection.getHeaderField("content-length"));
        if (content_length == 0) {
          ok = false;
          mensaje = "Error de conexion, por favor intente de nuevo";
          
        } else if (content_length != downloadedSize) { // La longitud de
          // descarga no es
          // igual al Content
          // Length del
          // Archivo
          ok = false;
          mensaje = "Error descargando la nueva version, por favor intente de nuevo";
          
        } else {
          ok = true;
          mensaje = "Descargo correctamente la Nueva Version";
        }
        
      } else {
        mensaje = "Error Creando el Archivo de la Nueva Version";
        ok = false;
      }
      
    } catch (Exception e) {
      mensaje = "Error Descargando la Nueva version de la Aplicacion\n";
      mensaje += "Detalle Error: " + e.getMessage();
      Log.e("Sync DownloadVersionApp", e.getMessage(), e);
      ok = false;
      
    } finally {
      try {
        if (fileOutput != null) fileOutput.close();
        if (inputStream != null) inputStream.close();
        
      } catch (IOException e) {
      }
    }
    sincronizador.RespSync(ok, mensaje, mensaje, codeRequest);
  }
  
  private void dirChecker(String dir) {
    File f = new File(Util.DirApp().getPath() + "/" + dir);
    if (!f.isDirectory()) {
      f.mkdirs();
    }
  }
  
  public boolean ComprimirArchivo() {
    File zipPedido = new File(Util.DirApp(), "Temp.zip");
    if (zipPedido.exists()) zipPedido.delete();
    FileOutputStream out = null;
    GZIPOutputStream gZipOut = null;
    FileInputStream fileInputStream = null;
    try {
      File dbFile = new File(Util.DirApp(), "Temp.db");
      if (dbFile.exists()) {
        fileInputStream = new FileInputStream(dbFile);
        int lenFile = fileInputStream.available();
        byte[] buffer = new byte[fileInputStream.available()];
        int byteRead = fileInputStream.read(buffer);
        if (byteRead == lenFile) {
          out = new FileOutputStream(zipPedido);
          gZipOut = new GZIPOutputStream(out);
          gZipOut.write(buffer);
          return true;
        }
        if (zipPedido.exists()) zipPedido.delete();
      }
      return false;
      
    } catch (Exception e) {
      if (zipPedido.exists()) zipPedido.delete();
      Log.e("ComprimirArchivo", e.getMessage(), e);
      return false;
      
    } finally {
      try {
        if (gZipOut != null) gZipOut.close();
        if (out != null) out.close();
        if (fileInputStream != null) fileInputStream.close();
        
      } catch (Exception e) {
        Log.e("ComprimirArchivo", e.getMessage(), e);
      }
    }
  }
  
  public String MensajeHttpError(int statusCode, String pagina) {
    switch (statusCode) {
      case HttpURLConnection.HTTP_NOT_IMPLEMENTED:
        return "Conexion no Disponible: No Implementado.";
      case HttpURLConnection.HTTP_VERSION:
        return "Conexion no Disponible: Version No Soportada.";
      case HttpURLConnection.HTTP_INTERNAL_ERROR:
        return "Conexion no Disponible: Error Interno.";
      case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
        return "Conexion no Disponible: Tiempo de Conexion Excedido.";
      case HttpURLConnection.HTTP_BAD_GATEWAY:
        return "Conexion no Disponible: Mala Conexion.";
      case HttpURLConnection.HTTP_NOT_FOUND:
        return "Pagina No Encontrada: " + pagina + ".";
      default:
        return "Conexion no Disponible. Http Error Code: " + statusCode + ".";
    }
  }
  
  public void validarNitOnLine() {
    boolean ok = false;
    respuestaServer = "";
    HttpURLConnection urlConnection = null;
    try {
      Locale locale = Locale.getDefault();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd@HH_mm_ss", locale);
      String fechaMovil = dateFormat.format(new Date());
      // Se hace es ajuste para el nuevo sincronizador
      String strURL = Const.URL_SYNC + "VerificarNit.aspx?nit=" + this.nit + "&fe=" + fechaMovil;
      Log.i("VerificarNit", "URL VerificarNit: " + strURL);
      URL url = new URL(strURL);
      urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.setRequestMethod("GET");
      urlConnection.setRequestProperty("Cache-Control", "no-cache");
      urlConnection.setRequestProperty("Pragma", "no-cache");
      urlConnection.setRequestProperty("Expires", "-1");
      urlConnection.setDoInput(true);
      /**
       * SE DEFINE EL TIEMPO DE ESPERA, MAXIMO ESPERA 1 MINUTO
       **/
      urlConnection.setConnectTimeout(timeoutOne);
      urlConnection.setReadTimeout(timeoutOne);
      BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
      int statusCode = urlConnection.getResponseCode();
      if (statusCode == HttpURLConnection.HTTP_OK) {
        /*****************************************************************
         * Si la Solicitud al Servidor fue Satisfactoria, lee la
         * Respuesta
         *****************************************************************/
        String line = null;
        while ((line = reader.readLine()) != null) {
          respuestaServer += line;
        }
        
      } else if (statusCode == -1) {
        mensaje = "Pagina No Encontrada: VerificarNit.aspx";
        
      } else {
        mensaje = MensajeHttpError(statusCode, "VerificarNit.aspx");
      }
      
    } catch (Exception e) {
      String motivo = e.getMessage();
      Log.e(TAG, "logIn -> " + motivo, e);
      if (motivo != null && motivo.startsWith("http://")) motivo = "Pagina no Encontrada: VerificarNit.aspx";
      mensaje = "No se pudo Validar Nit";
      if (motivo != null) mensaje += "\n\nMotivo: " + motivo;
      
    } finally {
      if (urlConnection != null) urlConnection.disconnect();
    }
    sincronizador.RespSync(ok, respuestaServer, mensaje, codeRequest);
  }
  
  public void downloadMessages() {
    boolean ok = false;
    respuestaServer = "";
    HttpURLConnection urlConnection = null;
    try {
      // Se hace es ajuste para el nuevo sincronizador
      String strURL = Const.URL_SYNC + "/mensajes/mensajes.asp?usuario=" + this.usuario;
      Log.i("downloadMessages", "URL downloadMessages: " + strURL);
      URL url = new URL(strURL);
      urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.setRequestMethod("GET");
      urlConnection.setRequestProperty("Cache-Control", "no-cache");
      urlConnection.setRequestProperty("Pragma", "no-cache");
      urlConnection.setRequestProperty("Expires", "-1");
      urlConnection.setDoInput(true);
      /**
       * SE DEFINE EL TIEMPO DE ESPERA, MAXIMO ESPERA 1 MINUTO
       **/
      urlConnection.setConnectTimeout(timeoutOne);
      urlConnection.setReadTimeout(timeoutOne);
      BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
      int statusCode = urlConnection.getResponseCode();
      if (statusCode == HttpURLConnection.HTTP_OK) {
        /*****************************************************************
         * Si la Solicitud al Servidor fue Satisfactoria, lee la
         * Respuesta
         *****************************************************************/
        String line = null;
        while ((line = reader.readLine()) != null) {
          respuestaServer += line;
        }
        
      } else if (statusCode == -1) {
        mensaje = "No se pudo descargar mensajes Pagina No Encontrada: mensajes.asp";
        
      } else {
        mensaje = MensajeHttpError(statusCode, "mensajes.aspx");
      }
      
    } catch (Exception e) {
      String motivo = e.getMessage();
      Log.e(TAG, "logIn -> " + motivo, e);
      if (motivo != null && motivo.startsWith("http://")) motivo = "Pagina no Encontrada: mensajes.asp";
      mensaje = "No se pudo descargar mensajes";
      if (motivo != null) mensaje += "\n\nMotivo: " + motivo;
      
    } finally {
      if (urlConnection != null) urlConnection.disconnect();
    }
    sincronizador.RespSync(ok, respuestaServer, mensaje, codeRequest);
  }
  
  public void senalStatus() {
    boolean ok = false;
    respuestaServer = "";
    HttpURLConnection urlConnection = null;
    try {
      // Se hace es ajuste para el nuevo sincronizador
      String strURL = Const.URL_SYNC + "/TestConectividad.asp";
      // String strURL =
      // "http://64.239.115.11/sync/wit/testconectividad.asp";
      Log.i("senalStatus", "URL senalStatus: " + strURL);
      URL url = new URL(strURL);
      urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.setRequestMethod("GET");
      urlConnection.setRequestProperty("Cache-Control", "no-cache");
      urlConnection.setRequestProperty("Pragma", "no-cache");
      urlConnection.setRequestProperty("Expires", "-1");
      urlConnection.setDoInput(true);
      /**
       * SE DEFINE EL TIEMPO DE ESPERA, MAXIMO ESPERA 1 MINUTO
       **/
      urlConnection.setConnectTimeout(timeoutOne);
      urlConnection.setReadTimeout(timeoutOne);
      BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
      int statusCode = urlConnection.getResponseCode();
      if (statusCode == HttpURLConnection.HTTP_OK) {
        /*****************************************************************
         * Si la Solicitud al Servidor fue Satisfactoria, lee la
         * Respuesta
         *****************************************************************/
        String line = null;
        while ((line = reader.readLine()) != null) {
          respuestaServer += line;
        }
        
      } else if (statusCode == -1) {
        mensaje = "No se pudo verificar conectividad";
        
      } else {
        mensaje = MensajeHttpError(statusCode, "TestConectividad.asp");
      }
      
    } catch (Exception e) {
      String motivo = e.getMessage();
      Log.e(TAG, "logIn -> " + motivo, e);
      if (motivo != null && motivo.startsWith("http://")) motivo = "Pagina no Encontrada: TestConectividad.asp";
      mensaje = "No se pudo verificar conectividad";
      if (motivo != null) mensaje += "\n\nMotivo: " + motivo;
      
    } finally {
      if (urlConnection != null) urlConnection.disconnect();
    }
    sincronizador.RespSync(ok, respuestaServer, mensaje, codeRequest);
  }
  
  public void verificarEdicionPedido() {
    boolean ok = false;
    respuestaServer = "";
    HttpURLConnection urlConnection = null;
    try {
      // Se hace es ajuste para el nuevo sincronizador
      String strURL = Const.URL_SYNC + "verificarPedido.aspx?documento=" + this.numeroDoc + "&tipo=0";
      Log.i("LogIn", "URL Login: " + strURL);
      URL url = new URL(strURL);
      urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.setRequestMethod("GET");
      urlConnection.setRequestProperty("Cache-Control", "no-cache");
      urlConnection.setRequestProperty("Pragma", "no-cache");
      urlConnection.setRequestProperty("Expires", "-1");
      urlConnection.setDoInput(true);
      /**
       * SE DEFINE EL TIEMPO DE ESPERA, MAXIMO ESPERA 1 MINUTO
       **/
      urlConnection.setConnectTimeout(timeoutOne);
      urlConnection.setReadTimeout(timeoutOne);
      BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
      int statusCode = urlConnection.getResponseCode();
      if (statusCode == HttpURLConnection.HTTP_OK) {
        /*****************************************************************
         * Si la Solicitud al Servidor fue Satisfactoria, lee la
         * Respuesta
         *****************************************************************/
        String line = null;
        while ((line = reader.readLine()) != null) {
          respuestaServer += line;
        }
        if (respuestaServer.toUpperCase().indexOf("NORMANDY_OK") != -1) {
          ok = true;
          mensaje = "Se puede editar pedido";
          
        } else {
          mensaje = "No se puede Editar el pedido.\n" + respuestaServer;
        }
        
      } else if (statusCode == -1) {
        mensaje = "No se pudo Iniciar Sesion. Pagina No Encontrada: VerificarPedido.aspx";
        
      } else {
        mensaje = MensajeHttpError(statusCode, "VerificarPedido.aspx");
      }
      
    } catch (Exception e) {
      String motivo = e.getMessage();
      Log.e(TAG, "logIn -> " + motivo, e);
      if (motivo != null && motivo.startsWith("http://")) motivo = "Pagina no Encontrada: VerificarPedido.aspx";
      mensaje = "No se pudo Comprobar edicion del pedido";
      if (motivo != null) mensaje += "\n\nMotivo: " + motivo;
      
    } finally {
      if (urlConnection != null) urlConnection.disconnect();
    }
    sincronizador.RespSync(ok, respuestaServer, mensaje, codeRequest);
  }
  
  public void enviarCoordenadas() {
    boolean ok = false;
    respuestaServer = "";
    mensaje = "";
    if (coordenada != null) {
      HttpURLConnection urlConnection = null;
      try {
        String separadorRows = ">>";
        String separadorReg = "@@";
        Locale locale = Locale.getDefault();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd@HH_mm_ss", locale);
        String fechaMovil = dateFormat.format(new Date());
        //trana [0: CodigoCliente], [1:Latitud], [2: Longitud], [3:fecha], [4:NumDoc]
        String tramaCoordenadas = coordenada.codigoCliente + separadorReg + coordenada.latitud + separadorReg + coordenada.longitud + separadorReg + coordenada.fechaCoordenada + separadorReg + coordenada.estado + separadorReg + coordenada.imei + separadorReg + coordenada.tipoCaptura + separadorReg + coordenada.id;
        String coordenadas = Coordenada.obtenerCoordenadas(separadorRows, separadorReg);
        if (coordenadas != null && coordenadas.length() > 0) {
          tramaCoordenadas += separadorRows + coordenadas;
        }
        Log.i(TAG, "TRAMA DE CORDENADAS VENDEDOR--> " + tramaCoordenadas);
        String strUrl = Const.URL_SYNC + "Coordenadas.aspx?un=" + coordenada.codigoVendedor + "&fe=" + fechaMovil;
        //String strUrl = Const.URL_SYNC + "Coordenadas.aspx?un=" + coordenada.codigoVendedor + "&fe=" + fechaMovil;//"&coordenadas=" + tramaCoordenadas +
        Log.i(TAG, "enviarCoordenadas-> URL coordenadas: " + strUrl);
   
            /*List<BasicNameValuePair> paramsPost = new ArrayList<BasicNameValuePair>(1);
            paramsPost.add(new BasicNameValuePair("coordenadas", tramaCoordenadas));*/
        String paramsPost = "coordenadas=" + tramaCoordenadas;
        // byte[] postData = paramsPost.getBytes(StandardCharsets.UTF_8);
        byte[] postData = paramsPost.getBytes("UTF-8");
        URL url = new URL(strUrl);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Cache-Control", "no-cache");
        urlConnection.setRequestProperty("Pragma", "no-cache");
        urlConnection.setRequestProperty("Expires", "-1");
        urlConnection.setDoInput(true);
        urlConnection.getOutputStream().write(postData);
        /**
         * SE DEFINE EL TIEMPO DE ESPERA, MAXIMO ESPERA 1 MINUTO
         **/
        urlConnection.setConnectTimeout(timeoutOne);// Expirar al 1 minuto si la conexión no se establece
        urlConnection.setReadTimeout(timeoutOne);// Esperar solo 1 minuto  para que finalice la lectura
        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        int statusCode = urlConnection.getResponseCode();
        if (statusCode == HttpURLConnection.HTTP_OK) {
          /*****************************************************************
           * Si la Solicitud al Servidor fue Satisfactoria, lee la
           * Respuesta
           *****************************************************************/
          String line = null;
          while ((line = reader.readLine()) != null) {
            respuestaServer += line;
          }
          if (respuestaServer.equals("ok")) {
            ok = true;
          } else if (respuestaServer.equals("")) {
            mensaje = "No se pudo registrar la coordenada";
          } else if (respuestaServer.startsWith("<") || respuestaServer.contains("<")) {
            mensaje = "No se pudo registrar la Coordenada\n\nPor favor verifique su conexion a Internet!";
          } else {
            mensaje = respuestaServer;
          }
          Log.i(TAG, "enviarCoordenadas -> respuestaServer = " + respuestaServer);
        } else if (statusCode == -1) {
          mensaje = "No se pudo registrar la coordenada. Página No Encontrada: Coordenadas.aspx";
        } else {
          mensaje = MensajeHttpError(statusCode, "Coordenadas.aspx");
        }
      } catch (Exception e) {
        String line = e.getMessage();
        String motivo = e.getMessage();
        mensaje = "No se pudo Registrar la Coordenada.";
        Log.e(TAG, "enviarCoordenadas -> " + mensaje + " " + motivo, e);
        if (motivo != null && motivo.startsWith("http://")) {
          motivo = "Pagina no Encontrada: Coordenadas.aspx";
        }
        if (motivo != null) {
          mensaje += "\n\nMotivo: " + motivo;
        }
      } finally {
        if (urlConnection != null) urlConnection.disconnect();
      }
    } else {
      Log.e(TAG, "enviarCoordenadas -> No se pudo leer la coordenada");
      mensaje = "No se pudo registrar la coordenada";
    }
    if (ok) {
      Coordenada.borrarCoordenadas();
    } else {
      Coordenada.guardarCoordenada(coordenada);
    }
    sincronizador.RespSync(ok, respuestaServer, mensaje, codeRequest);
  }
	
	
	
	/*
	* private void enviarCoordenadaPost(){
		
		mensaje = "";
		respuestaServer = "";
		
		boolean ok = false;
		
		if (coordenada != null && coordenada.codigoVendedor != null) {
			
			InputStream inputStream = null;
			FileOutputStream fileOutput = null;
			HttpURLConnection urlConnection = null;
			
			try {
				
				String fechaMovil = Util.FechaActual("yyyy-MM-dd@HH_mm_ss");
				String url = Const.URL_SYNC + "Coordenadas.aspx?un=" + coordenada.codigoVendedor + "&fe=" + fechaMovil;
				Log.i(TAG, "enviarCoordenadaPost -> url = " + url);
				
				int timeoutConnection = 10 * 1000; //10 Segundos para establecer la conexion con la Pagina.
				int timeoutSocket = 40 * 1000; //40 Segundos de Timeout para obtener la Respuesta
				
				HttpParams httpParameters = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
				
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
	 
				String separadorRows = ">>";
	            String separadorReg = "@@";
	            
	            String tramaCoordenadas = coordenada.codigoCliente + separadorReg + coordenada.latitud + separadorReg +
	            		coordenada.longitud  + separadorReg + coordenada.fechaCoordenada + separadorReg + coordenada.estado  +
	            		separadorReg + coordenada.imei +  separadorReg + coordenada.tipoCaptura + separadorReg + coordenada.id ;
	            
	            Log.i(TAG, "tramaCoordenadas ->  = " + tramaCoordenadas);
	            
	            String coordenadas = Coordenada.obtenerCoordenadas(separadorRows, separadorReg);
	            
	            if (coordenadas != null && coordenadas.length() > 0) {
	            	tramaCoordenadas += separadorRows + coordenadas;
	            }
	            
	            List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(1);
	            nameValuePairs.add(new BasicNameValuePair("coordenadas", tramaCoordenadas));
	 
	            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	            HttpResponse response = httpClient.execute(httpPost);
	            HttpEntity entity = response.getEntity();
	            
	            StatusLine status = response.getStatusLine();
	            int statusCode = status.getStatusCode();
	            
	            if (statusCode == HttpURLConnection.HTTP_OK) {
	            	
	            	inputStream = entity.getContent();
		            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

					String line = null;
					while ((line = reader.readLine()) != null) {
						respuestaServer += line;
					}
					
					if (respuestaServer.equals("ok")) {
						
						ok = true;
						
					} else {
						
						if (respuestaServer.equals("")) {
							
							mensaje = "No se pudo registrar la coordenada";
							
						} else {
							
							if (respuestaServer.startsWith("<") || respuestaServer.contains("<")) {
								mensaje = "No se pudo registrar la Coordenada\n\nPor favor verifique su conexion a Internet!";
							} else {
								mensaje = respuestaServer;
							}
						}
					}
	            
	            } else if (statusCode == -1) {
					
					mensaje = "No se pudo Registrar la Coordenada. Pagina No Encontrada: Coordenadas.aspx";
					
				} else {
					
					mensaje = MensajeHttpError(statusCode, "Coordenadas.aspx");
				}
				
			} catch (Exception e) {
				
				String motivo = e.getMessage();
				Log.e(TAG, "enviarCoordenadaPost -> " + motivo, e);
				
				if (motivo != null && motivo.startsWith("http://")) {
					motivo = "Pagina no Encontrada: Coordenadas.aspx";
				}
				
				mensaje = "No se pudo Registrar la Coordenada.";
				
				if (motivo != null) {
					mensaje += "\n\nMotivo: " + motivo;
				}
				
			} finally {
				
				try {
					
					if (fileOutput != null) {
						fileOutput.close();
					}
					
					if (inputStream != null) {
						inputStream.close();
					}
					
					if (urlConnection != null) {
						urlConnection.disconnect();
					}
					
				} catch (Exception e) {
					
					Log.e(TAG, "enviarCoordenadaPost -> Exception en el finally", e);
				}
			}
			
			Log.i(TAG, "enviarCoordenadaPost -> respuestaServer = " + respuestaServer);
			
		} else {
			
			Log.i(TAG, "enviarCoordenadaPost -> No se pudo leer la Coordenada");
			mensaje = "No se pudo Registrar la Coordenada";
		}
		
		if (ok) {
			Coordenada.borrarCoordenadas();
		} else {
			Coordenada.guardarCoordenada(coordenada);
		}
		
		sincronizador.RespSync(ok, mensaje, mensaje, codeRequest);
	}
	*/
  
  public void GetUrl(String tabla) {
    if (tabla.equals("TerminarLabores")) {
      url = Const.URL_SYNC + "TerminarLabores.aspx?un=" + Main.usuario.codigoVendedor.trim();
      
    } else if (tabla.equals("Coordenadas")) {
      url = Const.URL_SYNC_TRAMAS + "syncCoord.asp?usuario=" + Main.usuario.codigoVendedor.trim();
    }
  }
}
