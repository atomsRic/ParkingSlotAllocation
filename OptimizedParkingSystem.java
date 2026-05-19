import java.io.*;
import java.util.*;
import javax.swing.JTextArea;

public class OptimizedParkingSystem {
    private Graph graph;
    private MinHeap priorityQueue;
    private HashTable slotTable;
    private int totalSlots = 50;
    private int availableSlots;
    private static final String RECORDS_FILE = "parking_records_optimized.txt";
    
    public OptimizedParkingSystem(int n) {
        totalSlots = n;
        availableSlots = n;
        graph = new Graph(n + 11);
        priorityQueue = new MinHeap(n);
        slotTable = new HashTable(n * 2);
        loadFromFile();
        rebuildStructures();
        System.out.println("📁 Records file: " + new File(RECORDS_FILE).getAbsolutePath());
    }
    
    private void initializeSlots() {
        for (int i = 0; i < totalSlots; i++) {
            int slotId = i + 1;
            ParkingSlot slot = new ParkingSlot(slotId, "A", i/10 + 1, 10.0 + i * 0.5);
            slotTable.put(slotId, slot);
            priorityQueue.insert(slot.getDistance(), slotId);
        }
    }
    
    public void initializeFacilityGraph() {
        for (int i = 0; i < totalSlots; i++) {
            int slotId = i + 1;
            int aisle = i / 10 + 1;
            graph.addEdge(0, aisle, 5.0 + aisle * 2);
            graph.addEdge(aisle, slotId, 2.0 + (i % 10));
        }
    }
    
    public int findOptimalSlot(int entryPoint) {
        if (priorityQueue.isEmpty()) return -1;
        int bestSlot = priorityQueue.extractMin();
        ParkingSlot slot = slotTable.get(bestSlot);
        if (slot != null && slot.isAvailable()) {
            slot.allocate();
            availableSlots--;
            updateHeapAfterAllocation(bestSlot);
            return bestSlot;
        }
        return findOptimalSlot(entryPoint);
    }
    
    public String getShortestPath(int start, int end) {
        return graph.dijkstra(start, end);
    }
    
    public void freeSlot(int slotId) {
        ParkingSlot slot = slotTable.get(slotId);
        if (slot != null && !slot.isAvailable()) {
            slot.free();
            availableSlots++;
            priorityQueue.insert(slot.getDistance(), slotId);
        }
    }
    
    private void updateHeapAfterAllocation(int allocatedSlot) {
        priorityQueue = new MinHeap(totalSlots);
        for (int i = 1; i <= totalSlots; i++) {
            ParkingSlot slot = slotTable.get(i);
            if (slot != null && slot.isAvailable()) {
                priorityQueue.insert(slot.getDistance(), i);
            }
        }
    }
    
    public void displaySortedSlotsGUI(JTextArea area) {
        List<ParkingSlot> available = new ArrayList<>();
        for (int i = 1; i <= totalSlots; i++) {
            ParkingSlot slot = slotTable.get(i);
            if (slot != null && slot.isAvailable()) available.add(slot);
        }
        ParkingSlot[] arr = available.toArray(new ParkingSlot[0]);
        MergeSort.mergeSort(arr, 0, arr.length - 1);
        area.append("AVAILABLE SLOTS (Merge Sorted by Distance):\n");
        for (ParkingSlot slot : arr) {
            area.append(String.format("  Slot %d (Floor %d, %.1fm)\n", 
                slot.getId(), slot.getFloor(), slot.getDistance()));
        }
    }
    
    public void showStatsGUI(JTextArea area) {
        area.append("STATS:\n");
        area.append(String.format("Total Slots: %d\n", totalSlots));
        area.append(String.format("Available: %d\n", availableSlots));
        area.append(String.format("Occupied: %d\n", totalSlots - availableSlots));
        area.append("Algorithms: Dijkstra O((V+E)logV) + MergeSort O(nlogn)\n");
        area.append("Data: Graph + MinHeap + HashTable\n");
    }
    
    // FILE OPERATIONS
    public void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RECORDS_FILE))) {
            writer.println("slot_id,status,floor,distance,type");
            for (int i = 1; i <= totalSlots; i++) {
                ParkingSlot slot = slotTable.get(i);
                if (slot != null) {
                    String status = slot.isAvailable() ? "available" : "occupied";
                    writer.printf("%d,%s,%d,%.1f,%s\n", 
                        slot.getId(), status, slot.getFloor(), slot.getDistance(), slot.getType());
                }
            }
        } catch (IOException e) {
            System.err.println("Save error: " + e.getMessage());
        }
    }
    
    public void loadFromFile() {
        File file = new File(RECORDS_FILE);
        if (!file.exists()) {
            initializeSlots();
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // header
            String line;
            slotTable = new HashTable(totalSlots * 2);
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    int id = Integer.parseInt(parts[0]);
                    ParkingSlot slot = new ParkingSlot(id, parts[4], 
                        Integer.parseInt(parts[2]), Double.parseDouble(parts[3]));
                    if ("occupied".equals(parts[1])) slot.occupy();
                    else availableSlots++;
                    slotTable.put(id, slot);
                }
            }
        } catch (Exception e) {
            initializeSlots();
        }
    }
    
    public void reloadFromFile() {
        loadFromFile();
        rebuildStructures();
    }
    
    private void rebuildStructures() {
        initializeFacilityGraph();
        priorityQueue = new MinHeap(totalSlots);
        for (int i = 1; i <= totalSlots; i++) {
            ParkingSlot slot = slotTable.get(i);
            if (slot != null && slot.isAvailable()) {
                priorityQueue.insert(slot.getDistance(), i);
            }
        }
    }
}
