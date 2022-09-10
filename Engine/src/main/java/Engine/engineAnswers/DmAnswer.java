package Engine.engineAnswers;

import java.util.Set;

public class DmAnswer {
    private Set<String> dictionary;
    private int maxAgents;

    public Set<String> getDictionary() {
        return dictionary;
    }

    public int getMaxAgents() {
        return maxAgents;
    }

    public void setDictionary(Set<String> dictionary) {
        this.dictionary = dictionary;
    }

    public void setMaxAgents(int maxAgents) {
        this.maxAgents = maxAgents;
    }
}
