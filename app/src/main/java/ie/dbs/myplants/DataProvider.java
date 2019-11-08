package ie.dbs.myplants;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


class DataProvider implements RemoteViewsService.RemoteViewsFactory {
    final private List<String> plantNames = new ArrayList<>();
    final private List<String> plantTasks=new ArrayList<>();
    final private List<Boolean> plantTasksDone=new ArrayList<>();
    final private Context mContext;

    DataProvider(Context context, Intent intent) {
        mContext = context;
    }
    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return plantNames.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews view = new RemoteViews(mContext.getPackageName(),
                R.layout.widget_list_view_row);

        view.setTextViewText(R.id.widget_plant_name, plantNames.get(position));
        view.setTextViewText(R.id.widget_plant_task, plantTasks.get(position));
        if(plantTasksDone.get(position))
        {
            view.setTextViewText(R.id.widget_plant_task_done, "Done");
            view.setInt(R.id.widget_list_row_linear, "setBackgroundResource", R.color.colorPrimary);

        }
        else
        {
            view.setTextViewText(R.id.widget_plant_task_done, "TODO");
            view.setInt(R.id.widget_list_row_linear, "setBackgroundResource", R.color.colorWhite);

        }
     /*   Bundle extras = new Bundle();
        extras.putString("plantID", Utils.today_plants_task_string.get(position).getPlantID());
        extras.putString("task", plantTasks.get(position));
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        view.setOnClickFillInIntent(R.id.widget_list_row_linear,fillInIntent);*/
        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void initData() {
        plantNames.clear();
        plantTasks.clear();
        plantTasksDone.clear();
        if(Utils.today_plants_task_string.size()!=0) {
            for (int i = 0; i < Utils.today_plants_task_string.size(); i++) {
                Plant myPlant=Utils.today_plants_task_string.get(i);
                plantNames.add(myPlant.getName());

                final Date tomorrow = Utils.addOneSecondToDate(Utils.getLastMinuteOfDay(new Date()));
                Log.v("tomorrow", String.valueOf(tomorrow));
                final Date fertilizing_date = myPlant.getNextFertilizing();
                final Date watering_date = myPlant.getNextWatering();
                if (fertilizing_date != null && fertilizing_date.before(tomorrow)) {
                    plantTasks.add("fertilizing");
                    if (myPlant.isTaskFertilizinChecked())
                        plantTasksDone.add(true);
                    else
                        plantTasksDone.add(false);
            }
                if (watering_date != null && watering_date.before(tomorrow)) {
                    plantTasks.add("watering");
                    if (myPlant.isTaskWateringChecked())
                        plantTasksDone.add(true);
                    else
                        plantTasksDone.add(false);
                }

            }
        }
    }
}
