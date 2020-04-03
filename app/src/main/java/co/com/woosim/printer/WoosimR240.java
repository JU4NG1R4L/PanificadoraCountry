/**
 * 
 */
package co.com.woosim.printer;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import android.content.Context;
import android.util.Log;
import co.com.BusinessObject.DataBaseBO;
import co.com.BusinessObject.DataBaseBOJ;
import co.com.DataObject.Cliente;
import co.com.DataObject.Deposito;
import co.com.DataObject.DetalleDeposito;
import co.com.DataObject.DetalleImprimir;
import co.com.DataObject.DetalleProducto;
import co.com.DataObject.DevolucionesRealizados;
import co.com.DataObject.InformeInventario2;
import co.com.DataObject.InventarioSugerido;
import co.com.DataObject.Producto;
import co.com.DataObject.Resolucion;
import co.com.DataObject.SaldoCancelado;
import co.com.DataObject.Usuario;
import co.com.panificadoracountry.Const;
import co.com.panificadoracountry.Util;

import com.woosim.bt.WoosimPrinter;

/**
 * Clase que contiene los metodos y atributos necesarios para configurar la
 * impresora woosim WSP-R240
 * 
 * @author JICZ
 * 
 */
public class WoosimR240 {

	/* Datos de impresora necesarios para la conexion */
	public final static String CONFIG_IMPRESORA = "PRINTER";
	public final static String MAC_IMPRESORA = "MAC";
	public final static String NOMBREEMPRESA = "PANIFICADORA COUNTRY";
	/* ********************************************** */

	/* Tama�os maximos para cada columna-detalle de la tirilla */
	private static final int TAM_MAX_CODIGO = 6;
	private static final int TAM_MAX_VALOR_UNIT = 9;
	private static final int TAM_MAX_PRODUCTO = 15;
	private static final int TAM_MAX_CANTIDAD = 7;
	private static final int TAM_MAX_TOTAL = 14;
	private static final int TAM_MAX_TOTAL_UNIT = 11;

	private static final int TAM_MAX_PRODUCTO_INV = 18;
	private static final int TAM_MAX_ACTUAL = 10;
	private static final int TAM_MAX_CAMBIOS = 7;
	
	/* Tama�os maximos para cada columna-detalle de la tirilla de devoluciones*/
	private static final int TAM_MAX_PRODUCTO_DEV   = 18;
	private static final int TAM_MAX_CANTIDAD_DEV   = 6;
	private static final int TAM_MAX_VALOR_UNIT_DEV = 9;
	private static final int TAM_MAX_TOTAL_UNIT_DEV = 9;


	/* Tama�os maximos para cada columna-detalle de la tirilla  Deposito*/
	private static final int TAM_MAX_CLIENTE = 20;
	private static final int TAM_MAX_FACTURA = 10;
	private static final int TAM_MAX_VALOR = 12;
	/* ***************************************************** */

	/**
	 * Definir texto a imprimir con el doble de alto.
	 */
	private static final int TEXT_DOBLE_ALTO = 16;

	/**
	 * Comando rapido para imprimir negrilla
	 */
	private final static boolean NEGRILLA = true;

	/**
	 * Comando rapido para imprimir Texto normal
	 */
	private final static boolean NORMAL = false;

	/**
	 * Activity desde donde es llamada la funcion de la impresora.
	 */
	private Context context;

	/**
	 * Identifica el lenguaje usado para la impresora. Lenguaje conpatible con
	 * el idioma espa�ol (OEM Latin 1 + Euro Symbol)
	 */
	public final static String CP858 = "CP858";

	/**
	 * Limpiar datos y buffer, reiniciar configuracion de la impresora.
	 */
	private static final byte[] initialize = { 0x1B, 0x40 };

	/**
	 * Tabular a la mitad de la hoja.
	 */
	private static final byte[] posDerecha = { 27, 36, (byte) 140, 0 };

	/**
	 * Tabular a la mitad de la hoja.
	 */
	private static final byte[] posResolucion = { 27, 36, (byte) 194, 0 };

	/**
	 * Seleccionar set de caracteres para idioma espa�ol
	 */
	private static final byte[] characterSet = { 0x1b, 0x52, 0x07 };

	/**
	 * Jusitificar al centro el texto siguiente a este comando
	 */
	private static final byte[] centrar = { 0x1b, 0x61, 49 };

	/**
	 * Jusitificar a la izquierda el texto siguiente a este comando
	 */
	private static final byte[] izquierda = { 0x1b, 0x61, 48 };

	/**
	 * Print mode. Character FONT B (9x24) = 42 caracteres por linea. 
	 */
	private static final byte[] characterSpacing = { 27, 33, 1 };

	/**
	 * Imprimir los datos en el bufer y alimenta una linea y vuelve al inicio de
	 * la linea. (0x0A)
	 */
	private static final byte[] LF = { 0x0A };

	/**
	 * Imprimir los datos en el bufer y alimenta una linea y vuelve al inicio de
	 * la linea
	 */
	private static final byte[] FF = { 0x0C };

	/**
	 * Referencia a la libreria de la impresora.
	 */
	private WoosimPrinter woosimPrinter;

	/**
	 * 
	 * @param context
	 */
	public WoosimR240(Context context) {
		this.context = context;
		this.woosimPrinter = new WoosimPrinter();
	}

	/**
	 * Conectar a la impresora establecida como por defecto.
	 * 
	 * @return 1 = SUCCESS <BR>
	 *         -2 = FAIL<BR>
	 *         -5 = NOT_PARING<BR>
	 *         -6 = ALREADY_CONNECTED<BR>
	 *         -8 = BLUETOOTH_NOT_ENABLE<BR>
	 */
	public int conectarImpresora(String address) {
		int ret = this.woosimPrinter.BTConnection(address, false);
		Log.i("CONECTAR IMPRESORA " + address, " RET: -> " + ret);
		return ret;
	}

	/**
	 * Limpiar la memoria intermedia y desconectar la impresora.
	 */
	public void desconectarImpresora() {
		this.woosimPrinter.clearSpool();
		this.woosimPrinter.closeConnection();
	}

	/**
	 * Unicamente formato .bmp de 1 bit, (monocromatico) es compatible.
	 * 
	 * @param path
	 *            ruta a la imagen formato .bmp.
	 * @return 1 = SUCCESS <BR>
	 *         -2 = FAIL<BR>
	 *         -4 = NOT_FOUND_IMAGE_FILE<BR>
	 */
	public int imprimirLogo(String path) {
		try {
			return this.woosimPrinter.printBitmap(path);
		} catch (IOException e) {
			return -2;
		}
	}

	/**
	 * Unicamente formato .bmp de 1 bit, (monocromatico) es compatible.
	 *            ruta a la imagen formato .bmp.
	 * @return 1 = SUCCESS <BR>
	 *         -2 = FAIL<BR>
	 *         -4 = NOT_FOUND_IMAGE_FILE<BR>
	 */
	public int imprimirLogoEmpresarial() {
		int ret = -2;

		try {
			File file = new File(Util.DirApp(), PreferenceLogo.getNombreLogo(context).toString().trim());
			String path = file.getAbsolutePath();
			if (file.exists()) {
				ret = this.woosimPrinter.printBitmap(path);
				/*
				 * Asignar tiempo de espera para que culmine la transmision
				 * bluethooth de datos de la imagen
				 */
				Thread.sleep(Const.timeWait);
			} else {
				ret = -4;
			}
		} catch (IOException e) {
			Log.e("WoosimR240.java", e.getMessage().toString());
			ret = -2;
		} catch (InterruptedException e) {
			Log.e("WoosimR240.java", e.getMessage().toString());
			ret = -2;
		}
		return ret;
	}

	/**
	 * Cargar texto a imprimir, texto normal.
	 * 
	 * @param lineaTexto
	 * @return Numero de bytes<BR>
	 *         -3 = BUFFER_OVERFLOW cuando excede a 9999 bytes
	 */
	public int cargarTexto(String lineaTexto, boolean bold) {
		izquierda();
		return this.woosimPrinter.saveSpool(CP858, lineaTexto, 0, bold);
	}

	/**
	 * Cargar texto a imprimir, texto normal.
	 * 
	 * @param lineaTexto
	 * @param bold,
	 *            negrilla o texto normal
	 * @param tamFont,
	 *            tama�o de la fuente
	 * @return
	 */
	public int cargarTexto(String lineaTexto, boolean bold, int tamFont) {
		izquierda();
		return this.woosimPrinter.saveSpool(CP858, lineaTexto, tamFont, bold);
	}

	/**
	 * Cargar texto a imprimir, texto normal.

	 * @return Numero de bytes<BR>
	 *         -3 = BUFFER_OVERFLOW cuando excede a 9999 bytes
	 */
	public int cargarTextoCentro(String lineaTextoCentrado, boolean bold) {
		centrar();
		return this.woosimPrinter.saveSpool(CP858, lineaTextoCentrado, 0, bold);
	}

