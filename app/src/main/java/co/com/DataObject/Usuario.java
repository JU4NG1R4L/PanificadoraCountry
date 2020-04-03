package co.com.DataObject;

import java.io.Serializable;

public class Usuario implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String mensaje;
	public String bodega;
	
	public String codigoVendedor;
    public String nombreVendedor;
    public String nombreImprimir;
	public String fechaLabores;
	public String fechaConsecutivo;
	public String canalVenta;
	public int gps = 0;
	public int pedido_minimo;

	public String cuenta_contable;
}
