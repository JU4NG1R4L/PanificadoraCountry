/**
 *
 */
package co.com.panificadoracountry;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.SQLException;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import co.com.BusinessObject.DataBaseBO;
import co.com.BusinessObject.FileBO;
import co.com.BusinessObject.PrinterBO;
import co.com.Conexion.Sync;
import co.com.DataObject.Cliente;
import co.com.DataObject.ControlImpresion;
import co.com.DataObject.DetalleImprimir;
import co.com.DataObject.DetalleProducto;
import co.com.DataObject.Encabezado;
import co.com.DataObject.Inventario;
import co.com.DataObject.ItemListView;
import co.com.DataObject.Producto;
import co.com.DataObject.Usuario;
import co.com.preferences.PreferenceNumeroFactura;
import co.com.woosim.printer.WoosimR240;

/**
 *
 * Modulo de pedidos para autoventa , team mexico.
 *
 * @author JICZ
 *
 */
public class FormPedidosAutoventa extends Activity implements Sincronizador {
  /**
   * retardo para evitar el doble click en los botones
   */
  private long mLastClickTime = 0;
  /**
   * Constantes para ingresar en las columnas numericas de la tabla encabezado
   *
   */
  private final static int TIPO_TRANSACCION = 2;
  private final static int ENTREGADO = 0;
  private final static int FACTURA = 0;
  private final static int TIPO_VENTA_PEDIDO = 1;
  private final static int TIPO_DOCUMENTO = 1;
  private final static int ESTADO_PEDIDO = 1;
  private final static int ESTADO_PAGO = 1;
  /**
   * Referencia a la impresora WSP-R240 WOOSIM.
   */
  private WoosimR240 wR240 = null;
  /**
   * dialog progreso, usado para informar al usuario de alguna operacion.
   */
  private ProgressDialog progressDialog;
  /**
   * atributo para conservar el monto del descuento.
   */
  private String montoDecuento = "";
  /**
   * atributo para conservar el monto total del pedido.
   */
  private String precioTotal = "";
  /**
   * constante define el formato de las fechas usadas en el programa
   */
  private static final String FORMATO_FECHA = "yyyy-MM-dd";
  /**
   * atributo, conserva el cliente que ha sido seleccionado para realizar el
   * pedido.
   */
  private Cliente cliente;
  /**
   * lista que contiene todos los productos que son ofrecidos en autoventa.
   */
  private static List<Producto> listaProductos;
  /**
   * vector que contiene los productos que han sido pedidos por el usuario
   */
  private List<Producto> productosPedido;
  /**
   * vector que contiene los productos con sus respectivos lotes
   */
  Vector<Producto> listaProductoNew;
  /**
   * atributo, usado para identificar el tope maximo de productos que hay
   * disponibles para mostrar
   */
  int cantidadProductos;
  /**
   * atributo, usado para identificar la posicion actual del producto que se
   * esta mostrando en pantalla
   */
  int posicionProductoActual;
  /**
   * contiene la cantidad ingresada por el usuario
   */
  EditText editTextCantidadProductoAv;
  /**
   * codigo producto
   */
  private TextView textViewCodigoProductoAv;
  /**
   * descripcion del producto o nombre del producto.
   */
  private TextView textViewDescripcionProductoAv;
  /**
   * Precio del producto neto, sin impuestos, ni descuentos.
   */
  private TextView textViewPrecioProductoAv;
  /**
   * porcentaje del iva cobrado en un producto.
   */
  private TextView textViewIvaProductoAv;
  /**
   * descuento con el que cuenta el producto. depende de la cantidad, el
   * cliente y el grupo de producto.
   */
  private TextView textViewDescuentoProductoAv;
  /**
   * precio al que se cobrara el producto, incluye impuestos y descuentos.
   */
  private TextView textViewPrecioConIvaProductoAv;
  /**
   * mostrar informacion de la posicion del panel de navegacion o contador de
   * posicion.
   */
  private TextView TextViewPosicionNavegador;
  /**
   * atributo, variable que permite identificar que una cantidad no sea la
   * misma que la anterior
   */
  private String cantidadAnterior;
  /**
   * identificador unico de un pedido. se forma por el siguiente formato. - AC
   * a�o mes dia codVendedor 4digitos los 4 digitos conforman un consecutivo.
   */
  private static String numeroDocPedidoAv = "";
  /**
   * Dialog, para ingresar fechas de cobro y de entrega de pedido, ademas de
   * agregar observaciones.
   */
  private Dialog dialogTerminarPedidoAv;
  /**
   * Dialog, listar el resumen de los productos ingresados hasta el momento.
   */
  private Dialog dialogResumenPedidoAv;
  /**
   * Dialog, para mostrar los productos encontrados segurn criterio de
   * busqueda
   */
  private Dialog dialogMostrarProductoParametro;
  /**
   * mostrar calendario para ingresar fecha de entrega de pedido
   */
  private ImageButton fechaEntrega;
  /**
   * mostrar calendario para ingresar fecha de cobro
   */
  private ImageButton fechaCobro;
  /**
   * fecha de entrega ingresada por el usuario
   */
  private EditText editTextFechaEntregaPedidoAv;
  /**
   * fecha de cobro ingresada por el usuario
   */
  private EditText editTextFechaCobroPedidoAv;
  /**
   * observacion ingresada por el usuario
   */
  private EditText editTextObservacionesPedidoAv;
  /**
   * string que conserva la fecha de cobro
   */
  private String stringFechaCobro = "";
  /**
   * string que conserva la fecha de entrega
   */
  private String stringFechaEntrega = "";
  /**
   * conserva las obvservaciones ingresadas por el usuario.
   */
  private String observaciones;
  /**
   * listview que contiene los productos pedidos al momento.
   */
  private ListView listaPedido;
  /**
   * Orden de Compra ingresada por el usuario
   */
  private EditText editTextOrdenCompra;
  /**
   * conserva las Orden Compra ingresadas por el usuario.
   */
  private String ordenCompra;
  private CheckBox checkBoxClientePago;
  /**
   * codigo producto
   */
  private TextView textViewInventarioProductoAv;
  private Dialog dialogImprimirFacturaPedido;
  /* definicion de objetos usados para imprimir */
  /**
   * BluetoothAdapter para la conexion.
   */
  private BluetoothAdapter bluetoothAdapter = null;
  public final static String CONFIG_IMPRESORA = "PRINTER";
  public final static String MAC_IMPRESORA = "MAC";
  public final static String LABEL_IMPRESORA = "LABEL";
  /**
   * Atributos para la funcion de impresora
   */
  protected int _splashTime = 6000;
  Thread splashTread;
  String mensaje;
  Encabezado encabezado;
  Context context;
  
  boolean esClienteDeCredito = false;
  float disponible = 0;
  String codSaldoCancelado = "";
  
  /**
   * Se crea la variable para saber si se encuentra dentro del limite
   */
  
  private int ESVALIDO;

  //Manejo de coordenadas
  private double latitudActual, longitudActual;
  /*
   * (non-Javadoc)
   *
   * @see android.app.Activity#onCreate(android.os.Bundle)
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Remove title bar
    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    // Remove notification bar
    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.form_pedidos_autoventa);
    esClienteDeCredito = this.getIntent().getBooleanExtra("esClienteDeCredito", false);
    disponible = this.getIntent().getFloatExtra("disponible", 0);
    ESVALIDO = this.getIntent().getIntExtra("ESVALIDO", 0);
    encabezado = new Encabezado();
    DataBaseBO.ValidarUsuario();
    FileBO.validarCliente(this);
    listaPedido = (ListView) findViewById(R.id.listViewProductosPedidosAv);
    listaPedido.setOnItemClickListener(listenerModificaciones);
    // inicializar productos pedidos como un vector vacio.
    productosPedido = new ArrayList<Producto>();
    // impide mostrar el teclado cuando inicia la actividad con el focus en
    // el editText de codigo.
    Util.cerrarTeclado(this);
    // inicializar dialogTerminarPedidoAv
    dialogTerminarPedidoAv = null;
    // inicializar doalogMostrarProductoParamtro
    dialogMostrarProductoParametro = null;
    // cargar informacion del cliente y tipo de cliente, seleccionado.
    cliente = Main.cliente;
    // empezar siempre a mostrar el producto de la posicion 0 de la lista.
    posicionProductoActual = 0;
    // inicializar el string anterior como vacio
    cantidadAnterior = "";
    // cargar la lista de productos ofertados en autoventa.
    listaProductos = new ArrayList<Producto>();
    // Carga la lista de producto distribuido en diferentes lotes
    listaProductoNew = new Vector<Producto>();
    // Carga opciones de busqueda
    CargarOpcionesBusqueda();
    if (productosPedido.isEmpty()) {
      String aux = "-1";
      productosPedido = DataBaseBO.obtenerListaDetalleVenta(aux);
      devolverTotalCantidadInventarioGeneral();
      DataBaseBO.cancelarPedidoAv();
      if (productosPedido != null) {
        productosPedido.clear();
      }
    }
    if (DataBaseBO.cargarProductosDisponiblesAutoventa(listaProductos, cliente.listaPrecio)) {
      cantidadProductos = listaProductos.size();
      inicilizarVistasPantalla();
      try {
        mostrarProducto();
      } catch (Exception e) {
      }
    }
    // DataBaseBO.crearTriggerFacturacion(Main.usuario.codigoVendedor);
    /*
     * verificar que la lista de productos pedidos este nula, si es asi
     * crear un objeto para que se puedan agregar productos. puede estar
     * nula cuando se cierre la aplicacion
     */
    // if(productosPedido == null){
    // productosPedido = new ArrayList<Producto>();
    // agregarProductoListView();
    // }
    context = FormPedidosAutoventa.this;
    // crear numero de factura para el pedido.
    generarNumeroFacturacion();

