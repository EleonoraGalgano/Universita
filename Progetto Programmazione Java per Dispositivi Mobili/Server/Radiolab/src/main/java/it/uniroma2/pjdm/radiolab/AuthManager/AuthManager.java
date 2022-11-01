package it.uniroma2.pjdm.radiolab.AuthManager;

import com.auth0.jwt.interfaces.DecodedJWT;

import it.uniroma2.pjdm.radiolab.exception.AutorizzazioneScadutaException;
import it.uniroma2.pjdm.radiolab.exception.HandlerException;
import it.uniroma2.pjdm.radiolab.exception.UtenteNonAutorizzatoException;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthManager {
	
	public String generaToken(int sub, int ruolo) throws HandlerException;
	
	public DecodedJWT validazioneToken(HttpServletRequest requests) throws UtenteNonAutorizzatoException, AutorizzazioneScadutaException;
	
	public void controlloRuolo(DecodedJWT jwt, int ruolo) throws UtenteNonAutorizzatoException;
	
	public int getSub(DecodedJWT jwt);
}
