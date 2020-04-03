package co.com.DataObject;

import java.io.Serializable;

public class ImpresionDetalle implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String codigo;
	public String nombre;
	public float precio;
	public float iva;
	public float descuento;
	public float descuento_producto;
	public float cantidad;
	public int tipo;
}
