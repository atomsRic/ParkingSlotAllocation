/**
 * Time complexity: O(n²)  (vs O(n log n) with MergeSort)
 */
public class BubbleSort {

    /**
     * Sort the given array of ParkingSlot objects in-place
     * by distance, ascending.
     */
    public static void bubbleSort(ParkingSlot[] arr, int count) {
        for (int i = 0; i < count - 1; i++) {
            for (int j = 0; j < count - i - 1; j++) {
                if (arr[j] != null && arr[j + 1] != null
                        && arr[j].getDistance() > arr[j + 1].getDistance()) {
                    ParkingSlot temp = arr[j];
                    arr[j]     = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }
}
