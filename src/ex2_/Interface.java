package ex2_;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Interface {
    private BPlusTree tree;
    private Scanner scanner;
    private boolean printAfterEachOperation;

    public Interface(int order, String fileName) throws Exception {
        this.tree = new BPlusTree(order, fileName);
        this.scanner = new Scanner(System.in);
        this.printAfterEachOperation = false;
    }

    public void runInteractive() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("B+ tree manual commands");

        askPrintPreference();
        printHelp();

        while (true) {
            System.out.print("\nenter command: ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) continue;

            Statistic.reset();

            try {
                if (!processCommand(input)) {
                    break;
                }
            } catch (Exception e) {
                System.err.println("error: " + e.getMessage());
            }

            System.out.println("\nI/O statistics for this operation:");
            Statistic.printStats();

            if (printAfterEachOperation) {
                try {
                    System.out.println("\ncurrent tree structure:");
                    tree.printTree();
                } catch (Exception e) {
                    System.err.println("error printing tree: " + e.getMessage());
                }
            }
        }
    }

    public void runFromFile(String filename) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("reading commands from: " + filename);

        askPrintPreference();

        try (Scanner fileScanner = new Scanner(new File(filename))) {
            int lineNumber = 0;

            while (fileScanner.hasNextLine()) {
                lineNumber++;
                String line = fileScanner.nextLine().trim();

                if (line.isEmpty()) {
                    continue;
                }
                System.out.println("\n[Line " + lineNumber + "] executing: " + line);
                Statistic.reset();

                try {
                    processCommand(line);
                    System.out.println("  success");
                } catch (Exception e) {
                    System.err.println("  error: " + e.getMessage());
                }

                Statistic.printStats();

                if (printAfterEachOperation) {
                    try {
                        tree.printTree();
                    } catch (Exception e) {
                        System.err.println("error printing tree: " + e.getMessage());
                    }
                }
            }

            System.out.println("file processing complete");

        } catch (FileNotFoundException e) {
            System.err.println("file not found - " + filename);
        }
    }

    private void askPrintPreference() {
        System.out.print("\nprint tree after operation? (y/n): ");
        String response = scanner.nextLine().trim().toLowerCase();
        printAfterEachOperation = response.equals("y");
        System.out.println("Print mode: " + (printAfterEachOperation ? "ON" : "OFF"));
    }

    private boolean processCommand(String input) throws Exception {
        String[] parts = input.split("\\s+");
        String command = parts[0].toLowerCase();

        switch (command) {
            case "i":
                handleInsert(parts);
                break;

            case "s":
                handleSearch(parts);
                break;

            case "d":
                handleDelete(parts);
                break;

            case "u":
                handleUpdate(parts);
                break;

            case "r":
                handleReorganize();
                break;

            case "p":
                handlePrint(parts);
                break;

            case "h":
                printHelp();
                break;

            case "q":
                handleExit();
                return false;

            default:
                System.out.println("'h' for commands list.");
        }

        return true;
    }

    private void handleInsert(String[] parts) throws Exception {
        if (parts.length < 4) {
            System.out.println("insert key mass height");
            return;
        }

        int key = Integer.parseInt(parts[1]);
        double mass = Double.parseDouble(parts[2]);
        double height = Double.parseDouble(parts[3]);

        Record record = new Record(mass, height, key);
        tree.insert(record);
        System.out.printf("inserted: key=%d, mass=%.2f, height=%.2f%n", key, mass, height);
    }

    private void handleSearch(String[] parts) throws Exception {
        if (parts.length < 2) {
            System.out.println("search key");
            return;
        }

        int key = Integer.parseInt(parts[1]);
        Record record = tree.search(key);

        if (record != null) {
            System.out.printf("found: key=%d, mass=%.2f, height=%.2f",
                    key, record.getMass(), record.getHeight() );
        } else {
            System.out.println("not found: " + key);
        }
    }

    private void handleDelete(String[] parts) throws Exception {
        if (parts.length < 2) {
            System.out.println("delete key");
            return;
        }

        int key = Integer.parseInt(parts[1]);
        boolean deleted = tree.delete(key);

        if (deleted) {
            System.out.println("deleted key: " + key);
        } else {
            System.out.println("not found: " + key);
        }
    }

    private void handleUpdate(String[] parts) throws Exception {
        if (parts.length < 5) {
            System.out.println("update old_key new_key new_mass new_height");
            return;
        }

        int oldKey = Integer.parseInt(parts[1]);
        int newKey = Integer.parseInt(parts[2]);
        double newMass = Double.parseDouble(parts[3]);
        double newHeight = Double.parseDouble(parts[4]);

        Record newRecord = new Record(newMass, newHeight, newKey);
        boolean updated = tree.update(oldKey, newRecord);

        if (updated) {
            System.out.printf("updated: key %d to %d (mass=%.2f, height=%.2f)%n",
                    oldKey, newKey, newMass, newHeight);
        } else {
            System.out.println("key not found: " + oldKey);
        }
    }

    private void handleReorganize() throws Exception {
        System.out.println("Starting reorganization...");
        //tree.reorganize();
        System.out.println("Reorganization complete!");
    }

    private void handlePrint(String[] parts) throws Exception {
        String option = parts.length > 1 ? parts[1].toLowerCase() : "tree";

        switch (option) {
            case "t":
                tree.printTree();
                break;

            case "r":
                tree.printAllRecords();
                break;

            case "a":
                tree.printTree();
                System.out.println();
                tree.printAllRecords();
                break;

            default:
                System.out.println("Usage: print [tree|records|all]");
                System.out.println("  t     - Print tree structure (default)");
                System.out.println("  r     - Print all records in order");
                System.out.println("  a     - Print both tree and records");
        }
    }


    private void handleExit() throws Exception {
        tree.close();
    }

    private void printHelp() {
        System.out.println("\nAvailable commands:");
        System.out.println("  i, key mass height              - Insert a new record");
        System.out.println("  s, key                          - Search for a record");
        System.out.println("  d, delete key                   - Delete a record");
        System.out.println("  u, old_key new_key mass height  - Update a record");
        System.out.println("  r,                              - Reorganize the tree");
        System.out.println("  p,  t|r|a                       - Print tree/records/all");
        System.out.println("  h,                              - Show this help");
        System.out.println("  q,                              - Exit program");
    }

    public void close() throws Exception {
        tree.close();
        scanner.close();
    }
}
