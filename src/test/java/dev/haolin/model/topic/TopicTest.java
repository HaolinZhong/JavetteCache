package dev.haolin.model.topic;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class TopicTest {

    private final Map<String, String> db = Map.of(
            "Tom", "630",
            "Jack", "589",
            "Sam", "567"
    );

    private final Map<String, Integer> loadCounter = new HashMap<>();

    private final Function<String, Object> dbLoader = (k) -> {
        if (!db.containsKey(k)) return null;
        loadCounter.compute(k, (key, value) -> value == null ? 1 : value + 1);
        return db.get(k);
    };

    @Test
    void testGet() {
        Topic topic = new Topic("scores", dbLoader, 8192L);

        for (Map.Entry<String, String> entry : db.entrySet()) {
            String key = entry.getKey(), value = entry.getValue();
            assertEquals(value, topic.get(key));
            assertEquals(1, loadCounter.get(key));
        }

        assertNull(topic.get("unknown"));
    }
}
