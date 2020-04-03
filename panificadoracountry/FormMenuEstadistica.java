package co.com.panificadoracountry;

import java.io.File;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import co.com.BusinessObject.ConfigBO;
import co.com.BusinessObject.DataBaseBO;
import co.com.Conexion.Sync;
import co.com.DataObject.BotonesMenu;
import co.com.DataObject.Config;

public class FormMenuEstadistica extends Activity implements Sincronizador, OnClickListener{
	Dialog dialogEnviarInfo;
	Dialog dialogIniciarDia;
	Dialog dialogTerminarDia;
	Dialog dialogConfiguracion;
	Dialog dialogActualizarInventario;
	ProgressDialog progressDialog;
	Vector<BotonesMenu> vBtnMenu = new Vector<BotonesMenu>();
	private int tipoAplicacionAux = 0;
	Vector<BotonesMenu> listadoOpciones;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_menu_estadistica);
		DataBaseBO.setAppAutoventa();
		mostrarTitulo();
		crearBotonesMenu();
	}

	public void onResume(){
		super.onResume();
		if (Main.usuario == null || Main.usuario.codigoVendedor == null)
			DataBaseBO.CargarInfomacionUsuario();
	}

	public void crearBotonesMenu(){
		int numeroColumnas = DataBaseBO.ObtenerNumeroColumnas();
		listadoOpciones = DataBaseBO.CargarOpciones(1);
		int cantidadFilas = (listadoOpciones.size() / numeroColumnas);
		cantidadFilas++;
		int posBotonActual = 0;
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.lyMenu);
		for (int i = 0; i < cantidadFilas; i++){
			if (posBotonActual >= listadoOpciones.size())
				break;
			LinearLayout inflatedView;
			LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (numeroColumnas == 2)
				inflatedView = (LinearLayout) inflater.inflate(R.layout.layout_menu2, null);
			else
				inflatedView = (LinearLayout) inflater.inflate(R.layout.layout_menu3, null);
			for (int j = 0; j < numeroColumnas; j++){
				if (posBotonActual >= listadoOpciones.size())
					break;
				BotonesMenu botonMenu = listadoOpciones.elementAt(posBotonActual);
				int idImgTop;
				switch (botonMenu.id){
				case Const.MOD_EFECTIVIDAD:
					idImgTop = R.drawable.op_efectividad;
					break;
				case Const.MOD_PEDIDOS_REALIZADOS:
					idImgTop = R.drawable.pedidos_realizados;
					break;
				case Const.MOD_INFORME_DE_VENTAS:
					idImgTop = R.drawable.informe_ventas;
					break;
				case Const.MOD_INVENTARIO:
					idImgTop = R.drawable.op_informe_inv;
					break;
				case Const.MOD_KARDEX:
					idImgTop = R.drawable.kardes;
					break;
				case Const.MOD_IMPRESORA:
					idImgTop = R.drawable.op_printer;
					break;
				case Const.MOD_ESTADISTICAS_SALIR:
					idImgTop = R.drawable.op_regresar;
					break;
				case Const.MOD_CARGUE_INVENTARIO:
					idImgTop = R.drawable.op_informe_inv;
					break;
				case Const.MOD_CARGAR_INVENTARIO_SUGERIDO:
					idImgTop = R.drawable.cargar_inventario_n;
					break;
				case Const.MOD_DEPOSITOS_REALIZADOS:
					idImgTop = R.drawable.op_deposito;
					break;
				case Const.MOD_RACAUDOS_REALIZADOS:
					idImgTop = R.drawable.op_recaudo;
					break;
				case Const.MOD_CAMBIOS_DEVOLUCIONES:
					idImgTop = R.drawable.cambiosxprod;
					break;
				default:
					continue;
				}
				LinearLayout lyAux = (LinearLayout) inflatedView.getChildAt(j);
				Button btn = (Button) lyAux.getChildAt(0);
				btn.setText(botonMenu.texto);
				btn.setTag("" + botonMenu.id);
				btn.setOnClickListener(this);
				if (idImgTop > 0){
					btn.setCompoundDrawablesWithIntrinsicBounds(0, idImgTop, 0, 0);
				}
				posBotonActual++;
			}
			linearLayout.addView(inflatedView);
		}
	}

	OnClickListener clicksBtnMenu = new OnClickListener(){
		@Override
		public void onClick(View v){
		}
	};

	public void opcionesRemisiones(View view){
	}

	private void createActionIconsRemisiones(){
	}

	public void OnClickEnviarInfo(View view){
		enviarInformacion();
	}

	public void enviarInformacion(){
		if (DataBaseBO.HayInformacionXEnviar()){
			if (dialogEnviarInfo == null){
				dialogEnviarInfo = new Dialog(this);
				dialogEnviarInfo.requestWindowFeature(Window.FEATURE_LEFT_ICON);
				dialogEnviarInfo.setContentView(R.layout.dialog_enviar_info);
				dialogEnviarInfo.setTitle("Enviar Informacion");
			}
			((Button) dialogEnviarInfo.findViewById(R.id.btnAceptarEnviarInfo)).setOnClickListener(new OnClickListener(){
				public void onClick(View v){
					dialogEnviarInfo.cancel();
					progressDialog = ProgressDialog.show(FormMenuEstadistica.this, "", "Enviando Informacion...", true);
					progressDialog.show();
					// DataBaseBO.ActualizarSnPedidosTmp();
					Sync sync = new Sync(FormMenuEstadistica.this, Const.ENVIAR_PEDIDO);
					sync.start();
				}
			});
			((Button) dialogEnviarInfo.findViewById(R.id.btnCancelarEnviarInfo)).setOnClickListener(new OnClickListener(){
				public void onClick(View v){
					dialogEnviarInfo.cancel();
				}
			});
			dialogEnviarInfo.setCancelable(false);
			dialogEnviarInfo.show();
			dialogEnviarInfo.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.op_enviar_info);
		}else{
			Util.MostrarAlertDialog(this, "No hay informacion pendiente por Enviar");
		}
	}

	public void OnClickEfectividad(View view){
		// Intent intent = new Intent(this, FormEstadisticasActivity.class);
		// startActivity(intent);
	}

	public void OnClickActualizarInfo(View view){
		iniciarDia();
	}

	public void iniciarDia(){
		if (DataBaseBO.HayInformacionXEnviar()){
			Util.MostrarAlertDialog(this, "Hay informacion pendiente por Enviar. Antes de Iniciar Dia, por favor Envie Informacion");
		}else{
			Config config = ConfigBO.ObtenerConfigUsuario();
			MostrarDialogIniciarDia(config);
		}
	}

	public void OnClickActualizar(View view){
		if (DataBaseBO.HayInformacionXEnviar()){
			Util.MostrarAlertDialog(this, "Hay informacion pendiente por Enviar. Antes de Actualizar Informacion, por favor Envie Informacion");
		}else{
			AlertDialog alertDialog;
			AlertDialog.Builder builder = new AlertDialog.Builder(FormMenuEstadistica.this);
			builder.setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id){
					dialog.cancel();
					tipoAplicacionAux = Const.tipoAplicacion;
					// MostrarDialogIniciarDia(config);
					progressDialog = ProgressDialog.show(FormMenuEstadistica.this, "", "Descargando Informacion...", true);
					progressDialog.show();
					Sync sync = new Sync(FormMenuEstadistica.this, Const.DOWNLOAD_DATA_BASE);
					sync.version = ObtenerVersion();
					sync.imei = ObtenerImei();
					sync.start();
				}
			}).setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id){
					dialog.cancel();
				}
			});
			alertDialog = builder.create();
			alertDialog.setMessage("Esta Seguro de Actualizar Informacion?");
			alertDialog.show();
		}
	}

	public void OnClickActInv(View view){
		if (DataBaseBO.HayInformacionXEnviar()){
			Util.MostrarAlertDialog(this, "Hay informacion pendiente por Enviar. Antes de Actualizar Inventario, por favor Envie Informacion");
		}else{
			tipoAplicacionAux = Const.tipoAplicacion;
			// MostrarDialogIniciarDia(config);
			progressDialog = ProgressDialog.show(FormMenuEstadistica.this, "", "Descargando Informacion...", true);
			progressDialog.show();
			Sync sync = new Sync(FormMenuEstadistica.this, Const.DOWNLOAD_DATA_BASE);
			sync.version = ObtenerVersion();
			sync.imei = ObtenerImei();
			sync.start();
		}
	}

	public void OnClickTerminarDia(View view){
		terminarDia();
	}

	public void terminarDia(){
		if (dialogTerminarDia == null){
			dialogTerminarDia = new Dialog(this);
			dialogTerminarDia.requestWindowFeature(Window.FEATURE_LEFT_ICON);
			dialogTerminarDia.setContentView(R.layout.dialog_terminar_labores);
			dialogTerminarDia.setTitle("Terminar Labores");
		}
		String mensaje = "<b>Esta seguro de Terminar Labores?</b><br /><br />";
		mensaje += "Una vez Finalice labores no podra Registrar mas pedidos.";
		((TextView) dialogTerminarDia.findViewById(R.id.lblMsgTerminarDia)).setText(Html.fromHtml(mensaje));
		((Button) dialogTerminarDia.findViewById(R.id.btnAceptarTerminarDia)).setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				if (DataBaseBO.HayInformacionXEnviar()){
					dialogTerminarDia.cancel();
					progressDialog = ProgressDialog.show(FormMenuEstadistica.this, "", "Finalizando Labores...", true);
					progressDialog.show();
					Sync sync = new Sync(FormMenuEstadistica.this, Const.ENVIAR_PEDIDO_TERMINAR);
					sync.start();
				}else{
					dialogTerminarDia.cancel();
					progressDialog = ProgressDialog.show(FormMenuEstadistica.this, "", "Finalizando Labores...", true);
					progressDialog.show();
					Sync sync = new Sync(FormMenuEstadistica.this, Const.TERMINAR_LABORES);
					sync.imei = ObtenerImei();
					sync.start();
				}
			}
		});
		((Button) dialogTerminarDia.findViewById(R.id.btnCancelarTerminarDia)).setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				dialogTerminarDia.cancel();
			}
		});
		dialogTerminarDia.setCancelable(false);
		dialogTerminarDia.show();
		dialogTerminarDia.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.op_terminar_dia);
	}

	public void OnClickInventario(View view){
		if (DataBaseBO.HayInformacionXEnviar()){
			Util.MostrarAlertDialog(this, "Hay informacion pendiente por Enviar. Antes de Actualizar Inventario, por favor Envie Informacion");
		}else{}
	}

	public void OnClickInformeInventario(View view){
		// Intent intent = new Intent(this, ListaProductos.class);
		// startActivity(intent);
	}

	public void OnClickFiados(View view){
		// Intent intent = new Intent(this, FormFiados.class);
		// startActivity(intent);
	}

	public void OnClickCuadreCaja(View view){
		// Intent intent = new Intent(this, FormCuadreCaja.class);
		// startActivity(intent);
	}

	public void OnClickInfoVenta(View view){
		// Intent intent = new Intent(this, FormInformeVentas.class);
		// startActivity(intent);
	}

	public void OnClickSalir(View view){
		setResult(RESULT_OK);
		finish();
	}

	public void MostrarDialogIniciarDia(Config config){
		if (dialogIniciarDia == null){
			dialogIniciarDia = new Dialog(this);
			dialogIniciarDia.requestWindowFeature(Window.FEATURE_LEFT_ICON);
			dialogIniciarDia.setContentView(R.layout.dialog_iniciar_dia);
			dialogIniciarDia.setTitle("Iniciar Dia");
		}
		String mensaje = "<b>Usuario:</b> " + config.usuario + "<br />";
		// mensaje += "<b>Bodega:</b> " + config.bodega + "<br /><br />";
		mensaje += " " + "<br /><br />";
		mensaje += "Esta seguro de Iniciar Dia?<br /><br />";
		mensaje += "Iniciar Dia actualizara la informacion. Toda la informacion anterior se borrara.";
		((TextView) dialogIniciarDia.findViewById(R.id.lblMsgIniciarDia)).setText(Html.fromHtml(mensaje));
		((Button) dialogIniciarDia.findViewById(R.id.btnAceptarIniciarDia)).setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				tipoAplicacionAux = Const.tipoAplicacion;
				dialogIniciarDia.cancel();
				progressDialog = ProgressDialog.show(FormMenuEstadistica.this, "", "Descargando Informacion...", true);
				progressDialog.show();
				Sync sync = new Sync(FormMenuEstadistica.this, Const.DOWNLOAD_DATA_BASE);
				sync.version = ObtenerVersion();
				sync.imei = ObtenerImei();
				sync.start();
			}
		});
		((Button) dialogIniciarDia.findViewById(R.id.btnCancelarIniciarDia)).setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				dialogIniciarDia.cancel();
			}
		});
		dialogIniciarDia.setCancelable(false);
		dialogIniciarDia.show();
		dialogIniciarDia.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.op_actualizar);
	}

	public void MostrarDialogActualizarInventario(Config config){
		if (dialogActualizarInventario == null){
			dialogActualizarInventario = new Dialog(this);
			dialogActualizarInventario.requestWindowFeature(Window.FEATURE_LEFT_ICON);
			dialogActualizarInventario.setContentView(R.layout.dialog_iniciar_dia);
			dialogActualizarInventario.setTitle("Actualizar Inventario");
		}
		String mensaje = "<b>Usuario:</b> " + config.usuario + "<br />";
		// mensaje += "<b>Bodega:</b> " + config.bodega + "<br /><br />";
		mensaje += "" + "<br /><br />";
		mensaje += "Esta seguro de Actualizar el Inventario?<br />";
		((TextView) dialogActualizarInventario.findViewById(R.id.lblMsgIniciarDia)).setText(Html.fromHtml(mensaje));
		((Button) dialogActualizarInventario.findViewById(R.id.btnAceptarIniciarDia)).setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				tipoAplicacionAux = Const.tipoAplicacion;
				dialogActualizarInventario.cancel();
				progressDialog = ProgressDialog.show(FormMenuEstadistica.this, "", "Descargando Inventario...", true);
				progressDialog.show();
				Sync sync = new Sync(FormMenuEstadistica.this, Const.DOWNLOAD_DATA_BASE);
				sync.version = ObtenerVersion();
				sync.imei = ObtenerImei();
				sync.start();
			}
		});
		((Button) dialogActualizarInventario.findViewById(R.id.btnCancelarIniciarDia)).setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				dialogActualizarInventario.cancel();
			}
		});
		dialogActualizarInventario.setCancelable(false);
		dialogActualizarInventario.show();
		dialogActualizarInventario.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.inventario);
	}

	public void ActualizarVersionApp_(){
		final String versionSvr = DataBaseBO.ObtenerVersionApp();
		String versionApp = ObtenerVersion();
		if (versionSvr != null && versionApp != null){
			float versionServer = Util.ToFloat(versionSvr.replace(".", ""));
			float versionLocal = Util.ToFloat(versionApp.replace(".", ""));
			if (versionLocal < versionServer){
				AlertDialog.Builder builder = new AlertDialog.Builder(FormMenuEstadistica.this);
				builder.setMessage("Hay una version de la aplicacion: " + versionSvr).setCancelable(false).setPositiveButton("Actualizar", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int id){
						progressDialog = ProgressDialog.show(FormMenuEstadistica.this, "", "Descargando Version " + versionSvr + "...", true);
						progressDialog.show();
						Sync sync = new Sync(FormMenuEstadistica.this, Const.DOWNLOAD_VERSION_APP);
						sync.start();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}else{
				Util.MostrarAlertDialog(this, "Informacion Descargada Correctamente");
			}
		}
	}

	public String ObtenerVersion(){
		String versionApp;
		try{
			versionApp = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		}
		catch (NameNotFoundException e){
			versionApp = "0.0";
			Log.e("FormMenuEstadistica", "ObtenerVersion: " + e.getMessage(), e);
		}
		return versionApp;
	}

	public String ObtenerImei(){
		TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		Main.deviceId = manager.getDeviceId();
		if (Main.deviceId == null){
			WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
			if (wifiManager != null){
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				if (wifiInfo != null){
					String mac = wifiInfo.getMacAddress();
					if (mac != null){
						Main.deviceId = mac.replace(":", "").toUpperCase();
					}
				}
			}
		}
		return Main.deviceId;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if (keyCode == KeyEvent.KEYCODE_BACK){
			setResult(RESULT_OK);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void RespSync(boolean ok, String respuestaServer, String msg, int codeRequest){
		switch (codeRequest){
		case Const.DOWNLOAD_DATA_BASE:
			RespuestaDownloadInfo(ok, respuestaServer, msg);
			break;
		case Const.ENVIAR_PEDIDO:
			RespuestaEnviarInfo(ok, respuestaServer, msg);
			break;
		case Const.DOWNLOAD_VERSION_APP:
			RespuestaDownloadVersionApp(ok, respuestaServer, msg);
			break;
		case Const.ENVIAR_PEDIDO_TERMINAR:
			RespuestaEnviarInfoTerminarDia(ok, respuestaServer, msg);
			break;
		case Const.TERMINAR_LABORES:
			RespuestaTerminarLabores(ok, respuestaServer, msg);
			break;
		}
	}

	public void RespuestaDownloadInfo(final boolean ok, final String respuestaServer, String msg){
		final String mensaje = ok ? "Informacion Actualizada correctamente" : msg;
		if (progressDialog != null)
			progressDialog.cancel();
		this.runOnUiThread(new Runnable(){
			public void run(){
				if (DataBaseBO.CargarInfomacionUsuario()){
					DataBaseBO.setAppAutoventa();
					if (tipoAplicacionAux == Const.tipoAplicacion){
						if (ok){
							ActualizarVersionApp();
						}else{
							Util.MostrarAlertDialog(FormMenuEstadistica.this, mensaje);
						}
					}else{
						tipoAplicacionAux = Const.tipoAplicacion;
						progressDialog = ProgressDialog.show(FormMenuEstadistica.this, "", "Descargando Informacion...", true);
						progressDialog.show();
						Sync sync = new Sync(FormMenuEstadistica.this, Const.DOWNLOAD_DATA_BASE);
						sync.version = ObtenerVersion();
						sync.imei = ObtenerImei();
						sync.start();
					}
				}else{
					Util.MostrarAlertDialog(FormMenuEstadistica.this, "No se pudo Iniciar Dia.\n\n" + respuestaServer);
				}
			}
		});
	}

	public void RespuestaEnviarInfo(boolean ok, String respuestaServer, String msg){
		final String mensaje = ok ? "Informacion Registrada con Exito en el servidor" : msg;
		if (progressDialog != null)
			progressDialog.cancel();
		this.runOnUiThread(new Runnable(){
			public void run(){
				Util.MostrarAlertDialog(FormMenuEstadistica.this, mensaje);
			}
		});
	}

	public void RespuestaEnviarInfoTerminarDia(boolean ok, String respuestaServer, String msg){
		this.runOnUiThread(new Runnable(){
			public void run(){
				Sync sync = new Sync(FormMenuEstadistica.this, Const.TERMINAR_LABORES);
				sync.start();
			}
		});
	}

	public void RespuestaTerminarLabores(boolean ok, String respuestaServer, String msg){
		final String mensaje = ok ? "Terminar Labores Satisfactorio" : msg + "\n\nPor Favor intente Nuevamente";
		if (progressDialog != null)
			progressDialog.cancel();
		this.runOnUiThread(new Runnable(){
			public void run(){
				Util.MostrarAlertDialog(FormMenuEstadistica.this, mensaje);
			}
		});
	}

	public void RespuestaDownloadVersionApp(final boolean ok, final String respuestaServer, String msg){
		if (progressDialog != null)
			progressDialog.cancel();
		this.runOnUiThread(new Runnable(){
			public void run(){
				if (ok){
					File fileApp = new File(Util.DirApp(), Const.fileNameApk);
					if (fileApp.exists()){
						Uri uri = Uri.fromFile(fileApp);
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setDataAndType(uri, "application/vnd.android.package-archive");
						startActivityForResult(intent, Const.RESP_ACTUALIZAR_VERSION);
					}else{
						Util.MostrarAlertDialog(FormMenuEstadistica.this, "No se pudo actualizar la version.");
					}
				}else{
					Util.MostrarAlertDialog(FormMenuEstadistica.this, respuestaServer);
				}
			}
		});
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
	}

	public void ActualizarVersionApp(){
		final String versionSvr = DataBaseBO.ObtenerVersionApp();
		String versionApp = ObtenerVersion();
		if (versionSvr != null && versionApp != null){
			float versionServer = Util.ToFloat(Util.rpad(versionSvr.replace(".", ""), 4, "0"));
			float versionLocal = Util.ToFloat(Util.rpad(versionApp.replace(".", ""), 4, "0"));
			if (versionLocal < versionServer){
				AlertDialog.Builder builder = new AlertDialog.Builder(FormMenuEstadistica.this);
				builder.setMessage("Hay una version de la aplicacion: " + versionSvr).setCancelable(false).setPositiveButton("Actualizar", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int id){
						progressDialog = ProgressDialog.show(FormMenuEstadistica.this, "", "Descargando Version " + versionSvr + "...", true);
						progressDialog.show();
						Sync sync = new Sync(FormMenuEstadistica.this, Const.DOWNLOAD_VERSION_APP);
						sync.start();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}else{
				Util.MostrarAlertDialog(FormMenuEstadistica.this, "Informacion Actualizada Correctamente");
			}
		}else{
			Util.MostrarAlertDialog(FormMenuEstadistica.this, "Informacion Actualizada Correctamente");
		}
	}

	@Override
	public void onClick(View view){
		if (view instanceof Button){
			String strTag = view.getTag().toString();
			int position = Util.ToInt(strTag);
			int size = listadoOpciones.size();
			switch (position){
			case Const.MOD_EFECTIVIDAD:
				efectividad();
				break;
			case Const.MOD_PEDIDOS_REALIZADOS:
				pedidosInformacion();
				break;
			case Const.MOD_INFORME_DE_VENTAS:
				informeVentas();
				break;
			case Const.MOD_INVENTARIO:
				inventario();
				break;
			case Const.MOD_KARDEX:
				kardex();
				break;
			case Const.MOD_IMPRESORA:
				impresora();
				break;
			case Const.MOD_ESTADISTICAS_SALIR:
				finish();
				break;
			case Const.MOD_CARGUE_INVENTARIO:
				cargueInventario();
			case Const.MOD_CARGAR_INVENTARIO_SUGERIDO:
				cargueInventarioSugerido();
				break;
			case Const.MOD_DEPOSITOS_REALIZADOS:
				depositoInformacion();
				break;
			case Const.MOD_RACAUDOS_REALIZADOS:
				recaudosInformacion();
				break;
			case Const.MOD_CAMBIOS_DEVOLUCIONES:
				devolucionesRealizadas();
				break;
			}
		}
	}

	/**
	 * Iniciar activity para cargue de inventario sugerido, el cual es elegido
	 * por el vendedor.
	 */
	private void cargueInventarioSugerido(){
		Intent intent = new Intent(this, FormCargueInventarioSugerido.class);
		startActivity(intent);
	}

	private void cargueInventario(){
		Intent intent = new Intent(this, FormDescargueInventario.class);
		startActivity(intent);
	}

	public void efectividad(){
		Intent intent = new Intent(FormMenuEstadistica.this, FormEstadisticasActivity.class);
		startActivity(intent);
	}

	public void pedidosInformacion(){
		Intent intent = new Intent(FormMenuEstadistica.this, FormPedidosInformacion.class);
		startActivity(intent);
	}

	public void depositoInformacion(){
		Intent intent = new Intent(FormMenuEstadistica.this, FormDepositoInformacion.class);
		startActivity(intent);
	}

	private void recaudosInformacion(){
		Intent intent = new Intent(FormMenuEstadistica.this, FormRecaudoInformacion.class);
		startActivity(intent);
	}

	public void impresora(){
		Intent intent = new Intent(this, FormConfigPrinter.class);
		startActivity(intent);
	}

	public void inventario(){
		Intent intent = new Intent(this, FormInformeInventario.class);
		startActivity(intent);
	}

	public void kardex(){
		Intent intent = new Intent(FormMenuEstadistica.this, FormKardex.class);
		startActivity(intent);
	}

	private void devolucionesRealizadas(){
		Intent intent = new Intent(FormMenuEstadistica.this, FormDevolucionesRealizadas.class);
		startActivity(intent);
	}

	public void informeVentas(){
		Intent intent = new Intent(this, FormInformeVentas.class);
		startActivity(intent);
	}

	public void mostrarTitulo(){
		Spanned s;
		if (Const.tipoAplicacion == Const.AUTOVENTA){
			setTitle("AUTOVENTA");
			s = Html.fromHtml("<font color=\"brown\">" + "<u>" + "AUTOVENTA" + "</u></font>" + "");
			((TextView) findViewById(R.id.lblTipoAplicacion)).setText(s);
		}else if (Const.tipoAplicacion == Const.PREVENTA){
			setTitle("PREVENTA");
			s = Html.fromHtml("<font color=\"brown\">" + "<u>" + "PREVENTA" + "</u></font>" + "");
			((TextView) findViewById(R.id.lblTipoAplicacion)).setText(s);
		}else{
			setTitle("CELUWEB");
			((TextView) findViewById(R.id.lblTipoAplicacion)).setText("");
		}
	}
}
