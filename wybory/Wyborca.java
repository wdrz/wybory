package wybory;

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

    public int oIleZwiekszySumeKombinacjiCechKandydatowPartii(Dzialanie dzial, IteratorKandydatow it) {
        return 0;
    }

    abstract protected Kandydat glosuj();

    public Kandydat oddajGlosIwypisz() {
        Kandydat kand = glosuj();
        kand.otrzymajGlos();
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
        return wynik;
    }

}

abstract class WyborcaZWagami extends Wyborca {
    private int[] wagi;
    protected WyborcaZWagami(String imie, String nazwisko, OkragWyborczy okrag, int[] wagi) {
        super(imie, nazwisko, okrag);
        this.wagi = wagi;
    }

    public int getWaga(int numerWagi) {
        return wagi[numerWagi];
    }

    private int policzKombinacjeLiniowa(Kandydat k, int[] tablica) {
        int wynik = 0;
        for (int i = 0; i < tablica.length; i++) {
            wynik += tablica[i] * k.getCecha(i + 1);
        }
        return wynik;
    }

    protected Kandydat glosuj(IteratorKandydatow it) {
        Kandydat kand, wynik = it.next();
        int maks = policzKombinacjeLiniowa(wynik, wagi);
        int policzona_komb;

        while (it.hasNext()) {
            kand = it.next();
            policzona_komb = policzKombinacjeLiniowa(kand, wagi);
            if (policzona_komb > maks) {
                wynik = kand;
                maks = policzona_komb;
            }
        }
        return wynik;
    }

    private void modyfikujTablice(Dzialanie dzial, int[] tablica) {
        for (int i = 0; i < tablica.length; i++) {
            tablica[i] = Math.min(Math.max(tablica[i] + dzial.getZmiana(i), -100), 100);
        }
    }

    @Override
    public void modyfikujWagi(Dzialanie dzial) {
        modyfikujTablice(dzial, wagi);
        /*System.out.println("Modyfikuje wagi!"); */
    }

    @Override
    public int oIleZwiekszySumeKombinacjiCechKandydatowPartii(Dzialanie dzial, IteratorKandydatow it) {
        int wynik = 0;
        int[] symmulowaneWagi = wagi.clone();
        modyfikujTablice(dzial, symmulowaneWagi);
        while (it.hasNext()) {
            wynik += policzKombinacjeLiniowa(it.next(), symmulowaneWagi);
        }
        return wynik;
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
    protected Kandydat glosuj() {
        int losowyNr = (new Random(this.hashCode())).nextInt(okrag.wyborcyWScalonym() / 10) + 1;
        return okrag.getKandydat(nazwaPartii, losowyNr);
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
    protected Kandydat glosuj() {
        return okrag.getKandydat(nazwaPartii, pozycjaNaLiscie);
    }
}



class MinimalizujacyJednocechowy extends Jednocechowy {
    public MinimalizujacyJednocechowy(String imie, String nazwisko, OkragWyborczy okrag, int cecha) {
        super(imie, nazwisko, okrag, cecha);
    }
    protected Kandydat glosuj() {
        return super.glosuj(okrag.itaratorWszystkichKandydatow(), false);
    }
}



class MaksymalizujacyJednocechowy extends Jednocechowy {
    public MaksymalizujacyJednocechowy(String imie, String nazwisko, OkragWyborczy okrag, int cecha) {
        super(imie, nazwisko, okrag, cecha);
    }
    protected Kandydat glosuj() {
        return super.glosuj(okrag.itaratorWszystkichKandydatow(), true);
    }
}



class Wszechstronny extends WyborcaZWagami {
    public Wszechstronny(String imie, String nazwisko, OkragWyborczy okrag, int[] wagi) {
        super(imie, nazwisko, okrag, wagi);
    }

    protected Kandydat glosuj() {
        return super.glosuj(okrag.itaratorWszystkichKandydatow());
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
    protected Kandydat glosuj() {
        return super.glosuj(okrag.iteratorKandydatowPartii(nazwaPartii), false);
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
    protected Kandydat glosuj() {
        return super.glosuj(okrag.iteratorKandydatowPartii(nazwaPartii), true);
    }
}



class WszechstronnyPartia extends WyborcaZWagami implements WyborcaPartii {
    private final String nazwaPartii;
    public WszechstronnyPartia(String imie, String nazwisko, OkragWyborczy okrag, int[] wagi, String nazwaPartii) {
        super(imie, nazwisko, okrag, wagi);
        this.nazwaPartii = nazwaPartii;
    }
    public String getNazwaPartii() {
        return nazwaPartii;
    }
    protected Kandydat glosuj() {
        return super.glosuj(okrag.iteratorKandydatowPartii(nazwaPartii));
    }
    
}

