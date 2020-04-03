package co.com.panificadoracountry;

import java.util.Calendar;
import java.util.Vector;

import co.com.panificadoracountry.R;
import co.com.BusinessObject.DataBaseBO;
import co.com.BusinessObject.DataBaseBOJ;
import co.com.DataObject.Cliente;
import co.com.DataObject.ItemListView;
import co.com.DataObject.Usuario;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FormRuteroActivity extends Activity {

	int ciudadSeleccionada;

	Vector<String> listaDiasRutero;
	Vector<Cliente> listaClientes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_rutero);
		CargarDiasRutero();
		SetListenerListView();
	}

	//@Override
	public void respGPS(Location location) {

		Toast.makeText(getBaseContext(), "Coor: " + location.getLatitude() + " - " + location.getLongitude(), Toast.LENGTH_LONG).show();
	}

	public void CargarDiasRutero() {

		String[] items;
		listaDiasRutero = new Vector<String>();
		listaDiasRutero.add("Lunes");
		listaDiasRutero.add("Martes");
		listaDiasRutero.add("Miercoles");
		listaDiasRutero.add("Jueves");
		listaDiasRutero.add("Viernes");
		listaDiasRutero.add("Sabado");
		listaDiasRutero.add("Domingo");

		if (listaDiasRutero.size() > 0) {

			items = new String[listaDiasRutero.size()];
			listaDiasRutero.copyInto(items);

		} else {

			items = new String[] {};

			if (listaDiasRutero != null)
				listaDiasRutero.removeAllElements();
		}

		Spinner cbCiudadRutero = (Spinner) findViewById(R.id.cbDiasRutero);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		cbCiudadRutero.setAdapter(adapter);

		cbCiudadRutero.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View selectedItemView, int position, long id) {

				((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);

				CargarClientesRutero();
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) { }
		});
	}

	public void CargarClientesRutero() {

		ItemListView[] items;
		int pos = ((Spinner) findViewById(R.id.cbDiasRutero)).getSelectedItemPosition();
        int dia = 0;
        
		listaDiasRutero.add("Lunes");
		listaDiasRutero.add("Martes");
		listaDiasRutero.add("Miercoles");
		listaDiasRutero.add("Jueves");
		listaDiasRutero.add("Viernes");
		listaDiasRutero.add("Sabado");
		listaDiasRutero.add("Domingo");
        
		if (pos == 0)
			dia = 2;
		else if (pos == 1)
			dia = 3;
		else if (pos == 2)
			dia = 4;
		else if (pos == 3)
			dia = 5;
		else if (pos == 4)
			dia = 6;
		else if (pos == 5)
			dia = 7;
		else if (pos == 6)
			dia = 7;
		else if (pos == 7)
			dia = 1;
                                     

		Vector<ItemListView> listaItems = new Vector<ItemListView>();
		listaClientes = DataBaseBOJ.ListaClientesRutero(listaItems, dia+"");

		if (listaItems.size() > 0) {

			items = new ItemListView[listaItems.size()];
			listaItems.copyInto(items); 

		} else {

			items = new ItemListView[] {};

			if (listaClientes != null)
				listaClientes.removeAllElements();
		}

		ListViewAdapter adapter = new ListViewAdapter(this, items, R.drawable.cliente2, 0xff2E65AD);
		ListView listaClienteRutero = (ListView) findViewById(R.id.listaClienteRutero);
		listaClienteRutero.setAdapter(adapter);

		if(Main.pdialog != null)
			if(Main.pdialog.isShowing())
				Main.pdialog.cancel();
	}

	public void SetListenerListView() {

		ListView listaOpciones = (ListView)findViewById(R.id.listaClienteRutero);
		listaOpciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				CargarInformacionCliente(position);
			}
		});
	}

	public void CargarInformacionCliente(final int position) {

		Cliente clienteSel = listaClientes.elementAt(position);
		
	
			boolean tienePedido = DataBaseBO.ExistePedidoCliente(clienteSel.codigo);

			if (tienePedido) {

				AlertDialog alertDialog;

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setCancelable(false).setPositiveButton("Si", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {

						Usuario usuario = DataBaseBO.CargarUsuario();

						if (usuario == null) {

							Toast.makeText(getBaseContext(), "No se pudo cargar la informacion del Usuario", Toast.LENGTH_LONG).show();

						}else{


							Main.cliente = listaClientes.elementAt(position);
							Cliente.save(FormRuteroActivity.this, Main.cliente);
							Intent formInfoCliente = new Intent(FormRuteroActivity.this, FormInfoClienteActivity.class);				
							startActivityForResult(formInfoCliente, Const.RESP_PEDIDO_EXITOSO);
						}



						dialog.cancel();
					}
				}).setNegativeButton("No", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {

						dialog.cancel();
					}
				});

				alertDialog = builder.create();
				alertDialog.setMessage("El cliente " + clienteSel.razonSocial + " ya tiene un pedido para el dia de hoy.\n\nDesea tomar un nuevo pedido?");
				alertDialog.show();

			} else {


				Usuario usuario = DataBaseBO.CargarUsuario();

				if (usuario == null) {

					Util.MostrarAlertDialog(this, "No se pudo cargar la informacion del Usuario");

				}else{


					Main.cliente = listaClientes.elementAt(position);
					Cliente.save(this, Main.cliente);
					Intent formInfoCliente = new Intent(FormRuteroActivity.this, FormInfoClienteActivity.class);
					startActivityForResult(formInfoCliente, Const.RESP_PEDIDO_EXITOSO);
				}
			}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {

			switch (requestCode) {

			case Const.RESP_PEDIDO_EXITOSO:

				seleccionarDiaActual();
				break;
			}
		}
	}





	@Override
	protected void onDestroy() {

		super.onDestroy();
	}




	
	public void onResume(){

		super.onResume();

		if(Main.usuario == null || Main.usuario.codigoVendedor == null)
			DataBaseBO.CargarInfomacionUsuario();

		seleccionarDiaActual();
		
	
	}
	
	
	
	public void seleccionarDiaActual(){
		
		  Calendar c = Calendar.getInstance();
		  int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		  int posSeleccionar = 0;

		    if (Calendar.MONDAY == dayOfWeek) {
		    	posSeleccionar = 0;
		    } else if (Calendar.TUESDAY == dayOfWeek) {
		    	posSeleccionar = 1;
		    } else if (Calendar.WEDNESDAY == dayOfWeek) {
		    	posSeleccionar = 2;
		    } else if (Calendar.THURSDAY == dayOfWeek) {
		    	posSeleccionar = 3;
		    } else if (Calendar.FRIDAY == dayOfWeek) {
		    	posSeleccionar = 4;
		    } else if (Calendar.SATURDAY == dayOfWeek) {
		    	posSeleccionar = 5;
		    } else if (Calendar.SUNDAY == dayOfWeek) {
		    	posSeleccionar = 6;
		    }

		    ((Spinner) findViewById(R.id.cbDiasRutero)).setSelection(posSeleccionar);
		
	}






}
