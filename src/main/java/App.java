import enums.ErrorMessages;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;

public class App {
    public static final String QUIT = "QUIT";
    public static final String HELP = "HELP";
    public static final String BITCOIN_CURRENT_PRICE_ENDPOINT = "https://api.coindesk.com/v1/bpi/currentprice/%s.json";
    private OkHttpClient client = new OkHttpClient();
    private float currentPrice;

    public static void main(String[] args) {
        App app = new App();
        app.console();
    }

    private void console() {
        System.out.println("Welcome to the bitcoin-status.\nIf you need help, type 'HELP'\nOr type currency code to continue: ");
        String commandLine = "";
        Scanner scanner = new Scanner(System.in);
        while (!commandLine.equals(QUIT)) {
            commandLine = scanner.nextLine().trim().toUpperCase();
            if (commandLine.equals(QUIT)) {
                System.out.println("Thanks for using bitcoin-status. Bye....");
            } else if (commandLine.isEmpty()) {
                System.out.println("Please enter an input to continue or type 'HELP'");
            } else if (commandLine.equals(HELP)) {
                help();
            } else {
                String url = String.format(BITCOIN_CURRENT_PRICE_ENDPOINT, commandLine);
                Request request = new Request.Builder().url(url).build();
                try {
                    Response response = client.newCall(request).execute();
                    String responseBody = response.body().string();
                    if (response.code() == ErrorMessages.NOT_FOUND.getErrorCode()) {
                        System.out.println(responseBody);
                        System.out.println("Currency code not found in coindesk.com. Please enter another currency: ");
                        continue;
                    }
                    parseCurrentBitcoinPrice(responseBody, commandLine);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        scanner.close();
    }

    private void help() {
        System.out.println("These are commands that you can use: ");
        System.out.println("1. Currency codes: 'USD', 'EUR', 'GBP', etc.. ");
        System.out.println("2. Type 'QUIT' to exit from program");
    }

    private void parseCurrentBitcoinPrice(String str, String currency) {
        JSONObject jsonObject = new JSONObject(str);
        currentPrice = jsonObject.getJSONObject("bpi").getJSONObject(currency).getFloat("rate_float");
        System.out.println(LocalDateTime.now() + " - Current bitcoin rate = " + currentPrice + " " + currency + " \n");
    }
}
