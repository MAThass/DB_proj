package BPlusTree;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Scanner;


// Main class for testing
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("  1. manual commands");
            System.out.println("  2. commands from file");

            String choice = scanner.nextLine().trim();

            Interface btree = new Interface(ConstValues.d, "btree.dat");

            if (choice.equals("1")) {
                btree.runInteractive();
            } else if (choice.equals("2")) {
                System.out.print("enter filename: ");
                String filename = scanner.nextLine().trim();
                btree.runFromFile(filename);

                System.out.print("\ninteractive mode (y/n): ");
                String response = scanner.nextLine().trim().toLowerCase();
                if (response.equals("y") || response.equals("yes")) {
                    btree.runInteractive();
                }
            } else {
                System.out.println("Invalid choice. Exiting.");
            }

            btree.close();
            scanner.close();

        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(padding) + text + " ".repeat(width - text.length() - padding);
    }

//    public static void main(String[] args) {
//        try {
//            System.out.println("=== B+ Tree Comprehensive Test ===");
//            System.out.println("Insert 20 keys, Delete 5, Insert 10, Update 5\n");
//
//            BPlusTree tree = new BPlusTree(2, "btree.dat");
//            tree.clearFile();
//
//            // Phase 1: Insert 20 keys
//            System.out.println("PHASE 1: Insert 20 keys");
//            System.out.println("=".repeat(60));
//            int[] initialKeys = {50, 30, 70, 20, 40, 60, 80, 10, 25, 35,
//                    45, 55, 65, 75, 85, 15, 5, 90, 95, 100};
//
//            for (int key : initialKeys) {
//                double mass = key * 0.1;
//                double height = key * 0.5;
//                Record r = new Record(mass, height, key);
//                int read = Statistic.readBlocksCounter;
//                int write = Statistic.writeBlocksCounter;
//                tree.insert(r);
//                System.out.printf("✓ Inserted key=%d (mass=%.1f, height=%.1f)%n",
//                        key, mass, height);
//                System.out.println("total read: " + Statistic.readBlocksCounter + " total write: " + Statistic.writeBlocksCounter);
//                System.out.println("at record read: " + (Statistic.readBlocksCounter - read) + " write " + (Statistic.writeBlocksCounter - write) );
//
//            }
//
//            System.out.println("\nTree structure after 20 inserts:");
//            tree.printTree();
//
////            System.out.println("\nAll records after initial inserts:");
////            tree.printAllRecords();
//
//            // Phase 2: Delete 5 keys
//            System.out.println("\n" + "=".repeat(60));
//            System.out.println("PHASE 2: Delete 5 keys");
//            System.out.println("=".repeat(60));
//            int[] deleteKeys = {25, 45, 65, 85, 95};
//
//            for (int key : deleteKeys) {
//                boolean deleted = tree.delete(key);
//                System.out.printf("%s Deleted key=%d%n",
//                        deleted ? "✓" : "✗", key);
//            }
//
//            System.out.println("\nTree structure after 5 deletions:");
//            tree.printTree();
//
////            System.out.println("\nRemaining records after deletions:");
////            tree.printAllRecords();
//
//            // Phase 3: Insert 10 new keys
//            System.out.println("\n" + "=".repeat(60));
//            System.out.println("PHASE 3: Insert 10 new keys");
//            System.out.println("=".repeat(60));
//            int[] newKeys = {12, 18, 22, 28, 32, 38, 42, 48, 52, 58};
//
//            for (int key : newKeys) {
//                double mass = key * 0.12;
//                double height = key * 0.45;
//                Record r = new Record(mass, height, key);
//                tree.insert(r);
//                System.out.printf("✓ Inserted key=%d (mass=%.2f, height=%.2f)%n",
//                        key, mass, height);
//            }
//
//            System.out.println("\nTree structure after 10 new inserts:");
//            tree.printTree();
//
////            System.out.println("\nAll records after new inserts:");
////            tree.printAllRecords();
//
//            // Phase 4: Update 5 keys
//            System.out.println("\n" + "=".repeat(60));
//            System.out.println("PHASE 4: Update 5 keys");
//            System.out.println("=".repeat(60));
//
//            // Update: change key and data
//            int[][] updates = {
//                    {10, 11},  // Change key 10 to 11
//                    {30, 33},  // Change key 30 to 33
//                    {50, 51},  // Change key 50 to 51
//                    {70, 72},  // Change key 70 to 72
//                    {90, 92}   // Change key 90 to 92
//            };
//
//            for (int[] update : updates) {
//                int oldKey = update[0];
//                int newKey = update[1];
//                double newMass = newKey * 0.15;
//                double newHeight = newKey * 0.6;
//
//                Record newRecord = new Record(newMass, newHeight, newKey);
//                boolean updated = tree.update(oldKey, newRecord);
//
//                System.out.printf("%s Updated key=%d to key=%d (mass=%.2f, height=%.2f)%n",
//                        updated ? "✓" : "✗", oldKey, newKey, newMass, newHeight);
//            }
//
//            System.out.println("\nTree structure after 5 updates:");
//            tree.printTree();
//
////            System.out.println("\nAll records after updates:");
////            tree.printAllRecords();
//
//            // Phase 5: Verification - Search for specific keys
//            System.out.println("\n" + "=".repeat(60));
//            System.out.println("PHASE 5: Verification - Search Operations");
//            System.out.println("=".repeat(60));
//
//            System.out.println("\nSearching for DELETED keys (should not exist):");
//            for (int key : deleteKeys) {
//                Record found = tree.search(key);
//                System.out.printf("%s Key=%d: %s%n",
//                        found == null ? "✓" : "✗",
//                        key,
//                        found == null ? "Not found (correct)" : "ERROR: Still exists!");
//            }
//
//            System.out.println("\nSearching for UPDATED keys (old keys should not exist):");
//            for (int[] update : updates) {
//                int oldKey = update[0];
//                Record found = tree.search(oldKey);
//                System.out.printf("%s Old key=%d: %s%n",
//                        found == null ? "✓" : "✗",
//                        oldKey,
//                        found == null ? "Not found (correct)" : "ERROR: Still exists!");
//            }
//
//            System.out.println("\nSearching for UPDATED keys (new keys should exist):");
//            for (int[] update : updates) {
//                int newKey = update[1];
//                Record found = tree.search(newKey);
//                if (found != null) {
//                    System.out.printf("✓ New key=%d: Found (mass=%f, height=%f)%n",
//                            newKey, found.getMass(), found.getHeight());
//                } else {
//                    System.out.printf("✗ New key=%d: ERROR - Not found!%n", newKey);
//                }
//            }
//
//            System.out.println("\nSearching for some NEW inserted keys:");
//            int[] checkNewKeys = {12, 28, 42, 58};
//            for (int key : checkNewKeys) {
//                Record found = tree.search(key);
//                if (found != null) {
//                    System.out.printf("✓ Key=%d: Found (mass=%f, height=%f)%n",
//                            key, found.getMass(), found.getHeight());
//                } else {
//                    System.out.printf("✗ Key=%d: ERROR - Not found!%n", key);
//                }
//            }
//
//            // Summary
//            System.out.println("\n" + "=".repeat(60));
//            System.out.println("SUMMARY");
//            System.out.println("=".repeat(60));
//            System.out.println("Operations completed:");
//            System.out.println("  - Inserted 20 initial keys");
//            System.out.println("  - Deleted 5 keys: " + java.util.Arrays.toString(deleteKeys));
//            System.out.println("  - Inserted 10 new keys: " + java.util.Arrays.toString(newKeys));
//            System.out.println("  - Updated 5 keys: 10→11, 30→33, 50→51, 70→72, 90→92");
//            System.out.println("\nExpected total keys: 20 - 5 + 10 = 25");
//
//            // Count actual keys
//            BPlusNode current = tree.readNode(tree.rootAddress);
//            while (!current.isLeaf) {
//                current = tree.readNode(current.childAddresses.get(0));
//            }
//            int actualCount = 0;
//            while (current != null) {
//                actualCount += current.keys.size();
//                if (current.nextAddress != -1) {
//                    current = tree.readNode(current.nextAddress);
//                } else {
//                    break;
//                }
//            }
//            System.out.println("Actual total keys: " + actualCount);
//            System.out.println(actualCount == 25 ? "✓ Count matches!" : "✗ Count mismatch!");
//
//            tree.close();
//
//            System.out.println("\n=== All Tests Completed Successfully ===");
//
//        } catch (Exception e) {
//            System.err.println("\n✗ Test failed with exception:");
//            e.printStackTrace();
//        }
//    }
    
