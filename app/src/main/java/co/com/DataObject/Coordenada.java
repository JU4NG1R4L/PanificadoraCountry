package co.com.DataObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import co.com.panificadoracountry.Util;

public class Coordenada implements Serializable {

	/**
	 * Indica que el GPS esta ON, pero aun no ha capturado Coordenada.
	 **/
	public final static int ESTADO_GPS_SIN_RESPUESTA = 0;
	
	/**
	 * Indica que la Coordenada fue captura por el GPS del Movil
	 **/
	public final static int ESTADO_GPS_CAPTURO = 1;
	
	/**
	 * Se guarda Coordenada 0,0 indicando que el GPS esta Apagado.
	 **/
	public final static int ESTADO_GPS_APAGADO = 2;
	
	/**
	 * Constantes utilizadas para guardar en los Settings del Movil, 
	 * cual es el proveedor de GPS con el cual se estan capturando 
	 * Coordenadas. Puede ser: GPS_PROVIDER o NETWORK_PROVIDER
	 **/
	public final static String SETTINGS_GPS = "settings_gps";
	public final static String PROVIDER = "provider";
	
	/**
	 * Defincion de las Variables de la Clase
	 **/
	public String codigoVendedor;
	public String codigoCliente;
	public double latitud;
	public double longitud;
	public String horaCoordenada;
	public String id;
	public String fechaCoordenada;
	public String imei;
	public int	  tipoCaptura;// 0 gps provider, 1 network provider
	public int    inicioCliente; // 1 cuando inicia tiempo en un cliente, 0 cuando finaliza tiempo
	
	/**
	 * 0 -> El GPS esta ON pero no ha Capturado Coordenada.
	 * 1 -> La Coordenada fue capturada por el GPS del Movil durante la toma del Pedido!
	 * 2 -> El GPS Esta Apagado
	 **/
	public int estado;

	public int sincronizado;

	public int bandera;
	
	
	/*****************************************************************************
	 * Definicion de Metodos Para Obtener, Guardar y Eliminar el Objeto Coordenada
	 *****************************************************************************/
	public static Coordenada get(Context context) {
		
		Coordenada coordenada = null;
		
		if (context != null) {
			
			FileInputStream fileInputStream = null;
			ObjectInputStream objInputStream = null;
			
			try {
				
				boolean existe = false;
				String[] fileList = context.fileList();
				
				for (String file : fileList) {
					
					if (file.equals(fileName)) {
						
						existe = true;
						break;
					}
				}
				
				if (existe) {
					
					fileInputStream = context.openFileInput(fileName);
					objInputStream = new ObjectInputStream(fileInputStream);
					coordenada = (Coordenada) objInputStream.readObject();
				}
				
			} catch(Exception e) {
				
				String msg = e.getMessage();
				Log.e(TAG, "getObject -> " + msg, e);
				
			} finally {
				
				try {
					
					if (fileInputStream != null) {
						fileInputStream.close();
					}
					
					if (objInputStream != null) {
						objInputStream.close();
					}
					
				} catch (Exception e) { }
			}
			
		} else {
			
			Log.e(TAG, "No se pudo leer el Objeto, el Context es NULL.");
		}
		
		return coordenada;
	}
	
	public static boolean save(Context context, Coordenada coordenada) {
		
		boolean guardo = false;
		
		if (context != null) {
			
			if (coordenada != null) {
				
				FileOutputStream fileOutputStream = null;
				ObjectOutputStream objOutputStream = null;
				
				try {
					
					fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
					objOutputStream = new ObjectOutputStream(fileOutputStream);
					objOutputStream.writeObject(coordenada);
					guardo = true;
					
				} catch (Exception e) {
					
					String msg = e.getMessage();
					Log.e(TAG, "save -> " + msg, e);
					
				} finally {
					
					try {
						
						if (objOutputStream != null) {
							objOutputStream.close();
						}
						
						if (fileOutputStream != null) {
							fileOutputStream.close();
						}
						
						if (!guardo) {
							context.deleteFile(fileName);
						}
						
					} catch (Exception e) { }
				}
				
			} else {
				
				/**
				 * Si el objeto Coordenada es NULL, se borra el archivo!
				 **/
				context.deleteFile(fileName);
			}
			
		} else {
			
			Log.e(TAG, "No se pudo Guardar el Objeto, el Context es NULL.");
		}
		
		return guardo;
	}
	
	public static boolean delete(Context context) {
		
		boolean borro = false;
		
		if (context != null) {
			
			context.deleteFile(fileName);
			borro = true;
			
		} else {
			
			 Log.e(TAG, "No se pudo Eliminar el Objeto, el Context es NULL.");
		}
		
		return borro;
	}
	
	/******************************************************************************************
	 * Metodos para Obtener, Borrar y Guardar Coordenadas que no se pudieron enviar al Servidor
	 ******************************************************************************************/
	public static boolean crearTablaCoordenadas() {
		
		SQLiteDatabase db = null;
		
		try {
    		
			File dbFile = new File(Util.DirApp(), NAME_DB);
    		db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
    		
    		String tableCoordenadas2 = 
					
					"CREATE TABLE IF NOT EXISTS Coordenadas2 (" +
							"CodigoVendedor varchar(10), " +
							"CodigoCliente varchar(20), " +
							"latitud float, " +
							"longitud float, " +
							"horaCoordenada varchar(10), " +
							"fecha datetime, " +
							"estado int, " +
							"id varchar(30),"+
							"imei varchar(30),"+
							"fechaCoordenada datetime,"+
							"tipoCaptura int)";
    		
    		db.execSQL(tableCoordenadas2);
    		return true;
			
        } catch (Exception e) {
        	
        	Log.e(TAG, "crearTablaCoordenadas -> " +  e.getMessage(), e);
        	return false;
        	
		} finally {
			
			if (db != null) {
				db.close();
			}
		}
	}
	
