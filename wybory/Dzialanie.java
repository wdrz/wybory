package wybory;

public class Dzialanie {
    private final int[] zmianaCech;
    public final int kosztPerCapita;

    public Dzialanie(int[] zmianaCech) {
        this.zmianaCech = zmianaCech;
        int wynik = 0;
        for (int i : zmianaCech) wynik += Math.abs(i);
        this.kosztPerCapita = wynik;
    }

    public int getZmiana(int indexCechy) {
        return zmianaCech[indexCechy];
    }

}