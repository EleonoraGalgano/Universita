package it.uniroma2.pjdm.radiolab.entity;

public class Sala {
	private int idSala;
	private int numeroSala;
	private int codiceSede;
	
	public Sala(int idSala, int numeroSala, int codiceSede) {
		this.idSala = idSala;
		this.numeroSala = numeroSala;
		this.codiceSede = codiceSede;
	}

	public int getIdSala() {
		return idSala;
	}

	public void setIdSala(int idSala) {
		this.idSala = idSala;
	}

	public int getNumeroSala() {
		return numeroSala;
	}

	public void setNumeroSala(int numeroSala) {
		this.numeroSala = numeroSala;
	}

	public int getCodiceSede() {
		return codiceSede;
	}

	public void setCodiceSede(int codiceSede) {
		this.codiceSede = codiceSede;
	}

	@Override
	public String toString() {
		return "Sala [idSala=" + idSala + ", numeroSala=" + numeroSala + ", codiceSede=" + codiceSede + "]";
	}
	
}
