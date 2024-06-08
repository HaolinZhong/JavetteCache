package dev.haolin.model;

import java.util.*;
import java.util.stream.Collectors;

public class ConcurrentLinkedHashMap<K, V> implements Map<K, V> {

    private transient LinkedEntry<K, V> dummyHead, dummyTail;
    private Map<K, LinkedEntry<K, V>> keyToEntryMap;

    public ConcurrentLinkedHashMap() {
        this.dummyHead = LinkedEntry.dummy();
        this.dummyTail = LinkedEntry.dummy();
        this.keyToEntryMap = new HashMap<>();
        linkEntry(dummyHead, dummyTail);
    }

    @Override
    public V get(Object key) {
        if (!keyToEntryMap.containsKey(key)) return null;
        LinkedEntry<K, V> target = keyToEntryMap.get(key);
        moveToFront(target);
        return target.getValue();
    }

    @Override
    public V put(K key, V value) {
        // update
        if (keyToEntryMap.containsKey(key)) {
            LinkedEntry<K, V> target = keyToEntryMap.get(key);
            target.setValue(value);
            moveToFront(target);
            return value;
        }

        // add
        LinkedEntry<K, V> linkedEntry = LinkedEntry.of(key, value);
        keyToEntryMap.put(key, linkedEntry);
        insertToFront(linkedEntry);

        // remove until certain standards being satisfied
        while (removeEldestEntry(dummyTail.getPrev())) {
            if (dummyTail.getPrev().equals(dummyHead)) {
                break;
            }
            removeLruEntry();
        }
        return value;
    }

    @Override
    public V remove(Object key) {
        if (!keyToEntryMap.containsKey(key)) return null;
        LinkedEntry<K, V> target = keyToEntryMap.remove(key);
        linkEntry(target.getPrev(), target.getNext());
        return target.getValue();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        linkEntry(dummyHead, dummyTail);
        keyToEntryMap.clear();
    }

    @Override
    public Set<K> keySet() {
        return keyToEntryMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return keyToEntryMap.values().stream().map(LinkedEntry::getValue).toList();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return keyToEntryMap.size();
    }

    @Override
    public boolean isEmpty() {
        return keyToEntryMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return keyToEntryMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return keyToEntryMap.toString();
    }

    private void linkEntry(LinkedEntry<K, V> prev, LinkedEntry<K, V> next) {
        prev.setNext(next);
        next.setPrev(prev);
    }

    private void insertToFront(LinkedEntry<K, V> linkedEntry) {
        linkEntry(linkedEntry, dummyHead.getNext());
        linkEntry(dummyHead, linkedEntry);
    }

    private void moveToFront(LinkedEntry<K, V> linkedEntry) {
        LinkedEntry<K, V> prev = linkedEntry.getPrev(), next = linkedEntry.getNext();
        linkEntry(prev, next);
        insertToFront(linkedEntry);
    }

    private void removeLruEntry() {
        if (!dummyTail.getPrev().equals(dummyHead)) {
            remove(dummyTail.getPrev().getKey());
        }
    }

    protected boolean removeEldestEntry(LinkedEntry<K, V> linkedEntry) {
        return false;
    }

}


