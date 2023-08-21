package com.mobileapp.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mobileapp.myapplication.models.FAQ;
import com.mobileapp.myapplication.models.FAQHolder;
import com.mobileapp.myapplication.utils.Utils;

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.ArrayList;
import java.util.List;

public class AnswersActivity extends AppCompatActivity {

    private ListView answersLv;

    private List<String> answers = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers);

        answersLv = findViewById(R.id.answers_lv_answers);
        FAQHolder faqHolder = new FAQHolder();

        if(getIntent().getBooleanExtra("is_cat", true)){
            for (FAQ faq: faqHolder.faqList) {
                if(faq.category.equals(getIntent().getStringExtra("cat_name"))){
                    answers.add("\nQuestion: " + faq.question
                            +"\n\nAnswer: " + faq.answer +" \n");
                }
            }
        }else{
            String query = getIntent().getStringExtra("query");
            for (FAQ faq: faqHolder.faqList) {
                if(areStrings60PercentSimilar(faq.question, query) || areStrings60PercentSimilar(faq.answer, query)){
                    answers.add("\nQuestion: " + faq.question
                            +"\n\nAnswer: " + faq.answer +" \n");
                }
            }
        }

        if(answers.size()>0){
            ArrayAdapter<String> itemsAdapter =
                    new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, answers);
            answersLv.setAdapter(itemsAdapter);
        }else{
            Utils.showToast(AnswersActivity.this, "No record found");
        }

    }

    public boolean areStrings60PercentSimilar(String str1, String str2) {
        LevenshteinDistance levenshteinDistance = LevenshteinDistance.getDefaultInstance();
        int distance = levenshteinDistance.apply(str1, str2);

        // Calculate the length of the longer string
        int maxLength = Math.max(str1.length(), str2.length());

        // Calculate the percentage similarity
        double similarityPercentage = (1 - (double) distance / maxLength) * 100;

        return similarityPercentage > 20.0;
    }

}