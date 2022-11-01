package it.uniroma2.pjdm.radiolab.AuthManager;

import java.time.Instant;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import it.uniroma2.pjdm.radiolab.exception.AutorizzazioneScadutaException;
import it.uniroma2.pjdm.radiolab.exception.HandlerException;
import it.uniroma2.pjdm.radiolab.exception.UtenteNonAutorizzatoException;
import jakarta.servlet.http.HttpServletRequest;

public class AuthManagerImpl implements AuthManager {
	private Algorithm algorithm;
	private JWTVerifier verifier;
	private final String RUOLO = "ruolo";
	private final String SUB = "sub";

	public AuthManagerImpl(String sk) {
		this.algorithm = Algorithm.HMAC256(sk);
		this.verifier = JWT.require(this.algorithm).acceptExpiresAt(0).build();
	}

	public String generaToken(int sub, int ruolo) throws HandlerException {
		try {
			return JWT.create().withClaim(this.SUB, sub).withClaim(this.RUOLO, ruolo)
					.withExpiresAt(Instant.now().plusSeconds(1800)).sign(this.algorithm);
		} catch (JWTCreationException e) {
			e.printStackTrace();
			throw new HandlerException(e);
		}
	}

	public DecodedJWT validazioneToken(HttpServletRequest request) throws UtenteNonAutorizzatoException, AutorizzazioneScadutaException {
		String auth = request.getHeader("Authorization");
		if(!auth.matches("Bearer .+")) {
			throw new UtenteNonAutorizzatoException();
		}
		String token = auth.replaceAll("(Bearer)", "").trim();
		try {
			if (token != null) {
			return this.verifier.verify(token);
			}
			throw new UtenteNonAutorizzatoException();
		} catch (TokenExpiredException e) {
			e.printStackTrace();
			throw new AutorizzazioneScadutaException(e.getCause());
		} catch (JWTVerificationException e) {
			e.printStackTrace();
			throw new UtenteNonAutorizzatoException(e.getCause());
		}
	}

	public int getSub(DecodedJWT token) {
		Integer sub = token.getClaim(this.SUB).asInt();
		if(sub == null) {
			throw new NumberFormatException();
		}
		return sub;
	}

	public void controlloRuolo(DecodedJWT jwt, int ruolo) throws UtenteNonAutorizzatoException {
		if(jwt.getClaim(this.RUOLO).asInt() == null) {
			throw new NumberFormatException();
		}
		if (!jwt.getClaim(this.RUOLO).asInt().equals(ruolo)) {
			throw new UtenteNonAutorizzatoException();
		}
	}

}
