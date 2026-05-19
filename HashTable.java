public class HashTable {
    private ParkingSlot[] table;
    private int size;
    private static final int INITIAL_CAPACITY = 101;
    
    public HashTable(int capacity) {
        size = 0;
        table = new ParkingSlot[INITIAL_CAPACITY];
    }
    
    public void put(int key, ParkingSlot value) {
        int index = hash(key);
        table[index] = value;
        size++;
    }
    
    public ParkingSlot get(int key) {
        int index = hash(key);
        return table[index];
    }
    
    private int hash(int key) {
        return (key % INITIAL_CAPACITY + INITIAL_CAPACITY) % INITIAL_CAPACITY;
    }
}
