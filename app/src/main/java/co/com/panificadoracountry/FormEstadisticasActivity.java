package co.com.panificadoracountry;

import co.com.panificadoracountry.R;
import co.com.BusinessObject.DataBaseBO;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class FormEstadisticasActivity extends TabActivity {

	TabHost tabHost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_estadisticas);
		
		DataBaseBO.setAppAutoventa();


		if( Const.tipoAplicacion == Const.PREVENTA ){

		
		    CargarTabsPreventa();
		
		}else{
			
			CargarTabsAutoventa();
			
		}
		
			
	}
	
	public void CargarTabsAutoventa() {
		
		try {
			
			Intent intent;
			tabHost = getTabHost();
			tabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
			
			//Pestana Recorrido
			intent = new Intent().setClass(this, FormEstadisticaRecorrido.class);
			SetupTab(new TextView(this), "recorrido", "Recorrido", intent);
			
			//Pestana Visitas Realizadas 
			intent = new Intent().setClass(this, FormEstadisticaPedidos.class);
			intent.putExtra("pedido", 1);
			SetupTab(new TextView(this), "visitasRealizadas", "&nbsp;&nbsp;&nbsp;Visitas Realizadas", intent);
			
			
			
			//cambios
			intent = new Intent().setClass(this, FormEstadisticaPedidos.class);
			intent.putExtra("pedido", 4);
			SetupTab(new TextView(this), "notas", "&nbsp;&nbsp;&nbsp;Cambios", intent);
		

			
			
			//Pestana Visitas Realizadas 
			intent = new Intent().setClass(this, FormEstadisticaPedidos.class);
			intent.putExtra("pedido", -1);
			//SetupTab(new TextView(this), "visitasRealizadasCN", "&nbsp;&nbsp;Clientes Ocasionales", intent);
			SetupTab(new TextView(this), "visitasRealizadasCN", "&nbsp;&nbsp;Ventas Clientes Nuevos", intent);
			
								
			//Pestana Sin Sincronizar
			intent = new Intent().setClass(this, FormEstadisticaPedidos.class);
			intent.putExtra("pedido", 0);
			SetupTab(new TextView(this), "sinSincronizar", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Sin Sincronizar", intent);
			
			//Pestana Sin Sincronizar
			intent = new Intent().setClass(this, FormEstadisticasNoCompra.class);
			intent.putExtra("pedido", false);
			SetupTab(new TextView(this), "noCompra", "&nbsp;&nbsp;&nbsp;&nbsp;No Compra", intent);
			
			tabHost.setCurrentTab(0);
	
			
		} catch (Exception e) {
			
			String msg = e.getMessage();
			Log.e("Cargando Tabs", msg, e);
		}
	}
	
	
	public void CargarTabsPreventa() {
		
		try {
			
			Intent intent;
			tabHost = getTabHost();
			tabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
			
			//Pestana Recorrido
			intent = new Intent().setClass(this, FormEstadisticaRecorrido.class);
			SetupTab(new TextView(this), "recorrido", "Recorrido", intent);
			
			//Pestana Visitas Realizadas 
			intent = new Intent().setClass(this, FormEstadisticaPedidos.class);
			intent.putExtra("pedido", 1);
			SetupTab(new TextView(this), "visitasRealizadas", "&nbsp;&nbsp;&nbsp;Visitas Realizadas", intent);
			
			
			
			//cambios
			intent = new Intent().setClass(this, FormEstadisticaPedidos.class);
			intent.putExtra("pedido", 4);
			SetupTab(new TextView(this), "notas", "&nbsp;&nbsp;&nbsp;Cambios", intent);
		

			
			
			//Pestana Visitas Realizadas 
			intent = new Intent().setClass(this, FormEstadisticaPedidos.class);
			intent.putExtra("pedido", -1);
			//SetupTab(new TextView(this), "visitasRealizadasCN", "&nbsp;&nbsp;Clientes Ocasionales", intent);
			SetupTab(new TextView(this), "visitasRealizadasCN", "&nbsp;&nbsp;Ventas Clientes Nuevos", intent);
			
								
			//Pestana Sin Sincronizar
			intent = new Intent().setClass(this, FormEstadisticaPedidos.class);
			intent.putExtra("pedido", 0);
			SetupTab(new TextView(this), "sinSincronizar", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Sin Sincronizar", intent);
			
			//Pestana Sin Sincronizar
			intent = new Intent().setClass(this, FormEstadisticasNoCompra.class);
			intent.putExtra("pedido", false);
			SetupTab(new TextView(this), "noCompra", "&nbsp;&nbsp;&nbsp;&nbsp;No Compra", intent);
			
			tabHost.setCurrentTab(0);
			
			
			
		} catch (Exception e) {
			
			String msg = e.getMessage();
			Log.e("Cargando Tabs", msg, e);
		}
	}

	
	
	
	private void SetupTab(final View view, final String tag, final String titulo, Intent intent) {
		
		View tabView = CreateTabView(tabHost.getContext(), titulo);
		TabSpec setContent = tabHost.newTabSpec(tag).setIndicator(tabView).setContent(intent);
		tabHost.addTab(setContent);
	}
	
	private static View CreateTabView(final Context context, final String titulo) {

		View view = LayoutInflater.from(context).inflate(R.layout.tab_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(Html.fromHtml(titulo.trim().replace(" ", "<br />")));
		return view;
	}
	
	

    
	@Override
    protected void onDestroy() {
    	
    	super.onDestroy();
     }
	
	
	
	
	/*public void CargarOpcionesReportes() {

		ItemListView[] items = new ItemListView[6];
		
		items[0] = new ItemListView();
		items[0].titulo = "Resumen Recorrido";
		
		items[1] = new ItemListView();
		items[1].titulo = "Pedidos Realizados";
		
		items[2] = new ItemListView();
		items[2].titulo = "Pedidos sin Sincronizar";
		
		items[3] = new ItemListView();
		items[3].titulo = "Terminar Labores";
		
		items[4] = new ItemListView();
		items[4].titulo = "Actualizar Informacion";
		
		items[5] = new ItemListView();
		items[5].titulo = "Actualizar Vendedor";
		
		ListViewAdapter adapter = new ListViewAdapter(this, items);
		ListView listaClienteRutero = (ListView) findViewById(R.id.listaEstadisticas);
		listaClienteRutero.setAdapter(adapter);
	}*/
	
	/*
	public void CargarTabs() {
		
		try {
			
			tabHost = getTabHost();
			Intent intent;
			
			tabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
			
			intent = new Intent().setClass(this, FormEstadisticaRecorrido.class);
			setupTab(new TextView(this), "Recorrido", intent);
			
			intent = new Intent().setClass(this, FormEstadisticaPedidos.class);
			setupTab(new TextView(this), "Pedidos", intent);
			
			tabHost.setCurrentTab(0);
			
		} catch (Exception e) {
			
			String msg = e.getMessage();
			Log.e("Cargando Tabs...", msg, e);
		}
	}	
	
	private void setupTab(final View view, final String tag, Intent intent) {
		
		View tabView = createTabView(tabHost.getContext(), tag);
		TabSpec setContent = tabHost.newTabSpec(tag).setIndicator(tabView).setContent(intent);
		tabHost.addTab(setContent);
	}
	
	private static View createTabView(final Context context, final String text) {

		View view = LayoutInflater.from(context).inflate(R.layout.tab_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
	}*/
}
