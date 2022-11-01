package it.uniroma2.pjdm.radiolab.exception;

public class PrestazioneInesistenteException extends Exception{

	private static final long serialVersionUID = 3519333001002575411L;
	
	public PrestazioneInesistenteException() {
		super("Non esistono prestazioni associate al codice inserito");
	}
}
