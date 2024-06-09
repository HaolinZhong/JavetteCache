package dev.haolin.model.topic;

import dev.haolin.model.concurrent.SingleThreadLRUCache;

import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class Topic extends SingleThreadLRUCache {

    private final String name;
    private final Function<String, Object> loader;

    public Topic(String name, Function<String, Object> loader, long maxByte) {
        super(maxByte);
        this.name = name;
        this.loader = loader;
    }

    public Topic(String name, Function<String, Object> loader, long maxByte, BiConsumer<String, Object> onEviction) {
        super(maxByte, onEviction);
        this.name = name;
        this.loader = loader;
    }

    @Override
    public Object get(Object key) {
        try {
            return super.getAsync(key).thenApply(value -> value == null ? getLocally((String) key) : value).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getLocally(String key) {
        Object value = loader.apply(key);
        if (value != null) {
            return super.put(key, value);
        }
        return null;
    }
}
