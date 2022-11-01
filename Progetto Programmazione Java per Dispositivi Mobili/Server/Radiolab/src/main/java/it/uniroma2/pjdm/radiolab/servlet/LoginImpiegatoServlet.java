package it.uniroma2.pjdm.radiolab.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import com.auth0.jwt.interfaces.DecodedJWT;

import it.uniroma2.pjdm.radiolab.AuthManager.AuthManager;
import it.uniroma2.pjdm.radiolab.AuthManager.AuthManagerImpl;
import it.uniroma2.pjdm.radiolab.dao.RadiolabDAO;
import it.uniroma2.pjdm.radiolab.dao.RadiolabDAOJDBCImpl;
import it.uniroma2.pjdm.radiolab.entity.Impiegato;
import it.uniroma2.pjdm.radiolab.entity.Login;
import it.uniroma2.pjdm.radiolab.entity.MessaggioRisposta;
import it.uniroma2.pjdm.radiolab.enumeration.Ruolo;
import it.uniroma2.pjdm.radiolab.exception.AutorizzazioneScadutaException;
import it.uniroma2.pjdm.radiolab.exception.DAOException;
import it.uniroma2.pjdm.radiolab.exception.HandlerException;
import it.uniroma2.pjdm.radiolab.exception.ImpiegatoInesistenteException;
import it.uniroma2.pjdm.radiolab.exception.UtenteNonAutorizzatoException;

/**
 * Servlet implementation class LoginImpiegatoServelet
 */
public class LoginImpiegatoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private RadiolabDAO dao;
	private AuthManager th;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginImpiegatoServlet() {
		super();
	}

	@Override
	public void init() throws ServletException {
		String ip = getServletContext().getInitParameter("ip");
		String port = getServletContext().getInitParameter("port");
		String dbName = getServletContext().getInitParameter("dbName");
		String userName = getServletContext().getInitParameter("userName");
		String password = getServletContext().getInitParameter("password");
		String sk = getServletContext().getInitParameter("secret");

		System.out.print("LoginImpiegatoServlet. Apertura connessione DB...");

		try {
			dao = new RadiolabDAOJDBCImpl(ip, port, dbName, userName, password);
			th = new AuthManagerImpl(sk);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		System.out.println("DONE.");
	}

	@Override
	public void destroy() {
		System.out.print("LoginImpiegatoServlet. Chiusura connessione DB...");
		try {
			dao.closeConnection();
		} catch (DAOException e) {
			e.printStackTrace();
		}
		System.out.println("DONE.");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("LoginImpiegato. Invocato metodo doGet");

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		JSONObject resJsonObject = null;

		if (request.getParameter("username") == null || request.getParameter("password") == null) {
			response.setStatus(400);
			resJsonObject = new JSONObject(new MessaggioRisposta("Credenziali invalide"));
			response.getWriter().append(resJsonObject.toString());
			return;
		}
		String un = request.getParameter("username");
		String pw = request.getParameter("password");

		int res;
		Login login = new Login(un, pw);
		try {
			res = dao.loginImpiegato(login);
		} catch (DAOException e) {
			e.printStackTrace();
			response.setStatus(401);
			resJsonObject = new JSONObject(new MessaggioRisposta(e.getMessage()));
			response.getWriter().append(resJsonObject.toString());
			return;
		}

		if (res > 0) {
			String token = null;
			try {
				token = th.generaToken(res, Ruolo.AMMINISTRATIVO.getRuolo());
			} catch (HandlerException e) {
				e.printStackTrace();
				response.setStatus(500);
				resJsonObject = new JSONObject(new MessaggioRisposta(e.getMessage()));
				response.getWriter().append(resJsonObject.toString());

			}
			response.setStatus(200);
			resJsonObject = new JSONObject(new MessaggioRisposta("Accesso eseguito"));
			try {
				resJsonObject.put("accessToken", token);
				response.getWriter().append(resJsonObject.toString());
			} catch (JSONException e) {
				response.setStatus(500);
				e.printStackTrace();
				resJsonObject = new JSONObject(new MessaggioRisposta("Errore interno"));
				response.getWriter().append(resJsonObject.toString());
			}

		} else {
			response.setStatus(404);
			resJsonObject = new JSONObject(new MessaggioRisposta("Accesso non riuscito"));
			response.getWriter().append(resJsonObject.toString());
		}
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("LoginImpiegato. Invocato metodo doPut");

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		JSONObject resJsonObject = null;
		
		DecodedJWT jwt = null;
		try {
			jwt = th.validazioneToken(request);
			th.controlloRuolo(jwt, Ruolo.AMMINISTRATIVO.getRuolo());
		} catch (UtenteNonAutorizzatoException | AutorizzazioneScadutaException e) {
			e.printStackTrace();
			response.setStatus(401);
			resJsonObject = new JSONObject(new MessaggioRisposta(e.getMessage()));
			response.getWriter().append(resJsonObject.toString());
			return;
		}

		if (request.getParameter("nuovaPassword") == null || request.getParameter("precedentePassword") == null) {
			response.setStatus(400);
			resJsonObject = new JSONObject(new MessaggioRisposta("Campo non compilato"));
			response.getWriter().append(resJsonObject.toString());
			return;
		}

		String precedentePassword = request.getParameter("precedentePassword");
		String nuovaPassword = request.getParameter("nuovaPassword");

		int idImpiegato = -1;
		try {
			idImpiegato = th.getSub(jwt);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			response.setStatus(400);
			resJsonObject = new JSONObject(new MessaggioRisposta("idImpiegato invalido"));
			response.getWriter().append(resJsonObject.toString());
			return;
		}
		Impiegato impiegato = dao.trovaImpiegatoConID(idImpiegato);

		int res = -1;
		try {
			res = dao.modificaPasswordImpiegato(impiegato, precedentePassword, nuovaPassword);
		} catch (ImpiegatoInesistenteException e) {
			e.printStackTrace();
			response.setStatus(404);
			resJsonObject = new JSONObject(new MessaggioRisposta(e.getMessage()));
			response.getWriter().append(resJsonObject.toString());
			return;

		} catch (DAOException e) {
			e.printStackTrace();
			response.setStatus(500);
			resJsonObject = new JSONObject(new MessaggioRisposta(e.getMessage()));
			response.getWriter().append(resJsonObject.toString());
			return;
		}

		if (res == 1) {
			response.setStatus(200);
			resJsonObject = new JSONObject(new MessaggioRisposta("Modifica avvenuta con successo"));
			response.getWriter().append(resJsonObject.toString());
		} else {
			response.setStatus(404);
			resJsonObject = new JSONObject(new MessaggioRisposta("Modifica non riuscita"));
			response.getWriter().append(resJsonObject.toString());
		}
	}

}
