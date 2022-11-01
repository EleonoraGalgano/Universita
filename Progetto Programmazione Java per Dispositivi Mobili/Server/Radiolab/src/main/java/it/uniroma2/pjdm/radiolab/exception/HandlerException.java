package it.uniroma2.pjdm.radiolab.exception;

public class HandlerException extends Exception {

	private static final long serialVersionUID = 3515010855514559382L;

	public HandlerException(Throwable cause) {
		super("Errore nella creazione del Token: "+cause.getMessage(), cause);
	}
	
	
}
