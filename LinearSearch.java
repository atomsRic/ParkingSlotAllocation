/**
 * Time complexity: O(n) per search (vs O(log n) with MinHeap)
 */
public class LinearSearch {

    /**
     * Find the index (0-based) of the available slot with the
     * smallest distance value.
     */
    public static int findClosestAvailable(ParkingSlot[] slots, int count) {
        int bestIndex = -1;
        double bestDist = Double.MAX_VALUE;

        for (int i = 0; i < count; i++) {
            if (slots[i] != null
                    && slots[i].isAvailable()
                    && slots[i].getDistance() < bestDist) {
                bestDist = slots[i].getDistance();
                bestIndex = i;
            }
        }
        return bestIndex;
    }
}
