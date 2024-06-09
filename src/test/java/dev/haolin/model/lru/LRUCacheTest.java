package dev.haolin.model.lru;

import dev.haolin.exception.CacheException;
import dev.haolin.model.lru.LRUCache;
import dev.haolin.model.lru.LinkedEntry;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static dev.haolin.model.lru.LinkedEntry.entrySizeOf;
import static org.junit.jupiter.api.Assertions.*;

class LRUCacheTest {

    private static final long LARGE_CACHE_SIZE = 1024L;

    private static final String TEST_SMALL_KEY = "key";

    private static final String TEST_SMALL_VALUE = "value";

    private static final String TEST_LARGE_KEY = "keykey";

    private static final String TEST_LARGE_VALUE = "valuevalue";


    @Test
    void testGet() {
        LRUCache LRUCache = new LRUCache(LARGE_CACHE_SIZE);
        LRUCache.put("key", "value");
        assertEquals("value", LRUCache.get("key"));
        assertNull(LRUCache.get("something else"));
    }

    @Test
    void testRemoveEldest() {
        long cacheSize = entrySizeOf(TEST_LARGE_KEY, TEST_LARGE_VALUE);
        LRUCache LRUCache = new LRUCache(cacheSize);
        // the LRUCache's size should be just enough to put in the large key & large value
        LRUCache.put(TEST_LARGE_KEY, TEST_LARGE_VALUE);
        assertEquals(TEST_LARGE_VALUE, LRUCache.get(TEST_LARGE_KEY));

        // put anymore k-v pair should lead to the removal of the original key & value
        LRUCache.put(TEST_SMALL_KEY, TEST_SMALL_VALUE);
        assertEquals(TEST_SMALL_VALUE, LRUCache.get(TEST_SMALL_KEY));
        assertNull(LRUCache.get(TEST_LARGE_KEY));
    }

    @Test
    void testOnEviction() {
        long cacheSize = entrySizeOf(TEST_LARGE_KEY, TEST_LARGE_VALUE);
        List<LinkedEntry> removedList = new ArrayList<>();
        BiConsumer<String, Object> collect = (key, value) -> removedList.add(LinkedEntry.of(key, value));

        LRUCache LRUCache = new LRUCache(cacheSize, collect);

        LRUCache.put(TEST_LARGE_KEY, TEST_LARGE_VALUE);
        LRUCache.put(TEST_SMALL_KEY, TEST_SMALL_VALUE);
        assertEquals(LinkedEntry.of(TEST_LARGE_KEY, TEST_LARGE_VALUE), removedList.get(0));
    }

    @Test
    void testPutThrowException() {
        // when the input k-v pair's size exceeds the max size of the LRUCache, an exception should be thrown to abort this operation
        LRUCache LRUCache = new LRUCache(0L);
        assertThrows(CacheException.class, () -> LRUCache.put(TEST_SMALL_KEY, TEST_SMALL_VALUE));
    }
}
