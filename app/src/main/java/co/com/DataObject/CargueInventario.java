/**
 * 
 */
package co.com.DataObject;

import java.io.Serializable;


/**
 * Empaqueta la informacion que sera mostrada cuando el vendedor hace
 * entrega del inventario sobrante... cargue de inventario
 * @author JICZ
 *
 */
public class CargueInventario implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 575034213630511165L;
    
    /**
     * codigo de producto
     */
    public String codigoProducto;
    
    /**
     * nombre del producto
     */
    public String descripcion;
    
    /**
     * cantidad inicial en inventario
     */
    public String cantidadInicial;
    
    /**
     * cantidad en inventario.
     */
    public String cantidadFinal;
    
    /**
     * numero de datos (columnas = numero de atributos) a mostrar.
     */
    public int columnas;
}
