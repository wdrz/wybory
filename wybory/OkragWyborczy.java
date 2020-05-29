package wybory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class OkragWyborczy {
    public final int liczbaWyborcow;

    private boolean scalonyZPoprzednim = false;

    private OkragWyborczy okragScalony;

    public final int numerOkregu;
    
    private Map<String, ArrayList<Kandydat>> kandydaci;
    private Wyborca[] wyborcy;

    private int wyborcy_index = 0;

    public OkragWyborczy(int numerOkregu, int liczbaPartii, int liczbaWyborcow) throws IncorrectInputException {
        if (liczbaWyborcow % 10 != 0)
            throw new IncorrectInputException("Liczba wyborcow w podstawowym okregu ma byc postaci 10k");
        
        this.numerOkregu = numerOkregu;
        this.liczbaWyborcow = liczbaWyborcow;
        this.kandydaci = new HashMap<String, ArrayList<Kandydat>>(liczbaPartii);
        this.wyborcy = new Wyborca[liczbaWyborcow];
    }

    public void addWyborca(Wyborca wyb) {
        wyborcy[wyborcy_index] = wyb;
        wyborcy_index++;
    }

    public void scal(OkragWyborczy okrag) throws IncorrectInputException {
        if (this.okragScalony != null) {
            throw new IncorrectInputException("Iternowane scalanie zabronione");
        }

        if (this.numerOkregu + 1 == okrag.numerOkregu) {
            this.okragScalony = okrag;
        } else if (this.numerOkregu == okrag.numerOkregu + 1) {
            this.scalonyZPoprzednim = true;
            this.okragScalony = okrag;
        } else {
            throw new IncorrectInputException("Można scalać tylko sąsiednie okręgi");
        }
    }

    public boolean czyScalonyZPoprzednim() {
        return scalonyZPoprzednim;
    }

    public void addListaPartyjna(String nazwaPartii, ArrayList<Kandydat> lista) {
        kandydaci.put(nazwaPartii, lista);
    }

    public Kandydat getKandydat(String nazwaPartii, int pozycjaNaLiscie) {
        if (pozycjaNaLiscie > liczbaWyborcow / 10) {
            return okragScalony.getKandydat(nazwaPartii, pozycjaNaLiscie - liczbaWyborcow / 10);
        } else {
            return kandydaci.get(nazwaPartii).get(pozycjaNaLiscie - 1);
        }
    }

    public Iterator<ArrayList<Kandydat>> getIteratorListPartyjnych() {
        return kandydaci.values().iterator();
    }

    public Iterator<Kandydat> getIteratorKandydatowPartii(String nazwaPartii) {
        return kandydaci.get(nazwaPartii).iterator();
    }

    public IteratorKandydatow itaratorWszystkichKandydatow() {
        if (okragScalony == null) {
            return new IterKandydatowWszyskich(this.getIteratorListPartyjnych(), 
                (new ArrayList<ArrayList<Kandydat>>()).iterator());
        } else {
            return new IterKandydatowWszyskich(this.getIteratorListPartyjnych(), 
                okragScalony.getIteratorListPartyjnych());
        }
    }

    public IteratorKandydatow iteratorKandydatowPartii(String nazwaPartii) {
        if (okragScalony == null) {
            return new IterKandydatowPartii(this.getIteratorKandydatowPartii(nazwaPartii), 
                (new ArrayList<Kandydat>()).iterator());
        } else {
            return new IterKandydatowPartii(this.getIteratorKandydatowPartii(nazwaPartii), 
                okragScalony.getIteratorKandydatowPartii(nazwaPartii));
        }

    }

    private void glosowanieWPodstawowymOkregu(Map<String, Integer> mapa) {
        Kandydat kand;
        for (int i = 0; i < liczbaWyborcow; i++) {
            kand = wyborcy[i].oddajGlosIwypisz();
            mapa.replace(kand.nazwaPartii, mapa.get(kand.nazwaPartii) + 1);
        }
    }

    // zwraca mape z wynikami ze scalonego okregu, modyfikuje Metode
    // oraz pisze na standardowe wyjscie wyniki z tego scalonego okregu
    public void glosowanieWScalonymOkregu(Metoda met, Set<String> nazwyPartii) {
        if (scalonyZPoprzednim) return;

        Map<String, Integer> wynikOkregu = new HashMap<String, Integer>();
        for (String s : nazwyPartii) {
            wynikOkregu.put(s, 0);
        }
        if (okragScalony != null) {
            System.out.println("Okręg " + Integer.toString(this.numerOkregu) + 
                ", " + Integer.toString(okragScalony.numerOkregu));
        } else {
            System.out.println("Okręg " + Integer.toString(this.numerOkregu));
        }

        this.glosowanieWPodstawowymOkregu(wynikOkregu);
        if (okragScalony != null) {
            okragScalony.glosowanieWPodstawowymOkregu(wynikOkregu);
        }

        System.out.println("Kandydaci"); //
        IteratorKandydatow it = this.itaratorWszystkichKandydatow();
        Kandydat kand;
        while (it.hasNext()) {
            kand = it.next();
            System.out.println(kand.imie + " " + kand.nazwisko + " (" + kand.nazwaPartii + 
                ") - " + Integer.toString(kand.getLiczbaGlosow()));
        }

        met.zamienNaMandaty(this, wynikOkregu);
    }
}