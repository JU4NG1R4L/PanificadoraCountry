package co.com.panificadoracountry;

import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import co.com.panificadoracountry.R;
import co.com.BusinessObject.DataBaseBO;
import co.com.BusinessObject.DataBaseBOJ;
import co.com.DataObject.Cliente;
import co.com.DataObject.ItemListView;
import co.com.DataObject.Usuario;

public class FormBuscarClienteActivity extends Activity {
  
  Vector<Cliente> listaClientes;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.form_buscar_cliente);
    CargarOpcionesBusqueda();
    SetListenerListView();
    
  }
  
  public void OnClickFormBuscarCliente(View view) {
    EditText txtBusquedaCliente = (EditText) findViewById(R.id.txtBusquedaCliente);
    String cadBusqueda = txtBusquedaCliente.getText().toString().trim();
    OcultarTeclado(txtBusquedaCliente);
    if (cadBusqueda.equals("")) {
      Toast.makeText(getApplicationContext(), "Debe ingresar la opcion de Busqueda", Toast.LENGTH_SHORT).show();
      txtBusquedaCliente.requestFocus();
      
    } else {
      String where = "";
      Spinner cbBusquedaCliente = (Spinner) findViewById(R.id.cbBusquedaCliente);
      String opBusqueda = cbBusquedaCliente.getSelectedItem().toString();
      if (opBusqueda.equals(Const.POR_NOMBRE)) {
        where = " where representante_legal LIKE '%" + cadBusqueda + "%'";
        
      } else if (opBusqueda.equals(Const.POR_CODIGO)) {
        where = " where cod_cliente LIKE '%" + cadBusqueda + "%'";
        
      } else if (opBusqueda.equals(Const.POR_RAZON_SOCIAL)) {
        where = "where razon_social LIKE '%" + cadBusqueda + "%'";
      }
      ItemListView[] listaItems = null;
      Vector<ItemListView> listaItemsCliente = new Vector<ItemListView>();
      listaClientes = DataBaseBOJ.BuscarClientes(where, listaItemsCliente);
      if (listaItemsCliente.size() > 0) {
        listaItems = new ItemListView[listaItemsCliente.size()];
        listaItemsCliente.copyInto(listaItems);
        ListViewAdapter adapter = new ListViewAdapter(this, listaItems, R.drawable.cliente2, 0xff2E65AD);
        ListView listaBusquedaClientes = (ListView) findViewById(R.id.listaBusquedaClientes);
        listaBusquedaClientes.setAdapter(adapter);
        
      } else {
        ListViewAdapter adapter = new ListViewAdapter(FormBuscarClienteActivity.this, new ItemListView[]{}, R.drawable.cliente2, 0xff2E65AD);
        ListView listaBusquedaClientes = (ListView) findViewById(R.id.listaBusquedaClientes);
        listaBusquedaClientes.setAdapter(adapter);
        if (listaClientes != null) listaClientes.removeAllElements();
        Toast.makeText(getApplicationContext(), "Busqueda sin resultados", Toast.LENGTH_SHORT).show();
      }
    }
  }
  
  public void CargarOpcionesBusqueda() {
    Spinner cbBusquedaCliente = (Spinner) findViewById(R.id.cbBusquedaCliente);
    String[] items = new String[]{Const.POR_CODIGO, Const.POR_NOMBRE, Const.POR_RAZON_SOCIAL};
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    cbBusquedaCliente.setAdapter(adapter);
    cbBusquedaCliente.setOnItemSelectedListener(new OnItemSelectedListener() {
      
      @Override
      public void onItemSelected(AdapterView<?> parent, View selectedItemView, int position, long id) {
        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
        String opSel = ((Spinner) findViewById(R.id.cbBusquedaCliente)).getItemAtPosition(position).toString();
        if (opSel.equals(Const.POR_NOMBRE)) {
          OpcionSeleccionada("Ingrese Parte del Nombre:", InputType.TYPE_CLASS_TEXT);
          
        } else if (opSel.equals(Const.POR_CODIGO)) {
          OpcionSeleccionada("Ingrese Parte del Codigo:", InputType.TYPE_CLASS_TEXT);
          
        } else if (opSel.equals(Const.POR_RAZON_SOCIAL)) {
          OpcionSeleccionada("Ingrese Parte de la Razon Social:", InputType.TYPE_CLASS_TEXT);
        }
      }
      
      @Override
      public void onNothingSelected(AdapterView<?> parentView) {
      }
    });
  }
  
  public void OpcionSeleccionada(String label, int inputType) {
    TextView lblBusquedaCliente = (TextView) findViewById(R.id.lblBusquedaCliente);
    EditText txtBusquedaCliente = (EditText) findViewById(R.id.txtBusquedaCliente);
    txtBusquedaCliente.setText("");
    lblBusquedaCliente.setText(label);
    txtBusquedaCliente.setInputType(inputType);
    ListViewAdapter adapter = new ListViewAdapter(this, new ItemListView[]{}, R.drawable.cliente, 0x2E65AD);
    ListView listaBusquedaClientes = (ListView) findViewById(R.id.listaBusquedaClientes);
    listaBusquedaClientes.setAdapter(adapter);
    if (listaClientes != null) listaClientes.removeAllElements();
  }
  
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == Const.RESP_PEDIDO_EXITOSO && resultCode == RESULT_OK) {
      ((Spinner) findViewById(R.id.cbBusquedaCliente)).setSelection(0, true);
      OpcionSeleccionada("Ingrese Parte del Nombre:", InputType.TYPE_CLASS_TEXT);
    }
  }
  
  public void SetListenerListView() {
    ListView listaOpciones = (ListView) findViewById(R.id.listaBusquedaClientes);
    listaOpciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CargarInformacionCliente(position);
      }
    });
  }
  
  public void CargarInformacionCliente(final int position) {
    Cliente clienteSel = listaClientes.elementAt(position);
    boolean tienePedido = DataBaseBOJ.ExistePedidoCliente(clienteSel.codigo);
    if (tienePedido) {
      AlertDialog alertDialog;
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setCancelable(false).setPositiveButton("Si", new DialogInterface.OnClickListener() {
        
        public void onClick(DialogInterface dialog, int id) {
          Usuario usuario = DataBaseBO.CargarUsuario();
          if (usuario == null) {
            Toast.makeText(getBaseContext(), "No se pudo cargar la informacion del Usuario", Toast.LENGTH_LONG).show();
            
          } else {
            Main.cliente = listaClientes.elementAt(position);
            Cliente.save(FormBuscarClienteActivity.this, Main.cliente);
            Intent formInfoCliente = new Intent(FormBuscarClienteActivity.this, FormInfoClienteActivity.class);
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
        
      } else {
        Main.cliente = listaClientes.elementAt(position);
        Cliente.save(this, Main.cliente);
        Intent formInfoCliente = new Intent(FormBuscarClienteActivity.this, FormInfoClienteActivity.class);
        startActivityForResult(formInfoCliente, Const.RESP_PEDIDO_EXITOSO);
      }
    }
    
  }
  
  public void OcultarTeclado(EditText editText) {
    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
  }
  
  @Override
  protected void onDestroy() {
    super.onDestroy();
  }
  
}
