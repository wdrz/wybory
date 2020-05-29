package wybory;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public abstract class Metoda {
    protected Map<String, Integer> liczbaMandatow;
    protected final int liczbaPartii;

    protected Metoda(Set<String> listaNazwPartii) {
        liczbaMandatow = new LinkedHashMap<String, Integer>(listaNazwPartii.size());
        this.liczbaPartii = listaNazwPartii.size();
        for (String s : listaNazwPartii) {
            liczbaMandatow.put(s, 0);
        }
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

    @Override
    public int compareTo(IlorazWyborczy il) { 
        return il.licznik * mianownik - licznik * il.mianownik ; 
    } 

}

abstract class MetodaZIlorazem extends Metoda {
    protected MetodaZIlorazem(Set<String> listaNazwPartii) {
        super(listaNazwPartii);
    }

    protected void zamienNaMandaty(OkragWyborczy okrag, Map<String, Integer> wyniki, Function<Integer, Integer> ntymianownikIlorazu) {
        IlorazWyborczy[] ilorazy = new IlorazWyborczy[(okrag.wyborcyWScalonym() / 10) * liczbaPartii];
        int index = 0;
        // Utworzenie tablicy ilorazow wyborczych
        for (String partia : wyniki.keySet()) {
            for (int n = 1; n <= okrag.wyborcyWScalonym() / 10; n++) {
                ilorazy[index] = new IlorazWyborczy(wyniki.get(partia), ntymianownikIlorazu.apply(n), partia);
                index++;
            }
        }
        // odczytanie najwiekszych ilorazow
        Arrays.sort(ilorazy);
        
        /*System.out.println("DEBUG");
        for (int i = 0; i < (okrag.wyborcyWScalonym() / 10) * liczbaPartii ; i++) {
            System.out.println((float) ilorazy[i].licznik / (float) ilorazy[i].mianownik);

        } */

        for (int i = 0; i < okrag.wyborcyWScalonym() / 10; i++) {
            liczbaMandatow.replace(ilorazy[i].nazwaPartii, 
                liczbaMandatow.get(
                    ilorazy[i].nazwaPartii) + 1);
        }
        /*super.wypiszWyniki();*/
    }
}

class MetodaDHondta extends MetodaZIlorazem {
    public MetodaDHondta(Set<String> listaNazwPartii) {
        super(listaNazwPartii);
    }

    public void zamienNaMandaty(OkragWyborczy okrag, Map<String, Integer> wyniki) {
        Function<Integer, Integer> ntyMianownikIlorazu = e -> e;
        super.zamienNaMandaty(okrag, wyniki, ntyMianownikIlorazu);
    }
    @Override 
    public String toString() {
        return "Metoda D'Hondta";
    }
}


class MetodaSainteLague extends MetodaZIlorazem {
    public MetodaSainteLague(Set<String> listaNazwPartii) {
        super(listaNazwPartii);
    }

    public void zamienNaMandaty(OkragWyborczy okrag, Map<String, Integer> wyniki) {
        Function<Integer, Integer> ntyMianownikIlorazu = e -> 2 * e - 1;
        super.zamienNaMandaty(okrag, wyniki, ntyMianownikIlorazu);
    }
    @Override 
    public String toString() {
        return "Metoda Sainte-Lague";
    }
}

class MetodaHareaNiemeyera extends Metoda {
    public MetodaHareaNiemeyera(Set<String> listaNazwPartii) {
        super(listaNazwPartii);
    }

    public void zamienNaMandaty(OkragWyborczy okrag, Map<String, Integer> wyniki) {
        IlorazWyborczy[] ilorazy = new IlorazWyborczy[liczbaPartii];
        int index = 0, reszta, iloraz, dzielna, liczbaRozdzielonych = 0;
        for (Map.Entry<String, Integer> wynik : wyniki.entrySet()) {
            dzielna = wynik.getValue() * okrag.wyborcyWScalonym() / 10;
            iloraz = dzielna / okrag.wyborcyWScalonym();
            reszta = dzielna % okrag.wyborcyWScalonym();
            liczbaMandatow.replace(wynik.getKey(), 
                liczbaMandatow.get(wynik.getKey()) + iloraz);
            liczbaRozdzielonych += iloraz;
            ilorazy[index] = new IlorazWyborczy(reszta, 1, wynik.getKey());
            index++;
        }
        Arrays.sort(ilorazy);

        /*
        System.out.println("DEBUG");
        for (int i = 0; i < (okrag.wyborcyWScalonym() / 10) * liczbaPartii ; i++) {
            System.out.println((float) ilorazy[i].licznik / (float) ilorazy[i].mianownik);

        } */

        for (int i = 0; i < okrag.wyborcyWScalonym() / 10 - liczbaRozdzielonych; i++) {
            liczbaMandatow.replace(ilorazy[i].nazwaPartii, 
                liczbaMandatow.get(ilorazy[i].nazwaPartii) + 1);
        }
        /*super.wypiszWyniki();*/
    }
    @Override 
    public String toString() {
        return "Metoda Hare'a-Niemeyera";
    }
}