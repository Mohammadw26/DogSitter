package com.mobileapp.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobileapp.myapplication.adapters.MyRequestAdapter;
import com.mobileapp.myapplication.adapters.RequestAdapter;
import com.mobileapp.myapplication.databinding.ActivityMyRequestsBinding;
import com.mobileapp.myapplication.databinding.ActivityRequestBinding;
import com.mobileapp.myapplication.models.Request;
import com.mobileapp.myapplication.models.RequestStatus;
import com.mobileapp.myapplication.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MyRequestsActivity extends AppCompatActivity {

    private ActivityMyRequestsBinding binding;

    private List<Request> pendingRequestsList = new ArrayList<>();
    private List<Request> acceptedRequestsList = new ArrayList<>();
    private List<Request> declinedRequestsList = new ArrayList<>();
    private List<Request> completedRequestsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMyRequestsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.rgStatusMyrequests.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.rb_pending_myrequest)
                    setLvAdapter(RequestStatus.PENDING);
                else if(checkedId==R.id.rb_accepted_myrequest)
                    setLvAdapter(RequestStatus.ACCEPTED);
                else if(checkedId==R.id.rb_declined_myrequest)
                    setLvAdapter(RequestStatus.DECLINED);
                else if(checkedId==R.id.rb_completed_myrequest)
                    setLvAdapter(RequestStatus.COMPLETED);
            }
        });


        Utils.showProgressDialog(MyRequestsActivity.this, "Getting requests\nPlease wait");
        FirebaseFirestore.getInstance().collection(getResources().getString(R.string.requests_collection)).whereEqualTo("requesterId", Utils.getPref(MyRequestsActivity.this, "user_id", ""))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Utils.dismissProgressDialog();
                        if(task.isSuccessful()){
                            for (DocumentSnapshot documentSnapshot: task.getResult()) {
                                Request request = documentSnapshot.toObject(Request.class);
                                if(request.status.equals(RequestStatus.PENDING.toString()))
                                    pendingRequestsList.add(request);
                                else if(request.status.equals(RequestStatus.ACCEPTED.toString()))
                                    acceptedRequestsList.add(request);
                                else if(request.status.equals(RequestStatus.DECLINED.toString()))
                                    declinedRequestsList.add(request);
                                else if(request.status.equals(RequestStatus.COMPLETED.toString()))
                                    completedRequestsList.add(request);

                            }
                            setLvAdapter(RequestStatus.PENDING);
                        }else{
                            Utils.showToast(MyRequestsActivity.this, task.getException().getLocalizedMessage());
                        }
                    }
                });



    }

    private void setLvAdapter(RequestStatus requestStatus){
        binding.lvRequestsMyrequests.invalidateViews();
        MyRequestAdapter requestAdapter = null;
        if(requestStatus==RequestStatus.PENDING)
            requestAdapter = new MyRequestAdapter(MyRequestsActivity.this, (ArrayList<Request>) pendingRequestsList);
        else if(requestStatus==RequestStatus.ACCEPTED)
            requestAdapter = new MyRequestAdapter(MyRequestsActivity.this, (ArrayList<Request>) acceptedRequestsList);
        else if(requestStatus==RequestStatus.DECLINED)
            requestAdapter = new MyRequestAdapter(MyRequestsActivity.this, (ArrayList<Request>) declinedRequestsList);
        else if(requestStatus==RequestStatus.COMPLETED)
            requestAdapter = new MyRequestAdapter(MyRequestsActivity.this, (ArrayList<Request>) completedRequestsList);

        binding.lvRequestsMyrequests.setAdapter(requestAdapter);
    }
}