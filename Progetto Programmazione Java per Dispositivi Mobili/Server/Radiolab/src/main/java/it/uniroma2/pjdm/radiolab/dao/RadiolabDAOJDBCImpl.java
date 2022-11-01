package it.uniroma2.pjdm.radiolab.dao;

import java.sql.Statement;
import java.sql.Time;
import java.sql.Types;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TimeZone;

import it.uniroma2.pjdm.radiolab.entity.Disponibilita;
import it.uniroma2.pjdm.radiolab.entity.Impiegato;
import it.uniroma2.pjdm.radiolab.entity.Login;
import it.uniroma2.pjdm.radiolab.entity.Paziente;
import it.uniroma2.pjdm.radiolab.entity.PazienteRegistrato;
import it.uniroma2.pjdm.radiolab.entity.Prenotazione;
import it.uniroma2.pjdm.radiolab.entity.Prestazione;
import it.uniroma2.pjdm.radiolab.entity.Sala;
import it.uniroma2.pjdm.radiolab.entity.Sede;
import it.uniroma2.pjdm.radiolab.exception.DAOException;
import it.uniroma2.pjdm.radiolab.exception.ImpiegatoInesistenteException;
import it.uniroma2.pjdm.radiolab.exception.NessunAppuntamentoDisponibileException;
import it.uniroma2.pjdm.radiolab.exception.PazienteInesistenteException;
import it.uniroma2.pjdm.radiolab.exception.PrenotazioneInesistenteException;
import it.uniroma2.pjdm.radiolab.exception.PrestazioneInesistenteException;

public class RadiolabDAOJDBCImpl implements RadiolabDAO {

	private Connection conn;

	public RadiolabDAOJDBCImpl(String ip, String port, String dbName, String userName, String pwd)
			throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");

