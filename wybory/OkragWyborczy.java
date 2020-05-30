package wybory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

class OkragWyborczy {
    public final int liczbaWyborcow;

    private boolean scalonyZPoprzednim = false;

    private OkragWyborczy okragScalony;

    public final int numerOkregu;
    
    private Map<String, ArrayList<Kandydat>> kandydaci;
    private Wyborca[] wyborcy;

    private int wyborcy_index = 0;

    private Integer liczbaWyborcowWszechstronnych = null;

    public OkragWyborczy(int numerOkregu, int liczbaPartii, int liczbaWyborcow) throws IncorrectInputException {
        if (liczbaWyborcow % 10 != 0)
            throw new IncorrectInputException("Liczba wyborcow w podstawowym okregu ma byc postaci 10k");
        
        this.numerOkregu = numerOkregu;
        this.liczbaWyborcow = liczbaWyborcow;
        this.kandydaci = new LinkedHashMap<String, ArrayList<Kandydat>>(liczbaPartii);
        this.wyborcy = new Wyborca[liczbaWyborcow];
    }

    public int wyborcyWScalonym() {
        if (okragScalony == null) return liczbaWyborcow;
        else return liczbaWyborcow + okragScalony.liczbaWyborcow; 
    }

    public void addWyborca(Wyborca wyb) {
        wyborcy[wyborcy_index] = wyb;
        wyborcy_index++;
    }

    public int kosztDzialania(Dzialanie dzialanie) {
        return dzialanie.kosztPerCapita * wyborcyWScalonym();
    }

    public int oIleZwiekszySumeKombinacjiCechKandydatowPartii(Dzialanie dzial, 
        String nazwaPartii, boolean czyPropagowac) {
            int wynik = 0;
            if (okragScalony != null && czyPropagowac) {
                wynik += okragScalony.oIleZwiekszySumeKombinacjiCechKandydatowPartii(
                    dzial, nazwaPartii, false);
            }
            for (int i = 0; i < liczbaWyborcow; i++) {
                wynik += wyborcy[i].oIleZwiekszySumeKombinacjiCechKandydatowPartii(dzial, 
                    iteratorKandydatowPartii(nazwaPartii));
            }
            return wynik;
    }

    public void wykonajDzialanie(Dzialanie dzial, boolean czyPropagowac) {
        if (okragScalony != null && czyPropagowac) {
            okragScalony.wykonajDzialanie(dzial, false);
        }
        for (int i = 0; i < liczbaWyborcow; i++) {
            wyborcy[i].modyfikujWagi(dzial);
        }
    }

    public int ileWyborcowWszechstronnych(boolean czyPropagowac) {
        if (liczbaWyborcowWszechstronnych == null) {
            int wynik = 0;
            if (okragScalony != null && czyPropagowac) {
                wynik += okragScalony.ileWyborcowWszechstronnych(false);
            }
            for (int i = 0; i < liczbaWyborcow; i++) {
                if (wyborcy[i] instanceof Wszechstronny) {
                    wynik++;
                }
            }
            liczbaWyborcowWszechstronnych = wynik;
        } 
        return liczbaWyborcowWszechstronnych;
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


    public IteratorKandydatow itaratorWszystkichKandydatow() {
        if (okragScalony == null) {
            return new IterKandydatowWszyskichPodstawowegoOkregu(this.kandydaci.values());
        } else {
            return new IterKandydatowWszyskich(this.kandydaci.values(), 
                okragScalony.kandydaci.values());
        }
    }

    public IteratorKandydatow iteratorKandydatowPartii(String nazwaPartii) {
        if (okragScalony == null) {
            return new IterKandydatowPartii(this.kandydaci.get(nazwaPartii).iterator(), 
                (new ArrayList<Kandydat>()).iterator());
        } else {
            return new IterKandydatowPartii(this.kandydaci.get(nazwaPartii).iterator(), 
                okragScalony.kandydaci.get(nazwaPartii).iterator());
        }

    }

    private void glosowanieWPodstawowymOkregu(Map<String, Integer> mapa) {
        Kandydat kand;
        for (int i = 0; i < liczbaWyborcow; i++) {
            kand = wyborcy[i].oddajGlosIwypisz();
            mapa.replace(kand.nazwaPartii, mapa.get(kand.nazwaPartii) + 1);
        }
    }

    // Dopisuje do met wyniki z okregu oraz pisze na standardowe wyjscie wyniki z tego scalonego okregu
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