package co.com.panificadoracountry;

public class Const {
	
	public final static String TITULO;
	public final static String URL_SYNC;
	public final static String URL_SYNC_TRAMAS;
	public final static String URL_DOWNLOAD_NEW_VERSION;
	public final static String URL_LOGO_IMPRESORA;
	
	private final static int PRUEBAS    = 1;
	private final static int PRODUCCION = 2;
	
	public final static int PREVENTA  = 2;
	public final static int AUTOVENTA = 1;
	public final static float RETENCION_PORCENTAJE = 2.5f;
	public final static float RETENCIO_ICA = 1.104f;
	
	public static int tipoAplicacion = 0; 

	
	public final static String CONFIG_IMPRESORA = "PRINTER";
	public final static String MAC_IMPRESORA    = "MAC";
	public final static String LABEL_IMPRESORA  = "LABEL";
	
	public final static int timeWait = 6 * 1000;
	
	/**
	 * IMPORTANTE: Indica que aplicacion se va a generar
	 **/
	private final static int APLICACION = PRUEBAS;
	
	static {
		
		switch (APLICACION) {
		
			case PRUEBAS:
				TITULO = " - Demo ";
				URL_SYNC = "http://66.33.97.175/SyncPanificadoraPruebas/";
				URL_SYNC_TRAMAS = "http://66.33.97.175/syncAnd/";
				URL_DOWNLOAD_NEW_VERSION = "http://64.239.115.11/sync/app/PanificadoraCountryPruebas.apk";
				URL_LOGO_IMPRESORA = "http://66.33.97.175/SyncPanificadoraCountry/Images/logo.bmp";
				break;

			case PRODUCCION:
				TITULO = " - Produccion ";	
				URL_SYNC = "http://66.33.97.175/SyncPanificadoraCountry/";
				URL_SYNC_TRAMAS = "http://66.33.69.75/syncAnd/";
				URL_DOWNLOAD_NEW_VERSION = "http://64.239.115.11/PanificadoraCountry/PanificadoraCountry.apk";
				URL_LOGO_IMPRESORA = "http://66.33.97.175/SyncPanificadoraCountry/Images/logo.bmp";
				break;
				
			default:
				TITULO = " - Sin Definir";
				URL_SYNC = "Sin_Definir";
				URL_SYNC_TRAMAS = "http://66.33.69.75/syncAnd/";
				URL_DOWNLOAD_NEW_VERSION = "Sin_Definir";
				URL_LOGO_IMPRESORA = "http://66.33.97.175/SyncPanificadoraCountry/Images/logo.bmp";
				break;
		}
	}
	
	/**
	 * DEFINCION DE LAS CONSTANTES DE LA APLICACION
	 **/
	public final static int PEDIDO_VENTA = 1;	
	public final static int PEDIDO_CAMBIO = 2;
	public final static int PEDIDO_INVENTARIO = 3;
	public final static int PEDIDO_PROMOCION = 4;
	
	public final static int RESP_CERRAR_SESION        = 1000;	
	public final static int RESP_PEDIDO_EXITOSO       = 1001;	
	public final static int RESP_NO_COMPRA_EXITOSO    = 1002;
	public final static int RESP_BUSQUEDA_PRODUCTO    = 1003;
	public final static int RESP_FORM_RESUMEN         = 1004;
	public final static int RESP_FROM_AGREGAR_CARTERA = 1005;
	public final static int RESP_FORM_OPCIONES_PAGO   = 1006;
	public final static int RESP_RECAUDO_EXITOSO      = 1007;
	public final static int RESP_ULTIMO_PEDIDO        = 1008;
	public final static int RESP_FORM_INICIAR_DIA     = 1009;
	public final static int RESP_ACTUALIZAR_VERSION   = 1010;
	public final static int RESP_TOMAR_FOTO           = 1011;
	public final static int RESP_FORMAS_DE_PAGO       = 1012;
	public final static int RESP_EDITAR_CLIENTE       = 1013;
	public final static int RESP_CLIENTE_NUEVO        = 1014;
	public final static int RESP_FORM_LOGIN           = 1015;
	
	
	public final static int RESP_FORM_DETALLE_GASTOS    = 1017;
	
	public final static int RESP_FORM_DETALLE_TIEMPOS   = 1018;
	
	
	
	public final static int RESP_FORM_MERC_PRECIO_DETALLE = 1019;
	
	
	
