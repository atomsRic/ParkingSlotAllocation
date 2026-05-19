public class HashTable {

    private static final int     INITIAL_CAPACITY = 101;
    private static final double  LOAD_FACTOR      = 0.7;

    // Sentinel object to mark deleted slots during probing
    private static final ParkingSlot DELETED = new ParkingSlot(-1, "", -1, -1);

    private ParkingSlot[] table;
    private int[]         keys;
    private int           size;
    private int           capacity;

    public HashTable(int initialCapacity) {
        capacity = nextPrime(Math.max(initialCapacity, INITIAL_CAPACITY));
        table    = new ParkingSlot[capacity];
        keys     = new int[capacity];
        size     = 0;
    }

    // ---------------------------------------------------------------
    // Public API
    // ---------------------------------------------------------------

    /**
     * Insert or update a slot keyed by slotId — O(1) average.
     * Uses linear probing to resolve collisions.
     */
    public void put(int key, ParkingSlot value) {
        if ((double) size / capacity >= LOAD_FACTOR) resize();

        int idx = hash(key);
        int firstDeleted = -1;

        for (int i = 0; i < capacity; i++) {
            int probe = (idx + i) % capacity;

            if (table[probe] == null) {
                // Empty bucket — insert here (or at first deleted marker)
                int insertAt = (firstDeleted != -1) ? firstDeleted : probe;
                table[insertAt] = value;
                keys[insertAt]  = key;
                size++;
                return;
            }

            if (table[probe] == DELETED) {
                if (firstDeleted == -1) firstDeleted = probe;
                continue;
            }

            if (keys[probe] == key) {
                // Key already exists — update in place
                table[probe] = value;
                return;
            }
        }

        // Table full (shouldn't happen with resize) — fall back to first deleted
        if (firstDeleted != -1) {
            table[firstDeleted] = value;
            keys[firstDeleted]  = key;
            size++;
        }
    }

    /**
     * Retrieve a slot by slotId — O(1) average.
     * Returns null if not found.
     */
    public ParkingSlot get(int key) {
        int idx = hash(key);

        for (int i = 0; i < capacity; i++) {
            int probe = (idx + i) % capacity;

            if (table[probe] == null)    return null;          // definitively absent
            if (table[probe] == DELETED) continue;             // skip tombstone
            if (keys[probe]  == key)     return table[probe];  // found
        }
        return null;
    }

    /**
     * Remove a slot by slotId — O(1) average.
     * Marks the bucket with a DELETED sentinel to preserve probe chains.
     */
    public void remove(int key) {
        int idx = hash(key);

        for (int i = 0; i < capacity; i++) {
            int probe = (idx + i) % capacity;

            if (table[probe] == null)    return;
            if (table[probe] == DELETED) continue;

            if (keys[probe] == key) {
                table[probe] = DELETED;   // tombstone — keeps probe chain intact
                size--;
                return;
            }
        }
    }

    public int size() { return size; }

    // ---------------------------------------------------------------
    // Internal helpers
    // ---------------------------------------------------------------

    private int hash(int key) {
        return (key % capacity + capacity) % capacity;
    }

    /** Resize to the next prime roughly double the current capacity. */
    private void resize() {
        int           oldCapacity = capacity;
        ParkingSlot[] oldTable    = table;
        int[]         oldKeys     = keys;

        capacity = nextPrime(oldCapacity * 2);
        table    = new ParkingSlot[capacity];
        keys     = new int[capacity];
        size     = 0;

        for (int i = 0; i < oldCapacity; i++) {
            if (oldTable[i] != null && oldTable[i] != DELETED) {
                put(oldKeys[i], oldTable[i]);
            }
        }
    }

    /** Find the next prime >= n (keeps hash distribution uniform). */
    private int nextPrime(int n) {
        while (!isPrime(n)) n++;
        return n;
    }

    private boolean isPrime(int n) {
        if (n < 2)  return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }
}