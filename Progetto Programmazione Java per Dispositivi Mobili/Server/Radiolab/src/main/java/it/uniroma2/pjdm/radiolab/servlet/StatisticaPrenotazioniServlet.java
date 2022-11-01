package it.uniroma2.pjdm.radiolab.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.auth0.jwt.interfaces.DecodedJWT;

import it.uniroma2.pjdm.radiolab.AuthManager.AuthManager;
import it.uniroma2.pjdm.radiolab.AuthManager.AuthManagerImpl;
import it.uniroma2.pjdm.radiolab.dao.RadiolabDAO;
import it.uniroma2.pjdm.radiolab.dao.RadiolabDAOJDBCImpl;
import it.uniroma2.pjdm.radiolab.entity.MessaggioRisposta;
import it.uniroma2.pjdm.radiolab.enumeration.Ruolo;
import it.uniroma2.pjdm.radiolab.exception.AutorizzazioneScadutaException;
import it.uniroma2.pjdm.radiolab.exception.DAOException;
import it.uniroma2.pjdm.radiolab.exception.UtenteNonAutorizzatoException;

/**
 * Servlet implementation class StatisticaPrenotazioniServlet
 */
public class StatisticaPrenotazioniServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private RadiolabDAO dao;
	private AuthManager am;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public StatisticaPrenotazioniServlet() {
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
		System.out.print("StatisticaPrenotazioniServlet. Apertura connessione DB...");

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
		System.out.print("StatisticaPrenotazioniServlet. Chiusura connessione DB...");
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
		System.out.println("StatisticaPrenotazioniServlet. Invocato metodo doGet");

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		JSONObject resJsonObject = null;

		try {
			DecodedJWT jwt= am.validazioneToken(request);
			am.controlloRuolo(jwt,Ruolo.AMMINISTRATIVO.getRuolo());
		} catch (UtenteNonAutorizzatoException | AutorizzazioneScadutaException e) {
			e.printStackTrace();
			response.setStatus(401);
			resJsonObject = new JSONObject(new MessaggioRisposta(e.getMessage()));
			response.getWriter().append(resJsonObject.toString());
			return;
		} 
		
		if (request.getParameter("statoPrenotazione") == null || request.getParameter("inizioPeriodo") == null
				|| request.getParameter("finePeriodo") == null || request.getParameter("raggruppamento") == null) {
			response.setStatus(400);
			resJsonObject = new JSONObject(new MessaggioRisposta("Parametri insufficienti per eseguire la statistica"));
			response.getWriter().append(resJsonObject.toString());
			return;
		}

		int statoPrenotazione = -1;

		Date inizioPeriodo = null;
		Date finePeriodo = null;
		String raggruppamento = null;
		try {
			statoPrenotazione = Integer.valueOf(request.getParameter("statoPrenotazione"));
			inizioPeriodo = Date.valueOf(request.getParameter("inizioPeriodo"));
			finePeriodo = Date.valueOf(request.getParameter("finePeriodo"));
			raggruppamento = request.getParameter("raggruppamento");

		} catch (NumberFormatException e) {
			e.printStackTrace();
			response.setStatus(400);
			resJsonObject = new JSONObject(new MessaggioRisposta("Stato prenotazione non riconosciuto"));
			response.getWriter().append(resJsonObject.toString());
			return;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			response.setStatus(400);
			resJsonObject = new JSONObject(new MessaggioRisposta("Formato data invalido"));
			response.getWriter().append(resJsonObject.toString());
			return;
		}
		HashMap<Integer, Integer> calcoli = null;
		calcoli = dao.calcolaStatistichePrenotazione(inizioPeriodo, finePeriodo, statoPrenotazione, raggruppamento);

		PrintWriter out = response.getWriter();

		JSONArray calcoliJsonArray = new JSONArray();

		for (Entry<Integer, Integer> set : calcoli.entrySet()) {
			JSONObject calcoliJsonObject = new JSONObject();
			try {
				calcoliJsonObject.put("raggruppamento", set.getKey());
				calcoliJsonObject.put("ammontare", set.getValue());
				calcoliJsonArray.put(calcoliJsonObject);
			} catch (JSONException e) {
				response.setStatus(500);
				e.printStackTrace();
			}
		}
		out.print(calcoliJsonArray);
		out.flush();
	}

}
