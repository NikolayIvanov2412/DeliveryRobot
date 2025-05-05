package ru.netology;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RobotDeliveryApp {

    // Статическая карта для подсчета частот
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        // Количество маршрутов
        int numRoutes = 1000;

        // Запускаем потоки для обработки маршрутов
        for (int i = 0; i < numRoutes; i++) {
            executor.submit(() -> processRoute(generateRoute("RLRFR", 100)));
        }

        // Ждём завершение всех потоков
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException ignored) {}

        // Анализируем и выводим результат
        analyzeAndPrintResult();
    }

    // Генерация маршрута
    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    // Обработка маршрута
    public static void processRoute(String route) {
        int countR = 0;
        for (char c : route.toCharArray()) {
            if (c == 'R') {
                countR++; // считаем количество R
            }
        }

        // Потокобезопасное обновление карты
        synchronized (sizeToFreq) {
            sizeToFreq.put(countR, sizeToFreq.getOrDefault(countR, 0) + 1);
        }
    }

    // Анализ полученных данных и печать результата
    public static void analyzeAndPrintResult() {
        Map.Entry<Integer, Integer> mostCommonEntry = sizeToFreq.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).orElse(null);

        // Самое частое количество повторений
        System.out.println("Самое частое количество повторений "
                + mostCommonEntry.getKey() + " (встречалось "
                + mostCommonEntry.getValue() + " раз)");

        // Другие размеры
        System.out.println("Другие размеры:");
        sizeToFreq.entrySet().stream()
                .filter(entry -> !entry.equals(mostCommonEntry))
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered(entry -> System.out.println("- " + entry.getKey() + " (" + entry.getValue() + " раз)"));
    }
}