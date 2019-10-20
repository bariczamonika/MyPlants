package ie.dbs.myplants;

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

    public GalleryAdapter (ArrayList<PlantImage> plantImages)
    {
        this.plantImages=plantImages;
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView date_added;
        ImageView picture;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date_added=itemView.findViewById(R.id.gallery_date_added);
            picture=itemView.findViewById(R.id.gallery_image);
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
        holder.picture.setImageBitmap(Utils.getImageFromFile(plantImages.get(position).getPicturePath()));

    }


    @Override
    public int getItemCount() {
        return plantImages.size();
    }
}
