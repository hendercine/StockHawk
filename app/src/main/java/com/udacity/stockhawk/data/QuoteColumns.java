package com.udacity.stockhawk.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * StockHawk created by hendercine on 3/14/17.
 */

public class QuoteColumns {
    @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement
    public static final String _ID = "_id";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String SYMBOL = "symbol";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String PERCENT_CHANGE = "percent_change";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String CHANGE = "change";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String BIDPRICE = "bid_price";
    @DataType(DataType.Type.TEXT)
    public static final String CREATED = "created";
    @DataType(DataType.Type.INTEGER) @NotNull
    public static final String ISUP = "is_up";
    @DataType(DataType.Type.INTEGER) @NotNull
    public static final String ISCURRENT = "is_current";
    @DataType(DataType.Type.TEXT)@NotNull
    public static final String NAME = "name";
    @DataType(DataType.Type.TEXT)@NotNull
    public static final String CURRENCY = "currency";
    @DataType(DataType.Type.TEXT)@NotNull
    public static final String DAYLOW = "daylow";
    @DataType(DataType.Type.TEXT)@NotNull
    public static final String DAYHIGH = "dayhigh";
    @DataType(DataType.Type.TEXT)@NotNull
    public static final String YEARLOW = "yearlow";
    @DataType(DataType.Type.TEXT)@NotNull
    public static final String YEARHIGH = "yearhigh";
    @DataType(DataType.Type.TEXT)@NotNull
    public static final String EARNINGSSHARE = "earningsshare";
}
