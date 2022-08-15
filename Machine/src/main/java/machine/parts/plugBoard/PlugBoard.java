package machine.parts.plugBoard;

import java.util.Map;

public interface PlugBoard {
    String switchLetters(String letter);

    String getMatchingLetter(String letter);

    Map<String,String> getAllPlugs();

    @Override
    String toString();
}
