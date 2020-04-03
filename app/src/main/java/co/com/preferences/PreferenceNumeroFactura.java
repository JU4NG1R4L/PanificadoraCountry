/**
 * 
 */
package co.com.preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Clase que ayuda a la insercion y obtencion de datos del numero de factura.
 * de un pedido
 * 
 * @author JICZ
 *
 */
public class PreferenceNumeroFactura {
    
    /**
     * constante para conservar el nombre del archivo de preferencias
     */
    private static final String NOMBRE = "preferenceNumeroFactura";	
    
    /**
     * representa el key para acceder al numero actual
     */
    private static final String NUMERO_ACTUAL = "numeroActual";
    
    
    /**
     * representa el key para acceder al numero anterior
     */
    private static final String NUMERO_ANTERIOR = "numeroAnterior";
    
    
    
    /**
     * Permite guardar el numero actual
     * @param context contexto al que se aplica el preference
     * @param longitud
     */
    public static void guardarNumeroActual(Context context, String numeroActual) {
	SharedPreferences settings = context.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
	SharedPreferences.Editor editor = settings.edit();
	editor.putString(NUMERO_ACTUAL, numeroActual);
	editor.commit();
    }
    
    /**
     * obtener numero actual
     * @param context
     * @return
     */
    public static String getNumeroActual(Context context){
	SharedPreferences settings = context.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
	return settings.getString(NUMERO_ACTUAL, "0");
    }
    
    
    
    
    
    /**
     * Permite guardar el numero actual
     * @param context contexto al que se aplica el preference
     * @param longitud
     */
    public static void guardarNumeroAnterior(Context context, String numeroAnterior) {
	SharedPreferences settings = context.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
	SharedPreferences.Editor editor = settings.edit();
	editor.putString(NUMERO_ANTERIOR, numeroAnterior);
	editor.commit();
    }
    
    /**
     * obtener numero actual
     * @param context
     * @return el numero de factura anterior, Cero(0) en caso contrario.
     */
    public static String getNumeroAnterior(Context context){
	SharedPreferences settings = context.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
	return settings.getString(NUMERO_ANTERIOR, "0");
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
