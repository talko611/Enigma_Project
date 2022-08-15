package machine.parts.keyboard;

import java.util.Map;

public interface Keyboard {
    boolean isKeyExists(String key);

    Integer getEntryPointByKey(String key);

    String getEntryMatchKey(int entry);

    @Override
    String toString();

    Map<String, Integer> getABC();
}
