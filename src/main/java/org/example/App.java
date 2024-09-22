package org.example;

import java.util.*;

public class App {
    static ArrayList<Integer> pricePerHour;
    static String[] hours;

    public static void main(String[] args) {
        Locale.setDefault(new Locale("sv", "SE"));
        Scanner sc = new Scanner(System.in);

        hours = new String[24];
        for (int i = 0; i < 24; i++) {
            hours[i] = String.format("%02d", i) + "-" + String.format("%02d", i + 1);
        }

        pricePerHour = new ArrayList<>(24);

        String choice = "";

        while (!choice.equals("e")) {
            printMenu();
            choice = sc.nextLine().toLowerCase();

            switch (choice) {
                case "1" -> inputPrices(sc);
                case "2" -> printMinMaxAverage();
                case "3" -> sortAndPrintPrices();
                case "4" -> cheapestChargingPeriod();
                case "5" -> visualization();
                case "e" -> System.out.println("Avslutar programmet...");
                default -> System.out.print("Otillåtet svar, välj ett av alternativen...\n");
            }
        }
        sc.close();
    }

    private static void printMenu() {
        String menu = """
                Elpriser
                ========
                1. Inmatning
                2. Min, Max och Medel
                3. Sortera
                4. Bästa Laddningstid (4h)
                5. Visualisering
                e. Avsluta
                """;
        System.out.print(menu);
    }

    private static void inputPrices(Scanner sc) {
        pricePerHour.clear();
        for (int i = 0; i < 24; i++) {
            System.out.print("Pris klockan " + hours[i] + ": ");
            pricePerHour.add(sc.nextInt());
        }
        sc.nextLine();
    }

    private static void printMinMaxAverage() {
        if (pricePerHour.isEmpty()) {
            System.out.print("Data saknas.");
            return;
        }

        MinMaxAverage stats = getMinMaxAverage();
        System.out.print("Lägsta pris: " + hours[stats.minIndex] + ", " + stats.min() + " öre/kWh\n"
                + "Högsta pris: " + hours[stats.maxIndex] + ", " + stats.max() + " öre/kWh\n"
                + "Medelpris: " + String.format("%.2f", stats.average()) + " öre/kWh\n");
    }

    private static MinMaxAverage getMinMaxAverage() {
        int min = Integer.MAX_VALUE;
        int minIndex = 0;
        int max = Integer.MIN_VALUE;
        int maxIndex = 0;
        int total = 0;

        for (int price : pricePerHour) {
            if (price < min) {
                min = price;
                minIndex = pricePerHour.indexOf(min);
            }

            if (price > max) {
                max = price;
                maxIndex = pricePerHour.indexOf(max);
            }
            total += price;
        }

        float average = (float) total / pricePerHour.size();

        return new MinMaxAverage(min, minIndex, max, maxIndex, average);
    }

    private static void sortAndPrintPrices() {
        if (pricePerHour.isEmpty()) {
            System.out.println("Data saknas.");
            return;
        }

        ArrayList<ValueAndIndex> priceAndHour = new ArrayList<>();
        for (int i = 0; i < pricePerHour.size(); i++) {
            priceAndHour.add(new ValueAndIndex(pricePerHour.get(i), hours[i]));
        }

        Collections.sort(priceAndHour);

        for (ValueAndIndex v : priceAndHour) {
            System.out.print("\n" + v.hour() + " " + v.price() + " öre");
        }
        System.out.print("\n");
    }

    static class ValueAndIndex implements Comparable<ValueAndIndex> {
        private final int price;
        private final String hour;

        public ValueAndIndex(int price, String hour) {
            this.price = price;
            this.hour = hour;
        }

        public int price() {
            return price;
        }

        public String hour() {
            return hour;
        }

        @Override
        public int compareTo(ValueAndIndex other) {
            return Integer.compare(other.price, this.price);
        }
    }

    static void cheapestChargingPeriod() {
        if (pricePerHour.size() < 4) {
            System.out.println("Data saknas.");
            return;
        }

        int startIndex = 0;
        int minSum = Integer.MAX_VALUE;

        for (int i = 0; i <= pricePerHour.size() - 4; i++) {
            int sum = 0;
            for (int j = i; j < i + 4; j++) {
                sum += pricePerHour.get(j);
            }
            if (sum < minSum) {
                minSum = sum;
                startIndex = i;
            }
        }

        float averagePrice = (float) minSum / 4;

        String startTime = hours[startIndex].substring(0, 2);

        System.out.print("Påbörja laddning klockan " + startTime + "\n");
        System.out.print("Medelpris 4h: " + String.format("%.1f", averagePrice) + " öre/kWh\n");
    }

    static void visualization() {
        MinMaxAverage stats = getMinMaxAverage();
        float interval = (stats.max - stats.min) / 5.0f;
        String[] rows = new String[6];

        int maxPriceLength = String.valueOf(stats.max).length();
        int minPriceLength = String.valueOf(stats.min).length();

        for (int i = 0; i < rows.length; i++) {

            if (i == 0) {
                rows[i] = String.valueOf(stats.max);
            } else if (i == rows.length - 1) {
                rows[i] = " ".repeat(maxPriceLength - minPriceLength) + String.valueOf(stats.min);
            } else {
                rows[i] = " ".repeat(maxPriceLength);
            }
            rows[i] += "|";
            int tempRow = 5 - i;
            for (int j = 0; j < pricePerHour.size(); j++) {
                int price = pricePerHour.get(j);

                if (price >= (int) (stats.min + (interval * tempRow))) {
                    rows[i] += "  x";
                } else rows[i] += ("   ");
            }
        }

        String[] bottom = new String[2];
        bottom[0] = " ".repeat(maxPriceLength) + "|" + "-".repeat(72);
        bottom[1] = " ".repeat(maxPriceLength) + "| ";
        for (int i = 0; i < 24; i++) {
            bottom[1] += String.format("%02d", i) + (i != 23 ? " " : "");
        }

        for (int i = 0; i < rows.length; i++) {
            System.out.print(rows[i] + "\n");
        }
        System.out.print(bottom[0] + "\n");
        System.out.print(bottom[1] + "\n");
    }

}

class MinMaxAverage {
    int min;
    int minIndex;
    int max;
    int maxIndex;
    float average;

    public MinMaxAverage(int min, int minIndex, int max, int maxIndex, float average) {
        this.min = min;
        this.minIndex = minIndex;
        this.max = max;
        this.maxIndex = maxIndex;
        this.average = average;
    }

    public int min() {
        return min;
    }

    public int max() {
        return max;
    }

    public float average() {
        return average;
    }
}