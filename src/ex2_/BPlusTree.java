package ex2_;

import java.util.ArrayList;
import java.util.List;

class BPlusTree {
    private int order = ConstValues.d;
    public int rootAddress;
    private HandleIO io;

    public BPlusTree(String fileName) throws Exception {
        this.io = new HandleIO(fileName);
        io.open();
        BPlusNode root = new BPlusNode(order);
        root.isLeaf = true;
        root.pageAddress = io.setPageAddress();
        this.rootAddress = root.pageAddress;
        writeNode(root);
    }

    public BPlusTree(int order, String fileName) throws Exception {
        this.order = order;
        this.io = new HandleIO(fileName);
        io.open();

        // Create root node
        BPlusNode root = new BPlusNode(order);
        root.isLeaf = true;
        root.pageAddress = io.setPageAddress();
        this.rootAddress = root.pageAddress;
        writeNode(root);
    }

    // Write node to disk
    public void writeNode(BPlusNode node) throws Exception {
        byte[] data = node.serialize();
        io.writePage(node.pageAddress, data);
    }

    // Read node from disk
    public BPlusNode readNode(int pageAddress) throws Exception {
        return readNode(pageAddress, false);
    }

    public BPlusNode readNode(int pageAddress, boolean printing) throws Exception {
        byte[] data = new byte[ConstValues.PAGE_SIZE];
        io.readPage(pageAddress, data, printing);
        return BPlusNode.deserialize(data);
    }

    // Allocate a new node
    public BPlusNode allocateNode(boolean isLeaf) throws Exception {
        BPlusNode node = new BPlusNode(order);
        node.isLeaf = isLeaf;
        node.pageAddress = io.setPageAddress();
        return node;
    }

    // Insert operation
    public void insert(Record record) throws Exception {
        int key = record.getKey();
        BPlusNode leaf = searchLeaf(key);

        insertAtLeaf(leaf, key, record);
        if (leaf.keys.size() > 2 * order) {
            splitLeaf(leaf);
        } else {
            writeNode(leaf);
        }
    }

    // Delete operation
    public boolean delete(int key) throws Exception {
        BPlusNode leaf = searchLeaf(key);
        // Find and remove key from leaf
        int index = -1;
        for (int i = 0; i < leaf.keys.size(); i++) {
            if (leaf.keys.get(i) == key) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return false; // Key not found
        }
        // Remove key and record
        leaf.keys.remove(index);
        leaf.records.remove(index);
        // Check if underflow (less than order keys, except root)
        if (leaf.pageAddress == rootAddress) {
            // Root can have fewer keys
            writeNode(leaf);
            return true;
        }
        if (leaf.keys.size() >= order) {
            // No underflow
            writeNode(leaf);
            return true;
        }
        // Handle underflow
        handleLeafUnderflow(leaf);
        return true;
    }

    public boolean update(int oldKey, Record newRecord) throws Exception {
        // Delete old record
        boolean deleted = delete(oldKey);
        if (!deleted) {
            return false; // Old key not found
        }

        // Insert new record
        insert(newRecord);
        return true;
    }

    // Handle leaf node underflow
    private void handleLeafUnderflow(BPlusNode leaf) throws Exception {
        BPlusNode parent = readNode(leaf.parentAddress);

        // Find leaf position in parent
        int leafIndex = -1;
        for (int i = 0; i < parent.childAddresses.size(); i++) {
            if (parent.childAddresses.get(i) == leaf.pageAddress) {
                leafIndex = i;
                break;
            }
        }

        // Try to borrow from left sibling
        if (leafIndex > 0) {
            BPlusNode leftSibling = readNode(parent.childAddresses.get(leafIndex - 1));
            if (leftSibling.keys.size() > order) {
                // Borrow from left sibling
                borrowFromLeftLeaf(leaf, leftSibling, parent, leafIndex);
                return;
            }
        }

        // Try to borrow from right sibling
        if (leafIndex < parent.childAddresses.size() - 1) {
            BPlusNode rightSibling = readNode(parent.childAddresses.get(leafIndex + 1));
            if (rightSibling.keys.size() > order) {
                // Borrow from right sibling
                borrowFromRightLeaf(leaf, rightSibling, parent, leafIndex);
                return;
            }
        }

        // Must merge with sibling
        if (leafIndex > 0) {
            // Merge with left sibling
            BPlusNode leftSibling = readNode(parent.childAddresses.get(leafIndex - 1));
            mergeLeaves(leftSibling, leaf, parent, leafIndex - 1);
        } else {
            // Merge with right sibling
            BPlusNode rightSibling = readNode(parent.childAddresses.get(leafIndex + 1));
            mergeLeaves(leaf, rightSibling, parent, leafIndex);
        }
    }

