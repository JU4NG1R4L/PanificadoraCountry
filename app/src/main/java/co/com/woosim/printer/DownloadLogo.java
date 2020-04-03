/**
 * 
 */
package co.com.woosim.printer;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import co.com.panificadoracountry.Const;
import co.com.panificadoracountry.Util;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


/**
 * 
 * Clase que permite la descarga en segundo plano del logo que se imprime en las
 * tirillas.
 * Solamente se descarga si el logo NO esta en memoria interna.
 * 
 * @author JICZ
 * 
 */
public class DownloadLogo extends AsyncTask<Void, Integer, Boolean> {
    
    /**
     * TAG
     */
    private final static String TAG = "DownloadLogo.Task";
    
    /**
     * 2 minutos TIMEOUT
     */
    public int timeout = 2 * 60 * 1000;
    
    private Context context;
    
    
    /**
     * @param context
     */
    public DownloadLogo(Context context) {
	super();
	this.context = context;
    }
    
    
    
    @Override
    protected Boolean doInBackground(Void... params) {
	
	/*verificar si existe el logo, por lo cual el proceso termina. de lo contrario se descarga la imagen.*/
	if (existeLogo()) {
	    return true;
	}
	else {
	    return downloadLogoImpresora();
	}
    }
    
    
    
    /**
     * descargar el logo que se imprime en la tirilla.
     */
    private boolean downloadLogoImpresora() {
	
	boolean ok = false;
	InputStream inputStream = null;
	FileOutputStream fileOutput = null;
	HttpURLConnection urlConnection = null;
	
	try {
	    
	    /* URL de descarga del logo para imprimir */
	    String urlLogo = Const.URL_LOGO_IMPRESORA;
	    String fileName = "";
	    
	    Log.i(TAG, "URL LOGO = " + urlLogo);
	    
	    URL url = new URL(urlLogo);
	    urlConnection = (HttpURLConnection) url.openConnection();
	    int responseCode = urlConnection.getResponseCode();
	    
	    if (responseCode == HttpURLConnection.HTTP_OK) {
		
		/**
		 * SE DEFINE EL TIEMPO DE ESPERA, MAXIMO ESPERA 2 MINUTOS
		 **/
		urlConnection.setConnectTimeout(timeout);
		urlConnection.setReadTimeout(timeout);
		
		urlConnection.connect();
		
		inputStream = urlConnection.getInputStream();
		String contentDisposition = urlConnection.getHeaderField("Content-Disposition");
		
		if (contentDisposition == null) { // Viene Archivo Adjunto
		
		    fileName = urlLogo.substring(urlLogo.lastIndexOf("/") + 1, urlLogo.length());
		    File file = new File(Util.DirApp(), fileName);
		    
		    if (file.exists())
			file.delete();
		    
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
			
			if (content_length == downloadedSize) {
			    Descomprimir(file);
			    ok = true;
			    PreferenceLogo.guardarNombreLogo(context, fileName);
			    PreferenceLogo.guardarRutaLogo(context, file.getAbsolutePath());
			}
		    }
		}
	    }
	} catch (Exception e) {
	    
	    String motivo = e.getMessage();
	    ok = false;
	    Log.e(TAG, "download Logo --> " + motivo, e);
	}
	finally {
	    
	    try {
		if (fileOutput != null)
		    fileOutput.close();
		
		if (inputStream != null)
		    inputStream.close();
		
	    } catch (IOException e) {
	    }
	    
	    if (urlConnection != null)
		urlConnection.disconnect();
	}
	return ok;
    }
    
    
    
    
    @Override
    protected void onPostExecute(Boolean result) {
	Log.i(TAG, "¿Logo imprimir correcto? : " + result);
	this.cancel(true);
	super.onPostExecute(result);
    }
    
    
    
    /**
     * Descomprimir el zip en la carpeta de la aplicacion.
     * @param fileZip
     */
    public void Descomprimir(File fileZip) {
	try {
	    if (fileZip.exists()) {
		String nameFile = fileZip.getName().replace(".zip", "");
		File fileZipAux = new File(Util.DirApp(), nameFile);
		
		if (!fileZipAux.exists())
		    fileZipAux.delete();
		
		FileInputStream fin = new FileInputStream(fileZip);
		ZipInputStream zin = new ZipInputStream(fin);
		
		ZipEntry ze = null;
		
		while ((ze = zin.getNextEntry()) != null) {
		    
		    Log.v("Descomprimir", "Unzipping " + ze.getName());
		    
		    if (ze.isDirectory()) {
			
			dirChecker(ze.getName());
			
		    }
		    else {
			
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
    
    
    /**
     * verificar directorio
     * @param dir
     */
    private void dirChecker(String dir) {
	
	File f = new File(Util.DirApp().getPath() + "/" + dir);
	
	if (!f.isDirectory()) {
	    
	    f.mkdirs();
	}
    }
    
    
    /**
     * Verificar si existe el logo.
     * @return
     */
    public boolean existeLogo() {
	
	if(PreferenceLogo.getNombreLogo(context).equals("-")){
	    return false;
	}
	File dbFile = new File(Util.DirApp(), PreferenceLogo.getNombreLogo(context));
	return dbFile.exists();
    }
}// final de la clase.
