package wybory;

import java.util.ArrayList;

public class Dzialanie {
    private ArrayList<Integer> zmianaCech;
    public Dzialanie(ArrayList<Integer> zmianaCech) {
        this.zmianaCech = zmianaCech;
    }
    public void modyfikujWagiWyborcy(ArrayList<Integer> wagiWyborcy) {
        for (int w : wagiWyborcy) {
            //w += zmianaCech 
        }
    }
}