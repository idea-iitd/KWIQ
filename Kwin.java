import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Kwin{

	public static Set<Integer> answerSet = new HashSet<Integer>();
	public static int theta = 5;
	public static boolean bigNode = true;
	public static boolean degreeBound = true;

	public static void main(String[] args) {

		String company = args[0];
	    File file = new File(company);

		int startTimestamp = Integer.parseInt(args[1]);
	    int endTimestamp = startTimestamp + 86400*(Integer.parseInt(args[2]));
		theta = Integer.parseInt(args[3]);

		// int startTimestamp = -1;
	    // int endTimestamp =   Integer.MAX_VALUE;

	    Edge[] edges;
		int[] maxDegree;
		int maxVertex = 0;
		int numEdges = 0;
		long tempTime = 0;
		long iTime = 0;
		long dTime = 0;
		long dbTime = 0;
		long totalTime = 0;

	    try {

	        Scanner scan = new Scanner(file);
	        numEdges = scan.nextInt();
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
			        		tempTime = System.nanoTime();
		        			polarInsert(vertex1, vertex2, vertices);
		        			iTime += (System.nanoTime()-tempTime);
	        			}
		        	}else{
		        		if(vertices[vertex1].neighbours.contains(vertex2)){
			        		tempTime = System.nanoTime();
			        		polarRemoval(vertex1, vertex2, vertices);
			        		dTime += (System.nanoTime()-tempTime);
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
		        		if(vertices[vertex1].neighbours.contains(vertex2)){
			        		vertices[vertex1].neighbours.remove(new Integer(vertex2));
			        		vertices[vertex2].neighbours.remove(new Integer(vertex1));
		        		}
		        	}
	        	}
	        }
			
			totalTime = (dbTime + iTime + dTime);

			System.out.println("Invariant Core:");
			for(Integer ic: answerSet){
				System.out.println(ic);
			}

	        System.out.println("Query Time(ms): " + TimeUnit.NANOSECONDS.toMillis(totalTime));

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

		int v, w, du, pu, dw, pw;
		for(i = 0; i < n; i++){
			v = vert[i];
			vertices[v].k = degree[v];
			degPlus[v] = true;

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
					vertices[v].polarity.put(u, true);
					vertices[u].polarity.put(v, false);
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

	public static void polarInsert(int vertex1, int vertex2, Vertex[] vertices){

		vertices[vertex1].neighbours.add(vertex2);
		vertices[vertex2].neighbours.add(vertex1);

		boolean search = false;
		int root = vertex1;
		int theOther = vertex2;
		if(vertices[vertex1].k > vertices[vertex2].k){
			root = vertex2;
			theOther = vertex1;
			vertices[vertex2].mcd++;
			vertices[vertex2].degPlus++;
			vertices[vertex1].polarity.put(vertex2, false);
			vertices[vertex2].polarity.put(vertex1, true);

		}else if(vertices[vertex1].k == vertices[vertex2].k){

			search = true;

			vertices[vertex1].mcd++;
			vertices[vertex2].mcd++;

			if(vertices[vertex1].degPlus > vertices[vertex2].degPlus){
				vertices[vertex1].polarity.put(vertex2, true);
				vertices[vertex2].polarity.put(vertex1, false);
			}else{
				root = vertex2;
				theOther = vertex1;
				vertices[vertex1].polarity.put(vertex2, false);
				vertices[vertex2].polarity.put(vertex1, true);
			}

			vertices[root].degPlus++;

		}else{
			vertices[vertex1].mcd++;
			vertices[vertex1].degPlus++;
			vertices[vertex1].polarity.put(vertex2, true);
			vertices[vertex2].polarity.put(vertex1, false);

		}

		if(bigNode){
			if(vertices[root].k >= theta){
	            return;
	        }
		}

		int rootK = vertices[root].k;

		HashSet<Integer> vc = new HashSet<Integer>();
		HashSet<Integer> roots = new HashSet<Integer>();

		colorInsert(vertices, root, rootK, vc, roots);

		if(vc.size()==0 && search && (!bigNode || rootK<theta) && rootK>0){
			
			LinkedHashSet<Integer> q = new LinkedHashSet<Integer>();

			int startVertex = theOther;
			int endVertex = root;

			q.add(startVertex);

			boolean reachable = false;

			Iterator it = q.iterator();

			while(it.hasNext()){

				int bfsVertex = (Integer) it.next();
				// it.remove();

				for(Integer neighbour: vertices[bfsVertex].neighbours){

					if(vertices[bfsVertex].polarity.get(neighbour) && vertices[neighbour].k==rootK){

						if(neighbour==endVertex){
							reachable = true;
							break;
						}else{
							q.add(neighbour);
						}
					}

				}

				if(reachable){
					break;
				}

			}

			if(reachable){
				vertices[theOther].polarity.put(root, true);
				vertices[root].polarity.put(theOther, false);
				vertices[root].degPlus--;
				vertices[theOther].degPlus++;
			}

			return;

		}

		reColorInsert(vertices, rootK, vc, roots, root);

		if(vc.size()!=0 && search && (!bigNode || rootK<theta) && rootK>0){

			LinkedHashSet<Integer> q = new LinkedHashSet<Integer>();

			int startVertex = theOther;
			int endVertex = root;

			q.add(startVertex);

			boolean reachable = false;

			Iterator it = q.iterator();

			while(it.hasNext()){

				int bfsVertex = (Integer) it.next();
				// it.remove();

				for(Integer neighbour: vertices[bfsVertex].neighbours){

					if(vertices[bfsVertex].polarity.get(neighbour) && vc.contains(neighbour)){

						if(neighbour==endVertex){
							reachable = true;
							break;
						}else{
							q.add(neighbour);
						}
					}

				}

				if(reachable){
					break;
				}

			}

			if(reachable){
				vertices[theOther].polarity.put(root, true);
				vertices[root].polarity.put(theOther, false);
				vertices[root].degPlus--;
				vertices[theOther].degPlus++;
			}

		}
		
		updateInsert(vertices, rootK, vc);

	}

	public static void colorInsert(Vertex[] vertices, int root, int k, HashSet<Integer> vc,
		HashSet<Integer> roots){
		LinkedList<Integer> q = new LinkedList<>();
		HashSet<Integer> qSet = new HashSet<>();

		int w, i;
		q.addLast(root);
		qSet.add(root);

		while(q.size() != 0){
			w = q.getFirst();
			q.removeFirst();
			qSet.remove(w);

			if(vertices[w].degPlus + vertices[w].degStar > k){

				for(Integer x: vertices[w].neighbours){

					if(vertices[x].k == k && vertices[w].polarity.get(x)){

						vertices[x].degStar++;

						if(!vc.contains(x) && !qSet.contains(x)){

							q.add(x);
							qSet.add(x);
						}
					}
				}

				vc.add(w);
				roots.remove(w);

			}else{
				roots.add(w);
			}

		}

	}

	public static void reColorInsert(Vertex[] vertices, int k, HashSet<Integer> vc,
		HashSet<Integer> roots, int root){
		int i, j, u;

		LinkedList<Integer> q = new LinkedList<>();
		HashSet<Integer> qSet = new HashSet<>();

		if(vertices[root].degPlus + vertices[root].degStar <= k){
			q.add(root);
			qSet.add(root);
		}

		for(Integer w: roots){
			vertices[w].degPlus = vertices[w].degPlus + vertices[w].degStar;
			vertices[w].degStar = 0;

			for (Integer neighbour: vertices[w].neighbours) {
				if(vc.contains(neighbour) && !vertices[w].polarity.get(neighbour)){
					vertices[neighbour].degPlus--;

					vertices[neighbour].polarity.put(w, false);
					vertices[w].polarity.put(neighbour, true);

					if(vertices[neighbour].degPlus + vertices[neighbour].degStar <= k){
						if(!qSet.contains(neighbour)){
							q.add(neighbour);
							qSet.add(neighbour);
						}
					}

				}
			}
		}

		while(q.size()!=0){
			u = q.getFirst();
			q.removeFirst();
			qSet.remove(u);

			vertices[u].degStar = 0;

			vc.remove(u);

			for(Integer neighbour: vertices[u].neighbours){

				if(vc.contains(neighbour)){

					if(vertices[u].polarity.get(neighbour)){
						vertices[neighbour].degStar--;
						if(vertices[neighbour].degPlus + vertices[neighbour].degStar <= k){
							if(!qSet.contains(neighbour)){
								q.add(neighbour);
								qSet.add(neighbour);
							}
						}
					}else{
						vertices[neighbour].degPlus--;
						vertices[u].degPlus++;
						vertices[neighbour].polarity.put(u, false);
						vertices[u].polarity.put(neighbour, true);

						if(vertices[neighbour].degPlus + vertices[neighbour].degStar <= k){
							if(!qSet.contains(neighbour)){
								q.add(neighbour);
								qSet.add(neighbour);
							}
						}
					}
				}

			}

		}

	}

	public static void updateInsert(Vertex[] vertices, int k, HashSet<Integer> vc){

		for(Integer w: vc){
			vertices[w].k += 1;
		}

		for(Integer vStar: vc){
			calculateMCD(vertices, vStar);

			for(Integer neighbour: vertices[vStar].neighbours){

				if(vertices[neighbour].k==k+1 && !vc.contains(neighbour)){
					vertices[neighbour].mcd++;
				}
			}
			vertices[vStar].degStar = 0;
		}

	}

	public static void polarRemoval(int vertex1, int vertex2, Vertex[] vertices){

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

			if(vertices[vertex1].polarity.get(vertex2)){
				vertices[vertex1].degPlus--;
			}else{
				vertices[vertex2].degPlus--;
			}

		}else{
			vertices[root].mcd--;

			vertices[root].degPlus--;
		}

		vertices[vertex1].polarity.remove(vertex2);
		vertices[vertex2].polarity.remove(vertex1);

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

		    for (Integer neighbour: vertices[w].neighbours) {

				if(vertices[neighbour].k>=k && !vertices[w].polarity.get(neighbour)){

					vertices[neighbour].degPlus--;
					vertices[w].degPlus++;
					vertices[w].polarity.put(neighbour, true);
					vertices[neighbour].polarity.put(w, false);

				}

				if(vc.contains(neighbour) && !vertices[w].polarity.get(neighbour)){

					vertices[neighbour].degPlus--;
					vertices[w].degPlus++;
					vertices[w].polarity.put(neighbour, true);
					vertices[neighbour].polarity.put(w, false);

				}

		    }

		    it.remove();

		}

	}

}
