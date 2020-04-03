package co.com.DataObject;

import java.io.Serializable;

public class EncabezadoRecaudo implements Serializable {

	private static final long serialVersionUID = 1L;
	

	public double total;
	public String fecha_recaudo;
	public String nrodoc;
	public String razonSocial;
	public String representa;
	public String nit;
	public String vendedor;
	
	
	public double efectivo;
	public double consignacion;
	public double cheque;

	
}
