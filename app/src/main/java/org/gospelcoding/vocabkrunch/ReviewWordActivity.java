package org.gospelcoding.vocabkrunch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ReviewWordActivity extends AppCompatActivity {
    private String kword;
    private KrunchDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_word);

        dbHelper = new KrunchDBHelper(this);
        updateKWord();

//        Intent intent = getIntent();
//        String message = intent.getStringExtra(ListActivity.EXTRA_MESSAGE);
//        TextView textView = new TextView(this);
//        textView.setTextSize(40);
//        textView.setText(message);
//
//        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_review_word);
//        layout.addView(textView);
    }

    public void endReviewClick(View v){
        finish();
    }

    public void reviewAnotherWordClick(View v){
        updateKWord();
    }

    private void updateKWord(){
        kword = KrunchWord.getKrunchWordForReview(dbHelper);
        ((TextView) findViewById(R.id.review_kword)).setText(kword);
    }
}
