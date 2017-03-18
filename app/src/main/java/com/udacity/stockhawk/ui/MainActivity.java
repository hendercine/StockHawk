package com.udacity.stockhawk.ui;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.stetho.Stetho;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.QuoteColumns;
import com.udacity.stockhawk.data.QuoteProvider;
import com.udacity.stockhawk.sync.StockIntentService;
import com.udacity.stockhawk.sync.StockTaskService;
import com.udacity.stockhawk.sync.Utilities;
import com.udacity.stockhawk.touch_helper.SimpleItemTouchHelperCallback;
import com.udacity.stockhawk.widget.CollectionWidget;

public class MainActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor>{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private Intent mServiceIntent;
    private ItemTouchHelper mItemTouchHelper;
    private static final int CURSOR_LOADER_ID = 0;
    private QuoteCursorAdapter mCursorAdapter;
    private Context mContext;
    private Cursor mCursor;
    boolean isConnected;

    public String userText;
    //public static int stockStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        setContentView(R.layout.activity_main);


        // The intent service is for executing immediate pulls from the Yahoo API
        // GCMTaskService can only schedule tasks, they cannot execute immediately
        mServiceIntent = new Intent(this, StockIntentService.class);
        if (savedInstanceState == null){
            // Run the initialize task service so that some stocks appear upon an empty database
            mServiceIntent.putExtra("tag", "init");
            if (isConnected){
                startService(mServiceIntent);
                //Toast.makeText(mContext, getString(R.string.statusOK), Toast.LENGTH_SHORT).show();

            } else{
                networkToast();
            }
        }
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

        mCursorAdapter = new QuoteCursorAdapter(this, null);
        recyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(this,
                new RecyclerViewItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View v, int position) {
                        // do something on item click
                        Intent intent = new Intent(MainActivity.this, StockDetails.class);
                        intent.putExtra(QuoteColumns.SYMBOL,mCursorAdapter.getSymbol(position));
                        intent.putExtra(QuoteColumns.NAME,mCursorAdapter.getName(position));
                        intent.putExtra(QuoteColumns.CURRENCY,mCursorAdapter.getCurrency(position));
                        intent.putExtra(QuoteColumns.DAYHIGH,mCursorAdapter.getDayHigh(position));
                        intent.putExtra(QuoteColumns.DAYLOW,mCursorAdapter.getDayLow(position));
                        intent.putExtra(QuoteColumns.YEARHIGH,mCursorAdapter.getYearHigh(position));
                        intent.putExtra(QuoteColumns.YEARLOW,mCursorAdapter.getYearLow(position));
                        intent.putExtra(QuoteColumns.EARNINGSSHARE,mCursorAdapter.getEarnings(position));

                        startActivity(intent);
                    }
                }));
        recyclerView.setAdapter(mCursorAdapter);


        com.melnykov.fab.FloatingActionButton fab = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.fab);
        fab.attachToRecyclerView(recyclerView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (isConnected){
                    new MaterialDialog.Builder(mContext).title(R.string.symbol_search)
                            .content(R.string.content_test)
                            .inputType(InputType.TYPE_CLASS_TEXT)
                            .input(R.string.input_hint, R.string.input_prefill, new MaterialDialog.InputCallback() {
                                @Override public void onInput(MaterialDialog dialog, CharSequence input) {
                                    // On FAB click, receive user input. Make sure the stock doesn't already exist
                                    // in the DB and proceed accordingly

                                    //Make the stock symbols capital letter
                                    String newStockSymbol = input.toString().toUpperCase();

                                    Cursor c = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                                            new String[] { QuoteColumns.SYMBOL }, QuoteColumns.SYMBOL + "= ?",
                                            new String[] { newStockSymbol}, null);

                                    if (c.getCount() != 0) {
                                        Toast toast =
                                                Toast.makeText(MainActivity.this, "This stock is already saved!",
                                                        Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, Gravity.CENTER, 0);
                                        toast.show();
                                        return;
                                    } else {
                                        // Add the stock to DB
                                        mServiceIntent.putExtra("tag", "add");
                                        mServiceIntent.putExtra("symbol", input.toString());
                                        startService(mServiceIntent);
                                    }
                                }
                            })
                            .show();
                } else {
                    networkToast();
                }

            }
        });

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mCursorAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        mTitle = getTitle();
        if (isConnected){
            long period = 3600L;
            long flex = 10L;
            String periodicTag = "periodic";

            // create a periodic task to pull stocks once every hour after the app has been opened. This
            // is so Widget data stays up to date.
            PeriodicTask periodicTask = new PeriodicTask.Builder()
                    .setService(StockTaskService.class)
                    .setPeriod(period)
                    .setFlex(flex)
                    .setTag(periodicTag)
                    .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                    .setRequiresCharging(false)
                    .build();
            // Schedule task with tag "periodic." This ensure that only the stocks present in the DB
            // are updated.
            GcmNetworkManager.getInstance(this).schedule(periodicTask);
        }


        // Stetho is a tool created by facebook to view your database in chrome inspect.
        // The code below integrates Stetho into your app. More information here:
        // http://facebook.github.io/stetho/
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());

        ///////////////
    }



    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
        //networkToast();

    }

    public void networkToast(){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        @StockTaskService.StockStatus int stockStatus = prefs.getInt(mContext.getString(R.string.stockStatus),-1);


        switch (stockStatus){

            case StockTaskService.STATUS_OK:
                userText = getString(R.string.statusOK);

                break;

            case StockTaskService.STATUS_ERROR_JSON:
                userText = getString(R.string.ErrorJson);

                break;

            case StockTaskService.STATUS_SERVER_DOWN:
                userText = getString(R.string.ServerDown);

                break;

            case StockTaskService.STATUS_SERVER_ERROR:
                userText = getString(R.string.ErrorServer);

                break;

            case StockTaskService.STATUS_UNKNOWN:
                userText = getString(R.string.StatusUnknown);

                break;
            default:
                userText = getString(R.string.statusNoNetwork);
                break;
        }

        Toast.makeText(mContext, userText, Toast.LENGTH_SHORT).show();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_settings, menu);
        restoreActionBar();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_change_units:
                Utilities.showPercent = !Utilities.showPercent;
                this.getContentResolver().notifyChange(QuoteProvider.Quotes.CONTENT_URI, null);
                break;
            default:
                break;
        }
        return false;

    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args){
        // This narrows the return to only the stocks that are most current.
        return new android.content.CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                new String[]{ QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data){
        mCursorAdapter.swapCursor(data);
        mCursor = data;

        networkToast();

        //The widget will be updated whenever a new item is added to the database
        updateStocksWidget();
    }

    //Since we have a widget backed by a collection (database), we need to implement the code below
    //to update the widget. Source: http://stackoverflow.com/questions/5355022/app-widget-getting-content-from-database-update-issues

    private void updateStocksWidget(){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext.getApplicationContext());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, CollectionWidget.class));

        if (appWidgetIds.length > 0){
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds,R.id.appwidget_lv);
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader){
        mCursorAdapter.swapCursor(null);

    }

    @Override
    protected void onDestroy(){
        if (mCursor != null)
            mCursor.close();
        super.onDestroy();
    }
}