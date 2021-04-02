package com.tahadroid.tripaway.ui.map;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.tahadroid.tripaway.R;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

public class OpenMapActivity extends AppCompatActivity {
    CurrentLocation curr;
    Button getlocVutton;

    //    GetFloatingIconClick receiver;
    IntentFilter filter = new IntentFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_map);
        getlocVutton = findViewById(R.id.btn_get);

//         // Create instance from CurrentLocation Class
//         curr = new CurrentLocation(MainActivity.this);
//         // Set the Request and Callback (use setLocationRequest if you need a custom request)
//         curr.defaultLocationRequest();
//         curr.defaultLocationCallback();
//        // start the location getter every 60 second by default , call stop to stop the getter after retreving the last location
//         curr.start();
//
        getlocVutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//            // get the last known location
//                Log.i("locaa", "onClick: " + curr.getCurrentLocationResult());
//

                // Open Google Maps Directed to the Destination
                // pass the destination in latitude and longitude format
                Maps maps = new Maps(OpenMapActivity.this);
                maps.openMapsDirectedToDestination(30.002, 31.030434);

                // Check for Permissions then start The Widget Service
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(OpenMapActivity.this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 106);
                } else {
                    Intent startIntent = new Intent(OpenMapActivity.this, FloatWidgetService.class);
                    startIntent.putExtra("note1", "Go to the Supermarket");
                    startIntent.putExtra("note2", "Pick up Ramy");
                    startService(startIntent);
                }

            }
        });
    }
}