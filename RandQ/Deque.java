import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Doubly linked list implementation of a Deque. Uses a sentinel.
 *
 * An industrial strength solution would need to check to see if an iterator
 * had begun to yield values, but then the deque was modified before emptying.
 * This could be accomplished with an instance-local switch that gets turned
 * on when an iterator is "open", turned off when it's empty, and detected
 * before mutating methods are called.
 */
public class Deque<Item> implements Iterable<Item> {

    private class Node {
        private Item item;
        private Node previous;
        private Node next;
    }

    private Node sentinel;
    private int N = 0;

    // construct an empty deque
    public Deque() {
        sentinel = new Node();
        sentinel.item = null;
        sentinel.previous = sentinel;
        sentinel.next = sentinel;
        assert invariants();
    }

    // is the deque empty?
    public boolean isEmpty() {
        return sentinel.next == sentinel;
    }

    // return the number of items on the deque
    public int size() {
        return N;
    }

    // insert the item at the front
    public void addFirst(Item item) {
        if (item == null)
            throw new NullPointerException();
        Node newFirst = new Node();
        newFirst.item = item;
        newFirst.previous = sentinel;
        newFirst.next = sentinel.next;
        sentinel.next.previous = newFirst;
        sentinel.next = newFirst;
        N++;
        assert invariants();
    }

    // insert the item at the end
    public void addLast(Item item) {
        if (item == null)
            throw new NullPointerException();
        Node newLast = new Node();
        newLast.item = item;
        newLast.previous = sentinel.previous;
        newLast.next = sentinel;
        sentinel.previous.next = newLast;
        sentinel.previous = newLast;
        N++;
        assert invariants();
    }

    // delete and return the item at the front
    public Item removeFirst() {
        if (isEmpty())
            throw new NoSuchElementException();
        Item result = sentinel.next.item;
        // This is why we initialize sentinel.next = sentinel;
        sentinel.next = sentinel.next.next;
        sentinel.next.previous = sentinel; // To avoid loitering
        N--;
        assert invariants();
        return result;
    }

    // delete and return the item at the end
    public Item removeLast() {
        if (isEmpty())
            throw new NoSuchElementException();
        Item result = sentinel.previous.item;
        // This is why we initialize sentinel.previous = sentinel;
        sentinel.previous = sentinel.previous.previous;
        sentinel.previous.next = sentinel; // To avoid loitering
        N--;
        assert invariants();
        return result;
    }

    // return an iterator over items in order from front to end
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<Item> {

        private Node cursor = sentinel;

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public boolean hasNext() {
            return cursor.next != sentinel;
        }

        public Item next() {
            if (!hasNext())
                throw new NoSuchElementException();
            cursor = cursor.next;
            return cursor.item;
        }
    }

    private boolean invariants() {
        // Check pointers
        if (sentinel.item != null)
            return false;
        if (N < 0)
            return false;
        else if (N == 0 || N == 1) {
            if (sentinel.next != sentinel.previous)
                return false;
            if (N == 0 && sentinel.next != sentinel)
                return false;
            if (N == 1 && sentinel.next == sentinel)
                return false;
        }
        else {
            if (sentinel.next == sentinel)
                return false;
            if (sentinel.previous == sentinel)
                return false;
        }

        // Check N
        int countForward = 0;
        for (Node cursor = sentinel; cursor.next != sentinel; cursor = cursor.next)
            countForward++;
        int countBackward = 0;
        for (Node cursor = sentinel; cursor.previous != sentinel;
                cursor = cursor.previous)
            countBackward++;
        if (N != countForward || N != countBackward)
            return false;

        return true;
    }

    /*
     * Check order of pushes and pops from both sides
     *
     * ascending is true if ascending (0 to max_byte), false for descending
     * inFirst is true if items go in by addFirst, false for addLast
     * inFirst is true if items come out by removeFirst, false for removeLast
     * return true if test passes
     */
    private static boolean testOrder(boolean ascending, boolean inFirst,
            boolean outFirst) {
        int max = 0xff; // Use an int to avoid arithmetic overflow
        int[] seq = new int[max];
        for (int i = 0; i < max; i++) {
            if (ascending)
                seq[i] = i;
            else
                seq[i] = max - i - 1;
        }
        Deque<Integer> deque = new Deque<Integer>();

        // Loop twice to make sure we can fill, empty, and refill the deque
        boolean[] twoThings = {false, true};
        for (boolean doItTwice: twoThings) {
            assert deque.size() == 0;

            for (int i : seq) {
                if (inFirst)
                    deque.addFirst(i);
                else
                    deque.addLast(i);
            }

            assert deque.size() == max;

            int j;
            if (ascending == inFirst) j = max - 1;
            else j = 0;
            for (int next: deque) {
                assert next == j;
                if (ascending == inFirst) j--;
                else j++;

                // Test that multiple iterators can work simultaneously
                int k;
                if (ascending == inFirst) k = max - 1;
                else k = 0;
                for (int nextk: deque) {
                    assert nextk == k;
                    if (ascending == inFirst) k--;
                    else k++;
                }
                if (ascending == inFirst) assert k == -1;
                else assert k == max;
            }
            if (ascending == inFirst) assert j == -1;
            else assert j == max;

            assert deque.size() == max;

            if (inFirst != outFirst) {
                for (int i : seq) {
                    if (outFirst)
                        assert deque.removeFirst() == i;
                    else
                        assert deque.removeLast() == i;
                }
            }
            else if (ascending) {
                for (int i = max - 1; i >= 0; i--) {
                    if (outFirst)
                        assert deque.removeFirst() == i;
                    else
                        assert deque.removeLast() == i;
                }
            }
            else {
                for (int i = 0; i < max; i++) {
                    if (outFirst)
                        assert deque.removeFirst() == i;
                    else
                        assert deque.removeLast() == i;
                }
            }

            assert deque.size() == 0;

        }

        return true;
    }


    public static void main(String[] args) {
        int total = 0;
        int passes = 0;
        boolean[] tf = {false, true};
        for (boolean ascending: tf) {
            for (boolean inFirst: tf) {
                for (boolean outFirst: tf) {
                    total++;
                    if (testOrder(ascending, inFirst, outFirst))
                        passes++;
                }
            }
        }
        System.err.println("Passed/total: " + passes + " / " + total);
    }
}
