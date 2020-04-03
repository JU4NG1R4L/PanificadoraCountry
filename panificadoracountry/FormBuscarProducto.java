package co.com.panificadoracountry;

import java.util.Hashtable;
import java.util.Vector;

import co.com.panificadoracountry.R;
import co.com.BusinessObject.DataBaseBO;
import co.com.DataObject.Detalle;
import co.com.DataObject.Inventario;
import co.com.DataObject.ItemListView;
import co.com.DataObject.Linea;
import co.com.DataObject.Producto;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class FormBuscarProducto extends Activity {

	Vector<Inventario> listaInventaro;
	Dialog dialogInventario;
	Producto producto;
	ItemListView[] itemListView;
	Button btnBusquedaProduc;
	Vector<Linea> listaLineas;
	Vector<Producto> listaProductos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_buscar_producto);
		btnBusquedaProduc = (Button) findViewById(R.id.btnBusquedaProduc);
		InicializarDatos();
		CargarOpcionesBusqueda();
		CargarOpcionesLineas();
		SetListenerListView();


	}
	
	public void onResume(){
		
		super.onResume();
		
		if( itemListView != null ){
			
			( ( Spinner ) findViewById( R.id.cbOpBusquedaProduc ) ).setSelection( Main.posOpBusqProductos , true);
			( (EditText) findViewById(R.id.txtOpBusquedaProduc) ).setText( Main.cadBusqueda );	
			ListViewAdapter adapter = new ListViewAdapter(this, itemListView, R.drawable.prod, 0);
			ListView listaBusquedaProductos = (ListView) findViewById(R.id.listaBusquedaProductos);
			listaBusquedaProductos.setAdapter(adapter);
			
			
		}
	}
	
	public void InicializarDatos() {

		
		Bundle bundle = getIntent().getExtras();
		
	}

	public void CargarOpcionesBusqueda() {

		String[] items = new String[] {Const.POR_CODIGO, Const.POR_NOMBRE,Const.TODOS};
		Spinner cbOpBusquedaProduc = (Spinner) findViewById(R.id.cbOpBusquedaProduc);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		cbOpBusquedaProduc.setAdapter(adapter);        
		cbOpBusquedaProduc.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View selectedItemView, int position, long id) {

				String opSel = ((Spinner) findViewById(R.id.cbOpBusquedaProduc)).getItemAtPosition(position).toString();
				((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);

				if (opSel.equals(Const.POR_NOMBRE)) {
					estadoComponentes(true);
					OpcionSeleccionada("Ingrese Parte del Nombre:", InputType.TYPE_CLASS_TEXT);

				} else if (opSel.equals(Const.POR_CODIGO)) {
					estadoComponentes(true);
					OpcionSeleccionada("Ingrese Parte del Codigo:", InputType.TYPE_CLASS_TEXT);
				}
				else if (opSel.equals(Const.TODOS)) {
					estadoComponentes(false);
					BuscarTodosLosProductos();
					//OpcionSeleccionada("Ingrese Parte del Codigo:", InputType.TYPE_CLASS_NUMBER);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) { }
		});
	}

	public void CargarOpcionesLineas() {

		String[] items;
		Vector<String> listaItems = new Vector<String>();
		listaLineas = DataBaseBO.ListaLineas(listaItems);

		if (listaItems.size() > 0) {

			items = new String[listaItems.size()];
			listaItems.copyInto(items);

		} else {

			items = new String[] {};

			if (listaLineas != null)
				listaLineas.removeAllElements();
		}

		Spinner cbLinerasProduc = (Spinner) findViewById(R.id.cbLinerasProduc);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		cbLinerasProduc.setAdapter(adapter);


	}

	public void OpcionSeleccionada(String label, int inputType) {

		TextView lblOpBusquedaProduc = (TextView)findViewById(R.id.lblOpBusquedaProduc);
		EditText txtOpBusquedaProduc = (EditText)findViewById(R.id.txtOpBusquedaProduc);

		txtOpBusquedaProduc.setText("");
		lblOpBusquedaProduc.setText(label);
		txtOpBusquedaProduc.setInputType(inputType);

		ListViewAdapter adapter = new ListViewAdapter(this, new ItemListView[]{}, R.drawable.producto, 0);
		ListView listaBusquedaProductos = (ListView)findViewById(R.id.listaBusquedaProductos);
		listaBusquedaProductos.setAdapter(adapter);

		if (listaProductos != null)
			listaProductos.removeAllElements();
	}

	public void OnClickFormBuscarProducto(View view) {

		btnBusquedaProduc.setEnabled(false);
		OcultarTeclado((EditText)findViewById(R.id.txtOpBusquedaProduc));

		EditText txtOpBusquedaProduc = (EditText) findViewById(R.id.txtOpBusquedaProduc);
		String cadBusqueda = txtOpBusquedaProduc.getText().toString().trim();

		if (cadBusqueda.equals("")) {

			Toast.makeText(getApplicationContext(), "Debe ingresar la opcion de Busqueda", Toast.LENGTH_SHORT).show();
			txtOpBusquedaProduc.requestFocus();

		} else {



			boolean porCodigo = false;
			Spinner cbOpBusquedaProduc = (Spinner) findViewById(R.id.cbOpBusquedaProduc);
			int posBusqProductos       = cbOpBusquedaProduc.getSelectedItemPosition();
			String opBusqueda          = cbOpBusquedaProduc.getSelectedItem().toString();

			if (opBusqueda.equals(Const.POR_NOMBRE)) {

				porCodigo = false;

			} else if (opBusqueda.equals(Const.POR_CODIGO)) {

				porCodigo = true;
			}

			ItemListView[] items;
			Vector<ItemListView> listaItems = new Vector<ItemListView>();
			
			listaProductos = DataBaseBO.BuscarProductos(porCodigo, cadBusqueda, "", Main.cliente.listaPrecio, listaItems, true);
			

			if (listaItems.size() > 0) {

				items = new ItemListView[listaItems.size()];
				listaItems.copyInto(items);

			} else {

				items = new ItemListView[] {};

				if (listaProductos != null)
					listaProductos.removeAllElements();

				Toast.makeText(getApplicationContext(), "Busqueda sin resultados", Toast.LENGTH_SHORT).show();
			}
			
			Main.cadBusqueda        = cadBusqueda;
			Main.posOpBusqProductos = posBusqProductos;
			itemListView       = items;

			ListViewAdapter adapter = new ListViewAdapter(this, items, R.drawable.prod, 0);
			ListView listaBusquedaProductos = (ListView) findViewById(R.id.listaBusquedaProductos);
			listaBusquedaProductos.setAdapter(adapter);
		}

		btnBusquedaProduc.setEnabled(true);
	}

	public void SetListenerListView() {

		ListView listaOpciones = (ListView)findViewById(R.id.listaBusquedaProductos);

		listaOpciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				try {

					producto = listaProductos.elementAt(position);
					
					Intent intent = new Intent();
					intent.putExtra("producto", producto);
					
					FormBuscarProducto.this.setResult( RESULT_OK , intent);
					FormBuscarProducto.this.finish();
					
					//MostrarDialogAddCantidadInventario(producto.codigo);

				} catch (Exception e) {

					String msg = e.getMessage();
					Toast.makeText(getBaseContext(), "Error seleccionado Producto: " + msg, Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			setResult(RESULT_OK);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void OcultarTeclado(EditText editText) {

		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}




	@Override
	protected void onDestroy() {

		super.onDestroy();
	}



	public void refrescarProducto(String codProducto, int cantidad){


		ItemListView[] items;
		Vector<ItemListView> listaItems = new Vector<ItemListView>();

		ItemListView itemListView = new ItemListView();



		for(Producto prod: listaProductos){

			itemListView = new ItemListView();

			if(prod.codigo.equals(codProducto)){

				prod.existencia = prod.existencia+cantidad;

			}

			itemListView.titulo    = prod.codigo + " - " + prod.descripcion;
			itemListView.subTitulo = "Existencia: "+prod.existencia;

			listaItems.add(itemListView);

		}

		if (listaItems.size() > 0) {

			items = new ItemListView[listaItems.size()];
			listaItems.copyInto(items);

		} else {

			items = new ItemListView[] {};

			if (listaProductos != null)
				listaProductos.removeAllElements();


		}

		ListViewAdapter adapter = new ListViewAdapter(this, items, R.drawable.prod, 0);
		ListView listaBusquedaProductos = (ListView) findViewById(R.id.listaBusquedaProductos);
		listaBusquedaProductos.setAdapter(adapter);

	}


	public void estadoComponentes(boolean visible){
		
		TextView lblOpBusquedaProduc = (TextView)findViewById(R.id.lblOpBusquedaProduc);
		Button btnBusquedaProduc = (Button)findViewById(R.id.btnBusquedaProduc);
		EditText txtOpBusquedaProduc = (EditText)findViewById(R.id.txtOpBusquedaProduc);
		
		if(visible){
			
			lblOpBusquedaProduc.setVisibility(TextView.VISIBLE);
			btnBusquedaProduc.setVisibility(Button.VISIBLE);
			txtOpBusquedaProduc.setVisibility(EditText.VISIBLE);
			
		}else{
			
			lblOpBusquedaProduc.setVisibility(TextView.INVISIBLE);
			btnBusquedaProduc.setVisibility(Button.INVISIBLE);
			txtOpBusquedaProduc.setVisibility(EditText.INVISIBLE);
			
		}
		
	}

	
	
	public void BuscarTodosLosProductos() {

			ItemListView[] items;
			Vector<ItemListView> listaItems = new Vector<ItemListView>();
			listaProductos = DataBaseBO.BuscarTodosProductos(  "", Main.cliente.listaPrecio, listaItems, true);

			if (listaItems.size() > 0) {

				items = new ItemListView[listaItems.size()];
				listaItems.copyInto(items);

			} else {

				items = new ItemListView[] {};

				if (listaProductos != null)
					listaProductos.removeAllElements();

				Toast.makeText(getApplicationContext(), "Busqueda sin resultados", Toast.LENGTH_SHORT).show();
			}
			
			Main.cadBusqueda        = "";
			Main.posOpBusqProductos = 1;
			itemListView       = items;

			ListViewAdapter adapter = new ListViewAdapter(this, items, R.drawable.prod, 0);
			ListView listaBusquedaProductos = (ListView) findViewById(R.id.listaBusquedaProductos);
			listaBusquedaProductos.setAdapter(adapter);
			
			InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

	}


}
