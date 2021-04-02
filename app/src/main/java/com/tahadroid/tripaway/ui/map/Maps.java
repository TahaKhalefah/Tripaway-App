package com.tahadroid.tripaway.ui.map;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class Maps {
    double lat;
    double lon;
    private Context context;

    public Maps(Context context) {
        this.context = context;
    }

    public void openMapsDirectedToDestination(double latitude, double longitude) {
        this.lat = latitude;
        this.lon = longitude;
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=" + String.valueOf(this.lat) + "," + String.valueOf(this.lon)));
        context.startActivity(intent);
    }

}