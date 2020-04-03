package co.com.BusinessObject;

import java.io.File;

import co.com.DataObject.Config;
import co.com.panificadoracountry.Const;
import co.com.panificadoracountry.Util;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ConfigBO {
    
    public static final String TAG = "BusinessObject.ConfigBO";
    
    public static String mensaje;
    
    public static boolean CrearConfigDB() {
	
	SQLiteDatabase db = null;
	
	try {
	    
	    File dbFile = new File(Util.DirApp(), "Config.db");
	    db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
	    
	    String config = "CREATE TABLE IF NOT EXISTS Config(usuario varchar(20), bodega varchar(20), iniciarDia int,canal_venta int)";
	    db.execSQL(config);
	    return true;
	    
	} catch (Exception e) {
	    
	    mensaje = e.getMessage();
	    return false;
	    
	} finally {
	    
	    if (db != null)
		db.close();
	}
    }
    
    public static boolean GuardarConfigUsuario(String usuario, String bodega, int inciarDia,int canalVenta) {
	
	int total = 0;
	SQLiteDatabase db = null;
	
	try {
	    
	    File dbFile = new File(Util.DirApp(), "Config.db");
	    
	    if (dbFile.exists()) {
		
		db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
		
		String query = "SELECT COUNT(usuario) AS total FROM Config";
		Cursor cursor = db.rawQuery(query, null);
		
		if (cursor.moveToFirst()) {
		    
		    total = cursor.getInt(cursor.getColumnIndex("total"));
		}
		
		if (cursor != null)
		    cursor.close();
		
		ContentValues values = new ContentValues();
		values.put("usuario", usuario.trim());
		values.put("bodega", bodega.trim());
		values.put("iniciarDia", inciarDia);
		values.put("canal_venta", canalVenta);
		
		long rows = -1;
		
		if (total == 0) {
		    
		    rows = db.insertOrThrow("Config", null, values);
		    
		} else {
		    
		    rows = db.update("Config", values, null, null);
		}
		
		return rows > 0;
		
	    } else {
		
		Log.i(TAG, "GuardarConfigUsuario: No Existe la Base de Datos Config.db o No tiene Acceso a la SD");
		return false;
	    }
	    
	} catch (Exception e) {
	    
	    mensaje = e.getMessage();
	    return false;
	    
	} finally {
	    
	    if (db != null)
		db.close();
	}
    }
    
    public static Config ObtenerConfigUsuario() {
	
	Config config = null;
	SQLiteDatabase db = null;
	
	try {
	    
	    File dbFile = new File(Util  .DirApp(), "Config.db");
	    
	    if (dbFile.exists()) {
		
		db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
		
		String query = "SELECT usuario, bodega, iniciarDia,canal_venta FROM Config";
		Cursor cursor = db.rawQuery(query, null);
		
		if (cursor.moveToFirst()) {
		    
		    config = new Config();
		    config.usuario = cursor.getString(cursor.getColumnIndex("usuario"));
		    config.bodega = cursor.getString(cursor.getColumnIndex("bodega"));
		    config.iniciarDia = cursor.getInt(cursor.getColumnIndex("iniciarDia"));
		    config.canalVenta = cursor.getInt(cursor.getColumnIndex("canal_venta"));
		    
		}
		
		if (cursor != null)
		    cursor.close();
		
	    } else {
		
		Log.i(TAG, "ObtenerConfigUsuario: No Existe la Base de Datos Config.db o No tiene Acceso a la SD");
	    }
	    
	} catch (Exception e) {
	    
	    mensaje = e.getMessage();
	    Log.e("CargarConfigUsuario", mensaje, e);
	    
	} finally {
	    
	    if (db != null)
		db.close();
	}
	
	return config;
    }
    
    
    
    public static void setAppAutoventa(){
	
	SQLiteDatabase db = null;
	String sql = "";
	
	String result = "";
	
	Const.tipoAplicacion = 0;
	
	try{
	    
	    File dbFile = new File(Util.DirApp(), "Config.db");
	    
	    if (dbFile.exists()) {
		
		db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
		
		sql = "select canal_venta from Config limit 1";
		
		Cursor cursor = db.rawQuery( sql, null );
		
		if( cursor.moveToFirst() ){
		    
		    result = cursor.getString( cursor.getColumnIndex( "canal_venta" ) );
		}
		
		if( cursor != null )
		    cursor.close();
		
		if( result.toUpperCase().equals( "1" ) ){
		    
		    Const.tipoAplicacion = Const.AUTOVENTA;
		}
		else{
		    
		    Const.tipoAplicacion = Const.PREVENTA;
		}
	    }
	}
	catch( Exception e ){
	    
	}
	finally{
	    
	    if (db != null)
		db.close();
	}
    }
    
    
}
