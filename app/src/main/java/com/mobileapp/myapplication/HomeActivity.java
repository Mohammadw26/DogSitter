package com.mobileapp.myapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.mobileapp.myapplication.utils.PermissionsManager;
import com.mobileapp.myapplication.utils.Utils;


public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findViewById(R.id.home_btn_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        findViewById(R.id.cv_mychats_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ChatsListActivity.class)
                        .putExtra("user_id", Utils.getPref(HomeActivity.this, "user_id", "")));
            }
        });

        findViewById(R.id.home_btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PermissionsManager.isLocationPermissionsEnabled(HomeActivity.this)){
                    if(Utils.isLocationEnabled(HomeActivity.this)){
                        startActivity(new Intent(HomeActivity.this, AddActivity.class));
                    }else{
                        Utils.showToast(HomeActivity.this, "Enable location from the settings");
                    }
                }else {
                    requestPermissions(PermissionsManager.LOCATION_PERMISSIONS, PermissionsManager.LOCATION_CODE);
                }

            }
        });

        findViewById(R.id.cv_requests_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, RequestActivity.class));
            }
        });

        findViewById(R.id.home_btn_mydogs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, MyDogsActivity.class));
            }
        });

        findViewById(R.id.home_btn_chatbot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, VetbotActivity.class));
            }
        });

        findViewById(R.id.cv_myrequests_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, MyRequestsActivity.class));
            }
        });

        findViewById(R.id.home_btn_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!PermissionsManager.isLocationPermissionsEnabled(HomeActivity.this)){
                    requestPermissions(new String[]{
                                    android.Manifest.permission.ACCESS_FINE_LOCATION},
                            123);
                }else if(!Utils.isLocationEnabled(HomeActivity.this)){
                    Utils.showToast(HomeActivity.this, "Enable location service from the settings");
                }else{
                    startActivity(new Intent(HomeActivity.this, MapActivity.class));

                }

            }
        });

        findViewById(R.id.home_btn_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ListActivity.class));

            }
        });



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 123:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(!Utils.isLocationEnabled(HomeActivity.this)) {
                        Utils.showToast(HomeActivity.this, "Enable location service from the settings");
                    }else{
                        startActivity(new Intent(HomeActivity.this, MapActivity.class));
                    }

                } else {
                    Utils.showToast(HomeActivity.this, "Permission are required inorder to map works");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}