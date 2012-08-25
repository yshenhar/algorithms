/******************************************************************************
 * Author: William Schwartz
 * Written: 2012-08-25
 *
 * Compilation: $ javac Percolation.java
 * Testing: $ java Percolation
 *
 * Data type to model physical percolation (say of water through concrete).
 ******************************************************************************/

import java.lang.IndexOutOfBoundsException;


public class Percolation {

	private int N; // Length of one side of the grid.
	private boolean[] open;
	private WeightedQuickUnionUF paths;
	private int virtual_top;
	private int virtual_bottom;

	// create N-by-N grid, with all sites blocked
	public Percolation(int N) {
		this.N = N;
		open = new boolean[N^2];
		/*	Include two extra spots in the connections list two hold the virtual
			top and virtual bottom spots.
		*/
		paths = new WeightedQuickUnionUF(N^2 + 2);
		virtual_top = indexOf(N, N) + 1;
		virtual_bottom = indexOf(N, N) + 2;
		for (int col = 1; col <= N; col++)
			paths.union(virtual_top, indexOf(1, col));
		for (int col = 1; col <= N; col++)
			paths.union(virtual_bottom, indexOf(N, col));
	}

	// is site (row i, column j) open?
	public boolean isOpen(int i, int j) {
		return open[indexOf(i, j)];
	}

	// is site (row i, column j) full?
	public boolean isFull(int i, int j) {
		return !open[indexOf(i, j)];
	}

	// does the system percolate?
	public boolean percolates() {
		return paths.connected(virtual_top, virtual_bottom);
	}


	/* The rest of the API:

	public void open(int i, int j)		// open site (row i, column j) if it is not already
	*/

	/* Convert grid coordinates of the form (x, y) where x,y in {1,...,N}
	to an array index. E.g., indexOf(1,1) == 0; indexOf(N, N) = N^2 - 1.

	Assume the grid is in row-major form.
	*/
	private int indexOf(int row, int col) throws IndexOutOfBoundsException {
		if (row <= 0 || row > N || col <= 0 || col >= N)
			throw new IndexOutOfBoundsException(
					"(" + row + ", " + col + ") out of bounds" +
					"for " + N + "^2 grid.");
		return (row - 1) * N + (col - 1);
	}
}
