package com.udacity.stockhawk.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.QuoteColumns;
import com.udacity.stockhawk.data.QuoteProvider;

import java.util.ArrayList;

/**
 * StockHawk created by hendercine on 3/14/17.
 */

public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory{

    ArrayList<WidgetInformation> mcollection = new ArrayList<>();
    Cursor cursor;
    Context context;
    Intent intent;

    private void initData(){
        mcollection.clear();

        //Since the RemoteViewsFactory is being called from a remote process, and that context
        //is being used for permission enforcement I am having an error. To solve that i used
        //the code below. Source: http://stackoverflow.com/questions/13187284/android-permission-denial-in-widget-remoteviewsfactory-for-content

        final long token = Binder.clearCallingIdentity();
        try {
            cursor = context.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                    new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                            QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                    QuoteColumns.ISCURRENT + " = ?",
                    new String[]{"1"},
                    null);

            while (cursor.moveToNext()){
                mcollection.add(new WidgetInformation(cursor.getString(cursor.getColumnIndex(QuoteColumns.SYMBOL)),cursor.getString(cursor.getColumnIndex(QuoteColumns.BIDPRICE)),cursor.getString(cursor.getColumnIndex(QuoteColumns.CHANGE))));
            }

        } finally {
            Binder.restoreCallingIdentity(token);
        }

    }

    public WidgetDataProvider(Context context, Intent intent){
        this.context = context;
        this.intent = intent;

    }

    @Override
    public void onCreate() {
        initData();

    }

    @Override
    public void onDataSetChanged() {
        initData();

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mcollection.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_item); //I created a customized widget_item layout instead of using android simple list layout
        remoteView.setTextViewText(R.id.stock_symbol,mcollection.get(position).getSymbol());
        remoteView.setTextViewText(R.id.bid_price,mcollection.get(position).getBid());
        remoteView.setTextViewText(R.id.stock_change,mcollection.get(position).getChange());

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

