package co.com.panificadoracountry;

import java.util.Vector;

import co.com.panificadoracountry.R;
import co.com.BusinessObject.DataBaseBO;
import co.com.BusinessObject.FileBO;
import co.com.DataObject.Cartera;
import co.com.DataObject.Cliente;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.HorizontalScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class FormCarteraInformacion extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_cartera_informacion);

	}
	
	
	public void onResume(){
		
		super.onResume();
		DataBaseBO.ValidarUsuario();
		FileBO.validarCliente( this );
		DataBaseBO.setAppAutoventa();
		cargarCartera();
	}

	public void cargarCartera() {

		Vector<Cartera> listaCartera = DataBaseBO.listaCartera(Main.cliente.codigo);

		if (listaCartera.size() > 0) {

			TableLayout table = new TableLayout(this);
			table.setBackgroundColor(Color.WHITE);

			String[] cabecera = { "Documento", "valor", "Saldo",
					"Vencimiento", "Dias" };
			Util.Headers(table, cabecera, this);

			HorizontalScrollView scroll = (HorizontalScrollView) findViewById(R.id.scrollCartera);
			scroll.removeAllViews();
			scroll.addView(table);

			for (Cartera cartera : listaCartera) {

				TextView textViewAux;
				TableRow fila = new TableRow(this);

				textViewAux = new TextView(this);
				textViewAux.setText(cartera.documento + "\n");
				textViewAux.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
				textViewAux.setTextColor(Color.argb(255, 0, 0, 0));
				textViewAux.setBackgroundDrawable(this.getResources()
						.getDrawable(R.drawable.table_cell_row_1));
				fila.addView(textViewAux);

				textViewAux = new TextView(this);
				textViewAux.setText(Util.SepararMiles(Util.Redondear(
						cartera.strSaldo, 0)) + "\n");
				textViewAux.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
				textViewAux.setTextColor(Color.argb(255, 0, 0, 0));
				textViewAux.setBackgroundDrawable(this.getResources()
						.getDrawable(R.drawable.table_cell_row_1));
				fila.addView(textViewAux);

			
				textViewAux = new TextView(this);
				textViewAux.setText(Util.SepararMiles(Util.Redondear(
						String.valueOf(cartera.saldo) + "", 0))
						+ "\n");
				textViewAux.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
				textViewAux.setTextColor(Color.argb(255, 0, 0, 0));
				textViewAux.setBackgroundDrawable(this.getResources()
						.getDrawable(R.drawable.table_cell_row_1));
				fila.addView(textViewAux);

				textViewAux = new TextView(this);
				textViewAux.setText(cartera.FechaVecto + "\n");
				textViewAux.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
				textViewAux.setTextColor(Color.argb(255, 0, 0, 0));
				textViewAux.setBackgroundDrawable(this.getResources()
						.getDrawable(R.drawable.table_cell_row_1));
				fila.addView(textViewAux);

				textViewAux = new TextView(this);
				textViewAux.setText(cartera.dias + "\n");
				textViewAux.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
				textViewAux.setTextColor(Color.argb(255, 0, 0, 0));
				textViewAux.setBackgroundDrawable(this.getResources()
						.getDrawable(R.drawable.table_cell_row_1));
				fila.addView(textViewAux);

				table.addView(fila);
			}
		}
	}
}
