package com.tahadroid.tripaway.ui.splash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tahadroid.tripaway.R;
import com.tahadroid.tripaway.data.local.TripDatabase;
import com.tahadroid.tripaway.repository.TripRepository;
import com.tahadroid.tripaway.ui.TripViewModel;
import com.tahadroid.tripaway.ui.TripViewModelFactory;
import com.tahadroid.tripaway.ui.login.LoginActivity;
import com.tahadroid.tripaway.ui.MainActivity;
import com.tahadroid.tripaway.ui.user.UserActivity;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_SCREEN = 3000;
    private Animation tobAnim, bottomAnim;
    private ImageView appLogoImg;
    private TextView appName;
    private FirebaseUser currentUser;
    private int userId;
    private TripViewModel tripViewModel;
    private TripRepository tripRepository;
    private TripViewModelFactory tripViewModelFactory;

    public static final String LANG = "LANGUAGE";
    public static final String LANG_VALUE = "VALUE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SharedPreferences sharedPreferences = getSharedPreferences(LANG, MODE_PRIVATE);

        String lang = sharedPreferences.getString(LANG_VALUE, "en");
        setLocale(lang);
        tripRepository = new TripRepository(TripDatabase.getINSTANCE(this));
        tripViewModelFactory = new TripViewModelFactory(tripRepository);
        tripViewModel = new ViewModelProvider(this, tripViewModelFactory).get(TripViewModel.class);
        tripViewModel.getUsersWithTrips().observe(this, userWithTrips -> {
            userId = userWithTrips.get(0).user.mUserId;
        });

        appLogoImg = findViewById(R.id.appLogoIV);
        appName = findViewById(R.id.appNameTV);

        addAnimation();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUser.reload();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentUser != null) {
                    Intent toMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                    toMainActivity.putExtra("userId", userId);
                    startActivity(toMainActivity);
                } else {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
                finish();
            }
        }, SPLASH_SCREEN);
    }

    private void addAnimation() {
        tobAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        appLogoImg.setAnimation(tobAnim);
        appName.setAnimation(bottomAnim);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (userId < 0) {
            startActivity(new Intent(getBaseContext(), UserActivity.class));
        }
    }

    public void setLocale(String lang) {
        String languageToLoad  = lang;
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }
}