package co.com.DataObject;

import java.io.Serializable;

public class Detalle implements Serializable {

	private static final long serialVersionUID = 4L;
	
	public int cod_encabezado;         //Codigo Encabezado
	public String codigo_cliente;      //Codigo del Cliente
	public String codigo_producto;     //Codigo del Producto
	public String desc_producto;       //Descripcion del Producto se usa para mostrar en Pantalla
	public float cantidadInv;
	public String codLinea;
	
	public float precio;               //Precio del Producto
	public float iva;                  //Iva del Producto
	public float descuento_autorizado; //Descuento autorizado por linea
	public float cantidad;               //Cantidad del producto
	public int tipo_pedido;            //Para efectos del demo solo es 1(Venta)
	
	public String codMotivo;  //Codigo del Motivo, solo aplica para Cambios

	public String lote;					
	public String fechaFabricacion;
	public String fechaVencimiento;
	
	public String toString() {
		
		return "cod_pro: " + codigo_producto + ", cant = " + cantidad;
	}
	
}
