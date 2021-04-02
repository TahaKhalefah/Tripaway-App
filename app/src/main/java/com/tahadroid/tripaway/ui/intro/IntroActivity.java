package com.tahadroid.tripaway.ui.intro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.tahadroid.tripaway.R;
import com.tahadroid.tripaway.repository.TripRepository;
import com.tahadroid.tripaway.ui.TripViewModel;
import com.tahadroid.tripaway.ui.TripViewModelFactory;
import com.tahadroid.tripaway.ui.login.LoginActivity;
import com.tahadroid.tripaway.ui.splash.SplashActivity;

public class IntroActivity extends AppCompatActivity {
    private static final String START_FLAG = "yes";
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    public static int[] layouts;
    private Button btnSkip, btnNext;
    FirebaseUser currentUser;
    private DatabaseReference databaseReference;
    private boolean isComplete = true;
    private TripViewModel tripViewModel;
    private TripRepository tripRepository;
    private TripViewModelFactory tripViewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
/*
        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        flag = sharedpreferences.getBoolean(START_FLAG, false);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(START_FLAG, Boolean.TRUE);
        editor.apply();
*/
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        }

        chnageBarColour();

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int current = viewPager.getCurrentItem() + 1;
                if (current < layouts.length) {
                    viewPager.setCurrentItem(current);
                } else {
                    if (currentUser == null) {
                        startLoginActivity();
                    } else {
                        startSplashActivity();
                    }

                }

            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null) {
                    startLoginActivity();
                } else {
                    startSplashActivity();
                }
            }
        });
        layouts = new int[]{R.layout.screen_one, R.layout.screen_two, R.layout.screen_three, R.layout.screen_four};


        addButton(0);
        myViewPagerAdapter = new MyViewPagerAdapter(IntroActivity.this);
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                addButton(position);
                if (position == layouts.length - 1) {
                    btnSkip.setVisibility(View.GONE);
                } else {
                    btnSkip.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser != null) {
            startSplashActivity();
        }
    }

    private void startLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void startSplashActivity() {
        startActivity(new Intent(this, SplashActivity.class));
        finish();
    }

    private void chnageBarColour() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public void addButton(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }
        if (dots.length > 0) {
            dots[currentPage].setTextColor(colorsActive[currentPage]);
        }

    }
}