	/**
	 * Imprimir el texto salvado.
	 * 
	 * @param borrarBuffer,
	 *            limpiar buffer despues de imprimir.
	 * @return 1 = SUCCESS <BR>
	 *         0x15 = NACK <BR>
	 *         0x04 = EOT<BR>
	 *         -1 = TIMEOUT<BR>
	 */
	public int imprimirBuffer(boolean borrarBuffer) {
		lf();
		lf();
		ff();
		return this.woosimPrinter.printSpool(borrarBuffer);
	}

	public int imprimirEstatus() {
		byte[] status = { 0x10, 0x04, 0x04 };
		return this.woosimPrinter.controlCommand(status, status.length);
	}

	/**
	 * Imprime el codigo de barras correspondiente al nit de la empresa country
	 * ltda.
	 * 
	 * @return
	 */
	public int imprimirCodigoBarraPruebas() {
		byte[] barCode = { 0x1D, 0x6B, 0x00, '0', '0', '8', '0', '0', '0', '6', '4', '1', '2', '6', '6', 0x00 };
		return this.woosimPrinter.controlCommand(barCode, barCode.length);
	}

	/**
	 * Imprime un texto de prueba.
	 */
	public int generarPaginaPrueba() {
		inicializarImpresora();
		this.woosimPrinter.clearSpool();
		characterSet();
		characterSpacingDefault();
		cargarTextoCentro("INDUSTRIA PANIFICADORA\r\n", NEGRILLA);
		cargarTextoCentro("EL COUNTRY LTDA.\r\n\n", NEGRILLA);
		cargarTextoCentro("PRUEBA DE IMPRESORA\n", NORMAL);
		cargarTexto("PRUEBA: OK \n", NORMAL);
		cargarTexto("INPUT: 800064126-6: \n", NORMAL);
		int ret = imprimirCodigoBarraPruebas();
		lf();
		lf();
		ff();
		return ret;
	}

	/**
	 * Metodo permite la impresion del encabezado que llevan todas las tirillas.
	 * @param original 
	 * @param fechaVenta 
	 * 
	 * @param NumeroFacturaVenta,
	 *            numero de la factua, debe coincidir con la resolucion DIAN y
	 *            estar vigente.
	 */
	public void generarEncabezadoTirilla(String NumeroFacturaVenta, Cliente cliente, String fechaVenta, boolean original) {

		/*
		 * Estimar un tiempo de espera para el hilo de sincronizacion bluetooth
		 */
		try {
			Thread.sleep(Const.timeWait);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		/* Tabular al inicio de la hoja. (izquierda) */
		byte[] posNormal = { 27, 36, 0, 0 };

		inicializarImpresora();

		/* limpiar el buffer */
		this.woosimPrinter.clearSpool();

		characterSet(); // caracteres para idioma espa�ol.

		imprimirLogoEmpresarial();
		lf();
		lf();
		ff();
		characterSpacingDefault();
		cargarTextoCentro("\r\nINDUSTRIA PANIFICADORA\r\n", NEGRILLA);
		cargarTextoCentro("EL COUNTRY LTDA.\r\n", NEGRILLA);
		cargarTextoCentro("NIT: 800.064.126-6\r\n", NEGRILLA);
		cargarTextoCentro("IVA REGIMEN COMUN\r\n", NEGRILLA);
		cargarTextoCentro("NO SOMOS GRANDES CONTRIBUYENTES\r\n\n", NEGRILLA);
		//cargarTextoCentro("SOMOS GRANDES CONTRIBUYENTES\r\n", NORMAL);
		//cargarTextoCentro("Y AGENTES DE RETENCION\r\n", NORMAL);
		cargarTextoCentro("ACTIVIDAD ECONOMICA 1081\r\n", NORMAL);
		cargarTextoCentro("TARIFA ICA 4.14 x 1000\r\n\n\n", NORMAL);


		cargarTexto("Fecha Venta:", NORMAL);
		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posResolucion, posResolucion.length);
		cargarTexto(fechaVenta + "\r\n", NEGRILLA);


		cargarTexto("Factura Venta No.:", NORMAL);
		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posResolucion, posResolucion.length);
		cargarTexto(NumeroFacturaVenta + "\r\n", NEGRILLA);

		cargarInfoResolucion();

