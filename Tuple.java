import java.util.Comparator;

public class Tuple implements Comparable<Tuple>{

	public int vertex;
	public double rank;

	public Tuple(int vertex, double rank){
		this.vertex = vertex;
		this.rank = rank;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Tuple)) {
			return false;
		}
		Tuple p = (Tuple) o;
		if (this.vertex == p.vertex) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return this.vertex;
	}

	@Override
	public int compareTo(Tuple cp) {

		if(this.rank < cp.rank){
			return -1;
		}
		return 1;
			
	}

}
