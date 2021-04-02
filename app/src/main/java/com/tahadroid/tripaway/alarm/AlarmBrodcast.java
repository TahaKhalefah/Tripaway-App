package com.tahadroid.tripaway.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import com.tahadroid.tripaway.models.Trip;

public class AlarmBrodcast extends BroadcastReceiver {


    private Context context;
    private int code;
    String [] split;
    String action;

    //recieve from other applications
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() != ""){
            this.context = context;
            action = intent.getAction();

            split = action.split("-");
            displayAlert();
        }
    }

    private void displayAlert() {


        // Check for Permissions then start The Widget Service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);

        } else {

            Intent startIntent = new Intent(context, AlarmService.class);


            startIntent.putExtra("event",split[0]);
            startIntent.putExtra("lat",split[1]);
            startIntent.putExtra("lon",split[2]);

            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startIntent.setAction(action);

            AlarmService.enqueueWork(context, startIntent);
        }
    }

}
