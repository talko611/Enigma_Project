package Engine.Decipher;

import java.util.Set;

public class Decipher {
    Set<String> dictionary;

    public Decipher(Set<String> dictionary){
        this.dictionary = dictionary;
    }

    public Set<String> getDictionary() {
        return dictionary;
    }

    public void setDictionary(Set<String> dictionary) {
        this.dictionary = dictionary;
    }
}
