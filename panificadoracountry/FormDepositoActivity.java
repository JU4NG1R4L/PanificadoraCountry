package co.com.panificadoracountry;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import co.com.BusinessObject.DataBaseBO;
import co.com.BusinessObject.DataBaseBOJ;
import co.com.DataObject.Banco;
import co.com.DataObject.Cliente;
import co.com.DataObject.ControlImpresion;
import co.com.DataObject.Deposito;
import co.com.DataObject.DetalleDeposito;
import co.com.DataObject.DetalleImprimir;
import co.com.DataObject.DetalleProducto;
import co.com.DataObject.ItemListView;
import co.com.DataObject.SaldoCancelado;
import co.com.DataObject.Usuario;
import co.com.woosim.printer.WoosimR240;

public class FormDepositoActivity extends Activity {
	
	
	/* definicion de objetos usados para imprimir*/

	/**
	 * BluetoothAdapter para la conexion.
	 */
	private BluetoothAdapter bluetoothAdapter = null;

	public final static String CONFIG_IMPRESORA = "PRINTER";
	public final static String MAC_IMPRESORA    = "MAC";
	public final static String LABEL_IMPRESORA  = "LABEL";
	
	/**
	 * Referencia a la impresora WSP-R240 WOOSIM.
	 */
	private WoosimR240 wR240 = null;

	Vector<SaldoCancelado> listaSaldos;
	protected SaldoCancelado saldoSelect;
	private Dialog dialogDeposito;
	private EditText editTextCompBancarioDep;
	private EditText editTextObservacionesDep;
	private String docDeposito;
	private Vector<Banco> listaBancos;
	private EditText editTextCantidadDep;
	private Spinner spnBancosDeposito;
	private Dialog dialogImprimirFacturaDeposito;
	private ProgressDialog progressDialog;
	private Deposito deposito;
	private ArrayList<DetalleDeposito> detallesDeposito;
	
	
	
