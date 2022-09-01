package Engine;

import Engine.engineAnswers.*;
import Engine.enigmaParts.EnigmaParts;

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

    EncryptDecryptMessage encryptDecrypt(String message) throws CloneNotSupportedException;

    StatisticsAnswer getStatistics() throws CloneNotSupportedException;

    SizeOfElementAnswer getNumOfReflectors();
    EnigmaParts getEnigmaParts();
}
