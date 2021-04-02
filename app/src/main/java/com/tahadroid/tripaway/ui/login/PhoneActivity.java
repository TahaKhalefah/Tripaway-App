package com.tahadroid.tripaway.ui.login;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.tahadroid.tripaway.R;
import com.tahadroid.tripaway.data.local.TripDatabase;
import com.tahadroid.tripaway.models.Trip;
import com.tahadroid.tripaway.models.User;
import com.tahadroid.tripaway.repository.TripRepository;
import com.tahadroid.tripaway.ui.TripViewModel;
import com.tahadroid.tripaway.ui.TripViewModelFactory;
import com.tahadroid.tripaway.ui.splash.SplashActivity;
import com.tahadroid.tripaway.ui.user.UserActivity;

import java.util.concurrent.TimeUnit;

public class PhoneActivity extends AppCompatActivity {
    private CountryCodePicker ccp;
    private EditText phoneText;
    private EditText codeText;
    private Button continueAndNextBtn;
    private String checker = "", phoneNumber = "";
    private RelativeLayout relativeLayout;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mReesendToken;
    private ProgressDialog loadingBar;

    private TripViewModel tripViewModel;
    private TripRepository tripRepository;
    private TripViewModelFactory tripViewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        tripRepository = new TripRepository(TripDatabase.getINSTANCE(this));
        tripViewModelFactory = new TripViewModelFactory(tripRepository);
        tripViewModel = new ViewModelProvider(this, tripViewModelFactory).get(TripViewModel.class);

        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);
        phoneText = findViewById(R.id.phoneText);
        codeText = findViewById(R.id.codeText);
        continueAndNextBtn = findViewById(R.id.continueNextButton);
        relativeLayout = findViewById(R.id.phoneAuth);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(phoneText);


        continueAndNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (continueAndNextBtn.getText().equals("Submit") || checker.equals("Code Sent")) {

                    String verificationCode = codeText.getText().toString();
                    if (verificationCode.equals("")) {
                        Toast.makeText(PhoneActivity.this, "Please write verification code first ..", Toast.LENGTH_SHORT).show();
                    } else {
                        loadingBar.setTitle("Code Verifiction");
                        loadingBar.setMessage("Please wait, While we are verifying your Code.");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();

                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                        signInWithPhoneAuthCredential(credential);
                    }
                } else {
                    phoneNumber = ccp.getFullNumberWithPlus();
                    if (!phoneNumber.equals("")) {
                        loadingBar.setTitle("Phone Number Verifiction");
                        loadingBar.setMessage("Please wait, While we are verifying your phone Number.");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();
                        PhoneAuthOptions options =
                                PhoneAuthOptions.newBuilder(mAuth)
                                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                        .setActivity(PhoneActivity.this)                 // Activity (for callback binding)
                                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                        .build();
                        PhoneAuthProvider.verifyPhoneNumber(options);
                    } else {
                        Toast.makeText(PhoneActivity.this, "Please write valid Phone Number.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(PhoneActivity.this, "Invalid Phone Number....", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
                relativeLayout.setVisibility(View.VISIBLE);
                continueAndNextBtn.setText("Continue");
                codeText.setVisibility(View.GONE);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                mVerificationId = s;
                mReesendToken = forceResendingToken;
                relativeLayout.setVisibility(View.GONE);
                checker = "Code Sent";
                continueAndNextBtn.setText("Submit");
                codeText.setVisibility(View.VISIBLE);
                loadingBar.dismiss();
                Toast.makeText(PhoneActivity.this, "Code has been Sent, Please check.", Toast.LENGTH_SHORT).show();
            }
        };

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            Toast.makeText(PhoneActivity.this, "Congratulations , you are logged in successfully. ", Toast.LENGTH_SHORT).show();
                            //sendUserToMainActivity();
                            updateUI();
                        } else {
                            loadingBar.dismiss();
                            String e = task.getException().toString();
                            Toast.makeText(PhoneActivity.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                            updateUI();
                        }
                    }
                });
    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(PhoneActivity.this, UserActivity.class);
        startActivity(intent);
        finish();
    }
    private void updateUI() {

            String userID = mAuth.getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(userID)) {
                        User user  = snapshot.child(userID).getValue(User.class);
                        tripViewModel.getUsersWithTrips().observe(PhoneActivity.this,userWithTrips -> {
                            if(!(userWithTrips.size() > 0)){
                                tripViewModel.insertUser(user);
                            }
                            startActivity(new Intent(getApplicationContext(), SplashActivity.class));
                            finish();
                        });
                    } else {
                        Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                        startActivity(intent);
                        finish();
                        Log.d("TAG", "onDataChange: else snapshot.hasChild(userID)");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

    }
}