//    public static void main(String[] args) {
//
//        try {
//            System.out.println("=== B+ Tree Serialization Test ===\n");
//
//            // Test 1: Serialize and deserialize a leaf node
//            System.out.println("Test 1: Leaf Node Serialization");
//            BPlusNode leafNode = new BPlusNode(3);
//            leafNode.isLeaf = true;
//            leafNode.pageAddress = 0;
//            leafNode.keys.add(10);
//            leafNode.keys.add(20);
//            leafNode.keys.add(30);
//            leafNode.records.add(new Record(5.0, 10.0, 10));
//            leafNode.records.add(new Record(3.0, 15.0, 20));
//            leafNode.records.add(new Record(4.0, 20.0, 30));
//
//            byte[] serialized = leafNode.serialize();
//
//            System.out.println("Serialized size: " + serialized.length + " bytes");
//
//            BPlusNode deserialized = BPlusNode.deserialize(serialized);
//            System.out.println("Keys: " + deserialized.keys);
//            System.out.println("Is leaf: " + deserialized.isLeaf);
//            System.out.println("Records: " + deserialized.records.size());
//
//            // Test 2: Serialize and deserialize an internal node
//            System.out.println("\nTest 2: Internal Node Serialization");
//            BPlusNode internalNode = new BPlusNode(3);
//            internalNode.isLeaf = false;
//            internalNode.pageAddress = 1;
//            internalNode.keys.add(50);
//            internalNode.keys.add(100);
//            internalNode.childAddresses.add(2);
//            internalNode.childAddresses.add(3);
//            internalNode.childAddresses.add(4);
//
//            byte[] serialized2 = internalNode.serialize();
//            BPlusNode deserialized2 = BPlusNode.deserialize(serialized2);
//            System.out.println("Keys: " + deserialized2.keys);
//            System.out.println("Is leaf: " + deserialized2.isLeaf);
//            System.out.println("Child addresses: " + deserialized2.childAddresses);
//
//            // Test 3: Calculate max keys per page
//            System.out.println("\nTest 3: Maximum Keys Calculation");
//            int maxLeafKeys = BPlusNode.calculateMaxKeys(3, true);
//            int maxInternalKeys = BPlusNode.calculateMaxKeys(3, false);
//            System.out.println("Max keys in leaf node: " + maxLeafKeys);
//            System.out.println("Max keys in internal node: " + maxInternalKeys);
//
//            // Test 4: Disk-based operations
//            System.out.println("\nTest 4: Disk-Based Operations");
//            BPlusTree tree = new BPlusTree(3, "test.dat");
//
//            // Create and write a node
//            BPlusNode testNode = tree.allocateNode(true);
//            testNode.keys.add(15);
//            testNode.records.add(new Record(2.5, 12.0, 15));
//            tree.writeNode(testNode);
//
//            // Read it back
//            BPlusNode readBack = tree.readNode(testNode.pageAddress);
//            System.out.println("Read node keys: " + readBack.keys);
//            System.out.println("Read node has " + readBack.records.size() + " records");
//
//            // Test 2: Create tree and insert records
//
//            System.out.println("\nTest 2: Insert Records with Splitting");
//             tree = new BPlusTree(2, "file.dat");
//             //tree.delete();
//
//            int[] keys = {50, 30, 70, 20, 40, 60, 80, 10, 25, 35, 45, 55};
//
//            for (int key : keys) {
//                double mass = key * 0.1;
//                double height = key * 0.5;
//                Record r = new Record(mass, height, key);
//                System.out.printf("Inserting key=%d... ", key);
//                tree.insert(r);
//                System.out.println("✓");
//                tree.printTree();
//            }
//
//            // Test 3: Print tree structure
//            System.out.println("\nTest 3: Tree Structure After Inserts");
//            tree.printTree();
//
//            // Test 4: Print all records in order
//            System.out.println("\nTest 4: All Records in Sorted Order");
//            tree.printAllRecords();
//
//            // Test 5: Search for specific records
//            System.out.println("\nTest 5: Search Operations");
//            int[] searchKeys = {25, 50, 99, 10};
//            for (int key : searchKeys) {
//                Record found = tree.search(key);
//                if (found != null) {
//                    System.out.printf("✓ Found key=%d: mass=%.2f, height=%.2f%n",
//                            key, found.getMass(), found.getHeight());
//                } else {
//                    System.out.printf("✗ Key=%d not found%n", key);
//                }
//            }
//
//            // Test 6: Insert more records to trigger more splits
//            System.out.println("\nTest 6: Insert More Records");
//            int[] moreKeys = {5, 15, 65, 75, 85, 90};
//            for (int key : moreKeys) {
//                double mass = key * 0.15;
//                double height = key * 0.6;
//                tree.insert(new Record(mass, height, key));
//                System.out.printf("Inserted key=%d%n", key);
//            }
//
//            System.out.println("\nFinal Tree Structure:");
//            tree.printTree();
//
//            System.out.println("\nFinal Records:");
//            tree.printAllRecords();
//
//            tree.close();
//
//            System.out.println("\n=== All Tests Passed ===");
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        //Create B+ tree with order=2 (min=2 keys, max=4 keys per node)
////        BplusTree tree = new BplusTree(2);
////        Random random = new Random(10); // Seed for reproducibility
////
////        System.out.println("Inserting records with randomly generated keys:");
////        System.out.println("Each node will hold 2-4 keys\n");
////
////        // Generate 12 records with random keys
////        int numRecords = 12;
////        for (int i = 0; i < numRecords; i++) {
////            int key = random.nextInt(100) + 1; // Random key between 1-100
////            double mass = random.nextDouble() * 10 + 1; // Mass between 1-11
////            double height = random.nextDouble() * 30 + 5; // Height between 5-35
////
////            Record r = new Record(mass, height, key);
////            System.out.printf("Inserting: Key=%d, mass=%.2f, height=%.2f%n",
////                    key, mass, height);
////            tree.insert(r);
////        }
////
////        System.out.println("\n" + "=".repeat(60));
////        tree.printTree();
////
////        System.out.println("\n" + "=".repeat(60));
////        tree.printAllRecords();
////
////        System.out.println("\n" + "=".repeat(60));
////        System.out.println("Finding records with key=50:");
////        List<Record> found = tree.find(50);
////        if (found.isEmpty()) {
////            System.out.println("Not found");
////        } else {
////            for (Record r : found) {
////                System.out.println("Found: " + r);
////            }
////        }
//
////        System.out.println("\n" + "=".repeat(60));
////        System.out.println("\nGenerating new tree with 20 records and order=3:");
////        BplusTree tree2 = new BplusTree(3);
////        Random random2 = new Random();
////
////        for (int i = 0; i < 20; i++) {
////            int key = random2.nextInt(150) + 1;
////            double mass = random2.nextDouble() * 10 + 1;
////            double height = random2.nextDouble() * 30 + 5;
////            tree2.insert(new Record(mass, height, key));
////        }
////
////        System.out.println("\nB+ Tree with order=3 (min=3 keys, max=6 keys):");
////        tree2.printTree();
//    }
}