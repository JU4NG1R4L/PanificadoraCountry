package co.com.DataObject;

import java.io.Serializable;

public class InformeInventario implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String codigo;
	public String nombre;
	
	public int invInicial;
	public int invActual;
	public int cantVentas;
	public int cantDev;
	public int cantVentaC;
	public int cantDevC;
}
