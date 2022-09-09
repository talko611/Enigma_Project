package Main;

import Engine.*;
import Engine.engineAnswers.*;
import Engine.enums.ReflectorGreekNums;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ConsolePresentor {
    Engine engine;
    boolean isLoadFile;
    boolean isConfigMachine;

    public ConsolePresentor(){
        this.engine = new EngineImp();
        this.isConfigMachine = false;
        this.isLoadFile = false;
    }

    public void start()  {
        System.out.println("---------------------------------------------------------");
        System.out.println("Welcome to Enigma Machine App");
        System.out.println("---------------------------------------------------------");
        presentMainManu();
        Scanner scanner = new Scanner(System.in);
        int choice;
        boolean exit = false;

        while (!exit) {
            System.out.print(">>");
            choice =getNextInt(scanner);

            switch (choice) {
                case 1:
                    loadFile(scanner);
                    presentMainManu();
                    break;
                case 2:
                    presentMachineDetails(scanner);
                    presentMainManu();
                    break;
                case 3:
                    manualConfig(scanner);
                    presentMainManu();
                    break;
                case 4:
                    autoConfig();
                    presentMainManu();
                    break;
                case 5:
                    encryptDecrypt(scanner);
                    presentMainManu();
                    break;
                case 6:
                    resetMachine();
                    presentMainManu();
                    break;
                case 7:
                    presentStatistics(scanner);
                    presentMainManu();
                    break;
                case 8:
                    exit = true;
                    System.out.println("GOODBYE!");
                    break;
                default:
                    System.out.println("Invalid input!. Please enter only one of the values in the menu");

            }
        }

    }

    void resetMachine(){
        System.out.println("---------------------------------------------------------");
        if(isLoadFile){
            if(isConfigMachine){
                this.engine.resetMachine();
                System.out.println("Machine is reset\n");
            }
            else {
                System.out.println("Machine cannot be reset. First need to config machine\n");
            }
        }
        else {
            System.out.println("Machine cannot be reset. First need to load data from file\n");
        }
    }
    private void presentMainManu(){
        System.out.println("---------------------------------------------------------");
        System.out.println("Please choose one of the following options:");
        System.out.println("1. Load from file");
        System.out.println("2. Present Machine details");
        System.out.println("3. Manual config machine");
        System.out.println("4. Auto config machine");
        System.out.println("5. Encrypt/Decrypt");
        System.out.println("6. Reset to current configuration");
        System.out.println("7. Get statistics and history");
        System.out.println("8. Exit");
    }

    private void loadFile(Scanner scanner){
        scanner.nextLine();
        boolean exit = false;
        System.out.println("---------------------------------------------------------");
        System.out.println("Please enter a file path:");
        System.out.print(">>");
        InputOperationAnswer answer = engine.loadFromFile(scanner.nextLine());
        while (!exit){
            if(!answer.isSuccess()){
                System.out.println(answer.getMessage());
                System.out.println("Would you like to try again?[Y/N]");
                System.out.print(">>");
                String input = scanner.nextLine();
                while (!input.equalsIgnoreCase("y") && !input.equalsIgnoreCase("n")){
                    System.out.println("Invalid input!\nPlease enter [Y/N]");
                    System.out.print(">>");
                    input = scanner.nextLine();
                }
                if(input.equalsIgnoreCase("Y")){
                    System.out.println("Enter file path again:");
                    System.out.print(">>");
                    answer = engine.loadFromFile(scanner.nextLine());
                }
                else{
                 exit = true;
                }
            }
            else {
                System.out.println("\n" + answer.getMessage()+ "\n");
                this.isLoadFile = true;
                this.isConfigMachine = false;
                exit = true;
            }
        }
    }

    private void autoConfig(){
        System.out.println("---------------------------------------------------------");
        if(this.isLoadFile){
            InputOperationAnswer answer = this.engine.autoConfig();
            if(answer.isSuccess()) this.isConfigMachine = true;
            System.out.println(answer.getMessage() + "\n");
        }
        else{
            System.out.println("Machine data isn't loaded from file. please load data from file first\n");
        }
    }

    private void presentMachineDetails(Scanner scanner){
        System.out.println("---------------------------------------------------------");
        if(!isLoadFile) {
            System.out.println("Cannot present details. Machine is not loaded yet\n");
            return;
        }
        scanner.nextLine();
        MachineDetailsAnswer answer = this.engine.getMachineDetails();
        System.out.println("Machine details:");
        System.out.println("Used Vs Available Rotors: " + answer.getUsedVsAvailableRotors());
        System.out.println("Number of reflectors: " + answer.getNumOfReflectors());
        System.out.println("Number Of messages encrypted: " + answer.getNumOfProcessedMessages());
        if(answer.isMachineConfig()){
            System.out.println("Initial configuration: " + answer.getInitialConfiguration());
            System.out.println("Current state: " + answer.getCurrentState());
        }
        else {
            System.out.println("Machine is not confined yet!");
        }
        subMenuLoadOrBack(scanner);
    }

    private void presentStatistics(Scanner scanner) {
        System.out.println("---------------------------------------------------------");
        AtomicInteger counter = new AtomicInteger();
        if (!this.isLoadFile){
            System.out.println("Stats are unavailable please load file first\n");
        return;
        }
        try {
            StatisticsAnswer answer = this.engine.getStatistics();
            if(answer.getStats().isEmpty()){
                System.out.println("Stats are empty right now.\nTry to use the machine first in order to get statistics ");
            }
            else{
                System.out.println("statistics:");
                answer.getStats().forEach((i,k) -> {
                    System.out.println(i);
                    counter.set(1);
                    k.forEach((s)-> {
                        System.out.println(counter + ". <" + s.getSrc() + ">" + "-->" + "<" + s.getOut() + ">(" + String.format("%,d", s.getDuration()) + " ns)");
                        counter.set(counter.incrementAndGet());
                    });
                });
            }
            subMenuLoadOrBack(scanner);
        }catch (CloneNotSupportedException e){
            System.out.println("We are sorry something went wrong with the stats.\nWe will make sure that the problem will be handled shortly");
        }
    }

    private void subMenuLoadOrBack(Scanner scanner) {
        System.out.println("\nPlease choose one of the following options:");
        System.out.println("1. Load different file");
        System.out.println("2. Back to Main menu");
        System.out.print(">>");
        int choice = getNextInt(scanner);
        while(choice != 1 && choice != 2){
            System.out.println("Invalid input please enter valid number from the above");
            System.out.print(">>");
            choice = getNextInt(scanner);
        }
        if(choice == 1){
            loadFile(scanner);
        }
    }

    private void subMenuEncrypt(EncryptDecryptMessage answer){
        System.out.println("Please choose one of the following options:");
        if (answer.getSuccess()) {
            System.out.println("1. Process again");
        } else {
            System.out.println("1. Try again");
        }
        System.out.println("2. Load different file");
        System.out.println("3. Reset to initial config");
        System.out.println("4. Back to main menu");
        System.out.print(">>");
    }

    private int getNextInt(Scanner scanner){
        try {
            return scanner.nextInt();
        }catch (NoSuchElementException | IllegalStateException e){
            scanner.nextLine();
            return 0;
        }
    }

    private void encryptDecrypt(Scanner scanner) {
        System.out.println("---------------------------------------------------------");
        if(!isConfigMachine){
            System.out.println("Cannot process messages.\nPlease config machine first\n");
            return;
        }
        int choice;
        EncryptDecryptMessage answer;
        do {
            scanner.nextLine();
            System.out.println("Please enter a message to process");
            System.out.print(">>");
            answer = this.engine.encryptDecryptMessage(scanner.nextLine(),true,false);
            if (answer.getSuccess()) {
                System.out.println("<" + answer.getSrc() + ">--><" + answer.getOut() + ">(" + String.format("%,d", answer.getDuration()) + " ns)");
            } else {
                System.out.println(answer.getError());
            }

            subMenuEncrypt(answer);
            do {
                choice = getNextInt(scanner);

                if (choice == 3) {
                    this.engine.resetMachine();
                    System.out.println("Machine is rested");
                    choice = 5;
                    subMenuEncrypt(answer);
                } else if ((choice < 1) || (choice > 4)) {
                    System.out.println("Invalid input. Please enter a number between 1-4");
                    System.out.print(">>");
                }
            } while (choice < 1 || choice > 4);

        } while (choice != 2 && choice != 4);
        if(choice == 2){
            loadFile(scanner);
        }
    }

    private void manualConfig(Scanner scanner){
        System.out.println("---------------------------------------------------------");
        if(!isLoadFile){
            System.out.println("Please load from file before trying to config machine");
            return;
        }
        manualConfigRotors(scanner);
        manualConfigOffsets(scanner);
        manualConfigReflector(scanner);
        manualConfigPlugBoard(scanner);
        System.out.println("Machine config successfully\n");
    }

    private void manualConfigRotors(Scanner scanner){
        InputOperationAnswer answer;
        System.out.println("---------------------------------------------------------");
        System.out.println("Please enter rotors ids seperated by comma/space. Enter the rotors' ids from right to left");
        System.out.println("Meaning most right rotor will be enter first(Example: input-> 3,2,1)\nrotors order in the machine will be rotor 3 is the most right 1 is the most left");
        scanner.nextLine();
        do {
            System.out.print(">>");
            answer = this.engine.manualConfigRotors(scanner.nextLine());
            System.out.println(answer.getMessage());
            if(!answer.isSuccess()) System.out.println("Please enter again");
        }while (!answer.isSuccess());
    }

    private void manualConfigOffsets(Scanner scanner){
        InputOperationAnswer answer;
        System.out.println("---------------------------------------------------------");
        System.out.println("Please enter for each rotor you enter the letter you want to position the rotor on");
        System.out.println("Please enter the letter together in one line(example: LKI)");
        System.out.println("The order of the letter is correspond to the order you order the ids");
        do {
            System.out.print(">>");
            answer = this.engine.manualConfigOffsets(scanner.nextLine());
            System.out.println(answer.getMessage());
            if(!answer.isSuccess()) System.out.println("Please enter again");
        }while (!answer.isSuccess());
    }

    private void manualConfigReflector(Scanner scanner){
        System.out.println("---------------------------------------------------------");
        System.out.println("Please choose one of the following Reflectors(enter the decimal value of the reflector number");
        SizeOfElementAnswer reflectorNumAns = this.engine.getNumOfReflectors();
        int numOfReflectors = 0;
        if(reflectorNumAns.isElementExists()) numOfReflectors = reflectorNumAns.getSize();
        int finalNumOfReflectors = numOfReflectors;

        AtomicInteger counter = new AtomicInteger(1);
        Arrays.stream(ReflectorGreekNums.values())
                .filter(i-> i.getVal() <= finalNumOfReflectors)
                .collect(Collectors
                        .toList())
                            .forEach(i -> {
                                    System.out.println(counter + ". " + i.getSymbol());
                                    counter.incrementAndGet();
        });

        int choice;
        InputOperationAnswer answer;
        do {
            System.out.print(">>");
            choice = getNextInt(scanner);
            answer = this.engine.manualConfigReflector(String.valueOf(choice));
            System.out.println(answer.getMessage());
            if(!answer.isSuccess()) System.out.println("Please enter again");
        }while (!answer.isSuccess());
    }

    private void manualConfigPlugBoard(Scanner scanner){
        System.out.println("---------------------------------------------------------");
        System.out.println("Please enter pair of letter you want to connect in the plug Board");
        System.out.println("note: All pairs should be write together");
        System.out.println("Example: input \"afghjk\" connects A<->F, G<->H, J<->k");
        InputOperationAnswer answer;
        scanner.nextLine();
        do {
            System.out.print(">>");
            answer = this.engine.manualConfigPlugBoard(scanner.nextLine());
            System.out.println(answer.getMessage());
            if(!answer.isSuccess()) System.out.println("Please enter again");
        }while (!answer.isSuccess());
        this.isConfigMachine = true;
    }

}
