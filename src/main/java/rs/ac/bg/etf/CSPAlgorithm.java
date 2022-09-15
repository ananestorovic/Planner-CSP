package rs.ac.bg.etf;

import org.javatuples.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSPAlgorithm {

    public boolean complete(HashMap<String, List<Pair<String, Boolean>>> hm) {


        for (Map.Entry<String, List<Pair<String, Boolean>>> entry : hm.entrySet()) {
            for (Pair<String, Boolean> p : entry.getValue()) {
                if (p.getValue1() == false) return false;

            }
        }
        //teams have all assigned meetings
        return true;

    }


}
