package pjdm.pjdm2022.radiolab.entity;

public class ListaPrestazioniElement {
    private String nome;
    private String codicePrestazione;
    private float costo;
    private boolean selected;

    public ListaPrestazioniElement(String nome, String codicePrestazione, float costo) {
        this.nome = nome;
        this.codicePrestazione = codicePrestazione;
        this.costo = costo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodicePrestazione() {
        return codicePrestazione;
    }

    public void setCodicePrestazione(String codicePrestazione) {
        this.codicePrestazione = codicePrestazione;
    }

    public float getCosto() {
        return costo;
    }

    public void setCosto(float costo) {
        this.costo = costo;
    }

    public boolean isSelected() {
        return selected;
    }

    public void toogleSelected() {
        selected = !selected;
    }

    @Override
    public String toString() {
        return "ListaPrestazioniElement{" +
                "nome='" + nome + '\'' +
                ", codicePrestazione='" + codicePrestazione + '\'' +
                ", costo=" + costo +
                '}';
    }
}
