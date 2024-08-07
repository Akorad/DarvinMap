package org.UlGTU;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) {

        DarvinHashMap<Integer, String> map = new DarvinHashMap<>();
        DarvinHashMap<Integer, String> map2 = new DarvinHashMap<>();
        DarvinHashMap<Integer, String> map3 = new DarvinHashMap<>();
        DarvinHashMap<Integer, String> map4 = new DarvinHashMap<>();
        // Получение текущего времени
        someMethod();
        map.put(1,"Apple");
        map.put(2,"Banana");
        map.put(3,"Orange");
        for (int i = 4; i < 1000000; i++) {
            map.put(i,"Apple"+i);
        }
        someMethod();
        System.out.println("Значение ключа 5040: " + map.get(5040));
        someMethod();
        System.out.println("Значение ключа 50400 через getOrDefault: " + map.getOrDefault(50400, "Значение не найдено"));
        someMethod();
        System.out.println("Содержит значение «Banana»: " + map.containsValue("Banana"));
        someMethod();
        System.out.println("Есть ли ключ 5040: " + map.containsKey(5040));
        someMethod();
        System.out.println("Добавление значения с помощью  putIfAbsent" + map.putIfAbsent(50400, "Apple50400"));
        someMethod();
        System.out.println("Значение ключа 50400 через getOrDefault: " + map.getOrDefault(50400, "Значение не найдено"));
        someMethod();
//        Collection<String> values = map.values();
//        for (String value : values) {
//            System.out.println("Значение через Collection "+value);
//        }
        map.remove(3);
        System.out.println("Размер map после удаления «Orange»: " + map.size());
        someMethod();
//        System.out.println("Вывод всех значений:");
//        for (YourOwnAvlMap.Entry<Integer,String> entry : map.entrySet()) {
//            System.out.println("Ключ: " + entry.getKey() + ", Значение: " + entry.getValue());
//        }
        map.clear();
        someMethod();
        System.out.println("Размер map после выполнения метода clear " + map.size());
        someMethod();
        System.out.println("Проверка map на пустоту " + map.isEmpty());
        someMethod();
        map2.put(1,"one");
        map2.put(2,"two");
        map2.put(3,"three");
        map3.put(1,"one");
        map3.put(2,"two");
        map3.put(3,"three");
        System.out.println("Map одинаковы " + map2.equals(map3));
        someMethod();
        map4.put(4,"four");
        map4.put(5,"five");
        map3.putAll(map4);
        System.out.println("Проверка новых ключей в map3 " + map3.get(4));
        someMethod();

    }
    private static void someMethod() {
        // Время в этом методе
        System.out.println("Текущее время: " + TimeUtil.getCurrentTime());

    }
    public class TimeUtil {

        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSSS")
                .withZone(ZoneId.systemDefault());

        public static String getCurrentTime() {
            Instant now = Instant.now();
            return FORMATTER.format(now);
        }
    }
}
