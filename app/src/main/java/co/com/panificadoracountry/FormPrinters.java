package co.com.panificadoracountry;


import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


import co.com.panificadoracountry.R;
import co.com.DataObject.ItemListView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class FormPrinters extends Activity {
    
    public static final String TAG = FormPrinters.class.getName();
    
    public final static String CONFIG_IMPRESORA = "PRINTER";
    public final static String MAC_IMPRESORA = "MAC";
    public final static String LABEL_IMPRESORA = "LABEL";
    
    private static String mensaje;
    private static Activity context = null;
    private static ProgressDialog progressDialog;
    
    private String message = null;
    private ListViewPrinterAdapter adapter;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayList<ItemListView> listDevices = null;
    private ArrayList<ItemListView> listDevicesPaired = null;
    
    private ListViewPrinterAdapter mPairedDevicesArrayAdapter;
    
    boolean waitingForBonding = false;
    
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	setContentView(R.layout.form_printers);
	
	if (turnOnBluetooth()) {
	    
	    listDevices = new ArrayList<ItemListView>();
	    adapter = new ListViewPrinterAdapter(this, listDevices, R.drawable.op_bluetooth, 0x2E65AD);
	    ListView listViewDevices = (ListView) findViewById(R.id.listViewDevices);
	    listViewDevices.setAdapter(adapter);
	    
	    /*
	     * listview para dispositivos que ya estan emparejados en el telefono
	     */
	    listDevicesPaired = new ArrayList<ItemListView>();
	    mPairedDevicesArrayAdapter = new ListViewPrinterAdapter(this, listDevicesPaired, R.drawable.op_bluetooth, 0x2E65AD);
	    ListView pairedListView = (ListView) findViewById(R.id.dispositivos_emparejados);
	    pairedListView.setAdapter(mPairedDevicesArrayAdapter);
	    pairedListView.setOnItemClickListener(listenerDevicesPaired);
	    

	    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
	    this.registerReceiver(receiver, filter);
	    
	    filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
	    this.registerReceiver(receiver, filter);
	    
//	    filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
//	    this.registerReceiver(receiverChange, filter);
	    
	    setListenerListView();
	    doDiscovery();
	    
	}
	else {
	    
	    Util.MostrarAlertDialog(this, "El Bluetooth esta Deshabilitado");
	}
    }
    
    
    
    @Override
    protected void onDestroy() {
	
	super.onDestroy();
	
	try {
	    if (bluetoothAdapter != null) {
		bluetoothAdapter.cancelDiscovery();
	    }
	    
	    FormPrinters.this.unregisterReceiver(receiver);
	    // unregisterReceiver(receiverChange);
	} catch (Exception e) {
	    
	}
	
	// cancelDiscovery();
    }
    
    
    
    public boolean turnOnBluetooth() {
	bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	
	if (bluetoothAdapter.isEnabled()) {
	    return true;
	}
	
	if (!bluetoothAdapter.isEnabled()) {
	    
	    boolean activo = bluetoothAdapter.enable();
	    return activo;
	    
	}
	else {
	    
	    return false;
	}
    }
    
    
    
    public void cancelDiscovery() {
	
	BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	
	if (bluetoothAdapter.isDiscovering()) {
	    bluetoothAdapter.cancelDiscovery();
	}
    }
    
    
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	
	if (keyCode == KeyEvent.KEYCODE_BACK) {
	    
	    // cancelar = true;
	    // cancelDiscovery();
	    // setFeatureIndeterminateProgress(false);
	    
	    finish();
	    return true;
	}
	
	return super.onKeyDown(keyCode, event);
    }
    
    
    
    private void doDiscovery() {
	
	// Indicate scanning in the title
	setProgressBarIndeterminateVisibility(true);
	
	/* Get a set of currently paired devices */
	Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
	
	ItemListView item;
	
	// If there are paired devices, add each one to the ArrayAdapter
	if (pairedDevices.size() > 0) {
	    findViewById(R.id.textViewTituloDispositivosEmparejados).setVisibility(View.VISIBLE);
	    for (BluetoothDevice device : pairedDevices) {
		
		item = new ItemListView();
		
		String name = device.getName();
		item.titulo = device.getAddress();
		item.subTitulo = name != null ? name : "";
		item.state = device.getBondState();
		item.device = device;
		mPairedDevicesArrayAdapter.add(item);
		mPairedDevicesArrayAdapter.notifyDataSetChanged();
	    }
	}
	// buscar nuevos dispositivos.
	doDiscoveryNewDevices();
    }
    
    
    
    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscoveryNewDevices() {
	setTitle("Buscando Dispositivos");
	// If we're already discovering, stop it
	if (bluetoothAdapter.isDiscovering()) {
	    bluetoothAdapter.cancelDiscovery();
	}
	// Request discover from BluetoothAdapter
	bluetoothAdapter.startDiscovery();
    }
    
    ItemListView itemPrinter;
    
    
    
    public OnItemClickListener listenerDevicesPaired = new OnItemClickListener() {

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	    
	    /* Detener la busqueda de dispositivos. */
	    bluetoothAdapter.cancelDiscovery();
	    
	    /*obtener la impresora seleccionada por el usuario.*/
	    itemPrinter = listDevicesPaired.get(position);
	    
	    AlertDialog.Builder builder = new AlertDialog.Builder(FormPrinters.this);
	    builder.setCancelable(false).setPositiveButton("Si", new DialogInterface.OnClickListener() {
		
		public void onClick(DialogInterface dialog, int id) {
		    dialog.cancel();
		    SharedPreferences settings = getSharedPreferences(CONFIG_IMPRESORA, MODE_PRIVATE);
		    SharedPreferences.Editor editor = settings.edit();
		    
		    editor.putString(MAC_IMPRESORA, itemPrinter.titulo);
		    editor.putString(LABEL_IMPRESORA, itemPrinter.subTitulo);
		    editor.commit();
		    
		    /*setiar addres y Finalizar Activity*/
		    Intent intent = new Intent();
		    intent.putExtra("addres", itemPrinter.titulo);
		    setResult(Activity.RESULT_OK, intent);
		    finish();
		}
	    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
		
		public void onClick(DialogInterface dialog, int id) {
		    dialog.cancel();
		}
	    });
	    
	    AlertDialog alertDialog = builder.create();
	    alertDialog.setMessage("Esta seguro de establecer como Impresora el Dispositivo: "
		    + (itemPrinter.subTitulo.equals("") ? itemPrinter.titulo : itemPrinter.subTitulo));
	    alertDialog.show();
	}
    }; 
    
    
    public void setListenerListView() {
	
	ListView listaOpciones = (ListView) findViewById(R.id.listViewDevices);
	listaOpciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
		final ItemListView item = listDevices.get(position);
		itemPrinter = listDevices.get(position);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(FormPrinters.this);
		builder.setCancelable(false).setPositiveButton("Si", new DialogInterface.OnClickListener() {
		    
		    public void onClick(DialogInterface dialog, int id) {
			dialog.cancel();
			
			SharedPreferences settings = getSharedPreferences(CONFIG_IMPRESORA, MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			
			editor.putString(MAC_IMPRESORA, itemPrinter.titulo);
			editor.putString(LABEL_IMPRESORA, itemPrinter.subTitulo);
			editor.commit();
			
			/*setiar addres y Finalizar Activity*/
			Intent intent = new Intent();
			intent.putExtra("addres", itemPrinter.titulo);
			setResult(Activity.RESULT_OK, intent);
			finish();
			
			// bluetoothAdapter =
			// BluetoothAdapter.getDefaultAdapter();
			
			// if (bluetoothAdapter.isDiscovering()) {
			// bluetoothAdapter.cancelDiscovery();
			// }
			
//			verificarConexion(item);
			
		    }
		    
		}).setNegativeButton("No", new DialogInterface.OnClickListener() {
		    
		    public void onClick(DialogInterface dialog, int id) {
			
			dialog.cancel();
		    }
		});
		
		AlertDialog alertDialog = builder.create();
		alertDialog.setMessage("Esta seguro de establecer como Impresora el Dispositivo: "
		        + (item.subTitulo.equals("") ? item.titulo : item.subTitulo));
		alertDialog.show();
	    }
	});
    }
    
    
    
