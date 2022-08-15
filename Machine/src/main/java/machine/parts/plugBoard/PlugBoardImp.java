package machine.parts.plugBoard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlugBoardImp implements PlugBoard {
    private Map<String, String> lettersPairs;

    public PlugBoardImp(String configLine) throws InstantiationError{
        lettersPairs = new HashMap<>();
        List<String> letters = Arrays.stream(configLine.split("[|,]"))
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        if(letters.size() % 2 != 0){
            throw new InstantiationError("Error - number of pairs for plug bord is odd");
        }
        for(int i = 0; i < letters.size() -1; i += 2){
            if(lettersPairs.containsKey(letters.get(i)) || lettersPairs.containsKey(letters.get(i + 1))){
                throw new InstantiationError("Error - one letter is paired with two different letters");
            }
            else {
                lettersPairs.put(letters.get(i), letters.get(i + 1));
                lettersPairs.put(letters.get(i + 1), letters.get(i));
            }
        }
    }

    public PlugBoardImp(Map<String,String> lettersPairs){
        this.lettersPairs = lettersPairs;
    }

    @Override
    public String switchLetters(String letter) {
        if(lettersPairs.containsKey(letter)){
            return lettersPairs.get(letter);
        }
        return letter;
    }

    @Override
    public String getMatchingLetter(String letter) {
        return lettersPairs.get(letter);
    }

    @Override
    public Map<String, String> getAllPlugs() {
        return this.lettersPairs;
    }

    public Map<String, String> getLettersPairs() {
        return lettersPairs;
    }

    @Override
    public String toString(){
        return lettersPairs.toString();
    }
}
