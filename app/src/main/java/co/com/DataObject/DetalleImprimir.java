/**
 * 
 */
package co.com.DataObject;

import java.util.ArrayList;


/**
 * Empaqueta el detalle de un pedido que contiene los datos que seran impresos.
 * Ademas de los datos de encabezado. (Precio total, iva, etc)
 * @author JICZ
 *
 */
public class DetalleImprimir {
    
    public Encabezado encabezado;
    
    public ArrayList<DetalleProducto> listaDetalleImprimir;

    
    /**
     * @return the encabezado
     */
    public Encabezado getEncabezado() {
        return encabezado;
    }

    
    /**
     * @param encabezado the encabezado to set
     */
    public void setEncabezado(Encabezado encabezado) {
        this.encabezado = encabezado;
    }

    
    /**
     * @return the listaDetalleImprimir
     */
    public ArrayList<DetalleProducto> getListaDetalleImprimir() {
        return listaDetalleImprimir;
    }

    
    /**
     * @param listaDetalleImprimir the listaDetalleImprimir to set
     */
    public void setListaDetalleImprimir(ArrayList<DetalleProducto> listaDetalleImprimir) {
        this.listaDetalleImprimir = listaDetalleImprimir;
    }
}
