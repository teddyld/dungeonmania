package dungeonmania;

import java.io.Serializable;
import java.util.Comparator;

public class MyComparator implements Comparator<ComparableCallback>, Serializable {
    @Override
    public int compare(ComparableCallback arg0, ComparableCallback arg1) {
        return Integer.compare(arg0.getV(), arg1.getV());
    }
}
