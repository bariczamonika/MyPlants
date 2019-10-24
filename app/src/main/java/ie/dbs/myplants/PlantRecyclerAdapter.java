package ie.dbs.myplants;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;



public class PlantRecyclerAdapter extends RecyclerView.Adapter<PlantRecyclerAdapter.myViewHolder> {
    ArrayList<Plant> myPlants;

    public PlantRecyclerAdapter(ArrayList<Plant> myPlants){
        this.myPlants=myPlants;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.all_plants, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.plantName.setText(myPlants.get(position).getName());
        String myPath = myPlants.get(position).getProfilePicPath();
        Bitmap bitmap=Utils.getThumbNailFromFile(myPath);
        if(bitmap.getWidth()>bitmap.getHeight())
            bitmap=Bitmap.createBitmap(bitmap, 0,0,bitmap.getHeight(), bitmap.getHeight());
        else
            bitmap=Bitmap.createBitmap(bitmap, 0,0,bitmap.getWidth(), bitmap.getWidth());
        holder.plantImg.setImageBitmap(bitmap);
        holder.plantID=myPlants.get(position).getPlantID();

    }

    @Override
    public int getItemCount() {
        return myPlants.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        TextView plantName;
        ImageView plantImg;
        String plantID;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            plantName=itemView.findViewById(R.id.cardview_plant_name);
            plantImg=itemView.findViewById(R.id.cardview_avatar);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Utils.applicationContext, SinglePlant.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("plantID", plantID);
                    Utils.applicationContext.startActivity(intent);

                }
            });

        }
    }
}