//    public void verificarConexion(final ItemListView item) {
//	
//	new Thread(new Runnable() {
//	    public void run() {
//		mensaje = "";
//		try {
//		    Looper.prepare();
//		    BluetoothDevice printer = item.device;
//		    
//		    if (printer == null) {
//			mensaje = "No se pudo establecer la conexion con la Impresora.";
//		    }
//		    else {
//			int bondState = printer.getBondState();
//			if (bondState == BluetoothDevice.BOND_NONE || bondState == BluetoothDevice.BOND_BONDING) {
//			    connect(printer);
//			}
//			else {
//			    context = FormPrinters.this;
//			    handlerStart.sendEmptyMessage(0);
////			    validarImpresora(item);
//			    SharedPreferences settings = getSharedPreferences(CONFIG_IMPRESORA, MODE_PRIVATE);
//			    SharedPreferences.Editor editor = settings.edit();
//			    
//			    editor.putString(MAC_IMPRESORA, item.titulo);
//			    editor.putString(LABEL_IMPRESORA, item.subTitulo);
//			    editor.commit();
//			    handlerOK.sendEmptyMessage(0);
//			}
//		    }
//		    
//		    if (!mensaje.equals("")) {
//			context = FormPrinters.this;
//			handlerMensaje.sendEmptyMessage(0);
//		    }
//		    Looper.myLooper().quit();
//		} catch (Exception e) {
//		    
//		    String motivo = e.getMessage();
//		    Log.e(TAG, "validarImpresora -> " + motivo, e);
//		    
//		    mensaje = "No se pudo Establecer el Dispositivo Seleccionado.";
//		    
//		    if (motivo != null) {
//			mensaje += "\n\n" + motivo;
//		    }
//		    context = FormPrinters.this;
//		    handlerMensaje.sendEmptyMessage(0);
//		}
//	    }
//	}).start();
//    }
    
    
    
