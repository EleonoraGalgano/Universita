package it.uniroma2.pjdm.radiolab.dao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

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

public interface RadiolabDAO {
	
	public int loginPaziente(Login login) throws DAOException;

	public int loginImpiegato(Login login) throws DAOException;

	public ArrayList<Prestazione> visualizzaPrestazioni();
	
	public ArrayList<Disponibilita> visualizzaDisponibilita(Prestazione prestazione) throws PrestazioneInesistenteException;
	
	public int prenotaAppuntamento(PazienteRegistrato paziente, Prestazione prestazione, Disponibilita disponibilita) throws DAOException, PazienteInesistenteException, PrestazioneInesistenteException, NessunAppuntamentoDisponibileException;
	
	public int cancellaAppuntamento(PazienteRegistrato paziente, Prenotazione prenotazione) throws DAOException, PrenotazioneInesistenteException, PazienteInesistenteException;
	
	public int cancellazioneUtente(PazienteRegistrato paziente) throws DAOException, PazienteInesistenteException;

	public int registrazione(Paziente paziente, String pw) throws DAOException;

	public Prestazione trovaPrestazioneColCodice(String codicePrestazione);
	
	public int trovaIDPazienteConCodiceFiscale(String codiceFiscale);
	
	public PazienteRegistrato trovaPazienteConID(int id);
	
	public Prenotazione trovaPrenotazioneConID(int idPrenotazione);
	
	public ArrayList<Prenotazione> trovaPrenotazioniPaziente(PazienteRegistrato paziente) throws PazienteInesistenteException;
	
	public Impiegato trovaImpiegatoConID(int idImpiegato);

	public Sala trovaSalaConID(int idSala);

	public int modificaPaziente(PazienteRegistrato paziente, String parametro, String nuovoValoreParametro) throws DAOException, PazienteInesistenteException;
	
	public int modificaImpiegato(Impiegato impiegato, String parametro, String nuovoValoreParametro) throws DAOException, ImpiegatoInesistenteException;
	
	public int modificaPasswordPaziente(PazienteRegistrato paziente, String precedentePassword, String nuovaPassword) throws DAOException, PazienteInesistenteException;
	
	public int modificaPasswordImpiegato(Impiegato impiegato, String precedentePassword, String nuovaPassword) throws DAOException, ImpiegatoInesistenteException; 
	
	public ArrayList<Sede> visualizzaListaSedi();

	public HashMap<Integer, Integer> calcolaStatistichePrenotazione(Date inizioPeriodo, Date finePeriodo,
			int statoPrenotazione, String raggruppamento );

	public void closeConnection() throws DAOException;

}
