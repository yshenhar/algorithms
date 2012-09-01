import java.util.NoSuchElementException;
import java.util.Iterator;

public class RandomizedQueue<Item> implements Iterable<Item> {

    private static final int INIT_CAPACITY = 2;
    private Item[] q;
    private int N = 0;

    // construct an empty randomized queue
    @SuppressWarnings("unchecked")
    public RandomizedQueue() {
        assert INIT_CAPACITY > 0;
        q = (Item[]) new Object[INIT_CAPACITY]; // Will produce warning. Ignore.
    }

    // is the queue empty?
    public boolean isEmpty() {
        return N == 0;
    }

    // return the number of items on the queue
    public int size() {
        return N;
    }

    // Resize array when necessary
    @SuppressWarnings("unchecked")
    private void resize(int capacity) {
        assert capacity >= N;
        Item[] newq = (Item[]) new Object[capacity];
        for (int i = 0; i < q.length; i++)
            newq[i] = q[i];
        q = newq;
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null)
            throw new NullPointerException("Queue does not support nulls");
        if (N == q.length)
            resize(N << 1);
        q[N++] = item;
    }

    /* delete and return a random item
     *
     * Swap the random item with the last item in the list and return that
     * item because otherwise we'd get holes in the array and wouldn't know
     * where they are.
     */
    public Item dequeue() {
        if (N == 0)
            throw new NoSuchElementException("Empty queue");
        /* The advantage of adding 1 below is that the following lines won't
        halve the size of the array unless it's at least 4 in the worst case
        when N == 0. This is important because 4 is the smallest integer i for
        which i >> 2 > 0. Finally note that as N gets large, these lines only
        downsize when the array is a quarter full.
        */
        else if ((N + 1) * 4 < q.length)
            resize(N >> 1);
        exchange(q, StdRandom.uniform(N), --N);
        Item result = q[N];
        q[N] = null;
        return result;
    }

    // Swap entries i & j in the array a.
    private void exchange(Item[] a, int i, int j) {
        if (i == j)
            return;
        Item swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }


    // return (but do not delete) a random item
    public Item sample() {
        if (N == 0)
            throw new NoSuchElementException("Empty queue");
        return q[StdRandom.uniform(N)];
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomizedQueueIterator();
    }

    private class RandomizedQueueIterator implements Iterator<Item> {
        private int tail = N;

        public boolean hasNext() {
            return tail > 0;
        }

        public Item next() {
            if (tail == 0)
                throw new NoSuchElementException();
            exchange(q, StdRandom.uniform(tail), --tail);
            return q[tail];
        }

        public void remove() {
            throw new UnsupportedOperationException("Can't remove");
        }
    }
}
