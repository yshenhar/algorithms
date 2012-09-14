import java.util.Arrays;

public class Board {

    private final short[][] blocks;
    private final int dim;
    private int cachedManhattan = -1;

    // construct a board from an N-by-N array of blocks
    // (where blocks[i][j] = block in row i, column j)
    public Board(int[][] blocks) {
        this.blocks = copySquareArray(blocks);
        dim = blocks.length;
    }

    private Board(short[][] blocks) {
        this.blocks = copySquareArray(blocks);
        dim = blocks.length;
    }

    // Copy a square array.
    private short[][] copySquareArray(int[][] original) {
        int len = original.length;
        short[][] copy = new short[len][len];
        for (int row = 0; row < len; row++) {
            assert original[row].length == len;
            for (int col = 0; col < len; col++)
                // Assignment guarantees dim < 128, so casting is safe.
                copy[row][col] = (short) original[row][col];
        }
        return copy;
    }

    private short[][] copySquareArray(short[][] original) {
        int len = original.length;
        short[][] copy = new short[len][len];
        for (int row = 0; row < len; row++) {
            assert original[row].length == len;
            for (int col = 0; col < len; col++)
                copy[row][col] = original[row][col];
        }
        return copy;
    }

    // board dimension N
    public int dimension()
    {   return dim; }

    // number of blocks out of place
    public int hamming() {
        int count = 0;
        int expectation = 1;
        for (int row = 0; row < dim; row++)
            for (int col = 0; col < dim; col++)
                if (blocks[row][col] != expectation++)
                    count++;
        // Subtract 1 for the empty block.
        return count - 1;
    }

    // sum of Manhattan distances between blocks and goal
    public int manhattan() {
        if (cachedManhattan >= 0)
            return cachedManhattan;
        int sum = 0;
        int goalRow, goalCol;
        int value = 0;
        for (int row = 0; row < dim; row++) {
            for (int col = 0; col < dim; col++) {
                value = blocks[row][col];
                if (value == 0)
                    continue;
                // Because 0 is not at (0, 0).
                goalRow = (value - 1) / dim;
                goalCol = (value - 1) % dim;
                sum += Math.abs(row - goalRow) + Math.abs(col - goalCol);
            }
        }
        cachedManhattan = sum;
        return sum;
    }

    // is this board the goal board?
    public boolean isGoal() {
        if (blocks[dim - 1][dim - 1] != 0)
            return false;
        int value = 1;
        for (int row = 0; row < dim; row++)
            for (int col = 0; col < dim; col++)
                if (blocks[row][col] != value++
                        && (row != dim - 1 || col != dim - 1))
                    return false;
        return true;
    }

    // a board obtained by exchanging two adjacent blocks in the same row
    public Board twin() {
        short[][] copy = copySquareArray(blocks);
        if (dim <= 1)
            return new Board(copy);
        // Find zero so that we don't exchange with the blank
        // This looks like a O(dim^2) algorithm, but on average it should finish
        // in O(1).
        int row = 0;
        int col = 0;
        short value = 0;
        short lastValue = blocks[0][0];
        zerosearch:
        for (row = 0; row < dim; row++) {
            for (col = 0; col < dim; col++) {
                value = blocks[row][col];
                // Check col>0 because swap must occur on same row
                if (value != 0 && lastValue != 0 && col > 0)
                    break zerosearch;
                lastValue = value;
            }
        }
        copy[row][col] = lastValue;
        copy[row][col - 1] = value;
        return new Board(copy);
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        Board that = (Board) y;
        if (this.blocks.length != that.blocks.length) return false;
        for (int row = 0; row < dim; row++)
            if (!Arrays.equals(this.blocks[row], that.blocks[row]))
                return false;
        return true;
    }

    private short[][] swap(short[][] array, int fromRow, int fromCol, int toRow,
                         int toCol) {
        short[][] copy = copySquareArray(array);
        short tmp = copy[toRow][toCol];
        copy[toRow][toCol] = copy[fromRow][fromCol];
        copy[fromRow][fromCol] = tmp;
        return copy;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        Queue<Board> q = new Queue<Board>();
        // Find zero
        int row = 0;
        int col = 0;
        zerosearch:
        for (row = 0; row < dim; row++) {
            for (col = 0; col < dim; col++) {
                if (blocks[row][col] == 0) {
                    break zerosearch;
                }
            }
        }
        if (row > 0)
            q.enqueue(new Board(swap(blocks, row, col, row - 1, col)));
        if (row < dim - 1)
            q.enqueue(new Board(swap(blocks, row, col, row + 1, col)));
        if (col > 0)
            q.enqueue(new Board(swap(blocks, row, col, row, col - 1)));
        if (col < dim - 1)
            q.enqueue(new Board(swap(blocks, row, col, row, col + 1)));
        return q;
    }

    // string representation of the board (in the output format specified below)
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(dim + "\n");
        for (int row = 0; row < dim; row++) {
            for (int col = 0; col < dim; col++) {
                s.append(String.format("%2d ", blocks[row][col]));
            }
            s.append("\n");
        }
        return s.toString();
    }
}
