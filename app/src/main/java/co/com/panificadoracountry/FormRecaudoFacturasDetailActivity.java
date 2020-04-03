package co.com.panificadoracountry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;

import android.app.ProgressDialog;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import co.com.panificadoracountry.R;
import co.com.woosim.printer.WoosimR240;
import co.com.BusinessObject.DataBaseBO;
import co.com.BusinessObject.DataBaseBOJ;
import co.com.Conexion.Sync;
import co.com.DataObject.Cartera;
import co.com.DataObject.Cliente;
import co.com.DataObject.ControlImpresion;
import co.com.DataObject.Departamento;
import co.com.DataObject.Encabezado;
import co.com.DataObject.ItemListView;
import co.com.DataObject.SaldoCancelado;
import co.com.DataObject.TipoPago;
import co.com.DataObject.Usuario;



public class FormRecaudoFacturasDetailActivity extends Activity implements Sincronizador {
	
	
	Vector<TipoPago> listaTipoPago;
	TipoPago tipoPagoSeleccionado;
	ProgressDialog progressDialog;
	TextView txtDoc, txtCliente, txtVen, txtDiasVen, txtValor;
	EditText txt_valor, txt_observaciones;
	Encabezado encabezado;
	
	public long tiempoClick1 = 0;
	private Dialog dialogImprimirFacturaDeposito;
	public final static String CONFIG_IMPRESORA = "PRINTER";
	public final static String MAC_IMPRESORA    = "MAC";
	public final static String LABEL_IMPRESORA  = "LABEL";
	private WoosimR240 wR240 = null;
	private FormRecaudoFacturasDetailActivity actividad;
	private Cliente cliente;
	private SaldoCancelado saldoCancelado;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_recaudo_facturas_detail);
		cargarDatos();
		cargarTipoPago();		
	}
	
	
    public void onClickCancelarRecaudoFactura(View view) {
		
		finish();
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}
	
	public void cargarDatos(){
		
		try{
			
			
			txtDoc = (TextView) findViewById(R.id.lblDoc);
			txtCliente = (TextView) findViewById(R.id.lblCliente);
			txtVen = (TextView) findViewById(R.id.lblFechaVen);
			txtDiasVen = (TextView) findViewById(R.id.lblDiasVen);
			txtValor = (TextView) findViewById(R.id.lblValor);
			txt_valor = (EditText) findViewById(R.id.txt_valor);
			txt_observaciones = (EditText) findViewById(R.id.txt_observaciones);
			
			txtDoc.setText(Main.cartera.documento);
			txtCliente.setText(Main.cliente.nombre);
			txtVen.setText(Main.cartera.FechaVecto.substring(0, 10));
			txtDiasVen.setText(""+Main.cartera.dias);
			txtValor.setText(Main.cartera.strSaldo);
			txt_valor.setText(Main.cartera.strSaldo);
			
			txt_valor.setEnabled(false);
			
			
			
		}catch(Exception e){
			Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	 public void onRadioButtonClicked(View v)
    {

        boolean  checked = ((RadioButton) v).isChecked();  
        switch(v.getId()){
            case R.id.radioTotal:
                if(checked){
                	txt_valor.setText(Main.cartera.strSaldo);
                	txt_valor.setEnabled(false);
                }
            break;
            case R.id.radioParcial:
                if(checked){
                	txt_valor.setEnabled(true);
                }        
            break;
        }
    }
	 
	
	
	public void cargarTipoPago(){
		try{
			listaTipoPago = DataBaseBOJ.TipoPago();
			
			String[] listaItems = new String[listaTipoPago.size()];
			
			int contador = -1;
			
			
			for (TipoPago tipoPago : listaTipoPago) {
				contador++;
				listaItems[contador]=tipoPago.descripcion;
			}
			
			Spinner sp_tipoPago =(Spinner)findViewById(R.id.sp_tipoPago);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item ,listaItems);
		    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		    sp_tipoPago.setAdapter(adapter);
		    
		    sp_tipoPago.setOnItemSelectedListener(new OnItemSelectedListener() {
	
				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
					tipoPagoSeleccionado = listaTipoPago.elementAt(position);
					
				}
	
				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub
					
				}
		    });
		}catch(Exception e){
			Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
		}
			
	}
	
	public void onClickSaveRecaudo(View view){
		try{
			Usuario usuario = DataBaseBO.ObtenerUsuario();
			 
			if(Float.parseFloat(txt_valor.getText().toString()) > Float.parseFloat(Main.cartera.strSaldo)){
				Toast.makeText(getBaseContext(), "El valor no puede ser mayor al saldo de la Factura ", Toast.LENGTH_LONG).show();
			}else{
				int opcionSaldos = 0;
				float cambioValor = 0;
				
				if(Float.parseFloat(txt_valor.getText().toString()) < Float.parseFloat(Main.cartera.strSaldo)){
					opcionSaldos = 1;
					cambioValor = Float.parseFloat(Main.cartera.strSaldo) - Float.parseFloat(txt_valor.getText().toString());
				}
				String codSaldo = usuario.codigoVendedor + Util.ObtenerFechaId();
				String noFacura = DataBaseBO.getFactura(Main.cartera.documento);
				
				
				saldoCancelado = new SaldoCancelado();
				saldoCancelado.cod_usuario = Main.usuario.codigoVendedor;
				saldoCancelado.cod_saldo = Main.cartera.codSaldo;
				saldoCancelado.saldo_recibido = Float.parseFloat(txt_valor.getText().toString());
				saldoCancelado.codEstado = Integer.parseInt(tipoPagoSeleccionado.cod_pago);
				saldoCancelado.observacion = txt_observaciones.getText().toString();
				saldoCancelado.factura = noFacura;
				
				boolean ingreso = DataBaseBO.guardarSaldosRecaudoClientes(saldoCancelado);
				
				boolean cambiarSaldo = DataBaseBO.editarSaldoPendinte(saldoCancelado.cod_saldo, cambioValor, opcionSaldos);
				
				String	numero_doc = DataBaseBO.ObtenterNumeroDoc(usuario.codigoVendedor);
				 
			
				
				if (ingreso == cambiarSaldo) {
					
					DataBaseBO.guardarSaldosCliente(Main.usuario.codigoVendedor, numero_doc, codSaldo, saldoCancelado.saldo_recibido, 3, Main.cliente.codigo);
					DataBaseBO.guardarSaldosCanceladosCliente(Main.usuario.codigoVendedor, noFacura, numero_doc, codSaldo, saldoCancelado.saldo_recibido, 1);
						
		
					
					Util.MostrarAlertDialog(FormRecaudoFacturasDetailActivity.this,
							"Saldo pago automatico");
					
					
	
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage("Recaudo Registrado con Exito").setCancelable(false)
					.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
	
						public void onClick(DialogInterface dialog,	int id) {
							
							
	
							progressDialog = ProgressDialog.show(
						    FormRecaudoFacturasDetailActivity.this, "", "Enviando Informacion Recaudo...",true);
							progressDialog.show();
							
						
							Sync sync = new Sync(FormRecaudoFacturasDetailActivity.this, Const.ENVIAR_PEDIDO);
							sync.start();
							
							
							
							dialog.cancel();
							
							}
						
						});
					
					
	
						AlertDialog alert = builder.create();
						alert.show();
				}else{
						AlertDialog.Builder builder = new AlertDialog.Builder(this);
						builder.setMessage("Error al registrar Recaudo")
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
		}catch(Exception e){
			Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	public void RespuestaEnviarInfo(final boolean ok, String respuestaServer, String msg) {

		
		
		final String mensaje = ok ? "Informacion Registrada con Exito en el servidor" : msg;		

		if (progressDialog != null)
			progressDialog.cancel();

		this.runOnUiThread(new Runnable() {

			public void run() {

				AlertDialog.Builder builder = new AlertDialog.Builder(FormRecaudoFacturasDetailActivity.this);
				builder.setMessage(mensaje).setCancelable(false);
				builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						mostrarDialogImprimirFactura();
						
						//dialog.cancel();
						//FormRecaudoFacturasDetailActivity.this.finish();
					}
				});

				AlertDialog alert = builder.create();
				alert.show();
			}
		});
	}
	


	@Override
	public void RespSync(boolean ok, String respuestaServer, String msg, int codeRequest) {
		// TODO Auto-generated method stub
		switch (codeRequest) {

		case Const.ENVIAR_PEDIDO:
			RespuestaEnviarInfo(ok, respuestaServer, msg);
			break;
		}	
	}
	
	private void mostrarDialogImprimirFactura(){   

		dialogImprimirFacturaDeposito = new Dialog(FormRecaudoFacturasDetailActivity.this);
		dialogImprimirFacturaDeposito.setContentView(R.layout.dialog_imprimir_factura_pedido);
		dialogImprimirFacturaDeposito.setTitle("Imprimir Factura");


		((EditText)dialogImprimirFacturaDeposito.findViewById(R.id.txtNumCopias)).setText( "1" );

		((Button)dialogImprimirFacturaDeposito.findViewById(R.id.btnAceptarImprimirFactura)).setOnClickListener(new OnClickListener() {

			

			public void onClick(View v) {

				String txt = ((EditText)dialogImprimirFacturaDeposito.findViewById(R.id.txtNumCopias)).getText().toString();

				int numCopias = Util.ToInt( txt );
				
				/*Si se presenta un error con el numero asignar una copia para imprimir.*/
				if(numCopias == 0){
					numCopias = 1;
				}

				progressDialog = ProgressDialog.show(FormRecaudoFacturasDetailActivity.this, "", "Por Favor Espere...\n\nProcesando Informacion!", true);
				progressDialog.show();

				SharedPreferences settings = getSharedPreferences(Const.CONFIG_IMPRESORA, MODE_PRIVATE);
				String macImpresora = settings.getString(Const.MAC_IMPRESORA, "-");

				if (macImpresora.equals("-")) {

					if( progressDialog != null ){

						progressDialog.dismiss();
					}

					Util.MostrarAlertDialog(FormRecaudoFacturasDetailActivity.this, "Aun no hay Impresora Establecida.\n\nPor Favor primero Configure la Impresora!");

				} else {
					Usuario usuario = DataBaseBO.CargarUsuario();
					imprimir_WSP_R240(macImpresora, usuario, numCopias);					
				}
			}
		});

		((Button)dialogImprimirFacturaDeposito.findViewById(R.id.btnCancelarImprimirFactura)).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				dialogImprimirFacturaDeposito.cancel();
				FormRecaudoFacturasDetailActivity.this.finish();
			}
		});

		dialogImprimirFacturaDeposito.setCancelable(false);
		dialogImprimirFacturaDeposito.show();
	}
    
    private void imprimir_WSP_R240(final String mac, final Usuario usuario, final int numCopias){
		new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				SharedPreferences settings = getSharedPreferences(CONFIG_IMPRESORA, MODE_PRIVATE);
				final String macImpresora = settings.getString(MAC_IMPRESORA, "-");

				if (macImpresora.equals("-")) {
					progressDialog.dismiss();
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(FormRecaudoFacturasDetailActivity.this,
									"Aun no hay Impresora Predeterminada.\n\nPor Favor primero Configure la Impresora!", Toast.LENGTH_SHORT)
							.show();
						}
					});
				}
				else {

					if (wR240 == null) {
						wR240 = new WoosimR240(FormRecaudoFacturasDetailActivity.this);
					}
					int conect = wR240.conectarImpresora(macImpresora);

					switch (conect) {
					case 1:
						ControlImpresion control = new ControlImpresion();
						
						if (saldoCancelado != null) {

							for (int i = 0; i < numCopias; i++) {
								if(i > 0){
									control.original = false;
								}
								wR240.generarEncabezadoTirillaRecaudo(saldoCancelado,Main.cliente, Main.usuario,true);
								int succes = wR240.imprimirBuffer(true);
								
								try {
									Thread.sleep(Const.timeWait);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
						else {
							progressDialog.dismiss();
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(FormRecaudoFacturasDetailActivity.this,
											"Aun no hay datos para imprimir, revise el deposito.", Toast.LENGTH_SHORT)
									.show();
								}
							});
						}
						break;

					case -2:
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(FormRecaudoFacturasDetailActivity.this, "-2 fallo conexion", Toast.LENGTH_SHORT).show();
							}
						});
						break;

					case -8:
						progressDialog.dismiss();
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Util.MostrarAlertDialog(FormRecaudoFacturasDetailActivity.this,
										"Bluetooth apagado. Por favor habilite el bluetoth para imprimir.");
							}
						});
						break;

					default:
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(FormRecaudoFacturasDetailActivity.this, "error desconocido", Toast.LENGTH_SHORT).show();
							}
						});
						break;
					}

					try {
						Thread.sleep(Const.timeWait);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					if (wR240 != null) {
						wR240.desconectarImpresora();
						progressDialog.dismiss();
						handlerFinish.sendEmptyMessage(0);
					}
				}
				Looper.myLooper().quit();
			}
		}).start();
	}
    
    private Handler handlerFinish = new Handler() {

		@Override
		public void handleMessage(Message msg) {


			AlertDialog.Builder builder = new AlertDialog.Builder(FormRecaudoFacturasDetailActivity.this);
			builder.setMessage("Solicitud Almacenada Exitosamente")
			.setCancelable(false)
			.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int id) {
					
					dialog.cancel();		
					FormRecaudoFacturasDetailActivity.this.finish();
				}
			});

			AlertDialog alert = builder.create();
			alert.show();
		}
	};


	


}
