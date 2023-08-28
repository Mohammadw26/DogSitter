package com.mobileapp.myapplication;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobileapp.myapplication.models.Service;
import com.mobileapp.myapplication.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddActivity extends AppCompatActivity  implements OnMapReadyCallback {


    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private ImageView markerIv;
    private ScrollView parentSv;

    private LatLng midLatLng = new LatLng(0,0);
    private LatLng currentLatLng = null;
//    private AutocompleteSupportFragment autocompleteFragment;
    private FusedLocationProviderClient fusedLocationClient;


    private EditText titleEt, desEt, addressEt, startingPriceEt, maximumPriceEt, contactEt, startTimeEt, endTimeEt, startDateEt, endDateEt;
    private CheckBox dogCb, catCb, birdCb, babiesCb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        titleEt = findViewById(R.id.add_et_title);
        desEt = findViewById(R.id.add_et_description);
        addressEt = findViewById(R.id.add_et_address);
        startingPriceEt = findViewById(R.id.add_et_starting_price);
        maximumPriceEt = findViewById(R.id.add_et_maximum_price);
        contactEt = findViewById(R.id.add_et_contact);

        startTimeEt = findViewById(R.id.add_et_starttime);
        endTimeEt = findViewById(R.id.add_et_endtime);


        startDateEt = findViewById(R.id.add_et_startdate);
        endDateEt = findViewById(R.id.add_et_enddate);

        dogCb = findViewById(R.id.add_cb_dogs);
        catCb = findViewById(R.id.add_cb_cats);
        birdCb = findViewById(R.id.add_cb_birds);
        babiesCb = findViewById(R.id.add_cb_babies);




        setDateAndTimePickers();



        findViewById(R.id.add_btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!dogCb.isChecked() && !catCb.isChecked() && !birdCb.isChecked() && !babiesCb.isChecked()){
                    Utils.showToast(AddActivity.this, "Select each category at least");
                }else if(Utils.isEmpty(titleEt) || Utils.isEmpty(desEt) || Utils.isEmpty(addressEt)
                || Utils.isEmpty(startingPriceEt) || Utils.isEmpty(contactEt)  || Utils.isEmpty(maximumPriceEt)
                        || Utils.isEmpty(startTimeEt) || Utils.isEmpty(endTimeEt)  || Utils.isEmpty(startDateEt)  || Utils.isEmpty(endDateEt)){
                    Utils.showToast(AddActivity.this, "Some fields are still empty");
                }else{
                    Utils.showProgressDialog(AddActivity.this, "Adding new service\nPlease wait");

                    List<String> categories = new ArrayList<>();
                    if(dogCb.isChecked()){
                        categories.add("Dogs");
                    }
                    if(catCb.isChecked()){
                        categories.add("Cats");
                    }
                    if(birdCb.isChecked()){
                        categories.add("Birds");
                    }
                    if(babiesCb.isChecked()){
                        categories.add("Babies");
                    }
                    Service service = new Service(titleEt.getText().toString(), desEt.getText().toString(), addressEt.getText().toString(),
                            contactEt.getText().toString(), startingPriceEt.getText().toString(), maximumPriceEt.getText().toString(), midLatLng.latitude+"," + midLatLng.longitude,
                            startTimeEt.getText().toString(), endTimeEt.getText().toString(), startDateEt.getText().toString(), endDateEt.getText().toString(), null,
                            Utils.getPref(AddActivity.this, "user_id", null), categories);

                    FirebaseFirestore.getInstance().collection(getResources().getString(R.string.data_collection)).add(service)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    Utils.dismissProgressDialog();
                                    if(task.isSuccessful()){
                                        Utils.showToast(AddActivity.this, "Added successfully");
                                        onBackPressed();
                                    }else{
                                        Utils.showToast(AddActivity.this, task.getException().getLocalizedMessage());
                                    }
                                }
                            });
                }
            }
        });

        // Initialize the SDK
        Places.initialize(getApplicationContext(), getResources().getString(R.string.map_api_key));

        markerIv = findViewById(R.id.adddelivery_iv_marker);
        parentSv = findViewById(R.id.add_sv_parent);


//        // Initialize the AutocompleteSupportFragment.
//        autocompleteFragment = (AutocompleteSupportFragment)
//                getSupportFragmentManager().findFragmentById(R.id.adddelivery_fragment_searchquery);
//        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
//
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                // TODO: Get info about the selected place.
//                performSearch(place.getName());
//                //Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
//            }
//
//            @Override
//            public void onError(Status status) {
//                // TODO: Handle the error.
//                Utils.showToast(AddActivity.this, status.getStatusMessage());
//                //Log.i(TAG, "An error occurred: " + status);
//            }
//        });


        markerIv.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        parentSv.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        parentSv.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        parentSv.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });


        getCurrentLocation();
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation(){
        Utils.showProgressDialog(AddActivity.this, "please wait");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        Utils.dismissProgressDialog();
                        if (location != null) {
                            currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                        }else{
                            currentLatLng = new LatLng(48.858834, 2.340167);
                        }

                        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.adddelivery_fragment_map);
                        mapFragment.getMapAsync(AddActivity.this);
                    }
                });
    }


    private void setDateAndTimePickers(){
        DatePickerDialog.OnDateSetListener startDate =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                Utils.myCalendar.set(Calendar.YEAR, year);
                Utils.myCalendar.set(Calendar.MONTH,month);
                Utils.myCalendar.set(Calendar.MONTH,month);
                Utils.myCalendar.set(Calendar.DAY_OF_MONTH,day);
                Utils.updateDateEt(startDateEt);
            }
        };

        startDateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AddActivity.this,startDate,Utils.myCalendar.get(Calendar.YEAR),
                        Utils.myCalendar.get(Calendar.MONTH),Utils.myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        DatePickerDialog.OnDateSetListener endDate =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                Utils.myCalendar.set(Calendar.YEAR, year);
                Utils.myCalendar.set(Calendar.MONTH,month);
                Utils.myCalendar.set(Calendar.MONTH,month);
                Utils.myCalendar.set(Calendar.DAY_OF_MONTH,day);
                Utils.updateDateEt(endDateEt);
            }
        };

        endDateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AddActivity.this,endDate,Utils.myCalendar.get(Calendar.YEAR),
                        Utils.myCalendar.get(Calendar.MONTH),Utils.myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        startTimeEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        startTimeEt.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Start Time");
                mTimePicker.show();
            }
        });


        endTimeEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        endTimeEt.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select End Time");
                mTimePicker.show();
            }
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;


        LatLng defaultLocation = new LatLng(currentLatLng.latitude, currentLatLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 13.0f));

        mMap.getUiSettings().setZoomControlsEnabled(true);

        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                midLatLng = googleMap.getCameraPosition().target;
            }
        });
    }

    public void performSearch(String searchQuery) {

        List<Address> addressList = null;

        if (searchQuery != null || !searchQuery.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(searchQuery, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            //mMap.addMarker(new MarkerOptions().position(latLng).title(address.getAddressLine(0)));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }
}