package ie.dbs.myplants;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class TaskRecyclerAdapter extends RecyclerView.Adapter<TaskRecyclerAdapter.ViewHolder> {
    ArrayList<Plant> task_plants;

    public TaskRecyclerAdapter(ArrayList<Plant>task_plants){this.task_plants=task_plants;}

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView task_plant_name;
        TextView task_action;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            task_plant_name=itemView.findViewById(R.id.cardview_task_plant_name);
            task_action=itemView.findViewById(R.id.cardview_task_action);
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
        //TODO put the filter in activity
        holder.task_plant_name.setText(task_plants.get(position).getName());
        Date tomorrow=Utils.addOneSecondToDate(Utils.getLastMinuteOfDay(new Date()));
        Log.v("tomorrow", String.valueOf(tomorrow));
            Date fertilizing_date=task_plants.get(position).getNextFertilizing();
            Date watering_date=task_plants.get(position).getNextWatering();
            if(fertilizing_date!=null&&fertilizing_date.before(tomorrow)){
                holder.task_action.setText(R.string.task_fertilizing);
            }
            if(watering_date!=null&&watering_date.before(tomorrow)){
                holder.task_action.setText(R.string.task_watering);
            }
    }

    @Override
    public int getItemCount() {
        return task_plants.size();
    }

}
