package co.com.panificadoracountry;

import java.util.Objects;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import co.com.BusinessObject.DataBaseBO;
import co.com.BusinessObject.DataBaseBOJ;
import co.com.Conexion.Sync;
import co.com.DataObject.Ciudad;
import co.com.DataObject.Cliente;
import co.com.DataObject.ClienteNuevo;
import co.com.DataObject.Convencion;
import co.com.DataObject.Departamento;
import co.com.DataObject.ObjectSel;

public class FormClienteNuevoActivity extends Activity implements Sincronizador {

	public static final String TAG = FormClienteNuevoActivity.class.getName();

	ProgressDialog progressDialog;
	Vector<Departamento> listaDepartamentos;
	Vector<Ciudad> listaCiudad;
	Vector<ObjectSel> listaTipoPersona;
	Vector<ObjectSel> listaEstatusCliente;
	Spinner spinnerCiudad;
	Dialog dialogIngresarDireccion;
	Dialog dialogVerDireccion;
	public String direccionAux = "";
	ClienteNuevo clienteNuevo;
	public Spinner spTipoCliente, spEstatusCliente;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_cliente_nuevo);
		Util.cerrarTeclado(this);
		PoblarDpto();
		PoblarTipoPersona();
		estatusCliente();
		asignarCodigoClienteNuevo();

	}

	public void OnClickGuardarCliente(View view) {

		clienteNuevo = new ClienteNuevo();

		if (Main.cliente == null) {

			Main.cliente = new Cliente();
		}

		clienteNuevo.codigo = ((EditText) findViewById(R.id.txtCodigoCliente)).getText().toString().trim();
		clienteNuevo.nit = ((EditText) findViewById(R.id.txtNit)).getText().toString().trim();
		clienteNuevo.nombre = ((EditText) findViewById(R.id.txtNombreCliente)).getText().toString().trim();
		clienteNuevo.razonSocial = ((EditText) findViewById(R.id.txtRazonSocial)).getText().toString().trim();
		clienteNuevo.barrio = ((EditText) findViewById(R.id.txtBarrio)).getText().toString().trim();
		clienteNuevo.direccion = ((EditText) findViewById(R.id.txtDireccion)).getText().toString().trim();
		clienteNuevo.telefono = ((EditText) findViewById(R.id.txtTelefono)).getText().toString().trim();
		clienteNuevo.telefonoCel = ((EditText) findViewById(R.id.txtTelefonoCel)).getText().toString().trim();
		clienteNuevo.email = ((EditText) findViewById(R.id.txtEmail)).getText().toString().trim();
		clienteNuevo.tipoPersona = listaTipoPersona.get(spTipoCliente.getSelectedItemPosition()).codigo;
		clienteNuevo.estus = listaEstatusCliente.get(spEstatusCliente.getSelectedItemPosition()).codigo;

		if (clienteNuevo.codigo.equals("")) {

			Util.MostrarAlertDialog(this,

					"Por favor ingrese el Codigo del Cliente");

			((EditText) findViewById(R.id.txtCodigoCliente)).requestFocus();
			return;
		}

		if (clienteNuevo.nombre.equals("")) {

			Util.MostrarAlertDialog(this, "Por favor ingrese el nombre del Propietario");
			((EditText) findViewById(R.id.txtNombreCliente)).requestFocus();
			return;
		}

		if (clienteNuevo.razonSocial.equals("")) {

			Util.MostrarAlertDialog(this, "Por favor ingrese la Razon Social");

			((EditText) findViewById(R.id.txtRazonSocial)).requestFocus();
			return;
		}

		if (clienteNuevo.barrio.equals("")) {

			Util.MostrarAlertDialog(this, "Por favor ingrese el nombre del barrio");
			((EditText) findViewById(R.id.txtBarrio)).requestFocus();
			return;
		}

		if (clienteNuevo.direccion.equals("")) {

			Util.MostrarAlertDialog(this, "Por favor ingrese la Direccion");

			((EditText) findViewById(R.id.txtDireccion)).requestFocus();
			return;
		}

		if (clienteNuevo.telefono.equals("") || clienteNuevo.telefono.length() < 7) {

			Util.MostrarAlertDialog(this, "Por favor ingrese un numero de telefono valido");

			((EditText) findViewById(R.id.txtTelefono)).requestFocus();
			return;
		}

		if (clienteNuevo.telefonoCel.equals("") || clienteNuevo.telefonoCel.length() < 10) {

			Util.MostrarAlertDialog(this, "Por favor ingrese un numero de telefono celular valido");

			((EditText) findViewById(R.id.txtTelefonoCel)).requestFocus();
			return;
		}

		int pos = spinnerCiudad.getSelectedItemPosition();

		Ciudad ciudad = listaCiudad.elementAt(pos);

		clienteNuevo.cod_ciudad = ciudad.id;
		clienteNuevo.listaPrecio = Const.LISTA_PRECIO_DEFECTO;

		boolean ingreso = DataBaseBOJ.guardarClienteNuevo(clienteNuevo);

		if (ingreso) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Cliente Registrado con Exito").setCancelable(false).setPositiveButton("Aceptar",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int id) {

							progressDialog = ProgressDialog.show(FormClienteNuevoActivity.this, "",
									"Enviando Informacion Cliente...", true);
							progressDialog.show();
							dialog.cancel();

							Sync sync = new Sync(FormClienteNuevoActivity.this, Const.ENVIAR_PEDIDO);
							sync.start();
						}
					});

			AlertDialog alert = builder.create();
			alert.show();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Error al registrar cliente").setCancelable(false).setPositiveButton("Aceptar",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

						}
					});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}

	public void PoblarDpto() {

		listaDepartamentos = DataBaseBOJ.listaDpto();

		String[] listaItems = new String[listaDepartamentos.size()];

		int contador = -1;

		for (Departamento departamento : listaDepartamentos) {

			contador++;
			listaItems[contador] = departamento.nombre;

		}

		Spinner spinnerDpto = (Spinner) findViewById(R.id.spinnerDpto);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaItems);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerDpto.setAdapter(adapter);

		spinnerDpto.setAdapter(adapter);

		spinnerDpto.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

				((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
				Departamento departamentoSeleccionado = listaDepartamentos.elementAt(position);

				PoblarCiudad(departamentoSeleccionado);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

	}

	public void PoblarCiudad(Departamento departamento) {

		listaCiudad = DataBaseBOJ.listaCiudad(departamento);
		String[] listaItem = new String[listaCiudad.size()];

		int cont = -1;

		for (Ciudad ciudad : listaCiudad) {

			cont++;
			listaItem[cont] = ciudad.nombre;

		}

		spinnerCiudad = (Spinner) findViewById(R.id.spinnerCiudad);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaItem);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerCiudad.setAdapter(adapter);

		spinnerCiudad.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void OnClickCancelarCliente(View view) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage("Si ha ingresado informacion sera eliminada, Esta Seguro de Salir")

				.setCancelable(false).setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {

						dialog.cancel();

					}
				}).setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {

						finish();
					}
				});

		AlertDialog alert = builder.create();
		alert.show();
	}

	public void onBackPressed() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage("Si ha ingresado informacion sera eliminada, Esta Seguro de Salir")

				.setCancelable(false).setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {

						dialog.cancel();

					}
				}).setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {

						finish();
					}
				});

		AlertDialog alert = builder.create();
		alert.show();
	};

	@Override
	public void RespSync(boolean ok, String respuestaServer, final String msg, int codeRequest) {

		final String mensaje = ok
				? "Informacion registrada con exito en el servidor\n\n\n\nDesea Tomarle Pedido a Este Cliente Nuevo"
				: msg + "\n\n\n\nDesea Tomarle Pedido a Este Cliente Nuevo";

		if (progressDialog != null)
			progressDialog.cancel();

		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				AlertDialog.Builder builder = new AlertDialog.Builder(FormClienteNuevoActivity.this);
				builder.setMessage(mensaje).setCancelable(false)
						.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int id) {

								dialog.cancel();
								Cliente cliente = new Cliente();
								cliente.codigo = clienteNuevo.codigo;
								cliente.nit = clienteNuevo.nit;
								cliente.nombre = clienteNuevo.nombre;
								cliente.razonSocial = clienteNuevo.razonSocial;
								cliente.direccion = clienteNuevo.direccion;
								cliente.ciudad = clienteNuevo.ciudad;
								cliente.telefono = clienteNuevo.telefono;
								cliente.telefonoCel = clienteNuevo.telefonoCel;
								cliente.email = clienteNuevo.email;
								cliente.barrio = clienteNuevo.barrio;
								cliente.listaPrecio = clienteNuevo.listaPrecio;
								cliente.esClienteNuevo = true;

								Main.cliente = cliente;
								Cliente.save(FormClienteNuevoActivity.this, Main.cliente);
								Intent formInfoCliente = new Intent(FormClienteNuevoActivity.this,
										FormInfoClienteActivity.class);
								startActivityForResult(formInfoCliente, Const.RESP_PEDIDO_EXITOSO);

							}
						})

						.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int id) {

								dialog.cancel();
								finish();
								// progressDialog.cancel();
							}
						});

				AlertDialog alert = builder.create();
				alert.show();

			}
		});

	}

	public void OnClickIngresarDireccion(View view) {

		mostrarDialogoIngresarDireccion();

	}

	public void mostrarDialogoIngresarDireccion() {

		dialogIngresarDireccion = new Dialog(this);
		dialogIngresarDireccion.requestWindowFeature(Window.FEATURE_LEFT_ICON);
		dialogIngresarDireccion.setContentView(R.layout.form_ingresar_direccion);
		dialogIngresarDireccion.setTitle("Ingresar Direccion");

		((Button) dialogIngresarDireccion.findViewById(R.id.btnIngresarDireccion))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {// onClick

						direccionAux = "";

						Spinner spTipoConvenciones1 = (Spinner) dialogIngresarDireccion
								.findViewById(R.id.spinnerTipoUbicacion1);

						EditText txtComplementoDireccion1 = (EditText) dialogIngresarDireccion
								.findViewById(R.id.txtComplementoDireccion);

						Spinner spTipoConvenciones3 = (Spinner) dialogIngresarDireccion
								.findViewById(R.id.spinnerTipoUbicacion3);

						EditText txtComplementoDireccion3 = (EditText) dialogIngresarDireccion
								.findViewById(R.id.txtComplementoDireccion3);

						Spinner spTipoConvenciones4 = (Spinner) dialogIngresarDireccion
								.findViewById(R.id.spinnerTipoUbicacion4);

						EditText txtComplementoDireccion4 = (EditText) dialogIngresarDireccion
								.findViewById(R.id.txtComplementoDireccion4);

						Spinner spTipoConvenciones5 = (Spinner) dialogIngresarDireccion
								.findViewById(R.id.spinnerTipoUbicacion5);

						EditText txtComplementoDireccion5 = (EditText) dialogIngresarDireccion
								.findViewById(R.id.txtComplementoDireccion5);

						int contadorConvenciones = 0;

						EditText txtComplementoDireccion21 = (EditText) dialogIngresarDireccion
								.findViewById(R.id.txtComplementoDireccion21);
						EditText txtComplementoDireccion22 = (EditText) dialogIngresarDireccion
								.findViewById(R.id.txtComplementoDireccion22);
						EditText txtComplementoDireccion23 = (EditText) dialogIngresarDireccion
								.findViewById(R.id.txtComplementoDireccion23);
						EditText txtComplementoDireccion24 = (EditText) dialogIngresarDireccion
								.findViewById(R.id.txtComplementoDireccion24);

						if (!txtComplementoDireccion1.getText().toString().equals("")) {

							direccionAux = direccionAux + spTipoConvenciones1.getSelectedItem().toString();
							direccionAux = direccionAux + " " + txtComplementoDireccion1.getText().toString();
							contadorConvenciones++;

						}

						if (!txtComplementoDireccion21.getText().toString().equals("")
								|| !txtComplementoDireccion22.getText().toString().equals("")
								|| !txtComplementoDireccion23.getText().toString().equals("")
								|| !txtComplementoDireccion24.getText().toString().equals("")) {

							if (direccionAux.equals("")) {

								direccionAux = direccionAux + "  " + txtComplementoDireccion21.getText().toString();
								direccionAux = direccionAux + "" + txtComplementoDireccion22.getText().toString();
								direccionAux = direccionAux + " - " + txtComplementoDireccion23.getText().toString();
								direccionAux = direccionAux + "" + txtComplementoDireccion24.getText().toString();

							} else {

								direccionAux = direccionAux + "  " + txtComplementoDireccion21.getText().toString();
								direccionAux = direccionAux + "" + txtComplementoDireccion22.getText().toString();
								direccionAux = direccionAux + " - " + txtComplementoDireccion23.getText().toString();
								direccionAux = direccionAux + "" + txtComplementoDireccion24.getText().toString();

							}

							contadorConvenciones++;

						}

						if (!txtComplementoDireccion3.getText().toString().equals("")) {

							if (direccionAux.equals("")) {

								direccionAux = direccionAux + spTipoConvenciones3.getSelectedItem().toString();
								direccionAux = direccionAux + " " + txtComplementoDireccion3.getText().toString();

							} else {

								direccionAux = direccionAux + " " + spTipoConvenciones3.getSelectedItem().toString();
								direccionAux = direccionAux + " " + txtComplementoDireccion3.getText().toString();

							}
							contadorConvenciones++;

						}

						if (!txtComplementoDireccion4.getText().toString().equals("")) {

							if (direccionAux.equals("")) {

								direccionAux = direccionAux + spTipoConvenciones4.getSelectedItem().toString();
								direccionAux = direccionAux + " " + txtComplementoDireccion4.getText().toString();

							} else {

								direccionAux = direccionAux + " " + spTipoConvenciones4.getSelectedItem().toString();
								direccionAux = direccionAux + " " + txtComplementoDireccion4.getText().toString();

							}
							contadorConvenciones++;

						}

						if (!txtComplementoDireccion5.getText().toString().equals("")) {

							if (direccionAux.equals("")) {

								direccionAux = direccionAux + spTipoConvenciones5.getSelectedItem().toString();
								direccionAux = direccionAux + " " + txtComplementoDireccion5.getText().toString();

							} else {

								direccionAux = direccionAux + " " + spTipoConvenciones5.getSelectedItem().toString();
								direccionAux = direccionAux + " " + txtComplementoDireccion5.getText().toString();

							}

							contadorConvenciones++;
						}

						if (direccionAux.equals("")) {

							// dialogIngresarDireccion.cancel();
							Util.MostrarAlertDialog(FormClienteNuevoActivity.this, "No Ha Ingresado Ninguna Direccion",
									1);

						} else {

							if (contadorConvenciones <= 1) {

								Util.MostrarAlertDialog(FormClienteNuevoActivity.this,
										"Debe Utilizar al Menos 2 Convenciones para Ingresar una Direccion", 1);

							} else {

								if (dialogVerDireccion == null) {
									dialogVerDireccion = new Dialog(FormClienteNuevoActivity.this);
									dialogVerDireccion.requestWindowFeature(Window.FEATURE_LEFT_ICON);
								}
								dialogVerDireccion.setContentView(R.layout.dialog_direccion);
								dialogVerDireccion.setTitle("Direccion");

								((Button) dialogVerDireccion.findViewById(R.id.btnAceptarDialogDireccion))
										.setOnClickListener(new OnClickListener() {

											public void onClick(View v) {
												// TODO Auto-generated method
												// stub

												dialogVerDireccion.cancel();

												((EditText) findViewById(R.id.txtDireccion)).setText(direccionAux);

												dialogIngresarDireccion.cancel();

											}
										});

								((Button) dialogVerDireccion.findViewById(R.id.btnCancelarDialogDireccion))
										.setOnClickListener(new OnClickListener() {

											public void onClick(View v) {
												// TODO Auto-generated method
												// stub

												dialogVerDireccion.cancel();

											}
										});

								((TextView) dialogVerDireccion.findViewById(R.id.lblTituloTextField2))
										.setText(direccionAux);

								dialogVerDireccion.show();

								dialogVerDireccion.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
										R.drawable.address);

							}

						} //

					}// onClick
				});

		((Button) dialogIngresarDireccion.findViewById(R.id.btnCancelarIngresarDireccion))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {// onClick

						dialogIngresarDireccion.cancel();

					}// onClick
				});

		String[] items;
		Vector<String> listItems = new Vector<String>();
		Vector<Convencion> listConvenciones = DataBaseBO.TipoConvenciones(listItems);

		if (listItems.size() > 0) {
			items = new String[listItems.size()];
			listItems.copyInto(items);

		} else {
			items = new String[] {};

			if (listConvenciones != null)
				listConvenciones.removeAllElements();
		}

		Spinner spTipoConvenciones1 = (Spinner) dialogIngresarDireccion.findViewById(R.id.spinnerTipoUbicacion1);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spTipoConvenciones1.setAdapter(adapter);

		//// Spinner spTipoConvenciones2 =
		//// (Spinner)dialogIngresarDireccion.findViewById(R.id.spinnerTipoUbicacion2);
		//// ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
		//// android.R.layout.simple_spinner_item, items);
		//// adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//// spTipoConvenciones2.setAdapter(adapter2);

		Spinner spTipoConvenciones3 = (Spinner) dialogIngresarDireccion.findViewById(R.id.spinnerTipoUbicacion3);
		ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spTipoConvenciones3.setAdapter(adapter3);

		Spinner spTipoConvenciones4 = (Spinner) dialogIngresarDireccion.findViewById(R.id.spinnerTipoUbicacion4);
		ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
		adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spTipoConvenciones4.setAdapter(adapter4);

		Spinner spTipoConvenciones5 = (Spinner) dialogIngresarDireccion.findViewById(R.id.spinnerTipoUbicacion5);
		ArrayAdapter<String> adapter5 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
		adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spTipoConvenciones5.setAdapter(adapter5);

		dialogIngresarDireccion.show();

		dialogIngresarDireccion.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.address);

	}

	public void asignarCodigoClienteNuevo() {

		String codigo = Util.ObtenerFechaId("yyyy-MM-dd HH:mm:ss.SSS");
		codigo = codigo.replace("-", "");
		codigo = codigo.replace(" ", "");
		codigo = codigo.replace(".", "");
		codigo = codigo.replace(":", "");
		((EditText) findViewById(R.id.txtCodigoCliente)).setText(codigo);

	}

	public void onResume() {

		super.onResume();

		if (Main.usuario == null || Main.usuario.codigoVendedor == null)
			DataBaseBO.CargarInfomacionUsuario();

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		finish();

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
