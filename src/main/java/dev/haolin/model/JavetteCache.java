package dev.haolin.model;

import dev.haolin.exception.CacheException;
import dev.haolin.model.topic.Topic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static dev.haolin.exception.CacheExceptionMsgEnum.TOPIC_ALREADY_EXISTS;

public class JavetteCache {

    private JavetteCache() {}

    private static final Map<String, Topic> topics = new ConcurrentHashMap<>();

    public static Topic registerTopic(String name, Function<String, Object> loader, long maxByte) {
        return topics.compute(name, (key, value) -> {
            if (value != null) throw new CacheException(TOPIC_ALREADY_EXISTS);
            return new Topic(key, loader, maxByte);
        });
    }

    public static Topic registerTopic(String name, Function<String, Object> loader, long maxByte, BiConsumer<String, Object> onEviction) {
        return topics.compute(name, (key, value) -> {
            if (value != null) throw new CacheException(TOPIC_ALREADY_EXISTS);
            return new Topic(key, loader, maxByte, onEviction);
        });
    }

    public static Topic getTopic(String name) {
        return topics.get(name);
    }
}
