package com.mobileapp.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobileapp.myapplication.models.Request;
import com.mobileapp.myapplication.models.RequestStatus;
import com.mobileapp.myapplication.models.Service;
import com.mobileapp.myapplication.models.User;
import com.mobileapp.myapplication.utils.Utils;

import java.util.Calendar;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView userInfoTv, serviceInfoTv, contactInfoTv, availabilityTv, pricingTv;
    private Button chatBtn, requestBtn;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    private String serviceId = null;
    private Service service;
    private User user;

    Calendar mcurrentTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        init();
        serviceId = getIntent().getStringExtra("service_id");
        Utils.showProgressDialog(DetailActivity.this, "Getting details\nPlease wait");
        FirebaseFirestore.getInstance().collection(getResources().getString(R.string.data_collection)).document(serviceId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    service = task.getResult().toObject(Service.class);
                    mapFragment.getMapAsync(DetailActivity.this);
                    FirebaseFirestore.getInstance().collection(getResources().getString(R.string.users_collection)).document(service.userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                user = task.getResult().toObject(User.class);
                                setTextViews();
                                Utils.dismissProgressDialog();
                            }else{
                                Utils.dismissProgressDialog();
                                Utils.showToast(DetailActivity.this, task.getException().getLocalizedMessage());
                            }
                        }
                    });



                }else{
                    Utils.dismissProgressDialog();
                    Utils.showToast(DetailActivity.this, task.getException().getLocalizedMessage());
                }
            }
        });
    }


    private void init(){
        userInfoTv = findViewById(R.id.detail_tv_userinfo);
        serviceInfoTv = findViewById(R.id.detail_tv_serviceinfo);
        contactInfoTv = findViewById(R.id.detail_tv_contactinfo);
        availabilityTv = findViewById(R.id.detail_tv_availability);
        pricingTv = findViewById(R.id.detail_tv_pricing);
        chatBtn = findViewById(R.id.detail_btn_chat);
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DetailActivity.this, ChatActivity.class)
                        .putExtra("user_id", Utils.getPref(DetailActivity.this, "user_id", ""))
                        .putExtra("guest_id", service.userId));
            }
        });


        requestBtn = findViewById(R.id.detail_btn_request);
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRequestDialog(Utils.getPref(DetailActivity.this, "user_id", ""));
            }
        });
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.detail_frag_map);

    }

    private void showRequestDialog(String requesterId){
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);

        View viewInflated = LayoutInflater.from(DetailActivity.this).inflate(R.layout.dialog_request_service,
                (ViewGroup) getWindow().getDecorView().getRootView(), false);

        EditText fromDateEt = viewInflated.findViewById(R.id.dialog_from_et);
        EditText toDateEt = viewInflated.findViewById(R.id.dialog_to_et);
        EditText detailsEt = viewInflated.findViewById(R.id.dialog_details_et);

        EditText vaccinationNameEt = viewInflated.findViewById(R.id.dialog_vaccinationname_et);
        EditText vaccinationTimeEt = viewInflated.findViewById(R.id.dialog_vaccinationtime_et);

        EditText foodNameEt = viewInflated.findViewById(R.id.dialog_foodname_et);
        EditText foodTimeEt = viewInflated.findViewById(R.id.dialog_foodtime_et);


        DatePickerDialog.OnDateSetListener fromDateD = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                Utils.myCalendar.set(Calendar.YEAR, year);
                Utils.myCalendar.set(Calendar.MONTH, month);
                Utils.myCalendar.set(Calendar.MONTH, month);
                Utils.myCalendar.set(Calendar.DAY_OF_MONTH, day);
                Utils.updateDateEt(fromDateEt);

            }
        };
        fromDateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(DetailActivity.this, fromDateD, Utils.myCalendar.get(Calendar.YEAR),
                        Utils.myCalendar.get(Calendar.MONTH), Utils.myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        DatePickerDialog.OnDateSetListener toDateD = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                Utils.myCalendar.set(Calendar.YEAR, year);
                Utils.myCalendar.set(Calendar.MONTH, month);
                Utils.myCalendar.set(Calendar.MONTH, month);
                Utils.myCalendar.set(Calendar.DAY_OF_MONTH, day);
                Utils.updateDateEt(toDateEt);

            }
        };
        toDateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(DetailActivity.this, toDateD, Utils.myCalendar.get(Calendar.YEAR),
                        Utils.myCalendar.get(Calendar.MONTH), Utils.myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        int vHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int vMinute = mcurrentTime.get(Calendar.MINUTE);

        vaccinationTimeEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(DetailActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        vaccinationTimeEt.setText(selectedHour+":"+selectedMinute);
                    }
                }, vHour, vMinute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Vaccination Time");
                mTimePicker.show();
            }
        });


        int fHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int fMinute = mcurrentTime.get(Calendar.MINUTE);

        foodTimeEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(DetailActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        foodTimeEt.setText(selectedHour+":"+selectedMinute);
                    }
                }, fHour, fMinute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Food Time");
                mTimePicker.show();
            }
        });




        builder.setPositiveButton("Request Service", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(Utils.isEmpty(fromDateEt) || Utils.isEmpty(toDateEt))
                    Utils.showToast(DetailActivity.this, "Start and End date are compulsory");
                else{
                    Utils.showProgressDialog(DetailActivity.this, "Creating request\nPlease wait");
                    DocumentReference requestRef = FirebaseFirestore.getInstance().collection(getResources().getString(R.string.requests_collection)).document();
                    Request request = new Request(requestRef.getId(), requesterId, service.userId, service.serviceId, fromDateEt.getText().toString(), toDateEt.getText().toString(),
                            detailsEt.getText().toString(), RequestStatus.PENDING.toString(),
                             vaccinationNameEt.getText().toString(), vaccinationTimeEt.getText().toString(),
                            foodNameEt.getText().toString(), foodTimeEt.getText().toString());
                    requestRef.set(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Utils.dismissProgressDialog();
                                    if(task.isSuccessful()){
                                        Utils.showToast(DetailActivity.this, "Requested successfully\nYou can check requested services statuses from your Home screen");
                                    }else{
                                        Utils.showToast(DetailActivity.this, task.getException().getLocalizedMessage());
                                    }
                                    dialog.cancel();
                                }
                            });

                }


            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setView(viewInflated);

        builder.show();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;


        LatLng serviceLatLng = new LatLng(Double.parseDouble(service.latLng.split(",")[0]), Double.parseDouble(service.latLng.split(",")[1]));

        googleMap.addMarker(new MarkerOptions()
                .position(serviceLatLng)
                .title(service.title));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(serviceLatLng, 13.0f));

        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void setTextViews(){
        userInfoTv.setText(user.toString());
        serviceInfoTv.setText(service.getServiceInfo());
        contactInfoTv.setText(service.getContactInfo());
        availabilityTv.setText(service.getAvailabilityInfo());
        pricingTv.setText(service.getPricingInfo());
    }
}