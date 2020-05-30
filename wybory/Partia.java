package wybory;

import java.util.Iterator;

abstract class Partia {
    public final String nazwaPartii;
    public final int budzet;
    protected int pozostalyBudzet;

    public Partia(String nazwaPartii, int budzet) {
        this.nazwaPartii = nazwaPartii;
        this.budzet = budzet;
        this.pozostalyBudzet = budzet;
    }

    private DzialanieNaOkregu maksymalneDzialanie(Bajtocja baj) {
        Iterator<Dzialanie> it = baj.getDzialaniaIterator();
        DzialanieNaOkregu maks = null, curr;
        Dzialanie dzial;
        while (it.hasNext()) {
            dzial = it.next();
            for (int i = 1; i <= baj.liczbaOkregow; i++) {
                curr = new DzialanieNaOkregu(dzial, baj.getOkragByNumer(i));
                if (curr.okreg.czyScalonyZPoprzednim() || curr.koszt > pozostalyBudzet) {
                    continue;
                } else if (maks == null || porownaj(maks, curr)) {
                    maks = curr;
                }
            }
        }
        return maks;
    }

    abstract protected boolean porownaj(DzialanieNaOkregu maks, DzialanieNaOkregu curr);

    public boolean wykonajDzialanie(Bajtocja baj) {
        DzialanieNaOkregu dzialNaOkr = this.maksymalneDzialanie(baj);
        if (dzialNaOkr == null) {
            return false;
        } else {
            dzialNaOkr.okreg.wykonajDzialanie(dzialNaOkr.dzial, true);
            pozostalyBudzet -= dzialNaOkr.koszt;
            return true;
        }
    }
}

class PartiaZRozmachem extends Partia {
    public PartiaZRozmachem(String nazwaPartii, int budzet) {
        super(nazwaPartii, budzet);
    }

    protected boolean porownaj(DzialanieNaOkregu maks, DzialanieNaOkregu curr) {
        return maks.koszt < curr.koszt;
    }
}

class PartiaSkromna extends Partia {
    public PartiaSkromna(String nazwaPartii, int budzet) {
        super(nazwaPartii, budzet);
    }

    protected boolean porownaj(DzialanieNaOkregu maks, DzialanieNaOkregu curr) {
        return maks.koszt > curr.koszt;
    }
}


// Kazde dzialanie podejmowane przez Partie Wlasna maksymalizuje stosunek liczby Wyborcow wszechstronnych
// w zasiegu dzialania do kosztu tego dzialania.
class PartiaWlasna extends Partia {
    public PartiaWlasna(String nazwaPartii, int budzet) {
        super(nazwaPartii, budzet);
    }

    protected boolean porownaj(DzialanieNaOkregu maks, DzialanieNaOkregu curr) {
        curr.setWartosc((double)curr.okreg.ileWyborcowWszechstronnych(true) / (double)curr.koszt);
        return curr.getWartosc() > maks.getWartosc();
    }
}

class PartiaZachlanna extends Partia {
    public PartiaZachlanna(String nazwaPartii, int budzet) {
        super(nazwaPartii, budzet);
    }

    protected boolean porownaj(DzialanieNaOkregu maks, DzialanieNaOkregu curr) {
        curr.setWartosc(curr.okreg.oIleZwiekszySumeKombinacjiCechKandydatowPartii(
            curr.dzial, nazwaPartii, true));

        return curr.getWartosc() > maks.getWartosc();
    }
}