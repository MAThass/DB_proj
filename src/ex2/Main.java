import ex2.*;

import javax.swing.*;
import java.io.IOException;

private int randomData(){
    Display.RandomMenu();
    Scanner sc = new Scanner(System.in);
    int instruction = sc.nextInt();

    return instruction;
}

private String dataFromFile(){
    String fileName = "";
    Display.FielMenu();
    JFileChooser chooser = new JFileChooser();
    chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

    int result = chooser.showOpenDialog(null);

    if (result == JFileChooser.APPROVE_OPTION) {
        fileName = chooser.getSelectedFile().getAbsolutePath();
    }

    return fileName;
}

private void dataFromKeyboard() throws IOException {
    Scanner sc = new Scanner(System.in);
    String fileName = "gen.csv";
    FileWriter myFile = new FileWriter(fileName);
    while (true) {
        Display.KeyBoardMenu();

        String input = sc.nextLine();
        if (input.equalsIgnoreCase("q")) {
            break;
        }
        String[] parts = input.split("\\s+");
        if (parts.length != 2) {
            System.out.println("Błędny format");
            System.out.println("BPoprawny to: masa wysokość");
            continue;
        }
        try {
            double recordMass = Double.parseDouble(parts[0]);
            double recordHeight = Double.parseDouble(parts[1]);
            if(recordMass >= 0 || recordHeight >= 0){
                myFile.write(recordMass + ";" + recordHeight + "\n");
            }
            else{
                System.out.println("Ponadno wartości ujemne");
            }

        } catch (NumberFormatException e) {
            System.out.println("Podane wartości nie są liczbami!");
        }
    }
    myFile.close();

}

private void doPrintRuns() throws IOException {

    Scanner sc = new Scanner(System.in);
    while (true) {
        Display.printRunsMenu();
        String input = sc.nextLine();
        if(input.equals("1")){
            ConstValues.printRuns = true;
            break;
        }
        if(input.equals("2")){
            ConstValues.printRuns = false;
            break;
        }
    }

}

void main() throws IOException {
    Scanner sc = new Scanner(System.in);
    int numberOfRecords = 100;
    String fileName = "gen.csv";
    LOOP:
    while (true) {
        Display.MainMenu();
        char instruction = sc.next().charAt(0);
        switch (instruction) {
            case '1':
                numberOfRecords = randomData();
                //GenRandom.createFile(numberOfRecords);
                break LOOP;
            case '2':
                fileName = dataFromFile();
                break LOOP;
            case '3':
                dataFromKeyboard();
                break LOOP;
            default:
                Display.IncorrectInputMessage();
        }
    }

    doPrintRuns();
    sc.close();

    //MergingWithLargeBuffers.Merge(fileName);
}


