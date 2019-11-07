package ie.dbs.myplants;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;



public class ConnectionReceiver extends BroadcastReceiver{


    @Override
    public void onReceive(final Context context, Intent intent) {
        String action=intent.getAction();


        if (("android.net.conn.CONNECTIVITY_CHANGE").equals(action)) {
            final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            try {
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                if (networkInfo.isConnected()) {
                    Log.v("Internet", "There is internet");
                }
            } catch (Exception ex) {

                AlertDialog dialog=new AlertDialog.Builder(context).create();
                dialog.setTitle("OOPS");
                dialog.setMessage("No Internet connection, please connect to the internet");
                dialog.setIcon(R.drawable.no_internet_icon);
                dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();


            }
        }
    }

}

