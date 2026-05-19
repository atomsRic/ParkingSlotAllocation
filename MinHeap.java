public class MinHeap {

    private double[] priorities;
    private int[]    slotIds;
    private int      size;
    private int      capacity;

    public MinHeap(int capacity) {
        this.capacity   = capacity + 1;   // small buffer
        this.priorities = new double[this.capacity];
        this.slotIds    = new int[this.capacity];
        this.size       = 0;
    }

    // ---------------------------------------------------------------
    // Public API
    // ---------------------------------------------------------------

    /** Insert a slot with the given distance priority — O(log n). */
    public void insert(double priority, int slotId) {
        if (size >= capacity) growArray();
        priorities[size] = priority;
        slotIds[size]    = slotId;
        siftUp(size);
        size++;
    }

    /**
     * Remove and return the slot ID with the smallest distance — O(log n).
     * Returns -1 if the heap is empty.
     */
    public int extractMin() {
        if (size == 0) return -1;
        int result = slotIds[0];
        size--;
        priorities[0] = priorities[size];
        slotIds[0]    = slotIds[size];
        siftDown(0);
        return result;
    }

    /**
     * FIX — Remove a specific slot by its ID — O(n) scan + O(log n) fix.
     * This replaces the full heap-rebuild that was previously called after
     * every allocation. Worst case is still O(n) for the scan, but we only
     * pay that cost once per removal instead of rebuilding from scratch.
     */
    public void remove(int slotId) {
        // Find the slot's position in the heap array
        int idx = -1;
        for (int i = 0; i < size; i++) {
            if (slotIds[i] == slotId) { idx = i; break; }
        }
        if (idx == -1) return; // not found, nothing to do

        // Replace with last element and restore heap property
        size--;
        priorities[idx] = priorities[size];
        slotIds[idx]    = slotIds[size];

        if (idx < size) {
            siftUp(idx);
            siftDown(idx);
        }
    }

    public boolean isEmpty() { return size == 0; }
    public int size()        { return size; }

    // ---------------------------------------------------------------
    // Heap maintenance
    // ---------------------------------------------------------------

    private void siftUp(int idx) {
        while (idx > 0) {
            int parent = (idx - 1) / 2;
            if (priorities[parent] <= priorities[idx]) break;
            swap(parent, idx);
            idx = parent;
        }
    }

    private void siftDown(int idx) {
        while (true) {
            int left     = 2 * idx + 1;
            int right    = 2 * idx + 2;
            int smallest = idx;

            if (left  < size && priorities[left]  < priorities[smallest]) smallest = left;
            if (right < size && priorities[right] < priorities[smallest]) smallest = right;

            if (smallest == idx) break;
            swap(idx, smallest);
            idx = smallest;
        }
    }

    private void swap(int i, int j) {
        double tmpP  = priorities[i]; priorities[i] = priorities[j]; priorities[j] = tmpP;
        int    tmpId = slotIds[i];    slotIds[i]    = slotIds[j];    slotIds[j]    = tmpId;
    }

    /** Double the backing arrays when capacity is exceeded. */
    private void growArray() {
        capacity *= 2;
        double[] newP  = new double[capacity];
        int[]    newId = new int[capacity];
        System.arraycopy(priorities, 0, newP,  0, size);
        System.arraycopy(slotIds,    0, newId, 0, size);
        priorities = newP;
        slotIds    = newId;
    }
}