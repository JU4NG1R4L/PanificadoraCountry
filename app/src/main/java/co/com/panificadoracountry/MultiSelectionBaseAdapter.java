package co.com.panificadoracountry;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class MultiSelectionBaseAdapter extends BaseAdapter {

	private List<ItemListBaseAdapter> lista = Collections.emptyList();

	private final Context context;

	private boolean multiple = false;

	public MultiSelectionBaseAdapter(Context context, List<ItemListBaseAdapter> lista,
			boolean multiple) {
		this.context = context;
		this.multiple = multiple;

		actualizaLista(lista);
	}

	public void actualizaLista(List<ItemListBaseAdapter> lista) {

		ThreadPreconditions.checkOnMainThread();

		this.lista = lista;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {

		return lista.size();
	}

	@Override
	public ItemListBaseAdapter getItem(int position) {

		return lista.get(position);
	}

	public void setSelected(int position) {

		ThreadPreconditions.checkOnMainThread();

		boolean state = lista.get(position).isSelected();

		lista.get(position).setSelected(!state);

		System.out.println("puso : " + !state);

		notifyDataSetChanged();
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	public Vector<ItemListBaseAdapter> getSelectedItems() {
		
		Vector<ItemListBaseAdapter> lista = new Vector<ItemListBaseAdapter>();

		for (ItemListBaseAdapter item : this.lista) {
			if (item.isSelected())
				lista.add(item);
		}

		return lista;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ImageView imageView;
		TextView textView;
		CheckBox checkView = null;

		int idLayout = R.layout.list_item_pdv;

		// si estamos en la seleccion del PDV usamos la seleccion multiple
		if (multiple)
			idLayout = R.layout.list_item_pdv_multiple;

		if (convertView == null) {

			convertView = LayoutInflater.from(context).inflate(idLayout, parent, false);

			imageView = (ImageView) convertView.findViewById(R.id.iconListView);
			textView = (TextView) convertView.findViewById(R.id.lblTitulo);

			if (multiple) {
				checkView = (CheckBox) convertView.findViewById(R.id.checkView);
			}

			convertView.setTag(new ViewHolder(imageView, textView, checkView));
		} else {

			ViewHolder viewHolder = (ViewHolder) convertView.getTag();

			imageView = viewHolder.imageView;
			textView = viewHolder.textView;

			if (multiple) {
				checkView = viewHolder.checkView;
			}
		}

		ItemListBaseAdapter elemento = getItem(position);

		textView.setText(elemento.getCodigo() + " - " + elemento.getDescripcion());
		imageView.setImageResource(elemento.getIdImageResource());

		if (multiple) {
			checkView.setChecked(lista.get(position).isSelected());
		}

		return convertView;
	}

	@SuppressWarnings("unused")
	private static class ViewHolder {

		public final ImageView imageView;
		public final TextView textView;
		public final CheckBox checkView;

		public ViewHolder(ImageView imageView, TextView textView,
				CheckBox checkView) {
			this.imageView = imageView;
			this.textView = textView;
			this.checkView = checkView;
		}
	}

}