//    public void validarImpresora(final ItemListView item) {
//	
//	new Thread(new Runnable() {
//	    
//	    public void run() {
//		
//		mensaje = "";
//		BluetoothSocket socket = null;
//		
//		try {
//		    
//		    Looper.prepare();
//		    
//		    BluetoothDevice printer = item.device;
//		    
//		    if (printer == null) {
//			
//			mensaje = "No se pudo establecer la conexion con la Impresora.";
//			
//		    }
//		    else {
//			
//			int state = printer.getBondState();
//			
//			if (state == BluetoothDevice.BOND_BONDED) {
//			    
//			    UUID uuid = UUID.fromString(FormConfigPrinter.UUID_BPP);
//			    socket = printer.createRfcommSocketToServiceRecord(uuid);
//			    
//			    if (socket != null) {
//				
//				socket.connect();
//				
//				SharedPreferences settings = getSharedPreferences(CONFIG_IMPRESORA, MODE_PRIVATE);
//				SharedPreferences.Editor editor = settings.edit();
//				
//				editor.putString(MAC_IMPRESORA, item.titulo);
//				editor.putString(LABEL_IMPRESORA, item.subTitulo);
//				editor.commit();
//				
//				Thread.sleep(100);
//				context = FormPrinters.this;
//				handlerOK.sendEmptyMessage(0);
//				
//			    }
//			    else {
//				
//				mensaje = "No se pudo establecer la conexion con la Impresora.";
//			    }
//			    
//			}
//			else {
//			    
//			    mensaje = "La Impresora: " + item.titulo + " No esta Acoplada con el Dispositivo Movil.\n\nPor Favor intente de nuevo.";
//			}
//		    }
//		    
//		    if (!mensaje.equals("")) {
//			
//			context = FormPrinters.this;
//			handlerMensaje.sendEmptyMessage(0);
//		    }
//		    
//		    Looper.myLooper().quit();
//		    
//		} catch (Exception e) {
//		    
//		    String motivo = e.getMessage();
//		    Log.e(TAG, "validarImpresora -> " + motivo, e);
//		    
//		    mensaje = "No se pudo Establecer el Dispositivo Seleccionado.";
//		    
//		    if (motivo != null) {
//			mensaje += "\n\n" + motivo;
//		    }
//		    
//		    context = FormPrinters.this;
//		    handlerFinish.sendEmptyMessage(0);
//		    
//		}
//		finally {
//		    
//		    try {
//			
//			if (socket != null) {
//			    socket.close();
//			}
//			
//		    } catch (Exception e) {
//			
//			Log.e(TAG, "validarImpresora -> Cerrando socket ", e);
//		    }
//		}
//	    }
//	    
//	}).start();
//    }
    
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
	
	@Override
	public void onReceive(Context context, Intent intent) {
	    
	    String action = intent.getAction();
	    
	    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
		
		BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		
		if (bluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
		    if (listDevices != null) {
			
			String name = bluetoothDevice.getName();
			ItemListView item = new ItemListView();
			
			item.titulo = bluetoothDevice.getAddress();
			item.subTitulo = name != null ? name : "";
			item.state = bluetoothDevice.getBondState();
			item.device = bluetoothDevice;
			
			listDevices.add(item);
			adapter.notifyDataSetChanged();
		    }
		}
	    }
	    else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
		
		if (progressDialog != null)
		    progressDialog.dismiss();
		setTitle("Dispositivos");
		setProgressBarIndeterminateVisibility(false);
		Toast.makeText(FormPrinters.this, "Total Dispositivos Encontrados: " + listDevices.size(), Toast.LENGTH_SHORT).show();
		setProgressBarIndeterminateVisibility(false);
	    }
	    // else if
	    // (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
	    //
	    // Toast.makeText(FormPrinters.this, "parien cancel: ",
	    // Toast.LENGTH_SHORT).show();
	    // }
	}
    };
    
