package Engine.engineAnswers;

import java.util.List;
import java.util.Map;

public class StatisticsAnswer {
    private Map<String, List<EncryptDecryptMessage>> stats;

    public StatisticsAnswer(Map<String, List<EncryptDecryptMessage>> stats) {
        this.stats = stats;
    }

    public Map<String, List<EncryptDecryptMessage>> getStats() {
        return stats;
    }
}

