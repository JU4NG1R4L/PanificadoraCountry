package co.com.DataObject;

import java.io.Serializable;

public class Deposito implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public String documento;
	public String cod_usuario;
	public String observacion;
	public String cod_imagen;
	public String compr_bancario;
	public int cod_est_deposito = 0;
	public int cod_banco;
	public String cant_cheques;
	public float totalDeposito = 0;
	public String desc_banco;
	
}