//    private final BroadcastReceiver receiverChange = new BroadcastReceiver() {
//	
//	@Override
//	public void onReceive(Context context, Intent intent) {
//	    
//	    String action = intent.getAction();
//	    Log.e(TAG, action);
//	    
//	    if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction())) {
//		
//		int prevBondState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, -1);
//		int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
//		
//		if (waitingForBonding) {
//		    
//		    if (prevBondState == BluetoothDevice.BOND_BONDING) {
//			
//			// check for both BONDED and NONE here because in some
//			// error cases the bonding fails and we need to fail
//			// gracefully.
//			if (bondState == BluetoothDevice.BOND_BONDED || bondState == BluetoothDevice.BOND_NONE) {
//			    setTitle("Dispositivos");
//			    progressDialog = ProgressDialog.show(FormPrinters.this, "", "Por Favor Espere...\n\nVerificando Conexion Bluetooth!",
//				    true);
//			    progressDialog.show();
//			    
//			    validarImpresora(itemPrinter);
//			   
//			    
////			    handlerOK.sendEmptyMessage(0);
//			    
//			    // Util.MostrarAlertDialog(FormPrinters.this,
//			    // "El usuario ya dio una respuesta!");
//			    // safely notify your thread to continue
//			    
//			}
//			else {
//			    setTitle("Dispositivos");
//			    setProgressBarIndeterminateVisibility(false);
//			    context = FormPrinters.this;
//			    handlerFinish.sendEmptyMessage(0);
//			}
//			
//		    }
//		    else {
//			setTitle("Dispositivos");
//			setProgressBarIndeterminateVisibility(false);
//			context = FormPrinters.this;
//			handlerFinish.sendEmptyMessage(0);
//		    }
//		    
//		}
//		else {
//		    setTitle("Dispositivos");
//		    setProgressBarIndeterminateVisibility(false);
//		    context = FormPrinters.this;
//		    handlerFinish.sendEmptyMessage(0);
//		}
//		
//	    }
//	    else {
//		setTitle("Dispositivos");
//		setProgressBarIndeterminateVisibility(false);
//		context = FormPrinters.this;
//		handlerFinish.sendEmptyMessage(0);
//	    }
//	}
//    };
    
    
    
