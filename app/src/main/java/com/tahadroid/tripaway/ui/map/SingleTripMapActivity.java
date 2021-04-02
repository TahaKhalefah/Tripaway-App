package com.tahadroid.tripaway.ui.map;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.tahadroid.tripaway.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SingleTripMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap map;
    Location currLoc;
    LatLng latlng1;
    LatLng latlng2;
    Polyline polyline1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_trip_map);
        latlng1 = new LatLng(getIntent().getExtras().getDouble("lat1"), getIntent().getExtras().getDouble("long1"));
        latlng2 = new LatLng(getIntent().getExtras().getDouble("lat2"), getIntent().getExtras().getDouble("long2"));
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private String getMapsApiDirectionsUrl() {
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=%22" + String.valueOf(latlng1.latitude) + "," + String.valueOf(latlng1.longitude) + "%22&destination=%22" + String.valueOf(latlng2.latitude) + "," + String.valueOf(latlng2.longitude) + "%22&key=AIzaSyDVh2YvCYg-Mcjn-pfEIxeth4Ey9il9vFA";
        return url;
    }


    private class GetDircetion extends AsyncTask<Void, Void, List<List<LatLng>>> {
        String url;

        GetDircetion(String url) {
            this.url = url;
        }

        @Override
        protected List<List<LatLng>> doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String data = null;
            try {
                data = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("GoogleMap", " data : $data");
            ArrayList<List<LatLng>> result = new ArrayList<List<LatLng>>();
            try {

                GoogleMapDTO respObj = new Gson().fromJson(data, GoogleMapDTO.class);
                ArrayList<LatLng> path = new ArrayList<LatLng>();

                for (int i = 0; i < respObj.routes.get(0).legs.get(0).steps.size() - 1; i++) {
                    LatLng startLatLng = new LatLng(Double.parseDouble(respObj.routes.get(0).legs.get(0).steps.get(i).start_location.lat)
                            , Double.parseDouble(respObj.routes.get(0).legs.get(0).steps.get(i).start_location.lng));
                    path.add(startLatLng);
                    LatLng endLatLng = new LatLng(Double.parseDouble(respObj.routes.get(0).legs.get(0).steps.get(i).end_location.lat)
                            , Double.parseDouble(respObj.routes.get(0).legs.get(0).steps.get(i).end_location.lng));
                    path.add(endLatLng);

                }
                result.add(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<List<LatLng>> lists) {
            super.onPostExecute(lists);
            PolylineOptions lineoption = new PolylineOptions();
            for (int i = 0; i < lists.size(); i++) {
                lineoption.addAll(lists.get(i));
                lineoption.width(25f);
                lineoption.color(Color.BLUE);
                lineoption.geodesic(true);
            }
            map.addPolyline(lineoption);
        }

    }


    private void addMarkers() {
        if (map != null) {
            map.addMarker(new MarkerOptions().position(latlng1)
                    .title("First Point"));
            map.addMarker(new MarkerOptions().position(latlng2)
                    .title("Second Point"));
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.addMarker(new MarkerOptions().position(latlng1).title("Start Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng1, 7f));
        map.addMarker(new MarkerOptions().position(latlng2).title("Destination"));

        String url = getMapsApiDirectionsUrl();
        GetDircetion getDirection = new GetDircetion(url);
        getDirection.execute();


    }


}