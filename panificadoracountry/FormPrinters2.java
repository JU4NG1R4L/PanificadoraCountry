package co.com.panificadoracountry;

import java.util.ArrayList;

import co.com.panificadoracountry.R;
import co.com.DataObject.ItemListView;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.app.AlertDialog;

public class FormPrinters2 extends Activity {
	
	private ListViewPrinterAdapter adapter;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayList<ItemListView> listDevices = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.form_printers);
        setResult(Activity.RESULT_CANCELED);
        
        listDevices = new ArrayList<ItemListView>();
        adapter = new ListViewPrinterAdapter(this, listDevices, R.drawable.op_bluetooth, 0x2E65AD);
        ListView listViewDevices = (ListView) findViewById(R.id.listViewDevices);
        listViewDevices.setAdapter(adapter);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(receiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(receiver, filter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        doDiscovery();
        
        setListenerListView();
        
    }
    
    @Override
    protected void onDestroy() {
    	
    	super.onDestroy();
    	
    	if (bluetoothAdapter != null) {
        	bluetoothAdapter.cancelDiscovery();
        }

        this.unregisterReceiver(receiver);
    }

    private void doDiscovery() {
    	
        setProgressBarIndeterminateVisibility(true);
        
        if (bluetoothAdapter.isDiscovering()) {
        	bluetoothAdapter.cancelDiscovery();
        }
        
        bluetoothAdapter.startDiscovery();
    }
    
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
    	
    	@Override
        public void onReceive(Context context, Intent intent) {
    		
    		String action = intent.getAction();
    		
    		if (BluetoothDevice.ACTION_FOUND.equals(action)) {
    			
    			BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
    			
    			if (listDevices != null) {
    				
    				String name = bluetoothDevice.getName();
                	ItemListView item = new ItemListView();
                	
					item.titulo = bluetoothDevice.getAddress();
					item.subTitulo = name != null ? name : "";
					
					
					
					listDevices.add(item);
					adapter.notifyDataSetChanged();
                }
    			
    		} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
    			
    			//Toast.makeText(FormPrinters.this, "Total Dispositivos Encontrados: " + listDevices.size(), Toast.LENGTH_SHORT).show();
    			
    			 Toast toast= Toast.makeText(getApplicationContext(), 
    					 "Total Dispositivos Encontrados: " + listDevices.size(),Toast.LENGTH_SHORT);  
    					 toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
    					 toast.show();
    			
    			setProgressBarIndeterminateVisibility(false);
            }
        }
    };


    
    
public void setListenerListView() {
		
		ListView listaOpciones = (ListView) findViewById(R.id.listViewDevices);
		listaOpciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            
			@Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				ItemListView item = listDevices.get(position);
	
				final String addres = item.titulo; 
				final String name   = item.subTitulo ;
				
				
		
				AlertDialog.Builder builder = new AlertDialog.Builder(FormPrinters2.this);
	    		builder.setMessage("Desea Establecer como Impresora Predeterminada?\n"+name+" - "+addres)
	    		.setCancelable(false)
	    		.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
	    			
	    			public void onClick(DialogInterface dialog, int id) {
	    				
	    			
	    				dialog.cancel();

	    				
	    				SharedPreferences prefs =  getSharedPreferences("PRINTER",  MODE_PRIVATE);
	    				SharedPreferences.Editor editor = prefs.edit();
	    				editor.putString("MAC_IMPRESORA",addres);
	   	    			editor.putString("LABEL_IMPRESORA",name);
	   	    			editor.commit();
	   	    				

	    				finish();
	    				
	    				
	    			}
	    		})
	    		.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
	    			
	    			public void onClick(DialogInterface dialog, int id) {
	    				
	    				dialog.cancel();
	    			}
	    		});
	    		AlertDialog alert = builder.create();
	    		alert.show();

				
				
	
			}
        });
	}
	
    
    
    
    

}
