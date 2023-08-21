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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobileapp.myapplication.utils.Utils;

public class MainActivity extends AppCompatActivity {




    private TextView signupTv, forgetTv;
    private EditText emailEt, passwordEt;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        signupTv = findViewById(R.id.main_tv_signup);
        emailEt = findViewById(R.id.main_et_email);
        passwordEt = findViewById(R.id.main_et_password);
        forgetTv = findViewById(R.id.main_tv_forget);

        forgetTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.isEmpty(emailEt)){
                    Utils.showToast(MainActivity.this, "Enter your email first");
                } else if (!Utils.isValidEmail(emailEt.getText().toString())) {
                    Utils.showToast(MainActivity.this, "Please enter a valid email");
                } else{
                    Utils.showProgressDialog(MainActivity.this, "Resetting password");
                    mAuth.sendPasswordResetEmail(emailEt.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Utils.dismissProgressDialog();

                                    if (task.isSuccessful()) {
                                        Utils.showToast(MainActivity.this, "Password reset email sent");
                                    }else{
                                        Utils.showToast(MainActivity.this, task.getException().toString());

                                    }
                                }
                            });

                }
            }
        });

        signupTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SignupActivity.class));
            }
        });

        findViewById(R.id.main_btn_signin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utils.isEmpty(emailEt) || Utils.isEmpty(passwordEt)){
                    Utils.showToast(MainActivity.this, "Some fields are empty");
                }else if (!Utils.isValidEmail(emailEt.getText().toString())) {
                    Utils.showToast(MainActivity.this, "Please enter valid email and password");
                }else{


                    mAuth.signInWithEmailAndPassword(emailEt.getText().toString(), passwordEt.getText().toString())
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {

                                        Utils.dismissProgressDialog();
                                        Utils.showToast(MainActivity.this, task.getException().getLocalizedMessage());

                                    } else {
                                        checkIfEmailVerified();
                                    }
                                }
                            });

















                    Utils.showProgressDialog(MainActivity.this, "Signing in\nPlease wait");
//                    FirebaseFirestore.getInstance().collection(getResources().getString(R.string.users_collection))
//                            .whereEqualTo("username", emailEt.getText().toString())
//                            .whereEqualTo("password", passwordEt.getText().toString())
//                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                @Override
//                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                    if(task.isSuccessful()){
//                                        Utils.dismissProgressDialog();
//                                        if(task.getResult().size()>0){
//                                            QueryDocumentSnapshot documentSnapshot = task.getResult().iterator().next();
//                                            Utils.setPref(MainActivity.this, "user_id", documentSnapshot.getId());
//
//                                            startActivity(new Intent(MainActivity.this, HomeActivity.class)
//                                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
//                                        }else{
//                                            Utils.showToast(MainActivity.this, "Invalid credentials");
//                                        }
//
//
//                                    }else{
//                                        Utils.dismissProgressDialog();
//                                        Utils.showToast(MainActivity.this, task.getException().getLocalizedMessage());
//                                    }
//                                }
//                            });
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

    private void checkIfEmailVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isEmailVerified())
        {
            Utils.setPref(MainActivity.this, "user_id", user.getUid());
            Utils.dismissProgressDialog();
            startActivity(new Intent(MainActivity.this, HomeActivity.class)
                                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));


        }
        else
        {

            sendVerificationEmail();



        }
    }

    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Utils.dismissProgressDialog();

                        if (task.isSuccessful()) {

                            Utils.showToast(MainActivity.this, "Verification email sent again");
                            Utils.showToast(MainActivity.this, "Verify your email first");
                            FirebaseAuth.getInstance().signOut();

                        }  else
                        {
                            Utils.showToast(MainActivity.this, task.getException().getLocalizedMessage());
                        }
                    }
                });
    }
}