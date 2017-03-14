package com.udacity.stockhawk.ui;


import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

class DetailStockAdapter extends RecyclerView.Adapter<DetailStockAdapter.DetailStockViewHolder> {

    final private Context context;
    final private DecimalFormat dollarFormatWithPlus;
    final private DecimalFormat dollarFormat;
    final private DecimalFormat percentageFormat;
    private Cursor cursor;
    private DetailStockAdapterOnClickHandler clickHandler;

    DetailStockAdapter(Context context, DetailStockAdapterOnClickHandler clickHandler) {
        this.context = context;
        this.clickHandler = clickHandler;

        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");
        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");
    }

    void setCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    String getSymbolAtPosition(int position) {

        cursor.moveToPosition(position);
        return cursor.getString(Contract.Quote.POSITION_SYMBOL);
    }

    @Override
    public DetailStockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View item = LayoutInflater.from(context).inflate(R.layout.fragment_detail, parent, false);

        return new DetailStockViewHolder(item);
    }

    @Override
    public void onBindViewHolder(DetailStockViewHolder holder, int position) {

        cursor.moveToPosition(position);

        float rawAbsoluteChange = cursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
        float percentageChange = cursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

        holder.mStockSymbolView.setText(cursor.getString(Contract.Quote.POSITION_SYMBOL));
        holder.mDetailPrice.setText(dollarFormat.format(cursor.getFloat(Contract.Quote.POSITION_PRICE)));
        holder.mDetailChangeView.setText((int) rawAbsoluteChange);
        holder.mDetailChangePercentageView.setText((int) percentageChange);

    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (cursor != null) {
            count = cursor.getCount();
        }
        return count;
    }


    interface DetailStockAdapterOnClickHandler {
        void onClick(String symbol);
    }

    class DetailStockViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.detail_stock_title)
        TextView mStockTitleView;
        @BindView(R.id.detail_stock_symbol)
        TextView mStockSymbolView;
        @BindView(R.id.volume)
        TextView mVolumeView;
        @BindView(R.id.detail_price)
        TextView mDetailPrice;
        @BindView(R.id.day_high)
        TextView mDayHighView;
        @BindView(R.id.day_low)
        TextView mDayLowView;
        @BindView(R.id.year_high)
        TextView mYearHighView;
        @BindView(R.id.year_low)
        TextView mYearLowView;
        @BindView(R.id.detail_change)
        TextView mDetailChangeView;
        @BindView(R.id.detail_change_percentage)
        TextView mDetailChangePercentageView;

        DetailStockViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            cursor.moveToPosition(adapterPosition);
            int symbolColumn = cursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL);
            clickHandler.onClick(cursor.getString(symbolColumn));

        }


    }
}
