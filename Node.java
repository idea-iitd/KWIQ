import java.util.*;

public class Node{

	public Node prev;
	public Node next;
	public int vertex;

	public Node(Node prev, Node next, int vertex){
		this.prev = prev;
		this.next = next;
		this.vertex = vertex;
	}

}