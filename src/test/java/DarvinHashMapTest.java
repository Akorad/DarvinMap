import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.UlGTU.DarvinHashMap;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class DarvinHashMapTest {

    private DarvinHashMap<Integer, String> map;

    @BeforeEach
    public void setUp() {
        map = new DarvinHashMap<>();
    }

    @Test
    public void testPutAndGet() {
        map.put(1, "one");
        map.put(2, "two");

        assertEquals("one", map.get(1));
        assertEquals("two", map.get(2));
        assertNull(map.get(3));
    }

    @Test
    public void testSize() {
        assertEquals(0, map.size());
        map.put(1, "one");
        assertEquals(1, map.size());
        map.put(2, "two");
        assertEquals(2, map.size());
    }

    @Test
    public void testIsEmpty() {
        assertTrue(map.isEmpty());
        map.put(1, "one");
        assertFalse(map.isEmpty());
    }

    @Test
    public void testContainsKey() {
        map.put(1, "one");
        assertTrue(map.containsKey(1));
        assertFalse(map.containsKey(2));
    }

    @Test
    public void testContainsValue() {
        map.put(1, "one");
        assertTrue(map.containsValue("one"));
        assertFalse(map.containsValue("two"));
    }

    @Test
    public void testRemove() {
        map.put(1, "one");
        map.put(2, "two");
        assertEquals("one", map.remove(1));
        assertNull(map.remove(3));
        assertEquals(1, map.size());
    }

    @Test
    public void testClear() {
        map.put(1, "one");
        map.put(2, "two");
        map.clear();
        assertEquals(0, map.size());
        assertFalse(map.containsKey(1));
    }

    @Test
    public void testKeySet() {
        map.put(1, "one");
        map.put(2, "two");

        Set<Integer> keys = map.keySet();
        assertTrue(keys.contains(1));
        assertTrue(keys.contains(2));
    }

    @Test
    public void testValues() {
        map.put(1, "one");
        map.put(2, "two");

        Collection<String> values = map.values();
        assertTrue(values.contains("one"));
        assertTrue(values.contains("two"));
    }

    @Test
    public void testEntrySet() {
        map.put(1, "one");
        map.put(2, "two");

        Set<Map.Entry<Integer, String>> entries = map.entrySet();
        assertEquals(2, entries.size());

    }



    @Test
    public void testGetOrDefault() {
        map.put(1, "one");
        assertEquals("one", map.getOrDefault(1, "default"));
        assertEquals("default", map.getOrDefault(2, "default"));
    }

    @Test
    public void testForEach() {
        map.put(1, "one");
        map.put(2, "two");

        StringBuilder result = new StringBuilder();
        map.forEach((key, value) -> result.append(key).append(value));
        assertEquals("1one2two", result.toString());
    }

    @Test
    public void testReplaceAll() {
        map.put(1, "one");
        map.put(2, "two");

        map.replaceAll((key, value) -> value.toUpperCase());

        assertEquals("ONE", map.get(1));
        assertEquals("TWO", map.get(2));
    }

    @Test
    public void testPutIfAbsent() {
        map.put(1, "one");
        map.putIfAbsent(1, "ONE");
        map.putIfAbsent(2, "two");

        assertEquals("one", map.get(1));
        assertEquals("two", map.get(2));
    }

    @Test
    public void testRemoveConditionally() {
        map.put(1, "one");
        map.put(2, "two");

        assertTrue(map.remove(1, "one"));
        assertFalse(map.remove(2, "three"));
    }

    @Test
    public void testReplaceConditionally() {
        map.put(1, "one");
        map.put(2, "two");

        assertTrue(map.replace(1, "one", "ONE"));
        assertFalse(map.replace(2, "three", "TWO"));
    }

    @Test
    public void testComputeIfAbsent() {
        map.put(1, "one");

        map.computeIfAbsent(2, k -> "two");

        assertEquals("one", map.get(1));
        assertEquals("two", map.get(2));
    }

    @Test
    public void testComputeIfPresent() {
        map.put(1, "one");

        map.computeIfPresent(1, (k, v) -> "ONE");

        assertEquals("ONE", map.get(1));
    }

    @Test
    public void testCompute() {
        map.put(1, "one");

        map.compute(1, (k, v) -> v == null ? "ONE" : "two");

        assertEquals("two", map.get(1));
    }

    @Test
    public void testMerge() {
        map.put(1, "one");

        map.merge(1, "ONE", (oldValue, newValue) -> oldValue + newValue);

        assertEquals("oneONE", map.get(1));
    }

    @Test
    public void testEqualsAndHashCode() {
        DarvinHashMap<Integer, String> map2 = new DarvinHashMap<>();
        map.put(1, "one");
        map.put(2, "two");

        map2.put(1, "one");
        map2.put(2, "two");

        assertTrue(map.equals(map2));
        assertEquals(map.hashCode(), map2.hashCode());
    }
}
