package co.com.panificadoracountry;

import java.util.Vector;

import co.com.panificadoracountry.R;
import co.com.BusinessObject.DataBaseBO;
import co.com.DataObject.Cliente;
import co.com.DataObject.ItemListView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class FormEstadisticasNoCompra extends Activity {

	Vector<Cliente> listaClientes;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_estadisticas_nocompra);
		
		CargarClientesNoCompra();
		//SetListenerListView();
	}
	
	public void CargarClientesNoCompra() {
		
		ItemListView[] listaItems = null;
		Vector<ItemListView> listaItemsCliente = new Vector<ItemListView>(); 
		listaClientes = DataBaseBO.ClienteNoCompra(listaItemsCliente);
		
		if (listaItemsCliente.size() > 0) {
			
			listaItems = new ItemListView[listaItemsCliente.size()];
			listaItemsCliente.copyInto(listaItems);
			
			ListViewAdapter adapter = new ListViewAdapter(this, listaItems, R.drawable.cliente, 0xff2E65AD);
			ListView listaPedidosRealizados = (ListView)findViewById(R.id.listaClientesNoCompra);
			listaPedidosRealizados.setAdapter(adapter);
			
		} else {
			
			ListViewAdapter adapter = new ListViewAdapter(this, new ItemListView[]{}, R.drawable.cliente, 0xff2E65AD);
			ListView listaClientesNoCompra = (ListView)findViewById(R.id.listaClientesNoCompra);
			listaClientesNoCompra.setAdapter(adapter);					
		}
		
		int size = listaClientes.size();
		
		String msg = "<b>Total No Compras: " + size + "</b>";
		((TextView)findViewById(R.id.lblTitulo)).setText(Html.fromHtml(msg));
	}
	
	public void SetListenerListView() {
		
		ListView listaClientesNoCompra = (ListView)findViewById(R.id.listaClientesNoCompra);
		listaClientesNoCompra.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            
			@Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               
				Main.cliente = listaClientes.elementAt(position);
				
				Intent formInfoCliente = new Intent(FormEstadisticasNoCompra.this, FormInfoClienteActivity.class);
				startActivityForResult(formInfoCliente, Const.RESP_PEDIDO_EXITOSO);
            }
        });
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == Const.RESP_PEDIDO_EXITOSO && resultCode == RESULT_OK) {
			
			CargarClientesNoCompra();
		}
	}
	
	
	
	
	
}
