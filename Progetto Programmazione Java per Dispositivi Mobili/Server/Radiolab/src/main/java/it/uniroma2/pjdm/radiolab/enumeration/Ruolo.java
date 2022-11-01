package it.uniroma2.pjdm.radiolab.enumeration;

public enum Ruolo {
	AMMINISTRATIVO(1),
	PAZIENTE(2);
	
	private int ruolo;

	Ruolo(int ruolo){
		this.ruolo = ruolo;
	}

	public int getRuolo() {
		return ruolo;
	}
}
