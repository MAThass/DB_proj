package ex2;

import java.io.IOException;
import java.nio.ByteBuffer;

public class BPlusTree {
    private boolean isEmpty = true;
    private HandleIO handleIO;
    private int rootAdrress;
    private double alpha;
    private int treeDegree = ConstValues.TREE_DEGREE;

    public BPlusTree(String filename) throws IOException {
        handleIO = new HandleIO(filename);
        handleIO.open();
        rootAdrress = handleIO.allocatePageAddress();
//        LeafNode initialRoot = new LeafNode(handleIO, rootAdrress, true);
//        initialRoot.parentAddress = -1;
//        initialRoot.writeToDisk();
    }

    public BPlusTree(String filename, int treeDegree ) throws IOException {
        handleIO = new HandleIO(filename);
        handleIO.open();

        rootAdrress = handleIO.allocatePageAddress();
        this.treeDegree = treeDegree;
//        LeafNode initialRoot = new LeafNode(handleIO, rootAdrress, true);
//        initialRoot.parentAddress = -1;
//        initialRoot.writeToDisk();
    }

    public void insertRecord(Record record) throws IOException {
        if(!isEmpty){
            isEmpty = false;
            LeafNode leafNode = findLeafNode(record.getKey());
            try {
                leafNode.insert(record);
            } catch (IOException e) {
                handleLeafSplit(leafNode);
            }
        } else{
            LeafNode root = new LeafNode(handleIO, rootAdrress, true);
            root.insert(record);
        }
    }

    // return null when do not find key
    public Record getRecord(int key) throws IOException {
        LeafNode leafNode = findLeafNode(key);
        if (leafNode == null) {
            return null;
        }
        return leafNode.search(key);
    }

    public void traverseTree() throws IOException {

    }

    private NodePage loadNode(int pageAddress) throws IOException {
        byte[] data = new byte[ConstValues.PAGE_SIZE];
        handleIO.readPage(pageAddress, data);
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.position(0);
        boolean isLeaf = (buffer.getInt() == 1);
        buffer.position(0);
        if (isLeaf) {
            return new LeafNode(handleIO, pageAddress, buffer);
        }
        return new InternalNode(handleIO, pageAddress, buffer);
    }

    private LeafNode findLeafNode(int key) throws IOException {
        int currentAdrress = rootAdrress;
        NodePage currentNode = loadNode(currentAdrress);

        while (true){
            if(currentNode.isLeaf){
                return (LeafNode) currentNode;
            }
            currentAdrress = currentNode.searchNode(key);
            currentNode = loadNode(currentAdrress);
        }
        //return null;
    }

    private InternalNode createNewRoot(NodePage oldNode, NodePage newNode, int promotedKey) throws IOException {

        // 1. Alokacja nowej strony i utworzenie obiektu korzenia
        int newRootAddress = handleIO.allocatePageAddress();

        // Nowy korzeń ma parentAddress = -1, co jest jego naturalnym stanem.
        // Musisz mieć konstruktor InternalNode, który przyjmuje adres strony.
        InternalNode newRoot = new InternalNode(handleIO, newRootAddress, -1);

        // 2. Ustawienie ParentAddress dla starych dzieci (nowe węzły)
        // Zaktualizuj adresy rodziców w obu węzłach na adres nowego korzenia.
        oldNode.parentAddress = newRootAddress;
        newNode.parentAddress = newRootAddress;

        // Zapisz zaktualizowane węzły na dysk
        oldNode.writeToDisk();
        newNode.writeToDisk();

        // 3. Wstawienie danych do nowego korzenia
        // Nowy korzeń ma tylko jeden klucz i dwa wskaźniki (dzieci 0 i 1).
        newRoot.numberOfKeys = 1;
        newRoot.keys[0] = promotedKey;

        // Wskaźnik 0 (po lewej od klucza) prowadzi do starego węzła
        newRoot.childrenAddresses[0] = oldNode.pageAddress;

        // Wskaźnik 1 (po prawej od klucza) prowadzi do nowego węzła
        newRoot.childrenAddresses[1] = newNode.pageAddress;

        // 4. Zapis nowego korzenia na dysk
        newRoot.writeToDisk();

        // 5. Aktualizacja głównego wskaźnika drzewa (musi być wykonana w metodzie wywołującej)
        // W metodzie handle...Split: this.rootAdrress = newRoot.pageAddress;

        return newRoot;
    }

    private void handleLeafSplit(LeafNode leafNode) throws IOException{
        LeafNode newLeafNode = (LeafNode) leafNode.split();
        int promotedKey = newLeafNode.records[0].getKey(); // Klucz promocyjny

        // 2. Obsługa podziału korzenia
        if (leafNode.parentAddress == -1) {
            // leafNode był korzeniem. Musimy stworzyć nowy korzeń.
            InternalNode newRoot = createNewRoot(leafNode, newLeafNode, promotedKey);
            this.rootAdrress = newRoot.pageAddress; // Aktualizacja w BPlusTree
            return;
        }

        // 3. Wstawienie do Rodzica
        InternalNode parentNode = (InternalNode) loadNode(leafNode.parentAddress);

        try {
            parentNode.insert(promotedKey, newLeafNode.pageAddress);
        } catch (IOException e) {
            // Węzeł wewnętrzny przepełnił się - kontynuuj propagację
            handleInternalSplit(parentNode);
        }
    }