    // Borrow from left leaf sibling
    private void borrowFromLeftLeaf(BPlusNode leaf, BPlusNode leftSibling,
                                    BPlusNode parent, int leafIndex) throws Exception {
        // Move last key/record from left sibling to beginning of leaf
        int borrowedKey = leftSibling.keys.remove(leftSibling.keys.size() - 1);
        Record borrowedRecord = leftSibling.records.remove(leftSibling.records.size() - 1);

        leaf.keys.add(0, borrowedKey);
        leaf.records.add(0, borrowedRecord);

        // Update parent key
        parent.keys.set(leafIndex - 1, leaf.keys.get(0));

        writeNode(leaf);
        writeNode(leftSibling);
        writeNode(parent);
    }

    // Borrow from right leaf sibling
    private void borrowFromRightLeaf(BPlusNode leaf, BPlusNode rightSibling,
                                     BPlusNode parent, int leafIndex) throws Exception {
        // Move first key/record from right sibling to end of leaf
        int borrowedKey = rightSibling.keys.remove(0);
        Record borrowedRecord = rightSibling.records.remove(0);

        leaf.keys.add(borrowedKey);
        leaf.records.add(borrowedRecord);

        // Update parent key
        parent.keys.set(leafIndex, rightSibling.keys.get(0));

        writeNode(leaf);
        writeNode(rightSibling);
        writeNode(parent);
    }

    // Merge two leaf nodes
    private void mergeLeaves(BPlusNode left, BPlusNode right,
                             BPlusNode parent, int leftIndex) throws Exception {
        // Move all keys/records from right to left
        left.keys.addAll(right.keys);
        left.records.addAll(right.records);
        left.nextAddress = right.nextAddress;

        // Remove right child from parent
        parent.keys.remove(leftIndex);
        parent.childAddresses.remove(leftIndex + 1);

        writeNode(left);

        // Check parent underflow
        if (parent.pageAddress == rootAddress) {
            if (parent.keys.isEmpty()) {
                // Make left child the new root
                rootAddress = left.pageAddress;
                left.parentAddress = -1;
                writeNode(left);
            } else {
                writeNode(parent);
            }
        } else if (parent.keys.size() < order) {
            handleInternalUnderflow(parent);
        } else {
            writeNode(parent);
        }
    }

    // Handle internal node underflow
    private void handleInternalUnderflow(BPlusNode node) throws Exception {
        BPlusNode parent = readNode(node.parentAddress);

        // Find node position in parent
        int nodeIndex = -1;
        for (int i = 0; i < parent.childAddresses.size(); i++) {
            if (parent.childAddresses.get(i) == node.pageAddress) {
                nodeIndex = i;
                break;
            }
        }

        // Try to borrow from left sibling
        if (nodeIndex > 0) {
            BPlusNode leftSibling = readNode(parent.childAddresses.get(nodeIndex - 1));
            if (leftSibling.keys.size() > order) {
                borrowFromLeftInternal(node, leftSibling, parent, nodeIndex);
                return;
            }
        }

        // Try to borrow from right sibling
        if (nodeIndex < parent.childAddresses.size() - 1) {
            BPlusNode rightSibling = readNode(parent.childAddresses.get(nodeIndex + 1));
            if (rightSibling.keys.size() > order) {
                borrowFromRightInternal(node, rightSibling, parent, nodeIndex);
                return;
            }
        }

        // Must merge with sibling
        if (nodeIndex > 0) {
            BPlusNode leftSibling = readNode(parent.childAddresses.get(nodeIndex - 1));
            mergeInternal(leftSibling, node, parent, nodeIndex - 1);
        } else {
            BPlusNode rightSibling = readNode(parent.childAddresses.get(nodeIndex + 1));
            mergeInternal(node, rightSibling, parent, nodeIndex);
        }
    }