	public static String obtenerCoordenadas(String separadorRows, String separadorCols) {
		
		String coordenadas = "";
		SQLiteDatabase db = null;
		
		try {
						
			File fileDB = new File(Util.DirApp(), NAME_DB);
			
			if (fileDB.exists()) {
				
				db = SQLiteDatabase.openDatabase(fileDB.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
				Cursor cursor = db.rawQuery("SELECT CodigoCliente, latitud, longitud, horaCoordenada, estado, id, imei, fechaCoordenada, tipoCaptura " +
						"FROM Coordenadas2 ORDER BY fecha DESC", null);
				
				if (cursor.moveToFirst()) {
					
					do {
						
						if (!coordenadas.equals("")) {
							coordenadas += separadorRows;
						}
						
						coordenadas += cursor.getString(cursor.getColumnIndex("CodigoCliente")) + separadorCols;
						coordenadas += cursor.getString(cursor.getColumnIndex("latitud")) + separadorCols;
						coordenadas += cursor.getString(cursor.getColumnIndex("longitud")) + separadorCols;
						coordenadas += cursor.getString(cursor.getColumnIndex("horaCoordenada")) + separadorCols;
						coordenadas += cursor.getString(cursor.getColumnIndex("estado")) + separadorCols;
						coordenadas += cursor.getString(cursor.getColumnIndex("id")) + separadorCols;
						coordenadas += cursor.getString(cursor.getColumnIndex("imei")) + separadorCols;
						coordenadas += cursor.getString(cursor.getColumnIndex("fechaCoordenada")) + separadorCols;
						coordenadas += cursor.getString(cursor.getColumnIndex("tipoCaptura"));
					
					} while (cursor.moveToNext());
				}
				
			} else {
				
				Log.e(TAG, "obtenerCoordenadas -> No Existe la Base de Datos");
			}
			
		} catch (Exception e) {
			
			Log.e(TAG, "obtenerCoordenadas -> " +  e.getMessage(), e);
			
		} finally {
			
			if (db != null) {
				db.close();
			}
		}
		
		return coordenadas;
	}
	
	/**
	 * Borra las coordenadas de la Ruta.
	 * Esto se puede hacer por dos razones:
	 * 1. Envio coordendas correctamente al servidor.
	 * 2. Hubo un cambio de Usuario.
	 **/
	public static boolean borrarCoordenadas() {
		
		SQLiteDatabase db = null;
		
		try {
						
			File fileDB = new File(Util.DirApp(), NAME_DB);
			
			if (fileDB.exists()) {
				
				db = SQLiteDatabase.openDatabase(fileDB.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
				db.execSQL("DELETE FROM Coordenadas2");
				db.execSQL("VACUUM");
				
				return true;
				
			} else {
				
				Log.e(TAG, "borrarCoordenadas -> No Existe la Base de Datos");
				return false;
			}
			
		} catch (Exception e) {
			
			Log.e(TAG, "borrarCoordenadas -> " +  e.getMessage(), e);
			return false;
			
		} finally {
			
			if (db != null) {
				db.close();
			}
		}
	}
	
	public static boolean guardarCoordenada(Coordenada coordenada) {
		
		long rows = 0;
		SQLiteDatabase db = null;
		
		try {
						
			File fileDB = new File(Util.DirApp(), NAME_DB);
			
			if (fileDB.exists()) {
				
				db = SQLiteDatabase.openDatabase(fileDB.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
				
				ContentValues values = new ContentValues();
				values.put("CodigoVendedor", 	coordenada.codigoVendedor);
				values.put("CodigoCliente",  	coordenada.codigoCliente);
				values.put("latitud",        	coordenada.latitud);
				values.put("longitud",       	coordenada.longitud);
				values.put("horaCoordenada", 	coordenada.horaCoordenada);
				values.put("estado",       		coordenada.estado);
				values.put("fecha",         	Util.FechaActual("yyyy-MM-dd HH:mm:ss"));
				values.put("id",           	    coordenada.id);
				values.put("imei",           	coordenada.imei);
				values.put("fechaCoordenada", 	coordenada.fechaCoordenada);
				values.put("tipoCaptura",       coordenada.tipoCaptura);
				
				rows = db.insertOrThrow("Coordenadas2", null, values);
				return rows > 0;
				
			} else {
				
				Log.e(TAG, "guardarCoordenada -> No Existe la Base de Datos");
				return false;
			}
			
		} catch (Exception e) {
			
			Log.e(TAG, "guardarCoordenada -> " +  e.getMessage(), e);
			return false;
			
		} finally {
			
			if (db != null) {
				db.close();
			}
		}
	}
	
	public static String obtenerId(String codigoVendedor) {
		
		return "A" + codigoVendedor + Util.FechaActual("yyyyMMddHHmmss") + Util.lpad(Util.FechaActual("Ms"), 3, "0");
	}
	
	private static final long serialVersionUID = 1L;
	private static final String NAME_DB = "Config.db";
	private static final String fileName = "Coordenada";
	private static final String TAG = Coordenada.class.getName();
}
