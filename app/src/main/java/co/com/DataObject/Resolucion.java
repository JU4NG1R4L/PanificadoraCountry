package co.com.DataObject;

import java.io.Serializable;


public class Resolucion implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 7279718900319038435L;
    
    /**
     * numero de resolucion DIAN.
     */
    public String resolucion;
    
    /**
     * Fecha de inicio de la resolucion.
     */
    public String fechaResolucion;
    
    /**
     * Indica la fecha maxima de vigencia aprobada por la DIAN para la resolucion.
     * Una factura de venta expedida con una fecha mayor a la fechaVigencia NO
     * esta permitido por la ley colombiana y puede traer problemas lagales.
     */
    public String fechaVigencia;
    
    /**
     * indica el numero inicial valido de la resolucion DIAN
     */
    public String numeroInicial;
    
    /**
     * indica el numero final valido de la resolucion DIAN
     */
    public String numeroFinal;
    
}
