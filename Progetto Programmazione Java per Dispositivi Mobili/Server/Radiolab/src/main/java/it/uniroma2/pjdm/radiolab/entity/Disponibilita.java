package it.uniroma2.pjdm.radiolab.entity;

import java.sql.Date;
import java.sql.Time;

public class Disponibilita {
	private Date data;
	private Time ora;
	private int idSala;
	private String nomeSede;
	
	public Disponibilita(Date date, Time time, int idSala, String nomeSede) {
		this.data = date;
		this.ora = time;
		this.idSala = idSala;
		this.nomeSede = nomeSede;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Time getOra() {
		return ora;
	}

	public void setOra(Time ora) {
		this.ora = ora;
	}

	public int getIdSala() {
		return idSala;
	}

	public void setIdSala(int idSala) {
		this.idSala = idSala;
	}

	public String getNomeSede() {
		return nomeSede;
	}

	public void setNomeSede(String nomeSede) {
		this.nomeSede = nomeSede;
	}

	@Override
	public String toString() {
		return "Data: " + data + ", ora: " + ora + ", idSala: " + idSala + ", nomeSede: " + nomeSede;
	}
	
}
