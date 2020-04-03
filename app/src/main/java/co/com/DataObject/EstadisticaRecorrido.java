package co.com.DataObject;

import java.io.Serializable;

public class EstadisticaRecorrido implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public int total_visitas;
	public int total_pedidos;
	public int total_devoluciones;
	public int total_pedidos_sync;
	public int total_devoluciones_sync;
	public int total_pedidos_sin_sync;
	public int total_devoluciones_sin_sync;
	public int total_venta;
	public int valor_devoluciones;
	public float efectividad;
	public int totalNoCompras;
	public int totalNovedades;
}
