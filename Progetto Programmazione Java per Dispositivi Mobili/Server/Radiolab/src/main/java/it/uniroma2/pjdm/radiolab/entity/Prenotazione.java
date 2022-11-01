package it.uniroma2.pjdm.radiolab.entity;

import java.sql.Date;
import java.sql.Time;

public class Prenotazione {
	private int idPrenotazione;
	private int idPaziente;
	private String codPres;
	private String nomePrestazione;
	private Date dataApp;
	private Time oraApp;
	private int idSala;
	private String nomeSede;
	private int statoPrenotazione;
	
	public Prenotazione(int idPrenotazione, int idPaziente, String codPres, String nomePrestazione, Date data, Time oraApp, int idSala, String nomeSede, int statoPrenotazione) {
		this.idPrenotazione = idPrenotazione;
		this.idPaziente = idPaziente;
		this.codPres = codPres;
		this.nomePrestazione = nomePrestazione;
		this.dataApp = data;
		this.oraApp = oraApp;
		this.idSala = idSala;
		this.nomeSede = nomeSede;
		this.statoPrenotazione = statoPrenotazione;
	}
	public int getIdPrenotazione() {
		return idPrenotazione;
	}
	public void setIdPrenotazione(int idPrenotazione) {
		this.idPrenotazione = idPrenotazione;
	}
	public int getIdPaziente() {
		return idPaziente;
	}
	public void setIdPaziente(int idPaziente) {
		this.idPaziente = idPaziente;
	}
	public String getCodPres() {
		return codPres;
	}
	public void setCodPres(String codPres) {
		this.codPres = codPres;
	}
	public Date getDataApp() {
		return dataApp;
	}
	public void setDataApp(Date dataApp) {
		this.dataApp = dataApp;
	}
	public Time getOraApp() {
		return oraApp;
	}
	public void setOraApp(Time oraApp) {
		this.oraApp = oraApp;
	}
	public int getIdSala() {
		return idSala;
	}
	public void setIdSala(int idSala) {
		this.idSala = idSala;
	}
	public int getStatoPrenotazione() {
		return statoPrenotazione;
	}
	public void setStatoPrenotazione(int statoPrenotazione) {
		this.statoPrenotazione = statoPrenotazione;
	}
	public String getNomePrestazione() {
		return nomePrestazione;
	}
	public void setNomePrestazione(String nomePrestazione) {
		this.nomePrestazione = nomePrestazione;
	}
	public String getNomeSede() {
		return nomeSede;
	}
	public void setNomeSede(String nomeSede) {
		this.nomeSede = nomeSede;
	}
	@Override
	public String toString() {
		return "Prenotazione [idPrenotazione=" + idPrenotazione + ", idPaziente=" + idPaziente + ", codPres=" + codPres
				+ ", nomePrestazione=" + nomePrestazione + ", dataApp=" + dataApp + ", oraApp=" + oraApp + ", idSala="
				+ idSala + ", nomeSede=" + nomeSede + ", statoPrenotazione=" + statoPrenotazione + "]";
	}
}