    // Borrow from left internal sibling
    private void borrowFromLeftInternal(BPlusNode node, BPlusNode leftSibling,
                                        BPlusNode parent, int nodeIndex) throws Exception {
        // Move parent key down to node
        int parentKey = parent.keys.get(nodeIndex - 1);
        node.keys.add(0, parentKey);

        // Move last child from left sibling to node
        int movedChild = leftSibling.childAddresses.remove(leftSibling.childAddresses.size() - 1);
        node.childAddresses.add(0, movedChild);

        // Update moved child's parent
        BPlusNode movedChildNode = readNode(movedChild);
        movedChildNode.parentAddress = node.pageAddress;
        writeNode(movedChildNode);

        // Move last key from left sibling to parent
        int movedKey = leftSibling.keys.remove(leftSibling.keys.size() - 1);
        parent.keys.set(nodeIndex - 1, movedKey);

        writeNode(node);
        writeNode(leftSibling);
        writeNode(parent);
    }

    // Borrow from right internal sibling
    private void borrowFromRightInternal(BPlusNode node, BPlusNode rightSibling,
                                         BPlusNode parent, int nodeIndex) throws Exception {
        // Move parent key down to node
        int parentKey = parent.keys.get(nodeIndex);
        node.keys.add(parentKey);

        // Move first child from right sibling to node
        int movedChild = rightSibling.childAddresses.remove(0);
        node.childAddresses.add(movedChild);

        // Update moved child's parent
        BPlusNode movedChildNode = readNode(movedChild);
        movedChildNode.parentAddress = node.pageAddress;
        writeNode(movedChildNode);

        // Move first key from right sibling to parent
        int movedKey = rightSibling.keys.remove(0);
        parent.keys.set(nodeIndex, movedKey);

        writeNode(node);
        writeNode(rightSibling);
        writeNode(parent);
    }

    // Merge two internal nodes
    private void mergeInternal(BPlusNode left, BPlusNode right,
                               BPlusNode parent, int leftIndex) throws Exception {
        // Pull down key from parent
        int parentKey = parent.keys.get(leftIndex);
        left.keys.add(parentKey);

        // Move all keys and children from right to left
        left.keys.addAll(right.keys);
        left.childAddresses.addAll(right.childAddresses);

        // Update parent pointers for moved children
        for (int childAddr : right.childAddresses) {
            BPlusNode child = readNode(childAddr);
            child.parentAddress = left.pageAddress;
            writeNode(child);
        }

        // Remove right child from parent
        parent.keys.remove(leftIndex);
        parent.childAddresses.remove(leftIndex + 1);

        writeNode(left);

        // Check parent underflow
        if (parent.pageAddress == rootAddress) {
            if (parent.keys.isEmpty()) {
                // Make left child the new root
                rootAddress = left.pageAddress;
                left.parentAddress = -1;
                writeNode(left);
            } else {
                writeNode(parent);
            }
        } else if (parent.keys.size() < order) {
            handleInternalUnderflow(parent);
        } else {
            writeNode(parent);
        }
    }

    // Insert record at leaf in sorted order
    private void insertAtLeaf(BPlusNode leaf, int key, Record record) {
        if (leaf.keys.isEmpty()) {
            leaf.keys.add(key);
            leaf.records.add(record);
        } else {
            int i = 0;
            while (i < leaf.keys.size() && key > leaf.keys.get(i)) {
                i++;
            }
            leaf.keys.add(i, key);
            leaf.records.add(i, record);
        }
    }

