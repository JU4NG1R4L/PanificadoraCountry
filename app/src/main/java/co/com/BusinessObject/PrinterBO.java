package co.com.BusinessObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.content.pm.FeatureInfo;
import android.widget.TextView;
import co.com.DataObject.AutoCuadre;
import co.com.DataObject.Cliente;
import co.com.DataObject.Detalle;
import co.com.DataObject.DetalleRecaudo;
import co.com.DataObject.Encabezado;
import co.com.DataObject.EncabezadoRecaudo;
import co.com.DataObject.Impresion;
import co.com.DataObject.ImpresionCliente;
import co.com.DataObject.ImpresionDetalle;
import co.com.DataObject.InformeInventario;
import co.com.DataObject.InformeInventario2;
import co.com.DataObject.Inventario;
import co.com.DataObject.Kardex;
import co.com.panificadoracountry.Const;
import co.com.panificadoracountry.Main;
import co.com.panificadoracountry.Util;


public class PrinterBO {

	private static int cantEnter;
	public static final String TAG = "BusinessObject.PrinterBO";

	static String N0 = "! U1 SETBOLD 0";
	static String N2 = "! U1 SETBOLD 2";

	static String SUB_TOTAL = "SubTotal";

	/**
	 * Cantidad de Caracteres Minimo para la Columna SubTotal.
	 **/
	static int MIN_SUB_TOTAL = SUB_TOTAL.length() + 4;

	/**
	 * Cantidad de Caracteres Maximo para la Columna Cantidad.
	 **/
	static int MAX_CANTIDAD = 8;

	public static String formatoPruebaExtech() {

		String aux;		
		String strPrint; 

		char ret1 = 13;
		char ret2 = 10;
		char ESC  = 27;
		char XON  = 17;
		char FF   = 12;
		char menos = 45;
		char GS = 29;
		char admiracion = 33;
		
		char altoLetraSuperior = 17;
		char altoLetraNormal = 0;
		char altoLetra2 = 2;
		char altoLetra1 = 1;

		String normal   = ESC + "U" + "0";
		String negrilla = ESC + "U" + "1";
		String subrayado = ESC + "-" + "1";
		String desSubraydo = ESC + "-" + "0";
		String letraAlta = ""+GS + admiracion + altoLetraSuperior;
		String letraTamano2 = ""+GS + admiracion + altoLetra2;
		String letraNormal = ""+GS + admiracion + altoLetraNormal;
		String letraTamano1 = ""+GS + admiracion + altoLetra1;

		int i;
		String enter = String.valueOf(ret1) + String.valueOf(ret2);
 
		strPrint = "";
		//strPrint += ESC + FF;
		strPrint += normal;    

		strPrint += Util.CentrarLinea("APLICACION ESTANDAR", 42) + enter;
		strPrint += subrayado;
		strPrint += Util.CentrarLinea("DESARROLLADA POR CELUWEB.COM", 42) + enter;
		strPrint += desSubraydo;
		strPrint += Util.CentrarLinea("IMPRESION DE PRUEBA APP ESTANDAR", 42) + enter;
		strPrint += Util.CentrarLinea("IMPRESO DESDE DISPOSITIVO ANDROID", 42) + enter + enter;

		
		
		
		strPrint += letraAlta;

		
		//strPrint += Util.CentrarLinea("ESTO DEBE SALIR EN NEGRILLA", 42) + enter + enter;
		strPrint += "ESTO DEBE SALIR EN NEGRILLA" + enter + enter;
		
		strPrint += letraTamano2;
		
		strPrint += "ESTO DEBE SALIR EN TAMANO 2" + enter + enter;
		
	    strPrint += letraTamano1;
		
		strPrint += "ESTO DEBE SALIR EN TAMANO 1" + enter + enter;
		
		strPrint += letraNormal;

		strPrint += "IMPRESION SATISFACTORIA" + enter;
		aux = "";
		strPrint += aux + enter;

		for (i = 0; i < 3; i++) {
			strPrint += enter;
		}

		return strPrint;
	}


	public static String formatoVenta( String numeroDoc, int copia ) {

		String strPrint; 

		char ret1 = 13;
		char ret2 = 10;
		char ESC  = 27;
		char XON  = 17;
		String enter = String.valueOf(ret1) + String.valueOf(ret2);
		String K4  = ESC + "k" + "4"; //COURIER FONT 42
		String K5  = ESC + "k" + "5"; //COURIER FONT 48
		String K6  = ESC + "k" + "6"; //COURIER FONT 48
		String K10 = ESC + "k" + "10"; //COURIER FONT 48
		String K11 = ESC + "k" + "11"; //COURIER FONT 48
		String K15 = ESC + "k" + "15"; //COURIER FONT 48
		String K9  = ESC + "k" + "9"; //COURIER FONT 48
		String K8  = ESC + "k" + "8"; //COURIER FONT 48
		String K7  = ESC + "k" + "7"; //COURIER FONT 48
		String normal   = ESC + "U" + "0";
		String negrilla = ESC + "U" + "1";
		String subrayado = ESC + "-" + "1";
		String desSubraydo = ESC + "-" + "0";
		char GS = 29;
		char admiracion = 33;
		char altoLetraSuperior = 17;
		char altoLetraNormal = 0;
		char altoLetra1 = 1;
		String letraAlta = ""+GS + admiracion + altoLetraSuperior;
		String letraNormal = ""+GS + admiracion + altoLetraNormal;
		String letraTamano1 = ""+GS + admiracion + altoLetra1;
		strPrint = "" + XON;
		strPrint += "" + enter;
		LinkedHashMap<String, Float> detalleIva = new LinkedHashMap<String, Float>();				
		Impresion impresion = DataBaseBO.getImpresion();
		strPrint += "" + enter;

		ImpresionCliente impC = null;

		if (impresion != null) {

			strPrint += Util.CentrarLinea( impresion.razonSocial , 42 ) + enter;
			strPrint += Util.CentrarLinea( "NIT " + impresion.nit, 42 ) + enter;
			strPrint += Util.CentrarLinea( impresion.autoRetenedor, 42 ) + enter;
			strPrint += Util.CentrarLinea( impresion.fecha_Autoretenedor, 42 ) + enter;
			strPrint += Util.CentrarLinea( impresion.regimen, 42 ) + enter;
			strPrint += Util.CentrarLinea( impresion.direccion, 42 ) + enter;
			strPrint += Util.CentrarLinea( impresion.ciudad, 42 ) + enter;

			strPrint += "RESOLUCION DE FACTURACION: " + impresion.numResolucion + enter;
			strPrint += "RANGO AUTORIZADO: " + impresion.rangoInicial + " - " + impresion.rangoFinal + enter;

			if( copia == 0 ){

				strPrint += "FACTURA DE VENTA: " + enter;
			}
			else{

				strPrint += "COPIA FACTURA DE VENTA: " + enter;
			}

			impC = DataBaseBO.getImpresionCliente( numeroDoc );

			if( impC != null ){

				strPrint += letraTamano1;
				strPrint += "NRO: " + impC.numFactura + enter;
				strPrint += letraNormal;
				strPrint += "CLIENTE: " + impC.codigo + enter;

				if( !impC.nombre.equals( "" ) )
					strPrint += impC.nombre + enter;
				
				strPrint += letraTamano1;
				
				if( !impC.razonSocial.equals( "" ) )
					strPrint += impC.razonSocial + enter;
				
				strPrint += letraNormal;
				strPrint += "NIT: " + impC.nit + enter;
				strPrint += "FECHA: " + impC.fecha + enter;
			}

			strPrint += "VEND: " + Main.usuario.codigoVendedor + enter;
			strPrint += Util.lpad("", 42, "-") + enter;

		}        

		strPrint += Util.rpad("PRODUCTO", 24, " ") + Util.rpad("Iva", 5, " ") + Util.rpad("Precio", 8, " ")  + Util.rpad("Cant", 4, " ") + enter;
		strPrint += Util.lpad("", 42, "-") + enter;
		Vector<ImpresionDetalle> vImpDet = DataBaseBO.getImpresionDetalle( numeroDoc );

		int totalCant = 0;

		float subTotalImp  = 0;
		float descuentoImp = 0;
		float ivaImp       = 0;

		ImpresionDetalle detalle;

		if ( vImpDet.size() > 0 ) {

			int maxCant = 0;
			int maxSubTotal = 0;
			Enumeration< ImpresionDetalle > elements = vImpDet.elements();

			while (elements.hasMoreElements()) {

				detalle = elements.nextElement();
				strPrint += Util.rpad( detalle.nombre, 24, " ") + Util.rpad( Util.Redondear( "" + detalle.iva , 0), 5, " ") + Util.rpad( Util.Redondear( "" + detalle.precio, 0 ), 8, " ")  + Util.lpad( Util.Redondear( "" + detalle.cantidad, 1 ) , 5, " ") + enter;
				totalCant += detalle.cantidad;
				String strCant = "" + detalle.cantidad;

				if (strCant.length() > maxCant)
					maxCant = strCant.length();

				float sub_total       = 0;
				float valor_descuento = 0;
				float valor_iva       = 0;    			    			

				sub_total       = detalle.cantidad * detalle.precio;
				
				if (detalle.descuento_producto > 0) {

					float descuentoOperar = 0;

					descuentoOperar = detalle.descuento_producto
							+ detalle.descuento;
					
					valor_descuento = sub_total * descuentoOperar / 100;

				} else {
					valor_descuento = sub_total * detalle.descuento / 100;
				}
				
				valor_iva       = (sub_total - valor_descuento) * (detalle.iva / 100);

				subTotalImp  += sub_total;
				descuentoImp += valor_descuento;
				ivaImp       += valor_iva;    			
			}
		}

		strPrint += Util.lpad("", 42, "-") + enter;

		String strSubTotal  = Util.SepararMilesSin(Util.Redondear(Util.QuitarE("" + subTotalImp), 0));
		String strDescuento = Util.SepararMilesSin(Util.Redondear(Util.QuitarE("" + descuentoImp), 0));
		String strIva       = Util.SepararMilesSin(Util.Redondear(Util.QuitarE("" + ivaImp), 0));
		String strBaseGravable = Util.SepararMilesSin(Util.Redondear(Util.QuitarE("" + ( subTotalImp - descuentoImp )), 0));

		float neto     = subTotalImp + ivaImp - descuentoImp;
		String str_valor_neto = Util.SepararMilesSin(Util.Redondear(Util.QuitarE("" + neto), 0));

	
		strPrint += Util.rpad( "SUBTOTAL", 30, " ") + Util.lpad( Util.SepararMiles( Util.Redondear( "" + subTotalImp , 0)  ), 12, " ")  + enter;
		strPrint += Util.rpad( "DESCUENTO", 30, " ") + Util.lpad( Util.SepararMiles( Util.Redondear( "" + descuentoImp, 0) ), 12, " ")  + enter;

		strPrint += Util.rpad( "IVA ", 30, " ") + Util.lpad( Util.SepararMiles( "" + 0 ), 12, " ")  + enter;
		strPrint += Util.lpad("", 42, "-") + enter;
		strPrint += letraAlta;
		strPrint += Util.rpad( "TOTAL", 11, " ") + Util.lpad( Util.SepararMiles( Util.Redondear( "" + neto, 0) ), 10, " ")  + enter;
		strPrint += letraNormal;
		strPrint += Util.lpad("", 42, "-") + enter;
		strPrint += Util.lpad("", 42, "-") + enter;  

		vImpDet = null;

		if (impC != null) {

			if (!impC.observacion.equals("")) {

				strPrint += Util.lpad("", 42, "-") + enter;
				strPrint += "Observaciones: " + impC.observacion + enter;

			}
		}


		strPrint += Util.lpad("", 42, "-") + enter; 
		Vector< String > vMsj = Util.separarPalabras( impresion.msjComprador , 42);

		for( int i = 0; i < vMsj.size(); i++ ){

			strPrint += vMsj.elementAt( i ) + enter;
		}
		
		strPrint += Util.lpad("", 42, "-") + enter; 
		
		Vector<String> vMsjDian = Util.separarPalabras(impresion.msjDian, 42);
		for (int i = 0; i < vMsjDian.size(); i++) {
			
			strPrint += vMsjDian.elementAt( i ) + enter;
			
		}

		for (int i = 0; i < 4; i++) {
			strPrint += enter;
		}

		strPrint += Util.lpad("", 42, "-") + enter; 

		strPrint += "Aceptada Cliente." + enter;
		strPrint += "Firma" + enter;
		strPrint += "C.C." + enter;

		for (int i = 0; i < 4; i++) {
			strPrint += enter;
		}

		return strPrint;
	}
	
