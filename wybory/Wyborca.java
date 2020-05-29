package wybory;

import java.util.ArrayList;
import java.util.Random;

abstract class Wyborca {
    public final String imie, nazwisko;
    public final OkragWyborczy okrag;
    protected Wyborca(String imie, String nazwisko, OkragWyborczy okrag) {
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.okrag = okrag;
    }
    public void modyfikujWagi(Dzialanie dzial) {}

    abstract public Kandydat glosuj();

    public Kandydat oddajGlosIwypisz() {
        Kandydat kand = glosuj();
        System.out.println(this.imie + " " + this.nazwisko + ": " + kand.imie + " " + kand.nazwisko);
        return kand;
    }
}

abstract class Jednocechowy extends Wyborca {
    public final int cecha;
    protected Jednocechowy(String imie, String nazwisko, OkragWyborczy okrag, int cecha) {
        super(imie, nazwisko, okrag);
        this.cecha = cecha;
    }

    private boolean XNOR(boolean x, boolean y) {
        return (!( x || y ) || ( x && y ));
    }

    // modyfikator = true gdy kandydat maksymalizuje ceche
    // modyfikator = false gdy kandydat minimalizuje ceche
    protected Kandydat glosuj(IteratorKandydatow it, boolean modyfikator) { 
        Kandydat kand, wynik = it.next();
        while (it.hasNext()) {
            kand = it.next();
            if (XNOR(kand.getCecha(cecha) > wynik.getCecha(cecha), modyfikator)) {
                wynik = kand;
            }
        }
        return wynik.otrzymajGlos();
    }

}

abstract class WyborcaZWagami extends Wyborca {
    private ArrayList<Integer> wagi;
    protected WyborcaZWagami(String imie, String nazwisko, OkragWyborczy okrag, ArrayList<Integer> wagi) {
        super(imie, nazwisko, okrag);
        this.wagi = wagi;
    }

    public int getWaga(int numerWagi) {
        return wagi.get(numerWagi);
    }

    private int policzKombinacjeLiniowa(Kandydat k) {
        int wynik = 0;
        for (int i = 0; i < wagi.size(); i++) {
            wynik += wagi.get(i) * k.getCecha(i + 1);
        }
        return wynik;
    }

    protected Kandydat glosuj(IteratorKandydatow it) {
        Kandydat kand, wynik = it.next();
        int maks = policzKombinacjeLiniowa(wynik);
        int policzona_komb;

        while (it.hasNext()) {
            kand = it.next();
            policzona_komb = policzKombinacjeLiniowa(kand);
            if (policzona_komb > maks) {
                wynik = kand;
                maks = policzona_komb;
            }
        }
        return wynik.otrzymajGlos();
    }

    @Override
    public void modyfikujWagi(Dzialanie dzial) {
        // tu faktycznie jakas tresc
    }
}

interface WyborcaPartii {
    String getNazwaPartii();
}

class ZelaznyPartyjny extends Wyborca {
    public final String nazwaPartii;
    public ZelaznyPartyjny(String imie, String nazwisko, OkragWyborczy okrag, String nazwaPartii) {
        super(imie, nazwisko, okrag);
        this.nazwaPartii = nazwaPartii;
    }
    public Kandydat glosuj() {
        int losowyNr = (new Random()).nextInt(okrag.liczbaWyborcow / 10);
        return okrag.getKandydat(nazwaPartii, losowyNr).otrzymajGlos();
    }
}

class ZelaznyKandydata extends Wyborca {
    public final int pozycjaNaLiscie;
    public final String nazwaPartii;
    public ZelaznyKandydata(String imie, String nazwisko, OkragWyborczy okrag, String nazwaPartii, int pozycjaNaLiscie) {
        super(imie, nazwisko, okrag);
        this.nazwaPartii = nazwaPartii;
        this.pozycjaNaLiscie = pozycjaNaLiscie;
    }
    public Kandydat glosuj() {
        return okrag.getKandydat(nazwaPartii, pozycjaNaLiscie).otrzymajGlos();
    }
}



class MinimalizujacyJednocechowy extends Jednocechowy {
    public MinimalizujacyJednocechowy(String imie, String nazwisko, OkragWyborczy okrag, int cecha) {
        super(imie, nazwisko, okrag, cecha);
    }
    public Kandydat glosuj() {
        return super.glosuj(okrag.itaratorWszystkichKandydatow(), false).otrzymajGlos();
    }
}



class MaksymalizujacyJednocechowy extends Jednocechowy {
    public MaksymalizujacyJednocechowy(String imie, String nazwisko, OkragWyborczy okrag, int cecha) {
        super(imie, nazwisko, okrag, cecha);
    }
    public Kandydat glosuj() {
        return super.glosuj(okrag.itaratorWszystkichKandydatow(), true).otrzymajGlos();
    }
}



class Wszechstronny extends WyborcaZWagami {
    public Wszechstronny(String imie, String nazwisko, OkragWyborczy okrag, ArrayList<Integer> wagi) {
        super(imie, nazwisko, okrag, wagi);
    }

    public Kandydat glosuj() {
        return super.glosuj(okrag.itaratorWszystkichKandydatow()).otrzymajGlos();
    }
}



class MinimalizujacyJednocechowyPartia extends Jednocechowy implements WyborcaPartii {
    private final String nazwaPartii;
    public MinimalizujacyJednocechowyPartia(String imie, String nazwisko, OkragWyborczy okrag, int cecha, String nazwaPartii) {
        super(imie, nazwisko, okrag, cecha);
        this.nazwaPartii = nazwaPartii;
    }
    public String getNazwaPartii() {
        return nazwaPartii;
    }
    public Kandydat glosuj() {
        return super.glosuj(okrag.iteratorKandydatowPartii(nazwaPartii), false).otrzymajGlos();
    }
}



class MaksymalizujacyJednocechowyPartia extends Jednocechowy implements WyborcaPartii {
    private final String nazwaPartii;
    public MaksymalizujacyJednocechowyPartia(String imie, String nazwisko, OkragWyborczy okrag, int cecha, String nazwaPartii) {
        super(imie, nazwisko, okrag, cecha);
        this.nazwaPartii = nazwaPartii;
    }
    public String getNazwaPartii() {
        return nazwaPartii;
    }
    public Kandydat glosuj() {
        return super.glosuj(okrag.iteratorKandydatowPartii(nazwaPartii), true).otrzymajGlos();
    }
}



class WszechstronnyPartia extends WyborcaZWagami implements WyborcaPartii {
    private final String nazwaPartii;
    public WszechstronnyPartia(String imie, String nazwisko, OkragWyborczy okrag, ArrayList<Integer> wagi, String nazwaPartii) {
        super(imie, nazwisko, okrag, wagi);
        this.nazwaPartii = nazwaPartii;
    }
    public String getNazwaPartii() {
        return nazwaPartii;
    }
    public Kandydat glosuj() {
        return super.glosuj(okrag.iteratorKandydatowPartii(nazwaPartii)).otrzymajGlos();
    }
    
}

