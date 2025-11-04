import ex1.HandleFile;
import ex1.Record;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
void main() {
    HandleFile file = new HandleFile("./data.csv");
    try {
        file.open();
    } catch (IOException e) {
        System.err.println("Error opening file: " + e.getMessage());
    }

    List<Record> records = new ArrayList<>();

    do{
        try {
            records = file.readBlock();
            //System.out.println("Read one block");
        }catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        for(Record record : records){
            System.out.println(record);
        }
    }while (!records.isEmpty());

    try{
        file.close();
    }catch (IOException e) {
        System.err.println("Error closing file: " + e.getMessage());
    }

//    try {
//        records = file.readFileByChunks();
//    } catch (IOException e) {
//        throw new RuntimeException(e);
//    }
//
//    try {
//        String[] lines = (file.readOneBlock()).split("\\r?\\n");
//        for (String line : lines) {
//            line = line.trim();
//            if (!line.isEmpty()) {
//                try {
//                    records.add(new Record(line));
//                } catch (Exception e) {
//                    System.err.println("Skipping invalid record: " + line);
//                }
//            }
//        }
//    } catch (IOException e) {
//        throw new RuntimeException(e);
//    }
//
//   records.forEach(record -> {IO.println(record.toString());});

}
