package co.com.DataObject;

import java.io.Serializable;

public class SaldoCancelado implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public String cod_saldo_canc;
	public String cod_saldo;
	public String cod_cliente;
	public String razon_social;
	public float saldo_recibido;
	public String factura;
	public String cod_usuario;
	public int codEstado;
	public String observacion;
	
}