		cargarTexto("Direccion:", NORMAL);
		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posDerecha, posDerecha.length);
		cargarTexto("Cra 23 No. 164-36 Toberin.\r\n", NORMAL);

		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posDerecha, posDerecha.length);
		cargarTexto("www.panelcountry.com\r\n", NORMAL);

		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posDerecha, posDerecha.length);
		cargarTexto("Bogota D.C\r\n", NORMAL);

		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posNormal, posNormal.length);
		cargarTexto("Telefonos: ", NORMAL);

		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posDerecha, posDerecha.length);
		cargarTexto("PBX - 678 01 50\r\n", NEGRILLA);

		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posDerecha, posDerecha.length);
		cargarTexto("FAX - 679 85 59\r\n", NORMAL);

		cargarTexto("Fecha Imp.:", NORMAL);

		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posDerecha, posDerecha.length);
		cargarTexto(Util.FechaActual("yyyy-MM-dd HH:mm:ss") + "\r\n", NORMAL);

		//		/* tirar texto a la mitad de la hoja */
		//		this.woosimPrinter.controlCommand(posDerecha, posDerecha.length);
		//		cargarTexto(DateFormat.getTimeInstance().format(new Date()) + "\r\n", NORMAL);

		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posDerecha, posDerecha.length);
		cargarTexto((original? "ORIGINAL":"COPIA") + "\r\n\n", NORMAL);

		/*Imprimir informacion del cliente.*/
		if(cliente != null){
			cargarTexto("Cliente: ", NORMAL);

			/* tirar texto a la mitad de la hoja */
			this.woosimPrinter.controlCommand(posDerecha, posDerecha.length);
			cargarTexto(cliente.razonSocial + "\r\n", NORMAL);
			this.woosimPrinter.controlCommand(posDerecha, posDerecha.length);
			cargarTexto(cliente.nombre + "\r\n", NORMAL);
			
			cargarTexto("Direccion: ", NORMAL);
			this.woosimPrinter.controlCommand(posDerecha, posDerecha.length);
			cargarTexto(cliente.direccion + ".\r\n", NORMAL);
			
			cargarTexto("NIT: ", NORMAL);
			this.woosimPrinter.controlCommand(posDerecha, posDerecha.length);
			cargarTexto(cliente.nit + "\r\n", NORMAL);
		}

		cargarTexto("\r\n", NORMAL);// impresion

		/* Pintar columnas para detalles */
		// cargarTexto("------------------------------------------\r\n",NORMAL);
		// cargarTexto(" Cod Producto Cant Total+Imp. \r\n", NORMAL);
		// cargarTexto("------------------------------------------\r\n",NORMAL);

		//
		cargarTexto("------------------------------------------\r\n", NORMAL);
		cargarTexto(" Producto      Cant    V.Uni     Total    \r\n", NORMAL);
		cargarTexto("------------------------------------------\r\n", NORMAL);
		//

	}

	/**
	 * PErmite definir un tama�o de letras normal.
	 */
	private void characterSizeSetNormal() {
		/* Tama�o por default. */
		byte[] sizeNormal = { 29, 33, 0 };
		this.woosimPrinter.controlCommand(sizeNormal, sizeNormal.length);
	}

	private void cargarInfoResolucion() {
		Resolucion resolucion = new Resolucion();
		DataBaseBO.obtenerDatosResolucion(resolucion,DataBaseBO.getPrefijo());

		/* verificar que la resolcuion existe */
		if (!resolucion.resolucion.equals("0")) {

			cargarTexto("Resolucion DIAN: ", NORMAL);
			/* tirar texto a la mitad de la hoja */
			this.woosimPrinter.controlCommand(posResolucion, posResolucion.length);
			cargarTexto(resolucion.resolucion + "\r\n", NORMAL);

			this.woosimPrinter.controlCommand(posResolucion, posResolucion.length);
			cargarTexto("De: " + resolucion.fechaResolucion.substring(0, 11) + "\r\n", NORMAL);
			this.woosimPrinter.controlCommand(posResolucion, posResolucion.length);
			cargarTexto("Hasta: " + resolucion.fechaVigencia.substring(0, 11) + "\r\n", NORMAL);

			cargarTexto("Autoriza del No.: ", NORMAL);
			this.woosimPrinter.controlCommand(posResolucion, posResolucion.length);
			cargarTexto(resolucion.numeroInicial + " al " + resolucion.numeroFinal + "\r\n\n", NORMAL);
		}
	}

	/**
	 * Metodo permite formar los detalles que seran impresos. alineados de
	 * acuerdo a la impresora.
	 * 
	 * @param detalleImprimir
	 */
	public void generarDetalleTirilla(DetalleImprimir detalleImprimir) {
		/* define la posicion en la que se ubican los precios */
		byte[] posPrecio = { 27, 36, (byte) 0x17, 1 };

		/* define la posicion en la que se ubican los precios */
		byte[] posPrecioTotal = { 27, 36, (byte) 0xFC, 0 };

		/* Define las pocisiones en las que se ubican los totales del pedido */
		byte[] totalizado = { 27, 36, (byte) 0x8E, 0 };

		/* Habilitar subrayado. */
		byte[] subRayadoOn = { 27, 45, 2 };

		/* Apagar subrayado. */
		byte[] subRayadoOff = { 27, 45, 0 };

		/* Dar formato a cada detalle del pedido. */
		for (DetalleProducto detalle : detalleImprimir.getListaDetalleImprimir()) {
			formatearTexto(detalle);
			cargarTexto(detalle.nombre + detalle.cantidad + detalle.precioUnitario, NORMAL);

			/* tirar texto a la columna de precio en la hoja */
			this.woosimPrinter.controlCommand(posPrecio, posPrecio.length);
			cargarTexto(detalle.totalUnitario + "\r\n", NORMAL);
		}

		/* Pintar columnas para detalles */
		this.woosimPrinter.controlCommand(subRayadoOn, subRayadoOn.length);
		cargarTexto("                                          \r\n", NORMAL);
		this.woosimPrinter.controlCommand(subRayadoOff, subRayadoOff.length);

		/*
		 * Pintar datos de costos de encabezado (subtotal, iva y total. ) a 160
		 * dots.
		 */
		this.woosimPrinter.controlCommand(totalizado, totalizado.length);
		cargarTexto("Subtotal: ", NORMAL);

		/* tirar texto a la columna de precio en la hoja */
		this.woosimPrinter.controlCommand(posPrecioTotal, posPrecioTotal.length);
		cargarTexto(detalleImprimir.encabezado.str_sub_total.substring(0, TAM_MAX_TOTAL) + "\r\n", NORMAL);

		this.woosimPrinter.controlCommand(totalizado, totalizado.length);
		cargarTexto("Descuento: ", NORMAL);

		/* tirar texto a la columna de precio en la hoja */
		this.woosimPrinter.controlCommand(posPrecioTotal, posPrecioTotal.length);
		cargarTexto(detalleImprimir.encabezado.str_descuento.substring(0, TAM_MAX_TOTAL) + "\r\n", NORMAL);

		this.woosimPrinter.controlCommand(totalizado, totalizado.length);
		cargarTexto("Base: ", NORMAL);

		/* tirar texto a la columna de precio en la hoja */
		this.woosimPrinter.controlCommand(posPrecioTotal, posPrecioTotal.length);
		cargarTexto(detalleImprimir.encabezado.str_base.substring(0, TAM_MAX_TOTAL) + "\r\n", NORMAL);

		this.woosimPrinter.controlCommand(totalizado, totalizado.length);
		cargarTexto("IVA: ", NORMAL);

		/* tirar texto a la columna de precio en la hoja */
		this.woosimPrinter.controlCommand(posPrecioTotal, posPrecioTotal.length);
		cargarTexto(detalleImprimir.encabezado.str_total_iva.substring(0, TAM_MAX_TOTAL) + "\r\n", NORMAL);

		this.woosimPrinter.controlCommand(totalizado, totalizado.length);
		cargarTexto("Retefuente: ", NORMAL);


		/* tirar texto a la columna de precio en la hoja */
		this.woosimPrinter.controlCommand(posPrecioTotal, posPrecioTotal.length);
		cargarTexto(detalleImprimir.encabezado.str_total_retefuente.substring(0, TAM_MAX_TOTAL) + "\r\n", NORMAL);

		this.woosimPrinter.controlCommand(totalizado, totalizado.length);
		cargarTexto("Reteica: ", NORMAL);


		/* tirar texto a la columna de precio en la hoja */
		this.woosimPrinter.controlCommand(posPrecioTotal, posPrecioTotal.length);
		cargarTexto(detalleImprimir.encabezado.str_total_reteIca.substring(0, TAM_MAX_TOTAL) + "\r\n\n", NORMAL);



		this.woosimPrinter.controlCommand(totalizado, totalizado.length);
		cargarTexto("Total: ", NEGRILLA, TEXT_DOBLE_ALTO);

		/* tirar texto a la columna de precio en la hoja */
		this.woosimPrinter.controlCommand(posPrecioTotal, posPrecioTotal.length);
		cargarTexto(detalleImprimir.encabezado.str_valor_neto.substring(0, TAM_MAX_TOTAL) + "\r\n", NORMAL,
				TEXT_DOBLE_ALTO);

		/*Informacion adicional para la factura*/
		cargarTexto(" \r\n\n", NORMAL);
		cargarTexto("Esta factura se asimila para todos sus\r\n", NORMAL);
		cargarTexto("efectos a una letra de cambio,seg�n\r\n", NORMAL);
		cargarTexto("articulo 774 del codigo de comercio.\r\n", NORMAL);
		cargarTexto("Despues del vencimiento se cobraran\r\n", NORMAL);
		cargarTexto("intereses de mora bancarios. \r\n\n", NORMAL);
		cargarTextoCentro("Impreso por: CELUWEB.COM SAS \r\n\n\n", NORMAL);
	}

	/**
	 * Metodo permite dar el maximo tama�o de caracteres permitidos para cada
	 * columna sin perder la forma. (maximo tama�o permitido para la impresora
	 * WSP-R240 = 384 dots = 42 caracteres maximo, 1 caracter = 9 dots )
	 * 
	 * @param detalle
	 */
	private void formatearTexto(DetalleProducto detalle) {

		/*
		 * el nombre debe tener un tama�o max. de 15 caracteres (135 dots de
		 * impresora WSP-R240)
		 */
		if (detalle.nombre != null && detalle.nombre.length() > TAM_MAX_PRODUCTO) {
			detalle.nombre = detalle.nombre.substring(0, TAM_MAX_PRODUCTO);
		} else {
			do {
				detalle.nombre += " ";
			} while (detalle.nombre != null && detalle.nombre.length() < TAM_MAX_PRODUCTO);
		}

		/*
		 * cantidad debe tener un tama�o max. de 7 caracteres (63 dots de
		 * impresora WSP-R240)
		 */
		if (detalle.cantidad != null && detalle.cantidad.length() > TAM_MAX_CANTIDAD) {
			detalle.cantidad = detalle.cantidad.substring(0, TAM_MAX_CANTIDAD);
		} else {
			do {
				detalle.cantidad += " ";
			} while (detalle.cantidad != null && detalle.cantidad.length() < TAM_MAX_CANTIDAD);
		}

		/*
		 * precioUnitario debe tener un tama�o max. de 7 caracteres (54 dots de
		 * impresora WSP-R240)
		 */
		if (detalle.precioUnitario != null && detalle.precioUnitario.length() > TAM_MAX_VALOR_UNIT) {
			detalle.precioUnitario = detalle.precioUnitario.substring(0, TAM_MAX_VALOR_UNIT);
		} else {
			do {
				detalle.precioUnitario += " ";
			} while (detalle.precioUnitario != null && detalle.precioUnitario.length() < TAM_MAX_VALOR_UNIT);
		}

		/*
		 * totalUnitario debe tener un tama�o max. de 14 caracteres (126 dots de
		 * impresora WSP-R240)
		 */
		if (detalle.totalUnitario != null && detalle.totalUnitario.length() > TAM_MAX_TOTAL_UNIT) {
			detalle.totalUnitario = detalle.totalUnitario.substring(0, TAM_MAX_TOTAL_UNIT);
		} else {
			do {
				detalle.totalUnitario += " ";
			} while (detalle.totalUnitario != null && detalle.totalUnitario.length() < TAM_MAX_TOTAL_UNIT);
		}
	}

	/**
	 * Metodo permite la impresion del encabezado que llevan todas las tirillas.
	 *            numero de la factua, debe coincidir con la resolucion DIAN y
	 *            estar vigente.
	 */
	public void generarEncabezadoTirillaInventario() {

		/* define la posicion en la que se ubican los precios */
		final byte[] posCambio = { 27, 36, (byte) 0x3B, 1 };

		/*
		 * define la posicion del texto a mostrar en el encabezado de la tirilla
		 */
		final byte[] posText = { 27, 36, (byte) 122, 0 };
		
		/* define la posicion en la que se ubican los precios */
		byte[] posPrecioTotal = { 27, 36, (byte) 0xFC, 0 };

		/* Define las pocisiones en las que se ubican los totales del pedido */
		byte[] totalizado = { 27, 36, (byte) 0x8E, 0 };
		

		/*
		 * Estimar un tiempo de espera para el hilo de sincronizacion bluetooth
		 */
		try {
			Thread.sleep(Const.timeWait);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		/* Cargar la lista de inventario para entregar */
		//Vector<InformeInventario2> listaInventario = DataBaseBO.CargarInformeInventario2();
		Vector<InformeInventario2> listaInventario = DataBaseBO.CargarInformeInventario33();
		Usuario usuario = DataBaseBO.CargarUsuario();

		inicializarImpresora();

		/* limpiar el buffer */
		this.woosimPrinter.clearSpool();

		imprimirLogoEmpresarial();
		lf();
		lf();
		ff();
		characterSet(); // caracteres especiales para idioma espa�ol
		characterSpacingDefault();
		cargarTextoCentro("\r\nCONTROL DE INVENTARIO\r\n", NEGRILLA);
		cargarTextoCentro("\r\nENTREGA A BODEGA PRINCIPAL\r\n", NEGRILLA);
		cargarTexto("                                          \r\n", NORMAL);

		cargarTexto("Vendedor:", NORMAL);
		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posText, posText.length);
		cargarTexto(usuario.codigoVendedor + "\r\n", NORMAL);
		this.woosimPrinter.controlCommand(posText, posText.length);
		cargarTexto(usuario.nombreVendedor + "\r\n", NORMAL);

		cargarTexto("Fecha Exp.: ", NORMAL);

		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posText, posText.length);
		cargarTexto(Util.FechaActual("yyyy-MM-dd") + "\r\n", NORMAL);

		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posText, posText.length);
		cargarTexto(DateFormat.getTimeInstance().format(new Date()) + "\r\n\n", NORMAL); // hora
		// de
		// impresion

		/* Pintar columnas para detalles */
		cargarTexto("------------------------------------------\r\n", NORMAL);
		//cargarTexto(" Cod   Producto         Actual    Cambios \r\n", NORMAL);
		
		
		String cadena = Util.rpad("Cod", 7, " ")+Util.rpad("Producto", 17, " ")+"  "+Util.rpad("Actual"+"", 8, " ")+Util.rpad("Cambios"+"", 8, " ");
		
		cargarTexto(cadena+"\r\n", NORMAL);
		
		cargarTexto("------------------------------------------\r\n", NORMAL);

		/* Dar formato a cada detalle del pedido. */
		for (InformeInventario2 inventario : listaInventario) {

			//**formatearTextoInventario(inventario);
			//**cargarTexto(inventario.codigo + inventario.nombre + inventario.invActual, NORMAL);

			/* tirar texto a la columna de precio en la hoja */
			//**this.woosimPrinter.controlCommand(posCambio, posCambio.length);
			//**cargarTexto(inventario.cantDevC + "\r\n", NORMAL);
			
			cadena = Util.rpad(inventario.codigo, 7, " ")+Util.rpad(inventario.nombre, 17, " ")+"  "+Util.rpad(inventario.invActual+"", 8, " ")+Util.rpad(inventario.cantDevC+"", 8, " ");
			
			cargarTexto(cadena+"\r\n", NORMAL);
			
			
		}
		cargarTexto("------------------------------------------\r\n\n\n\n", NORMAL);
		cargarTexto(" -----------------      ------------------\r\n", NORMAL);
		cargarTexto("    Recibe.                   Entrega.    \r\n\n", NORMAL);
	}


	/**
	 * Metodo permite la impresion del encabezado que llevan todas las tirillas.
	 * 
	 */
	public void generarEncabezadoTirillaDevoluciones() {

		/* define la posicion en la que se ubican los precios */
		final byte[] posCambio = { 27, 36, (byte) 0x3B, 1 };

		/*
		 * define la posicion del texto a mostrar en el encabezado de la tirilla
		 */
		final byte[] posText = { 27, 36, (byte) 122, 0 };
		
		/* define la posicion en la que se ubican los precios */
		byte[] posPrecioTotal = { 27, 36, (byte) 0xFC, 0 };

		/* Define las pocisiones en las que se ubican los totales del pedido */
		byte[] totalizado = { 27, 36, (byte) 0x8E, 0 };
		

		/*
		 * Estimar un tiempo de espera para el hilo de sincronizacion bluetooth
		 */
		try {
			Thread.sleep(Const.timeWait);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		/* Cargar la lista de inventario para entregar */
		Vector<DevolucionesRealizados> listaDevoluciones = DataBaseBO.CargarInformeDevoluciones(DataBaseBO.ObtenerUsuario());
		Usuario usuario = DataBaseBO.CargarUsuario();

		inicializarImpresora();

		/* limpiar el buffer */
		this.woosimPrinter.clearSpool();

		imprimirLogoEmpresarial();
		lf();
		lf();
		ff();
		characterSet(); // caracteres especiales para idioma espa�ol
		characterSpacingDefault();
		cargarTextoCentro("\r\nDEVOLUCIONES REALIZADAS\r\n", NEGRILLA);
		cargarTextoCentro("\r\nENTREGA A BODEGA PRINCIPAL\r\n", NEGRILLA);
		cargarTexto("                                          \r\n", NORMAL);

		cargarTexto("Vendedor:", NORMAL);
		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posText, posText.length);
		cargarTexto(usuario.codigoVendedor + "\r\n", NORMAL);
		this.woosimPrinter.controlCommand(posText, posText.length);
		cargarTexto(usuario.nombreVendedor + "\r\n", NORMAL);

		cargarTexto("Fecha Exp.: ", NORMAL);

		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posText, posText.length);
		cargarTexto(Util.FechaActual("yyyy-MM-dd") + " "+DateFormat.getTimeInstance().format(new Date())+"\r\n\n", NORMAL);

		/* tirar texto a la mitad de la hoja */
