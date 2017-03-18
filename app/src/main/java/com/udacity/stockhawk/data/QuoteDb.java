package com.udacity.stockhawk.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * StockHawk created by hendercine on 3/14/17.
 */

@Database(version = QuoteDb.VERSION)
public class QuoteDb {
    private QuoteDb(){}

    public static final int VERSION = 8;

    @Table(QuoteColumns.class) public static final String QUOTES = "quotes";
}
