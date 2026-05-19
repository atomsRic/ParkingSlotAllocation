public class ParkingSlot {
    private int id;
    private String type;
    private int floor;
    private double distance;
    private boolean available;
    
    public ParkingSlot(int id, String type, int floor, double distance) {
        this.id = id;
        this.type = type;
        this.floor = floor;
        this.distance = distance;
        this.available = true;
    }
    
    // Getters
    public int getId() { return id; }
    public String getType() { return type; }
    public int getFloor() { return floor; }
    public double getDistance() { return distance; }
    public boolean isAvailable() { return available; }
    
    public void allocate() { available = false; }
    public void occupy() { available = false; }
    public void free() { available = true; }
}
