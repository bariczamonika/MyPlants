package ie.dbs.myplants;

import android.content.Intent;
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
    String plantID;

    public PlantRecyclerAdapter(@NonNull View itemView) {
        super(itemView);

        txt_name=(TextView)itemView.findViewById(R.id.cardview_plant_name);
        img_avatar=(ImageView) itemView.findViewById(R.id.cardview_avatar);
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
