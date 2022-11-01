package pjdm.pjdm2022.radiolab.entity;

public class ListaStatisticaElement {
    String mese_anno;
    Integer ammontare;

    public ListaStatisticaElement( String mese_anno, Integer ammontare) {
        this.mese_anno = mese_anno;
        this.ammontare = ammontare;
    }

    public String getMeseAnno() {
        return mese_anno;
    }

    public void setMese(String mese) {
        this.mese_anno = mese_anno;
    }

    public Integer getAmmontare() {
        return ammontare;
    }

    public void setAmmontare(Integer ammontare) {
        this.ammontare = ammontare;
    }
}
