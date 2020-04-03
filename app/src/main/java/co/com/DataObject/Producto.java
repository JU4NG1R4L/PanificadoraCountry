package co.com.DataObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Producto implements Serializable, Cloneable {
	
	private static final long serialVersionUID = 1L;
	
	public String codigo;
	public String descripcion;
	public float precio;
	public float iva;
	public float precioIva;
	public float cantidadInv;
	public float cantUltimo;
	public int cantidadPedida;
	public int cantidad;
	public int saldo;
	public String unidadMedida;
	public String linea;
	public String grupo;
	public int indice;
	public String lote;
	public String fechaVencimiento;
	public String fechaFabricacion;
	public boolean existe;
	
	public float descuento;
	public float descuentoPorProducto;
	public float existencia;
	public String codLinea;
	public String codMotivo;
	
	
	public int inventarioMaquina;
	public int inventarioConteo;
	
	
	
	/**
	 * Lista que conserva las cantidades de productos en inventario de los diferentes lotes disponibles.
	 */
	private ArrayList<Inventario> listaInventario;

	public String observacionDescuentoPorProducto;

	public String fechaInicialDescuento;

	public String fechaFinalDescuento;

	public int valorDescuentoProductoMostrar;

	/**
         * @param listaInventario
         */
        public Producto() {
	    super();
	    this.listaInventario = new ArrayList<Inventario>();
        }

	
        /**
         * @return the listaInventario
         */
        public ArrayList<Inventario> getListaInventario() {
            return listaInventario;
        }

	
        /**
         * @param listaInventario the listaInventario to set
         */
        public void setListaInventario(ArrayList<Inventario> listaInventario) {
            this.listaInventario = listaInventario;
        }
        
        @Override
        public Object clone() throws CloneNotSupportedException {
        // TODO Auto-generated method stub
        return super.clone();
        }
	
}