//    private Boolean connect(BluetoothDevice bluetoothDevice) {
//	
//	Boolean bool = false;
//	
//	try {
//	    
//	    // Log.i("Log", "service metohd is called ");
//	    
//	    /*
//	     * Class cl = Class.forName("android.bluetooth.BluetoothDevice");
//	     * Class[] par = {}; Method method = cl.getMethod("createBond",
//	     * par); Object[] args = {}; bool = (Boolean)
//	     * method.invoke(bdDevice);//, args);// this invoke creates the
//	     * detected devices paired. //Log.i("Log",
//	     * "This is: "+bool.booleanValue()); //Log.i("Log",
//	     * "devicesss: "+bdDevice.getName());
//	     */
//	    
//	    // ////////////////////////////////////////////////////////////////////
//	    
//	    Method m = bluetoothDevice.getClass().getMethod("createBond", (Class[]) null);
//	    // Method m =
//	    // bluetoothDevice.getClass().getMethod("createInsecureRfcommSocket",
//	    // (Class[])null);
//	    m.invoke(bluetoothDevice, (Object[]) null);
//	    
//	    int bondState = bluetoothDevice.getBondState();
//	    
//	    if (bondState == BluetoothDevice.BOND_NONE || bondState == BluetoothDevice.BOND_BONDING) {
//		
//		waitingForBonding = true; // Class variable used later in the
//		// broadcast receiver
//		
//		// Also...I have the whole bluetooth session running on a
//		// thread. This was a key point for me. If the bond state is not
//		// BOND_BONDED, I wait here. Then see the snippets below
//		synchronized (this) {
//		    
//		    wait();
//		}
//	    }
//	    
//	} catch (Exception e) {
//	    
//	    Log.i("Log", "Inside catch of serviceFromDevice Method");
//	    e.printStackTrace();
//	}
//	
//	return bool.booleanValue();
//    };
    
    
    
//    private static Handler handlerOK = new Handler() {
//	
//	@Override
//	public void handleMessage(Message msg) {
//	    
//	    if (context != null) {
//		
//		AlertDialog.Builder builder = new AlertDialog.Builder(context);
//		builder.setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
//		    
//		    public void onClick(DialogInterface dialog, int id) {
//			
//			dialog.cancel();
//			context.finish();
//		    }
//		});
//		
//		AlertDialog alertDialog = builder.create();
//		alertDialog.setMessage("Impresora Establecida con Exito");
//		alertDialog.show();
//	    }
//	}
//    };
    
//    private static Handler handlerStart = new Handler() {
//	
//	@Override
//	public void handleMessage(Message msg) {
//	    
//	    if (context != null) {
//		
//		if (progressDialog != null) {
//		    progressDialog.cancel();
//		}
//		
//		progressDialog = ProgressDialog.show(context, "", "Por Favor Espere...\n\nVerificando Conexion Bluetooth!", true);
//		progressDialog.show();
//	    }
//	}
//    };
    
//    private static Handler handlerFinish = new Handler() {
//	
//	@Override
//	public void handleMessage(Message msg) {
//	    
//	    if (progressDialog != null) {
//		progressDialog.cancel();
//	    }
//	}
//    };
    
//    private static Handler handlerMensaje = new Handler() {
//	
//	@Override
//	public void handleMessage(Message msg) {
//	    
//	    if (context != null) {
//		
//		AlertDialog.Builder builder = new AlertDialog.Builder(context);
//		builder.setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
//		    
//		    public void onClick(DialogInterface dialog, int id) {
//			
//			dialog.cancel();
//		    }
//		});
//		
//		AlertDialog alertDialog = builder.create();
//		alertDialog.setMessage(mensaje);
//		alertDialog.show();
//	    }
//	    
//	    if (progressDialog != null) {
//		progressDialog.cancel();
//	    }
//	}
//    };
    
    
    
    public void OnClickCancelar(View view) {
	
	finish();
    }
    
    
    
//    public static String formatoPrueba() {
//	
//	char ret1 = 13;
//	char ret2 = 10;
//	
//	String ret = String.valueOf(ret1) + String.valueOf(ret2);
//	
//	String encabezado = "                                        " + ret + ret + ret;
//	encabezado = encabezado + "                                        " + ret;
//	encabezado = encabezado + "        INDUSTRIAS NORMANDY S.A.        " + ret;
//	encabezado = encabezado + "          NIT 890.807.529-8             " + ret;
//	encabezado = encabezado + "            INVENTARIO" + ret;
//	encabezado = encabezado + "----------------------------------------" + ret;
//	encabezado = encabezado + "           Vend:  " + Main.usuario.nombreVendedor + ret;
//	encabezado = encabezado + "           Fecha: " + Util.FechaActual("yyyy-MM-dd") + ret + ret + ret;
//	
//	return encabezado;
//    }
    
}
