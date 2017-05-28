package com.udacity.stockhawk.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;

/**
 * Created by mupac_000 on 27-05-17.
 */

public class StockAppWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for(int id : appWidgetIds)
        {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.stock_appwidget);

            Intent i = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,i, 0);
            views.setOnClickPendingIntent(R.id.widget_ok,pendingIntent);

            //perform update
            appWidgetManager.updateAppWidget(id,views);
        }
    }
}
