package it.uniroma2.pjdm.radiolab.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.SQLException;
import org.json.JSONObject;

import com.auth0.jwt.interfaces.DecodedJWT;

import it.uniroma2.pjdm.radiolab.AuthManager.AuthManager;
import it.uniroma2.pjdm.radiolab.AuthManager.AuthManagerImpl;
import it.uniroma2.pjdm.radiolab.dao.RadiolabDAO;
import it.uniroma2.pjdm.radiolab.dao.RadiolabDAOJDBCImpl;
import it.uniroma2.pjdm.radiolab.entity.MessaggioRisposta;
import it.uniroma2.pjdm.radiolab.entity.Paziente;
import it.uniroma2.pjdm.radiolab.entity.PazienteRegistrato;
import it.uniroma2.pjdm.radiolab.enumeration.Ruolo;
import it.uniroma2.pjdm.radiolab.exception.AutorizzazioneScadutaException;
import it.uniroma2.pjdm.radiolab.exception.DAOException;
import it.uniroma2.pjdm.radiolab.exception.PazienteInesistenteException;
import it.uniroma2.pjdm.radiolab.exception.UtenteNonAutorizzatoException;

/**
 * Servlet implementation class PazienteServlet
 */
public class PazienteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private RadiolabDAO dao;
	private AuthManager am;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PazienteServlet() {
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

		System.out.print("PazienteServlet. Apertura Connessione DB...");

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
		System.out.print("PazienteServlet. Chiusura connessione DB...");
		try {
			dao.closeConnection();
		} catch (DAOException e) {
			e.printStackTrace();
		}
		System.out.println("DONE.");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("PazienteServlet. Invocato metodo doGet");

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

		PazienteRegistrato paziente = dao.trovaPazienteConID(idPaziente);

		PrintWriter out = response.getWriter();

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		JSONObject pazienteJson = null;

		pazienteJson = new JSONObject(paziente);

		out.print(pazienteJson.toString());
		out.flush();

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("PazienteServlet. Invocato metodo doPost");

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		JSONObject resJsonObject = null;

		if (request.getParameter("nomePaziente") == null || request.getParameter("cognomePaziente") == null
				|| request.getParameter("genere") == null || request.getParameter("codiceFiscale") == null
				|| request.getParameter("dataNascita") == null || request.getParameter("cittaNascita") == null
				|| request.getParameter("telefono") == null || request.getParameter("indirizzo") == null
				|| request.getParameter("cap") == null || request.getParameter("email") == null
				|| request.getParameter("password") == null) {
			response.setStatus(400);
			resJsonObject = new JSONObject(new MessaggioRisposta("Campo non compilato"));
			response.getWriter().append(resJsonObject.toString());
			return;
		}

		Paziente paziente = null;
		String password = null;
		String nomePaziente = request.getParameter("nomePaziente");
		String cognomePaziente = request.getParameter("cognomePaziente");
		String genere = request.getParameter("genere");
		String codiceFiscale = request.getParameter("codiceFiscale");
		String cittaNascita = request.getParameter("cittaNascita");
		String telefono = request.getParameter("telefono");
		String indirizzo = request.getParameter("indirizzo");
		String cap = request.getParameter("cap");
		String email = request.getParameter("email");
		password = request.getParameter("password");
		try {
			Date dataNascita = Date.valueOf(request.getParameter("dataNascita"));
			paziente = new Paziente(nomePaziente, cognomePaziente, genere, codiceFiscale, dataNascita, cittaNascita,
					telefono, indirizzo, cap, email);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			response.setStatus(400);
			resJsonObject = new JSONObject(new MessaggioRisposta("Data di nascita non valida"));
			response.getWriter().append(resJsonObject.toString());
			return;
		}

		int res = -1;

		try {
			res = dao.registrazione(paziente, password);
		} catch (DAOException e) {
			e.printStackTrace();
			response.setStatus(500);
			resJsonObject = new JSONObject(new MessaggioRisposta(e.getMessage()));
			response.getWriter().append(resJsonObject.toString());
			return;
		}

		if (res == 1) {
			response.setStatus(200);
			resJsonObject = new JSONObject(new MessaggioRisposta("Registrazione avvenuta con successo"));
			response.getWriter().append(resJsonObject.toString());

		} else {
			response.setStatus(404);
			resJsonObject = new JSONObject(new MessaggioRisposta("Operazione non riuscita"));
			response.getWriter().append(resJsonObject.toString());
		}

	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("PazienteServlet. Invocato metodo doPut");

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

		if (request.getParameter("parametroDaModificare") == null || request.getParameter("nuovoValore") == null) {
			response.setStatus(400);
			resJsonObject = new JSONObject(new MessaggioRisposta("Campo non compilato"));
			response.getWriter().append(resJsonObject.toString());
			return;
		}

		String parametro = request.getParameter("parametroDaModificare");
		String nuovoValore = request.getParameter("nuovoValore");
		
		int idPaziente = -1;

		try {
			idPaziente = am.getSub(jwt);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			resJsonObject = new JSONObject(new MessaggioRisposta("idPaziente invalido"));
			response.getWriter().append(resJsonObject.toString());
			return;
		}

		PazienteRegistrato paziente = dao.trovaPazienteConID(idPaziente);

		int res;

		try {
			res = dao.modificaPaziente(paziente, parametro, nuovoValore);
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
			resJsonObject = new JSONObject(new MessaggioRisposta("Modifica effettuata con successo"));
			response.getWriter().append(resJsonObject.toString());
		} else {
			response.setStatus(404);
			resJsonObject = new JSONObject(new MessaggioRisposta("Operazione non riuscita"));
			response.getWriter().append(resJsonObject.toString());
		}
	}
}
