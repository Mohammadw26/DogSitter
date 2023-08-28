package com.mobileapp.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobileapp.myapplication.models.MyReminder;
import com.mobileapp.myapplication.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddNewReminderActivity extends AppCompatActivity {

    private EditText nameEt, detailsEt;
    private TextView remindersTv;
    private Button addNewReminderBtn, saveReminderBtn;

    private String reminders = "";

    Calendar mcurrentTime = Calendar.getInstance();

    private CheckBox dogCb, catCb, birdCb, babiesCb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_reminder);

        nameEt = findViewById(R.id.add_name_et);
        detailsEt = findViewById(R.id.add_details_et);
        remindersTv = findViewById(R.id.add_reminders_tv);
        addNewReminderBtn = findViewById(R.id.add_addreminder_btn);
        saveReminderBtn = findViewById(R.id.add_savedog_btn);

        dogCb = findViewById(R.id.addreminder_cb_dogs);
        catCb = findViewById(R.id.addreminder_cb_cats);
        birdCb = findViewById(R.id.addreminder_cb_birds);
        babiesCb = findViewById(R.id.addreminder_cb_babies);

        addNewReminderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReminderDialog();
            }
        });



        saveReminderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!dogCb.isChecked() && !catCb.isChecked() && !birdCb.isChecked() && !babiesCb.isChecked()){
                    Utils.showToast(AddNewReminderActivity.this, "Select each category at least");
                }else if(Utils.isEmpty(nameEt)){
                    Utils.showToast(AddNewReminderActivity.this, "Please enter title for reference");
                }else{
                    Utils.showProgressDialog(AddNewReminderActivity.this, "Adding new my reminder\nPlease wait");
                    DocumentReference requestRef = FirebaseFirestore.getInstance().collection(getResources().getString(R.string.myreminderss_collection)).document();

                    List<String> categories = new ArrayList<>();
                    if(dogCb.isChecked()){
                        categories.add("Dogs");
                    }
                    if(catCb.isChecked()){
                        categories.add("Cats");
                    }
                    if(birdCb.isChecked()){
                        categories.add("Birds");
                    }
                    if(babiesCb.isChecked()){
                        categories.add("Babies");
                    }

                    MyReminder myReminder = new MyReminder(Utils.getPref(AddNewReminderActivity.this, "user_id", ""), nameEt.getText().toString(), detailsEt.getText().toString(),
                            reminders, categories);
                    requestRef.set(myReminder).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Utils.dismissProgressDialog();
                            if(task.isSuccessful()){
                                Utils.showToast(AddNewReminderActivity.this, "My reminder added successfully");
                                onBackPressed();
                            }else{
                                Utils.showToast(AddNewReminderActivity.this, task.getException().getLocalizedMessage());
                            }

                        }
                    });
                }



            }
        });



    }

    private void showReminderDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(AddNewReminderActivity.this);

        View viewInflated = LayoutInflater.from(AddNewReminderActivity.this).inflate(R.layout.reminder,
                (ViewGroup) getWindow().getDecorView().getRootView(), false);


        EditText messageEt = viewInflated.findViewById(R.id.reminder_et_message);

        EditText dateEt = viewInflated.findViewById(R.id.reminder_et_date);
        EditText timeEt = viewInflated.findViewById(R.id.reminder_et_time);



        DatePickerDialog.OnDateSetListener dateReminder = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                Utils.myCalendar.set(Calendar.YEAR, year);
                Utils.myCalendar.set(Calendar.MONTH, month);
                Utils.myCalendar.set(Calendar.MONTH, month);
                Utils.myCalendar.set(Calendar.DAY_OF_MONTH, day);
                Utils.updateDateEt(dateEt);

            }
        };
        dateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AddNewReminderActivity.this, dateReminder, Utils.myCalendar.get(Calendar.YEAR),
                        Utils.myCalendar.get(Calendar.MONTH), Utils.myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        int fHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int fMinute = mcurrentTime.get(Calendar.MINUTE);

        timeEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddNewReminderActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        timeEt.setText(selectedHour+":"+selectedMinute);
                    }
                }, fHour, fMinute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Reminder Time");
                mTimePicker.show();
            }
        });




        builder.setPositiveButton("Set Reminder", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(Utils.isEmpty(messageEt))
                    Utils.showToast(AddNewReminderActivity.this, "Please set a message for reminder");
                else if(Utils.isEmpty(dateEt))
                    Utils.showToast(AddNewReminderActivity.this, "Please select a date for reminder");
                else{
                    Utils.scheduleNotification(AddNewReminderActivity.this, messageEt.getText().toString() +"\nDog name: " + nameEt.getText().toString(), dateEt.getText().toString(), Utils.isEmpty(timeEt) ? "8:0" : timeEt.getText().toString());
                    reminders+="\n"+messageEt.getText().toString() + " - " + dateEt.getText().toString() + " - " + timeEt.getText().toString();
                    remindersTv.setText(reminders);

                }


            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setView(viewInflated);

        builder.show();
    }
}