    // Insert key and child in parent node
    private void insertInParent(BPlusNode left, int key, BPlusNode right) throws Exception {
        // If left is root, create new root
        if (left.pageAddress == rootAddress) {
            BPlusNode newRoot = allocateNode(false);
            newRoot.keys.add(key);
            newRoot.childAddresses.add(left.pageAddress);
            newRoot.childAddresses.add(right.pageAddress);

            left.parentAddress = newRoot.pageAddress;
            right.parentAddress = newRoot.pageAddress;

            this.rootAddress = newRoot.pageAddress;

            writeNode(newRoot);
            writeNode(left);
            writeNode(right);
            return;
        }

        // Insert in existing parent
        BPlusNode parent = readNode(left.parentAddress);

        // Find position of left child
        int i = 0;
        while (i < parent.childAddresses.size() &&
                parent.childAddresses.get(i) != left.pageAddress) {
            i++;
        }

        // Insert key and right child
        parent.keys.add(i, key);
        parent.childAddresses.add(i + 1, right.pageAddress);
        right.parentAddress = parent.pageAddress;

        writeNode(parent);
        writeNode(right);

        // Check if parent needs split
        if (parent.keys.size() > 2 * order) {
            splitInternal(parent);
        }
    }

    // Split a leaf node
    private void splitLeaf(BPlusNode leaf) throws Exception {
        BPlusNode newLeaf = allocateNode(true);
        newLeaf.parentAddress = leaf.parentAddress;

        int splitPoint = order;

        // Move half keys and records to new leaf
        newLeaf.keys.addAll(leaf.keys.subList(splitPoint, leaf.keys.size()));
        newLeaf.records.addAll(leaf.records.subList(splitPoint, leaf.records.size()));

        leaf.keys.subList(splitPoint, leaf.keys.size()).clear();
        leaf.records.subList(splitPoint, leaf.records.size()).clear();

        // Update next pointers
        newLeaf.nextAddress = leaf.nextAddress;
        leaf.nextAddress = newLeaf.pageAddress;

        // Write both nodes
        writeNode(leaf);
        writeNode(newLeaf);

        // Insert in parent
        insertInParent(leaf, newLeaf.keys.get(0), newLeaf);
    }

    // Split an internal node
    private void splitInternal(BPlusNode node) throws Exception {
        BPlusNode newNode = allocateNode(false);
        newNode.parentAddress = node.parentAddress;

        int splitPoint = order;
        int midKey = node.keys.get(splitPoint);

        // Move keys and children to new node
        newNode.keys.addAll(node.keys.subList(splitPoint + 1, node.keys.size()));
        newNode.childAddresses.addAll(node.childAddresses.subList(splitPoint + 1,
                node.childAddresses.size()));

        node.keys.subList(splitPoint, node.keys.size()).clear();
        node.childAddresses.subList(splitPoint + 1, node.childAddresses.size()).clear();

        // Update parent pointers for moved children
        for (int childAddr : newNode.childAddresses) {
            BPlusNode child = readNode(childAddr);
            child.parentAddress = newNode.pageAddress;
            writeNode(child);
        }

        // Write both nodes
        writeNode(node);
        writeNode(newNode);

        // Insert in parent
        insertInParent(node, midKey, newNode);
    }

    // Search for appropriate leaf node
    private BPlusNode searchLeaf(int key) throws Exception {
        BPlusNode current = readNode(rootAddress);

        while (!current.isLeaf) {
            int i = 0;
            while (i < current.keys.size() && key >= current.keys.get(i)) {
                i++;
            }
            current = readNode(current.childAddresses.get(i));
        }

        return current;
    }

    // Search operation
    public Record search(int key) throws Exception {
        BPlusNode leaf = searchLeaf(key);

        // Search in leaf
        for (int i = 0; i < leaf.keys.size(); i++) {
            if (leaf.keys.get(i) == key) {
                return leaf.records.get(i);
            }
        }

        return null;
    }

