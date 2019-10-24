package ie.dbs.myplants;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    ArrayList<PlantImage> plantImages;
    String plantID;


    public GalleryAdapter (ArrayList<PlantImage> plantImages, String plantID)
    {
        this.plantImages=plantImages;
        this.plantID=plantID;
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView date_added;
        ImageView picture;
        int position;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            date_added=itemView.findViewById(R.id.gallery_date_added);
            picture=itemView.findViewById(R.id.gallery_image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Utils.applicationContext, ImageDialog.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("plantID",plantID);
                    intent.putExtra("position", position);
                    Utils.applicationContext.startActivity(intent);
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_plant_gallery, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.date_added.setText(Utils.getPictureDateFromPicturePath(plantImages.get(position).getPicturePath()));
        holder.picture.setImageBitmap(Utils.getThumbNailFromFile(plantImages.get(position).getPicturePath()));
        holder.position=position;

    }


    @Override
    public int getItemCount() {
        return plantImages.size();
    }
}
