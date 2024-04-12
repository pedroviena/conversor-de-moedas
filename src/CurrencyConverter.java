import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.Set;

public class CurrencyConverter {

    private static final String API_KEY = "1894cdb3889a161cae539b72";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        // Listar moedas disponíveis
        System.out.println("Moedas disponíveis:");
        Set<String> currencies = getCurrencies();
        for (String currency : currencies) {
            System.out.println(currency);
        }

        // Solicitar a moeda de origem e de destino
        System.out.print("\nDigite a moeda de origem (ex: USD, EUR, etc.): ");
        String fromCurrency = scanner.nextLine().toUpperCase();
        System.out.print("Digite a moeda de destino (ex: USD, EUR, etc.): ");
        String toCurrency = scanner.nextLine().toUpperCase();

        // Solicitar o valor a ser convertido
        System.out.print("Digite o valor em " + fromCurrency + ": ");
        double amount = scanner.nextDouble();

        // Obter a taxa de câmbio
        double exchangeRate = getExchangeRate(fromCurrency, toCurrency);

        if (exchangeRate != -1) {
            // Calcular e exibir o resultado da conversão
            double convertedAmount = amount * exchangeRate;
            System.out.println(amount + " " + fromCurrency + " = " + convertedAmount + " " + toCurrency);
        } else {
            System.out.println("Não foi possível obter a taxa de câmbio. Verifique suas moedas.");
        }
    }

    private static Set<String> getCurrencies() throws IOException {
        String urlString = BASE_URL + "USD"; // Escolha arbitrariamente, pois as taxas são as mesmas para todas as moedas
        URL url = new URL(urlString);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();

        JsonParser jsonParser = new JsonParser();
        JsonElement root = jsonParser.parse(new InputStreamReader((InputStream) request.getContent()));
        JsonObject jsonObject = root.getAsJsonObject();

        if (jsonObject.has("conversion_rates")) {
            JsonObject conversionRates = jsonObject.getAsJsonObject("conversion_rates");
            return conversionRates.keySet();
        }

        return null;
    }

    private static double getExchangeRate(String fromCurrency, String toCurrency) throws IOException {
        String urlString = BASE_URL + fromCurrency;
        URL url = new URL(urlString);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();

        JsonParser jsonParser = new JsonParser();
        JsonElement root = jsonParser.parse(new InputStreamReader((InputStream) request.getContent()));
        JsonObject jsonObject = root.getAsJsonObject();

        if (jsonObject.has("conversion_rates")) {
            JsonObject conversionRates = jsonObject.getAsJsonObject("conversion_rates");
            if (conversionRates.has(toCurrency)) {
                return conversionRates.get(toCurrency).getAsDouble();
            }
        }

        return -1;
    }
}
