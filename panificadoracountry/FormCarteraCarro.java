package co.com.panificadoracountry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Vector;

import co.com.BusinessObject.DataBaseBO;
import co.com.BusinessObject.FileBO;
import co.com.DataObject.Cartera;
import co.com.DataObject.Usuario;

public class FormCarteraCarro extends Activity {


    private EditText txtInventario, txtCargue, txtSubtotal, txtConsignaciones, txtCambios, txtDescuentos, txtReteica, txtRetefuente, txtTotal;
    private long mLastClickTime = 0;

    long inventario, cargue, subtotal, consignacion, cambios, descuentos, reteica, retefuente, total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_cartera_carro);

        inicilizarVistasPantalla();

    }


    public void onResume(){

        super.onResume();
        DataBaseBO.ValidarUsuario();
        FileBO.validarCliente( this );
        DataBaseBO.setAppAutoventa();
        inicilizarVistasPantalla();
    }

    private void inicilizarVistasPantalla(){
        txtInventario = ((EditText) findViewById(R.id.txtInventario));
        inventario = DataBaseBO.buscarValorInventario();
        txtInventario.setText(Util.SepararMiles(inventario+""));

        txtCargue = ((EditText) findViewById(R.id.txtCargue));
        cargue = DataBaseBO.buscarValorCargue();
        txtCargue.setText(Util.SepararMiles(cargue+""));

        txtSubtotal = ((EditText) findViewById(R.id.txtSubtotal));
        subtotal = inventario + cargue;
        txtSubtotal.setText(Util.SepararMiles(subtotal + ""));

        txtConsignaciones = ((EditText) findViewById(R.id.txtConsignacion));
        consignacion = DataBaseBO.buscarValorConsignacionCancelada();
        txtConsignaciones.setText(Util.SepararMiles(consignacion + ""));

        txtCambios = ((EditText) findViewById(R.id.txtCambio));
        cambios = DataBaseBO.buscarValorCambios();
        txtCambios.setText(Util.SepararMiles(cambios + ""));

        txtDescuentos = ((EditText) findViewById(R.id.txtDescuentos));
        descuentos = DataBaseBO.buscarValorDescuentos();
        txtDescuentos.setText(Util.SepararMiles(descuentos + ""));

        txtReteica = ((EditText) findViewById(R.id.txtReteica));
        reteica = DataBaseBO.buscarValorReteica();
        txtReteica.setText(Util.SepararMiles(reteica + ""));


        txtRetefuente = ((EditText) findViewById(R.id.txtRetefuente));
        retefuente = DataBaseBO.buscarValorRetefuente();
        txtRetefuente.setText(Util.SepararMiles(retefuente + ""));


        txtTotal = ((EditText) findViewById(R.id.txtTotal));
        total = subtotal - (consignacion + cambios + descuentos + reteica + retefuente);
        txtTotal.setText(Util.SepararMiles(total + ""));
    }

    public void OnClickFormNoCompra(View view) {

        switch (view.getId()) {

            case R.id.btnAceptar:
                GuardarMotivoNoCompra();
                break;

        }
    }


    public void GuardarMotivoNoCompra() {


        /*retardo de 1500ms para evitar eventos de doble click.
         * esto significa que solo se puede hacer click cada 1500ms.
         * es decir despues de presionar el boton, se debe esperar que transcurran
         * 1500ms para que se capture un nuevo evento.*/
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1500){
            return;
        }

        mLastClickTime = SystemClock.elapsedRealtime();

        // solicitar confirmacion para terminar el pedido.
        AlertDialog alertDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false).setPositiveButton("Si", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                Usuario usuario = DataBaseBO.ObtenerUsuario();

                boolean pedidoCompleto = DataBaseBO.guardarCarteraCarro(inventario, cargue, subtotal, consignacion, cambios, descuentos,reteica, retefuente,total, usuario);

                if(pedidoCompleto){
                    Log.println(Log.ASSERT,"guardarCarteraCarro","Guardo exitosamente Cartera carro");
                }else{
                    Log.println(Log.ASSERT,"guardarCarteraCarro"," NO Guardo Cartera carro");
                }
                dialog.cancel();
                finish();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                // handlerFinish.sendEmptyMessage(0);
                // guardar reporte, pero no imprimir
                // imprimirTirilla(false);
                // progressDialog =
                // ProgressDialog.show(FormPedidosAutoventa.this, "",
                // "Enviando Informacion...", true);
                // progressDialog.show();
                // progressDialog.setIndeterminate(true);
                // progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_icon_drawable_animation));
                //
                // Sync sync = new Sync(FormPedidosAutoventa.this,
                // Const.ENVIAR_PEDIDO);
                // sync.start();
                dialog.cancel();

            }
        });
        alertDialog = builder.create();
        alertDialog.setMessage("Desea finalizar?");
        alertDialog.show();

    }

}
