package it.uniroma2.pjdm.radiolab.exception;

public class ImpiegatoInesistenteException extends Exception {

	private static final long serialVersionUID = 839265239520145345L;
	
	public ImpiegatoInesistenteException() {
		super("L'ID selezionato non corrisponde a nessun impiegato");
	}
}