	public static String formatoDevolucion( String numeroDoc, int copia ) {

		String strPrint; 

		char ret1 = 13;
		char ret2 = 10;
		char ESC  = 27;
		char XON  = 17;

		String enter = String.valueOf(ret1) + String.valueOf(ret2);

		String K4  = ESC + "k" + "4"; //COURIER FONT 42
		String K5  = ESC + "k" + "5"; //COURIER FONT 48
		String K6  = ESC + "k" + "6"; //COURIER FONT 48
		String K10 = ESC + "k" + "10"; //COURIER FONT 48
		String K11 = ESC + "k" + "11"; //COURIER FONT 48
		String K15 = ESC + "k" + "15"; //COURIER FONT 48
		String K9  = ESC + "k" + "9"; //COURIER FONT 48
		String K8  = ESC + "k" + "8"; //COURIER FONT 48
		String K7  = ESC + "k" + "7"; //COURIER FONT 48

		String normal   = ESC + "U" + "0";
		String negrilla = ESC + "U" + "1";

		String subrayado = ESC + "-" + "1";
		String desSubraydo = ESC + "-" + "0";
		
		
		char GS = 29;
		char admiracion = 33;
		
		char altoLetraSuperior = 17;
		char altoLetraNormal = 0;
		char altoLetra1 = 1;
		
		
		String letraAlta = ""+GS + admiracion + altoLetraSuperior;
		String letraNormal = ""+GS + admiracion + altoLetraNormal;
		
		String letraTamano1 = ""+GS + admiracion + altoLetra1;
		

		strPrint = "" + XON;
		//strPrint += ESC + "F" + "1";
		strPrint += "" + enter;

		if( Main.usuario == null || Main.usuario.codigoVendedor == null )
			Main.usuario = DataBaseBO.ObtenerUsuario();

		LinkedHashMap<String, Float> detalleIva = new LinkedHashMap<String, Float>();				

		Impresion impresion = DataBaseBO.getImpresion();

		//strPrint += K4;
		strPrint += "" + enter;

		ImpresionCliente impC = null;

		if (impresion != null) {

			strPrint += Util.CentrarLinea( impresion.razonSocial , 42 ) + enter;
			strPrint += Util.CentrarLinea( "NIT " + impresion.nit, 42 ) + enter;
			//strPrint += Util.CentrarLinea( impresion.autoRetenedor, 42 ) + enter;
			//strPrint += Util.CentrarLinea( impresion.fecha_Autoretenedor, 42 ) + enter;
			//strPrint += Util.CentrarLinea( impresion.regimen, 42 ) + enter;
			strPrint += Util.CentrarLinea( impresion.direccion, 42 ) + enter;
			strPrint += Util.CentrarLinea( impresion.ciudad, 42 ) + enter;

			//strPrint += "RESOLUCION DE FACTURACION: " + impresion.numResolucion + enter;

			//strPrint += "RANGO AUTORIZADO: " + impresion.rangoInicial + " - " + impresion.rangoFinal + enter;

			if( copia == 0 ){

				strPrint += "NOTA DE DEVOLUCION: " + enter;
			}
			else{

				strPrint += "COPIA NOTA DE DEVOLUCION: " + enter;
			}

			impC = DataBaseBO.getImpresionCliente( numeroDoc );

			if( impC != null ){

				//strPrint += subrayado;
				
				strPrint += letraTamano1;


				strPrint += "NRO: " + impC.numFactura + enter;
				
				
				 strPrint += letraNormal;
				

				strPrint += "CLIENTE: " + impC.codigo + enter;

				if( !impC.nombre.equals( "" ) )
					strPrint += impC.nombre + enter;
				
				
				strPrint += letraTamano1;

				if( !impC.razonSocial.equals( "" ) )
					strPrint += impC.razonSocial + enter;
				
				strPrint += letraNormal;
				

				//strPrint += desSubraydo;

				strPrint += "NIT: " + impC.nit + enter;
				//strPrint += "FECHA: " + Util.FechaActual( "yyyy-MM-dd HH:mm:ss" ) + enter;
				strPrint += "FECHA: " + impC.fecha + enter;
			}

			strPrint += "VEND: " + Main.usuario.codigoVendedor + enter;

			strPrint += Util.lpad("", 42, "-") + enter;

		}        

		strPrint += Util.rpad("PRODUCTO", 24, " ") + Util.rpad("Iva", 5, " ") + Util.rpad("Precio", 8, " ")  + Util.rpad("Cant", 4, " ") + enter;

		strPrint += Util.lpad("", 42, "-") + enter;

		Hashtable<Integer, Vector<ImpresionDetalle>> hVImpDet = new Hashtable<Integer, Vector<ImpresionDetalle>>();//DataBaseBO.getImpresionDetalle( numeroDoc );

		Vector< ImpresionDetalle > vImpDet = null;
		Vector< ImpresionDetalle > vImpDetVenc = null;
		Vector< ImpresionDetalle > vImpDetCali = null;

		if( hVImpDet.containsKey( Const.PEDIDO_VENTA ) ){

			vImpDet = hVImpDet.get( Const.PEDIDO_VENTA );
		}
		else{

			vImpDet = new Vector<ImpresionDetalle>();
		}
		
		
		
		if( hVImpDet.containsKey( Const.PEDIDO_CAMBIO ) ){

			vImpDetVenc = hVImpDet.get( Const.PEDIDO_CAMBIO );
		}
		else{

			vImpDetVenc = new Vector<ImpresionDetalle>();
		}
		

		if( hVImpDet.containsKey( Const.PEDIDO_INVENTARIO ) ){

			vImpDetCali = hVImpDet.get( Const.PEDIDO_INVENTARIO );
		}
		else{

			vImpDetCali = new Vector<ImpresionDetalle>();
		}




		int totalCant = 0;

		Map<Integer, Float> bGravable = new HashMap<Integer, Float>();

		Vector< Float > vIva = DataBaseBO.getIvaProductos();

		for( int i = 0; i < vIva.size(); i++ ){

			int qIva = vIva.elementAt( i ).intValue();
			float bValores = 0;
			bGravable.put( qIva , bValores );
		}

		float subTotalImp  = 0;
		float descuentoImp = 0;
		float ivaImp       = 0;

		ImpresionDetalle detalle;

		if ( vImpDet.size() > 0 ) {

			int maxCant = 0;
			int maxSubTotal = 0;
			Enumeration< ImpresionDetalle > elements = vImpDet.elements();

			while (elements.hasMoreElements()) {

				detalle = elements.nextElement();

				strPrint += Util.rpad( detalle.nombre, 24, " ") + Util.rpad( Util.Redondear( "" + detalle.iva , 0), 5, " ") + Util.rpad( Util.Redondear( "" + detalle.precio, 0 ), 8, " ")  + Util.lpad( Util.Redondear( "" + detalle.cantidad, 1 ) , 5, " ") + enter;

				totalCant += detalle.cantidad;

				String strCant = "" + detalle.cantidad;

				if (strCant.length() > maxCant)
					maxCant = strCant.length();

				float sub_total       = 0;
				float valor_descuento = 0;
				float valor_iva       = 0;    			    			

				sub_total       = detalle.cantidad * detalle.precio;
				valor_descuento = sub_total * detalle.descuento / 100;
				valor_iva       = (sub_total - valor_descuento) * (detalle.iva / 100);


				Float totalIva = detalleIva.get("" + (int) detalle.iva);

				if( detalle.iva > 0 ){

					//Guardamos los valores gravables de la factura
					if( bGravable.containsKey( (int) detalle.iva ) ){

						float bValores;// = new float[ 2 ];
						bValores = bGravable.get( ( int ) detalle.iva );
						bValores += sub_total - valor_descuento;
						bGravable.put( (int) detalle.iva , bValores);
					}
					else{

						float bValores = 0;
						bValores = sub_total - valor_descuento;
						bGravable.put( (int) detalle.iva , bValores);
					}
				}

				if (totalIva == null) {

					totalIva = valor_iva;
					detalleIva.put("" + (int )detalle.iva, totalIva);

				} else {

					totalIva += valor_iva;
					detalleIva.put("" + (int) detalle.iva, totalIva);
				}

				subTotalImp  += sub_total;
				descuentoImp += valor_descuento;
				ivaImp       += valor_iva;    			
			}
		}
		
		
		
		
		if ( vImpDetVenc.size() > 0 ) {

			int maxCant = 0;
			int maxSubTotal = 0;
			Enumeration< ImpresionDetalle > elements = vImpDetVenc.elements();

			while (elements.hasMoreElements()) {

				detalle = elements.nextElement();

				strPrint += Util.rpad( detalle.nombre, 24, " ") + Util.rpad( Util.Redondear( "" + detalle.iva , 0), 5, " ") + Util.rpad( Util.Redondear( "" + detalle.precio, 0 ), 8, " ")  + Util.lpad( Util.Redondear( "" + detalle.cantidad, 1 ) , 5, " ") + enter;

				totalCant += detalle.cantidad;

				String strCant = "" + detalle.cantidad;

				if (strCant.length() > maxCant)
					maxCant = strCant.length();

				float sub_total       = 0;
				float valor_descuento = 0;
				float valor_iva       = 0;    			    			

				sub_total       = detalle.cantidad * detalle.precio;
				valor_descuento = sub_total * detalle.descuento / 100;
				valor_iva       = (sub_total - valor_descuento) * (detalle.iva / 100);


				Float totalIva = detalleIva.get("" + (int) detalle.iva);

				if( detalle.iva > 0 ){

					//Guardamos los valores gravables de la factura
					if( bGravable.containsKey( (int) detalle.iva ) ){

						float bValores;// = new float[ 2 ];
						bValores = bGravable.get( ( int ) detalle.iva );
						bValores += sub_total - valor_descuento;
						bGravable.put( (int) detalle.iva , bValores);
					}
					else{

						float bValores = 0;
						bValores = sub_total - valor_descuento;
						bGravable.put( (int) detalle.iva , bValores);
					}
				}

				if (totalIva == null) {

					totalIva = valor_iva;
					detalleIva.put("" + (int )detalle.iva, totalIva);

				} else {

					totalIva += valor_iva;
					detalleIva.put("" + (int) detalle.iva, totalIva);
				}

				subTotalImp  += sub_total;
				descuentoImp += valor_descuento;
				ivaImp       += valor_iva;    			
			}
		}
		
		
		
		
		if ( vImpDetCali.size() > 0 ) {

			int maxCant = 0;
			int maxSubTotal = 0;
			Enumeration< ImpresionDetalle > elements = vImpDetCali.elements();

			while (elements.hasMoreElements()) {

				detalle = elements.nextElement();

				strPrint += Util.rpad( detalle.nombre, 24, " ") + Util.rpad( Util.Redondear( "" + detalle.iva , 0), 5, " ") + Util.rpad( Util.Redondear( "" + detalle.precio, 0 ), 8, " ")  + Util.lpad( Util.Redondear( "" + detalle.cantidad, 1 ) , 5, " ") + enter;

				totalCant += detalle.cantidad;

				String strCant = "" + detalle.cantidad;

				if (strCant.length() > maxCant)
					maxCant = strCant.length();

				float sub_total       = 0;
				float valor_descuento = 0;
				float valor_iva       = 0;    			    			

				sub_total       = detalle.cantidad * detalle.precio;
				valor_descuento = sub_total * detalle.descuento / 100;
				valor_iva       = (sub_total - valor_descuento) * (detalle.iva / 100);


				Float totalIva = detalleIva.get("" + (int) detalle.iva);

				if( detalle.iva > 0 ){

					//Guardamos los valores gravables de la factura
					if( bGravable.containsKey( (int) detalle.iva ) ){

						float bValores;// = new float[ 2 ];
						bValores = bGravable.get( ( int ) detalle.iva );
						bValores += sub_total - valor_descuento;
						bGravable.put( (int) detalle.iva , bValores);
					}
					else{

						float bValores = 0;
						bValores = sub_total - valor_descuento;
						bGravable.put( (int) detalle.iva , bValores);
					}
				}

				if (totalIva == null) {

					totalIva = valor_iva;
					detalleIva.put("" + (int )detalle.iva, totalIva);

				} else {

					totalIva += valor_iva;
					detalleIva.put("" + (int) detalle.iva, totalIva);
				}

				subTotalImp  += sub_total;
				descuentoImp += valor_descuento;
				ivaImp       += valor_iva;    			
			}
		}
		
		
		
		

		strPrint += Util.lpad("", 42, "-") + enter;

		String strSubTotal  = Util.SepararMilesSin(Util.Redondear(Util.QuitarE("" + subTotalImp), 0));
		String strDescuento = Util.SepararMilesSin(Util.Redondear(Util.QuitarE("" + descuentoImp), 0));
		String strIva       = Util.SepararMilesSin(Util.Redondear(Util.QuitarE("" + ivaImp), 0));
		String strBaseGravable = Util.SepararMilesSin(Util.Redondear(Util.QuitarE("" + ( subTotalImp - descuentoImp )), 0));

		float neto     = subTotalImp + ivaImp - descuentoImp;
		String str_valor_neto = Util.SepararMilesSin(Util.Redondear(Util.QuitarE("" + neto), 0));

		//Ordenamos el Hashtable de los valores de base gravable
		Map<Integer, Float> mBGravable = sortByKeys( bGravable );

		strPrint += Util.rpad( "SUBTOTAL", 30, " ") + Util.lpad( Util.SepararMiles( Util.Redondear( "" + subTotalImp , 0)  ), 12, " ")  + enter;
		strPrint += Util.rpad( "DESCUENTO", 30, " ") + Util.lpad( Util.SepararMiles( Util.Redondear( "" + descuentoImp, 0) ), 12, " ")  + enter;

		int qIva = 0;

		if( !vIva.isEmpty() ){

			for( int i = 0; i < vIva.size(); i++ ){

				qIva = vIva.elementAt( i ).intValue();

				if( detalleIva.containsKey( "" + qIva ) ){

					float iva = detalleIva.get( "" + qIva );
					strPrint += Util.rpad( "IVA " + qIva + "%", 30, " ") + Util.lpad( Util.SepararMiles( Util.Redondear( "" + iva, 0) ), 12, " ")  + enter;
				}
				else{

					strPrint += Util.rpad( "IVA " + qIva + "%", 30, " ") + Util.lpad( Util.SepararMiles( Util.Redondear( "0", 0) ), 12, " ")  + enter;
				}
			}
		}
		else{

			strPrint += Util.rpad( "IVA ", 30, " ") + Util.lpad( Util.SepararMiles( "" + 0 ), 12, " ")  + enter;
		}

		strPrint += Util.lpad("", 42, "-") + enter;
		
		
		strPrint += letraAlta;
		
		strPrint += Util.rpad( "TOTAL", 11, " ") + Util.lpad( Util.SepararMiles( Util.Redondear( "" + neto, 0) ), 10, " ")  + enter;
		
		strPrint += letraNormal;

		strPrint += Util.lpad("", 42, "-") + enter;

		Iterator myVeryOwnIterator = mBGravable.keySet().iterator();

		while(myVeryOwnIterator.hasNext()) {

			int key  = (Integer) myVeryOwnIterator.next();
			float value = (float) mBGravable.get(key);    	    

			String strBGravable = Util.SepararMilesSin(Util.Redondear(Util.QuitarE("" + ( value )), 0));

			strPrint += Util.rpad( "BASE IVA "+ key +"%", 30, " ") + Util.lpad( strBGravable , 12, " ") + enter;
		}

		strPrint += Util.lpad("", 42, "-") + enter;  

		vImpDet = null;

		boolean colocarLinea = false;

		if( hVImpDet.containsKey( Const.PEDIDO_CAMBIO ) ){

			vImpDet = hVImpDet.get( Const.PEDIDO_CAMBIO );
		}
		else{

			vImpDet = new Vector<ImpresionDetalle>();
		}

		for( int i = 0; i < vImpDet.size(); i++ ){

			if( i == 0 ){

				strPrint += "CAMBIOS VENCIDOS" + enter;
				colocarLinea = true;
			}

			detalle = vImpDet.get( i );

			strPrint += Util.rpad( detalle.nombre, 36, " ") + Util.lpad( Util.Redondear( "" + detalle.cantidad, 1 ), 6, " " ) + enter;
		}

		vImpDet = null;

		if( hVImpDet.containsKey( Const.PEDIDO_INVENTARIO ) ){

			vImpDet = hVImpDet.get( Const.PEDIDO_INVENTARIO );
		}
		else{

			vImpDet = new Vector<ImpresionDetalle>();
		}

		for( int i = 0; i < vImpDet.size(); i++ ){

			if( i == 0 ){

				strPrint += Util.lpad("", 21, "-") + enter;

				strPrint += "CAMBIOS CALIDAD" + enter;
				colocarLinea = true;
			}

			detalle = vImpDet.get( i );

			strPrint += Util.rpad( detalle.nombre, 36, " ") + Util.lpad( Util.Redondear( "" + detalle.cantidad, 1 ), 6, " " ) + enter;
		}

		vImpDet = null;

		if( hVImpDet.containsKey( Const.PEDIDO_PROMOCION ) ){

			vImpDet = hVImpDet.get( Const.PEDIDO_PROMOCION );
		}
		else{

			vImpDet = new Vector<ImpresionDetalle>();
		}

		for( int i = 0; i < vImpDet.size(); i++ ){

			if( i == 0 ){

				strPrint += Util.lpad("", 21, "-") + enter;

				strPrint += "PROMOCIONES" + enter;
				colocarLinea = true;
			}

			detalle = vImpDet.get( i );

			strPrint += Util.rpad( detalle.nombre, 36, " ") + Util.lpad(Util.Redondear( "" + detalle.cantidad, 1 ), 6, " " ) + enter;
		}

		if( impC != null ){

			if( !impC.observacion.equals( "" ) ){

				strPrint += Util.lpad("", 42, "-") + enter;
				strPrint += "Observaciones: " + impC.observacion + enter;
				colocarLinea = true;
			}
		}


		if( colocarLinea )
			strPrint += Util.lpad("", 42, "-") + enter; 


		/*Vector< String > vMsj = Util.separarPalabras( impresion.msjComprador , 42);

		for( int i = 0; i < vMsj.size(); i++ ){

			strPrint += vMsj.elementAt( i ) + enter;
		}*/

		for (int i = 0; i < 4; i++) {
			strPrint += enter;
		}

		strPrint += Util.lpad("", 42, "-") + enter; 

		strPrint += "Aceptada Cliente." + enter;
		strPrint += "Firma" + enter;
		strPrint += "C.C." + enter;

		for (int i = 0; i < 4; i++) {
			strPrint += enter;
		}


		return strPrint;
	}

