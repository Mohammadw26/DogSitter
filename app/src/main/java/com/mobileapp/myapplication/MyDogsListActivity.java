package com.mobileapp.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobileapp.myapplication.models.MyDog;
import com.mobileapp.myapplication.models.Service;
import com.mobileapp.myapplication.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MyDogsListActivity extends AppCompatActivity {


    private ListView listView;
    List<MyDog> myDogsList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_dogs_list);

        listView = findViewById(R.id.mydogslist_lv_list);



        Utils.showProgressDialog(MyDogsListActivity.this, "Getting data\nPlease wait");


        FirebaseFirestore.getInstance().collection(getResources().getString(R.string.mydogs_collection))
                .whereEqualTo("ownerId", Utils.getPref(MyDogsListActivity.this, "user_id", ""))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Utils.dismissProgressDialog();
                        List<String> listData = new ArrayList<>();
                        for (DocumentSnapshot document: task.getResult()) {
                            MyDog myDog = document.toObject(MyDog.class);
                            myDogsList.add(myDog);
                            listData.add(myDog.getInfo());
                        }
                        setListviewAdapter(listData);
                    }
                });




    }

    private void setListviewAdapter(List<String> items){
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(MyDogsListActivity.this, R.layout.mytextview, items);
        listView.setAdapter(itemsAdapter);
    }
}