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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobileapp.myapplication.models.MyDog;
import com.mobileapp.myapplication.utils.Utils;

import java.util.Calendar;

public class AddNewDogActivity extends AppCompatActivity {

    private EditText nameEt, detailsEt;
    private TextView remindersTv;
    private Button addNewReminderBtn, saveDogBtn;

    private String reminders = "";

    Calendar mcurrentTime = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_dog);

        nameEt = findViewById(R.id.add_name_et);
        detailsEt = findViewById(R.id.add_details_et);
        remindersTv = findViewById(R.id.add_reminders_tv);
        addNewReminderBtn = findViewById(R.id.add_addreminder_btn);
        saveDogBtn = findViewById(R.id.add_savedog_btn);

        addNewReminderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReminderDialog();
            }
        });



        saveDogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Utils.isEmpty(nameEt)){
                    Utils.showToast(AddNewDogActivity.this, "Please enter dog name for reference");
                }else{
                    Utils.showProgressDialog(AddNewDogActivity.this, "Adding new dog\nPlease wait");
                    DocumentReference requestRef = FirebaseFirestore.getInstance().collection(getResources().getString(R.string.mydogs_collection)).document();

                    MyDog myDog = new MyDog(Utils.getPref(AddNewDogActivity.this, "user_id", ""), nameEt.getText().toString(), detailsEt.getText().toString(),
                            reminders);
                    requestRef.set(myDog).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Utils.dismissProgressDialog();
                            if(task.isSuccessful()){
                                Utils.showToast(AddNewDogActivity.this, "Dog added successfully");
                                onBackPressed();
                            }else{
                                Utils.showToast(AddNewDogActivity.this, task.getException().getLocalizedMessage());
                            }

                        }
                    });
                }



            }
        });



    }

    private void showReminderDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(AddNewDogActivity.this);

        View viewInflated = LayoutInflater.from(AddNewDogActivity.this).inflate(R.layout.reminder,
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
                new DatePickerDialog(AddNewDogActivity.this, dateReminder, Utils.myCalendar.get(Calendar.YEAR),
                        Utils.myCalendar.get(Calendar.MONTH), Utils.myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        int fHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int fMinute = mcurrentTime.get(Calendar.MINUTE);

        timeEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddNewDogActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                    Utils.showToast(AddNewDogActivity.this, "Please set a message for reminder");
                else if(Utils.isEmpty(dateEt))
                    Utils.showToast(AddNewDogActivity.this, "Please select a date for reminder");
                else if(Utils.isEmpty(dateEt))
                    Utils.showToast(AddNewDogActivity.this, "Please select a time for reminder");
                else{

                    Utils.scheduleNotification(AddNewDogActivity.this, messageEt.getText().toString() +"\nDog name: " + nameEt.getText().toString(), dateEt.getText().toString(), timeEt.getText().toString());
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