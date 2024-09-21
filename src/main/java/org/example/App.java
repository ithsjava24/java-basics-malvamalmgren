package org.example;

import java.util.*;

public class App {


    public static void main(String[] args) {
        Locale.setDefault(new Locale("sv", "SE"));
        Scanner sc = new Scanner(System.in);

        String[] hours = new String[24];

        for (int i = 0; i < 24; i++) {
            String hour1 = String.format("%02d", i);
            String hour2 = (i == 23) ? "24" : String.format("%02d", (i + 1) % 24);
            hours[i] = hour1 + "-" + hour2;
        }


        ArrayList<Integer> pricePerHour = new ArrayList<>(24);

        String choice = "";

        while (!choice.equalsIgnoreCase("e")) {
            printMenu();
            choice = sc.nextLine().toLowerCase();

            switch (choice) {
                case "1" ->
                        input(hours, pricePerHour, sc);
                case "2" -> {
                    if (pricePerHour.isEmpty()) {
                        System.out.println("Listan är tom.");
                        continue;
                    }
                    Statistics stats = calculateStatistics(pricePerHour);
                    System.out.print("Lägsta pris: " + hours[stats.minIndex] + ", " + stats.min() + " öre/kWh\n"
                            + "Högsta pris: " + hours[stats.maxIndex] + ", " + stats.max() + " öre/kWh\n"
                            + "Medelpris: " + String.format("%.2f", stats.average()) + " öre/kWh\n");
                }
                case "3" -> {
                    if (pricePerHour.isEmpty()) {
                        System.out.println("Listan är tom, inget att sortera.");
                        continue;
                    }

                    List<ValueAndIndex> priceAndHourList = new ArrayList<>();
                    for (int i = 0; i < pricePerHour.size(); i++) {
                        priceAndHourList.add(new ValueAndIndex(pricePerHour.get(i), hours[i]));
                    }
                    sortPrices(priceAndHourList);

                    for (ValueAndIndex v : priceAndHourList) {
                        System.out.print("\n" + v.hour() + " " + v.price() + " öre");
                    }
                    System.out.print("\n");
                }
                case "4" -> {
                    if (pricePerHour.size() < 4) {
                        System.out.println("Listan är tom.");
                        continue;
                    }

                    int minSum = Integer.MAX_VALUE;
                    int startIndex = 0;

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
                case "5" -> System.out.println("");
                case "e" -> System.out.println("");
                default -> System.out.print("Otillåtet svar, välj ett av alternativen...\n");
            }
        }
    }

    static class ValueAndIndex implements Comparable<ValueAndIndex> {
        private int price;
        private String hour;

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

    private static void sortPrices(List<ValueAndIndex> priceAndHourList) {
        Collections.sort(priceAndHourList);
    }

    private static void printMenu() {
        String menu = """
                Elpriser
                ========
                1. Inmatning
                2. Min, Max och Medel
                3. Sortera
                4. Bästa Laddningstid (4h)
                e. Avsluta
                """;
        System.out.print(menu);
    }

    private static Statistics calculateStatistics(ArrayList<Integer> pricePerHour) {
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

        return new Statistics(min, minIndex, max, maxIndex, average);
    }

    private static void input(String[] hours, ArrayList<Integer> pricePerHour, Scanner sc) {
        pricePerHour.clear();
        for (int i = 0; i < 24; i++) {
            System.out.print("Ange elpriset i hela ören för kl. " + hours[i] + ": ");
            pricePerHour.add(sc.nextInt()); // Mata in priset i ören
        }
        sc.nextLine();
    }
}

class Statistics {
    int min;
    int minIndex;
    int max;
    int maxIndex;
    float average;

    public Statistics(int min, int minIndex, int max, int maxIndex, float average) {
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