//		this.woosimPrinter.controlCommand(posText, posText.length);
//		cargarTexto(DateFormat.getTimeInstance().format(new Date()) + "\r\n\n", NORMAL); // hora
		// de
		// impresion

		/* Pintar columnas para detalles */
		cargarTexto("------------------------------------------\r\n", NORMAL);
		
		
		
		String cadena = Util.rpad("Producto", 18, " ")+""+Util.rpad("Cant", 4, " ")+"  "+Util.rpad("Vr.Unit"+"", 8, " ")+"  "+Util.rpad("Vr.Total"+"", 8 , " ");
		cargarTexto(cadena+"\r\n", NORMAL);
		
		cargarTexto("------------------------------------------\r\n", NORMAL);
		double total=0;

		/* Dar formato a cada detalle del pedido. */
		for (DevolucionesRealizados devolucionesRealizados : listaDevoluciones) {
			total+=Double.parseDouble(devolucionesRealizados.valorTotal);
			formatearTextoDevoluciones(devolucionesRealizados);

		 cadena = Util.rpad(""+devolucionesRealizados.nombreProducto, 18, " ")+"  "+Util.rpad(""+devolucionesRealizados.cantidad, 4, " ")
		+"  "+Util.rpad(""+""+Util.Redondear(devolucionesRealizados.precio, 0), 8, " ")+Util.rpad(""+""+Util.Redondear(devolucionesRealizados.valorTotal, 0),8 , " ");
		
		cargarTexto(cadena+"\r\n", NORMAL);
			
			/* tirar texto a la columna de precio en la hoja */
			//this.woosimPrinter.controlCommand(posCambio, posCambio.length);
			//cargarTexto(inventario.cantidadSugerido + "\r\n", NORMAL);
		}
		
	
		
		cargarTexto("-----------------------------------------\r\n\n", NORMAL);

		this.woosimPrinter.controlCommand(totalizado, totalizado.length);
		cargarTexto("Total: ", NEGRILLA, TEXT_DOBLE_ALTO);

		/* tirar texto a la columna de precio en la hoja */
		this.woosimPrinter.controlCommand(posPrecioTotal, posPrecioTotal.length);
		cargarTexto(Util.SepararMiles(Util.Redondear(String.valueOf(total), 0)) + "\r\n\n\n\n", NORMAL,TEXT_DOBLE_ALTO);

		
		cargarTexto(" -----------------      ------------------\r\n", NORMAL);
		cargarTexto("    Recibe.                   Entrega.    \r\n\n", NORMAL);
	}


	/**
	 * Metodo permite dar el maximo tama�o de caracteres permitidos para cada
	 * columna sin perder la forma. (maximo tama�o permitido para la impresora
	 * WSP-R240 = 384 dots = 42 caracteres maximo, 1 caracter = 9 dots )
	 *
	 */
	private void formatearTextoInventario(InformeInventario2 inventario) {

		/*
		 * el codigo debe tener un tama�o max. de 6 caracteres (54 dots de
		 * impresora WSP-R240)
		 */
		if (inventario.codigo != null && inventario.codigo.length() > TAM_MAX_CODIGO) {
			inventario.codigo = inventario.codigo.substring(0, TAM_MAX_CODIGO);
		} else {
			do {
				inventario.codigo += " ";
			} while (inventario.codigo != null && inventario.codigo.length() < TAM_MAX_CODIGO);
		}

		/*
		 * el codigo debe tener un tama�o max. de 15 caracteres (135 dots de
		 * impresora WSP-R240)
		 */
		if (inventario.nombre != null && inventario.nombre.length() > TAM_MAX_PRODUCTO_INV) {
			inventario.nombre = inventario.nombre.substring(0, TAM_MAX_PRODUCTO_INV);
		} else {
			do {
				inventario.nombre += " ";
			} while (inventario.nombre != null && inventario.nombre.length() < TAM_MAX_PRODUCTO_INV);
		}

		/*
		 * el codigo debe tener un tama�o max. de 7 caracteres (63 dots de
		 * impresora WSP-R240)
		 */
		if (inventario.strInvInicial != null && inventario.strInvInicial.length() > TAM_MAX_ACTUAL) {
			inventario.strInvInicial = inventario.strInvInicial.substring(0, TAM_MAX_ACTUAL);
		} else {
			do {
				inventario.strInvInicial += " ";
			} while (inventario.strInvInicial != null && inventario.strInvInicial.length() < TAM_MAX_ACTUAL);
		}

		/*
		 * el codigo debe tener un tama�o max. de 14 caracteres (126 dots de
		 * impresora WSP-R240)
		 */
		if (inventario.strCantDevC != null && inventario.strCantDevC.length() > TAM_MAX_CAMBIOS) {
			inventario.strCantDevC = inventario.strCantDevC.substring(0, TAM_MAX_CAMBIOS);
		} else {
			do {
				inventario.strCantDevC += " ";
			} while (inventario.strCantDevC != null && inventario.strCantDevC.length() < TAM_MAX_CAMBIOS);
		}
	}


	/**
	 * Metodo permite dar el maximo tama�o de caracteres permitidos para cada
	 * columna sin perder la forma. (maximo tama�o permitido para la impresora
	 * WSP-R240 = 384 dots = 42 caracteres maximo, 1 caracter = 9 dots )
	 *
	 */

