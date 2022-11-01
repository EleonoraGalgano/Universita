package it.uniroma2.pjdm.radiolab.exception;

public class PrenotazioneInesistenteException extends Exception {
	
	private static final long serialVersionUID = -5426062668968256903L;

	public PrenotazioneInesistenteException() {
		super("Nessuna prenotazione corrisponde all'ID selezionato");
	}
}
