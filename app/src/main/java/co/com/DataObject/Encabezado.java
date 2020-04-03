package co.com.DataObject;

import java.io.Serializable;

public class Encabezado implements Serializable {

	private static final long serialVersionUID = 3L;
	
	public String numeroFactura = "";
	public float retefuente = 0.0f;
	public float reteIca = 0.0f;
	public float valorAplicaRetefuntesIca = 925000f;
	public float total_retefuente = 0.0f;
	public String str_total_retefuente;
	public float total_reteIca= 0.0f;
	public String str_total_reteIca;
	
	public String codigo_cliente;  //Codigo del Cliente
	public String nombre_cliente;  //Nombre del Cliente, solo es para mostrar el Nombre en pantalla
	public String razon_social;    //Razon Social
	public int codigo_novedad;     //1 -> Venta, para no compra es MotivosCompras.codigo
	
	public String lista_precio;       //Lista de Precio
	public int fiado;
	
	public String str_valor_neto;       //Valor Neto con Separacion de Miles, se usa solo para mostrar en Pantalla
	public float valor_neto;            //Valor Neto de la Venta      (sub_total + total_iva)
	public long valor_total;			//valor cantidad x precio,
	public float sub_total;             //Valor Sub Total de la Venta (precio * cantidad)
	public String str_sub_total;
	
	public float descuento;
	public String str_descuento;
	
	public float base;
	public String str_base;
	
	public float total_iva;             //Valor Total IVA
	public String str_total_iva;
	public float valor_descuento;       //Valor del Descuento
	
	public String observacion;     //Observacion del Pedido
	public String hora_inicial;    //Hora Inicial
	public String hora_final;      //Hora Final
	public String tipo_cliente;    //Tipo Cliente (Cliente viejo o Cliente nuevo)
	public int extra_ruta;         //Indica si el cliente del Pedido es Extra Ruta, es decir no esta en el rutero
	
	public String numero_doc;      //Numero Unico generado por cada encabezado
	public int tipoTrans;          //0 -> Pedido, 2  -> Cambio (Tabla Encabezado -> tipoTrans)
	public int motivoCambio;       //El codigo lo da tabla MotivosCambio (Tabla Detalle -> motivo)
	public int motivo;             //1 -> Pedido, 10 -> Cambio. Para No Compra el motivo lo da la tabla NoCompra (Tabla NovedadesCompras -> motivo)
	
	public int sync;
	public int sn_envio = -1;
	
	public String fecha;
	
	public String tipoDoc = "";
	
	public String factura = "";
	
	
	public String fp = "";
	
	public String resolucion = "";
	public String prefijo;
	public long consecutivoResolucion;
	public boolean pago;
	
	//public boolean carteraVencida; //Indica si Tiene Cartera Vencida
	//public boolean excedeCupo;     //Indica si Excede el Cupo
}