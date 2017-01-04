package com.udacity.stockhawk.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.udacity.stockhawk.R;

public class DetailActivity extends AppCompatActivity implements DetailFragment.OnStockSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Create new fragment and transaction
        DetailFragment mDetailFragment = new DetailFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        mDetailFragment.setArguments(getIntent().getExtras());
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.activity_detail_container, mDetailFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onStockSelected(Uri uri) {

    }
}
