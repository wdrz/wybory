package wybory;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String args[]) throws FileNotFoundException, IncorrectInputException {
        Input in = new Input("wybory/" + "test1.in");
        Bajtocja baj = in.wczytaj();
        baj.przeprowadzKampanie();
        baj.przeprowadzGlosowanieIWypisz();
    }
}