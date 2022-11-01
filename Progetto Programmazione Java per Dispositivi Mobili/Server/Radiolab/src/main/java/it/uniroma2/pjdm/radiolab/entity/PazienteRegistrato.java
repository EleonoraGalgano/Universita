package it.uniroma2.pjdm.radiolab.entity;

import java.sql.Date;

public class PazienteRegistrato extends Paziente{
	private int idPaziente;

	public PazienteRegistrato(int idPaziente, String nomePaziente, String cognomePaziente, String genere, String codiceFiscale,
			Date dataNascita, String cittaNascita, String telefono, String indirizzo, String cap, String email) {
		super(nomePaziente, cognomePaziente, genere, codiceFiscale, dataNascita, cittaNascita, telefono, indirizzo, cap,
				email);
		this.idPaziente = idPaziente;
	}

	public int getIdPaziente() {
		return idPaziente;
	}
	public void setIdPaziente(int idPaziente) {
		this.idPaziente = idPaziente;
	}

	@Override
	public String toString() {
		return "PazienteRegistrato [idPaziente= " + idPaziente + ", nomePaziente= "+super.getNomePaziente()
				+", cognomePaziente="+super.getCognomePaziente()+", genere="+super.getGenere()+", codiceFiscale ="+super.getCodiceFiscale()
				+", dataNascita="+super.getDataNascita()+", cittaNascita="+ super.getCittaNascita()
				+", telefono="+super.getTelefono()+", indirizzo="+super.getIndirizzo()
				+", cap="+super.getCap()+",  email= "+super.getEmail()+"]";
	}
	
	
}
