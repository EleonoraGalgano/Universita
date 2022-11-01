package it.uniroma2.pjdm.radiolab.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.auth0.jwt.interfaces.DecodedJWT;

import it.uniroma2.pjdm.radiolab.AuthManager.AuthManager;
import it.uniroma2.pjdm.radiolab.AuthManager.AuthManagerImpl;
import it.uniroma2.pjdm.radiolab.dao.RadiolabDAO;
import it.uniroma2.pjdm.radiolab.dao.RadiolabDAOJDBCImpl;
import it.uniroma2.pjdm.radiolab.entity.MessaggioRisposta;
import it.uniroma2.pjdm.radiolab.entity.PazienteRegistrato;
import it.uniroma2.pjdm.radiolab.entity.Prenotazione;
import it.uniroma2.pjdm.radiolab.enumeration.Ruolo;
import it.uniroma2.pjdm.radiolab.exception.AutorizzazioneScadutaException;
import it.uniroma2.pjdm.radiolab.exception.DAOException;
import it.uniroma2.pjdm.radiolab.exception.PazienteInesistenteException;
import it.uniroma2.pjdm.radiolab.exception.UtenteNonAutorizzatoException;

/**
 * Servlet implementation class AppuntamentiServlet
 */
public class AppuntamentiServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private RadiolabDAO dao;
	private AuthManager am;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AppuntamentiServlet() {
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
		
		System.out.print("AppuntamentiServlet. Apertura connessione DB...");

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
		System.out.print("AppuntamentiServlet. Chiusura connessione DB...");
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
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("AppuntamentiServlet. Invocato metodo doGet");

		JSONObject resJsonObject = null;
		DecodedJWT jwt = null;
		try {
			jwt = am.validazioneToken(request);
			am.controlloRuolo(jwt, Ruolo.PAZIENTE.getRuolo());
		} catch(NumberFormatException e) {
			e.printStackTrace();
			response.setStatus(500);
			resJsonObject = new JSONObject(new MessaggioRisposta("Errore interno"));
			response.getWriter().append(resJsonObject.toString());
			return;
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
			resJsonObject = new JSONObject(new MessaggioRisposta("ID paziente invalido"));
			response.getWriter().append(resJsonObject.toString());
			return;
		}

		PazienteRegistrato paziente = dao.trovaPazienteConID(idPaziente);
		ArrayList<Prenotazione> listaPrenotazioni = new ArrayList<>();
		try {
			listaPrenotazioni = dao.trovaPrenotazioniPaziente(paziente);
			System.out.println(listaPrenotazioni);
		} catch (PazienteInesistenteException e) {
			e.printStackTrace();
			e.printStackTrace();
			response.setStatus(404);
			resJsonObject = new JSONObject(new MessaggioRisposta(e.getMessage()));
			response.getWriter().append(resJsonObject.toString());
			return;
		}

		PrintWriter out = response.getWriter();

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		if (listaPrenotazioni == null) {
			response.setStatus(404);
			resJsonObject = new JSONObject(new MessaggioRisposta("Nessuna prenotazione trovata"));
			response.getWriter().append(resJsonObject.toString());
		} else {
			JSONArray listaPrenotazioniJson = new JSONArray(listaPrenotazioni);
			out.print(listaPrenotazioniJson.toString());
		}
		out.flush();
	}

}
