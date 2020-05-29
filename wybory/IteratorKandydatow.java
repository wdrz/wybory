package wybory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

abstract class IteratorKandydatow {
    abstract Kandydat next();
    abstract boolean hasNext();
}

class IterKandydatowWszyskich extends IteratorKandydatow {
    private final Iterator<ArrayList<Kandydat>> itList1;
    private Iterator<Kandydat> itKand1;

    private final Iterator<ArrayList<Kandydat>> itList2;
    private Iterator<Kandydat> itKand2;

    private boolean pierwszaLista = true;

    public IterKandydatowWszyskich(Iterator<ArrayList<Kandydat>> itList1, Iterator<ArrayList<Kandydat>> itList2) {
        this.itList1 = itList1;
        this.itKand1 = itList1.next().iterator();
        this.itList2 = itList2;
        this.itKand2 = itList2.next().iterator();  
    }

    public Kandydat next() {
        if (pierwszaLista) {
            if (itKand1.hasNext()) {
                return itKand1.next();
            }
            if (itList1.hasNext()) {
                this.itKand1 = itList1.next().iterator();
                return itKand1.next();
            }
            pierwszaLista = false;
        }

        if (itKand2.hasNext()) {
            return itKand2.next();
        }
        if (itList2.hasNext()) {
            this.itKand2 = itList2.next().iterator();
            return itKand2.next();
        }

        throw new NoSuchElementException();
    }

    public boolean hasNext() {
        return itKand1.hasNext() || itList1.hasNext() || itKand2.hasNext() || itList2.hasNext();
    }

}

class IterKandydatowPartii extends IteratorKandydatow {
    private final Iterator<Kandydat> it1;
    private final Iterator<Kandydat> it2;
    public IterKandydatowPartii(Iterator<Kandydat> it1, Iterator<Kandydat> it2) {
        this.it1 = it1;
        this.it2 = it2;
    }
    public Kandydat next() {
        if (it1.hasNext()) {
            return it1.next();
        }
        if (it2.hasNext()) {
            return it2.next();
        }
        throw new NoSuchElementException();
    }

    public boolean hasNext() {
        return it1.hasNext() || it2.hasNext();
    }
}