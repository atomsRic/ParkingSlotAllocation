/**
 * BenchmarkRunner.java
 * 
 * Standalone benchmark — no package declarations needed.
 * Place this file in the SAME folder as all your .java files.
 *
 * Compile:  javac *.java
 * Run:      java BenchmarkRunner
 */
public class BenchmarkRunner {

    private static final int   RUNS        = 5;
    private static final int[] INPUT_SIZES = {100, 500, 1000};

    public static void main(String[] args) {
        printHeader();

        for (int n : INPUT_SIZES) {
            System.out.println("\n========== INPUT SIZE: " + n + " slots ==========\n");
            benchmarkAllocation(n);
            benchmarkSorting(n);
            benchmarkLookup(n);
            benchmarkRouting(n);
        }

        System.out.println("\n=== Benchmark complete ===");
        System.out.println("* Route Computation: baseline is O(1) array lookup (no graph).");
        System.out.println("  Optimized adds real Dijkstra shortest-path computation.");
        System.out.println("  This is a design enhancement, not a brute-force reduction.");
    }

    // ---------------------------------------------------------------
    // 1. Slot Allocation
    // ---------------------------------------------------------------
    private static void benchmarkAllocation(int n) {
        long baselineTotal  = 0;
        long optimizedTotal = 0;

        for (int run = 0; run < RUNS; run++) {
            // Baseline: linear scan
            ParkingSlot[] baseSlots = buildSlots(n);
            long t1 = System.nanoTime();
            int bi = LinearSearch.findClosestAvailable(baseSlots, n);
            if (bi != -1) baseSlots[bi].allocate();
            baselineTotal += System.nanoTime() - t1;

            // Optimized: MinHeap extractMin
            MinHeap heap = buildHeap(n);
            long t2 = System.nanoTime();
            heap.extractMin();
            optimizedTotal += System.nanoTime() - t2;
        }

        printResult("Slot Allocation", n,
            baselineTotal / RUNS, optimizedTotal / RUNS,
            "O(n)", "O(log n)");
    }

    // ---------------------------------------------------------------
    // 2. Sorting
    // ---------------------------------------------------------------
    private static void benchmarkSorting(int n) {
        long baselineTotal  = 0;
        long optimizedTotal = 0;

        for (int run = 0; run < RUNS; run++) {
            // Baseline: BubbleSort
            ParkingSlot[] baseArr = buildSlots(n);
            long t1 = System.nanoTime();
            BubbleSort.bubbleSort(baseArr, n);
            baselineTotal += System.nanoTime() - t1;

            // Optimized: MergeSort
            ParkingSlot[] optArr = buildSlots(n);
            long t2 = System.nanoTime();
            MergeSort.mergeSort(optArr, 0, n - 1);
            optimizedTotal += System.nanoTime() - t2;
        }

        printResult("Sorting", n,
            baselineTotal / RUNS, optimizedTotal / RUNS,
            "O(n^2)", "O(n log n)");
    }

    // ---------------------------------------------------------------
    // 3. Slot Lookup
    // ---------------------------------------------------------------
    private static void benchmarkLookup(int n) {
        long baselineTotal  = 0;
        long optimizedTotal = 0;
        int targetId = n; // worst case for linear scan

        for (int run = 0; run < RUNS; run++) {
            // Baseline: linear array scan
            ParkingSlot[] baseSlots = buildSlots(n);
            long t1 = System.nanoTime();
            for (int i = 0; i < n; i++) {
                if (baseSlots[i] != null && baseSlots[i].getId() == targetId) break;
            }
            baselineTotal += System.nanoTime() - t1;

            // Optimized: HashTable get
            HashTable table = buildHashTable(n);
            long t2 = System.nanoTime();
            table.get(targetId);
            optimizedTotal += System.nanoTime() - t2;
        }

        printResult("Slot Lookup", n,
            baselineTotal / RUNS, optimizedTotal / RUNS,
            "O(n)", "O(1) avg");
    }

