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
import it.uniroma2.pjdm.radiolab.entity.Sede;
import it.uniroma2.pjdm.radiolab.enumeration.Ruolo;
import it.uniroma2.pjdm.radiolab.exception.AutorizzazioneScadutaException;
import it.uniroma2.pjdm.radiolab.exception.DAOException;
import it.uniroma2.pjdm.radiolab.exception.UtenteNonAutorizzatoException;

/**
 * Servlet implementation class InfoSediServlet
 */
public class InfoSediServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private RadiolabDAO dao;
	private AuthManager am;
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public InfoSediServlet() {
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
		
		System.out.print("InfoSediServlet. Apertura connessione DB...");

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
		System.out.print("InfoSediServlet. Chiusura connessione DB...");
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
		System.out.println("InfoSediServlet. Invocato metodo doGet");
		
		JSONObject resJsonObject = null;
		
		try {
			DecodedJWT jwt = am.validazioneToken(request);
			am.controlloRuolo(jwt, Ruolo.PAZIENTE.getRuolo());
		} catch (UtenteNonAutorizzatoException | AutorizzazioneScadutaException e) {
			e.printStackTrace();
			response.setStatus(401);
			resJsonObject = new JSONObject(new MessaggioRisposta(e.getMessage()));
			response.getWriter().append(resJsonObject.toString());
			return;
		}
		
		ArrayList<Sede> listaSedi = dao.visualizzaListaSedi();

		PrintWriter out = response.getWriter();

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		JSONArray listaSediJson = new JSONArray(listaSedi);

		out.print(listaSediJson.toString());
		out.flush();
	}
}