package it.uniroma2.pjdm.radiolab.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.auth0.jwt.interfaces.DecodedJWT;

import it.uniroma2.pjdm.radiolab.AuthManager.AuthManager;
import it.uniroma2.pjdm.radiolab.AuthManager.AuthManagerImpl;
import it.uniroma2.pjdm.radiolab.dao.RadiolabDAO;
import it.uniroma2.pjdm.radiolab.dao.RadiolabDAOJDBCImpl;
import it.uniroma2.pjdm.radiolab.entity.Disponibilita;
import it.uniroma2.pjdm.radiolab.entity.MessaggioRisposta;
import it.uniroma2.pjdm.radiolab.entity.PazienteRegistrato;
import it.uniroma2.pjdm.radiolab.entity.Prenotazione;
import it.uniroma2.pjdm.radiolab.entity.Prestazione;
import it.uniroma2.pjdm.radiolab.enumeration.Ruolo;
import it.uniroma2.pjdm.radiolab.exception.AutorizzazioneScadutaException;
import it.uniroma2.pjdm.radiolab.exception.DAOException;
import it.uniroma2.pjdm.radiolab.exception.NessunAppuntamentoDisponibileException;
import it.uniroma2.pjdm.radiolab.exception.PazienteInesistenteException;
import it.uniroma2.pjdm.radiolab.exception.PrenotazioneInesistenteException;
import it.uniroma2.pjdm.radiolab.exception.PrestazioneInesistenteException;
import it.uniroma2.pjdm.radiolab.exception.UtenteNonAutorizzatoException;

/**
 * Servlet implementation class GestioneAppuntamentiServlet
 */
public class GestionePrenotazioniServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private RadiolabDAO dao;
	private AuthManager am;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GestionePrenotazioniServlet() {
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

		System.out.print("GestioneAppuntamentiServlet. Apertura connessione DB...");

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
		System.out.print("GestioneAppuntamentiServlet. Chiusura connessione DB...");
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
		System.out.println("GestionePrenotazioniServlet. Invocato metodo doGet");

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

		if (request.getParameter("codicePrestazione") == null) {
			response.setStatus(400);
			resJsonObject = new JSONObject(new MessaggioRisposta("Occorre Specificare una prestazione"));
			response.getWriter().append(resJsonObject.toString());
			return;
		}
		String codicePrestazione = request.getParameter("codicePrestazione");
		Prestazione prestazione = dao.trovaPrestazioneColCodice(codicePrestazione);
		if (prestazione == null) {
			response.setStatus(404);
			resJsonObject = new JSONObject(new MessaggioRisposta("Nessuna corrispondenza trovata"));
			response.getWriter().append(resJsonObject.toString());
			return;
		}

		PrintWriter out = response.getWriter();

		ArrayList<Disponibilita> appuntamentiDisponibili;
		try {
			appuntamentiDisponibili = dao.visualizzaDisponibilita(prestazione);
		} catch (PrestazioneInesistenteException e) {
			e.printStackTrace();
			response.setStatus(500);
			resJsonObject = new JSONObject(new MessaggioRisposta(e.getMessage()));
			response.getWriter().append(resJsonObject.toString());
			return;
		}

		JSONArray appuntamentiDisponibiliJson = new JSONArray(appuntamentiDisponibili);

		out.print(appuntamentiDisponibiliJson.toString());
		out.flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("GestionePrenotazioniServlet. Invocato metodo doPost");

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
		
		if (request.getParameter("data") == null || request.getParameter("ora") == null
				|| request.getParameter("idSala") == null || request.getParameter("nomeSede") == null
				|| request.getParameter("codicePrestazione") == null) {
			response.setStatus(400);
			resJsonObject = new JSONObject(new MessaggioRisposta("Campo non compilato"));
			response.getWriter().append(resJsonObject.toString());
			return;
		}

		int idPaziente = -1;
		int idSala = -1;
		Date data = null;
		Time ora = null;
		String nomeSede = request.getParameter("nomeSede");
		String codicePrestazione = request.getParameter("codicePrestazione");

		try {
			idPaziente = am.getSub(jwt);
			idSala = Integer.valueOf(request.getParameter("idSala"));
			data = Date.valueOf(request.getParameter("data"));
			ora = Time.valueOf(request.getParameter("ora"));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			response.setStatus(400);
			resJsonObject = new JSONObject(new MessaggioRisposta("Dati inseriti invalidi"));
			response.getWriter().append(resJsonObject.toString());
			return;
		}

		PazienteRegistrato paziente = dao.trovaPazienteConID(idPaziente);
		Prestazione prestazione = dao.trovaPrestazioneColCodice(codicePrestazione);
		Disponibilita disponibilita = new Disponibilita(data, ora, idSala, nomeSede);
		int res = -1;

		try {
			res = dao.prenotaAppuntamento(paziente, prestazione, disponibilita);
		} catch (DAOException e) {
			e.printStackTrace();
			response.setStatus(500);
			resJsonObject = new JSONObject(new MessaggioRisposta(e.getMessage()));
			response.getWriter().append(resJsonObject.toString());
			return;
		} catch (PazienteInesistenteException | PrestazioneInesistenteException
				| NessunAppuntamentoDisponibileException e) {
			e.printStackTrace();
			response.setStatus(404);
			resJsonObject = new JSONObject(new MessaggioRisposta(e.getMessage()));
			response.getWriter().append(resJsonObject.toString());
			return;
		}

		if (res == 1) {
			response.setStatus(200);
			resJsonObject = new JSONObject(new MessaggioRisposta("Prenotazione avvenuta con successo"));
			response.getWriter().append(resJsonObject.toString());
		} else {
			response.setStatus(404);
			resJsonObject = new JSONObject(new MessaggioRisposta("Prenotazione non riuscita"));
			response.getWriter().append(resJsonObject.toString());
		}
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("GestionePrenotazioniServlet. Invocato metodo doDelete");

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
		
		if (request.getParameter("idPrenotazione") == null) {
			response.setStatus(400);
			resJsonObject = new JSONObject(new MessaggioRisposta("Nessun appuntamento selezionato"));
			response.getWriter().append(resJsonObject.toString());
			return;
		}
		int idPaziente = -1;
		int idPrenotazione = -1;
		try {
			idPaziente = am.getSub(jwt);
			idPrenotazione = Integer.valueOf(request.getParameter("idPrenotazione"));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			response.setStatus(400);
			resJsonObject = new JSONObject(new MessaggioRisposta("Dati inseriti invalidi"));
			response.getWriter().append(resJsonObject.toString());
			return;
		}
		
		PazienteRegistrato paziente = dao.trovaPazienteConID(idPaziente);
		Prenotazione prenotazione = dao.trovaPrenotazioneConID(idPrenotazione);

		int res;
		try {
			res = dao.cancellaAppuntamento(paziente, prenotazione);
		} catch (PrenotazioneInesistenteException | PazienteInesistenteException e) {
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
			resJsonObject = new JSONObject(new MessaggioRisposta("Cancellazione appuntamento avvenuta con successo"));
			response.getWriter().append(resJsonObject.toString());
		} else {
			response.setStatus(404);
			resJsonObject = new JSONObject(new MessaggioRisposta("Operazione non riuscita"));
			response.getWriter().append(resJsonObject.toString());
		}
	}

}
