package co.com.panificadoracountry;

import java.util.ArrayList;

import co.com.panificadoracountry.R;
import co.com.DataObject.ItemListView;
import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewAdapter1 extends ArrayAdapter<ItemListView> {
    
    int icono;
    int colorTitulo;
    Activity context;
    ItemListView[] listItems = null;
    ArrayList<ItemListView> arrayListaItems = null;
    
    
    public ListViewAdapter1(Activity context, ItemListView[] listItems, int icono, int colorTitulo) {
	
	super(context, R.layout.list_item2, listItems);
	this.listItems = listItems; 
	this.context = context;
	
	this.icono = icono;
	this.colorTitulo = colorTitulo; 
    }
    
    
    
    /**
     * Constructor para usar con arraylist
     * @param context
     * @param listItems
     * @param icono
     * @param colorTitulo
     */
    public ListViewAdapter1(Activity context, ArrayList<ItemListView> listItems, int icono, int colorTitulo) {
	super(context, R.layout.list_item2, listItems);
	this.arrayListaItems = listItems; 
	this.context = context;
	this.icono = icono;
	this.colorTitulo = colorTitulo; 
    }
    
    
    
    
    /**
     * optimizacion de getView
     */
    public View getView(int position, View convertView, ViewGroup parent) {
	
	if(convertView == null) {
	    LayoutInflater inflater = context.getLayoutInflater();
	    convertView = inflater.inflate(R.layout.list_item2, null);
	    
	    //configurar viewHolder (optimizacion)
	    Row rowHolder = new Row();			
	    rowHolder.tituloItem = (TextView) convertView.findViewById(R.id.lblTitulo);
	    rowHolder.subtitulo = (TextView) convertView.findViewById(R.id.lblSubTitulo);			
	    rowHolder.icono = (ImageView) convertView.findViewById(R.id.iconListView);
	    rowHolder.vista = convertView;
	    convertView.setTag(rowHolder);
	}
	
	//llenar datos
	Row holder = (Row) convertView.getTag();
	
	/*capturar el item de la lista adecuada*/
	ItemListView item = null;
	if (listItems != null && listItems.length > 0) {
	    item = getItem(position);
	}
	else if(arrayListaItems != null && !arrayListaItems.isEmpty()){
	    item = getArrayItem(position);
	}
	
	/*llenar el item con la informacion actual a mostrar */
	if (item != null) {
	    holder.tituloItem.setText(item.titulo);
	    holder.subtitulo.setText(item.subTitulo);
	    
	    /*seleccionar icono*/
	    if(icono > 0 ){
		holder.icono.setImageResource(icono);
	    }
	    else {
		holder.icono.setImageResource(item.icono);
	    }
	    
	    /*Seleccionar color*/
	    if(colorTitulo != 0){
		holder.tituloItem.setTextColor(colorTitulo);
	    }
	    
	    
	   
	}
	return convertView;
    }
    
    
    
    
    @Override
    public ItemListView getItem(int position) {
	
	return listItems[position];
    }
    
    
    public ItemListView getArrayItem(int position) {
	return arrayListaItems.get(position);
    }
    
    
    
    /**
     * representa una fila, (ViewHolder).
     * @author JICZ
     */
    public static class Row {
	TextView tituloItem;
	TextView subtitulo;
	ImageView icono;
	View vista;
    }
}
