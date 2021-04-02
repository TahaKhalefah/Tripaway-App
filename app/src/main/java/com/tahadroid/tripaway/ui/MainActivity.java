package com.tahadroid.tripaway.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tahadroid.tripaway.R;
import com.tahadroid.tripaway.data.local.TripDatabase;
import com.tahadroid.tripaway.data.remote.FirebaseHandler;
import com.tahadroid.tripaway.models.Note;
import com.tahadroid.tripaway.models.Trip;
import com.tahadroid.tripaway.models.User;
import com.tahadroid.tripaway.repository.TripRepository;
import com.tahadroid.tripaway.ui.addTrip.AddTripDetailsActivity;
import com.tahadroid.tripaway.ui.login.LoginActivity;
import com.tahadroid.tripaway.ui.map.AllTripsMapActivity;
import com.tahadroid.tripaway.ui.map.FloatWidgetService;
import com.tahadroid.tripaway.ui.splash.SplashActivity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TripViewModel tripViewModel;
    private TripRepository tripRepository;
    private TripViewModelFactory tripViewModelFactory;
    public static final String USER_ID_KEY = "userId";
    private static final String TAG = "TripsActivity";
    private int userId;
    private AppBarConfiguration mAppBarConfiguration;
    private SwitchCompat switch_id;
    private boolean FLAG_SWITCH = true;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private AlertDialog alert;
    private List<Trip> trips;
    private ImageView profilePicIVND;
    private TextView profileNameTVND;
    private User user;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    FirebaseHandler firebaseHandler;
    private boolean flag_lang = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseHandler = new FirebaseHandler(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        FLAG_SWITCH = sharedpreferences.getBoolean("sync", false);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        profileNameTVND = (TextView) hView.findViewById(R.id.profileNameTVND);
        profilePicIVND = (ImageView) hView.findViewById(R.id.profilePicIVND);

        tripRepository = new TripRepository(TripDatabase.getINSTANCE(this));
        tripViewModelFactory = new TripViewModelFactory(tripRepository);
        tripViewModel = new ViewModelProvider(this, tripViewModelFactory).get(TripViewModel.class);
        trips = new ArrayList<>();
        tripViewModel.getUsersWithTrips().observe(this, userWithTrips -> {
            user = userWithTrips.get(0).user;
            profileNameTVND.setText(user.mUserName);
            Glide.with(this).load(user.mUserProfilePicture)
                    .centerCrop()
                    .placeholder(R.drawable.ic_profile_pic)
                    .into(profilePicIVND);
            trips.clear();
            trips = userWithTrips.get(0).tripList;

            if (!(trips.size() > 0)) {
                firebaseHandler.getTripsByEmail();
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + this.getPackageName()));
            startActivityForResult(intent, 106);
        }

        userId = getIntent().getIntExtra("userId", 0);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToAddDetailsOfTripIntent = new Intent(getBaseContext(), AddTripDetailsActivity.class);
                goToAddDetailsOfTripIntent.putExtra(USER_ID_KEY, userId);
                startActivity(goToAddDetailsOfTripIntent);
            }
        });


        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_upcoming, R.id.nav_history)
                .setDrawerLayout(drawerLayout)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.getMenu().findItem(R.id.nav_switch).setOnMenuItemClickListener(menuItem -> {
            //add sync function
            switch_id = findViewById(R.id.switch_id);
            switch_id.setChecked(true);
            firebaseHandler.syncDataWithFirebaseDatabase(trips);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
        navigationView.getMenu().findItem(R.id.action_language).setOnMenuItemClickListener(menuItem -> {
            SharedPreferences sharedPreferences = getSharedPreferences(SplashActivity.LANG, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            if (menuItem.getTitle().equals("English")) {
                setLocale("ar");
                editor.putString(SplashActivity.LANG_VALUE, "ar");
            } else {
                setLocale("en");
                editor.putString(SplashActivity.LANG_VALUE, "en");
            }

            editor.commit();
            return true;
        });
        navigationView.getMenu().
                findItem(R.id.nav_map).
                setOnMenuItemClickListener(item ->
                {
                    ArrayList<LatLng> listLatlng = new ArrayList<LatLng>();
                    ArrayList<LatLng> listLatlngDestination = new ArrayList<LatLng>();
                    for (Trip trip : trips) {
                        listLatlng.add(new LatLng(trip.mTripStartLatitude, trip.mTripStartLongitude));
                        listLatlngDestination.add(new LatLng(trip.mTripEndLatitude, trip.mTripEndLongitude));
                    }

                    Intent intent = new Intent(this, AllTripsMapActivity.class);
                    intent.putExtra("list", listLatlng);
                    intent.putExtra("list_des", listLatlngDestination);

                    startActivity(intent);
                    return true;
                });
        navigationView.getMenu().

                findItem(R.id.action_logout).

                setOnMenuItemClickListener(item ->

                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Are you sure you want to logout")
                            .setCancelable(false)
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                    finish();
                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    return true;
                });
        navigationView.setItemIconTintList(null);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void setLocale(String lang) {
        String languageToLoad = lang;
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        Intent refresh = new Intent(this, MainActivity.class);
        finish();
        startActivity(refresh);
    }
}
