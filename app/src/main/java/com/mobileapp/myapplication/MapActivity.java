package com.mobileapp.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobileapp.myapplication.models.Service;
import com.mobileapp.myapplication.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    private LatLng currentLatLng = null;

    List<Service> listData = new ArrayList<>();


    private FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);





        Utils.showProgressDialog(MapActivity.this, "Getting data\nPlease wait");
        FirebaseFirestore.getInstance().collection(getResources().getString(R.string.data_collection))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Utils.dismissProgressDialog();

                        for (DocumentSnapshot document: task.getResult()) {
                            Service service = document.toObject(Service.class);
                            service.serviceId = document.getId();
                            listData.add(service);
                        }
                        getCurrentLocation();
                    }
                });

    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation(){
        Utils.showProgressDialog(MapActivity.this, "please wait");
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
                                .findFragmentById(R.id.services_fragment_map);
                        mapFragment.getMapAsync(MapActivity.this);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Utils.dismissProgressDialog();
                        currentLatLng = new LatLng(48.858834, 2.340167);
                        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.services_fragment_map);
                        mapFragment.getMapAsync(MapActivity.this);
                    }
                });
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        int position = 0;
        for (Service service: listData) {
            LatLng markerLatLng = new LatLng(Double.parseDouble(service.latLng.split(",")[0]), Double.parseDouble(service.latLng.split(",")[1]));
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(markerLatLng)
                    .title(service.title));
            marker.setTag(position);
            position++;
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                int position = (int)(marker.getTag());
                startActivity(new Intent(MapActivity.this, DetailActivity.class).putExtra("service_id", listData.get(position).serviceId));
                return false;
            }
        });


        LatLng defaultLocation = new LatLng(currentLatLng.latitude, currentLatLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 13.0f));

        mMap.getUiSettings().setZoomControlsEnabled(true);

    }
}