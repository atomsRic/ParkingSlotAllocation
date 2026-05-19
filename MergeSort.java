public class MergeSort {
    public static void mergeSort(ParkingSlot[] arr, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);
            merge(arr, left, mid, right);
        }
    }
    
    private static void merge(ParkingSlot[] arr, int left, int mid, int right) {
        ParkingSlot[] temp = new ParkingSlot[right - left + 1];
        int i = left, j = mid + 1, k = 0;
        
        while (i <= mid && j <= right) {
            if (arr[i].getDistance() <= arr[j].getDistance()) {
                temp[k++] = arr[i++];
            } else {
                temp[k++] = arr[j++];
            }
        }
        
        while (i <= mid) temp[k++] = arr[i++];
        while (j <= right) temp[k++] = arr[j++];
        
        for (k = 0; k < temp.length; k++) {
            arr[left + k] = temp[k];
        }
    }
}
