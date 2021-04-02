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

public class AllTripsMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap map;
    Location currLoc;
    LatLng latlng1;
    LatLng latlng2;
    Polyline polyline1;
    ArrayList<LatLng> listLatlng = new ArrayList<LatLng>();
    ArrayList<LatLng> listLatlngDestination = new ArrayList<LatLng>();
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_trips_map);
        listLatlng = getIntent().getExtras().getParcelableArrayList("list");
        listLatlngDestination = getIntent().getExtras().getParcelableArrayList("list_des");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private String getMapsApiDirectionsUrl(LatLng origin, LatLng des) {
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=%22" + String.valueOf(origin.latitude) + "," + String.valueOf(origin.longitude) + "%22&destination=%22" + String.valueOf(des.latitude) + "," + String.valueOf(des.longitude) + "%22&key=AIzaSyDVh2YvCYg-Mcjn-pfEIxeth4Ey9il9vFA";
        return url;
    }


    private class GetDircetion extends AsyncTask<Void, Void, List<List<LatLng>>> {
        String url;
        int roadColor;

        GetDircetion(String url, int roadColor) {
            this.url = url;
            this.roadColor = roadColor;
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
                lineoption.color(this.roadColor);
                lineoption.geodesic(true);
            }
            map.addPolyline(lineoption);
        }

    }

    class ZoomTask extends AsyncTask {
        LatLng latlng;
        float zoom;

        ZoomTask(LatLng latlng, float zoom) {
            this.latlng = latlng;
            this.zoom = zoom;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));

        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        for (int i = 0; i < listLatlng.size(); i++) {
            count = i;
            map.addMarker(new MarkerOptions().position(listLatlng.get(i)).title("Start Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            ZoomTask zoom = new ZoomTask(listLatlngDestination.get(i), 12f);

            zoom.execute();

            map.addMarker(new MarkerOptions().position(listLatlngDestination.get(i)).title("Destination").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            String url = getMapsApiDirectionsUrl(listLatlng.get(i), listLatlngDestination.get(i));
            GetDircetion getDirection;
            switch (i) {
                case 1:
                    getDirection = new GetDircetion(url, Color.BLUE);
                    getDirection.execute();

                    break;
                case 2:
                    getDirection = new GetDircetion(url, Color.RED);
                    getDirection.execute();

                    break;
                case 3:
                    getDirection = new GetDircetion(url, Color.CYAN);
                    getDirection.execute();

                    break;
                case 4:
                    getDirection = new GetDircetion(url, Color.GREEN);
                    getDirection.execute();

                    break;
                case 5:
                    getDirection = new GetDircetion(url, Color.YELLOW);
                    getDirection.execute();

                    break;
                case 6:
                    getDirection = new GetDircetion(url, Color.DKGRAY);
                    getDirection.execute();

                    break;
                default:
                    getDirection = new GetDircetion(url, Color.BLACK);
                    getDirection.execute();

            }
        }
        if (listLatlngDestination.size() > 0) {
            ZoomTask zoom = new ZoomTask(listLatlngDestination.get(0), 7f);
            zoom.execute();

        }


    }


}