    private void handleInternalSplit(InternalNode internalNode) throws IOException{
        int promotedKeyIndex = ConstValues.MAX_INTERNAL_KEYS / 2;
        int promotedKey = internalNode.keys[promotedKeyIndex]; // ✅ POPRAWNE

        // 2. Wywołanie podziału (internalNode.split() musi w tym momencie usunąć ten klucz)
        InternalNode newInternalNode = (InternalNode) internalNode.split();

        // KRYTYCZNY KROK: AKTUALIZACJA PARENTADDRESS DZIECI
        // Dzieci, które zostały przeniesione do newInternalNode, muszą mieć zaktualizowany wskaźnik rodzica.
        for (int i = 0; i < newInternalNode.numberOfKeys + 1; i++) {
            int childAddress = newInternalNode.childrenAddresses[i];
            NodePage child = loadNode(childAddress);
            child.parentAddress = newInternalNode.pageAddress;
            child.writeToDisk(); // 👈 Zapis zaktualizowanej strony dziecka
        }

        // KROK 3: OBSŁUGA KORZENIA
        if (internalNode.parentAddress == -1) {
            // Węzeł był korzeniem. Musimy stworzyć nowy korzeń.
            InternalNode newRoot = createNewRoot(internalNode, newInternalNode, promotedKey);
            rootAdrress = newRoot.pageAddress;
            return;
        }

        // KROK 4: Wstawienie do istniejącego Rodzica
        InternalNode internalParentNode = (InternalNode) loadNode(internalNode.parentAddress);

        try {
            // Wstawiamy promowany klucz i adres nowej strony
            internalParentNode.insert(promotedKey, newInternalNode.pageAddress);
        } catch (IOException e) {
            handleInternalSplit(internalParentNode);
        }
    }

    public void printTree() throws IOException {
        if (rootAdrress == -1) {
            System.out.println("Drzewo B+ jest puste.");
            return;
        }

        System.out.println("==========================================");
        System.out.println("STRUKTURA DRZEWA B+ (Wypisanie poziomami)");
        System.out.println("==========================================");

        // Rozpoczęcie rekurencyjnego wypisywania od korzenia na poziomie 0
        printNode(rootAdrress, 0);

        System.out.println("\n==========================================");
        System.out.println("LISTA LIŚCI (Wypisanie sekwencyjne)");
        System.out.println("==========================================");
        printLeafList();
    }

    /**
     * Rekurencyjna funkcja pomocnicza do wypisywania węzła i jego dzieci.
     * * @param pageAddress Adres strony do załadowania i wypisania.
     * @param level Aktualny poziom węzła w drzewie (0 = korzeń).
     */
    private void printNode(int pageAddress, int level) throws IOException {
        if (pageAddress == -1) {
            return; // Zakończ, jeśli adres jest nieprawidłowy
        }

        NodePage currentNode = loadNode(pageAddress);
        String indent = "  ".repeat(level);

        // 1. Wypisanie aktualnego węzła
        System.out.print(indent + "Poziom " + level + " | Adres: " + pageAddress + " | Rodzic: " + currentNode.parentAddress + " | ");

        if (currentNode.isLeaf) {
            LeafNode leafNode = (LeafNode) currentNode;

            System.out.print("LIŚĆ | Klucze: [");
            for (int i = 0; i < leafNode.numberOfKeys; i++) {
                System.out.print(leafNode.records[i].getKey());
                if (i < leafNode.numberOfKeys - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println("]");
            // Nie kontynuujemy rekursji (liście to koniec drzewa)

        } else {
            InternalNode internalNode = (InternalNode) currentNode;

            System.out.print("WEWNĘTRZNY | Klucze: [");
            for (int i = 0; i < internalNode.numberOfKeys; i++) {
                System.out.print(internalNode.keys[i]);
                if (i < internalNode.numberOfKeys - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println("] | Wskaźniki: " + internalNode.numberOfKeys + 1);

            // 2. Rekurencyjne wywołanie dla dzieci
            for (int i = 0; i <= internalNode.numberOfKeys; i++) {
                printNode(internalNode.childrenAddresses[i], level + 1);
            }
        }
    }

    /**
     * Przechodzi przez wszystkie węzły liści, używając listy dwukierunkowej.
     */
    private void printLeafList() throws IOException {
        // 1. Znajdź pierwszy liść (najbardziej lewy liść)
        int currentAddress = rootAdrress;
        NodePage currentNode = loadNode(currentAddress);

        // Nawigacja w dół do lewego liścia
        while (!currentNode.isLeaf) {
            currentAddress = currentNode.searchNode(Integer.MIN_VALUE); // Użyj najmniejszego klucza, by iść lewym wskaźnikiem
            currentNode = loadNode(currentAddress);
        }

        LeafNode currentLeaf = (LeafNode) currentNode;

        // 2. Sekwencyjne wypisywanie liści
        System.out.print("Liście sekwencyjnie (Key, Address): ");

        while (currentLeaf.pageAddress != -1) {
            System.out.print("[Adres " + currentLeaf.pageAddress + ": ");
            for (int i = 0; i < currentLeaf.numberOfKeys; i++) {
                System.out.print(currentLeaf.records[i].getKey());
                if (i < currentLeaf.numberOfKeys - 1) {
                    System.out.print(", ");
                }
            }
            System.out.print("] --> ");

            if (currentLeaf.nextLeafAddress == -1) {
                System.out.println("KONIEC");
                break;
            }

            // Przejście do następnego liścia
            currentLeaf = (LeafNode) loadNode(currentLeaf.nextLeafAddress);
        }
    }
}
