package pjdm.pjdm2022.radiolab.entity;

import java.sql.Date;
import java.sql.Time;

public class ListaAppuntamentiElement {
    private String idPrenotazione;
    private String nomePrestazione;
    private String data;
    private String ora;
    private String nomeSede;

    public ListaAppuntamentiElement(String idPrenotazione, String nomePrestazione, String data, String ora, String nomeSede) {
        this.idPrenotazione = idPrenotazione;
        this.nomePrestazione = nomePrestazione;
        this.data = data;
        this.ora = ora;
        this.nomeSede = nomeSede;
    }

    public String getIdPrenotazione() {
        return idPrenotazione;
    }

    public void setIdPrenotazione(String idPrenotazione) {
        this.idPrenotazione = idPrenotazione;
    }

    public String getNomePrestazione() {
        return nomePrestazione;
    }

    public void setNomePrestazione(String nomePrestazione) {
        this.nomePrestazione = nomePrestazione;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getOra() {
        return ora;
    }

    public void setOra(String ora) {
        this.ora = ora;
    }

    public String getNomeSede() {
        return nomeSede;
    }

    public void setNomeSede(String nomeSede) {
        this.nomeSede = nomeSede;
    }

    @Override
    public String toString() {
        return "ListaAppuntamentiElement{" +
                "idPrenotazione='" + idPrenotazione + '\'' +
                ", nomePrestazione='" + nomePrestazione + '\'' +
                ", data='" + data + '\'' +
                ", ora='" + ora + '\'' +
                ", nomeSede='" + nomeSede + '\'' +
                '}';
    }
}
