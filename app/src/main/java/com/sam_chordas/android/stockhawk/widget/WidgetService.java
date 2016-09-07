package com.sam_chordas.android.stockhawk.widget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.ui.DetailActivity;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

public class WidgetService extends IntentService {

    public WidgetService() {
        super("WidgetService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                StockWidgetProvider.class));

        Cursor data = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
            new String[] {
                "Distinct " + QuoteColumns.SYMBOL,
                QuoteColumns.BIDPRICE,
                QuoteColumns.CHANGE
            },
            null,
            null,
            QuoteColumns._ID + " DESC");
        if (data == null) {
            return;
        }
        if (!data.moveToFirst()) {
            data.close();
            return;
        }

        // Extract the weather data from the Cursor
        String stock = data.getString(data.getColumnIndex(QuoteColumns.SYMBOL));
        String bid = data.getString(data.getColumnIndex(QuoteColumns.BIDPRICE));
        String bid_change = data.getString(data.getColumnIndex(QuoteColumns.CHANGE));
        data.close();

        // Perform this loop procedure for each Today widget
        for (int appWidgetId : appWidgetIds) {

            int layoutId = R.layout.widget_stock;
            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            // Content Descriptions for RemoteViews were only added in ICS MR1
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                setRemoteContentDescription(views, stock);
            }
            views.setTextViewText(R.id.stock_name, stock);
            views.setTextViewText(R.id.stock_bid, bid);
            views.setTextViewText(R.id.stock_bid_change, bid_change);

            views.setContentDescription(R.id.stock_name, stock);
            views.setContentDescription(R.id.stock_bid, bid);
            views.setContentDescription(R.id.stock_bid_change, bid_change);

            Intent launchIntent = new Intent(this, DetailActivity.class);
            launchIntent.putExtra("symbol", stock);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.stock_name, description);
    }
}
