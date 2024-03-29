package ie.dbs.myplants;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Date;

public class TomorrowTaskRecyclerAdapter extends RecyclerView.Adapter<TomorrowTaskRecyclerAdapter.ViewHolder> {
    final private ArrayList<Plant> task_plants;

    TomorrowTaskRecyclerAdapter(ArrayList<Plant> task_plants){this.task_plants=task_plants;}

    class ViewHolder extends RecyclerView.ViewHolder{
        final TextView task_plant_name;
        final TextView task_action;
        String plantID;
        final CheckBox task_check_box;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            task_plant_name=itemView.findViewById(R.id.cardview_task_plant_name);
            task_action=itemView.findViewById(R.id.cardview_task_action);
            task_check_box=itemView.findViewById(R.id.cardview_task_check_task);
            task_check_box.setVisibility(View.INVISIBLE);
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.task_plant_name.setText(task_plants.get(position).getName());
        Date day_after_tomorrow=Utils.addOneSecondToDate(Utils.addDaysToDate(Utils.getLastMinuteOfDay(new Date()),1));
        Date today=Utils.addOneSecondToDate(Utils.getLastMinuteOfDay(new Date()));
        Log.v("day after tomorrow", String.valueOf(day_after_tomorrow));
        Log.v("today", String.valueOf(today));

            Date fertilizing_date=task_plants.get(position).getNextFertilizing();
            Date watering_date=task_plants.get(position).getNextWatering();
            boolean isFertilizingDateTomorrow=fertilizing_date!=null&&fertilizing_date.before(day_after_tomorrow)&&fertilizing_date.after(today);
            boolean isWateringDateTomorrow=watering_date!=null&&watering_date.before(day_after_tomorrow) && watering_date.after(today);
            boolean isWateringDateTodayAndDaily=watering_date!=null && watering_date.before(today)&&task_plants.get(position).getWateringNeeds().value==1;
            if(isFertilizingDateTomorrow){
                holder.task_action.setText(R.string.task_fertilizing);
            }
            if((isWateringDateTomorrow) ||isWateringDateTodayAndDaily){
                holder.task_action.setText(R.string.task_watering);
            }
            holder.plantID=task_plants.get(position).getPlantID();
    }


    @Override
    public int getItemCount() {
        return task_plants.size();
    }

}
