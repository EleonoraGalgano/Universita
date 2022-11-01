package pjdm.pjdm2022.radiolab.entity;

import java.sql.Date;
import java.sql.Time;

public class ListaDisponibilitaElement {
    private String data;
    private String ora;
    private String nomeSede;
    private int idSala;
    private boolean selected;

    public ListaDisponibilitaElement(String data, String ora, String nomeSede, int idSala) {
        this.data = data;
        this.ora = ora;
        this.nomeSede = nomeSede;
        this.idSala = idSala;
    }

    public String getData() {
        return data;
    }

    public String getOra() {
        return ora;
    }

    public String getNomeSede() {
        return nomeSede;
    }

    public int getIdSala() {
        return idSala;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setOra(String ora) {
        this.ora = ora;
    }

    public void setNomeSede(String nomeSede) {
        this.nomeSede = nomeSede;
    }

    public void setIdSala(int idSala) {
        this.idSala = idSala;
    }

    public boolean isSelected() {
        return selected;
    }

    public void toogleSelected() {
        selected = !selected;
    }

    @Override
    public String toString() {
        return "ListaDisponibilitaElement{" +
                "data='" + data + '\'' +
                ", ora='" + ora + '\'' +
                ", nomeSede='" + nomeSede + '\'' +
                ", idSala=" + idSala +
                '}';
    }
}