	public static String formatoInventario() {

		String aux;		
		String strPrint; 

		char ret1 = 13;
		char ret2 = 10;
		char ESC  = 27;
		char XON  = 17;

		String normal   = ESC + "U" + "0";
		String negrilla = ESC + "U" + "1";

		String enter = String.valueOf(ret1) + String.valueOf(ret2);

		strPrint = "" + XON;
		strPrint += ESC + "F" + "1";
		strPrint += "" + enter;

		strPrint += "Inventario" + enter;
		strPrint += "Fecha: " + Util.FechaActual("yyyy-MM-dd") + enter;

		strPrint += Util.lpad("", 42, "-") + enter;

		strPrint += Util.rpad("PRODUCTO", 10, " ") + Util.lpad("CANT", 8, " ")    + Util.lpad("CANT", 8, " ")  + Util.lpad("VENTAS", 8, " ") + Util.lpad("DEV", 8, " ") + enter;
		strPrint += Util.rpad("", 10, " ")         + Util.lpad("INICIAL", 8, " ") + Util.lpad("FINAL", 8, " ") + Util.lpad("", 8, " ") + enter; 

		strPrint += Util.lpad("", 42, "-") + enter;

		Vector<InformeInventario> lInformeInv = DataBaseBO.CargarInformeInventario();
		InformeInventario infoInv;

		for( int i = 0; i < lInformeInv.size(); i++ ){

			infoInv = lInformeInv.elementAt( i );

			strPrint += Util.rpad( infoInv.codigo, 10, " ") + Util.lpad( "" + infoInv.invInicial, 8, " ") + Util.lpad("" + infoInv.invActual, 8, " ") + Util.lpad("" + infoInv.cantVentas, 8, " ") + Util.lpad("" + infoInv.cantDev, 8, " ") + enter;
		}

		for (int i = 0; i < 4; i++) {
			strPrint += enter;
		}

		return strPrint;
	}

