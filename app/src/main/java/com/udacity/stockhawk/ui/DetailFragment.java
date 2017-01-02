package com.udacity.stockhawk.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.stockhawk.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnStockSelectedListener} interface
 * to handle interaction events.
 */
public class DetailFragment extends Fragment {

    private OnStockSelectedListener mCallBack;
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    static final String DETAIL_URI = "URI";

    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        onButtonPressed(Uri.parse(DETAIL_URI));
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mCallBack != null) {
            mCallBack.onStockSelected(uri);
        }
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
