package co.com.panificadoracountry;

import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import co.com.panificadoracountry.R;
import co.com.BusinessObject.DataBaseBOJ;
import co.com.BusinessObject.FileBO;
import co.com.Conexion.Sync;
import co.com.DataObject.Ciudad;
import co.com.DataObject.Cliente;
import co.com.DataObject.ClienteNuevo;
import co.com.DataObject.Departamento;
import co.com.DataObject.ObjectSel;

	

public class FormEditarClienteActivity extends Activity implements Sincronizador {
	
	public static final String TAG = FormEditarClienteActivity.class.getName();
	ProgressDialog progressDialog;
	Vector<Departamento> edListaDepartamentos;
	Vector<Ciudad> edListaCiudades;
	Spinner spinnerEdCiudad;
	Vector<ObjectSel> listaTipoPersona;
	Vector<ObjectSel> listaEstatusCliente;
	public Spinner spTipoCliente, spEstatusCliente;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_editar_cliente);
		editarDpto();
		PoblarTipoPersona();
		estatusCliente();
		cargarInfoCliente();
				
		Util.cerrarTeclado(this);
	}	
	//Poblar los editText
	public void cargarInfoCliente(){
		
		
		FileBO.validarCliente(this);
		
	
		((EditText)findViewById(R.id.txtNombreCliente)).setText("" + Main.cliente.nombre);
		((EditText)findViewById(R.id.txtRazonSocial)).setText("" + Main.cliente.razonSocial);
		((EditText)findViewById(R.id.txtBarrio)).setText("" + Main.cliente.barrio);
		((EditText)findViewById(R.id.txtDireccion)).setText("" + Main.cliente.direccion);
		((EditText)findViewById(R.id.txtTelefono)).setText("" + Main.cliente.telefono);
		((EditText)findViewById(R.id.txtTelefonoCel)).setText("" + Main.cliente.telefonoCel);
		((EditText)findViewById(R.id.txtEmail)).setText("" + Main.cliente.email);
		
		seleccionarEstatus(Main.cliente.estatus);
		seleccionarTipoPersona(Main.cliente.tipoCliente);
			

	}
	
	 public void seleccionarEstatus(int cod) {

			int size = listaEstatusCliente.size();
			for (int i = 0; i < size; i++) {
				
				ObjectSel manejante = listaEstatusCliente.elementAt(i);
				
				if ((manejante.codigo) == (cod)) {
					
					spEstatusCliente.setSelection(i);
					break;
				}
				
			}
		}
	
	 public void seleccionarTipoPersona(int cod) {
			
			int size = listaTipoPersona.size();
			for (int i = 0; i < size; i++) {
				
				ObjectSel sector = listaTipoPersona.elementAt(i);
				
				if (sector.codigo == cod) { //Si es Multivitaminicos lo selecciona.
					//Spinner spinnerEstatus =(Spinner)findViewById(R.id.spTipoCliente);
					spTipoCliente.setSelection(i);
					break;
				}
			}
		}
	public void OnClickGuardarClienteModificado(View view){
		
		ClienteNuevo clienteNuevo= new ClienteNuevo();
		
		FileBO.validarCliente(this);
		
		clienteNuevo.codigo = Main.cliente.codigo;
		
		clienteNuevo.nit = Main.cliente.nit;
		
		clienteNuevo.nombre=((EditText)findViewById(R.id.txtNombreCliente))
				.getText().toString().trim();
		clienteNuevo.razonSocial=((EditText)findViewById(R.id.txtRazonSocial))
				.getText().toString().trim();
		clienteNuevo.barrio=((EditText)findViewById(R.id.txtBarrio))
				.getText().toString().trim();
		clienteNuevo.direccion=((EditText)findViewById(R.id.txtDireccion))
				.getText().toString().trim();
		clienteNuevo.telefono=((EditText)findViewById(R.id.txtTelefono))
				.getText().toString().trim();
		clienteNuevo.telefonoCel=((EditText)findViewById(R.id.txtTelefonoCel))
				.getText().toString().trim();
		clienteNuevo.email=((EditText)findViewById(R.id.txtEmail))
				.getText().toString().trim();
		clienteNuevo.tipoPersona = listaTipoPersona.get(spTipoCliente.getSelectedItemPosition()).codigo;
		clienteNuevo.estus = listaEstatusCliente.get(spEstatusCliente.getSelectedItemPosition()).codigo;
	
		if (clienteNuevo.nombre.equals("")) {

			Util.MostrarAlertDialog(this,
					"Por favor ingrese su nombre");
			((EditText) findViewById(R.id.txtNombreCliente)).requestFocus();
			return;
		}
		
		if (clienteNuevo.razonSocial.equals("")) {

				Util.MostrarAlertDialog(this,
						"Por favor ingrese la razon social");
				((EditText) findViewById(R.id.txtRazonSocial)).requestFocus();
				return;
		}
		
		if (clienteNuevo.barrio.equals("")) {

			Util.MostrarAlertDialog(this,
					"Por favor ingrese el nombre del barrio");
			((EditText) findViewById(R.id.txtBarrio)).requestFocus();
			return;
		}	
		
		if (clienteNuevo.direccion.equals("")) {

			Util.MostrarAlertDialog(this,
					"Por favor ingrese la direccion");
			((EditText) findViewById(R.id.txtDireccion)).requestFocus();
			return;
		}	
		
		
		if (clienteNuevo.telefono.equals("")) {

			Util.MostrarAlertDialog(this,
					"Por favor ingrese el numero de telefono ");
			((EditText) findViewById(R.id.txtTelefono)).requestFocus();
			return;
		}
		
		
		int pos = spinnerEdCiudad.getSelectedItemPosition();
		Ciudad ciudad =  edListaCiudades.elementAt(pos);
		clienteNuevo.cod_ciudad = ciudad.id;
		
		int pos2 = spEstatusCliente.getSelectedItemPosition();
		ObjectSel estatus =  listaEstatusCliente.elementAt(pos2);
		clienteNuevo.estus = estatus.codigo;
		//boolean ingreso = DataBaseBOJ.guardarClienteModificado(clienteNuevo);
		
		int pos3 = spTipoCliente.getSelectedItemPosition();
		ObjectSel tipoCliente =  listaTipoPersona.elementAt(pos3);
		clienteNuevo.tipoPersona = tipoCliente.codigo;
		
		boolean ingreso = DataBaseBOJ.guardarClienteModificado(clienteNuevo);
		
		
		
		if (ingreso) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Cliente Registrado con Exito").setCancelable(false)
			.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog,	int id) {

					progressDialog = ProgressDialog.show(
						FormEditarClienteActivity.this, "", "Enviando Informacion Cliente...",true);
						progressDialog.show();
						dialog.cancel();
				
						
						Sync sync = new Sync(FormEditarClienteActivity.this, Const.ENVIAR_PEDIDO);
						sync.start();
						
					}
				});

				AlertDialog alert = builder.create();
				alert.show();
		}else{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("Error al registrar cliente")
				.setCancelable(false).setPositiveButton("Aceptar",
						new  DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
				AlertDialog alert = builder.create();
				alert.show();	
		}
		
		
	}
	
	public void editarDpto(){
		edListaDepartamentos= DataBaseBOJ.edListaDpto();
		
		String[] listaItems = new String[edListaDepartamentos.size()];
		
		int contador = -1;
		
		for (Departamento departamento : edListaDepartamentos) {
			
			contador++;
			listaItems[contador]=departamento.nombre;
			
		}
		
		Spinner spinnerDpto =(Spinner)findViewById(R.id.spinnerEdDpto);
		 ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item ,listaItems);
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        spinnerDpto.setAdapter(adapter);
	   
	   spinnerDpto.setAdapter(adapter);
	   
	   
	   spinnerDpto.setOnItemSelectedListener(new OnItemSelectedListener() {
		   
		  

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			
			((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
			Departamento departamentoSeleccionado= edListaDepartamentos.elementAt(position);
			
			editarCiudad(departamentoSeleccionado);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
	});
		
	}
	
	public void editarCiudad(Departamento departamento){
		
		
		
		edListaCiudades= DataBaseBOJ.edListaCiudad(departamento);
		String[] listaItem = new String[edListaCiudades.size()];
		
		int cont =-1;
		

		for (Ciudad ciudad: edListaCiudades) {
			
			cont++;
			listaItem[cont]=ciudad.nombre;
			
		}
		
	
		spinnerEdCiudad =(Spinner)findViewById(R.id.spinnerEdCiudad);
		
		 ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaItem);
			
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        spinnerEdCiudad.setAdapter(adapter);
	        
	        spinnerEdCiudad.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
					
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub
					
				}
			});
	}

	
	public void onBackPressed() {
		
		 AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Si ha modificado informacion no se guardara, �Esta seguro de salir?")
				.setCancelable(false)
				.setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int id) {
							
							
							dialog.cancel();
							
						}
					})
					.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int id) {

							finish();
						}
					});

			AlertDialog alert = builder.create();
			alert.show();
	};
	
	public void OnClickCancelarCliente(View view){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Si ha modificado informacion no se guardara, ¿Esta seguro de salir?")
			.setCancelable(false)
			.setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						
						
						dialog.cancel();
						
					}
				})
				.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {

						finish();
					}
				});

		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public void onResume(){
		super.onResume();
		
	}

	@Override
	public void RespSync(boolean ok, String respuestaServer, final String msg,
			int codeRequest) {
		

		final String mensaje= ok ? "Informacion registrada con exito en el servidor": msg;

		
		
		if (progressDialog != null)
			progressDialog.cancel();

		if(ok){
		
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				
				AlertDialog.Builder builder= new AlertDialog.Builder(FormEditarClienteActivity.this);
				builder.setMessage(mensaje).setCancelable(false).setPositiveButton("Aceptar",
						new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int id) {
						
						//dialog.cancel();
						finish();
						
						//progressDialog.cancel();
					}
				});
				
				AlertDialog alert = builder.create();
				alert.show();
				
			}
		});
		
		}else{
			this.runOnUiThread(new Runnable() {

				public void run() {

					Util.MostrarAlertDialog(FormEditarClienteActivity.this, msg);
				}
			});
		}
		}
	
	public void PoblarTipoPersona() {

		listaTipoPersona = DataBaseBOJ.listaTipoPersona();

		String[] listaItems = new String[listaTipoPersona.size()];

		int contador = -1;

		for (ObjectSel departamento : listaTipoPersona) {

			contador++;
			listaItems[contador] = departamento.descripcion;

		}

		spTipoCliente = (Spinner) findViewById(R.id.spTipoCliente);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaItems);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spTipoCliente.setAdapter(adapter);

	}

	public void estatusCliente() {

		listaEstatusCliente = DataBaseBOJ.listaEstatusCliente();

		String[] listaItems = new String[listaEstatusCliente.size()];

		int contador = -1;
		
	
		for (ObjectSel departamento : listaEstatusCliente) {

			contador++;
			listaItems[contador] = departamento.descripcion;

		}

		spEstatusCliente = (Spinner) findViewById(R.id.spEstatusCliente);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaItems);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spEstatusCliente.setAdapter(adapter);

	}
	
}
