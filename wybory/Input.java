package wybory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Input {

    private final Scanner in;

    private Bajtocja wczytajBajtocje() throws IncorrectInputException {
        int liczbaOkregow = in.nextInt();
        int liczbaPartii = in.nextInt();
        int liczbaDzialan = in.nextInt();
        int liczbaCech = in.nextInt();
        int liczbaScalonych = in.nextInt();

        int[] scaleniaLewe = new int[liczbaScalonych];
        int[] scaleniaPrawe = new int[liczbaScalonych];

        Pattern p;
        Matcher m;
        for (int i = 0; i < liczbaScalonych; i++) {
            p = Pattern.compile("\\d+");
            m = p.matcher(in.next());
            m.find();
            scaleniaLewe[i] = Integer.parseInt(m.group()) - 1;
            m.find();
            scaleniaPrawe[i] = Integer.parseInt(m.group()) - 1;
        }

        Map<String, Partia> listaPartii = wczytajPartie(liczbaPartii);

        OkragWyborczy okregi[] = wczytajOkregiWyborcze(liczbaOkregow, liczbaPartii);
        for (int i = 0; i < liczbaScalonych; i++) {
            okregi[scaleniaLewe[i]].scal(okregi[scaleniaPrawe[i]]);
            okregi[scaleniaPrawe[i]].scal(okregi[scaleniaLewe[i]]);
        }
        return new Bajtocja(liczbaOkregow, liczbaPartii, liczbaDzialan, liczbaCech, liczbaScalonych, listaPartii, okregi);
    }

    private Map<String, Partia> wczytajPartie(int liczbaPartii) throws IncorrectInputException {
        Map<String, Partia> listaPartii = new LinkedHashMap<String, Partia>();
        String[] nazwy = new String[liczbaPartii];
        int[] budzety = new int[liczbaPartii];
        for (int i = 0; i < liczbaPartii; i++)
            nazwy[i] = in.next();

        for (int i = 0; i < liczbaPartii; i++)
            budzety[i] = in.nextInt();

        String symbol;
        for (int i = 0; i < liczbaPartii; i++) {
            symbol = in.next();
            if (symbol.length() != 1) 
                throw new IncorrectInputException("Strategia powinna być pojedynczym znakiem");
            switch (symbol.charAt(0)) {
                case 'R':
                    listaPartii.put(nazwy[i], new PartiaZRozmachem(nazwy[i], budzety[i]));
                    break;
                case 'S':
                    listaPartii.put(nazwy[i], new PartiaSkromna(nazwy[i], budzety[i]));
                    break;
                case 'W':
                    listaPartii.put(nazwy[i], new PartiaWlasna(nazwy[i], budzety[i]));
                    break;
                case 'Z':
                    listaPartii.put(nazwy[i], new PartiaZachlanna(nazwy[i], budzety[i]));
                    break;
                default:
                    throw new IncorrectInputException("Nieznany symbol strategii");
            }
        }
        return listaPartii;
    }

    private OkragWyborczy[] wczytajOkregiWyborcze(int liczbaOkregow, int liczbaPartii) throws IncorrectInputException {
        OkragWyborczy[] okregi = new OkragWyborczy[liczbaOkregow];
        for (int i = 0; i < liczbaOkregow; i++) {
            okregi[i] = new OkragWyborczy(i + 1, liczbaPartii, in.nextInt());
        }
        return okregi;
    }

    private void wczytajListePartyjna(Bajtocja baj, Partia partia, OkragWyborczy okrag) throws IncorrectInputException {
        ArrayList<Kandydat> lista = new ArrayList<Kandydat>(okrag.liczbaWyborcow / 10);
        for (int j = 0; j < okrag.liczbaWyborcow / 10; j++) {
            String imie = in.next();
            String nazwisko = in.next();
            int numerOkregu = in.nextInt();
            String nazwaPartii = in.next();
            int pozycjaNaLiscie = in.nextInt();

            if (!nazwaPartii.equals(partia.nazwaPartii) || numerOkregu != okrag.numerOkregu
                || pozycjaNaLiscie != j + 1) {
                    throw new IncorrectInputException("Kandydaci w nieprawidlowej kolejnosci " + 
                    nazwaPartii + " " + partia.nazwaPartii + " " + Integer.toString(numerOkregu) + " " 
                    + Integer.toString(okrag.numerOkregu) + " " + Integer.toString(pozycjaNaLiscie) +
                    " " + Integer.toString(j + 1) + " " + imie + " " + nazwisko);
            }

            int cechy[] = new int[baj.liczbaCech];

            for (int i = 0; i < baj.liczbaCech; i++) {
                cechy[i] = in.nextInt();
            }

            Kandydat k = new Kandydat(baj.liczbaCech, imie, nazwisko, numerOkregu, nazwaPartii, pozycjaNaLiscie, cechy);

            lista.add(k);
        }
        okrag.addListaPartyjna(partia.nazwaPartii, lista);
    }

    private void wczytajKandydatow(Bajtocja baj) throws IncorrectInputException {
        for (int i = 1; i <= baj.liczbaOkregow; i++) {
            Iterator<Partia> it = baj.getPartiaIterator();
            while (it.hasNext()) {
                wczytajListePartyjna(baj, it.next(), baj.getOkragByNumer(i));
            }
        }
    }

    private int[] wczytajLiczby(int ile) {

        int[] liczby = new int[ile];
        for (int j = 0; j < ile; j++) {
            liczby[j] = in.nextInt();
        }
        return liczby;
    }

    private Wyborca wczytajWyborce(Bajtocja baj) throws IncorrectInputException {
        String imie = in.next(), nazwisko = in.next();
        int numerOkregu = in.nextInt(), typWyborcy = in.nextInt();
        OkragWyborczy okrag = baj.getOkragByNumer(numerOkregu);
        switch (typWyborcy) {
            case 1:
                return new ZelaznyPartyjny(imie, nazwisko, okrag, in.next());
            case 2:
                return new ZelaznyKandydata(imie, nazwisko, okrag, in.next(), in.nextInt());
            case 3:
                return new MinimalizujacyJednocechowy(imie, nazwisko, okrag, in.nextInt());
            case 4:
                return new MaksymalizujacyJednocechowy(imie, nazwisko, okrag, in.nextInt());
            case 5:
                return new Wszechstronny(imie, nazwisko, okrag, this.wczytajLiczby(baj.liczbaCech));
            case 6:
                return new MinimalizujacyJednocechowyPartia(imie, nazwisko, okrag, in.nextInt(), in.next());
            case 7:
                return new MaksymalizujacyJednocechowyPartia(imie, nazwisko, okrag, in.nextInt(), in.next());
            case 8:
                return new WszechstronnyPartia(imie, nazwisko, okrag, this.wczytajLiczby(baj.liczbaCech), in.next());
            default:
                throw new IncorrectInputException("Nieznany typ wyborcy");
        }

    }

    private void wczytajWyborcow(Bajtocja baj) throws IncorrectInputException {
        Wyborca wyb;
        for (int i = 1; i <= baj.liczbaOkregow; i++) {
            for (int j = 0; j < baj.getOkragByNumer(i).liczbaWyborcow; j++) {
                wyb = wczytajWyborce(baj); 
                if (wyb.okrag.numerOkregu != i) throw new IncorrectInputException("Błędny numer okręgu wyborcy");
                baj.getOkragByNumer(i).addWyborca(wyb);
            }
        }
    }

    private void wczytajDzialania(Bajtocja baj) {
        for (int i = 0; i < baj.liczbaDzialan; i++) {
            baj.addDzialanie(new Dzialanie(this.wczytajLiczby(baj.liczbaCech)));
        }
    }

    public Input(String fileName) throws FileNotFoundException {
        in = new Scanner(new File(fileName));
    }

    public Bajtocja wczytaj() throws IncorrectInputException {
        Bajtocja baj = wczytajBajtocje(); // 1 - 6 wiersz

        wczytajKandydatow(baj); // nastepne duzo linii
        wczytajWyborcow(baj); // nastepne duzo linii // to nie koniec
        wczytajDzialania(baj); // kolejne d linii


        return baj;
    }

}