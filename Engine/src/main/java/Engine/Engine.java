package Engine;

import Engine.engineAnswers.*;
import Engine.enigmaParts.EnigmaParts;
import Engine.enums.DmTaskDifficulty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.util.Pair;
import machine.Machine;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface Engine {
    InputOperationAnswer loadFromFile(String filePath);

    /*
    Assume that the rotors' ids order given in the string is from right to left
    meaning first ids will be the most right rotor
    */
    InputOperationAnswer manualConfigRotors(String rotorsList);

    /*
    Assume that the offsets order in the string are without white spaces(unless is part of the characters) and that
     they are order correspondingly to the rotors(first offset is for the most right rotor)
    */
    InputOperationAnswer manualConfigOffsets(String configurationLine);

    InputOperationAnswer manualConfigReflector(String configurationLine);

    /*
    Set that a valid config line is pairs of letters seperated by comma.
     */
    InputOperationAnswer manualConfigPlugBoard(String configurationLine);

    InputOperationAnswer autoConfig();

    void resetMachine();

    MachineDetailsAnswer getMachineDetails();

    EncryptDecryptMessage encryptDecryptMessage(String message,boolean saveStats, boolean decipherState);

    void saveEncryptionData(EncryptDecryptMessage data) throws CloneNotSupportedException;

    StatisticsAnswer getStatistics() throws CloneNotSupportedException;

    SizeOfElementAnswer getNumOfReflectors();
    EnigmaParts getEnigmaParts();

    DmInitAnswer initializeDm(DmTaskDifficulty difficulty, String encrypted, int allowedAgents, int taskSize);
    void startBruteForce(BiConsumer<String, Pair<String, String>> reportUpdate, Consumer<Integer> progressUpdate, SimpleBooleanProperty isPaused);

    //Test func
    Machine getMachine();

    DmAnswer getDmDetails();

    void removePlugsFromMachine();

    void abortBruteForce();
    void pauseBruteForce();
    void resumeBruteForce();

}