	public static String formatoRecaudo( String numRecaudo ){


		char XON  = 17;
	
		char ret1 = 13;
		char ret2 = 10;
		char ESC  = 27;
		
		char GS = 29;
		char admiracion = 33;
		
		char altoLetra1 = 1;
		char altoLetraSuperior = 17;

		String enter = String.valueOf(ret1) + String.valueOf(ret2);
		
		String subrayado = ESC + "-" + "1";
		String desSubraydo = ESC + "-" + "0";
		
		String letraTamano1 = ""+GS + admiracion + altoLetra1;
		char altoLetraNormal = 0;
		String letraNormal = ""+GS + admiracion + altoLetraNormal;
		String letraAlta = ""+GS + admiracion + altoLetraSuperior;
		

		if(Main.usuario == null)
			DataBaseBO.CargarInfomacionUsuario();


		String strPrint = "" + enter;

		strPrint += Util.CentrarLinea("INDUSTRIAS NORMANDY S.A.", 42) + enter;
		strPrint += Util.CentrarLinea("NIT 890.807.529-8", 42) + enter + enter;

		strPrint += "RECAUDOS" + enter;
		strPrint += "Vend: " +Main.usuario.codigoVendedor+" "+ Main.usuario.nombreVendedor + enter;	
		strPrint += "Fecha: " + Util.FechaActual("yyyy-MM-dd") + enter ;

		double total = 0;
		double totalNotas = 0;	
		double totalDescuento = 0;	
		double totalRetencion = 0;	
		double totalOtros = 0;	

		EncabezadoRecaudo encRec = DataBaseBO.listaRecaudosRealizado( numRecaudo );


		String strPrintBody = "Recibo de Caja Nro: "+ enter;
		strPrintBody +=  letraTamano1;
		strPrintBody += "" + encRec.nrodoc + enter;
		strPrintBody +=  letraNormal;
		//strPrintBody += "Cliente: "+ encRec.razonSocial + enter;
		//strPrintBody += "Cliente: "+ encRec.representa + enter;
		strPrintBody += "Cliente: "+Main.cliente.codigo+enter;
		strPrintBody += ""+Main.cliente.nombre+enter;
		
		
		strPrintBody +=  letraTamano1;
		
		strPrintBody += ""+Main.cliente.razonSocial+enter;
		
		strPrintBody +=  letraNormal;
		
		
		strPrintBody += "Nit: "    + encRec.nit + enter;
		strPrintBody += "Fecha Recaudo: "+ encRec.fecha_recaudo + enter;
		//strPrintBody += "Vendedor: " + encRec.vendedor + enter;

		strPrintBody += Util.lpad("", 42, "-") + enter;

		Vector<DetalleRecaudo> listaDetallesDelRecaudo = DataBaseBO.listaDetallesRecaudo(encRec.nrodoc);

		DetalleRecaudo detRec;

		for(int i = 0; i < listaDetallesDelRecaudo.size(); i++){

			detRec = listaDetallesDelRecaudo.elementAt(i);

			strPrintBody += Util.rpad("Factura:"   , 28, " ") + Util.lpad( detRec.nroInterno, 14, " ") + enter;
			strPrintBody += Util.rpad("Valor:"     , 28, " ") + Util.lpad( Util.SepararMiles(Util.Redondear("" + detRec.valor,0)), 14, " ") + enter;
			strPrintBody += Util.rpad("Descuento:" , 28, " ") + Util.lpad( "" + detRec.descuento, 14, " ") + enter;
			strPrintBody += Util.rpad("Retencion:", 28, " ") + Util.lpad( "" + detRec.retefuente, 14, " ") + enter;
			strPrintBody += Util.rpad("Otros:"     , 28, " ") + Util.lpad( "" + detRec.otros, 14, " ") + enter;

			if(detRec.valor > 0){

				total = total+detRec.valor;	

			}else{

				totalNotas = totalNotas-detRec.valor;		 				 			
			}

			totalDescuento = totalDescuento+detRec.descuento;
			totalRetencion = totalRetencion+detRec.retefuente;
			totalOtros = totalOtros +detRec.otros;

			strPrintBody += Util.lpad("", 42, "-") + enter;
		}

		strPrintBody += Util.rpad("Valor Total:"     , 28, " ") + Util.lpad( Util.SepararMiles( Util.Redondear( "" + total, 0 ) ), 14, " ") + enter;
		strPrintBody += Util.rpad("Total Notas:"     , 28, " ") + Util.lpad( Util.SepararMiles( Util.Redondear( "" + totalNotas, 0 ) ), 14, " ") + enter;
		strPrintBody += Util.rpad("Total Descuentos:", 28, " ") + Util.lpad( Util.SepararMiles( Util.Redondear( "" + totalDescuento, 0 ) ), 14, " ") + enter;
		strPrintBody += Util.rpad("Retencion:", 28, " ") + Util.lpad( Util.SepararMiles( Util.Redondear( "" + totalRetencion, 0) ), 14, " ") + enter;
		strPrintBody += Util.rpad("Otros Descuentos:", 28, " ") + Util.lpad( Util.SepararMiles( Util.Redondear( "" + totalOtros, 0) ), 14, " ") + enter;

		strPrintBody += Util.lpad("", 42, "-") + enter;
		
		strPrintBody +=  letraAlta;
		
		
		//strPrintBody += Util.rpad("Total:", 28, " ") + Util.lpad( Util.SepararMiles( Util.Redondear( String.valueOf( total - ( totalNotas + totalDescuento + totalRetencion + totalOtros ) ), 0) ), 14, " ") + enter;
		
		
		
		strPrintBody += Util.rpad( "TOTAL:", 11, " ") + Util.lpad( Util.SepararMiles( Util.Redondear( String.valueOf( total - ( totalNotas + totalDescuento + totalRetencion + totalOtros ) ), 0) ), 10 , " ")  + enter;
		
		strPrintBody +=  letraNormal;
		
		
		
		strPrintBody += Util.lpad("", 42, "-") + enter;
		
		strPrintBody += Util.rpad("Efectivo:"    , 28, " ") + Util.lpad( Util.SepararMiles( Util.Redondear( String.valueOf( encRec.efectivo ), 0) ), 14, " ") + enter;
		strPrintBody += Util.rpad("Cheque:"      , 28, " ") + Util.lpad( Util.SepararMiles( Util.Redondear( String.valueOf( encRec.cheque), 0) ), 14, " ") + enter;
		strPrintBody += Util.rpad("Consignacion:", 28, " ") + Util.lpad( Util.SepararMiles( Util.Redondear( String.valueOf( encRec.consignacion) , 0) ), 14, " ") + enter;

		strPrintBody += Util.lpad("", 42, "-") + enter;

		String textoImpimir =strPrint + strPrintBody;

		for( int i = 0; i < 4; i++ ){

			textoImpimir += "" + enter;
		}

		return textoImpimir;

	}


