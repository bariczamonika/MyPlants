package ie.dbs.myplants;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PlantRecyclerAdapter extends RecyclerView.ViewHolder {
    TextView txt_name;
    ImageView img_avatar;

    public PlantRecyclerAdapter(@NonNull View itemView) {
        super(itemView);

        txt_name=(TextView)itemView.findViewById(R.id.cardview_plant_name);
        img_avatar=(ImageView) itemView.findViewById(R.id.cardview_avatar);
    }
}
