public class Triple {
	// thread, iteration, method id. begins with 1;
	int thread;
	int iteration;
	int method;
	public Triple(int t, int i, int m) {
		thread = t;
		iteration = i;
		method = m;
	}
	
	public boolean equals(Object t2) {
		if (this == t2) return true;
		if (!(t2 instanceof Triple)) return false;
		Triple t2Triple = (Triple)t2;
		return thread == t2Triple.thread && iteration == t2Triple.iteration && method == t2Triple.method;
	}
	
	public String toString() {
		return thread + ", " + iteration + ", " + method;
	}
	
}