package wybory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

class Bajtocja {
    public final int liczbaOkregow, liczbaPartii, liczbaDzialan, liczbaCech, ileScalonych;
    private Map<String, Partia> listaPartii;

    private OkragWyborczy[] listaOkregow;

    private ArrayList<Dzialanie> listaDzialan;

    private final Metoda metodaZamiany;

    private Metoda losujMetodeZamianyGlosow() {
        Random rand = new Random();
        switch (rand.nextInt(3)) {
            case 0:
                return new MetodaDHondta(listaPartii.keySet()); 
            case 1:
                return new MetodaSainteLague(listaPartii.keySet());
            default:
                return new MetodaHareaNiemeyera(listaPartii.keySet());
        }
    }

    public Bajtocja(int liczbaOkregow, int liczbaPartii, int liczbaDzialan, int liczbaCech, 
        int ileScalonych, Map<String, Partia> listaPartii, OkragWyborczy[] listaOkregow) {
            this.liczbaOkregow = liczbaOkregow;
            this.liczbaPartii = liczbaPartii;
            this.liczbaDzialan = liczbaDzialan;
            this.liczbaCech = liczbaCech;
            this.ileScalonych = ileScalonych;

            this.listaOkregow = listaOkregow;
            this.listaPartii = listaPartii;

            this.listaDzialan = new ArrayList<Dzialanie>();
            this.metodaZamiany = this.losujMetodeZamianyGlosow();
    }

    public Iterator<Partia> getPartiaIterator() {
        return listaPartii.values().iterator();
    }

    public Partia getPartiaByName(String nazwaPartii) throws IncorrectInputException {
        Partia p = listaPartii.get(nazwaPartii);
        if (p == null) throw new IncorrectInputException("Nieznana nazwa partii");
        return p;
    }

    public OkragWyborczy getOkragByNumer(int numerOkregu) {
        return listaOkregow[numerOkregu - 1];
    }
    
    public void addDzialanie(Dzialanie dzial) {
        this.listaDzialan.add(dzial);
    }

    public Iterator<Dzialanie> getDzialaniaIterator() {
        return listaDzialan.iterator();
    }

    public void przeprowadzKampanie() {
        boolean wszystkieSkonczyly = false;
        Iterator<Partia> it;
        while (!wszystkieSkonczyly) {
            it = listaPartii.values().iterator();
            wszystkieSkonczyly = true;
            while (it.hasNext()) {
                if (it.next().wykonajDzialanie(this)) {
                    wszystkieSkonczyly = false;
                }
            }
        }
    }

    public void przeprowadzGlosowanieIWypisz() {
        System.out.println(metodaZamiany);
        for (int i = 0; i < liczbaOkregow; i++) {
            listaOkregow[i].glosowanieWScalonymOkregu(metodaZamiany, listaPartii.keySet());
        }
        metodaZamiany.wypiszWyniki();
    }
}