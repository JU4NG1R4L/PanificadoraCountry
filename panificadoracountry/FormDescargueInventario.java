/**
 * 
 */
package co.com.panificadoracountry;

import java.util.ArrayList;



import co.com.BusinessObject.DataBaseBO;
import co.com.DataObject.CargueInventario;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


/**
 * Activity que permite hacer la sincronizacion de inventarios.
 * Se debe ejecutar siempre al finalizar el dia.
 * Ademas dibuja una tabla de forma dinamica donde se resume el cargue de inventario.
 * @author JICZ
 *
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2) 
public class FormDescargueInventario extends Activity {
    
    
    
    /**
     * Views que representan las tablas que seran usadas para contenido y cabecera y parametrizacion 
     */
    private TableRow.LayoutParams filaTotal;
    
    
    /**
     * tamaño del display
     */
    Point point;
    
    
    // color de la letra. (Negra)
    int color = R.color.black;
    
    
    /**
     * Se usaran para determinar el ancho de las columnas  de ambas tablas, cabecera y contenido
     */
    private TableRow.LayoutParams params; 
    
    
    
    
    private TableLayout tablaCabecera;
    private TableLayout tablaContenido;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	
	//Eliminamos el titulo de la ventana
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	
	setContentView(R.layout.form_descargue_inventario);
	
	
	/*Instancia al tablelayout que mostrara la cabecera*/
	tablaCabecera = (TableLayout) findViewById(R.id.tableLayoutCabeceraCargueInv);
	
	/*Instancia al tablelayout que mostrara el contenido*/
	tablaContenido = (TableLayout) findViewById(R.id.tableLayoutContenidoCargueInv);
	
	/*Obtener el tamaño de la pantalla para definir el ancho de cada caolumna*/
	point = new Point();
	getWindowManager().getDefaultDisplay().getSize(point);
	
	
	/*ancho total de una fila completa*/
	filaTotal = new TableRow.LayoutParams(point.x, TableRow.LayoutParams.WRAP_CONTENT);
	
	/*Ancho definido para cada columna, se recomienda que sea un numero par y que la diferencia con point.x sea un multiplo del numero par.
	 * para este caso 24 es multiplo del ancho definido que es 4 pixeles*/
	params = new TableRow.LayoutParams(((point.x - 24) / 4), TableRow.LayoutParams.WRAP_CONTENT);
	
	/*mostrar la cabecera*/
	agregarCabecera();
	
	/*Mostrar contenido*/
	agregarContenido();
    }
    
    
    /**
     * se define el ttulo de las filas de la cabecera de la tabla.
     */
    private void agregarCabecera() {
	
	//capturar los nombres de las columnas definidas en strings.xml
	String[] tituloColumnas = getResources().getStringArray(R.array.columnas_descargue_inventario);
	
	TableRow row = new TableRow(this);
	row.setLayoutParams(filaTotal);
	row.setGravity(Gravity.CENTER);
	
	// definicion de columnas
	final TextView txtCodigo = new TextView(this);
	final TextView txtProducto = new TextView(this);
	final TextView txtCantInicial = new TextView(this);
	final TextView txtCantFinal = new TextView(this);
	
	
	txtCodigo.setText(tituloColumnas[0]);
	txtCodigo.setTextColor(color);
	txtCodigo.setLayoutParams(params);
	txtCodigo.setBackgroundResource(R.drawable.celda_cabecera_tabla);
	
	
	txtProducto.setText(tituloColumnas[1]);
	txtProducto.setTextColor(color);
	txtProducto.setLayoutParams(params);
	txtProducto.setBackgroundResource(R.drawable.celda_cabecera_tabla);
	
	
	txtCantInicial.setText(tituloColumnas[2]);
	txtCantInicial.setTextColor(color);
	txtCantInicial.setLayoutParams(params);
	txtCantInicial.setBackgroundResource(R.drawable.celda_cabecera_tabla);
	
	
	txtCantFinal.setText(tituloColumnas[3]);
	txtCantFinal.setTextColor(color);
	txtCantFinal.setLayoutParams(params);
	txtCantFinal.setBackgroundResource(R.drawable.celda_cabecera_tabla);
	
	
	/*alinear la cuadricula para que se conserven del mismo tamaño que su par.
	 * el valor height de categoria es siempre mas grande que el de total, por esto se elige como referencia*/
	ViewTreeObserver viewTreeObserver = txtProducto.getViewTreeObserver();
	viewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	    @Override
	    public void onGlobalLayout() {
		
		int height = txtProducto.getMeasuredHeight();
		txtCodigo.setHeight(height);
		txtProducto.setHeight(height);
		txtCantInicial.setHeight(height);
	    }
	});
	
	// insertar en el table contenido.
	row.addView(txtCodigo, params);
	row.addView(txtProducto, params);
	row.addView(txtCantInicial, params);
	row.addView(txtCantFinal, params);
	tablaCabecera.addView(row);
    }
    
    
    
    
    private void agregarContenido() {
	
	/* capturar los datos que seran mostrados en la tabla */
	ArrayList<CargueInventario> listaCargueInventario = new ArrayList<CargueInventario>();
	DataBaseBO.obtenerCargueInventario(listaCargueInventario);
	
	for (CargueInventario cargue : listaCargueInventario) {
	    
	    // definicion de una fila que ocupa todo el ancho del padre, es
	    // decir de la tabla.
	    // el tablerow ocupara todo el ancho del padre y sus hijos pueden
	    // usar un peso maximo de 1. (weightsum = 1).
	    TableRow row = new TableRow(this);
	    row.setLayoutParams(filaTotal);
	    row.setGravity(Gravity.CENTER);
	    
	    // definicion de columnas
	    final TextView txtCodigo = new TextView(this);
	    final TextView txtProducto = new TextView(this);
	    final TextView txtCantInicial = new TextView(this);
	    final TextView txtCantFinal = new TextView(this);
	    
	    txtCodigo.setText(cargue.codigoProducto);
	    txtCodigo.setTextColor(color);
	    txtCodigo.setLayoutParams(params);
	    txtCodigo.setBackgroundResource(R.drawable.celda_contenido_tabla);
	    
	    txtProducto.setText(cargue.descripcion);
	    txtProducto.setTextColor(color);
	    txtProducto.setLayoutParams(params);
	    txtProducto.setBackgroundResource(R.drawable.celda_contenido_tabla);
	    
	    txtCantInicial.setText(cargue.cantidadInicial);
	    txtCantInicial.setTextColor(color);
	    txtCantInicial.setLayoutParams(params);
	    txtCantInicial.setBackgroundResource(R.drawable.celda_contenido_tabla);
	    
	    txtCantFinal.setText(cargue.cantidadFinal);
	    txtCantFinal.setTextColor(color);
	    txtCantFinal.setLayoutParams(params);
	    txtCantFinal.setBackgroundResource(R.drawable.celda_contenido_tabla);
	    
	    /*
	     * alinear la cuadricula para que se conserven del mismo tamaño que
	     * su par. el valor height de categoria es siempre mas grande que el
	     * de total, por esto se elige como referencia
	     */
	    ViewTreeObserver viewTreeObserver = txtProducto.getViewTreeObserver();
	    viewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
		
		@Override
		public void onGlobalLayout() {
		    
		    int height = txtProducto.getMeasuredHeight();
		    txtCodigo.setHeight(height);
		    txtProducto.setHeight(height);
		    txtCantInicial.setHeight(height);
		    txtCantFinal.setHeight(height);
		}
	    });
	    
	    // insertar en el table contenido.
	    row.addView(txtCodigo, params);
	    row.addView(txtProducto, params);
	    row.addView(txtCantInicial, params);
	    row.addView(txtCantFinal, params);
	    tablaContenido.addView(row);
	}
    }
}