	public final static int LOGIN                  = 1;
	public final static int LOGOUT                 = 2;	
	public final static int DOWNLOAD_DATA_BASE     = 3;
	public final static int ENVIAR_PEDIDO          = 4;
	public final static int ENVIAR_PEDIDO_TERMINAR = 5;
	public final static int DOWNLOAD_VERSION_APP   = 6;
	public final static int TERMINAR_LABORES       = 7;
	public final static int VALIDAR_NIT            = 8;
	public final static int DOWNLOAD_MESSAGE       = 9;
	public final static int STATUS_SENAL           = 10;
	public final static int VERIFICAR_EDICION_PEDIDO = 11;
	public final static int ENVIAR_COORDENADAS     = 12;
	public final static int VERIFICAR_ELIMINACION_PEDIDO = 13;
	public final static int DOWNLOAD_LOGO_IMPRESORA  = 14;
	
	public final static String CLIENTE_VIEJO     = "V";
	public final static String TODOS             = "Todos";
	public final static String POR_NOMBRE        = "Parte Nombre";
	public final static String POR_CODIGO        = "Parte Codigo";
	public final static String POR_RAZON_SOCIAL  = "Parte Razon Social";
	public final static String POR_PLU           = "Parte Plu";
	public final static String POR_CODIGO_        = "Por Codigo";
	
	public final static String MERCADEO_PRECIOS       = "PRECIOS";	
	public final static String MERCADEO_AGOTADOS      = "AGOTADOS";	
	public final static String MERCADEO_EXHIBICIONES  = "EXHIBICIONES";
	
	public final static int MAX_ITEMS = 999;
	
	public final static int LUNES     = 0;  // Calendar.MONDAY    (2)
	public final static int MARTES    = 1;  // Calendar.TUESDAY   (3)
	public final static int MIERCOLES = 2;  // Calendar.WEDNESDAY (4)
	public final static int JUEVES    = 3;  // Calendar.THURSDAY  (5)
	public final static int VIERNES   = 4;  // Calendar.FRIDAY    (6)
	public final static int SABADO    = 5;  // Calendar.SATURDAY  (7)
	public final static int DOMINGO   = 6;  // Calendar.SUNDAY    (1)
	
	public final static String nameDirApp = "PanificadoraCountry";
	public final static String fileNameApk = "PanificadoraCountry.apk";
	
	public final static String EFECTIVO = "EF";
	public final static String CREDITO  = "CR";
	
	
	public final static String SETTINGS_RECAUDO = "settings_recaudo";
	public final static String CONSECUTIVO_RECAUDO = "consecutivo_recaudo";
	
	
	public final static int MOD_EFECTIVIDAD = 1;
	public final static int MOD_PEDIDOS_REALIZADOS = 2;
	public final static int MOD_INFORME_DE_VENTAS = 3;
	public final static int MOD_INVENTARIO = 4;
	public final static int MOD_KARDEX= 5;
	public final static int MOD_IMPRESORA = 6;
	public final static int MOD_PEDIDOS= 7;
	public final static int MOD_CAMBIOS = 8;
	public final static int MOD_NO_COMPRA = 9;
	public final static int MOD_CARTERA = 10;
	public final static int MOD_RECAUDO = 11;
	public final static int MOD_EDITAR_CLIENTE = 12;
	public final static int MOD_MERCADEO= 13;
	public final static int MOD_ESTADISTICAS_SALIR= 14;
	public final static int MOD_INFO_CLIENTE_SALIR= 15;
	public final static int MOD_CARGUE_INVENTARIO = 15;
	public final static int MOD_CARGAR_INVENTARIO_SUGERIDO = 16;
	public final static int MOD_DEPOSITOS_REALIZADOS = 17;
	public final static int MOD_RACAUDOS_REALIZADOS = 18;
	public final static int MOD_CAMBIOS_DEVOLUCIONES = 19;
	public final static int MOD_CERTIFICADO = 20;
	
	
	public final static String NUM_TEMP = "NT";
	
	public final static int FACTURA_EXCEDIO_LIMITE = 0;
	public final static int FACTURA_OK = 1;
	public final static int FACTURA_REPETIDA = 2;
	public final static int FACTURA_CONTEO_FALLO = 3;
	
	public final static int DEPOSITO_PEND_REVISION = 1;
	public static final String LISTA_PRECIO_DEFECTO = "LISTA 1";
	
	public final static double DISTANCIA_MAX = 50;
	
}
