package com.mobileapp.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobileapp.myapplication.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private Button signupBtn;
    private EditText usernameEt, passwordEt, emailEt;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        init();
    }

    private void init(){


        usernameEt = findViewById(R.id.signup_et_username);
        passwordEt = findViewById(R.id.signup_et_password);
        emailEt = findViewById(R.id.signup_et_email);

        signupBtn = findViewById(R.id.signup_btn_signup);

        findViewById(R.id.signup_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Utils.isEmpty(usernameEt) || Utils.isEmpty(passwordEt) || Utils.isEmpty(emailEt)){
                    Utils.showToast(SignupActivity.this, "Some fields are empty");
                }else if (!Utils.isValidEmail(emailEt.getText().toString())) {
                    Utils.showToast(SignupActivity.this, "Please enter valid email and password");
                }else if(!isLength(passwordEt.getText().toString())){
                    Utils.showToast(SignupActivity.this, "Password length must be at least 8");
                }else if(!isSpecial(passwordEt.getText().toString())){
                    Utils.showToast(SignupActivity.this, "Password must contains a special letter");
                }else if(!isUpper(passwordEt.getText().toString())){
                    Utils.showToast(SignupActivity.this, "Password must contains a Upper case letter");
                }else if(!isLower(passwordEt.getText().toString())){
                    Utils.showToast(SignupActivity.this, "Password must contains a Lower case letter");
                }else{

                    Utils.showProgressDialog(SignupActivity.this,"Signing up\nPlease wait");

                    mAuth.createUserWithEmailAndPassword(emailEt.getText().toString(), passwordEt.getText().toString())
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        sendVerificationEmail();
                                        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();


                                        Map<String, Object> user = new HashMap<>();
                                        user.put("username", usernameEt.getText().toString());
                                        user.put("password", passwordEt.getText().toString());
                                        user.put("email", emailEt.getText().toString());
                                        user.put("request_noti", false);
                                        user.put("myrequest_noti", false);
                                        user.put("chat_noti", false);
                                        user.put("rating", 0);
                                        user.put("ratingCounts", 0);

                                        FirebaseFirestore.getInstance().collection(getResources().getString(R.string.users_collection)).document(fUser.getUid())
                                                .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
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



                                    } else {
                                        Utils.dismissProgressDialog();
                                        Utils.showToast(SignupActivity.this, task.getException().getLocalizedMessage());


                                    }
                                }
                            });











                }

            }
        });
    }

    public boolean isLength(String passwordhere){
        if (passwordhere.length() < 8) {
            return false;
        }
        return true;
    }


    public boolean isSpecial(String passwordhere){
        Pattern specailCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);

        if (!specailCharPatten.matcher(passwordhere).find()) {
            return false;
        }
        return true;
    }


    public boolean isUpper(String passwordhere){
        Pattern UpperCasePatten = Pattern.compile("[A-Z ]");

        if (!UpperCasePatten.matcher(passwordhere).find()) {
            return false;
        }
        return true;
    }

    public boolean isLower(String passwordhere){
        Pattern lowerCasePatten = Pattern.compile("[a-z ]");

        if (!lowerCasePatten.matcher(passwordhere).find()) {
            return false;
        }
        return true;
    }


    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {;
                        if (task.isSuccessful()) {
                            //Utils.showAlert(AmbassadorSignupActivity.this, "Email sent",  "Please verify your email before signing in", 3000);
                            Utils.showToast(SignupActivity.this, "Verification email sent");
                        }  else
                        {
                            Utils.showToast(SignupActivity.this, task.getException().getLocalizedMessage());
                        }
                    }
                });
    }


}