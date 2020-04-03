/**
 * 
 */
package co.com.woosim.printer;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Clase que ayuda a la insercion y obtencion de datos del logo para imprimir
 * de la aplicacion
 * 
 * @author JICZ
 *
 */
public class PreferenceLogo {
    
    /**
     * constante para conservar el nombre del archivo de preferencias
     */
    private static final String NOMBRE = "preferenceLogo";	
    
    /**
     * representa el key para acceder al nombre del logo
     */
    private static final String NAME_LOGO = "nombreLogo";
    
    
    /**
     * representa el key para acceder a la ruta del logo
     */
    private static final String RUTA_LOGO = "rutaLogo";
    
    
    
    /**
     * Permite guardar el nombre del logo
     * @param context contexto al que se aplica el preference
     * @param longitud
     */
    public static void guardarNombreLogo(Context context, String nombre) {
	SharedPreferences settings = context.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
	SharedPreferences.Editor editor = settings.edit();
	editor.putString(NAME_LOGO, nombre);
	editor.commit();
    }
    
    /**
     * obtener nombre de logo
     * @param context
     * @return
     */
    public static String getNombreLogo(Context context){
	SharedPreferences settings = context.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
	return settings.getString(NAME_LOGO, "-");
    }
    
    
    
    /**
     * Permite guardar ruta del logo
     * @param context contexto al que se aplica el preference
     * @param longitud
     */
    public static void guardarRutaLogo(Context context, String path) {
	SharedPreferences settings = context.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
	SharedPreferences.Editor editor = settings.edit();
	editor.putString(RUTA_LOGO, path);
	editor.commit();
    }
    
    /**
     * obtener ruta (path) de logo
     * @param context
     * @return
     */
    public static String getRutaLogo(Context context){
	SharedPreferences settings = context.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
	return settings.getString(RUTA_LOGO, "-");
    }
    

    
    /**
     * vaciar el preference. Remover todos los datos guardados.
     * @param context
     */
    public static void vaciarPreferences(Context context) {
	SharedPreferences settings = context.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
	SharedPreferences.Editor editor = settings.edit();
	//vaciar todo el preference
	editor.clear();
	editor.commit();
    }
}
