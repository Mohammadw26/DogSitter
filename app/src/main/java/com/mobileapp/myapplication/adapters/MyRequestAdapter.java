package com.mobileapp.myapplication.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobileapp.myapplication.R;
import com.mobileapp.myapplication.models.Request;
import com.mobileapp.myapplication.models.RequestStatus;
import com.mobileapp.myapplication.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MyRequestAdapter extends ArrayAdapter<Request> {

    public MyRequestAdapter(Context context, ArrayList<Request> requests) {
        super(context, 0, requests);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Request request = getItem(position);


        convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_request, parent, false);


        TextView datesTv = convertView.findViewById(R.id.item_requests_dates_tv);
        TextView statusTv = convertView.findViewById(R.id.item_requests_tv_status);
        TextView detailsTv = convertView.findViewById(R.id.item_requests_tv_details);
        TextView btnTv = convertView.findViewById(R.id.item_requests_approve_tv);
        CardView approveCv = convertView.findViewById(R.id.item_requests_approve_cv);
        CardView declineCv = convertView.findViewById(R.id.item_requests_decline_cv);
        LinearLayout btnsLl = convertView.findViewById(R.id.item_requests_ll_buttons);
        RatingBar ratingsRb = convertView.findViewById(R.id.item_requests_rating_rb);


        datesTv.setText("From " + request.fromDate + "\nTo " + request.toDate);
        statusTv.setText(request.status);
        detailsTv.setText(request.details);
        btnsLl.setVisibility(View.VISIBLE);


        if(request.status.equals(RequestStatus.ACCEPTED.toString())){
            btnsLl.setVisibility(View.VISIBLE);
            declineCv.setVisibility(View.GONE);
            btnTv.setText("MARK AS COMPLETED");

            ratingsRb.setVisibility(View.VISIBLE);
            approveCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateStatus(request.requestId, RequestStatus.COMPLETED.toString(), ratingsRb.getRating(), request.ownerId);


                }
            });
        }else{
            btnsLl.setVisibility(View.GONE);
        }


        return convertView;
    }

    private void updateStatus(String requestId, String updatedStatus, float newRating, String ownerId){
        Map<String, Object> update = new HashMap<>();
        update.put("status", updatedStatus);
        Utils.showProgressDialog(getContext(), "Updating status\nPlease wait");
        FirebaseFirestore.getInstance().collection(getContext().getResources().getString(R.string.requests_collection))
                .document(requestId).update(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){





                            if(newRating>0){
                                Map<String, Object> update = new HashMap<>();
                                update.put("rating", FieldValue.increment(newRating));
                                update.put("ratingCounts", FieldValue.increment(1));
                                FirebaseFirestore.getInstance().collection(getContext().getResources().getString(R.string.users_collection))
                                        .document(ownerId).update(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Utils.dismissProgressDialog();
                                                if(task.isSuccessful()){
                                                    Utils.showToast(getContext(), "Status updated successfully");
                                                    ((Activity) getContext()).onBackPressed();
                                                }else{
                                                    Utils.showToast(getContext(), task.getException().getLocalizedMessage());

                                                }
                                            }
                                        });
                            }else{
                                Utils.showToast(getContext(), "Status updated successfully");
                                ((Activity) getContext()).onBackPressed();
                            }



                        }else{
                            Utils.dismissProgressDialog();
                            Utils.showToast(getContext(), task.getException().getLocalizedMessage());
                        }
                    }
                });
    }
}