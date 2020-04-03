package co.com.panificadoracountry;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import co.com.panificadoracountry.R;
import co.com.DataObject.ItemListView;

public class ListViewPrinterAdapter extends ArrayAdapter<ItemListView> {
	
	int icono;
	int colorTitulo;
	int layout_list_item;
	
	Activity context;
	ArrayList<ItemListView> listItems;	
	
	ListViewPrinterAdapter(Activity context, ArrayList<ItemListView> listItems, int icono, int colorTitulo) {
		
		super(context, R.layout.list_item, listItems);
		
		this.layout_list_item = R.layout.list_item;
		this.listItems = listItems; 
		this.context = context;
		
		this.icono = icono;
		this.colorTitulo = colorTitulo; 
	}
	
	ListViewPrinterAdapter(Activity context, ArrayList<ItemListView> listItems, int icono, int colorTitulo, int layout_list_item) {
		
		super(context, layout_list_item, listItems);
		
		this.layout_list_item = layout_list_item;
		this.listItems = listItems; 
		this.context = context;
		
		this.icono = icono;
		this.colorTitulo = colorTitulo; 
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = context.getLayoutInflater();
		View item = inflater.inflate(layout_list_item, null);
		
		TextView lblTitulo = (TextView)item.findViewById(R.id.lblTitulo);
		lblTitulo.setText(listItems.get(position).titulo);
		
		TextView lblSubtitulo = (TextView)item.findViewById(R.id.lblSubTitulo);
		lblSubtitulo.setText(listItems.get(position).subTitulo);
		
		
		((ImageView)item.findViewById(R.id.iconListView)).setImageResource(icono);
		
		/*
		if (listItems.get(position).icono > 0)
			((ImageView)item.findViewById(R.id.iconListView)).setImageResource(listItems.get(position).icono);
		else if (icono > 0)
			((ImageView)item.findViewById(R.id.iconListView)).setImageResource(icono);
		*/
		return item;
    }
	
	@Override
	public ItemListView getItem(int position) {
		
		return listItems.get(position);
	}
}
