public class Pair {
	int thread;
	int iteration;
	public Pair(int t, int i) {
		thread = t;
		iteration = i;
	}
	public boolean equals(Object p2) {
		if (this == p2) return true;
		if (!(p2 instanceof Pair)) return false;
		Pair p2Pair = (Pair)p2;
		return thread == p2Pair.thread && iteration == p2Pair.iteration;
	}
	
	public String toString() {
		return thread + ", " + iteration;
	}
}