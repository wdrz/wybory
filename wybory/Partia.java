package wybory;

public class Partia {
    public final String nazwaPartii;
    public final int budzet;

    public Partia(String nazwaPartii, int budzet) {
        this.nazwaPartii = nazwaPartii;
        this.budzet = budzet;
    }
}

class PartiaZRozmachem extends Partia {
    public PartiaZRozmachem(String nazwaPartii, int budzet) {
        super(nazwaPartii, budzet);
    }
}

class PartiaSkromna extends Partia {
    public PartiaSkromna(String nazwaPartii, int budzet) {
        super(nazwaPartii, budzet);
    }
}

class PartiaWlasna extends Partia {
    public PartiaWlasna(String nazwaPartii, int budzet) {
        super(nazwaPartii, budzet);
    }
}

class PartiaZachlanna extends Partia {
    public PartiaZachlanna(String nazwaPartii, int budzet) {
        super(nazwaPartii, budzet);
    }
}