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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.sync.QuoteSyncJob;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static android.R.attr.data;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnStockSelectedListener} interface
 * to handle interaction events.
 */
public class DetailFragment extends Fragment implements LoaderManager
        .LoaderCallbacks<Cursor>,
        DetailStockAdapter.DetailStockAdapterOnClickHandler {

    private static final int STOCK_LOADER = 0;
    private OnStockSelectedListener mCallBack;
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    static final String DETAIL_URI = "URI";
    private Uri mUri;
    private Cursor cursor;
//    private DetailStockAdapter.DetailStockAdapterOnClickHandler clickHandler = null;
//    DetailStockAdapter adapter = new DetailStockAdapter(getContext(), clickHandler);

    private static final int DETAIL_LOADER = 0;
    String[] DETAIL_COLUMNS = Contract.Quote.QUOTE_COLUMNS;

//    @BindView(R.id.detail_card_view)
//    RecyclerView mDetailRecyclerView;
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

//        mDetailRecyclerView.setAdapter(adapter);
//        mDetailRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(rootView);

//        QuoteSyncJob.initialize(getContext());
//        getLoaderManager().initLoader(STOCK_LOADER, null, this);
//
//        String stockSymbolText = Contract.Quote.COLUMN_SYMBOL;
//        mStockSymbolView.setText(stockSymbolText);
//
//        String stockPriceText = Contract.Quote.COLUMN_PRICE;
//        mDetailPrice.setText(stockPriceText);
//
//        String stockChangeText = Contract.Quote.COLUMN_ABSOLUTE_CHANGE;
//        mDetailChangeView.setText(stockChangeText);
//
//        String stockChangePercentageText = Contract.Quote.COLUMN_PERCENTAGE_CHANGE;
//        mDetailChangePercentageView.setText(stockChangePercentageText);

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
        QuoteSyncJob.initialize(getContext());
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
                    Contract.Quote.COLUMN_SYMBOL
            );
        }
        return null;
    }

    void setCursor(Cursor cursor) {
        this.cursor = cursor;
        notify();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
//            int stockId = stock.getInt(Contract.Quote.POSITION_ID);
//
//            Stock stockTitle = null;
//            try {
//                stockTitle = YahooFinance.get(String.valueOf(data));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            assert stockTitle != null;
//            String stockTitleText = stockTitle.getName();
//            mStockTitleView.setText(stockTitleText);

            QuoteSyncJob.syncImmediately(getContext());

//            String stockSymbolText = data.getString(Contract.Quote.POSITION_SYMBOL);
//            mStockSymbolView.setText(stockSymbolText);
//
//            String stockPriceText = data.getString(Contract.Quote.POSITION_PRICE);
//            mDetailPrice.setText(stockPriceText);
//
//            String stockChangeText = data.getString(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
//            mDetailChangeView.setText(stockChangeText);
//
//            String stockChangePercentageText = data.getString(Contract.Quote.POSITION_PERCENTAGE_CHANGE);
//            mDetailChangePercentageView.setText(stockChangePercentageText);
        }

        setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        setCursor(null);

    }

    @Override
    public void onClick(String symbol) {
        Timber.d("Symbol clicked: %s", symbol);
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
