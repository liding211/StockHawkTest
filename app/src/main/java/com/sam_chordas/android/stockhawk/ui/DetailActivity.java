package com.sam_chordas.android.stockhawk.ui;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        TextView stockName = (TextView) findViewById(R.id.stock_symbol);
        TextView bidPrice = (TextView) findViewById(R.id.bid_price);
        TextView change = (TextView) findViewById(R.id.change);

        Cursor data = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[] {
                        QuoteColumns.SYMBOL,
                        QuoteColumns.BIDPRICE,
                        QuoteColumns.CHANGE
                },
                QuoteColumns.SYMBOL + " = ?",
                new String[] { getIntent().getStringExtra("symbol") },
                null);
        if (data == null) {
            return;
        }
        if (!data.moveToFirst()) {
            data.close();
            return;
        }

        // Extract the weather data from the Cursor
        String stockNameText = data.getString(data.getColumnIndex(QuoteColumns.SYMBOL));
        stockName.setText(stockNameText);
        stockName.setContentDescription(stockNameText);

        String bidPriceText = data.getString(data.getColumnIndex(QuoteColumns.BIDPRICE));
        bidPrice.setText(bidPriceText);
        bidPrice.setContentDescription(bidPriceText);

        String changeText = data.getString(data.getColumnIndex(QuoteColumns.CHANGE));
        change.setText(changeText);
        change.setContentDescription(changeText);
        data.close();
    }


}
