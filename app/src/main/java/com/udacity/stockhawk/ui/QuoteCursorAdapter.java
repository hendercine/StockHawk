package com.udacity.stockhawk.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.QuoteColumns;
import com.udacity.stockhawk.data.QuoteProvider;
import com.udacity.stockhawk.sync.Utilities;
import com.udacity.stockhawk.touch_helper.ItemTouchHelperAdapter;
import com.udacity.stockhawk.touch_helper.ItemTouchHelperViewHolder;


/**
 * StockHawk created by hendercine on 3/14/17.
 * Credit to skyfishjy gist:
 *    https://gist.github.com/skyfishjy/443b7448f59be978bc59
 * for the code structure
 */

public class QuoteCursorAdapter extends CursorRecyclerViewAdapter<QuoteCursorAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {

    private static Context mContext;
    private static Typeface robotoLight;
    private boolean isPercent;
    public QuoteCursorAdapter(Context context, Cursor cursor){
        super(context, cursor);
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        robotoLight = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Light.ttf");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_quote, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final Cursor cursor){
        viewHolder.symbol.setText(cursor.getString(cursor.getColumnIndex("symbol")));
        viewHolder.bidPrice.setText(cursor.getString(cursor.getColumnIndex("bid_price")));
        int sdk = Build.VERSION.SDK_INT;
        if (cursor.getInt(cursor.getColumnIndex("is_up")) == 1){
            if (sdk < Build.VERSION_CODES.JELLY_BEAN){
                viewHolder.change.setBackgroundDrawable(
                        mContext.getResources().getDrawable(R.drawable.percent_change_pill_green));
            }else {
                viewHolder.change.setBackground(
                        mContext.getResources().getDrawable(R.drawable.percent_change_pill_green));
            }
        } else{
            if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                viewHolder.change.setBackgroundDrawable(
                        mContext.getResources().getDrawable(R.drawable.percent_change_pill_red));
            } else{
                viewHolder.change.setBackground(
                        mContext.getResources().getDrawable(R.drawable.percent_change_pill_red));
            }
        }
        if (Utilities.showPercent){
            viewHolder.change.setText(cursor.getString(cursor.getColumnIndex("percent_change")));
        } else{
            viewHolder.change.setText(cursor.getString(cursor.getColumnIndex("change")));
        }
    }

    @Override public void onItemDismiss(int position) {
        Cursor c = getCursor();
        c.moveToPosition(position);
        String symbol = c.getString(c.getColumnIndex(QuoteColumns.SYMBOL));
        mContext.getContentResolver().delete(QuoteProvider.Quotes.withSymbol(symbol), null, null);
        notifyItemRemoved(position);
    }

    @Override public int getItemCount() {
        return super.getItemCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements ItemTouchHelperViewHolder, View.OnClickListener{
        public final TextView symbol;
        public final TextView bidPrice;
        public final TextView change;
        public ViewHolder(View itemView){
            super(itemView);
            symbol = (TextView) itemView.findViewById(R.id.stock_symbol);
            symbol.setTypeface(robotoLight);
            bidPrice = (TextView) itemView.findViewById(R.id.bid_price);
            change = (TextView) itemView.findViewById(R.id.change);
        }

        @Override
        public void onItemSelected(){
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear(){
            itemView.setBackgroundColor(0);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public String getSymbol(int position){
        Cursor cursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns.SYMBOL},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);
        assert cursor != null;
        cursor.moveToPosition(position);
        String symbol = cursor.getString(cursor.getColumnIndex(QuoteColumns.SYMBOL));
        cursor.close();
        return symbol;

    }
    public String getName(int position){
        Cursor cursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns.NAME},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);
        assert cursor != null;
        cursor.moveToPosition(position);
        String name = cursor.getString(cursor.getColumnIndex(QuoteColumns.NAME));
        cursor.close();
        return name;

    }
    public String getCurrency(int position){
        Cursor cursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns.CURRENCY},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);
        assert cursor != null;
        cursor.moveToPosition(position);
        String currency = cursor.getString(cursor.getColumnIndex(QuoteColumns.CURRENCY));
        cursor.close();
        return currency;

    }

    public String getDayLow(int position){
        Cursor cursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns.DAYLOW},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);
        assert cursor != null;
        cursor.moveToPosition(position);
        String daylow = cursor.getString(cursor.getColumnIndex(QuoteColumns.DAYLOW));
        cursor.close();
        return daylow;
    }

    public String getDayHigh(int position){
        Cursor cursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns.DAYHIGH},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);
        assert cursor != null;
        cursor.moveToPosition(position);
        String dayhigh = cursor.getString(cursor.getColumnIndex(QuoteColumns.DAYHIGH));
        cursor.close();
        return dayhigh;

    }
    public String getYearLow(int position){
        Cursor cursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns.YEARLOW},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);
        assert cursor != null;
        cursor.moveToPosition(position);
        String yearlow = cursor.getString(cursor.getColumnIndex(QuoteColumns.YEARLOW));
        cursor.close();
        return yearlow;

    }

    public String getYearHigh(int position){
        Cursor cursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns.YEARHIGH},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);
        assert cursor != null;
        cursor.moveToPosition(position);
        String yearhigh = cursor.getString(cursor.getColumnIndex(QuoteColumns.YEARHIGH));
        cursor.close();
        return yearhigh;

    }

    public String getEarnings(int position){
        Cursor cursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns.EARNINGSSHARE},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);
        assert cursor != null;
        cursor.moveToPosition(position);
        String earnings = cursor.getString(cursor.getColumnIndex(QuoteColumns.EARNINGSSHARE));
        cursor.close();
        return earnings;

    }


}
