package com.udacity.stockhawk.sync;

import android.content.ContentProviderOperation;
import android.util.Log;

import com.udacity.stockhawk.data.QuoteColumns;
import com.udacity.stockhawk.data.QuoteProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

/**
 * StockHawk created by hendercine on 3/14/17.
 */

public class Utilities {

    private static String LOG_TAG = Utilities.class.getSimpleName();

    public static boolean showPercent = true;

    public static ArrayList<ContentProviderOperation> quoteJsonToContentVals(String JSON)throws JSONException {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
        JSONObject jsonObject;
        JSONArray resultsArray;
        ContentProviderOperation cpo;

        try{
            jsonObject = new JSONObject(JSON);
            if (jsonObject != null && jsonObject.length() != 0){
                jsonObject = jsonObject.getJSONObject("query");
                int count = Integer.parseInt(jsonObject.getString("count"));
                if (count == 1){
                    jsonObject = jsonObject.getJSONObject("results")
                            .getJSONObject("quote");
                    cpo = buildBatchOperation(jsonObject);
                    if (cpo != null){
                        batchOperations.add(cpo);
                    }

                } else{
                    resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");

                    if (resultsArray != null && resultsArray.length() != 0){
                        for (int i = 0; i < resultsArray.length(); i++){
                            jsonObject = resultsArray.getJSONObject(i);
                            cpo = buildBatchOperation(jsonObject);
                            if (cpo != null){
                                batchOperations.add(cpo);
                            }
                        }
                    }
                }
            }
        } catch (JSONException e){
            Log.e(LOG_TAG, "String to JSON failed: " + e);
        }
        return batchOperations;
    }

    public static String truncateBidPrice(String bidPrice){
        bidPrice = String.format(Locale.US, "%.2f", Float.parseFloat(bidPrice));
        return bidPrice;
    }

    public static String truncateChange(String change, boolean isPercentChange){
        String weight = change.substring(0,1);
        String ampersand = "";
        if (isPercentChange){
            ampersand = change.substring(change.length() - 1, change.length());
            change = change.substring(0, change.length() - 1);
        }
        change = change.substring(1, change.length());
        double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
        change = String.format(Locale.US, "%.2f", round);
        StringBuilder changeBuffer = new StringBuilder(change);
        changeBuffer.insert(0, weight);
        changeBuffer.append(ampersand);
        change = changeBuffer.toString();
        return change;
    }

    public static ContentProviderOperation buildBatchOperation(JSONObject jsonObject) throws JSONException{
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                QuoteProvider.Quotes.CONTENT_URI);
        if (!jsonObject.getString("Change").equals("null") && !jsonObject.getString("Bid").equals("null")) {
            String change = jsonObject.getString("Change");
            builder.withValue(QuoteColumns.SYMBOL, jsonObject.getString("symbol"));
            builder.withValue(QuoteColumns.BIDPRICE, truncateBidPrice(jsonObject.getString("Bid")));
            builder.withValue(QuoteColumns.PERCENT_CHANGE, truncateChange(jsonObject.getString("ChangeinPercent"), true));
            builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false));
            builder.withValue(QuoteColumns.ISCURRENT, 1);
            if (change.charAt(0) == '-') {
                builder.withValue(QuoteColumns.ISUP, 0);
            } else {
                builder.withValue(QuoteColumns.ISUP, 1);
            }
            builder.withValue(QuoteColumns.NAME, jsonObject.getString("Name"));
            builder.withValue(QuoteColumns.CURRENCY, jsonObject.getString("Currency"));
            builder.withValue(QuoteColumns.DAYLOW, jsonObject.getString("DaysLow"));
            builder.withValue(QuoteColumns.DAYHIGH, jsonObject.getString("DaysHigh"));
            builder.withValue(QuoteColumns.YEARLOW, jsonObject.getString("YearLow"));
            builder.withValue(QuoteColumns.YEARHIGH, jsonObject.getString("YearHigh"));
            builder.withValue(QuoteColumns.EARNINGSSHARE, jsonObject.getString("EarningsShare"));

        } else {
            return null;
        }
        return builder.build();
    }
}

