package dev.haolin.model.lru;

import lombok.Data;

import java.util.Objects;

import static dev.haolin.util.SizeUtil.byteSizeOf;

@Data
public class LinkedEntry<K, V> {

    public static <K, V> LinkedEntry<K, V> of(K key, V value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        return new LinkedEntry<>(key, value);
    }

    public static <K, V> LinkedEntry<K, V> dummy() {
        return new LinkedEntry<>();
    }

    public static <K, V> long entrySizeOf(K key, V value) {
        return byteSizeOf(key) + byteSizeOf(value);
    }

    private K key;
    private V value;
    private LinkedEntry<K, V> prev;
    private LinkedEntry<K, V> next;

    private LinkedEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    private LinkedEntry() {}

}
