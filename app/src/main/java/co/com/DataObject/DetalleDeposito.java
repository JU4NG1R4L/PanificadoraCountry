package co.com.DataObject;

import java.io.Serializable;

public class DetalleDeposito implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public String documento;
	public String cod_saldo_canc;
	public float saldo = 0;
	public int cod_est_deposito;
	public String cod_cliente;
	public String razon_social_cliente;
	public String factura;
	public String saldoString = "";
	
}
