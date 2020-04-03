package co.com.panificadoracountry;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import co.com.DataObject.Cartera;
import co.com.DataObject.Cliente;
import co.com.DataObject.Usuario;

public final class Main {
	
	public static boolean activoGPS = false;
	public static String tipoVenta = "T";
	public static ProgressDialog pdialog;
	public static Context contextoActual;
	public static String versionApp;
	public static String deviceId;
	
	//Referencia a los Formularios (Activity)
	public static Intent formOpcionesPedido;
	public static Intent formOpcionesPedido2;

	public static Drawable fotoActual;
	public static boolean guardarFoto = false;
	public static Usuario usuario;
	public static Cliente cliente;
	public static Cartera cartera;


	///////////////////////MERCADEO///////////////////////////////////////
		
	public static boolean realizoMercadeo;
	public static boolean hizoMercadeo;
	public static boolean ingreso_x_informe_noCompra;
	public static String horaInicial;
	
	//Variables para conservar ultima busqueda
	public static String codBusqProductos;
	public static int posOpBusqProductos;
	public static int posOpLineas;
	public static boolean primeraVez;

	
	//Variables Busqueda
	public static int posSpBuspProd;
	public static String cadBusqueda;

	
	static 
	{	
		usuario       = new Usuario();
		cliente      = new Cliente();
		cartera      = new Cartera();

	}
}
