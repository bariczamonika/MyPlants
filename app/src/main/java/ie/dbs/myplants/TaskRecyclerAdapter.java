package ie.dbs.myplants;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import java.util.Date;


public class TaskRecyclerAdapter extends RecyclerView.Adapter<TaskRecyclerAdapter.ViewHolder> {
    final private ArrayList<Plant> task_plants;


    public TaskRecyclerAdapter(ArrayList<Plant>task_plants){this.task_plants=task_plants;}

    class ViewHolder extends RecyclerView.ViewHolder{
        final TextView task_plant_name;
        final TextView task_action;
        final CheckBox task_check_box;
        String plantID;
        private ViewHolder(@NonNull final View itemView) {
            super(itemView);
            task_plant_name=itemView.findViewById(R.id.cardview_task_plant_name);
            task_action=itemView.findViewById(R.id.cardview_task_action);
            task_check_box=itemView.findViewById(R.id.cardview_task_check_task);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Utils.applicationContext, SinglePlant.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("plantID", plantID);
                    Utils.applicationContext.startActivity(intent);
                }
            });

        }
    }
    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Plant myPlant=task_plants.get(position);
        holder.task_plant_name.setText(task_plants.get(position).getName());
        final Date tomorrow=Utils.addOneSecondToDate(Utils.getLastMinuteOfDay(new Date()));
        Log.v("tomorrow", String.valueOf(tomorrow));
            final Date fertilizing_date=task_plants.get(position).getNextFertilizing();
            final Date watering_date=task_plants.get(position).getNextWatering();
            if(fertilizing_date!=null&&fertilizing_date.before(tomorrow)){
                holder.task_action.setText(R.string.task_fertilizing);
                if(myPlant.isTaskFertilizinChecked())
                    holder.task_check_box.setChecked(true);
                else
                    holder.task_check_box.setChecked(false);
            }
            if(watering_date!=null&&watering_date.before(tomorrow)){
                holder.task_action.setText(R.string.task_watering);
                if(myPlant.isTaskWateringChecked())
                    holder.task_check_box.setChecked(true);
                else
                    holder.task_check_box.setChecked(false);
            }
            holder.plantID=task_plants.get(position).getPlantID();


        holder.task_check_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)
                {

                    if(watering_date!=null&&watering_date.before(tomorrow)){
                        myPlant.setTaskWateringChecked(true);
                    }
                    else if(fertilizing_date!=null&&fertilizing_date.before(tomorrow)){
                        myPlant.setTaskFertilizinChecked(true);
                }
                }
                else{
                    if(watering_date!=null&&watering_date.before(tomorrow)){
                        myPlant.setTaskWateringChecked(false);

                    }
                    else if(fertilizing_date!=null&&fertilizing_date.before(tomorrow)){
                        myPlant.setTaskFertilizinChecked(false);
                    }
                }
                Utils.addPlant(myPlant);
                for (int i=0;i<Utils.today_plants_task_string.size();i++)
                {
                    if(Utils.today_plants_task_string.get(i).getPlantID().equals(myPlant.getPlantID()))
                        Utils.today_plants_task_string.set(i, myPlant);
                }
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(Utils.applicationContext);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(Utils.applicationContext, NewAppWidget.class));
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
            }
        });

    }

    @Override
    public int getItemCount() {
        return task_plants.size();
    }

}
