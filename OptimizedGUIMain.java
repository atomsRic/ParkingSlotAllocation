import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class OptimizedGUIMain extends JFrame implements ActionListener {
    private OptimizedParkingSystem system;
    private JTextArea outputArea;
    private JTextField slotInput;
    
    public OptimizedGUIMain() {
        system = new OptimizedParkingSystem(50);
        initGUI();
    }
    
    private void initGUI() {
        setTitle("OPTIMIZED Parking System v2.0 - GUI + Save");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        outputArea = new JTextArea();
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);
        
        JPanel controls = new JPanel(new FlowLayout());
        
        JButton allocateBtn = new JButton("Allocate Closest Slot");
        allocateBtn.addActionListener(this);
        allocateBtn.setActionCommand("allocate");
        
        JButton pathBtn = new JButton("Navigation Path");
        pathBtn.addActionListener(this);
        pathBtn.setActionCommand("path");
        
        JButton freeBtn = new JButton("Free Slot");
        freeBtn.addActionListener(this);
        freeBtn.setActionCommand("free");
        
        JButton sortBtn = new JButton("Sorted Slots");
        sortBtn.addActionListener(this);
        sortBtn.setActionCommand("sort");
        
        JButton statsBtn = new JButton("Stats");
        statsBtn.addActionListener(this);
        statsBtn.setActionCommand("stats");
        
        JButton saveBtn = new JButton("Save Records");
        saveBtn.addActionListener(this);
        saveBtn.setActionCommand("save");
        
        JButton loadBtn = new JButton("Reload");
        loadBtn.addActionListener(this);
        loadBtn.setActionCommand("load");
        
        slotInput = new JTextField(12);
        slotInput.setText("slot ID");
        
        controls.add(allocateBtn); controls.add(pathBtn); controls.add(freeBtn);
        controls.add(sortBtn); controls.add(statsBtn); controls.add(saveBtn);
        controls.add(loadBtn);
        controls.add(new JLabel("Slot:"));
        controls.add(slotInput);
        
        add(controls, BorderLayout.SOUTH);
        
        showWelcome();
        setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        switch (cmd) {
            case "allocate": allocateSlot(); break;
            case "path": getPath(); break;
            case "free": freeSlot(); break;
            case "sort": showSorted(); break;
            case "stats": showStats(); break;
            case "save": saveRecords(); break;
            case "load": reloadRecords(); break;
        }
    }
    
    private void log(String msg) {
        outputArea.append(msg + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }
    
    private void showWelcome() {
        log("=== OPTIMIZED PARKING SYSTEM LOADED ===");
        log("Working directory: " + new File(".").getAbsolutePath());
        log("Records: parking_records_optimized.txt");
        log("Click buttons to test algorithms!");
        log("");
        showStats();
    }
    
    private void allocateSlot() {
        long start = System.nanoTime();
        int slot = system.findOptimalSlot(0);
        long time = System.nanoTime() - start;
        
        if (slot != -1) {
            log("ALLOCATED: Slot #" + slot + " (Dijkstra + MinHeap)");
            log("Time: " + String.format("%.3f", time / 1_000_000.0) + " ms");
        } else {
            log("NO AVAILABLE SLOTS!");
        }
        log("");
    }
    
    private void getPath() {
        try {
            int slot = Integer.parseInt(slotInput.getText());
            long start = System.nanoTime();
            String path = system.getShortestPath(0, slot);
            long time = System.nanoTime() - start;
            
            log("PATH to Slot " + slot + ": " + path);
            log("Dijkstra: " + String.format("%.3f", time / 1_000_000.0) + " ms");
        } catch (Exception ex) {
            log("Enter valid slot ID (1-50)");
        }
        log("");
    }
    
    private void freeSlot() {
        try {
            int slot = Integer.parseInt(slotInput.getText());
            system.freeSlot(slot);
            log("Slot " + slot + " FREED");
            showStats();
        } catch (Exception ex) {
            log("Invalid slot ID");
        }
        log("");
    }
    
    private void showSorted() {
        long start = System.nanoTime();
        system.displaySortedSlotsGUI(outputArea);
        long time = System.nanoTime() - start;
        log("MergeSort: " + String.format("%.3f", time / 1_000_000.0) + " ms");
        log("");
    }
    
    private void showStats() {
        system.showStatsGUI(outputArea);
        log("");
    }
    
    private void saveRecords() {
        system.saveToFile();
        log("SAVED to parking_records_optimized.txt");
        log("Check your folder!");
        log("");
    }
    
    private void reloadRecords() {
        system.reloadFromFile();
        log("RELOADED records from file!");
        showStats();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new OptimizedGUIMain();
            }
        });
    }
}
