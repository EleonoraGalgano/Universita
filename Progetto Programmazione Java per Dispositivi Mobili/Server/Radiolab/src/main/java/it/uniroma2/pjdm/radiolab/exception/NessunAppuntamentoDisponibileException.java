package it.uniroma2.pjdm.radiolab.exception;

public class NessunAppuntamentoDisponibileException extends Exception {

	private static final long serialVersionUID = 5865389405200978431L;

	public NessunAppuntamentoDisponibileException() {
		super("Non esistono appuntamenti disponibili");
	}
	
}
