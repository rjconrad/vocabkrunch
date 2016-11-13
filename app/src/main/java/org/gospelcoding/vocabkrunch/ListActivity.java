package org.gospelcoding.vocabkrunch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;



public class ListActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "org.gospelcoding.vocabkrunch.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //KrunchDBHelper dbHelper = new KrunchDBHelper(this); //Debug code
    }

    @Override
    protected void onResume(){
        super.onResume();
        updateList();
    }

    /** Called when user clicks the Send button */
    public void addWord(View view){
        //Intent intent = new Intent(this, ReviewWordActivity.class);
        //intent.putExtra(EXTRA_MESSAGE, message);
        //startActivity(intent);

        EditText newWordText = (EditText) findViewById(R.id.edit_message);
        String newWord = newWordText.getText().toString();

    }

    protected void updateList(){
        //Call this to update the vocab list
    }
}
