package com.mobileapp.myapplication.adapters;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobileapp.myapplication.AddNewReminderActivity;
import com.mobileapp.myapplication.DetailActivity;
import com.mobileapp.myapplication.R;
import com.mobileapp.myapplication.models.Message;
import com.mobileapp.myapplication.models.MyReminder;
import com.mobileapp.myapplication.models.Request;
import com.mobileapp.myapplication.models.RequestStatus;
import com.mobileapp.myapplication.models.UserModel;
import com.mobileapp.myapplication.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class RequestAdapter extends ArrayAdapter<Request> {
    Calendar mcurrentTime = Calendar.getInstance();

    public RequestAdapter(Context context, ArrayList<Request> requests) {
        super(context, 0, requests);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Request request = getItem(position);


        convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_request, parent, false);


        TextView datesTv = convertView.findViewById(R.id.item_requests_dates_tv);
        TextView statusTv = convertView.findViewById(R.id.item_requests_tv_status);
        TextView detailsTv = convertView.findViewById(R.id.item_requests_tv_details);

        CardView approveCv = convertView.findViewById(R.id.item_requests_approve_cv);
        CardView declineCv = convertView.findViewById(R.id.item_requests_decline_cv);
        CardView setReminderCv = convertView.findViewById(R.id.item_requests_addreminder_cv);


        setReminderCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(request.reminders!=null){
                    if(request.reminders.size()>0){
                        for (String reminder: request.reminders) {
                            String[] reminderSplit = reminder.split("##");
                            Utils.scheduleNotification(getContext(), reminderSplit[0], reminderSplit[1], reminderSplit[2]);
                            Utils.showToast(getContext(), "Reminder set for " + reminder.replace("$$", " "));
                            MyReminder myReminder = new MyReminder(Utils.getPref(getContext(), "user_id", ""), reminderSplit[0]
                                    +"\nDetails: " + request.getDetailInfo(), "Date: " + reminderSplit[1] + "  Time: " + reminderSplit[2],
                                    "", new ArrayList<>());

                            FirebaseFirestore.getInstance().collection(getContext().getResources().getString(R.string.allreminderss_collection)).add(myReminder);
                        }
                        Utils.showToast(getContext(), "All reminder set successfully");

                    }else{
                        Utils.showToast(getContext(), "No reminder added by client");

                    }
                }else{
                    Utils.showToast(getContext(), "No reminder added by client");
                }
//                showReminderDialog( request);
            }
        });

        LinearLayout btnsLl = convertView.findViewById(R.id.item_requests_ll_buttons);



        datesTv.setText("From " + request.fromDate + "\nTo " + request.toDate);
        statusTv.setText(request.status);
        detailsTv.setText(request.getDetailInfo());
        if(request.status.equals(RequestStatus.PENDING.toString())){
            btnsLl.setVisibility(View.VISIBLE);







            approveCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateStatus(request.requestId, RequestStatus.ACCEPTED.toString(), request.requesterId);
                }
            });

            declineCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateStatus(request.requestId, RequestStatus.DECLINED.toString(), request.requesterId);
                }
            });

        }else{
            btnsLl.setVisibility(View.GONE);
        }



        return convertView;
    }


    private void updateStatus(String requestId, String updatedStatus, String requesterId){
        Map<String, Object> update = new HashMap<>();
        update.put("status", updatedStatus);
        Utils.showProgressDialog(getContext(), "Updating status\nPlease wait");
        FirebaseFirestore.getInstance().collection(getContext().getResources().getString(R.string.requests_collection))
                .document(requestId).update(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            FirebaseFirestore.getInstance().collection(getContext().getResources().getString(R.string.users_collection))
                                    .document(requesterId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()){
                                                Utils.dismissProgressDialog();
                                                UserModel userModel = task.getResult().toObject(UserModel.class);
                                                userModel.myrequest_noti = true;
                                                FirebaseFirestore.getInstance().collection(getContext().getResources().getString(R.string.users_collection))
                                                        .document(requesterId).set(userModel);
                                                Utils.showToast(getContext(), "Status updated successfully");
                                                ((Activity) getContext()).onBackPressed();
                                            }
                                        }
                                    });



                        }else{
                            Utils.dismissProgressDialog();
                            Utils.showToast(getContext(), task.getException().getLocalizedMessage());
                        }
                    }
                });
    }

    private void showReminderDialog( Request request){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.reminder,
                (ViewGroup) ((Activity) getContext()).getWindow().getDecorView().getRootView(), false);


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
                new DatePickerDialog(getContext(), dateReminder, Utils.myCalendar.get(Calendar.YEAR),
                        Utils.myCalendar.get(Calendar.MONTH), Utils.myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        int fHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int fMinute = mcurrentTime.get(Calendar.MINUTE);

        timeEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
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
                    Utils.showToast(getContext(), "Please set a message for reminder");
                else if(Utils.isEmpty(dateEt))
                    Utils.showToast(getContext(), "Please select a date for reminder");
                else if(Utils.isEmpty(dateEt))
                    Utils.showToast(getContext(), "Please select a time for reminder");
                else{
                    Utils.showToast(getContext(), "Reminder set successfully");
                    Utils.scheduleNotification(getContext(), messageEt.getText().toString() +"\nDetails: " + request.getDetailInfo(), dateEt.getText().toString(), timeEt.getText().toString());
                    MyReminder myReminder = new MyReminder(Utils.getPref(getContext(), "user_id", ""), messageEt.getText().toString() +"\nDetails: " + request.getDetailInfo(), "Date: " + dateEt.getText().toString() + "  Time: " + timeEt.getText().toString(),
                            null, null);
                    FirebaseFirestore.getInstance().collection(getContext().getResources().getString(R.string.allreminderss_collection)).add(myReminder);
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