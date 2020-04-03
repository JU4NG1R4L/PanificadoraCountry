package co.com.panificadoracountry;

import java.io.File;
import java.util.Vector;



import co.com.panificadoracountry.R;
import co.com.BusinessObject.DataBaseBO;
import co.com.DataObject.Cartera;
import co.com.DataObject.ItemListView;
import co.com.DataObject.Producto;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class FormConsultarPreciosActivity extends Activity {
	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	Vector<Producto> listaProducto;
	Vector<String> listado;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO Put your code here

		setContentView(R.layout.consultar_precios);
		mostrarHeader();
		Util.closeTecladoStartActivity(this);
	}


	
	public void OnClickFormBuscarProductos(View view){

		cargarProductos();

	}


	public void OnClickBuscarXCodigo(View view){

		if(((RadioButton)findViewById(R.id.radioBucarXCodigo)).isChecked()){

			((RadioButton)findViewById(R.id.radioBuscarXNombre)).setChecked(false);
			((RadioButton)findViewById(R.id.radioBuscarXTodos)).setChecked(false);


			((TextView)findViewById(R.id.lblOpBusquedaProduc22)).setVisibility(TextView.VISIBLE);
			((EditText)findViewById(R.id.txtbuscarprecios)).setVisibility(EditText.VISIBLE);

			OpcionSeleccionada("Ingrese Parte\nDel Codigo:", InputType.TYPE_CLASS_NUMBER);

		}

	}


	public void OnClickBuscarXNombre(View view){

		if(((RadioButton)findViewById(R.id.radioBuscarXNombre)).isChecked()){


			((RadioButton)findViewById(R.id.radioBucarXCodigo)).setChecked(false);
			((RadioButton)findViewById(R.id.radioBuscarXTodos)).setChecked(false);


			((TextView)findViewById(R.id.lblOpBusquedaProduc22)).setVisibility(TextView.VISIBLE);
			((EditText)findViewById(R.id.txtbuscarprecios)).setVisibility(EditText.VISIBLE);

			OpcionSeleccionada("Ingrese Parte\nDel Nombre:", InputType.TYPE_CLASS_TEXT);

		}


	}


	public void OnClickBuscarXTodos(View view){

		if(((RadioButton)findViewById(R.id.radioBuscarXTodos)).isChecked()){


			((RadioButton)findViewById(R.id.radioBuscarXNombre)).setChecked(false);
			((RadioButton)findViewById(R.id.radioBucarXCodigo)).setChecked(false);


			((TextView)findViewById(R.id.lblOpBusquedaProduc22)).setVisibility(TextView.GONE);
			((EditText)findViewById(R.id.txtbuscarprecios)).setVisibility(EditText.GONE);

		}


	}




	public void cargarProductos() {

		OcultarTeclado((EditText)findViewById(R.id.txtbuscarprecios));

		EditText txtOpBusquedaProduc = (EditText) findViewById(R.id.txtbuscarprecios);
		String cadBusqueda = txtOpBusquedaProduc.getText().toString().trim();
		
		if (cadBusqueda.equals("") && !((RadioButton)findViewById(R.id.radioBuscarXTodos)).isChecked()) {

			Toast toast= Toast.makeText(getApplicationContext(), 
					"Debe ingresar la opcion de Busqueda", Toast.LENGTH_SHORT);  
			toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
			toast.show();
			txtOpBusquedaProduc.requestFocus();

		} else {


			if(((RadioButton)findViewById(R.id.radioBucarXCodigo)).isChecked()){

				listaProducto = DataBaseBO.BuscarProductos2(1, cadBusqueda);

			}else{


				if(((RadioButton)findViewById(R.id.radioBuscarXNombre)).isChecked()){

					listaProducto = DataBaseBO.BuscarProductos2(2, cadBusqueda);

				}else{


					if(((RadioButton)findViewById(R.id.radioBuscarXTodos)).isChecked()){

					  listaProducto = DataBaseBO.BuscarProductos2(3, cadBusqueda);

					}else{

					}  
				}  
			}  

			if (listaProducto.size() > 0) {

				TableLayout table = new TableLayout(this);
				table.setBackgroundColor(Color.WHITE);

				String[] cabecera = { "Codigo", "Nombre", "Precio Sin Iva"};
				Util.Headers(table, cabecera, this);

				HorizontalScrollView scroll = (HorizontalScrollView)findViewById(R.id.scrollAgotados);
				scroll.removeAllViews();
				scroll.addView(table);

				for (Producto producto : listaProducto) {

					TextView textViewAux;
					TableRow fila = new TableRow(this);

					textViewAux = new TextView(this);
					textViewAux.setText(producto.codigo);			
					textViewAux.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
					textViewAux.setTextColor(Color.argb(255, 0, 0, 0));
					textViewAux.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.table_cell_row_1));
					fila.addView(textViewAux);

					textViewAux = new TextView(this);
					textViewAux.setText(producto.descripcion);			
					textViewAux.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
					textViewAux.setTextColor(Color.argb(255, 0, 0, 0));
					textViewAux.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.table_cell_row_1));
					fila.addView(textViewAux);

					textViewAux = new TextView(this);
					textViewAux.setText(Util.SepararMiles(Util.Redondear( "" + producto.precio, 0)));			
					textViewAux.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
					textViewAux.setTextColor(Color.argb(255, 0, 0, 0));
					textViewAux.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.table_cell_row_1));
					fila.addView(textViewAux);

					table.addView(fila);
				}
			}
		}
	}




	public void OpcionSeleccionada(String label, int inputType) {

		TextView lblOpBusquedaProduc = (TextView)findViewById(R.id.lblOpBusquedaProduc22);
		EditText txtOpBusquedaProduc = (EditText)findViewById(R.id.txtbuscarprecios);

		txtOpBusquedaProduc.setText("");
		lblOpBusquedaProduc.setText(label);
		txtOpBusquedaProduc.setInputType(inputType);

	}



	public void OcultarTeclado(EditText editText) {

		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}


	public void mostrarHeader(){

		TableLayout table = new TableLayout(this);
		table.setBackgroundColor(Color.WHITE);

		String[] cabecera = { " Codigo ", "    Nombre    ", " Precio Sin Iva "};
		Util.Headers(table, cabecera, this);

		HorizontalScrollView scroll = (HorizontalScrollView)findViewById(R.id.scrollAgotados);
		scroll.removeAllViews();
		scroll.addView(table);

	}





}
