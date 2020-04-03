package co.com.BusinessObject;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import android.R;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;
import co.com.DataObject.AutoCuadre;
import co.com.DataObject.Banco;
import co.com.DataObject.BotonesMenu;
import co.com.DataObject.CargueInventario;
import co.com.DataObject.Cartera;
import co.com.DataObject.Cliente;
import co.com.DataObject.ClienteNuevo;
import co.com.DataObject.ControlImpresion;
import co.com.DataObject.Convencion;
import co.com.DataObject.Coordenada;
import co.com.DataObject.Departamento;
import co.com.DataObject.Deposito;
import co.com.DataObject.Detalle;
import co.com.DataObject.DetalleDeposito;
import co.com.DataObject.DetalleImprimir;
import co.com.DataObject.DetalleProducto;
import co.com.DataObject.DetalleRecaudo;
import co.com.DataObject.DevolucionesRealizados;
import co.com.DataObject.Encabezado;
import co.com.DataObject.EncabezadoRecaudo;
import co.com.DataObject.EstadisticaRecorrido;
import co.com.DataObject.Impresion;
import co.com.DataObject.ImpresionCliente;
import co.com.DataObject.ImpresionDetalle;
import co.com.DataObject.InformeInventario;
import co.com.DataObject.InformeInventario2;
import co.com.DataObject.Inventario;
import co.com.DataObject.InventarioSugerido;
import co.com.DataObject.ItemListView;
import co.com.DataObject.Kardex;
import co.com.DataObject.Linea;
import co.com.DataObject.MotivoCambio;
import co.com.DataObject.MotivoCompra;
import co.com.DataObject.Producto;
import co.com.DataObject.Resolucion;
import co.com.DataObject.SaldoCancelado;
import co.com.DataObject.Usuario;
import co.com.DataObject.Venta;
import co.com.panificadoracountry.Const;
import co.com.panificadoracountry.Main;
import co.com.panificadoracountry.NumeroAleatorios;
import co.com.panificadoracountry.Util;

public class DataBaseBO {

	public static final String TAG = "BusinessObject.DataBaseBO";

	private static File dbFile;
	public static String mensaje;

	public static boolean ExisteDataBase() {

		File dbFile = new File(Util.DirApp(), "DataBase.db");
		return dbFile.exists();
	}

	public static String ObtenerCodigoDeUsuario() {

		mensaje = "";
		String usuario = "";
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				String query = "SELECT cod_usuario as codigo FROM Vendedor";
				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					usuario = cursor.getString(cursor.getColumnIndex("codigo"));

					mensaje = "Cargo Informacion del Usuario Correctamente";
				}

