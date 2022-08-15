package machine;

import machine.parts.keyboard.Keyboard;
import machine.parts.plugBoard.PlugBoard;
import machine.parts.reflector.Reflector;
import machine.parts.rotor.Rotor;

import java.util.List;

public interface Machine {

    String encryptDecrypt(String input);

    void setRotors(List<Rotor> rotors);

    void setReflector(Reflector reflector);

    void setPlugBord(PlugBoard plugBord);

    void setKeyboard(Keyboard keyboard);

    List<Rotor> getRotors();

    Keyboard getKeyboard();

    PlugBoard getPlugBord();

    Reflector getReflector();

    void reset();
}
