/******************************************************************************
 * Author: William Schwartz
 * Written: 2012-08-26
 *
 * Compilation: $ javac PercolationStats.java
 * Running an experiment: $ java PercolationStats
 *
 * Data type to model physical percolation (say of water through concrete).
 ******************************************************************************/

public class PercolationStats {
	private static final double Z95 = 1.96;
	private final int N;
	private double[] results;

	// perform T independent computational experiments on an N-by-N grid
		//if (N <= 0 || T <= 0)
		//	throw IllegalArgumentException("Nonnegative arguments");
	public PercolationStats(int N, int T) {
		int i, j, n;
		Percolation p;
		this.N = N;
		results = new double[T];
		for (int t = 0; t < T; t++) {
			n = 0;
			p = new Percolation(N);
			while (!p.percolates()) {
				do {
					i = StdRandom.uniform(1, N + 1);
					j = StdRandom.uniform(1, N + 1);
				} while(p.isOpen(i, j));
				p.open(i, j);
				n++;
			}
			results[t] = ((double) n) / (N*N);
		}
	}

	// sample mean of percolation threshold
	public double mean() {
		return StdStats.mean(results);
	}

	// sample standard deviation of percolation threshold
	public double stddev() {
		if (results.length <= 1)
			return Double.NaN;
		return StdStats.stddev(results);
	}

	private double ci(int side) {
		return mean() + side * (Z95 * stddev()) / Math.sqrt(results.length);
	}

	public static void main(String[] args) {
		int N, T;
		PercolationStats p;
		N = Integer.parseInt(args[0]);
		T = Integer.parseInt(args[1]);
		p = new PercolationStats(N, T);
		System.out.println("mean                    = " + p.mean());
		System.out.println("stddev                  = " + p.stddev());
		System.out.println("95% confidence interval = " + p.ci(-1) + ", " + p.ci(1));
	}
}
