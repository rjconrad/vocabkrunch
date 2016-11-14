package org.gospelcoding.vocabkrunch;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;



public class ListActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "org.gospelcoding.vocabkrunch.MESSAGE";
    private KrunchDBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        dbHelper = new KrunchDBHelper(this);
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
        KrunchWord kword = new KrunchWord(newWord);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        kword.addToDatabase(db);
    }

    protected void updateList(){
        //Call this to update the vocab list
    }
}
