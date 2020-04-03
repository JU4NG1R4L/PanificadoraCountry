package co.com.panificadoracountry;
import co.com.panificadoracountry.R;
import co.com.BusinessObject.DataBaseBO;
import co.com.DataObject.EstadisticaRecorrido;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TableRow;
import android.widget.TextView;

public class FormEstadisticaRecorrido extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_estadistica_recorrido);

		CargarEstadisticas();
		
		if( Const.tipoAplicacion == Const.PREVENTA ){
			
			( ( TableRow ) findViewById( R.id.trDevSinSync ) ).setVisibility( View.GONE );
			( ( TableRow ) findViewById( R.id.trDevSync ) ).setVisibility( View.GONE );
			( ( TableRow ) findViewById( R.id.trTotalDev ) ).setVisibility( View.GONE );
			( ( TableRow ) findViewById( R.id.trTotalDevoluciones ) ).setVisibility( View.GONE );
		}

	}

	public void CargarEstadisticas() {

		EstadisticaRecorrido estadisticaRecorrido; 
		
		DataBaseBO.setAppAutoventa();
		
		estadisticaRecorrido =  DataBaseBO.CargarEstadisticas();

		((TextView) findViewById(R.id.lblTotalPedidos)).setText("" + estadisticaRecorrido.total_pedidos);
		((TextView) findViewById(R.id.lblTotalDevoluciones)).setText("" + estadisticaRecorrido.total_devoluciones);
		((TextView) findViewById(R.id.lblPedidosSincronizados)).setText("" + estadisticaRecorrido.total_pedidos_sync);
		((TextView) findViewById(R.id.lblDevolucionesSincronizados)).setText("" + estadisticaRecorrido.total_devoluciones_sync);
		((TextView) findViewById(R.id.lblPedidosSinSincronizar)).setText("" + estadisticaRecorrido.total_pedidos_sin_sync);
		((TextView) findViewById(R.id.lblDevolucionesSinSincronizar)).setText("" + estadisticaRecorrido.total_devoluciones_sin_sync);
		((TextView) findViewById(R.id.lblEfectividad)).setText("" + estadisticaRecorrido.efectividad + "%");
		((TextView) findViewById(R.id.lblTotalVenta)).setText("" + Util.SepararMiles("" + estadisticaRecorrido.total_venta));
		((TextView) findViewById(R.id.lblValorDevoluciones)).setText("" + Util.SepararMiles("" + estadisticaRecorrido.valor_devoluciones));
		((TextView) findViewById(R.id.lblNoCompras)).setText(""  + estadisticaRecorrido.totalNoCompras);
		((TextView) findViewById(R.id.lblVisitas)).setText("" + String.valueOf(estadisticaRecorrido.totalNoCompras+estadisticaRecorrido.total_pedidos));


	}

	@Override
	protected void onResume() {

		super.onResume();
		if (Main.usuario == null || Main.usuario.codigoVendedor == null)
		DataBaseBO.CargarInfomacionUsuario();
		CargarEstadisticas();
	}





	@Override
	protected void onDestroy() {

		super.onDestroy();
	}




}
