package co.com.panificadoracountry;


import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import co.com.BusinessObject.DataBaseBO;
import co.com.DataObject.Cliente;
import co.com.DataObject.Detalle;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class Util {
    
    private static final String TAG = Util.class.getName();
	public static String mensaje = "";
    
    
    
    public static int ToInt(String value) {
	
	try {
	    
	    return Integer.parseInt(value);
	    
	} catch (NumberFormatException e) {
	    
	    return 0;
	}
    }
    
    
    
    public static long ToLong(String value) {
	
	try {
	    
	    return Long.parseLong(value);
	    
	} catch (NumberFormatException e) {
	    
	    return 0L;
	}
    }
    
    
    
    public static float ToFloat(String value) {
	
	try {
	    
	    return Float.parseFloat(value);
	    
	} catch (NumberFormatException e) {
	    
	    return 0F;
	}
    }
    
    
    
    public static String Redondear(String numero, int cantDec) {
	
	int tamNumero = 0;
	double numRedondear;
	int cantAfterPunto;
	
	if (numero.indexOf(".") == -1) {
	    
	    return numero;
	}
	
	tamNumero = numero.length();
	cantAfterPunto = tamNumero - (numero.indexOf(".") + 1);
	
	if (cantAfterPunto <= cantDec)
	    return numero;
	
	String numeroSumar = "0.";
	
	for (int i = 0; i < cantDec; i++) {
	    
	    numeroSumar = numeroSumar.concat("0");
	}
	
	numeroSumar = numeroSumar.concat("5");
	
	numRedondear = Double.parseDouble(numero);
	
	numRedondear = numRedondear + Double.parseDouble(numeroSumar);
	
	numero = String.valueOf(numRedondear);
	
	tamNumero = numero.length();
	cantAfterPunto = tamNumero - (numero.indexOf(".") + 1);
	
	if (cantAfterPunto <= cantDec)
	    return numero;
	else {
	    
	    if (cantDec == 0)
		numero = numero.substring(0, numero.indexOf("."));
	    else
		numero = numero.substring(0, (numero.indexOf(".") + 1 + cantDec));
	    
	    return numero;
	}
    }
    
    
    
    public static String RedondearFit(String numero, int cantDec) {
	
	int tamNumero = 0;
	double numRedondear;
	int cantAfterPunto;
	
	if (numero.indexOf(".") == -1) {
	    
	    return numero;
	}
	
	tamNumero = numero.length();
	cantAfterPunto = tamNumero - (numero.indexOf(".") + 1);
	
	if (cantAfterPunto <= cantDec) {
	    
	    int falta = cantDec - cantAfterPunto;
	    
	    if (falta > 0) {
		
		for (int i = 0; i < falta; i++)
		    numero += "0";
	    }
	    
	    return numero;
	}
	
	String numeroSumar = "0.";
	
	for (int i = 0; i < cantDec; i++) {
	    
	    numeroSumar = numeroSumar.concat("0");
	}
	
	numeroSumar = numeroSumar.concat("5");
	
	numRedondear = Double.parseDouble(numero);
	
	numRedondear = numRedondear + Double.parseDouble(numeroSumar);
	
	numero = String.valueOf(numRedondear);
	
	tamNumero = numero.length();
	cantAfterPunto = tamNumero - (numero.indexOf(".") + 1);
	
	if (cantAfterPunto <= cantDec) {
	    
	    int falta = cantDec - cantAfterPunto;
	    
	    if (falta > 0) {
		
		for (int i = 0; i < falta; i++)
		    numero += "0";
	    }
	    
	    return numero;
	    
	}
	else {
	    
	    if (cantDec == 0)
		numero = numero.substring(0, numero.indexOf("."));
	    else
		numero = numero.substring(0, (numero.indexOf(".") + 1 + cantDec));
	    
	    return numero;
	}
    }
    
    
    
    public static String SepararMiles(String numero) {
	
	String cantidad;
	String cantidadAux1;
	String cantidadAux2;
	boolean tieneMenos;
	
	int posPunto;
	int i;
	
	cantidad = "";
	cantidadAux1 = "";
	cantidadAux2 = "";
	
	tieneMenos = false;
	if (numero.indexOf("-") != -1) {
	    
	    String aux;
	    tieneMenos = true;
	    aux = numero.substring(0, numero.indexOf("-"));
	    aux = aux + numero.substring(numero.indexOf("-") + 1, numero.length());
	    numero = aux;
	}
	
	if (numero.indexOf(".") == -1) {
	    
	    if (numero.length() > 3) {
		
		cantidad = ColocarComas(numero, numero.length());
		
	    }
	    else {
		
		if (tieneMenos)
		    numero = "$-" + numero;
		else
		    numero = "$" + numero;
		
		return numero;
	    }
	    
	}
	else {
	    
	    posPunto = numero.indexOf(".");
	    
	    for (i = 0; i < posPunto; i++) {
		
		cantidadAux1 = cantidadAux1 + numero.charAt(i);
	    }
	    
	    for (i = posPunto; i < numero.length(); i++) {
		
		cantidadAux2 = cantidadAux2 + numero.charAt(i);
	    }
	    
	    if (cantidadAux1.length() > 3) {
		
		cantidad = ColocarComas(cantidadAux1, posPunto);
		cantidad = cantidad + cantidadAux2;
		
	    }
	    else {
		
		if (tieneMenos)
		    numero = "$-" + numero;
		else
		    numero = "$" + numero;
		
		return numero;
	    }
	}
	
	if (tieneMenos)
	    cantidad = "$-" + cantidad;
	else
	    cantidad = "$" + cantidad;
	
	return cantidad;
    }
    
    
    
    public static String Miles(String numero) {
	
	String cantidad;
	String cantidadAux1;
	String cantidadAux2;
	boolean tieneMenos;
	
	int posPunto;
	int i;
	
	cantidad = "";
	cantidadAux1 = "";
	cantidadAux2 = "";
	
	tieneMenos = false;
	if (numero.indexOf("-") != -1) {
	    
	    String aux;
	    tieneMenos = true;
	    aux = numero.substring(0, numero.indexOf("-"));
	    aux = aux + numero.substring(numero.indexOf("-") + 1, numero.length());
	    numero = aux;
	}
	
	if (numero.indexOf(".") == -1) {
	    
	    if (numero.length() > 3) {
		
		cantidad = ColocarComas(numero, numero.length());
		
	    }
	    else {
		
		if (tieneMenos)
		    numero = "-" + numero;
		
		return numero;
	    }
	    
	}
	else {
	    
	    posPunto = numero.indexOf(".");
	    
	    for (i = 0; i < posPunto; i++) {
		
		cantidadAux1 = cantidadAux1 + numero.charAt(i);
	    }
	    
	    for (i = posPunto; i < numero.length(); i++) {
		
		cantidadAux2 = cantidadAux2 + numero.charAt(i);
	    }
	    
	    if (cantidadAux1.length() > 3) {
		
		cantidad = ColocarComas(cantidadAux1, posPunto);
		cantidad = cantidad + cantidadAux2;
		
	    }
	    else {
		
		if (tieneMenos)
		    numero = "-" + numero;
		
		return numero;
	    }
	}
	
	if (tieneMenos)
	    cantidad = "-" + cantidad;
	
	return cantidad;
    }
    
    
    
    public static String SepararMilesSin(String numero) {
	
	String cantidad;
	String cantidadAux1;
	String cantidadAux2;
	boolean tieneMenos;
	
	int posPunto;
	int i;
	
	cantidad = "";
	cantidadAux1 = "";
	cantidadAux2 = "";
	
	tieneMenos = false;
	if (numero.indexOf("-") != -1) {
	    
	    String aux;
	    tieneMenos = true;
	    aux = numero.substring(0, numero.indexOf("-"));
	    aux = aux + numero.substring(numero.indexOf("-") + 1, numero.length());
	    numero = aux;
	}
	
	if (numero.indexOf(".") == -1) {
	    
	    if (numero.length() > 3) {
		
		cantidad = ColocarComas(numero, numero.length());
		
	    }
	    else {
		
		if (tieneMenos)
		    numero = "-" + numero;
		else
		    numero = "" + numero;
		
		return numero;
	    }
	    
	}
	else {
	    
	    posPunto = numero.indexOf(".");
	    
	    for (i = 0; i < posPunto; i++) {
		
		cantidadAux1 = cantidadAux1 + numero.charAt(i);
	    }
	    
	    for (i = posPunto; i < numero.length(); i++) {
		
		cantidadAux2 = cantidadAux2 + numero.charAt(i);
	    }
	    
	    if (cantidadAux1.length() > 3) {
		
		cantidad = ColocarComas(cantidadAux1, posPunto);
		cantidad = cantidad + cantidadAux2;
		
	    }
	    else {
		
		if (tieneMenos)
		    numero = "-" + numero;
		else
		    numero = "" + numero;
		
		return numero;
	    }
	}
	
	if (tieneMenos)
	    cantidad = "-" + cantidad;
	else
	    cantidad = "" + cantidad;
	
	return cantidad;
    }
    
    
    
    private static String ColocarComas(String numero, int pos) {
	
	String cantidad;
	Vector<String> cantidadAux;
	String cantidadAux1;
	int i;
	int cont;
	
	cantidadAux = new Vector<String>();
	cantidad = "";
	cantidadAux1 = "";
	cont = 0;
	
	for (i = (pos - 1); i >= 0; i--) {
	    
	    if (cont == 3) {
		
		cantidadAux1 = "," + cantidadAux1;
		cantidadAux.addElement(cantidadAux1);
		cantidadAux1 = "";
		cont = 0;
	    }
	    
	    cantidadAux1 = numero.charAt(i) + cantidadAux1;
	    cont++;
	}
	
	cantidad = cantidadAux1;
	
	for (i = cantidadAux.size() - 1; i >= 0; i--) {
	    
	    cantidad = cantidad + cantidadAux.elementAt(i).toString();
	}
	
	return cantidad;
    }
    
    
    
    public static String ObtenerHora() {
	
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(new Date());
	
	int hh = calendar.get(Calendar.HOUR_OF_DAY);
	String hora = hh < 10 ? "0" + hh : "" + hh;
	
	int mm = calendar.get(Calendar.MINUTE);
	String minutos = mm < 10 ? "0" + mm : "" + mm;
	
	int ss = calendar.get(Calendar.SECOND);
	String segundos = ss < 10 ? "0" + ss : "" + ss;
	
	return hora + ":" + minutos + ":" + segundos;
    }
    
    
    
    public static String lpad(String cadena, int tamano, String caracter) {
	
	int i;
	int tamano1;
	
	tamano1 = cadena.length();
	
	if (tamano1 > tamano){
		cadena = cadena.substring(0, tamano);
	}
	    
	tamano1 = cadena.length();
	
	for (i = tamano1; i < tamano; i++){
		 cadena = caracter + cadena;
	}
	   
	
	return cadena;
    }
    
    
    
    public static File DirApp() {
	
	File SDCardRoot = Environment.getExternalStorageDirectory();
	File dirApp = new File(SDCardRoot.getPath() + "/" + Const.nameDirApp);
	
	if (!dirApp.isDirectory())
	    dirApp.mkdirs();
	
	return dirApp;
    }
    
    
    
    public static void MostrarAlertDialog(Context context, String mensaje) {
	
	AlertDialog alertDialog;
	
	ContextThemeWrapper ctw = new ContextThemeWrapper(context, R.style.Theme_Dialog_Translucent);
	AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
	builder.setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
	    
	    public void onClick(DialogInterface dialog, int id) {
		
		dialog.cancel();
	    }
	});
	
	alertDialog = builder.create();
	alertDialog.setMessage(mensaje);
	alertDialog.show();
    }
    
    
    
    public static void MostrarAlertDialog(Context context, String mensaje, int tipoIcono) {
	
	AlertDialog alertDialog;
	
	ContextThemeWrapper ctw = new ContextThemeWrapper(context, R.style.Theme_Dialog_Translucent);
	AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
	builder.setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
	    
	    public void onClick(DialogInterface dialog, int id) {
		
		dialog.cancel();
	    }
	});
	
	alertDialog = builder.create();
	alertDialog.setMessage(mensaje);
	
	if (tipoIcono == 1) {
	    
	    alertDialog.setIcon(R.drawable.alert);
	    alertDialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
	    alertDialog.setTitle("Atencion");
	    alertDialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.alert);
	    
	}
	
	if (tipoIcono == 2) {
	    
	    alertDialog.setIcon(R.drawable.inform);
	    alertDialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
	    alertDialog.setTitle("Mensaje");
	    alertDialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.inform);
	    
	}
	
	if (tipoIcono == 3) {
	    
	    alertDialog.setIcon(R.drawable.aceptar);
	    alertDialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
	    alertDialog.setTitle("Mensaje");
	    alertDialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.aceptar);
	    
	}
	
	if (tipoIcono == 4) {
	    
	    alertDialog.setIcon(R.drawable.actualizar);
	    alertDialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
	    alertDialog.setTitle("Atencion");
	    alertDialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.aceptar);
	    
	}
	
	// alertDialog.getWindow().setBackgroundDrawable(new
	// ColorDrawable(android.graphics.Color.WHITE));
	alertDialog.show();
	
    }
    
    
    
    public static void MostrarAlertDialog2(Context context, String mensaje, int tipoAlerta) {
	
	AlertDialog alertDialog;
	
	ContextThemeWrapper ctw = new ContextThemeWrapper(context, R.style.Theme_Dialog_Translucent);
	AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
	builder.setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
	    
	    public void onClick(DialogInterface dialog, int id) {
		
		dialog.cancel();
	    }
	});
	
	alertDialog = builder.create();
	alertDialog.setMessage(mensaje);
	if (tipoAlerta == 1) {
	    alertDialog.setIcon(R.drawable.atencion);
	    alertDialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
	    alertDialog.setTitle("Atencion");
	    
	    alertDialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.atencion);
	    
	}
	else {
	    
	    alertDialog.setIcon(R.drawable.email_receive_icon);
	    alertDialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
	    alertDialog.setTitle("Mensaje");
	    
	    alertDialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.email_receive_icon);
	    
	}
	
	alertDialog.show();
	
    }
    
    
    
    public static String DateToStringMM_DD_YYYY(Date date) {
	
	Calendar calendario;
	String fechaRetorna = "";
	
	calendario = Calendar.getInstance();
	calendario.setTime(date);
	
	if ((calendario.get(Calendar.MONTH) + 1) < 10)
	    fechaRetorna += "0" + (calendario.get(Calendar.MONTH) + 1);
	else
	    fechaRetorna += String.valueOf(calendario.get(Calendar.MONTH) + 1);
	
	fechaRetorna += "/";
	
	if (calendario.get(Calendar.DAY_OF_MONTH) < 10)
	    fechaRetorna += "0" + calendario.get(Calendar.DAY_OF_MONTH);
	else
	    fechaRetorna += String.valueOf(calendario.get(Calendar.DAY_OF_MONTH));
	
	fechaRetorna += "/";
	
	fechaRetorna += String.valueOf(calendario.get(Calendar.YEAR));
	
	return fechaRetorna;
    }
    
    
    
    public static String QuitarE(String numero) {
	
	int posE;
	int cantMover;
	int posAux;
	int cantCeros;
	int posPunto;
	String cantMoverString;
	String cantidad;
	String cantidadAux1, cantidadAux2;
	
	cantMoverString = "";
	cantidad = "";
	
	if (!(numero.indexOf("E") != -1)) {
	    
	    return numero;
	}
	else {
	    
	    posE = numero.indexOf("E");
	    posE++;
	    
	    while (posE < numero.length()) {
		
		cantMoverString = cantMoverString + numero.charAt(posE);
		posE++;
	    }
	    
	    cantMover = Integer.parseInt(cantMoverString);
	    
	    posE = numero.indexOf("E");
	    posAux = 0;
	    posPunto = 0;
	    
	    while (posAux < posE) {
		
		if (numero.charAt(posAux) != '.') {
		    
		    cantidad = cantidad + numero.charAt(posAux);
		}
		else {
		    
		    posPunto = posAux;
		}
		posAux++;
	    }
	    
	    if (cantidad.length() < (cantMover + posPunto)) {
		
		cantCeros = cantMover - cantidad.length() + posPunto;
		
		for (int i = 0; i < cantCeros; i++) {
		    
		    cantidad = cantidad + "0";
		}
	    }
	    else {
		
		cantidadAux1 = cantidad.substring(0, (cantMover + posPunto));
		cantidadAux2 = cantidad.substring((cantMover + posPunto), cantidad.length());
		
		if (!cantidadAux2.equals("")) {
		    
		    cantidad = cantidadAux1 + "." + cantidadAux2;
		}
		else {
		    
		    cantidad = cantidadAux1;
		}
	    }
	}
	
	return cantidad;
    }
    
    
    
    public static String FechaActual(String format) {
	
	Date date = new Date();
	try {
	    SimpleDateFormat sdf = new SimpleDateFormat(format);
	    return sdf.format(date);
	} catch (Exception e) {
	    return null;
	}
    }
    
    
    
    public static String ObtenerFechaId() {
	
	Date date = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
	return sdf.format(date);
    }
    
    
    
    public static Drawable ResizedImage(byte[] image, int newWidth, int newHeight) {
	
	Matrix matrix;
	Bitmap resizedBitmap = null;
	Bitmap bitmapOriginal = null;
	
	try {
	    
	    bitmapOriginal = BitmapFactory.decodeByteArray(image, 0, image.length);
	    
	    int width = bitmapOriginal.getWidth();
	    int height = bitmapOriginal.getHeight();
	    
	    if (width == newWidth && height == newHeight) {
		
		return new BitmapDrawable(bitmapOriginal);
	    }
	    
	    // Reescala el Ancho y el Alto de la Imagen
	    float scaleWidth = ((float) newWidth) / width;
	    float scaleHeight = ((float) newHeight) / height;
	    
	    matrix = new Matrix();
	    matrix.postScale(scaleWidth, scaleHeight);
	    
	    // Crea la Imagen con el nuevo Tamano
	    resizedBitmap = Bitmap.createBitmap(bitmapOriginal, 0, 0, width, height, matrix, true);
	    mensaje = "Imagen escalada correctamente";
	    return new BitmapDrawable(resizedBitmap);
	    
	} catch (Exception e) {
	    
	    mensaje = "Error escalando la Imagen: " + e.toString();
	    return null;
	    
	}
	finally {
	    
	    matrix = null;
	    resizedBitmap = null;
	    bitmapOriginal = null;
	    System.gc();
	}
    }
    
    
    
    public static Drawable ResizedImage(Drawable imgOriginal, int newWidth, int newHeight) {
	
	Matrix matrix;
	Bitmap resizedBitmap = null;
	Bitmap bitmapOriginal = null;
	
	try {
	    
	    // bitmapOriginal = BitmapDrawable(resizedBitmap);
	    bitmapOriginal = ((BitmapDrawable) imgOriginal).getBitmap();
	    
	    int width = bitmapOriginal.getWidth();
	    int height = bitmapOriginal.getHeight();
	    
	    if (width == newWidth && height == newHeight) {
		
		return new BitmapDrawable(bitmapOriginal);
	    }
	    
	    // Reescala el Ancho y el Alto de la Imagen
	    float scaleWidth = ((float) newWidth) / width;
	    float scaleHeight = ((float) newHeight) / height;
	    
	    matrix = new Matrix();
	    matrix.postScale(scaleWidth, scaleHeight);
	    
	    // Crea la Imagen con el nuevo Tamano
	    resizedBitmap = Bitmap.createBitmap(bitmapOriginal, 0, 0, width, height, matrix, true);
	    mensaje = "Imagen escalada correctamente";
	    return new BitmapDrawable(resizedBitmap);
	    
	} catch (Exception e) {
	    
	    mensaje = "Error escalando la Imagen: " + e.toString();
	    return null;
	    
	}
	finally {
	    
	    matrix = null;
	    resizedBitmap = null;
	    bitmapOriginal = null;
	    System.gc();
	}
    }
    
    
    
    public static boolean BorrarDataBase() {
	
	File dbFile = new File(Util.DirApp(), "DataBase.db");
	
	try {
	    
	    boolean borro = false;
	    
	    if (dbFile.exists())
		borro = dbFile.delete();
	    
	    return borro;
	    
	} catch (Exception e) {
	    
	    return false;
	}
    }
    
    
    
    public static String SepararPalabrasTextView(String cadena, int caracteres) {
	
	StringBuffer buffer = new StringBuffer();
	String mainString = cadena;
	
	if (mainString.equals("")) {
	    
	    buffer.append(mainString);
	}
	
	while (!mainString.equals("")) {
	    
	    if (mainString.length() <= caracteres) {
		
		if (!buffer.toString().equals(""))
		    buffer.append("<br />");
		
		buffer.append(mainString);
		mainString = "";
		
	    }
	    else {
		
		String tempString;
		
		if (mainString.length() >= caracteres) {
		    
		    if (mainString.lastIndexOf(' ') == -1) {
			
			if (!buffer.toString().equals(""))
			    buffer.append("<br />");
			
			buffer.append(mainString);
			
			mainString = "";
			continue;
			
		    }
		    else {
			
			tempString = mainString.substring(0, caracteres);
		    }
		    
		}
		else {
		    
		    tempString = mainString;
		}
		
		if (tempString.lastIndexOf(' ') == -1) {
		    
		    if (!buffer.toString().equals(""))
			buffer.append("<br />");
		    
		    buffer.append(tempString);
		    
		    if (!tempString.equals(mainString)) {
			
			mainString = mainString.substring(caracteres + 1);
			
		    }
		    else {
			
			mainString = "";
		    }
		    
		}
		else {
		    
		    if (!buffer.toString().equals(""))
			buffer.append("<br />");
		    
		    buffer.append(tempString.substring(0, tempString.lastIndexOf(' ')));
		    
		    mainString = mainString.substring(tempString.lastIndexOf(' ') + 1);
		}
	    }
	}
	
	return buffer.toString();
    }
    
    
    
    public static void Headers(TableLayout tabla, final String[] cabecera, Context context) {
	
	final TableRow fila = new TableRow(context);
	TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
	params.setMargins(0, 3, 0, 30);
	
	for (int i = 0; i < cabecera.length; i++) {
	    
	    TextView lbl = new TextView(context);
	    lbl.setText("" + cabecera[i]);
	    lbl.setPadding(5, 0, 5, 0);
	    lbl.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
	    lbl.setTextColor(Color.argb(255, 255, 255, 255));
	    lbl.setTextSize(14);
	    // lbl.setBackgroundDrawable(context.getResources().getDrawable(android.R.drawable.editbox_dropdown_dark_frame));
	    lbl.setBackground(context.getResources().getDrawable(R.drawable.table_cell_header));
	    
	    fila.addView(lbl);
	}
	
	/*
	 * alinear la cuadricula para que se conserven del mismo tamaño que su
	 * par. el valor height de categoria es siempre mas grande que el de
	 * total, por esto se elige como referencia
	 */
	ViewTreeObserver viewTreeObserver = fila.getChildAt(0).getViewTreeObserver();
	viewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	    
	    @Override
	    public void onGlobalLayout() {
		
		int height = fila.getChildAt(0).getMeasuredHeight();
		for (int i = 0; i < cabecera.length; i++) {
		    fila.getChildAt(i).setMinimumHeight(height);
		}
	    }
	});
	fila.setLayoutParams(params);
	tabla.addView(fila);
    }
    
    
    
    public static String[] split(String original, String separator) {
	
	Vector nodes = new Vector();
	
	// Parse nodes into vector
	int index = original.indexOf(separator);
	while (index >= 0) {
	    nodes.addElement(original.substring(0, index));
	    original = original.substring(index + separator.length());
	    index = original.indexOf(separator);
	}
	// Get the last node
	nodes.addElement(original);
	
	// Create splitted string array
	String[] result = new String[nodes.size()];
	if (nodes.size() > 0) {
	    for (int loop = 0; loop < nodes.size(); loop++)
		result[loop] = (String) nodes.elementAt(loop);
	}
	return result;
    }
    
    
    
    public static boolean checkSDCard() {
	
	boolean check = false;
	
	try {
	    
	    if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
		
		check = true;
		
	    }
	    else {
		
		check = false;
		
	    }
	    
	} catch (Exception e) {
	    
	    check = false;
	    
	}
	
	return check;
	
    }
    
    
    
    public static void closeTecladoStartActivity(Activity context) {
	
	context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	
    }
    
    
    
    public static boolean isCurrentActivity(final Activity context) {
	
	try {
	    
	    ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
	    
	    List<ActivityManager.RunningTaskInfo> taskInfo = activityManager.getRunningTasks(1);
	    
	    String nameActivity = context.getComponentName().getClassName();
	    
	    String currentActivity = taskInfo.get(0).topActivity.getClassName();
	    
	    boolean is = nameActivity.equals(currentActivity);
	    
	    if (is) {
		
		context.runOnUiThread(new Runnable() {
		    
		    public void run() {
			
			playSound4(context);
			vibratePhone(context);
			// playSound2();
			
		    }
		});
		
	    }
	    
	    return is;
	    
	} catch (Exception e) {
	    
	    Log.e("TAG", e.getMessage(), e);
	    
	    return false;
	    
	}
	
    }
    
    
    
    public static String rpad(String cadena, int tamano, String caracter) {
	
	int i;
	
	int tamano1;
	
	tamano1 = cadena.length();
	
	if (tamano1 > tamano){
		cadena = cadena.substring(0, tamano);
	}
	    
	tamano1 = cadena.length();
	
	for (i = tamano1; i < tamano; i++){
		cadena = cadena + caracter;
	}
	    
	return cadena;
	
    }
    
    
    
    public static void playSound(Context context) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {
	Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	MediaPlayer mMediaPlayer = new MediaPlayer();
	mMediaPlayer.setDataSource(context, soundUri);
	final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
	if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
	    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
	    mMediaPlayer.setLooping(true);
	    mMediaPlayer.prepare();
	    mMediaPlayer.start();
	}
    }
    
    
    
    public static void vibratePhone(Activity context) {
	
	Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
	
	v.vibrate(2000);
	
    }
    
    
    
    public static void playSound2() {
	
	final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_RING, 100);
	tg.startTone(ToneGenerator.TONE_PROP_BEEP);
	
    }
    
    
    
    public static void playSound4(Context context) {
	
	try {
	    
	    MediaPlayer mp = MediaPlayer.create(context, R.raw.alarm);
	    mp.start();
	    
	} catch (Exception e) {
	}
    }
    
    
    
    public static String getCadenaEspacio(int longitud) {
	
	String espacio = "";
	
	for (int i = 0; i < longitud - 1; i++) {
	    
	    espacio = espacio + " ";
	    
	}
	
	return espacio;
    }
    
    
    
    public static String ajustarNombre(String nombre, int l) {
	
	int iteraciones = nombre.length();
	
	if (nombre.length() >= l) {
	    
	    return nombre.substring(0, l);
	    
	}
	else {
	    
	    for (int i = 0; i < l - iteraciones; i++)
		nombre = nombre + " ";
	    return nombre;
	    
	}
	
    }
    
    
    
    public static String getDecimalFormat(float numero) {
	
	DecimalFormat myFormatter = new DecimalFormat("0");
	
	String output = myFormatter.format(numero);
	
	return output;
    }
    
    
    
    public static String getDecimalFormat(double numero) {
	
	DecimalFormat myFormatter = new DecimalFormat("0");
	
	String output = myFormatter.format(numero);
	
	return output;
    }
    
    
    
    public static void quickSortVector(Vector<String> v, int izq, int der) {
	
	int pivote;
	
	if (izq < der) {
	    
	    pivote = partirVector(v, izq, der);
	    quickSortVector(v, izq, pivote - 1);
	    quickSortVector(v, pivote + 1, der);
	}
    }
    
    
    
    private static int partirVector(Vector<String> v, int izq, int der) {
	
	String pivote = v.elementAt(izq).toString();
	String temporal;
	int primero = izq + 1;
	int ultimo = der;
	
	do {// Pivotear
	
	    while ((primero <= ultimo) && (v.elementAt(primero).toString().compareTo(pivote) <= 0))
		primero++;
	    
	    while ((primero <= ultimo) && (v.elementAt(ultimo).toString().compareTo(pivote) > 0))
		ultimo--;
	    
	    if (primero < ultimo) {
		
		temporal = v.elementAt(primero).toString();
		v.setElementAt(v.elementAt(ultimo).toString(), primero);
		v.setElementAt(temporal.toString(), ultimo);
		ultimo--;
		primero++;
	    }
	} while (primero <= ultimo);
	
	temporal = v.elementAt(izq).toString();
	v.setElementAt(v.elementAt(ultimo).toString(), izq);
	v.setElementAt(temporal, ultimo);
	
	return ultimo;
    }
    
    
    
    public static String CentrarLinea(String linea, int numSpace) {
	
	int space, longitud;
	String centrado;
	centrado = "";
	
	if (linea.length() > numSpace) {
	    
	    linea = linea.substring(0, numSpace);
	}
	
	longitud = linea.length() / 2;
	
	if (longitud % 2 != 0) {
	    
	    longitud++;
	}
	
	space = numSpace / 2;
	space = space - longitud;
	
	for (int i = 0; i < space; i++) {
	    
	    centrado = centrado + " ";
	}
	
	centrado = centrado + linea;
	
	for (int i = centrado.length(); i < numSpace; i++) {
	    
	    centrado = centrado + " ";
	}
	
	return centrado;
    }
    
    
    
    public static Vector<String> separarPalabras(String cadena, int cantCaracteres) {
	
	Vector<String> vPalabras = new Vector<String>();
	
	if (cadena.length() <= cantCaracteres) {
	    
	    vPalabras.add(cadena);
	}
	else {
	    
	    String aux = "";
	    String datos[] = cadena.split(" ");
	    
	    for (int i = 0; i < datos.length; i++) {
		
		if (aux.equals("")) {
		    
		    aux = datos[i];
		}
		else {
		    
		    if ((aux.length() + datos[i].length() + 1) > cantCaracteres) {
			
			vPalabras.add(aux);
			aux = datos[i];
		    }
		    else {
			
			aux += " " + datos[i];
		    }
		}
	    }
	    
	    if (aux.length() > 0)
		vPalabras.add(aux);
	}
	
	return vPalabras;
    }
    
    
    
    public static String replicate(int tam, String caract) {
	
	String aux = "";
	
	for (int i = 0; i < tam; i = i + (caract.length())) {
	    
	    aux += caract;
	}
	
	return aux;
    }
    
    
    
    public static String Formato_Fecha(String fecha) {
	String year = fecha.substring(0, 4);
	String month = fecha.substring(4, 6);
	String day = fecha.substring(6);
	
	fecha = year + "/" + month + "/" + day;
	
	return fecha;
    }
    
    
    
    public static String Formato_Fecha_Orden(String fecha) {
	String day = fecha.substring(0, 2);
	String month = fecha.substring(3, 5);
	String year = fecha.substring(6);
	
	fecha = year + "/" + month + "/" + day;
	
	return fecha;
    }
    
    
    
    public static int fechasDiferenciaEnDias(String fechaFinal, String fechaActual) {
	int yearAc = Integer.parseInt(fechaActual.substring(0, 4));
	int monthAc = Integer.parseInt(fechaActual.substring(5, 7)) - 1;
	int dayAc = Integer.parseInt(fechaActual.substring(8));
	int yearVenc = Integer.parseInt(fechaFinal.substring(0, 4));
	int monthVenc = Integer.parseInt(fechaFinal.substring(5, 7)) - 1;
	int dayVenc = Integer.parseInt(fechaFinal.substring(8));
	
	GregorianCalendar t1 = new GregorianCalendar(yearAc, monthAc, dayAc);
	GregorianCalendar t2 = new GregorianCalendar(yearVenc, monthVenc, dayVenc);
	
	// int dias = t1.get(Calendar.DAY_OF_YEAR) -
	// t2.get(Calendar.DAY_OF_YEAR);
	
	long dif = t1.getTimeInMillis() - t2.getTimeInMillis();
	int dias = (int) (dif / (1000 * 60 * 60 * 24));
	
	return dias;
    }
    
    
    
    public static String Pasar_Entero(String numero) {
	String valoruno;
	String valordos;
	int esta = numero.indexOf("e");
	String Numero = numero;
	
	if (esta == -1) {
	    Numero = numero;
	}
	else {
	    // Si el valor de numero sin e es de una sola cifra
	    if (esta == 1) {
		valoruno = "" + numero.charAt(0); // 1
	    }
	    else {
		valoruno = numero.substring(0, esta - 1); // 1.5
	    }
	    
	    valordos = numero.substring(esta + 2, numero.length()); // 06
	    int esta1 = valoruno.indexOf(".");
	    int contador = 1;
	    
	    if (esta1 == -1) {
		float a = Float.parseFloat(valoruno);
		int b = Integer.parseInt(valordos);
		
		for (int i = 0; i < b; i++) {
		    contador = contador * 10;
		}
		int resultado = (int) (a * contador);
		Numero = Integer.toString(resultado);
	    }
	    else {
		float a = Float.parseFloat(valoruno);
		int b = Integer.parseInt(valordos);
		
		for (int i = 0; i < b; i++) {
		    contador = contador * 10;
		}
		int resultado = (int) (a * contador);
		Numero = Integer.toString(resultado);
	    }
	}
	return Numero;
    }
    
    
    
    public static String Quitar_Cero(String numero) {
	int limite = numero.indexOf(".");
	
	if (limite == -1) {
	    
	    return numero;
	    
	}
	else {
	    
	    String numerosincero = numero.substring(0, limite);
	    
	    return numerosincero;
	    
	}
    }
    
    
    
    public static boolean estaActualLaFecha() {
	
	String fechaActual;
	boolean bien = false;
	
	fechaActual = FechaActual("yyyy/MM/dd");
	
	if (fechaActual.equals(Main.usuario.fechaLabores)) {
	    
	    bien = true;
	}
	
	// if(Const.tipoAplicacion == Const.AUTOVENTA){
	// bien = true;
	// }
	// else{
	
	// }
	return bien;
    }
    
    
    
    public static void mostrarToast(Context context, String mensaje) {
	
	Toast toast = Toast.makeText(context, mensaje, Toast.LENGTH_LONG);
	toast.setGravity(Gravity.CENTER, 0, 0);
	toast.show();
	
    }
    
    
    
    public static void cerrarTeclado(Activity context) {
	context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    
    
    
    public static boolean isNumberInteger(String number) {
	try {
	    Integer.parseInt(number);
	    return true;
	} catch (NumberFormatException e) {
	    return false;
	}
    }
    
    
    
    public static void quickSortListaDetalle(List<Detalle> lista, int izq, int der) {
	
	int pivote;
	
	if (izq < der) {
	    
	    pivote = partirListaDetalle(lista, izq, der);
	    quickSortListaDetalle(lista, izq, pivote - 1);
	    quickSortListaDetalle(lista, pivote + 1, der);
	}
    }
    
    
    
    private static int partirListaDetalle(List<Detalle> lista, int izq, int der) {
	
	Detalle pivote = lista.get(izq);
	Detalle temporal;
	int primero = izq + 1;
	int ultimo = der;
	
	do {// Pivotear
	
	    while ((primero <= ultimo) && (lista.get(primero).codLinea.compareTo(pivote.codLinea) <= 0))
		primero++;
	    
	    while ((primero <= ultimo) && (lista.get(ultimo).codLinea.compareTo(pivote.codLinea) > 0))
		ultimo--;
	    
	    if (primero < ultimo) {
		
		temporal = lista.get(primero);
		lista.set(primero, lista.get(ultimo));
		lista.set(ultimo, temporal);
		ultimo--;
		primero++;
	    }
	} while (primero <= ultimo);
	
	temporal = lista.get(izq);
	lista.set(izq, lista.get(ultimo));
	lista.set(ultimo, temporal);
	
	return ultimo;
    }
    
    public static String ObtenerFechaId(String formato) {
	
	Date date = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat(formato);
	return sdf.format(date);
    }
    
    /**
     * Devuelve el numero imei del movil
     * 
     * @param context
     * @return
     */
    public static String obtenerImei(Context context) {
	TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	return manager.getDeviceId();
    }
    
	/**
	 * Metodo que muesrta un AlertDialog y al aceptar finaliza la actividad
	 * @param context
	 * @param mensaje
	 */
	public static void mostrarAlertDialogCerrarActivity(final Context context, String mensaje) {
    	
		AlertDialog alertDialog;
			
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int id) {
				
				dialog.cancel();
				
				try {
					((Activity)context).finish();
				} catch (Exception e) {}
			}
		});

		alertDialog = builder.create();
    	alertDialog.setMessage(mensaje);
    	alertDialog.show();
    }
	
	public static boolean validarAccesoGPS(LocationManager locationManager){
		
		boolean activado = false;
		boolean activado2 = false;
		boolean acceso = false;

		if (locationManager != null) {

			activado  = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			activado2 = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		}
		
		if (activado && activado2) 
			acceso = true;
		
		return acceso;
	}
    
	
	public static double validarRangoGeoCerca(double latitudCliente, double longitudCliente, Cliente coordenada) {
		
		double distance = -1;

        if (latitudCliente != 0 && longitudCliente != 0) {

            if(coordenada != null){

                if (coordenada.latitud != 0 && coordenada.longitud != 0) {



                    double lonAtual  = coordenada.latitud;
                    double latActual = coordenada.longitud;


                    Location locationA = new Location("pointclient");
                    locationA.setLatitude(latitudCliente);
                    locationA.setLongitude(longitudCliente);
                    
                    Location locationB = new Location("pointcurrent");
                    locationB.setLatitude(latActual);
                    locationB.setLongitude(lonAtual);
                    
                    distance = locationA.distanceTo(locationB);

                    float[] results = new float[5];
                    Location.distanceBetween(latitudCliente, longitudCliente, latActual, lonAtual, results);

                    for (int i = 0; i < results.length; i++) {
                        Log.i(TAG, " +++++++ results: ***" + results[i]);
                    }

                    Log.i(TAG, "data coordenada cliente LAT: ***" + latitudCliente + " LONG: " + longitudCliente);
                    Log.i(TAG, "data coordenada actuales LAT: ***" + latActual + " LONG: " + lonAtual);
                    Log.i(TAG, "DISTANCE TO: ***" + distance);

                } else {
                    distance = -99;
                }

            }


        }

        return distance;
        
	}
	
	
	public static boolean validarDistanciaMaximaGeoCerca(double diff) {
		
		double distMax = DataBaseBO.obtenerDistanciaMaxima();
        Log.i("DISTANCE MAXIMA: ", distMax + "");
        Log.i("DISTANCE DIFF: ", diff + "");

        if (diff > distMax || diff < 0) {
            return false;
        } else {
            return true;
        }
		
	}
	
	
    
}
