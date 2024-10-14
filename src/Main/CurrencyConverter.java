package Main;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class CurrencyConverter {
    private static final String API_KEY = "90b58f23c15396f443b3f3e1";
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/USD";

    public enum Moneda {
        USD("USD"),
        ARS("ARS"),
        CLP("CLP"),
        COP("COP");

        private final String codigo;

        Moneda(String codigo) {
            this.codigo = codigo;
        }

        public String getCodigo() {
            return codigo;
        }
    }

    public static void main(String[] args) {
        HttpClient client = HttpClient.newHttpClient();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("******** Bienvenido al Convertidor de Monedas ********");
            System.out.println("1. USD a ARS");
            System.out.println("2. ARS a USD");
            System.out.println("3. USD a CLP");
            System.out.println("4. CLP a USD");
            System.out.println("5. USD a COP");
            System.out.println("6. COP a USD");
            System.out.println("7. Salir");

            System.out.print("Escriba la opción deseada: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea

            switch (choice) {
                case 1:
                    convertirMoneda(client, Moneda.USD, Moneda.ARS, scanner);
                    break;
                case 2:
                    convertirMoneda(client, Moneda.ARS, Moneda.USD, scanner);
                    break;
                case 3:
                    convertirMoneda(client, Moneda.USD, Moneda.CLP, scanner);
                    break;
                case 4:
                    convertirMoneda(client, Moneda.CLP, Moneda.USD, scanner);
                    break;
                case 5:
                    convertirMoneda(client, Moneda.USD, Moneda.COP, scanner);
                    break;
                case 6:
                    convertirMoneda(client, Moneda.COP, Moneda.USD, scanner);
                    break;
                case 7:
                    System.out.println("Finalizando Programa. Gracias por utilizar nuestro servicio");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opción incorrecta. Vuelva a intentarlo.");
            }
        }
    }

    private static void convertirMoneda(HttpClient client, Moneda monedaOrigen, Moneda monedaDestino, Scanner scanner) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(response.body(), JsonObject.class);

            JsonElement conversionRate = jsonObject.get("conversion_rates");
            if (conversionRate != null && conversionRate.isJsonObject()) {
                JsonObject conversionRateObject = conversionRate.getAsJsonObject();
                JsonElement rate = conversionRateObject.get(monedaDestino.getCodigo());
                if (rate != null) {
                    double rateValue = rate.getAsDouble();

                    System.out.print("Ingrese el monto en " + monedaOrigen.getCodigo() + " que desea convertir: ");
                    double amount = scanner.nextDouble();
                    scanner.nextLine(); // Consumir el salto de línea

                    double convertedAmount = amount * rateValue;
                    System.out.printf("%.2f " + monedaOrigen.getCodigo() + " = %.2f " + monedaDestino.getCodigo() + "%n", amount, convertedAmount);
                } else {
                    System.out.println("No se encontró la tasa de conversión para " + monedaDestino.getCodigo());
                }
            } else {
                System.out.println("No se encontró la tasa de conversión");
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error: " + ((Throwable) e).getMessage());
        }
    }
}