		conn = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + dbName
				+ "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
				userName, pwd);
	}

	@Override
	public int registrazione(Paziente paziente, String pw) throws DAOException {
		String procedure = "{call registrazione(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)}";
		try (CallableStatement stmt = conn.prepareCall(procedure)) {
			stmt.setString(1, paziente.getNomePaziente());
			stmt.setString(2, paziente.getCognomePaziente());
			stmt.setString(3, paziente.getGenere());
			stmt.setString(4, paziente.getCodiceFiscale());
			stmt.setDate(5, paziente.getDataNascita(), Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome")));
			stmt.setString(6, paziente.getCittaNascita());
			stmt.setString(7, paziente.getTelefono());
			stmt.setString(8, paziente.getIndirizzo());
			stmt.setString(9, paziente.getCap());
			stmt.setString(10, paziente.getEmail());
			stmt.setString(11, pw);
			stmt.registerOutParameter(12, Types.INTEGER);
			stmt.executeQuery();
			return stmt.getInt(12);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public int loginPaziente(Login login) throws DAOException {
		String procedure = "{call loginPaziente(?, ?, ?)}";
		try (CallableStatement stmt = conn.prepareCall(procedure)){
			stmt.setString(1, login.getUsername());
			stmt.setString(2, login.getPassword());
			stmt.registerOutParameter(3, Types.INTEGER);
			stmt.executeQuery();
			return stmt.getInt(3);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}

	}

	@Override
	public int loginImpiegato(Login login) throws DAOException {
		String procedure = "{call loginImpiegato(?, ?, ?)}";
		try (CallableStatement stmt = conn.prepareCall(procedure)) {
			stmt.setString(1, login.getUsername());
			stmt.setString(2, login.getPassword());
			stmt.registerOutParameter(3, Types.INTEGER);
			stmt.executeQuery();
			return stmt.getInt(3);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public ArrayList<Sede> visualizzaListaSedi() {
		String query = "SELECT codiceSede, nomeSede, indirizzoSede, cap, telefono, email, orariApertura FROM Sede;";
		ArrayList<Sede> listaSedi = new ArrayList<Sede>();
		try (Statement stmt = conn.createStatement(); ResultSet rset = stmt.executeQuery(query)) {
			while (rset.next()) {
				int codiceSede = rset.getInt(1);
				String nomeSede = rset.getString(2);
				String indirizzoSede = rset.getString(3);
				String cap = rset.getString(4);
				String telefono = rset.getString(5);
				String email = rset.getString(6);
				String orariApertura = rset.getString(7);
				Sede sede = new Sede(codiceSede, nomeSede, indirizzoSede, cap, telefono, email, orariApertura);
				listaSedi.add(sede);
			}
			return listaSedi;

		} catch (SQLException e) {

			e.printStackTrace();

			return null;
		}

	}

	@Override
	public ArrayList<Prestazione> visualizzaPrestazioni() {
		String query = "SELECT nomePrestazione, codicePrestazione, costo FROM Prestazione order by nomePrestazione";
		ArrayList<Prestazione> prestazioni = new ArrayList<>();
		try (Statement stmt = conn.createStatement(); ResultSet rset = stmt.executeQuery(query)) {
			while (rset.next()) {
				String codicePrestazione = rset.getString(2);
				String nomePrestazione = rset.getString(1);
				float costo = rset.getFloat(3);
				Prestazione p = new Prestazione(codicePrestazione, nomePrestazione, costo);
				prestazioni.add(p);
			}
			return prestazioni;

		} catch (SQLException e) {

			e.printStackTrace();

			return null;
		}
	}

	@Override
	public ArrayList<Disponibilita> visualizzaDisponibilita(Prestazione prestazione)
			throws PrestazioneInesistenteException {

		if (prestazione == null) {
			throw new PrestazioneInesistenteException();
		}

		String query = "select str_to_date(CONCAT(anno.anno,'-',LPAD(mese.mese,2,'00'),'-',LPAD(giorno.giorno,2,'00')),'%Y-%m-%d') as data,\r\n"
				+ "	orario.Ora, salaP.idSala, salap.nomeSede from \n"
				+ " Anno inner join Mese inner join Giorno inner join Orario inner join\r\n"
				+ " (select sala.idSala, sede.codiceSede, sede.nomeSede from Sede,Sala, Eseguibile where sala.codiceSede = sede.codiceSede\r\n"
				+ " and sala.idSala = eseguibile.idSala and codicePrestazione = '" + prestazione.getCodicePrestazione()
				+ "') as salaP\r\n"
				+ " left join (select idPrenotazione, anno, mese, giorno, ora, idSala from Prenotazione ignore index (FKanno,FKmese,FKgiorno,FKora)) as agenda\r\n"
				+ " on agenda.anno = anno.anno\r\n" + " and agenda.mese = mese.mese \r\n"
				+ " and agenda.giorno = giorno.giorno\r\n" + " and agenda.ora = orario.ora \r\n"
				+ " and agenda.idSala = salaP.idSala \r\n"
				+ " where timestamp(str_to_date(CONCAT(anno.anno,'-',LPAD(mese.mese,2,'00'),'-',LPAD(giorno.giorno,2,'00')),'%Y-%m-%d'), orario.Ora) >= current_timestamp() \r\n"
				+ " AND Festivo( giorno.giorno, mese.mese, anno.anno) = 0 \r\n"
				+ " and agenda.idPrenotazione is null order by anno.anno, mese.mese, giorno.giorno, orario.ora, salap.idSala;";
		ArrayList<Disponibilita> appuntamentiDisponibili = new ArrayList<>();
		try (Statement stmt = conn.createStatement(); ResultSet rset = stmt.executeQuery(query)) {
			while (rset.next()) {
				Date data = rset.getDate(1, Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome")));
				Time ora = rset.getTime(2, Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome")));
				Disponibilita appuntamentoDisponibile = new Disponibilita(data, ora, rset.getInt(3), rset.getString(4));
				appuntamentiDisponibili.add(appuntamentoDisponibile);
			}
			return appuntamentiDisponibili;

		} catch (SQLException e) {

			e.printStackTrace();

			return null;
		}
	}

	@Override
	public HashMap<Integer, Integer> calcolaStatistichePrenotazione(Date inizioPeriodo, Date finePeriodo,
			int statoPrenotazione, String raggruppamento) {
		String query = "Select " + raggruppamento + ", count(prenotazione.codicePrestazione) "
				+ "from prenotazione, prestazione "
				+ "where prenotazione.codicePrestazione = prestazione.codicePrestazione "
				+ "and prenotazione.statoPrenotazione = " + statoPrenotazione
				+ " and str_to_date(CONCAT(anno,'-',LPAD(mese,2,'00'),'-',LPAD(giorno,2,'00')),'%Y-%m-%d') between '"
				+ inizioPeriodo + "' and '" + finePeriodo + "' " + "group by " + raggruppamento + " order by "
				+ raggruppamento + ";";
		HashMap<Integer, Integer> statisticaPrenotazioni = new HashMap<>();
		try (Statement stmt = conn.createStatement(); ResultSet rset = stmt.executeQuery(query)) {
			while (rset.next()) {
				int raggruppamentoValore = rset.getInt(1);
				int countPrestazione = rset.getInt(2);
				statisticaPrenotazioni.put(raggruppamentoValore, countPrestazione);
			}
			return statisticaPrenotazioni;

		} catch (SQLException e) {

			e.printStackTrace();

			return null;
		}
	}

	@Override
	public int prenotaAppuntamento(PazienteRegistrato paziente, Prestazione prestazione, Disponibilita disponibilita)
			throws DAOException, PazienteInesistenteException, PrestazioneInesistenteException,
			NessunAppuntamentoDisponibileException {

		if (paziente == null) {
			throw new PazienteInesistenteException();
		} else if (prestazione == null) {
			throw new PrestazioneInesistenteException();
		} else if (disponibilita == null) {
			throw new NessunAppuntamentoDisponibileException();
		}
		String procedure = "{call prenotaAppuntamento(?,?,?,?,?,?)}";
		try (CallableStatement stmt = conn.prepareCall(procedure)) {
			stmt.setInt(1, paziente.getIdPaziente());
			stmt.setString(2, prestazione.getCodicePrestazione());
			stmt.setDate(3, disponibilita.getData(), Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome")));
			stmt.setTime(4, disponibilita.getOra(), Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome")));
			stmt.setInt(5, disponibilita.getIdSala());
			stmt.registerOutParameter(6, Types.INTEGER);
			stmt.executeQuery();
			return stmt.getInt(6);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public ArrayList<Prenotazione> trovaPrenotazioniPaziente(PazienteRegistrato paziente)
			throws PazienteInesistenteException {
		if (paziente == null) {
			throw new PazienteInesistenteException();
		}
		String query = "SELECT idPrenotazione, Prenotazione.codicePrestazione, nomePrestazione, str_to_date(CONCAT(anno,'-',LPAD(mese,2,'00'),'-',LPAD(giorno,2,'00')),'%Y-%m-%d') as data, \r\n"
				+ "ora, Prenotazione.idSala, Sede.nomeSede, statoPrenotazione FROM Prenotazione, Sala, Sede, Prestazione\r\n"
				+ "where Prenotazione.codicePrestazione = Prestazione.codicePrestazione\r\n"
				+ "and Prenotazione.idSala = Sala.idSala\r\n" + "and Sala.codiceSede = Sede.codiceSede\r\n"
				+ "and idPaziente = " + paziente.getIdPaziente() + "\r\n"
				+ "and timestamp(str_to_date(CONCAT(anno,'-',LPAD(mese,2,'00'),'-',LPAD(giorno,2,'00')),'%Y-%m-%d'), ora) >= current_timestamp() \r\n"
				+ "order by data";
		ArrayList<Prenotazione> prenotazioni = new ArrayList<>();
		try (Statement stmt = conn.createStatement(); ResultSet rset = stmt.executeQuery(query)) {
			while (rset.next()) {
				Prenotazione p = new Prenotazione(rset.getInt(1), paziente.getIdPaziente(), rset.getString(2),
						rset.getString(3), rset.getDate(4, Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"))),
						rset.getTime(5, Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"))), rset.getInt(6),
						rset.getString(7), rset.getInt(8));
				prenotazioni.add(p);
			}
			return prenotazioni;

		} catch (SQLException e) {

			e.printStackTrace();
			return prenotazioni;
		}
	}

	@Override
	public int cancellaAppuntamento(PazienteRegistrato paziente, Prenotazione prenotazione) throws DAOException, PrenotazioneInesistenteException, PazienteInesistenteException {

		if (prenotazione == null) {
			throw new PrenotazioneInesistenteException();
		}else if(paziente == null) {
			throw new PazienteInesistenteException();
		}

		String procedure = "{call cancellaAppuntamento(?,?,?)}";
		try (CallableStatement stmt = conn.prepareCall(procedure)) {
			stmt.setInt(1, prenotazione.getIdPrenotazione());
			stmt.setInt(2, paziente.getIdPaziente());
			stmt.registerOutParameter(3, Types.INTEGER);
			stmt.executeQuery();
			return stmt.getInt(3);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public int cancellazioneUtente(PazienteRegistrato paziente) throws DAOException, PazienteInesistenteException {
		if(paziente == null) {
			throw new PazienteInesistenteException();
		}
		String procedure = "{call cancellaLogin(?,?)}";
		try (CallableStatement stmt = conn.prepareCall(procedure)) {
			stmt.setInt(1, paziente.getIdPaziente());
			stmt.registerOutParameter(2, Types.INTEGER);
			stmt.executeQuery();
			return stmt.getInt(2);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public int modificaPaziente(PazienteRegistrato paziente, String parametro, String nuovoValoreParametro)
			throws DAOException, PazienteInesistenteException {

		if (paziente == null) {
			throw new PazienteInesistenteException();
		}

		String procedure = "{call modificaParametroPaziente(?,?,?,?)}";
		try (CallableStatement stmt = conn.prepareCall(procedure)) {
			stmt.setInt(1, paziente.getIdPaziente());
			stmt.setString(2, parametro);
			stmt.setString(3, nuovoValoreParametro);
			stmt.executeQuery();
			return stmt.getInt(4);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public int modificaPasswordPaziente(PazienteRegistrato paziente, String precedentePassword, String nuovaPassword)
			throws DAOException, PazienteInesistenteException {

		if (paziente == null) {
			throw new PazienteInesistenteException();
		}

		String procedure = "{call modificaPasswordPaziente(?,?,?,?)}";
		try (CallableStatement stmt = conn.prepareCall(procedure)) {
			stmt.setInt(1, paziente.getIdPaziente());
			stmt.setString(2, precedentePassword);
			stmt.setString(3, nuovaPassword);
			stmt.executeQuery();
			return stmt.getInt(4);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public int modificaImpiegato(Impiegato impiegato, String parametro, String nuovoValoreParametro)
			throws DAOException, ImpiegatoInesistenteException {
		if (impiegato == null) {
			throw new ImpiegatoInesistenteException();
		}

		String procedure = "{call modificaParametroImpiegato(?,?,?,?)}";
		try (CallableStatement stmt = conn.prepareCall(procedure)) {
			stmt.setInt(1, impiegato.getIdImpiegato());
			stmt.setString(2, parametro);
			stmt.setString(3, nuovoValoreParametro);
			stmt.executeQuery();
			return stmt.getInt(4);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public int modificaPasswordImpiegato(Impiegato impiegato, String precedentePassword, String nuovaPassword)
			throws DAOException, ImpiegatoInesistenteException {
		if (impiegato == null) {
			throw new ImpiegatoInesistenteException();
		}

		String procedure = "{call modificaPasswordImpiegato(?,?,?,?)}";
		try (CallableStatement stmt = conn.prepareCall(procedure)) {
			stmt.setInt(1, impiegato.getIdImpiegato());
			stmt.setString(2, precedentePassword);
			stmt.setString(3, nuovaPassword);
			stmt.executeQuery();
			return stmt.getInt(4);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public int trovaIDPazienteConCodiceFiscale(String codiceFiscale) {
		String query = "SELECT idPaziente FROM Paziente where codiceFiscale = '" + codiceFiscale + "'";
		try (Statement stmt = conn.createStatement();
			ResultSet rset = stmt.executeQuery(query)){
			rset.last();
			return rset.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public PazienteRegistrato trovaPazienteConID(int id) {
		String query = "SELECT nome, cognome, genere, codiceFiscale, dataNascita, cittaNascita, telefono, indirizzo, cap, email "
				+ "FROM Paziente where idPaziente = " + id;
		try (Statement stmt = conn.createStatement(); ResultSet rset = stmt.executeQuery(query)) {
			PazienteRegistrato paziente = null;
			if (rset.next()) {
				String nome = rset.getString(1);
				String cognome = rset.getString(2);
				String genere = rset.getString(3);
				String codiceFiscale = rset.getString(4);
				Date dataNascita = rset.getDate(5);
				String cittaNascita = rset.getString(6);
				String telefono = rset.getString(7);
				String indirizzo = rset.getString(8);
				String cap = rset.getString(9);
				String email = rset.getString(10);
				paziente = new PazienteRegistrato(id, nome, cognome, genere, codiceFiscale, dataNascita, cittaNascita,
						telefono, indirizzo, cap, email);
			}
			return paziente;

		} catch (SQLException e) {

			e.printStackTrace();

			return null;
		}
	}

	@Override
	public Impiegato trovaImpiegatoConID(int idImpiegato) {
		String query = "SELECT nomeImpiegato, cognomeImpiegato, genere, codiceFiscale, dataNascita, cittaNascita, telefono, indirizzo, cap, emailImpiegato "
				+ "FROM Impiegato where idImpiegato = " + idImpiegato;
		try (Statement stmt = conn.createStatement(); ResultSet rset = stmt.executeQuery(query)) {
			Impiegato impiegato = null;
			if (rset.next()) {
				String nome = rset.getString(1);
				String cognome = rset.getString(2);
				String genere = rset.getString(3);
				String codiceFiscale = rset.getString(4);
				Date dataNascita = rset.getDate(5);
				String cittaNascita = rset.getString(6);
				String telefono = rset.getString(7);
				String indirizzo = rset.getString(8);
				String cap = rset.getString(9);
				String email = rset.getString(10);
				impiegato = new Impiegato(idImpiegato, nome, cognome, genere, codiceFiscale, dataNascita, cittaNascita,
						telefono, indirizzo, cap, email);
			}
			return impiegato;

		} catch (SQLException e) {

			e.printStackTrace();

			return null;
		}
	}

	@Override
	public Prenotazione trovaPrenotazioneConID(int idPrenotazione) {
		String query = "SELECT idPrenotazione, Prenotazione.codicePrestazione, nomePrestazione, str_to_date(CONCAT(anno,'-',LPAD(mese,2,'00'),'-',LPAD(giorno,2,'00')),'%Y-%m-%d') as data, \n"
				+ "ora, Prenotazione.idSala, Sede.nomeSede, statoPrenotazione FROM Prenotazione, Sala, Sede, Prestazione \n"
				+ "where Prenotazione.codicePrestazione = Prestazione.codicePrestazione \n"
				+ "and Prenotazione.idSala = Sala.idSala and Sala.codiceSede = Sede.codiceSede \n"
				+ "and idPrenotazione = " + idPrenotazione;
		try (Statement stmt = conn.createStatement(); ResultSet rset = stmt.executeQuery(query)) {
			Prenotazione prenotazione = null;
			if (rset.next()) {
				int idPaziente = rset.getInt(1);
				String codPres = rset.getString(2);
				String nomePres = rset.getString(3);
				Date data = rset.getDate(4);
				Time oraApp = rset.getTime(5);
				int idSala = rset.getInt(6);
				String nomeSede = rset.getString(7);
				int statoPrenotazione = rset.getInt(8);
				prenotazione = new Prenotazione(idPrenotazione, idPaziente, codPres, nomePres, data, oraApp, idSala,
						nomeSede, statoPrenotazione);
			}
			return prenotazione;

		} catch (SQLException e) {

			e.printStackTrace();

			return null;
		}
	}

	@Override
	public Prestazione trovaPrestazioneColCodice(String codicePrestazione) {
		String query = "SELECT nomePrestazione, costo FROM Prestazione where codicePrestazione = '" + codicePrestazione
				+ "';";
		try (Statement stmt = conn.createStatement(); ResultSet rset = stmt.executeQuery(query)) {
			Prestazione prestazione = null;
			if (rset.next()) {
				String nomePrestazione = rset.getString(1);
				float costo = rset.getFloat(2);
				prestazione = new Prestazione(codicePrestazione, nomePrestazione, costo);
			}
			return prestazione;

		} catch (SQLException e) {

			e.printStackTrace();

			return null;
		}
	}

	@Override
	public Sala trovaSalaConID(int idSala) {
		String query = "SELECT numeroSala, codiceSede from sala where idSala = " + idSala;
		try (Statement stmt = conn.createStatement(); ResultSet rset = stmt.executeQuery(query)) {
			Sala sala = null;
			if (rset.next()) {
				int numeroSala = rset.getInt(1);
				int codiceSede = rset.getInt(2);
				sala = new Sala(idSala, numeroSala, codiceSede);
			}
			return sala;

		} catch (SQLException e) {

			e.printStackTrace();

			return null;
		}
	}

	@Override
	public void closeConnection() throws DAOException {
		try {
			conn.close();
			Enumeration<Driver> enumDrivers = DriverManager.getDrivers();
			while (enumDrivers.hasMoreElements()) {
				Driver driver = enumDrivers.nextElement();
				DriverManager.deregisterDriver(driver);
			}
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
}
