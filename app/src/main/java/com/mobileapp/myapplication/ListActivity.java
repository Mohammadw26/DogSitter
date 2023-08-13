package com.mobileapp.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobileapp.myapplication.models.Service;
import com.mobileapp.myapplication.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private ListView listView;
    List<Service> servicesList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = findViewById(R.id.list_lv_list);


        findViewById(R.id.list_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Utils.showProgressDialog(ListActivity.this, "Getting data\nPlease wait");


        FirebaseFirestore.getInstance().collection(getResources().getString(R.string.data_collection))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Utils.dismissProgressDialog();
                        List<String> listData = new ArrayList<>();
                        for (DocumentSnapshot document: task.getResult()) {
                            Service service = document.toObject(Service.class);
                            service.serviceId = document.getId();
                            servicesList.add(service);
                            listData.add(service.getServiceInfo());
                        }
                        setListviewAdapter(listData);
                    }
                });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(ListActivity.this, DetailActivity.class).putExtra("service_id", servicesList.get(position).serviceId));
            }
        });


    }

    private void setListviewAdapter(List<String> items){
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(ListActivity.this, R.layout.mytextview, items);
        listView.setAdapter(itemsAdapter);
    }
}