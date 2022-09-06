package machine.parts.keyboard;

import org.apache.commons.lang3.SerializationUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class KeyboardImp implements Keyboard {
    private final Map<String, Integer> alphabetToEntryNum;

    public Map<String, Integer> getAlphabetToEntryNum() {
        return alphabetToEntryNum;
    }

    public KeyboardImp(List<String> keyToEntryPoint){
        alphabetToEntryNum = IntStream
                .range(0, keyToEntryPoint.size())
                .boxed()
                .collect(Collectors.toMap(keyToEntryPoint::get, i->i));
    }

    public KeyboardImp (KeyboardImp copyFrom){
        this.alphabetToEntryNum = SerializationUtils.clone(new HashMap<>(copyFrom.alphabetToEntryNum));
    }

    @Override
    public boolean isKeyExists(String key) {
        return alphabetToEntryNum.containsKey(key);
    }

    @Override
    public Integer getEntryPointByKey(String key) {
        return alphabetToEntryNum.get(key);

    }

    @Override
    public String getEntryMatchKey(int entry) {
        for(Map.Entry<String, Integer> pair : alphabetToEntryNum.entrySet()){
            if(pair.getValue() == entry){
                return pair.getKey();
            }
        }
        return null;
    }

    @Override
    public String toString(){
        return this.alphabetToEntryNum.toString();
    }

    @Override
    public Map<String, Integer> getABC() {
        return this.alphabetToEntryNum;
    }


}
