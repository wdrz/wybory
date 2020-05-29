package wybory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class Bajtocja {
    public final int liczbaOkregow, liczbaPartii, liczbaDzialan, liczbaCech, ileScalonych;
    private Map<String, Partia> listaPartii;

    private OkragWyborczy[] listaOkregow;

    private ArrayList<Dzialanie> listaDzialan;

    private final Metoda metodaZamiany;

    private Metoda losujMetodeZamianyGlosow() {
        Random rand = new Random();
        switch (rand.nextInt(3)) {
            case 0:
                return new MetodaDHondta(liczbaPartii); 
            case 1:
                return new MetodaSainteLague(liczbaPartii);
            default:
                return new MetodaHareaNiemeyera(liczbaPartii);
        }
    }

    public Bajtocja(int liczbaOkregow, int liczbaPartii, int liczbaDzialan, int liczbaCech, 
        int ileScalonych, Map<String, Partia> listaPartii, OkragWyborczy[] listaOkregow) {
            this.liczbaOkregow = liczbaOkregow;
            this.liczbaPartii = liczbaPartii;
            this.liczbaDzialan = liczbaDzialan;
            this.liczbaCech = liczbaCech;
            this.ileScalonych = ileScalonych;
            this.metodaZamiany = this.losujMetodeZamianyGlosow();

            this.listaOkregow = listaOkregow;
            this.listaPartii = listaPartii;

            this.listaDzialan = new ArrayList<Dzialanie>();
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
        /// Brakuje
    }

    public void przeprowadzGlosowanieIWypisz() {
        System.out.println(metodaZamiany);
        for (int i = 0; i < liczbaOkregow; i++) {
            listaOkregow[i].glosowanieWScalonymOkregu(metodaZamiany, listaPartii.keySet());
        }
        metodaZamiany.wypiszWyniki();
    }
}