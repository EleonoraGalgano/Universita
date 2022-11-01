package it.uniroma2.pjdm.radiolab.exception;

public class UtenteNonAutorizzatoException extends Exception {

	private static final long serialVersionUID = 9031214287488891189L;

	public UtenteNonAutorizzatoException(Throwable cause) {
		super("Utente non autorizzato",cause);
	}

	public UtenteNonAutorizzatoException() {
		super("Utente non autorizzato");
	}
	
	
}
