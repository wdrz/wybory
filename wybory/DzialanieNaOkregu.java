package wybory;

class DzialanieNaOkregu {
    public final Dzialanie dzial;
    public final OkragWyborczy okreg;
    public final int koszt;
    private double wartosc;
    public DzialanieNaOkregu(Dzialanie dzial, OkragWyborczy okreg) {
        this.dzial = dzial;
        this.okreg = okreg;
        this.koszt = okreg.kosztDzialania(dzial);
    }
    public void setWartosc(double wartosc) {
        this.wartosc = wartosc;
    }
    public double getWartosc() {
        return this.wartosc;
    }
}