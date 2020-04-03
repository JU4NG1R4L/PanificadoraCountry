package co.com.DataObject;

import java.io.Serializable;

public class Kardex implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String fecha;
	public String tipoDocumento;
	public String numeroDocumento;
	public float cantidad;
	public int  tipoReg;// (1,2,3,4)
}
