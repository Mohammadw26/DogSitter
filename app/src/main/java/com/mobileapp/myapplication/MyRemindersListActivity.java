package com.mobileapp.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobileapp.myapplication.models.MyReminder;
import com.mobileapp.myapplication.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MyRemindersListActivity extends AppCompatActivity {


    private ListView listView;
    List<MyReminder> myDogsList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reminders_list);

        listView = findViewById(R.id.myreminderslist_lv_list);



        Utils.showProgressDialog(MyRemindersListActivity.this, "Getting data\nPlease wait");


        FirebaseFirestore.getInstance().collection(getResources().getString(getIntent().getBooleanExtra("is_my", true) ? R.string.myreminderss_collection : R.string.allreminderss_collection))
                .whereEqualTo("ownerId", Utils.getPref(MyRemindersListActivity.this, "user_id", ""))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Utils.dismissProgressDialog();
                        List<String> listData = new ArrayList<>();
                        for (DocumentSnapshot document: task.getResult()) {
                            MyReminder myReminder = document.toObject(MyReminder.class);
                            myDogsList.add(myReminder);
                            listData.add(getIntent().getBooleanExtra("is_my", true) ? myReminder.getInfo() : myReminder.getAllReminderDetail());
                        }
                        setListviewAdapter(listData);
                    }
                });




    }

    private void setListviewAdapter(List<String> items){
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(MyRemindersListActivity.this, R.layout.mytextview, items);
        listView.setAdapter(itemsAdapter);
    }
}