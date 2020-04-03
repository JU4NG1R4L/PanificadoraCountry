package co.com.panificadoracountry;

public class ItemListBaseAdapter {

	private String descripcion;
	private String codigo;
	private String nit;
	private int idImageResource;

	private boolean isSelected = false;

	public ItemListBaseAdapter(String descripcion, String codigo, String nit,
			int idImageResource) {
		this.descripcion = descripcion;
		this.codigo = codigo;
		this.nit = nit;
		this.idImageResource = idImageResource;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNit() {
		return nit;
	}

	public void setNit(String nit) {
		this.nit = nit;
	}

	public int getIdImageResource() {
		return idImageResource;
	}

	public void setIdImageResource(int idImageResource) {
		this.idImageResource = idImageResource;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

}
