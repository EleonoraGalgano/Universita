package it.uniroma2.pjdm.radiolab.entity;

public class Prestazione {
	private String codicePrestazione;
	private String nomePrestazione;
	private float costo;
	
	public Prestazione(String codicePrestazione, String nomePrestazione, float costo) {
		this.codicePrestazione = codicePrestazione;
		this.nomePrestazione = nomePrestazione;
		this.costo = costo;
	}

	public String getCodicePrestazione() {
		return codicePrestazione;
	}

	public void setCodicePrestazione(String codicePrestazione) {
		this.codicePrestazione = codicePrestazione;
	}

	public String getNomePrestazione() {
		return nomePrestazione;
	}

	public void setNomePrestazione(String nomePrestazione) {
		this.nomePrestazione = nomePrestazione;
	}

	public float getCosto() {
		return costo;
	}

	public void setCosto(float costo) {
		this.costo = costo;
	}

	@Override
	public String toString() {
		return "Prestazione: " + nomePrestazione + ", prezzo: " + costo;
	}
	
}
