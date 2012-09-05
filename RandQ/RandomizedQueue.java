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
        assert capacity >= N && capacity > 0;
        Item[] newq = (Item[]) new Object[capacity];
        for (int i = 0; i < N; i++)
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
        exchange(q, StdRandom.uniform(N), --N);
        Item result = q[N];
        q[N] = null;
        if (N * 4 < q.length && q.length > 1)
            resize(q.length >> 1);
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
        private int count = N;
        private int[] idx;

        public RandomizedQueueIterator() {
            idx = new int[count];
            for (int i = 0; i < count; i++)
                idx[i] = i;
            StdRandom.shuffle(idx);
        }

        public boolean hasNext() {
            return count > 0;
        }

        public Item next() {
            if (!hasNext())
                throw new NoSuchElementException();
            return q[idx[--count]];
        }

        public void remove() {
            throw new UnsupportedOperationException("Can't remove");
        }
    }

    // For manual testing. An integer argument is pushed down. A dash pops off
    // a random item in the queue. Good test sequences are
    // java -ea RandomizedQueue 0 - 1 - 2
    // java RandomizedQueue 1 - 2 - 3 - 4 5 6 7 8 9 10 11 12 13 14
    public static void main(String[] args) {
        RandomizedQueue<Integer> q = new RandomizedQueue<Integer>();
        for (String arg: args) {
            if (arg.equals("-"))
                System.out.print(q.dequeue() + " ");
            else
                q.enqueue(Integer.parseInt(arg));
        }
        System.out.println("\nNumbers remaining in the queue: " + q.size());
        System.out.println("Remaining in the queue:");
        int count = 0;
        Iterator<Integer> it = q.iterator();
        int i = 0;
        while (it.hasNext()) {
            i = it.next();
            count++;
            System.out.print(i + " ");
        }
        System.out.println("\nFound " + count + " numbers.");
        System.out.println("Shown again for checking iterator consistency:");
        count = 0;
        for (int j: q) {
            count++;
            System.out.print(j + " ");
        }
        System.out.println("\nFound " + count + " numbers.");
    }
}
