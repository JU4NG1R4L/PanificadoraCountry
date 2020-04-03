package co.com.panificadoracountry;

import java.util.Hashtable;

import co.com.panificadoracountry.R;
import co.com.BusinessObject.DataBaseBO;
import co.com.DataObject.EstadisticaRecorrido;
import co.com.DataObject.Venta;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class FormInformeVentas extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_informe_ventas);

		CargarInformacionVentas();
	
	}

	public void CargarInformacionVentas() {
		
		DataBaseBO.setAppAutoventa();

		Venta venta = DataBaseBO.getValoresVentas();

		if (venta != null) {

			((TextView) findViewById(R.id.lblTotRetenciones)).setText(Util.SepararMiles(Util.Redondear(("" + Util.QuitarE(venta.totalRetenciones + "")), 0)));
			((TextView) findViewById(R.id.lblTotSubTotal)).setText(Util.SepararMiles(Util.Redondear(("" + Util.QuitarE(venta.subTotal + "")), 0)));
			((TextView) findViewById(R.id.lblTotIva)).setText(Util.SepararMiles(Util.Redondear(("" + Util.QuitarE(venta.iva + "")),0)));
			((TextView) findViewById(R.id.lblTotDescuento)).setText(Util.SepararMiles(Util.Redondear(("" + Util.QuitarE(venta.descuento + "")), 0)));
			((TextView) findViewById(R.id.lblTotNeto)).setText(Util.SepararMiles(Util.Redondear(("" + Util.QuitarE(venta.neto + "")), 0)));

		}

	}

	@Override
	protected void onResume() {

		super.onResume();
		CargarInformacionVentas();
	}



	@Override
	protected void onDestroy() {

		super.onDestroy();
	}



	

}
