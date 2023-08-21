package com.mobileapp.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobileapp.myapplication.adapters.MessagesAdapter;
import com.mobileapp.myapplication.databinding.ActivityChatBinding;
import com.mobileapp.myapplication.models.Message;
import com.mobileapp.myapplication.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
    };
    private List<Message> messages = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.messages_realtime)).child(getIntent().getStringExtra("user_id"))
                .child(getIntent().getStringExtra("guest_id"))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            Message message = childSnapshot.getValue(Message.class);
                            messages.add(message);
                        }
                        setListviewAdapter();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        binding.ivSendChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Utils.isEmpty(binding.etMsgChat)){
                    Message message = new Message(binding.etMsgChat.getText().toString(),
                            Utils.getCurrentDate() + " " + Utils.getCurrentTime(),
                            getIntent().getStringExtra("user_id"), getIntent().getStringExtra("guest_id"),
                            false);
                    FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.messages_realtime)).child(getIntent().getStringExtra("user_id"))
                            .child(getIntent().getStringExtra("guest_id")).push().setValue(message);
                    FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.messages_realtime)).child(getIntent().getStringExtra("guest_id"))
                            .child(getIntent().getStringExtra("user_id")).push().setValue(message);
                    binding.etMsgChat.setText("");
                }
            }
        });

        binding.ivCameraChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.hasPermissions(ChatActivity.this, PERMISSIONS)) {
                    ActivityCompat.requestPermissions(ChatActivity.this, PERMISSIONS, PERMISSION_ALL);
                }else{
                    pickImage();
                }
            }
        });
    }

    private void setListviewAdapter(){
        binding.lvMsgsChat.invalidateViews();
        MessagesAdapter adapter = new MessagesAdapter(ChatActivity.this, (ArrayList<Message>) messages, getIntent().getStringExtra("user_id"));
        binding.lvMsgsChat.setAdapter(adapter);
        binding.lvMsgsChat.setSelection(messages.size()-1);
    }

    public void pickImage(){

        ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if(resultCode == Activity.RESULT_OK) {
            uploadImageToFirebase(data.getData());
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(getResources().getString(R.string.images_storage)).child(imageUri.getLastPathSegment());

        imageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Image uploaded successfully, get the download URL
                        imageRef.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri downloadUrl) {
                                        String imageUrl = downloadUrl.toString();
                                        Message message = new Message(imageUrl,
                                                Utils.getCurrentDate() + " " + Utils.getCurrentTime(),
                                                getIntent().getStringExtra("user_id"), getIntent().getStringExtra("guest_id"),
                                                true);
                                        FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.messages_realtime)).child(getIntent().getStringExtra("user_id"))
                                                .child(getIntent().getStringExtra("guest_id")).push().setValue(message);
                                        FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.messages_realtime)).child(getIntent().getStringExtra("guest_id"))
                                                .child(getIntent().getStringExtra("user_id")).push().setValue(message);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Handle any errors
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors
                    }
                });
    }

    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Toast.makeText(this, String.valueOf(requestCode), Toast.LENGTH_LONG).show();
        switch (requestCode) {
            case 1:
                pickImage();
                break;

            default:
        }
    }





}