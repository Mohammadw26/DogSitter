package com.mobileapp.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import com.mobileapp.myapplication.models.FAQHolder;
import com.mobileapp.myapplication.utils.Utils;

public class ChatbotActivity extends AppCompatActivity {

    EditText queryEt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        queryEt = findViewById(R.id.query);

        findViewById(R.id.general).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatbotActivity.this, AnswersActivity.class)
                        .putExtra("is_cat", true)
                        .putExtra("cat_name", "All"));
            }
        });

        findViewById(R.id.vaccines).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatbotActivity.this, AnswersActivity.class)
                        .putExtra("is_cat", true)
                        .putExtra("cat_name", "Vaccines"));
            }
        });

        findViewById(R.id.food).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatbotActivity.this, AnswersActivity.class)
                        .putExtra("is_cat", true)
                        .putExtra("cat_name", "Food"));
            }
        });

        findViewById(R.id.sickness).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatbotActivity.this, AnswersActivity.class)
                        .putExtra("is_cat", true)
                        .putExtra("cat_name", "Sickness"));
            }
        });

        findViewById(R.id.dia).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatbotActivity.this, AnswersActivity.class)
                        .putExtra("is_cat", true)
                        .putExtra("cat_name", "Diagnosis"));
            }
        });

        findViewById(R.id.othertopics).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatbotActivity.this, AnswersActivity.class)
                        .putExtra("is_cat", true)
                        .putExtra("cat_name", "Other Topics"));
            }
        });

        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.isEmpty(queryEt)){
                    Utils.showToast(ChatbotActivity.this, "Enter your question first");
                }else{
                    startActivity(new Intent(ChatbotActivity.this, AnswersActivity.class)
                            .putExtra("is_cat", false)
                            .putExtra("query", queryEt.getText().toString()));
                }

            }
        });


    }
}