    // ---------------------------------------------------------------
    // 4. Route Computation
    // ---------------------------------------------------------------
    private static void benchmarkRouting(int n) {
        long baselineTotal  = 0;
        long optimizedTotal = 0;
        int targetSlot = n / 2;

        for (int run = 0; run < RUNS; run++) {
            // Baseline: PathFinder array lookup
            PathFinder pf = buildPathFinder(n);
            long t1 = System.nanoTime();
            pf.getPath(0, targetSlot);
            baselineTotal += System.nanoTime() - t1;

            // Optimized: Dijkstra graph traversal
            Graph graph = buildGraph(n);
            long t2 = System.nanoTime();
            graph.dijkstra(0, targetSlot);
            optimizedTotal += System.nanoTime() - t2;
        }

        printResult("Route Computation *", n,
            baselineTotal / RUNS, optimizedTotal / RUNS,
            "O(1) array lookup", "O((V+E)logV)");
    }

    // ---------------------------------------------------------------
    // Builder helpers — creates fresh test data each run
    // ---------------------------------------------------------------
    private static ParkingSlot[] buildSlots(int n) {
        ParkingSlot[] slots = new ParkingSlot[n];
        for (int i = 0; i < n; i++) {
            slots[i] = new ParkingSlot(i + 1, "A", i / 10 + 1, 10.0 + i * 0.5);
        }
        return slots;
    }

    private static MinHeap buildHeap(int n) {
        MinHeap heap = new MinHeap(n);
        for (int i = 0; i < n; i++) {
            heap.insert(10.0 + i * 0.5, i + 1);
        }
        return heap;
    }

    private static HashTable buildHashTable(int n) {
        HashTable table = new HashTable(n * 2);
        for (int i = 0; i < n; i++) {
            ParkingSlot slot = new ParkingSlot(i + 1, "A", i / 10 + 1, 10.0 + i * 0.5);
            table.put(i + 1, slot);
        }
        return table;
    }

    private static PathFinder buildPathFinder(int n) {
        PathFinder pf = new PathFinder(n);
        for (int i = 0; i < n; i++) {
            pf.registerSlot(i + 1, i / 10 + 1, 10.0 + i * 0.5);
        }
        return pf;
    }

    private static Graph buildGraph(int n) {
        Graph graph = new Graph(n + 11);
        for (int i = 0; i < n; i++) {
            int slotId = i + 1;
            int aisle  = i / 10 + 1;
            graph.addEdge(0,     aisle,  5.0 + aisle * 2);
            graph.addEdge(aisle, slotId, 2.0 + (i % 10));
        }
        return graph;
    }

    // ---------------------------------------------------------------
    // Output helpers
    // ---------------------------------------------------------------
    private static void printHeader() {
        System.out.println("============================================================");
        System.out.println("  PARKING SYSTEM - BENCHMARK RUNNER");
        System.out.println("  Baseline vs Optimized | 5 runs avg | time in ms");
        System.out.println("============================================================");
        System.out.printf("%-22s %-6s %-14s %-14s %-10s %-20s%n",
            "Operation", "n", "Baseline(ms)", "Optimized(ms)", "Improv.%", "Complexity");
        System.out.println("------------------------------------------------------------");
    }

    private static void printResult(String op, int n,
                                    long baseNs, long optNs,
                                    String baseC, String optC) {
        double baseMs = baseNs / 1_000_000.0;
        double optMs  = optNs  / 1_000_000.0;
        double improv = baseMs > 0 ? ((baseMs - optMs) / baseMs) * 100.0 : 0.0;

        System.out.printf("%-22s %-6d %-14s %-14s %-10s %s -> %s%n",
            op, n,
            String.format("%.4f", baseMs),
            String.format("%.4f", optMs),
            String.format("%.1f%%", improv),
            baseC, optC);
    }
}