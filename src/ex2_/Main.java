package ex2_;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;


// Node class for B+ Tree
class BPlusNode {
    int order;
    List<Integer> keys;             // Natural number keys
    List<BPlusNode> children;       // Child nodes (for internal nodes)
    List<Record> records;           // Records (for leaf nodes)
    BPlusNode next;                 // Next leaf node
    BPlusNode parent;
    boolean isLeaf;

    public BPlusNode(int order) {
        this.order = order;
        this.keys = new ArrayList<>();
        this.children = new ArrayList<>();
        this.records = new ArrayList<>();
        this.next = null;
        this.parent = null;
        this.isLeaf = false;
    }

    // Insert record at leaf
    public void insertAtLeaf(int key, Record record) {
        if (keys.isEmpty()) {
            keys.add(key);
            records.add(record);
        } else {
            int i = 0;
            while (i < keys.size() && key > keys.get(i)) {
                i++;
            }
            keys.add(i, key);
            records.add(i, record);
        }
    }

    // Get maximum capacity
    public int getMaxKeys() {
        return 2 * order;
    }

    // Get minimum keys (except root)
    public int getMinKeys() {
        return order;
    }
}

// B+ Tree implementation
class BplusTree {
    BPlusNode root;
    int order;

    public BplusTree(int order) {
        this.order = order;
        this.root = new BPlusNode(order);
        this.root.isLeaf = true;
    }

    // Insert a record using its key
    public void insert(Record record) {
        int key = record.getKey();
        BPlusNode leaf = search(key);
        leaf.insertAtLeaf(key, record);

        // Split when node exceeds 2*order keys
        if (leaf.keys.size() > leaf.getMaxKeys()) {
            splitLeaf(leaf);
        }
    }

    // Search for the appropriate leaf node
    private BPlusNode search(int key) {
        BPlusNode current = root;
        while (!current.isLeaf) {
            int i = 0;
            while (i < current.keys.size()) {
                if (key < current.keys.get(i)) {
                    break;
                }
                i++;
            }
            current = current.children.get(i);
        }
        return current;
    }

    // Split a leaf node
    private void splitLeaf(BPlusNode leaf) {
        BPlusNode newLeaf = new BPlusNode(order);
        newLeaf.isLeaf = true;
        newLeaf.parent = leaf.parent;

        // Split point: keep order keys in left, move rest to right
        int splitPoint = order;

        // Move keys and records to new leaf
        newLeaf.keys.addAll(leaf.keys.subList(splitPoint, leaf.keys.size()));
        newLeaf.records.addAll(leaf.records.subList(splitPoint, leaf.records.size()));

        leaf.keys.subList(splitPoint, leaf.keys.size()).clear();
        leaf.records.subList(splitPoint, leaf.records.size()).clear();

        // Update next pointers
        newLeaf.next = leaf.next;
        leaf.next = newLeaf;

        // The first key of the new leaf goes up to parent
        insertInParent(leaf, newLeaf.keys.getFirst(), newLeaf);
    }

    // Insert in parent node
    private void insertInParent(BPlusNode left, int key, BPlusNode right) {
        if (left == root) {
            BPlusNode newRoot = new BPlusNode(order);
            newRoot.keys.add(key);
            newRoot.children.add(left);
            newRoot.children.add(right);
            root = newRoot;
            left.parent = newRoot;
            right.parent = newRoot;
            return;
        }

        BPlusNode parent = left.parent;
        int i = 0;
        while (i < parent.children.size() && parent.children.get(i) != left) {
            i++;
        }

        parent.keys.add(i, key);
        parent.children.add(i + 1, right);
        right.parent = parent;

        // Split if parent exceeds 2*order keys
        if (parent.keys.size() > parent.getMaxKeys()) {
            splitInternal(parent);
        }
    }

