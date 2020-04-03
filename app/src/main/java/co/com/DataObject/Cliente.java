package co.com.DataObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.content.Context;
import android.util.Log;

public class Cliente implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public String codigo;
	public String nombre;
	public String direccion;
	public String telefono;
	public String telefonoCel;
	public String nit;
	public String razonSocial;
	public String ciudad;
	public int cod_ciudad;
	public String listaPrecio; 
	public float descuento;
	public String tipoCredito;
	public String email;
	public int retefuente = 0;
	public int reteIca = 0;
	
	public String canal;
	public String cupo;
	public String bloqueado;
	
	public int extra_ruta;
	public int ordenVisita;
	
	public String territorio;
	public String ruta_parada;
	public String motivoNoCompra;
	public String barrio;
	public String Canal;
	public String SubCanal;
	
	public int calcularIva;
	public String grupoclientes;
	public String codLista;
	public boolean esClienteNuevo = false;
	
	
	public static Cliente get(Context context) {
		 
		Cliente cliente = null;
		String nameMethod = "get";
		 
		 if (context != null) {
			
			 String fileName = CLASS.getSimpleName();
			 
			 if (fileName != null) {

				 FileInputStream fileInputStream = null;
				 ObjectInputStream objInputStream = null;
				 
				 try {
					 
					 fileInputStream = context.openFileInput(fileName);
					 objInputStream = new ObjectInputStream(fileInputStream);
					 cliente = (Cliente) objInputStream.readObject();
					 
				 } catch(Exception e) {
					 
					 String msg = e.getMessage();
					 Log.e(TAG, nameMethod + " -> " + msg, e);
					 
				 } finally {
					 
					 try {
						 
						 if (fileInputStream != null) {
							 fileInputStream.close();
						 }
						 
						 if (objInputStream != null) {
							 objInputStream.close();
						 }
						 
					 } catch (Exception e) { }
				 }
				 
			 } else {
				 
				 Log.e(TAG, nameMethod + " -> No se pudo leer el nombre de la clase!");
			 }
			 
		 } else {
			 
			 Log.e(TAG, nameMethod + " -> No se pudo leer el Objeto, el Context es NULL");
		 }
		 
		 return cliente;
	 }
	
	 public static boolean save(Context context, Cliente obj) {
		 
		 boolean guardo = false;
		 String nameMethod = "save";
		 
		 if (context != null) {
			 
			 String fileName = CLASS.getSimpleName();
			 
			 if (fileName != null) {
				
				 if (obj != null) {
					 
					 FileOutputStream fileOutputStream = null;
					 ObjectOutputStream objOutputStream = null;
					 
					 try {
						 
						 context.deleteFile(fileName);
						 
						 fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
						 objOutputStream = new ObjectOutputStream(fileOutputStream);
						 objOutputStream.writeObject(obj);
						 guardo = true;
						 
					 } catch (Exception e) {
						 
						 String msg = e.getMessage();
						 Log.e(TAG, nameMethod + " -> " + msg, e);
						 
					 } finally {
						 
						 try {
							 
							 if (objOutputStream != null) {
								 objOutputStream.close();
							 }
							 
							 if (fileOutputStream != null) {
								 fileOutputStream.close();
							 }
							 
							 if (!guardo) {
								 context.deleteFile(fileName);
							 }
							 
						 } catch (Exception e) { }
					 }
					 
				 } else {
					 
					 /**
					  * Si el objeto es NULL, se borra el archivo!
					  **/
					 context.deleteFile(fileName);
				 }
				 
			 } else {
				 
				 Log.e(TAG, nameMethod + " -> No se pudo leer el nombre de la clase!");
			 }
			 
		 } else {
			 
			 Log.e(TAG, nameMethod + " -> No se pudo Eliminar el Objeto, el Context es NULL");
		 }
		 
		 return guardo;
	 }
	 
	 public static boolean delete(Context context) {
		 
		 boolean elimino = false;
		 String nameMethod = "delete";
		 
		 if (context != null) {
			 
			 String fileName = CLASS.getSimpleName();
			 
			 if (fileName != null) {
				 
				 elimino = context.deleteFile(fileName);
				 
			 } else {
				 
				 Log.e(TAG, nameMethod + " -> No se pudo leer el nombre de la clase!");
			 }
			 
		 } else {
			 
			 Log.e(TAG, nameMethod + " -> No se pudo Eliminar el Objeto, el Context es NULL");
		 }
		 
		 return elimino;
	 }
	 
	 private static final Class<Cliente> CLASS = Cliente.class;
	 
	 private static final String TAG = CLASS.getName();
	 
	 
	 public double longitud;
	 public double latitud;

	public int tipoCliente;
	public int estatus;
}
