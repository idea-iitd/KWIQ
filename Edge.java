import java.util.*;

public class Edge implements Comparable{

	public int vertex1;
	public int vertex2;
	public int timestamp;
	public int type; 

	public Edge(int vertex1, int vertex2, int timestamp, int type){
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;
		this.timestamp = timestamp;
		this.type = type;
	}

	@Override
    public int compareTo(Object compareTuple) {
        return this.timestamp - ((Edge) compareTuple).timestamp;
    }

    @Override
    public String toString() {
        return this.vertex1 + " " + this.vertex2 + " " + this.timestamp + " " + this.type;
    }

}