    public void close() throws Exception {
        io.close();
    }

    public void clearFile() throws Exception {
        io.delete();
    }

    // Print all records in order
    public void printAllRecords() throws Exception {
        // Find leftmost leaf
        BPlusNode current = readNode(rootAddress, true);
        while (!current.isLeaf) {
            current = readNode(current.childAddresses.get(0), true);
        }

        System.out.println("All Records (sorted by key):");
        int count = 0;
        while (current != null) {
            for (int i = 0; i < current.records.size(); i++) {
                Record r = current.records.get(i);
                System.out.printf("Key=%d | mass=%f, height=%f, energy=%f%n",
                        r.getKey(), r.getMass(), r.getHeight(),
                        r.getMass() * r.getHeight());
                count++;
            }

            if (current.nextAddress != -1) {
                current = readNode(current.nextAddress, true);
            } else {
                break;
            }
        }
        System.out.println("Total records: " + count);
    }

    // Print tree structure
    public void printTree() throws Exception {
        System.out.println("B+ Tree Structure (Order=" + order +
                ", Min keys=" + order + ", Max keys=" + (2*order) + "):");

        List<Integer> currentLevel = new ArrayList<>();
        currentLevel.add(rootAddress);
        int level = 0;

        while (!currentLevel.isEmpty()) {
            System.out.println("\nLevel " + level + ":");
            List<Integer> nextLevel = new ArrayList<>();

            for (int addr : currentLevel) {
                BPlusNode node = readNode(addr, true);
                System.out.print("[ ");
                if (node.keys.size() > 1) {
                    for (int i = 0; i < node.keys.size() - 1; i++) {
                        System.out.print(node.keys.get(i) + " | ");
                    }
                    System.out.print(node.keys.get(node.keys.size() - 1));
                } else if (node.keys.size() == 1) {
                    System.out.print(node.keys.get(0));
                }
                System.out.print(" ](page=" + addr + ") ");

                if (!node.isLeaf) {
                    nextLevel.addAll(node.childAddresses);
                }
            }

            currentLevel = nextLevel;
            level++;
        }
        System.out.println();
    }
}


