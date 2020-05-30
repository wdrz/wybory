package wybory;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String args[]) throws FileNotFoundException, IncorrectInputException {
        Input in = new Input("test_own.txt");
        Bajtocja baj = in.wczytaj();
        baj.przeprowadzKampanie();
        baj.przeprowadzGlosowanieIWypisz();
    }
}