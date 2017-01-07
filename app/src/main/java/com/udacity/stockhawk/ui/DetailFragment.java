package com.udacity.stockhawk.ui;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.io.IOException;

import butterknife.BindView;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnStockSelectedListener} interface
 * to handle interaction events.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private OnStockSelectedListener mCallBack;
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    static final String DETAIL_URI = "URI";
    private Uri mUri;
    private StockAdapter adapter;

    private static final int DETAIL_LOADER = 0;
    String[] DETAIL_COLUMNS = Contract.Quote.QUOTE_COLUMNS;

    @BindView(R.id.detail_stock_title)
    private TextView mStockTitleView;
    @BindView(R.id.detail_stock_symbol)
    private TextView mStockSymbolView;
    @BindView(R.id.volume)
    private TextView mVolumeView;
    private TextView mDetailPriceView;
    private TextView mDayHighView;
    private TextView mDayLowView;
    private TextView mYearHighView;
    private TextView mYearLowView;
    private TextView mDetailChangeView;
    private TextView mDetailChangePercentageView;

    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (mCallBack != null) {
            mCallBack.onStockSelected(Uri.parse(DETAIL_URI));
        }

        Bundle args = getArguments();
        if (args != null) {
            mUri = args.getParcelable(DetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mDetailPriceView = (TextView) rootView.findViewById(R.id.detail_price);
        mDayHighView = (TextView) rootView.findViewById(R.id.day_high);
        mDayLowView = (TextView) rootView.findViewById(R.id.day_low);
        mYearHighView = (TextView) rootView.findViewById(R.id.year_high);
        mYearLowView = (TextView) rootView.findViewById(R.id.year_low);
        mDetailChangeView = (TextView) rootView.findViewById(R.id.detail_change);
        mDetailChangePercentageView = (TextView) rootView.findViewById(R.id.detail_change_percentage);
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnStockSelectedListener) {
            mCallBack = (OnStockSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnStockSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallBack = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
//            int stockId = stock.getInt(Contract.Quote.POSITION_ID);

//            Stock stockTitle = null;
//            try {
//                stockTitle = YahooFinance.get(String.valueOf(data));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            assert stockTitle != null;
//            String stockTitleText = stockTitle.getName();
//            mStockTitleView.setText(stockTitleText);

            String stockSymbolText = data.getString(Contract.Quote.POSITION_SYMBOL);
            mStockSymbolView.setText(stockSymbolText);

            String stockPriceText = data.getString(Contract.Quote.POSITION_PRICE);
            mDetailPriceView.setText(stockPriceText);

            String stockChangeText = data.getString(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
            mDetailChangeView.setText(stockChangeText);

            String stockChangePercentageText = data.getString(Contract.Quote.POSITION_PERCENTAGE_CHANGE);
            mDetailChangePercentageView.setText(stockChangePercentageText);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnStockSelectedListener {
        void onStockSelected(Uri uri);
    }
}
