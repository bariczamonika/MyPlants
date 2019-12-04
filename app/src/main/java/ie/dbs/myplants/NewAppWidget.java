package ie.dbs.myplants;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


//implement app widget
public class NewAppWidget extends AppWidgetProvider {

    public static final String REFRESH_ACTION = "ie.dbs.myplants.REFRESH_ACTION";
    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        setRemoteAdapter(context, views);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            for(int i=0;i<Utils.myPlants.size();i++) {
                Utils.myPlants.set(i,Utils.autoChangeDatesOnceItIsReached(Utils.myPlants.get(i)));
                Utils.addPlant(Utils.myPlants.get(i));
            }
            SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(Utils.applicationContext);
            String lang=sharedPreferences.getString("language", "en");
            Log.v("widgetLang", lang);
            Utils.setLocale(lang);
            updateAppWidget(context, appWidgetManager, appWidgetId);
            RemoteViews views = new RemoteViews(
                    context.getPackageName(),
                    R.layout.new_app_widget);

            Intent intent = new Intent(context, Splash.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId    );
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget_click_on_me, pendingIntent);
            views.setTextViewText(R.id.widget_click_on_me, context.getResources().getString(R.string.widget_open_app));


            Intent intentServiceCall = new Intent(context, WidgetService.class);
            intentServiceCall.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);



            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private static void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.widget_list,
                new Intent(context, WidgetService.class));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(NewAppWidget.REFRESH_ACTION)) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                int[] appWidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);

                if (appWidgetIds != null && appWidgetIds.length > 0) {
                    Toast.makeText(context, "REFRESH", Toast.LENGTH_SHORT).show();
                    this.onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds);
                }
            }
        }
    }
}

