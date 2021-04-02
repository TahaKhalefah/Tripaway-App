package com.tahadroid.tripaway.ui.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tahadroid.tripaway.R;
import com.tahadroid.tripaway.data.local.TripDatabase;
import com.tahadroid.tripaway.models.User;
import com.tahadroid.tripaway.repository.TripRepository;
import com.tahadroid.tripaway.ui.TripViewModel;
import com.tahadroid.tripaway.ui.TripViewModelFactory;
import com.tahadroid.tripaway.ui.splash.SplashActivity;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class UserActivity extends AppCompatActivity {
    private EditText usernameET, emailET, passET, confirmPassET;
    private Button registerBtn;
    private ImageView profilePic;
    private static final String TAG = "UserActivity";
    private TripViewModel tripViewModel;
    private TripRepository tripRepository;
    private TripViewModelFactory tripViewModelFactory;
    private boolean isComplete = false;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users").child(mAuth.getCurrentUser().getUid());

        usernameET = findViewById(R.id.editTextTextPersonName);
        emailET = findViewById(R.id.editTextTextEmailAddress);
        passET = findViewById(R.id.editTextTextPassword);
        confirmPassET = findViewById(R.id.editTextTextPassword2);
        registerBtn = findViewById(R.id.buttonRegister);
        profilePic = findViewById(R.id.imageViewProfilePic);

        Intent getIntentData = getIntent();

        Glide.with(this).load(getIntentData.getStringExtra("personPhoto"))
                .centerCrop()
                .placeholder(R.drawable.ic_profile_pic)
                .into(profilePic);
        usernameET.setText(getIntentData.getStringExtra("personName"));
        emailET.setText(mAuth.getCurrentUser().getEmail());

        profilePic.setOnClickListener(v -> {
            pickImage();
        });

        tripRepository = new TripRepository(TripDatabase.getINSTANCE(this));
        tripViewModelFactory = new TripViewModelFactory(tripRepository);
        tripViewModel = new ViewModelProvider(this, tripViewModelFactory).get(TripViewModel.class);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    User user = new User(usernameET.getText().toString().trim(),
                            emailET.getText().toString().trim(),
                            passET.getText().toString().trim(),
                            getIntentData.getStringExtra("personPhoto"));
                    isComplete = true;
                    tripViewModel.insertUser(user);
                    myRef.setValue(user);
                    Intent toSplashActivity = new Intent(getBaseContext(), SplashActivity.class);
                    startActivity(toSplashActivity);
                    finish();
                }
            }
        });

    }

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == UserActivity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            try {
                InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }

    private boolean validate() {
        boolean temp = true;
        String pass = passET.getText().toString();
        String cpass = confirmPassET.getText().toString();
        if (pass.length() < 6) {
            passET.setError("Password must be more than 6 digit");
            return false;
        } else if (!pass.equals(cpass)) {
            confirmPassET.setError("Password Not matching");
            temp = false;
        }
        return temp;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isComplete) {
            FirebaseAuth.getInstance().signOut();
        }
    }
}