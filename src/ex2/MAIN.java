//import ex2.ConstValues;
//import ex2.Display;
//import ex2.*;
//import ex2.Record;
//
//import javax.swing.*;
//
//private int randomData(){
//    Display.RandomMenu();
//    Scanner sc = new Scanner(System.in);
//    int instruction = sc.nextInt();
//
//    return instruction;
//}
//
//private String dataFromFile(){
//    String fileName = "";
//    Display.FielMenu();
//    JFileChooser chooser = new JFileChooser();
//    chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
//
//    int result = chooser.showOpenDialog(null);
//
//    if (result == JFileChooser.APPROVE_OPTION) {
//        fileName = chooser.getSelectedFile().getAbsolutePath();
//    }
//
//    return fileName;
//}
//
//private void dataFromKeyboard() throws IOException {
//    Scanner sc = new Scanner(System.in);
//    String fileName = "gen.csv";
//    FileWriter myFile = new FileWriter(fileName);
//    while (true) {
//        Display.KeyBoardMenu();
//
//        String input = sc.nextLine();
//        if (input.equalsIgnoreCase("q")) {
//            break;
//        }
//        String[] parts = input.split("\\s+");
//        if (parts.length != 2) {
//            System.out.println("Błędny format");
//            System.out.println("BPoprawny to: masa wysokość");
//            continue;
//        }
//        try {
//            double recordMass = Double.parseDouble(parts[0]);
//            double recordHeight = Double.parseDouble(parts[1]);
//            if(recordMass >= 0 || recordHeight >= 0){
//                myFile.write(recordMass + ";" + recordHeight + "\n");
//            }
//            else{
//                System.out.println("Ponadno wartości ujemne");
//            }
//
//        } catch (NumberFormatException e) {
//            System.out.println("Podane wartości nie są liczbami!");
//        }
//    }
//    myFile.close();
//
//}
//
//private void doPrintRuns() throws IOException {
//
//    Scanner sc = new Scanner(System.in);
//    while (true) {
//        Display.printRunsMenu();
//        String input = sc.nextLine();
//        if(input.equals("1")){
//            ConstValues.printRuns = true;
//            break;
//        }
//        if(input.equals("2")){
//            ConstValues.printRuns = false;
//            break;
//        }
//    }
//
//}
//
//private static final String DATABASE_FILE = "bplustree_test.db";
//// Stała dla liczby rekordów do wstawienia
//private static final int NUM_RECORDS = 20;
//
//void main() throws IOException {
////    Scanner sc = new Scanner(System.in);
////    int numberOfRecords = ConstValues.TREE_DEGREE;
////    String fileName = "gen.csv";
////    LOOP:
////    while (true) {
////        Display.MainMenu();
////        char instruction = sc.next().charAt(0);
////        switch (instruction) {
////            case '1':
////                numberOfRecords = randomData();
////                //GenRandom.createFile(numberOfRecords);
////                break LOOP;
////            case '2':
////                fileName = dataFromFile();
////                break LOOP;
////            case '3':
////                dataFromKeyboard();
////                break LOOP;
////            default:
////                Display.IncorrectInputMessage();
////        }
////    }
////
////    doPrintRuns();
////    sc.close();
//
//    //MergingWithLargeBuffers.Merge(fileName);
//
//
//
//
//        // 1. Inicjalizacja stałych (Tylko w celach demonstracyjnych, powinny być w ConstValues!)
//        // Ustawienie małych wartości wymuszających podział, np. dla d*=2, r=2
//        // ConstValues.MAX_INTERNAL_KEYS = 2;
//        // ConstValues.MAX_LEAF_KEYS = 2;
//
//        BPlusTree tree = null;
//        Random random = new Random();
//        int[] keysToInsert = new int[NUM_RECORDS];
//
//        try {
//            // USUŃ ISTNIEJĄCY PLIK (OPCJONALNIE)
//            // Usunięcie starego pliku bazy danych, jeśli istnieje, aby test był czysty
//            new java.io.File(DATABASE_FILE).delete();
//
//            // 2. INICJALIZACJA DRZEWA
//            System.out.println("--- 🌳 INICJALIZACJA DRZEWA ---");
//            tree = new BPlusTree(DATABASE_FILE);
//            System.out.println("Utworzono nowy plik bazy danych: " + DATABASE_FILE);
//
//            // 3. PRZYGOTOWANIE REKORDÓW
//            System.out.println("--- 💾 GENEROWANIE DANYCH ---");
//            for (int i = 0; i < NUM_RECORDS; i++) {
//                // Generowanie unikalnych (lub pseudolosowych) kluczy
//                keysToInsert[i] = random.nextInt(1000) + 1;
//                // Tworzenie rekordu
//                Record newRecord = new Record(keysToInsert[i],random.nextDouble(), random.nextDouble());
//
//                // 4. WSTAWIANIE REKORDÓW
//                System.out.printf("Wstawiam rekord nr %2d (Klucz: %3d)...%n", i + 1, keysToInsert[i]);
//                tree.insertRecord(newRecord);
//                tree.printTree();
//            }
//
//            // Wypisanie kluczy wstawionych (do późniejszej weryfikacji)
//            Arrays.sort(keysToInsert);
//            System.out.println("\nKlucze wstawione (posortowane): " + Arrays.toString(keysToInsert));
//
//            // 5. WERYFIKACJA DRZEWA
//            System.out.println("\n--- 🔎 WERYFIKACJA STRUKTURY DRZEWA ---");
//            tree.printTree();
//
//            // 6. WERYFIKACJA WYSZUKIWANIA
//            System.out.println("\n--- 🔍 TEST WYSZUKIWANIA (5 losowych kluczy) ---");
//
//            // Losowy wybór kluczy do wyszukania
//            for (int i = 0; i < 5; i++) {
//                int keyToSearch = keysToInsert[random.nextInt(NUM_RECORDS)];
//                Record foundRecord = tree.getRecord(keyToSearch);
//
//                if (foundRecord != null) {
//                    System.out.printf("   [SUKCES] Znaleziono klucz %3d: %s%n", keyToSearch, foundRecord.toString());
//                } else {
//                    System.err.printf("   [BŁĄD] Nie znaleziono klucza %3d (powinien istnieć).%n", keyToSearch);
//                }
//            }
//
//            // Test klucza, którego na pewno nie ma
//            int nonExistentKey = 9999;
//            if (tree.getRecord(nonExistentKey) == null) {
//                System.out.printf("   [OK] Nie znaleziono klucza %d (poprawne zachowanie).%n", nonExistentKey);
//            }
//
//        } catch (IOException e) {
//            System.err.println("\n--- 🛑 WYSTĄPIŁ BŁĄD KRYTYCZNY ---");
//            System.err.println("Wystąpił błąd podczas operacji I/O lub przepełnienia (overflow): " + e.getMessage());
//            e.printStackTrace();
//        } finally {
//            if (tree != null) {
//                // Konieczne zamknięcie uchwytu do pliku
//                // tree.handleIO.close(); // Zakładam, że masz publiczną metodę close()
//                System.out.println("\n--- Zakończono testowanie ---");
//            }
//        }
//
//}
//
//
//
//
