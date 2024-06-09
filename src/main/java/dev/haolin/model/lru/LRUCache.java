package dev.haolin.model.lru;

import dev.haolin.exception.CacheException;

import java.util.Optional;
import java.util.function.BiConsumer;

import static dev.haolin.exception.CacheExceptionMsgEnum.OVERFLOW_EXCEPTION;
import static dev.haolin.model.lru.LinkedEntry.entrySizeOf;
import static dev.haolin.util.SizeUtil.byteSizeOf;

public class LRUCache extends LinkedHashMap<String, Object> {

    private final long maxBytes;

    private long nBytes;

    private LinkedHashMap<String, Object> map;

    private Optional<BiConsumer<String, Object>> optionalOnEviction;

    public LRUCache(long maxBytes) {
        this.maxBytes = maxBytes;
        this.map = new LinkedHashMap<>();
        this.optionalOnEviction = Optional.empty();
    }

    public LRUCache(long maxBytes, BiConsumer<String, Object> optionalOnEviction) {
        this.maxBytes = maxBytes;
        this.optionalOnEviction = Optional.ofNullable(optionalOnEviction);
        this.map = new LinkedHashMap<>();
    }

    @Override
    protected final boolean removeEldestEntry(LinkedEntry<String, Object> linkedEntry) {
        return nBytes > maxBytes;
    }

    /***
     * Remove is the only method that delete an entry from LinkedHashMap.
     * Updating nBytes in this overrode method ensures nBytes remains consistent before/after remove operation.
     * @param key key whose mapping is to be removed from the map
     * @return
     */
    @Override
    public Object remove(Object key) {
        Object value = super.remove(key);
        if (value != null) {
            nBytes -= entrySizeOf(key, value);
        }
        optionalOnEviction.ifPresent(onEviction -> onEviction.accept((String) key, value));
        return value;
    }

    @Override
    public Object put(String key, Object value) {
        if (entrySizeOf(key, value) > maxBytes) {
            throw new CacheException(OVERFLOW_EXCEPTION);
        }

        // now the new value will certainly be put into the map

        // update
        if (map.containsKey(key)) {
            Object prevValue = map.get(key);
            nBytes += byteSizeOf(value) - byteSizeOf(prevValue);
        } else {
            // add
            nBytes += entrySizeOf(key, value);
        }

        return super.put(key, value);
    }

}
