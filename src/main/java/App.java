import enums.ErrorMessages;
import exceptions.ResponseBodyIsEmpty;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Map;
import java.util.Scanner;

public class App {
    public static final String QUIT = "QUIT";
    public static final String HELP = "HELP";
    public static final String BITCOIN_CURRENT_PRICE_ENDPOINT = "https://api.coindesk.com/v1/bpi/currentprice/%s.json";
    public static final String BITCOIN_PAST_PRICES_ENDPOINT = "https://api.coindesk.com/v1/bpi/historical/close.json?start=%s&end=%s&currency=%s";

    private OkHttpClient client = new OkHttpClient();
    private BigDecimal currentPrice;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Currency currency;

    public App() {
        this.currentPrice = null;
        this.minPrice = null;
        this.maxPrice = null;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public static void main(String[] args) {
        App app = new App();
        app.console();
    }


    /**
     * Main console method that runs the app
     */
    private void console() {
        printLogo();
        System.out.println("Welcome to the bitcoin-status!");
        String commandLine = "";
        Scanner scanner = new Scanner(System.in);
        while (!commandLine.equals(QUIT)) {
            System.out.println("\nIf you need help, type 'HELP'\nType 'QUIT' to exit" +
                    "\nOr type currency code to continue:");
            commandLine = scanner.nextLine().trim().toUpperCase();
            // Check the user input commandline
            if (commandLine.equals(QUIT)) {
                System.out.println("Thanks for using bitcoin-status. Bye...");
            } else if (commandLine.isEmpty()) {
                continue;
            } else if (commandLine.equals(HELP)) {
                help();
            } else {
                try {
                    handleCurrencyCall(commandLine);
                } catch (IllegalArgumentException e) {
                    System.out.println(ErrorMessages.REQUESTED_CURRENCY_IS_NOT_VALID);
                } catch (IOException e) {
                    System.out.println(ErrorMessages.COINDESK_API_SERVICE_FAILED);
                } catch (NullPointerException e) {
                    System.out.println(ErrorMessages.USER_INPUT_FOR_CURRENCY_IS_NULL);
                } catch (JSONException e) {
                    System.out.println(ErrorMessages.COINDESK_API_JSON_IS_NOT_VALID_AS_MENTION_IN_DOCUMENTATION);
                } catch (ResponseBodyIsEmpty e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        scanner.close();
    }

    /**
     * Call api that returns Response of api service
     */
    protected Response callApi(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        return client.newCall(request).execute();
    }

    /**
     * Once the user provides the currency code, the application should display the following information
     * •	The current Bitcoin rate, in the requested currency
     * •	The lowest Bitcoin rate in the last 30 days, in the requested currency
     * •	The highest Bitcoin rate in the last 30 days, in the requested currency
     */
    protected void handleCurrencyCall(String reqCurrency) throws IllegalArgumentException, IOException, NullPointerException, ResponseBodyIsEmpty, JSONException {
        // if the user typed currency, validate correction of currency
        this.setCurrency(Currency.getInstance(reqCurrency));
        String url = String.format(BITCOIN_CURRENT_PRICE_ENDPOINT, currency.getCurrencyCode());
        // call coindesk api service to get current bitcoin rate
        Response response = callApi(url);
        String responseBody = response.body().string();
        if (response.code() == ErrorMessages.NOT_FOUND.getErrorCode()) {
            System.out.println(responseBody);
        } else if (response.code() == 200) {
            if (responseBody.isEmpty()) {
                throw new ResponseBodyIsEmpty(ErrorMessages.RESPONSE_BODY_OF_API_IS_EMPTY.name());
            }
            parseCurrentBitcoinPrice(responseBody);
        }
        // call coindesk api service to get max, min rate of bitcoin in last 30 days
        LocalDate currentDate = LocalDate.now();
        url = String.format(BITCOIN_PAST_PRICES_ENDPOINT, currentDate.minusDays(30), currentDate, this.getCurrency().getCurrencyCode());
        response = callApi(url);
        responseBody = response.body().string();
        if (response.code() == ErrorMessages.NOT_FOUND.getErrorCode()) {
            System.out.println(responseBody);
        } else if (response.code() == 200) {
            if (responseBody.isEmpty()) {
                ResponseBodyIsEmpty ex = new ResponseBodyIsEmpty("Response body of coindesk.com api is empty");
                throw ex;
            }
            parseBitcoinPastPrice(responseBody);
        }
    }

    /**
     * Application guide for user
     */
    private void help() {
        System.out.println("These are the commands that you can use: ");
        System.out.println("1. Currency codes: 'USD', 'EUR', 'GBP', etc.. ");
        System.out.println("2. Type 'QUIT' to exit from program");
    }

    /**
     * Parse json string of current bitcoin rate api service's response body
     */
    protected void parseCurrentBitcoinPrice(String str) {
        JSONObject jsonObject = new JSONObject(str);
        currentPrice = jsonObject.getJSONObject("bpi").getJSONObject(currency.getCurrencyCode()).getBigDecimal("rate_float");
        System.out.println(LocalDateTime.now() + " - Current bitcoin rate = " + currentPrice.toString() + " " + currency);
    }

    /**
     * Parse json string of min, max bitcoin rate in last 30 days api service's response body
     */
    protected void parseBitcoinPastPrice(String str) {
        JSONObject jsonObject = new JSONObject(str);
        BigDecimal minRate = null, maxRate = null;
        for (Map.Entry<String, Object> entry : jsonObject.getJSONObject("bpi").toMap().entrySet()) {
            minRate = minRate == null ? (BigDecimal) entry.getValue() : minRate.min((BigDecimal) entry.getValue());
            maxRate = maxRate == null ? (BigDecimal) entry.getValue() : maxRate.max((BigDecimal) entry.getValue());
        }
        if (minRate != null && maxRate != null) {
            System.out.println("Minimum bitcoin rate in last 30 days = " + minRate + " " + currency.getCurrencyCode());
            System.out.println("Maximum bitcoin rate in last 30 days = " + maxRate + " " + currency.getCurrencyCode());
            this.maxPrice = maxRate;
            this.minPrice = minRate;
        }
    }

    /**
     *
     */
    private void printLogo() {
        System.out.println("\n" +
                "██████╗░██╗████████╗░█████╗░░█████╗░██╗███╗░░██╗\n" +
                "██╔══██╗██║╚══██╔══╝██╔══██╗██╔══██╗██║████╗░██║\n" +
                "██████╦╝██║░░░██║░░░██║░░╚═╝██║░░██║██║██╔██╗██║\n" +
                "██╔══██╗██║░░░██║░░░██║░░██╗██║░░██║██║██║╚████║\n" +
                "██████╦╝██║░░░██║░░░╚█████╔╝╚█████╔╝██║██║░╚███║\n" +
                "╚═════╝░╚═╝░░░╚═╝░░░░╚════╝░░╚════╝░╚═╝╚═╝░░╚══╝");
    }
}
