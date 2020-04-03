package co.com.panificadoracountry;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import co.com.DataObject.ItemListView;

public class ListViewAdapterCheckBox extends ArrayAdapter<ItemListView> {
	
	Activity context;
	final ItemListView[] listItems;
	boolean[] isChecked ;
	
	TextView lblTitulo;
	TextView lblSubTitulo;
	CheckBox chkbox;
	Button btn;
			
	int icono;
	//protected int cant = 0;
	
	ListViewAdapterCheckBox(Activity context, ItemListView[] listItems, int icono) {
		
		super(context, R.layout.list_item_checkbox, listItems);
		this.listItems = listItems; 
		this.context = context;
		this.icono = icono;
		isChecked = new boolean[listItems.length];
		
	}
	
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		View item = convertView;
		final ViewHolder viewHolder;
		
		
		if (item == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			item = inflater.inflate(R.layout.list_item_checkbox, null);
			
			lblTitulo = (TextView)item.findViewById(R.id.lblTitulo);
			lblSubTitulo = (TextView)item.findViewById(R.id.lblSubTitulo);
			chkbox = (CheckBox)item.findViewById(R.id.checkList);
			
			viewHolder = new ViewHolder();
			viewHolder.lblTitulo = lblTitulo;
			viewHolder.lblSubTitulo = lblSubTitulo;
			viewHolder.checkBox = chkbox;
			
			item.setTag(viewHolder);
			
		}else{
			
			viewHolder = (ViewHolder) item.getTag();
		}
		
		viewHolder.lblTitulo.setText(listItems[position].titulo);
		viewHolder.lblSubTitulo.setText(listItems[position].subTitulo);
		
		viewHolder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			  @Override
			  public void onCheckedChanged(CompoundButton buttonView, boolean checked) {
//				  isChecked[position] = checked;
//				  listItems[position].isChecked = isChecked[position];
				  listItems[position].isChecked = checked;
				 
			  }
			});

		viewHolder.checkBox.setChecked(listItems[position].isChecked);
		viewHolder.checkBox.setEnabled(listItems[position].isEnable);
		
//		viewHolder.checkBox.setChecked(isChecked[position]);
		
		
		
		if (listItems[position].icono > 0)
			((ImageView)item.findViewById(R.id.iconListView)).setImageResource(listItems[position].icono);
		else if (icono > 0)
			((ImageView)item.findViewById(R.id.iconListView)).setImageResource(icono);

			
		
		return item;
    }
	

	@Override
	public ItemListView getItem(int position) {
		
		return listItems[position];
	}
	
	public ItemListView[] getList() {
		
		return listItems;
	}

	// clase ViewHolder
	private static class ViewHolder {

		public CheckBox checkBox = null;
		public TextView lblTitulo = null;
		public TextView lblSubTitulo = null;
		public int position;
	}// end clase ViewHolder
		
}


