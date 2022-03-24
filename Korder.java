import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Korder{

	public static Node[] headOrder; //head
	public static Node[] tailOrder; //tail
	public static TreeMap<Integer, Double>[] orderA;
	public static int maxK = 1000;
	public static Set<Integer> answerSet = new HashSet<Integer>();
	public static int theta = 3;
	public static long startTime;
	public static long endTime;
	public static long insertionTime = 0;
	public static long deletionTime = 0;
	public static long currentTime;
	public static boolean degreeBound = true;
	public static boolean bigNode = true;

	public static void main(String[] args) {

		String company = args[0];
	    File file = new File(company);

		int startTimestamp = Integer.parseInt(args[1]);
	    int endTimestamp = startTimestamp + 86400*(Integer.parseInt(args[2]));
		theta = Integer.parseInt(args[3]);

	    // int startTimestamp = -1;
	    // int endTimestamp = 35;

	    Edge[] edges;
	    int maxVertex = 0;
		int[] maxDegree;
		long tempTime = 0;
		long dbTime = 0;
		long totalTime = 0;

	    try {

	        Scanner scan = new Scanner(file);

	        int numEdges = scan.nextInt();

	        edges = new Edge[numEdges];
	        int counter = 0;
	        while (scan.hasNextLine()) {

				try{
						int type = scan.nextInt();

			            int vertex1 = scan.nextInt();
			            if(vertex1>maxVertex){
			            	maxVertex=vertex1;
			            }

			            int vertex2 = scan.nextInt();
			            if(vertex2>maxVertex){
			            	maxVertex=vertex2;
			            }

			            int timestamp = scan.nextInt();

			            edges[counter] = new Edge(vertex1, vertex2, timestamp, type);
			            counter++;
							}catch(Exception e){
									break;
							}

	        }
	        scan.close();

	        Vertex[] vertices = new Vertex[maxVertex+1];
	        for(int i=0; i<vertices.length; i++){
	        	vertices[i] = new Vertex();
	        }

			maxDegree = new int[maxVertex+1];
			int[] currentDegree = new int[maxVertex+1];
	        boolean decomposed = false;
	        for(int i=0 ; i<edges.length; i++){

	        	// System.err.println(i);

	        	int vertex1 = edges[i].vertex1;
	        	int vertex2 = edges[i].vertex2;
	        	int timestamp = edges[i].timestamp;
	        	int type = edges[i].type;

	        	if(timestamp>endTimestamp){
	        		break;
	        	}else if(timestamp>startTimestamp){
	        		if(!decomposed){

						if(degreeBound){

							tempTime = System.nanoTime();

							for(int j=i; j<edges.length; j++){

								int vertex1d = edges[j].vertex1;
								int vertex2d = edges[j].vertex2;
								int typed = edges[j].type;
								int timestampd = edges[j].timestamp;

								if(timestampd > endTimestamp){
									break;
								}

								if(typed==1){
									currentDegree[vertex1d]++;
									if(vertices[vertex1d].neighbours.size() + currentDegree[vertex1d] > maxDegree[vertex1d]){
										maxDegree[vertex1d] = vertices[vertex1d].neighbours.size() + currentDegree[vertex1d];
									}
									currentDegree[vertex2d]++;
									if(vertices[vertex2d].neighbours.size() + currentDegree[vertex2d] > maxDegree[vertex2d]){
										maxDegree[vertex2d] = vertices[vertex2d].neighbours.size() + currentDegree[vertex2d];
									}
								}else{
									currentDegree[vertex1d]--;
									if(maxDegree[vertex1d]==0){
										maxDegree[vertex1d] = vertices[vertex1d].neighbours.size();
									}
									currentDegree[vertex2d]--;
									if(maxDegree[vertex2d]==0){
										maxDegree[vertex2d] = vertices[vertex2d].neighbours.size();
									}
								}
							}

							dbTime = (System.nanoTime()-tempTime);

						}

	        			decomposed = true;
	        			coreDecomposition(vertices);
	        			for(int j=0; j<vertices.length; j++){
	        				if(vertices[j].k >= theta){
	        					answerSet.add(j);
	        				}
	        			}

	        			// System.out.println(answerSet.size());

	        			startTime = System.nanoTime();

		        		if(answerSet.size()==0){
							break;
						}

	        		}

					if(degreeBound){
						if(maxDegree[vertex1] < theta || maxDegree[vertex2] < theta){
							continue;
						}
					}

		        	if(type==1){
		        		if(!vertices[vertex1].neighbours.contains(vertex2)){

		        			currentTime = System.nanoTime();
		        			orderInsert(vertex1, vertex2, vertices);
		        			insertionTime += (System.nanoTime() - currentTime);

		        		}
		        	}else{

		        		if(vertices[vertex1].neighbours.contains(vertex2)){

		        			currentTime = System.nanoTime();
			        		orderRemoval(vertex1, vertex2, vertices);
			        		deletionTime += (System.nanoTime() - currentTime);

		        		}
		        		if(answerSet.size()==0){
							break;
						}
		        	}

	        	}else{
		        	if(type==1){
		        		if(!vertices[vertex1].neighbours.contains(vertex2)){
		        			vertices[vertex1].neighbours.add(vertex2);
		        			vertices[vertex2].neighbours.add(vertex1);
		        		}
		        	}else{
		        		vertices[vertex1].neighbours.remove(new Integer(vertex2));
		        		vertices[vertex2].neighbours.remove(new Integer(vertex1));
		        	}
	        	}
	        }

	        endTime = System.nanoTime();

	        totalTime = (dbTime + insertionTime + deletionTime);

					// System.out.println(company);

	    	System.out.println(TimeUnit.NANOSECONDS.toMillis(totalTime));

	        // System.out.println(TimeUnit.NANOSECONDS.toMillis(insertionTime+deletionTime));

	        // System.out.println("insertionTime " + TimeUnit.NANOSECONDS.toMillis(insertionTime));
	        // System.out.println("deletionTime " + TimeUnit.NANOSECONDS.toMillis(deletionTime));

			// for(int j=0; j<vertices.length; j++){
			// 	if(vertices[j].k!=0){
			// 		System.out.println(j + "=" + vertices[j].k + "," + vertices[j].mcd);	
			// 	}
			// }

	    }
	    catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }

	}

	public static void coreDecomposition(Vertex[] vertices){

		int i, tmp, maxDegree = 0;
		int start = 0;
		int n = vertices.length;
		int[] degree = new int[n];
		int[] pos = new int[n];
		int[] vert = new int[n];

		for(i=0; i<n; i++){
			vertices[i].degree = vertices[i].neighbours.size();
			degree[i] = vertices[i].degree;
			vertices[i].degPlus = degree[i];
			if(degree[i] > maxDegree){
				maxDegree = degree[i];
			}
		}

		int[] bin = new int[maxDegree + 1];
		for(i = 0; i <= maxDegree; i++){
			bin[i] = 0;
		}

		for(i = 0; i < n; i++){
			bin[degree[i]] += 1;
		}

		for(i = 0; i <= maxDegree; i++){
			tmp = bin[i];
			bin[i] = start;
			start += tmp;
		}

		for(i = 0; i < n; i++){
			pos[i] = bin[degree[i]];
			vert[pos[i]] = i;
			bin[degree[i]] += 1;
		}

		for(i = maxDegree; i > 0; i--){
			bin[i] = bin[i-1];
		}
		bin[0] = 0;

		boolean[] degPlus = new boolean[n];

		headOrder = new Node[maxK+1];
		tailOrder = new Node[maxK+1];
		orderA = new TreeMap[maxK+1];

		for(i=0; i<=maxK; i++){
			headOrder[i] = null;
			tailOrder[i] = null;
			orderA[i] = new TreeMap<>();
		}

		int v, w, du, pu, dw, pw;
		for(i = 0; i < n; i++){
			v = vert[i];
			vertices[v].k = degree[v];
			degPlus[v] = true;

			if(tailOrder[vertices[v].k] != null){
				Node temp = new Node(tailOrder[vertices[v].k], null, v);
				vertices[v].node = temp;
				tailOrder[vertices[v].k].next = temp;
				tailOrder[vertices[v].k] = temp;
			}else{
				Node temp = new Node(null, null, v);
				vertices[v].node = temp;
				tailOrder[vertices[v].k] = temp;
				headOrder[vertices[v].k] = temp;
			}

			if (vertices[v].k < theta){
				orderA[vertices[v].k].put(v, new Double(orderA[vertices[v].k].size()));
			}

			for(Integer u: vertices[v].neighbours){
				if(degree[u] > degree[v]){
					du = degree[u]; pu = pos[u];
					pw = bin[du]; w = vert[pw];
					if(u != w){
						pos[u] = pw; vert[pu] = w;
						pos[w] = pu; vert[pw] = u;
					}
					bin[du]++;
					degree[u]--;
				}
				if(!degPlus[u]){
					vertices[u].degPlus--;
				}
			}

		}

		if(bigNode){
			for(i=0; i<n; i++){
	            vertices[i].k = Math.min(vertices[i].k, theta);
	        }
		}

		for(i=0; i<n; i++){
			calculateMCD(vertices, i);
		}

	}

	public static void calculateMCD(Vertex[] vertices, int index){
		vertices[index].mcd = 0;

		for(Integer w: vertices[index].neighbours){
			if(vertices[w].k >= vertices[index].k){
				vertices[index].mcd += 1;
			}
		}
	}

	public static void orderInsert(int vertex1, int vertex2, Vertex[] vertices){

		vertices[vertex1].neighbours.add(vertex2);
		vertices[vertex2].neighbours.add(vertex1);

		int root = vertex1;
		if(vertices[vertex1].k > vertices[vertex2].k){
			root = vertex2;
		}
		int rootK = vertices[root].k;

		if(bigNode && rootK >= theta){
			vertices[vertex1].mcd++;
			vertices[vertex2].mcd++;
            return;
		}

		Node headK = headOrder[rootK];
		Node tailK = tailOrder[rootK];
		PriorityQueue<Tuple> minHeapB;

		if(vertices[vertex1].k == vertices[vertex2].k){

			if(orderA[rootK].get(vertex1) > orderA[rootK].get(vertex2)){
				root = vertex2;
			}

			vertices[vertex1].mcd++;
			vertices[vertex2].mcd++;
		}else{
			vertices[root].mcd++;
		}

		vertices[root].degPlus++;

		if(vertices[root].degPlus<=rootK){
			return;
		}else{
			minHeapB = new PriorityQueue<>();
			minHeapB.add(new Tuple(root, orderA[rootK].get(root)));
		}

		Node headKPrime = null;
		Node tailKPrime = null;
		LinkedHashSet<Integer> vc = new LinkedHashSet<Integer>();

		LinkedList<Integer> buffer = new LinkedList<>();
		double lastRank = orderA[rootK].get(headK.vertex);

		Node curr = headK;

		while(curr!=null){
			int vi = curr.vertex;
			double viRank = orderA[rootK].get(vi);

			if(vertices[vi].degStar + vertices[vi].degPlus > rootK){

				vc.add(vi);
				for(Integer neighbour: vertices[vi].neighbours){

					if(vertices[neighbour].k == rootK){
						double neighbourRank = orderA[rootK].get(neighbour);

					 	if(viRank < neighbourRank){
							vertices[neighbour].degStar++;

							if(vertices[neighbour].degStar==1){
								minHeapB.add(new Tuple(neighbour, neighbourRank));
							}

						}
					}
				}

				curr = curr.next;

				if(curr == null){
					for(Integer buf: buffer){
						orderA[rootK].put(buf, lastRank+1);
						lastRank = lastRank + 1;
					}
				}

			}else if(vertices[vi].degStar == 0){

				Tuple vj = minHeapB.peek();

				if(vj==null){
					if(tailKPrime != null){
						tailKPrime.next = vertices[vi].node;
						vertices[vi].node.prev = tailKPrime;
						tailKPrime = tailK;
						// tailKPrime.next = null;
					}else{
						headKPrime = vertices[vi].node;
						// headKPrime.prev = null;
						tailKPrime = tailK;
						// tailKPrime.next = null;
					}

					double offset = (viRank - lastRank)/(buffer.size()+1);
					int counter = 1;
					for(Integer buf: buffer){
						orderA[rootK].put(buf,lastRank + offset*counter);
						counter++;
					}

					curr = null;
				}else{

					if(tailKPrime != null){
						tailKPrime.next = vertices[vi].node;
						vertices[vi].node.prev = tailKPrime;

						tailKPrime = vertices[vj.vertex].node.prev;

						// tailKPrime.next = null;

					}else{
						headKPrime = vertices[vi].node;
						// headKPrime.prev = null;
						tailKPrime = vertices[vj.vertex].node.prev;
						// tailKPrime.next = null;
					}
					curr = vertices[vj.vertex].node;

					double offset = (viRank - lastRank)/(buffer.size()+1);
					int counter = 1;
					for(Integer buf: buffer){
						orderA[rootK].put(buf,lastRank + offset*counter);
						counter++;
					}

					lastRank = orderA[rootK].get(vj.vertex);

				}

				buffer.clear();

			}else{

				if(tailKPrime != null){
					Node temp = new Node(tailKPrime, null, vi);
					vertices[vi].node = temp;
					tailKPrime.next = temp;
					tailKPrime = temp;
				}else{
					Node temp = new Node(null, null, vi);
					vertices[vi].node = temp;
					tailKPrime = temp;
					headKPrime = temp;
				}

				buffer.add(vi);

				vertices[vi].degPlus = vertices[vi].degPlus + vertices[vi].degStar;
				vertices[vi].degStar = 0;
				tailKPrime = removeCandidates(vertices, vi, rootK, vc, tailKPrime, minHeapB, buffer);
				curr = curr.next;

				if(curr == null){
					for(Integer buf: buffer){
						orderA[rootK].put(buf, lastRank+1);
						lastRank = lastRank + 1;
					}
				}

			}

			minHeapB.remove(new Tuple(vi, viRank));

		}

		LinkedList<Integer> vcList = new LinkedList<>(vc);
		Iterator<Integer> iterator = vcList.descendingIterator();
		while (iterator.hasNext()){
			int vStar = iterator.next();
			vertices[vStar].degStar = 0;
			vertices[vStar].k++;

			if(headOrder[rootK+1]!=null){
				if((!bigNode || (rootK+1)<theta)){
					orderA[rootK+1].put(vStar, orderA[rootK+1].get(headOrder[rootK+1].vertex) -1);
				}
				orderA[rootK].remove(vStar);
				Node temp = new Node(null, headOrder[rootK+1], vStar);
				vertices[vStar].node = temp;
				headOrder[rootK+1].prev = temp;
				headOrder[rootK+1] = temp;
			}else{
				orderA[rootK+1].put(vStar, 0.0);
				orderA[rootK].remove(vStar);
				Node temp = new Node(null, null, vStar);
				vertices[vStar].node = temp;
				headOrder[rootK+1] = temp;
				tailOrder[rootK+1] = temp;
			}

		}

		if(tailKPrime!=null){
			tailKPrime.next = null;
			headKPrime.prev = null;
		}

		headOrder[rootK] = headKPrime;
		tailOrder[rootK] = tailKPrime;

		for(Integer vStar: vcList){
			calculateMCD(vertices, vStar);
			for(Integer neighbour: vertices[vStar].neighbours){
				if(vertices[neighbour].k==rootK+1 && !vc.contains(neighbour)){
					vertices[neighbour].mcd++;
				}
			}
		}

	}

	public static Node removeCandidates(Vertex[] vertices, int w, int k, LinkedHashSet<Integer> vc,
		Node tailKPrime, PriorityQueue<Tuple> minHeapB, LinkedList<Integer> buffer){

		LinkedList<Integer> q = new LinkedList<>();

		double wRank = orderA[k].get(w);

		for(Integer neighbour: vertices[w].neighbours){
			if(vc.contains(neighbour)){
				vertices[neighbour].degPlus--;
				if(vertices[neighbour].degPlus + vertices[neighbour].degStar <= k){
					q.addLast(neighbour);
				}
			}
		}

		while(q.size()!=0){

			int wPrime = q.getFirst();
			q.remove(new Integer(wPrime));
			vertices[wPrime].degPlus = vertices[wPrime].degPlus + vertices[wPrime].degStar;
			vertices[wPrime].degStar = 0;

			vc.remove(new Integer(wPrime));

			Node temp = new Node(tailKPrime, null, wPrime);
			vertices[wPrime].node = temp;
			tailKPrime.next = temp;
			tailKPrime = temp;

			buffer.add(wPrime);

			double wPrimeRank = orderA[k].get(wPrime);

			for(Integer neighbour: vertices[wPrime].neighbours){
				if(vertices[neighbour].k == k){

					double neighbourRank = orderA[k].get(neighbour);

					if(wRank < neighbourRank){
						vertices[neighbour].degStar--;

						if(vertices[neighbour].degStar == 0){

							minHeapB.remove(new Tuple(neighbour, neighbourRank));
						}

					}else if(wPrimeRank < neighbourRank && vc.contains(neighbour)){
						vertices[neighbour].degStar--;
						if(vertices[neighbour].degPlus + vertices[neighbour].degStar <= k && !q.contains(neighbour)){
							q.addLast(neighbour);
						}
					}else if(vc.contains(neighbour)){
						vertices[neighbour].degPlus--;
						if(vertices[neighbour].degPlus + vertices[neighbour].degStar <= k && !q.contains(neighbour)){
							q.addLast(neighbour);
						}
					}
				}
			}

		}

		return tailKPrime;

	}

	public static void orderRemoval(int vertex1, int vertex2, Vertex[] vertices){

		vertices[vertex1].neighbours.remove(new Integer(vertex2));
		vertices[vertex2].neighbours.remove(new Integer(vertex1));

		int root = vertex1;
		if(vertices[vertex1].k > vertices[vertex2].k){
			root = vertex2;
		}
		int rootK = vertices[root].k;

		if(vertices[vertex1].k == vertices[vertex2].k){
			vertices[vertex1].mcd--;
			vertices[vertex2].mcd--;

			if(!bigNode || rootK<theta){
				if(orderA[rootK].get(vertex1) < orderA[rootK].get(vertex2)){
					vertices[vertex1].degPlus--;
				}else{
					vertices[vertex2].degPlus--;
				}
			}

		}else{
			vertices[root].mcd--;
			vertices[root].degPlus--;

		}

		LinkedHashSet<Integer> vc = new LinkedHashSet<Integer>();

		reColorDelete(vertices, vertex1, vertex2, rootK, vc);

		updateDelete(vertices, vc, rootK);

	}

	public static void reColorDelete(Vertex[] vertices, int vertex1, int vertex2, int k,
		LinkedHashSet<Integer> vc){

		int i, j, u;
		LinkedList<Integer> roots = new LinkedList<>();

		if (vertices[vertex1].mcd == (k - 1) && vertices[vertex1].k==k){
			roots.add(vertex1);
		}

		if (vertices[vertex2].mcd == (k - 1) && vertices[vertex2].k==k){
			roots.add(vertex2);
		}

		for(Integer w: roots){
			LinkedList<Integer> q = new LinkedList<>();
			q.addLast(w);

			while(q.size() != 0){
				u = q.getFirst();
				q.remove(new Integer(u));

				if((vertices[u].mcd == (k - 1)) && !vc.contains(u)){
					for(Integer x: vertices[u].neighbours){

						if(vertices[x].k == k && vertices[x].mcd > (k - 1)){
							vertices[x].mcd -= 1;
							if(vertices[x].mcd == (k-1) && !vc.contains(u)){
								q.add(x);
							}
						}

					}
					vc.add(u);
				}
			}
		}

	}

	public static void updateDelete(Vertex[] vertices, LinkedHashSet<Integer> vc, int k){

		for(Integer w: vc){
			vertices[w].k--;
			if(vertices[w].k < theta){
				answerSet.remove(w);
			}
		}

		Iterator<Integer> it = vc.iterator();
		while (it.hasNext()) {
		    Integer w = it.next();

		    calculateMCD(vertices, w);

		    double wRank = 0;
		    if(!bigNode || k<theta){
		    	wRank = orderA[k].get(w);
		    }

		    vertices[w].degPlus = 0;

		    for (Integer neighbour: vertices[w].neighbours) {

		    	if((!bigNode || k<theta) && vertices[neighbour].k==k && orderA[k].get(neighbour) < wRank){
		    		vertices[neighbour].degPlus--;
		    	}

		    	if(vertices[neighbour].k>=k || vc.contains(neighbour)){
		    		vertices[w].degPlus++;
		    	}
		    }

		    it.remove();
		    Node toRemove = vertices[w].node;
		    if(toRemove.prev != null){
		    	toRemove.prev.next = toRemove.next;
		    }else{
		    	headOrder[k] = headOrder[k].next;
		    }

			if(toRemove.next!=null){
				toRemove.next.prev = toRemove.prev;
			}else{
				tailOrder[k] = tailOrder[k].prev;
			}

			if(tailOrder[k-1]==null){
				orderA[k-1].put(w, 0.0);
				orderA[k].remove(w);
				Node temp = new Node(null, null, w);
				vertices[w].node = temp;
				headOrder[k-1] = temp;
				tailOrder[k-1] = temp;
			}else{
				orderA[k-1].put(w, orderA[k-1].get(tailOrder[k-1].vertex) +1);
				orderA[k].remove(w);
			    Node temp = new Node(tailOrder[k-1], null, w);
			    vertices[w].node = temp;
			    tailOrder[k-1].next = temp;
			    tailOrder[k-1] = temp;
			}

		}

	}

}

