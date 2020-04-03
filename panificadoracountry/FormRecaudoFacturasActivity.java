package co.com.panificadoracountry;

import java.util.Vector;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import co.com.panificadoracountry.R;
import co.com.BusinessObject.DataBaseBO;
import co.com.BusinessObject.DataBaseBOJ;
import co.com.DataObject.Cartera;
import co.com.DataObject.Cliente;
import co.com.DataObject.ItemListView;
import co.com.DataObject.Usuario;



public class FormRecaudoFacturasActivity extends Activity {

	//Vector<Cliente> listaClientes;
	Vector<Cartera> listaCartera;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_recaudo_facturas);
		cargarLista();
		SetListenerListView();

	}
	
	public void cargarLista(){
		ItemListView[] listaItems = null;
		Vector<ItemListView> listaItemsFacturas = new Vector<ItemListView>();
		
		listaCartera = DataBaseBOJ.listaCartera(Main.cliente.codigo, listaItemsFacturas);
		
		if (listaItemsFacturas.size() > 0) {

			listaItems = new ItemListView[listaItemsFacturas.size()];
			listaItemsFacturas.copyInto(listaItems);

			ListViewAdapter adapter = new ListViewAdapter(this, listaItems, R.drawable.cartera, 0xff2E65AD);
			ListView listaBusquedaFacturas = (ListView)findViewById(R.id.listaFacturas);
			listaBusquedaFacturas.setAdapter(adapter);

		}else{
			Toast.makeText(getBaseContext(), "El cliente no tiene cartera pendiente", Toast.LENGTH_LONG).show();
			finish();
		}
	}


	public void SetListenerListView() {

		ListView listaOpciones = (ListView)findViewById(R.id.listaFacturas);
		listaOpciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				cargarInformacionFacturas(position);
			}
		});
	}
	
	public void cargarInformacionFacturas(final int position){
		Cartera carteraSel = listaCartera.elementAt(position);
		Main.cartera = carteraSel;
		
		Intent intentRecaudoDetail = new Intent(this, FormRecaudoFacturasDetailActivity.class);
		startActivity(intentRecaudoDetail);
		finish();
	}
	
	
    public void onClickCancelarRecaudoFactura(View view) {
		
		finish();
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}



	


}
