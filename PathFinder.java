/**
 * Time complexity: O(1) per path query  (vs O((V+E) log V) Dijkstra)
 */
public class PathFinder {

    private int[]    slotFloor;    // slotFloor[slotId]    = floor number
    private double[] slotDistance; // slotDistance[slotId] = distance from entrance
    private int      totalSlots;

    public PathFinder(int totalSlots) {
        this.totalSlots   = totalSlots;
        this.slotFloor    = new int[totalSlots + 1];    // index 1..totalSlots
        this.slotDistance = new double[totalSlots + 1];
    }

    /** Register a slot's floor and distance so paths can be built. */
    public void registerSlot(int slotId, int floor, double distance) {
        if (slotId >= 1 && slotId <= totalSlots) {
            slotFloor[slotId]    = floor;
            slotDistance[slotId] = distance;
        }
    }

    /**
     * Return a human-readable path from the entrance to the given slot.
     */
    public String getPath(int entryPoint, int slotId) {
        if (slotId < 1 || slotId > totalSlots) {
            return "Invalid slot ID";
        }
        int    floor = slotFloor[slotId];
        double dist  = slotDistance[slotId];

        return String.format("Entrance (0) -> Aisle %d -> Slot %d  [distance: %.1f m]",
                             floor, slotId, dist);
    }
}
