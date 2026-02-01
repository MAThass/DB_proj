package BPlusTree;

import java.util.ArrayList;
import java.util.List;

class BPlusTree {
    public int order = ConstValues.d;
    public int rootAddress;
    private HandleIO io;

    public BPlusTree(String fileName) throws Exception {
        this.io = new HandleIO(fileName);
        io.open();
        io.delete();
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
        io.delete();
        BPlusNode root = new BPlusNode(order);
        root.isLeaf = true;
        root.pageAddress = io.setPageAddress();
        this.rootAddress = root.pageAddress;
        writeNode(root);
    }

    public void writeNode(BPlusNode node) throws Exception {
        byte[] data = node.serialize();
        io.writePage(node.pageAddress, data);
    }

    public BPlusNode readNode(int pageAddress) throws Exception {
        return readNode(pageAddress, false);
    }
    public BPlusNode readNode(int pageAddress, boolean printing) throws Exception {
        byte[] data = new byte[ConstValues.PAGE_SIZE];
        io.readPage(pageAddress, data, printing);
        return BPlusNode.deserialize(data);
    }

    public BPlusNode allocateNode(boolean isLeaf) throws Exception {
        BPlusNode node = new BPlusNode(order);
        node.isLeaf = isLeaf;
        node.pageAddress = io.setPageAddress();
        return node;
    }

    ////##############################################################################################################################################
    ////##############################################################################################################################################
    //SEARCH
    ////##############################################################################################################################################
    public Record search(int key) throws Exception {
        BPlusNode leaf = searchLeaf(key);

        for (int i = 0; i < leaf.keys.size(); i++) {
            if (leaf.keys.get(i) == key) {
                return leaf.records.get(i);
            }
        }

        return null;
    }
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

    ////##############################################################################################################################################
    ////##############################################################################################################################################
    //INSERT
    ////##############################################################################################################################################
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
    private void insertAtLeaf(BPlusNode leaf, int key, Record record) {
        if (leaf.keys.isEmpty()) {
            leaf.keys.add(key);
            leaf.records.add(record);
        } else {
            int i = 0;
            while (i < leaf.keys.size() && key > leaf.keys.get(i)) {
                i++;
            }
            if (i < leaf.keys.size() && key == leaf.keys.get(i)) {
                //leaf.records.set(i, record);
                System.out.println("record with key " + key + " exist!!!");
            }else {
                leaf.keys.add(i, key);
                leaf.records.add(i, record);
            }

        }
    }
    //may be used during split
    private void insertInParent(BPlusNode left, int key, BPlusNode right) throws Exception {
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

        //insert in existing parent
        BPlusNode parent = readNode(left.parentAddress);

        //find position of left child
        int i = 0;
        while (i < parent.childAddresses.size() &&
                parent.childAddresses.get(i) != left.pageAddress) {
            i++;
        }

        //insert key and right child
        parent.keys.add(i, key);
        parent.childAddresses.add(i + 1, right.pageAddress);
        right.parentAddress = parent.pageAddress;

        writeNode(parent);
        writeNode(right);

        //check if parent needs split
        if (parent.keys.size() > 2 * order) {
            splitInternal(parent);
        }
    }

    ////##############################################################################################################################################
    ////##############################################################################################################################################
    //SPLIT
    ////##############################################################################################################################################
    private void splitLeaf(BPlusNode leaf) throws Exception {
        BPlusNode newLeaf = allocateNode(true);
        newLeaf.parentAddress = leaf.parentAddress;

        int splitPoint = order;

        //half keys and records to new leaf
        newLeaf.keys.addAll(leaf.keys.subList(splitPoint, leaf.keys.size()));
        newLeaf.records.addAll(leaf.records.subList(splitPoint, leaf.records.size()));

        leaf.keys.subList(splitPoint, leaf.keys.size()).clear();
        leaf.records.subList(splitPoint, leaf.records.size()).clear();

        //update next pointers
        newLeaf.nextAddress = leaf.nextAddress;
        leaf.nextAddress = newLeaf.pageAddress;

        writeNode(leaf);
        writeNode(newLeaf);

        insertInParent(leaf, newLeaf.keys.get(0), newLeaf);
    }

    private void splitInternal(BPlusNode node) throws Exception {
        BPlusNode newNode = allocateNode(false);
        newNode.parentAddress = node.parentAddress;

        int splitPoint = order;
        int midKey = node.keys.get(splitPoint);

        // move keys and children to new node
        newNode.keys.addAll(node.keys.subList(splitPoint + 1, node.keys.size()));
        newNode.childAddresses.addAll(node.childAddresses.subList(splitPoint + 1, node.childAddresses.size()));

        node.keys.subList(splitPoint, node.keys.size()).clear();
        node.childAddresses.subList(splitPoint + 1, node.childAddresses.size()).clear();

        // update parent pointers for moved children
        for (int childAddr : newNode.childAddresses) {
            BPlusNode child = readNode(childAddr);
            child.parentAddress = newNode.pageAddress;
            writeNode(child);
        }

        writeNode(node);
        writeNode(newNode);

        insertInParent(node, midKey, newNode);
    }