private void formatearTextoDevoluciones(DevolucionesRealizados devolucionesRealizados) {

		/*
		 * el codigo debe tener un tama�o max. de 6 caracteres (54 dots de
		 * impresora WSP-R240)
		 */
//		if (devolucionesRealizados.codigo != null && devolucionesRealizados.codigo.length() > TAM_MAX_CODIGO) {
//			devolucionesRealizados.codigo = devolucionesRealizados.codigo.substring(0, TAM_MAX_CODIGO);
//		} else {
//			do {
//				devolucionesRealizados.codigo += " ";
//			} while (devolucionesRealizados.codigo != null && devolucionesRealizados.codigo.length() < TAM_MAX_CODIGO);
//		}

		/*
		 * el codigo debe tener un tama�o max. de 15 caracteres (135 dots de
		 * impresora WSP-R240)
		 */
		if (devolucionesRealizados.nombreProducto != null && devolucionesRealizados.nombreProducto.length() > TAM_MAX_PRODUCTO_DEV) {
			devolucionesRealizados.nombreProducto = devolucionesRealizados.nombreProducto.substring(0, TAM_MAX_PRODUCTO_DEV);
		} else {
			do {
				devolucionesRealizados.nombreProducto += " ";
			} while (devolucionesRealizados.nombreProducto != null && devolucionesRealizados.nombreProducto.length() < TAM_MAX_PRODUCTO_DEV);
		}

		/*
		 * el codigo debe tener un tama�o max. de 7 caracteres (63 dots de
		 * impresora WSP-R240)
		 */
		if (devolucionesRealizados.cantidad != null &&
			devolucionesRealizados.cantidad.length() > TAM_MAX_CANTIDAD_DEV) {
			devolucionesRealizados.cantidad = devolucionesRealizados.cantidad.substring(0, TAM_MAX_CANTIDAD_DEV);
		} else {
			do {
				devolucionesRealizados.cantidad += " ";
			} while (devolucionesRealizados.cantidad != null && devolucionesRealizados.cantidad.length() < TAM_MAX_CANTIDAD_DEV);
		}

		/*
		 * el codigo debe tener un tama�o max. de 14 caracteres (126 dots de
		 * impresora WSP-R240)
		 */
		if (devolucionesRealizados.precio != null && devolucionesRealizados.precio.length() > TAM_MAX_VALOR_UNIT_DEV) {
			devolucionesRealizados.precio = devolucionesRealizados.precio.substring(0, TAM_MAX_VALOR_UNIT_DEV);
		} else {
			do {
				devolucionesRealizados.precio += " ";
			} while (devolucionesRealizados.precio != null && devolucionesRealizados.precio.length() < TAM_MAX_VALOR_UNIT_DEV);
		}
		
		/*
		 * el codigo debe tener un tama�o max. de 14 caracteres (126 dots de
		 * impresora WSP-R240)
		 */
		if (devolucionesRealizados.valorTotal != null && devolucionesRealizados.valorTotal.length() > TAM_MAX_TOTAL_UNIT_DEV) {
			devolucionesRealizados.valorTotal = devolucionesRealizados.valorTotal.substring(0, TAM_MAX_TOTAL_UNIT_DEV);
		} else {
			do {
				devolucionesRealizados.valorTotal += " ";
			} while (devolucionesRealizados.valorTotal != null && devolucionesRealizados.valorTotal.length() < TAM_MAX_TOTAL_UNIT_DEV);
		}
	}


	/**
	 * Dar formato al detalle de inventario sugerido
	 * @param inventario
	 */
	private void formatearTextoInventarioSugerido(InventarioSugerido inventario) {

		/*
		 * el codigo debe tener un tama�o max. de 6 caracteres (54 dots de
		 * impresora WSP-R240)
		 */
		if (inventario.codigo != null && inventario.codigo.length() > TAM_MAX_CODIGO) {
			inventario.codigo = inventario.codigo.substring(0, TAM_MAX_CODIGO);
		} else {
			do {
				inventario.codigo += " ";
			} while (inventario.codigo != null && inventario.codigo.length() < TAM_MAX_CODIGO);
		}

		/*
		 * el codigo debe tener un tama�o max. de 15 caracteres (135 dots de
		 * impresora WSP-R240)
		 */
		if (inventario.descripcion != null && inventario.descripcion.length() > TAM_MAX_PRODUCTO_INV) {
			inventario.descripcion = inventario.descripcion.substring(0, TAM_MAX_PRODUCTO_INV);
		} else {
			do {
				inventario.descripcion += " ";
			} while (inventario.descripcion != null && inventario.descripcion.length() < TAM_MAX_PRODUCTO_INV);
		}

		/*
		 * el codigo debe tener un tama�o max. de 7 caracteres (63 dots de
		 * impresora WSP-R240)
		 */
		if (inventario.cantidadActual != null && inventario.cantidadActual.length() > TAM_MAX_ACTUAL) {
			inventario.cantidadActual = inventario.cantidadActual.substring(0, TAM_MAX_ACTUAL);
		} else {
			do {
				inventario.cantidadActual += " ";
			} while (inventario.cantidadActual != null && inventario.cantidadActual.length() < TAM_MAX_ACTUAL);
		}

		/*
		 * el codigo debe tener un tama�o max. de 14 caracteres (126 dots de
		 * impresora WSP-R240)
		 */
		if (inventario.cantidadSugerido != null && inventario.cantidadSugerido.length() > TAM_MAX_CAMBIOS) {
			inventario.cantidadSugerido = inventario.cantidadSugerido.substring(0, TAM_MAX_CAMBIOS);
		} else {
			do {
				inventario.cantidadSugerido += " ";
			} while (inventario.cantidadSugerido != null && inventario.cantidadSugerido.length() < TAM_MAX_CAMBIOS);
		}
	}



	/**
	 * Metodo permite la impresion de la tirilla de inventario sugerido.
	 *
	 *            numero de la factua, debe coincidir con la resolucion DIAN y
	 *            estar vigente.
	 */
	public void generarEncabezadoTirillaInventarioSugerido() {

		/* define la posicion en la que se ubican los precios */
		final byte[] posCambio = { 27, 36, (byte) 0x3B, 1 };

		/*
		 * define la posicion del texto a mostrar en el encabezado de la tirilla
		 */
		final byte[] posText = { 27, 36, (byte) 122, 0 };

		/*
		 * Estimar un tiempo de espera para el hilo de sincronizacion bluetooth
		 */
		try {
			Thread.sleep(Const.timeWait);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		/* Cargar la lista de inventario para entregar */
		ArrayList<InventarioSugerido>listaInventario = DataBaseBO.cargarReporteSugeridos();
		Usuario usuario = DataBaseBO.CargarUsuario();

		inicializarImpresora();

		/* limpiar el buffer */
		this.woosimPrinter.clearSpool();

		imprimirLogoEmpresarial();
		lf();
		lf();
		ff();
		characterSet(); // caracteres especiales para idioma espa�ol
		characterSpacingDefault();
		cargarTextoCentro("\r\nCONTROL DE INVENTARIO SUGERIDO\r\n", NEGRILLA);
		cargarTexto("                                          \r\n", NORMAL);

		cargarTexto("Vendedor:", NORMAL);
		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posText, posText.length);
		cargarTexto(usuario.codigoVendedor + "\r\n", NORMAL);
		this.woosimPrinter.controlCommand(posText, posText.length);
		cargarTexto(usuario.nombreVendedor + "\r\n", NORMAL);

		cargarTexto("Fecha Exp.: ", NORMAL);

		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posText, posText.length);
		cargarTexto(Util.FechaActual("yyyy-MM-dd") + "\r\n", NORMAL);

		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posText, posText.length);
		cargarTexto(DateFormat.getTimeInstance().format(new Date()) + "\r\n\n", NORMAL); // hora
		// de
		// impresion

		/* Pintar columnas para detalles */
		cargarTexto("------------------------------------------\r\n", NORMAL);
		//cargarTexto("Cod   Producto        Actual    Sugerido  \r\n", NORMAL);
		
		
		String cadena = Util.rpad("Cod", 7, " ")+Util.rpad("Producto", 25, " ")+""+Util.rpad("Cant.Sug"+"", 8, " ");
		cargarTexto(cadena+"\r\n", NORMAL);
		
		cargarTexto("------------------------------------------\r\n", NORMAL);

		/* Dar formato a cada detalle del pedido. */
		for (InventarioSugerido inventario : listaInventario) {

			formatearTextoInventarioSugerido(inventario);
			//cargarTexto(inventario.codigo + inventario.descripcion + inventario.cantidadActual, NORMAL);

			
			cadena = Util.rpad(""+inventario.codigo.toUpperCase(), 7, " ")+Util.rpad(""+inventario.descripcion, 23, " ")
			+"  "+Util.rpad(""+""+inventario.cantidadSugerido, 8, " ");
			cargarTexto(cadena+"\r\n", NORMAL);
			

			
			/* tirar texto a la columna de precio en la hoja */
			//this.woosimPrinter.controlCommand(posCambio, posCambio.length);
			//cargarTexto(inventario.cantidadSugerido + "\r\n", NORMAL);
		}


		cargarTexto("------------------------------------------\r\n\n\n\n", NORMAL);
		cargarTexto(" -----------------      ------------------\r\n", NORMAL);
		cargarTexto("    Recibe.                   Entrega.    \r\n\n", NORMAL);
	}

	public void generarEncabezadoTirillaRecaudo(SaldoCancelado saldoCancelado, 
			Cliente cliente, Usuario usuario, boolean original){



		/*
		 * Estimar un tiempo de espera para el hilo de sincronizacion bluetooth
		 */
		try {
			Thread.sleep(Const.timeWait);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		/* Tabular al inicio de la hoja. (izquierda) */
		byte[] posNormal = { 27, 36, 0, 0 };

		inicializarImpresora();

		/* limpiar el buffer */
		this.woosimPrinter.clearSpool();

		characterSet(); // caracteres para idioma espa�ol.

		imprimirLogoEmpresarial();
		lf();
		lf();
		ff();
		characterSpacingDefault();


		cargarTexto("\r\nFecha Imp.:", NORMAL);

		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posDerecha, posDerecha.length);
		cargarTexto(Util.FechaActual("yyyy-MM-dd HH:mm:ss") + "\r\n\n", NORMAL);


		cargarTextoCentro("Documento de Recibo de Caja\r\n", NORMAL);
		cargarTextoCentro("No "+saldoCancelado.cod_saldo_canc+"\r\n\n", NORMAL);

		cargarTexto("Direccion:", NORMAL);
		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posDerecha, posDerecha.length);
		cargarTexto("Cra 23 No. 164-36 Toberin.\r\n", NORMAL);

		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posDerecha, posDerecha.length);
		cargarTexto("www.panelcountry.com\r\n", NORMAL);

		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posDerecha, posDerecha.length);
		cargarTexto("Bogota D.C\r\n", NORMAL);

		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posNormal, posNormal.length);
		cargarTexto("Telefonos: ", NORMAL);

		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posDerecha, posDerecha.length);
		cargarTexto("+593 4-210-3514\r\n\n", NEGRILLA);

		cargarTexto("Cliente:", NORMAL);

		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posDerecha, posDerecha.length);
		cargarTexto(cliente.nombre+"\r\n", NORMAL);

		cargarTexto("Vendedor:", NORMAL);

		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posDerecha, posDerecha.length);
		cargarTexto(usuario.nombreVendedor+"\r\n", NORMAL);

		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posDerecha, posDerecha.length);
		cargarTexto((original? "ORIGINAL":"COPIA") + "\r\n\n", NORMAL);




		/*if(cliente != null){
			cargarTexto("Cliente: ", NORMAL);

			this.woosimPrinter.controlCommand(posDerecha, posDerecha.length);
			cargarTexto(cliente.razonSocial + "\r\n", NORMAL);
			this.woosimPrinter.controlCommand(posDerecha, posDerecha.length);
			cargarTexto(cliente.nombre + "\r\n", NORMAL);

			cargarTexto("NIT: ", NORMAL);
			this.woosimPrinter.controlCommand(posDerecha, posDerecha.length);
			cargarTexto(cliente.nit + "\r\n", NORMAL);
		}

		cargarTexto("\r\n", NORMAL);*/

		float [] valores = DataBaseBOJ.getValores(saldoCancelado.cod_saldo);
		float saldoPendinte = 0;
		float saldoCliente = 0;
		String tipo = "";

		if(valores != null){
			saldoCliente  = valores[0];
			saldoPendinte = valores[1];

			if(saldoPendinte == 0)
				tipo = "total";
			else
				tipo = "parcial";
		}

		cargarTexto("Yo "+usuario.nombreVendedor+
				" con documento de identificaci�n "+usuario.codigoVendedor+
				" en representaci�n de "+NOMBREEMPRESA+
				" certifico que he recibido por parte del se�or "+cliente.nombre+" quien actua"+
				" en representaci�n de "+cliente.razonSocial+
				" la cantidad de $"+saldoCancelado.saldo_recibido+" por motivo del "+
				" recaudo no "+saldoCancelado.cod_saldo_canc+", el c�al  corresponde al pago "+
				tipo+"  de la factura con  no "+saldoCancelado.factura+" "+
				" por valor  total  de $"+saldoCliente+", adeudando un saldo "+
				" por valor de $"+saldoPendinte+" \r\n\n",NORMAL);	

		cargarTexto("------------------------------------------\r\n\n", NORMAL);

		cargarTexto("Valor Total: ", NORMAL);
		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posDerecha, posDerecha.length);
		cargarTexto("$"+saldoCliente+"\r\n", NORMAL);

		cargarTexto("Abono: ", NORMAL);
		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posDerecha, posDerecha.length);
		cargarTexto("$"+saldoCancelado.saldo_recibido+"\r\n", NORMAL);

		cargarTexto("Saldo: ", NORMAL);
		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posDerecha, posDerecha.length);
		cargarTexto("$"+saldoPendinte+"\r\n\n\n\n", NORMAL);

		cargarTexto(" -----------------      ------------------\r\n", NORMAL);
		cargarTexto("    Cliente.                   Vendedor.    \r\n\n", NORMAL);

	}

	/**
	 * Metodo permite la impresion del encabezado que llevan todas las tirillas de un Deposito.

	 *            numero de la factua, debe coincidir con la resolucion DIAN y
	 *            estar vigente.
	 */
	public void generarEncabezadoTirillaDeposito(Deposito deposito, boolean original) {

		/*
		 * Estimar un tiempo de espera para el hilo de sincronizacion bluetooth
		 */
		try {
			Thread.sleep(Const.timeWait);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		/* Tabular al inicio de la hoja. (izquierda) */
		byte[] posNormal = { 27, 36, 0, 0 };

		inicializarImpresora();

		/* limpiar el buffer */
		this.woosimPrinter.clearSpool();

		characterSet(); // caracteres para idioma espa�ol.

		imprimirLogoEmpresarial();
		lf();
		lf();
		ff();
		characterSpacingDefault();
		cargarTextoCentro("\r\nINDUSTRIA PANIFICADORA\r\n", NEGRILLA);
		cargarTextoCentro("EL COUNTRY LTDA.\r\n", NEGRILLA);
		cargarTextoCentro("NIT: 800.064.126-6\r\n", NEGRILLA);
		cargarTextoCentro("IVA REGIMEN COMUN\r\n\n", NEGRILLA);
		cargarTextoCentro("SOMOS GRANDES CONTRIBUYENTES\r\n", NORMAL);
		cargarTextoCentro("Y AGENTES DE RETENCION\r\n", NORMAL);
		cargarTextoCentro("ACTIVIDAD ECONOMICA 1551\r\n", NORMAL);
		cargarTextoCentro("TARIFA ICA 4.14 x 1000\r\n\n", NORMAL);


		cargarTextoCentro("DEPOSITO VENDEDOR\r\n\n", NORMAL);


		Usuario usuario = DataBaseBO.ObtenerUsuario();

		cargarTexto("Fecha Impres�on:", NORMAL);
		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posResolucion, posResolucion.length);
		cargarTexto(Util.FechaActual("yyyy-MM-dd HH:mm:ss") + "\r\n", NORMAL);

		cargarTexto("Vendedor:", NORMAL);
		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posResolucion, posResolucion.length);
		cargarTexto(usuario.nombreVendedor + "\r\n", NORMAL);

		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posDerecha, posDerecha.length);
		cargarTexto((original? "ORIGINAL":"COPIA") + "\r\n\n", NORMAL);

		cargarTexto("Numero Comprobante:", NORMAL);
		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posResolucion, posResolucion.length);
		cargarTexto(deposito.compr_bancario + "\r\n", NORMAL);

		cargarTexto("Banco:", NORMAL);
		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posResolucion, posResolucion.length);
		cargarTexto(deposito.desc_banco + "\r\n", NORMAL);

		cargarTexto("Observaci�n:", NORMAL);
		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posResolucion, posResolucion.length);
		cargarTexto(deposito.observacion + "\r\n", NORMAL);

		cargarTexto("Numero Dep.: ", NORMAL);
		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posResolucion, posResolucion.length);
		cargarTexto(deposito.documento + "\r\n", NORMAL);

		cargarTexto("Cuenta Contable:", NORMAL);
		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posResolucion, posResolucion.length);
		cargarTexto(usuario.cuenta_contable + "\r\n", NORMAL);

		cargarTexto("Cantidad Cheques:", NORMAL);
		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posResolucion, posResolucion.length);
		cargarTexto(deposito.cant_cheques + "\r\n", NORMAL);

		cargarTexto("Total Deposito:", NORMAL);
		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posResolucion, posResolucion.length);
		cargarTexto(Util.SepararMiles(deposito.totalDeposito + "" ) + "\r\n", NORMAL);

		cargarTexto("\r\n", NORMAL);// impresion

		/* Pintar columnas para detalles */
		// cargarTexto("------------------------------------------\r\n",NORMAL);
		// cargarTexto(" Cod Producto Cant Total+Imp. \r\n", NORMAL);
		// cargarTexto("------------------------------------------\r\n",NORMAL);

		//
		cargarTexto("------------------------------------------\r\n", NORMAL);
		cargarTexto(" Pagos Relacionados                       \r\n", NORMAL);
		cargarTexto("------------------------------------------\r\n", NORMAL);
		cargarTexto("------------------------------------------\r\n", NORMAL);
		cargarTexto(" Cliente            N. Factura      Valor \r\n", NORMAL);
		cargarTexto("------------------------------------------\r\n", NORMAL);
		//
	}

	/**
	 * Metodo permite formar los detalles que seran impresos. alineados de
	 * acuerdo a la impresora.

	 */
	public void generarDetalleTirillaDeposito(ArrayList<DetalleDeposito> detallesDeposito) {

		/* Dar formato a cada detalle del pedido. */
		for (DetalleDeposito detalle : detallesDeposito) {
			formatearTextoDetalleDeposito(detalle);
			cargarTexto(detalle.razon_social_cliente + detalle.factura + detalle.saldoString, NORMAL);
		}

		//		/* Pintar columnas para detalles */
		//		this.woosimPrinter.controlCommand(subRayadoOn, subRayadoOn.length);
		//		cargarTexto("                                          \r\n", NORMAL);
		//		this.woosimPrinter.controlCommand(subRayadoOff, subRayadoOff.length);

		/*
		 * Pintar datos de costos de encabezado (subtotal, iva y total. ) a 160
		 * dots.
		 */
		//		this.woosimPrinter.controlCommand(totalizado, totalizado.length);
		//		cargarTexto("Subtotal: ", NORMAL);
		//
		//		this.woosimPrinter.controlCommand(totalizado, totalizado.length);
		//		cargarTexto("Total: ", NEGRILLA, TEXT_DOBLE_ALTO);

		//		/* tirar texto a la columna de precio en la hoja */
		//		this.woosimPrinter.controlCommand(posPrecioTotal, posPrecioTotal.length);
		//		cargarTexto(detalleImprimir.encabezado.str_valor_neto.substring(0, TAM_MAX_TOTAL) + "\r\n", NORMAL,
		//				TEXT_DOBLE_ALTO);

		/*Informacion adicional para la factura*/
		cargarTexto(" \r\n\n", NORMAL);
		cargarTexto("______________              ______________\r\n", NORMAL);
		cargarTexto("Firma Vendedor              Firma Recibe \r\n", NORMAL);
	}


	/**
	 * Metodo permite dar el maximo tama�o de caracteres permitidos para cada
	 * columna sin perder la forma. (maximo tama�o permitido para la impresora
	 * WSP-R240 = 384 dots = 42 caracteres maximo, 1 caracter = 9 dots )
	 * 
	 * @param detalle
	 */
	private void formatearTextoDetalleDeposito(DetalleDeposito detalle) {

		/*
		 * el nombre debe tener un tama�o max. de 15 caracteres (135 dots de
		 * impresora WSP-R240)
		 */
		if (detalle.razon_social_cliente != null && detalle.razon_social_cliente.length() > TAM_MAX_CLIENTE) {
			detalle.razon_social_cliente = detalle.razon_social_cliente.substring(0, TAM_MAX_CLIENTE);
		} else {

			detalle.razon_social_cliente = Util.rpad(detalle.razon_social_cliente, TAM_MAX_CLIENTE, " ");
			//			do {
			//				detalle.razon_social_cliente += " ";
			//			} while (detalle.razon_social_cliente != null && detalle.razon_social_cliente.length() < TAM_MAX_CLIENTE);
		}

		/*
		 * cantidad debe tener un tama�o max. de 7 caracteres (63 dots de
		 * impresora WSP-R240)
		 */
		if (detalle.factura != null && detalle.factura.length() > TAM_MAX_FACTURA) {
			detalle.factura = detalle.factura.substring(0, TAM_MAX_FACTURA);
		} else {

			detalle.factura = Util.lpad(detalle.factura, TAM_MAX_FACTURA, " ");
			//			do {
			//				detalle.factura += " ";
			//			} while (detalle.factura != null && detalle.factura.length() < TAM_MAX_FACTURA);
		}

		/*
		 * precioUnitario debe tener un tama�o max. de 7 caracteres (54 dots de
		 * impresora WSP-R240)
		 */
		if (detalle.saldoString != null && detalle.saldoString.length() > TAM_MAX_VALOR) {
			detalle.saldoString = detalle.saldoString.substring(0, TAM_MAX_VALOR);
		} else {

			detalle.saldoString = Util.lpad(detalle.saldoString, TAM_MAX_VALOR, " ");

			//			do {
			//				detalle.saldoString += " ";
			//			} while (detalle.saldoString != null && detalle.saldoString.length() < TAM_MAX_VALOR);
		}
	}

	/**
	 * Inicializar la impresora.
	 */
	public int inicializarImpresora() {
		return this.woosimPrinter.controlCommand(initialize, initialize.length);
	}

	/**
	 * Inicializar la impresora a 42 cpl (caracteres por linea).
	 */
	public int characterSpacingDefault() {
		return this.woosimPrinter.controlCommand(characterSpacing, characterSpacing.length);
	}

	/**
	 * Seleccionar set de caracteres para idioma espa�ol.
	 */
	public int characterSet() {
		return this.woosimPrinter.controlCommand(characterSet, characterSet.length);
	}

	/**
	 * Imprimir los datos en el bufer y alimenta una linea y vuelve al inicio de
	 * la linea
	 */
	public int lf() {
		return this.woosimPrinter.controlCommand(LF, LF.length);
	}

	/**
	 * Imprimir los datos en el bufer y alimenta una linea y vuelve al inicio de
	 * la linea
	 */
	public int ff() {
		return this.woosimPrinter.controlCommand(FF, FF.length);
	}

	/**
	 * Justificar al centro el siguiente texto a este comando.
	 */
	public int centrar() {
		return this.woosimPrinter.controlCommand(centrar, centrar.length);
	}

	/**
	 * Justificar a la derecha el siguiente texto a este comando.
	 */
	public int izquierda() {
		return this.woosimPrinter.controlCommand(izquierda, izquierda.length);
	}

	/**
	 * @return the woosimPrinter
	 */
	public WoosimPrinter getWoosimPrinter() {
		return woosimPrinter;
	}

	/**
	 * @param woosimPrinter
	 *            the woosimPrinter to set
	 */
	public void setWoosimPrinter(WoosimPrinter woosimPrinter) {
		this.woosimPrinter = woosimPrinter;
	}
	
	
	public void generarEncabezadoTirillaCuadreCaja(long  totalVenta,long saldosDepositados,long totalVentaCredito,long totalVentaContado,long recaudo,long recaudoEfectivo,long recaudoCheque,long recaudoChequePostF,long recaudoTransferencia) {

	
	
		/* define la posicion en la que se ubican los precios */
		final byte[] posCambio = { 27, 36, (byte) 0x3B, 1 };

		/*
		 * define la posicion del texto a mostrar en el encabezado de la tirilla
		 */
		final byte[] posText = { 27, 36, (byte) 122, 0 };
		
		/* define la posicion en la que se ubican los precios */
		byte[] posPrecioTotal = { 27, 36, (byte) 0xFC, 0 };

		/* Define las pocisiones en las que se ubican los totales del pedido */
		byte[] totalizado = { 27, 36, (byte) 0x8E, 0 };
		

		/*
		 * Estimar un tiempo de espera para el hilo de sincronizacion bluetooth
		 */
		try {
			Thread.sleep(Const.timeWait);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Usuario usuario = DataBaseBO.CargarUsuario();

		inicializarImpresora();

		/* limpiar el buffer */
		this.woosimPrinter.clearSpool();

		imprimirLogoEmpresarial();
		lf();
		lf();
		ff();
		characterSet(); // caracteres especiales para idioma espa�ol
		characterSpacingDefault();
		cargarTextoCentro("\r\nCUADRE DE CAJA\r\n", NEGRILLA);
		cargarTexto("                                          \r\n", NORMAL);

		cargarTexto("Vendedor:", NORMAL);
		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posText, posText.length);
		cargarTexto(usuario.codigoVendedor + "\r\n", NORMAL);
		this.woosimPrinter.controlCommand(posText, posText.length);
		cargarTexto(usuario.nombreVendedor + "\r\n", NORMAL);

	
		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posText, posText.length);
		cargarTexto(Util.FechaActual("yyyy-MM-dd") + "\r\n", NORMAL);

		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posText, posText.length);
		cargarTexto(DateFormat.getTimeInstance().format(new Date()) + "\r\n\n", NORMAL); // hora
		// de
		// impresion

		
		/* Pintar columnas para detalles */
	
		cargarTexto("------------------------------------------\r\n", NORMAL);
		cargarTexto("Total Venta En Clientes CREDITO \r\n", NORMAL);
		cargarTexto(""+Util.SepararMiles(totalVentaCredito+"")+"\r\n", NORMAL);
		cargarTexto("------------------------------------------\r\n", NORMAL);
		
		
		cargarTexto("Total Venta En Clientes CONTADO \r\n", NORMAL);
		cargarTexto(""+Util.SepararMiles(totalVentaContado+"")+"\r\n", NORMAL);
		cargarTexto("------------------------------------------\r\n", NORMAL);
		
		
		cargarTexto("Total Venta \r\n", NORMAL);
		cargarTexto(""+Util.SepararMiles(totalVenta+"")+"\r\n", NORMAL);
		cargarTexto("------------------------------------------\r\n", NORMAL);
		
		
		
		cargarTexto("Saldos Depositados \r\n", NORMAL);
		cargarTexto(""+Util.SepararMiles(saldosDepositados+"")+"\r\n", NORMAL);
		cargarTexto("------------------------------------------\r\n", NORMAL);
		
		cargarTexto(" \r\n", NORMAL);
		cargarTexto(" \r\n", NORMAL);
		
		cargarTexto("Recaudos \r\n", NORMAL);
		
		cargarTexto(" \r\n", NORMAL);
		cargarTexto(" \r\n", NORMAL);
		
		
		cargarTexto("Recaudo Efectivo \r\n", NORMAL);
		cargarTexto(""+Util.SepararMiles(recaudoEfectivo+"")+"\r\n", NORMAL);
		cargarTexto("------------------------------------------\r\n", NORMAL);
	

		cargarTexto("Recaudo Cheque \r\n", NORMAL);
		cargarTexto(""+Util.SepararMiles(recaudoCheque+"")+"\r\n", NORMAL);
		cargarTexto("------------------------------------------\r\n", NORMAL);
	
		
		cargarTexto("Recaudo Cheque PostFecxhado \r\n", NORMAL);
		cargarTexto(""+Util.SepararMiles(recaudoChequePostF+"")+"\r\n", NORMAL);
		cargarTexto("------------------------------------------\r\n", NORMAL);
	
		
		cargarTexto("Recaudo Tranferencia \r\n", NORMAL);
		cargarTexto(""+Util.SepararMiles(recaudoTransferencia+"")+"\r\n", NORMAL);
		cargarTexto("------------------------------------------\r\n", NORMAL);
	
		
		cargarTexto("Total Recaudo \r\n", NORMAL);
		cargarTexto(""+Util.SepararMiles(recaudo+"")+"\r\n", NORMAL);
		cargarTexto("------------------------------------------\r\n", NORMAL);
	
		
	    String fechaActual = Util.FechaActual("yyyy-MM-dd HH:mm:ss");
		
		cargarTexto("Fecha/Hora Impresion:"+fechaActual+ "\r\n"+ "\r\n"+ "\r\n", NORMAL);
		
		
		
		if(DataBaseBO.yaTerminoLabores()) {
			
			
			cargarTexto("El Vendedor Ya Termino Labores \r\n", NORMAL);
			
		}else {
			
			cargarTexto("El Vendedor No ha Terminado Labores \r\n", NORMAL);
			
		}
		
	
		
		cargarTexto("------------------------------------------\r\n", NORMAL);

	
	}
	
	
	
	
	
	
	public void generarEncabezadoTirillaInventarioConteo() {

		/* define la posicion en la que se ubican los precios */
		final byte[] posCambio = { 27, 36, (byte) 0x3B, 1 };

		/*
		 * define la posicion del texto a mostrar en el encabezado de la tirilla
		 */
		final byte[] posText = { 27, 36, (byte) 122, 0 };
		
		/* define la posicion en la que se ubican los precios */
		byte[] posPrecioTotal = { 27, 36, (byte) 0xFC, 0 };

		/* Define las pocisiones en las que se ubican los totales del pedido */
		byte[] totalizado = { 27, 36, (byte) 0x8E, 0 };
		

		/*
		 * Estimar un tiempo de espera para el hilo de sincronizacion bluetooth
		 */
		try {
			Thread.sleep(Const.timeWait);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		/* Cargar la lista de inventario para entregar */
		Usuario usuario = DataBaseBO.CargarUsuario();

		inicializarImpresora();

		/* limpiar el buffer */
		this.woosimPrinter.clearSpool();

		imprimirLogoEmpresarial();
		lf();
		lf();
		ff();
		characterSet(); // caracteres especiales para idioma espa�ol
		characterSpacingDefault();
		cargarTextoCentro("\r\nAUDITORIA\r\n", NEGRILLA);
		cargarTextoCentro("\r\nCONTEO DE INVENTARIO\r\n", NEGRILLA);
		cargarTexto("                                          \r\n", NORMAL);

		cargarTexto("Vendedor:", NORMAL);
		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posText, posText.length);
		cargarTexto(usuario.codigoVendedor + "\r\n", NORMAL);
		this.woosimPrinter.controlCommand(posText, posText.length);
		cargarTexto(usuario.nombreVendedor + "\r\n", NORMAL);

		cargarTexto("Fecha Exp.: ", NORMAL);

		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posText, posText.length);
		cargarTexto(Util.FechaActual("yyyy-MM-dd") + "\r\n", NORMAL);

		/* tirar texto a la mitad de la hoja */
		this.woosimPrinter.controlCommand(posText, posText.length);
		cargarTexto(DateFormat.getTimeInstance().format(new Date()) + "\r\n\n", NORMAL); // hora
		// de
		// impresion

		/* Pintar columnas para detalles */
		cargarTexto("------------------------------------------\r\n", NORMAL);
		//cargarTexto(" Cod   Producto         Actual    Cambios \r\n", NORMAL);
		
		
		String cadena = Util.rpad("Cod", 7, " ")+Util.rpad("Producto", 15, " ")+"  "+Util.rpad("Act."+"", 6, " ")+Util.rpad("Cont."+"", 6, " ")+Util.rpad("Dif"+"", 6, " ");
		
		cargarTexto(cadena+"\r\n", NORMAL);
		
		cargarTexto("------------------------------------------\r\n", NORMAL);

		Vector<Producto> listaInventario = DataBaseBO.BuscarTodosProductosConteoInventario();
		
		
		/* Dar formato a cada detalle del pedido. */
		for (Producto producto : listaInventario) {

			//**formatearTextoInventario(inventario);
			//**cargarTexto(inventario.codigo + inventario.nombre + inventario.invActual, NORMAL);

			/* tirar texto a la columna de precio en la hoja */
			//**this.woosimPrinter.controlCommand(posCambio, posCambio.length);
			//**cargarTexto(inventario.cantDevC + "\r\n", NORMAL);
			
			cadena = Util.rpad(""+producto.codigo, 7, " ")+Util.rpad(""+producto.descripcion, 15, " ")+"  "+Util.rpad(""+producto.inventarioMaquina, 6, " ")+Util.rpad(""+producto.inventarioConteo, 6, " ")+Util.rpad(""+String.valueOf(producto.inventarioConteo-producto.inventarioMaquina), 6, " ");
			
			cargarTexto(cadena+"\r\n", NORMAL);
			
			
		}
		cargarTexto("------------------------------------------\r\n\n\n\n", NORMAL);
		cargarTexto(" -----------------      ------------------\r\n", NORMAL);
		cargarTexto("    Recibe.                   Entrega.    \r\n\n", NORMAL);
	}
	
	
	

}
