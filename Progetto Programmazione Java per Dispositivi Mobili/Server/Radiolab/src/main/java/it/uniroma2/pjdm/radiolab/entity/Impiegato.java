package it.uniroma2.pjdm.radiolab.entity;

import java.sql.Date;

public class Impiegato {
	private int idImpiegato;
	private String nomeImpiegato;
	private String cognomeImpiegato;
	private String genere;
	private String codiceFiscale;
	private Date dataNascita;
	private String cittaNascita;
	private String telefono;
	private String indirizzo;
	private String cap;
	private String emailLavorativa;
	
	public Impiegato(int idImpiegato, String nomeImpiegato, String cognomeImpiegato, String genere,
			String codiceFiscale, Date dataNascita, String cittaNascita, String telefono, String indirizzo, String cap,
			String emailLavorativa) {
		this.idImpiegato = idImpiegato;
		this.nomeImpiegato = nomeImpiegato;
		this.cognomeImpiegato = cognomeImpiegato;
		this.genere = genere;
		this.codiceFiscale = codiceFiscale;
		this.dataNascita = dataNascita;
		this.cittaNascita = cittaNascita;
		this.telefono = telefono;
		this.indirizzo = indirizzo;
		this.cap = cap;
		this.emailLavorativa = emailLavorativa;
	}

	public int getIdImpiegato() {
		return idImpiegato;
	}

	public void setIdImpiegato(int idImpiegato) {
		this.idImpiegato = idImpiegato;
	}

	public String getNomeImpiegato() {
		return nomeImpiegato;
	}

	public void setNomeImpiegato(String nomeImpiegato) {
		this.nomeImpiegato = nomeImpiegato;
	}

	public String getCognomeImpiegato() {
		return cognomeImpiegato;
	}

	public void setCognomeImpiegato(String cognomeImpiegato) {
		this.cognomeImpiegato = cognomeImpiegato;
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

	public String getEmailLavorativa() {
		return emailLavorativa;
	}

	public void setEmailLavorativa(String emailLavorativa) {
		this.emailLavorativa = emailLavorativa;
	}

	@Override
	public String toString() {
		return "Impiegato [idImpiegato=" + idImpiegato + ", nomeImpiegato=" + nomeImpiegato + ", cognomeImpiegato="
				+ cognomeImpiegato + ", genere=" + genere + ", codiceFiscale=" + codiceFiscale + ", dataNascita="
				+ dataNascita + ", cittaNascita=" + cittaNascita + ", telefono=" + telefono + ", indirizzo=" + indirizzo
				+ ", cap=" + cap + ", emailLavorativa=" + emailLavorativa + "]";
	}
	
}
