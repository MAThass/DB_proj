package ex2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class InternalNode extends NodePage{

    int[] childrenAddresses = new int[ConstValues.MAX_INTERNAL_KEYS + 2];
    int[] keys = new int[ConstValues.MAX_INTERNAL_KEYS + 1];

    public InternalNode(HandleIO handleIO, int pageAddress, ByteBuffer buffer) throws IOException {
        super(handleIO, pageAddress, buffer);
        //deserialize(buffer);
    }

    public InternalNode(HandleIO handleIO, int pageAddress, int parentAddress) throws IOException {
        super(handleIO, pageAddress, parentAddress);
    }

    // Konieczny konstruktor do tworzenia NOWEGO węzła wewnętrznego
    public InternalNode(HandleIO handleIO, int parentAddress, boolean isLeaf) throws IOException {
        super(handleIO, parentAddress, isLeaf);
        // Po super() węzeł ma już przydzielony nowy pageAddress
         // Zapisz pusty węzeł
    }

    @Override
    public void deserialize(ByteBuffer buffer) throws IOException{
        super.deserialize(buffer);
        for( int i = 0; i < this.numberOfKeys; i++ ) {
            childrenAddresses[i] = buffer.getInt();
            keys[i] = buffer.getInt();
        }
        childrenAddresses[numberOfKeys] = buffer.getInt(); // tu jest wyrzycany błąd
    }

    @Override
    public void serialize(ByteBuffer buffer) throws IOException {
        super.serialize(buffer);
        for( int i = 0; i < this.numberOfKeys; i++ ) {
            buffer.putInt(childrenAddresses[numberOfKeys]);
            buffer.putInt(keys[numberOfKeys]);
        }
        buffer.putInt(childrenAddresses[numberOfKeys]);
    }

    @Override
    public void writeToDisk() throws IOException {
        byte[] data = new byte[ConstValues.PAGE_SIZE];
        ByteBuffer buffer = ByteBuffer.wrap(data);
        this.serialize(buffer);
        handleIO.writePage(pageAddress, data);
    }

    //@Override
    public void insert(int newKey, int newChildAddress) throws IOException {
        int insertIndex = numberOfKeys;
        for (int i = numberOfKeys - 1; i >= 0; i--) {
            if (keys[i] > newKey) {
                keys[i + 1] = keys[i];
                childrenAddresses[i + 2] = childrenAddresses[i + 1];
                insertIndex = i;
            } else {
                break;
            }
        }

        keys[insertIndex] = newKey;
        childrenAddresses[insertIndex + 1] = newChildAddress;
        numberOfKeys++;
        if(numberOfKeys > ConstValues.MAX_INTERNAL_KEYS ) {
            throw new IOException("Overflow internal");
        }

        this.writeToDisk();
    }

    @Override
    public NodePage split() throws IOException {
        // 1. Definicja punktu podziału (Indeksy bazują na pełnej tablicy kluczy/wskaźników)

        // Maksymalna liczba kluczy to ConstValues.MAX_INTERNAL_KEYS (d*)
        // Węzeł jest przepełniony, więc ma MAX_INTERNAL_KEYS + 1 kluczy.

        // Klucz środkowy (promocyjny) - jest usuwany i idzie do góry.
        int promotedKeyIndex = ConstValues.MAX_INTERNAL_KEYS / 2;

        // Węzeł oryginalny zatrzyma klucze i wskaźniki przed tym indeksem.
        int oldNodeKeyCount = promotedKeyIndex;
        int oldNodePointerCount = oldNodeKeyCount + 1;

        // 2. Alokacja nowej strony i inicjalizacja nowego węzła wewnętrznego
        // Nowy węzeł dziedziczy parentAddress (który zostanie zaktualizowany przez BPlusTree)
        InternalNode newInternalNode = new InternalNode(handleIO, handleIO.allocatePageAddress(), this.parentAddress);

        // 3. Przeniesienie kluczy i wskaźników do nowego węzła

        // Indeks startowy dla kluczy w nowym węźle (klucze po promocyjnym)
        int sourceKeyStart = promotedKeyIndex + 1;

        // Indeks startowy dla wskaźników w nowym węźle (wskaźniki po kluczu promocyjnym)
        int sourcePointerStart = promotedKeyIndex + 1;

        // Liczba kluczy do przeniesienia (reszta)
        int newInternalNodeKeyCount = ConstValues.MAX_INTERNAL_KEYS - promotedKeyIndex;

        // a) Przeniesienie kluczy
        for (int i = 0; i < newInternalNodeKeyCount; i++) {
            newInternalNode.keys[i] = this.keys[sourceKeyStart + i];
        }

        // b) Przeniesienie wskaźników (ChildrenAddresses)
        for (int i = 0; i < newInternalNodeKeyCount + 1; i++) {
            newInternalNode.childrenAddresses[i] = this.childrenAddresses[sourcePointerStart + i];
        }

        newInternalNode.numberOfKeys = newInternalNodeKeyCount;

        // 4. Modyfikacja oryginalnego węzła (OldNode)

        // a) "Ucięcie" kluczy i wskaźników
        // Stary węzeł zatrzymuje klucze i wskaźniki do indeksu promotedKeyIndex

        // Zresetuj tablice (usuń referencje i utnij rozmiar)
        this.keys = Arrays.copyOf(this.keys, oldNodeKeyCount);
        this.childrenAddresses = Arrays.copyOf(this.childrenAddresses, oldNodePointerCount);

        // Zaktualizuj licznik kluczy
        this.numberOfKeys = oldNodeKeyCount;


        // 6. Zapis na dysk (I/O)
        // Zapisz stary węzeł (po jego modyfikacji)
        this.writeToDisk();

        // Zapisz nowy węzeł (nowa strona w pliku)
        newInternalNode.writeToDisk();

        // 7. Zwróć nowy węzeł do propagacji (klucz promocyjny zostanie pobrany w handleInternalSplit)
        return newInternalNode;
    }

    @Override
    public Record search(int key) {
        return null;
    }

    @Override
    public int searchNode(int key){
        int L = 0;
        int R = this.numberOfKeys - 1;
        int searchedIndex = this.numberOfKeys;
        while(L <= R) {
            int S = (L + R) / 2;
            if (keys[S] >= key) {
                searchedIndex = S;
                R = S - 1;
            } else {
                L = S + 1;
            }
        }
        return childrenAddresses[searchedIndex];
    }

    @Override
    public String displayContent() {
        return "";
    }
}
