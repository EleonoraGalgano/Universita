package it.uniroma2.pjdm.radiolab.entity;

public class Sede {
	private int codiceSede; 
	private String nomeSede; 
	private String indirizzoSede; 
	private String cap; 
	private String telefono; 
	private String email; 
	private String orariApertura;
	
	public Sede(int codiceSede, String nomeSede, String indirizzoSede, String cap, String telefono, String email,
			String orariApertura) {
		super();
		this.codiceSede = codiceSede;
		this.nomeSede = nomeSede;
		this.indirizzoSede = indirizzoSede;
		this.cap = cap;
		this.telefono = telefono;
		this.email = email;
		this.orariApertura = orariApertura;
	}
	
	public int getCodiceSede() {
		return codiceSede;
	}
	public void setCodiceSede(int codiceSede) {
		this.codiceSede = codiceSede;
	}
	public String getNomeSede() {
		return nomeSede;
	}
	public void setNomeSede(String nomeSede) {
		this.nomeSede = nomeSede;
	}
	public String getIndirizzoSede() {
		return indirizzoSede;
	}
	public void setIndirizzoSede(String indirizzoSede) {
		this.indirizzoSede = indirizzoSede;
	}
	public String getCap() {
		return cap;
	}
	public void setCap(String cap) {
		this.cap = cap;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getOrariApertura() {
		return orariApertura;
	}
	public void setOrariApertura(String orariApertura) {
		this.orariApertura = orariApertura;
	}

	@Override
	public String toString() {
		return "Sede [codiceSede=" + codiceSede + ", nomeSede=" + nomeSede + ", indirizzoSede=" + indirizzoSede
				+ ", cap=" + cap + ", telefono=" + telefono + ", email=" + email + ", orariApertura=" + orariApertura
				+ "]";
	}
}
