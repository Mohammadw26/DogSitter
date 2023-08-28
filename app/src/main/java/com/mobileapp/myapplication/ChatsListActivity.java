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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobileapp.myapplication.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatsListActivity extends AppCompatActivity {

    private List<String> usersList = new ArrayList<>();
    private ListView chatsListLv;
    private String userId;

    private Map<String, String> nameIdsMap = new HashMap<>();
    List<String> documentIds = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats_list);

        if(getIntent().hasExtra("user_id")){
            userId = getIntent().getStringExtra("user_id");
        }

        Map<String, Object> update = new HashMap<>();
        update.put("chat_noti", false);
        FirebaseFirestore.getInstance().collection(getResources().getString(R.string.users_collection))
                .document(Utils.getPref(ChatsListActivity.this, "user_id", "")).update(update);


        chatsListLv = findViewById(R.id.lv_chatslist_chatlist);



        Utils.showProgressDialog(ChatsListActivity.this, "Getting chats\nPlease wait");
        FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.messages_realtime)).child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        usersList.clear();
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            documentIds.add(childSnapshot.getKey());
                        }

                        if(documentIds.size()>0){
                            FirebaseFirestore.getInstance().collection(getResources().getString(R.string.users_collection))
                                    .whereIn(FieldPath.documentId(), documentIds).get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isComplete()){


                                                for (DocumentSnapshot document: task.getResult()) {
                                                    usersList.add(document.getString("username"));
                                                    nameIdsMap.put(document.getString("username"),document.getId());

                                                }
                                                Utils.dismissProgressDialog();
                                                ArrayAdapter<String> itemsAdapter =
                                                        new ArrayAdapter<String>(ChatsListActivity.this, android.R.layout.simple_list_item_1, usersList);
                                                chatsListLv.setAdapter(itemsAdapter);
                                            }else{
                                                Utils.showToast(ChatsListActivity.this, task.getException().getLocalizedMessage());
                                            }
                                        }
                                    });

                        }else{
                            Utils.dismissProgressDialog();
                            Utils.showToast(ChatsListActivity.this, "No chat found");
                        }







                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Utils.showToast(ChatsListActivity.this, error.getMessage());
                    }
                });




//        FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.messages_realtime)).child(userId)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        usersList.clear();
//                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
//                            FirebaseFirestore.getInstance().collection(getResources().getString(R.string.users_collection)).document(childSnapshot.getKey()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                @Override
//                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                    if(task.isComplete()){
//                                        usersList.add(task.getResult().getString("username"));
//                                        nameIdsMap.put(task.getResult().getString("username"), childSnapshot.getKey());
//                                    }else{
//                                        Utils.showToast(ChatsListActivity.this, task.getException().getLocalizedMessage());
//                                    }
//                                }
//                            });
//                        }
//                        ArrayAdapter<String> itemsAdapter =
//                                new ArrayAdapter<String>(ChatsListActivity.this, android.R.layout.simple_list_item_1, usersList);
//                        chatsListLv.setAdapter(itemsAdapter);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Utils.showToast(ChatsListActivity.this, error.getMessage());
//                    }
//                });


        chatsListLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int size = nameIdsMap.size();
                startActivity(new Intent(ChatsListActivity.this, ChatActivity.class).putExtra("user_id", userId)
                        .putExtra("guest_id", nameIdsMap.get(usersList.get(position))));
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        ChatsListActivity.this.finish();
    }
}