	private static String enter() {

		cantEnter++;
		return "\r\n";
	}

	public static <K extends Comparable,V extends Comparable> Map<K,V> sortByKeys (Map<K,V>map ){

		List<K> keys = new LinkedList<K>(map.keySet());

		Collections.sort(keys);

		//LinkedHashMap will keep the keys in the order they are inserted
		//which is currently sorted on natural ordering

		Map<K,V> sortedMap = new LinkedHashMap<K,V>();

		for(K key: keys){

			sortedMap.put(key, map.get(key));

		}

		return sortedMap;
	}
	
	
	
	public static String formatoInventarioNuevo() {

		String aux;		
		String strPrint; 

		char ret1 = 13;
		char ret2 = 10;
		char ESC  = 27;
		char XON  = 17;

		String normal   = ESC + "U" + "0";
		String negrilla = ESC + "U" + "1";

		String enter = String.valueOf(ret1) + String.valueOf(ret2);

		strPrint = "" + XON;
		strPrint += ESC + "F" + "1";
		strPrint += "" + enter;

		strPrint += "Inventario" + enter;
		strPrint += "Fecha: " + Util.FechaActual("yyyy-MM-dd") + enter;

		strPrint += Util.lpad("", 42, "-") + enter;

		strPrint +=  Util.rpad("COD", 5, " ")+Util.rpad("PRODUCTO", 20, " ")+Util.lpad("ACTUAL", 8, " ")   + Util.lpad("AVERIAS", 8, " ")  + enter;
		//strPrint +=  Util.rpad("",   5, " ") +Util.rpad("", 12, " ")+ Util.rpad("", 7, " ")           + Util.lpad("", 7, " ") + enter; 

		strPrint += Util.lpad("", 42, "-") + enter;

		Vector<InformeInventario> lInformeInv = DataBaseBO.CargarInformeInventario();
		InformeInventario infoInv;

		for( int i = 0; i < lInformeInv.size(); i++ ){

			infoInv = lInformeInv.elementAt( i );

			strPrint += Util.rpad( infoInv.codigo, 5, " ") + Util.rpad( "" + infoInv.nombre, 20, " ") + Util.lpad("" + infoInv.invInicial, 8, " ") + Util.lpad("" + String.valueOf(infoInv.cantVentaC+infoInv.cantDevC), 8, " ")  + enter;
		}

		for (int i = 0; i < 4; i++) {
			strPrint += enter;
		}

		return strPrint;
	}

	
	public static String formatoAutoCuadre() {

		String aux;		
		String strPrint; 

		char ret1 = 13;
		char ret2 = 10;
		char ESC  = 27;
		char XON  = 17;

		String normal   = ESC + "U" + "0";
		String negrilla = ESC + "U" + "1";

		String enter = String.valueOf(ret1) + String.valueOf(ret2);

		strPrint = "" + XON;
		strPrint += ESC + "F" + "1";
		strPrint += "" + enter;

		strPrint += "AutoCuadre" + enter;
		strPrint += "Vendedor: "+Main.usuario.codigoVendedor + enter;
		strPrint += "Fecha: " + Util.FechaActual("yyyy-MM-dd") + enter;

		strPrint += Util.lpad("", 32, "-") + enter;

		strPrint +=  Util.rpad("Descripcion", 15, " ")+Util.rpad("Valor", 20, " ") + enter;
		//strPrint +=  Util.rpad("",   5, " ") +Util.rpad("", 12, " ")+ Util.rpad("", 7, " ")           + Util.lpad("", 7, " ") + enter; 

		strPrint += Util.lpad("", 32, "-") + enter;

		AutoCuadre autocuadre = DataBaseBO.obtenerAutoCuadre();

		strPrint += Util.rpad( "Ventas:", 15, " ") + Util.lpad( "" +   Util.SepararMiles(Util.Redondear(Util.QuitarE("" + autocuadre.ventas), 0))      , 20, " ")  + enter;
		strPrint += Util.rpad( "V. Credito:", 15, " ") + Util.lpad( "" +   Util.SepararMiles(Util.Redondear(Util.QuitarE("" + autocuadre.ventasCredito), 0))        , 20, " ")  + enter;
		strPrint += Util.rpad( "V. Contado:", 15, " ") + Util.lpad( "" +   Util.SepararMiles(Util.Redondear(Util.QuitarE("" + autocuadre.ventasContado), 0))     , 20, " ")  + enter;
		
		
		strPrint += Util.rpad( "Devoluciones:", 15, " ") + Util.lpad( "" +  Util.SepararMiles(Util.Redondear(Util.QuitarE("" + autocuadre.devoluciones), 0))    , 20, " ")  + enter;
		strPrint += Util.rpad( "D. Credito:", 15, " ") + Util.lpad( "" +  Util.SepararMiles(Util.Redondear(Util.QuitarE("" + autocuadre.devolucionesCredito), 0))      , 20, " ")  + enter;
		strPrint += Util.rpad( "D. Contado:", 15, " ") + Util.lpad( "" +  Util.SepararMiles(Util.Redondear(Util.QuitarE("" + autocuadre.devolucionesContado), 0))      , 20, " ")  + enter;
		
		
		strPrint += Util.rpad( "Cartera:", 15, " ") + Util.lpad( "" +    Util.SepararMiles(Util.Redondear(Util.QuitarE("" + autocuadre.cartera), 0))    , 20, " ")  + enter;
		strPrint += Util.rpad( "Gastos:", 15, " ") + Util.lpad( "" +    Util.SepararMiles(Util.Redondear(Util.QuitarE("" + autocuadre.gastos), 0))  , 20, " ")  + enter;
		strPrint += Util.rpad( "Total:", 15, " ") + Util.lpad( "" +     Util.SepararMiles(Util.Redondear(Util.QuitarE("" + autocuadre.total), 0))     , 20, " ")  + enter;
		

		for (int i = 0; i < 4; i++) {
			strPrint += enter;
		}

		return strPrint;
	}

