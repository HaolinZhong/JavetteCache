package dev.haolin.model;

import dev.haolin.exception.CacheException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static dev.haolin.model.LinkedEntry.entrySizeOf;
import static org.junit.jupiter.api.Assertions.*;

class CacheTest {

    private static final long LARGE_CACHE_SIZE = 1024L;

    private static final String TEST_SMALL_KEY = "key";

    private static final String TEST_SMALL_VALUE = "value";

    private static final String TEST_LARGE_KEY = "keykey";

    private static final String TEST_LARGE_VALUE = "valuevalue";


    @Test
    void testGet() {
        Cache cache = new Cache(LARGE_CACHE_SIZE);
        cache.put("key", "value");
        assertEquals("value", cache.get("key"));
        assertNull(cache.get("something else"));
    }

    @Test
    void testRemoveEldest() {
        long cacheSize = entrySizeOf(TEST_LARGE_KEY, TEST_LARGE_VALUE);
        Cache cache = new Cache(cacheSize);
        // the cache's size should be just enough to put in the large key & large value
        cache.put(TEST_LARGE_KEY, TEST_LARGE_VALUE);
        assertEquals(TEST_LARGE_VALUE, cache.get(TEST_LARGE_KEY));

        // put anymore k-v pair should lead to the removal of the original key & value
        cache.put(TEST_SMALL_KEY, TEST_SMALL_VALUE);
        assertEquals(TEST_SMALL_VALUE, cache.get(TEST_SMALL_KEY));
        assertNull(cache.get(TEST_LARGE_KEY));
    }

    @Test
    void testOnEviction() {
        long cacheSize = entrySizeOf(TEST_LARGE_KEY, TEST_LARGE_VALUE);
        List<LinkedEntry> removedList = new ArrayList<>();
        BiConsumer<String, Object> collect = (key, value) -> removedList.add(LinkedEntry.of(key, value));

        Cache cache = new Cache(cacheSize, collect);

        cache.put(TEST_LARGE_KEY, TEST_LARGE_VALUE);
        cache.put(TEST_SMALL_KEY, TEST_SMALL_VALUE);
        assertEquals(LinkedEntry.of(TEST_LARGE_KEY, TEST_LARGE_VALUE), removedList.get(0));
    }

    @Test
    void testPutThrowException() {
        // when the input k-v pair's size exceeds the max size of the cache, an exception should be thrown to abort this operation
        Cache cache = new Cache(0L);
        assertThrows(CacheException.class, () -> cache.put(TEST_SMALL_KEY, TEST_SMALL_VALUE));
    }
}
