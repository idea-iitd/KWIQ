import java.util.*;

public class Vertex implements Comparable{

	public int k = 0;
	public int mcd = 0;
	public int degree = 0;
    public int degPlus = 0;
    public int degStar = 0;
    public int rank = 0;
	public LinkedList<Integer> neighbours = new LinkedList<Integer>();
    public HashMap<Integer, Boolean> polarity = new HashMap<>();

    public Node node;

	@Override
    public int compareTo(Object compareVertex) {
        return this.degPlus - ((Vertex) compareVertex).degPlus;
    }

    @Override
    public String toString() {
        return this.k + " " + this.mcd + " " + this.degree;
    }

}
