package com.udacity.stockhawk.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.QuoteColumns;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StockDetails extends AppCompatActivity {
    private static String LOG_TAG = "StockDetailsActivity";

    private LineChartView lineChartView;
    private String symbol;
    private String name;
    private Float high;
    private Float low;
    private Context mContext;

    //Information about the stocks
    private TextView tv_stock_name;
    private TextView tv_DayHigh;
    private TextView tv_DayLow;
    private TextView tv_stock_currency;
    private TextView tv_year_high;
    private TextView tv_year_low;
    private TextView tv_earnings;
    private TextView tv_stock_symbol;

    private String daylow;
    private String dayhigh;
    private String yearlow;
    private String yearhigh;
    private String currency;
    private String sharesearning;


    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        mContext=this;

        setContentView(R.layout.activity_detail);
        lineChartView = (LineChartView) findViewById(R.id.linechart);

        symbol= getIntent().getExtras().getString(QuoteColumns.SYMBOL);
        tv_stock_symbol = (TextView)findViewById(R.id.stock_symbol);
        tv_stock_symbol.setText(symbol);

        name= getIntent().getExtras().getString(QuoteColumns.NAME);
        tv_stock_name = (TextView)findViewById(R.id.stock_name);
        tv_stock_name.setText(name);

        currency= getIntent().getExtras().getString(QuoteColumns.CURRENCY);
        tv_stock_currency = (TextView)findViewById(R.id.stock_currency);
        tv_stock_currency.setText(currency);

        daylow= getIntent().getExtras().getString(QuoteColumns.DAYLOW);
        tv_DayLow = (TextView)findViewById(R.id.stock_daylow);
        tv_DayLow.setText(daylow);

        dayhigh= getIntent().getExtras().getString(QuoteColumns.DAYHIGH);
        tv_DayHigh = (TextView)findViewById(R.id.stock_dayhigh);
        tv_DayHigh.setText(dayhigh);

        yearhigh= getIntent().getExtras().getString(QuoteColumns.YEARHIGH);
        tv_year_high = (TextView)findViewById(R.id.stock_yearhigh);
        tv_year_high.setText(yearhigh);

        yearlow= getIntent().getExtras().getString(QuoteColumns.YEARLOW);
        tv_year_low = (TextView)findViewById(R.id.stock_yearmin);
        tv_year_low.setText(yearlow);

        sharesearning= getIntent().getExtras().getString(QuoteColumns.EARNINGSSHARE);
        tv_earnings = (TextView)findViewById(R.id.earning_share);
        tv_earnings.setText(sharesearning);



        //Default high and low stock values
        high = 0.00f;
        low = 999999.00f;

        if (getIntent().getExtras().containsKey(QuoteColumns.SYMBOL)){
            symbol = getIntent().getExtras().getString(QuoteColumns.SYMBOL);
            new StockGraphAsyncTask().execute(symbol,null);


        }else{

            Toast.makeText(mContext,"Nothing to show",Toast.LENGTH_SHORT).show();
        }

    }

    private class StockGraphAsyncTask extends AsyncTask<String, Void, LineSet> {

        @Override
        protected LineSet doInBackground(String... params) {

            StringBuilder urlStringBuilder = new StringBuilder();

            try{
                //Base URL for the Yahoo query
                urlStringBuilder.append("https://query.yahooapis.com/v1/public/yql?q=");
                urlStringBuilder.append(URLEncoder.encode("select * from yahoo.finance.historicaldata where symbol = \"", "UTF-8"));
                urlStringBuilder.append(params[0]);
                //Add initial date argument, which will just be 12 months before today
                urlStringBuilder.append(URLEncoder.encode("\" and startDate = \"", "UTF-8"));

                //Get our start and end dates
                Calendar c = Calendar.getInstance();
                Date now = c.getTime();
                c.add(Calendar.MONTH, -11);
                Date twelveMonthsAgo = c.getTime();

                //Append Date Parameters
                urlStringBuilder.append(getFormattedDate(twelveMonthsAgo));
                urlStringBuilder.append(URLEncoder.encode("\" and endDate = \"", "UTF-8"));
                urlStringBuilder.append(getFormattedDate(now));
                urlStringBuilder.append("\"&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables."
                        + "org%2Falltableswithkeys&callback=");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String getResponse;

            try{
                getResponse=fetchData(urlStringBuilder.toString());
                return parseJSONtoPoints(getResponse);

            } catch (IOException e){
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute (LineSet result){

            //Collections.reverse(Arrays.asList(result));
            if (result!=null){
                setGraph(result);


            }else{

                Toast.makeText(mContext, "Nothing to show", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private LineSet parseJSONtoPoints(String jsonResults){
        JSONArray resultsArray = null;
        LineSet set = new LineSet();

        int resultCount = 0;
        try{
            JSONObject jsonObject = new JSONObject(jsonResults);

            if (jsonObject.length()!= 0){
                jsonObject = jsonObject.getJSONObject("query");

                //Grab a count, make sure its more than one and return a JSONArray
                resultCount = Integer.parseInt(jsonObject.getString("count"));
                Log.e(LOG_TAG, String.valueOf(resultCount));
                if(resultCount>=1){
                    resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");
                }

            }

        }catch (JSONException e){
            Log.e(LOG_TAG, "String to JSON failed: " + e);
        }

        JSONObject point;
        Float close;// Price of stock when the markets close
        //If we got results, convert them to Strings and Ints, then Points for Graph use
        if(resultCount>0) {
            //for (int i = 0; i <= resultCount-1; i++) {
            for (int i = (resultCount -1); i >= 0; i -- ){
                try{
                    assert resultsArray != null;
                    point = resultsArray.getJSONObject(i);
                    close=Float.valueOf(point.getString("Open"));
                    //Introduce here the other information??

                    if(close>high){
                        high=close;
                    }

                    if(close<low){
                        low=close;
                    }
                    set.addPoint(point.getString("Date"), close);
                } catch(JSONException e){
                    Log.e(LOG_TAG, "Conversion of JSONARRAY to Object failed:" + e);
                }
            }

            return set;
        }

        return null;

    }

    private String getFormattedDate(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    private String fetchData(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    //source: https://github.com/diogobernardino/WilliamChart/wiki/(2)-Chart
    private void setGraph(LineSet result){

        //Collections.reverse(Arrays.asList(result));

        result.setColor(ContextCompat.getColor(this, R.color.accent_pink_clear));
        lineChartView.setContentDescription("Last Year of Stock Comparison");
        lineChartView.addData(result);
        lineChartView.setLabelsColor(Color.WHITE);
        lineChartView.setAxisColor(Color.WHITE);
        lineChartView.setXLabels(AxisController.LabelPosition.NONE);
        lineChartView.setStep(10);//Distance between labels
        lineChartView.setAxisBorderValues(Math.round(low), Math.round(high), Math.round(high) / Math.round(low)); // To define axis minimum and maximum values

        lineChartView.show();

    }


}