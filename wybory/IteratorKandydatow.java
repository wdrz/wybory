package wybory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

abstract class IteratorKandydatow {
    abstract Kandydat next();
    abstract boolean hasNext();
}


class IterKandydatowWszyskichPodstawowegoOkregu extends IteratorKandydatow {
    private final Iterator<ArrayList<Kandydat>> itList;
    private Iterator<Kandydat> itKand;

    public IterKandydatowWszyskichPodstawowegoOkregu(Iterable<ArrayList<Kandydat>> itList) {
        this.itList = itList.iterator();
        this.itKand = this.itList.next().iterator();
    }

    public Kandydat next() {
        if (itKand.hasNext()) {
            return itKand.next();
        }
        if (itList.hasNext()) {
            this.itKand = itList.next().iterator();
            return itKand.next();
        }

        throw new NoSuchElementException();
    }

    public boolean hasNext() {
        return itKand.hasNext() || itList.hasNext();
    }
}


class IterKandydatowWszyskich extends IteratorKandydatow {

    private final IterKandydatowWszyskichPodstawowegoOkregu okrag1;
    private final IterKandydatowWszyskichPodstawowegoOkregu okrag2;

    public IterKandydatowWszyskich(Iterable<ArrayList<Kandydat>> itList1, Iterable<ArrayList<Kandydat>> itList2) {
        this.okrag1 = new IterKandydatowWszyskichPodstawowegoOkregu(itList1);
        this.okrag2 = new IterKandydatowWszyskichPodstawowegoOkregu(itList2);
    }

    public Kandydat next() {
        if (okrag1.hasNext()) return okrag1.next();
        if (okrag2.hasNext()) return okrag2.next();

        throw new NoSuchElementException();
    }

    public boolean hasNext() {
        return okrag1.hasNext() || okrag2.hasNext();
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