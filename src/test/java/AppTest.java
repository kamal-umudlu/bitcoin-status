import exceptions.ResponseBodyIsEmpty;
import okhttp3.Response;
import org.json.JSONException;
import org.junit.Test;

import java.io.IOException;
import java.util.Currency;

public class AppTest {
    /**
     * Test if the requested url for api service is null
     */
    @Test(expected = NullPointerException.class)
    public void testCallApiNullUrl() {
        App a = new App();
        try {
            Response response = a.callApi(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test if the requested url for api service is empty
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCallApiEmptryUrl() {
        App a = new App();
        try {
            Response response = a.callApi("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test if the currency is null
     */
    @Test(expected = NullPointerException.class)
    public void testHandleCurrencyCallIfNull() {
        App a = new App();
        try {
            a.handleCurrencyCall(null);
        } catch (IOException | ResponseBodyIsEmpty e) {
            e.printStackTrace();
        }
    }

    /**
     * Test if the currency is null
     */
    @Test(expected = IllegalArgumentException.class)
    public void testHandleCurrencyCallIfEmpty() {
        App a = new App();
        try {
            a.handleCurrencyCall("");
        } catch (IOException | ResponseBodyIsEmpty e) {
            e.printStackTrace();
        }
    }

    /**
     * Test if the currency is not valid
     */
    @Test(expected = IllegalArgumentException.class)
    public void testHandleCurrencyCallIfNotValid() {
        App a = new App();
        try {
            a.handleCurrencyCall("AMERICANUSD");
        } catch (IOException | ResponseBodyIsEmpty e) {
            e.printStackTrace();
        }
    }

    /**
     * Test if the json string is null
     */
    @Test(expected = NullPointerException.class)
    public void testParseCurrentBitcoinPriceIfNull() {
        App a = new App();
        a.setCurrency(Currency.getInstance("USD"));
        a.parseCurrentBitcoinPrice(null);
    }

    /**
     * Test if the json string is empty
     */
    @Test(expected = JSONException.class)
    public void testParseCurrentBitcoinPriceIfEmpty() {
        App a = new App();
        a.setCurrency(Currency.getInstance("USD"));
        a.parseCurrentBitcoinPrice("");
    }

    /**
     * Test if the json string doesn't have valid keys
     * 1. If json doesn't have key 'bpi'
     * 2. If json doesn't have key currency
     * 3. If 'rate_float' value doesn't exist
     */
    @Test(expected = JSONException.class)
    public void testParseCurrentBitcoinPriceIfNotValid() {
        App a = new App();
        a.setCurrency(Currency.getInstance("USD"));
        String notValid = "{\"time\":{\"updated\":\"Oct 20, 2021 09:39:00 UTC\",\"updatedISO\":\"2021-10-20T09:39:00+00:00\",\"updateduk\":\"Oct 20, 2021 at 10:39 BST\"},\"disclaimer\":\"This data was produced from the CoinDesk Bitcoin Price Index (USD). Non-USD currency data converted using hourly conversion rate from openexchangerates.org\",\"bpiNotValid\":{\"USD\":{\"code\":\"USD\",\"rate\":\"63,975.3850\",\"description\":\"United States Dollar\",\"rate_float\":63975.385},\"EUR\":{\"code\":\"EUR\",\"rate\":\"55,027.0839\",\"description\":\"Euro\",\"rate_float\":55027.0839}}}";
        a.parseCurrentBitcoinPrice(notValid);
    }

    /**
     * Test if the json string is null
     */
    @Test(expected = NullPointerException.class)
    public void testParseBitcoinPastPriceIfNull() {
        App a = new App();
        a.setCurrency(Currency.getInstance("USD"));
        a.parseBitcoinPastPrice(null);
    }

    /**
     * Test if the json string is null
     */
    @Test(expected = JSONException.class)
    public void testParseBitcoinPastPriceIfEmpty() {
        App a = new App();
        a.setCurrency(Currency.getInstance("USD"));
        a.parseBitcoinPastPrice("{}");
    }

    /**
     * Test if the json string doesn't have valid keys
     * 1. If json doesn't have key 'bpi'
     */
    @Test(expected = JSONException.class)
    public void testParseBitcoinPastPriceIfNotValid() {
        App a = new App();
        a.setCurrency(Currency.getInstance("USD"));
        a.parseBitcoinPastPrice("{\"bpiNotValid\":{\"2013-09-01\":128.2597,\"2013-09-02\":127.3648,\"2013-09-03\":127.5915,\"2013-09-04\":120.5738,\"2013-09-05\":120.5333},\"disclaimer\":\"This data was produced from the CoinDesk Bitcoin Price Index. BPI value data returned as USD.\",\"time\":{\"updated\":\"Sep 6, 2013 00:03:00 UTC\",\"updatedISO\":\"2013-09-06T00:03:00+00:00\"}}");
    }
}