				if (cursor != null)
					cursor.close();

			} else {

				Log.e("DatabaseBO", "ObtenerCodigoDeUsuario -> no existe base de datos ");
			}

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e("DatabaseBO", "ObtenerCodigoDeUsuario -> " + mensaje, e);

		} finally {

			if (db != null)
				db.close();
		}

		return usuario;
	}

	public static boolean CargarInfomacionUsuario() {

		mensaje = "";
		boolean cargo = false;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				String query = " SELECT cod_usuario as codigo, nombre AS nombre, fechaLabores,cod_canal_venta as cod_canal_venta ,0 as pedido_minimo "
						+ " FROM Vendedor ";
				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					Main.usuario = null;
					System.gc();

					Main.usuario = new Usuario();
					Main.usuario.bodega = "";
					Main.usuario.codigoVendedor = cursor.getString(cursor.getColumnIndex("codigo"));
					Main.usuario.nombreVendedor = cursor.getString(cursor.getColumnIndex("nombre"));
					Main.usuario.fechaLabores = cursor.getString(cursor.getColumnIndex("fechaLabores"));
					Main.usuario.canalVenta = cursor.getString(cursor.getColumnIndex("cod_canal_venta"));
					Main.usuario.pedido_minimo = cursor.getInt(cursor.getColumnIndex("pedido_minimo"));
					cargo = true;
				}

				if (cursor != null)
					cursor.close();

			} else {

				Log.e(TAG, "CargarInfomacionUsuario: No Existe la Base de Datos DataBase.db o No tiene Acceso a la SD");
			}

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, "CargarInfomacionUsuario: " + mensaje, e);

		} finally {

			if (db != null)
				db.close();
		}

		return cargo;
	}

	public static boolean LogIn(String usuario, String password) {

		boolean existe = false;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = "SELECT cod_usuario  FROM Usuario WHERE cod_usuario = '" + usuario + "' AND password = '"
					+ password + "'";
			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				existe = true;
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}

		return existe;
	}

	public static void ValidarUsuario() {

		if (Main.usuario == null)
			Main.usuario = new Usuario();

		if (Main.usuario.codigoVendedor == null || Main.usuario.nombreVendedor == null
				|| Main.usuario.fechaConsecutivo == null) {

			Usuario usuario = ObtenerUsuario();

			if (usuario != null) {

				Main.usuario.codigoVendedor = usuario.codigoVendedor;
				Main.usuario.nombreVendedor = usuario.nombreVendedor;
				Main.usuario.fechaLabores = usuario.fechaLabores;
				Main.usuario.fechaConsecutivo = usuario.fechaConsecutivo;
				Main.usuario.canalVenta = usuario.canalVenta;
				// Main.usuario.gps = usuario.gps;
				// Main.usuario.pedidoMinimo = usuario.pedidoMinimo;
			}
		}
	}

	public static Usuario ObtenerUsuario() {

		mensaje = "";
		Usuario usuario = null;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

				String query = " SELECT cod_usuario as codigo, nombre AS nombre, fechaLabores,cod_canal_venta as cod_canal_venta ,0 as pedido_minimo,  "
						+ " cuenta_contable as cuenta_contable FROM Vendedor ";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					usuario = new Usuario();
					usuario.bodega = "";
					usuario.codigoVendedor = cursor.getString(cursor.getColumnIndex("codigo"));
					usuario.nombreVendedor = cursor.getString(cursor.getColumnIndex("nombre"));
					usuario.fechaLabores = cursor.getString(cursor.getColumnIndex("fechaLabores"));
					usuario.canalVenta = cursor.getString(cursor.getColumnIndex("cod_canal_venta"));
					usuario.pedido_minimo = cursor.getInt(cursor.getColumnIndex("pedido_minimo"));
					usuario.cuenta_contable = cursor.getString(cursor.getColumnIndex("cuenta_contable"));
					mensaje = "Cargo Informacion del Usuario Correctamente";
				}

				if (cursor != null)
					cursor.close();
			}
		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, "ObtenerUsuario -> " + mensaje, e);

		} finally {

			if (db != null)
				db.close();
		}

		return usuario;
	}

	public static boolean HayInformacionXEnviar() {

		mensaje = "";
		SQLiteDatabase db = null;
		boolean hayInfoPendiente = false;

		try {

			File dbFile = new File(Util.DirApp(), "Temp.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				Vector<String> tableNames = new Vector<String>();
				String query = "SELECT tbl_name FROM sqlite_master";
				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					do {

						String tableName = cursor.getString(cursor.getColumnIndex("tbl_name"));

						if (tableName.equals("android_metadata"))
							continue;

						tableNames.addElement(tableName);

					} while (cursor.moveToNext());
				}

				if (cursor != null)
					cursor.close();

				for (String tableName : tableNames) {

					if (tableName.startsWith("02")) {

						query = "SELECT COUNT(*) AS total FROM [" + tableName + "]";

					} else {

						query = "SELECT COUNT(*) AS total FROM " + tableName;

					}
					cursor = db.rawQuery(query, null);

					if (cursor.moveToFirst()) {

						int total = cursor.getInt(cursor.getColumnIndex("total"));

						if (total > 0) {

							hayInfoPendiente = true;
							break;
						}
					}

					if (cursor != null)
						cursor.close();
				}

				if (cursor != null)
					cursor.close();

			} else {

				Log.e(TAG, "La base datos Temp.db No Existe o No tiene Acceso");
			}

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, "HayInformacionXEnviar: " + mensaje, e);

		} finally {

			if (db != null)
				db.close();
		}

		return hayInfoPendiente;
	}

	public static String ObtenerUsuarioActual() {

		SQLiteDatabase db = null;
		String usuarioActual = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = "SELECT cod_usuario as usuario FROM Usuario";
			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				usuarioActual = cursor.getString(cursor.getColumnIndex("usuario"));
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}

		return usuarioActual;
	}

	public static void setAppAutoventa() {

		if (ExisteDataBase()) {

			SQLiteDatabase db = null;
			String sql = "";

			String result = "";

			Const.tipoAplicacion = 0;

			try {

				File dbFile = new File(Util.DirApp(), "DataBase.db");

				if (dbFile.exists()) {

					db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

					sql = "select cod_canal_venta from vendedor limit 1";

					Cursor cursor = db.rawQuery(sql, null);

					if (cursor.moveToFirst()) {

						result = cursor.getString(cursor.getColumnIndex("cod_canal_venta"));
					}

					if (cursor != null)
						cursor.close();

					if (result.toUpperCase(Locale.getDefault()).equals("1")) {

						Const.tipoAplicacion = Const.AUTOVENTA;
					} else {

						Const.tipoAplicacion = Const.PREVENTA;
					}
				}
			} catch (Exception e) {

			} finally {

				closeDataBase(db);
			}

		} else {

			ConfigBO.setAppAutoventa();
		}
	}

	public static void closeDataBase(SQLiteDatabase db) {

		if (db != null) {

			if (db.inTransaction())
				db.endTransaction();

			db.close();
		}
	}

	public static boolean ExisteInformacion(String nombreTabla) {

		boolean existe = false;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (!dbFile.exists())
				return false;

			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
			String consulta = "SELECT COUNT(*) AS total FROM " + nombreTabla;
			Cursor cursor = db.rawQuery(consulta, null);

			if (cursor.moveToFirst()) {

				int total = cursor.getInt(cursor.getColumnIndex("total"));
				existe = total > 0;
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e("ExisteInformacion", mensaje, e);

		} finally {

			if (db != null)
				db.close();
		}

		return existe;
	}

	public static String ObtenerVersionApp() {

		String version = "";
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = "SELECT version FROM version";
			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				version = cursor.getString(cursor.getColumnIndex("version"));
			}

			Log.i("ObtenerVersionApp", "version = " + version);

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, "ObtenerVersionApp: " + mensaje, e);

		} finally {

			if (db != null)
				db.close();
		}

		return version;
	}

	public static Vector<Cliente> ListaClientesRutero(Vector<ItemListView> listaItems, String codDia) {

		mensaje = "";
		Cliente cliente;
		SQLiteDatabase db = null;

		ItemListView itemListView;
		Vector<Cliente> listaClientes = new Vector<Cliente>();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = " select distinct r.cod_cliente  as codigo,c.representante_legal as nombre, c.razon_social as razonSocial,direccion as direccion,cod_lista as cod_lista,c.ciudad,c.barrio,c.telefono from clientes as c "
					+ " inner join rutero r on r.cod_cliente = c.cod_cliente " + " where r.cod_dia = " + codDia
					+ " and codigo not in (select distinct cod_cliente from novedades) "
					+ " order by r.orden_visita ASC ";

			int contador = 1;

			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				do {

					cliente = new Cliente();
					itemListView = new ItemListView();

					cliente.codigo = cursor.getString(cursor.getColumnIndex("codigo")).trim();
					cliente.nombre = cursor.getString(cursor.getColumnIndex("nombre")).trim();
					cliente.razonSocial = cursor.getString(cursor.getColumnIndex("razonSocial")).trim();
					cliente.direccion = cursor.getString(cursor.getColumnIndex("direccion")).trim();
					cliente.codLista = cursor.getString(cursor.getColumnIndex("cod_lista")).trim();
					cliente.ciudad = cursor.getString(cursor.getColumnIndex("ciudad")).trim();
					cliente.telefono = cursor.getString(cursor.getColumnIndex("telefono")).trim();
					cliente.barrio = cursor.getString(cursor.getColumnIndex("barrio")).trim();

					itemListView.titulo = contador + ") " + cliente.codigo + " " + cliente.nombre + "\n"
							+ cliente.razonSocial;
					itemListView.subTitulo = cliente.ciudad + " - " + cliente.direccion;

					listaItems.add(itemListView);
					listaClientes.addElement(cliente);

					contador++;

				} while (cursor.moveToNext());

				mensaje = "Rutero Cargado Correctamente";

			} else {

				mensaje = "Consulta sin Resultados";
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}

		return listaClientes;
	}

	public static boolean ExistePedidoCliente(String codigoCliente) {

		mensaje = "";
		int total = 0;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = "SELECT COUNT(*) AS total " + "FROM [encabezado_venta] " + "WHERE cod_cliente = '"
					+ codigoCliente + "' ";

			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				do {

					total = cursor.getInt(cursor.getColumnIndex("total"));

				} while (cursor.moveToNext());

				mensaje = "Consulta Satisfactoria";
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e("ExistePedidoCliente", mensaje, e);

		} finally {

			if (db != null)
				db.close();
		}

		return total > 0;
	}

	public static Usuario CargarUsuario() {

		mensaje = "";
		Usuario usuario = null;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = " SELECT cod_usuario as codigo, nombre AS nombre, fechaLabores,"
					+ "substr(coalesce(nombre,'-'),0,21) AS nombreImprimir,cod_canal_venta as cod_canal_venta ,0 as pedido_minimo "
					+ " FROM Vendedor ";

			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				usuario = new Usuario();
				usuario.bodega = "";
				usuario.codigoVendedor = cursor.getString(cursor.getColumnIndex("codigo"));
				usuario.nombreVendedor = cursor.getString(cursor.getColumnIndex("nombre"));
				usuario.fechaLabores = cursor.getString(cursor.getColumnIndex("fechaLabores"));
				usuario.nombreImprimir = cursor.getString(cursor.getColumnIndex("nombreImprimir"));
				usuario.canalVenta = cursor.getString(cursor.getColumnIndex("cod_canal_venta"));
				usuario.pedido_minimo = cursor.getInt(cursor.getColumnIndex("pedido_minimo"));

				mensaje = "Cargo Informacion del Usuario Correctamente";

			} else {

				mensaje = "Consulta sin resultados";
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e("CargarUsuario", mensaje, e);

		} finally {

			if (db != null)
				db.close();
		}

		return usuario;
	}

	public static Vector<BotonesMenu> CargarOpciones(int codModulo) {

		mensaje = "";
		BotonesMenu botonMenu = null;
		Vector<BotonesMenu> listadoBotones = new Vector<BotonesMenu>();
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = " SELECT cod_sub_modulo, descripcion " + " FROM modulos where cod_modulo =  " + codModulo;

			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				do {

					botonMenu = new BotonesMenu();
					botonMenu.id = cursor.getInt(cursor.getColumnIndex("cod_sub_modulo"));
					botonMenu.texto = cursor.getString(cursor.getColumnIndex("descripcion"));
					listadoBotones.add(botonMenu);

				} while (cursor.moveToNext());

			} else {

				mensaje = "Consulta sin resultados";
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}

		return listadoBotones;
	}

	public static int ObtenerNumeroColumnas() {

		mensaje = "";
		int numeroColumnas = 2;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				String query = "SELECT numero_columnas FROM parametrosMovil";
				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					numeroColumnas = cursor.getInt(cursor.getColumnIndex("numero_columnas"));

				}

				if (cursor != null)
					cursor.close();

			} else {

			}

		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}

		if (numeroColumnas < 2 || numeroColumnas > 3)
			numeroColumnas = 2;

		return numeroColumnas;
	}

	/**
	 * metodo que carga la lista de productos que seran mostrados al usuario a
	 * medida que avanza en el navegador de productos.
	 * 
	 * @param listaProductos
	 * @return
	 * @by JICZ
	 */
	public static boolean cargarProductosDisponiblesAutoventa(List<Producto> listaProductos, String codLista) {
		SQLiteDatabase db = null;
		boolean finalizado = false;
		try {
			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			/*
			 * Consulta para obtener la lista de productos disponibles para
			 * autoventa
			 */
			String query = "select cod_producto,nombre,precio,iva,descuento,cod_linea from productos WHERE CodigoLista = '"
					+ codLista
					+ "' ORDER  BY CAST(REPLACE(REPLACE(REPLACE(productos.cod_producto, 'PF', ''), 'PFM', ''), 'M', '') AS INTEGER) ASC";

			// String query = "select
			// cod_producto,nombre,precio,iva,descuento,cod_linea from productos
			// ";
			System.out.println("Consulta productos: " + query);

			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				do {
					Producto producto = new Producto();
					producto.codigo = cursor.getString(cursor.getColumnIndex("cod_producto"));
					producto.descripcion = cursor.getString(cursor.getColumnIndex("nombre"));
					producto.precio = cursor.getFloat(cursor.getColumnIndex("precio"));
					producto.iva = cursor.getFloat(cursor.getColumnIndex("iva"));
					producto.descuento = cursor.getFloat(cursor.getColumnIndex("descuento"));
					producto.codLinea = cursor.getString(cursor.getColumnIndex("cod_linea"));

					if (Const.tipoAplicacion == Const.AUTOVENTA) {
						// ***producto.cantidadInv = getInventarioProd2(db,
						// producto);

						InformeInventario2 infInv = ObtenerInventarioProd(db, producto.codigo);

						if (infInv != null)
							producto.cantidadInv = infInv.invActual;
						else
							producto.cantidadInv = 0;

					}

					/* agregar producto a la lista */
					listaProductos.add(producto);

				} while (cursor.moveToNext());
			}

			// cerrar cursor.
			if (cursor != null) {
				cursor.close();
			}

			/* verificar que la lista de productos no este vacia */
			if (!listaProductos.isEmpty()) {
				finalizado = true;
			}
		} catch (Exception e) {
			mensaje = e.getMessage();
		} finally {
			if (db != null)
				db.close();
		}
		return finalizado;
	}

	/**
	 * cargar la informacion de inventario del producto.
	 * 
	 * @param db
	 * @param producto
	 * @return
	 */
	public static float getInventarioProd(SQLiteDatabase db, Producto producto) {

		String sql = "";
		float inv = 0;

		try {

			sql = "SELECT i.[cantidad] AS cantidad , " + " 	  i.[lote] AS lote, "
					+ "	  i.[fecha_vencimiento] AS fechaVenc, " + " 	  i.[fecha_fabricacion] AS fechaFab  "
					+ "FROM inventario i " + "WHERE cod_producto = '" + producto.codigo + "' AND i.[cantidad] > 0 "
					+ "ORDER BY [fecha_vencimiento]";

			Cursor cursor = db.rawQuery(sql, null);

			if (cursor.moveToFirst()) {

				do {
					Inventario i = new Inventario();
					i.cantidad = cursor.getInt(cursor.getColumnIndex("cantidad"));
					i.lote = cursor.getString(cursor.getColumnIndex("lote"));
					i.fechaVencimiento = cursor.getString(cursor.getColumnIndex("fechaVenc"));
					i.fechaFabricacion = cursor.getString(cursor.getColumnIndex("fechaFab"));
					/* agregamos las cantidades disponibles al producto. */
					producto.getListaInventario().add(i);
				} while (cursor.moveToNext());
			}

			if (cursor != null)
				cursor.close();
		} catch (Exception e) {

		} finally {

			for (Inventario i : producto.getListaInventario()) {
				inv += i.cantidad;
			}
		}
		return inv;
	}

	/**
	 * Metodo para insertar un producto pedido por un cliente. se guarda con un
	 * numero documento temporal. esto con el fin de cancelar en pedido en
	 * cualquier momento.
	 * 
	 * @param producto
	 * @throws SQLException
	 */
	public static boolean agregarProductoDetalleAv(Producto producto, String canal) throws SQLException {

		SQLiteDatabase db = null;
		SQLiteDatabase tmp = null;
		boolean state = false;

		// se inserta en la base de datos.
		long rowDb = -2;
		long rowTmp = -4;

		try {
			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			File dbTmp = new File(Util.DirApp(), "Temp.db");
			tmp = SQLiteDatabase.openDatabase(dbTmp.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			// iniciar transacciones para garantizar la correcta insercion en
			// las base de datos.
			db.beginTransaction();
			tmp.beginTransaction();

			/*
			 * captura de los datos que seran insertados en las bases de datos.
			 */
			ContentValues values = new ContentValues();
			values = new ContentValues();

			/*
			 * CONSTANTE TEMPORAL, LUEGO SERA ACTUALIZADA CUANDO SE GUARDE EL
			 * PEDIDO DE FORMA DEFINITIVA.
			 */
			values.put("documento", Const.NUM_TEMP);
			values.put("cod_producto", producto.codigo);
			values.put("cod_linea", producto.codLinea);
			values.put("precio", producto.precio);
			values.put("iva", (producto.iva) / 100);
			values.put("descuento", producto.descuento);
			values.put("cantidad", producto.cantidadPedida);
			values.put("cod_motivo", "");
			values.put("cod_canal_venta", canal);
			values.put("lote", producto.lote);
			values.put("fecha_vencimiento", producto.fechaVencimiento);
			values.put("fecha_fabricacion", producto.fechaFabricacion);
			values.put("validado", "0");

			rowDb = db.insertOrThrow("detalle_venta", null, values);
			rowTmp = tmp.insertOrThrow("detalle_venta", null, values);

			// verificar que no haya ocurrido error en la insercion
			if (rowDb == -1 || rowTmp == -1) {
				throw new SQLException(
						"Error Insertando en base de datos!\nPor favor salga de pedidos e intente de nuevo");
			} else {
				/* confirmar transacciones exitosas */
				db.setTransactionSuccessful();
				tmp.setTransactionSuccessful();
				state = true;
			}
		} catch (SQLException e) {
			Log.e("insertando producto en detalle_venta", "error: " + e.getMessage());
		} finally {
			closeDataBase(db);
			closeDataBase(tmp);
		}

		return state;
	}

	/**
	 * metodo para borrar un pedido sin terminar.
	 */
	public static void cancelarPedidoAv() {
		SQLiteDatabase db = null;
		SQLiteDatabase tmp = null;

		try {
			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			File dbTmp = new File(Util.DirApp(), "Temp.db");
			tmp = SQLiteDatabase.openDatabase(dbTmp.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			// iniciar transacciones para garantizar la correcta insercion en
			// las base de datos.
			db.beginTransaction();
			tmp.beginTransaction();

			// se hace el borrado de los productos pedidos. en ambas bases de
			// datos.
			long dbDel = db.delete("detalle_venta", "documento = '" + Const.NUM_TEMP + "'", null);
			long tmpDel = tmp.delete("detalle_venta", "documento = '" + Const.NUM_TEMP + "'", null);

			// verificar que el borrado fue correcto en ambas bases de datos. y
			// confirmar la transaccion como exitosa.
			if (dbDel == tmpDel) {
				db.setTransactionSuccessful();
				tmp.setTransactionSuccessful();
			} else {
				throw new Exception("No se logro borrar datos");
			}
		} catch (Exception e) {
			Log.e("Eliminando producto en Detalle_pedido_av", "error: " + e.getMessage());
		} finally {
			closeDataBase(db);
			closeDataBase(tmp);
		}
	}

	public static String generarNumeroDocPedidoAv(String codigoVendedor) throws Exception {

		SQLiteDatabase db = null;
		String numeroDocPedidoAv = "";

		try {
			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			/*
			 * consulta para generar la base del numero doc formato
			 * AC-codVendedor-año-mes-dia-hora-minuto-segundo
			 */
			String query = "SELECT 'PD" + codigoVendedor + "' || STRFTIME('%Y%m%d%H%M%S','now','localtime') AS numero;";

			// ejecutar consulta
			Cursor cursor = db.rawQuery(query, null);
			Log.i("generar numeroDocPedidoAv", query);

			if (cursor.moveToFirst()) {
				do {
					/* recuperacion del numero obtenido */
					numeroDocPedidoAv = cursor.getString(cursor.getColumnIndex("numero"));

				} while (cursor.moveToNext());
			}

		} catch (Exception e) {
			Log.e("generar numeroDocPedidoAv", "error: " + e.getMessage());
		}

		finally {
			if (db != null) {
				db.close();
			}

			// verificar que el numero generado es correcto, de lo contrario
			// enviar una exception
			if (numeroDocPedidoAv.equals("") || numeroDocPedidoAv.length() < 17 || numeroDocPedidoAv == null) {
				throw new Exception(
						"numeroDocPedidoAv No se genero correctamente!\nPor favor salga de pedidos e intente de nuevo");
			}
		}
		return numeroDocPedidoAv;
	}

	public static boolean guardarEncabezadoPedidoAv(String numeroDocPedidoAv, Cliente cliente, Usuario usuario,
			String fechaEntrega, String fechaCobro, String observaciones, String ordenCompra, int tipoTransaccion,
			int entregado, int factura, int tipoVentaPedido, int tipoDocumento, int estadoPedido, int estadoPago,
			Encabezado encabezadoPedido, String prefijo, int esPedidoValido) {

		SQLiteDatabase db = null;
		SQLiteDatabase tmp = null;
		boolean insertado = false;

		// se define variables para contar inserciones
		long rowDb = -2;
		long rowTmp = -4;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			File dbTmp = new File(Util.DirApp(), "Temp.db");
			tmp = SQLiteDatabase.openDatabase(dbTmp.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			// iniciar transacciones para garantizar la correcta insercion en
			// las base de datos.
			db.beginTransaction();
			tmp.beginTransaction();

			// consulta para generar el id.
			String count = "SELECT DATETIME('now', 'localtime') AS fechaSistema;";
			String fechaSistema = "";

			// ejecutar consulta
			Cursor cursor = db.rawQuery(count, null);

			/* recuperacion del numero obtenido */
			if (cursor.moveToFirst()) {
				fechaSistema = cursor.getString(cursor.getColumnIndex("fechaSistema"));
			}

			// cerrar cursor.
			if (cursor != null) {
				cursor.close();
			}

			/*
			 * captura de los datos que seran insertados en las bases de datos.
			 */
			ContentValues values = new ContentValues();
			values = new ContentValues();

			values.put("cod_cliente", cliente.codigo);
			values.put("documento", numeroDocPedidoAv);
			values.put("cod_usuario", usuario.codigoVendedor);
			values.put("fecha_movil", encabezadoPedido.hora_inicial);
			values.put("valor", Util.QuitarE("" + encabezadoPedido.valor_neto));// Util.RedondearFit(String.valueOf(encabezadoPedido.valor_neto),
																				// 0));
			values.put("iva", Util.RedondearFit(String.valueOf(encabezadoPedido.total_iva), 0));
			values.put("descuento", Util.RedondearFit(String.valueOf(encabezadoPedido.valor_descuento), 0));
			values.put("retencion_fuente", String.valueOf(encabezadoPedido.retefuente));
			values.put("valor_retencion", Util.RedondearFit(String.valueOf(encabezadoPedido.total_retefuente), 0));
			values.put("retencion_ica", String.valueOf(encabezadoPedido.reteIca));
			values.put("valor_retencion_ica", encabezadoPedido.total_reteIca);
			values.put("orden_compra", ordenCompra);
			values.put("fecha_entrega", fechaEntrega);
			values.put("observacion", observaciones);
			values.put("cod_canal_venta", usuario.canalVenta);
			values.put("cod_est_pedido", 1);
			values.put("cod_tipo_trans", tipoTransaccion);
			values.put("resolucion", encabezadoPedido.resolucion);
			values.put("prefijo", prefijo);
			values.put("consecutivo", encabezadoPedido.consecutivoResolucion);
			values.put("sync", 0);
			values.put("copia", 0);
			values.put("pedidoValido", esPedidoValido);

			// se hace la atualizacion del producto pedido. en ambas bases de
			// datos.
			rowDb = db.insertOrThrow("encabezado_venta", null, values);
			rowTmp = tmp.insertOrThrow("encabezado_venta", null, values);

			// verificar que la actualizacion fue correcta en ambas bases de
			// datos. y confirmar la transaccion como exitosa.
			if (rowDb != -1 && rowTmp != -1) {

				// actualizar el detalle
				boolean actualizado = actualizarDetallePedidoAv(numeroDocPedidoAv, db, tmp);
				insertado = true;
				db.setTransactionSuccessful();
				tmp.setTransactionSuccessful();
			} else {
				throw new Exception("No se logro insertar datos de encabezado " + numeroDocPedidoAv);
			}
		} catch (Exception e) {
			Log.e("No se logró insertar datos de encabezado", "error: " + e.getMessage());
		} finally {
			closeDataBase(db);
			closeDataBase(tmp);
		}
		return insertado;
	}

	private static boolean actualizarDetallePedidoAv(String numeroDocPedidoAv, SQLiteDatabase db, SQLiteDatabase tmp) {
		boolean actualizado = false;

		/* captura de los datos que seran insertados en las bases de datos. */
		ContentValues values = new ContentValues();
		values = new ContentValues();

		values.put("documento", numeroDocPedidoAv);
		// values.put("documento_movil", numeroDocPedidoAv);

		// se hace la atualizacion del producto pedido. en ambas bases de datos.
		long dbDel = db.update("detalle_venta", values, "documento = '" + Const.NUM_TEMP + "'", null);
		long tmpDel = tmp.update("detalle_venta", values, "documento = '" + Const.NUM_TEMP + "'", null);

		// verificar que la actualizacion fue correcta en ambas bases de datos.
		// y confirmar la transaccion como exitosa.
		if (dbDel > 0 && tmpDel > 0) {
			actualizado = true;
		}
		return actualizado;
	}

	private static String calcularPrecioTotalPedidoAv(SQLiteDatabase db) {

		String precioDescuento = "0;0";

		try {

			/*
			 * consulta para generar el total del descuento y el total a cobrar
			 * por la cantidad de producto.
			 */
			String query = "" + "SELECT vp.[descuento] AS descuento, x.[grupo] AS grupo, "
					+ "(x.[Precio]) AS precio_desc, " + "(x.[descontado]) AS monto_descontado "
					+ "FROM [vista_productos] vp " + "INNER JOIN "
					+ "(SELECT SUM(ROUND((d.[precio] - (d.[precio]*d.[descuento]))) * d.[cantidad]) AS Precio, "
					+ "        SUM((d.[precio]*d.[descuento])* d.[cantidad]) AS descontado, "
					+ "        SUM(d.[cantidad]) AS cantidad, d.[grupo] as grupo " + "        FROM Detalle_pedido_av d "
					+ "        WHERE d.[documento]='" + Const.NUM_TEMP + "' GROUP BY d.[grupo]) AS x  "
					+ "ON  (x.[cantidad] >= vp.[cantidad_min] AND  x.[cantidad] <= vp.[cantidad_max]) AND (x.[grupo] = vp.[Grupo]) "
					+ "GROUP BY vp.[Grupo], vp.[cantidad_min],vp.[cantidad_max] " + "UNION "
					+ "SELECT 0 AS descuento, x.[grupo] AS grupo, "
					+ "(x.[Precio]) AS precio_desc, 0 AS monto_descontado " + "FROM "
					+ "(SELECT SUM(d.[precio] * d.[cantidad]) AS Precio, SUM(d.[cantidad]) AS cantidad , d.[grupo] as grupo "
					+ "        FROM Detalle_pedido_av d WHERE d.[documento]='" + Const.NUM_TEMP
					+ "'  GROUP BY d.[grupo]) AS x " + "WHERE x.[grupo] not in (SELECT vp.[Grupo] AS grupo "
					+ "FROM [vista_productos] vp GROUP BY vp.[Grupo]) " + "UNION "
					+ "SELECT 0 AS descuento, x.[grupo] AS grupo, "
					+ "x.[Precio] AS precio_desc, 0 AS monto_descontado " + "FROM "
					+ "(SELECT SUM(d.[precio] * d.[cantidad]) AS Precio, SUM(d.[cantidad]) AS cantidad , d.[grupo] as grupo "
					+ "        FROM Detalle_pedido_av d WHERE d.[documento]='" + Const.NUM_TEMP
					+ "'  GROUP BY d.[grupo]) AS x "
					+ "INNER JOIN (SELECT MIN(vp.[cantidad_min]) AS minimo, max(vp.[cantidad_max]) AS maximo, vp.[grupo] AS grupo "
					+ "from [vista_productos] vp GROUP BY vp.[Grupo]) AS vp ON  (x.[grupo] = vp.[Grupo]) "
					+ "AND (x.[cantidad] < vp.[minimo] OR x.[cantidad] > vp.[maximo] ) GROUP BY vp.[Grupo];";

			// ejecutar consulta
			Cursor cursor = db.rawQuery(query, null);
			Log.i("confirmar precios", query);

			// varialbes temporales para guardar precios
			double precioConDescuento = 0;
			double montoDescontado = 0;

			if (cursor.moveToFirst()) {
				do {
					/* recuperacion de precios y descuentos */
					precioConDescuento += cursor.getDouble(cursor.getColumnIndex("precio_desc"));
					montoDescontado += cursor.getDouble(cursor.getColumnIndex("monto_descontado"));
				} while (cursor.moveToNext());
			}

			// cerrar cursor.
			if (cursor != null) {
				cursor.close();
			}

			// concatenar precios, se separan por ';'
			if (precioConDescuento != 0) {
				precioDescuento = "" + precioConDescuento + ";" + montoDescontado;
			}
		} catch (Exception e) {
			Log.e("confirmar precio definitivo", "error: " + e.getMessage());
		}
		return precioDescuento;
	}

	public static boolean GuardarNovedadNoCompraAutoventa(Encabezado encabezado, String codven, String imei,
			String observacion) {

		SQLiteDatabase db = null;
		SQLiteDatabase tmp = null;

		boolean insertado = false;
		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			File filePedido = new File(Util.DirApp(), "Temp.db");
			tmp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			// inicia transaccion
			db.beginTransaction();
			tmp.beginTransaction();

			// consulta para generar la fecha del sistema
			String count = "SELECT DATETIME('now', 'localtime') AS fechaSistema, v.[version] from version v;";
			String fechaSistema = "";
			String version = "";

			// ejecutar consulta
			Cursor cursor = db.rawQuery(count, null);

			/* recuperacion del numero obtenido */
			if (cursor.moveToFirst()) {
				fechaSistema = cursor.getString(cursor.getColumnIndex("fechaSistema"));
				version = cursor.getString(cursor.getColumnIndex("version"));
			}

			// cerrar cursor.
			if (cursor != null) {
				cursor.close();
			}

			// carga de campos a insertar.
			ContentValues valuesTemp = new ContentValues();

			valuesTemp.put("cod_novedad", encabezado.codigo_cliente);
			valuesTemp.put("cod_cliente", encabezado.codigo_novedad);
			valuesTemp.put("cod_usuario", codven); // Codigo del vendedor
			valuesTemp.put("cod_tipo_nov", 0); // se inserta cero por defecto.
			valuesTemp.put("documento", fechaSistema);
			valuesTemp.put("version_movil", encabezado.numero_doc);

			long dbIn = db.insertOrThrow("novedades", null, valuesTemp);
			long tmpIn = tmp.insertOrThrow("novedades", null, valuesTemp);

			if (dbIn != -1 && tmpIn != -1) {
				db.setTransactionSuccessful();
				tmp.setTransactionSuccessful();
				insertado = true;
			}

		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {
			closeDataBase(db);
			closeDataBase(tmp);
		}
		return insertado;
	}

	public static boolean guardarNovedad(Encabezado encabezado, String numeroDoc, Cliente cliente, Usuario usuario,
			String precioTotal, String observaciones, String imei, String version, int motivo) {
		SQLiteDatabase db = null;
		SQLiteDatabase dbTemp = null;

		try {

			dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			File filePedido = new File(Util.DirApp(), "Temp.db");
			dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			ContentValues values = new ContentValues();
			values = new ContentValues();
			values.put("cod_cliente", cliente.codigo);//
			values.put("cod_tipo_nov", encabezado.codigo_novedad);//
			values.put("cod_usuario", usuario.codigoVendedor);//
			values.put("cod_novedad", encabezado.numero_doc);//
			values.put("documento", encabezado.numero_doc);
			values.put("fecha_movil", encabezado.hora_inicial);//
			values.put("version_movil", version);
			values.put("imei", imei);
			values.put("cod_canal_venta", usuario.canalVenta);
			values.put("latitud", encabezado.lat);
			values.put("longitud", encabezado.lon);

			db.insertOrThrow("novedades", null, values);
			dbTemp.insertOrThrow("novedades", null, values);

			return true;

		} catch (Exception e) {

			mensaje = e.getMessage();
			return false;

		} finally {

			if (db != null)
				db.close();

			if (dbTemp != null)
				dbTemp.close();
		}
	}

	public static int ObtenerConsecutivoVend() {

		int consecutivo = 0;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			Cursor cursor = db.rawQuery("SELECT consecutivo FROM Vendedor", null);

			if (cursor.moveToFirst())
				consecutivo = cursor.getInt(cursor.getColumnIndex("consecutivo"));

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}

		return consecutivo;
	}

	public static boolean ActualizarSyncPedidos() {

		SQLiteDatabase db = null;
		// SQLiteDatabase dbTemp = null;

		try {

			// int i = 0;
			// String in = "(";

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			/*
			 * File filePedido = new File(Util.DirApp(), "Temp.db"); dbTemp =
			 * SQLiteDatabase.openDatabase(filePedido.getPath(), null,
			 * SQLiteDatabase.OPEN_READWRITE);
			 * 
			 * Cursor cursor = dbTemp.rawQuery(
			 * "SELECT DISTINCT numeroDoc FROM Encabezado", null); if
			 * (cursor.moveToFirst()) {
			 * 
			 * do {
			 * 
			 * if (i++ > 0) in += ", ";
			 * 
			 * in += cursor.getInt(cursor.getColumnIndex("numeroDoc"));
			 * 
			 * } while(cursor.moveToNext()); }
			 * 
			 * in += ")";
			 * 
			 * if (cursor != null) cursor.close();
			 */

			db.execSQL("UPDATE encabezado_venta SET sync = 1 WHERE sync = 0");
			return true;

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, "ActualizarSyncPedidos: " + mensaje, e);
			return false;

		} finally {

			if (db != null)
				db.close();

			// if (dbTemp != null)
			// dbTemp.close();
		}
	}

	public static boolean BorrarInfoTemp() {

		SQLiteDatabase dbTemp = null;

		try {

			File dbFile = new File(Util.DirApp(), "Temp.db");

			if (dbFile.exists()) {

				dbTemp = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				Vector<String> tableNames = new Vector<String>();
				String query = "SELECT tbl_name FROM sqlite_master";
				Cursor cursor = dbTemp.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					do {

						String tableName = cursor.getString(cursor.getColumnIndex("tbl_name"));

						if (tableName.equals("android_metadata"))
							continue;

						tableNames.addElement(tableName);

					} while (cursor.moveToNext());
				}

				if (cursor != null)
					cursor.close();

				for (String tableName : tableNames) {

					if (tableName.startsWith("02")) {

						query = "DELETE FROM [" + tableName + "]";

					} else {

						query = "DELETE FROM " + tableName;

					}

					dbTemp.execSQL(query);
				}
			}

			return true;

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, "BorrarInfoTemp: " + mensaje, e);
			return false;

		} finally {

			if (dbTemp != null)
				dbTemp.close();
		}
	}

	public static String ObtenterNumeroDoc(String codVendedor) {

		String num = "-";
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			int consecutivo = 0;
			Cursor cursor = db.rawQuery("SELECT consecutivo FROM vendedor", null);

			if (cursor.moveToFirst())
				consecutivo = cursor.getInt(cursor.getColumnIndex("consecutivo"));

			if (cursor != null)
				cursor.close();

			consecutivo = (consecutivo + 1);
			db.execSQL("UPDATE vendedor SET consecutivo = " + consecutivo);

			if (cursor != null)
				cursor.close();

			// A+vendedor+anomesdia+consecutivo(4)

			num = "A" + codVendedor + Util.FechaActual("yyyyMMddHHmmss") + Util.lpad("" + consecutivo, 4, "0");
			return num;

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e("ObtenterNumeroDoc", mensaje, e);
			return "-";

		} finally {

			if (db != null)
				db.close();
		}
	}

	public static boolean GuardarNoCompraSinFoto(Encabezado encabezado, Usuario usuario, String version, String imei) {

		SQLiteDatabase db = null;
		SQLiteDatabase dbTemp = null;

		try {

			dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			File filePedido = new File(Util.DirApp(), "Temp.db");
			dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			ContentValues values = new ContentValues();
			values = new ContentValues();
			values.put("cod_cliente", encabezado.codigo_cliente);//
			values.put("cod_tipo_nov", encabezado.codigo_novedad);//
			values.put("cod_usuario", usuario.codigoVendedor);//
			values.put("cod_novedad", encabezado.numero_doc);//
			values.put("documento", encabezado.numero_doc);
			values.put("fecha_movil", encabezado.hora_inicial);//
			values.put("version_movil", version);
			values.put("imei", imei);
			values.put("cod_canal_venta", usuario.canalVenta);
			values.put("foto", 0);
			values.put("latitud", encabezado.latitud);
			values.put("longitud", encabezado.longitud);

			db.insertOrThrow("novedades", null, values);
			dbTemp.insertOrThrow("novedades", null, values);

			return true;

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, "GuardarNoCompra: " + mensaje, e);
			return false;

		} finally {

			if (db != null)
				db.close();

			if (dbTemp != null)
				dbTemp.close();
		}
	}

	public static Vector<MotivoCompra> ListaMotivosNoCompra(Vector<String> items) {

		SQLiteDatabase db = null;
		MotivoCompra motivoCompra;
		Vector<MotivoCompra> listaMotivos = new Vector<MotivoCompra>();

		try {

			if (items == null)
				items = new Vector<String>();

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			Cursor cursor = db.query("tipo_novedad", new String[] { "cod_tipo_nov", "descripcion" }, "cod_tipo_nov = 8",
					null, null, null, "cod_tipo_nov");

			if (cursor.moveToFirst()) {

				do {

					motivoCompra = new MotivoCompra();

					motivoCompra.codigo = Util.ToInt(cursor.getString(cursor.getColumnIndex("cod_tipo_nov")));
					motivoCompra.motivo = cursor.getString(cursor.getColumnIndex("descripcion"));
					motivoCompra.tipo = "nocompra";

					listaMotivos.addElement(motivoCompra);
					items.addElement(motivoCompra.motivo);

				} while (cursor.moveToNext());
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e("ListaMotivosNoCompra", mensaje, e);

		} finally {

			if (db != null)
				db.close();
		}

		return listaMotivos;
	}

	public static boolean GuardarNoCompra(Encabezado encabezado, String codVend, String version, String imei,
			byte[] image) {

		SQLiteDatabase db = null;
		SQLiteDatabase dbTemp = null;

		try {

			dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			File filePedido = new File(Util.DirApp(), "Temp.db");
			dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			ContentValues values = new ContentValues();
			values = new ContentValues();
			values.put("codigoCliente", encabezado.codigo_cliente);
			values.put("motivo", encabezado.codigo_novedad);
			values.put("vendedor", codVend);
			values.put("valor", 0);
			values.put("NroDoc", encabezado.numero_doc);
			values.put("horaInicio", encabezado.hora_inicial);
			values.put("horaFinal", encabezado.hora_final);
			values.put("version", version);
			values.put("dispositivo", "ANDROID");
			values.put("imei", imei);

			db.insertOrThrow("NovedadesCompras", null, values);
			dbTemp.insertOrThrow("NovedadesCompras", null, values);

			values = new ContentValues();
			values.put("Id", "A" + Util.ObtenerFechaId());
			values.put("Imagen", image);
			values.put("CodCliente", encabezado.codigo_cliente);
			// values.put("CodVendedor", Main.usuario.codigoVendedor);
			values.put("CodVendedor", codVend);
			values.put("modulo", 1);
			values.put("nroDoc", encabezado.numero_doc);

			dbTemp.insertOrThrow("Fotos", null, values);
			return true;

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, "GuardarNoCompra: " + mensaje, e);
			return false;

		} finally {

			if (db != null)
				db.close();

			if (dbTemp != null)
				dbTemp.close();
		}
	}

	public static Vector<Linea> ListaLineas(Vector<String> listaItems) {

		mensaje = "";
		Linea linea;

		SQLiteDatabase db = null;
		Vector<Linea> listaLineas = new Vector<Linea>();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = "SELECT cod_linea as Codigo, descripcion as Nombre FROM Linea";
			Cursor cursor = db.rawQuery(query, null);

			linea = new Linea();
			linea.nombre = "TODAS LAS LINEAS";
			linea.codigo = "0";

			listaItems.addElement(linea.nombre);
			listaLineas.addElement(linea);

			if (cursor.moveToFirst()) {

				do {

					linea = new Linea();
					linea.codigo = cursor.getString(cursor.getColumnIndex("Codigo"));
					linea.nombre = cursor.getString(cursor.getColumnIndex("Nombre"));

					listaItems.addElement(linea.nombre);
					listaLineas.addElement(linea);

				} while (cursor.moveToNext());
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, "ListaLineas: " + mensaje, e);

		} finally {

			if (db != null)
				db.close();
		}

		return listaLineas;
	}

	public static Vector<Producto> BuscarTodosProductos(String codLinea, String listaPrecio,
			Vector<ItemListView> listaItems, boolean pedido) {

		Producto producto;
		SQLiteDatabase db = null;

		ItemListView itemListView;
		Vector<Producto> listaProductos = new Vector<Producto>();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query;
			query = "select cod_producto,nombre,precio,iva,descuento,cod_linea from productos";

			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				do {

					producto = new Producto();
					itemListView = new ItemListView();

					producto.codigo = cursor.getString(cursor.getColumnIndex("cod_producto"));
					producto.descripcion = cursor.getString(cursor.getColumnIndex("nombre"));
					producto.precio = cursor.getFloat(cursor.getColumnIndex("precio"));
					producto.iva = cursor.getFloat(cursor.getColumnIndex("iva"));
					producto.descuento = (float) 0.0;
					producto.codLinea = cursor.getString(cursor.getColumnIndex("cod_linea"));

					if (Const.tipoAplicacion == Const.PREVENTA) {

					} else {

						if (Const.tipoAplicacion == Const.AUTOVENTA) {

							producto.cantidadInv = getInventarioProd(db, producto);

						}

					}

					itemListView.titulo = producto.codigo + " - " + producto.descripcion;
					itemListView.subTitulo = "Precio: $" + producto.precio + " - Iva: " + producto.iva;
					itemListView.subTitulo += "\nPrecio Con Iva: $" + Util.Redondear("" + producto.precioIva, 0);

					listaProductos.addElement(producto);
					listaItems.addElement(itemListView);

				} while (cursor.moveToNext());
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}

		return listaProductos;
	}

	public static float getInventarioProd(String codProduco) {

		SQLiteDatabase db = null;
		String sql = "";
		float inv = 0;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			sql = "select cantidad - " + "ifnull(( " + "select SUM( d.cantidad ) from [02Encabezado] e "
					+ "inner join [02Detalle] d on d.Numdoc = e.NumeroDoc and d.tipo = 1 and d.CodigoRef = i.codProducto "
					+ "where fechaTrans IS NOT NULL " + "group by d.CodigoRef " + "),0) as cantidad "
					+ "from inventario i " + "where codProducto = '" + codProduco + "'";

			Cursor cursor = db.rawQuery(sql, null);

			if (cursor.moveToFirst()) {

				inv = cursor.getFloat(cursor.getColumnIndex("cantidad"));
			}

			if (cursor != null)
				cursor.close();
		} catch (Exception e) {

		} finally {

			if (db != null)
				db.close();
		}

		return inv;
	}

	public static Vector<Producto> BuscarProductos(boolean porCodigo, String opBusqueda, String codLinea,
			String listaPrecio, Vector<ItemListView> listaItems, boolean pedido) {

		Producto producto;
		SQLiteDatabase db = null;

		ItemListView itemListView;
		Vector<Producto> listaProductos = new Vector<Producto>();

		String where = pedido ? " AND bs.Precio > 0" : "";
		// String where = pedido ? " ListaPrecios.Precio > 0" : "";

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query;

			if (porCodigo) {

				query = "select cod_producto,nombre,precio,iva,descuento,cod_linea from productos where cod_producto like '%"
						+ opBusqueda + "%' AND CodigoLista= '" + listaPrecio + "' ";// AND
																					// CodigoLista=
																					// '"+listaPrecio+"'";

			} else {

				query = "select cod_producto,nombre,precio,iva,descuento,cod_linea from productos where  nombre like '%"
						+ opBusqueda + "%'  AND CodigoLista= '" + listaPrecio + "' ";

			}

			System.out.println("Consulta buscarProducto --> " + query);
			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				do {

					producto = new Producto();
					itemListView = new ItemListView();

					producto = new Producto();
					producto.codigo = cursor.getString(cursor.getColumnIndex("cod_producto"));
					producto.descripcion = cursor.getString(cursor.getColumnIndex("nombre"));
					producto.precio = cursor.getFloat(cursor.getColumnIndex("precio"));
					producto.iva = cursor.getFloat(cursor.getColumnIndex("iva"));
					producto.descuento = (float) 0.0;
					producto.codLinea = cursor.getString(cursor.getColumnIndex("cod_linea"));

					itemListView.titulo = producto.codigo + " - " + producto.descripcion;
					itemListView.subTitulo = "Precio: $" + producto.precio + " - Iva: " + producto.iva;
					itemListView.subTitulo += "\nPrecio Con Iva: $" + Util.Redondear("" + producto.precioIva, 0);

					if (Const.tipoAplicacion == Const.PREVENTA) {

					} else {

						if (Const.tipoAplicacion == Const.AUTOVENTA) {

							producto.cantidadInv = getInventarioProd(db, producto);

						}

					}
					listaProductos.addElement(producto);
					listaItems.addElement(itemListView);

				} while (cursor.moveToNext());
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e("BuscarProductos", mensaje, e);

		} finally {

			if (db != null)
				db.close();
		}

		return listaProductos;
	}

	public static boolean ActualizarProductoPedido(Encabezado encabezado, String codProducto, float cantidad) {

		ContentValues values;
		SQLiteDatabase db = null;
		SQLiteDatabase dbPedido = null;

		try {

			dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			File filePedido = new File(Util.DirApp(), "Temp.db");
			dbPedido = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String whereArgs[] = new String[] { encabezado.numero_doc, codProducto };

			/*************************************************************
			 * Se Actualiza los datos del Producto, en Detalle y TmpPedido
			 ************************************************************/
			values = new ContentValues();
			values.put("cantidad", cantidad);

			int rowDetalle = db.update("[02Detalle]", values, "numDoc = ? AND codigoRef = ?", whereArgs);
			int rowsDetalleTemp = dbPedido.update("[02Detalle]", values, "numDoc = ? AND codigoRef = ?", whereArgs);

			Log.i("ActualizarProductoPedido", "rowDetalle = " + rowDetalle + " rowsDetalleTemp = " + rowsDetalleTemp);
			return rowDetalle > 0 && rowsDetalleTemp > 0;

		} catch (Exception e) {

			mensaje = e.getMessage();
			return false;

		} finally {

			if (db != null)
				db.close();

			if (dbPedido != null)
				dbPedido.close();
		}
	}

	public static boolean EliminarProductoPedido(Encabezado encabezado, String codProducto, String tipo) {

		SQLiteDatabase db = null;
		// SQLiteDatabase dbPedido = null;

		try {

			dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			// File filePedido = new File(Util.DirApp(), "Temp.db");
			// dbPedido = SQLiteDatabase.openDatabase(filePedido.getPath(),
			// null, SQLiteDatabase.OPEN_READWRITE);

			String whereArgs[] = new String[] { encabezado.numero_doc, codProducto, tipo };

			/*************************************************************
			 * Se Elimina el Producto del pedido, en Detalle y TmpPedido
			 ************************************************************/

			int rowDetalle = db.delete("[02Detalle]", "numDoc = ? AND codigoRef = ? AND tipo = ?", whereArgs);
			// int rowsDetalleTmp = dbPedido.delete("[02Detalle]", "numDoc = ?
			// AND codigoRef = ? AND tipo = ?", whereArgs);

			Log.i("EliminarProductoPedido", "rowDetalle = "
					+ rowDetalle /* + " rowsTmpPedido = " + rowsDetalleTmp */);
			return rowDetalle > 0 /* && rowsDetalleTmp > 0 */;

		} catch (Exception e) {

			mensaje = e.getMessage();
			return false;

		} finally {

			closeDataBase(db);

			// if (db != null)
			// db.close();

			// if (dbPedido != null)
			// dbPedido.close();
		}
	}

	public static boolean RegistrarEncabezado(Encabezado encabezado, Detalle detalle, String version, String imei,
			String tipoTrans) {

		ContentValues values;
		SQLiteDatabase db = null;
		// SQLiteDatabase dbPedido = null;

		try {

			dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			// File filePedido = new File(Util.DirApp(), "Temp.db");
			// dbPedido = SQLiteDatabase.openDatabase(filePedido.getPath(),
			// null, SQLiteDatabase.OPEN_READWRITE);

			String fechaActual = Util.FechaActual("yyyy-MM-dd HH:mm:ss");

			/**********************************
			 * Se crea el Encabezado del Pedido
			 *********************************/
			values = new ContentValues();
			values.put("codigo", encabezado.codigo_cliente);
			values.put("numeroDoc", encabezado.numero_doc);
			values.put("fechaReal", fechaActual);
			values.put("fechaentrega", fechaActual);
			values.put("tipoTrans", tipoTrans); // 0 -> Pedido, 2 -> Cambio
												// (PNC)
			values.put("montoFact", 0);
			values.put("desc1", 0);
			values.put("iva", 0);
			values.put("usuario", Main.usuario.codigoVendedor);
			values.put("telefono", Main.usuario.codigoVendedor);
			// values.put("fechaInicial", encabezado.hora_inicial);
			// values.put("fechaFinal", encabezado.hora_final);
			values.put("observacion", encabezado.observacion);
			// values.put("bodega", Main.usuario.bodega);
			values.put("dispositivo", "ANDROID");
			values.put("sincronizado", 0);
			values.put("fp", "1");
			// values.put("tipo","1");
			values.put("sync", 0);

			values.put("cedulaCliente", Main.cliente.nit);

			db.insertOrThrow("[02Encabezado]", null, values);
			// dbPedido.insertOrThrow("[02Encabezado]", null, values);

			/*******************************
			 * Se crea el Detalle del Pedido
			 *******************************/
			values = new ContentValues();
			values.put("numDoc", encabezado.numero_doc);
			values.put("codigoRef", detalle.codigo_producto);
			values.put("precio", detalle.precio);
			values.put("tarifaIva", detalle.iva);
			values.put("descuentoRenglon", detalle.descuento_autorizado);
			values.put("cantidad", detalle.cantidad);
			// values.put("vendedor", Main.usuario.codigoVendedor);
			// values.put("motivo", encabezado.motivoCambio);
			values.put("fechaReal", fechaActual);
			values.put("fecha", fechaActual);
			// values.put("tipo", detalle.codMotivo);

			values.put("Sincronizado", 0);
			values.put("tipo", detalle.tipo_pedido);

			db.insertOrThrow("[02Detalle]", null, values);
			// dbPedido.insertOrThrow("[02Detalle]", null, values);

			/******************************
			 * Se crea la Novedad del Pedido
			 ******************************/
			values = new ContentValues();
			values.put("codigoCliente", encabezado.codigo_cliente);
			values.put("motivo", encabezado.motivo);
			values.put("vendedor", Main.usuario.codigoVendedor);
			values.put("valor", 0);
			values.put("numerodoc", encabezado.numero_doc);
			values.put("fecha", fechaActual);
			// values.put("horaFinal", encabezado.hora_final);
			values.put("version", version);
			values.put("dispositivo", "ANDROID");
			values.put("imei", imei);

			values.put("telefono", Main.usuario.codigoVendedor);
			values.put("sincronizado", "0");
			if (encabezado.hora_inicial != null)
				values.put("horainicial", encabezado.hora_inicial);

			db.insertOrThrow("[02NovedadesCompras]", null, values);
			// dbPedido.insertOrThrow("[02NovedadesCompras]", null, values);

			/*
			 * values.put("lista_precio", encabezado.lista_precio);
			 * 
			 * 
			 * values.put("tipo_cliente", encabezado.tipo_cliente);
			 * values.put("extra_ruta", encabezado.extra_ruta);
			 * 
			 * values.put("no_compra", encabezado.no_compra);
			 * 
			 * values.put("fecha_consecutivo", Main.usuario.fechaConsecutivo);
			 * values.put("sync", 0); values.put("version", Main.versionApp);
			 * values.put("codigo_vendedor", Main.usuario.codigoVendedor);
			 */

			// long rowId = db.insertOrThrow("Encabezado", null, values);

			// if (rowId != -1) {

			// encabezado.id = "" + rowId;

			/************************************
			 * Se Ingresa el Detalle del Producto
			 ************************************/

			/*******************************************************
			 * Se Ingresa la Informacion del Producto en la Temporal
			 ******************************************************/
			/*
			 * ContentValues valuesTemp = new ContentValues();
			 * valuesTemp.put("cod_encabezado", rowId);
			 * valuesTemp.put("codigoCliente", encabezado.codigo_cliente);
			 * valuesTemp.put("codigoRef", detalle.codigo_producto);
			 * valuesTemp.put("cantidad", detalle.cantidad);
			 * valuesTemp.put("precio", detalle.precio); valuesTemp.put("iva",
			 * detalle.iva); valuesTemp.put("descuento",
			 * detalle.descuento_autorizado); valuesTemp.put("tipoPedido",
			 * detalle.tipo_pedido); valuesTemp.put("inicioPedido",
			 * encabezado.hora_inicial); valuesTemp.put("tipo_cliente",
			 * encabezado.tipo_cliente); valuesTemp.put("extra_ruta",
			 * encabezado.extra_ruta); valuesTemp.put("no_compra",
			 * encabezado.no_compra); valuesTemp.put("numero_doc",
			 * encabezado.numero_doc); valuesTemp.put("version",
			 * Main.versionApp);
			 * 
			 * dbPedido.insertOrThrow("TmpPedido", null, valuesTemp);
			 */

			return true;
			// }

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e("RegistrarEncabezado", mensaje, e);

		} finally {

			closeDataBase(db);
			// if (db != null)
			// db.close();

			// if (dbPedido != null)
			// dbPedido.close();
		}

		return false;
	}

	public static boolean RegistrarProductoPedido(Encabezado encabezado, Detalle detalle) {

		ContentValues values;
		SQLiteDatabase db = null;
		// SQLiteDatabase dbPedido = null;

		try {

			dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			// File filePedido = new File(Util.DirApp(), "Temp.db");
			// dbPedido = SQLiteDatabase.openDatabase(filePedido.getPath(),
			// null, SQLiteDatabase.OPEN_READWRITE);

			String fechaActual = Util.FechaActual("yyyy-MM-dd HH:mm:ss");

			/************************************
			 * Se Ingresa el Detalle del Producto
			 ************************************/
			values = new ContentValues();
			values.put("numDoc", encabezado.numero_doc);
			values.put("codigoRef", detalle.codigo_producto);
			values.put("precio", detalle.precio);
			values.put("tarifaIva", detalle.iva);
			values.put("descuentoRenglon", detalle.descuento_autorizado);
			values.put("cantidad", detalle.cantidad);
			// values.put("vendedor", Main.usuario.codigoVendedor);
			// values.put("motivo", encabezado.motivoCambio);
			values.put("fechaReal", fechaActual);
			values.put("fecha", fechaActual);
			// values.put("tipo", detalle.codMotivo);

			values.put("Sincronizado", 0);
			values.put("tipo", detalle.tipo_pedido);

			db.insertOrThrow("[02Detalle]", null, values);
			// dbPedido.insertOrThrow("[02Detalle]", null, values);

			/*
			 * values = new ContentValues(); values.put("cod_encabezado",
			 * encabezado.id); values.put("codigo_cliente",
			 * detalle.codigo_cliente); values.put("codigo_producto",
			 * detalle.codigo_producto); values.put("precio", detalle.precio);
			 * values.put("iva", detalle.iva); values.put("descuento",
			 * detalle.descuento_autorizado); values.put("cantidad",
			 * detalle.cantidad); values.put("tipo_pedido",
			 * detalle.tipo_pedido); values.put("sync", 0);
			 * 
			 * db.insertOrThrow("Detalle", null, values);
			 * 
			 * /******************************************************* Se
			 * Ingresa la Informacion del Producto en la Temporal
			 *******************************************************
			 * ContentValues valuesTemp = new ContentValues();
			 * valuesTemp.put("cod_encabezado", encabezado.id);
			 * valuesTemp.put("codigoCliente", encabezado.codigo_cliente);
			 * valuesTemp.put("codigoRef", detalle.codigo_producto);
			 * valuesTemp.put("cantidad", detalle.cantidad);
			 * valuesTemp.put("precio", detalle.precio); valuesTemp.put("iva",
			 * detalle.iva); valuesTemp.put("descuento",
			 * detalle.descuento_autorizado); valuesTemp.put("tipoPedido",
			 * detalle.tipo_pedido); valuesTemp.put("inicioPedido",
			 * encabezado.hora_inicial); valuesTemp.put("tipo_cliente",
			 * encabezado.tipo_cliente); valuesTemp.put("extra_ruta",
			 * encabezado.extra_ruta); valuesTemp.put("no_compra",
			 * encabezado.no_compra); valuesTemp.put("numero_doc",
			 * encabezado.numero_doc); valuesTemp.put("version",
			 * Main.versionApp);
			 * 
			 * dbPedido.insertOrThrow("TmpPedido", null, valuesTemp);
			 */

			return true;

		} catch (Exception e) {

			mensaje = e.getMessage();
			return false;

		} finally {

			closeDataBase(db);

			// if (db != null)
			// db.close();

			// if (dbPedido != null)
			// dbPedido.close();
		}
	}

	public static boolean ProductoXCodigo(String codigoProducto, String listaPrecio, Producto producto) {

		if (producto == null) {

			mensaje = "El producto no puede ser NULL";
			return false;
		}

		mensaje = "";
		boolean cargo = false;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = "select cod_producto,nombre,precio,iva,descuento,cod_linea from productos where cod_producto='"
					+ codigoProducto + "'";

			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				producto.codigo = cursor.getString(cursor.getColumnIndex("cod_producto"));
				producto.descripcion = cursor.getString(cursor.getColumnIndex("nombre"));
				producto.precio = cursor.getFloat(cursor.getColumnIndex("precio"));
				producto.iva = cursor.getFloat(cursor.getColumnIndex("iva"));
				producto.descuento = (float) 0.0;
				producto.codLinea = cursor.getString(cursor.getColumnIndex("cod_linea"));

				if (Const.tipoAplicacion == Const.PREVENTA) {

				} else {

					if (Const.tipoAplicacion == Const.AUTOVENTA) {

						producto.cantidadInv = getInventarioProd(db, producto);

					}

				}

				cargo = true;
				mensaje = "Cargo el producto correctamente";

			} else {

				mensaje = "Consulta sin resultados";
			}

			if (cursor != null)
				cursor.close();

			return cargo;

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e("ProductoXCodigo", mensaje, e);
			return false;

		} finally {

			if (db != null)
				db.close();
		}
	}

	public static double obtenerDescuentoNuevo(Producto producto, Cliente cliente) {

		mensaje = "";
		double descuento = 0.0;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				String query =

						" select codTipo,porcentaje from descuentos2 where codCliente='" + cliente.codigo
								+ "' and codProducto='" + producto.codigo + "' and codTipo = 9 "
								+ " UNION  select codTipo,porcentaje from descuentos2 where codCliente='"
								+ cliente.codigo + "' and codProducto='" + producto.grupo + "' and codTipo = 8 "
								+ " UNION  select codTipo,porcentaje from descuentos2 where codCliente='"
								+ cliente.codigo + "'  and codTipo = 7 "
								+ " UNION  select codTipo,porcentaje from descuentos2 where codCliente='"
								+ cliente.grupoclientes + "' and codProducto='" + producto.codigo + "' and codTipo = 6 "
								+ " UNION  select codTipo,porcentaje from descuentos2 where codCliente='"
								+ cliente.grupoclientes + "' and codProducto='" + producto.grupo + "' and codTipo = 5 "
								+ " UNION  select codTipo,porcentaje from descuentos2 where codCliente='"
								+ cliente.grupoclientes + "'  and codTipo = 4 "
								+ " UNION  select codTipo,porcentaje from descuentos2 where codProducto='"
								+ producto.codigo + "'   and codTipo = 3 "
								+ " UNION  select codTipo,porcentaje from descuentos2 where codProducto='"
								+ producto.grupo + "'   and codTipo = 2 "
								+ " UNION  select codTipo,porcentaje from descuentos2  where codTipo = 1 "
								+ " ORDER BY codTipo desc ";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					descuento = cursor.getDouble(cursor.getColumnIndex("porcentaje"));

					mensaje = "obtenerDescuento";
				}

				if (cursor != null)
					cursor.close();

			} else {

				Log.e("DatabaseBO", "obtenerDescuento -> no existe base de datos ");
			}

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e("DatabaseBO", "obtenerDescuento -> " + mensaje, e);

		} finally {

			if (db != null)
				db.close();
		}

		return descuento;

	}

	public static int getCantidadPromocion(String codProducto) {

		SQLiteDatabase db = null;
		String sql = "";
		int cantidad = 0;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				sql = "select cantidad " + "from Promociones " + "where producto = '" + codProducto + "' " + "limit 1";

				Cursor cursor = db.rawQuery(sql, null);

				if (cursor.moveToFirst()) {

					cantidad = cursor.getInt(cursor.getColumnIndex("cantidad"));
				}

				if (cursor != null)
					cursor.close();
			}
		} catch (Exception e) {

		} finally {

			closeDataBase(db);
		}

		return cantidad;

	}

	public static String getNumFacturaActual(boolean esPedido) {

		String numFactura = "";
		String prefijo = "";
		long consecutivo = 0;

		prefijo = getPrefijo();
		consecutivo = getConsecutivo(esPedido);

		// if(esPedido)
		// numFactura = prefijo + "" + ( consecutivo + 1 );

		if (esPedido)
			numFactura = prefijo + "" + (String.valueOf(consecutivo));
		else
			numFactura = prefijo + "" + Util.FechaActual("MMdd") + Util.lpad(String.valueOf(consecutivo), 3, "0");
		return numFactura;
	}

	public static String getPrefijo() {

		SQLiteDatabase db = null;
		String sql = "";
		String prefijo = "";

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			sql = "select prefijo " + "from consecutivos " + "limit 1";

			Cursor cursor = db.rawQuery(sql, null);

			if (cursor.moveToFirst()) {

				prefijo = cursor.getString(cursor.getColumnIndex("prefijo"));
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

		} finally {

			closeDataBase(db);
		}

		return prefijo;
	}

	public static long getConsecutivo(boolean esPedido) {

		SQLiteDatabase db = null;
		String sql = "";
		long consecutivo = 0;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			if (esPedido) {

				sql = "select consec " + "from Consecutivo " + "limit 1";
			} else {

				sql = "select consec " + "from ConsecutivoDev " + "limit 1";
			}

			Cursor cursor = db.rawQuery(sql, null);

			if (cursor.moveToFirst()) {

				consecutivo = cursor.getInt(cursor.getColumnIndex("consec"));
				consecutivo++;
			}

			if (cursor != null)
				cursor.close();
		} catch (Exception e) {

		} finally {

			closeDataBase(db);
		}

		return consecutivo;
	}

	public static boolean setConsecutivo(long consecutivo, boolean esPedido) {

		SQLiteDatabase db = null;
		String sql = "";
		boolean bien = false;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			if (esPedido) {

				sql = "update Consecutivo " + "set consec = " + consecutivo;
			} else {

				sql = "update ConsecutivoDev " + "set consec = " + consecutivo;
			}

			db.execSQL(sql);
			bien = true;
		} catch (Exception e) {

		} finally {

			closeDataBase(db);
		}

		return bien;
	}

	public static boolean CancelarPedidoEstadisticas(Encabezado encabezado) {

		SQLiteDatabase db = null;
		SQLiteDatabase dbPedido = null;

		try {

			dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			File filePedido = new File(Util.DirApp(), "Temp.db");
			dbPedido = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = " SELECT count(1) as cantidad" + " FROM saldos_cancelados AS s"
					+ " JOIN saldos_cliente c ON  s.cod_saldo = c.cod_saldo " + " WHERE c.documento='"
					+ encabezado.numero_doc + "'" + " AND c.saldo<>0";

			Cursor cursor = db.rawQuery(query, null);
			int cantidad = 0;

			if (cursor.moveToFirst()) {

				do {

					cantidad = cursor.getInt(cursor.getColumnIndex("cantidad"));
					Log.i("cantidad: ", "cantidad:  " + cantidad);
				} while (cursor.moveToNext());

			} else {

				mensaje = "Consulta sin Resultados";
			}

			// if(cantidad==0){

			String whereArgs[] = new String[] { encabezado.numero_doc, encabezado.codigo_cliente };
			ContentValues contentV = new ContentValues();
			contentV.put("valor", 0);
			contentV.put("iva", 0);
			contentV.put("descuento", 0);
			contentV.put("valor_retencion", 0);
			contentV.put("valor_retencion_ica", 0);

			int rowsEncabezado = db.update("[encabezado_venta]", contentV, "documento = ? AND cod_cliente = ?",
					whereArgs);
			rowsEncabezado += dbPedido.update("[encabezado_venta]", contentV, "documento = ? AND cod_cliente = ?",
					whereArgs);

			contentV.clear();
			contentV.put("precio", 0);
			contentV.put("cantidad", 0);
			contentV.put("descuento", 0);
			contentV.put("iva", 0);

			int rowDetalle = db.update("[detalle_venta]", contentV, "documento = ?",
					new String[] { encabezado.numero_doc });
			rowDetalle += dbPedido.update("[detalle_venta]", contentV, "documento = ?",
					new String[] { encabezado.numero_doc });

			contentV.clear();
			contentV.put("saldo", 0);

			int rowSaldoCliente = db.update("[saldos_cliente]", contentV, "documento = ?",
					new String[] { encabezado.numero_doc });
			rowSaldoCliente += dbPedido.update("[saldos_cliente]", contentV, "documento = ?",
					new String[] { encabezado.numero_doc });

			String query2 = " SELECT c.cod_saldo as cod_saldo" + " FROM saldos_cliente as c" + " WHERE c.documento='"
					+ encabezado.numero_doc + "'";

			Cursor cursor2 = db.rawQuery(query2, null);
			String cod_saldo = "";

			if (cursor2.moveToFirst()) {

				do {

					cod_saldo = cursor2.getString(cursor2.getColumnIndex("cod_saldo"));
					Log.i("cod_saldo: ", "cod_saldo:  " + cod_saldo);
				} while (cursor2.moveToNext());

			} else {

				mensaje = "Consulta sin Resultados";
			}

			contentV.clear();
			contentV.put("saldo_recibido", 0);

			int rowSaldoCancelados = db.update("[saldos_cancelados]", contentV, "cod_saldo_canc = ?",
					new String[] { cod_saldo });
			rowSaldoCliente += dbPedido.update("[saldos_cancelados]", contentV, "cod_saldo_canc = ?",
					new String[] { cod_saldo });

			// db.delete("[saldos_cancelados]", " documento =
			// '"+encabezado.numero_doc+"'", null);

			// contentV.clear();
			// int rowNovedades = db.update("[novedadescompras]",contentV,
			// "numeroDoc = ? AND codigoCliente = ?", whereArgs);
			// rowNovedades += dbPedido.update("[novedadescompras]", contentV,
			// "numeroDoc = ? AND codigoCliente = ?", whereArgs);

			Log.i("CancelarPedido", "rowsEncabezado = " + rowsEncabezado + " rowDetalle = " + rowDetalle);
			return rowsEncabezado > 0 && rowDetalle > 0;
			// }else{
			// Log.i("men: ", "men: No se puede cancelar el pedido, ya tiene un
			// recaudo");
			// return false;
			// }

		} catch (Exception e) {

			mensaje = e.getMessage();
			return false;

		} finally {

			if (db != null)
				db.close();

			if (dbPedido != null)
				dbPedido.close();
		}
	}

	public static boolean CancelarPedido(Encabezado encabezado) {

		SQLiteDatabase db = null;
		SQLiteDatabase dbPedido = null;

		try {

			dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			File filePedido = new File(Util.DirApp(), "Temp.db");
			dbPedido = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String whereArgs[] = new String[] { encabezado.numero_doc, encabezado.codigo_cliente };

			int rowsEncabezado = db.delete("[02Encabezado]", "numeroDoc = ? AND codigo = ?", whereArgs);
			int rowDetalle = db.delete("[02Detalle]", "numDoc = ?", new String[] { encabezado.numero_doc });
			int rowNovedades = db.delete("[02novedadescompras]", "numeroDoc = ? AND codigoCliente = ?", whereArgs);

			rowsEncabezado += dbPedido.delete("[02Encabezado]", "numeroDoc = ? AND codigo = ?", whereArgs);
			rowDetalle += dbPedido.delete("[02Detalle]", "numDoc = ?", new String[] { encabezado.numero_doc });
			rowNovedades += dbPedido.delete("[02novedadescompras]", "numeroDoc = ? AND codigoCliente = ?", whereArgs);
			// int rowFotos = dbPedido.delete("Fotos", "NroDoc = ? AND
			// CodCliente = ?", whereArgs);

			Log.i("CancelarPedido", "rowsEncabezado = " + rowsEncabezado + " rowDetalle = " + rowDetalle
					+ " rowNovedades = " + rowNovedades);
			return rowsEncabezado > 0 && rowDetalle > 0 && rowNovedades > 0;

		} catch (Exception e) {

			mensaje = e.getMessage();
			return false;

		} finally {

			if (db != null)
				db.close();

			if (dbPedido != null)
				dbPedido.close();
		}
	}

	public static boolean FinalizarPedido(Encabezado encabezado) {

		SQLiteDatabase db = null;
		SQLiteDatabase dbPedido = null;

		try {

			dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			File filePedido = new File(Util.DirApp(), "Temp.db");
			dbPedido = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			ContentValues values = new ContentValues();
			String valorNeto = Util.Redondear(Util.QuitarE("" + encabezado.valor_neto), 0);

			values.put("montoFact", Util.Redondear(Util.QuitarE("" + encabezado.sub_total), 0));
			values.put("iva", Util.Redondear(Util.QuitarE("" + encabezado.total_iva), 0));
			values.put("desc1", Util.Redondear(Util.QuitarE("" + encabezado.valor_descuento), 0));
			values.put("fechatrans", encabezado.hora_final);
			values.put("observacion", encabezado.observacion);

			if (Main.tipoVenta.equals("F")) {

				String numFactura = getNumFacturaActual(true);

				values.put("factura", numFactura);
			} else if (Main.tipoVenta.equals("C")) {

				String numFactura = getNumFacturaActual(false);

				values.put("factura", numFactura);
			}

			String whereArgs[] = new String[] { encabezado.numero_doc, encabezado.codigo_cliente };
			int rowsEncabezado = db.update("[02Encabezado]", values, "numeroDoc = ? AND codigo = ?", whereArgs);
			rowsEncabezado += dbPedido.update("[02Encabezado]", values, "numeroDoc = ? AND codigo = ?", whereArgs);

			values = new ContentValues();
			values.put("valor", valorNeto);

			int rowsNovedades = db.update("[02NovedadesCompras]", values, "numerodoc = ? AND codigoCliente = ?",
					whereArgs);
			rowsNovedades += dbPedido.update("[02NovedadesCompras]", values, "numerodoc = ? AND codigoCliente = ?",
					whereArgs);

			Vector<String> trans = new Vector<String>();
			Vector<String> trans2 = new Vector<String>();
			Vector<String> trans3 = new Vector<String>();
			Cursor cursor = db
					.rawQuery("SELECT codigoRef as codigoRef, cantidad as cantidad FROM [02Detalle] WHERE numDoc = '"
							+ encabezado.numero_doc + "'", null);

			if (cursor.moveToFirst()) {

				do {

					String codigoRef = cursor.getString(cursor.getColumnIndex("codigoRef"));
					float cantidad = cursor.getFloat(cursor.getColumnIndex("cantidad"));

					trans.addElement("UPDATE inventario02 SET inventarioactual = inventarioactual - " + cantidad
							+ " WHERE codigoproducto = '" + codigoRef + "'");
					trans2.addElement("UPDATE productos SET existencia = existencia - " + cantidad + " WHERE codigo = '"
							+ codigoRef + "'");

				} while (cursor.moveToNext());
			}

			if (cursor != null)
				cursor.close();

			if (Main.tipoVenta.equals("F")) {

				for (String update : trans) {

					db.execSQL(update);

				}

				for (String update2 : trans2) {

					db.execSQL(update2);

				}

				/*
				 * for (String update3 : trans3) {
				 * 
				 * db.execSQL(update3);
				 * 
				 * }
				 */

			}

			String sql = "SELECT codigoRef as codigoRef, cantidad as cantidad " + "FROM [02Detalle] "
					+ "WHERE numDoc = '" + encabezado.numero_doc + "' and tipo = '" + Const.PEDIDO_PROMOCION + "'";

			cursor = db.rawQuery(sql, null);

			if (cursor.moveToFirst()) {

				do {

					String codigoRef = cursor.getString(cursor.getColumnIndex("codigoRef"));
					float cantidad = cursor.getFloat(cursor.getColumnIndex("cantidad"));

					sql = "update Promociones set cantidad = cantidad - " + cantidad + " " + "where producto = '"
							+ codigoRef + "'";

					db.execSQL(sql);
				} while (cursor.moveToNext());
			}

			if (cursor != null)
				cursor.close();

			Log.i("FinalizarPedido", "rowsEncabezado = " + rowsEncabezado + " rowsNovedades = " + rowsNovedades);
			return rowsEncabezado > 0 && rowsNovedades > 0;

		} catch (Exception e) {

			mensaje = e.getMessage();
			return false;

		} finally {

			if (db != null)
				db.close();

			if (dbPedido != null)
				dbPedido.close();
		}
	}

	public static boolean armarEncabezadoEnviar(String nDoc) {

		SQLiteDatabase db = null;
		SQLiteDatabase dbTmp = null;
		String sql = "";
		boolean bien = false;

		try {

			String codigo, numerodoc, fechatrans, tipotrans, usuario, telefono, terminolabores, observacion,
					fechaentrega, fechareal, factura, dispositivo, cedulaCliente;
			float montofact, desc1, iva;
			int sincronizado, fp, sync;

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				File dbFileTmp = new File(Util.DirApp(), "Temp.db");
				dbTmp = SQLiteDatabase.openDatabase(dbFileTmp.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				sql = "select codigo, numerodoc, fechatrans, tipotrans, montofact, desc1, iva, usuario, "
						+ "sincronizado, telefono, observacion, fechaentrega, fechareal, fp, dispositivo, sync, cedulaCliente, "
						+ "ifnull( factura, '' ) as factura " + "from [02encabezado] " + "where numerodoc = '" + nDoc
						+ "'";

				dbTmp.beginTransaction();

				dbTmp.execSQL("delete from [02encabezado] where numerodoc = '" + nDoc + "'");

				Cursor cursor = db.rawQuery(sql, null);

				if (cursor.moveToFirst()) {

					do {

						codigo = cursor.getString(cursor.getColumnIndex("codigo"));
						numerodoc = cursor.getString(cursor.getColumnIndex("numerodoc"));
						fechatrans = cursor.getString(cursor.getColumnIndex("fechatrans"));
						tipotrans = cursor.getString(cursor.getColumnIndex("tipotrans"));
						montofact = cursor.getFloat(cursor.getColumnIndex("montofact"));
						desc1 = cursor.getFloat(cursor.getColumnIndex("desc1"));
						iva = cursor.getFloat(cursor.getColumnIndex("iva"));
						usuario = cursor.getString(cursor.getColumnIndex("usuario"));
						sincronizado = cursor.getInt(cursor.getColumnIndex("sincronizado"));
						telefono = cursor.getString(cursor.getColumnIndex("telefono"));
						observacion = cursor.getString(cursor.getColumnIndex("observacion"));
						fechaentrega = cursor.getString(cursor.getColumnIndex("fechaentrega"));
						fechareal = cursor.getString(cursor.getColumnIndex("fechareal"));
						fp = cursor.getInt(cursor.getColumnIndex("fp"));
						dispositivo = cursor.getString(cursor.getColumnIndex("dispositivo"));
						sync = cursor.getInt(cursor.getColumnIndex("sync"));
						cedulaCliente = cursor.getString(cursor.getColumnIndex("cedulaCliente"));
						factura = cursor.getString(cursor.getColumnIndex("factura"));

						sql = "insert into [02encabezado] "
								+ "( codigo, numerodoc, fechatrans, tipotrans, montofact, desc1, iva, usuario, "
								+ "sincronizado, telefono, observacion, fechaentrega, fechareal, fp, dispositivo, sync, cedulaCliente, factura ) "
								+ "values ( '" + codigo + "', '" + numerodoc + "', '" + fechatrans + "', '" + tipotrans
								+ "', " + montofact + ", " + desc1 + ", " + iva + ", '" + usuario + "', " + ""
								+ sincronizado + ", '" + telefono + "', '" + observacion + "', '" + fechaentrega
								+ "', '" + fechareal + "', '" + fp + "', '" + dispositivo + "', " + sync + ", '"
								+ cedulaCliente + "', '" + factura + "' )";

						dbTmp.execSQL(sql);
					} while (cursor.moveToNext());
				}

				if (cursor != null)
					cursor.close();

				bien = true;

				dbTmp.setTransactionSuccessful();
			}

		} catch (Exception e) {

		} finally {

			dbTmp.endTransaction();
			closeDataBase(db);
			closeDataBase(dbTmp);
		}

		return bien;
	}

	public static boolean armarNovedadesComprasEnviar(String nDoc) {

		SQLiteDatabase db = null;
		SQLiteDatabase dbTmp = null;
		String sql = "";
		boolean bien = false;

		try {

			String codigocliente, fecha, vendedor, telefono, numerodoc, horainicial, dispositivo, imei, version;
			float valor;
			int motivo, sincronizado;

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				File dbFileTmp = new File(Util.DirApp(), "Temp.db");
				dbTmp = SQLiteDatabase.openDatabase(dbFileTmp.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				sql = "select codigocliente, fecha, motivo, vendedor, telefono, sincronizado, valor, numerodoc, horainicial, dispositivo, imei, version "
						+ "from [02novedadescompras] " + "where numerodoc = '" + nDoc + "'";

				dbTmp.beginTransaction();

				dbTmp.execSQL("delete from [02novedadescompras] where numerodoc = '" + nDoc + "'");

				Cursor cursor = db.rawQuery(sql, null);

				if (cursor.moveToFirst()) {

					do {

						codigocliente = cursor.getString(cursor.getColumnIndex("codigocliente"));
						fecha = cursor.getString(cursor.getColumnIndex("fecha"));
						motivo = cursor.getInt(cursor.getColumnIndex("motivo"));
						vendedor = cursor.getString(cursor.getColumnIndex("vendedor"));
						telefono = cursor.getString(cursor.getColumnIndex("telefono"));
						sincronizado = cursor.getInt(cursor.getColumnIndex("sincronizado"));
						valor = cursor.getFloat(cursor.getColumnIndex("valor"));
						numerodoc = cursor.getString(cursor.getColumnIndex("numerodoc"));
						horainicial = cursor.getString(cursor.getColumnIndex("horainicial"));
						dispositivo = cursor.getString(cursor.getColumnIndex("dispositivo"));
						imei = cursor.getString(cursor.getColumnIndex("imei"));
						version = cursor.getString(cursor.getColumnIndex("version"));

						sql = "insert into [02novedadescompras] "
								+ "( codigocliente, fecha, motivo, vendedor, telefono, sincronizado, valor, numerodoc, horainicial, dispositivo, imei, version ) "
								+ "values ( '" + codigocliente + "', '" + fecha + "', " + motivo + ", '" + vendedor
								+ "', '" + telefono + "', " + sincronizado + ", " + valor + ", '" + numerodoc + "', '"
								+ horainicial + "', '" + dispositivo + "', '" + imei + "', '" + version + "') ";

						dbTmp.execSQL(sql);
					} while (cursor.moveToNext());
				}

				if (cursor != null)
					cursor.close();

				bien = true;

				dbTmp.setTransactionSuccessful();
			}

		} catch (Exception e) {

		} finally {

			dbTmp.endTransaction();
			closeDataBase(db);
			closeDataBase(dbTmp);
		}

		return bien;
	}

	public static boolean armarDetalleEnviar(String nDoc) {

		SQLiteDatabase db = null;
		SQLiteDatabase dbTmp = null;
		String sql = "";
		boolean bien = false;

		try {

			String Numdoc, Fecha, CodigoRef, fechaReal;
			float Precio, TarifaIva, DescuentoRenglon, Cantidad;
			int Sincronizado, tipo;

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				File dbFileTmp = new File(Util.DirApp(), "Temp.db");
				dbTmp = SQLiteDatabase.openDatabase(dbFileTmp.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				sql = "select Numdoc, Fecha, CodigoRef, Precio, TarifaIva, DescuentoRenglon, Cantidad, Sincronizado, fechaReal, tipo "
						+ "from [02detalle] " + "where numdoc = '" + nDoc + "'";

				dbTmp.beginTransaction();

				dbTmp.execSQL("delete from [02Detalle] where Numdoc = '" + nDoc + "'");

				Cursor cursor = db.rawQuery(sql, null);

				if (cursor.moveToFirst()) {

					do {

						Numdoc = cursor.getString(cursor.getColumnIndex("Numdoc"));
						Fecha = cursor.getString(cursor.getColumnIndex("Fecha"));
						CodigoRef = cursor.getString(cursor.getColumnIndex("CodigoRef"));
						Precio = cursor.getFloat(cursor.getColumnIndex("Precio"));
						TarifaIva = cursor.getFloat(cursor.getColumnIndex("TarifaIva"));
						DescuentoRenglon = cursor.getFloat(cursor.getColumnIndex("DescuentoRenglon"));
						Cantidad = cursor.getFloat(cursor.getColumnIndex("Cantidad"));
						Sincronizado = cursor.getInt(cursor.getColumnIndex("Sincronizado"));
						fechaReal = cursor.getString(cursor.getColumnIndex("fechaReal"));
						tipo = cursor.getInt(cursor.getColumnIndex("tipo"));

						sql = "insert into [02Detalle] "
								+ "( Numdoc, Fecha, CodigoRef, Precio, TarifaIva, DescuentoRenglon, Cantidad, Sincronizado, fechaReal, tipo ) "
								+ "values ( '" + Numdoc + "', '" + Fecha + "', '" + CodigoRef + "', " + Precio + ", "
								+ TarifaIva + ", " + DescuentoRenglon + ", " + Cantidad + ", " + Sincronizado + ", '"
								+ fechaReal + "', " + tipo + " ) ";

						dbTmp.execSQL(sql);
					} while (cursor.moveToNext());
				}

				if (cursor != null)
					cursor.close();

				bien = true;

				dbTmp.setTransactionSuccessful();
			}

		} catch (Exception e) {

		} finally {

			dbTmp.endTransaction();
			closeDataBase(db);
			closeDataBase(dbTmp);
		}

		return bien;
	}

	public static boolean reorganizarInventario() {

		SQLiteDatabase db = null;
		String sql = "";
		boolean bien = false;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			sql = "delete from Inventario";

			db.beginTransaction();

			db.execSQL(sql);

			sql = "insert into Inventario ( codProducto, cantidad ) "
					+ "select codProducto, sum( cantidad ) as cantidad " + "from Remisiones " + "where tipo = 'CARGUE'"
					+ "group by codProducto";

			db.execSQL(sql);

			db.setTransactionSuccessful();

			bien = true;
		} catch (Exception e) {

		} finally {

			db.endTransaction();

			closeDataBase(db);
		}

		return bien;
	}

	public static boolean reorganizarInventario(Context contexto) {

		SQLiteDatabase db = null;
		String sql = "";
		boolean bien = false;
		String codProducto;
		float cantidad;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			sql = "delete from Inventario";

			db.execSQL(sql);

			sql = "select " + " codProducto, " + " ifnull( sum( cantidad ), 0 ) - " + " ifnull(( "
					+ " select SUM( d.cantidad ) from [02Encabezado] e "
					+ " INNER join [02Detalle] d on d.Numdoc = e.NumeroDoc and d.CodigoRef = r.codProducto "
					+ " where  e.TipoTrans = 'T'   and   e.numerodoc not in(select distinct numerodoc from anulaciones)  "
					+ " group by d.CodigoRef " + " ),0) + " + " ifnull(( "
					+ " select SUM( d.cantidad ) from [02Encabezado] e "
					+ " INNER join [02Detalle] d on d.Numdoc = e.NumeroDoc and d.CodigoRef = r.codProducto "
					+ " where  e.TipoTrans = 'C' and d.tipo = 1    and   e.numerodoc not in(select distinct numerodoc from anulaciones) "
					+ " group by d.CodigoRef " + " ),0) as cantidad " + " from remisiones r "
					+ " group by r.codProducto "

					+ " union "
					+ " select d.CodigoRef as codProducto ,sum(d.cantidad) as cantidad from [02Detalle] as d "
					+ " inner join [02Encabezado]  as e on d.Numdoc = e.NumeroDoc  where  e.TipoTrans = 'C' and d.tipo = 1 "
					+ " and  d.CodigoRef  not in(select distinct codProducto from remisiones)  group by d.CodigoRef " +

					" order by r.codProducto ";

			Cursor cursor = db.rawQuery(sql, null);

			if (cursor.moveToFirst()) {

				do {

					codProducto = cursor.getString(cursor.getColumnIndex("codProducto"));
					cantidad = cursor.getFloat(cursor.getColumnIndex("cantidad"));

					sql = "insert into Inventario " + "( codProducto,cantidad ) " + "values " + "( '" + codProducto
							+ "', '" + cantidad + "'  )";

					db.execSQL(sql);
				} while (cursor.moveToNext());
			}

			if (cursor != null)
				cursor.close();

			bien = true;
		} catch (Exception e) {

			Toast.makeText(contexto, "reorganizarInventario", Toast.LENGTH_LONG).show();

		} finally {

			if (db != null)
				db.close();

		}

		return bien;
	}

	public static Vector<MotivoCambio> ListaMotivosCambio(Vector<String> listaItems) {

		mensaje = "";
		MotivoCambio motivoCambio;

		SQLiteDatabase db = null;
		Vector<MotivoCambio> listaMotivosCambio = new Vector<MotivoCambio>();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			Cursor cursor = db.rawQuery("SELECT cod_motivo as codigo, descripcion FROM motivos_cambio", null);

			if (cursor.moveToFirst()) {

				do {

					motivoCambio = new MotivoCambio();
					motivoCambio.codigo = cursor.getString(cursor.getColumnIndex("codigo"));
					motivoCambio.concepto = cursor.getString(cursor.getColumnIndex("descripcion"));

					listaItems.addElement(motivoCambio.concepto);
					listaMotivosCambio.addElement(motivoCambio);

				} while (cursor.moveToNext());
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e("ListaMotivosCambio", mensaje, e);

		} finally {

			if (db != null)
				db.close();
		}

		return listaMotivosCambio;
	}

	public static Impresion getImpresion() {

		SQLiteDatabase db = null;
		String sql = "";
		Impresion imp = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			sql = "  select prefijo, cod_resolucion, limite_inf, limite_sup, regimen,fecha_vigencia ,razonSocial, nit, direccion, ciudad, msjComprador, MensajeDian "
					+ " from consecutivos limit 1 ";

			Cursor cursor = db.rawQuery(sql, null);

			if (cursor.moveToFirst()) {

				do {

					imp = new Impresion();

					imp.razonSocial = cursor.getString(cursor.getColumnIndex("razonSocial"));
					imp.nit = cursor.getString(cursor.getColumnIndex("nit"));
					imp.autoRetenedor = "";
					imp.fecha_Autoretenedor = cursor.getString(cursor.getColumnIndex("fecha_vigencia"));
					imp.regimen = cursor.getString(cursor.getColumnIndex("regimen"));
					imp.direccion = cursor.getString(cursor.getColumnIndex("direccion"));
					imp.ciudad = cursor.getString(cursor.getColumnIndex("ciudad"));
					imp.numResolucion = cursor.getString(cursor.getColumnIndex("cod_resolucion"));
					imp.rangoInicial = cursor.getString(cursor.getColumnIndex("limite_inf"));
					imp.rangoFinal = cursor.getString(cursor.getColumnIndex("limite_sup"));
					imp.msjComprador = cursor.getString(cursor.getColumnIndex("msjComprador"));
					imp.msjDian = cursor.getString(cursor.getColumnIndex("MensajeDian"));
				} while (cursor.moveToNext());
			}

			if (cursor != null)
				cursor.close();
		} catch (Exception e) {

		} finally {

			closeDataBase(db);
		}

		return imp;
	}

	public static ImpresionCliente getImpresionCliente(String numeroDoc) {

		SQLiteDatabase db = null;
		String sql = "";
		ImpresionCliente impClient = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			sql = "select c.cod_cliente as codigo, c.representante_legal as representa, c.razon_social as razonSocial, c.nit as nit, e.consecutivo as consecutivo, strftime('%Y-%m-%d %H:%M:%S', e.fecha_movil ) as fecha, e.observacion as observacion "
					+ "from encabezado_venta e " + "inner join clientes c on c.cod_cliente = e.cod_cliente "
					+ "where e.documento = '" + numeroDoc + "' ";

			Cursor cursor = db.rawQuery(sql, null);

			if (cursor.moveToFirst()) {

				do {

					impClient = new ImpresionCliente();

					impClient.razonSocial = cursor.getString(cursor.getColumnIndex("razonSocial"));
					impClient.nit = cursor.getString(cursor.getColumnIndex("nit"));
					impClient.codigo = cursor.getString(cursor.getColumnIndex("codigo"));
					impClient.nombre = cursor.getString(cursor.getColumnIndex("representa"));
					impClient.numFactura = cursor.getString(cursor.getColumnIndex("consecutivo"));
					impClient.observacion = cursor.getString(cursor.getColumnIndex("observacion"));
					impClient.fecha = cursor.getString(cursor.getColumnIndex("fecha"));
				} while (cursor.moveToNext());
			}

			if (cursor != null)
				cursor.close();
		} catch (Exception e) {

		} finally {

			closeDataBase(db);
		}

		return impClient;
	}

	public static EncabezadoRecaudo listaRecaudosRealizado(String doc) {

		mensaje = "";
		SQLiteDatabase db = null;

		EncabezadoRecaudo encabezadoRecaudo = new EncabezadoRecaudo();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query =

					" select  Encabezadorecaudo.*,CLIENTES.RAZONSOCIAL,CLIENTES.REPRESENTA,CLIENTES.NIT "
							+ " from Encabezadorecaudo inner join CLIENTES on Encabezadorecaudo.CODIGOCLIENTE=CLIENTES.codigo where nrodoc= '"
							+ doc + "' ";

			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				do {

					encabezadoRecaudo = new EncabezadoRecaudo();

					encabezadoRecaudo.total = cursor.getFloat(cursor.getColumnIndex("total"));
					encabezadoRecaudo.fecha_recaudo = cursor.getString(cursor.getColumnIndex("fecha_recaudo"));
					encabezadoRecaudo.nrodoc = cursor.getString(cursor.getColumnIndex("nrodoc"));
					encabezadoRecaudo.razonSocial = cursor.getString(cursor.getColumnIndex("razonsocial"));
					encabezadoRecaudo.representa = cursor.getString(cursor.getColumnIndex("representa"));
					encabezadoRecaudo.nit = cursor.getString(cursor.getColumnIndex("nit"));
					encabezadoRecaudo.vendedor = cursor.getString(cursor.getColumnIndex("vendedor"));

					encabezadoRecaudo.efectivo = cursor.getFloat(cursor.getColumnIndex("efectivo"));
					encabezadoRecaudo.cheque = cursor.getFloat(cursor.getColumnIndex("valorcheque"));
					encabezadoRecaudo.consignacion = cursor.getFloat(cursor.getColumnIndex("valorconsignacion"));

				} while (cursor.moveToNext());
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, "listaEncabezadoRecaudo - > " + mensaje, e);

		} finally {

			if (db != null)
				db.close();
		}

		return encabezadoRecaudo;
	}

	public static Vector<DetalleRecaudo> listaDetallesRecaudo(String nroDoc) {

		mensaje = "";
		SQLiteDatabase db = null;

		DetalleRecaudo detalleRecaudo = new DetalleRecaudo();
		Vector<DetalleRecaudo> listaDetalleRecaudos = new Vector<DetalleRecaudo>();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = " select  * FROM detallerecaudo where NRODOC= '" + nroDoc + "' ";

			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				do {

					detalleRecaudo = new DetalleRecaudo();

					detalleRecaudo.nroInterno = cursor.getString(cursor.getColumnIndex("nrointerno"));
					detalleRecaudo.valor = cursor.getFloat(cursor.getColumnIndex("valor"));
					detalleRecaudo.descuento = cursor.getFloat(cursor.getColumnIndex("descuento"));
					detalleRecaudo.retefuente = cursor.getFloat(cursor.getColumnIndex("retefuente"));
					detalleRecaudo.otros = cursor.getFloat(cursor.getColumnIndex("otros"));

					listaDetalleRecaudos.addElement(detalleRecaudo);

				} while (cursor.moveToNext());
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, "listaDetallesRecaudo - > " + mensaje, e);

		} finally {

			if (db != null)
				db.close();
		}

		return listaDetalleRecaudos;
	}

	public static Vector<Float> getIvaProductos() {

		SQLiteDatabase db = null;
		String sql = "";
		float iva = 0;

		Vector<Float> vIva = new Vector<Float>();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			sql = "select iva " + "from iva " + "where iva <> 0 " + "order by iva asc";

			Cursor cursor = db.rawQuery(sql, null);

			if (cursor.moveToFirst()) {

				do {

					iva = cursor.getFloat(cursor.getColumnIndex("iva"));
					vIva.add(iva);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {

		} finally {

			closeDataBase(db);
		}

		return vIva;
	}

	public static Vector<ImpresionDetalle> getImpresionDetalle(String numeroDoc) {

		SQLiteDatabase db = null;
		String sql = "";
		ImpresionDetalle impDet = null;
		Vector<ImpresionDetalle> vImpDet = new Vector<ImpresionDetalle>();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			sql = "select p.cod_producto as codigo, p.nombre as nombre, p.precio as precio, d.iva, d.descuento, ifnull(d.descuento_producto,0) AS descuento_producto, cantidad as cantidad "
					+ " from detalle_venta d " + " inner join productos p on p.cod_producto = d.cod_producto  "
					+ " where documento = '" + numeroDoc + "'";

			Cursor cursor = db.rawQuery(sql, null);

			if (cursor.moveToFirst()) {

				do {

					impDet = new ImpresionDetalle();

					impDet.codigo = cursor.getString(cursor.getColumnIndex("codigo"));
					impDet.nombre = cursor.getString(cursor.getColumnIndex("nombre"));
					impDet.precio = cursor.getFloat(cursor.getColumnIndex("precio"));
					impDet.iva = cursor.getFloat(cursor.getColumnIndex("iva"));
					impDet.descuento = cursor.getFloat(cursor.getColumnIndex("descuento"));
					impDet.descuento = cursor.getFloat(cursor.getColumnIndex("descuento_producto"));
					impDet.cantidad = cursor.getFloat(cursor.getColumnIndex("cantidad"));
					// impDet.tipo = cursor.getInt( cursor.getColumnIndex(
					// "tipo" ) );
					vImpDet.add(impDet);

				} while (cursor.moveToNext());
			}

			if (cursor != null)
				cursor.close();
		} catch (Exception e) {

		} finally {

			closeDataBase(db);
		}

		return vImpDet;
	}

	public static Vector<InformeInventario> CargarInformeInventario() {

		SQLiteDatabase db = null;
		InformeInventario informeInv;
		Vector<InformeInventario> listaInformeInv = new Vector<InformeInventario>();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				String query = "select " + "codProducto as codigo, cantidad as cantInicial, " + "cantidad - "
						+ "ifnull(( select SUM( d.cantidad ) from [02Encabezado] e inner join [02Detalle] d on d.Numdoc = e.NumeroDoc and d.CodigoRef = i.codProducto where fechaTrans IS NOT NULL and e.tipoTrans = 'T' and   e.numerodoc not in(select distinct numerodoc from anulaciones) group by d.CodigoRef ),0) + "
						+ "ifnull(( select SUM( d.cantidad ) from [02Encabezado] e inner join [02Detalle] d on d.Numdoc = e.NumeroDoc and d.CodigoRef = i.codProducto where fechaTrans IS NOT NULL and e.tipoTrans = 'C' and   e.numerodoc not in(select distinct numerodoc from anulaciones) group by d.CodigoRef ),0) as cantActual, "
						+ "ifnull(( select SUM( d.cantidad ) from [02Encabezado] e inner join [02Detalle] d on d.Numdoc = e.NumeroDoc and d.tipo in( 1,4) and d.CodigoRef = i.codProducto where fechaTrans IS NOT NULL and e.tipoTrans = 'T' and   e.numerodoc not in(select distinct numerodoc from anulaciones) group by d.CodigoRef ),0) as cantVenta, "
						+ "ifnull(( select SUM( d.cantidad ) from [02Encabezado] e inner join [02Detalle] d on d.Numdoc = e.NumeroDoc and d.tipo in(2,3) and d.CodigoRef = i.codProducto where fechaTrans IS NOT NULL and e.tipoTrans = 'T' and   e.numerodoc not in(select distinct numerodoc from anulaciones) group by d.CodigoRef ),0) as cantVentaCambio, "
						+ "ifnull(( select SUM( d.cantidad ) from [02Encabezado] e inner join [02Detalle] d on d.Numdoc = e.NumeroDoc and d.tipo = 1 and d.CodigoRef = i.codProducto where fechaTrans IS NOT NULL and e.tipoTrans = 'C' and   e.numerodoc not in(select distinct numerodoc from anulaciones) group by d.CodigoRef ),0) as cantDevolucion, "
						+ "ifnull(( select SUM( d.cantidad ) from [02Encabezado] e inner join [02Detalle] d on d.Numdoc = e.NumeroDoc and d.tipo <> 1 and d.CodigoRef = i.codProducto where fechaTrans IS NOT NULL and e.tipoTrans = 'C' and  e.numerodoc not in(select distinct numerodoc from anulaciones) group by d.CodigoRef ),0) as cantDevolucionCambio, "
						+ "p.descripcion as nombre " + "from inventario i "
						+ "inner join productos p on p.codigo = i.codProducto "
						+ "order by  CAST(codProducto as integer) ";// +

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					do {

						informeInv = new InformeInventario();
						informeInv.codigo = cursor.getString(cursor.getColumnIndex("codigo"));
						informeInv.nombre = cursor.getString(cursor.getColumnIndex("nombre"));
						informeInv.invActual = Util.ToInt(cursor.getString(cursor.getColumnIndex("cantActual")));
						informeInv.cantVentas = Util.ToInt(cursor.getString(cursor.getColumnIndex("cantVenta")));
						informeInv.cantDev = Util.ToInt(cursor.getString(cursor.getColumnIndex("cantDevolucion")));
						informeInv.cantVentaC = Util.ToInt(cursor.getString(cursor.getColumnIndex("cantVentaCambio")));
						informeInv.cantDevC = Util
								.ToInt(cursor.getString(cursor.getColumnIndex("cantDevolucionCambio")));
						informeInv.invInicial = Util.ToInt(cursor.getString(cursor.getColumnIndex("cantInicial")));

						listaInformeInv.addElement(informeInv);

					} while (cursor.moveToNext());
				}

				if (cursor != null)
					cursor.close();

			} else {

				Log.e(TAG, "CargarInformeInventario -> La Base de Datos No Existe");
			}

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, "CargarInformeInventario -> " + mensaje, e);

		} finally {

			if (db != null)
				db.close();
		}

		return listaInformeInv;
	}

	public static AutoCuadre obtenerAutoCuadre() {

		mensaje = "";
		SQLiteDatabase db = null;
		AutoCuadre autoCuadre = new AutoCuadre();
		float ventas = 0;
		float vcredito = 0;
		float cartera = 0;
		float gastos = 0;
		float total = 0;
		float vcontado = 0;
		float devoluciones;
		float dcredito;
		float dcontado;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = "SELECT sum(montofact+iva-desc1) as ventas FROM  [02ENCABEZADO] "
					+ " where  numerodoc  not in(select distinct numeroDoc from anulaciones) and tipoTrans = 'T' or tipoTrans = '0' ";

			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				do {

					ventas = cursor.getFloat(cursor.getColumnIndex("ventas"));
					autoCuadre.ventas = ventas;
					break;

				} while (cursor.moveToNext());

			} else {

				mensaje = "Consulta sin Resultados";
			}

			query = "SELECT sum(montofact+iva-desc1) as credito FROM  [02ENCABEZADO]  where  numerodoc  not in(select distinct numeroDoc from anulaciones)   and tipoTrans = 'T' and fp=2 ";

			cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				do {

					vcredito = cursor.getFloat(cursor.getColumnIndex("credito"));
					autoCuadre.ventasCredito = vcredito;
					break;

				} while (cursor.moveToNext());

			} else {

				mensaje = "Consulta sin Resultados";
			}

			query = "SELECT sum(montofact+iva-desc1) as contado FROM  [02ENCABEZADO]  where  numerodoc  not in(select distinct numeroDoc from anulaciones)   and tipoTrans = 'T' and fp=1 ";

			cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				do {

					vcontado = cursor.getFloat(cursor.getColumnIndex("contado"));
					autoCuadre.ventasContado = vcontado;
					break;

				} while (cursor.moveToNext());

			} else {

				mensaje = "Consulta sin Resultados";
			}

			query = "SELECT sum(montofact+iva-desc1) as devoluciones FROM  [02ENCABEZADO] "
					+ " where  numerodoc  not in(select distinct numeroDoc from anulaciones) and tipoTrans = 'C' or tipoTrans = '0' ";

			cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				do {

					devoluciones = cursor.getFloat(cursor.getColumnIndex("devoluciones"));
					autoCuadre.devoluciones = devoluciones;
					break;

				} while (cursor.moveToNext());

			} else {

				mensaje = "Consulta sin Resultados";
			}

			query = "SELECT sum(montofact+iva-desc1) as credito FROM  [02ENCABEZADO]  where  numerodoc  not in(select distinct numeroDoc from anulaciones)   and tipoTrans = 'C' and fp=2 ";

			cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				do {

					dcredito = cursor.getFloat(cursor.getColumnIndex("credito"));
					autoCuadre.devolucionesCredito = dcredito;
					break;

				} while (cursor.moveToNext());

			} else {

				mensaje = "Consulta sin Resultados";
			}

			query = "SELECT sum(montofact+iva-desc1) as contado FROM  [02ENCABEZADO]  where  numerodoc  not in(select distinct numeroDoc from anulaciones)   and tipoTrans = 'C' and fp=1 ";

			cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				do {

					dcontado = cursor.getFloat(cursor.getColumnIndex("contado"));
					autoCuadre.devolucionesContado = dcontado;
					break;

				} while (cursor.moveToNext());

			} else {

				mensaje = "Consulta sin Resultados";
			}

			/*
			 * 
			 * query =
			 * "SELECT sum(montofact+iva-desc1) as contado FROM  [02ENCABEZADO]  where  numerodoc  not in(select distinct numeroDoc from anulaciones)   and tipoTrans = 'T' and fp=2 "
			 * ;
			 * 
			 * cursor = db.rawQuery(query, null);
			 * 
			 * if (cursor.moveToFirst()) {
			 * 
			 * do {
			 * 
			 * con = cursor.getFloat(cursor.getColumnIndex("credito"));
			 * autoCuadre.ventasContado = credito; break;
			 * 
			 * } while (cursor.moveToNext());
			 * 
			 * 
			 * } else {
			 * 
			 * mensaje = "Consulta sin Resultados"; }
			 */

			query = "SELECT sum(total) as cartera FROM  ENCABEZADORECAUDO";

			cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				do {

					cartera = cursor.getFloat(cursor.getColumnIndex("cartera"));
					autoCuadre.cartera = cartera;
					break;

				} while (cursor.moveToNext());

			} else {

				mensaje = "Consulta sin Resultados";
			}

			query = "SELECT sum(valor) as gastos FROM  detallegastos";

			cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				do {

					gastos = cursor.getFloat(cursor.getColumnIndex("gastos"));
					autoCuadre.gastos = gastos;
					break;

				} while (cursor.moveToNext());

			} else {

				mensaje = "Consulta sin Resultados";
			}

			autoCuadre.total = autoCuadre.ventasContado - autoCuadre.devolucionesContado + autoCuadre.cartera
					- autoCuadre.gastos;

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e("DataBaseBO", mensaje, e);

		} finally {

			if (db != null)
				db.close();
		}

		return autoCuadre;
	}

	public static String NombreProducto(String codigo) {

		SQLiteDatabase db = null;
		String nombre = "";
		mensaje = "";

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = "SELECT descripcion as Nombre  FROM Productos WHERE Codigo = '" + codigo + "'";
			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				nombre = cursor.getString(cursor.getColumnIndex("Nombre"));
				mensaje = "Consulta Satisfactoria";

			} else {

				mensaje = "Consulta sin Resultados";
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}

		return nombre;
	}

	public static Vector<InformeInventario2> CargarInformeInventario2() {

		SQLiteDatabase db = null;
		InformeInventario2 informeInv2;
		Vector<InformeInventario2> listaInformeInv = new Vector<InformeInventario2>();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				String query = "select  i.cod_producto as codigo, SUM(cantidad) as cantInicial,  "
						+ " cantidad as cantActual, " +
						// " cantidad - "+
						// " ifnull(( select SUM( d.cantidad ) from
						// encabezado_venta e inner join detalle_venta d on
						// d.documento = e.documento and d.cod_producto =
						// i.cod_producto where e.cod_tipo_trans = '2' and
						// e.sync = 0 group by d.cod_producto ),0) +"+
						// " ifnull(( select SUM( d.cantidad ) from
						// encabezado_venta e inner join detalle_venta d on
						// d.documento = e.documento and d.cod_producto =
						// i.cod_producto where e.cod_tipo_trans = '5' and
						// e.sync = 0 group by d.cod_producto ),0) as
						// cantActual, "+
						" ifnull(( select SUM( d.cantidad ) from encabezado_venta e inner join detalle_venta d on d.documento = e.documento  and d.cod_producto = i.cod_producto where e.cod_tipo_trans = '2' group by d.cod_producto ),0) as cantVenta, "
						+ " ifnull(( select SUM( d.cantidad ) from encabezado_venta e inner join detalle_venta d on d.documento = e.documento and d.cod_producto = i.cod_producto where  e.cod_tipo_trans = '5' group by d.cod_producto ),0) as cantVentaCambio, "
						+ " ifnull(( select SUM( d.cantidad ) from encabezado_venta e inner join detalle_venta d on d.documento = e.documento and  d.cod_producto = i.cod_producto where  e.cod_tipo_trans = '5' group by d.cod_producto ),0) as cantDevolucion, "
						+ " ifnull(( select SUM( d.cantidad ) from encabezado_venta e inner join detalle_venta d on d.documento = e.documento and  d.cod_producto = i.cod_producto where  e.cod_tipo_trans = '5' group by d.cod_producto ),0) as cantDevolucionCambio, "
						+ " p.[nombre] as nombre, " + " CantidadInicial as CantidadInicial " + " from inventario i "
						+ " inner join productos p on p.cod_producto = i.cod_producto " + " WHERE i.[cantidad] > 0 "
						+ " group by  i.cod_producto "
						+ " order by  CAST(replace(p.[cod_producto],'PF', '') AS INTEGER) ";

				System.out.println("Query Inventario: " + query);

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					do {

						informeInv2 = new InformeInventario2();
						informeInv2.codigo = " " + cursor.getString(cursor.getColumnIndex("codigo"));
						informeInv2.nombre = cursor.getString(cursor.getColumnIndex("nombre"));

						informeInv2.invActual = cursor.getInt(cursor.getColumnIndex("cantActual"));
						informeInv2.strInvActual = String.valueOf(informeInv2.invActual) + "                     .";

						informeInv2.cantVentas = cursor.getInt(cursor.getColumnIndex("cantVenta"));
						informeInv2.strCantVentas = String.valueOf(informeInv2.cantVentas) + "                   .";

						informeInv2.cantDev = cursor.getInt(cursor.getColumnIndex("cantDevolucion"));
						informeInv2.strCantDev = String.valueOf(informeInv2.cantDev) + "                         .";

						informeInv2.cantVentaC = cursor.getInt(cursor.getColumnIndex("cantVentaCambio"));
						informeInv2.strCantVentaC = String.valueOf(informeInv2.cantVentaC) + "                   .";

						informeInv2.cantDevC = cursor.getInt(cursor.getColumnIndex("cantDevolucionCambio"));
						informeInv2.strCantDevC = String.valueOf(informeInv2.cantDevC) + "                       .";

						informeInv2.invInicial = cursor.getInt(cursor.getColumnIndex("cantInicial"));
						informeInv2.strInvInicial = String.valueOf(informeInv2.invInicial) + "                   .";

						listaInformeInv.addElement(informeInv2);

					} while (cursor.moveToNext());
				}

				if (cursor != null)
					cursor.close();

			} else {

				Log.e(TAG, "CargarInformeInventario -> La Base de Datos No Existe");
			}

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, "CargarInformeInventario -> " + mensaje, e);

		} finally {

			if (db != null)
				db.close();
		}

		return listaInformeInv;
	}

	public static Vector<InformeInventario2> CargarInformeInventario3() {

		SQLiteDatabase db = null;
		InformeInventario2 informeInv2;
		Vector<InformeInventario2> listaInformeInv = new Vector<InformeInventario2>();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				String query = " SELECT p.nombre as nombre, i.cod_producto AS codigo, sum(i.CantidadInventario) as cantInicial, sum(i.CantidadInventario) - ( select sum([detalle_venta].[cantidad]) as cantidad from [encabezado_venta] inner join [detalle_venta] on [detalle_venta].[documento] = [encabezado_venta].documento "
						+ " where cod_tipo_trans=2 group by detalle_venta.[cod_producto]) as cantActual "
						+ " FROM    inventario i " + " inner join productos p on p.cod_producto = i.cod_producto  "
						+ " GROUP BY i.cod_producto ORDER BY CAST(REPLACE(i.[cod_producto], 'PF', '') AS INTEGER)";

				System.out.println("Query Inventario: " + query);

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					do {

						informeInv2 = new InformeInventario2();
						informeInv2.codigo = " " + cursor.getString(cursor.getColumnIndex("codigo"));
						informeInv2.nombre = cursor.getString(cursor.getColumnIndex("nombre"));

						informeInv2.invInicial = cursor.getInt(cursor.getColumnIndex("cantInicial"));
						informeInv2.strInvInicial = String.valueOf(informeInv2.invInicial) + "                   .";

						informeInv2.cantVentas = cantidadVentas(informeInv2.codigo);
						informeInv2.strCantVentas = String.valueOf(informeInv2.cantVentas) + "                   .";

						informeInv2.cantDev = 0;
						informeInv2.strCantDev = String.valueOf(informeInv2.cantDev) + "                         .";

						informeInv2.cantVentaC = cantidadCambios(informeInv2.codigo);
						informeInv2.strCantVentaC = String.valueOf(informeInv2.cantVentaC) + "                   .";

						informeInv2.invActual = cursor.getInt(cursor.getColumnIndex("cantInicial"))
								- informeInv2.cantVentas + informeInv2.cantVentaC;
						informeInv2.strInvActual = String.valueOf(informeInv2.invActual) + "                     .";

						informeInv2.cantDevC = 0;
						informeInv2.strCantDevC = String.valueOf(informeInv2.cantDevC) + "                       .";

						listaInformeInv.addElement(informeInv2);

					} while (cursor.moveToNext());
				}

				if (cursor != null)
					cursor.close();

			} else {

				Log.e(TAG, "CargarInformeInventario -> La Base de Datos No Existe");
			}

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, "CargarInformeInventario -> " + mensaje, e);

		} finally {

			if (db != null)
				db.close();
		}

		return listaInformeInv;
	}

	public static Vector<Cliente> BuscarClientes(String where, Vector<ItemListView> listaItems) {

		mensaje = "";
		Cliente cliente;
		SQLiteDatabase db = null;

		ItemListView itemListView;
		Vector<Cliente> listaClientes = new Vector<Cliente>();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = " Select c.cod_cliente  as codigo,c.representante_legal as nombre, c.razon_social as razonSocial,direccion as direccion,cod_lista as cod_lista,c.ciudad,c.barrio,c.telefono, c.codigoStatusCliente as estatus, c.codigotipocliente as tipoCliente from clientes as c "
					+ where;

			int contador = 1;

			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				do {

					cliente = new Cliente();
					itemListView = new ItemListView();

					cliente.codigo = cursor.getString(cursor.getColumnIndex("codigo")).trim();
					cliente.nombre = cursor.getString(cursor.getColumnIndex("nombre")).trim();
					cliente.razonSocial = cursor.getString(cursor.getColumnIndex("razonSocial")).trim();
					cliente.direccion = cursor.getString(cursor.getColumnIndex("direccion")).trim();
					cliente.codLista = cursor.getString(cursor.getColumnIndex("cod_lista")).trim();
					cliente.ciudad = cursor.getString(cursor.getColumnIndex("ciudad")).trim();
					cliente.telefono = cursor.getString(cursor.getColumnIndex("telefono")).trim();
					cliente.barrio = cursor.getString(cursor.getColumnIndex("barrio")).trim();
					cliente.estatus = cursor.getInt(cursor.getColumnIndex("estatus"));
					cliente.tipoCliente = cursor.getInt(cursor.getColumnIndex("tipoCliente"));

					itemListView.titulo = contador + ") " + cliente.codigo + " " + cliente.nombre + "\n"
							+ cliente.razonSocial;
					itemListView.subTitulo = cliente.ciudad + " - " + cliente.direccion;

					listaItems.add(itemListView);
					listaClientes.addElement(cliente);

					contador++;

				} while (cursor.moveToNext());

				mensaje = "Rutero Cargado Correctamente";

			} else {

				mensaje = "Consulta sin Resultados";
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}

		return listaClientes;
	}

	public static boolean buscarProductoPorParametroConocido(String where, List<Producto> listaProductos) {
		SQLiteDatabase db = null;
		boolean finalizado = false;
		try {
			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			/*
			 * Consulta para obtener la lista de productos disponibles para
			 * autoventa
			 */
			// String query = "select
			// cod_producto,nombre,precio,iva,descuento,cod_linea from productos
			// where cod_producto like '%"+parametroBusqueda+"%' or nombre like
			// '%"+parametroBusqueda+"%'";
			// String query = "select
			// cod_producto,nombre,precio,iva,descuento,cod_linea from productos
			// where cod_producto = '"+parametroBusqueda+"'";
			String query = "select cod_producto,nombre,precio,iva,descuento,cod_linea from productos " + where;
			System.out.println("consula de busqueda de producto: " + query);
			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				do {
					Producto producto = new Producto();
					producto.codigo = cursor.getString(cursor.getColumnIndex("cod_producto"));
					producto.descripcion = cursor.getString(cursor.getColumnIndex("nombre"));
					producto.precio = cursor.getFloat(cursor.getColumnIndex("precio"));
					producto.iva = cursor.getFloat(cursor.getColumnIndex("iva"));
					producto.descuento = cursor.getFloat(cursor.getColumnIndex("descuento"));
					producto.codLinea = cursor.getString(cursor.getColumnIndex("cod_linea"));

					/* agregar producto a la lista */
					listaProductos.add(producto);

				} while (cursor.moveToNext());
			}

			// cerrar cursor.
			if (cursor != null) {
				cursor.close();
			}

			/* verificar que la lista de productos no este vacia */
			if (!listaProductos.isEmpty()) {
				finalizado = true;
			}
		} catch (Exception e) {
			mensaje = e.getMessage();
		} finally {
			if (db != null)
				db.close();
		}
		return finalizado;
	}

	
	/**
	 * Metodo que permite borrar un producto que ha sido pedido. y se desea
	 * quitar de la lista de detalle de pedido.
	 * 
	 * @param producto
	 * @by JICZ
	 */
	public static boolean quitarProductoProductoPedidoAv(Producto producto) {
		SQLiteDatabase db = null;
		SQLiteDatabase tmp = null;
		boolean borrado = false;

		try {
			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			File dbTmp = new File(Util.DirApp(), "Temp.db");
			tmp = SQLiteDatabase.openDatabase(dbTmp.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			// iniciar transacciones para garantizar la correcta insercion en
			// las base de datos.
			db.beginTransaction();
			tmp.beginTransaction();

			// se hace el borrado del producto pedidos. en ambas bases de datos.
			long dbDel = db.delete("detalle_venta", "cod_producto = '" + producto.codigo + "' AND documento = 'NT'",
					null);
			long tmpDel = tmp.delete("detalle_venta", "cod_producto = '" + producto.codigo + "' AND documento = 'NT'",
					null);

			// verificar que el borrado fue correcto en ambas bases de datos. y
			// confirmar la transaccion como exitosa.
			if (dbDel == tmpDel) {
				db.setTransactionSuccessful();
				tmp.setTransactionSuccessful();
				borrado = true;
			} else {
				throw new Exception("No se logró borrar datos de producto " + producto.codigo);
			}
		} catch (Exception e) {
			Log.e("Eliminando producto en Detalle_pedido_av", "error: " + e.getMessage());
		}

		finally {
			closeDataBase(db);
			closeDataBase(tmp);
		}

		return borrado;
	}

	/**
	 * Metodo que modifica la cantidad pedida de un producto por una nueva
	 * cantidad.
	 * 
	 * @param producto
	 * @return
	 * @by JICZ
	 */
	public static boolean modificarProductoProductoPedidoAv(Producto producto) {

		SQLiteDatabase db = null;
		SQLiteDatabase tmp = null;
		boolean actualizado = false;

		try {
			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			File dbTmp = new File(Util.DirApp(), "Temp.db");
			tmp = SQLiteDatabase.openDatabase(dbTmp.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			// iniciar transacciones para garantizar la correcta insercion en
			// las base de datos.
			db.beginTransaction();
			tmp.beginTransaction();

			/*
			 * captura de los datos que seran insertados en las bases de datos.
			 */
			ContentValues values = new ContentValues();
			values = new ContentValues();

			values.put("cantidad", producto.cantidadPedida);

			// se hace la atualizacion del producto pedido. en ambas bases de
			// datos.
			long dbDel = db.update("detalle_venta", values,
					"cod_producto = '" + producto.codigo + "' AND documento = 'NT'", null);
			long tmpDel = tmp.update("detalle_venta", values,
					"cod_producto = '" + producto.codigo + "' AND documento = 'NT'", null);

			// verificar que la actualizacion fue correcta en ambas bases de
			// datos. y confirmar la transaccion como exitosa.
			if (dbDel == tmpDel) {
				db.setTransactionSuccessful();
				tmp.setTransactionSuccessful();
				actualizado = true;
			} else {
				throw new Exception("No se logró actualizar datos de producto " + producto.codigo);
			}
		} catch (Exception e) {
			Log.e("Actualizando producto en Detalle_pedido_av", "error: " + e.getMessage());
		}

		finally {
			closeDataBase(db);
			closeDataBase(tmp);
		}
		return actualizado;
	}

	public static boolean consecutivoEnElRango(long consecutivoRecibido) {

		SQLiteDatabase db = null;
		String sql = "";
		long consecutivo = 0;
		long rangoInicial = 0;
		long rangoFinal = 0;
		String strRangoI = "";
		String strRangoF = "";

		consecutivo = consecutivoRecibido;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			sql = "select limite_inf, limite_sup " + "from resoluciones " + "limit 1";

			Cursor cursor = db.rawQuery(sql, null);

			if (cursor.moveToFirst()) {

				strRangoI = cursor.getString(cursor.getColumnIndex("limite_inf"));
				strRangoF = cursor.getString(cursor.getColumnIndex("limite_sup"));

				rangoInicial = Util.ToLong(strRangoI);
				rangoFinal = Util.ToLong(strRangoF);
			}
		} catch (Exception e) {

		} finally {

			closeDataBase(db);
		}

		if (rangoInicial <= consecutivo && rangoFinal >= consecutivo)
			return true;
		else
			return false;
	}

	public static long getConsecutivoActual() {

		SQLiteDatabase db = null;
		String sql = "";
		long consecutivo = -1;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			sql = "select cons_actual " + "from resoluciones " + "limit 1";

			Log.e("getConsecutivoActual -> ", sql);

			Cursor cursor = db.rawQuery(sql, null);

			if (cursor.moveToFirst()) {

				consecutivo = cursor.getInt(cursor.getColumnIndex("cons_actual"));
				consecutivo++;
			}

			if (cursor != null)
				cursor.close();
		} catch (Exception e) {

		} finally {

			closeDataBase(db);
		}

		return consecutivo;
	}

	public static long getConsecutivoActualEncabezado(String prefijo) {

		SQLiteDatabase db = null;
		String sql = "";
		long consecutivo = -1;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			sql = "select max(consecutivo) as cons_actual from encabezado_venta where prefijo='" + prefijo + "' ";

			Log.e("getConsecutivoActualEncabezado -> ", sql);

			Cursor cursor = db.rawQuery(sql, null);

			if (cursor.moveToFirst()) {

				consecutivo = cursor.getInt(cursor.getColumnIndex("cons_actual"));
			}

			if (cursor != null)
				cursor.close();
		} catch (Exception e) {

		} finally {

			closeDataBase(db);
		}

		return consecutivo;
	}

	public static String getResolucion() {

		SQLiteDatabase db = null;
		String sql = "";
		String resolucion = "";

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			sql = "select cod_resolucion " + "from resoluciones " + "limit 1";

			Cursor cursor = db.rawQuery(sql, null);

			if (cursor.moveToFirst()) {

				resolucion = cursor.getString(cursor.getColumnIndex("cod_resolucion"));

			}

			if (cursor != null)
				cursor.close();
		} catch (Exception e) {

		} finally {

			closeDataBase(db);
		}

		return resolucion;
	}

	public static String getFactura(String documento) {

		SQLiteDatabase db = null;
		String sql = "";
		String factura = "";

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			sql = "select prefijo, consecutivo " + "from encabezado_venta where documento = '" + documento + "'";

			Cursor cursor = db.rawQuery(sql, null);

			if (cursor.moveToFirst()) {

				factura = cursor.getString(cursor.getColumnIndex("prefijo"))
						+ cursor.getString(cursor.getColumnIndex("consecutivo"));

			}

			if (cursor != null)
				cursor.close();
		} catch (Exception e) {

		} finally {

			closeDataBase(db);
		}

		return factura;
	}

	/**
	 * USe {@link #incrementarNumeroFactura(String)}
	 * 
	 * @return
	 */
	/*
	 * @Deprecated public static String ActualizarConsecutivo() {
	 * 
	 * String num = "-"; SQLiteDatabase db = null;
	 * 
	 * try {
	 * 
	 * File dbFile = new File(Util.DirApp(), "DataBase.db"); db =
	 * SQLiteDatabase.openDatabase(dbFile.getPath(), null,
	 * SQLiteDatabase.OPEN_READWRITE);
	 * 
	 * long consecutivo = 0; Cursor cursor = db.rawQuery(
	 * "SELECT cons_actual FROM resoluciones limit 1", null);
	 * 
	 * if (cursor.moveToFirst()) consecutivo =
	 * cursor.getLong(cursor.getColumnIndex("cons_actual"));
	 * 
	 * if (cursor != null) cursor.close();
	 * 
	 * consecutivo = (consecutivo + 1); db.execSQL(
	 * "UPDATE resoluciones SET cons_actual = " + consecutivo);
	 * 
	 * if (cursor != null) cursor.close();
	 * 
	 * 
	 * return num;
	 * 
	 * } catch ( Exception e ) {
	 * 
	 * mensaje = e.getMessage(); return "-";
	 * 
	 * } finally {
	 * 
	 * if (db != null) db.close(); } }
	 */

	public static boolean guardarConsecutivoResoluciones(String codUsuario, String codResolucion, long consecuivo) {

		SQLiteDatabase tmp = null;
		boolean insertado = false;
		long rowTmp;

		try {

			File dbTmp = new File(Util.DirApp(), "Temp.db");
			tmp = SQLiteDatabase.openDatabase(dbTmp.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			// consulta para generar el id.
			String sqlDelete = "delete from cons_resoluciones";

			tmp.execSQL(sqlDelete);

			/*
			 * captura de los datos que seran insertados en las bases de datos.
			 */
			ContentValues values = new ContentValues();
			values.put("cod_resolucion", codResolucion);
			values.put("consecutivo", consecuivo);
			values.put("cod_usuario", codUsuario);

			rowTmp = tmp.insertOrThrow("cons_resoluciones", null, values);

			// verificar que la actualizacion fue correcta en ambas bases de
			// datos. y confirmar la transaccion como exitosa.
			if (rowTmp != -1) {

				insertado = true;
			} else {

			}
		} catch (Exception e) {

		} finally {
			closeDataBase(tmp);
		}
		return insertado;
	}

	public static EstadisticaRecorrido CargarEstadisticas() {

		mensaje = "";
		SQLiteDatabase db = null;
		EstadisticaRecorrido estadisticaRecorrido = new EstadisticaRecorrido();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				String query = "";

				if (Const.tipoAplicacion == Const.AUTOVENTA) {

					query = " SELECT COUNT( distinct cod_cliente ) AS total_visitas, "
							+ " (SELECT COUNT(  cod_cliente )  FROM novedades) AS total_novedades, "
							+ " (SELECT COUNT(*) FROM encabezado_venta where cod_tipo_trans = '2' ) AS total_pedidos, "
							+ " (SELECT COUNT(*) FROM encabezado_venta where   cod_tipo_trans = '5' ) AS total_devoluciones, "
							+ " (SELECT COUNT(*) FROM novedades  WHERE cod_tipo_nov >5) AS no_compras, "
							+ " (SELECT COUNT(*) FROM encabezado_venta where    sync = 1 and ( cod_tipo_trans = '2'  ) ) AS total_pedidos_sync, "
							+ " (SELECT COUNT(*) FROM encabezado_venta where    sync = 0 and ( cod_tipo_trans = '2'  ) ) AS total_pedidos_sin_sync, "
							+ " (SELECT COUNT(*) FROM encabezado_venta where   sync = 1 and ( cod_tipo_trans = '5' ) ) AS total_devoluciones_sync, "
							+ " (SELECT COUNT(*) FROM encabezado_venta where    sync = 0 and ( cod_tipo_trans = '5' ) ) AS total_devoluciones_sin_sync, "
							+ " (SELECT ( SUM(valor)  ) FROM encabezado_venta where   cod_tipo_trans = '2' ) AS total_venta, "
							+ " (SELECT ( SUM(valor) ) FROM encabezado_venta where   cod_tipo_trans = '5' ) AS valor_devoluciones "
							+ " FROM novedades ";

				} else {

					if (Const.tipoAplicacion == Const.PREVENTA) {

						query = " SELECT COUNT( distinct cod_cliente ) AS total_visitas, "
								+ " (SELECT COUNT(  cod_cliente )  FROM novedades) AS total_novedades, "
								+ " (SELECT COUNT(*) FROM encabezado_venta where cod_tipo_trans = '1' ) AS total_pedidos, "
								+ " (SELECT COUNT(*) FROM encabezado_venta where   cod_tipo_trans = '4' ) AS total_devoluciones, "
								+ " (SELECT COUNT(*) FROM novedades  WHERE cod_tipo_nov >5) AS no_compras, "
								+ " (SELECT COUNT(*) FROM encabezado_venta where    sync = 1 and ( cod_tipo_trans = '1'  ) ) AS total_pedidos_sync, "
								+ " (SELECT COUNT(*) FROM encabezado_venta where    sync = 0 and ( cod_tipo_trans = '1'  ) ) AS total_pedidos_sin_sync, "
								+ " (SELECT COUNT(*) FROM encabezado_venta where   sync = 1 and ( cod_tipo_trans = '4' ) ) AS total_devoluciones_sync, "
								+ " (SELECT COUNT(*) FROM encabezado_venta where    sync = 0 and ( cod_tipo_trans = '4' ) ) AS total_devoluciones_sin_sync, "
								+ " (SELECT ( SUM(valor)  ) FROM encabezado_venta where   cod_tipo_trans = '1' ) AS total_venta, "
								+ " (SELECT ( SUM(valor) ) FROM encabezado_venta where   cod_tipo_trans = '4' ) AS valor_devoluciones "
								+ " FROM novedades ";

					} else {

					}

				}

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					estadisticaRecorrido.total_visitas = cursor.getInt(cursor.getColumnIndex("total_visitas"));
					estadisticaRecorrido.total_pedidos = cursor.getInt(cursor.getColumnIndex("total_pedidos"));
					estadisticaRecorrido.total_devoluciones = cursor
							.getInt(cursor.getColumnIndex("total_devoluciones"));
					estadisticaRecorrido.total_pedidos_sync = cursor
							.getInt(cursor.getColumnIndex("total_pedidos_sync"));
					estadisticaRecorrido.total_devoluciones_sync = cursor
							.getInt(cursor.getColumnIndex("total_devoluciones_sync"));
					estadisticaRecorrido.total_pedidos_sin_sync = cursor
							.getInt(cursor.getColumnIndex("total_pedidos_sin_sync"));
					estadisticaRecorrido.total_devoluciones_sin_sync = cursor
							.getInt(cursor.getColumnIndex("total_devoluciones_sin_sync"));
					estadisticaRecorrido.total_venta = cursor.getInt(cursor.getColumnIndex("total_venta"));
					estadisticaRecorrido.valor_devoluciones = cursor
							.getInt(cursor.getColumnIndex("valor_devoluciones"));
					estadisticaRecorrido.totalNoCompras = cursor.getInt(cursor.getColumnIndex("no_compras"));
					estadisticaRecorrido.totalNovedades = cursor.getInt(cursor.getColumnIndex("total_novedades"));

					// if (estadisticaRecorrido.total_visitas > 0)
					// estadisticaRecorrido.efectividad =
					// (estadisticaRecorrido.total_pedidos * 100) /
					// estadisticaRecorrido.total_visitas;

					if (estadisticaRecorrido.totalNovedades > 0)
						estadisticaRecorrido.efectividad = (estadisticaRecorrido.total_pedidos * 100)
								/ estadisticaRecorrido.totalNovedades;

				}

				if (cursor != null)
					cursor.close();

			} else {

				Log.e(TAG, "CargarEstadisticas -> No Existe la Base de Datos");
			}

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, "CargarEstadisticas -> " + mensaje, e);

		} finally {

			if (db != null)
				db.close();
		}

		return estadisticaRecorrido;
	}

	public static Deposito cargarDeposito(String documento) {

		mensaje = "";
		SQLiteDatabase db = null;
		ItemListView itemListView;

		Deposito deposito = new Deposito();

		try {
			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {
				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				String query = "SELECT d.documento AS documento, d.cod_usuario AS cod_usuario, "
						+ "d.observacion AS observacion, d.cod_imagen AS cod_imagen, "
						+ "d.compr_bancario AS compr_bancario, d.cod_est_deposito AS cod_est_deposito, "
						+ "d.cod_banco AS cod_banco, d.cant_cheques AS cant_cheques, d.valor AS valor, "
						+ "b.descripcion AS descripcion " + "FROM deposito_realizados d "
						+ "INNER JOIN bancos b ON b.cod_banco = d.cod_banco " + "WHERE documento ='" + documento + "'";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					do {

						deposito = new Deposito();
						deposito.documento = cursor.getString(cursor.getColumnIndex("documento"));
						deposito.cod_usuario = cursor.getString(cursor.getColumnIndex("cod_usuario"));
						deposito.observacion = cursor.getString(cursor.getColumnIndex("observacion"));
						deposito.cod_imagen = cursor.getString(cursor.getColumnIndex("cod_imagen"));
						deposito.compr_bancario = cursor.getString(cursor.getColumnIndex("compr_bancario"));
						deposito.cod_est_deposito = cursor.getInt(cursor.getColumnIndex("cod_est_deposito"));
						deposito.cod_banco = cursor.getInt(cursor.getColumnIndex("cod_banco"));
						deposito.cant_cheques = cursor.getString(cursor.getColumnIndex("cant_cheques"));
						deposito.totalDeposito = cursor.getFloat(cursor.getColumnIndex("valor"));
						deposito.desc_banco = cursor.getString(cursor.getColumnIndex("descripcion"));

						return deposito;

					} while (cursor.moveToNext());

				}

				if (cursor != null)
					cursor.close();

			} else {
				Log.e(TAG, "CargarDepositosRealizados: No existe la Base de Datos");
			}

		} catch (Exception e) {

		}

		return null;
	}

	public static ArrayList<DetalleDeposito> cargarDepositoRealizado() {

		mensaje = "";
		SQLiteDatabase db = null;
		ItemListView itemListView;

		DetalleDeposito detalleDeposito = new DetalleDeposito();
		ArrayList<DetalleDeposito> listaDeposito = new ArrayList<DetalleDeposito>();

		try {
			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {
				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				String query = "SELECT d.documento AS documento, d.cod_saldo_canc AS cod_saldo_canc, "
						+ "SUM(d.saldo) AS saldo, d.cod_est_deposito AS cod_est_deposito, "
						+ "d.cod_cliente AS cod_cliente, c.razon_social AS razon_social, " + "s.factura AS factura "
						+ "FROM detalle_deposito_realizados AS d "
						+ "INNER JOIN Clientes c ON d.cod_cliente = c.cod_cliente "
						+ "INNER JOIN [saldos_depositados] s ON s.[cod_saldo_canc] = d.cod_saldo_canc "
						+ "GROUP BY  documento";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					do {

						detalleDeposito = new DetalleDeposito();
						detalleDeposito.documento = cursor.getString(cursor.getColumnIndex("documento"));
						detalleDeposito.cod_saldo_canc = cursor.getString(cursor.getColumnIndex("cod_saldo_canc"));
						detalleDeposito.saldo = cursor.getFloat(cursor.getColumnIndex("saldo"));
						detalleDeposito.cod_est_deposito = cursor.getInt(cursor.getColumnIndex("cod_est_deposito"));
						detalleDeposito.cod_cliente = cursor.getString(cursor.getColumnIndex("cod_cliente"));
						detalleDeposito.razon_social_cliente = cursor.getString(cursor.getColumnIndex("razon_social"));
						detalleDeposito.documento = cursor.getString(cursor.getColumnIndex("documento"));
						detalleDeposito.factura = cursor.getString(cursor.getColumnIndex("factura"));
						detalleDeposito.saldo = cursor.getFloat(cursor.getColumnIndex("saldo"));
						detalleDeposito.saldoString = "" + detalleDeposito.saldo;

						listaDeposito.add(detalleDeposito);

					} while (cursor.moveToNext());

				}

				if (cursor != null)
					cursor.close();

			} else {
				Log.e(TAG, "CargarDepositosRealizados: No existe la Base de Datos");
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return listaDeposito;
	}

	public static ArrayList<DetalleDeposito> cargarDepositoRealizadoDetalle(String documento) {

		mensaje = "";
		SQLiteDatabase db = null;
		ItemListView itemListView;

		DetalleDeposito detalleDeposito = new DetalleDeposito();
		ArrayList<DetalleDeposito> listaDepositoDetalle = new ArrayList<DetalleDeposito>();

		try {
			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {
				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				String query = "SELECT d.documento AS documento, d.cod_saldo_canc AS cod_saldo_canc, "
						+ "d.saldo AS saldo, d.cod_est_deposito AS cod_est_deposito, "
						+ "d.cod_cliente AS cod_cliente, c.razon_social AS razon_social, " + "s.factura AS factura "
						+ "FROM detalle_deposito_realizados AS d "
						+ "INNER JOIN Clientes c ON d.cod_cliente = c.cod_cliente "
						+ "INNER JOIN [saldos_depositados] s ON s.[cod_saldo_canc] = d.cod_saldo_canc "
						+ "WHERE d.documento='" + documento + "'";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					do {

						detalleDeposito = new DetalleDeposito();
						detalleDeposito.documento = cursor.getString(cursor.getColumnIndex("documento"));
						detalleDeposito.cod_saldo_canc = cursor.getString(cursor.getColumnIndex("cod_saldo_canc"));
						detalleDeposito.saldo = cursor.getFloat(cursor.getColumnIndex("saldo"));
						detalleDeposito.cod_est_deposito = cursor.getInt(cursor.getColumnIndex("cod_est_deposito"));
						detalleDeposito.cod_cliente = cursor.getString(cursor.getColumnIndex("cod_cliente"));
						detalleDeposito.razon_social_cliente = cursor.getString(cursor.getColumnIndex("razon_social"));
						detalleDeposito.documento = cursor.getString(cursor.getColumnIndex("documento"));
						detalleDeposito.factura = cursor.getString(cursor.getColumnIndex("factura"));
						detalleDeposito.saldo = cursor.getFloat(cursor.getColumnIndex("saldo"));
						detalleDeposito.saldoString = "" + detalleDeposito.saldo;

						listaDepositoDetalle.add(detalleDeposito);

					} while (cursor.moveToNext());

				}

				if (cursor != null)
					cursor.close();

			} else {
				Log.e(TAG, "CargarDepositosRealizados: No existe la Base de Datos");
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return listaDepositoDetalle;
	}

	public static ArrayList<SaldoCancelado> cargarRecaudoRealizado() {

		mensaje = "";
		SQLiteDatabase db = null;
		ItemListView itemListView;

		SaldoCancelado saldosCancelados = new SaldoCancelado();
		ArrayList<SaldoCancelado> listaSaldosCanc = new ArrayList<SaldoCancelado>();

		try {
			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {
				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				String query = "SELECT  " + "s.cod_saldo_canc AS cod_saldo_canc, s.cod_saldo AS cod_saldo, "
						+ "s.cod_usuario AS cod_usuario, s.saldo_recibido AS saldo_recibido, "
						+ "s.cod_tipo_pago AS cod_tipo_pago, s.observacion AS observacion, " + "s.factura AS factura, "
						+ "c.razon_social AS razon_social," + "c.cod_cliente AS cod_cliente "
						+ "FROM saldos_depositados AS s " + "INNER JOIN Clientes c ON c.cod_cliente = s.cod_cliente ";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					do {

						saldosCancelados = new SaldoCancelado();
						saldosCancelados.cod_saldo_canc = cursor.getString(cursor.getColumnIndex("cod_saldo_canc"));
						saldosCancelados.cod_saldo = cursor.getString(cursor.getColumnIndex("cod_saldo"));
						saldosCancelados.cod_usuario = cursor.getString(cursor.getColumnIndex("cod_usuario"));
						saldosCancelados.saldo_recibido = cursor.getFloat(cursor.getColumnIndex("saldo_recibido"));
						saldosCancelados.cod_cliente = cursor.getString(cursor.getColumnIndex("cod_cliente"));
						saldosCancelados.razon_social = cursor.getString(cursor.getColumnIndex("razon_social"));
						saldosCancelados.codEstado = cursor.getInt(cursor.getColumnIndex("cod_tipo_pago"));
						saldosCancelados.factura = cursor.getString(cursor.getColumnIndex("factura"));
						saldosCancelados.observacion = cursor.getString(cursor.getColumnIndex("observacion"));
						// saldosCancelados.saldoString =
						// ""+detalleDeposito.saldo;

						listaSaldosCanc.add(saldosCancelados);

					} while (cursor.moveToNext());

				}

				if (cursor != null)
					cursor.close();

			} else {
				Log.e(TAG, "CargarDepositosRealizados: No existe la Base de Datos");
			}

		} catch (Exception e) {
			e.getMessage();
		}

		return listaSaldosCanc;
	}

	public static Vector<Encabezado> CargarPedidosRealizados(String fechaConsecutivo,
			Vector<ItemListView> listaItemsCliente, int sync) {

		if (Const.tipoAplicacion == Const.AUTOVENTA) {

			mensaje = "";
			SQLiteDatabase db = null;
			ItemListView itemListView;

			Encabezado encabezado;
			Vector<Encabezado> listaPedidos = new Vector<Encabezado>();

			if (listaItemsCliente == null)
				listaItemsCliente = new Vector<ItemListView>();

			try {

				File dbFile = new File(Util.DirApp(), "DataBase.db");

				if (dbFile.exists()) {

					db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

					String query = "";
					String query2 = "";

					if (sync == 1) {

						query = " SELECT distinct e.cod_cliente AS codigo_cliente, "
								+ " c.representante_legal as nombre_cliente, c.razon_social  AS razon_cliente, documento, valor, "
								+ " descuento, iva, e.cod_tipo_trans as tipoTrans " + " FROM encabezado_venta e "
								+ " INNER JOIN Clientes c ON e.cod_cliente = c.cod_cliente " + " ";

						query2 = " SELECT distinct e.cod_cliente AS codigo_cliente, "
								+ " cn.representante_legal as nombre_cliente, cn.razon_social  AS razon_cliente, documento, valor, "
								+ " descuento, iva, e.cod_tipo_trans as tipoTrans " + " FROM encabezado_venta e "
								+ " INNER JOIN clientes_nuevos cn ON e.cod_cliente = cn.cod_cliente "
								+ " WHERE e.cod_tipo_trans = '2' ";

						query = query + " UNION " + query2;

					} else

					if (sync == 4) {

						query = " SELECT distinct e.cod_cliente AS codigo_cliente, "
								+ " c.representante_legal as nombre_cliente, c.razon_social  AS razon_cliente, documento, valor, "
								+ " descuento, iva, e.cod_tipo_trans as tipoTrans " + " FROM encabezado_venta e "
								+ " INNER JOIN Clientes c ON e.cod_cliente = c.cod_cliente "
								+ " WHERE e.cod_tipo_trans = '5' ";

					} else

					if (sync == 0) {

						query = " SELECT distinct e.cod_cliente AS codigo_cliente, "
								+ " c.representante_legal as nombre_cliente, c.razon_social  AS razon_cliente, documento, valor, "
								+ " descuento, iva, e.cod_tipo_trans as tipoTrans " + " FROM encabezado_venta e "
								+ " INNER JOIN Clientes c ON e.cod_cliente = c.cod_cliente "
								+ " WHERE e.cod_tipo_trans = '2' and sync=0";

						query2 = " SELECT distinct e.cod_cliente AS codigo_cliente, "
								+ " cn.representante_legal as nombre_cliente, cn.razon_social  AS razon_cliente, documento, valor, "
								+ " descuento, iva, e.cod_tipo_trans as tipoTrans " + " FROM encabezado_venta e "
								+ " INNER JOIN clientes_nuevos cn ON e.cod_cliente = cn.cod_cliente "
								+ " WHERE e.cod_tipo_trans = '2' and sync=0";

						query = query + " UNION " + query2;

					}

					else {

						if (sync == -1) {

							query = " SELECT distinct e.cod_cliente AS codigo_cliente, "
									+ " cn.representante_legal as nombre_cliente, cn.razon_social  AS razon_cliente, documento, valor, "
									+ " descuento, iva, e.cod_tipo_trans as tipoTrans " + " FROM encabezado_venta e "
									+ " INNER JOIN clientes_nuevos cn ON e.cod_cliente = cn.cod_cliente "
									+ " WHERE e.cod_tipo_trans = '2' ";

						}

					}

					Cursor cursor = db.rawQuery(query, null);

					if (cursor.moveToFirst()) {

						do {

							encabezado = new Encabezado();
							encabezado.codigo_cliente = cursor.getString(cursor.getColumnIndex("codigo_cliente"));
							encabezado.nombre_cliente = cursor.getString(cursor.getColumnIndex("nombre_cliente"));
							encabezado.razon_social = cursor.getString(cursor.getColumnIndex("razon_cliente"));
							encabezado.numero_doc = cursor.getString(cursor.getColumnIndex("documento"));
							String tipoTrans = cursor.getString(cursor.getColumnIndex("tipoTrans"));
							encabezado.tipoDoc = tipoTrans;
							encabezado.codigo_novedad = 1;
							encabezado.extra_ruta = 0;
							encabezado.tipo_cliente = "V";

							float valor_neto = cursor.getFloat(cursor.getColumnIndex("valor"));

							itemListView = new ItemListView();

							itemListView.titulo = encabezado.codigo_cliente + " - " + encabezado.razon_social;

							itemListView.subTitulo = "Valor Venta: "
									+ Util.SepararMiles(Util.Redondear(Util.QuitarE("" + valor_neto), 0));

							if (tipoTrans.equals("2")) {

								itemListView.subTitulo += "\nTipo: " + "VENTA";
							} else {

								itemListView.subTitulo += "\nTipo: " + "DEVOLUCION";
							}

							listaItemsCliente.addElement(itemListView);
							listaPedidos.addElement(encabezado);

						} while (cursor.moveToNext());

					}

					if (cursor != null)
						cursor.close();

				} else {

					Log.e(TAG, "CargarPedidosRealizados: No existe la Base de Datos");
				}

			} catch (Exception e) {

				mensaje = e.getMessage();
				Log.e(TAG, "CargarPedidosRealizados: " + mensaje, e);

			} finally {

				if (db != null)
					db.close();
			}

			return listaPedidos;

		}

		else {

			mensaje = "";
			SQLiteDatabase db = null;
			ItemListView itemListView;

			Encabezado encabezado;
			Vector<Encabezado> listaPedidos = new Vector<Encabezado>();

			if (listaItemsCliente == null)
				listaItemsCliente = new Vector<ItemListView>();

			try {

				File dbFile = new File(Util.DirApp(), "DataBase.db");

				if (dbFile.exists()) {

					db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

					String query = "";
					String query2 = "";

					if (sync == 1) {

						query = " SELECT distinct e.cod_cliente AS codigo_cliente, "
								+ " c.representante_legal as nombre_cliente, c.razon_social  AS razon_cliente, documento, valor, "
								+ " descuento, iva, e.cod_tipo_trans as tipoTrans " + " FROM encabezado_venta e "
								+ " INNER JOIN Clientes c ON e.cod_cliente = c.cod_cliente " + " ";

						query2 = " SELECT distinct e.cod_cliente AS codigo_cliente, "
								+ " cn.representante_legal as nombre_cliente, cn.razon_social  AS razon_cliente, documento, valor, "
								+ " descuento, iva, e.cod_tipo_trans as tipoTrans " + " FROM encabezado_venta e "
								+ " INNER JOIN clientes_nuevos cn ON e.cod_cliente = cn.cod_cliente "
								+ " WHERE e.cod_tipo_trans = '1' ";

						query = query + " UNION " + query2;

					} else

					if (sync == 4) {

						query = " SELECT distinct e.cod_cliente AS codigo_cliente, "
								+ " c.representante_legal as nombre_cliente, c.razon_social  AS razon_cliente, documento, valor, "
								+ " descuento, iva, e.cod_tipo_trans as tipoTrans " + " FROM encabezado_venta e "
								+ " INNER JOIN Clientes c ON e.cod_cliente = c.cod_cliente "
								+ " WHERE e.cod_tipo_trans = '4' ";

					} else {

						if (sync == 0) {

							query = " SELECT distinct e.cod_cliente AS codigo_cliente, "
									+ " c.representante_legal as nombre_cliente, c.razon_social  AS razon_cliente, documento, valor, "
									+ " descuento, iva, e.cod_tipo_trans as tipoTrans " + " FROM encabezado_venta e "
									+ " INNER JOIN Clientes c ON e.cod_cliente = c.cod_cliente "
									+ " WHERE e.cod_tipo_trans = '1' and sync=0";

							query2 = " SELECT distinct e.cod_cliente AS codigo_cliente, "
									+ " cn.representante_legal as nombre_cliente, cn.razon_social  AS razon_cliente, documento, valor, "
									+ " descuento, iva, e.cod_tipo_trans as tipoTrans " + " FROM encabezado_venta e "
									+ " INNER JOIN clientes_nuevos cn ON e.cod_cliente = cn.cod_cliente "
									+ " WHERE e.cod_tipo_trans = '1' and sync=0";

							query = query + " UNION " + query2;

						}

						else {

							if (sync == -1) {

								query = " SELECT distinct e.cod_cliente AS codigo_cliente, "
										+ " cn.representante_legal as nombre_cliente, cn.razon_social  AS razon_cliente, documento, valor, "
										+ " descuento, iva, e.cod_tipo_trans as tipoTrans "
										+ " FROM encabezado_venta e "
										+ " INNER JOIN clientes_nuevos cn ON e.cod_cliente = cn.cod_cliente "
										+ " WHERE e.cod_tipo_trans = '1' ";

							}

						}

					}

					Cursor cursor = db.rawQuery(query, null);

					if (cursor.moveToFirst()) {

						do {

							encabezado = new Encabezado();
							encabezado.codigo_cliente = cursor.getString(cursor.getColumnIndex("codigo_cliente"));
							encabezado.nombre_cliente = cursor.getString(cursor.getColumnIndex("nombre_cliente"));
							encabezado.razon_social = cursor.getString(cursor.getColumnIndex("razon_cliente"));
							encabezado.numero_doc = cursor.getString(cursor.getColumnIndex("documento"));
							String tipoTrans = cursor.getString(cursor.getColumnIndex("tipoTrans"));
							encabezado.tipoDoc = tipoTrans;
							encabezado.codigo_novedad = 1;
							encabezado.extra_ruta = 0;
							encabezado.tipo_cliente = "V";

							float valor_neto = cursor.getFloat(cursor.getColumnIndex("valor"));

							itemListView = new ItemListView();

							itemListView.titulo = encabezado.codigo_cliente + " - " + encabezado.razon_social;

							itemListView.subTitulo = "Valor Venta: "
									+ Util.SepararMiles(Util.Redondear(Util.QuitarE("" + valor_neto), 0));

							if (tipoTrans.equals("2") || tipoTrans.equals("1")) {

								itemListView.subTitulo += "\nTipo: " + "Venta";
							} else {

								itemListView.subTitulo += "\nTipo: " + "DEVOLUCION";
							}

							listaItemsCliente.addElement(itemListView);
							listaPedidos.addElement(encabezado);

						} while (cursor.moveToNext());

					}

					if (cursor != null)
						cursor.close();

				} else {

					Log.e(TAG, "CargarPedidosRealizados: No existe la Base de Datos");
				}

			} catch (Exception e) {

				mensaje = e.getMessage();
				Log.e(TAG, "CargarPedidosRealizados: " + mensaje, e);

			} finally {

				if (db != null)
					db.close();
			}

			return listaPedidos;

		}

	}

	public static Vector<Cliente> ClienteNoCompra(Vector<ItemListView> listaItemsCliente) {

		Cliente cliente;
		SQLiteDatabase db = null;

		ItemListView itemListView;
		Vector<Cliente> listaClientes = new Vector<Cliente>();

		if (listaItemsCliente == null)
			listaItemsCliente = new Vector<ItemListView>();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = " SELECT distinct Clientes.cod_cliente AS Codigo, razon_social, Clientes.representante_legal AS Nombre, Direccion, Nit, "
					+ " tipo_novedad.descripcion AS MotivoNoCompra FROM novedades INNER JOIN Clientes ON "
					+ " novedades.cod_cliente = Clientes.cod_cliente INNER JOIN tipo_novedad ON tipo_novedad.cod_tipo_nov = "
					+ " novedades.cod_tipo_nov LEFT JOIN encabezado_venta ON novedades.documento = "
					+ " encabezado_venta.documento WHERE encabezado_venta.documento IS NULL ";

			String query2 = " SELECT distinct clientes_nuevos.cod_cliente AS Codigo, razon_social, clientes_nuevos.representante_legal AS Nombre, Direccion, Nit, "
					+ " tipo_novedad.descripcion AS MotivoNoCompra FROM novedades INNER JOIN clientes_nuevos ON "
					+ " novedades.cod_cliente = clientes_nuevos.cod_cliente INNER JOIN tipo_novedad ON tipo_novedad.cod_tipo_nov = "
					+ " novedades.cod_tipo_nov LEFT JOIN encabezado_venta ON novedades.documento = "
					+ " encabezado_venta.documento WHERE encabezado_venta.documento IS NULL ";

			query = query + " UNION " + query2;

			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				do {

					cliente = new Cliente();
					itemListView = new ItemListView();

					cliente.codigo = cursor.getString(cursor.getColumnIndex("Codigo")).trim();
					cliente.razonSocial = cursor.getString(cursor.getColumnIndex("razon_social")).trim();
					cliente.nombre = cursor.getString(cursor.getColumnIndex("Nombre"));
					cliente.direccion = cursor.getString(cursor.getColumnIndex("Direccion")).trim();

					cliente.nit = cursor.getString(cursor.getColumnIndex("Nit")).trim();
					// cliente.ciudad =
					// cursor.getString(cursor.getColumnIndex("Ciudad"));

					// cliente.ruta_parada =
					// cursor.getString(cursor.getColumnIndex("RutaBinario"));
					// cliente.ruta_parada = "";
					cliente.motivoNoCompra = cursor.getString(cursor.getColumnIndex("MotivoNoCompra"));

					itemListView.titulo = cliente.codigo + " - " + cliente.razonSocial;
					itemListView.subTitulo = "Motivo: " + cliente.motivoNoCompra;

					listaClientes.addElement(cliente);
					listaItemsCliente.addElement(itemListView);

				} while (cursor.moveToNext());

				mensaje = "Busqueda de Clientes Satisfactoria";
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, mensaje, e);

		} finally {

			if (db != null)
				db.close();
		}

		return listaClientes;
	}

	public static Vector<Encabezado> CargarPedidosRealizados(String fechaConsecutivo, int sync) {

		if (sync == 0 || sync == 1) {

			mensaje = "";
			SQLiteDatabase db = null;
			ItemListView itemListView;

			Encabezado encabezado;
			Vector<Encabezado> listaPedidos = new Vector<Encabezado>();

			try {

				File dbFile = new File(Util.DirApp(), "DataBase.db");

				if (dbFile.exists()) {

					db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

					String query = "";

					if (Const.tipoAplicacion == Const.AUTOVENTA) {

						query = " 	SELECT distinct encabezado_venta.cod_cliente AS codigo_cliente, "
								+ "    clientes.razon_social  AS nombre_cliente, documento, valor,    "
								+ "    descuento, iva,fecha_movil, sync FROM encabezado_venta INNER JOIN Clientes ON encabezado_venta.cod_cliente "
								+ "    = Clientes.cod_cliente WHERE ( cod_tipo_trans = '2' ) "
								+ "    order by fecha_movil desc ";

					}

					if (Const.tipoAplicacion == Const.PREVENTA) {

						query = " 	SELECT distinct encabezado_venta.cod_cliente AS codigo_cliente, "
								+ "    clientes.razon_social  AS nombre_cliente, documento, valor,    "
								+ "    descuento, iva,fecha_movil, sync FROM encabezado_venta INNER JOIN Clientes ON encabezado_venta.cod_cliente "
								+ "    = Clientes.cod_cliente WHERE ( cod_tipo_trans = '1' ) "
								+ "    order by fecha_movil desc ";

					}

					Cursor cursor = db.rawQuery(query, null);

					if (cursor.moveToFirst()) {

						do {

							encabezado = new Encabezado();
							encabezado.codigo_cliente = cursor.getString(cursor.getColumnIndex("codigo_cliente"));
							encabezado.nombre_cliente = cursor.getString(cursor.getColumnIndex("nombre_cliente"));
							encabezado.numero_doc = cursor.getString(cursor.getColumnIndex("documento"));
							encabezado.sync = cursor.getInt(cursor.getColumnIndex("sync"));
							encabezado.fecha = cursor.getString(cursor.getColumnIndex("fecha_movil"));

							float valor_neto = cursor.getFloat(cursor.getColumnIndex("valor"));

							encabezado.valor_neto = valor_neto;

							listaPedidos.addElement(encabezado);

						} while (cursor.moveToNext());

					}

					if (cursor != null)
						cursor.close();

				} else {

					Log.e(TAG, "CargarPedidosRealizados: No existe la Base de Datos");
				}

			} catch (Exception e) {

				mensaje = e.getMessage();
				Log.e(TAG, "CargarPedidosRealizados: " + mensaje, e);

			} finally {

				if (db != null)
					db.close();
			}

			return listaPedidos;

		} else {

			mensaje = "";
			SQLiteDatabase db = null;
			ItemListView itemListView;

			Encabezado encabezado;
			Vector<Encabezado> listaPedidos = new Vector<Encabezado>();

			try {

				File dbFile = new File(Util.DirApp(), "DataBase.db");

				if (dbFile.exists()) {

					db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

					String query = "";

					if (Const.tipoAplicacion == Const.AUTOVENTA) {

						query = "SELECT [02Encabezado].codigo AS codigo_cliente, ClientesNuevos.nombre AS nombre_cliente, numeroDoc, montoFact, desc1, iva "
								+ "FROM [02Encabezado] "
								+ "INNER JOIN ClientesNuevos ON [02Encabezado].codigo = ClientesNuevos.codigo "
								+ "WHERE tipoTrans = '0' AND fechatrans IS NOT NULL AND sync = '1' or sync = '0' ";

					}

					if (Const.tipoAplicacion == Const.PREVENTA) {

						query = "SELECT [02Encabezado].codigo AS codigo_cliente, ClientesNuevos.nombre AS nombre_cliente, numeroDoc, montoFact, desc1, iva "
								+ "FROM [02Encabezado] "
								+ "INNER JOIN ClientesNuevos ON [02Encabezado].codigo = ClientesNuevos.codigo "
								+ "WHERE tipoTrans = '0' AND fechatrans IS NOT NULL AND sync = '1' or sync = '0' ";

					}

					Cursor cursor = db.rawQuery(query, null);

					if (cursor.moveToFirst()) {

						do {

							encabezado = new Encabezado();
							encabezado.codigo_cliente = cursor.getString(cursor.getColumnIndex("codigo_cliente"));
							encabezado.nombre_cliente = cursor.getString(cursor.getColumnIndex("nombre_cliente"));
							encabezado.numero_doc = cursor.getString(cursor.getColumnIndex("numerodoc"));

							listaPedidos.addElement(encabezado);

						} while (cursor.moveToNext());

					}

					if (cursor != null)
						cursor.close();

				} else {

					Log.e(TAG, "CargarPedidosRealizados: No existe la Base de Datos");
				}

			} catch (Exception e) {

				mensaje = e.getMessage();
				Log.e(TAG, "CargarPedidosRealizados: " + mensaje, e);

			} finally {

				if (db != null)
					db.close();
			}

			return listaPedidos;

		}

	}

	public static Hashtable<String, Detalle> CargarDetallePedido(String numDoc) {

		mensaje = "";
		SQLiteDatabase db = null;

		Detalle detalle;
		Hashtable<String, Detalle> listaDetalle = new Hashtable<String, Detalle>();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = " SELECT d.cod_producto AS codigo_producto, d.precio as Precio, d.iva AS iva, d.descuento as desc, cantidad as Cantidad, "
					+ " p.nombre as descripcion, p.cod_linea as linea,0 as saldo, lote as lote, fecha_fabricacion as fecha_fabricacion , fecha_vencimiento as fecha_vencimiento "
					+ "FROM detalle_venta d "
					+ " inner join productos p on p.cod_producto = d.cod_producto WHERE documento = '" + numDoc + "'";

			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				do {

					detalle = new Detalle();
					detalle.codigo_cliente = Main.cliente.codigo;
					detalle.codigo_producto = cursor.getString(cursor.getColumnIndex("codigo_producto"));
					detalle.desc_producto = cursor.getString(cursor.getColumnIndex("descripcion"));
					detalle.precio = cursor.getFloat(cursor.getColumnIndex("Precio"));
					detalle.iva = cursor.getFloat(cursor.getColumnIndex("iva"));
					detalle.descuento_autorizado = cursor.getFloat(cursor.getColumnIndex("desc"));
					detalle.cantidad = cursor.getFloat(cursor.getColumnIndex("Cantidad"));
					detalle.tipo_pedido = 2;
					detalle.codLinea = cursor.getString(cursor.getColumnIndex("linea"));
					detalle.codMotivo = "";
					detalle.cantidadInv = cursor.getInt(cursor.getColumnIndex("saldo"));
					detalle.cantidadInv = cursor.getInt(cursor.getColumnIndex("saldo"));
					detalle.lote = cursor.getString(cursor.getColumnIndex("lote"));
					detalle.fechaFabricacion = cursor.getString(cursor.getColumnIndex("fecha_fabricacion"));
					detalle.fechaVencimiento = cursor.getString(cursor.getColumnIndex("fecha_vencimiento"));

					detalle.iva = detalle.iva * 100;

					listaDetalle.put("" + detalle.tipo_pedido + detalle.codigo_producto + "", detalle);

				} while (cursor.moveToNext());

				mensaje = "Cargo Pedidos Realizados Correctamente";

			} else {

				mensaje = "Consulta sin resultados";
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}

		return listaDetalle;
	}

	/**
	 * Metodo para insertar un producto pedido por un cliente. se guarda con un
	 * numero documento temporal. esto con el fin de cancelar en pedido en
	 * cualquier momento.
	 * 
	 * @param producto
	 * @throws SQLException
	 */
	public static void agregarProductoDetalleAv2(Producto producto, String canal) throws SQLException {

		SQLiteDatabase db = null;
		SQLiteDatabase tmp = null;

		// se inserta en la base de datos.
		long rowDb = -2;
		long rowTmp = -4;

		try {
			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			File dbTmp = new File(Util.DirApp(), "Temp.db");
			tmp = SQLiteDatabase.openDatabase(dbTmp.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			// iniciar transacciones para garantizar la correcta insercion en
			// las base de datos.
			db.beginTransaction();
			tmp.beginTransaction();

			String lote = "";
			String fecha_fabricacion = "";
			String fecha_vencimiento = "";

			/*
			 * captura de los datos que seran insertados en las bases de datos.
			 */
			ContentValues values = new ContentValues();
			values = new ContentValues();

			values.put("documento", Const.NUM_TEMP); // CONSTANTE TEMPORAL,
														// LUEGO SERA
														// ACTUALIZADA CUANDO SE
														// GUARDE EL PEDIDO DE
														// FORMA DEFINITIVA.
			values.put("cod_producto", producto.codigo);
			values.put("precio", producto.precio);
			values.put("iva", (producto.iva) / 100);
			values.put("descuento", producto.descuento);
			values.put("cantidad", producto.cantidadPedida);
			values.put("cod_motivo", producto.codMotivo);
			values.put("cod_canal_venta", canal);
			values.put("lote", producto.lote);
			values.put("fecha_vencimiento", producto.fechaVencimiento);
			values.put("fecha_fabricacion", producto.fechaFabricacion);
			values.put("validado", "0");

			rowDb = db.insertOrThrow("detalle_venta", null, values);
			rowTmp = tmp.insertOrThrow("detalle_venta", null, values);

			// verificar que no haya ocurrido error en la insercion
			if (rowDb == -1 || rowTmp == -1) {
				throw new SQLException(
						"Error Insertando en base de datos!\nPor favor salga de pedidos e intente de nuevo");
			} else {
				/* confirmar transacciones exitosas */
				db.setTransactionSuccessful();
				tmp.setTransactionSuccessful();
			}
		} catch (SQLException e) {
			Log.e("insertando producto en detalle_venta", "error: " + e.getMessage());
		} finally {
			closeDataBase(db);
			closeDataBase(tmp);
		}
	}

	public static boolean guardarEncabezadoCambioAv(String numeroDocPedidoAv, Cliente cliente, Usuario usuario,
			String fechaEntrega, String fechaCobro, String observaciones, int tipoTransaccion, int entregado,
			int factura, int tipoVentaPedido, int tipoDocumento, int estadoPedido, int estadoPago,
			Encabezado encabezadoPedido) {

		SQLiteDatabase db = null;
		SQLiteDatabase tmp = null;
		boolean insertado = false;

		// se define variables para contar inserciones
		long rowDb = -2;
		long rowTmp = -4;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			File dbTmp = new File(Util.DirApp(), "Temp.db");
			tmp = SQLiteDatabase.openDatabase(dbTmp.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			// iniciar transacciones para garantizar la correcta insercion en
			// las base de datos.
			db.beginTransaction();
			tmp.beginTransaction();

			// consulta para generar el id.
			String count = "SELECT DATETIME('now', 'localtime') AS fechaSistema;";
			String fechaSistema = "";

			// ejecutar consulta
			Cursor cursor = db.rawQuery(count, null);

			/* recuperacion del numero obtenido */
			if (cursor.moveToFirst()) {
				fechaSistema = cursor.getString(cursor.getColumnIndex("fechaSistema"));
			}

			// cerrar cursor.
			if (cursor != null) {
				cursor.close();
			}

			/*
			 * captura de los datos que seran insertados en las bases de datos.
			 */
			ContentValues values = new ContentValues();
			values = new ContentValues();

			values.put("cod_cliente", cliente.codigo);
			values.put("documento", numeroDocPedidoAv);
			values.put("cod_usuario", usuario.codigoVendedor);
			values.put("cod_tipo_trans", tipoTransaccion);
			values.put("valor", encabezadoPedido.valor_neto);
			values.put("descuento", encabezadoPedido.valor_descuento);
			values.put("iva", encabezadoPedido.total_iva);
			values.put("observacion", encabezadoPedido.observacion);
			values.put("orden_compra", "");
			values.put("resolucion", encabezadoPedido.resolucion);
			values.put("consecutivo", encabezadoPedido.consecutivoResolucion);
			values.put("fecha_movil", encabezadoPedido.hora_inicial);
			values.put("fecha_entrega", encabezadoPedido.hora_inicial);
			values.put("cod_canal_venta", usuario.canalVenta);
			values.put("cod_est_pedido", 1);
			values.put("sync", 0);

			// se hace la atualizacion del producto pedido. en ambas bases de
			// datos.
			rowDb = db.insertOrThrow("encabezado_venta", null, values);
			rowTmp = tmp.insertOrThrow("encabezado_venta", null, values);

			// verificar que la actualizacion fue correcta en ambas bases de
			// datos. y confirmar la transaccion como exitosa.
			if (rowDb != -1 && rowTmp != -1) {

				// actualizar el detalle
				boolean actualizado = actualizarDetallePedidoAv(numeroDocPedidoAv, db, tmp);
				insertado = true;
				db.setTransactionSuccessful();
				tmp.setTransactionSuccessful();
			} else {
				throw new Exception("No se logro insertar datos de encabezado " + numeroDocPedidoAv);
			}
		} catch (Exception e) {
			Log.e("No se logro insertar datos de encabezado", "error: " + e.getMessage());
		} finally {
			closeDataBase(db);
			closeDataBase(tmp);
		}
		return insertado;
	}

	public static Venta getValoresVentas() {

		SQLiteDatabase db = null;
		String sql = "";

		float subTotal, desc, iva, neto, ret;
		Venta venta = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				if (Const.tipoAplicacion == Const.AUTOVENTA) {

					sql = "select ifnull( sum( d.precio * d.cantidad ), 0 ) as subTotal, ifnull( sum( valor_retencion + valor_retencion_ica ), 0 ) as totalRetencion, "
							+ "ifnull( sum( ( ( d.precio * d.descuento )  ) * d.cantidad ), 0 ) as descuento, "
							+ " ifnull( sum( ( ( ( d.precio - ( ( d.precio * d.descuento ) / 100 ) ) * d.iva ) / 100 ) * d.cantidad ), 0 ) as iva "
							+ "from detalle_venta d "
							+ "inner join productos p on d.cod_producto = p.cod_producto AND p.CodigoLista = clientes.ListaPrecio "
							+ "inner join  encabezado_venta on encabezado_venta.documento = d.documento "
							+ "INNER JOIN clientes ON clientes.cod_cliente = encabezado_venta.cod_cliente "
							+ "where encabezado_venta.cod_tipo_trans=2 ";

				}

				if (Const.tipoAplicacion == Const.PREVENTA) {

					sql = "select ifnull( sum( d.precio * d.cantidad ), 0 ) as subTotal, ifnull( sum( valor_retencion + valor_retencion_ica ), 0 ) as totalRetencion, "
							+ " ifnull( sum( ( ( d.precio * d.descuento )  ) * d.cantidad ), 0 ) as descuento, "
							+ " ifnull( sum( ( ( ( d.precio - ( ( d.precio * d.descuento ) / 100 ) ) * d.iva ) / 100 ) * d.cantidad ), 0 ) as iva "
							+ " from detalle_venta d "
							+ " inner join productos p on d.cod_producto = p.cod_producto AND p.CodigoLista = clientes.ListaPrecio "
							+ " inner join  encabezado_venta on encabezado_venta.documento = d.documento "
							+ " INNER JOIN clientes ON clientes.cod_cliente = encabezado_venta.cod_cliente "
							+ " where encabezado_venta.cod_tipo_trans= 1  ";

				}

				Cursor cursor = db.rawQuery(sql, null);

				if (cursor.moveToFirst()) {

					do {

						venta = new Venta();

						subTotal = cursor.getFloat(cursor.getColumnIndex("subTotal"));
						iva = cursor.getFloat(cursor.getColumnIndex("iva"));
						desc = cursor.getFloat(cursor.getColumnIndex("descuento"));
						ret = cursor.getFloat(cursor.getColumnIndex("totalRetencion"));

						iva = iva * 100;

						neto = subTotal + iva - desc;
						venta.subTotal = subTotal;
						venta.descuento = desc;
						venta.iva = iva;
						venta.neto = neto;
						venta.totalRetenciones = ret;

					} while (cursor.moveToNext());
				}

				if (cursor != null)
					cursor.close();
			}
		} catch (Exception e) {

		} finally {

			closeDataBase(db);
		}

		return venta;
	}

	public static boolean guardarSaldosCliente(String codUsuario, String documento, String codSaldo, float saldo,
			int codEstado, String codCliente) {

		SQLiteDatabase tmp = null;
		SQLiteDatabase db = null;
		boolean insertado = false;
		long rowTmp, row;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			File dbTmp = new File(Util.DirApp(), "Temp.db");
			tmp = SQLiteDatabase.openDatabase(dbTmp.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			// iniciar transacciones para garantizar la correcta insercion en
			// las base de datos.
			tmp.beginTransaction();
			db.beginTransaction();

			/*
			 * captura de los datos que seran insertados en las bases de datos.
			 */
			ContentValues values = new ContentValues();
			values.put("cod_saldo", codSaldo);
			values.put("documento", documento);
			values.put("cod_usuario", codUsuario);
			values.put("saldo", Util.Redondear(String.valueOf(saldo), 0));
			values.put("cod_est_saldo", codEstado);
			values.put("cod_cliente", codCliente);

			row = db.insertOrThrow("saldos_cliente", null, values);
			rowTmp = tmp.insertOrThrow("saldos_cliente", null, values);

			// verificar que la actualizacion fue correcta en ambas bases de
			// datos. y confirmar la transaccion como exitosa.
			if (rowTmp != -1 && row != -1) {

				insertado = true;
				tmp.setTransactionSuccessful();
				db.setTransactionSuccessful();

			} else {

			}
		} catch (Exception e) {

		} finally {
			closeDataBase(db);
			closeDataBase(tmp);
		}
		return insertado;
	}

	public static boolean guardarSaldosCanceladosCliente(String codUsuario, String factura, String documento,
			String codSaldo, float saldo, int codEstado) {

		SQLiteDatabase db = null;
		SQLiteDatabase tmp = null;
		boolean insertado = false;
		long rowTmp, row;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			File dbTmp = new File(Util.DirApp(), "Temp.db");
			tmp = SQLiteDatabase.openDatabase(dbTmp.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			// iniciar transacciones para garantizar la correcta insercion en
			// las base de datos.
			tmp.beginTransaction();
			db.beginTransaction();

			/*
			 * captura de los datos que seran insertados en las bases de datos.
			 */
			ContentValues values = new ContentValues();
			values.put("cod_saldo_canc", codSaldo);
			values.put("cod_saldo", codSaldo);
			values.put("cod_usuario", codUsuario);
			values.put("saldo_recibido", Util.Redondear(String.valueOf(saldo), 0));
			values.put("cod_tipo_pago", codEstado);
			values.put("factura", factura);
			values.put("observacion", "Saldo pago automatico");
			values.put("factura", factura);

			row = db.insertOrThrow("saldos_cancelados", null, values);
			row = db.insertOrThrow("saldos_depositados", null, values);
			// row =+ tmp.insertOrThrow("saldos_depositados", null, values);

			rowTmp = tmp.insertOrThrow("saldos_cancelados", null, values);

			// verificar que la actualizacion fue correcta en ambas bases de
			// datos. y confirmar la transaccion como exitosa.
			if (rowTmp != -1 && row != -1) {

				insertado = true;
				tmp.setTransactionSuccessful();
				db.setTransactionSuccessful();

			} else {

			}
		} catch (Exception e) {

		} finally {
			closeDataBase(db);
			closeDataBase(tmp);
		}
		return insertado;
	}

	public static String generarCodigo(String vendedor) {
		return "A" + vendedor + Util.FechaActual("yyyyMMddHHmmssSSS");

	}

	public static boolean guardarSaldosRecaudoClientes(SaldoCancelado saldoCancelado) {

		SQLiteDatabase db = null;
		SQLiteDatabase tmp = null;
		boolean insertado = false;
		long rowTmp, row;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			File dbTmp = new File(Util.DirApp(), "Temp.db");
			tmp = SQLiteDatabase.openDatabase(dbTmp.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			// iniciar transacciones para garantizar la correcta insercion en
			// las base de datos.
			tmp.beginTransaction();
			db.beginTransaction();

			/*
			 * captura de los datos que seran insertados en las bases de datos.
			 */
			saldoCancelado.cod_saldo_canc = generarCodigo(saldoCancelado.cod_usuario);

			ContentValues values = new ContentValues();
			values.put("cod_saldo_canc", saldoCancelado.cod_saldo_canc);
			values.put("cod_saldo", saldoCancelado.cod_saldo);
			values.put("cod_usuario", saldoCancelado.cod_usuario);
			values.put("saldo_recibido", saldoCancelado.saldo_recibido);
			values.put("cod_tipo_pago", saldoCancelado.codEstado);
			values.put("observacion", saldoCancelado.observacion);
			values.put("factura", saldoCancelado.factura);

			row = db.insertOrThrow("saldos_cancelados", null, values);
			// row = db.insertOrThrow("saldos_depositados", null, values);
			rowTmp = tmp.insertOrThrow("saldos_cancelados", null, values);

			// verificar que la actualizacion fue correcta en ambas bases de
			// datos. y confirmar la transaccion como exitosa.
			if (rowTmp != -1 && row != -1) {

				insertado = true;
				tmp.setTransactionSuccessful();
				db.setTransactionSuccessful();

			} else {

			}
		} catch (Exception e) {
			e.getCause();
			e.getMessage();
		} finally {
			closeDataBase(db);
			closeDataBase(tmp);
		}
		return insertado;
	}

	/**
	 * Metodo que permite actualizar el inventario despues de realizar una
	 * venta.
	 * 
	 * @param contexto
	 * @return
	 */
	public static boolean organizarInventario(Context contexto) {

		return true;

		// SQLiteDatabase db = null;
		// String sql = "";
		// boolean bien = false;
		//
		// try {
		//
		// File dbFile = new File(Util.DirApp(), "DataBase.db");
		// db = SQLiteDatabase.openDatabase(dbFile.getPath(), null,
		// SQLiteDatabase.OPEN_READWRITE);
		//
		// sql = "DELETE FROM [inventario];";
		//
		// db.execSQL(sql);
		//
		// /*
		// *Actualizar el inventario disponible para posteriores ventas.
		// */
		// String sqlInsert =
		// "INSERT INTO [inventario] " +
		// "SELECT i.[cod_producto] as codProducto, " +
		// " COALESCE( SUM( i.[cant_inicial] ), 0 ) - " +
		// " COALESCE ( " +
		// " ( " +
		// " SELECT SUM( d.[cantidad] ) " +
		// " FROM encabezado_venta e " +
		// " INNER JOIN detalle_venta d ON d.[documento] = e.documento " +
		// " AND d.cod_producto = i.cod_producto " +
		// " AND (d.[lote] = i.[lote] OR d.[lote] IS NULL) " +
		// " WHERE e.cod_tipo_trans in (1,2,3,4,5) " +
		// " ) " +
		// " ,0) AS cantidad, " +
		// " i.[lote] AS lote, " +
		// " i.[fecha_vencimiento] AS fecha_vencimiento , " +
		// " i.[fecha_fabricacion] AS fecha_fabricacion " +
		// "FROM [inventario_cargado] i " +
		// "GROUP BY i.[cod_producto], i.[lote] " +
		// "ORDER BY i.[cant_inicial];";
		//
		// db.execSQL(sqlInsert);
		// bien = true;
		// } catch (Exception e) {
		// Toast.makeText(contexto, "organizarInventario",
		// Toast.LENGTH_LONG).show();
		// }
		// finally {
		// if (db != null)
		// db.close();
		// }
		// return bien;
	}

	// ***********************
	public static Vector<Departamento> listaDpto() {

		mensaje = "";
		SQLiteDatabase db = null;
		Vector<Departamento> listaDpto = new Vector<Departamento>();
		Departamento departamento;

		try {

			File dbFile = new File(Util.DirApp(), "database.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			Cursor cursor = db.rawQuery("SELECT cod_departamento, Descripcion AS descripcion FROM departamento", null);

			if (cursor.moveToFirst()) {

				do {
					departamento = new Departamento();

					departamento.id = cursor.getInt(cursor.getColumnIndex("cod_departamento"));
					departamento.nombre = cursor.getString(cursor.getColumnIndex("descripcion"));

					listaDpto.addElement(departamento);

				} while (cursor.moveToNext());
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, "listaCiudadCliente -> " + mensaje, e);

		} finally {

			if (db != null)
				db.close();
		}
		return listaDpto;
	}

	public static Vector<String> listaCiudad(Departamento departamento) {

		mensaje = "";
		SQLiteDatabase db = null;
		Vector<String> listaCiudad = new Vector<String>();
		try {

			File dbFile = new File(Util.DirApp(), "database.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
			Cursor cursor = db.rawQuery(
					"SELECT Descripcion AS Descripcion FROM ciudad where cod_departamento = '" + departamento.id + "'",
					null);

			if (cursor.moveToFirst()) {
				do {
					String lista = cursor.getString(cursor.getColumnIndex("Descripcion"));
					listaCiudad.addElement(lista);
				} while (cursor.moveToNext());
			}
			if (cursor != null)
				cursor.close();
		} catch (Exception e) {
			mensaje = e.getMessage();
			Log.e(TAG, "listaCiudadCliente -> " + mensaje, e);
		} finally {
			if (db != null)
				db.close();
		}
		return listaCiudad;
	}

	public static boolean guardarClienteNuevo(ClienteNuevo clienteNuevo) {

		SQLiteDatabase db = null;
		SQLiteDatabase dbTemp = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			File fileTemp = new File(Util.DirApp(), "Temp.db");
			dbTemp = SQLiteDatabase.openDatabase(fileTemp.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String fechaActual = Util.FechaActual("yyyy-MM-dd HH:mm:ss");

			ContentValues values = new ContentValues();
			values.put("cod_cliente", clienteNuevo.codigo);
			values.put("representante_legal", clienteNuevo.nombre);
			values.put("barrio", clienteNuevo.barrio);
			values.put("razon_social", clienteNuevo.razonSocial);
			values.put("nit", clienteNuevo.nit);
			values.put("direccion", clienteNuevo.direccion);
			values.put("cod_ciudad", clienteNuevo.ciudad);
			values.put("telefono", clienteNuevo.telefono);
			values.put("telefono_movil", clienteNuevo.telefonoCel);
			values.put("email", clienteNuevo.email);
			values.put("cod_lista", "");
			values.put("cupo", "");
			values.put("cod_usuario", Main.usuario.codigoVendedor + fechaActual);
			values.put("cod_canal_venta", Main.usuario.canalVenta);

			db.insertOrThrow("clientes_nuevos", null, values);
			dbTemp.insertOrThrow("clientes_nuevos", null, values);

			return true;

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, "guardarClienteNuevo -> " + mensaje, e);
			return false;

		} finally {

			if (db != null)
				db.close();

			if (dbTemp != null)
				dbTemp.close();
		}
	}

	public static Vector<Cartera> listaCartera(String codigoCliente) {

		mensaje = "";
		SQLiteDatabase db = null;

		Cartera cartera = new Cartera();
		Vector<Cartera> listaCartera = new Vector<Cartera>();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = "SELECT documento,saldo,cod_cliente,fecha,fecha_venc,dias from saldos_pendientes where cod_cliente='"
					+ codigoCliente + "'";
			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {
				do {
					cartera = new Cartera();
					// Se valida que el Saldo no Contenga e+
					String saldo = cursor.getString(cursor.getColumnIndex("saldo"));
					if (saldo.contains("e+")) {
						saldo = saldo.replace("e+", "E");
					}
					saldo = Util.QuitarE(saldo);
					cartera.strSaldo = Util.Redondear(saldo, 0);
					cartera.codCliente = cursor.getString(cursor.getColumnIndex("cod_cliente"));
					cartera.documento = cursor.getString(cursor.getColumnIndex("documento"));
					cartera.saldo = Util.ToFloat(Util.Redondear(saldo, 0));
					cartera.fecha = cursor.getString(cursor.getColumnIndex("fecha"));
					cartera.FechaVecto = cursor.getString(cursor.getColumnIndex("fecha_venc"));
					cartera.dias = cursor.getInt(cursor.getColumnIndex("dias"));
					listaCartera.addElement(cartera);
				} while (cursor.moveToNext());
			}
			if (cursor != null)
				cursor.close();
		} catch (Exception e) {
			mensaje = e.getMessage();
			Log.e(TAG, "listaCartera - > " + mensaje, e);
		} finally {
			if (db != null)
				db.close();
		}
		return listaCartera;
	}

	public static boolean editarSaldoPendinte(String codSaldo, float saldo, int opcion) {
		SQLiteDatabase db = null;

		try {
			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			if (opcion == 0) {
				ContentValues valores = new ContentValues();
				valores.put("saldo", saldo);
				valores.put("estado", 1);

				db.update("saldos_pendientes", valores, "cod_saldo = '" + codSaldo + "'", null);
			} else {
				ContentValues valores = new ContentValues();
				valores.put("saldo", saldo);

				db.update("saldos_pendientes", valores, "cod_saldo = '" + codSaldo + "'", null);
			}

			return true;

		} catch (Exception e) {
			e.getMessage();
		} finally {
			closeDataBase(db);
		}

		return false;
	}

	public static boolean guardarSaldosPendientesCliente(String codUsuario, String documento, String codSaldo,
			float saldo, int codEstado, String prefijo, long consecutivo) {

		SQLiteDatabase db = null;
		boolean insertado = false;
		long rowTmp;
		String fecha = Util.FechaActual("yyyy-MM-dd HH:mm:ss");

		try {

			File dbPedido = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbPedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			// iniciar transacciones para garantizar la correcta insercion en
			// las base de datos.
			db.beginTransaction();

			/*
			 * captura de los datos que seran insertados en las bases de datos.
			 */
			ContentValues values = new ContentValues();
			values.put("cod_saldo", codSaldo);
			values.put("documento", documento);
			values.put("cod_usuario", codUsuario);
			values.put("saldo", Util.Redondear(String.valueOf(saldo), 0));
			values.put("valor", Util.Redondear(String.valueOf(saldo), 0));
			values.put("cod_est_saldo", codEstado);
			values.put("cod_cliente", Main.cliente.codigo);
			values.put("fecha", fecha);
			values.put("fecha_venc", fecha);
			values.put("dias", "0");
			values.put("estado", 0);
			values.put("prefijo", prefijo);
			values.put("consecutivo", consecutivo);

			rowTmp = db.insertOrThrow("saldos_pendientes", null, values);

			// verificar que la actualizacion fue correcta en ambas bases de
			// datos. y confirmar la transaccion como exitosa.
			if (rowTmp != -1) {

				insertado = true;
				db.setTransactionSuccessful();

			} else {

			}
		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			closeDataBase(db);
		}
		return insertado;
	}

	/**
	 * Metodo que permite borrar un producto que ha sido pedido. y se desea
	 * quitar de la lista de detalle de pedido.
	 * 
	 * @param producto
	 * @by JICZ
	 */
	public static boolean quitarProductoProductoPedidoAv(String codProducto) {
		SQLiteDatabase db = null;
		SQLiteDatabase tmp = null;
		boolean borrado = false;

		try {
			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			File dbTmp = new File(Util.DirApp(), "Temp.db");
			tmp = SQLiteDatabase.openDatabase(dbTmp.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			// iniciar transacciones para garantizar la correcta insercion en
			// las base de datos.
			db.beginTransaction();
			tmp.beginTransaction();

			// se hace el borrado del producto pedidos. en ambas bases de datos.
			long dbDel = db.delete("detalle_venta", "cod_producto = '" + codProducto + "' AND documento = 'NT'", null);
			long tmpDel = tmp.delete("detalle_venta", "cod_producto = '" + codProducto + "' AND documento = 'NT'",
					null);

			// verificar que el borrado fue correcto en ambas bases de datos. y
			// confirmar la transaccion como exitosa.
			if (dbDel == tmpDel) {
				db.setTransactionSuccessful();
				tmp.setTransactionSuccessful();
				borrado = true;
			} else {
				throw new Exception("No se logró borrar datos de producto " + codProducto);
			}
		} catch (Exception e) {
			Log.e("Eliminando producto en Detalle_pedido_av", "error: " + e.getMessage());
		}

		finally {
			closeDataBase(db);
			closeDataBase(tmp);
		}

		return borrado;
	}

	public static Vector<Convencion> TipoConvenciones(Vector<String> items) {
		mensaje = "";
		Convencion convencion;

		SQLiteDatabase db = null;
		Vector<Convencion> listadoConvenciones = new Vector<Convencion>();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			Cursor cursor = db.rawQuery("Select codigo,descripcion from convenciones", null);

			if (cursor.moveToFirst()) {
				do {
					convencion = new Convencion();
					convencion.codigo = cursor.getString(cursor.getColumnIndex("codigo"));
					convencion.descripcion = cursor.getString(cursor.getColumnIndex("descripcion"));
					items.addElement(convencion.descripcion);
					listadoConvenciones.addElement(convencion);

				} while (cursor.moveToNext());

				mensaje = "Sectores Cargados Correctamente";

			} else {

				mensaje = "Consulta sin Resultados";
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e("ListaSector", mensaje, e);

		} finally {

			if (db != null)
				db.close();
		}
		return listadoConvenciones;
	}

	public static Vector<String> ListasPrecios() {

		mensaje = "";
		SQLiteDatabase db = null;
		Vector<String> listadoListasPrecios = new Vector<String>();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String descripcion;
			Cursor cursor = db.rawQuery("SELECT DISTINCT cod_lista FROM Lista", null);

			if (cursor.moveToFirst()) {

				do {

					descripcion = cursor.getString(cursor.getColumnIndex("cod_lista"));
					listadoListasPrecios.addElement(descripcion);

				} while (cursor.moveToNext());
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}

		return listadoListasPrecios;
	}

	public static Vector<Producto> BuscarProductos2(int opBusq, String opBusqueda) {

		Producto producto;
		SQLiteDatabase db = null;

		ItemListView itemListView;
		Vector<Producto> listaProductos = new Vector<Producto>();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = "";

			if (opBusq == 1) {

				query = "select cod_producto,nombre,precio,iva,descuento,cod_linea from productos where cod_producto like '%"
						+ opBusqueda + "%' ";

			} else {

				if (opBusq == 2) {

					query = "select cod_producto,nombre,precio,iva,descuento,cod_linea from productos where  nombre like '%"
							+ opBusqueda + "%' ";

				} else {

					if (opBusq == 3) {

						query = " select cod_producto,nombre,precio,iva,descuento,cod_linea from productos ";

					} else {

					}

				}

			}

			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				do {

					producto = new Producto();
					itemListView = new ItemListView();

					producto.codigo = cursor.getString(cursor.getColumnIndex("cod_producto"));
					producto.descripcion = cursor.getString(cursor.getColumnIndex("nombre"));
					producto.precio = cursor.getFloat(cursor.getColumnIndex("precio"));
					producto.iva = cursor.getFloat(cursor.getColumnIndex("iva"));
					producto.descuento = (float) 0.0;
					producto.codLinea = cursor.getString(cursor.getColumnIndex("cod_linea"));

					if (Const.tipoAplicacion == Const.PREVENTA) {

					} else {

						if (Const.tipoAplicacion == Const.AUTOVENTA) {

							producto.cantidadInv = getInventarioProd(db, producto);

						}

					}

					itemListView.titulo = producto.codigo + " - " + producto.descripcion;
					itemListView.subTitulo = "Precio: $" + producto.precio + " - Iva: " + producto.iva;

					listaProductos.addElement(producto);

				} while (cursor.moveToNext());
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e("BuscarProductos", mensaje, e);

		} finally {

			if (db != null)
				db.close();
		}

		return listaProductos;
	}

	public static Vector<InformeInventario> CargarProductos() {

		SQLiteDatabase db = null;
		InformeInventario informeInv;
		Vector<InformeInventario> listaInformeInv = new Vector<InformeInventario>();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				String query =

						" select  " + "  i.cod_producto as codigo, " + "  p.nombre as nombre  "
								+ "  from inventario i   "
								+ "  inner join productos p on p.cod_producto = i.cod_producto  "
								+ "  order by  CAST(i.cod_producto as integer)  ";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					do {

						informeInv = new InformeInventario();
						informeInv.codigo = cursor.getString(cursor.getColumnIndex("codigo"));
						informeInv.nombre = cursor.getString(cursor.getColumnIndex("nombre"));
						listaInformeInv.addElement(informeInv);

					} while (cursor.moveToNext());
				}

				if (cursor != null)
					cursor.close();

			} else {

			}

		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}

		return listaInformeInv;
	}

	public static Vector<Kardex> CargarKardex(String codProducto) {

		SQLiteDatabase db = null;
		Kardex kardex;
		Vector<Kardex> listaInformeDeKardex = new Vector<Kardex>();
		Cursor cursor;
		String query = "";

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				if (Const.tipoAplicacion == Const.PREVENTA) {

					query = " select e.documento as  documento,d.cantidad,e.fecha_movil from encabezado_venta e  inner join detalle_venta d  "
							+ " on d.documento = e.documento  " + " and   e.cod_tipo_trans = '1' "
							+ " and   d.cod_producto  = '" + codProducto + "' " + " order by documento";

				}

				if (Const.tipoAplicacion == Const.AUTOVENTA) {

					query = " select e.documento as  documento,d.cantidad,e.fecha_movil from encabezado_venta e  inner join detalle_venta d  "
							+ " on d.documento = e.documento  " + " and   e.cod_tipo_trans = '2' "
							+ " and   d.cod_producto  = '" + codProducto + "' " + " order by documento";

				}

				cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					do {

						kardex = new Kardex();
						kardex.fecha = cursor.getString(cursor.getColumnIndex("fecha_movil"));
						kardex.fecha = kardex.fecha.substring(0, 10);
						kardex.tipoDocumento = "V";
						kardex.numeroDocumento = cursor.getString(cursor.getColumnIndex("documento"));
						kardex.cantidad = cursor.getFloat(cursor.getColumnIndex("cantidad"));
						// kardex.tipoReg =
						// cursor.getInt(cursor.getColumnIndex("tipo"));
						listaInformeDeKardex.add(kardex);

					} while (cursor.moveToNext());
				}

				if (Const.tipoAplicacion == Const.PREVENTA) {

					query = " select e.documento as  documento,d.cantidad,e.fecha_movil from encabezado_venta e  inner join detalle_venta d  "
							+ " on d.documento = e.documento  " + " and   e.cod_tipo_trans = '4' "
							+ " and   d.cod_producto  = '" + codProducto + "' " + " order by documento";

				}

				if (Const.tipoAplicacion == Const.AUTOVENTA) {

					query = " select e.documento as  documento,d.cantidad,e.fecha_movil from encabezado_venta e  inner join detalle_venta d  "
							+ " on d.documento = e.documento  " + " and   e.cod_tipo_trans = '5' "
							+ " and   d.cod_producto  = '" + codProducto + "' " + " order by documento";

				}

				cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					do {

						kardex = new Kardex();
						kardex.fecha = cursor.getString(cursor.getColumnIndex("fecha_movil"));
						kardex.fecha = kardex.fecha.substring(0, 10);
						kardex.tipoDocumento = "C";
						kardex.numeroDocumento = cursor.getString(cursor.getColumnIndex("documento"));
						kardex.cantidad = cursor.getFloat(cursor.getColumnIndex("cantidad"));
						// kardex.tipoReg =
						// cursor.getInt(cursor.getColumnIndex("tipo"));
						listaInformeDeKardex.add(kardex);

					} while (cursor.moveToNext());
				}

				if (cursor != null)
					cursor.close();

			} else {

			}

		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}

		return listaInformeDeKardex;
	}

	/**
	 * genera el informe con los datos necesarios para imprimir la tirilla de
	 * venta. Los precios con ajustes a dos decimales.
	 * 
	 * @param numeroDoc
	 * @param detallesImprimir
	 * @param listaDetalleProductos
	 * @param original
	 * @param fechaVenta
	 */
	public static String cargarDetallesImprimir(String numeroDoc, DetalleImprimir detalleImprimir,
			ArrayList<DetalleProducto> listaDetalleProductos, ControlImpresion control, String listaPrecio) {

		SQLiteDatabase db = null;

		Encabezado encabezado = new Encabezado();
		encabezado.valor_neto = 0.001f;
		String numeroFactura = "";
		BigDecimal big = new BigDecimal(0); // objeto usado para ajuste de
											// decimales.

		// garantizar que parametros no esten nulos
		if (detalleImprimir == null) {
			detalleImprimir = new DetalleImprimir();
		}

		if (listaDetalleProductos == null) {
			listaDetalleProductos = new ArrayList<DetalleProducto>();
		}

		if (control == null) {
			control = new ControlImpresion();
		}

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

				String query = "" + "SELECT DISTINCT " + " 		d.[descuento] as descuentoIn,"
						+ " 		e.[copia] as copia," + " 		SUBSTR(e.[fecha_movil],0,21) as fechaFactura,"
						+ " 		r.[prefijo] || e.[consecutivo] AS factura," + "		s.[subtotal] AS subTotal,  "
						+ "       e.[iva] AS iva, " + "		e.[descuento] AS descuento,"
						+ "		(s.[subtotal] - e.[descuento]) AS base,"
						+ "       COALESCE(e.[valor_retencion],0) AS retencion, "
						+ "       COALESCE(e.[valor_retencion_ica],0) AS retencionIca, " + "       e.[valor] AS total, "
						+ "	d.[cod_producto] AS codigoProducto," + "	p.[nombre] AS nombre, "
						+ "	SUM(d.[cantidad]) AS cantidad, " + "	SUM((d.[precio] * d.[iva]) * d.[cantidad]) AS diva,"
						+ "	SUM((d.[precio] * e.[retencion_fuente]) * d.[cantidad]) AS retefuente,"
						+ "	d.[precio] AS unitario, " + "	SUM(d.[precio] * d.[cantidad]) AS totalUnitario, "
						+ "	SUM(round((d.[precio] * (1 + d.[iva] + e.[retencion_fuente])),2) * d.[cantidad]) AS vtotal "
						+ "FROM [encabezado_venta] e, "
						+ "(SELECT SUM(precio * cantidad) AS subtotal FROM [detalle_venta] WHERE [documento] = '"
						+ numeroDoc.trim() + "') s "
						+ "INNER JOIN [resoluciones] r ON r.[cod_resolucion] = e.[resolucion] "
						+ "INNER JOIN [detalle_venta] d ON e.[documento] = d.[documento] "
						+ "INNER JOIN [productos] p ON p.[cod_producto] = d.[cod_producto] AND p.CodigoLista ='"
						+ listaPrecio + "' " + "WHERE e.[documento] = '" + numeroDoc.trim()
						+ "' GROUP BY d.[cod_producto];";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					do {
						// Setiar el encabezado a imprimir.
						if (encabezado.valor_neto == 0.001f) {

							int verificarCopia = cursor.getInt(cursor.getColumnIndex("copia"));
							if (verificarCopia > 0) {
								control.original = false;
							}

							/* Obtener la fecha de creacion de la factura. */
							control.fechaVenta = cursor.getString(cursor.getColumnIndex("fechaFactura"));

							/* Obtener el numero de la factura */
							numeroFactura = cursor.getString(cursor.getColumnIndex("factura"));

							/*
							 * IMPORTANTE: No borrar los puntos al final, son
							 * usados para dar formato a la tirilla de
							 * impresion.
							 */
							big = new BigDecimal(cursor.getFloat(cursor.getColumnIndex("subTotal")));
							encabezado.str_sub_total = Util
									.SepararMiles(String.valueOf(big.setScale(2, BigDecimal.ROUND_HALF_UP)))
									+ "                             .";

							big = new BigDecimal(cursor.getFloat(cursor.getColumnIndex("iva")));
							encabezado.str_total_iva = Util
									.SepararMiles(String.valueOf(big.setScale(2, BigDecimal.ROUND_HALF_UP)))
									+ "                             .";

							big = new BigDecimal(cursor.getFloat(cursor.getColumnIndex("descuento")));
							encabezado.str_descuento = Util
									.SepararMiles(String.valueOf(big.setScale(2, BigDecimal.ROUND_HALF_UP)))
									+ "                             .";

							big = new BigDecimal(cursor.getFloat(cursor.getColumnIndex("base")));
							encabezado.str_base = Util
									.SepararMiles(String.valueOf(big.setScale(2, BigDecimal.ROUND_HALF_UP)))
									+ "                                  .";

							big = new BigDecimal(cursor.getFloat(cursor.getColumnIndex("retencion")));
							encabezado.str_total_retefuente = Util
									.SepararMiles(String.valueOf(big.setScale(2, BigDecimal.ROUND_HALF_UP)))
									+ "                      .";

							big = new BigDecimal(cursor.getFloat(cursor.getColumnIndex("retencionIca")));
							encabezado.str_total_reteIca = Util
									.SepararMiles(String.valueOf(big.setScale(2, BigDecimal.ROUND_HALF_UP)))
									+ "                         .";

							encabezado.valor_neto = cursor.getFloat(cursor.getColumnIndex("total"));
							big = new BigDecimal(encabezado.valor_neto);
							encabezado.str_valor_neto = Util
									.SepararMiles(String.valueOf(big.setScale(2, BigDecimal.ROUND_HALF_UP)))
									+ "                            .";

							detalleImprimir.setEncabezado(encabezado);
						}

						DetalleProducto producto = new DetalleProducto();

						float valorDescuento = cursor.getFloat(cursor.getColumnIndex("descuentoIn"));

						float valorUnitario = cursor.getFloat(cursor.getColumnIndex("unitario"));

						float descuentoTotal = valorUnitario * valorDescuento;

						descuentoTotal = valorUnitario - descuentoTotal;

						float totalUni = cursor.getInt(cursor.getColumnIndex("cantidad")) * valorUnitario;

						/*
						 * IMPORTANTE: No borrar los puntos al final, son usados
						 * para dar forma a la tirilla de impresion
						 */
						producto.codigo = cursor.getString(cursor.getColumnIndex("codigoProducto"))
								+ "                         .";
						producto.nombre = cursor.getString(cursor.getColumnIndex("nombre"))
								+ "                                 .";
						producto.cantidad = " " + cursor.getString(cursor.getColumnIndex("cantidad"))
								+ "                       .";
						big = new BigDecimal(cursor.getDouble(cursor.getColumnIndex("vtotal")));
						producto.valor = Util.SepararMiles(String.valueOf(big.setScale(2, BigDecimal.ROUND_HALF_UP)))
								+ "                                             .";
						producto.precioUnitario = Util.SepararMiles(Util.Redondear(String.valueOf(valorUnitario), 0))
								+ "                                           .";
						producto.totalUnitario = Util.SepararMiles(Util.Redondear(String.valueOf(totalUni), 0))
								+ "                         .";

						listaDetalleProductos.add(producto);

					} while (cursor.moveToNext());
				}

				if (cursor != null)
					cursor.close();
			}
			// agregar los productos para imprimir.
			detalleImprimir.setListaDetalleImprimir(listaDetalleProductos);
		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}
		return numeroFactura;
	}

	/**
	 * Se obtiene el valor total del inventario que fue vendido
	 *
	 * @return
	 */
	public static int buscarValorInventario() {

		int retefuente = 0;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

				String query = "select  sum (ins.cantidad * pro.precio) as total from inventario as ins INNER JOIN productos as pro on ins.cod_producto = pro.cod_producto";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					do {
						retefuente = cursor.getInt(cursor.getColumnIndex("total"));
					} while (cursor.moveToNext());
				}

				if (cursor != null)
					cursor.close();
			}
		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}
		return retefuente;
	}



	/**
	 * Se obtiene el valor total del inventario que fue sugerido para la cartera carro
	 *
	 * @return
	 */
	public static int buscarValorCargue() {

		int retefuente = 0;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

				String query = "select  sum (ins.cantidad * pro.precio) as total from ReadInventario_alistamiento as ins INNER JOIN productos_alistamiento as pro on ins.cod_producto = pro.cod_producto";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					do {
						retefuente = cursor.getInt(cursor.getColumnIndex("total"));
					} while (cursor.moveToNext());
				}

				if (cursor != null)
					cursor.close();
			}
		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}
		return retefuente;
	}



	/**
	 * Se obtiene el valor total del inventario que fue sugerido para la cartera carro
	 *
	 * @return
	 */
	public static int buscarValorConsignacionCancelada() {

		int retefuente = 0;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

				String query = "select sum (saldo_recibido) as total from saldos_cancelados";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					do {
						retefuente = cursor.getInt(cursor.getColumnIndex("total"));
					} while (cursor.moveToNext());
				}

				if (cursor != null)
					cursor.close();
			}
		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}
		return retefuente;
	}


	/**
	 * Se obtiene el valor total del inventario que fue sugerido para la cartera carro
	 *
	 * @return
	 */
	public static int buscarValorCambios() {

		int retefuente = 0;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

				String query = "select sum (valor) as total from encabezado_venta where cod_tipo_trans = '5'";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					do {
						retefuente = cursor.getInt(cursor.getColumnIndex("total"));
					} while (cursor.moveToNext());
				}

				if (cursor != null)
					cursor.close();
			}
		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}
		return retefuente;
	}

	/**
	 * Se obtiene el valor total del inventario que fue sugerido para la cartera carro
	 *
	 * @return
	 */
	public static int buscarValorDescuentos() {

		int retefuente = 0;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

				String query = "select sum (descuento) as total from encabezado_venta ";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					do {
						retefuente = cursor.getInt(cursor.getColumnIndex("total"));
					} while (cursor.moveToNext());
				}

				if (cursor != null)
					cursor.close();
			}
		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}
		return retefuente;
	}

	/**
	 * Se obtiene el valor total del inventario que fue sugerido para la cartera carro
	 *
	 * @return
	 */
	public static int buscarValorReteica() {

		int retefuente = 0;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

				String query = "select sum (valor_retencion_ica) as total from encabezado_venta";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					do {
						retefuente = cursor.getInt(cursor.getColumnIndex("total"));
					} while (cursor.moveToNext());
				}

				if (cursor != null)
					cursor.close();
			}
		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}
		return retefuente;
	}



	/**
	 * Se obtiene el valor total del inventario que fue sugerido para la cartera carro
	 *
	 * @return
	 */
	public static int buscarValorRetefuente() {

		int retefuente = 0;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

				String query = "select sum (valor_retencion) as total from encabezado_venta";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					do {
						retefuente = cursor.getInt(cursor.getColumnIndex("total"));
					} while (cursor.moveToNext());
				}

				if (cursor != null)
					cursor.close();
			}
		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}
		return retefuente;
	}


	/**
	 * Se obtiene el porcentaje de retencion en la fuente que sera aplicado a la
	 * venta.
	 * 
	 * @return
	 */
	public static float obtenerPorcentajeRetefuente() {

		float retefuente = 0.0f;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

				String query = "SELECT DISTINCT r.[valor] AS retefuente FROM [retencion_fuente] r";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					do {
						retefuente = cursor.getFloat(cursor.getColumnIndex("retefuente"));
					} while (cursor.moveToNext());
				}

				if (cursor != null)
					cursor.close();
			}
		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}
		return retefuente;
	}

	/**
	 * Obtiene la resolucion de la DIAN permitida para generar facturas.
	 * 
	 * @param resolucion
	 */
	public static void obtenerDatosResolucion(Resolucion resolucion, String prefijo) {

		SQLiteDatabase db = null;

		if (resolucion == null) {
			resolucion = new Resolucion();
		}
		/* cargar el vendedor actual */
		Usuario usuario = DataBaseBO.CargarUsuario();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

				String query = "" + "SELECT count(1) AS existe, " + "       r.[cod_resolucion] AS codigoResolucion, "
						+ "       r.[fecha_inicio] AS  fechaResolucion, "
						+ "       r.[fecha_vigencia] AS fechaVigencia, " + "        r.[limite_inf]   AS numeroInicial, "
						+ "         r.[limite_sup]  AS numeroFinal " + "FROM [resoluciones] r "
						+ "WHERE r.[activo] = '1' AND r.[cod_usuario] = '" + usuario.codigoVendedor + "' LIMIT 1;";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					do {
						int existe = cursor.getInt(cursor.getColumnIndex("existe"));
						if (existe > 0) {
							resolucion.resolucion = cursor.getString(cursor.getColumnIndex("codigoResolucion"));
							resolucion.fechaResolucion = cursor.getString(cursor.getColumnIndex("fechaResolucion"))
									+ "           .";
							resolucion.fechaVigencia = cursor.getString(cursor.getColumnIndex("fechaVigencia"))
									+ "           .";
							resolucion.numeroInicial = prefijo
									+ cursor.getString(cursor.getColumnIndex("numeroInicial"));
							resolucion.numeroFinal = prefijo + cursor.getString(cursor.getColumnIndex("numeroFinal"));
						} else {
							resolucion.resolucion = "0";
							resolucion.fechaResolucion = "0";
							resolucion.fechaVigencia = "0";
							resolucion.numeroInicial = "0";
							resolucion.numeroFinal = "0";
						}
					} while (cursor.moveToNext());
				}
				if (cursor != null)
					cursor.close();
			}
		} catch (Exception e) {
			mensaje = e.getMessage();
		} finally {
			if (db != null)
				db.close();
		}
	}

	/**
	 * Metodo que devuleve el numero de factua disponible.
	 * 
	 * @param codigoVendedor
	 * @return
	 */
	public static String obtenerNumeroFactura(String codigoVendedor) {

		SQLiteDatabase db = null;
		String numeroFactura = "";

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

				String query = "" + "SELECT COUNT(1) AS existe, "
						+ "       CAST(r.[cons_actual] AS NUMERIC) AS numeroFactura " + "FROM [resoluciones] r "
						+ "WHERE r.[activo] = '1' " + "      AND r.[cod_usuario] = '" + codigoVendedor + "' "
						+ "      AND strftime('%Y%m%d', 'now') <= strftime('%Y%m%d', r.[fecha_vigencia])  "
						+ "      AND strftime('%Y%m%d', 'now') >= strftime('%Y%m%d', r.[fecha_inicio])  LIMIT 1;";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {
					do {
						int existe = cursor.getInt(cursor.getColumnIndex("existe"));
						if (existe > 0) {
							numeroFactura = cursor.getString(cursor.getColumnIndex("numeroFactura"));
						} else {
							/* verificar vigencia de la resolucion */
							numeroFactura = null;
						}
					} while (cursor.moveToNext());
				}
				if (cursor != null)
					cursor.close();
			}
		} catch (Exception e) {
			mensaje = e.getMessage();
		} finally {
			closeDataBase(db);
		}
		return numeroFactura;
	}

	/**
	 * 
	 * @param codigoVendedor
	 * @param numeroFacturaVerificar
	 * @return 0 = Se supero el limite de numeros de factura disponibles en la
	 *         resolucion.<br>
	 *         1 = numero de factura correcto.<br>
	 *         2 = el numero es igual al anterior, no se incremento el
	 *         consecutivo.<br>
	 *         3 = se han perdido numero de facturas. Los consecutivos presentan
	 *         un salto mayor a uno en el conteo.
	 */
	public static int verificarNumeroFactura(String codigoVendedor, int numeroFacturaVerificar, String numeroAnterior) {

		int validado = 0;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

				String query = "" + "SELECT " + numeroFacturaVerificar + " >= CAST(r.[limite_inf] AS NUMERIC) AND "
						+ numeroFacturaVerificar + " <= CAST(r.[limite_sup] AS NUMERIC) AS validoLimite, "
						+ numeroFacturaVerificar + " - " + numeroAnterior + " AS validoConsecutivo "
						+ "FROM [resoluciones] r " + "WHERE r.[activo] = '1' AND r.[cod_usuario] = '" + codigoVendedor
						+ "' AND r.[prefijo] ='" + getPrefijo() + "';";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst())
					do {
						int validadoLimite = cursor.getInt(cursor.getColumnIndex("validoLimite"));
						int validadoConsecutivo = cursor.getInt(cursor.getColumnIndex("validoConsecutivo"));

						if (validadoLimite == 1) {
							if (validadoConsecutivo >= 1) {
								validado = Const.FACTURA_OK;
							}
							/* No se incremento consecutivo. */
							else if (validadoConsecutivo == 0) {
								validado = Const.FACTURA_REPETIDA;
							} else {
								/*
								 * Error en el seguimiento de los consecutivos,
								 * el incremento con respecto al consecutivo
								 * anterior es mayor a 1, se han perdido numero
								 * de facturas.
								 */
								validado = Const.FACTURA_CONTEO_FALLO;
							}
						} else {
							/* se ha superado el limite de facturas */
							validado = Const.FACTURA_EXCEDIO_LIMITE;
						}
					} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			mensaje = e.getMessage();
			Log.e("Verificar Numero Factura ", e.getMessage());
		} finally {
			closeDataBase(db);
		}
		return validado;
	}

	public static int cantidadCambios(String producto) {

		int validado = 0;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

				String query = "select sum([detalle_venta].[cantidad]) as cantidad from [encabezado_venta] "
						+ " inner join [detalle_venta] on [detalle_venta].[documento] = [encabezado_venta].documento "
						+ " where cod_tipo_trans=5 and detalle_venta.cod_producto='" + producto.replace(" ", "") + "' "
						+ " group by detalle_venta.cod_producto ";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst())
					do {

						validado = cursor.getInt(cursor.getColumnIndex("cantidad"));

					} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			mensaje = e.getMessage();
			Log.e("cantidadCambios", e.getMessage());
		} finally {
			closeDataBase(db);
		}
		return validado;
	}

	public static int cantidadVentas(String producto) {

		int validado = 0;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

				String query = "select sum([detalle_venta].[cantidad]) as cantidad from [encabezado_venta] "
						+ " inner join [detalle_venta] on [detalle_venta].[documento] = [encabezado_venta].documento "
						+ " where cod_tipo_trans=2 and detalle_venta.cod_producto='" + producto.replace(" ", "") + "' "
						+ " group by detalle_venta.cod_producto ";

				Log.i("cantidadCambios", query);

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst())
					do {

						validado = cursor.getInt(cursor.getColumnIndex("cantidad"));

					} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			mensaje = e.getMessage();
			Log.e("cantidadCambios", e.getMessage());
		} finally {
			closeDataBase(db);
		}
		return validado;
	}

	/**
	 * Trigger para generar el consecutivo usado en facturacion.
	 * 
	 * @param codigoVendedor
	 */
	public static void crearTriggerFacturacion(String codigoVendedor) {
		/*
		 * SQLiteDatabase db = null;
		 * 
		 * try {
		 * 
		 * File dbFile = new File(Util.DirApp(), "DataBase.db");
		 * 
		 * if (dbFile.exists()){
		 * 
		 * db = SQLiteDatabase.openDatabase(dbFile.getPath(), null,
		 * SQLiteDatabase.OPEN_READWRITE); String trigger = "" +
		 * "CREATE TRIGGER IF NOT EXISTS [consecutivo] " +
		 * "AFTER UPDATE OF [cons_actual] " + "ON [resoluciones] " + "BEGIN " +
		 * "     UPDATE OR ROLLBACK [resoluciones] " +
		 * "     SET [cons_actual] = ( " +
		 * "         SELECT r.[cons_actual] + 1 FROM [resoluciones] r " +
		 * "         WHERE r.[activo] = '1' AND r.[cod_usuario] = '" +
		 * codigoVendedor + "' " + "     ) " +
		 * "     WHERE [activo] = '1'  AND [cod_usuario] = '" + codigoVendedor +
		 * "';  " + "END;";
		 * 
		 * 
		 * Log.e("crearTriggerFacturacion Trigger [resoluciones]", trigger);
		 * 
		 * db.execSQL(trigger); } } catch (Exception e) { mensaje =
		 * e.getMessage(); Log.e("Trigger [resoluciones]", e.getMessage()); }
		 */
	}

	/**
	 * Hacer llamado al trigger que incrementa el consecutivo de la facturacion.
	 * <br>
	 * ver {@link #crearTriggerFacturacion(String)}
	 */
	public static void incrementarNumeroFactura(String codigoUsuario) {

		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
				String update = "" + "UPDATE [resoluciones] SET [cons_actual] = ( "
						+ "SELECT r.[cons_actual] + 1 FROM [resoluciones] r "
						+ "WHERE r.[activo] = '1' AND r.[cod_usuario] = '" + codigoUsuario + "');";

				Log.e("incrementarNumeroFactura [resoluciones]", update);

				db.execSQL(update);
			}
		} catch (Exception e) {
			mensaje = e.getMessage();
			Log.e("Trigger [resoluciones]", e.getMessage());
		} finally {
			closeDataBase(db);
		}
	}

	/**
	 * Permite verificar si el vendedor no ha cargado el inventario sobrante a
	 * la base de datos. No se puede iniciar un nuevo dia de labores sin antes
	 * hacer el cargue de inventario.
	 * 
	 * @return 0 = cargue de inventario sincronizado.<br>
	 *         1 = las fechas de movil y servidor no coinciden.<br>
	 *         2 = el cargue de inventario no esta sincronizado.
	 */
	public static int verificarCargueInventario() {

		SQLiteDatabase db = null;
		int verificar = Integer.MIN_VALUE;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

				String query = "" + "SELECT " + "      CASE WHEN "
						+ "           CAST(REPLACE(date('now'),'-','') AS NUMERIC) > CAST(REPLACE(v.[fechaLabores],'/','') AS NUMERIC) "
						+ "           THEN 1 " + "           ELSE 0 " + "      END AS cargueVencido, "
						+ "      CASE WHEN "
						+ "           CAST(REPLACE(v.[fechaLabores],'/','') AS NUMERIC) > CAST(REPLACE(date('now'),'-','') AS NUMERIC) "
						+ "           THEN 1 " + "           ELSE 0 " + "      END AS fechaDesactualizada "
						+ "FROM [vendedor] v";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {
					do {
						int cargueVencido = cursor.getInt(cursor.getColumnIndex("cargueVencido"));
						int fechaDesactualizada = cursor.getInt(cursor.getColumnIndex("fechaDesactualizada"));

						if (fechaDesactualizada == 1) {
							verificar = 1;
							break;
						} else if (cargueVencido == 1) {
							verificar = 2;
							break;
						} else {
							verificar = 0;
							break;
						}
					} while (cursor.moveToNext());
				}
			}
		} catch (Exception e) {
			mensaje = e.getMessage();
			Log.e("Trigger [resoluciones]", e.getMessage());
		}
		return verificar;
	}

	/**
	 * llenar la lista con los cargues de inventario que quedaron disponibles en
	 * la venta diaria.
	 * 
	 * @param listaCargues
	 */
	public static void obtenerCargueInventario(ArrayList<CargueInventario> listaCargues) {

		SQLiteDatabase db = null;

		if (listaCargues == null) {
			listaCargues = new ArrayList<CargueInventario>();
		}

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

				String query = "" + "SELECT o.[cod_producto] AS codigoProducto, " + "       p.[nombre] AS descripcion, "
						+ "       i.[cantidadInicial] AS cantidadInicial, " + "       o.[cantidad] AS cantidadFinal "
						+ "FROM [inventario] o " + "INNER JOIN ( " + "      SELECT c.[cod_producto] AS codigoProducto, "
						+ "             SUM(c.[cant_inicial]) AS cantidadInicial "
						+ "      FROM [inventario_cargado] c " + "      GROUP BY c.[cod_producto] "
						+ ") i   ON i.[codigoProducto] = o.[cod_producto] " + " "
						+ "INNER JOIN [productos] p ON o.[cod_producto] = p.[cod_producto];";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {
					do {
						CargueInventario cargue = new CargueInventario();
						cargue.codigoProducto = cursor.getString(cursor.getColumnIndex("codigoProducto"));
						cargue.descripcion = cursor.getString(cursor.getColumnIndex("descripcion"));
						cargue.cantidadInicial = cursor.getString(cursor.getColumnIndex("cantidadInicial"));
						cargue.cantidadFinal = cursor.getString(cursor.getColumnIndex("cantidadFinal"));
						listaCargues.add(cargue);
					} while (cursor.moveToNext());
				}
			}
		} catch (Exception e) {
			mensaje = e.getMessage();
			Log.e("Llenar lista cargues ->", e.getMessage());
		}
	}

	/**
	 * Consultar la lista de productos disponibles para el cargue de invetario
	 * sugerido.
	 * 
	 * @param listaItems
	 * @param listaProductos
	 */
	public static boolean cargarProductosParaCargueSugerido(ArrayList<ItemListView> listaItems,
			ArrayList<Producto> listaProductos, String paramBusqueda) {

		SQLiteDatabase db = null;

		if (listaItems == null) {
			listaItems = new ArrayList<ItemListView>();
		}

		if (listaProductos == null) {
			listaProductos = new ArrayList<Producto>();
		}

		/* limpiar las listas */
		listaProductos.clear();
		listaItems.clear();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

				String query = "SELECT p.[cod_producto] As codigo," + "       p.[nombre] As nombre, "
						+ "       p.[precio] AS precio, " + "       p.[LineaProducto] AS linea, "
						+ "       p.[UnidadMedida] AS medida " + "FROM [productos_alistamiento] p "
						+ "WHERE p.[cod_producto] LIKE('%" + paramBusqueda + "%') OR p.[nombre] LIKE('%" + paramBusqueda
						+ "%') " + "ORDER BY CAST(replace(p.[cod_producto],'PF', '') AS INTEGER) ASC  LIMIT 60;";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {
					do {
						ItemListView item = new ItemListView();
						Producto p = new Producto();
						p.codigo = cursor.getString(cursor.getColumnIndex("codigo")).trim();
						p.descripcion = cursor.getString(cursor.getColumnIndex("nombre")).trim();
						p.precio = cursor.getFloat(cursor.getColumnIndex("precio"));
						p.linea = cursor.getString(cursor.getColumnIndex("linea")).trim();
						p.unidadMedida = cursor.getString(cursor.getColumnIndex("medida")).trim();

						String precioString = Util.SepararMiles(String.valueOf(p.precio)).trim();
						item.titulo = p.codigo + " -Desc: " + p.descripcion;
						item.subTitulo = "precio: " + precioString + " -linea: " + p.linea + " -Medida: "
								+ p.unidadMedida;

						/* Agregar a las listas. */
						listaProductos.add(p);
						listaItems.add(item);
					} while (cursor.moveToNext());
				}
			}
		} catch (Exception e) {
			mensaje = e.getMessage();
			Log.e("Llenar lista cargue sugerido ->", e.getMessage());
		} finally {
			closeDataBase(db);
		}
		return !listaProductos.isEmpty();
	}

	/**
	 * Insertar el inventario_alistamiento. Inventario sugerido por el vendedor.
	 * 
	 * @param listaProductosPedidos
	 * @param usuario
	 * @param idMovil
	 * @return
	 */
	public static boolean insertarInventarioSugerido(ArrayList<Producto> listaProductosPedidos, Usuario usuario,
			String idMovil, String fecha) {

		boolean insertado = false;
		SQLiteDatabase db = null;
		SQLiteDatabase temp = null;

		try {
			File dbFile = new File(Util.DirApp(), "DataBase.db");
			File dbFileTemp = new File(Util.DirApp(), "Temp.db");

			if (dbFile.exists() && dbFileTemp.exists()) {
				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
				temp = SQLiteDatabase.openDatabase(dbFileTemp.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				/* Iniciar transaccion para insercion de los datos. */
				db.beginTransaction();
				temp.beginTransaction();

				/* Contenedor de datos a insertar */
				ContentValues values = new ContentValues();

				/* Recorrer productos a insertar. */
				for (Producto producto : listaProductosPedidos) {

					values.put("id_movil", idMovil);
					values.put("cod_canal_venta", usuario.canalVenta);
					values.put("cod_producto", producto.codigo);
					values.put("cod_usuario", usuario.codigoVendedor);
					values.put("fecha_alistamiento", fecha);
					values.put("cantidad", producto.cantidad);

					/* Intentar insertar */
					long rowDb = db.insertOrThrow("inventario_alistamiento", null, values);
					long rowTmp = temp.insertOrThrow("inventario_alistamiento", null, values);

					/* Verificar el correcto insertado */
					if (rowDb == -1 || rowTmp == -1) {
						insertado = false;
						break;
					} else {
						values.clear();
						insertado = true;
					}
				}

				/* Confirmar transaccion */
				if (insertado) {
					db.setTransactionSuccessful();
					temp.setTransactionSuccessful();
				}
			}
		} catch (Exception e) {
			mensaje = e.getMessage();
			Log.e("Insertar inventario sugerido -> ", e.getMessage());
			insertado = false;
		} finally {
			closeDataBase(db);
			closeDataBase(temp);
		}
		return insertado;
	}

	/**
	 * Verificar si ya hay un sugerido del dia.
	 * 
	 * @return
	 */
	public static boolean verificarSugeridoDelDia() {
		SQLiteDatabase db = null;

		boolean sugerido = false;
		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

				String query = "SELECT COUNT(1) AS existe FROM [inventario_alistamiento];";

				Cursor cursor = db.rawQuery(query, null);
				if (cursor.moveToFirst()) {
					do {
						int existe = cursor.getInt(cursor.getColumnIndex("existe"));
						if (existe > 0) {
							sugerido = true;
							break;
						}
						break;
					} while (cursor.moveToNext());
				}

				if (!cursor.isClosed()) {
					cursor.close();
				}
			}
		} catch (Exception e) {
			mensaje = e.getMessage();
			Log.e("verificarSugeridoDelDia ->", e.getMessage());
		} finally {
			closeDataBase(db);
		}
		return sugerido;
	}

	public static boolean verificarDeposito() {
		SQLiteDatabase db = null;

		boolean bandera = false;
		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

				String query = " SELECT count (s.cod_saldo_canc) AS existe, s.cod_saldo AS cod_saldo, s.saldo_recibido AS saldo_recibido, c.cod_cliente AS cod_cliente, cl.razon_social AS razon_social, s.factura as factura "
						+ "FROM saldos_cancelados AS s " + "JOIN saldos_cliente c ON s.cod_saldo = c.cod_saldo "
						+ "JOIN clientes cl ON cl.cod_cliente = c.cod_cliente "
						+ "WHERE (c.cod_cliente like '%%' or cl.razon_social like '%%') "
						+ " AND s.cod_saldo_canc NOT IN (SELECT cod_saldo_canc FROM detalle_deposito_realizados )";

				Cursor cursor = db.rawQuery(query, null);
				if (cursor.moveToFirst()) {
					do {
						int existe = cursor.getInt(cursor.getColumnIndex("existe"));
						if (existe > 0) {
							bandera = true;
							break;
						}
						break;
					} while (cursor.moveToNext());
				}

				if (!cursor.isClosed()) {
					cursor.close();
				}
			}
		} catch (Exception e) {
			mensaje = e.getMessage();
			Log.e("verificarSugeridoDelDia ->", e.getMessage());
		} finally {
			closeDataBase(db);
		}
		return bandera;
	}

	/**
	 * eliminar el inventario sugerido.
	 */
	public static void eliminarInventarioSugerido() {

		SQLiteDatabase db = null;
		SQLiteDatabase temp = null;
		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			File dbFileTemp = new File(Util.DirApp(), "Temp.db");

			if (dbFile.exists() && dbFileTemp.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
				temp = SQLiteDatabase.openDatabase(dbFileTemp.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				String query = "DELETE FROM [inventario_alistamiento];";

				db.execSQL(query);
				temp.execSQL(query);
			}
		} catch (Exception e) {
			mensaje = e.getMessage();
			Log.e("eliminarInventarioSugerido ->", e.getMessage());
		} finally {
			closeDataBase(db);
		}
	}

	/**
	 * Buscar un cliente por el encabezado de una venta.
	 * 
	 * @param numero_doc
	 * @return
	 */
	public static Cliente buscarClientePorEncabezadoVenta(String numero_doc) {

		SQLiteDatabase db = null;
		Cliente cliente = null;
		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

				String query = "SELECT coalesce(c.[nit],'-') AS nit, "
						+ "       substr(coalesce(c.[razon_social],'-'),0,21) AS razon, "
						+ "       substr(coalesce(c.[direccion],'-'),0,27) AS direccion,"
						+ "       substr(coalesce(c.[representante_legal],'-'),0,21) AS nombre, "
						+ "		  c.ListaPrecio as listaPrecio " + "FROM [clientes] c "
						+ "INNER JOIN [encabezado_venta] e ON c.[cod_cliente] = e.[cod_cliente] "
						+ "WHERE e.[documento] = '" + numero_doc + "'";

				Cursor cursor = db.rawQuery(query, null);
				if (cursor.moveToFirst()) {
					do {
						cliente = new Cliente();
						cliente.nit = cursor.getString(cursor.getColumnIndex("nit")).trim();
						cliente.direccion = cursor.getString(cursor.getColumnIndex("direccion")).trim();
						cliente.razonSocial = cursor.getString(cursor.getColumnIndex("razon")).trim();
						cliente.nombre = cursor.getString(cursor.getColumnIndex("nombre")).trim();
						cliente.listaPrecio = cursor.getString(cursor.getColumnIndex("listaPrecio")).trim();
						break;
					} while (cursor.moveToNext());
				}

				if (!cursor.isClosed()) {
					cursor.close();
				}
			}
		} catch (Exception e) {
			mensaje = e.getMessage();
			Log.e("buscarClientePorEncabezadoVenta(String numero_doc) ->", e.getMessage());
		} finally {
			closeDataBase(db);
		}
		return cliente;
	}

	/**
	 * Actualizar el descuento a los detalles del pedido.
	 * 
	 * @param producto
	 * @param cliente
	 */
	public static void aplicarDescuentoDetalle(Producto producto, Cliente cliente) {

		SQLiteDatabase db = null;
		SQLiteDatabase tmp = null;
		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			File dbTemp = new File(Util.DirApp(), "Temp.db");

			if (dbFile.exists() && dbTemp.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
				tmp = SQLiteDatabase.openDatabase(dbTemp.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				String query = "SELECT ROUND(l.[descuento],2) AS descuento, l.[cod_linea] AS linea "
						+ "  FROM [lista] l, "
						+ "    (  SELECT SUM(d.[cantidad]) AS cantidad, d.[cod_linea] AS linea FROM [detalle_venta] d "
						+ "       GROUP BY d.[cod_linea] " + "    ) c "
						+ "  WHERE l.[cod_linea] = c.linea  AND l.[cod_lista] = '" + cliente.codLista + "' "
						+ "  AND (c.cantidad BETWEEN l.[cant_min] AND l.[cant_max]) ";

				System.out.println("QUERY DE DESCUENTOS: " + query);

				Cursor cursor = db.rawQuery(query, null);
				if (cursor.moveToFirst()) {
					do {
						double descuento = cursor.getDouble(cursor.getColumnIndex("descuento"));
						int codigoLinea = cursor.getInt(cursor.getColumnIndex("linea"));

						db.execSQL("UPDATE OR ROLLBACK [detalle_venta] SET [descuento] = round(" + descuento
								+ ",2) , [cod_descuento] = 0  WHERE [cod_linea] = " + codigoLinea + ";");
						tmp.execSQL("UPDATE OR ROLLBACK [detalle_venta] SET [descuento] = round(" + descuento
								+ ",2) , [cod_descuento] = 0   WHERE [cod_linea] = " + codigoLinea + ";");

						/* aplicar el descuento al producto */
						if (producto.codLinea.equals(String.valueOf(codigoLinea))) {
							producto.descuento = (float) descuento;
							System.out.println("APLICO DESCUENTO DE: " + producto.descuento);
						}
					} while (cursor.moveToNext());
				}

				if (!cursor.isClosed()) {
					cursor.close();
				}
			}
		} catch (Exception e) {
			mensaje = e.getMessage();
			Log.e("aplicarDescuentoDetalle  ->", e.getMessage());
		} finally {
			closeDataBase(db);
			closeDataBase(tmp);
		}
	}

	/**
	 * Actualizar el descuento a los detalles del pedido.
	 * 
	 * @param producto
	 * @param cliente
	 */
	public static void aplicarDescuentoProducto(Producto producto, Cliente cliente) {

		SQLiteDatabase db = null;
		SQLiteDatabase tmp = null;
		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			File dbTemp = new File(Util.DirApp(), "Temp.db");

			if (dbFile.exists() && dbTemp.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
				tmp = SQLiteDatabase.openDatabase(dbTemp.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				String query = "SELECT descuento, cod_descuento, observacion, fecha_inicial, fecha_final  FROM descuento_productos WHERE cod_producto='"
						+ producto.codigo + "'";

				System.out.println("QUERY DE DESCUENTOS: " + query);

				Cursor cursor = db.rawQuery(query, null);
				if (cursor.moveToFirst()) {
					do {
						double descuento = cursor.getDouble(cursor.getColumnIndex("descuento"));
						String cod_descuento = cursor.getString(cursor.getColumnIndex("cod_descuento"));

						String observacion = cursor.getString(cursor.getColumnIndex("observacion"));

						String[] fechaInicia = cursor.getString(cursor.getColumnIndex("fecha_inicial")).split(" ");
						String[] fechaFin = cursor.getString(cursor.getColumnIndex("fecha_final")).split(" ");

						if (existePromocionCliente(cliente.codigo, cod_descuento)) {
							db.execSQL("UPDATE OR ROLLBACK [detalle_venta] SET [descuento_producto] = " + descuento
									+ "," + "  [cod_descuento] = '" + cod_descuento + "'  WHERE [cod_producto] = '"
									+ producto.codigo + "'");

							tmp.execSQL("UPDATE OR ROLLBACK [detalle_venta] SET [descuento_producto] = " + descuento
									+ "," + "  [cod_descuento] = '" + cod_descuento + "'  WHERE [cod_producto] = '"
									+ producto.codigo + "'");

							/* aplicar el descuento al producto */
							if (producto.codigo.equals(producto.codigo)) {
								producto.descuentoPorProducto = (float) descuento;
								producto.observacionDescuentoPorProducto = observacion;
								producto.fechaInicialDescuento = fechaInicia[0];
								producto.fechaFinalDescuento = fechaFin[0];
								double descuentoM = descuento * 100;
								producto.valorDescuentoProductoMostrar = (int) descuentoM;
								System.out.println("APLICO DESCUENTO DE: " + producto.descuento);
							}
						}

					} while (cursor.moveToNext());
				}

				if (!cursor.isClosed()) {
					cursor.close();
				}
			}
		} catch (Exception e) {
			mensaje = e.getMessage();
			Log.e("aplicarDescuentoDetalle  ->", e.getMessage());
		} finally {
			closeDataBase(db);
			closeDataBase(tmp);
		}
	}

	/**
	 * El metodo permite marcar el pedido por parametro como copia = 1 indicando
	 * que ya se imprimio su recibo original de venta y en adelante las futuras
	 * impresiones que se hagan seran copia de la original.
	 * 
	 * @param numeroDoc
	 */
	public static void marcarComoCopiaProximaImpresion(String numeroDoc, Usuario usuario) {

		SQLiteDatabase db = null;
		SQLiteDatabase tmp = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			File dbTemp = new File(Util.DirApp(), "Temp.db");

			if (dbFile.exists() && dbTemp.exists()) {

				String update = "UPDATE OR ROLLBACK [encabezado_venta] SET [copia] = 1 WHERE [documento] = '"
						+ numeroDoc + "';";

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
				tmp = SQLiteDatabase.openDatabase(dbTemp.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				/* REALIZAR INSERCION SIN REPETIR DOCUMENTO */
				String insert = "INSERT INTO [copia_impresiones](cod_canal_venta,documento,copia,cod_usuario) "
						+ "SELECT " + usuario.canalVenta + ",'" + numeroDoc + "',1,'" + usuario.codigoVendedor + "' "
						+ "WHERE NOT EXISTS (SELECT DISTINCT 1 FROM [copia_impresiones] WHERE [documento] = '"
						+ numeroDoc + "')";

				db.execSQL(insert);
				tmp.execSQL(insert);

				db.execSQL(update);
				tmp.execSQL(update);
			}
		} catch (Exception e) {
			mensaje = e.getMessage();
			Log.e("aplicarDescuentoDetalle  ->", e.getMessage());
		} finally {
			closeDataBase(db);
			closeDataBase(tmp);
		}
	}

	public static Cliente buscarCliente(Cliente cliente) {

		SQLiteDatabase db = null;
		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

				String query = "Select [cod_lista] from [clientes] where [cod_lista] <> -1 " + "AND [cod_cliente] = '"
						+ cliente.codigo + "' ";

				Cursor cursor = db.rawQuery(query, null);
				if (cursor.moveToFirst()) {
					do {
						cliente = new Cliente();
						cliente.codLista = cursor.getString(cursor.getColumnIndex("cod_lista")).trim();
						break;
					} while (cursor.moveToNext());
				}

				if (!cursor.isClosed()) {
					cursor.close();
				}
			}
		} catch (Exception e) {
			mensaje = e.getMessage();
			Log.e("BUscar Cliente ->", e.getMessage());
		} finally {
			closeDataBase(db);
		}
		return cliente;
	}

	/**
	 * Se obtiene el porcentaje de retencion Ica que sera aplicado a la venta.
	 * 
	 * @return
	 */
	public static float obtenerPorcentajeReteIca() {

		float reteIca = 0.0f;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

				String query = "SELECT DISTINCT r.[valor] AS reteIca FROM [retencion_ica] r";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					do {
						reteIca = cursor.getFloat(cursor.getColumnIndex("reteIca"));
					} while (cursor.moveToNext());
				}

				if (cursor != null)
					cursor.close();
			}
		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}
		return reteIca;
	}

	public static Vector<Inventario> obtenerLotesXCodigo(String codProducto) {

		SQLiteDatabase db = null;
		Inventario lote;
		Vector<Inventario> listaLote = new Vector<Inventario>();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				String query = "SELECT cod_producto, cantidad, lote, fecha_vencimiento, fecha_fabricacion FROM [inventario] "
						+ "WHERE [cod_producto] = '" + codProducto + "' ORDER BY [lote] ASC ";

				// System.out.println("Busqueda de lote viejo por codigo:
				// "+query);

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					do {

						lote = new Inventario();
						lote.codigoProducto = cursor.getString(cursor.getColumnIndex("cod_producto"));
						lote.cantidad = cursor.getInt(cursor.getColumnIndex("cantidad"));
						lote.lote = cursor.getString(cursor.getColumnIndex("lote"));
						lote.fechaFabricacion = cursor.getString(cursor.getColumnIndex("fecha_fabricacion"));
						lote.fechaVencimiento = cursor.getString(cursor.getColumnIndex("fecha_vencimiento"));

						listaLote.addElement(lote);

					} while (cursor.moveToNext());
				}

				if (cursor != null)
					cursor.close();

			} else {

				Log.e(TAG, "CargarListaLotes -> La Base de Datos No Existe");
			}

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, "CargarListaLotes -> " + mensaje, e);

		} finally {

			if (db != null)
				db.close();
		}

		return listaLote;

	}

	public static int obtenerCantTotalLotes(String codProducto) {

		SQLiteDatabase db = null;
		int cantidadTotal = 0;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				String query = "SELECT sum(cantidad) as cantidadTotal FROM [inventario] " + "WHERE [cod_producto] = '"
						+ codProducto + "' ORDER BY [lote] ASC  ";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					do {

						cantidadTotal = Integer.parseInt(cursor.getString(cursor.getColumnIndex("cantidadTotal")));

					} while (cursor.moveToNext());
				}

				if (cursor != null)
					cursor.close();

			} else {

				Log.e(TAG, "CargarTotalInventario-> La Base de Datos No Existe");
			}

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, "CargarListaLotes -> " + mensaje, e);

		} finally {

			if (db != null)
				db.close();
		}

		return cantidadTotal;

	}

	public static boolean restarLote(int cantidadFinal, String lote, String codigo) {

		SQLiteDatabase db = null;
		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			db.execSQL("UPDATE inventario SET cantidad = '" + cantidadFinal + "' WHERE lote = '" + lote
					+ "' AND cod_producto = '" + codigo + "'");
			return true;

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, "ActualizarLoteInventario: " + mensaje, e);
			return false;

		} finally {

			if (db != null)
				db.close();
		}
	}

	public static boolean sumarLote(int cantidadADevolver, String lote, String codigo, String fechaRegistro,
			String fechaVencimiento) {

		SQLiteDatabase db = null;
		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			db.execSQL("UPDATE inventario SET cantidad = (cantidad + '" + cantidadADevolver + "') WHERE cod_producto= '"
					+ codigo + "' ");
			return true;

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, "ActualizarLoteInventario: " + mensaje, e);
			return false;

		} finally {

			if (db != null)
				db.close();
		}
	}

	public static Vector<Producto> obtenerListaDetalleVenta(String codProducto) {

		SQLiteDatabase db = null;
		Producto prodDetalle;
		Vector<Producto> listaDetalle = new Vector<Producto>();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			String and = " ";
			if (!codProducto.equals("-1")) {

				and = "AND cod_producto ='" + codProducto + "'";
			}

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				String query = "SELECT documento, cod_producto, cantidad, lote, fecha_fabricacion, fecha_vencimiento "
						+ "FROM [detalle_venta] where documento = '" + Const.NUM_TEMP + "' " + and + " ";

				// System.out.println("Busqueda de lote viejo por codigo:
				// "+query);

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					do {

						prodDetalle = new Producto();
						prodDetalle.codigo = cursor.getString(cursor.getColumnIndex("cod_producto"));
						prodDetalle.cantidadPedida = cursor.getInt(cursor.getColumnIndex("cantidad"));
						prodDetalle.fechaFabricacion = cursor.getString(cursor.getColumnIndex("fecha_fabricacion"));
						prodDetalle.fechaVencimiento = cursor.getString(cursor.getColumnIndex("fecha_vencimiento"));
						prodDetalle.lote = cursor.getString(cursor.getColumnIndex("lote"));

						listaDetalle.addElement(prodDetalle);

					} while (cursor.moveToNext());
				}

				if (cursor != null)
					cursor.close();

			} else {

				Log.e(TAG, "CargarListaDetalle -> La Base de Datos No Existe");
			}

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, "CargarListaDetalle -> " + mensaje, e);

		} finally {

			if (db != null)
				db.close();
		}

		return listaDetalle;

	}

	/**
	 * Permite cargar la lista de sugeridos aceptados por el usuario.
	 * 
	 * @param listaSugeridos
	 */
	public static ArrayList<InventarioSugerido> cargarReporteSugeridos() {

		SQLiteDatabase db = null;
		ArrayList<InventarioSugerido> listaSugeridos = new ArrayList<InventarioSugerido>();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				/*
				 * String query = "" + "SELECT i.[cod_producto] AS codigo, " +
				 * "       p.[nombre] AS descripcion, " +
				 * "       i.[cantidad] AS cantidadSugerido, " +
				 * "       COAlESCE(c.[cant_final],0) AS cantidadActual " +
				 * "FROM [inventario_alistamiento] i " +
				 * "INNER JOIN [productos2] p ON p.[cod_producto] = i.[cod_producto] "
				 * +
				 * "LEFT JOIN [inventario_cargado] c ON c.[cod_producto] = i.[cod_producto]"
				 * +
				 * "ORDER BY CAST(replace(i.[cod_producto],'PF', '' ) AS INTEGER) ASC ;"
				 * ;
				 */

				String query = "" + "SELECT i.[cod_producto] AS codigo, " + "       p.[nombre] AS descripcion, "
						+ "       i.[cantidad] AS cantidadSugerido" + "       " + "FROM [inventario_alistamiento] i "
						+ "INNER JOIN [productos_alistamiento] p ON p.[cod_producto] = i.[cod_producto] ";
				// + "ORDER BY CAST(replace(i.[cod_producto],'PF', '' ) AS
				// INTEGER) ASC ;";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {
					do {
						InventarioSugerido inventarioSugerido = new InventarioSugerido();
						inventarioSugerido.codigo = cursor.getString(cursor.getColumnIndex("codigo")).trim()
								+ "                   .";
						inventarioSugerido.descripcion = cursor.getString(cursor.getColumnIndex("descripcion")).trim()
								+ "                   .";
						inventarioSugerido.cantidadSugerido = cursor
								.getString(cursor.getColumnIndex("cantidadSugerido")).trim() + "                   .";
						// inventarioSugerido.cantidadActual =
						// cursor.getString(cursor.getColumnIndex("cantidadActual")).trim()
						// + " .";
						listaSugeridos.add(inventarioSugerido);
					} while (cursor.moveToNext());
				}

				if (cursor != null)
					cursor.close();

			} else {
				Log.e(TAG, "cargarReporteSugeridos -> La Base de Datos No Existe");
			}
		} catch (Exception e) {
			mensaje = e.getMessage();
			Log.e(TAG, "cargarReporteSugeridos -> " + mensaje, e);
		} finally {
			if (db != null)
				db.close();
		}
		return listaSugeridos;
	}

	public static Vector<Banco> listaBancos(Vector<String> items) {

		SQLiteDatabase db = null;
		Banco banco;
		Vector<Banco> listaBancos = new Vector<Banco>();

		try {

			if (items == null)
				items = new Vector<String>();

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = "Select -1 AS cod_banco, 'SELECCIONE UN BANCO' AS descripcion FROM bancos " + "UNION "
					+ "Select cod_banco, descripcion FROM bancos";

			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				do {

					banco = new Banco();

					banco.cod_banco = Util.ToInt(cursor.getString(cursor.getColumnIndex("cod_banco")));
					banco.descripcion = cursor.getString(cursor.getColumnIndex("descripcion"));

					listaBancos.addElement(banco);
					items.addElement(banco.descripcion);

				} while (cursor.moveToNext());
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e("ListaMotivosNoCompra", mensaje, e);

		} finally {

			if (db != null)
				db.close();
		}

		return listaBancos;
	}

	public static boolean GuardarCoordenadaCliente(Coordenada coordenada) {

		long rows = 0;
		boolean existe = false;
		SQLiteDatabase dbTemp = null;

		try {

			File filePedido = new File(Util.DirApp(), "Temp.db");

			if (filePedido.exists()) {

				if (coordenada.codigoCliente != null) {

					dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

					String query = "SELECT cod_cliente FROM coordenadas WHERE cod_cliente = '"
							+ coordenada.codigoCliente + "' ";
					Cursor cursor = dbTemp.rawQuery(query, null);

					if (cursor.moveToFirst())
						existe = true;

					if (cursor != null)
						cursor.close();

					if (existe) {

						ContentValues values = new ContentValues();
						values.put("latitud", coordenada.latitud);
						values.put("longitud", coordenada.longitud);
						values.put("cod_novedad", coordenada.id);
						values.put("imei", coordenada.imei);
						values.put("fecha_registro", coordenada.fechaCoordenada);
						values.put("network_provider", coordenada.tipoCaptura);

						rows = dbTemp.update("coordenadas", values, "cod_cliente = ? ",
								new String[] { coordenada.codigoCliente });

					} else {

						ContentValues values = new ContentValues();
						values = new ContentValues();
						values.put("cod_usuario", coordenada.codigoVendedor);
						values.put("cod_cliente", coordenada.codigoCliente);
						values.put("latitud", coordenada.latitud);
						values.put("longitud", coordenada.longitud);
						values.put("cod_novedad", coordenada.id);
						values.put("imei", coordenada.imei);
						values.put("fecha_registro", coordenada.fechaCoordenada);
						values.put("network_provider", coordenada.tipoCaptura);
						values.put("cod_est_coor", Coordenada.ESTADO_GPS_CAPTURO);

						rows = dbTemp.insertOrThrow("coordenadas", null, values);
					}

					return rows > 0;

				} else {

					Log.e(TAG, "GuardarCoordenada -> El codigo del cliente es NULL");
					return false;
				}

			} else {

				Log.e(TAG, "GuardarCoordenada -> ");
				return false;
			}

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, "GuardarCoordenada -> " + mensaje, e);
			return false;

		} finally {

			if (dbTemp != null)
				dbTemp.close();
		}
	}

	/**
	 * Metodos nuevos para el requerimiento de descuentos
	 */

	public static boolean existePromocionCliente(String codigoCliente, String cod_descuento) {

		mensaje = "";
		int total = 0;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = "SELECT COUNT (*) AS total FROM descuento_clientes WHERE cod_cliente='" + codigoCliente
					+ "' AND " + " cod_descuento='" + cod_descuento + "' ";

			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				do {

					total = cursor.getInt(cursor.getColumnIndex("total"));

				} while (cursor.moveToNext());

				mensaje = "Consulta Satisfactoria";
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e("ExistePedidoCliente", mensaje, e);

		} finally {

			if (db != null)
				db.close();
		}

		return total > 0;
	}

	public static Vector<DevolucionesRealizados> CargarInformeDevoluciones(Usuario usuario) {
		SQLiteDatabase db = null;
		Vector<DevolucionesRealizados> listaInformeDevoluciones = new Vector<DevolucionesRealizados>();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				String query = "SELECT  productos.nombre as nombreProducto,detalle_venta.cantidad as cantidad, detalle_venta.precio as valorUnitario,"
						+ "( detalle_venta.precio * detalle_venta.cantidad ) valorTotal" + " FROM    encabezado_venta"
						+ " INNER JOIN detalle_venta ON encabezado_venta.documento = detalle_venta.documento"
						+ " INNER JOIN clientes ON clientes.cod_cliente = encabezado_venta.cod_cliente "
						+ " INNER JOIN productos ON detalle_venta.cod_producto = productos.cod_producto  "
						+ " AND productos.CodigoLista = clientes.ListaPrecio "
						+ " WHERE   encabezado_venta.cod_usuario = " + usuario.codigoVendedor
						+ " AND encabezado_venta.cod_tipo_trans = 5";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					do {

						DevolucionesRealizados devolucionesRealizados = new DevolucionesRealizados();

						devolucionesRealizados.nombreProducto = cursor
								.getString(cursor.getColumnIndex("nombreProducto"));
						devolucionesRealizados.cantidad = cursor.getString(cursor.getColumnIndex("cantidad"));
						devolucionesRealizados.precio = cursor.getString(cursor.getColumnIndex("valorUnitario"));
						devolucionesRealizados.valorTotal = cursor.getString(cursor.getColumnIndex("valorTotal"));

						listaInformeDevoluciones.addElement(devolucionesRealizados);

					} while (cursor.moveToNext());
				}

				if (cursor != null)
					cursor.close();

			} else {

				Log.e(TAG, "CargarInformeDevoluciones-> La Base de Datos No Existe");
			}

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, "CargarInformeDevoluciones -> " + mensaje, e);

		} finally {

			if (db != null)
				db.close();
		}

		return listaInformeDevoluciones;
	}

	public static Hashtable<String, Detalle> CargarDetallePedidoAv(String numDoc, String listaPrecio) {

		mensaje = "";
		SQLiteDatabase db = null;

		Detalle detalle;
		Hashtable<String, Detalle> listaDetalle = new Hashtable<String, Detalle>();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = "SELECT DISTINCT " + "d.[descuento] as descuentoIn, " + "e.[iva] AS iva, "
					+ "d.[cod_producto] AS codigoProducto, " + "p.[nombre] AS nombre, "
					+ "SUM(d.[cantidad]) AS cantidad, " + "d.[precio] AS unitario, " + "p.cod_linea as linea "
					+ "FROM [encabezado_venta] e, "
					+ "(SELECT SUM(precio * cantidad) AS subtotal FROM [detalle_venta] WHERE [documento] ='" + numDoc
					+ "') " + "INNER JOIN [resoluciones] r ON r.[cod_resolucion] = e.[resolucion] "
					+ "INNER JOIN [detalle_venta] d ON e.[documento] = d.[documento] "
					+ "INNER JOIN [productos] p ON p.[cod_producto] = d.[cod_producto] AND p.CodigoLista = '"
					+ listaPrecio + "'  " + "WHERE e.[documento] = '" + numDoc + "' GROUP BY d.[cod_producto] ";

			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				do {

					detalle = new Detalle();
					detalle.codigo_cliente = Main.cliente.codigo;
					detalle.codigo_producto = cursor.getString(cursor.getColumnIndex("codigoProducto"));
					detalle.desc_producto = cursor.getString(cursor.getColumnIndex("nombre"));
					detalle.precio = cursor.getFloat(cursor.getColumnIndex("unitario"));
					detalle.iva = cursor.getFloat(cursor.getColumnIndex("iva"));
					detalle.descuento_autorizado = cursor.getFloat(cursor.getColumnIndex("descuentoIn"));
					detalle.cantidad = cursor.getFloat(cursor.getColumnIndex("cantidad"));
					detalle.codLinea = cursor.getString(cursor.getColumnIndex("linea"));
					detalle.tipo_pedido = 2;

					detalle.iva = detalle.iva * 100;

					listaDetalle.put("" + detalle.tipo_pedido + detalle.codigo_producto + "", detalle);

				} while (cursor.moveToNext());

				mensaje = "Cargo Pedidos Realizados Correctamente";

			} else {

				mensaje = "Consulta sin resultados";
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}

		return listaDetalle;
	}

	public static float obtenerCupoCliente(String codigoCliente) {

		float cupo = 0.0f;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

				String query = "select cupo as cupo from clientes where cod_cliente = '" + codigoCliente + "'";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					cupo = cursor.getFloat(cursor.getColumnIndex("cupo"));

				}

				if (cursor != null)
					cursor.close();
			}
		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}
		return cupo;
	}

	public static float obtenerSaldoPendienteCliente(String codigoCliente) {

		float saldo = 0.0f;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

				String query = "SELECT  sum(s.saldo) as total FROM saldos_pendientes AS s " + " WHERE s.cod_cliente='"
						+ codigoCliente + "'";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					saldo = cursor.getFloat(cursor.getColumnIndex("total"));

				}

				if (cursor != null)
					cursor.close();
			}
		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}
		return saldo;
	}

	public static float obtenerVentaDeVendedor() {

		float saldo = 0.0f;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

				String query = "SELECT ( SUM(valor)  ) as total FROM encabezado_venta where   cod_tipo_trans = '2'";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					saldo = cursor.getFloat(cursor.getColumnIndex("total"));

				}

				if (cursor != null)
					cursor.close();
			}
		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}
		return saldo;
	}

	public static float obtenerSaldosDepositados() {

		mensaje = "";
		SQLiteDatabase db = null;
		float totalDepositos = 0;

		try {
			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {
				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				String query = "SELECT d.documento AS documento, d.cod_saldo_canc AS cod_saldo_canc, "
						+ "SUM(d.saldo) AS saldo, d.cod_est_deposito AS cod_est_deposito, "
						+ "d.cod_cliente AS cod_cliente, c.razon_social AS razon_social, " + "s.factura AS factura "
						+ "FROM detalle_deposito_realizados AS d "
						+ "INNER JOIN Clientes c ON d.cod_cliente = c.cod_cliente "
						+ "INNER JOIN [saldos_depositados] s ON s.[cod_saldo_canc] = d.cod_saldo_canc "
						+ "GROUP BY  documento";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					do {

						totalDepositos = totalDepositos + cursor.getFloat(cursor.getColumnIndex("saldo"));

					} while (cursor.moveToNext());

				}

				if (cursor != null)
					cursor.close();

			} else {
				Log.e(TAG, "CargarDepositosRealizados: No existe la Base de Datos");
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return (long) totalDepositos;

	}

	public static boolean yaTerminoLabores() {

		// return false;

		mensaje = "";
		SQLiteDatabase db = null;

		boolean existe = false;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = "SELECT cod_usuario from FinLabores ";

			Cursor cursor = db.rawQuery(query, null);
			if (cursor.moveToFirst()) {

				existe = true;

			} else {

				existe = false;

			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

		} finally {

			if (db != null)
				db.close();
		}

		return existe;

	}

	public static boolean GuardarTerminoLabores(String usuario, String fechaMovil) {

		SQLiteDatabase db = null;
		SQLiteDatabase dbTemp = null;

		try {
			File dbFile = new File(Util.DirApp(), "DataBase.db");
			File dbFileTemp = new File(Util.DirApp(), "Temp.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
			dbTemp = SQLiteDatabase.openDatabase(dbFileTemp.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
			ContentValues valuesTemp = new ContentValues();

			valuesTemp.put("cod_usuario", usuario);
			valuesTemp.put("fechaMovil", fechaMovil);
			db.insertOrThrow("FinLabores", null, valuesTemp);
			dbTemp.insertOrThrow("FinLabores", null, valuesTemp);
			return true;

		} catch (Exception e) {

			mensaje = e.getMessage();
			return false;

		} finally {

			if (db != null)
				db.close();

			if (dbTemp != null)
				dbTemp.close();

		}
	}

	public static String ObtenerClaveDeAuditor() {

		mensaje = "";
		String clave = "";
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				String query = "select Password as clave from ReadUsuarioAuditoria";
				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					clave = cursor.getString(cursor.getColumnIndex("clave"));

					mensaje = "Cargo Informacion del Usuario Correctamente";
				}

				if (cursor != null)
					cursor.close();

			} else {

				Log.e("DatabaseBO", "ObtenerCodigoDeUsuario -> no existe base de datos ");
			}

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e("DatabaseBO", "ObtenerCodigoDeUsuario -> " + mensaje, e);

		} finally {

			if (db != null)
				db.close();
		}

		return clave;
	}

	/**
	 * Consultar la lista de productos disponibles para el cargue de invetario
	 * sugerido.
	 * 
	 * @param listaItems
	 * @param listaProductos
	 */
	public static boolean cargarProductosParaConteoFisico(ArrayList<ItemListView> listaItems,
			ArrayList<Producto> listaProductos, ArrayList<InformeInventario2> listaInventario, String paramBusqueda) {

		SQLiteDatabase db = null;

		if (listaItems == null) {
			listaItems = new ArrayList<ItemListView>();
		}

		if (listaProductos == null) {
			listaProductos = new ArrayList<Producto>();
		}

		/* limpiar las listas */
		listaProductos.clear();
		listaItems.clear();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

				/*
				 * String query = "SELECT p.[cod_producto] As codigo," +
				 * "       p.[nombre] As nombre, " +
				 * "       p.[precio] AS precio, " +
				 * "       p.[LineaProducto] AS linea, " +
				 * "       p.[UnidadMedida] AS medida " +
				 * "FROM [productos_alistamiento] p " +
				 * "WHERE p.[cod_producto] LIKE('%" + paramBusqueda +
				 * "%') OR p.[nombre] LIKE('%" + paramBusqueda + "%') " +
				 * "ORDER BY CAST(replace(p.[cod_producto],'PF', '') AS INTEGER) ASC  LIMIT 60;"
				 * ;
				 */

				String query = " SELECT p.nombre as nombre, i.cod_producto AS codigo, sum(i.Cantidad) as cantActual "
						+ " FROM    inventario i " + " inner join productos2 p on p.cod_producto = i.cod_producto  "
						+ " GROUP BY i.cod_producto ORDER BY CAST(REPLACE(i.[cod_producto], 'PF', '') AS INTEGER)";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {
					do {

						InformeInventario2 informeInv2;

						informeInv2 = new InformeInventario2();
						informeInv2.codigo = "" + cursor.getString(cursor.getColumnIndex("codigo"));
						informeInv2.nombre = cursor.getString(cursor.getColumnIndex("nombre"));

						// informeInv2.invInicial =
						// cursor.getInt(cursor.getColumnIndex("cantInicial"));
						// informeInv2.strInvInicial =
						// String.valueOf(informeInv2.invInicial) + " .";

						/*
						 * informeInv2.cantVentas =
						 * cantidadVentas(informeInv2.codigo);
						 * informeInv2.strCantVentas =
						 * String.valueOf(informeInv2.cantVentas) +
						 * "                   .";
						 * 
						 * informeInv2.cantDev = 0; informeInv2.strCantDev =
						 * String.valueOf(informeInv2.cantDev) +
						 * "                         .";
						 * 
						 * informeInv2.cantVentaC =
						 * cantidadCambios(informeInv2.codigo);
						 * informeInv2.strCantVentaC =
						 * String.valueOf(informeInv2.cantVentaC) +
						 * "                   .";
						 */

						// informeInv2.invActual =
						// cursor.getInt(cursor.getColumnIndex("cantInicial"))-informeInv2.cantVentas+informeInv2.cantVentaC;

						informeInv2.invActual = cursor.getInt(cursor.getColumnIndex("cantActual"));

						informeInv2.strInvActual = String.valueOf(informeInv2.invActual) + "                     .";

						informeInv2.cantDevC = 0;
						informeInv2.strCantDevC = String.valueOf(informeInv2.cantDevC) + "                       .";

						ItemListView item = new ItemListView();
						Producto p = new Producto();
						p.codigo = informeInv2.codigo;
						p.descripcion = informeInv2.nombre;
						p.inventarioMaquina = informeInv2.invActual;
						p.inventarioConteo = 0;

						item.titulo = p.codigo + " -Desc: " + p.descripcion;
						// item.subTitulo = "Inv Inicial:
						// "+informeInv2.invInicial+" ";
						// item.subTitulo += "Cant.Ventas:
						// "+informeInv2.cantVentas+"\n";
						// item.subTitulo += "Cant.Cambios:
						// "+informeInv2.cantVentaC+"\n";
						item.subTitulo = "Inv.Actual: " + informeInv2.invActual + "\n";
						item.subTitulo += "Inv.Conteo: " + p.inventarioConteo + "\n";
						item.subTitulo += "Diferencia: " + "*";

						item.icono = co.com.panificadoracountry.R.drawable.prodsinmed;

						/* Agregar a las listas. */
						listaProductos.add(p);
						listaInventario.add(informeInv2);
						listaItems.add(item);
					} while (cursor.moveToNext());
				}
			}
		} catch (Exception e) {
			mensaje = e.getMessage();
			Log.e("Llenar lista cargue sugerido ->", e.getMessage());
		} finally {
			closeDataBase(db);
		}
		return !listaProductos.isEmpty();
	}

	public static boolean insertarInventarioConteoFisico(Hashtable<String, Producto> htConteo, Usuario usuario,
			String idMovil) {

		boolean insertado = false;
		SQLiteDatabase db = null;
		SQLiteDatabase temp = null;
		String codAuditor = ObtenerClaveDeAuditor();
		String fechaActual = Util.FechaActual("yyyy-MM-dd HH:mm:ss");

		try {
			File dbFile = new File(Util.DirApp(), "DataBase.db");
			File dbFileTemp = new File(Util.DirApp(), "Temp.db");

			if (dbFile.exists() && dbFileTemp.exists()) {
				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
				temp = SQLiteDatabase.openDatabase(dbFileTemp.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				/* Iniciar transaccion para insercion de los datos. */
				db.beginTransaction();
				temp.beginTransaction();

				/* Contenedor de datos a insertar */
				ContentValues values = new ContentValues();

				/* Recorrer productos a insertar. */

				Enumeration<Producto> enumeracionProductos = htConteo.elements();

				while (enumeracionProductos.hasMoreElements()) {

					Producto producto = enumeracionProductos.nextElement();

					values.put("Numerodoc", "AI" + idMovil);
					values.put("cod_producto", producto.codigo);
					values.put("Inventario_Actual", producto.inventarioMaquina);
					values.put("conteo_inventario", producto.inventarioConteo);
					values.put("cod_usuario", usuario.codigoVendedor);
					values.put("cod_auditor", codAuditor);
					values.put("fechaMovil", fechaActual);

					/* Intentar insertar */
					long rowDb = db.insertOrThrow("AuditoriaInventario", null, values);
					long rowTmp = temp.insertOrThrow("AuditoriaInventario", null, values);

					/* Verificar el correcto insertado */
					if (rowDb == -1 || rowTmp == -1) {
						insertado = false;
						break;
					} else {
						values.clear();
						insertado = true;
					}
				}

				/* Confirmar transaccion */
				if (insertado) {
					db.setTransactionSuccessful();
					temp.setTransactionSuccessful();
				}
			}
		} catch (Exception e) {
			mensaje = e.getMessage();
			Log.e("Insertar inventario sugerido -> ", e.getMessage());
			insertado = false;
		} finally {
			closeDataBase(db);
			closeDataBase(temp);
		}
		return insertado;
	}

	public static boolean existeConteoFisicoInventario() {

		boolean existe = false;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = " select cod_producto  from AuditoriaInventario limit 1";
			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				existe = true;
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}

		return existe;
	}

	public static float obtenerVentaDeVendedorClientesCredito() {

		float saldo = 0.0f;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

				String query = " SELECT ifnull(( SUM(valor)  ),0) as total FROM encabezado_venta where   cod_tipo_trans = '2' "
						+ " and cod_cliente in(select cod_cliente from clientes where cupo>0) ";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					saldo = cursor.getFloat(cursor.getColumnIndex("total"));

				}

				if (cursor != null)
					cursor.close();
			}
		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}
		return saldo;
	}

	public static float obtenerVentaDeVendedorClientesContado() {

		float saldo = 0.0f;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

				String query = " SELECT ifnull(( SUM(valor)  ),0) as total FROM encabezado_venta where   cod_tipo_trans = '2' "
						+ " and cod_cliente in(select cod_cliente from clientes where cupo<=0) ";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					saldo = cursor.getFloat(cursor.getColumnIndex("total"));

				}

				if (cursor != null)
					cursor.close();
			}
		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}
		return saldo;
	}

	public static float obtenerRecaudoVendedorTotal() {

		float saldototal = 0.0f;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

				String query = "select sum(saldos_cancelados.saldo_recibido) as total from saldos_cancelados";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					saldototal = cursor.getFloat(cursor.getColumnIndex("total"));

				}

				if (cursor != null)
					cursor.close();
			}
		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}
		return saldototal;
	}

	public static float obtenerRecaudoVendedor(int tipoPago) {

		float saldo = 0.0f;
		SQLiteDatabase db = null;
		String cod_saldo_canc = generarCodigo1();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

				/*
				 * Muestra los saldos cancelados estan pendientes por pagar
				 */
				String query = "select sum(saldos_cancelados.saldo_recibido) as total,tipo_pago.descripcion "
						+ "from saldos_cancelados "
						+ "inner join tipo_pago on tipo_pago.cod_tipo_pago = saldos_cancelados.cod_tipo_pago "
						+ "where tipo_pago.cod_tipo_pago = " + tipoPago + " and saldos_cancelados.cod_saldo_canc ";
				// + "like '%"+cod_saldo_canc+"%'";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					saldo = cursor.getFloat(cursor.getColumnIndex("total"));

				}

				if (cursor != null)
					cursor.close();
			}
		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}
		return saldo;
	}

	public static float obtenerRecaudoVendedor() {

		float saldo = 0.0f;
		SQLiteDatabase db = null;
		String cod_saldo_canc = generarCodigo1();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

				String query = " select sum(saldos_cancelados.saldo_recibido) as total,tipo_pago.descripcion from saldos_cancelados inner join tipo_pago on tipo_pago.cod_tipo_pago = saldos_cancelados.cod_tipo_pago  and saldos_cancelados.cod_saldo_canc like '%"
						+ cod_saldo_canc + "%'";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					saldo = cursor.getFloat(cursor.getColumnIndex("total"));

				}

				if (cursor != null)
					cursor.close();
			}
		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}
		return saldo;
	}

	public static Vector<InformeInventario2> CargarInformeInventario33() {

		SQLiteDatabase db = null;
		InformeInventario2 informeInv2;
		Vector<InformeInventario2> listaInformeInv = new Vector<InformeInventario2>();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				String query = " SELECT p.nombre as nombre, i.cod_producto AS codigo, sum(i.Cantidad) as cantActual "
						+ " FROM    inventario i " + " inner join productos2 p on p.cod_producto = i.cod_producto  "
						+ " GROUP BY i.cod_producto ORDER BY CAST(REPLACE(i.[cod_producto], 'PF', '') AS INTEGER)";

				System.out.println("Query Inventario: " + query);

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					do {

						informeInv2 = new InformeInventario2();
						informeInv2.codigo = " " + cursor.getString(cursor.getColumnIndex("codigo"));
						informeInv2.nombre = cursor.getString(cursor.getColumnIndex("nombre"));

						// informeInv2.invInicial =
						// cursor.getInt(cursor.getColumnIndex("cantInicial"));
						// informeInv2.strInvInicial =
						// String.valueOf(informeInv2.invInicial) + " .";

						informeInv2.cantVentas = cantidadVentas(informeInv2.codigo);
						informeInv2.strCantVentas = String.valueOf(informeInv2.cantVentas) + "                   .";

						informeInv2.cantDev = 0;
						informeInv2.strCantDev = String.valueOf(informeInv2.cantDev) + "                         .";

						informeInv2.cantVentaC = cantidadCambios(informeInv2.codigo);
						informeInv2.strCantVentaC = String.valueOf(informeInv2.cantVentaC) + "                   .";

						// informeInv2.invActual =
						// cursor.getInt(cursor.getColumnIndex("cantInicial"))-informeInv2.cantVentas+informeInv2.cantVentaC;

						informeInv2.invActual = cursor.getInt(cursor.getColumnIndex("cantActual"));

						informeInv2.strInvActual = String.valueOf(informeInv2.invActual) + "                     .";

						informeInv2.cantDevC = 0;
						informeInv2.strCantDevC = String.valueOf(informeInv2.cantDevC) + "                       .";

						listaInformeInv.addElement(informeInv2);

					} while (cursor.moveToNext());
				}

				if (cursor != null)
					cursor.close();

			} else {

				Log.e(TAG, "CargarInformeInventario -> La Base de Datos No Existe");
			}

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, "CargarInformeInventario -> " + mensaje, e);

		} finally {

			if (db != null)
				db.close();
		}

		return listaInformeInv;
	}

	public static Vector<Producto> BuscarTodosProductosConteoInventario() {

		Producto producto;
		SQLiteDatabase db = null;

		Vector<Producto> listaProductos = new Vector<Producto>();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query;
			query = "select AuditoriaInventario.cod_producto,nombre,inventario_actual,conteo_inventario from AuditoriaInventario\r\n"
					+ "inner join productos2 on productos2.cod_producto =  AuditoriaInventario.cod_producto\r\n"
					+ "ORDER BY CAST(REPLACE(AuditoriaInventario.[cod_producto], 'PF', '') AS INTEGER)";

			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				do {

					producto = new Producto();

					producto.codigo = cursor.getString(cursor.getColumnIndex("cod_producto"));
					producto.descripcion = cursor.getString(cursor.getColumnIndex("nombre"));
					producto.inventarioMaquina = cursor.getInt(cursor.getColumnIndex("Inventario_Actual"));
					producto.inventarioConteo = cursor.getInt(cursor.getColumnIndex("conteo_inventario"));

					listaProductos.addElement(producto);

				} while (cursor.moveToNext());
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}

		return listaProductos;
	}

	/**
	 * cargar la informacion de inventario del producto.
	 * 
	 * @param db
	 * @param producto
	 * @return
	 */
	public static float getInventarioProd2(SQLiteDatabase db, Producto producto) {

		String sql = "";
		float inv = 0;

		try {

			sql = "SELECT i.[cantidadInventario] AS cantidad , " + " 	  i.[lote] AS lote, "
					+ "	  i.[fecha_vencimiento] AS fechaVenc, " + " 	  i.[fecha_fabricacion] AS fechaFab  "
					+ "FROM inventario i " + "WHERE cod_producto = '" + producto.codigo + "' AND i.[cantidad] > 0 "
					+ "ORDER BY [fecha_vencimiento]";

			Cursor cursor = db.rawQuery(sql, null);

			if (cursor.moveToFirst()) {

				do {
					Inventario i = new Inventario();
					i.cantidad = cursor.getInt(cursor.getColumnIndex("cantidad"));
					i.lote = cursor.getString(cursor.getColumnIndex("lote"));
					i.fechaVencimiento = cursor.getString(cursor.getColumnIndex("fechaVenc"));
					i.fechaFabricacion = cursor.getString(cursor.getColumnIndex("fechaFab"));
					/* agregamos las cantidades disponibles al producto. */
					producto.getListaInventario().add(i);
				} while (cursor.moveToNext());
			}

			if (cursor != null)
				cursor.close();
		} catch (Exception e) {

		} finally {

			for (Inventario i : producto.getListaInventario()) {
				inv += i.cantidad;
			}
		}
		return inv;
	}

	public static InformeInventario2 ObtenerInventarioProd(SQLiteDatabase db, String codigo) {

		InformeInventario2 informeInv2 = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				String query = " SELECT p.nombre as nombre, i.cod_producto AS codigo, sum(i.Cantidad) as cantActual "
						+ " FROM    inventario i " + " inner join productos2 p on p.cod_producto = i.cod_producto  "
						+ "  where i.cod_producto = '" + codigo + "' "
						+ " GROUP BY i.cod_producto ORDER BY CAST(REPLACE(i.[cod_producto], 'PF', '') AS INTEGER)";

				System.out.println("Query Inventario: " + query);

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					do {

						informeInv2 = new InformeInventario2();
						informeInv2.codigo = " " + cursor.getString(cursor.getColumnIndex("codigo"));
						informeInv2.nombre = cursor.getString(cursor.getColumnIndex("nombre"));

						// informeInv2.invInicial =
						// cursor.getInt(cursor.getColumnIndex("cantInicial"));
						// informeInv2.strInvInicial =
						// String.valueOf(informeInv2.invInicial) + " .";

						informeInv2.cantVentas = cantidadVentas(informeInv2.codigo);
						informeInv2.strCantVentas = String.valueOf(informeInv2.cantVentas) + "                   .";

						informeInv2.cantDev = 0;
						informeInv2.strCantDev = String.valueOf(informeInv2.cantDev) + "                         .";

						informeInv2.cantVentaC = cantidadCambios(informeInv2.codigo);
						informeInv2.strCantVentaC = String.valueOf(informeInv2.cantVentaC) + "                   .";

						// informeInv2.invActual =
						// cursor.getInt(cursor.getColumnIndex("cantInicial"))-informeInv2.cantVentas+informeInv2.cantVentaC;

						informeInv2.invActual = cursor.getInt(cursor.getColumnIndex("cantActual"));

						informeInv2.strInvActual = String.valueOf(informeInv2.invActual) + "                     .";

						informeInv2.cantDevC = 0;
						informeInv2.strCantDevC = String.valueOf(informeInv2.cantDevC) + "                       .";

					} while (cursor.moveToNext());
				}

				if (cursor != null)
					cursor.close();

			} else {

				Log.e(TAG, "CargarInformeInventario -> La Base de Datos No Existe");
			}

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, "CargarInformeInventario -> " + mensaje, e);

		} finally {

		}

		return informeInv2;
	}

	/*
	 * public static int cantidadCambiosNoSyncronizados(String producto){
	 * 
	 * int validado = 0; SQLiteDatabase db = null;
	 * 
	 * try {
	 * 
	 * File dbFile = new File(Util.DirApp(), "DataBase.db");
	 * 
	 * if (dbFile.exists()){
	 * 
	 * db = SQLiteDatabase.openDatabase(dbFile.getPath(), null,
	 * SQLiteDatabase.OPEN_READONLY);
	 * 
	 * String query =
	 * "select sum([detalle_venta].[cantidad]) as cantidad from [encabezado_venta] "
	 * +
	 * " inner join [detalle_venta] on [detalle_venta].[documento] = [encabezado_venta].documento "
	 * + " where cod_tipo_trans=5 and detalle_venta.cod_producto='"
	 * +producto.replace(" ", "")+"'    and validado<>'1' " +
	 * " group by detalle_venta.cod_producto ";
	 * 
	 * Cursor cursor = db.rawQuery(query, null);
	 * 
	 * if(cursor.moveToFirst()) do {
	 * 
	 * validado = cursor.getInt(cursor.getColumnIndex("cantidad"));
	 * 
	 * } while (cursor.moveToNext()); } } catch (Exception e) { mensaje =
	 * e.getMessage(); Log.e("cantidadCambios", e.getMessage()); }finally {
	 * closeDataBase(db); } return validado; }
	 * 
	 * public static int cantidadVentasNoSyncronizados(String producto){
	 * 
	 * int validado = 0; SQLiteDatabase db = null;
	 * 
	 * try {
	 * 
	 * File dbFile = new File(Util.DirApp(), "DataBase.db");
	 * 
	 * if (dbFile.exists()){
	 * 
	 * db = SQLiteDatabase.openDatabase(dbFile.getPath(), null,
	 * SQLiteDatabase.OPEN_READONLY);
	 * 
	 * String query =
	 * "select sum([detalle_venta].[cantidad]) as cantidad from [encabezado_venta] "
	 * +
	 * " inner join [detalle_venta] on [detalle_venta].[documento] = [encabezado_venta].documento "
	 * + " where cod_tipo_trans=2 and detalle_venta.cod_producto='"
	 * +producto.replace(" ", "")+"'   and validado<>'1'  " +
	 * " group by detalle_venta.cod_producto ";
	 * 
	 * Log.i("cantidadCambios", query);
	 * 
	 * Cursor cursor = db.rawQuery(query, null);
	 * 
	 * if(cursor.moveToFirst()) do {
	 * 
	 * validado = cursor.getInt(cursor.getColumnIndex("cantidad"));
	 * 
	 * } while (cursor.moveToNext()); } } catch (Exception e) { mensaje =
	 * e.getMessage(); Log.e("cantidadCambios", e.getMessage()); }finally {
	 * closeDataBase(db); } return validado; }
	 */

	public static String generarCodigo1() {
		return Main.usuario.codigoVendedor + Util.FechaActual("yyyyMMdd");

	}

	public static long totalDepositos() {

		mensaje = "";
		SQLiteDatabase db = null;
		float totalDepositos = 0;

		try {
			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {
				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				String query = "SELECT d.documento AS documento, d.cod_saldo_canc AS cod_saldo_canc, "
						+ "SUM(d.saldo) AS saldo, d.cod_est_deposito AS cod_est_deposito, "
						+ "d.cod_cliente AS cod_cliente, c.razon_social AS razon_social, " + "s.factura AS factura "
						+ "FROM detalle_deposito_realizados AS d "
						+ "INNER JOIN Clientes c ON d.cod_cliente = c.cod_cliente "
						+ "INNER JOIN [saldos_depositados] s ON s.[cod_saldo_canc] = d.cod_saldo_canc "
						+ "GROUP BY  documento";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					do {

						totalDepositos = totalDepositos + cursor.getFloat(cursor.getColumnIndex("saldo"));

					} while (cursor.moveToNext());

				}

				if (cursor != null)
					cursor.close();

			} else {
				Log.e(TAG, "CargarDepositosRealizados: No existe la Base de Datos");
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return (long) totalDepositos;
	}

	public static boolean existeCodigoVerificacion(String codigoVerificacion, String vendedor) {

		boolean existe = false;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = "select CodigoVerificacion from CodigoVerificacionAnulacion where CodigoVerificacion = '"
					+ codigoVerificacion + "'  and Cod_usuario = '" + vendedor
					+ "'    and CodigoVerificacion not in (select CodigoVerificacion from RegistroCodigoVerificacion where cod_usuario='"
					+ vendedor + "')";
			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				existe = true;
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}

		return existe;
	}

	public static boolean GuardarCodigoAutorizacionIngresado(String documento, String codigoVendedor,
			String codigoVerficacion, String fechaMovil) {

		SQLiteDatabase db = null;
		SQLiteDatabase dbTemp = null;

		try {

			dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			File filePedido = new File(Util.DirApp(), "Temp.db");
			dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			ContentValues values = new ContentValues();
			values = new ContentValues();
			values.put("Documento", documento);//
			values.put("CodigoVendedor", codigoVendedor);//
			values.put("CodigoVerificacion", codigoVerficacion);//
			values.put("FechaMovil", fechaMovil);//
			values.put("cod_usuario", codigoVendedor);//

			db.insertOrThrow("RegistroCodigoVerificacion", null, values);
			dbTemp.insertOrThrow("RegistroCodigoVerificacion", null, values);

			return true;

		} catch (Exception e) {

			mensaje = e.getMessage();
			return false;

		} finally {

			if (db != null)
				db.close();

			if (dbTemp != null)
				dbTemp.close();
		}
	}

	public static boolean existeCertificado(String codCliente) {

		boolean existe = false;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = " SELECT Latitud, Longitud \n" + "FROM CertificarCoordanada \n " + "WHERE cod_cliente = '"
					+ codCliente + "' ";
			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				existe = true;
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}

		return existe;
	}

	
	public static double verLatitud(String codCliente) {

		double existe = 0;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = " select Latitud from CertificarCoordanada where cod_cliente  = '"+ codCliente + "' LIMIT 1 ";
			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				existe = cursor.getDouble(cursor.getColumnIndex("Latitud"));
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}

		return existe;
	}
	

	public static double verLongitud(String codCliente) {

		double existe = 0;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = "select Longitud from CertificarCoordanada where cod_cliente  = '"+ codCliente + "'  LIMIT 1 ";
			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				existe = cursor.getDouble(cursor.getColumnIndex("Longitud"));
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}

		return existe;
	}
	
	
	
	public static boolean existeCertificadoCoordenadas(String codCliente) {

		boolean existe = false;
		SQLiteDatabase db = null;
		String lat = "";
		String lon = "";

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = " SELECT latitud, longitud FROM clientes WHERE cod_cliente = '" + codCliente
					+ "' ";
			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				lat = cursor.getString(cursor.getColumnIndex("latitud"));
				lon = cursor.getString(cursor.getColumnIndex("longitud"));
				
				if (lat.equals(null)&&lon.equals(null)){
					existe = false;
				}else{
					existe = true;
				}
			}

			if (cursor != null)
				cursor.close();

		} catch (Exception e) {

			mensaje = e.getMessage();

		} finally {

			if (db != null)
				db.close();
		}

		return existe;
	}

	public static boolean GuardarCoordenadaCertificadas(Coordenada coordenada) {

		long rows = 0;
		SQLiteDatabase dbTemp = null;
		SQLiteDatabase db = null;

		try {

			File filePedido = new File(Util.DirApp(), "Temp.db");
			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (filePedido.exists()) {

				if (coordenada.codigoCliente != null) {

					dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
					db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

					ContentValues values = new ContentValues();
					values = new ContentValues();
					values.put("Cod_usuario", coordenada.codigoVendedor);
					values.put("cod_cliente", coordenada.codigoCliente);
					values.put("Latitud", coordenada.latitud);
					values.put("Longitud", coordenada.longitud);
					values.put("FechaMovil", coordenada.fechaCoordenada);

					rows = dbTemp.insertOrThrow("CertificarCoordanada", null, values);
					rows = db.insertOrThrow("CertificarCoordanada", null, values);
				}

				return rows > 0;

			} else {

				Log.e(TAG, "GuardarCoordenada -> ");
				return false;
			}

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, "GuardarCoordenada -> " + mensaje, e);
			return false;

		} finally {

			if (dbTemp != null)
				dbTemp.close();
		}

	}

	public static double obtenerDistanciaMaxima() {
		mensaje = "";
		double distMax = Const.DISTANCIA_MAX;
		SQLiteDatabase db = null;
		try {
			File dbFile = new File(Util.DirApp(), "DataBase.db");
			if (dbFile.exists()) {
				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
				String query = "SELECT descripcion FROM Limite ";
				Cursor cursor = db.rawQuery(query, null);
				if (cursor.moveToFirst()) {
					distMax = cursor.getDouble(cursor.getColumnIndex("descripcion"));
				}
				if (cursor != null)
					cursor.close();
			}
		} catch (Exception e) {
			mensaje = e.getMessage();
			Log.e(TAG, "obtenerDistanciaMaxima -> " + mensaje, e);
		} finally {
			if (db != null)
				closeDataBase(db);
		}
		return distMax;
	}


	@SuppressLint("LongLogTag")
	public static boolean guardarCarteraCarro(long inventario, long cargue, long subtotal, long consignacion, long cambios, long descuentos, long reteica, long retefuente, long total, Usuario usuario) {

		//SQLiteDatabase db = null;
		SQLiteDatabase tmp = null;
		boolean insertado = false;

		String fechaMovil = Util.FechaActual("yyyy-MM-dd HH:mm:ss");

		// se define variables para contar inserciones
		long rowDb = -2;
		long rowTmp = -4;

		try {
/*
			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);*/

			File dbTmp = new File(Util.DirApp(), "Temp.db");
			tmp = SQLiteDatabase.openDatabase(dbTmp.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			// iniciar transacciones para garantizar la correcta insercion en
			// las base de datos.
			//db.beginTransaction();
			tmp.beginTransaction();
			/*
			 * captura de los datos que seran insertados en las bases de datos.
			 */
			ContentValues values = new ContentValues();
			values = new ContentValues();

			values.put("Cod_usuario", usuario.codigoVendedor);
			values.put("ValorInventario", inventario);
			values.put("ValorCargue", cargue);
			values.put("SubTotal", subtotal);
			values.put("ValorConsignacion", consignacion);
			values.put("ValorCambio", cambios);
			values.put("ValorDescuento", descuentos);
			values.put("ValorReteica", reteica);
			values.put("ValorRetencion", retefuente);
			values.put("Total", total);
			values.put("FechaMovil", fechaMovil);


			// se hace la atualizacion del producto pedido. en ambas bases de
			// datos.
			//rowDb = db.insertOrThrow("encabezado_venta", null, values);
			rowTmp = tmp.insertOrThrow("CarteraCarro", null, values);

			// verificar que la actualizacion fue correcta en ambas bases de
			// datos. y confirmar la transaccion como exitosa.
			if (rowDb != -1 && rowTmp != -1) {

				// actualizar el detalle
				//boolean actualizado = actualizarDetallePedidoAv(numeroDocPedidoAv, db, tmp);
				insertado = true;
				//db.setTransactionSuccessful();
				tmp.setTransactionSuccessful();
			} else {
				throw new Exception("No se logro insertar datos de encabezado " );
			}
		} catch (Exception e) {
			Log.e("No se logró insertar datos de encabezado", "error: " + e.getMessage());
		} finally {
			//closeDataBase(db);
			closeDataBase(tmp);
		}
		return insertado;
	}

}
