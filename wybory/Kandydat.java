package wybory;

class Kandydat {
    public final String imie, nazwisko, nazwaPartii;
    public final int numerOkregu, pozycjaNaLiscie;
    private int[] cechy;

    private int liczbaGlosow = 0;

    public Kandydat(int liczbaCech, String imie, String nazwisko, int numerOkregu, 
        String nazwaPartii, int pozycjaNaLiscie, int[] cechy) {
            this.cechy = new int[liczbaCech];
            this.imie = imie;
            this.nazwisko = nazwisko;
            this.numerOkregu = numerOkregu;
            this.nazwaPartii = nazwaPartii;
            this.pozycjaNaLiscie = pozycjaNaLiscie;
            this.cechy = cechy;
        }

    public void otrzymajGlos() {
        liczbaGlosow++;
    }

    public int getLiczbaGlosow() {
        return liczbaGlosow;
    }

    public int getCecha(int nrCechy) {
        return cechy[nrCechy - 1];
    }

    @Override
    public String toString() {
        return this.imie + " " + this.nazwisko;
    }
}