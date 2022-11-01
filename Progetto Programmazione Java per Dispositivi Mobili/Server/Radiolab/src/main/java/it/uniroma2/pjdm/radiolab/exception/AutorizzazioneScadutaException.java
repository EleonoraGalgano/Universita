package it.uniroma2.pjdm.radiolab.exception;

public class AutorizzazioneScadutaException extends Exception {

	private static final long serialVersionUID = 6156208066414346208L;

	public AutorizzazioneScadutaException(Throwable cause) {
		super("Autorizzazione scaduta",cause);
	}
	
	
}
