package co.com.BusinessObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import co.com.DataObject.Cliente;
import co.com.DataObject.Detalle;
import co.com.DataObject.Encabezado;
import co.com.panificadoracountry.Main;
import co.com.panificadoracountry.Util;
import android.content.Context;
import android.util.Log;

public class FileBO {
	
	private final static String TAG = FileBO.class.getName();
	
	public static String mensaje;
	
	public static boolean comprimirArchivo(String fileName, String zipFileName) {
		
		boolean comprimio = false;
		File zipFile = new File(Util.DirApp(), zipFileName);
		
		if (zipFile.exists())
			zipFile.delete();
		
		FileOutputStream out = null;
		GZIPOutputStream gZipOut = null;
		FileInputStream fileInputStream = null;
		
		try {
			
			/**
			 * Archivo a Comprimir. Debe Existir en el Directorio de la Aplicacion
			 */
			File file = new File(Util.DirApp(), fileName);
			
			if (file.exists()) {
				
				fileInputStream = new FileInputStream(file);
				
				int lenFile = fileInputStream.available();
				byte[] buffer = new byte[lenFile];
				
				int byteRead = fileInputStream.read(buffer);
				
				if (byteRead == lenFile) {
					
					out = new FileOutputStream(zipFile);
					gZipOut = new GZIPOutputStream(out);
					gZipOut.write(buffer);
					comprimio = true;
					
				} else {
					
					if (zipFile.exists())
						zipFile.delete();
				}
			}
			
		} catch (Exception e) {
			
			if (zipFile.exists())
				zipFile.delete();
			
			Log.e(TAG, "comprimirArchivo -> " + e.getMessage(), e);
			
		} finally {
						
			try {
				
				if (gZipOut !=  null)
					gZipOut.close();
				
				if (out != null)
					out.close();
				
				if (fileInputStream != null)
					fileInputStream.close();

			} catch (Exception e) {
				
				Log.e(TAG, "comprimirArchivo -> " + e.getMessage(), e);				
			}
		}
		
		return comprimio;
	}
	
	public static void descomprimir(File zipFile) {
		
		try  {
			
			if (zipFile.exists()) {
	        	
				FileInputStream fin = new FileInputStream(zipFile);
	        	ZipInputStream zin = new ZipInputStream(fin);
	        	
	        	ZipEntry ze = null;
	        	int bufferSize = 2 * 1024;
	        	File dirApp = Util.DirApp();
	        	
	        	while ((ze = zin.getNextEntry()) != null) {
	        		
	        		if (ze.isDirectory()) {
			        	
			        	dirChecker(ze.getName());
			        	
			        } else {
			        	
			        	String pathFile = dirApp + "/" + ze.getName();
			        	FileOutputStream fout = new FileOutputStream(pathFile);
			        	
			        	int bufferLength = 0;
			        	byte[] buffer = new byte[bufferSize];
				        
				        while ( (bufferLength = zin.read(buffer)) > 0 ) {
				        	
				        	fout.write(buffer, 0, bufferLength);
				        }
			        	
				        zin.closeEntry();
				        fout.flush();
			        	fout.close();
			        }
	        	}
	        	zin.close();
	        }
	        
		} catch(Exception e) {
			
			Log.e(TAG, "descomprimir -> " + e.getMessage(), e);
		}
	}
	
	private static void dirChecker(String dir) {
		
		File file = new File(Util.DirApp().getPath() + "/" + dir);

		if (!file.isDirectory()) {
			
			file.mkdirs();
		}
	}
	
	public static void validarCliente(Context context) {
		
		if (Main.cliente == null || Main.cliente.codigo == null || Main.cliente.nombre == null ) {
			
			if (context != null) {
				
				Cliente cliente = Cliente.get(context);
				
				if (cliente != null && cliente.codigo != null) {
					
					Main.cliente = cliente;
					
				} else {
					
					Log.e(TAG, "No se pudo leer la informacion del Cliente!");
				}
				
			} else {
				
				Log.e(TAG, "No se pudo leer la informacion del Cliente. El Context es NULL");
			}
		}
	}
	
	public static void deleteObjectsCliente(Context context) {
		
		Cliente.delete(context);
	}
}
