package co.com.DataObject;

import java.io.Serializable;

public class Cartera implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String codSaldo;
	public String codCliente;    //Codigo del Cliente
	public String referencia;    //Numero Documento
	public float saldo;          //Saldo - abono
	public int dias;
	public String FechaVecto;
	public String fecha;
	public String concepto;
	public String documento;
	public String descripcion;
	public String numero_factura; // prefijo + consecutdivo
	
	//Datos Registrados en el Recaudo
	public boolean pagoTotal;     //true -> Pago Total, False -> Pago Parcial
	public float valorARecaudar;
	public int codigoMotivoAbono;
	public String observacion;
	
	
	public String strSaldo ;
	public String strAbono ;
	public float abono;	
	
	public int dias2;
	public double valorProntoPago;
	
	public double desc;
	public double rete;
	public double otros;
	public boolean esPagoParcial;
	
	
}
