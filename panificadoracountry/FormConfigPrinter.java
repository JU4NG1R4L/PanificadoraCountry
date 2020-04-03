package co.com.panificadoracountry;


import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;



import co.com.panificadoracountry.R;
import co.com.BusinessObject.DataBaseBO;
import co.com.BusinessObject.PrinterBO;
import co.com.DataObject.DetalleImprimir;
import co.com.DataObject.DetalleProducto;
import co.com.DataObject.ItemListView;
import co.com.woosim.printer.WoosimR240;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class FormConfigPrinter extends Activity {
    
    private WoosimR240 wR240 = null;
    
    private static String mensaje;
    private static ProgressDialog progressDialog;
    private static Activity context;
    
    BluetoothAdapter bluetoothAdapter = null;
    
    public final static String CONFIG_IMPRESORA = "PRINTER";
    public final static String MAC_IMPRESORA = "MAC";
    public final static String LABEL_IMPRESORA = "LABEL";
    
    public static final String UUID_BPP = "00001101-0000-1000-8000-00805F9B34FB";
    
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	
	super.onCreate(savedInstanceState);
	setContentView(R.layout.form_config_printer);
	cargarOpciones();
	setListenerListView();
	//	bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	//	onBluetooth();
    }
    
    
    
    @Override
    protected void onResume() {
	
	super.onResume();
	settingsPrinter();
    }
    
    
    
    public void cargarOpciones() {
	
	ItemListView[] items = new ItemListView[3];
	
	items[0] = new ItemListView();
	items[0].titulo = "Definir Impresora";
	items[0].subTitulo = "Establece la Impresora Predeterminada";
	
	// items[0].icono = R.drawable.definir_impresora;
	
	items[1] = new ItemListView();
	items[1].titulo = "Acoplar Dispositivo";
	items[1].subTitulo = "Enlaza la Impresora al Dispositivo";
	// items[1].icono = R.drawable.acoplar_dispositivo;
	
	items[2] = new ItemListView();
	items[2].titulo = "Imprimir Prueba";
	items[2].subTitulo = "Imprime Tirilla de Prueba!";
	// items[2].icono = R.drawable.imprimir_prueba;
	
	ListViewAdapter adapter = new ListViewAdapter(this, items, R.drawable.op_printer, 0x2E65AD);
	ListView listaOpciones = (ListView) findViewById(R.id.listaOpciones);
	listaOpciones.setAdapter(adapter);
    }
    
    
    
    public void settingsPrinter() {
	
	TextView lblNombreImpresora = (TextView) findViewById(R.id.lblNombreImpresora);
	TextView lblMacImpresora = (TextView) findViewById(R.id.lblMacImpresora);
	
	SharedPreferences settings = getSharedPreferences(CONFIG_IMPRESORA, MODE_PRIVATE);
	String macImpresora = settings.getString(MAC_IMPRESORA, "-");
	String labelImpresora = settings.getString(LABEL_IMPRESORA, "-");
	
	lblNombreImpresora.setText(labelImpresora);
	lblMacImpresora.setText(macImpresora);
    }
    
    
    
    public void imprimirTirillaExtech(final String macPrinter) {
	
	new Thread(new Runnable() {
	    
	    public void run() {
		
		mensaje = "";
		BluetoothSocket socket = null;
		
		try {
		    
		    Looper.prepare();
		    
		    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		    
		    if (bluetoothAdapter == null) {
			
			mensaje = "No hubo conexion con la impresora.\n\nPor Favor intente de nuevo.";
			
		    }
		    else if (!bluetoothAdapter.isEnabled()) {
			
			mensaje = "No hubo conexion con la impresora.\n\nPor Favor intente de nuevo.";
			
		    }
		    else {
			
			BluetoothDevice printer = null;
			Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
			
			for (BluetoothDevice device : pairedDevices) {
			    
			    String macAddress = device.getAddress();
			    
			    if (macAddress.equals(macPrinter)) {
				
				printer = device;
				break;
			    }
			}
			
			if (printer == null) {
			    
			    mensaje = "No se pudo establecer la conexion con la Impresora.";
			    
			}
			else {
			    
			    int state = printer.getBondState();
			    
			    if (state == BluetoothDevice.BOND_BONDED) {
				
				UUID uuid = UUID.fromString(FormConfigPrinter.UUID_BPP);
				socket = printer.createRfcommSocketToServiceRecord(uuid);
				
				if (socket != null) {
				    
				    socket.connect();
				    
				    Thread.sleep(Const.timeWait);
				    
				    OutputStream stream = socket.getOutputStream();
				    String strPrint = PrinterBO.formatoPruebaExtech();
				    stream.write(strPrint.getBytes("UTF-8"));
				    
				    // Se asegura que los datos son enviados a
				    // la Impresora antes de cerrar la conexion.
				    Thread.sleep(500);
				    
				    handlerFinish.sendEmptyMessage(0);
				    
				}
				else {
				    
				    mensaje = "No se pudo abrir la conexion con la Impresora.\n\nPor Favor intente de nuevo.";
				}
				
			    }
			    else {
				
				mensaje = "La Impresora: " + macPrinter
					+ " Actualmente no esta Acoplada con el Dispositivo Movil.\n\nPor Favor configure primero la impresora.";
			    }
			}
		    }
		    
		    if (!mensaje.equals("")) {
			
			context = FormConfigPrinter.this;
			handlerMensaje.sendEmptyMessage(0);
		    }
		    
		    Looper.myLooper().quit();
		    
		} catch (Exception e) {
		    
		    String motivo = e.getMessage();
		    Log.e(TAG, "imprimirPruebaExtech -> " + motivo, e);
		    
		    mensaje = "No se pudo ejecutar la Impresion.";
		    
		    if (motivo != null) {
			mensaje += "\n\n" + motivo;
		    }
		    
		    context = FormConfigPrinter.this;
		    handlerMensaje.sendEmptyMessage(0);
		    
		}
		finally {
		    
		    try {
			
			if (socket != null) {
			    socket.close();
			}
			
		    } catch (Exception e) {
			
			Log.e(TAG, "imprimirPruebaExtech -> Cerrando socket", e);
		    }
		}
	    }
	    
	}).start();
    }
    
    
    
    private void imprimirPrueba(final String printerName) {
	
	new Thread(new Runnable() {
	    
	    public void run() {
		
		BluetoothSocket socket = null;
		
		try {
		    
		    Looper.prepare();
		    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		    
		    if (bluetoothAdapter == null) {
			return;
		    }
		    
		    if (!bluetoothAdapter.isEnabled()) {
			return;
		    }
		    
		    BluetoothDevice printer = null;
		    Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
		    
		    for (BluetoothDevice device : pairedDevices) {
			
			String s = device.getName();
			
			if (s.equals(printerName)) {
			    printer = device;
			    break;
			}
		    }
		    
		    if (printer == null) {
			return;
			
		    }
		    else {
			
			UUID uuid = UUID.fromString(UUID_BPP);
			socket = printer.createRfcommSocketToServiceRecord(uuid);
			
			if (socket != null) {
			    
			    socket.connect();
			    
			    OutputStream stream = socket.getOutputStream();
			    String strPrint = "";// PrinterBO.formatoPruebaExtech();
			    stream.write(strPrint.getBytes());
			    stream.flush();
			    stream.close();
			    
			}
			else {
			    
			    handlerError.sendEmptyMessage(0);
			}
		    }
		    
		    Thread.sleep(500);
		    
		    handlerFinish.sendEmptyMessage(0);
		    
		    Looper.myLooper().quit();
		    
		} catch (Exception e) {
		    
		    String motivo = e.getMessage();
		    mensaje = "No se pudo Imprimir.";
		    
		    if (motivo != null) {
			mensaje += "\n\nMotivo: " + motivo;
		    }
		    
		    handlerError.sendEmptyMessage(0);
		    
		}
		finally {
		    
		    try {
			
			if (socket != null)
			    socket.close();
			
		    } catch (Exception e) {
		    }
		}
	    }
	    
	}).start();
    }
    
    private Handler handlerFinish = new Handler() {
	
	@Override
	public void handleMessage(Message msg) {
	    
	    if (progressDialog != null)
		progressDialog.cancel();
	}
    };
    
    private Handler handlerError = new Handler() {
	
	@Override
	public void handleMessage(Message msg) {
	    
	    AlertDialog.Builder builder = new AlertDialog.Builder(FormConfigPrinter.this);
	    builder.setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
		
		public void onClick(DialogInterface dialog, int id) {
		    
		    dialog.cancel();
		}
	    });
	    
	    AlertDialog alertDialog = builder.create();
	    alertDialog.setMessage(mensaje);
	    
	    if (progressDialog != null)
		progressDialog.cancel();
	    
	    alertDialog.show();
	}
    };
    
    private ProgressDialog dialog;
    
    
    
    public void setListenerListView() {
	
	ListView listaOpciones = (ListView) findViewById(R.id.listaOpciones);
	listaOpciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
		if (position == 0) {
		    
		    Intent intent = new Intent(FormConfigPrinter.this, FormPrinters.class);
		    startActivity(intent);
		    
		}
		else if (position == 1) {
		    
		    Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
		    startActivity(intent);
		    
		}
		else if (position == 2) {
		    
		    dialog = ProgressDialog.show(FormConfigPrinter.this, "Imprimiendo", "Retire la tirilla cuando este lista.", true);
		    dialog.show();
		    
		    new Thread(new Runnable() {
			
			@Override
			public void run() {
			    Looper.prepare();
			    SharedPreferences settings = getSharedPreferences(CONFIG_IMPRESORA, MODE_PRIVATE);
			    final String macImpresora = settings.getString(MAC_IMPRESORA, "-");
			    
			    if (macImpresora.equals("-")) {
				dialog.dismiss();
				runOnUiThread(new Runnable() {
				    
				    @Override
				    public void run() {
					Toast.makeText(FormConfigPrinter.this,
						"Aun no hay Impresora Predeterminada.\n\nPor Favor primero Configure la Impresora!", Toast.LENGTH_SHORT)
						.show();
				    }
				});
			    }
			    else {
				
				if (wR240 == null) {
				    wR240 = new WoosimR240(FormConfigPrinter.this);
				}
				int conect = wR240.conectarImpresora(macImpresora);
				
				switch (conect) {
				    case 1:
					wR240.generarPaginaPrueba();
					wR240.imprimirBuffer(true);
					break;
					
				    case -2:
					runOnUiThread(new Runnable() {
					    
					    @Override
					    public void run() {
						Toast.makeText(FormConfigPrinter.this, "-2 fallo conexion", Toast.LENGTH_SHORT).show();
					    }
					});
					break;
					
				    case -8:
					dialog.dismiss();
					runOnUiThread(new Runnable() {
					    
					    @Override
					    public void run() {
						Util.MostrarAlertDialog(FormConfigPrinter.this,
							"Bluetooth apagado. Por favor habilite el bluetoth para imprimir.");
					    }
					});
					break;
					
				    default:
					runOnUiThread(new Runnable() {
					    
					    @Override
					    public void run() {
						Toast.makeText(FormConfigPrinter.this, "Error desconocido, intente nuevamente.", Toast.LENGTH_SHORT).show();
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
				    dialog.dismiss();
				}
			    }
			    Looper.myLooper().quit();
			}
		    }).start();
		}
	    }
	});
    }
    
    
    
    @Override
    protected void onDestroy() {
	// TODO Auto-generated method stub
	super.onDestroy();
	if(wR240 != null){
	    wR240.desconectarImpresora();
	}
    }
    
    
    private static Handler handlerMensaje = new Handler() {
	
	@Override
	public void handleMessage(Message msg) {
	    
	    if (progressDialog != null) {
		progressDialog.cancel();
	    }
	    
	    if (context != null) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
		    
		    public void onClick(DialogInterface dialog, int id) {
			
			dialog.cancel();
		    }
		});
		
		AlertDialog alertDialog = builder.create();
		alertDialog.setMessage(mensaje);
		alertDialog.show();
	    }
	}
    };
    
    public static final String TAG = FormConfigPrinter.class.getName();
}
