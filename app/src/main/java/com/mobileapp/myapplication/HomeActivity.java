package com.mobileapp.myapplication;

import android.content.Intent;
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

        findViewById(R.id.home_btn_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, MapActivity.class));

            }
        });

        findViewById(R.id.home_btn_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ListActivity.class));

            }
        });



    }
}