    //Capturar las coordenadas cuando se ejecute el activity
    validarUbicacion();
  }
  
  private void eliminarRegistrosEnTemp() {
    devolverTotalCantidadInventarioGeneral();
    DataBaseBO.cancelarPedidoAv();
    productosPedido.clear();
  }
  
  /**
   * validar y generar el numero de factura para este pedido.
   */
  private void generarNumeroFacturacion() {
    try {
      /*
       * Cargar el numero de factura que podria ser usado en este pedido.
       * El numero de factura de venta solo es asignado cuando el pedido
       * finaliza de forma correcta.mientras tanto sera conservado en un
       * archivo de preferencias
       */
      String numeroFactura = null;
      String numeroFacturaAnterior = PreferenceNumeroFactura.getNumeroAnterior(context);
      int verificado = Integer.MIN_VALUE;
      int numeroFacturaInt = 0;
      String titulo = "Error Numero Factura.";
      String mensaje = "";
      do {
        numeroFactura = DataBaseBO.obtenerNumeroFactura(Main.usuario.codigoVendedor);
        if (numeroFactura == null) {
          /*
           * No se puede generar numero de facturacion, comuniquese
           * con el coordinador para verificar vigencia de la
           * resolucion.
           */
          mensaje = "Comuniquese con el coordinador para verificar vigencia de la resolucion.";
          break;
        } else {
          numeroFacturaInt = Integer.parseInt(numeroFactura);
          verificado = DataBaseBO.verificarNumeroFactura(Main.usuario.codigoVendedor, numeroFacturaInt, numeroFacturaAnterior);
          if (verificado == Const.FACTURA_REPETIDA) {
            /* incrementar consecutivo. */
            DataBaseBO.incrementarNumeroFactura(Main.usuario.codigoVendedor);
            continue;
          } else if (verificado == Const.FACTURA_CONTEO_FALLO) {
            mensaje = "Por favor sincronice informacion, luego intente nuevamente realizar el pedido.";
            break;
          } else if (verificado == Const.FACTURA_EXCEDIO_LIMITE) {
            /*
             * se supero el limite de numeracion permitido por la
             * resolucion
             */
            mensaje = "Comuniquese con el coordinador para verificar limites de numeracion en la resolucion.";
            break;
          }
        }
      }
      while (verificado != Const.FACTURA_OK);
      /* guardar numero de factura valido */
      if (verificado == Const.FACTURA_OK) {
        PreferenceNumeroFactura.guardarNumeroActual(context, numeroFactura);
      } else {
        /* informar novedad al usuario */
        mostrarAlertDialog(titulo, mensaje);
      }
    } catch (NumberFormatException e) {
      PreferenceNumeroFactura.vaciarPreferences(context);
      String titulo = "Error Numero Factura.";
      mensaje = "Por favor sincronice informacion, luego intente nuevamente realizar el pedido.";
      mostrarAlertDialog(titulo, mensaje);
    }
  }
  
  /*
   * (non-Javadoc)
   *
   * @see android.app.Activity#onResume()
   */
  @Override
  protected void onResume() {
    super.onResume();
    try {
      DataBaseBO.ValidarUsuario();
      FileBO.validarCliente(this);
      DataBaseBO.setAppAutoventa();
    } catch (Exception e) {
    }
  }
  
  /*
   * (non-Javadoc)
   *
   * @see android.app.Activity#onStop()
   */
  @Override
  protected void onStop() {
    // TODO Auto-generated method stub
    super.onStop();
  }
  
  /**
   * Metodo que hace la asignacion de referencias para insertar datos en las
   * vistas de la pantalla. ademas se inician los eventos de los botones del
   * formulario, apuntando a sus respectivos metodos onCLick
   *
   * @by JICZ
   */
  private void inicilizarVistasPantalla() {
    textViewCodigoProductoAv = ((TextView) findViewById(R.id.textViewCodigoProductoAv));
    textViewPrecioProductoAv = ((TextView) findViewById(R.id.textViewPrecioProductoAv));
    textViewIvaProductoAv = ((TextView) findViewById(R.id.textViewIvaProductoAv));
    textViewDescuentoProductoAv = ((TextView) findViewById(R.id.textViewDescuentoProductoAv));
    textViewPrecioConIvaProductoAv = ((TextView) findViewById(R.id.textViewPrecioConIvaProductoAv));
    textViewDescripcionProductoAv = ((TextView) findViewById(R.id.textViewDescripcionProductoAv));
    TextViewPosicionNavegador = ((TextView) findViewById(R.id.TextViewPosicionNavegador));
    editTextCantidadProductoAv = ((EditText) findViewById(R.id.editTextCantidadProductoAv));
    textViewInventarioProductoAv = ((TextView) findViewById(R.id.textViewInventario));
    // definir un valor inicial para el precio total
    textViewPrecioConIvaProductoAv.setText("0");
    // definir un valor inicial para el descuento
    textViewDescuentoProductoAv.setText("0");
    /*
     * Se captura el evento del boton de retroceso del navegador de
     * productos
     */
    ((Button) findViewById(R.id.buttonNavegadorAtrasAv)).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        OnClickNavegadorAtrasAv(v);
      }
    });
    /* Se captura el evento del boton para ir al primer producto */
    ((Button) findViewById(R.id.buttonPrimerProducto)).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        OnClickPrimerProductoAv(v);
      }
    });
    /*
     * Se captura el evento del boton de adelantar del navegador de
     * productos
     */
    ((Button) findViewById(R.id.buttonNavegadorSiguienteAv)).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        OnClickNavegadorAdelanteAv(v);
      }
    });
    /* Se captura el evento del boton para ir al ultimo producto */
    ((Button) findViewById(R.id.buttonUltimoProducto)).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        OnClickUltimoProductoAv(v);
      }
    });
    /*
     * Se captura el evento del boton para agregar un producto al carrito de
     * pedidos
     */
    ((Button) findViewById(R.id.buttonAgregarProducto)).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        /*
         * retardo de 1500ms para evitar eventos de doble click. esto
         * significa que solo se puede hacer click cada 1500ms. es decir
         * despues de presionar el boton, se debe esperar que
         * transcurran 1500ms para que se capture un nuevo evento.
         */
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
          return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        ocultarTeclado(editTextCantidadProductoAv);
        OnClickAgregarProductoAv(v);
      }
    });
    /* Se captura el evento del boton para terminar un pedido */
    ((Button) findViewById(R.id.buttonTerminarPedido)).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        /*
         * retardo de 1500ms para evitar eventos de doble click. esto
         * significa que solo se puede hacer click cada 1500ms. es decir
         * despues de presionar el boton, se debe esperar que
         * transcurran 1500ms para que se capture un nuevo evento.
         */
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
          return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        if (productosPedido.isEmpty()) {
          mostrarToast("El Pedido esta vacio!\nPor favor ingrese como minimo un producto.");
        } else {
          mostrarDialogResumenPedido();
        }
      }
    });
    /*
     * Se captura el evento del boton para buscar un producto por parametro
     * de busqueda
     */
    ((Button) findViewById(R.id.buttonBuscarProductoPedidoAv)).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        // -- buscar productos que contengan el parametro
        // conocido por el cliente.
        buscarProductoParametroConocido();
      }
    });
    /*
     * Captura de evento del editTex para capturar la cantidad de producto
     * ingresada por el usuario esto permite recalcular los precios y los
     * descuentos de un producto dado.
     */
    editTextCantidadProductoAv.addTextChangedListener(new TextWatcher() {
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        onTextChangedCantidad(s);
      }
      
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }
      
      @Override
      public void afterTextChanged(Editable s) {
      }
    });
    /* Se captura el evento del boton para terminar un pedido */
    ((Button) findViewById(R.id.buttonCancelarPedidoRealizado)).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        /*
         * retardo de 1500ms para evitar eventos de doble click. esto
         * significa que solo se puede hacer click cada 1500ms. es decir
         * despues de presionar el boton, se debe esperar que
         * transcurran 1500ms para que se capture un nuevo evento.
         */
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
          return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        salir();
      }
    });
  }
  
  /**
   *
   */
  protected void mostrarDialogResumenPedido() {
    dialogResumenPedidoAv = null;
    if (dialogResumenPedidoAv == null) {
      dialogResumenPedidoAv = new Dialog(this);
      dialogResumenPedidoAv.setContentView(R.layout.dialog_resumen_pedido_autoventa);
      dialogResumenPedidoAv.setTitle("Resumen Pedido Autoventa");
      // mostrar el dialog en pantalla
      dialogResumenPedidoAv.setCancelable(true);
      dialogResumenPedidoAv.show();
    } else {
      // si no es nulo entonces limpiar el texto y mostrar de nuevo.
      editTextFechaEntregaPedidoAv.setText("");
      editTextFechaCobroPedidoAv.setText("");
      dialogResumenPedidoAv.show();
    }
    // captura evento del boton de cancelar para salir del dialog.
    ((Button) dialogResumenPedidoAv.findViewById(R.id.buttonAceptarDialogResumenPedidoAv)).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        // TODO Auto-generated method stub
        mostrarDialogTerminarPedidoAv();
        dialogResumenPedidoAv.dismiss();
      }
    });
    // captura evento del boton de cancelar para salir del dialog.
    ((Button) dialogResumenPedidoAv.findViewById(R.id.buttonCancelarDialogResumenPedidoAv)).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        // TODO Auto-generated method stub
        dialogResumenPedidoAv.dismiss();
      }
    });
    llenarListaResumen();
  }
  
  /**
   * Metodo que llena la lista del Resumen del pedido con los productos
   * pedidos
   */
  private void llenarListaResumen() {
    // TODO Auto-generated method stub
    Vector<ItemListView> datosPedido = new Vector<ItemListView>();
    // recorrer los productos pedidos para crear la lista de items
    for (Producto producto : productosPedido) {
      ItemListView itemListView = new ItemListView();
      /* se define la informacion que sera mostrada */
      itemListView.titulo = producto.codigo + " - " + producto.descripcion;
      itemListView.subTitulo = "Cantidad: " + producto.cantidadPedida + "\n" + "Precio: " + Util.SepararMiles("" + producto.precio) + "\n" + "Iva: " + producto.iva;
      // agregar item a datosPedido
      datosPedido.addElement(itemListView);
    }
    // se establece la lista de items en base al vector creado con la lista
    // de productos.
    ItemListView[] listaItems = new ItemListView[datosPedido.size()];
    datosPedido.copyInto(listaItems);
    // configuracion del listViewAdapter para que muestre la informacion
    // obtenida
    ListViewAdapter adapter = new ListViewAdapter(this, listaItems, R.drawable.prod, 0xff7D0000);
    ListView listaResumen = (ListView) dialogResumenPedidoAv.findViewById(R.id.listResumenPedido);
    listaResumen.setAdapter(adapter);
  }
  
  /**
   * Metodo que permite al usuario buscar un producto segun un parametro de
   * busqueda. el parametro puede ser: -parte del codigo de producto -parte
   * del nombre del producto. se captura una lista de los posibles productos
   * que el usuario necesita. el escoje el producto que requiere.
   *
   * @by JICZ.
   */
  protected void buscarProductoParametroConocido() {
    FileBO.validarCliente(this);
    cliente = Main.cliente;
    // obtener el parametro de busqueda de un producto.
    String parametroBusqueda = textViewCodigoProductoAv.getText().toString().trim();
    // lista que contiene los posibles prodcutos que seran mostrados al
    // usuario para seleccionar el adecuado.
    List<Producto> productosPosibles = new ArrayList<Producto>();
    if (!parametroBusqueda.equals("") && !parametroBusqueda.equals(" ")) {
      String where = "";
      Spinner cbBusquedaProducto = (Spinner) findViewById(R.id.cbBusquedaProducto);
      String opBusqueda = cbBusquedaProducto.getSelectedItem().toString();
      if (opBusqueda.equals(Const.POR_NOMBRE)) {
        String parametroMayus = capitalizarCharBusqueda(parametroBusqueda);
        where = " where nombre  like '%" + parametroMayus + "%'  AND CodigoLista = '" + cliente.listaPrecio + "' ";
      } else if (opBusqueda.equals(Const.POR_CODIGO_)) {
        where = " where cod_producto  like '%" + parametroBusqueda + "%' AND CodigoLista = '" + cliente.listaPrecio + "' ";
      }
      boolean encontrados = DataBaseBO.buscarProductoPorParametroConocido(where, productosPosibles);
      if (encontrados) {
        mostrarListaProductoEncontrados(productosPosibles);
      } else {
        // mostrar toast informando que no se han encontrado productos
        mostrarToast("No se encontraron productos!\nIntente de nuevo");
      }
    } else {
      // mostrar toast informando parametro de busqueda no valido.
      mostrarToast("Parametro de busqueda no valido!\nPor favor ingrese el codigo o la descripcion.");
    }
  }
  
  private String capitalizarCharBusqueda(String line) {
    return Character.toUpperCase(line.charAt(0)) + line.substring(1);
  }
  
  public void CargarOpcionesBusqueda() {
    Spinner cbBusquedaProducto = (Spinner) findViewById(R.id.cbBusquedaProducto);
    String[] items = new String[]{Const.POR_CODIGO_, Const.POR_NOMBRE};
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    cbBusquedaProducto.setAdapter(adapter);
    cbBusquedaProducto.setOnItemSelectedListener(new OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View selectedItemView, int position, long id) {
        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
        String opSel = ((Spinner) findViewById(R.id.cbBusquedaProducto)).getItemAtPosition(position).toString();
        if (opSel.equals(Const.POR_NOMBRE)) {
          OpcionSeleccionada("Ingrese referencia del producto:", InputType.TYPE_CLASS_TEXT);
        } else if (opSel.equals(Const.POR_CODIGO_)) {
          OpcionSeleccionada("Ingrese codigo del producto:", InputType.TYPE_CLASS_TEXT);
        }
      }
      
      @Override
      public void onNothingSelected(AdapterView<?> parentView) {
      }
    });
  }
  
  public void OpcionSeleccionada(String label, int inputType) {
    TextView lblBusquedaProducto = (TextView) findViewById(R.id.lblBusquedaProducto);
    EditText txtBusquedaProducto = (EditText) findViewById(R.id.textViewCodigoProductoAv);
    txtBusquedaProducto.setText("");
    lblBusquedaProducto.setText(label);
    txtBusquedaProducto.setInputType(inputType);
  }
  
  /**
   * Metodo que muestra en pantalla la lista de productos encontrados de
   * acuerdo con el parametro ingresado por el usuario.
   *
   * @param productosPosibles
   * @by JICZ
   */
  private void mostrarListaProductoEncontrados(final List<Producto> productosPosibles) {
    // verificar que el dialog sea nulo, se iniciaria uno nuevo cada vez.
    if (dialogMostrarProductoParametro != null) {
      dialogMostrarProductoParametro = null;
    }
    dialogMostrarProductoParametro = new Dialog(this);
    dialogMostrarProductoParametro.setContentView(R.layout.dialog_producto_por_parametro);
    dialogMostrarProductoParametro.setTitle("Productos Encontrados");
    // vector para conservar los productos que seran mostrados como items en
    // la pantalla.
    Vector<ItemListView> productosItem = new Vector<ItemListView>();
    // recorrer los productos encontrados para crear la lista de items
    for (Producto producto : productosPosibles) {
      ItemListView itemListView = new ItemListView();
      /* se define la informacion que sera mostrada */
      itemListView.titulo = producto.codigo;
      itemListView.subTitulo = "- " + producto.descripcion;
      // agregar item a productosItem
      productosItem.addElement(itemListView);
    }
    // se establece la lista de items en base al vector creado con la lista
    // de productos.
    ItemListView[] listaItems = new ItemListView[productosItem.size()];
    productosItem.copyInto(listaItems);
    // configuracion del listViewAdapter para que muestre la informacion
    // obtenida
    ListViewAdapter adapter = new ListViewAdapter(this, listaItems, R.drawable.prod, 0);
    ListView listaPedido = (ListView) dialogMostrarProductoParametro.findViewById(R.id.listViewProductosPosibles);
    listaPedido.setAdapter(adapter);
    listaPedido.setTextFilterEnabled(true);
    /*
     * Implementacion del metodo que captura un click en algun item
     * seleccionado por el usuario.
     */
    listaPedido.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> arg0, View view, int posicion, long id) {
        try {
          // captura del producto seleccionado
          Producto p = productosPosibles.get(posicion);
          // buscar producto en la lista listaProductos
          for (Producto producto : listaProductos) {
            // comparar los codigos de productos sean iguales.
            if (p.codigo.equals(producto.codigo)) {
              posicionProductoActual = listaProductos.indexOf(producto);
              break;
            }
          }
          // mostrar el producto seleccionado por el usuario.
          mostrarProducto();
          // cerrar el dialog
          dialogMostrarProductoParametro.dismiss();
        } catch (Exception e) {
          mostrarToast("fallo la busqueda de productos! cierre e intente de nuevo.");
        }
      }
    });
    // mostrar el dialog en pantalla
    dialogMostrarProductoParametro.setCancelable(true);
    dialogMostrarProductoParametro.show();
  }
  
  /**
   * Metodo para mostrar infromacion del primer producto en la lista, el
   * usuario inicia con este producto, pero puede navegar por todos los demas.
   *
   * @throws Exception
   *             , se lanza excepcion en caso de que la lista de productos
   *             este vacia o nula.
   * @by JICZ
   */
  private void mostrarProducto() throws Exception {
    if (listaProductos != null && !(listaProductos.isEmpty())) {
      Producto producto = listaProductos.get(posicionProductoActual);
      String cantidad = editTextCantidadProductoAv.getText().toString().trim();
      // FileBO.validarCliente(this);
      // cliente = Main.cliente;
      // calcular descuentos y nuevo precio. si no hay descuentos, se
      // define solo su precio segun el impuesto.
      // DataBaseBO.calcularDescuentoProductoAutoventa(producto, cantidad,
      // cliente.codLista);
      // cambiar el producto anterior por el nuevo producto con descuentos
      // y precio segun la cantidad
      listaProductos.set(posicionProductoActual, producto);
      // mostrar los datos obtenidos del producto en las vistas
      TextView lblBusquedaProducto = (TextView) findViewById(R.id.lblBusquedaProducto);
      lblBusquedaProducto.setText("Ingrese codigo del producto:");
      Spinner spnBusqueda = (Spinner) findViewById(R.id.cbBusquedaProducto);
      spnBusqueda.setSelection(0);
      float descuento = producto.descuento * 100;
      textViewCodigoProductoAv.setText(producto.codigo);
      textViewDescripcionProductoAv.setText(producto.descripcion);
      textViewPrecioProductoAv.setText(Util.SepararMiles(Util.Redondear("" + producto.precio, 0)));
      textViewIvaProductoAv.setText(Util.Redondear("" + producto.iva, 0));
      textViewInventarioProductoAv.setText(Util.Redondear("" + producto.cantidadInv, 0));
      textViewDescuentoProductoAv.setText(Util.Redondear("" + descuento, 0));
      textViewPrecioConIvaProductoAv.setText(Util.SepararMiles(Util.Redondear(String.valueOf((producto.precio) * (1 + (producto.iva / 100))), 0)));
      System.out.println("InfoProducto:\n codProd:" + producto.codigo + "\n Descripcion: " + producto.descripcion + "\n Precio: " + producto.precio + "\n Iva: " + producto.iva + "\n Descuento: " + producto.descuento + "\n Inventario: " + producto.cantidadInv);
      // mostrar informacion del navegador de productos.
      TextViewPosicionNavegador.setText("Producto " + (posicionProductoActual + 1) + "- de " + cantidadProductos);
    } else {
      throw new Exception("No se logro cargar la lista de productos, por favor iniciar de nuevo");
    }
  }
  
  /**
   * metodo del evento del boton atrasar. permite navegar al anterior producto
   * en la lista de productos disponibles en autoventa
   *
   * @param v
   */
  protected void OnClickNavegadorAtrasAv(View v) {
    try {
      posicionProductoActual--;
      if (posicionProductoActual < 0) {
        posicionProductoActual = (cantidadProductos - 1);
      }
      mostrarProducto();
      editTextCantidadProductoAv.setText("");
    } catch (Exception e) {
      Log.e("FormPedidosAutoventa OnClickNavegadorAtrasAv()", "Error: " + e.getMessage());
    }
  }
  
  /**
   * metodo del evento del boton adelantar. permite navegar al siguiente
   * producto en la lista de productos disponibles en autoventa
   *
   * @param v
   */
  protected void OnClickNavegadorAdelanteAv(View v) {
    try {
      posicionProductoActual++;
      if (posicionProductoActual > (cantidadProductos - 1)) {
        posicionProductoActual = 0;
      }
      mostrarProducto();
      editTextCantidadProductoAv.setText("");
    } catch (Exception e) {
      Log.e("FormPedidosAutoventa OnClickNavegadorAdelanteAv()", "Error: " + e.getMessage());
    }
  }
  
  /**
   * metodo del evento del boton primer producto. permite ir al primer
   * producto disponible en la lista de productos disponibles en autoventa.
   *
   * @param v
   */
  protected void OnClickPrimerProductoAv(View v) {
    try {
      posicionProductoActual = 0;
      mostrarProducto();
      editTextCantidadProductoAv.setText("");
    } catch (Exception e) {
      Log.e("FormPedidosAutoventa OnClickPrimerProductoAv()", "Error: " + e.getMessage());
    }
  }
  
  /**
   * metodo del evento del boton ultimo producto. permite ir al ultimo
   * producto disponible en la lista de productos disponibles en autoventa.
   *
   * @param v
   */
  protected void OnClickUltimoProductoAv(View v) {
    try {
      posicionProductoActual = (cantidadProductos - 1);
      mostrarProducto();
      editTextCantidadProductoAv.setText("");
    } catch (Exception e) {
      Log.e("FormPedidosAutoventa OnClickUltimoProductoAv()", "Error: " + e.getMessage());
    }
  }
  
  /**
   * evento de cambio en el texto del editText de la cantidad de productos,
   * requeridos por el usuario. se recalcula el precio debido a los descuentos
   * disponibles por cantidades de pedido y de grupos del producto solicitado.
   *
   * @param s
   */
  protected void onTextChangedCantidad(CharSequence s) {
    try {
      // cantidad ingresada
      String cantidad = s.toString().trim();
      if (!(cantidad.length() == 0) && !cantidad.equals(" ") && !cantidadAnterior.equals(cantidad)) {
        // nueva asignacion de cantidadAnterior
        cantidadAnterior = cantidad;
        mostrarProducto();
      }
    } catch (Exception e) {
      Log.e("FormPedidosAutoventa onTextChangedCantidad()", "Error: " + e.getMessage());
    }
  }
  
  /**
   * metodo que permite agregar un producto a la lista de pedidos. se almacena
   * en base de datos y se muestra en la vista de productos pedidos.
   *
   * @by JICZ
   * @param v
   */
  protected void OnClickAgregarProductoAv(View v) {
    Producto prod = listaProductos.get(posicionProductoActual);
    // verificar la cantidad solicitada del producto.
    String cantidad = editTextCantidadProductoAv.getText().toString().trim();
    // verificar que la cantidad es un numero valido diferente de cero
    if (cantidad.length() == 0 || !Util.isNumberInteger(cantidad) || Float.valueOf(cantidad) <= 0 || Float.valueOf(cantidad) > prod.cantidadInv) {
      mostrarToast("Por favor ingrese una cantidad valida!\nDebe ser un valor mayor a cero y menos de " + Util.Redondear("" + prod.cantidadInv, 0) + " unidades");
      editTextCantidadProductoAv.requestFocus();
    } else {
      // capturar el producto seleccionado
      Producto producto = listaProductos.get(posicionProductoActual);
      // verificar que el producto no este agregado al pedido
      // anteriormente
      if (productosPedido.contains(producto)) {
        // informar que del producto ya se ha solicitado una cantidad
        // igual a (producto.cantidad):
        // -- informar que el producto ya esta en la lista de productos
        // pedidos
        alertDialogCambiosProductoPedidoAv(producto);
      } else {
        System.out.println(cantidad + "  " + producto.cantidadInv);
        // if(Integer.parseInt(cantidad) <= producto.cantidadInv){
        // agregar la cantidad
        producto.cantidadPedida = Integer.parseInt(cantidad);
        int cantidadPedidaInicial = Integer.parseInt(cantidad);
        // se actualiza la cantidad de producto pedida.
        listaProductos.set(posicionProductoActual, producto);
        // verificar que el producto sea agregado y mostrar toast
        // informativo de producto agregado
        // intentar guardar producto en la tabla Detalle_av de la
        // base de datos Temp.db y DataBase.db
        try {
          Usuario usuario = DataBaseBO.ObtenerUsuario();
          /*
           * for (Inventario i : producto.getListaInventario()) {
           *
           * // if(producto.cantidadPedida >= i.cantidad && i.cantidad
           * > 0){ // // Producto p = (Producto) producto.clone(); //
           * p.cantidadPedida = i.cantidad; // p.lote = i.lote; //
           * p.fechaVencimiento = i.fechaVencimiento; //
           * p.fechaFabricacion = i.fechaFabricacion; // // agregar el
           * producto al detalle detalle //
           * DataBaseBO.agregarProductoDetalleAv
           * (p,usuario.canalVenta); // descontar la cantidad del lote
           * viejo // producto.cantidadPedida -= i.cantidad; // } //
           * else }
           */
          /*
           * calcular los descuentos aplicados por el producto
           * agregado.
           */
          //
          int cantidadTotal = DataBaseBO.obtenerCantTotalLotes(producto.codigo);
          if (cantidadTotal >= producto.cantidadPedida) {
            System.out.println("Entro a ++++++++++++++++" + producto.cantidadPedida);
            // Producto p = producto;
            Vector<Producto> productoAguardar = validarCantidadPorLotes(producto);
            System.out.println("Cantidad de productos: " + productoAguardar.size());
            // p.lote = i.lote;
            // p.fechaVencimiento = i.fechaVencimiento;
            // p.fechaFabricacion = i.fechaFabricacion;
            // System.out.println("Lote de producto: "+p.lote);
            int count = 0;
            for (Producto productox : productoAguardar) {
              System.out.println("............ " + productox.cantidadPedida + "###########" + productox.lote);
              /* agregar el producto al detalle detalle */
              boolean state = DataBaseBO.agregarProductoDetalleAv(productox, usuario.canalVenta);
              if (state) {
                count++;
              }
            }
            productosPedido.add(producto);
            /* descontar la cantidad del lote */
            // producto.cantidadPedida -= p.cantidadPedida;
            if (count == productoAguardar.size()) {
              listaProductoNew.removeAll(productoAguardar);
              if (aplicarDescuentos(producto)) {
                /* asignar la cantidad pedida inicialmente */
                producto.cantidadPedida = cantidadPedidaInicial;
                // producto al listView inferior de la pantalla,
                // para mostrar al usuario.
                agregarProductoListView();
                // mostrar informacion al usuario.
                mostrarToast("El producto: " + producto.descripcion + "\nHa sido agregado!");
                // inicializar valores de cantidad
                editTextCantidadProductoAv.setText("");
                editTextCantidadProductoAv.requestFocus();
                if (producto.descuentoPorProducto > 0) {
                  String mensaje = "";
                  mensaje = "Apreciad@ usuario, el presente producto tiene las siguientes observaciones de descuentos :" + "\n\n Vigencia: " + producto.fechaInicialDescuento + " al " + producto.fechaFinalDescuento + "\n\n Descuento: " + producto.valorDescuentoProductoMostrar + "% " + "\n\n Observaci�n:" + producto.observacionDescuentoPorProducto;
                  mostrarAlertDialogDescuento(mensaje);
                }
              } else {
                mostrarToast("Hubo un error al insertar el registro");
              }
            } else {
              mostrarToast("Hubo un error al insertar el registro");
            }
          } else {
            mostrarToast("La cantidad ingresada supera el Inventario del Producto \n" + "El inventario de " + producto.descripcion + " es de " + Util.Redondear("" + producto.cantidadInv, 0));
          }
        } catch (SQLException e) {
          Util.MostrarAlertDialog(this.getBaseContext(), "Error guardando el producto!\nCausa: " + e.getMessage());
        }
        // }
        /*
         * else{ mostrarToast(
         * "La cantidad Pedida Supera el Inventario del Producto "
         * +producto.cantidadInv); }
         */
      }
    }
  }
  
  /**
   * Aplicar el descuento al producto segun la linea y lista de descuentos
   * aplicada.
   *
   * @param producto
   */
  /*
   * private void aplicarDescuentos(Producto producto) {
   *
   * DataBaseBO.aplicarDescuentoDetalle(producto, cliente);
   *
   * buscar el producto en la lista de pedidos para actualizar el descuento
   * aplicado a ese producto. int index = productosPedido.indexOf(producto);
   * if(producto.descuento > 0 && index >= 0 && index <
   * productosPedido.size()){ Producto p = productosPedido.get(index);
   * p.descuento = producto.descuento; productosPedido.set(index, p); } }
   */
  public boolean aplicarDescuentos(Producto producto) {
    FileBO.validarCliente(this);
    cliente = Main.cliente;
    boolean respuesta = false;
    if (cliente != null) {
      DataBaseBO.aplicarDescuentoDetalle(producto, cliente);
      DataBaseBO.aplicarDescuentoProducto(producto, cliente);
      respuesta = true;
    } else {
      Util.MostrarAlertDialog(FormPedidosAutoventa.this, "Ocurrio un error al aplicar el descuento.");
      respuesta = false;
    }
    // buscar el producto en la lista de pedidos para actualizar el
    // descuento aplicado a ese producto.
    int index = productosPedido.indexOf(producto);
    if (producto.descuento > 0 && index >= 0 && index < productosPedido.size()) {
      Producto p = productosPedido.get(index);
      p.descuento = producto.descuento;
      p.descuentoPorProducto = producto.descuentoPorProducto;
      p.observacionDescuentoPorProducto = producto.observacionDescuentoPorProducto;
      p.fechaInicialDescuento = producto.fechaInicialDescuento;
      p.fechaFinalDescuento = producto.fechaFinalDescuento;
      p.valorDescuentoProductoMostrar = producto.valorDescuentoProductoMostrar;
      productosPedido.set(index, p);
    }
    return respuesta;
  }
  
  /**
   * metodo para agregar los productos pedidos al list view ubicado en la
   * parte inferior de la pantalla.
   *
   * @by JICZ
   */
  private void agregarProductoListView() {
    Vector<ItemListView> datosPedido = new Vector<ItemListView>();
    // recorrer los productos pedidos para crear la lista de items
    for (Producto producto : productosPedido) {
      ItemListView itemListView = new ItemListView();
      /* se define la informacion que sera mostrada */
      itemListView.titulo = producto.codigo + " - " + producto.descripcion;
      itemListView.subTitulo = "Cantidad: " + producto.cantidadPedida + "\n" + "Precio: " + Util.SepararMiles("" + producto.precio) + "\n" + "Iva: " + producto.iva;
      // agregar item a datosPedido
      datosPedido.addElement(itemListView);
    }
    // se establece la lista de items en base al vector creado con la lista
    // de productos.
    ItemListView[] listaItems = new ItemListView[datosPedido.size()];
    datosPedido.copyInto(listaItems);
    // configuracion del listViewAdapter para que muestre la informacion
    // obtenida
    ListViewAdapter adapter = new ListViewAdapter(this, listaItems, R.drawable.prod, 0xff7D0000);
    listaPedido.setAdapter(adapter);
    /*
     * actualizar precio y descuento en la pantalla, se recibe
     * (precio;descuento) separado por ";" el descuento no el valor
     * porcentual, sino el monto total en pesos descontado del total del
     * pedido.
     */
    /*
     * String precioDescuento =
     * DataBaseBO.calcularPrecioDescuentoPedidoAv(); String[]
     * arrayPrecioDescueto = precioDescuento.split(";"); montoDecuento =
     * arrayPrecioDescueto[1]; precioTotal = arrayPrecioDescueto[0];
     * textViewDescuentoProductoAv.setText(montoDecuento);
     * textViewPrecioConIvaProductoAv.setText(precioTotal);
     */
    calcularMontoDePedido();
  }
  
  /**
   * metodo que permite quitar un producto de la lista de pedidos, tembien es
   * borrado de la base de datos.
   *
   * @by JICZ.
   */
  protected void eliminarProductoPedidos(Producto producto) {
    try {
      // eliminar producto de la lista de productos pedidos.
      boolean removido = productosPedido.remove(producto);
      // definir sus valores iniciales y actualizar
      producto.cantidadPedida = 0;
      listaProductos.set(posicionProductoActual, producto);
      devolverCantidadInventario(producto.codigo);
      // remover el producto de la lista de pedido.
      if (DataBaseBO.quitarProductoProductoPedidoAv(producto) && removido) {
        mostrarToast("Producto codigo: " + producto.codigo + ".\nHa sido eliminado del pedido correctamente!");
        // calcular descuentos y nuevo precio. si no hay descuentos, se
        // define solo su precio segun el impuesto.
        // DataBaseBO.calcularDescuentoProductoAutoventa(producto, "0");
      }
      // actualizar la lista de pedido en pantalla.
      agregarProductoListView();
    } catch (Exception e) {
      mostrarToast("ERROR: " + e.getMessage());
    }
  }
  
  /**
   * metodo permite modificar la cantidad de un producto a una nueva cantidad
   * ingresada por el susuario.
   *
   * @by JICZ
   */
  protected void modificarProductoPedidos(final Producto producto) {
    try {
      // obtener nueva cantidad, ya viene validada.
      String cantidad = editTextCantidadProductoAv.getText().toString().trim();
      if (Integer.parseInt(cantidad) <= producto.cantidadInv) {
        eliminarProductoPedidos(producto);
        OnClickAgregarProductoAv(null);
        // //obtener el indice del producto a modificar.
        // int index = productosPedido.indexOf(producto);
        //
        // //definir sus valores iniciales y actualizar
        // producto.cantidadPedida = Integer.parseInt(cantidad);
        //
        // //actualizar listas de productos
        // listaProductos.set(posicionProductoActual, producto);
        // productosPedido.set(index, producto);
        //
        // for (Inventario i : producto.getListaInventario()) {
        //
        // if(producto.cantidadPedida >= i.cantidad && i.cantidad > 0){
        //
        // Producto p = (Producto) producto.clone();
        // p.cantidadPedida = i.cantidad;
        // p.lote = i.lote;
        // p.fechaVencimiento = i.fechaVencimiento;
        // p.fechaFabricacion = i.fechaFabricacion;
        //
        // /*agregar el producto al detalle detalle */
        // if(DataBaseBO.modificarProductoProductoPedidoAv(p)) {
        //
        // mostrarProducto();
        // mostrarToast("Producto codigo: " + producto.codigo +
        // ".\nHa sido modificado del pedido correctamente!");
        // }
        // /*descontar la cantidad del lote viejo*/
        // producto.cantidadPedida -= i.cantidad;
        // }
        // else if(producto.cantidadPedida > 0){
        // Producto p = producto;
        // p.lote = i.lote;
        // p.fechaVencimiento = i.fechaVencimiento;
        // p.fechaFabricacion = i.fechaFabricacion;
        //
        // /*agregar el producto al detalle detalle */
        // if(DataBaseBO.modificarProductoProductoPedidoAv(p)) {
        //
        // mostrarProducto();
        // mostrarToast("Producto codigo: " + producto.codigo +
        // ".\nHa sido modificado del pedido correctamente!");
        // }
        // /*descontar la cantidad del lote*/
        // producto.cantidadPedida -= p.cantidadPedida;
        // }
        // }
        //
        // actualizar la lista de pedido en pantalla.
        agregarProductoListView();
      } else {
        mostrarToast("No Se Pudo Modificar\nLa cantidad Pedida Supera el Inventario del Producto " + producto.cantidadInv);
      }
    } catch (Exception e) {
      mostrarToast("ERROR: " + e.getMessage());
    }
  }
  
  /**
   * metodo para confirmar que se desea finalizar un pedido en autoventa.
   *
   * @param v
   * @by JICZ
   */
  protected void finalizarPedidoAv(View v) {
    // se elimina el alertdialog por peticion del cliente. 15/ago/2014

    //Se valida si las coordenadas actuales estan vacias, si lo estan, se capturan de nuevo
    if(latitudActual==0.0||longitudActual==0.0)
        validarUbicacion();
    finalizarPedidoAutoVenta();
  }
  
  /**
   * metodo para confirmacion definitiva de un pedido. Se usan transacciones
   * en bd para confirmar solo pedidos que terminan correctamente. si se
   * produce un error el pedido no se realiza.
   *
   * @by JICZ
   */
  protected void finalizarPedidoAutoVenta() {
    // verificar que la lista de pedido no este vacia
    if (!productosPedido.isEmpty()) {
      // cargar la informacion del usuario
      Usuario usuario = DataBaseBO.ObtenerUsuario();
      try {
        numeroDocPedidoAv = DataBaseBO.ObtenterNumeroDoc(usuario.codigoVendedor);
        // captura de observaciones
        observaciones = editTextObservacionesPedidoAv.getText().toString().trim();
        ordenCompra = editTextOrdenCompra.getText().toString().trim();
        FileBO.validarCliente(this);
        cliente = Main.cliente;
        // validarCantidadPorLotes(productosPedido);
        calcularMontoDePedido();
        String resolucion = DataBaseBO.getResolucion();
        encabezado.prefijo = DataBaseBO.getPrefijo();
        long consec = DataBaseBO.getConsecutivoActual();
        long consecutivoEncabezado = DataBaseBO.getConsecutivoActualEncabezado(encabezado.prefijo);
        Log.e("consec ", "consec " + consec + " consecutivoEncabezado " + consecutivoEncabezado);
        if (consec == consecutivoEncabezado) {
          consec = consec + 1;
          Log.e("1 ", consec + "");
        }
        if (consec < consecutivoEncabezado) {
          consec = consecutivoEncabezado + 1;
          Log.e("2 ", consec + "");
        }
        Log.e("consec ", "consec " + consec + " consecutivoEncabezado " + consecutivoEncabezado);
        String noFactura = encabezado.prefijo + consec;
        encabezado.numero_doc = numeroDocPedidoAv;
        encabezado.resolucion = resolucion;
        encabezado.consecutivoResolucion = consec;
        encabezado.codigo_novedad = TIPO_TRANSACCION;
        encabezado.hora_inicial = Util.FechaActual("yyyy-MM-dd HH:mm:ss");
        encabezado.pago = checkBoxClientePago.isChecked();
        boolean pedidoCompleto = DataBaseBO.guardarEncabezadoPedidoAv(numeroDocPedidoAv, cliente, usuario, stringFechaEntrega, stringFechaCobro, observaciones, ordenCompra, TIPO_TRANSACCION, ENTREGADO, FACTURA, TIPO_VENTA_PEDIDO, TIPO_DOCUMENTO, ESTADO_PEDIDO, ESTADO_PAGO, encabezado, DataBaseBO.getPrefijo(), ESVALIDO);
        // guardar la novedad
        String imei = obtenerImei();
        String version = ObtenerVersion();
        boolean guardarNovedad = DataBaseBO.guardarNovedad(encabezado, numeroDocPedidoAv, cliente, usuario, precioTotal, observaciones, imei, version, 1, latitudActual+"", longitudActual+""); // 6
        // y
        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // // 32
        // Nocompras.
        if (pedidoCompleto && guardarNovedad) {
          // DataBaseBO.ActualizarConsecutivo();
          DataBaseBO.incrementarNumeroFactura(usuario.codigoVendedor);
          DataBaseBO.guardarConsecutivoResoluciones(usuario.codigoVendedor, resolucion, consec);
          String codSaldo = usuario.codigoVendedor + Util.ObtenerFechaId();
          /* guardar preferences de numeros de facturacion */
          String numeroActual = PreferenceNumeroFactura.getNumeroActual(FormPedidosAutoventa.this);
          PreferenceNumeroFactura.guardarNumeroAnterior(FormPedidosAutoventa.this, numeroActual);
          if (encabezado.pago) {
            DataBaseBO.guardarSaldosCliente(usuario.codigoVendedor, encabezado.numero_doc, codSaldo, encabezado.valor_neto, 3, cliente.codigo);
            DataBaseBO.guardarSaldosCanceladosCliente(usuario.codigoVendedor, noFactura, encabezado.numero_doc, codSaldo, encabezado.valor_neto, 1);
            codSaldoCancelado = codSaldo;
            mostrarToast("Saldo pago automatico");
            
          } else {
            DataBaseBO.guardarSaldosCliente(usuario.codigoVendedor, encabezado.numero_doc, codSaldo, encabezado.valor_neto, 1, cliente.codigo);
            DataBaseBO.guardarSaldosPendientesCliente(usuario.codigoVendedor, encabezado.numero_doc, codSaldo, encabezado.valor_neto, 2, encabezado.prefijo, consec);
          }
          DataBaseBO.organizarInventario(this);
          mostrarToast("Se ha guardado el pedido correctamente!\n Numero de Pedido: " + numeroDocPedidoAv);
          // se elimina la vista de la base de datos creada en el
          // onCreate()
          // DataBaseBO.eliminarVista();
          // confirmar si desea imprimir la tirilla de reporte de
          // pedido
          mostrarAlertParaConfirmarSiImprimir();
        } else {
          mostrarToast("No se logro guardar el pedido!\n Numero de Pedido: " + numeroDocPedidoAv);
        }
      } catch (Exception e) {
        Util.MostrarAlertDialog(this.getBaseContext(), e.getMessage());
      }
    } else {
      // --mostrar informacion de pedido vacio
      Util.MostrarAlertDialog(FormPedidosAutoventa.this, "No hay pedido para guardar!");
    }
  }
  
  private Vector<Producto> validarCantidadPorLotes(Producto producto) {
    int cantFaltante = 0;
    int cantVendida = producto.cantidadPedida;
    int cantAux = 0;
    System.out.println("Producto: " + producto.descripcion + " cod:" + producto.codigo + " N " + producto.cantidadPedida);
    Vector<Inventario> listaLote = DataBaseBO.obtenerLotesXCodigo(producto.codigo);
    Producto productoNew;
    for (int i = 0; i < listaLote.size(); i++) {
      productoNew = new Producto();
      productoNew.codigo = producto.codigo;
      productoNew.codLinea = producto.codLinea;
      productoNew.precio = producto.precio;
      productoNew.iva = producto.iva;
      productoNew.descuento = producto.descuento;
      productoNew.cantidad = producto.cantidadPedida;
      productoNew.lote = listaLote.get(i).lote;
      productoNew.fechaFabricacion = listaLote.get(i).fechaFabricacion;
      productoNew.fechaVencimiento = listaLote.get(i).fechaVencimiento;
      cantAux = cantVendida + cantFaltante;
      System.out.println("LOTE: " + productoNew.lote);
      System.out.println("Cantidad AUXILIAR: " + cantAux + "Cantidad vendida " + cantVendida + " // " + cantFaltante);
      if (listaLote.get(i).cantidad >= cantAux) {
        restarLote(listaLote.get(i).cantidad, cantAux, listaLote.get(i).lote, producto.codigo);
        cantVendida = 0;
        System.out.println(listaLote.get(i).lote + " Cant: " + listaLote.get(i).cantidad);
        productoNew.cantidadPedida = cantAux;
        listaProductoNew.add(productoNew);
        System.out.println("Hay suficiente cantidad en el lote" + productoNew.cantidadPedida);
        break;
        // Hay cantidad suficiente en el lote
      } else {
        cantFaltante = producto.cantidadPedida - listaLote.get(i).cantidad;
        restarLote(listaLote.get(i).cantidad, cantAux - cantFaltante, listaLote.get(i).lote, producto.codigo);
        cantVendida = 0;
        productoNew.cantidadPedida = cantAux - cantFaltante;
        listaProductoNew.add(productoNew);
        System.out.println("Hace falta un total de " + cantFaltante + " inventario en el lote" + productoNew.cantidadPedida);
      }
    }
    return listaProductoNew;
  }
  
  private void restarLote(int cantidadLote, int cantVender, String lote, String codigo) {
    // System.out.println("Restar Lote");
    int cantidadSobrante = cantidadLote - cantVender;
    // System.out.println("Cantidad solicitada: "+ cantVender +" Cant total:
    // "+cantidadLote + " Cant sobrante: "+cantidadSobrante);
    DataBaseBO.restarLote(cantidadSobrante, lote, codigo);
  }
  
  /**
   * metodo para confirmar si el usuario desea imprimir o no la tirilla de
   * reporte de un pedido
   */
  private void mostrarAlertParaConfirmarSiImprimir() {
    // solicitar confirmacion para terminar el pedido.
    AlertDialog alertDialog;
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setCancelable(false).setPositiveButton("Si", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        dialog.cancel();
        MostrarDialogImprimirFactura();
      }
    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
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
        finalizar(true);
      }
    });
    alertDialog = builder.create();
    alertDialog.setMessage("Desea Imprimir Tirilla?");
    alertDialog.show();
  }
  
  /**
   * metodo que crea un toast (mensaje informativo) en la pantalla para
   * indicar diferentes situaciones
   *
   * @param text
   *            , texto a mostrar
   * @by JICZ
   */
  private void mostrarToast(CharSequence text) {
    /* captura del contexto de la aplicacion */
    Context context = getApplicationContext();
    /* mostrar el mensaje en la parte inferior izquierda */
    Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
    toast.show();
  }
  
  /**
   * metodo que permite ocultar el teclado cuando esta activado en un editText
   *
   * @param editText
   */
  private void ocultarTeclado(EditText editText) {
    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
  }
  
  /**
   * AlertDialog, se muestra cuando se intenta agregar un producto que ya esta
   * en la lista de pedido. permite modificar la cantidad anteriomente
   * ingresada, camo tambien eliminar el producto del pedido. o salir del
   * alert dialog y continuar con el pedido.
   *
   * @by JICZ
   */
  private void alertDialogCambiosProductoPedidoAv(final Producto producto) {
    AlertDialog alertDialog;
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setCancelable(false)
      /*
       * .setPositiveButton("Modificar", new
       * DialogInterface.OnClickListener() {
       *
       * public void onClick(DialogInterface dialog, int id) {
       * modificarProductoPedidos(); dialog.cancel(); // inicializar
       * valores de cantidad editTextCantidadProductoAv.setText("");
       * editTextCantidadProductoAv.requestFocus(); } })
       */.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    }).setNeutralButton("Eliminar", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        eliminarProductoPedidos(producto);
        dialog.cancel();
        // inicializar valores de cantidad
        editTextCantidadProductoAv.setText("");
        editTextCantidadProductoAv.requestFocus();
      }
    });
    alertDialog = builder.create();
    alertDialog.setMessage("Este Producto ya esta pedido!\nQue desea hacer?");
    alertDialog.show();
  }
  
  /**
   * Metodo para mostrar el dialog que permite agregar las fechas de entrega
   * del pedido y de cobro de un pedido.
   *
   * @by JICZ
   */
  private void mostrarDialogTerminarPedidoAv() {
    dialogTerminarPedidoAv = null;
    // verificar que el dialog sea nulo, se iniciaria por primera vez
    if (dialogTerminarPedidoAv == null) {
      dialogTerminarPedidoAv = new Dialog(this);
      dialogTerminarPedidoAv.setContentView(R.layout.dialog_pedido_autoventa);
      dialogTerminarPedidoAv.setTitle("Terminar Pedido Autoventa");
      // inicializacion de los componentes del dialog
      fechaEntrega = (ImageButton) dialogTerminarPedidoAv.findViewById(R.id.buttonFechaEntregaPedidoAv);
      fechaCobro = (ImageButton) dialogTerminarPedidoAv.findViewById(R.id.buttonFechaCobroPedidoAv);
      editTextFechaEntregaPedidoAv = ((EditText) dialogTerminarPedidoAv.findViewById(R.id.editTextFechaEntregaPedidoAv));
      editTextFechaCobroPedidoAv = ((EditText) dialogTerminarPedidoAv.findViewById(R.id.editTextFechaCobroPedidoAv));
      editTextObservacionesPedidoAv = ((EditText) dialogTerminarPedidoAv.findViewById(R.id.editTextObservacionesPedidoAv));
      editTextOrdenCompra = ((EditText) dialogTerminarPedidoAv.findViewById(R.id.editTextOrdenCompra));
      checkBoxClientePago = ((CheckBox) dialogTerminarPedidoAv.findViewById(R.id.chEstadoPago));
      // mostrar el dialog en pantalla
      dialogTerminarPedidoAv.setCancelable(true);
      dialogTerminarPedidoAv.show();
    } else {
      // si no es nulo entonces limpiar el texto y mostrar de nuevo.
      editTextFechaEntregaPedidoAv.setText("");
      editTextFechaCobroPedidoAv.setText("");
      dialogTerminarPedidoAv.show();
    }
    checkBoxClientePago.setClickable(false);
    if (esClienteDeCredito) {
      checkBoxClientePago.setChecked(false);
      
    } else {
      checkBoxClientePago.setChecked(true);
      
    }
    // captura evento del boton de aceptar el pedido
    ((Button) dialogTerminarPedidoAv.findViewById(R.id.buttonAceptarDialogPedidoAv)).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        /*
         * retardo de 1500ms para evitar eventos de doble click. esto
         * significa que solo se puede hacer click cada 1500ms. es decir
         * despues de presionar el boton, se debe esperar que
         * transcurran 1500ms para que se capture un nuevo evento.
         */
        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
          return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        boolean pago = checkBoxClientePago.isChecked();
        //si el cliente no pago y es cliente de credito validar el disponible
        if (!pago) {
          if (esClienteDeCredito) {
            if (encabezado.valor_neto > disponible) {
              Util.MostrarAlertDialog(FormPedidosAutoventa.this, "No se Puede Finalizar Venta Supera el cupo disponible de" + Util.SepararMiles((long) disponible + "") + "\n\nValor Venta: " + Util.SepararMiles((long) encabezado.valor_neto + ""));
              return;
            }
            
          }
          
        }
        // verificar que las fechas estan ingresadas
        // correctamente
        if (verificarFechasIngresadas()) {
          if (compararFechaActual()) {
            finalizarPedidoAv(v);
            dialogTerminarPedidoAv.dismiss();
          } else {
            Util.MostrarAlertDialog(FormPedidosAutoventa.this, "fecha ingresada menor a la actual!\nIngrese una fecha mayor o igual a la de hoy");
          }
        } else {
          Util.MostrarAlertDialog(FormPedidosAutoventa.this, "fechas ingresadas no validas!\nIngrese fechas en formato yyyy-mm-dd");
        }
      }
    });
    // captura evento del boton de cancelar para salir del dialog.
    ((Button) dialogTerminarPedidoAv.findViewById(R.id.buttonCancelarDialogPedidoAv)).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        // TODO Auto-generated method stub
        dialogTerminarPedidoAv.dismiss();
      }
    });
    // captura del evento del boton de definir la fecha de entrega del
    // pedido
    fechaEntrega.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        // mostrar el seleccionador de fecha para agregarla al edit del
        // fecha de entrega
        mostrarDataPicker(editTextFechaEntregaPedidoAv);
      }
    });
    // captura del evento del boton de definir la fecha de cobro.
    fechaCobro.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        // mostrar el seleccionador de fecha para agregarla al edit del
        // fecha de cobro
        mostrarDataPicker(editTextFechaCobroPedidoAv);
      }
    });
    LinearLayout lyFechaCobro = (LinearLayout) dialogTerminarPedidoAv.findViewById(R.id.lyFechaCobro);
    lyFechaCobro.setVisibility(LinearLayout.GONE);
    // LinearLayout lyFechaEntrega =
    // (LinearLayout)dialogTerminarPedidoAv.findViewById(R.id.lyFechaEntrega);
    // lyFechaEntrega.setVisibility(LinearLayout.GONE);
    Calendar c = new GregorianCalendar();
    c.add(Calendar.DATE, 1);
    Date d = c.getTime();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    editTextFechaEntregaPedidoAv.setText(sdf.format(d));
  }
  
  /**
   * metodo para validar que las fecahs ingresadas con validas.
   *
   * @return true, si son validas, false en caso contrario.
   *
   * @by JICZ.
   */
  protected boolean verificarFechasIngresadas() {
    boolean validado = false;
    // capturar fechas ingresadas
    String fechaEntrega = editTextFechaEntregaPedidoAv.getText().toString().trim();
    String fechaCobro = editTextFechaCobroPedidoAv.getText().toString().trim();
    // verificar que ambas fechas son validas
    if (isFechaValida(fechaEntrega)) {
      // if(isFechaValida(fechaCobro)){
      validado = true;
      // }
    }
    return validado;
  }
  
  /**
   * metodo para comprobar si una fecha tiene el formato de fecha adecuado
   *
   * @param fecha
   * @return true, si es valida, false en caso contrario.
   * @by JICZ
   */
  private static boolean isFechaValida(String fecha) {
    try {
      SimpleDateFormat formatoFecha = new SimpleDateFormat(FORMATO_FECHA, Locale.getDefault());
      formatoFecha.setLenient(false);
      formatoFecha.parse(fecha);
    } catch (java.text.ParseException e) {
      return false;
    }
    return true;
  }
  
  /**
   * metodo para verificar que las fechas ingresadas de cobro y de entrega no
   * sean anteriores a la fecha actual. si son menores a la fecha actual, no
   * pueden ser usadas.
   *
   * @return true, si la fecha es igual o mayor a la actual. false en caso
   *         contrario.
   * @by JICZ
   */
  protected boolean compararFechaActual() {
    boolean validado = false;
    // capturar fechas ingresadas
    String fechaEntrega = editTextFechaEntregaPedidoAv.getText().toString().trim();
    // String fechaCobro =
    // editTextFechaCobroPedidoAv.getText().toString().trim();
    String fechaCobro = fechaEntrega;
    /*
     * Obtenemos la fecha del sistema y la convertirmos al String con el
     * formato en el que vamos a trabajar
     */
    Date fechaActual = new Date();
    SimpleDateFormat formateador = new SimpleDateFormat(FORMATO_FECHA, Locale.getDefault());
    String fechaSistema = formateador.format(fechaActual);
    // se intenta formatear la fecha
    try {
      Date fechaDateEntrega = formateador.parse(fechaEntrega);
      Date fechaDateCobro = formateador.parse(fechaCobro);
      Date fechaDateActual = formateador.parse(fechaSistema);
      // comprar si las fechas de cobro y de entrega no sean menores a la
      // fecha actual.
      if (!fechaDateCobro.before(fechaDateActual)) {
        if (!fechaDateEntrega.before(fechaDateActual)) {
          validado = true;
          stringFechaCobro = fechaCobro;
          stringFechaEntrega = fechaEntrega;
        }
      }
    } catch (java.text.ParseException e) {
      validado = false;
    }
    return validado;
  }
  
  /**
   * Metodo para mostrar un DialogDatePicker, donde el usuario define las
   * fechas de cobro o de entrega de un pedido.
   *
   * @param editText
   *            , edit donde se mostrara la fecha ingresada por el usuario.
   *
   * @by JICZ
   */
  protected void mostrarDataPicker(final EditText editText) {
    /*
     * Capturar la fecha actual que sera mostrada por primera vez en el
     * datepicker
     */
    final Calendar c = Calendar.getInstance();
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH);
    int day = c.get(Calendar.DAY_OF_MONTH);
    /*
     * inicializar el dialog picker para que el usuario ingrese la fecha
     * deseada, se define la fecha actual capturada
     */
    DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
      @Override
      public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String fecha = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
        SimpleDateFormat formato = new SimpleDateFormat(FORMATO_FECHA, Locale.getDefault());
        Date fechas;
        try {
          fechas = formato.parse(fecha);
          fecha = formato.format(fechas);
          editText.setText(fecha);
        } catch (ParseException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }, year, month, day);
    // mostrar el el dialog datepicker.
    dpd.show();
  }
  
  /**
   * Metodo para enviar la informacion que sera registrada en el servidor
   *
   * @by JICZ
   */
  protected void enviarInfo() {
    AlertDialog.Builder builder = new AlertDialog.Builder(FormPedidosAutoventa.this);
    builder.setMessage("Solicitud Almacenada Exitosamente").setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        progressDialog = ProgressDialog.show(FormPedidosAutoventa.this, "", "Enviando Informacion...", true);
        progressDialog.show();
        // enviar el pedido
        Sync sync = new Sync(FormPedidosAutoventa.this, Const.ENVIAR_PEDIDO);
        sync.start();
        dialog.cancel();
      }
    });
    AlertDialog alert = builder.create();
    alert.show();
  }
  
  /*
   * metodo sobrecargado de la interface Sincronizador. se activa
   * imendiatamente despues de recibir una respuesta del servidor. tras enviar
   * un pedido.
   */
  @Override
  public void RespSync(boolean ok, String respuestaServer, String msg, int codeRequest) {
    switch (codeRequest) {
      case Const.ENVIAR_PEDIDO:
        RespuestaEnviarInfo(ok, respuestaServer, msg);
        break;
    }
  }
  
  /**
   * metodo para mostrar la respuesta del servidor al usuario. cuando se
   * confirme la respuesta. se finaliza el activity.
   *
   * @param ok
   * @param respuestaServer
   * @param msg
   * @by JICZ
   */
  public void RespuestaEnviarInfo(boolean ok, String respuestaServer, String msg) {
    final String mensaje = ok ? "Informacion Registrada con Exito en el servidor" : msg;
    if (progressDialog != null) progressDialog.cancel();
    this.runOnUiThread(new Runnable() {
      public void run() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FormPedidosAutoventa.this);
        builder.setMessage(mensaje).setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
            finalizar(true);
          }
        });
        AlertDialog alert = builder.create();
        alert.show();
      }
    });
  }
  
  private void finalizar(boolean mostrarMensaje) {
    if (dialogImprimirFacturaPedido != null) {
      dialogImprimirFacturaPedido.cancel();
    }
    if (mostrarMensaje) {
      String mensaje = "Informacion Registrada con Exito";
      AlertDialog.Builder builder = new AlertDialog.Builder(FormPedidosAutoventa.this);
      builder.setMessage(mensaje).setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
					/*
					devolverTotalCantidadInventario();
					// limpiar listaProductos
					listaProductos = null;
					// limpiar productosPedido
					productosPedido = null;
					// dejar en cero la cantidad de productos.
					cantidadProductos = 0;
					// limpiar cliente actual
					cliente = null;
					// DataBaseBO.eliminarVista();
					DataBaseBO.cancelarPedidoAv();
					dialog.cancel();
					finish();
					*/
          dialog.cancel();
          if (encabezado.pago) {
            limpiarDatos();
            Intent intent = new Intent(FormPedidosAutoventa.this, FormDepositoActivity.class);
            intent.putExtra("codSaldo", codSaldoCancelado);
            startActivity(intent);
            
          } else {
            limpiarDatos();
            
          }
          
        }
      });
      AlertDialog alert = builder.create();
      alert.show();
    } else {
			/*
			devolverTotalCantidadInventario();
			// limpiar listaProductos
			listaProductos = null;
			// limpiar productosPedido
			productosPedido = null;
			// dejar en cero la cantidad de productos.
			cantidadProductos = 0;
			// limpiar cliente actual
			cliente = null;
			// DataBaseBO.eliminarVista();
			DataBaseBO.cancelarPedidoAv();
			finish();
			*/
      if (encabezado.pago) {
        limpiarDatos();
        Intent intent = new Intent(FormPedidosAutoventa.this, FormDepositoActivity.class);
        intent.putExtra("codSaldo", codSaldoCancelado);
        startActivity(intent);
        
      } else {
        limpiarDatos();
        
      }
      
    }
  }
  
  private void onBluetooth() {
    if (bluetoothAdapter != null) if (!bluetoothAdapter.isEnabled()) {
      bluetoothAdapter.enable();
      Log.i("Log", "Bluetooth is Enabled");
    }
  }
  
  /**
   * metodo para imprimir la tirilla del reporte del pedido.
   */
  public void imprimirFactura() {
    // captura de settings de la impresora
    SharedPreferences settings = getSharedPreferences(CONFIG_IMPRESORA, MODE_PRIVATE);
    final String macImpresora = settings.getString(MAC_IMPRESORA, "-");
    /*
     * verificar que la impresora este configurada, si no lo esta, enviar a
     * configurar
     */
    if (macImpresora.equals("-")) {
      AlertDialog.Builder builder = new AlertDialog.Builder(FormPedidosAutoventa.this);
      builder.setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          Intent iExp = new Intent(FormPedidosAutoventa.this, FormConfigPrinter.class);
          startActivity(iExp);
          dialog.cancel();
        }
      });
      AlertDialog alertDialog;
      alertDialog = builder.create();
      alertDialog.setMessage("Aun no hay Impresora Establecida.\n\nPor Favor primero Configure la Impresora!");
      alertDialog.show();
    } else {
      progressDialog = ProgressDialog.show(FormPedidosAutoventa.this, "", "Imprimiendo . . . ", true);
      progressDialog.show();
      imprimir(macImpresora);
    }
  }
  
  private void imprimir(final String printerName) {
    new Thread(new Runnable() {
      public void run() {
        BluetoothSocket socket = null;
        try {
          Looper.prepare();
          bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
          if (bluetoothAdapter == null) {
            return;
          }
          if (!bluetoothAdapter.isEnabled()) {
            return;
          }
          BluetoothDevice printer = null;
          Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
          for (BluetoothDevice device : pairedDevices) {
            String s = device.getAddress();
            if (s.equals(printerName)) {
              printer = device;
              break;
            }
          }
          if (printer == null) {
            if (progressDialog != null) {
              progressDialog.dismiss();
            }
            return;
          } else {
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(printerName);
            Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            socket = (BluetoothSocket) m.invoke(device, 1);
            // -------fin-Cambio
            if (socket != null) {
              try {
                socket.connect();
                Imprimiendo(socket);
              } catch (Exception e) {
                mensaje = "No se pudo conectar con la impresora, verifique si la impresora esta encendida";
                handlerError.sendEmptyMessage(0);
              }
            } else {
              if (bluetoothAdapter != null) {
                bluetoothAdapter.disable();
                onBluetooth();
              }
              device = bluetoothAdapter.getRemoteDevice(printerName);
              m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
              socket = (BluetoothSocket) m.invoke(device, 1);
              try {
                socket.connect();
                Imprimiendo(socket);
              } catch (Exception e) {
                mensaje = "No se pudo conectar con la impresora, verifique si la impresora esta encendida";
                handlerError.sendEmptyMessage(0);
              }
            }
          }
          Thread.sleep(500);
          Looper.myLooper().quit();
        } catch (Exception e) {
          if (progressDialog != null) {
            progressDialog.dismiss();
          }
          String motivo = e.getMessage();
          mensaje = "No se pudo Imprimir.";
          if (motivo != null) {
            mensaje += "\n\nMotivo: " + motivo;
          }
          handlerError.sendEmptyMessage(0);
        }
      }
    }).start();
  }
  
  public void Imprimiendo(final BluetoothSocket socket) {
    splashTread = new Thread() {
      @Override
      public void run() {
        try {
          synchronized (this) {
            wait(_splashTime);
          }
        } catch (InterruptedException e) {
          mensaje = "error inesperado: " + e.getMessage();
          handlerError.sendEmptyMessage(0);
        } finally {
          try {
            OutputStream stream = socket.getOutputStream();
            String strPrint = "";
            // se captura el formato de la tirilla.
            strPrint = formatoTirillaPedidoAutoVenta();
            stream.write(strPrint.getBytes());
            stream.flush();
            stream.close();
            handlerFinish.sendEmptyMessage(0);
            if (progressDialog != null) {
              progressDialog.dismiss();
            }
            try {
              if (socket != null) socket.close();
            } catch (Exception e) {
            }
          } catch (Exception e2) {
            if (progressDialog != null) {
              progressDialog.dismiss();
            }
            handlerError.sendEmptyMessage(0);
          }
        }
      }
    };
    splashTread.start();
  }
  
  private Handler handlerFinish = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      AlertDialog.Builder builder = new AlertDialog.Builder(FormPedidosAutoventa.this);
      builder.setMessage("Solicitud Almacenada Exitosamente").setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          // progressDialog =
          // ProgressDialog.show(FormPedidosAutoventa.this,
          // "","Enviando Informacion...", true);
          // progressDialog.show();
          // progressDialog.setIndeterminate(true);
          // progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_icon_drawable_animation));
          //
          //
          // Sync sync = new
          // Sync(FormPedidosAutoventa.this,
          // Const.ENVIAR_PEDIDO);
          // sync.start();
          finalizar(false);
          dialog.cancel();
        }
      });
      AlertDialog alert = builder.create();
      alert.show();
    }
  };
  /**
   * metodo administrador de errores en hilo principal, se captura si se
   * produce algun error en la ejecucion de la actividad.
   */
  private Handler handlerError = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      AlertDialog.Builder builder = new AlertDialog.Builder(FormPedidosAutoventa.this);
      builder.setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          AlertDialog.Builder builder = new AlertDialog.Builder(FormPedidosAutoventa.this);
          builder.setMessage("Solicitud Almacenada Exitosamente").setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
              // progressDialog =
              // ProgressDialog.show(FormPedidosAutoventa.this,
              // "","Enviando Informacion...",
              // true);
              // progressDialog.show();
              // progressDialog.setIndeterminate(true);
              // progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_icon_drawable_animation));
              //
              //
              // Sync sync = new
              // Sync(FormPedidosAutoventa.this,
              // Const.ENVIAR_PEDIDO);
              // sync.start();
              dialog.cancel();
              finalizar(false);
            }
          });
          AlertDialog alert = builder.create();
          alert.show();
        }
      });
      AlertDialog alertDialog = builder.create();
      alertDialog.setMessage(mensaje);
      if (progressDialog != null) progressDialog.cancel();
      alertDialog.show();
    }
  };
  
  /**
   * metodo que genera un texto para ser impreso en la tirilla del reporte.
   *
   * @return
   */
  public String formatoTirillaPedidoAutoVenta() {
    char ret1 = 13;
    char ret2 = 10;
    Cliente cliente;
    Usuario usuario;
    String sFecha = stringFechaEntrega;
    SimpleDateFormat formato = new SimpleDateFormat(FORMATO_FECHA);
    Date fechaDate = null;
    try {
      fechaDate = formato.parse(sFecha);
      sFecha = fechaDate.toGMTString();
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    usuario = DataBaseBO.ObtenerUsuario();
    String ret = String.valueOf(ret1) + String.valueOf(ret2);
    // int entero = Math.round(Float.valueOf(precioTotal));
    cliente = Main.cliente;
    String encabezado = "                                     " + ret + ret;
    encabezado = encabezado + "                                    " + ret;
    encabezado = encabezado + "	     	    PEDIDO   " + ret;
    encabezado = encabezado + "   No " + numeroDocPedidoAv + "   " + ret + ret;
    // encabezado =
    // encabezado+"YO "+Util.removerAcentos(cliente.contacto)+","+ret;
    encabezado = encabezado + "REALICE EL PEDIDO AL SR(A):" + ret;
    // encabezado =
    // encabezado+Util.removerAcentos(usuario.codigoVendedor)+ret;
    encabezado = encabezado + "QUIEN ACTUA EN REPRESENTACION DE" + ret;
    encabezado = encabezado + "TEAM FOODS MEXICO SA DE CV, LOS " + ret;
    encabezado = encabezado + "SIGUIENTES PRODUCTOS EN CALIDAD " + ret;
    encabezado = encabezado + "DE VENTA CON CONDICION DE PAGO:" + ret;
    // encabezado =
    // encabezado+"("+Util.removerAcentos(cliente.tipoVenta)+")"+ret;
    encabezado = encabezado + "LOS CUALES REQUIERO QUE SE " + ret;
    encabezado = encabezado + "ENTREGUEN EN LA SIGUIENTE FECHA:" + ret;
    encabezado = encabezado + sFecha.substring(0, 12).toUpperCase(Locale.getDefault()) + ret;
    encabezado = encabezado + "Y DE LOS CUALES RECONOZCO SU " + ret;
    encabezado = encabezado + "VALOR COMERCIAL COMO SE DESCRIBE" + ret;
    encabezado = encabezado + "A CONTINUACION:" + ret + ret;
    encabezado = encabezado + "Producto    UM CAN DES%  IMPORTE" + ret;
    encabezado = encabezado + "________________________________" + ret;
    // ciclo para imprimir el detalle del pedido.
    int precio = 0;
    for (Producto p : productosPedido) {
      // DataBaseBO.actualizarProductoDetallePedidoAv(p,
      // numeroDocPedidoAv);
      double importe = Math.round(p.precio - (p.precio * (p.descuento / 100)));
      precio += (importe * p.cantidadPedida);
      String cantidad = String.valueOf(p.cantidadPedida) + "    ";
      // encabezado = encabezado+ p.nombreImprimir +" " +
      // p.unidadMedidaImprimir + " " + cantidad.substring(0, 3) +" "+
      // Math.rint(p.descuento * 10)/10 +" $" +
      // String.valueOf(Math.round(importe)) + ret;
    }
    encabezado = encabezado + "________________________________" + ret;
    encabezado = encabezado + "TOTAL              " + Util.SepararMiles(String.valueOf(precio)) + ret; // se
    // cambia
    // para
    // poder
    // colocar
    // el
    // precio
    // redondeado,
    // implica
    // un
    // incremento
    // en
    // el
    // valor
    // total
    // de
    // la
    // factura
    // encabezado = encabezado+Util.convertNumberToLetter(precio)+ret+ret;
    encabezado = encabezado + "COMO CONSTANCIA DE HABER " + ret;
    encabezado = encabezado + "REALIZADO EL PEDIDO, FIRMO EN " + ret;
    // encabezado = encabezado+"NOMBRE DE: " +
    // Util.removerAcentos(cliente.contacto)+","+ret;
    encabezado = encabezado + "A LOS " + Util.FechaActual("dd") + " DIAS DEL MES DE " + ret;
    // encabezado =
    // encabezado+Util.MesLetra(Util.FechaActual("MM"))+" DEL
    // "+Util.FechaActual("yyyy")+"."+ret+ret+ret;
    encabezado = encabezado + "--------------------------------" + ret;
    encabezado = encabezado + "	     	 FIRMA CLIENTE   " + ret + ret + ret;
    encabezado = encabezado + ret + ret + ret;
    return encabezado;
  }
  
  /**
   * metodo para vargar el imei del dispositivo.
   *
   * @return
   */
  public String obtenerImei() {
    String deviceId;
    TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    deviceId = manager.getDeviceId();
    if (deviceId.equals("")) {
      WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
      if (wifiManager != null) {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
          String mac = wifiInfo.getMacAddress();
          if (mac != null) {
            deviceId = mac.replace(":", "").toUpperCase(Locale.getDefault());
          }
        }
      }
    }
    return deviceId;
  }
  
  /**
   * evento de listview para eliminar un producto de la lista.
   */
  private OnItemClickListener listenerModificaciones = new OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long id) {
      // mostrar el producto seleccionado
      try {
        mostrarElProductoSeleccionado(position);
      } catch (Exception e) {
        e.printStackTrace();
      }
      final Producto prodS = productosPedido.get(position);
      AlertDialog.Builder builder = new AlertDialog.Builder(FormPedidosAutoventa.this);
      builder.setTitle("Eliminar el producto seleccionado?\n" + prodS.codigo + " - " + prodS.descripcion);
      builder.setPositiveButton("SI", new Dialog.OnClickListener() {
        @Override
        public void onClick(DialogInterface arg0, int arg1) {
          eliminarProductoPedidoPorPosicion(position, prodS);
        }
      });
      builder.setNegativeButton("NO", new Dialog.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          dialog.dismiss();
        }
      });
      AlertDialog alert = builder.create();
      alert.show();
    }
  };
  
  /**
   * muestra en pantalla el producto seleccionado
   *
   * @param position
   * @throws Exception
   */
  protected void mostrarElProductoSeleccionado(int position) throws Exception {
    if (listaProductos != null && !(listaProductos.isEmpty())) {
      Producto p = productosPedido.get(position);
      posicionProductoActual = listaProductos.indexOf(p);
      Producto producto = listaProductos.get(posicionProductoActual);
      // mostrar los datos obtenidos del producto en las vistas
      textViewCodigoProductoAv.setText(producto.codigo);
      textViewDescripcionProductoAv.setText(producto.descripcion);
      textViewPrecioProductoAv.setText("" + producto.precio);
      textViewIvaProductoAv.setText("" + producto.iva);
      // mostrar informacion del navegador de productos.
      TextViewPosicionNavegador.setText("" + (posicionProductoActual + 1) + "-" + cantidadProductos);
    } else {
      throw new Exception("No se logro cargar la lista de productos, por favor iniciar de nuevo");
    }
  }
  
  /**
   * metodo que permite quitar un producto de la lista de pedidos, tembien es
   * borrado de la base de datos.
   *
   * @by JICZ.
   */
  protected void eliminarProductoPedidoPorPosicion(int pos, Producto producto) {
    try {
      int index = listaProductos.indexOf(producto);
      // eliminar producto de la lista de productos pedidos.
      boolean removido = productosPedido.remove(producto);
      // definir sus valores iniciales y actualizar
      producto.cantidadPedida = 0;
      listaProductos.set(index, producto);
      devolverCantidadInventario(producto.codigo);
      // remover el producto de la lista de pedido.
      if (DataBaseBO.quitarProductoProductoPedidoAv(producto) && removido) {
        mostrarToast("Producto codigo: " + producto.codigo + ".\nHa sido eliminado del pedido correctamente!");
      }
      // actualizar la lista de pedido en pantalla.
      agregarProductoListView();
    } catch (Exception e) {
      mostrarToast("ERROR: " + e.getMessage());
    }
  }
  
  private void devolverCantidadInventario(String codProducto) {
    Vector<Producto> listaDetallePdto = DataBaseBO.obtenerListaDetalleVenta(codProducto);
    for (int j = 0; j < listaDetallePdto.size(); j++) {
      System.out.println("Codigo " + listaDetallePdto.get(j).codigo);
      System.out.println("LOTE: " + listaDetallePdto.get(j).lote);
      System.out.println("Cantidad : " + listaDetallePdto.get(j).cantidadPedida);
      DataBaseBO.sumarLote(listaDetallePdto.get(j).cantidadPedida, listaDetallePdto.get(j).lote, codProducto, listaDetallePdto.get(j).fechaFabricacion, listaDetallePdto.get(j).fechaVencimiento);
    }
  }
  
  private void devolverTotalCantidadInventario() {
    if (productosPedido != null) {
      for (Producto producto : productosPedido) {
        Vector<Producto> listaDetallePdto = DataBaseBO.obtenerListaDetalleVenta(producto.codigo);
        for (int j = 0; j < listaDetallePdto.size(); j++) {
          System.out.println("Producto No " + j);
          System.out.println("Codigo " + listaDetallePdto.get(j).codigo);
          System.out.println("LOTE: " + listaDetallePdto.get(j).lote);
          System.out.println("Cantidad : " + listaDetallePdto.get(j).cantidadPedida);
          DataBaseBO.sumarLote(listaDetallePdto.get(j).cantidadPedida, listaDetallePdto.get(j).lote, producto.codigo, listaDetallePdto.get(j).fechaFabricacion, listaDetallePdto.get(j).fechaVencimiento);
        }
        System.out.println("Cerro ciclo de sumatoria inventario");
      }
    }
  }
  
  private void devolverTotalCantidadInventarioGeneral() {
    if (productosPedido != null) {
      for (Producto producto : productosPedido) {
        // Vector<Producto> listaDetallePdto = DataBaseBO
        // .obtenerListaDetalleVenta(producto.codigo);
        // for (int j = 0; j < listaDetallePdto.size(); j++) {
        System.out.println("Codigo " + producto.codigo);
        System.out.println("LOTE: " + producto.lote);
        System.out.println("Cantidad : " + producto.cantidadPedida);
        DataBaseBO.sumarLote(producto.cantidadPedida, producto.lote, producto.codigo, producto.fechaFabricacion, producto.fechaVencimiento);
        // }
        System.out.println("Cerro ciclo de sumatoria inventario");
      }
    }
  }
  
  /**
   * obtener version de la aplicacion
   *
   * @return
   */
  public String ObtenerVersion() {
    String versionApp;
    try {
      versionApp = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
      Log.i("FormInfoClienteActivity", "Version = " + versionApp);
    } catch (NameNotFoundException e) {
      versionApp = "0.0.0";
      Log.e("FormInfoClienteActivity", e.getMessage(), e);
    }
    return versionApp;
  }
  
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    // replaces the default 'Back' button action
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      AlertDialog alertDialog;
      // ContextThemeWrapper ctw = new ContextThemeWrapper( this,
      // R.style.Theme_Dialog_Translucent );
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          dialog.cancel();
          finalizar(false);
        }
      }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          dialog.cancel();
        }
      });
      alertDialog = builder.create();
      alertDialog.setMessage("Desea Salir. Se eliminara la informacion que hubiese ingresado");
      alertDialog.show();
    }
    return true;
  }
  
  public void calcularMontoDePedido() {
    encabezado = new Encabezado();
    /* Calcular retefuente para el cliente */
    if (cliente.retefuente != 0) {
      if (cliente.tipoCliente == 2 && encabezado.sub_total >= encabezado.valorAplicaRetefuntesIca) {
        encabezado.retefuente = (float) 0.025;
      }
      /*
       * cargar el porcentaje de retefuente que se debe aplicar a la
       * venta.
       */
      encabezado.retefuente = DataBaseBO.obtenerPorcentajeRetefuente();
      System.out.println("RETEFUENTE DEL Cliente: " + encabezado.retefuente);
    } else {
      /*
       * cargar el porcentaje de retefuente que se debe aplicar a la
       * venta.
       */
      encabezado.retefuente = 0;
    }
    
    if (cliente.reteIca != 0) {
      if (cliente.tipoCliente == 2 && encabezado.sub_total >= encabezado.valorAplicaRetefuntesIca) {
        encabezado.reteIca = (float) 0.0104;
        
      }
      encabezado.reteIca = DataBaseBO.obtenerPorcentajeReteIca();
      System.out.println("ES DIFERENTE DE CERO LA RETEICA");
    } else {
      encabezado.reteIca = 0;
      System.out.println("ES CERO LA RETEICA");
    }
    encabezado.sub_total = 0;
    encabezado.total_iva = 0;
    encabezado.valor_descuento = 0;
    float retefuente = 0f;
    float reteIca = 0f;
    for (Producto producto : productosPedido) {
      float descuentoTotal = 0;
      if (producto.descuentoPorProducto > 0) {
        descuentoTotal = producto.descuento + producto.descuentoPorProducto;
      } else {
        descuentoTotal = producto.descuento;
      }
      float sub_total = producto.cantidadPedida * producto.precio;
      float valor_descuento = sub_total * descuentoTotal;
      float valor_iva = (sub_total - valor_descuento) * (producto.iva / 100);
      encabezado.sub_total += sub_total;
      encabezado.total_iva += valor_iva;
      encabezado.valor_descuento += valor_descuento;
      if (cliente.tipoCliente == 2 && encabezado.sub_total >= encabezado.valorAplicaRetefuntesIca) { //SE VALIDA QUE EL MONTO SEA MAYOR A LOS 925MIL
        retefuente = (encabezado.sub_total - encabezado.valor_descuento) * encabezado.retefuente;
        reteIca = (encabezado.sub_total - encabezado.valor_descuento) * encabezado.reteIca;
        
      } else if (cliente.tipoCliente == 1) {
        retefuente = (encabezado.sub_total - encabezado.valor_descuento) * encabezado.retefuente;
        reteIca = (encabezado.sub_total - encabezado.valor_descuento) * encabezado.reteIca;
        
      }
      encabezado.total_retefuente = retefuente;
      encabezado.total_reteIca = reteIca;
    }
    encabezado.valor_neto = encabezado.sub_total + encabezado.total_iva - encabezado.total_retefuente - encabezado.valor_descuento - encabezado.total_reteIca;
    encabezado.str_valor_neto = Util.SepararMilesSin(Util.RedondearFit(Util.QuitarE("" + encabezado.valor_neto), 0));
    ((TextView) findViewById(R.id.txtTotal)).setText(getString(R.string.sub_total_venta) + Util.SepararMiles(Util.RedondearFit(Util.QuitarE("" + encabezado.sub_total), 2)));
  }
  
  public void MostrarDialogImprimirFactura() {
    dialogImprimirFacturaPedido = new Dialog(this);
    dialogImprimirFacturaPedido.setContentView(R.layout.dialog_imprimir_factura_pedido);
    dialogImprimirFacturaPedido.setTitle("Imprimir Factura");
    ((EditText) dialogImprimirFacturaPedido.findViewById(R.id.txtNumCopias)).setText("1");
    ((Button) dialogImprimirFacturaPedido.findViewById(R.id.btnAceptarImprimirFactura)).setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        String txt = ((EditText) dialogImprimirFacturaPedido.findViewById(R.id.txtNumCopias)).getText().toString();
        int numCopias = Util.ToInt(txt);
        /*
         * Si se presenta un error con el numero asignar una copia para
         * imprimir.
         */
        if (numCopias == 0) {
          numCopias = 1;
        }
        progressDialog = ProgressDialog.show(FormPedidosAutoventa.this, "", "Por Favor Espere...\n\nProcesando Informacion!", true);
        progressDialog.show();
        SharedPreferences settings = getSharedPreferences(Const.CONFIG_IMPRESORA, MODE_PRIVATE);
        String macImpresora = settings.getString(Const.MAC_IMPRESORA, "-");
        if (macImpresora.equals("-")) {
          if (progressDialog != null) {
            progressDialog.dismiss();
          }
          Util.MostrarAlertDialog(FormPedidosAutoventa.this, "Aun no hay Impresora Establecida.\n\nPor Favor primero Configure la Impresora!");
        } else {
          Usuario usuario = DataBaseBO.CargarUsuario();
          Cliente cliente = DataBaseBO.buscarClientePorEncabezadoVenta(encabezado.numero_doc);
          imprimir_WSP_R240(macImpresora, encabezado.numero_doc, cliente, usuario, numCopias);
          // imprimirTirilla( macImpresora,
          // encabezado.numero_doc, numCopias );
        }
      }
    });
    ((Button) dialogImprimirFacturaPedido.findViewById(R.id.btnCancelarImprimirFactura)).setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        dialogImprimirFacturaPedido.cancel();
        // progressDialog =
        // ProgressDialog.show(FormPedidosAutoventa.this,
        // "","Enviando Informacion...", true);
        // progressDialog.show();
        // progressDialog.setIndeterminate(true);
        // progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_icon_drawable_animation));
        // Sync sync = new Sync(FormPedidosAutoventa.this,
        // Const.ENVIAR_PEDIDO);
        // sync.start();
        finalizar(false);
      }
    });
    dialogImprimirFacturaPedido.setCancelable(false);
    dialogImprimirFacturaPedido.show();
  }
  
  public void imprimirTirilla(final String macPrinter, final String numeroDoc, final int numCopias) {
    new Thread(new Runnable() {
      public void run() {
        mensaje = "";
        BluetoothSocket socket = null;
        try {
          Looper.prepare();
          BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
          if (bluetoothAdapter == null) {
            mensaje = "No hubo conexion con la impresora.\n\nPor Favor intente de nuevo.";
          } else if (!bluetoothAdapter.isEnabled()) {
            mensaje = "No hubo conexion con la impresora.\n\nPor Favor intente de nuevo.";
          } else {
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
            } else {
              int state = printer.getBondState();
              if (state == BluetoothDevice.BOND_BONDED) {
                UUID uuid = UUID.fromString(FormConfigPrinter.UUID_BPP);
                socket = printer.createRfcommSocketToServiceRecord(uuid);
                if (socket != null) {
                  socket.connect();
                  synchronized (this) {
                    wait(Const.timeWait);
                  }
                  for (int i = 0; i < (1 + numCopias); i++) {
                    OutputStream stream = socket.getOutputStream();
                    String strPrint = PrinterBO.formatoVenta(numeroDoc, i);
                    stream.write(strPrint.getBytes("UTF-8"));
                    Thread.sleep(1500);
                  }
                  handlerFinish.sendEmptyMessage(0);
                } else {
                  mensaje = "No se pudo abrir la conexion con la Impresora.\n\nPor Favor intente de nuevo.";
                }
              } else {
                mensaje = "La Impresora: " + macPrinter + " Actualmente no esta Acoplada con el Dispositivo Movil.\n\nPor Favor configure primero la impresora.";
              }
            }
          }
          if (!mensaje.equals("")) {
            context = FormPedidosAutoventa.this;
            handlerMensaje.sendEmptyMessage(0);
          }
          Looper.myLooper().quit();
        } catch (Exception e) {
          String motivo = e.getMessage();
          mensaje = "No se pudo ejecutar la Impresion.";
          if (motivo != null) {
            mensaje += "\n\n" + motivo;
          }
          context = FormPedidosAutoventa.this;
          handlerMensaje.sendEmptyMessage(0);
        } finally {
          try {
            if (socket != null) {
              socket.close();
            }
          } catch (Exception e) {
          }
        }
      }
    }).start();
  }
  
  public void imprimir(final BluetoothSocket socket, final String strPrint, final int copia, final int numCopias) {
    Thread printerThread = new Thread() {
      @Override
      public void run() {
        mensaje = "";
        try {
          if (copia == 0) {
            synchronized (this) {
              wait(Const.timeWait);
            }
          } else {
            synchronized (this) {
              wait(1500);
            }
          }
          OutputStream stream = socket.getOutputStream();
          stream.write(strPrint.getBytes());
          if ((copia + 1) == numCopias) {
            stream.flush();
            stream.close();
            socket.close();
          }
        } catch (Exception ex) {
          mensaje = "No se pudo imprimir la Prueba.\n\nPor favor intente de nuevo.";
        }
        if (mensaje.equals("")) {
          if ((copia + 1) == numCopias) {
            if (handlerFinish != null) {
              handlerFinish.sendEmptyMessage(0);
            } else {
              if (progressDialog != null) {
                progressDialog.cancel();
              }
            }
          }
        } else {
          if (handlerMensaje != null) {
            // initContext();
            handlerMensaje.sendEmptyMessage(0);
          } else {
            if (progressDialog != null) {
              progressDialog.cancel();
            }
          }
        }
      }
    };
    printerThread.start();
  }
  
  Handler handlerMensaje = new Handler() {
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
  
  public void salir() {
    AlertDialog alertDialog;
    // ContextThemeWrapper ctw = new ContextThemeWrapper( this,
    // R.style.Theme_Dialog_Translucent );
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        dialog.cancel();
        finalizar(false);
      }
    }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        dialog.cancel();
      }
    });
    alertDialog = builder.create();
    alertDialog.setMessage("Desea Salir. Se eliminara la informacion que hubiese ingresado");
    alertDialog.show();
  }
  
  /**
   * Imprimir en la impresora WR-240, debidamente establecida con
   * anterioridad.
   *
   * @param mac
   * @param numeroDoc
   * @param usuario
   * @param numCopias
   * @param cliente2
   */
  private void imprimir_WSP_R240(final String mac, final String numeroDoc, final Cliente clienteEncabezado, final Usuario usuario, final int numCopias) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        Looper.prepare();
        SharedPreferences settings = getSharedPreferences(CONFIG_IMPRESORA, MODE_PRIVATE);
        final String macImpresora = settings.getString(MAC_IMPRESORA, "-");
        if (macImpresora.equals("-")) {
          progressDialog.dismiss();
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              Toast.makeText(FormPedidosAutoventa.this, "Aun no hay Impresora Predeterminada.\n\nPor Favor primero Configure la Impresora!", Toast.LENGTH_SHORT).show();
            }
          });
        } else {
          if (wR240 == null) {
            wR240 = new WoosimR240(FormPedidosAutoventa.this);
          }
          int conect = wR240.conectarImpresora(macImpresora);
          switch (conect) {
            case 1:
              ControlImpresion control = new ControlImpresion();
              /*
               * lista que contendra los detalles que seran impresos
               */
              DetalleImprimir detalleImprimir = new DetalleImprimir();
              ArrayList<DetalleProducto> listaDetalleProductos = new ArrayList<DetalleProducto>();
              String numeroFactura = DataBaseBO.cargarDetallesImprimir(numeroDoc, detalleImprimir, listaDetalleProductos, control, cliente.listaPrecio);
              if (detalleImprimir != null && detalleImprimir.getEncabezado() != null && listaDetalleProductos != null && !listaDetalleProductos.isEmpty()) {
                for (int i = 0; i < numCopias; i++) {
                  if (i > 0) {
                    control.original = false;
                  }
                  wR240.generarEncabezadoTirilla(numeroFactura, clienteEncabezado, control.fechaVenta, control.original);
                  wR240.generarDetalleTirilla(detalleImprimir);
                  int succes = wR240.imprimirBuffer(true);
                  if ((control.original) && succes == 1) {
                    DataBaseBO.marcarComoCopiaProximaImpresion(numeroDoc, usuario);
                  }
                  try {
                    Thread.sleep(Const.timeWait);
                  } catch (InterruptedException e) {
                    e.printStackTrace();
                  }
                }
              } else {
                progressDialog.dismiss();
                runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    Toast.makeText(FormPedidosAutoventa.this, "Aun no hay datos para imprimir, revise el pedido.", Toast.LENGTH_SHORT).show();
                  }
                });
              }
              break;
            case -2:
              runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  Toast.makeText(FormPedidosAutoventa.this, "-2 fallo conexion", Toast.LENGTH_SHORT).show();
                }
              });
              break;
            case -8:
              progressDialog.dismiss();
              runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  Util.MostrarAlertDialog(FormPedidosAutoventa.this, "Bluetooth apagado. Por favor habilite el bluetoth para imprimir.");
                }
              });
              break;
            default:
              runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  Toast.makeText(FormPedidosAutoventa.this, "error desconocido", Toast.LENGTH_SHORT).show();
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
            progressDialog.dismiss();
            handlerFinish.sendEmptyMessage(0);
          }
        }
        Looper.myLooper().quit();
      }
    }).start();
  }
  
  /**
   * Mostrar mensaje de alerta al usuario.
   *
   * @param titulo
   * @param mensaje
   */
  private void mostrarAlertDialog(String titulo, String mensaje) {
    AlertDialog.Builder builder = new AlertDialog.Builder(FormPedidosAutoventa.this);
    builder.setCancelable(false);
    builder.setIcon(R.drawable.alert);
    builder.setTitle(titulo);
    builder.setMessage(mensaje);
    builder.setPositiveButton("Aceptar", new Dialog.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        FormPedidosAutoventa.this.finish();
      }
    });
    AlertDialog alert = builder.create();
    alert.show();
  }
  
  private void mostrarAlertDialogDescuento(String mensaje) {
    AlertDialog.Builder builder = new AlertDialog.Builder(FormPedidosAutoventa.this);
    builder.setCancelable(false);
    builder.setMessage(mensaje);
    builder.setPositiveButton("Aceptar", new Dialog.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });
    AlertDialog alert = builder.create();
    alert.show();
  }
  
  public void limpiarDatos() {
    devolverTotalCantidadInventario();
    // limpiar listaProductos
    listaProductos = null;
    // limpiar productosPedido
    productosPedido = null;
    // dejar en cero la cantidad de productos.
    cantidadProductos = 0;
    // limpiar cliente actual
    cliente = null;
    // DataBaseBO.eliminarVista();
    DataBaseBO.cancelarPedidoAv();
    finish();
    
  }

  private void validarUbicacion() {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, new String[]{
              Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
    } else {
      capturarCoordenadas();
    }
  }

  private void capturarCoordenadas() {
    LocationManager mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    Localizacion local = new Localizacion();
    local.setMainActivity(this, mLocManager);
    final boolean gpsEnabled = mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    if (!gpsEnabled) {
      Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
      startActivity(settingsIntent);
    }

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
      return;
    }

    mLocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) local);

    mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) local);
  }

  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    if (requestCode == 1000) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        capturarCoordenadas();
        return;
      }
    }
  }

  public class Localizacion implements LocationListener {
    FormPedidosAutoventa formPedidosAutoventa;
    LocationManager lManager;

    public FormPedidosAutoventa getMainActivity() {
      return formPedidosAutoventa;
    }

    public void setMainActivity(FormPedidosAutoventa mainActivity, LocationManager manager) {
      this.formPedidosAutoventa = mainActivity;
      this.lManager = manager;
    }

    @Override
    public void onLocationChanged(Location loc) {
      // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
      // debido a la deteccion de un cambio de ubicacion
      loc.getLatitude();
      loc.getLongitude();

      //Removemos para calcular solo la primera posicion
      lManager.removeUpdates(this);

      latitudActual = loc.getLatitude();
      longitudActual = loc.getLongitude();

    }

    @Override
    public void onProviderDisabled(String provider) {
      // Este metodo se ejecuta cuando el GPS es desactivado
      //Util.MostrarAlertDialog(FormSubMenu.this, "GPS Desactivado");
    }

    @Override
    public void onProviderEnabled(String provider) {
      // Este metodo se ejecuta cuando el GPS es activado
      //Util.MostrarAlertDialog(FormSubMenu.this, "GPS Activado");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
      switch (status) {
        case LocationProvider.AVAILABLE:
          break;
        case LocationProvider.OUT_OF_SERVICE:
          break;
        case LocationProvider.TEMPORARILY_UNAVAILABLE:
          break;
      }
    }
  }
  
}// final de la clase
