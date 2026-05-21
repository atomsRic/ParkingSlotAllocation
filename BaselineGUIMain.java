import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class BaselineGUIMain extends JFrame implements ActionListener {

    private BaselineParkingSystem system;
    private JTextArea             outputArea;
    private JTextField            slotInput;

    public BaselineGUIMain() {
        system = new BaselineParkingSystem(50);
        initGUI();
    }

    private void initGUI() {
        setTitle("BASELINE Parking System v1.0 - Arrays Only");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        outputArea = new JTextArea();
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout());

        JButton allocateBtn = new JButton("Allocate Closest Slot");
        allocateBtn.setActionCommand("allocate");
        allocateBtn.addActionListener(this);

        JButton pathBtn = new JButton("Navigation Path");
        pathBtn.setActionCommand("path");
        pathBtn.addActionListener(this);

        JButton freeBtn = new JButton("Free Slot");
        freeBtn.setActionCommand("free");
        freeBtn.addActionListener(this);

        JButton sortBtn = new JButton("Sorted Slots");
        sortBtn.setActionCommand("sort");
        sortBtn.addActionListener(this);

        JButton statsBtn = new JButton("Stats");
        statsBtn.setActionCommand("stats");
        statsBtn.addActionListener(this);

        JButton saveBtn = new JButton("Save Records");
        saveBtn.setActionCommand("save");
        saveBtn.addActionListener(this);

        JButton loadBtn = new JButton("Reload");
        loadBtn.setActionCommand("load");
        loadBtn.addActionListener(this);

        slotInput = new JTextField(12);
        slotInput.setText("slot ID");

        controls.add(allocateBtn);
        controls.add(pathBtn);
        controls.add(freeBtn);
        controls.add(sortBtn);
        controls.add(statsBtn);
        controls.add(saveBtn);
        controls.add(loadBtn);
        controls.add(new JLabel("Slot:"));
        controls.add(slotInput);

        add(controls, BorderLayout.SOUTH);

        showWelcome();
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "allocate": allocateSlot();   break;
            case "path":     getPath();        break;
            case "free":     freeSlot();       break;
            case "sort":     showSorted();     break;
            case "stats":    showStats();      break;
            case "save":     saveRecords();    break;
            case "load":     reloadRecords();  break;
        }
    }

    // Helpers
    private void log(String msg) {
        outputArea.append(msg + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    private void showWelcome() {
        log("=== BASELINE PARKING SYSTEM LOADED (Arrays Only) ===");
        log("Working directory: " + new File(".").getAbsolutePath());
        log("Records: parking_records_baseline.txt");
        log("Algorithms: LinearSearch + BubbleSort + PathFinder (array-based)");
        log("Click buttons to test!");
        log("");
        showStats();
    }

    private void allocateSlot() {
        long start = System.nanoTime();
        int  slot  = system.findAndAllocateSlot();
        long time  = System.nanoTime() - start;

        if (slot != -1) {
            log("ALLOCATED: Slot #" + slot + "  (LinearSearch)");
            log("Time: " + String.format("%.3f", time / 1_000_000.0) + " ms");
        } else {
            log("NO AVAILABLE SLOTS!");
        }
        log("");
    }

    private void getPath() {
        try {
            int    slotId = Integer.parseInt(slotInput.getText().trim());
            long   start  = System.nanoTime();
            String path   = system.getPath(slotId);
            long   time   = System.nanoTime() - start;

            log("PATH to Slot " + slotId + ": " + path);
            log("PathFinder (array lookup): " + String.format("%.3f", time / 1_000_000.0) + " ms");
        } catch (NumberFormatException ex) {
            log("Enter a valid slot ID (1-50)");
        }
        log("");
    }

    private void freeSlot() {
        try {
            int slotId = Integer.parseInt(slotInput.getText().trim());
            system.freeSlot(slotId);
            log("Slot " + slotId + " FREED");
            showStats();
        } catch (NumberFormatException ex) {
            log("Enter a valid slot ID (1-50)");
        }
        log("");
    }

    private void showSorted() {
        long start = System.nanoTime();
        system.displaySortedSlotsGUI(outputArea);
        long time = System.nanoTime() - start;
        log("BubbleSort: " + String.format("%.3f", time / 1_000_000.0) + " ms");
        log("");
    }

    private void showStats() {
        system.showStatsGUI(outputArea);
        log("");
    }

    private void saveRecords() {
        system.saveToFile();
        log("SAVED to parking_records_baseline.txt");
        log("");
    }

    private void reloadRecords() {
        system.reloadFromFile();
        log("RELOADED records from file!");
        showStats();
    }
    // Entry point
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new BaselineGUIMain();
            }
        });
    }
}