	public static String formatoKardex(String codProducto,Vector<Kardex> listaInformeKardexs ) {

		String aux;		
		String strPrint; 

		char ret1 = 13;
		char ret2 = 10;
		char ESC  = 27;
		char XON  = 17;

		String normal   = ESC + "U" + "0";
		String negrilla = ESC + "U" + "1";

		String enter = String.valueOf(ret1) + String.valueOf(ret2);

		strPrint = "" + XON;
		//strPrint += ESC + "F" + "1";
		strPrint += "" + enter;

		strPrint += "Kardex" + enter;
		strPrint += "Vendedor: "+Main.usuario.codigoVendedor + enter;
		strPrint += "Producto: "+codProducto+" "+ DataBaseBO.NombreProducto(codProducto)+enter;
		strPrint += "Fecha: " + Util.FechaActual("yyyy-MM-dd") + enter;

		strPrint += Util.lpad("", 42, "-") + enter;

		strPrint +=  Util.rpad("FECHA", 12, " ")+Util.rpad("TIPO", 4, " ") +Util.rpad("  Num. Documento", 21, " ")+Util.rpad("CANT", 5, " ") + enter;
		//strPrint +=  Util.rpad("",   5, " ") +Util.rpad("", 12, " ")+ Util.rpad("", 7, " ")           + Util.lpad("", 7, " ") + enter; 

		strPrint += Util.lpad("", 42, "-") + enter;

		for (Kardex kardex : listaInformeKardexs) {
			
			strPrint +=  Util.rpad(kardex.fecha,12, " ")+Util.rpad(" "+kardex.tipoDocumento, 4, " ") +Util.rpad("   "+kardex.numeroDocumento.substring(kardex.numeroDocumento.length()-18, kardex.numeroDocumento.length()), 21, " ")+Util.lpad(Util.Redondear(kardex.cantidad+"",1), 5, " ") + enter;
			
			
		}
	
	

		for (int i = 0; i < 4; i++) {
			strPrint += enter;
		}

		return strPrint;
	}

	
	public static String formatoReporteXFecha(Vector<Encabezado> listaEncabezado,String desde,String hasta,int posicion ) {

		String aux;		
		String strPrint; 
        String infoAux = "";
		char ret1 = 13;
		char ret2 = 10;
		char ESC  = 27;
		char XON  = 17;
		double totalContado = 0;
		double ivaContado = 0;
		double descuentoContado = 0;
		
		
		double totalCredito = 0;
		double ivaCredito = 0;
		double descuentoCredito= 0;
		

		String normal   = ESC + "U" + "0";
		String negrilla = ESC + "U" + "1";

		String enter = String.valueOf(ret1) + String.valueOf(ret2);

		strPrint = "" + XON;
		strPrint += ESC + "F" + "1";
		strPrint += "" + enter;
		String tipo = "";

		strPrint += "Reporte X Rango de Fechas" + enter;
		strPrint += "Desde: " +desde+enter;
		strPrint += "Hasta: " +hasta+enter;
		strPrint += "Fecha: " + Util.FechaActual("yyyy-MM-dd") + enter;
		strPrint += enter+enter;
		
		
		if(posicion == 0){
		
		
		strPrint += Util.CentrarLinea("CONTADO", 42);

		strPrint += Util.lpad("", 42, "-") + enter;

		strPrint +=  Util.rpad("TIPO", 10, " ")+Util.rpad("FACTURA", 12, " ") +Util.rpad("FECHA", 10, " ")+Util.lpad("VALOR", 10, " ")+ enter;
		//strPrint +=  Util.rpad("",   5, " ") +Util.rpad("", 12, " ")+ Util.rpad("", 7, " ")           + Util.lpad("", 7, " ") + enter; 

		strPrint += Util.lpad("", 42, "-") + enter;

		for (Encabezado encabezado : listaEncabezado) {
			
			
			if(encabezado.fp.equals("1")){
		
			
			if (encabezado.tipoDoc.equals("T")) {

				tipo = "FACTURA";
				
				totalContado = totalContado + encabezado.valor_neto;
				ivaContado = ivaContado+encabezado.total_iva;
				descuentoContado = descuentoContado+encabezado.valor_descuento;
				
			}else
			  if (encabezado.tipoDoc.equals("C")) {

					tipo = "NOTA";
					totalContado = totalContado - encabezado.valor_neto;
					ivaContado = ivaContado-encabezado.total_iva;
					descuentoContado = descuentoContado-encabezado.valor_descuento;
					
			  }
			
			
			
			infoAux = encabezado.codigo_cliente+" "+encabezado.razon_social;
			
			if(infoAux.length()>40)
				infoAux = infoAux.substring(0, 40);
			
			
			
			strPrint +=  Util.rpad(infoAux,40, " ")+ enter;
			
			
			strPrint +=  Util.rpad(tipo,10, " ")+Util.rpad(encabezado.factura, 12, " ") +Util.rpad(encabezado.fecha, 10, " ")+Util.lpad(Util.SepararMiles(Util.Redondear(Util.QuitarE("" + String.valueOf(encabezado.valor_neto+encabezado.total_iva-encabezado.valor_descuento)), 0)), 10, " ")+ enter;
			
			}
			
		}
		

	
		strPrint += enter;
		strPrint +=  Util.rpad("",10, " ")+Util.rpad("", 12, " ") +Util.rpad("SUBTOTAL", 10, " ")+Util.lpad(Util.SepararMiles(Util.Redondear(Util.QuitarE("" + totalContado), 0)), 10, " ")+ enter;
		strPrint +=  Util.rpad("",10, " ")+Util.rpad("", 12, " ") +Util.rpad("IVA", 10, " ")+Util.lpad(Util.SepararMiles(Util.Redondear(Util.QuitarE("" + ivaContado), 0)), 10, " ")+ enter;
		strPrint +=  Util.rpad("",10, " ")+Util.rpad("", 12, " ") +Util.rpad("DESCUENTO", 10, " ")+Util.lpad(Util.SepararMiles(Util.Redondear(Util.QuitarE("" + descuentoContado), 0)), 10, " ")+ enter;
		strPrint +=  Util.rpad("",10, " ")+Util.rpad("", 12, " ") +Util.rpad("TOTAL", 10, " ")+Util.lpad(Util.SepararMiles(Util.Redondear(Util.QuitarE(""+String.valueOf(totalContado+ivaContado-descuentoContado)), 0)), 10, " ")+ enter;
		
		
		
		
		
		strPrint += enter;
		strPrint += enter;
		strPrint += enter;
		
		
		}
		
		strPrint += Util.CentrarLinea("CREDITO", 42);
		
		
		
		strPrint += Util.lpad("", 42, "-") + enter;

		strPrint +=  Util.rpad("TIPO", 10, " ")+Util.rpad("FACTURA", 12, " ") +Util.rpad("FECHA", 10, " ")+Util.lpad("VALOR", 10, " ")+ enter;
		//strPrint +=  Util.rpad("",   5, " ") +Util.rpad("", 12, " ")+ Util.rpad("", 7, " ")           + Util.lpad("", 7, " ") + enter; 

		strPrint += Util.lpad("", 42, "-") + enter;
		
		
		
		
		for (Encabezado encabezado : listaEncabezado) {
			
			
			if(encabezado.fp.equals("2")){
		
			
			if (encabezado.tipoDoc.equals("T")) {

				tipo = "FACTURA";
				
				totalCredito = totalCredito + encabezado.valor_neto;
				ivaCredito = ivaCredito+encabezado.total_iva;
				descuentoCredito = descuentoCredito+encabezado.valor_descuento;
				
			}else
			  if (encabezado.tipoDoc.equals("C")) {

					tipo = "NOTA";
					totalCredito = totalCredito - encabezado.valor_neto;
					ivaCredito = ivaCredito-encabezado.total_iva;
					descuentoCredito = descuentoCredito-encabezado.valor_descuento;
					
			  }
			
			
			
			infoAux = encabezado.codigo_cliente+" "+encabezado.nombre_cliente;
			
			if(infoAux.length()>40)
				infoAux = infoAux.substring(0, 40);
			
			
			
			strPrint +=  Util.rpad(infoAux,40, " ")+ enter;
			
			
			strPrint +=  Util.rpad(tipo,10, " ")+Util.rpad(encabezado.factura, 12, " ") +Util.rpad(encabezado.fecha, 10, " ")+Util.lpad(Util.SepararMiles(Util.Redondear(Util.QuitarE("" +String.valueOf(encabezado.valor_neto+encabezado.total_iva-encabezado.valor_descuento)), 0)), 10, " ")+ enter;
			
			}
			
		}
		

	
		strPrint += enter;
		strPrint +=  Util.rpad("",10, " ")+Util.rpad("", 12, " ") +Util.rpad("SUBTOTAL", 10, " ")+Util.lpad(Util.SepararMiles(Util.Redondear(Util.QuitarE("" + totalCredito), 0)), 10, " ")+ enter;
		strPrint +=  Util.rpad("",10, " ")+Util.rpad("", 12, " ") +Util.rpad("IVA", 10, " ")+Util.lpad(Util.SepararMiles(Util.Redondear(Util.QuitarE("" + ivaCredito), 0)), 10, " ")+ enter;
		strPrint +=  Util.rpad("",10, " ")+Util.rpad("", 12, " ") +Util.rpad("DESCUENTO", 10, " ")+Util.lpad(Util.SepararMiles(Util.Redondear(Util.QuitarE("" + descuentoCredito), 0)), 10, " ")+ enter;
		strPrint +=  Util.rpad("",10, " ")+Util.rpad("", 12, " ") +Util.rpad("TOTAL", 10, " ")+Util.lpad(Util.SepararMiles(Util.Redondear(Util.QuitarE(""+String.valueOf(totalCredito+ivaCredito-descuentoCredito)), 0)), 10, " ")+ enter;
		
		
		
		
		strPrint += enter;
		strPrint += enter;
		strPrint += enter;
		
		
		
		
		
		strPrint +=  Util.rpad("",10, " ")+Util.rpad("", 12, " ") +Util.rpad("SUBTOTAL", 10, " ")+Util.lpad(Util.SepararMiles(Util.Redondear(Util.QuitarE("" + String.valueOf(totalCredito+totalContado)), 0)), 10, " ")+ enter;
		strPrint +=  Util.rpad("",10, " ")+Util.rpad("", 12, " ") +Util.rpad("IVA", 10, " ")+Util.lpad(Util.SepararMiles(Util.Redondear(Util.QuitarE("" + String.valueOf(ivaCredito+ivaContado)), 0)), 10, " ")+ enter;
		strPrint +=  Util.rpad("",10, " ")+Util.rpad("", 12, " ") +Util.rpad("DESCUENTO", 10, " ")+Util.lpad(Util.SepararMiles(Util.Redondear(Util.QuitarE("" + String.valueOf(descuentoCredito+descuentoContado)), 0)), 10, " ")+ enter;
		strPrint +=  Util.rpad("",10, " ")+Util.rpad("", 12, " ") +Util.rpad("TOTAL", 10, " ")+Util.lpad(Util.SepararMiles(Util.Redondear(Util.QuitarE(""+String.valueOf(totalCredito+ivaCredito-descuentoCredito+totalContado+ivaContado-descuentoContado)), 0)), 10, " ")+ enter;
		
	
		
		

		for (int i = 0; i < 7; i++) {
			strPrint += enter;
		}

		return strPrint;
	}
	
	
	
	
	public static String formatoInventarioNuevo2() {
	    
	    String aux;		
	    String strPrint; 
	    char ret1 = 13;
	    char ret2 = 10;
	    char ESC  = 27;
	    char XON  = 17;
	    String iniciar = ESC + "@";
	    String normal   = ESC + "U" + "0";
	    String negrilla = ESC + "U" + "1";
	    String enter = String.valueOf(ret1) + String.valueOf(ret2);
	    strPrint = iniciar + "";	
	    strPrint += "" + enter;
	    strPrint += "Inventario" + enter; //RAMIREZ GOMEZ CARLOS ALCIDES
	    strPrint += "Vendedor: "+Main.usuario.codigoVendedor + enter;
	    strPrint += "          "+Main.usuario.nombreVendedor + enter;
	    strPrint += "Fecha: " + Util.FechaActual("yyyy-MM-dd HH:mm:ss") + enter;
	    
	    strPrint += Util.lpad("", 42, "-") + enter;
	    
	    strPrint +=  Util.rpad("COD", 5, " ")+Util.rpad("PRODUCTO", 20, " ")+Util.lpad("ACTUAL", 8, " ")   + Util.lpad("AVERIAS", 8, " ")  + enter;
	    
	    strPrint += Util.lpad("", 42, "-") + enter;
	    
	    //Vector<InformeInventario2> lInformeInv = DataBaseBO.CargarInformeInventario2();
	    Vector<InformeInventario2> lInformeInv = DataBaseBO.CargarInformeInventario33();
	    
	    InformeInventario2 infoInv;
	    
	    for( int i = 0; i < lInformeInv.size(); i++ ){
		
		infoInv = lInformeInv.elementAt( i );
		
		strPrint += Util.rpad( infoInv.codigo, 5, " ") + Util.rpad( "" + infoInv.nombre, 20, " ") + Util.lpad("" + Util.Redondear(infoInv.invInicial+"",1), 8, " ") + Util.lpad("" + Util.Redondear(String.valueOf(infoInv.cantVentaC),1), 8, " ")  + enter;
	    }
	    
	    for (int i = 0; i < 4; i++) {
		strPrint += enter;
	    }
	    
	    return strPrint;
	}
	
	
	
	
	public static String formatoRecaudo2( String numRecaudo ){


		char XON  = 17;
	
		char ret1 = 13;
		char ret2 = 10;
		char ESC  = 27;
		
		char GS = 29;
		char admiracion = 33;
		
		char altoLetra1 = 1;
		char altoLetraSuperior = 17;

		String enter = String.valueOf(ret1) + String.valueOf(ret2);
		
		String subrayado = ESC + "-" + "1";
		String desSubraydo = ESC + "-" + "0";
		
		String letraTamano1 = ""+GS + admiracion + altoLetra1;
		char altoLetraNormal = 0;
		String letraNormal = ""+GS + admiracion + altoLetraNormal;
		String letraAlta = ""+GS + admiracion + altoLetraSuperior;
		

		if(Main.usuario == null)
			DataBaseBO.CargarInfomacionUsuario();


		String strPrint = "" + enter;

		strPrint += Util.CentrarLinea("INDUSTRIAS NORMANDY S.A.", 42) + enter;
		strPrint += Util.CentrarLinea("NIT 890.807.529-8", 42) + enter + enter;

		strPrint += "RECAUDOS" + enter;
		strPrint += "Vend: " +Main.usuario.codigoVendedor+" "+ Main.usuario.nombreVendedor + enter;	
		strPrint += "Fecha: " + Util.FechaActual("yyyy-MM-dd") + enter ;

		double total = 0;
		double totalNotas = 0;	
		double totalDescuento = 0;	
		double totalRetencion = 0;	
		double totalOtros = 0;	

		EncabezadoRecaudo encRec = DataBaseBO.listaRecaudosRealizado( numRecaudo );


		String strPrintBody = "Recibo de Caja Nro: "+ enter;
		strPrintBody +=  letraTamano1;
		strPrintBody += "" + encRec.nrodoc + enter;
		strPrintBody +=  letraNormal;
		//strPrintBody += "Cliente: "+ encRec.razonSocial + enter;
		strPrintBody += "Cliente: "+ encRec.representa + enter;
		//strPrintBody += "Cliente: "+Main.cliente.codigo+enter;
		//strPrintBody += ""+Main.cliente.nombre+enter;
		
		
		strPrintBody +=  letraTamano1;
		
		strPrintBody += "Cliente: "+ encRec.razonSocial + enter;
		
		//strPrintBody += ""+Main.cliente.razonSocial+enter;
		
		strPrintBody +=  letraNormal;
		
		
		strPrintBody += "Nit: "    + encRec.nit + enter;
		strPrintBody += "Fecha Recaudo: "+ encRec.fecha_recaudo + enter;
		//strPrintBody += "Vendedor: " + encRec.vendedor + enter;

		strPrintBody += Util.lpad("", 42, "-") + enter;

		Vector<DetalleRecaudo> listaDetallesDelRecaudo = DataBaseBO.listaDetallesRecaudo(encRec.nrodoc);

		DetalleRecaudo detRec;

		for(int i = 0; i < listaDetallesDelRecaudo.size(); i++){

			detRec = listaDetallesDelRecaudo.elementAt(i);

			strPrintBody += Util.rpad("Factura:"   , 28, " ") + Util.lpad( detRec.nroInterno, 14, " ") + enter;
			strPrintBody += Util.rpad("Valor:"     , 28, " ") + Util.lpad( Util.SepararMiles(Util.Redondear("" + detRec.valor,0)), 14, " ") + enter;
			strPrintBody += Util.rpad("Descuento:" , 28, " ") + Util.lpad( "" + detRec.descuento, 14, " ") + enter;
			strPrintBody += Util.rpad("Retencion:", 28, " ") + Util.lpad( "" + detRec.retefuente, 14, " ") + enter;
			strPrintBody += Util.rpad("Otros:"     , 28, " ") + Util.lpad( "" + detRec.otros, 14, " ") + enter;

			if(detRec.valor > 0){

				total = total+detRec.valor;	

			}else{

				totalNotas = totalNotas-detRec.valor;		 				 			
			}

			totalDescuento = totalDescuento+detRec.descuento;
			totalRetencion = totalRetencion+detRec.retefuente;
			totalOtros = totalOtros +detRec.otros;

			strPrintBody += Util.lpad("", 42, "-") + enter;
		}

		strPrintBody += Util.rpad("Valor Total:"     , 28, " ") + Util.lpad( Util.SepararMiles( Util.Redondear( "" + total, 0 ) ), 14, " ") + enter;
		strPrintBody += Util.rpad("Total Notas:"     , 28, " ") + Util.lpad( Util.SepararMiles( Util.Redondear( "" + totalNotas, 0 ) ), 14, " ") + enter;
		strPrintBody += Util.rpad("Total Descuentos:", 28, " ") + Util.lpad( Util.SepararMiles( Util.Redondear( "" + totalDescuento, 0 ) ), 14, " ") + enter;
		strPrintBody += Util.rpad("Retencion:", 28, " ") + Util.lpad( Util.SepararMiles( Util.Redondear( "" + totalRetencion, 0) ), 14, " ") + enter;
		strPrintBody += Util.rpad("Otros Descuentos:", 28, " ") + Util.lpad( Util.SepararMiles( Util.Redondear( "" + totalOtros, 0) ), 14, " ") + enter;

		strPrintBody += Util.lpad("", 42, "-") + enter;
		
		strPrintBody +=  letraAlta;
		
		
		//strPrintBody += Util.rpad("Total:", 28, " ") + Util.lpad( Util.SepararMiles( Util.Redondear( String.valueOf( total - ( totalNotas + totalDescuento + totalRetencion + totalOtros ) ), 0) ), 14, " ") + enter;
		
		
		
		strPrintBody += Util.rpad( "TOTAL:", 11, " ") + Util.lpad( Util.SepararMiles( Util.Redondear( String.valueOf( total - ( totalNotas + totalDescuento + totalRetencion + totalOtros ) ), 0) ), 10 , " ")  + enter;
		
		strPrintBody +=  letraNormal;
		
		
		
		strPrintBody += Util.lpad("", 42, "-") + enter;
		
		strPrintBody += Util.rpad("Efectivo:"    , 28, " ") + Util.lpad( Util.SepararMiles( Util.Redondear( String.valueOf( encRec.efectivo ), 0) ), 14, " ") + enter;
		strPrintBody += Util.rpad("Cheque:"      , 28, " ") + Util.lpad( Util.SepararMiles( Util.Redondear( String.valueOf( encRec.cheque), 0) ), 14, " ") + enter;
		strPrintBody += Util.rpad("Consignacion:", 28, " ") + Util.lpad( Util.SepararMiles( Util.Redondear( String.valueOf( encRec.consignacion) , 0) ), 14, " ") + enter;

		strPrintBody += Util.lpad("", 42, "-") + enter;

		String textoImpimir =strPrint + strPrintBody;

		for( int i = 0; i < 4; i++ ){

			textoImpimir += "" + enter;
		}

		return textoImpimir;

	}




	
	
}
