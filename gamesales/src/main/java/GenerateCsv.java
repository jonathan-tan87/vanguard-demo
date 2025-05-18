import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

public class GenerateCsv {

    private static final Random random = new Random();
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        String filePath = "large_game_sales.csv";
        int rowCount = 1_000_000; // 1 million rows
//        String filePath = "small_game_sales.csv";
//        int rowCount = 10000; // For testing

        LocalDate startDate = LocalDate.of(2024, 4, 1);
        LocalDate endDate = LocalDate.of(2024, 4, 30);

        long start = System.currentTimeMillis();
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Header
            writer.println("id,game_no,game_name,game_code,type,cost_price,tax,sale_price,date_of_sale");

            for (int i = 1; i <= rowCount; i++) {
                int id = i;
                int gameNo = random.nextInt(100) + 1;
                String gameName = "Game_" + generateRandomString(10);
                String gameCode = generateRandomString(5).toUpperCase();
                int type = random.nextInt(2) + 1; // 1 or 2

                BigDecimal costPrice = BigDecimal.valueOf(random.nextDouble() * 99.99 + 0.01)
                                            .setScale(2, RoundingMode.HALF_UP);
                if (costPrice.compareTo(new BigDecimal("100.00")) > 0) {
                    costPrice = new BigDecimal("100.00");
                }

                BigDecimal taxRate = new BigDecimal("0.09");
                BigDecimal tax = costPrice.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal salePrice = costPrice.add(tax).setScale(2, RoundingMode.HALF_UP);

                // Random date in April 2024
                long startEpochDay = startDate.toEpochDay();
                long endEpochDay = endDate.toEpochDay();
                long randomEpochDay = startEpochDay + random.nextInt((int) (endEpochDay - startEpochDay + 1));
                LocalDate randomDate = LocalDate.ofEpochDay(randomEpochDay);
                LocalTime randomTime = LocalTime.of(random.nextInt(24), random.nextInt(60), random.nextInt(60));
                LocalDateTime randomDateTime = LocalDateTime.of(randomDate, randomTime);
                String dateOfSale = randomDateTime.format(TIMESTAMP_FORMATTER);

                writer.printf("%d,%d,%s,%s,%d,%.2f,%.2f,%.2f,%s\n",
                        id, gameNo, gameName, gameCode, type, costPrice, tax, salePrice, dateOfSale);

                if (i % 100000 == 0) {
                    System.out.println("Generated " + i + " rows...");
                }
            }
            System.out.println("CSV file generated successfully: " + filePath);
            System.out.println("Time taken: " + (System.currentTimeMillis() - start) + " ms");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }
}