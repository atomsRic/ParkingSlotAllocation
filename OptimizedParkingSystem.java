import java.io.*;
import java.util.*;
import javax.swing.JTextArea;

public class OptimizedParkingSystem {

    private Graph graph;
    private MinHeap priorityQueue;
    private HashTable slotTable;
    private int totalSlots;
    private int availableSlots;

    // Per-operation timing accumulators (nanoseconds)
    private long totalAllocationTime = 0;
    private long totalLookupTime = 0;
    private long totalSortTime = 0;
    private long totalRoutingTime = 0;
    private int allocationCount = 0;
    private int lookupCount = 0;
    private int sortCount = 0;
    private int routingCount = 0;

    private static final String RECORDS_FILE = "parking_records_optimized.txt";

    // ---------------------------------------------------------------
    // Construction
    // ---------------------------------------------------------------

    public OptimizedParkingSystem(int n) {
        totalSlots = n;
        availableSlots = 0;
        graph = new Graph(n + 11);
        priorityQueue = new MinHeap(n);
        slotTable = new HashTable(n * 2);
        loadFromFile();
        rebuildStructures();
        System.out.println("Records file: " + new File(RECORDS_FILE).getAbsolutePath());
    }

    // ---------------------------------------------------------------
    // Initialization
    // ---------------------------------------------------------------

    private void initializeSlots() {
        availableSlots = 0;
        for (int i = 0; i < totalSlots; i++) {
            int slotId = i + 1;
            int floor = i / 10 + 1;
            double distance = 10.0 + i * 0.5;
            ParkingSlot slot = new ParkingSlot(slotId, "A", floor, distance);
            slotTable.put(slotId, slot);
            availableSlots++;
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

    // ---------------------------------------------------------------
    // Core operations (FIX 1: no heap rebuild)
    // ---------------------------------------------------------------

    /**
     * Allocate the nearest available slot.
     * extractMin() is O(log n) — the heap is NOT rebuilt afterward.
     */
    public int findOptimalSlot(int entryPoint) {
        long start = System.nanoTime();

        if (priorityQueue.isEmpty())
            return -1;

        // Keep extracting until we find a slot that is still marked available
        // (handles edge case where slot was manually occupied externally)
        while (!priorityQueue.isEmpty()) {
            int bestSlot = priorityQueue.extractMin();
            ParkingSlot slot = slotTable.get(bestSlot);
            if (slot != null && slot.isAvailable()) {
                slot.allocate();
                availableSlots--;
                // NO heap rebuild — extractMin already removed the slot
                totalAllocationTime += System.nanoTime() - start;
                allocationCount++;
                return bestSlot;
            }
        }
        return -1;
    }

    /**
     * Get shortest path via Dijkstra — O((V+E) log V).
     */
    public String getShortestPath(int start, int end) {
        long t = System.nanoTime();
        String path = graph.dijkstra(start, end);
        totalRoutingTime += System.nanoTime() - t;
        routingCount++;
        return path;
    }

    /**
     * Free a slot — re-inserts into heap with insert() O(log n).
     */
    public void freeSlot(int slotId) {
        long t = System.nanoTime();
        ParkingSlot slot = slotTable.get(slotId);
        if (slot != null && !slot.isAvailable()) {
            slot.free();
            availableSlots++;
            priorityQueue.insert(slot.getDistance(), slotId); // O(log n)
        }
        totalLookupTime += System.nanoTime() - t;
        lookupCount++;
    }

    // ---------------------------------------------------------------
    // Display (FIX 3: per-operation benchmarks)
    // ---------------------------------------------------------------

    public void displaySortedSlotsGUI(JTextArea area) {
        long start = System.nanoTime();

        ParkingSlot[] arr = new ParkingSlot[totalSlots];
        int count = 0;
        for (int i = 1; i <= totalSlots; i++) {
            ParkingSlot slot = slotTable.get(i);
            if (slot != null && slot.isAvailable())
                arr[count++] = slot;
        }
        ParkingSlot[] sorted = Arrays.copyOf(arr, count);
        MergeSort.mergeSort(sorted, 0, count - 1);

        totalSortTime += System.nanoTime() - start;
        sortCount++;

        area.append("AVAILABLE SLOTS (Merge Sorted by Distance):\n");
        for (int i = 0; i < count; i++) {
            area.append(String.format("  Slot %d (Floor %d, %.1f m)\n",
                    sorted[i].getId(), sorted[i].getFloor(), sorted[i].getDistance()));
        }
    }

    /** FIX 3: Stats now include separate per-operation timing. */
    public void showStatsGUI(JTextArea area) {
        area.append("=== SYSTEM STATS ===\n");
        area.append(String.format("Total Slots : %d\n", totalSlots));
        area.append(String.format("Available   : %d\n", availableSlots));
        area.append(String.format("Occupied    : %d\n", totalSlots - availableSlots));
        area.append("\n--- Per-Operation Benchmark ---\n");
        area.append(String.format("Slot Allocation  (extractMin O(log n))  : %s\n",
                formatAvg(totalAllocationTime, allocationCount)));
        area.append(String.format("Slot Lookup      (HashTable  O(1) avg)  : %s\n",
                formatAvg(totalLookupTime, lookupCount)));
        area.append(String.format("Sorting          (MergeSort  O(n log n)): %s\n",
                formatAvg(totalSortTime, sortCount)));
        area.append(String.format("Route Computation(Dijkstra O((V+E)logV)): %s\n",
                formatAvg(totalRoutingTime, routingCount)));
        area.append("Data Structures: Graph + MinHeap + HashTable (linear probing)\n");
    }

    private String formatAvg(long totalNs, int count) {
        if (count == 0)
            return "no data yet";
        return String.format("avg %.4f ms over %d call(s)",
                (totalNs / (double) count) / 1_000_000.0, count);
    }

    // ---------------------------------------------------------------
    // File persistence
    // ---------------------------------------------------------------

    public void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RECORDS_FILE))) {
            writer.println("slot_id,status,floor,distance,type");
            for (int i = 1; i <= totalSlots; i++) {
                ParkingSlot slot = slotTable.get(i);
                if (slot != null) {
                    String status = slot.isAvailable() ? "available" : "occupied";
                    writer.printf("%d,%s,%d,%.1f,%s\n",
                            slot.getId(), status,
                            slot.getFloor(), slot.getDistance(), slot.getType());
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
            reader.readLine(); // skip header
            String line;
            slotTable = new HashTable(totalSlots * 2);
            availableSlots = 0;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    int id = Integer.parseInt(parts[0].trim());
                    String status = parts[1].trim();
                    int floor = Integer.parseInt(parts[2].trim());
                    double distance = Double.parseDouble(parts[3].trim());
                    String type = parts[4].trim();

                    ParkingSlot slot = new ParkingSlot(id, type, floor, distance);
                    if ("occupied".equals(status))
                        slot.occupy();
                    else
                        availableSlots++;
                    slotTable.put(id, slot);
                }
            }
        } catch (Exception e) {
            System.err.println("Load error: " + e.getMessage() + " — reinitializing.");
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