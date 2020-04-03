package co.com.BusinessObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;
import co.com.DataObject.AutoCuadre;
import co.com.DataObject.BotonesMenu;
import co.com.DataObject.Cartera;
import co.com.DataObject.Ciudad;
import co.com.DataObject.Cliente;
import co.com.DataObject.ClienteNuevo;
import co.com.DataObject.Departamento;
import co.com.DataObject.Deposito;
import co.com.DataObject.Detalle;
import co.com.DataObject.DetalleDeposito;
import co.com.DataObject.DetalleRecaudo;
import co.com.DataObject.Encabezado;
import co.com.DataObject.EncabezadoRecaudo;
import co.com.DataObject.EstadisticaRecorrido;
import co.com.DataObject.Impresion;
import co.com.DataObject.ImpresionCliente;
import co.com.DataObject.ImpresionDetalle;
import co.com.DataObject.InformeInventario;
import co.com.DataObject.ItemListView;
import co.com.DataObject.Linea;
import co.com.DataObject.MotivoCambio;
import co.com.DataObject.MotivoCompra;
import co.com.DataObject.ObjectSel;
import co.com.DataObject.Producto;
import co.com.DataObject.SaldoCancelado;
import co.com.DataObject.Usuario;
import co.com.DataObject.Venta;
import co.com.DataObject.TipoPago;
import co.com.panificadoracountry.Const;
import co.com.panificadoracountry.Main;
import co.com.panificadoracountry.Util;

