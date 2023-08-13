package com.mobileapp.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobileapp.myapplication.models.Service;
import com.mobileapp.myapplication.models.User;
import com.mobileapp.myapplication.utils.Utils;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView userInfoTv, serviceInfoTv, contactInfoTv, availabilityTv, pricingTv;
    private Button chatBtn, requestBtn;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    private String serviceId = null;
    private Service service;
    private User user;

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
        requestBtn = findViewById(R.id.detail_btn_request);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.detail_frag_map);

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