package dev.haolin.model.concurrent;

import dev.haolin.model.lru.LRUCache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Supplier;


public class SingleThreadLRUCache extends LRUCache {

    private final Executor cacheManager;
    private final AtomicReference<Thread> cacheManagerThread = new AtomicReference<>();


    public SingleThreadLRUCache(long maxBytes) {
        super(maxBytes);
        this.cacheManager = Executors.newSingleThreadExecutor();
        CompletableFuture.runAsync(() -> cacheManagerThread.set(Thread.currentThread()), this.cacheManager).join();
    }

    public SingleThreadLRUCache(long maxBytes, BiConsumer<String, Object> onEviction) {
        super(maxBytes, onEviction);
        this.cacheManager = Executors.newSingleThreadExecutor();
        CompletableFuture.runAsync(() -> cacheManagerThread.set(Thread.currentThread()), this.cacheManager).join();
    }

    private <T> CompletableFuture<T> supplyOperation(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, cacheManager);
    }

    private CompletableFuture<Void> supplyOperation(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, cacheManager);
    }

    private boolean isRanByManagerThread() {
        return Thread.currentThread() == cacheManagerThread.get();
    }

    public CompletableFuture<Object> getAsync(Object key) {
        return supplyOperation(() -> super.get(key));
    }

    public CompletableFuture<Object> putAsync(String key, Object value) {
        return supplyOperation(() -> super.put(key, value));
    }

    public CompletableFuture<Object> removeAsync(Object key) {
        return supplyOperation(() -> super.remove(key));
    }


    @Override
    public Object get(Object key) {
        try {
            return isRanByManagerThread() ? super.get(key) : getAsync(key).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object put(String key, Object value) {
        try {
            return isRanByManagerThread() ? super.put(key, value) : putAsync(key, value).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object remove(Object key) {
        try {
            return isRanByManagerThread() ? super.remove(key) : removeAsync(key).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        if (isRanByManagerThread()) {
            super.clear();
        } else {
            supplyOperation(super::clear);
        }
    }

    @Override
    public Set<String> keySet() {
        try {
            return isRanByManagerThread() ? super.keySet() : supplyOperation(super::keySet).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<Object> values() {
        try {
            return isRanByManagerThread() ? super.values() : supplyOperation(super::values).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        try {
            return isRanByManagerThread() ? super.size() : supplyOperation(super::size).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isEmpty() {
        try {
            return isRanByManagerThread() ? super.isEmpty() : supplyOperation(super::isEmpty).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean containsKey(Object key) {
        try {
            return isRanByManagerThread() ? super.containsKey(key) : supplyOperation(() -> super.containsKey(key)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }
}
