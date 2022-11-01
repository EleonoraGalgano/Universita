package it.uniroma2.pjdm.radiolab.servlet;

import org.json.JSONException;
import org.json.JSONObject;

import com.auth0.jwt.interfaces.DecodedJWT;

import it.uniroma2.pjdm.radiolab.AuthManager.AuthManager;
import it.uniroma2.pjdm.radiolab.AuthManager.AuthManagerImpl;
import it.uniroma2.pjdm.radiolab.dao.RadiolabDAO;
import it.uniroma2.pjdm.radiolab.dao.RadiolabDAOJDBCImpl;
import it.uniroma2.pjdm.radiolab.entity.Login;
import it.uniroma2.pjdm.radiolab.entity.MessaggioRisposta;
import it.uniroma2.pjdm.radiolab.entity.PazienteRegistrato;
import it.uniroma2.pjdm.radiolab.enumeration.Ruolo;
import it.uniroma2.pjdm.radiolab.exception.AutorizzazioneScadutaException;
import it.uniroma2.pjdm.radiolab.exception.DAOException;
import it.uniroma2.pjdm.radiolab.exception.HandlerException;
import it.uniroma2.pjdm.radiolab.exception.PazienteInesistenteException;
import it.uniroma2.pjdm.radiolab.exception.UtenteNonAutorizzatoException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Servlet implementation class LoginServlet
 */
public class LoginPazienteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private RadiolabDAO dao;
	private AuthManager am;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginPazienteServlet() {
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

		System.out.print("LoginPazienteServlet. Apertura connessione DB...");

		try {
			dao = new RadiolabDAOJDBCImpl(ip, port, dbName, userName, password);
			am = new AuthManagerImpl(sk);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		System.out.println("DONE.");
	}

	@Override
	public void destroy() {
		System.out.print("LoginPazienteServlet. Chiusura connessione DB...");
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
		System.out.println("LoginPazienteServlet. Invocato metodo doGet");

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		JSONObject resJsonObject = null;

		if (request.getParameter("codiceFiscale") == null || request.getParameter("password") == null) {
			response.setStatus(400);
			resJsonObject = new JSONObject(new MessaggioRisposta("Credenziali invalide"));
			response.getWriter().append(resJsonObject.toString());
			return;
		}
		String cf = request.getParameter("codiceFiscale");
		String pw = request.getParameter("password");

		int res;
		Login login = new Login(cf, pw);
		try {
			res = dao.loginPaziente(login);
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
				token = am.generaToken(res, Ruolo.PAZIENTE.getRuolo());
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
		System.out.println("LoginPazienteServlet. Invocato metodo doPut");

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		JSONObject resJsonObject = null;
		
		DecodedJWT jwt = null;
		try {
			jwt = am.validazioneToken(request);
			am.controlloRuolo(jwt, Ruolo.PAZIENTE.getRuolo());
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

		int idPaziente = -1;
		try {
			idPaziente = am.getSub(jwt);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			response.setStatus(400);
			resJsonObject = new JSONObject(new MessaggioRisposta("idPaziente invalido"));
			response.getWriter().append(resJsonObject.toString());
			return;
		}

		PazienteRegistrato paziente = dao.trovaPazienteConID(idPaziente);

		int res;
		try {
			res = dao.modificaPasswordPaziente(paziente, precedentePassword, nuovaPassword);
		} catch (PazienteInesistenteException e) {
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

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("LoginPazienteServlet. Invocato metodo doDelete");

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		JSONObject resJsonObject = null;
		DecodedJWT jwt = null;
		try {
			jwt = am.validazioneToken(request);
			am.controlloRuolo(jwt, Ruolo.PAZIENTE.getRuolo());
		} catch (UtenteNonAutorizzatoException | AutorizzazioneScadutaException e) {
			e.printStackTrace();
			response.setStatus(401);
			resJsonObject = new JSONObject(new MessaggioRisposta(e.getMessage()));
			response.getWriter().append(resJsonObject.toString());
			return;
		}
		
		int idPaziente = -1;
		try {
			idPaziente = am.getSub(jwt);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			response.setStatus(400);
			resJsonObject = new JSONObject(new MessaggioRisposta("idPaziente invalido"));
			response.getWriter().append(resJsonObject.toString());
			return;
		}

		int res;
		PazienteRegistrato paziente = dao.trovaPazienteConID(idPaziente);
		try {
			res = dao.cancellazioneUtente(paziente);
		} catch (DAOException e) {
			e.printStackTrace();
			response.setStatus(500);
			resJsonObject = new JSONObject(new MessaggioRisposta(e.getMessage()));
			response.getWriter().append(resJsonObject.toString());
			return;
		} catch (PazienteInesistenteException e) {
			e.printStackTrace();
			response.setStatus(404);
			resJsonObject = new JSONObject(new MessaggioRisposta(e.getMessage()));
			response.getWriter().append(resJsonObject.toString());
			return;
		}

		if (res == 1) {
			response.setStatus(200);
			resJsonObject = new JSONObject(new MessaggioRisposta("Cancellazione account avvenuta con successo"));
			response.getWriter().append(resJsonObject.toString());
		} else {
			resJsonObject = new JSONObject(new MessaggioRisposta("Cancellazione account non riuscita"));
			response.getWriter().append(resJsonObject.toString());
		}
	}
}
