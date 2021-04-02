package com.tahadroid.tripaway.ui.addTrip;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;
import com.tahadroid.tripaway.R;
import com.tahadroid.tripaway.alarm.AlarmBrodcast;
import com.tahadroid.tripaway.data.local.TripDatabase;
import com.tahadroid.tripaway.models.Trip;
import com.tahadroid.tripaway.models.User;
import com.tahadroid.tripaway.repository.TripRepository;
import com.tahadroid.tripaway.ui.TripViewModel;
import com.tahadroid.tripaway.ui.TripViewModelFactory;
import com.tahadroid.tripaway.ui.MainActivity;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.tahadroid.tripaway.utils.Constants.ERROR_FIEL_MSG;
import static com.tahadroid.tripaway.utils.Constants.FLAG_END;
import static com.tahadroid.tripaway.utils.Constants.FLAG_START;
import static com.tahadroid.tripaway.utils.Constants.GOOGLE_MAP_KEY;

public class AddTripDetailsActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, View.OnClickListener {

    private static final String TAG = "AddTripDetailsActivity";
    private static final String TIME_PICKER_TAG = "timePicker";
    private static final String EGYPT_PLACES = "EG";
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private Dialog confirmDialog;
    private EditText dateET, dateRoundET, timeET, timeRoundET, tripTitle, tripDescription;
    private Switch asyncSwitch;
    private Switch roundSwitch;
    private DatePickerDialog picker;
    private TextView startTrip, endTrip;
    private double endLat, endLong, startLat, startLong;
    private String locationFlag;
    private String timeFlag;
    private Button addTripBtn, updateChangeBtn;
    private boolean tripIsAsync = false;
    private boolean tripIsRound = false;
    private boolean isDone = false;
    private boolean status = false;
    private String startAddress;
    private String endAddress;
    private TripViewModel tripViewModel;
    private TripRepository tripRepository;
    private TripViewModelFactory tripViewModelFactory;
    private User user;
    private int userId;
    private String timeTonotify;
    private Trip oldTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip_details);

        tripTitle = findViewById(R.id.editTextTripTitle);
        tripDescription = findViewById(R.id.editTextTripDescription);
        asyncSwitch = findViewById(R.id.switchAsync);
        roundSwitch = findViewById(R.id.switchRound);
        dateET = findViewById(R.id.editTextDate);
        dateRoundET = findViewById(R.id.editTextRoundDate);
        timeET = findViewById(R.id.editTextTime);
        timeRoundET = findViewById(R.id.editTextRoundTime);
        startTrip = findViewById(R.id.textViewStartLocation);
        endTrip = findViewById(R.id.textViewDistinationLocation);
        addTripBtn = findViewById(R.id.addTripBtn);
        updateChangeBtn = findViewById(R.id.updateChangesBtn);

        tripRepository = new TripRepository(TripDatabase.getINSTANCE(this));
        tripViewModelFactory = new TripViewModelFactory(tripRepository);
        tripViewModel = new ViewModelProvider(this, tripViewModelFactory).get(TripViewModel.class);

        Intent intent = getIntent();
        oldTrip = (Trip) intent.getSerializableExtra("tripKey");
        if (oldTrip != null) {
            updateTrip();
        } else {
            addTripBtn.setVisibility(View.VISIBLE);
            updateChangeBtn.setVisibility(View.GONE);
            roundSwitch.setVisibility(View.VISIBLE);
        }
        tripViewModel.getUsersWithTrips().observe(this, userWithTrips -> {
            userId = userWithTrips.get(0).user.mUserId;
        });

        asyncSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tripIsAsync = true;
                } else {
                    tripIsAsync = false;
                }
            }
        });

        roundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dateRoundET.setVisibility(View.VISIBLE);
                    timeRoundET.setVisibility(View.VISIBLE);
                    tripIsRound = true;
                } else {
                    dateRoundET.setVisibility(View.GONE);
                    timeRoundET.setVisibility(View.GONE);
                    tripIsRound = false;
                }
            }
        });


        //Places.initialize(getApplicationContext(), GOOGLE_MAPS_KEY);
        Places.initialize(getApplicationContext(), GOOGLE_MAP_KEY);
        confirmDialog = new Dialog(this);
        startTrip.setOnClickListener(this);
        endTrip.setOnClickListener(this);
        timeET.setOnClickListener(this);
        dateET.setOnClickListener(this);
        timeRoundET.setOnClickListener(this);
        dateRoundET.setOnClickListener(this);
        addTripBtn.setOnClickListener(this);
    }

    private void updateTrip() {
        addTripBtn.setVisibility(View.GONE);
        updateChangeBtn.setVisibility(View.VISIBLE);
        roundSwitch.setVisibility(View.GONE);

        tripTitle.setText(oldTrip.mTripTitle);
        tripDescription.setText(oldTrip.mTripDescription);
        asyncSwitch.setChecked(oldTrip.mIsAsync);
        roundSwitch.setChecked(oldTrip.mIsRound);
        dateET.setText(oldTrip.mTripDate);
        timeET.setText(oldTrip.mTripTime);
        startTrip.setText(oldTrip.mStartAddress);
        endTrip.setText(oldTrip.mEndAddress);

        updateChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldTrip.mTripTitle = tripTitle.getText().toString().trim();
                oldTrip.mTripDescription = tripDescription.getText().toString().trim();
                oldTrip.mIsAsync = asyncSwitch.isChecked();
                oldTrip.mTripDate = dateET.getText().toString().trim();
                oldTrip.mTripTime = timeET.getText().toString().trim();
                oldTrip.mStartAddress = startTrip.getText().toString().trim();
                oldTrip.mEndAddress = endTrip.getText().toString().trim();

                tripViewModel.updateTrip(oldTrip);

                setAlarm(oldTrip.mTripTitle, dateET.getText().toString().trim(), timeET.getText().toString().trim(), oldTrip);

                startActivity(new Intent(getBaseContext(), MainActivity.class));
            }
        });
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Toast.makeText(this, "The Switch is " + (isChecked ? "on" : "off"),
                Toast.LENGTH_SHORT).show();
        if (isChecked) {
            //do stuff when Switch is ON
        } else {
            //do stuff when Switch if OFF
        }
    }

    @Override
    public void onClick(View v) {
        if (v == dateET) {
            getDateFromPicker();
        } else if (v == timeET) {
            timeFlag = "oneDirection";
            DialogFragment newFragment = new TimePickerFragment();
            newFragment.show(getSupportFragmentManager(), TIME_PICKER_TAG);
        } else if (v == startTrip) {
            locationFlag = FLAG_START;
            showAutoCompleteActivity();
        } else if (v == endTrip) {
            locationFlag = FLAG_END;
            showAutoCompleteActivity();
        } else if (v == addTripBtn) {
            if (!checkValidity()) {
                Snackbar.make(v, "Please fill in all required fields correctly!", Snackbar.LENGTH_LONG).show();
            } else {
                showConfirmDialog();
            }
        } else if (v == dateRoundET) {
            getRoundDateFromPicker();
        } else if (v == timeRoundET) {
            timeFlag = "Round";
            DialogFragment newFragment = new TimePickerFragment();
            newFragment.show(getSupportFragmentManager(), TIME_PICKER_TAG);
        }
    }

    private boolean checkValidity() {
        if (dateET.getText().toString().trim().isEmpty()) {
            dateET.setError(ERROR_FIEL_MSG);
            return false;
        } else if (timeET.getText().toString().trim().isEmpty()) {
            timeET.setError(ERROR_FIEL_MSG);
            return false;
        } else if (tripTitle.getText().toString().trim().isEmpty()) {
            tripTitle.setError(ERROR_FIEL_MSG);
            return false;
        } else if (tripDescription.getText().toString().trim().isEmpty()) {
            tripDescription.setError(ERROR_FIEL_MSG);
            return false;
        } else if (startTrip.getText().toString().trim().isEmpty()) {
            startTrip.setError(ERROR_FIEL_MSG);
            return false;
        } else if (endTrip.getText().toString().trim().isEmpty()) {
            endTrip.setError(ERROR_FIEL_MSG);
            return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showConfirmDialog() {
        confirmDialog.setContentView(R.layout.dialog_confirm_adding);
        confirmDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_shape));
        Button confirmBtn = confirmDialog.findViewById(R.id.yesDeleteBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTrip();
                confirmDialog.dismiss();
            }
        });
        Button cancelBtn = confirmDialog.findViewById(R.id.noDeleteBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.cancel();
                Toast.makeText(AddTripDetailsActivity.this, "Trip Cancel", Toast.LENGTH_SHORT).show();
            }
        });
        confirmDialog.show();
    }

    private void addTrip() {
        startAddress = startTrip.getText().toString();
        endAddress = endTrip.getText().toString();

        Trip trip = new Trip(tripTitle.getText().toString(),
                tripDescription.getText().toString(),
                dateET.getText().toString(),
                timeET.getText().toString(),
                startAddress,
                endAddress,
                startLat,
                startLong,
                endLat,
                endLat,
                tripIsAsync,
                isDone,
                tripIsRound,
                status,
                userId
        );
        if (tripIsRound) {
            Trip roundTrip = new Trip(tripTitle.getText().toString(),
                    tripDescription.getText().toString(),
                    dateRoundET.getText().toString(),
                    timeRoundET.getText().toString(),
                    startAddress,
                    endAddress,
                    endLat,
                    endLat,
                    startLat,
                    startLong,
                    tripIsAsync,
                    isDone,
                    tripIsRound,
                    status,
                    userId
            );
            tripViewModel.insertTrip(roundTrip);
        }

        tripViewModel.insertTrip(trip);

        //set alarm for this trip
        setAlarm(trip.mTripTitle, dateET.getText().toString().trim(), timeET.getText().toString().trim(), trip);

        startActivity(new Intent(this, MainActivity.class));
    }

    private void getDateFromPicker() {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        picker = new DatePickerDialog(AddTripDetailsActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateET.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                }, year, month, day);
        picker.show();
    }

    private void getRoundDateFromPicker() {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        picker = new DatePickerDialog(AddTripDetailsActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateRoundET.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, day);
        picker.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        timeTonotify = hourOfDay + ":" + minute;
        if (timeFlag.equals("Round"))
            timeRoundET.setText(timeTonotify);
        else
            timeET.setText(timeTonotify);
    }

    // AutoCompleteActivity
    public void showAutoCompleteActivity() {
        Intent toAutoActivityIntent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
                .setTypeFilter(TypeFilter.ADDRESS)
                .setCountries(Arrays.asList(EGYPT_PLACES))
                .build(AddTripDetailsActivity.this);
        startActivityForResult(toAutoActivityIntent, AUTOCOMPLETE_REQUEST_CODE);
    }

    // after a place is selected or autocomplete cancelled
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                if (locationFlag.equals(FLAG_START)) {
                    startLat = place.getLatLng().latitude;
                    startLong = place.getLatLng().longitude;
                    startTrip.setText(getNameOfRegion(startLat, startLong));
                } else if (locationFlag.equals(FLAG_END)) {
                    endLat = place.getLatLng().latitude;
                    endLong = place.getLatLng().longitude;
                    endTrip.setText(getNameOfRegion(endLat, endLong));
                } else if (locationFlag.equals(FLAG_END)) {
                    endLat = place.getLatLng().latitude;
                    endLong = place.getLatLng().longitude;
                    endTrip.setText(getNameOfRegion(endLat, endLong));
                } else if (locationFlag.equals(FLAG_END)) {
                    endLat = place.getLatLng().latitude;
                    endLong = place.getLatLng().longitude;
                    endTrip.setText(getNameOfRegion(endLat, endLong));
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // here handle error
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }

    //get address of region by latlong
    private String getNameOfRegion(Double lat, Double lon) {
        String region = "";
        List<Address> addresses;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            if (lat > 0 & lon > 0) {
                addresses = geocoder.getFromLocation(lat, lon, 1);
                String address = addresses.get(0).getAddressLine(0);
                region = addresses.get(0).getLocality();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return region;
    }

    private void setAlarm(String text, String date, String time, Trip trip) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmBrodcast.class);
        intent.putExtra("event", text);
        intent.putExtra("time", date);
        intent.putExtra("date", time);
        intent.putExtra("tripKey", trip);
        intent.putExtra("code", 15);
        intent.setAction(trip.mTripTitle+"-"+trip.mTripEndLatitude+"-"+trip.mTripEndLongitude);

        int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, uniqueInt, intent, PendingIntent.FLAG_CANCEL_CURRENT);


        String dateandtime = date + " " + timeTonotify;
        DateFormat formatter = new SimpleDateFormat("d-M-yyyy hh:mm");
        try {
            Date date1 = formatter.parse(dateandtime);
            am.set(AlarmManager.RTC, date1.getTime(), pendingIntent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        finish();
    }
}