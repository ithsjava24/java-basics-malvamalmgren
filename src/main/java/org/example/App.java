package org.example;

import java.util.*;

public class App {


    public static void main(String[] args) {
        Locale locale = new Locale("sv", "SE");
        Locale.setDefault(locale);
        Scanner sc = new Scanner(System.in);

        String[] hours = new String[24];
        for (int i = 0; i < 24; i++) {
            String hour1 = String.format("%02d", i);
            String hour2 = String.format("%02d", (i + 1) % 24);
            hours[i] = hour1 + "-" + hour2;
        }

        ArrayList<Integer> pricePerHour = new ArrayList<>(24);

        String choice = "";

        while (!choice.equalsIgnoreCase("e")) {
            printMenu();
            choice = sc.nextLine().toLowerCase();

            switch (choice) {
                case "1":
                    // Inmatning av elpriser för 24 timmar
                    input(hours, pricePerHour, sc);
                    break;
                case "2":
                    //När alternativ 2 väljs på menyn så ska programmet skriva ut lägsta priset, högsta priset samt vilka
                    //timmar som detta infaller under dygnet. Dygnets medelpris ska också räknas fram och presenteras
                    //på skärmen. Se testerna för önskat format på utmatningen.
                    if (pricePerHour.isEmpty()) {
                        System.out.println("Listan är tom.");
                        continue;
                    }
                    Statistics stats = calculateStatistics(pricePerHour);
                    System.out.print("Lägsta pris: " + hours[stats.minIndex] + ", " + stats.min() + " öre/kWh\n"
                            + "Högsta pris: " + hours[stats.maxIndex] + ", " + stats.max() + " öre/kWh\n"
                            + "Medelpris: " + String.format("%.2f", stats.average()) + " öre/kWh\n");
                    break;
                case "3":
                    if (pricePerHour.isEmpty()) {
                        throw new RuntimeException();
                        //System.out.println("Listan är tom, inget att sortera.");
                        //continue;
                    }
                    //sortering
                    ValueAndIndex priceAndHour = new ValueAndIndex(pricePerHour, hours);
                    sortPrices(priceAndHour);
//                    for (ValueAndIndex v : priceAndHour) {
//                        System.out.println("Index: " + v.index() + " Value: " + v.value());
//                    }

                    break;
                case "4":
                    System.out.println("");
                    break;
                case "5":
                    System.out.println("");
                    break;
                case "e":
                    System.out.println("");
                    break;
                default:
                    System.out.print("Otillåtet svar, välj ett av alternativen...\n");
                    break;
            }

            //Sortera!


//            int lowestPrice = pricePerHour.get(0);
//            int highestPrice = pricePerHour.get(1);
//            int mediumPrice = pricePerHour.get(2);
        }

//        1. Inmatning
//        Senaste året har elpriserna blivit högre och varierar mycket. Det här programmet ska kunna hjälpa
//        till med att analysera elpriser för ett dygn. När man väljer alternativet inmatning från menyn ska
//        programmet fråga efter priserna under dygnets timmar. Inmatningen av värden ska ske med hela
//        ören. T.ex. kan priser vara 50 102 eller 680 öre per kW/h. Priset sätts per intervall mellan två hela
//        timmar. Dygnets första pris är då mellan 00-01, andra intervallet är mellan 01-02 osv


//        float f = 2.3f;
//        String.format("%.2f", f).replace('.', ',');
//        System.out.printf(Locale.of("sv", "SE"), "%.2f", f);
//        System.out.println("Hello There!");


    }
    record ValueAndIndex(ArrayList<Integer> value, String[] index) {}

    private static void sortPrices(ValueAndIndex priceAndHour) {
        //3. ValueAndIndex priceAndHour = new ValueAndIndex(pricePerHour, hours);
        //vill ha priserna och timmarna, kopior
        ArrayList<Integer> pricePerHour = priceAndHour.value;
        String[] hours = new String[priceAndHour.index.length];
        System.out.println("HI 1");

        //temps
//        ArrayList<Integer> sortedPrices = new ArrayList<>(pricePerHour);
//        String[] sortedHours = new String[pricePerHour.size()];

        //sortera ören
        for (int i = 0; i < pricePerHour.size(); i++) {
            int max = pricePerHour.get(i);
            for (int j = i + 1; j < pricePerHour.size(); j++) {
                if (pricePerHour.get(j) > max) {
                    max = j;
                }
            }
            int tempPrice = pricePerHour.get(i);
            pricePerHour.set(i, pricePerHour.get(max));
            pricePerHour.set(max, tempPrice);

            String tempHour = hours[i];
            hours[i] = hours[max];
            hours[max] = tempHour;

        }


//        for (int i = 0; i < pricePerHour.size(); i++) { //Går genom priserna
//            int maxIndex = i;
//            for (int j = i + 1; j < hours.length; j++) {
//                if (pricePerHour.get(j) > pricePerHour.get(maxIndex)) {
//
//                }
//            }
//        }
        System.out.println("HI 2");

        System.out.println("HI 3");
        //ha index för vilken timma

        //PRINT
//        for (ValueAndIndex v : priceAndHour) {
//            System.out.println("Index: " + v.index() + " Value: " + v.value());
//        }
//        System.out.print();
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

        float average = (float) total / pricePerHour.size(); // Medelvärdet som float

        return new Statistics(min, minIndex, max, maxIndex, average);
    }

    private static void input(String[] hours, ArrayList<Integer> pricePerHour, Scanner sc) {
        pricePerHour.clear();
        for (int i = 0; i < 24; i++) {
//            if (i <= 0)
//                throw new IllegalArgumentException();
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

    public int minIndex() {
        return minIndex;
    }

    public int max() {
        return max;
    }

    public int maxIndex() {
        return maxIndex;
    }

    public float average() {
        return average;
    }
}

