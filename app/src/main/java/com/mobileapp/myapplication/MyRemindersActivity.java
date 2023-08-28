package com.mobileapp.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MyRemindersActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reminders);


        findViewById(R.id.myreminders_btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyRemindersActivity.this, AddNewReminderActivity.class));
            }
        });

        findViewById(R.id.myreminders_btn_see).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyRemindersActivity.this, MyRemindersListActivity.class)
                        .putExtra("is_my", true));
            }
        });

        findViewById(R.id.allreminders_btn_see).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyRemindersActivity.this, MyRemindersListActivity.class)
                        .putExtra("is_my", false));
            }
        });

    }




}