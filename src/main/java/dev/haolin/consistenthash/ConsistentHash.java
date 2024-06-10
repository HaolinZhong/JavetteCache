package dev.haolin.consistenthash;

import dev.haolin.exception.CacheException;
import dev.haolin.exception.CacheExceptionMsgEnum;

import java.util.*;
import java.util.function.Function;

public class ConsistentHash {

    private final TreeSet<Integer> circle = new TreeSet<>();
    private final Function<String, Integer> hashFn;
    private final Map<Integer, String> vNodeToNode = new HashMap<>();

    private final int replicas; // replica number for virtual nodes

    public ConsistentHash(Function<String, Integer> hashFn, int replicas) {
        this.hashFn = hashFn;
        this.replicas = replicas;
    }

    public ConsistentHash(int replicas) {
        this.replicas = replicas;
        this.hashFn = String::hashCode;
    }

    public void addNodes(String... names) {
        for (String name : names) {
            for (int i = 0; i < replicas; i++) {
                int nameHash = hashFn.apply(i + name);
                circle.add(nameHash);
                vNodeToNode.put(nameHash, name);
            }
        }
    }

    public String findNode(String key) {
        if (circle.isEmpty()) {
            throw new CacheException(CacheExceptionMsgEnum.NO_AVAILABLE_NODE);
        }

        int hash = hashFn.apply(key);
        Integer nearestVNodeHash = circle.ceiling(hash);
        if (nearestVNodeHash == null) {
            nearestVNodeHash = circle.first();
        }
        return vNodeToNode.get(nearestVNodeHash);
    }
}