    ////##############################################################################################################################################
    ////##############################################################################################################################################
    //DELETE
    ////##############################################################################################################################################
    public boolean delete(int key) throws Exception {
        BPlusNode leaf = searchLeaf(key);
        int index = -1;
        for (int i = 0; i < leaf.keys.size(); i++) {
            if (leaf.keys.get(i) == key) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return false; //not found
        }
        //remove key and record
        leaf.keys.remove(index);
        leaf.records.remove(index);
        //check if underflow
        if (leaf.pageAddress == rootAddress) {
            writeNode(leaf);
            return true; //root may have less
        }
        if (leaf.keys.size() >= ConstValues.FILING_LEVEL) {
            writeNode(leaf);
            return true;
        }
        handleLeafUnderflow(leaf);
        return true;
    }

    //##############################################################################################################################################
    //HANDLE LEAF
    private void handleLeafUnderflow(BPlusNode leaf) throws Exception {
        BPlusNode parent = readNode(leaf.parentAddress);

        //fnd leaf position in parent
        int leafIndex = -1;
        for (int i = 0; i < parent.childAddresses.size(); i++) {
            if (parent.childAddresses.get(i) == leaf.pageAddress) {
                leafIndex = i;
                break;
            }
        }

        //try to borrow from left sibling
        if (leafIndex > 0) {
            BPlusNode leftSibling = readNode(parent.childAddresses.get(leafIndex - 1));
            if (leftSibling.keys.size() > ConstValues.FILING_LEVEL) {
                borrowFromLeftLeaf(leaf, leftSibling, parent, leafIndex);
                return;
            }
        }

        //try to borrow from right sibling
        if (leafIndex < parent.childAddresses.size() - 1) {
            BPlusNode rightSibling = readNode(parent.childAddresses.get(leafIndex + 1));
            if (rightSibling.keys.size() > ConstValues.FILING_LEVEL) {
                borrowFromRightLeaf(leaf, rightSibling, parent, leafIndex);
                return;
            }
        }

        //cannot borrow, must merge with sibling
        if (leafIndex > 0) {
            //with leaft
            BPlusNode leftSibling = readNode(parent.childAddresses.get(leafIndex - 1));
            mergeLeaves(leftSibling, leaf, parent, leafIndex - 1);
        } else {
            //with right
            BPlusNode rightSibling = readNode(parent.childAddresses.get(leafIndex + 1));
            mergeLeaves(leaf, rightSibling, parent, leafIndex);
        }
    }

    private void borrowFromLeftLeaf(BPlusNode leaf, BPlusNode leftSibling,
                                    BPlusNode parent, int leafIndex) throws Exception {
        //move last record from left sibling to beginning of leaf
        int borrowedKey = leftSibling.keys.remove(leftSibling.keys.size() - 1);
        Record borrowedRecord = leftSibling.records.remove(leftSibling.records.size() - 1);

        leaf.keys.add(0, borrowedKey);
        leaf.records.add(0, borrowedRecord);

        parent.keys.set(leafIndex - 1, leaf.keys.get(0));

        writeNode(leaf);
        writeNode(leftSibling);
        writeNode(parent);
    }

    private void borrowFromRightLeaf(BPlusNode leaf, BPlusNode rightSibling,
                                     BPlusNode parent, int leafIndex) throws Exception {
        //move first record from right sibling to end of leaf
        int borrowedKey = rightSibling.keys.remove(0);
        Record borrowedRecord = rightSibling.records.remove(0);

        leaf.keys.add(borrowedKey);
        leaf.records.add(borrowedRecord);

        parent.keys.set(leafIndex, rightSibling.keys.get(0));

        writeNode(leaf);
        writeNode(rightSibling);
        writeNode(parent);
    }

    private void mergeLeaves(BPlusNode left, BPlusNode right,
                             BPlusNode parent, int leftIndex) throws Exception {
        //move all records from right to left
        left.keys.addAll(right.keys);
        left.records.addAll(right.records);
        left.nextAddress = right.nextAddress;

        //remove right child from parent
        parent.keys.remove(leftIndex);
        parent.childAddresses.remove(leftIndex + 1);

        writeNode(left);

        //check parent underflow
        if (parent.pageAddress == rootAddress) {
            if (parent.keys.isEmpty()) {
                //make left child the new root
                rootAddress = left.pageAddress;
                left.parentAddress = -1;
                writeNode(left);
            } else {
                writeNode(parent);
            }
        } else if (parent.keys.size() < ConstValues.FILING_LEVEL) {
            handleInternalUnderflow(parent);
        } else {
            writeNode(parent);
        }
    }

