package com.example.greent.petals;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

import com.example.greent.petals.data.ProductDBContract;
import com.example.greent.petals.ui.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.Random;

/**
 * Implementation of App Widget functionality.
 */
public class PetalsWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, int[] appWidgetIds) {

        //Create an Intent to launch the Petals app when the widget is tapped
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.petals_widget);
        //views.setTextViewText(R.id.widget_title, widgetText);

        views.setOnClickPendingIntent(R.id.widget_container,pendingIntent);

        //Get access to the app's content resolver
        ContentResolver resolver = context.getContentResolver();

        Cursor data = resolver.query(ProductDBContract.ProductEntry.CONTENT_URI,null,null,null,null);

        if (data.moveToFirst()) {
            //We have data, Yay!
            //Get a relative Random number
            Random r = new Random();
            int offset = r.nextInt(data.getCount());
            data.move(offset);

            //This is our random item of the day. Extract the item image.
            String itemOfTheDayImg = data.getString(data.getColumnIndex(ProductDBContract.ProductEntry.COLUMN_NAME_IMG_LOC_LRG));

            //Use Picasso to load up the image
            Picasso.with(context)
                    .load(itemOfTheDayImg)
                    .into(views, R.id.widget_product_image, appWidgetIds);

            data.close();

        }


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, appWidgetIds);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

