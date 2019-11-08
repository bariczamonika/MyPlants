package ie.dbs.myplants;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WeatherForecastRecyclerAdapter extends RecyclerView.Adapter<WeatherForecastRecyclerAdapter.ViewHolder> {
    final private ArrayList<WeatherInfo> weatherInfos;

    public WeatherForecastRecyclerAdapter(ArrayList<WeatherInfo> weatherInfos){
        this.weatherInfos=weatherInfos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_forecast_card_view,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        DecimalFormat decimalFormat=new DecimalFormat("#.##");
        final WeatherInfo weatherInfo=weatherInfos.get(position);
        holder.weather_date.setText(weatherInfo.getDateTime());
        holder.weather_full_desc.setText(weatherInfo.getDetailedDescription());
        holder.weather_brief_desc.setText(weatherInfo.getBriefDescription());
        holder.current_temp.setText(holder.itemView.getResources().getString(R.string.celsius,decimalFormat.format( weatherInfo.getCurrentTemp())));
        holder.min_temp.setText(holder.itemView.getResources().getString(R.string.celsius,decimalFormat.format( weatherInfo.getCurrentMinTemp())));
        holder.max_temp.setText(holder.itemView.getResources().getString(R.string.celsius,decimalFormat.format( weatherInfo.getCurrentMaxTemp())));
        holder.wind_speed.setText(holder.itemView.getResources().getString(R.string.kmh,decimalFormat.format( weatherInfo.getCurrentWindSpeed())));

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            final URL url = new URL(Utils.applicationContext.getResources().
                    getString(R.string.weather_icon_url,weatherInfo.getWeatherIcon()));
            Log.v("weatherIcon", url.toString());
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        //database.advertsDAO().removeAllAdverts();
                        Bitmap avatar = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        holder.weather_icon.setImageBitmap(avatar);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }catch(Exception e)
        {
            e.printStackTrace();
            Log.v("SOME TAG", e.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return weatherInfos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final private TextView weather_date;
        final private TextView weather_brief_desc;
        final private TextView weather_full_desc;
        final private ImageView weather_icon;
        final private TextView current_temp;
        final private TextView max_temp;
        final private TextView min_temp;
        final private TextView wind_speed;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            weather_date=itemView.findViewById(R.id.card_view_weather_date);
            weather_brief_desc=itemView.findViewById(R.id.card_view_weather_brief_desc);
            weather_full_desc=itemView.findViewById(R.id.card_view_weather_full_desc);
            weather_icon=itemView.findViewById(R.id.card_view_weather_icon);
            current_temp=itemView.findViewById(R.id.card_view_weather_current_temp);
            max_temp=itemView.findViewById(R.id.card_view_weather_current_max);
            min_temp=itemView.findViewById(R.id.card_view_weather_current_min);
            wind_speed=itemView.findViewById(R.id.card_view_weather_current_wind_speed);
        }
    }
}
