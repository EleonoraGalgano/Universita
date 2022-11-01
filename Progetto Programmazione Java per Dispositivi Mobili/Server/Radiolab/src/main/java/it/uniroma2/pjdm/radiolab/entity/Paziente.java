package it.uniroma2.pjdm.radiolab.entity;

import java.sql.Date;

public class Paziente {
	private String nomePaziente;
	private String cognomePaziente;
	private String genere;
	private String codiceFiscale;
	private Date dataNascita;
	private String cittaNascita;
	private String telefono;
	private String indirizzo;
	private String cap;
	private String email;

	public Paziente(String nomePaziente, String cognomePaziente, String genere, String codiceFiscale,
			Date dataNascita, String cittaNascita, String telefono, String indirizzo, String cap, String email) {
		this.nomePaziente = nomePaziente;
		this.cognomePaziente = cognomePaziente;
		this.genere = genere;
		this.codiceFiscale = codiceFiscale;
		this.dataNascita = dataNascita;
		this.cittaNascita = cittaNascita;
		this.telefono = telefono;
		this.indirizzo = indirizzo;
		this.cap = cap;
		this.email = email;
	}
	
	public String getNomePaziente() {
		return nomePaziente;
	}
	public void setNomePaziente(String nomePaziente) {
		this.nomePaziente = nomePaziente;
	}
	public String getCognomePaziente() {
		return cognomePaziente;
	}
	public void setCognomePaziente(String cognomePaziente) {
		this.cognomePaziente = cognomePaziente;
	}
	public String getGenere() {
		return genere;
	}
	public void setGenere(String genere) {
		this.genere = genere;
	}
	public String getCodiceFiscale() {
		return codiceFiscale;
	}
	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}
	public Date getDataNascita() {
		return dataNascita;
	}
	public void setDataNascita(Date dataNascita) {
		this.dataNascita = dataNascita;
	}
	public String getCittaNascita() {
		return cittaNascita;
	}
	public void setCittaNascita(String cittaNascita) {
		this.cittaNascita = cittaNascita;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getIndirizzo() {
		return indirizzo;
	}
	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}
	public String getCap() {
		return cap;
	}
	public void setCap(String cap) {
		this.cap = cap;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Override
	public String toString() {
		return "Paziente [ nomePaziente=" + nomePaziente + ", cognomePaziente="
				+ cognomePaziente + ", genere=" + genere + ", codiceFiscale=" + codiceFiscale + ", dataNascita="
				+ dataNascita + ", cittaNascita=" + cittaNascita + ", telefono=" + telefono + ", indirizzo=" + indirizzo
				+ ", cap=" + cap + ", email=" + email + "]";
	}
}