// B+ Tree implementation
//class BplusTree {
//    BPlusNode root;
//    int order;
//
//    public BplusTree(int order) {
//        this.order = order;
//        this.root = new BPlusNode(order);
//        this.root.isLeaf = true;
//    }
//
//    // Insert a record using its key
//    public void insert(Record record) {
//        int key = record.getKey();
//        BPlusNode leaf = search(key);
//        leaf.insertAtLeaf(key, record);
//
//        // Split when node exceeds 2*order keys
//        if (leaf.keys.size() > leaf.getMaxKeys()) {
//            splitLeaf(leaf);
//        }
//    }
//
//    // Search for the appropriate leaf node
//    private BPlusNode search(int key) {
//        BPlusNode current = root;
//        while (!current.isLeaf) {
//            int i = 0;
//            while (i < current.keys.size()) {
//                if (key < current.keys.get(i)) {
//                    break;
//                }
//                i++;
//            }
//            current = current.children.get(i);
//        }
//        return current;
//    }
//
//    // Split a leaf node
//    private void splitLeaf(BPlusNode leaf) {
//        BPlusNode newLeaf = new BPlusNode(order);
//        newLeaf.isLeaf = true;
//        newLeaf.parent = leaf.parent;
//
//        // Split point: keep order keys in left, move rest to right
//        int splitPoint = order;
//
//        // Move keys and records to new leaf
//        newLeaf.keys.addAll(leaf.keys.subList(splitPoint, leaf.keys.size()));
//        newLeaf.records.addAll(leaf.records.subList(splitPoint, leaf.records.size()));
//
//        leaf.keys.subList(splitPoint, leaf.keys.size()).clear();
//        leaf.records.subList(splitPoint, leaf.records.size()).clear();
//
//        // Update next pointers
//        newLeaf.next = leaf.next;
//        leaf.next = newLeaf;
//
//        // The first key of the new leaf goes up to parent
//        insertInParent(leaf, newLeaf.keys.getFirst(), newLeaf);
//    }
//
//    // Insert in parent node
//    private void insertInParent(BPlusNode left, int key, BPlusNode right) {
//        if (left == root) {
//            BPlusNode newRoot = new BPlusNode(order);
//            newRoot.keys.add(key);
//            newRoot.children.add(left);
//            newRoot.children.add(right);
//            root = newRoot;
//            left.parent = newRoot;
//            right.parent = newRoot;
//            return;
//        }
//
//        BPlusNode parent = left.parent;
//        int i = 0;
//        while (i < parent.children.size() && parent.children.get(i) != left) {
//            i++;
//        }
//
//        parent.keys.add(i, key);
//        parent.children.add(i + 1, right);
//        right.parent = parent;
//
//        // Split if parent exceeds 2*order keys
//        if (parent.keys.size() > parent.getMaxKeys()) {
//            splitInternal(parent);
//        }
//    }
//
//    // Split internal node
//    private void splitInternal(BPlusNode node) {
//        BPlusNode newNode = new BPlusNode(order);
//        newNode.parent = node.parent;
//
//        // Split at order position
//        int splitPoint = order;
//        int midKey = node.keys.get(splitPoint);
//
//        // Move keys and children to new node (middle key goes up, not copied)
//        newNode.keys.addAll(node.keys.subList(splitPoint + 1, node.keys.size()));
//        newNode.children.addAll(node.children.subList(splitPoint + 1, node.children.size()));
//
//        node.keys.subList(splitPoint, node.keys.size()).clear();
//        node.children.subList(splitPoint + 1, node.children.size()).clear();
//
//        for (BPlusNode child : newNode.children) {
//            child.parent = newNode;
//        }
//
//        insertInParent(node, midKey, newNode);
//    }
//
//    // Find records by key
//    public List<Record> find(int key) {
//        BPlusNode leaf = search(key);
//        List<Record> result = new ArrayList<>();
//
//        for (int i = 0; i < leaf.keys.size(); i++) {
//            if (leaf.keys.get(i) == key) {
//                result.add(leaf.records.get(i));
//            }
//        }
//        return result;
//    }
//    // Print all records in order
//    public void printAllRecords() {
//        BPlusNode current = root;
//        while (!current.isLeaf) {
//            current = current.children.getFirst();
//        }
//
//        System.out.println("All Records (sorted by key):");
//        while (current != null) {
//            for (int i = 0; i < current.records.size(); i++) {
//                Record r = current.records.get(i);
//                System.out.println(r.toString());
//            }
//            current = current.next;
//        }
//    }
//
//    // Print tree structure
//    public void printTree() {
//        if (root == null) return;
//
//        System.out.println("B+ Tree Structure (Order=" + order +
//                ", Min keys=" + order + ", Max keys=" + (2*order) + "):");
//
//        List<BPlusNode> currentLevel = new ArrayList<>();
//        currentLevel.add(root);
//        int level = 0;
//
//        while (!currentLevel.isEmpty()) {
//            System.out.println("\nLevel " + level + ":");
//            List<BPlusNode> nextLevel = new ArrayList<>();
//
//            for (BPlusNode node : currentLevel) {
//                System.out.print("[ ");
//                if(node.keys.size() > 1) {
//                    for (int i = 0; i < node.keys.size() - 1; i++) {
//                        System.out.print(node.keys.get(i) + " | ");
//                    }
//                    System.out.print(node.keys.getLast());
//                } else if (node.keys.size() == 1) {
//                    System.out.print(node.keys.getFirst());
//                }
//                System.out.print(" ] ");
//
//                if (!node.isLeaf) {
//                    nextLevel.addAll(node.children);
//                }
//            }
//
//            currentLevel = nextLevel;
//            level++;
//        }
//        System.out.println();
//    }
//}