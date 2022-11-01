package it.uniroma2.pjdm.radiolab.exception;

public class PazienteInesistenteException extends Exception{

	private static final long serialVersionUID = 7936450046469068284L;

	public PazienteInesistenteException() {
		super("L'ID selezionato non appartiene ad alcun paziente");
	}
	
}
