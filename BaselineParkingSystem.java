import java.io.*;
import javax.swing.JTextArea;

public class BaselineParkingSystem {

    private ParkingSlot[] slots;      // slots[0] = slot ID 1, slots[i] = slot ID (i+1)
    private PathFinder    pathFinder;
    private int           totalSlots;
    private int           availableSlots;

    private static final String RECORDS_FILE = "parking_records_baseline.txt";

    // Construction
    public BaselineParkingSystem(int n) {
        totalSlots     = n;
        availableSlots = 0;
        slots          = new ParkingSlot[n];
        pathFinder     = new PathFinder(n);

        loadFromFile();          // fills slots[] from CSV (or calls initializeSlots)
        registerAllPaths();      // tell PathFinder about every slot

        System.out.println("Records file: " + new File(RECORDS_FILE).getAbsolutePath());
    }

    // Initialization helpers
    // Called when no save file exists — builds fresh slots. 
    private void initializeSlots() {
        availableSlots = 0;
        for (int i = 0; i < totalSlots; i++) {
            int    slotId   = i + 1;
            int    floor    = i / 10 + 1;          // floors 1-5 for 50 slots
            double distance = 10.0 + i * 0.5;
            slots[i] = new ParkingSlot(slotId, "A", floor, distance);
            availableSlots++;
        }
    }

    /** Register every slot in PathFinder so path queries work. */
    private void registerAllPaths() {
        for (int i = 0; i < totalSlots; i++) {
            if (slots[i] != null) {
                pathFinder.registerSlot(
                    slots[i].getId(),
                    slots[i].getFloor(),
                    slots[i].getDistance()
                );
            }
        }
    }

    // Core operations
    public int findAndAllocateSlot() {
        // LinearSearch scans the array and returns the best index (0-based)
        int bestIndex = LinearSearch.findClosestAvailable(slots, totalSlots);
        if (bestIndex == -1) return -1;

        slots[bestIndex].allocate();
        availableSlots--;
        return slots[bestIndex].getId();
    }

    /**
     * Get a navigation path to a slot.
     * Uses PathFinder (array lookup) — O(1).
     */
    public String getPath(int slotId) {
        return pathFinder.getPath(0, slotId);
    }

    /**
     * Free a slot by its ID.
     * Array scan to locate the slot — O(n).
     */
    public void freeSlot(int slotId) {
        int index = findIndexById(slotId);
        if (index != -1 && !slots[index].isAvailable()) {
            slots[index].free();
            availableSlots++;
        }
    }

    // Display helpers (GUI)
    /**
     * Display available slots sorted by distance.
     * Uses BubbleSort on a copied array — O(n²).
     */
    public void displaySortedSlotsGUI(JTextArea area) {
        // Copy available slots into a temp array
        ParkingSlot[] temp  = new ParkingSlot[totalSlots];
        int           count = 0;
        for (int i = 0; i < totalSlots; i++) {
            if (slots[i] != null && slots[i].isAvailable()) {
                temp[count++] = slots[i];
            }
        }

        // Sort the copy
        BubbleSort.bubbleSort(temp, count);

        area.append("AVAILABLE SLOTS (Bubble Sorted by Distance):\n");
        for (int i = 0; i < count; i++) {
            area.append(String.format("  Slot %d (Floor %d, %.1f m)\n",
                temp[i].getId(), temp[i].getFloor(), temp[i].getDistance()));
        }
    }

    public void showStatsGUI(JTextArea area) {
        area.append("STATS:\n");
        area.append(String.format("Total Slots   : %d\n", totalSlots));
        area.append(String.format("Available     : %d\n", availableSlots));
        area.append(String.format("Occupied      : %d\n", totalSlots - availableSlots));
        area.append("Algorithms: LinearSearch O(n) + BubbleSort O(n²) + PathFinder O(1)\n");
        area.append("Data Structure: plain arrays only\n");
    }

    // File persistence

    public void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RECORDS_FILE))) {
            writer.println("slot_id,status,floor,distance,type");
            for (int i = 0; i < totalSlots; i++) {
                if (slots[i] != null) {
                    String status = slots[i].isAvailable() ? "available" : "occupied";
                    writer.printf("%d,%s,%d,%.1f,%s\n",
                        slots[i].getId(), status,
                        slots[i].getFloor(), slots[i].getDistance(),
                        slots[i].getType());
                }
            }
        } catch (IOException e) {
            System.err.println("Save error: " + e.getMessage());
        }
    }

    public void loadFromFile() {
        java.io.File file = new java.io.File(RECORDS_FILE);
        if (!file.exists()) {
            initializeSlots();
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // skip header
            String line;
            int    idx        = 0;
            availableSlots    = 0;
            slots             = new ParkingSlot[totalSlots];

            while ((line = reader.readLine()) != null && idx < totalSlots) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    int    id       = Integer.parseInt(parts[0].trim());
                    String status   = parts[1].trim();
                    int    floor    = Integer.parseInt(parts[2].trim());
                    double distance = Double.parseDouble(parts[3].trim());
                    String type     = parts[4].trim();

                    ParkingSlot slot = new ParkingSlot(id, type, floor, distance);
                    if ("occupied".equals(status)) slot.occupy();
                    else                           availableSlots++;

                    slots[idx++] = slot;
                }
            }
        } catch (Exception e) {
            System.err.println("Load error: " + e.getMessage() + " — reinitializing.");
            initializeSlots();
        }
    }

    public void reloadFromFile() {
        loadFromFile();
        registerAllPaths();
    }

    // ---------------------------------------------------------------
    // Private utility
    // ---------------------------------------------------------------

    /** Linear scan to find a slot's array index by its ID. */
    private int findIndexById(int slotId) {
        for (int i = 0; i < totalSlots; i++) {
            if (slots[i] != null && slots[i].getId() == slotId) return i;
        }
        return -1;
    }
}
