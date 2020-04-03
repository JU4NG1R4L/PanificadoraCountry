package co.com.DataObject;

import java.io.Serializable;

public class Venta implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public float subTotal  = 0;
	public float iva       = 0;
	public float descuento = 0;
	public float neto      = 0;
	public String tipo     = "";

	public float totalRetenciones = 0;
	
}