public class DataBaseBOJ {

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

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				String query = " SELECT cod_usuario as codigo, nombre AS nombre, fechaLabores,cod_canal_venta as cod_canal_venta ,0 as pedido_minimo "
						+ " FROM Vendedor ";

				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					usuario = new Usuario();
					usuario.bodega = "";
					usuario.codigoVendedor = cursor.getString(cursor.getColumnIndex("codigo"));
					usuario.nombreVendedor = cursor.getString(cursor.getColumnIndex("nombre"));
					usuario.fechaLabores = cursor.getString(cursor.getColumnIndex("fechaLabores"));
					usuario.canalVenta = cursor.getString(cursor.getColumnIndex("cod_canal_venta"));
					usuario.pedido_minimo = cursor.getInt(cursor.getColumnIndex("pedido_minimo"));
					mensaje = "Cargo Informacion del Usuario Correctamente";
				}

				if (cursor != null)
					cursor.close();

			} else {

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

					if (result.toUpperCase().equals("1")) {

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

	public static void BorrarPedidosSinTerminar() {

		SQLiteDatabase db = null;
		SQLiteDatabase dbPedido = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				File filePedido = new File(Util.DirApp(), "Temp.db");
				dbPedido = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				db.execSQL(
						"DELETE FROM [02Detalle] WHERE numDoc IN (SELECT numeroDoc FROM [02Encabezado] WHERE fechaTrans IS NULL)");
				db.execSQL(
						"DELETE FROM [02NovedadesCompras] WHERE numerodoc IN (SELECT numeroDoc FROM [02Encabezado] WHERE fechaTrans IS NULL)");
				db.execSQL("DELETE FROM [02Encabezado] WHERE fechaTrans IS NULL");

				dbPedido.execSQL(
						"DELETE FROM [02Detalle] WHERE numDoc IN (SELECT numeroDoc FROM [02Encabezado] WHERE fechaTrans IS NULL)");
				dbPedido.execSQL(
						"DELETE FROM [02NovedadesCompras] WHERE numerodoc IN (SELECT numeroDoc FROM [02Encabezado] WHERE fechaTrans IS NULL)");
				dbPedido.execSQL("DELETE FROM [02Encabezado] WHERE fechaTrans IS NULL");

				dbPedido.execSQL("VACUUM");

			} else {

				Log.e(TAG, "BorrarPedidosSinFinalizar: No existe la Base de Datos");
			}

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, "BorrarPedidosSinFinalizar: " + mensaje, e);

		} finally {

			if (db != null)
				db.close();

			if (dbPedido != null)
				dbPedido.close();
		}
	}

	public static void BorrarSugeridoSinTerminar() {

		SQLiteDatabase db = null;
		SQLiteDatabase dbPedido = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				File filePedido = new File(Util.DirApp(), "Temp.db");
				dbPedido = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				db.execSQL(
						"DELETE FROM [02DetalleSugerido] WHERE numDoc IN (SELECT numeroDoc FROM [02Encabezado] WHERE fechaTrans IS NULL)");
				db.execSQL("DELETE FROM [02EncabezadoSugerido] WHERE fechaTrans IS NULL");

				dbPedido.execSQL(
						"DELETE FROM [02DetalleSugerido] WHERE numDoc IN (SELECT numeroDoc FROM [02Encabezado] WHERE fechaTrans IS NULL)");
				dbPedido.execSQL("DELETE FROM [02EncabezadoSugerido] WHERE fechaTrans IS NULL");

				dbPedido.execSQL("VACUUM");

			} else {

				Log.e(TAG, "BorrarPedidosSinFinalizar: No existe la Base de Datos");
			}

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, "BorrarPedidosSinFinalizar: " + mensaje, e);

		} finally {

			if (db != null)
				db.close();

			if (dbPedido != null)
				dbPedido.close();
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

	public static boolean TerminoLabores() {

		int total = 0;
		SQLiteDatabase db = null;

		try {

			dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				String query = "SELECT COUNT(*) AS total FROM NovedadesCompras WHERE fechaTermino IS NOT NULL";
				Cursor cursor = db.rawQuery(query, null);

				if (cursor.moveToFirst()) {

					total = cursor.getInt(cursor.getColumnIndex("total"));
				}

				if (cursor != null)
					cursor.close();

				return total > 0;

			} else {

				Log.e(TAG, "Verificando si Termino Labores: No existe la base de Datos: DataBase.db");
				return false;
			}

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, "Verificando si Termino Labores: " + mensaje, e);
			return false;

		} finally {

			if (db != null)
				db.close();
		}
	}

	public static String ObtenerVersionApp() {

		String version = "";
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = "SELECT version FROM Version";
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

			String query = " select distinct r.cod_cliente  as codigo, c.nit, c.representante_legal as nombre, c.razon_social as razonSocial,"
					+ "direccion as direccion,cod_lista as cod_lista,c.ciudad,c.barrio,c.telefono, c.telefono_movil, "
					+ "c.email, c.retefuente as retefuente, c.reteica as reteica, c.ListaPrecio from clientes as c "
					+ " inner join rutero r on r.cod_cliente = c.cod_cliente " + " where r.cod_dia = " + codDia
					+ " and codigo not in (select distinct cod_cliente from novedades) "
					+ " order by r.orden_visita ASC ";
			
			//, c.ListaPrecio 
			
			System.out.println("Query Cliente :"+query);

			int contador = 1;

			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				do {

					cliente = new Cliente();
					itemListView = new ItemListView();

					cliente.codigo = cursor.getString(cursor.getColumnIndex("codigo")).trim();
					cliente.nit = cursor.getString(cursor.getColumnIndex("nit")).trim();
					cliente.nombre = cursor.getString(cursor.getColumnIndex("nombre")).trim();
					cliente.razonSocial = cursor.getString(cursor.getColumnIndex("razonSocial")).trim();
					cliente.direccion = cursor.getString(cursor.getColumnIndex("direccion")).trim();
					cliente.codLista = cursor.getString(cursor.getColumnIndex("cod_lista")).trim();
					cliente.ciudad = cursor.getString(cursor.getColumnIndex("ciudad")).trim();
					cliente.telefono = cursor.getString(cursor.getColumnIndex("telefono")).trim();
					cliente.telefonoCel = cursor.getString(cursor.getColumnIndex("telefono_movil")).trim();
					cliente.email = cursor.getString(cursor.getColumnIndex("email")).trim();
					cliente.barrio = cursor.getString(cursor.getColumnIndex("barrio")).trim();
					cliente.retefuente = cursor.getInt(cursor.getColumnIndex("retefuente"));
					cliente.reteIca = cursor.getInt(cursor.getColumnIndex("reteica"));
					cliente.listaPrecio = cursor.getString(cursor.getColumnIndex("ListaPrecio"));

					itemListView.titulo = "Orden: " + contador + "  -- " + cliente.codigo + " " + cliente.nombre + "\n"
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
					+ codigoCliente + "'";

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

			String query = " SELECT cod_usuario as codigo, nombre AS nombre, fechaLabores,cod_canal_venta as cod_canal_venta ,0 as pedido_minimo "
					+ " FROM Vendedor ";

			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				usuario = new Usuario();
				usuario.bodega = "";
				usuario.codigoVendedor = cursor.getString(cursor.getColumnIndex("codigo"));
				usuario.nombreVendedor = cursor.getString(cursor.getColumnIndex("nombre"));
				usuario.fechaLabores = cursor.getString(cursor.getColumnIndex("fechaLabores"));
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
	public static boolean cargarProductosDisponiblesAutoventa(List<Producto> listaProductos) {
		SQLiteDatabase db = null;
		boolean finalizado = false;
		try {
			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			/*
			 * Consulta para obtener la lista de productos disponibles para
			 * autoventa
			 */
			String query = "select cod_producto,nombre,precio,iva,descuento,cod_linea from productos";

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

					if (Const.tipoAplicacion == Const.PREVENTA) {

					} else {

						if (Const.tipoAplicacion == Const.AUTOVENTA) {

							producto.cantidadInv = getInventarioProd(db, producto.codigo);

						}

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
	 * Metodo para insertar un producto pedido por un cliente. se guarda con un
	 * numero documento temporal. esto con el fin de cancelar en pedido en
	 * cualquier momento.
	 * 
	 * @param producto
	 * @throws SQLException
	 */
	public static void agregarProductoDetalleAv(Producto producto, String canal) throws SQLException {

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
			values.put("cod_motivo", "");
			values.put("cod_canal_venta", canal);

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
			values.put("observacion", observaciones);
			values.put("orden_compra", ordenCompra);
			values.put("resolucion", encabezadoPedido.resolucion);
			values.put("consecutivo", encabezadoPedido.consecutivoResolucion);
			values.put("fecha_movil", encabezadoPedido.hora_inicial);
			values.put("fecha_entrega", fechaEntrega);
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

			Cursor cursor = db.query("tipo_novedad", new String[] { "cod_tipo_nov", "descripcion" }, "cod_tipo_nov > 5",
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

			String query = "SELECT Codigo, Nombre FROM Lineas";
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

							producto.cantidadInv = getInventarioProd(db, producto.codigo);

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

	public static float getInventarioProd(SQLiteDatabase db, String codProduco) {

		String sql = "";
		float inv = 0;

		try {

			sql = "select cantidad  " + " from inventario  " + " where cod_producto = '" + codProduco + "'";

			Cursor cursor = db.rawQuery(sql, null);

			if (cursor.moveToFirst()) {

				inv = cursor.getFloat(cursor.getColumnIndex("cantidad"));
			}

			if (cursor != null)
				cursor.close();
		} catch (Exception e) {

		} finally {

		}

		return inv;
	}

	public static Vector<Producto> BuscarProductos(boolean porCodigo, String opBusqueda, String codLinea,
			String listaPrecio, Vector<ItemListView> listaItems, boolean pedido) {

		Producto producto;
		SQLiteDatabase db = null;

		ItemListView itemListView;
		Vector<Producto> listaProductos = new Vector<Producto>();

		String where = pedido ? " AND ListaPrecios.Precio > 0" : "";
		// String where = pedido ? " ListaPrecios.Precio > 0" : "";

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query;

			if (porCodigo) {

				query = "select cod_producto,nombre,precio,iva,descuento,cod_linea from productos where cod_producto like '%"
						+ opBusqueda + "%' ";

			} else {

				query = "select cod_producto,nombre,precio,iva,descuento,cod_linea from productos where  nombre like '%"
						+ opBusqueda + "%' ";

			}

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

							producto.cantidadInv = getInventarioProd(db, producto.codigo);

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

						producto.cantidadInv = getInventarioProd(db, producto.codigo);

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
						+ " UNION  select codTipo,porcentaje from descuentos2 where codCliente='" + cliente.codigo
						+ "' and codProducto='" + producto.grupo + "' and codTipo = 8 "
						+ " UNION  select codTipo,porcentaje from descuentos2 where codCliente='" + cliente.codigo
						+ "'  and codTipo = 7 "
						+ " UNION  select codTipo,porcentaje from descuentos2 where codCliente='"
						+ cliente.grupoclientes + "' and codProducto='" + producto.codigo + "' and codTipo = 6 "
						+ " UNION  select codTipo,porcentaje from descuentos2 where codCliente='"
						+ cliente.grupoclientes + "' and codProducto='" + producto.grupo + "' and codTipo = 5 "
						+ " UNION  select codTipo,porcentaje from descuentos2 where codCliente='"
						+ cliente.grupoclientes + "'  and codTipo = 4 "
						+ " UNION  select codTipo,porcentaje from descuentos2 where codProducto='" + producto.codigo
						+ "'   and codTipo = 3 "
						+ " UNION  select codTipo,porcentaje from descuentos2 where codProducto='" + producto.grupo
						+ "'   and codTipo = 2 "
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

			+ " union " + " select d.CodigoRef as codProducto ,sum(d.cantidad) as cantidad from [02Detalle] as d "
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

			sql = "  select prefijo, cod_resolucion, limite_inf, limite_sup, regimen,fecha_vigencia ,razonSocial, nit, direccion, ciudad, msjComprador "
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

			sql = "select p.cod_producto as codigo, p.nombre as nombre, p.precio as precio, d.iva, d.descuento, cantidad as cantidad "
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

	// public static Vector<InformeInventario2> CargarInformeInventario2() {
	//
	// SQLiteDatabase db = null;
	// InformeInventario2 informeInv2;
	// Vector<InformeInventario2> listaInformeInv = new
	// Vector<InformeInventario2>();
	//
	// try {
	//
	// File dbFile = new File(Util.DirApp(), "DataBase.db");
	//
	// if (dbFile.exists()) {
	//
	// db = SQLiteDatabase.openDatabase(dbFile.getPath(), null,
	// SQLiteDatabase.OPEN_READWRITE);
	//
	//
	// String query = "select i.cod_producto as codigo, cantidad as cantInicial,
	// "+
	// " cantidad - "+
	// " ifnull(( select SUM( d.cantidad ) from encabezado_venta e inner join
	// detalle_venta d on d.documento = e.documento and d.cod_producto =
	// i.cod_producto where e.cod_tipo_trans = '2' group by d.cod_producto ),0)
	// +"+
	// " ifnull(( select SUM( d.cantidad ) from encabezado_venta e inner join
	// detalle_venta d on d.documento = e.documento and d.cod_producto =
	// i.cod_producto where e.cod_tipo_trans = '5' group by d.cod_producto ),0)
	// as cantActual, "+
	// " ifnull(( select SUM( d.cantidad ) from encabezado_venta e inner join
	// detalle_venta d on d.documento = e.documento and d.cod_producto =
	// i.cod_producto where e.cod_tipo_trans = '2' group by d.cod_producto ),0)
	// as cantVenta, "+
	// " ifnull(( select SUM( d.cantidad ) from encabezado_venta e inner join
	// detalle_venta d on d.documento = e.documento and d.cod_producto =
	// i.cod_producto where e.cod_tipo_trans = '5' group by d.cod_producto ),0)
	// as cantVentaCambio, "+
	// " ifnull(( select SUM( d.cantidad ) from encabezado_venta e inner join
	// detalle_venta d on d.documento = e.documento and d.cod_producto =
	// i.cod_producto where e.cod_tipo_trans = '5' group by d.cod_producto ),0)
	// as cantDevolucion, "+
	// " ifnull(( select SUM( d.cantidad ) from encabezado_venta e inner join
	// detalle_venta d on d.documento = e.documento and d.cod_producto =
	// i.cod_producto where e.cod_tipo_trans = '5' group by d.cod_producto ),0)
	// as cantDevolucionCambio, "+
	// " p.nombre as nombre "+
	// " from inventario i "+
	// " inner join productos p on p.cod_producto = i.cod_producto "+
	// " order by CAST(i.cod_producto as integer) ";
	//
	//
	// Cursor cursor = db.rawQuery(query, null);
	//
	// if (cursor.moveToFirst()) {
	//
	// do {
	//
	// informeInv2 = new InformeInventario2();
	// informeInv2.codigo = cursor.getString(cursor.getColumnIndex("codigo"));
	// informeInv2.nombre = cursor.getString(cursor.getColumnIndex("nombre"));
	// informeInv2.invActual =
	// cursor.getInt(cursor.getColumnIndex("cantActual"));
	// informeInv2.cantVentas =
	// cursor.getInt(cursor.getColumnIndex("cantVenta")) ;
	// informeInv2.cantDev =
	// cursor.getInt(cursor.getColumnIndex("cantDevolucion")) ;
	// informeInv2.cantVentaC =
	// cursor.getInt(cursor.getColumnIndex("cantVentaCambio")) ;
	// informeInv2.cantDevC =
	// cursor.getInt(cursor.getColumnIndex("cantDevolucionCambio"));
	// informeInv2.invInicial =
	// cursor.getInt(cursor.getColumnIndex("cantInicial"));
	//
	// listaInformeInv.addElement(informeInv2);
	//
	// } while(cursor.moveToNext());
	// }
	//
	// if (cursor != null)
	// cursor.close();
	//
	// } else {
	//
	// Log.e(TAG, "CargarInformeInventario -> La Base de Datos No Existe");
	// }
	//
	// } catch (Exception e) {
	//
	// mensaje = e.getMessage();
	// Log.e(TAG, "CargarInformeInventario -> " + mensaje, e);
	//
	// } finally {
	//
	// if (db != null)
	// db.close();
	// }
	//
	// return listaInformeInv;
	// }

	public static Vector<Cliente> BuscarClientes(String where, Vector<ItemListView> listaItems) {

		mensaje = "";
		Cliente cliente;
		SQLiteDatabase db = null;

		ItemListView itemListView;
		Vector<Cliente> listaClientes = new Vector<Cliente>();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = " Select c.cod_cliente  as codigo, c.nit, c.representante_legal as nombre, c.razon_social as razonSocial,"
					+ "direccion as direccion, cod_lista as cod_lista, c.ciudad as ciudad, ifnull(c.barrio , '' ) as barrio, c.telefono as telefono, ifnull(c.telefono_movil, '') as telefono_movil, ifnull(c.email, '') as email, "
					+ " retefuente as retefuente, c.reteica as reteica, c.ListaPrecio as ListaPrecio, ifnull (c.latitud, 0) as latitud, ifnull(c.longitud, 0) as longitud, c.codigoStatusCliente as estatus, c.codigotipocliente as tipoCliente  "
					+ "from clientes as c " + where; //, c.ListaPrecio

			int contador = 1;

			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				do {

					cliente = new Cliente();
					itemListView = new ItemListView();

					cliente.codigo = cursor.getString(cursor.getColumnIndex("codigo")).trim();
					cliente.nit = cursor.getString(cursor.getColumnIndex("nit")).trim();
					cliente.nombre = cursor.getString(cursor.getColumnIndex("nombre")).trim();
					cliente.razonSocial = cursor.getString(cursor.getColumnIndex("razonSocial")).trim();
					cliente.direccion = cursor.getString(cursor.getColumnIndex("direccion")).trim();
					cliente.codLista = cursor.getString(cursor.getColumnIndex("cod_lista")).trim();
					cliente.ciudad = cursor.getString(cursor.getColumnIndex("ciudad")).trim();
					cliente.telefono = cursor.getString(cursor.getColumnIndex("telefono")).trim();
					cliente.telefonoCel = cursor.getString(cursor.getColumnIndex("telefono_movil")).trim();
					cliente.email = cursor.getString(cursor.getColumnIndex("email")).trim();
					cliente.barrio = cursor.getString(cursor.getColumnIndex("barrio")).trim();
					cliente.retefuente = cursor.getInt(cursor.getColumnIndex("retefuente"));
					cliente.reteIca = cursor.getInt(cursor.getColumnIndex("reteica"));
					cliente.listaPrecio = cursor.getString(cursor.getColumnIndex("ListaPrecio"));
					cliente.latitud = cursor.getDouble(cursor.getColumnIndex("latitud"));
					cliente.longitud = cursor.getDouble(cursor.getColumnIndex("longitud"));
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
	
	public static Cliente getCliente(String codigoCliente) {
		
		Cliente cliente;
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = " Select c.cod_cliente  as codigo, c.nit, c.representante_legal as nombre, c.razon_social as razonSocial,"
					+ "direccion as direccion,cod_lista as cod_lista,c.ciudad,c.barrio,c.telefono, c.telefono_movil, c.email, retefuente as retefuente, ifnull (c.latitud, 0) as latitud, ifnull(c.longitud) as longitud  "
					+ "from clientes as c where c.cod_cliente = '"+codigoCliente+"'";


			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				do {

					cliente = new Cliente();
					cliente.codigo = cursor.getString(cursor.getColumnIndex("codigo")).trim();
					cliente.nit = cursor.getString(cursor.getColumnIndex("nit")).trim();
					cliente.nombre = cursor.getString(cursor.getColumnIndex("nombre")).trim();
					cliente.razonSocial = cursor.getString(cursor.getColumnIndex("razonSocial")).trim();
					cliente.direccion = cursor.getString(cursor.getColumnIndex("direccion")).trim();
					cliente.codLista = cursor.getString(cursor.getColumnIndex("cod_lista")).trim();
					cliente.ciudad = cursor.getString(cursor.getColumnIndex("ciudad")).trim();
					cliente.telefono = cursor.getString(cursor.getColumnIndex("telefono")).trim();
					cliente.telefonoCel = cursor.getString(cursor.getColumnIndex("telefono_movil")).trim();
					cliente.email = cursor.getString(cursor.getColumnIndex("email")).trim();
					cliente.barrio = cursor.getString(cursor.getColumnIndex("barrio")).trim();
					cliente.retefuente = cursor.getInt(cursor.getColumnIndex("retefuente"));
					cliente.latitud = cursor.getDouble(cursor.getColumnIndex("latitud"));
					cliente.longitud = cursor.getDouble(cursor.getColumnIndex("longitud"));

					return cliente;
					
				} while (cursor.moveToNext());

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

		return null;
	}
	
    public static Vector<TipoPago> TipoPago() {
		
		
		mensaje = "";
		SQLiteDatabase db = null;

		TipoPago tipoPago = new TipoPago();
		Vector<TipoPago> listaTipoPago = new Vector<TipoPago>();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = "SELECT cod_tipo_pago, descripcion FROM tipo_pago";
			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {
				do {
					tipoPago = new TipoPago();
					tipoPago.cod_pago = cursor.getString(cursor.getColumnIndex("cod_tipo_pago"));
					tipoPago.descripcion = cursor.getString(cursor.getColumnIndex("descripcion"));
					listaTipoPago.addElement(tipoPago);
				} while (cursor.moveToNext());
			}
			if (cursor != null)
				cursor.close();
		} catch (Exception e) {
			mensaje = e.getMessage();
			Log.e(TAG, "listaTipoPago - > " + mensaje, e);
		} finally {
			if (db != null)
				db.close();
		}
		return listaTipoPago;
	}
    
    public static float [] getValores(String codigoSaldo) {
		
		
		mensaje = "";
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = "SELECT sp.saldo AS saldo_pendiente, sp.valor AS saldo_cliente "
					+ "from saldos_pendientes AS sp "
					+ "where sp.cod_saldo='" + codigoSaldo + "'";
			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {
				do {
					float [] valores = new float[2];
					valores[0] = cursor.getFloat(cursor.getColumnIndex("saldo_cliente"));
					valores[1] = cursor.getFloat(cursor.getColumnIndex("saldo_pendiente"));
					return valores;
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
		return null;
	}
    
    public static boolean VerificarRecaudo(String codigoCliente) {

		boolean existe = false;
		SQLiteDatabase db = null;
		float Dia= 0;
		float diasCartera=0;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = "SELECT  s.dias,s.saldo,c.clienteCartera " + 
					" FROM saldos_pendientes s " + 
					"inner join clientes c on s.cod_cliente = c.cod_cliente " +
					" WHERE s.cod_cliente='" +codigoCliente + "' " + 
					"AND s.estado = 1 AND s.dias > c.diasRecaudo ";
			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {
//				Dia = cursor.getFloat(cursor.getColumnIndex("dias"));
//				diasCartera = cursor.getFloat(cursor.getColumnIndex("clienteCartera"));
				do {
					if(cursor.getFloat(cursor.getColumnIndex("dias"))>=cursor.getFloat(cursor.getColumnIndex("clienteCartera"))) {
						if(cursor.getFloat(cursor.getColumnIndex("saldo"))!=0) {
						existe = true;}
					}
					
				}while(cursor.moveToNext());
				
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
    public static float diasDePago(String codigoCliente) {

		SQLiteDatabase db = null;
		String sql = "";
		float dias = 0;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			

				sql =  "SELECT  s.dias" + 
						" FROM saldos_pendientes s " + 
						"inner join clientes c on s.cod_cliente = c.cod_cliente " +
						" WHERE s.cod_cliente='" +codigoCliente + "' " + 
						"AND s.estado = 1 AND s.dias > c.diasRecaudo";
			

			Cursor cursor = db.rawQuery(sql, null);

			if (cursor.moveToFirst()) {

				dias = cursor.getInt(cursor.getColumnIndex("dias"));
				
			}

			if (cursor != null)
				cursor.close();
		} catch (Exception e) {

		} finally {

			closeDataBase(db);
		}

		return dias;
	}
    
    
	
	public static Vector<Cartera> listaCartera(String codigoCliente, Vector<ItemListView> listaItems) {
		
		
		mensaje = "";
		SQLiteDatabase db = null;

		Cartera cartera = new Cartera();
		
		ItemListView itemListView;
		Vector<Cartera> listaCartera = new Vector<Cartera>();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			
			String query = " SELECT  s.cod_saldo,s.documento,s.saldo,s.cod_cliente,s.fecha,s.fecha_venc,s.dias,s.prefijo||s.consecutivo AS numero_factura  "+
							" FROM saldos_pendientes AS s "
							+ " WHERE s.cod_cliente='" +codigoCliente + "' "									
									+ " AND s.estado = 1 NOT IN (SELECT s.saldo=0 FROM saldos_pendientes ) ";
			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {
				do {
					cartera = new Cartera();
					itemListView = new ItemListView();
					// Se valida que el Saldo no Contenga e+
					String saldo = cursor.getString(cursor.getColumnIndex("saldo"));
					if (saldo.contains("e+")) {
						saldo = saldo.replace("e+", "E");
					}
					saldo = Util.QuitarE(saldo);
					cartera.strSaldo = Util.Redondear(saldo, 0);
					cartera.codSaldo = cursor.getString(cursor.getColumnIndex("cod_saldo"));
					cartera.codCliente = cursor.getString(cursor.getColumnIndex("cod_cliente"));
					cartera.documento = cursor.getString(cursor.getColumnIndex("documento"));
					cartera.saldo = Util.ToFloat(Util.Redondear(saldo, 0));
					cartera.fecha = cursor.getString(cursor.getColumnIndex("fecha"));
					cartera.FechaVecto = cursor.getString(cursor.getColumnIndex("fecha_venc"));
					cartera.dias = cursor.getInt(cursor.getColumnIndex("dias"));
					cartera.numero_factura = cursor.getString(cursor.getColumnIndex("numero_factura"));
					listaCartera.addElement(cartera);
					
//					itemListView.titulo = "Doc:" + cartera.documento;
					itemListView.titulo = "Número factura:" + cartera.numero_factura;
					itemListView.subTitulo = "$"+ cartera.strSaldo +"/ dias venc: "+ cartera.dias+ "/ fecha realización: "+cartera.fecha.substring(0, 10) +"/ fecha vence: "+cartera.FechaVecto.substring(0, 10);

					listaItems.add(itemListView);
			

					
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


	public static boolean buscarProductoPorParametroConocido(String parametroBusqueda, List<Producto> listaProductos) {
		SQLiteDatabase db = null;
		boolean finalizado = false;
		try {
			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			/*
			 * Consulta para obtener la lista de productos disponibles para
			 * autoventa
			 */
			String query = "select cod_producto,nombre,precio,iva,descuento,cod_linea from productos where cod_producto like '%"
					+ parametroBusqueda + "%' or nombre like '%" + parametroBusqueda + "%'";

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
				throw new Exception("No se logro borrar datos de producto " + producto.codigo);
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
				throw new Exception("No se logro actualizar datos de producto " + producto.codigo);
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

	public static String ActualizarConsecutivo() {

		String num = "-";
		SQLiteDatabase db = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			long consecutivo = 0;
			Cursor cursor = db.rawQuery("SELECT cons_actual FROM resoluciones limit 1", null);

			if (cursor.moveToFirst())
				consecutivo = cursor.getLong(cursor.getColumnIndex("cons_actual"));

			if (cursor != null)
				cursor.close();

			consecutivo = (consecutivo + 1);
			db.execSQL("UPDATE resoluciones SET cons_actual = " + consecutivo);

			if (cursor != null)
				cursor.close();

			return num;

		} catch (Exception e) {

			mensaje = e.getMessage();
			return "-";

		} finally {

			if (db != null)
				db.close();
		}
	}

	public static boolean guardarConsecutivoResoluciones(String codUsuario, String codResolucion, long consecuivo) {

		SQLiteDatabase tmp = null;
		boolean insertado = false;
		long rowTmp;

		try {

			File dbTmp = new File(Util.DirApp(), "Temp.db");
			tmp = SQLiteDatabase.openDatabase(dbTmp.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			// iniciar transacciones para garantizar la correcta insercion en
			// las base de datos.
			tmp.beginTransaction();

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
				tmp.setTransactionSuccessful();

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

					if (sync == 1) {

						query = " SELECT distinct e.cod_cliente AS codigo_cliente, "
								+ " c.representante_legal as nombre_cliente, c.razon_social  AS razon_cliente, documento, valor, "
								+ " descuento, iva, e.cod_tipo_trans as tipoTrans " + " FROM encabezado_venta e "
								+ " INNER JOIN Clientes c ON e.cod_cliente = c.cod_cliente " + " ";

					} else

					if (sync == 4) {

						query = " SELECT distinct e.cod_cliente AS codigo_cliente, "
								+ " c.representante_legal as nombre_cliente, c.razon_social  AS razon_cliente, documento, valor, "
								+ " descuento, iva, e.cod_tipo_trans as tipoTrans " + " FROM encabezado_venta e "
								+ " INNER JOIN Clientes c ON e.cod_cliente = c.cod_cliente "
								+ " WHERE e.cod_tipo_trans = '5' ";

					} else {

						if (sync == 0) {

							query = " SELECT distinct e.cod_cliente AS codigo_cliente, "
									+ " c.representante_legal as nombre_cliente, c.razon_social  AS razon_cliente, documento, valor, "
									+ " descuento, iva, e.cod_tipo_trans as tipoTrans " + " FROM encabezado_venta e "
									+ " INNER JOIN Clientes c ON e.cod_cliente = c.cod_cliente "
									+ " WHERE e.cod_tipo_trans = '2' and sync=0";

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

					if (sync == 1) {

						query = " SELECT distinct e.cod_cliente AS codigo_cliente, "
								+ " c.representante_legal as nombre_cliente, c.razon_social  AS razon_cliente, documento, valor, "
								+ " descuento, iva, e.cod_tipo_trans as tipoTrans " + " FROM encabezado_venta e "
								+ " INNER JOIN Clientes c ON e.cod_cliente = c.cod_cliente " + " ";

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

			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				do {

					cliente = new Cliente();
					itemListView = new ItemListView();

					cliente.codigo = cursor.getString(cursor.getColumnIndex("Codigo")).trim();
					cliente.razonSocial = cursor.getString(cursor.getColumnIndex("razon_social")).trim();
					cliente.nombre = cursor.getString(cursor.getColumnIndex("Nombre"));
					cliente.direccion = cursor.getString(cursor.getColumnIndex("direccion")).trim();

					cliente.nit = cursor.getString(cursor.getColumnIndex("nit")).trim();
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
								+ "    descuento, iva,fecha_movil FROM encabezado_venta INNER JOIN Clientes ON encabezado_venta.cod_cliente "
								+ "    = Clientes.cod_cliente WHERE ( cod_tipo_trans = '2' ) "
								+ "    order by fecha_movil desc ";

					}

					if (Const.tipoAplicacion == Const.PREVENTA) {

						query = " 	SELECT distinct encabezado_venta.cod_cliente AS codigo_cliente, "
								+ "    clientes.razon_social  AS nombre_cliente, documento, valor,    "
								+ "    descuento, iva,fecha_movil FROM encabezado_venta INNER JOIN Clientes ON encabezado_venta.cod_cliente "
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
					+ " p.nombre as descripcion, p.cod_linea as linea,0 as saldo FROM detalle_venta d "
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
			Log.e("No se logroo insertar datos de encabezado", "error: " + e.getMessage());
		} finally {
			closeDataBase(db);
			closeDataBase(tmp);
		}
		return insertado;
	}

	public static Venta getValoresVentas() {

		SQLiteDatabase db = null;
		String sql = "";

		float subTotal, desc, iva, neto;
		Venta venta = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");

			if (dbFile.exists()) {

				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				if (Const.tipoAplicacion == Const.AUTOVENTA) {

					sql = "select ifnull( sum( d.precio * d.cantidad ), 0 ) as subTotal, "
							+ " ifnull( sum( ( ( d.precio * d.descuento )  ) * d.cantidad ), 0 ) as descuento, "
							+ " ifnull( sum( ( ( ( d.precio - ( ( d.precio * d.descuento ) / 100 ) ) * d.iva ) / 100 ) * d.cantidad ), 0 ) as iva "
							+ " from detalle_venta d inner join productos p on d.cod_producto = p.cod_producto "
							+ "  inner join  encabezado_venta on encabezado_venta.documento = d.documento where encabezado_venta.cod_tipo_trans=2";

				}

				if (Const.tipoAplicacion == Const.PREVENTA) {

					sql = "select ifnull( sum( d.precio * d.cantidad ), 0 ) as subTotal, "
							+ " ifnull( sum( ( ( d.precio * d.descuento )  ) * d.cantidad ), 0 ) as descuento, "
							+ " ifnull( sum( ( ( ( d.precio - ( ( d.precio * d.descuento ) / 100 ) ) * d.iva ) / 100 ) * d.cantidad ), 0 ) as iva "
							+ " from detalle_venta d inner join productos p on d.cod_producto = p.cod_producto "
							+ "  inner join  encabezado_venta on encabezado_venta.documento = d.documento where encabezado_venta.cod_tipo_trans=1";

				}

				Cursor cursor = db.rawQuery(sql, null);

				if (cursor.moveToFirst()) {

					do {

						venta = new Venta();

						subTotal = cursor.getFloat(cursor.getColumnIndex("subTotal"));
						iva = cursor.getFloat(cursor.getColumnIndex("iva"));
						desc = cursor.getFloat(cursor.getColumnIndex("descuento"));

						iva = iva * 100;

						neto = subTotal + iva - desc;
						venta.subTotal = subTotal;
						venta.descuento = desc;
						venta.iva = iva;
						venta.neto = neto;

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
			int codEstado) {
		
		SQLiteDatabase tmp = null;
		boolean insertado = false;
		long rowTmp;

		try {
					
			File dbTmp = new File(Util.DirApp(), "Temp.db");
			tmp = SQLiteDatabase.openDatabase(dbTmp.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			// iniciar transacciones para garantizar la correcta insercion en
			// las base de datos.
			tmp.beginTransaction();

			/*
			 * captura de los datos que seran insertados en las bases de datos.
			 */
			ContentValues values = new ContentValues();
			values.put("cod_saldo", codSaldo);
			values.put("documento", documento);
			values.put("cod_usuario", codUsuario);
			values.put("saldo", saldo);
			values.put("cod_est_saldo", codEstado);

			rowTmp = tmp.insertOrThrow("saldos_cliente", null, values);


			// verificar que la actualizacion fue correcta en ambas bases de
			// datos. y confirmar la transaccion como exitosa.
			if (rowTmp != -1) {

				insertado = true;
				tmp.setTransactionSuccessful();

			} else {

			}
		} catch (Exception e) {

		} finally {
			closeDataBase(tmp);
		}
		return insertado;
	}

	public static boolean guardarSaldosCanceladosCliente(String codUsuario, String documento, String codSaldo,
			float saldo, int codEstado) {

		SQLiteDatabase tmp = null;
		boolean insertado = false;
		long rowTmp;

		try {

			File dbTmp = new File(Util.DirApp(), "Temp.db");
			tmp = SQLiteDatabase.openDatabase(dbTmp.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			// iniciar transacciones para garantizar la correcta insercion en
			// las base de datos.
			tmp.beginTransaction();

			/*
			 * captura de los datos que seran insertados en las bases de datos.
			 */
			ContentValues values = new ContentValues();
			values.put("cod_saldo_canc", codSaldo);
			values.put("cod_saldo", codSaldo);
			values.put("cod_usuario", codUsuario);
			values.put("saldo_recibido", saldo);
			values.put("cod_tipo_pago", codEstado);
			values.put("observacion", "Saldo pago automatico");

			rowTmp = tmp.insertOrThrow("saldos_cancelados", null, values);

			// verificar que la actualizacion fue correcta en ambas bases de
			// datos. y confirmar la transaccion como exitosa.
			if (rowTmp != -1) {

				insertado = true;
				tmp.setTransactionSuccessful();

			} else {

			}
		} catch (Exception e) {

		} finally {
			closeDataBase(tmp);
		}
		return insertado;
	}

	public static boolean organizarInventario(Context contexto) {

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

			sql =

			" select i.cod_producto as codProducto," + " ifnull( sum( cant_inicial ), 0 ) - "
					+ " ifnull((select SUM( d.cantidad ) from encabezado_venta e "
					+ " inner join detalle_venta d on d.documento = e.documento and d.cod_producto = i.cod_producto "
					+ " where  e.cod_tipo_trans in (1,2,3,4,5) " + " group by d.cod_producto " + " ),0)  as cantidad "
					+ " from inventario_cargado i " + " group by i.cod_producto " + " order by i.cod_producto ";

			Cursor cursor = db.rawQuery(sql, null);

			if (cursor.moveToFirst()) {

				do {

					codProducto = cursor.getString(cursor.getColumnIndex("codProducto"));
					cantidad = cursor.getFloat(cursor.getColumnIndex("cantidad"));

					sql = "insert into Inventario " + "( cod_producto,cantidad ) " + "values " + "( '" + codProducto
							+ "', '" + cantidad + "'  )";

					db.execSQL(sql);

				} while (cursor.moveToNext());
			}

			if (cursor != null)
				cursor.close();

			bien = true;
		} catch (Exception e) {

			Toast.makeText(contexto, "organizarInventario", Toast.LENGTH_LONG).show();

		} finally {

			if (db != null)
				db.close();

		}

		return bien;
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

	public static Vector<Ciudad> listaCiudad(Departamento departamento) {

		mensaje = "";
		SQLiteDatabase db = null;
		Vector<Ciudad> listaCiudad = new Vector<Ciudad>();
		Ciudad ciudad;
		try {

			File dbFile = new File(Util.DirApp(), "database.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			Cursor cursor = db
					.rawQuery("SELECT cod_ciudad, Descripcion AS descripcion FROM ciudad where cod_departamento="
							+ departamento.id, null);

			if (cursor.moveToFirst()) {

				do {

					ciudad = new Ciudad();
					ciudad.id = cursor.getInt(cursor.getColumnIndex("cod_ciudad"));
					ciudad.nombre = cursor.getString(cursor.getColumnIndex("descripcion"));
					listaCiudad.addElement(ciudad);

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
			values.put("telefono", clienteNuevo.telefono);
			values.put("telefono_movil", clienteNuevo.telefonoCel);
			values.put("email", clienteNuevo.email);
			values.put("cod_lista", "");
			values.put("cupo", "");
			values.put("cod_usuario", Main.usuario.codigoVendedor);
			values.put("cod_canal_venta", Main.usuario.canalVenta);
			values.put("cod_ciudad", clienteNuevo.cod_ciudad);
			values.put("cod_usr_registro", Main.usuario.codigoVendedor);
			values.put("cliente_nuevo", "1");
			values.put("listaPrecios", clienteNuevo.listaPrecio);
			values.put("codigotipocliente", clienteNuevo.tipoPersona);
			values.put("codigoStatusCliente", clienteNuevo.estus);

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

	public static boolean guardarClienteModificado(ClienteNuevo clienteNuevo) {

		SQLiteDatabase db = null;
		SQLiteDatabase dbTemp = null;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			File fileTemp = new File(Util.DirApp(), "Temp.db");
			dbTemp = SQLiteDatabase.openDatabase(fileTemp.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			ContentValues values = new ContentValues();
			values.put("cod_cliente", clienteNuevo.codigo);
			values.put("nit", clienteNuevo.nit);
			values.put("representante_legal", clienteNuevo.nombre);
			values.put("razon_social", clienteNuevo.razonSocial);
			values.put("barrio", clienteNuevo.barrio);
			values.put("direccion", clienteNuevo.direccion);
			values.put("telefono", clienteNuevo.telefono);
			values.put("telefono_movil", clienteNuevo.telefonoCel);
			values.put("email", clienteNuevo.email);
			values.put("cod_lista", "0");
			values.put("cupo", "");
			values.put("cod_usuario", Main.usuario.codigoVendedor);
			values.put("cod_canal_venta", Main.usuario.canalVenta);
			values.put("cod_ciudad", clienteNuevo.cod_ciudad);
			values.put("cod_usr_registro", "NA");
			values.put("cliente_nuevo", "0");
			values.put("codigotipocliente", clienteNuevo.tipoPersona);
			values.put("codigoStatusCliente", clienteNuevo.estus);
			

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

	public static Vector<Departamento> edListaDpto() {

		mensaje = "";
		SQLiteDatabase db = null;
		Vector<Departamento> edListaDpto = new Vector<Departamento>();
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

					edListaDpto.addElement(departamento);

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

		return edListaDpto;

	}

	public static Vector<Ciudad> edListaCiudad(Departamento departamento) {

		mensaje = "";
		SQLiteDatabase db = null;
		Vector<Ciudad> edListaCiudad = new Vector<Ciudad>();
		Ciudad ciudad;
		try {

			File dbFile = new File(Util.DirApp(), "database.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			Cursor cursor = db
					.rawQuery("SELECT cod_ciudad, Descripcion AS descripcion FROM ciudad where cod_departamento="
							+ departamento.id, null);

			if (cursor.moveToFirst()) {

				do {

					ciudad = new Ciudad();
					ciudad.id = cursor.getInt(cursor.getColumnIndex("cod_ciudad"));
					ciudad.nombre = cursor.getString(cursor.getColumnIndex("descripcion"));
					edListaCiudad.addElement(ciudad);

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

		return edListaCiudad;

	}
	
	public static Vector<SaldoCancelado> buscarDepositos( Vector<ItemListView> listaItems, String cadBusqueda) {

		mensaje = "";
		SaldoCancelado saldo;
		SQLiteDatabase db = null;

		ItemListView itemListView;
		Vector<SaldoCancelado> listaClientes = new Vector<SaldoCancelado>();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = " SELECT s.cod_saldo_canc AS cod_saldo_canc, s.cod_saldo AS cod_saldo, s.saldo_recibido AS saldo_recibido, c.cod_cliente AS cod_cliente, cl.razon_social AS razon_social, s.factura as factura "
							+ "FROM saldos_cancelados AS s "
							+ "JOIN saldos_cliente c ON s.cod_saldo = c.cod_saldo "
							+ "JOIN clientes cl ON cl.cod_cliente = c.cod_cliente "
							+ "WHERE (c.cod_cliente like '%" + cadBusqueda + "%' or cl.razon_social like '%" + cadBusqueda + "%') "
							+ " AND s.cod_saldo_canc NOT IN (SELECT cod_saldo_canc FROM detalle_deposito_realizados )" ;
			
			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				do {

					saldo = new SaldoCancelado();
					itemListView = new ItemListView();

					saldo.cod_saldo_canc = cursor.getString(cursor.getColumnIndex("cod_saldo_canc")).trim();
					saldo.cod_saldo = cursor.getString(cursor.getColumnIndex("cod_saldo")).trim();
					saldo.cod_cliente = cursor.getString(cursor.getColumnIndex("cod_cliente")).trim();
					saldo.razon_social = cursor.getString(cursor.getColumnIndex("razon_social")).trim();
					saldo.saldo_recibido = cursor.getFloat(cursor.getColumnIndex("saldo_recibido"));
					saldo.cod_cliente =  cursor.getString(cursor.getColumnIndex("cod_cliente"));
					saldo.factura =  cursor.getString(cursor.getColumnIndex("factura"));
					
					itemListView.titulo = " Valor a Depositar : " + Util.SepararMiles(saldo.saldo_recibido + "") ;
					itemListView.subTitulo = " Cliente : " + saldo.cod_cliente + " - " + saldo.razon_social;
					itemListView.codSaldoCan = saldo.cod_saldo_canc;
					itemListView.codSaldo = saldo.cod_saldo;
					itemListView.saldo = saldo.saldo_recibido;
					itemListView.cod_cliente = saldo.cod_cliente;
					itemListView.razon_social = saldo.razon_social;
					itemListView.factura = saldo.factura;

					listaItems.add(itemListView);
					listaClientes.addElement(saldo);

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
	
	public static boolean guardarDeposito(Deposito depo, ArrayList<DetalleDeposito> detDepo) {

		SQLiteDatabase db = null;
		SQLiteDatabase dbTemp = null;
		long row = 0, rowTemp = 0;

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			File fileTemp = new File(Util.DirApp(), "Temp.db");
			dbTemp = SQLiteDatabase.openDatabase(fileTemp.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			ContentValues values = new ContentValues();
			
			float totalDepo = 0;

			
			for (DetalleDeposito detalle : detDepo) {
				values.clear();
				values.put("documento", detalle.documento);
				values.put("cod_saldo_canc", detalle.cod_saldo_canc);
				values.put("saldo", detalle.saldo);
				values.put("cod_est_deposito", detalle.cod_est_deposito);
				values.put("cod_cliente", detalle.cod_cliente);
				
				totalDepo += detalle.saldo;

				rowTemp += db.insertOrThrow("detalle_deposito_realizados", null, values);
				       dbTemp.insertOrThrow("detalle_deposito", null, values);
			}
			
			depo.totalDeposito = totalDepo;
			
			values.clear();
			values.put("fecha_movil",Util.FechaActual("yyyy-MM-dd HH:mm:ss.SSS"));
			values.put("documento", depo.documento);
			values.put("cod_usuario", depo.cod_usuario);
			values.put("observacion", depo.observacion);
			values.put("compr_bancario", depo.compr_bancario);
			values.put("cod_est_deposito", depo.cod_est_deposito);
			values.put("cod_banco", depo.cod_banco);
			values.put("cant_cheques", depo.cant_cheques);
			values.put("valor", totalDepo);

			row = db.insertOrThrow("encabezado_deposito", null, values);
			dbTemp.insertOrThrow("encabezado_deposito", null, values);
			
			if(row > 0 && rowTemp > 0){
				
			}
			
			return row > 0 && rowTemp > 0;

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, "guardarDeposito -> " + mensaje, e);
			return false;

		} finally {

			if (db != null)
				db.close();

			if (dbTemp != null)
				dbTemp.close();
		}
	}
	
	public static boolean guardarImagenDeposito(String docDeposito, byte[] image, double latitud, double longitud) {

		SQLiteDatabase db = null;
		SQLiteDatabase dbTemp = null;

		try {

			dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			File filePedido = new File(Util.DirApp(), "Temp.db");
			dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
			
			ContentValues values = new ContentValues();
			
			String idFoto = "A" + Main.usuario.codigoVendedor + Util.ObtenerFechaId();;

			if (image != null) {

				values = new ContentValues();
				values.put("cod_novedad", docDeposito);
				values.put("imagen", image);
				values.put("extension", ".jpg");
				

				db.insertOrThrow("fotos", null, values);
				dbTemp.insertOrThrow("fotos", null, values);
			}
			
			values = new ContentValues();
			values.put("cod_imagen", idFoto);
			db.update("encabezado_deposito", values, "documento = '" + docDeposito + "'", null);
			dbTemp.update("encabezado_deposito", values, "documento = '" + docDeposito + "'", null);
			
			
			values = new ContentValues();
			values.put("foto", 1);
			values.put("latitud",latitud);
			values.put("longitud",longitud);
			db.update("novedades", values, "cod_novedad = '" + docDeposito + "'", null);
			dbTemp.update("novedades", values, "cod_novedad = '" + docDeposito + "'", null);

			return true;

		} catch (Exception e) {

			mensaje = e.getMessage();
			Log.e(TAG, "guardarImagenDeposito: " + mensaje, e);
			return false;

		} finally {

			if (db != null)
				db.close();

			if (dbTemp != null)
				dbTemp.close();
		}
	}
	
	
	
	
	public static Vector<SaldoCancelado> buscarDepositos( Vector<ItemListView> listaItems, String cadBusqueda,String codSaldo) {

		mensaje = "";
		SaldoCancelado saldo;
		SQLiteDatabase db = null;

		ItemListView itemListView;
		Vector<SaldoCancelado> listaClientes = new Vector<SaldoCancelado>();

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = " SELECT s.cod_saldo_canc AS cod_saldo_canc, s.cod_saldo AS cod_saldo, s.saldo_recibido AS saldo_recibido, c.cod_cliente AS cod_cliente, cl.razon_social AS razon_social, s.factura as factura "
							+ "FROM saldos_cancelados AS s "
							+ "JOIN saldos_cliente c ON s.cod_saldo = c.cod_saldo "
							+ "JOIN clientes cl ON cl.cod_cliente = c.cod_cliente "
							+ "WHERE (c.cod_cliente like '%" + cadBusqueda + "%' or cl.razon_social like '%" + cadBusqueda + "%') "
							+ " AND s.cod_saldo_canc NOT IN (SELECT cod_saldo_canc FROM detalle_deposito_realizados )" ;
			
		
			 
			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {

				do {

					saldo = new SaldoCancelado();
					itemListView = new ItemListView();

					saldo.cod_saldo_canc = cursor.getString(cursor.getColumnIndex("cod_saldo_canc")).trim();
					saldo.cod_saldo = cursor.getString(cursor.getColumnIndex("cod_saldo")).trim();
					saldo.cod_cliente = cursor.getString(cursor.getColumnIndex("cod_cliente")).trim();
					saldo.razon_social = cursor.getString(cursor.getColumnIndex("razon_social")).trim();
					saldo.saldo_recibido = cursor.getFloat(cursor.getColumnIndex("saldo_recibido"));
					saldo.cod_cliente =  cursor.getString(cursor.getColumnIndex("cod_cliente"));
					saldo.factura =  cursor.getString(cursor.getColumnIndex("factura"));
					
					itemListView.titulo = " Valor a Depositar : " + Util.SepararMiles(saldo.saldo_recibido + "") ;
					itemListView.subTitulo = " Cliente : " + saldo.cod_cliente + " - " + saldo.razon_social;
					itemListView.codSaldoCan = saldo.cod_saldo_canc;
					itemListView.codSaldo = saldo.cod_saldo;
					itemListView.saldo = saldo.saldo_recibido;
					itemListView.cod_cliente = saldo.cod_cliente;
					itemListView.razon_social = saldo.razon_social;
					itemListView.factura = saldo.factura;
					
					if(codSaldo.equals(saldo.cod_saldo_canc)) {
						itemListView.isChecked = true;
						itemListView.isEnable = false;
					}else {
						itemListView.isChecked = true;
						itemListView.isEnable = false;
					}

					listaItems.add(itemListView);
					listaClientes.addElement(saldo);

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
	
	
	// lugar donde se calcula el total de la lista saldos depositos
	
	public static String SaldoTotalDeposito() {

		SQLiteDatabase db = null;
		String saldototal = "";
		mensaje = "";

		try {

			File dbFile = new File(Util.DirApp(), "DataBase.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			String query = "SELECT (SUM(saldo_recibido))AS totalSaldo FROM saldos_cancelados "
					+ "where cod_saldo_canc " 
					+ "NOT IN (SELECT cod_saldo_canc FROM detalle_deposito_realizados )";

			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {
				
				saldototal = cursor.getString(cursor.getColumnIndex("totalSaldo"));
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

		return saldototal;
	}
	
	//LISTA EL TIPO DE PERSONA SI JURIDICA O NATURAL
	public static Vector<ObjectSel> listaTipoPersona() {

		mensaje = "";
		SQLiteDatabase db = null;
		Vector<ObjectSel> listaDpto = new Vector<ObjectSel>();
		ObjectSel departamento;

		try {

			File dbFile = new File(Util.DirApp(), "database.db");
			db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

			Cursor cursor = db.rawQuery("SELECT Codigo, Descripcion FROM tipoCliente", null);

			if (cursor.moveToFirst()) {

				do {
					departamento = new ObjectSel();

					departamento.codigo = cursor.getInt(cursor.getColumnIndex("Codigo"));
					departamento.descripcion = cursor.getString(cursor.getColumnIndex("Descripcion"));

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
	
	
	//LISTA EL TIPO DE PERSONA SI JURIDICA O NATURAL
		public static Vector<ObjectSel> listaEstatusCliente() {

			mensaje = "";
			SQLiteDatabase db = null;
			Vector<ObjectSel> listaDpto = new Vector<ObjectSel>();
			ObjectSel departamento;

			try {

				File dbFile = new File(Util.DirApp(), "database.db");
				db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

				Cursor cursor = db.rawQuery("SELECT codigo, descripcion FROM statusCliente", null);

				if (cursor.moveToFirst()) {

					do {
						departamento = new ObjectSel();

						departamento.codigo = cursor.getInt(cursor.getColumnIndex("codigo"));
						departamento.descripcion = cursor.getString(cursor.getColumnIndex("descripcion"));

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
	
	
}
