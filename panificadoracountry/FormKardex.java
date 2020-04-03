/**
 * 
 */
package co.com.panificadoracountry;

import java.util.Vector;

import co.com.panificadoracountry.R;
import co.com.BusinessObject.DataBaseBO;
import co.com.DataObject.InformeInventario;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author JohnFaber
 *
 */
public class FormKardex extends Activity implements OnClickListener{

	
	ProgressDialog progressDialog;

	private static String mensaje;
	private static Activity context;
	private static String TAG = FormKardex.class.getName();
	Dialog dialogInventario;
	Vector<InformeInventario> listaInformeInv;
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO Auto-generated method stub
        setContentView(R.layout.form_kardex);
        
		progressDialog = ProgressDialog.show(FormKardex.this, "", "Cargando Informacion...", true);
		progressDialog.show();

		new Thread(){

			public void run(){

				CargarProductos();
			}
		}.start();
        
	}
	


	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		// TODO Auto-generated method stub
		
		if(Main.usuario == null || Main.usuario.codigoVendedor == null)
			DataBaseBO.CargarInfomacionUsuario();
		

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		// TODO Auto-generated method stub

	}
	
	
	public void CargarProductos() {

		listaInformeInv = DataBaseBO.CargarProductos();

		this.runOnUiThread( new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				TableLayout table = new TableLayout(FormKardex.this);
				table.setBackgroundColor(Color.WHITE);

				HorizontalScrollView scroll = (HorizontalScrollView)findViewById(R.id.scrollInventario);
				scroll.removeAllViews();
				scroll.addView(table);

				if (listaInformeInv.size() > 0) {

					//String[] cabecera = {"CODIGO\n", "INV\nINICIAL", "INV\nACTUAL", "CANT\nVENTAS", "CANT CAMBIO\nVENTAS", "CANT\nDEVOLUCIONES", "CANT CAMBIO\nDEVOLUCIONES",  "NOMBRE\n"};
					
					String[] cabecera = {"     \n", "CODIGO\n", "PRODUCTO\n"};
					
					
					Util.Headers(table, cabecera, FormKardex.this);

					TextView textViewAux;
					ImageView imageView;

					for (InformeInventario informeInv : listaInformeInv) {

						TableRow fila = new TableRow(FormKardex.this);

						imageView = new ImageView(FormKardex.this);
						imageView.setBackgroundResource(R.drawable.print);
						imageView.setOnClickListener(FormKardex.this);
						imageView.setTag(informeInv.codigo+"");
						fila.addView(imageView);
						
						textViewAux = new TextView(FormKardex.this);
						textViewAux.setText(informeInv.codigo+"");			
						textViewAux.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
						textViewAux.setTextColor(Color.argb(255,0,0,0));
						textViewAux.setTextSize(16);
						textViewAux.setBackgroundDrawable(FormKardex.this.getResources().getDrawable(android.R.drawable.editbox_dropdown_light_frame));
						fila.addView(textViewAux);
						
						textViewAux = new TextView(FormKardex.this);
						textViewAux.setText(informeInv.nombre);			
						textViewAux.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
						textViewAux.setTextColor(Color.argb(255,0,0,0));	
						textViewAux.setTextSize(14);
						textViewAux.setBackgroundDrawable(FormKardex.this.getResources().getDrawable(android.R.drawable.editbox_dropdown_light_frame));
						fila.addView(textViewAux);

						table.addView(fila);
					}

				} else {

					Toast.makeText(getApplicationContext(), "Busqueda sin resultados", Toast.LENGTH_SHORT).show();
				}

				if( progressDialog != null )
					progressDialog.dismiss();
			}
		} );
	}
	
	
	public void onClick(View view){
		
		
		Intent intent = new Intent(this,FormKardexDetallado.class);
		intent.putExtra("producto_mostrar", view.getTag().toString());
		startActivity(intent);
		
		
	}
	
	public void OnClickAtras(View view){
		
		finish();
		
	}
	


}
