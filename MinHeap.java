import java.util.*;

public class MinHeap {
    private List<Node> heap;
    
    public MinHeap(int capacity) {
        heap = new ArrayList<>();
    }
    
    public void insert(double priority, int slotId) {
        heap.add(new Node(priority, slotId));
        siftUp(heap.size() - 1);
    }
    
    public int extractMin() {
        if (heap.isEmpty()) return -1;
        int result = heap.get(0).slotId;
        int last = heap.size() - 1;
        heap.set(0, heap.get(last));
        heap.remove(last);
        siftDown(0);
        return result;
    }
    
    private void siftUp(int idx) {
        while (idx > 0) {
            int parent = (idx - 1) / 2;
            if (heap.get(parent).priority <= heap.get(idx).priority) break;
            swap(parent, idx);
            idx = parent;
        }
    }
    
    private void siftDown(int idx) {
        int size = heap.size();
        while (true) {
            int left = 2 * idx + 1;
            int right = 2 * idx + 2;
            int smallest = idx;
            
            if (left < size && heap.get(left).priority < heap.get(smallest).priority)
                smallest = left;
            if (right < size && heap.get(right).priority < heap.get(smallest).priority)
                smallest = right;
            
            if (smallest == idx) break;
            swap(idx, smallest);
            idx = smallest;
        }
    }
    
    private void swap(int i, int j) {
        Node temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }
    
    public boolean isEmpty() { return heap.isEmpty(); }
    public int size() { return heap.size(); }
    
    private static class Node {
        double priority;
        int slotId;
        Node(double p, int id) { priority = p; slotId = id; }
    }
}
