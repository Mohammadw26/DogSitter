package com.mobileapp.myapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.mobileapp.myapplication.utils.PermissionsManager;
import com.mobileapp.myapplication.utils.Utils;


public class HomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseFirestore.getInstance().collection(getResources().getString(R.string.users_collection))
                .document(Utils.getPref(HomeActivity.this, "user_id", "")).addSnapshotListener(
                        new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (value != null && value.exists()) {
                                    if((boolean) value.getData().get("request_noti")){
                                        findViewById(R.id.cv_requestsnoti_home).setVisibility(View.VISIBLE);
                                    }else{
                                        findViewById(R.id.cv_requestsnoti_home).setVisibility(View.GONE);

                                    }
                                    if((boolean) value.getData().get("chat_noti")){
                                        findViewById(R.id.cv_mychatsnoti_home).setVisibility(View.VISIBLE);
                                    }else{
                                        findViewById(R.id.cv_mychatsnoti_home).setVisibility(View.GONE);

                                    }
                                    if((boolean) value.getData().get("myrequest_noti")){
                                        findViewById(R.id.cv_myrequestsnoti_home).setVisibility(View.VISIBLE);
                                    }else{
                                        findViewById(R.id.cv_myrequestsnoti_home).setVisibility(View.GONE);

                                    }
                                }
                            }
                        }
                );

//        FirebaseFirestore.getInstance().collection(getResources().getString(R.string.users_collection))
//                        .document(Utils.getPref(HomeActivity.this, "user_id", ""))
//                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if(task.isSuccessful()){
//                            if(task.getResult().getBoolean("request_noti")){
//                                findViewById(R.id.cv_requestsnoti_home).setVisibility(View.VISIBLE);
//                            }else{
//                                findViewById(R.id.cv_requestsnoti_home).setVisibility(View.GONE);
//
//                            }
//                            if(task.getResult().getBoolean("chat_noti")){
//                                findViewById(R.id.cv_mychatsnoti_home).setVisibility(View.VISIBLE);
//                            }else{
//                                findViewById(R.id.cv_mychatsnoti_home).setVisibility(View.GONE);
//
//                            }
//                            if(task.getResult().getBoolean("myrequest_noti")){
//                                findViewById(R.id.cv_myrequestsnoti_home).setVisibility(View.VISIBLE);
//                            }else{
//                                findViewById(R.id.cv_myrequestsnoti_home).setVisibility(View.GONE);
//
//                            }
//                        }
//                    }
//                });

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

        findViewById(R.id.home_btn_myreminders).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, MyRemindersActivity.class));
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