	String codSaldo = "";


	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_deposito);
		DataBaseBO.ValidarUsuario();
		
		codSaldo = this.getIntent().getStringExtra("codSaldo");
		
		if(codSaldo == null)
			codSaldo = "";
		
		buscarSaldos();
		Util.closeTecladoStartActivity(this);
	}
	
	
	

	public void onClickBuscarSaldos(View view) {
		DataBaseBO.ValidarUsuario();
		buscarSaldos();
	}
	
	private void buscarSaldos() {

		EditText txtBusquedaSaldo = (EditText)findViewById(R.id.txtBusquedaSaldo);
		TextView lblTotalDeposito = (TextView)findViewById(R.id.lblTotalDeposito);
		String cadBusqueda = txtBusquedaSaldo.getText().toString().trim();
		
		
		String totalSaldo="";
		
		OcultarTeclado(txtBusquedaSaldo);

			ItemListView[] listaItems = null;
			Vector<ItemListView> listaItemsSaldos = new Vector<ItemListView>();
			
			if(codSaldo.equals(""))
			listaSaldos = DataBaseBOJ.buscarDepositos(listaItemsSaldos, cadBusqueda);
			else
			listaSaldos = DataBaseBOJ.buscarDepositos(listaItemsSaldos, cadBusqueda,codSaldo);
			totalSaldo = DataBaseBOJ.SaldoTotalDeposito();	
			
			
			if (listaItemsSaldos.size() > 0) {

				listaItems = new ItemListView[listaItemsSaldos.size()];
				listaItemsSaldos.copyInto(listaItems);
				lblTotalDeposito.setText("Total a Depositar: $ "+totalSaldo);

			} else {
				
				listaItems = new ItemListView[]{};
				lblTotalDeposito.setText("Total a Depositar:  $0 ");

				if (listaSaldos != null)
					listaSaldos.removeAllElements();

				Toast.makeText(getApplicationContext(), "Busqueda sin resultados", Toast.LENGTH_SHORT).show();
			}
			
			ListViewAdapterCheckBox adapter = new ListViewAdapterCheckBox(this, listaItems, R.drawable.producto);
			
			
			ListView listaBusquedaSaldos = (ListView)findViewById(R.id.listaBusquedaSaldos);
			
			listaBusquedaSaldos.setAdapter(adapter);
			
			
//			ListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		txtBusquedaSaldo.setText("");
	}

	public void OpcionSeleccionada(String label, int inputType) {

		EditText txtBusquedaSaldo = (EditText)findViewById(R.id.txtBusquedaSaldo);

		txtBusquedaSaldo.setText("");
		txtBusquedaSaldo.setInputType(inputType);

		ListViewAdapter adapter = new ListViewAdapter(this, new ItemListView[]{}, R.drawable.cliente, 0x2E65AD);
		ListView listaBusquedaClientes = (ListView)findViewById(R.id.listaBusquedaSaldos);
		listaBusquedaClientes.setAdapter(adapter);
		

		if (listaSaldos != null)
			listaSaldos.removeAllElements();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == Const.RESP_PEDIDO_EXITOSO && resultCode == RESULT_OK) {

			((Spinner)findViewById(R.id.cbBusquedaCliente)).setSelection(0, true);
			OpcionSeleccionada("Ingrese Parte del Nombre:", InputType.TYPE_CLASS_TEXT);
			
		}else if (requestCode == Const.RESP_TOMAR_FOTO && resultCode == RESULT_OK) {
			
			//Drawable imgFoto = resizedImage(320, 480);
			Bundle extras = data.getExtras();
			Bitmap imageBitmap = (Bitmap) extras.get("data");
			Drawable imgFoto = (Drawable) new BitmapDrawable(imageBitmap);// resizedImage(320, 480);

			byte[] byteArray = null;
			
			if(imgFoto == null){
				
				Util.MostrarAlertDialog(this, "No se pudo guardar la imagen");
				
			}else  {

				Bitmap bitmap = ((BitmapDrawable) imgFoto).getBitmap();
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
				byteArray = stream.toByteArray();
				DataBaseBO.ValidarUsuario();
				double latitud = DataBaseBO.verLatitud(Main.cliente.codigo);
				double longitud = DataBaseBO.verLongitud(Main.cliente.codigo);
				if(DataBaseBOJ.guardarImagenDeposito(docDeposito, byteArray,latitud,longitud)){
					Util.MostrarAlertDialog(this, "Deposito registrado con exito.");
					buscarSaldos();
					
					mostrarDialogImprimirFactura();
				}
			}
		}
	}
	
	public void onClickDepositar(View view){
		
		if(!validarDepositosCheckeados()){
			
			Util.MostrarAlertDialog(this, getString(R.string.msj_error_falta_saldo));
			return;
		}
		
		mostrarDialogDeposito();
	}

	protected void mostrarDialogDeposito() {	

		//verificar que el dialog sea nulo, se iniciaria por primera vez
		if (dialogDeposito == null) {
			dialogDeposito = new Dialog(this);
			dialogDeposito.setContentView(R.layout.dialog_deposito);
			dialogDeposito.setTitle("Deposito");

			editTextCompBancarioDep = ((EditText) dialogDeposito.findViewById(R.id.editTextCompBancarioDep));
			editTextCantidadDep  = ((EditText) dialogDeposito.findViewById(R.id.editTextCantidadDep));
			editTextObservacionesDep = ((EditText) dialogDeposito.findViewById(R.id.editTextObservacionesDep));
			spnBancosDeposito = ((Spinner) dialogDeposito.findViewById(R.id.spnBancosDeposito));

			//mostrar el dialog en pantalla
			dialogDeposito.setCancelable(true);
			dialogDeposito.show();

		}
		else {
			//si no es nulo entonces limpiar el texto y mostrar de nuevo.
			editTextCompBancarioDep.setText("");
			editTextCantidadDep.setText("");
			editTextObservacionesDep.setText("");
			spnBancosDeposito.setSelection(0);
			dialogDeposito.show();			
		}
		
		llenarBancos();


		//captura evento del boton de aceptar el deposito
		((Button)dialogDeposito.findViewById(R.id.buttonAceptarDialogDep)).setOnClickListener(new OnClickListener() {



			@Override
			public void onClick(View v) {
				
				int index = spnBancosDeposito.getSelectedItemPosition();
				
				if(index == Spinner.INVALID_POSITION || listaBancos.get(index).cod_banco == Spinner.INVALID_POSITION){
					
					Util.MostrarAlertDialog(FormDepositoActivity.this, "Debe seleccionar un banco.");
					return;
				}
				
				String compBanc = editTextCompBancarioDep.getText().toString();
				String cant_cheques = editTextCantidadDep.getText().toString();
				String observacion = editTextObservacionesDep.getText().toString();
				int cod_banco = listaBancos.get(index).cod_banco;
				
			if (compBanc.equals("")) {
				
				Util.MostrarAlertDialog(FormDepositoActivity.this, "Debe ingresar el numero de comprobante bancario.");
				return;
			}	
			
				deposito = new Deposito();
				
				DataBaseBO.ValidarUsuario();
				docDeposito = "A" + Main.usuario.codigoVendedor + Util.ObtenerFechaId();
				
				deposito.documento = docDeposito;
				deposito.cod_usuario = Main.usuario.codigoVendedor;
				deposito.observacion = observacion;
				deposito.compr_bancario = compBanc;
				deposito.cod_est_deposito = Const.DEPOSITO_PEND_REVISION;
				deposito.cod_banco = cod_banco;
				deposito.cant_cheques = cant_cheques;
				deposito.desc_banco = listaBancos.get(index).descripcion;
				
				detallesDeposito = getItemsChecked(deposito.documento);
				
				
				if(!DataBaseBOJ.guardarDeposito(deposito, detallesDeposito)){
			
					Util.MostrarAlertDialog(FormDepositoActivity.this, "No se pudo registrar el Deposito.. Error!");
					
				}else{
					
					AlertDialog.Builder builder = new AlertDialog.Builder(FormDepositoActivity.this);
					builder.setMessage("Desea capturar una imagen del deposito?")
					.setCancelable(false)
					.setPositiveButton("Si", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int id) {
							
							Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							String filePath = Util.DirApp().getPath() + "/foto.jpg";
							
							/*Uri output = Uri.fromFile(new File(filePath));
							intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
							startActivityForResult(intent, Const.RESP_TOMAR_FOTO);*/

							Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
								startActivityForResult(takePictureIntent, Const.RESP_TOMAR_FOTO);
							}
							
							dialog.cancel();
							dialogDeposito.dismiss();
							
							
						}
					})
					.setNegativeButton("No", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int id) {
							
							Toast.makeText(FormDepositoActivity.this, "Deposito registrado con exito.", Toast.LENGTH_SHORT).show();
							buscarSaldos();
							
							mostrarDialogImprimirFactura();
							
							dialog.cancel();
							dialogDeposito.dismiss();
						}
					});

					AlertDialog alert = builder.create();
					alert.show();
					
				}
			}
		});

		//captura evento del boton de cancelar para salir del dialog.
		((Button)dialogDeposito.findViewById(R.id.buttonCancelarDialogDep)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialogDeposito.dismiss();
			}
		});
	}
	
	private void llenarBancos() {

		ArrayAdapter<String> adapter;
		Vector<String> listaItems = new Vector<String>();
		listaBancos = DataBaseBO.listaBancos(listaItems);

		if (listaItems.size() > 0) {

			String[] items = new String[listaItems.size()];
			listaItems.copyInto(items);
			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);

		} else {

			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[] {});
		}

		Spinner spinner = (Spinner) dialogDeposito.findViewById(R.id.spnBancosDeposito);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
		
		int posSeleccionar = 0;
		
		if(!codSaldo.equals("")) {
			
			for(int i=0; i<listaBancos.size(); i++) {
				
				if(listaBancos.elementAt(i).descripcion.toUpperCase().contains("EFECTIVO")) {
				
					posSeleccionar = i;
					break;
					
				}
				
			}
			
			
		}
		
		spinner.setSelection(posSeleccionar);
		
		
		
	}

	/**
	 * 
	 * metodo encargado de validar si almenos hay un saldo checkeado
	 * @param docDeposito
	 * @return
	 */
	public boolean validarDepositosCheckeados(){
		
		ListView listaBusquedaSaldos = (ListView)findViewById(R.id.listaBusquedaSaldos);
		ItemListView[] items = ((ListViewAdapterCheckBox)listaBusquedaSaldos.getAdapter()).getList();
		
		
		for (ItemListView item : items) {
			
			if(item.isChecked){
				
				return true;
				
			}
		}
		return false;
	}
	
	public ArrayList<DetalleDeposito> getItemsChecked(String docDeposito){
		
		ArrayList<DetalleDeposito> detalles = new ArrayList<DetalleDeposito>();
		
		ListView listaBusquedaSaldos = (ListView)findViewById(R.id.listaBusquedaSaldos);
		
		ItemListView[] items = ((ListViewAdapterCheckBox)listaBusquedaSaldos.getAdapter()).getList();
		
		
		for (ItemListView item : items) {
			
			if(item.isChecked){
				
				DetalleDeposito detalle = new DetalleDeposito();
				detalle.cod_saldo_canc = item.codSaldoCan ;
				detalle.cod_est_deposito = Const.DEPOSITO_PEND_REVISION;
				detalle.documento = docDeposito;
				detalle.saldo = item.saldo;
				detalle.cod_cliente = item.cod_cliente;
				detalle.razon_social_cliente = item.razon_social;
				detalle.factura = item.factura;
				detalle.saldoString = item.saldo + "";
				
				detalles.add(detalle);
			}
		}
		
		return detalles;
	}

	public void OcultarTeclado(EditText editText) {

		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}
	
	public Drawable resizedImage(int newWidth, int newHeight) {
        
        Matrix matrix;
        FileInputStream fd = null;
        Bitmap resizedBitmap = null;
        Bitmap bitmapOriginal = null;
       
        try {
              
            File fileImg = new File(Util.DirApp(), "foto.jpg");
              
            if (fileImg.exists()) {
                    
               fd = new FileInputStream(fileImg.getPath());
               BitmapFactory.Options option = new BitmapFactory.Options();
               option.inDither = false;
               option.inPurgeable = true;
               option.inInputShareable = true;
               option.inTempStorage = new byte[32 * 1024];
               option.inPreferredConfig = Bitmap.Config.RGB_565;
                    
               bitmapOriginal = BitmapFactory.decodeFileDescriptor(fd.getFD(), null, option);
                    
               int width = bitmapOriginal.getWidth();
               int height = bitmapOriginal.getHeight();
             
              // Reescala el Ancho y el Alto de la Imagen
              float scaleWidth = ((float) newWidth) / width;
              float scaleHeight = ((float) newHeight) / height;

              matrix = new Matrix();
              matrix.postScale(scaleWidth, scaleHeight);
             
              // Crea la Imagen con el nuevo Tamano
              resizedBitmap = Bitmap.createBitmap(bitmapOriginal, 0, 0, width, height, matrix, true);
                     BitmapDrawable bmd = new BitmapDrawable(resizedBitmap);      
                    
                     bitmapOriginal.recycle();
                     bitmapOriginal = null;
                     return bmd.mutate();
               }
              
               return null;

        } catch(Exception e) {
              
               Log.e("", "resizedImage -> " + e.getMessage(), e);
               return null;
              
        } finally {
              
               if (fd != null) {
                    
                     try {
                           
                            fd.close();
                           
                     } catch (IOException e) { }
               }
              
               fd = null;
               matrix = null;
               resizedBitmap = null;
               bitmapOriginal = null;
               System.gc();
        }
  }
	
	
	private void mostrarDialogImprimirFactura(){

		dialogImprimirFacturaDeposito = new Dialog(FormDepositoActivity.this);
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

				progressDialog = ProgressDialog.show(FormDepositoActivity.this, "", "Por Favor Espere...\n\nProcesando Informacion!", true);
				progressDialog.show();

				SharedPreferences settings = getSharedPreferences(Const.CONFIG_IMPRESORA, MODE_PRIVATE);
				String macImpresora = settings.getString(Const.MAC_IMPRESORA, "-");

				if (macImpresora.equals("-")) {
					
					

					if( progressDialog != null ){

						progressDialog.dismiss();
					}
					

					MostrarAlertDialog(FormDepositoActivity.this, "Aun no hay Impresora Establecida.\n\nPor Favor primero Configure la Impresora!");

				} else {
					Usuario usuario = DataBaseBO.CargarUsuario();
					imprimir_WSP_R240(macImpresora, usuario, numCopias);
					//		    imprimirTirilla( macImpresora, encabezado.numero_doc, numCopias );
				}
			}
		});

		((Button)dialogImprimirFacturaDeposito.findViewById(R.id.btnCancelarImprimirFactura)).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				buscarSaldos();

				dialogImprimirFacturaDeposito.cancel();
			}
		});

		dialogImprimirFacturaDeposito.setCancelable(false);
		dialogImprimirFacturaDeposito.show();
	}
	
	public void MostrarAlertDialog(Context context, String mensaje) {
		
		AlertDialog alertDialog;
		
		ContextThemeWrapper ctw = new ContextThemeWrapper(context, R.style.Theme_Dialog_Translucent);
		AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
		builder.setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
		    
		    public void onClick(DialogInterface dialog, int id) {
		    	
		    	buscarSaldos();
			
			dialog.cancel();
		    }
		});
		
		alertDialog = builder.create();
		alertDialog.setMessage(mensaje);
		alertDialog.show();
	    }
	
	
	/**
	 * Imprimir en la impresora WR-240, debidamente establecida con anterioridad.
	 * @param mac
	 * @param numeroDoc
	 * @param usuario 
	 * @param numCopias 
	 * @param cliente2 
	 */
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
							
							Toast.makeText(FormDepositoActivity.this,
									"Aun no hay Impresora Predeterminada.\n\nPor Favor primero Configure la Impresora!", Toast.LENGTH_SHORT)
							.show();
						}
					});
				}
				else {

					if (wR240 == null) {
						wR240 = new WoosimR240(FormDepositoActivity.this);
					}
					int conect = wR240.conectarImpresora(macImpresora);

					switch (conect) {
					case 1:
						ControlImpresion control = new ControlImpresion();
						/*lista que contendra los detalles que seran impresos*/
//						DetalleImprimir detalleImprimir = new DetalleImprimir();

						if (deposito != null) {

							for (int i = 0; i < numCopias; i++) {
								if(i > 0){
									control.original = false;
								}
								wR240.generarEncabezadoTirillaDeposito(deposito, true);
								wR240.generarDetalleTirillaDeposito(detallesDeposito);
								int succes = wR240.imprimirBuffer(true);
								if( (control.original) && succes == 1 ){
//									DataBaseBO.marcarComoCopiaProximaImpresion(numeroDoc, usuario);
								}
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
									
									Toast.makeText(FormDepositoActivity.this,
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
							
								Toast.makeText(FormDepositoActivity.this, "-2 fallo conexion", Toast.LENGTH_SHORT).show();
							}
						});
						break;

					case -8:
						progressDialog.dismiss();
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								
								MostrarAlertDialog(FormDepositoActivity.this,
										"Bluetooth apagado. Por favor habilite el bluetoth para imprimir.");
							}
						});
						break;

					default:
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								
								Toast.makeText(FormDepositoActivity.this, "error desconocido", Toast.LENGTH_SHORT).show();
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


			AlertDialog.Builder builder = new AlertDialog.Builder(FormDepositoActivity.this);
			builder.setMessage("Solicitud Almacenada Exitosamente")
			.setCancelable(false)
			.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int id) {

//					progressDialog = ProgressDialog.show(FormPedidosAutoventa.this, "","Enviando Informacion...", true);
//					progressDialog.show();
//					progressDialog.setIndeterminate(true);
//					progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_icon_drawable_animation));
//
//
//					Sync sync = new Sync(FormPedidosAutoventa.this, Const.ENVIAR_PEDIDO);
//					sync.start();
					
					buscarSaldos();
					
					dialog.cancel();					
				}
			});

			AlertDialog alert = builder.create();
			alert.show();
		}
	};
	
	
	
	public void onClickSalirDeposito(View view) {
		
		AlertDialog alertDialog;
		
		ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.Theme_Dialog_Translucent);
		AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
		builder.setCancelable(false)
		
		.setPositiveButton("Si", new DialogInterface.OnClickListener() {
		    
		    public void onClick(DialogInterface dialog, int id) {
			
			dialog.cancel();
			finish();
		    }
		})
		
		
       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		    
		    public void onClick(DialogInterface dialog, int id) {
			
			dialog.cancel();
		    }
		});
		
		alertDialog = builder.create();
		alertDialog.setMessage("Desea Salir");
		alertDialog.show();
		
	}
	


}
