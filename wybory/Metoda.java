package wybory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class Metoda {
    protected Map<String, Integer> liczbaMandatow;
    protected final int liczbaPartii;

    protected Metoda(int liczbaPartii) {
        liczbaMandatow = new HashMap<String, Integer>(liczbaPartii);
        this.liczbaPartii = liczbaPartii;
    }

    abstract public void zamienNaMandaty(OkragWyborczy okrag, Map<String, Integer> wyniki);

    
    public void wypiszWyniki() {
        for (Map.Entry<String, Integer> wynik : liczbaMandatow.entrySet()) {
            System.out.println(wynik.getKey() + " - " + Integer.toString(wynik.getValue()));
        }
    }
}

class IlorazWyborczy implements Comparable<IlorazWyborczy> {
    public final int licznik;
    public final int mianownik;
    public final String nazwaPartii;


    public IlorazWyborczy(int licznik, int mianownik, String nazwaPartii) {
        this.licznik = licznik;
        this.mianownik = mianownik;
        this.nazwaPartii = nazwaPartii;
    }

    public int compareTo(IlorazWyborczy il) { 
        return licznik * il.mianownik - il.licznik * mianownik; 
    } 

}

class MetodaDHondta extends Metoda {
    public MetodaDHondta(int liczbaPartii) {
        super(liczbaPartii);
    }

    public void zamienNaMandaty(OkragWyborczy okrag, Map<String, Integer> wyniki) {
        IlorazWyborczy[] ilorazy = new IlorazWyborczy[(okrag.liczbaWyborcow / 10) * liczbaPartii];
        int index = 0;
        // Utworzenie tablicy ilorazow wyborczych
        for (String partia : wyniki.keySet()) {
            for (int n = 1; n <= okrag.liczbaWyborcow / 10; n++) {
                ilorazy[index] = new IlorazWyborczy(wyniki.get(partia), n, partia);
                index++;
            }
        }
        // odczytanie najwiekszych ilorazow
        Arrays.sort(ilorazy);
        for (int i = 0; i < okrag.liczbaWyborcow / 10; i++) {
            liczbaMandatow.replace(ilorazy[index].nazwaPartii, 
                liczbaMandatow.get(ilorazy[index].nazwaPartii) + 1);
        }

    }
    @Override 
    public String toString() {
        return "Metoda D'Hondta";
    }
}


class MetodaSainteLague extends Metoda {
    public MetodaSainteLague(int liczbaPartii) {
        super(liczbaPartii);
    }

    public void zamienNaMandaty(OkragWyborczy okrag, Map<String, Integer> wyniki) {
        
    }
    @Override 
    public String toString() {
        return "Metoda Sainte-Lague";
    }
}

class MetodaHareaNiemeyera extends Metoda {
    public MetodaHareaNiemeyera(int liczbaPartii) {
        super(liczbaPartii);
    }

    public void zamienNaMandaty(OkragWyborczy okrag, Map<String, Integer> wyniki) {
        
    }
    @Override 
    public String toString() {
        return "Metoda Hare'a-Niemeyera";
    }
}