    //##############################################################################################################################################
    //HANDLE INTERNAL
    private void handleInternalUnderflow(BPlusNode node) throws Exception {
        BPlusNode parent = readNode(node.parentAddress);

        //find node position in parent
        int nodeIndex = -1;
        for (int i = 0; i < parent.childAddresses.size(); i++) {
            if (parent.childAddresses.get(i) == node.pageAddress) {
                nodeIndex = i;
                break;
            }
        }

        //try to borrow from left sibling
        if (nodeIndex > 0) {
            BPlusNode leftSibling = readNode(parent.childAddresses.get(nodeIndex - 1));
            if (leftSibling.keys.size() > order) {
                borrowFromLeftInternal(node, leftSibling, parent, nodeIndex);
                return;
            }
        }

        //try to borrow from right sibling
        if (nodeIndex < parent.childAddresses.size() - 1) {
            BPlusNode rightSibling = readNode(parent.childAddresses.get(nodeIndex + 1));
            if (rightSibling.keys.size() > order) {
                borrowFromRightInternal(node, rightSibling, parent, nodeIndex);
                return;
            }
        }
        //merge
        if (nodeIndex > 0) {
            BPlusNode leftSibling = readNode(parent.childAddresses.get(nodeIndex - 1));
            mergeInternal(leftSibling, node, parent, nodeIndex - 1);
        } else {
            BPlusNode rightSibling = readNode(parent.childAddresses.get(nodeIndex + 1));
            mergeInternal(node, rightSibling, parent, nodeIndex);
        }
    }

    private void borrowFromLeftInternal(BPlusNode node, BPlusNode leftSibling,
                                        BPlusNode parent, int nodeIndex) throws Exception {
        //move parent key down to node
        int parentKey = parent.keys.get(nodeIndex - 1);
        node.keys.add(0, parentKey);

        //move last child from left sibling to node
        int movedChild = leftSibling.childAddresses.remove(leftSibling.childAddresses.size() - 1);
        node.childAddresses.add(0, movedChild);

        //update moved child parent
        BPlusNode movedChildNode = readNode(movedChild);
        movedChildNode.parentAddress = node.pageAddress;
        writeNode(movedChildNode);

        //move last key from left sibling to parent
        int movedKey = leftSibling.keys.remove(leftSibling.keys.size() - 1);
        parent.keys.set(nodeIndex - 1, movedKey);

        writeNode(node);
        writeNode(leftSibling);
        writeNode(parent);
    }

    private void borrowFromRightInternal(BPlusNode node, BPlusNode rightSibling,
                                         BPlusNode parent, int nodeIndex) throws Exception {
        //move parent key down to node
        int parentKey = parent.keys.get(nodeIndex);
        node.keys.add(parentKey);

        //move first child from right sibling to node
        int movedChild = rightSibling.childAddresses.remove(0);
        node.childAddresses.add(movedChild);

        //update moved child parent
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

    private void mergeInternal(BPlusNode left, BPlusNode right,
                               BPlusNode parent, int leftIndex) throws Exception {
        //getkey from parent
        int parentKey = parent.keys.get(leftIndex);
        left.keys.add(parentKey);

        //move all keys and children from right to left
        left.keys.addAll(right.keys);
        left.childAddresses.addAll(right.childAddresses);

        //update parent pointers for moved children
        for (int childAddr : right.childAddresses) {
            BPlusNode child = readNode(childAddr);
            child.parentAddress = left.pageAddress;
            writeNode(child);
        }

        //remove right child from parent
        parent.keys.remove(leftIndex);
        parent.childAddresses.remove(leftIndex + 1);

        writeNode(left);

        //check parent underflow
        if (parent.pageAddress == rootAddress) {
            if (parent.keys.isEmpty()) {
                //make left root
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


    //##############################################################################################################################################
    //##############################################################################################################################################
    //UPDATE
    //##############################################################################################################################################
    public boolean update(int oldKey, Record newRecord) throws Exception {
        boolean deleted = delete(oldKey);
        if (!deleted) {
            return false;
        }

        insert(newRecord);
        return true;
    }



    public void close() throws Exception {
        io.close();
    }

    public void clearFile() throws Exception {
        io.delete();
    }


    public void printAllRecords() throws Exception {
        BPlusNode current = readNode(rootAddress, true);
        while (!current.isLeaf) {
            current = readNode(current.childAddresses.get(0), true);
        }

        System.out.println("All Records:");
        int count = 0;
        while (current != null) {
            for (int i = 0; i < current.records.size(); i++) {
                Record r = current.records.get(i);
                System.out.printf("Key=%d, mass=%f, height=%f\n",
                        r.getKey(), r.getMass(), r.getHeight());
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

    public void printTree() throws Exception {
        System.out.println("B+ Tree Structure (Order=" + order + "):");

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
        System.out.println("Level=" + (level-1));
        System.out.println();
    }
}