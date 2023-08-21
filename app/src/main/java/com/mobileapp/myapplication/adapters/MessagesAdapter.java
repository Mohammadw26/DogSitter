package com.mobileapp.myapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mobileapp.myapplication.R;
import com.mobileapp.myapplication.models.Message;


import java.util.ArrayList;

public class MessagesAdapter extends ArrayAdapter<Message> {

    private String userid;
    public MessagesAdapter(Context context, ArrayList<Message> messages, String userid) {
        super(context, 0, messages);
        this.userid = userid;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Message message = getItem(position);

        TextView msgTv, timestampTv;
        ImageView msgIv;
        if(message.senderId.equals(userid)){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_msg_right, parent, false);
            msgTv = convertView.findViewById(R.id.item_msgright_tv_msg);
            timestampTv = convertView.findViewById(R.id.item_msgright_tv_timestamp);
            msgIv = convertView.findViewById(R.id.item_msgright_iv_msg);
        }else{
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_msg_left, parent, false);
            msgTv = convertView.findViewById(R.id.item_msgleft_tv_msg);
            timestampTv = convertView.findViewById(R.id.item_msgleft_tv_timestamp);
            msgIv = convertView.findViewById(R.id.item_msgleft_iv_msg);
        }




        if(message.isImg){
            msgTv.setVisibility(View.GONE);
            msgIv.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(message.msg).placeholder(R.drawable.app_logo).into(msgIv);
        }else{
            msgIv.setVisibility(View.GONE);
            msgTv.setVisibility(View.VISIBLE);
            msgTv.setText(message.msg);
        }
        timestampTv.setText(message.timestamp);



        return convertView;
    }

}