    // Split internal node
    private void splitInternal(BPlusNode node) {
        BPlusNode newNode = new BPlusNode(order);
        newNode.parent = node.parent;

        // Split at order position
        int splitPoint = order;
        int midKey = node.keys.get(splitPoint);

        // Move keys and children to new node (middle key goes up, not copied)
        newNode.keys.addAll(node.keys.subList(splitPoint + 1, node.keys.size()));
        newNode.children.addAll(node.children.subList(splitPoint + 1, node.children.size()));

        node.keys.subList(splitPoint, node.keys.size()).clear();
        node.children.subList(splitPoint + 1, node.children.size()).clear();

        for (BPlusNode child : newNode.children) {
            child.parent = newNode;
        }

        insertInParent(node, midKey, newNode);
    }

    // Find records by key
    public List<Record> find(int key) {
        BPlusNode leaf = search(key);
        List<Record> result = new ArrayList<>();

        for (int i = 0; i < leaf.keys.size(); i++) {
            if (leaf.keys.get(i) == key) {
                result.add(leaf.records.get(i));
            }
        }
        return result;
    }

    // Print all records in order
    public void printAllRecords() {
        BPlusNode current = root;
        while (!current.isLeaf) {
            current = current.children.getFirst();
        }

        System.out.println("All Records (sorted by key):");
        while (current != null) {
            for (int i = 0; i < current.records.size(); i++) {
                Record r = current.records.get(i);
                System.out.println(r.toString());
            }
            current = current.next;
        }
    }

    // Print tree structure
    public void printTree() {
        if (root == null) return;

        System.out.println("B+ Tree Structure (Order=" + order +
                ", Min keys=" + order + ", Max keys=" + (2*order) + "):");

        List<BPlusNode> currentLevel = new ArrayList<>();
        currentLevel.add(root);
        int level = 0;

        while (!currentLevel.isEmpty()) {
            System.out.println("\nLevel " + level + ":");
            List<BPlusNode> nextLevel = new ArrayList<>();

            for (BPlusNode node : currentLevel) {
                System.out.print("[ ");
                if(node.keys.size() > 1) {
                    for (int i = 0; i < node.keys.size() - 1; i++) {
                        System.out.print(node.keys.get(i) + " | ");
                    }
                    System.out.print(node.keys.getLast());
                } else if (node.keys.size() == 1) {
                    System.out.print(node.keys.getFirst());
                }
                System.out.print(" ] ");

                if (!node.isLeaf) {
                    nextLevel.addAll(node.children);
                }
            }

            currentLevel = nextLevel;
            level++;
        }
        System.out.println();
    }
}

// Main class for testing
public class Main {
    public static void main(String[] args) {
        // Create B+ tree with order=2 (min=2 keys, max=4 keys per node)
        BplusTree tree = new BplusTree(2);
        Random random = new Random(10); // Seed for reproducibility

        System.out.println("Inserting records with randomly generated keys:");
        System.out.println("Each node will hold 2-4 keys\n");

        // Generate 12 records with random keys
        int numRecords = 12;
        for (int i = 0; i < numRecords; i++) {
            int key = random.nextInt(100) + 1; // Random key between 1-100
            double mass = random.nextDouble() * 10 + 1; // Mass between 1-11
            double height = random.nextDouble() * 30 + 5; // Height between 5-35

            Record r = new Record(mass, height, key);
            System.out.printf("Inserting: Key=%d, mass=%.2f, height=%.2f%n",
                    key, mass, height);
            tree.insert(r);
        }

        System.out.println("\n" + "=".repeat(60));
        tree.printTree();

        System.out.println("\n" + "=".repeat(60));
        tree.printAllRecords();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("Finding records with key=50:");
        List<Record> found = tree.find(50);
        if (found.isEmpty()) {
            System.out.println("Not found");
        } else {
            for (Record r : found) {
                System.out.println("Found: " + r);
            }
        }

//        System.out.println("\n" + "=".repeat(60));
//        System.out.println("\nGenerating new tree with 20 records and order=3:");
//        BplusTree tree2 = new BplusTree(3);
//        Random random2 = new Random();
//
//        for (int i = 0; i < 20; i++) {
//            int key = random2.nextInt(150) + 1;
//            double mass = random2.nextDouble() * 10 + 1;
//            double height = random2.nextDouble() * 30 + 5;
//            tree2.insert(new Record(mass, height, key));
//        }
//
//        System.out.println("\nB+ Tree with order=3 (min=3 keys, max=6 keys):");
//        tree2.printTree();
    }
}