package datastructure;

/**
 * Created by huangwaleking on 5/1/17.
 */
public class IDSorter implements Comparable<IDSorter> {
    int id; double p;
    public IDSorter(int id, double p) { this.id = id; this.p = p; }
    public IDSorter(int id, int p) { this.id = id; this.p = p; }

    public final int compareTo (IDSorter o2) {

        double otherP = o2.p;

        if (p > o2.p) {
            return -1;
        }
        else if (p < o2.p) {
            return 1;
        }

        // p == otherP, sort by ID

        int otherID = o2.id;

        if (id > otherID) { return -1; }
        else if (id < otherID) { return 1; }

        return 0;
    }

    public int getID() {return id;}
    public double getWeight() {return p;}

    /** Reinitialize an IDSorter */
    public void set(int id, double p) { this.id = id; this.p = p; }
}

