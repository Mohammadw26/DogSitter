package com.mobileapp.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobileapp.myapplication.utils.Utils;

public class MainActivity extends AppCompatActivity {




    private TextView signupTv;
    private EditText usernameEt, passwordEt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        signupTv = findViewById(R.id.main_tv_signup);
        usernameEt = findViewById(R.id.main_et_username);
        passwordEt = findViewById(R.id.main_et_password);

        signupTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SignupActivity.class));
            }
        });

        findViewById(R.id.main_btn_signin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utils.isEmpty(usernameEt) || Utils.isEmpty(passwordEt)){
                    Utils.showToast(MainActivity.this, "Some fields are empty");
                }else{
                    Utils.showProgressDialog(MainActivity.this, "Signing in\nPlease wait");
                    FirebaseFirestore.getInstance().collection(getResources().getString(R.string.users_collection))
                            .whereEqualTo("username", usernameEt.getText().toString())
                            .whereEqualTo("password", passwordEt.getText().toString())
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        Utils.dismissProgressDialog();
                                        if(task.getResult().size()>0){
                                            QueryDocumentSnapshot documentSnapshot = task.getResult().iterator().next();
                                            Utils.setPref(MainActivity.this, "user_id", documentSnapshot.getId());

                                            startActivity(new Intent(MainActivity.this, HomeActivity.class)
                                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                        }else{
                                            Utils.showToast(MainActivity.this, "Invalid credentials");
                                        }


                                    }else{
                                        Utils.dismissProgressDialog();
                                        Utils.showToast(MainActivity.this, task.getException().getLocalizedMessage());
                                    }
                                }
                            });
                }
            }
        });

        findViewById(R.id.main_btn_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}