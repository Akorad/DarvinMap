package org.UlGTU;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.Lock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DarvinHashMap<K, V> implements Map<K, V> {
    private static final int INITIAL_CAPACITY = 10240;
    private final LinkedList<Entry<K, V>>[] buckets;
    private final ReentrantReadWriteLock[] locks;
    private int size = 0;
    private final ReentrantReadWriteLock globalLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock globalReadLock = globalLock.readLock();
    private final ReentrantReadWriteLock.WriteLock globalWriteLock = globalLock.writeLock();

    @SuppressWarnings("unchecked")
    public DarvinHashMap() {
        buckets = new LinkedList[INITIAL_CAPACITY];
        locks = new ReentrantReadWriteLock[INITIAL_CAPACITY];
        for (int i = 0; i < INITIAL_CAPACITY; i++) {
            buckets[i] = new LinkedList<>();
            locks[i] = new ReentrantReadWriteLock();
        }
    }

    private int hash(Object key) {
        return Math.abs(key.hashCode() % INITIAL_CAPACITY);
    }

    @Override
    public int size() {
        globalReadLock.lock();
        try {
            return size;
        } finally {
            globalReadLock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        globalReadLock.lock();
        try {
            return size == 0;
        } finally {
            globalReadLock.unlock();
        }
    }

    @Override
    public boolean containsKey(Object key) {
        int bucketIndex = hash(key);
        Lock readLock = locks[bucketIndex].readLock();
        readLock.lock();
        try {
            LinkedList<Entry<K, V>> bucket = buckets[bucketIndex];
            for (Entry<K, V> entry : bucket) {
                if (entry.getKey().equals(key)) {
                    return true;
                }
            }
            return false;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean containsValue(Object value) {
        globalReadLock.lock();
        try {
            for (int i = 0; i < INITIAL_CAPACITY; i++) {
                Lock readLock = locks[i].readLock();
                readLock.lock();
                try {
                    for (Entry<K, V> entry : buckets[i]) {
                        if (entry.getValue().equals(value)) {
                            return true;
                        }
                    }
                } finally {
                    readLock.unlock();
                }
            }
            return false;
        } finally {
            globalReadLock.unlock();
        }
    }

    @Override
    public V get(Object key) {
        int bucketIndex = hash(key);
        Lock readLock = locks[bucketIndex].readLock();
        readLock.lock();
        try {
            LinkedList<Entry<K, V>> bucket = buckets[bucketIndex];
            for (Entry<K, V> entry : bucket) {
                if (entry.getKey().equals(key)) {
                    return entry.getValue();
                }
            }
            return null;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public V put(K key, V value) {
        int bucketIndex = hash(key);
        Lock writeLock = locks[bucketIndex].writeLock();
        writeLock.lock();
        try {
            LinkedList<Entry<K, V>> bucket = buckets[bucketIndex];
            for (Entry<K, V> entry : bucket) {
                if (entry.getKey().equals(key)) {
                    V oldValue = entry.getValue();
                    entry.setValue(value);
                    return oldValue;
                }
            }
            bucket.add(new Entry<>(key, value));
            globalWriteLock.lock();
            try {
                size++;
            } finally {
                globalWriteLock.unlock();
            }
            return null;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public V remove(Object key) {
        int bucketIndex = hash(key);
        Lock writeLock = locks[bucketIndex].writeLock();
        writeLock.lock();
        try {
            LinkedList<Entry<K, V>> bucket = buckets[bucketIndex];
            Iterator<Entry<K, V>> iterator = bucket.iterator();
            while (iterator.hasNext()) {
                Entry<K, V> entry = iterator.next();
                if (entry.getKey().equals(key)) {
                    iterator.remove();
                    globalWriteLock.lock();
                    try {
                        size--;
                    } finally {
                        globalWriteLock.unlock();
                    }
                    return entry.getValue();
                }
            }
            return null;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        globalWriteLock.lock();
        try {
            for (int i = 0; i < INITIAL_CAPACITY; i++) {
                Lock writeLock = locks[i].writeLock();
                writeLock.lock();
                try {
                    buckets[i].clear();
                } finally {
                    writeLock.unlock();
                }
            }
            size = 0;
        } finally {
            globalWriteLock.unlock();
        }
    }

    @Override
    public Set<K> keySet() {
        globalReadLock.lock();
        try {
            Set<K> keySet = new HashSet<>();
            for (int i = 0; i < INITIAL_CAPACITY; i++) {
                Lock readLock = locks[i].readLock();
                readLock.lock();
                try {
                    for (Entry<K, V> entry : buckets[i]) {
                        keySet.add(entry.getKey());
                    }
                } finally {
                    readLock.unlock();
                }
            }
            return keySet;
        } finally {
            globalReadLock.unlock();
        }
    }

    @Override
    public Collection<V> values() {
        globalReadLock.lock();
        try {
            List<V> values = new ArrayList<>();
            for (int i = 0; i < INITIAL_CAPACITY; i++) {
                Lock readLock = locks[i].readLock();
                readLock.lock();
                try {
                    for (Entry<K, V> entry : buckets[i]) {
                        values.add(entry.getValue());
                    }
                } finally {
                    readLock.unlock();
                }
            }
            return values;
        } finally {
            globalReadLock.unlock();
        }
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        globalReadLock.lock();
        try {
            Set<Map.Entry<K, V>> entrySet = new HashSet<>();
            for (int i = 0; i < INITIAL_CAPACITY; i++) {
                Lock readLock = locks[i].readLock();
                readLock.lock();
                try {
                    for (Entry<K, V> entry : buckets[i]) {
                        entrySet.add(entry);
                    }
                } finally {
                    readLock.unlock();
                }
            }
            return entrySet;
        } finally {
            globalReadLock.unlock();
        }
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        V value = get(key);
        return value != null ? value : defaultValue;
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        globalReadLock.lock();
        try {
            for (int i = 0; i < INITIAL_CAPACITY; i++) {
                Lock readLock = locks[i].readLock();
                readLock.lock();
                try {
                    for (Entry<K, V> entry : buckets[i]) {
                        action.accept(entry.getKey(), entry.getValue());
                    }
                } finally {
                    readLock.unlock();
                }
            }
        } finally {
            globalReadLock.unlock();
        }
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        globalWriteLock.lock();
        try {
            for (int i = 0; i < INITIAL_CAPACITY; i++) {
                Lock writeLock = locks[i].writeLock();
                writeLock.lock();
                try {
                    for (Entry<K, V> entry : buckets[i]) {
                        entry.setValue(function.apply(entry.getKey(), entry.getValue()));
                    }
                } finally {
                    writeLock.unlock();
                }
            }
        } finally {
            globalWriteLock.unlock();
        }
    }

    @Override
    public V putIfAbsent(K key, V value) {
        int bucketIndex = hash(key);
        Lock writeLock = locks[bucketIndex].writeLock();
        writeLock.lock();
        try {
            V existingValue = get(key);
            if (existingValue == null) {
                put(key, value);
                return null;
            }
            return existingValue;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean remove(Object key, Object value) {
        int bucketIndex = hash(key);
        Lock writeLock = locks[bucketIndex].writeLock();
        writeLock.lock();
        try {
            LinkedList<Entry<K, V>> bucket = buckets[bucketIndex];
            Iterator<Entry<K, V>> iterator = bucket.iterator();
            while (iterator.hasNext()){
                Entry<K, V> entry = iterator.next();
                if (entry.getKey().equals(key) && entry.getValue().equals(value)) {
                    iterator.remove();
                    globalWriteLock.lock();
                    try {
                        size--;
                    } finally {
                        globalWriteLock.unlock();
                    }
                    return true;
                }
            }
            return false;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        int bucketIndex = hash(key);
        Lock writeLock = locks[bucketIndex].writeLock();
        writeLock.lock();
        try {
            LinkedList<Entry<K, V>> bucket = buckets[bucketIndex];
            for (Entry<K, V> entry : bucket) {
                if (entry.getKey().equals(key) && entry.getValue().equals(oldValue)) {
                    entry.setValue(newValue);
                    return true;
                }
            }
            return false;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public V replace(K key, V value) {
        int bucketIndex = hash(key);
        Lock writeLock = locks[bucketIndex].writeLock();
        writeLock.lock();
        try {
            LinkedList<Entry<K, V>> bucket = buckets[bucketIndex];
            for (Entry<K, V> entry : bucket) {
                if (entry.getKey().equals(key)) {
                    V oldValue = entry.getValue();
                    entry.setValue(value);
                    return oldValue;
                }
            }
            return null;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        int bucketIndex = hash(key);
        Lock writeLock = locks[bucketIndex].writeLock();
        writeLock.lock();
        try {
            V value = get(key);
            if (value == null) {
                value = mappingFunction.apply(key);
                put(key, value);
            }
            return value;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        int bucketIndex = hash(key);
        Lock writeLock = locks[bucketIndex].writeLock();
        writeLock.lock();
        try {
            V value = get(key);
            if (value != null) {
                V newValue = remappingFunction.apply(key, value);
                if (newValue != null) {
                    put(key, newValue);
                    return newValue;
                } else {
                    remove(key);
                    return null;
                }
            }
            return null;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        int bucketIndex = hash(key);
        Lock writeLock = locks[bucketIndex].writeLock();
        writeLock.lock();
        try {
            V oldValue = get(key);
            V newValue = remappingFunction.apply(key, oldValue);
            if (newValue == null) {
                remove(key);
                return null;
            } else {
                put(key, newValue);
                return newValue;
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        int bucketIndex = hash(key);
        Lock writeLock = locks[bucketIndex].writeLock();
        writeLock.lock();
        try {
            V oldValue = get(key);
            V newValue = (oldValue == null) ? value : remappingFunction.apply(oldValue, value);
            if (newValue == null) {
                remove(key);
                return null;
            } else {
                put(key, newValue);
                return newValue;
            }
        } finally {
            writeLock.unlock();
        }
    }

    private static class Entry<K, V> implements Map.Entry<K, V> {
        private final K key;
        private V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Entry<?, ?> entry = (Entry<?, ?>) o;
            return Objects.equals(key, entry.key) && Objects.equals(value, entry.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }
    }
}
