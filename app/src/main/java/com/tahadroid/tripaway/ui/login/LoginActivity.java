package com.tahadroid.tripaway.ui.login;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tahadroid.tripaway.R;

import com.tahadroid.tripaway.data.local.TripDatabase;
import com.tahadroid.tripaway.models.Trip;
import com.tahadroid.tripaway.models.User;
import com.tahadroid.tripaway.repository.TripRepository;
import com.tahadroid.tripaway.ui.TripViewModel;
import com.tahadroid.tripaway.ui.TripViewModelFactory;
import com.tahadroid.tripaway.ui.splash.SplashActivity;
import com.tahadroid.tripaway.ui.user.UserActivity;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private SignInButton btnGoogle;
    private Button signInWithPhoneBtn;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 121;

    private TripViewModel tripViewModel;
    private TripRepository tripRepository;
    private TripViewModelFactory tripViewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnGoogle = findViewById(R.id.btnGoogle);
        signInWithPhoneBtn = findViewById(R.id.signInWithPhoneBtn);
        mAuth = FirebaseAuth.getInstance();

        tripRepository = new TripRepository(TripDatabase.getINSTANCE(this));
        tripViewModelFactory = new TripViewModelFactory(tripRepository);
        tripViewModel = new ViewModelProvider(this, tripViewModelFactory).get(TripViewModel.class);


        setGooglePlusButtonText(btnGoogle, getString(R.string.GOOGLE) + "");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        signInWithPhoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(),PhoneActivity.class));
            }
        });
        btnGoogle.setOnClickListener(v -> {
            startActivityForResult(googleSignInClient.getSignInIntent(), RC_SIGN_IN);
        });
    }

    private void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setGravity(Gravity.CENTER);
                tv.setText(buttonText);
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Toast.makeText(this, "Google Sign In Successfully", Toast.LENGTH_SHORT).show();
            handelGoogleToken(account);
            //handelGoogleToken(account.getIdToken());
        } catch (ApiException e) {
            Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show();
            // handelGoogleToken(null); //cause nullPointerException
        }
    }


    private void handelGoogleToken(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            updateUI(currentUser);
                        } else {
                            Toast.makeText(getApplicationContext(), "Google sign in failed;(", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser firebaseUser) {
        GoogleSignInAccount inAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (inAccount != null) {

            String personName = inAccount.getDisplayName();
            String personGivenName = inAccount.getGivenName();
            String personFamilyName = inAccount.getFamilyName();
            String personId = inAccount.getId();
            String personPhoto = inAccount.getPhotoUrl().toString();
            Log.d("TAG", "personName: " + personName);
            Log.d("TAG", "personGivenName: " + personGivenName);
            Log.d("TAG", "personFamilyName: " + personFamilyName);
            Log.d("TAG", "personId: " + personId);
            Log.d("TAG", "personPhoto: " + personPhoto);

            String userID = mAuth.getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(userID)) {
                        User user  = snapshot.child(userID).getValue(User.class);
                        tripViewModel.getUsersWithTrips().observe(LoginActivity.this,userWithTrips -> {
                            if(!(userWithTrips.size() > 0)){
                                tripViewModel.insertUser(user);
                                for(Trip trip : getTripsFromFirebase()){
                                    tripViewModel.insertTrip(trip);
                                }
                            }
                            startActivity(new Intent(getApplicationContext(), SplashActivity.class));
                        });
                    } else {
                        Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                        intent.putExtra("personName",  personName);
                        intent.putExtra("personPhoto", personPhoto);
                        startActivity(intent);
                        Log.d("TAG", "onDataChange: else snapshot.hasChild(userID)");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<Trip> getTripsFromFirebase(){
        List<Trip>tripList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("trips").get().addOnCompleteListener(task -> {
            DataSnapshot result = task.getResult();
            Iterable<DataSnapshot> children = result.getChildren();
            children.forEach(dataSnapshot -> {
                Trip value = dataSnapshot.getValue(Trip.class);
                tripList.add(value);
            });
        });

        return tripList;
    }
}