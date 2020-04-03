package co.com.DataObject;

import java.io.Serializable;

import android.bluetooth.BluetoothDevice;

public class ItemListView implements Serializable {

	private static final long serialVersionUID = 6L;
	
	public String titulo;
	public String subTitulo;
	public String subTitulo1 = "";
	public String referencia;
	public int colorTitulo;	
	public int cantAjuste;
	public String codigoRef;
	public int icono;
	public boolean enable = true;
	public boolean aceptado = false;
	public String fechaUltimaEncuesta;
	public int state;
	public BluetoothDevice device;
	public boolean isChecked = true;
	public boolean isEnable = false;
	public int position;
	
	//Deposito
	public String codSaldo;
	public float saldo = 0;
	public String cod_cliente;
	public String razon_social;
	public String factura;

	public String codSaldoCan;
	public float saldoTotal =0; 
	public boolean chequeado = false;
	
	
}