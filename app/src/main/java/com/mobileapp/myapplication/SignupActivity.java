package com.mobileapp.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobileapp.myapplication.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private Button signupBtn;
    private EditText usernameEt, passwordEt, emailEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        init();
    }

    private void init(){


        usernameEt = findViewById(R.id.signup_et_username);
        passwordEt = findViewById(R.id.signup_et_password);
        emailEt = findViewById(R.id.signup_et_email);

        signupBtn = findViewById(R.id.signup_btn_signup);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Utils.isEmpty(usernameEt) || Utils.isEmpty(passwordEt) || Utils.isEmpty(emailEt)){
                    Utils.showToast(SignupActivity.this, "Some fields are empty");
                }else{
                    Utils.showProgressDialog(SignupActivity.this,"Signing up\nPlease wait");
                    Map<String, Object> user = new HashMap<>();
                    user.put("username", usernameEt.getText().toString());
                    user.put("password", passwordEt.getText().toString());
                    user.put("email", emailEt.getText().toString());
                    user.put("rating", 0);
                    FirebaseFirestore.getInstance().collection(getResources().getString(R.string.users_collection)).add(user)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    Utils.dismissProgressDialog();
                                    if(task.isSuccessful()){
                                        Utils.showToast(SignupActivity.this, "Signup successfully");
                                        onBackPressed();
                                    }else{
                                        Utils.showToast(SignupActivity.this, task.getException().getLocalizedMessage());
                                        onBackPressed();
                                    }
                                }
                            });
                }

            }
        });
    }
}