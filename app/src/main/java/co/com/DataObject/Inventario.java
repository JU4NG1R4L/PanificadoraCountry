package co.com.DataObject;

import java.io.Serializable;

public class Inventario implements Serializable 
{
	private static final long serialVersionUID = 1L;
	
	public String codigoProducto;
	public String descripcionProducto;
	public String inventarioInicial;
	public String inventarioActual;
	public String calentado;
	public int cantidad;
	public String lote;
	public String fechaVencimiento;
	public String fechaFabricacion;
	
}
