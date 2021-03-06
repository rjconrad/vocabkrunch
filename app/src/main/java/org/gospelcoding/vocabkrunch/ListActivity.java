package org.gospelcoding.vocabkrunch;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class ListActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "org.gospelcoding.vocabkrunch.MESSAGE";
    private KrunchDBHelper dbHelper;
    private SimpleCursorAdapter listViewCursorAdapter;
    private Cursor dbCursor;

//    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            if(view instanceof CheckBox){
//                boolean isLearned = ((CheckBox) view).isChecked();
//                KrunchWord.updateLearned(dbHelper.getWritableDatabase(), id, isLearned);
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        dbHelper = new KrunchDBHelper(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        krunchList();
        //justForFun();
        setAlarmsIfNecessary();
        //justForFun();
    }

    public void addWord(View view){
        //Intent intent = new Intent(this, ReviewWordActivity.class);
        //intent.putExtra(EXTRA_MESSAGE, message);
        //startActivity(intent);

        EditText newWordText = (EditText) findViewById(R.id.edit_message);
        String newWord = newWordText.getText().toString();
        KrunchWord kword = new KrunchWord(newWord);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        kword.addToDatabase(db);
        krunchList();
        newWordText.setText("");
    }

    public void doAReviewClick(View v){
        Intent intent = new Intent(this, ReviewWordActivity.class);
        startActivity(intent);
    }

    public void setLearned(View v){
        CheckBox check = (CheckBox) v;
        boolean isChecked = check.isChecked();
        long id = (long) check.getTag();
        KrunchWord.updateLearned(dbHelper.getWritableDatabase(), id, isChecked);
        krunchList();
    }

    protected void krunchList(){
        dbCursor = KrunchWord.getAllKrunchWords(dbHelper.getReadableDatabase());
        String[] fromColumns = {KrunchWord.WORD_COLUMN_NAME, KrunchWord.LEARNED_COLUMN_NAME, KrunchWord.REVIEWS_COUNT_COLUMN_NAME};
        int[] toViews = {R.id.listed_word_text, R.id.listed_word_learned_check, R.id.listed_word_review_number};
        listViewCursorAdapter = new SimpleCursorAdapter(this, R.layout.listed_krunch_word, dbCursor, fromColumns, toViews, 0);
        listViewCursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                //only for the checkbox
                if(columnIndex != cursor.getColumnIndex(KrunchWord.LEARNED_COLUMN_NAME))
                    return false;
                int learnedValue = cursor.getInt(cursor.getColumnIndex(KrunchWord.LEARNED_COLUMN_NAME));
                boolean isLearned = (learnedValue==KrunchWord.LEARNED_TRUE);
                view.setTag(cursor.getLong(cursor.getColumnIndex(KrunchWord._ID)));
                ((CheckBox) view).setChecked(isLearned);
                return true;
            }
        });
        ListView krunchList = (ListView) findViewById(R.id.krunch_word_list_view);
        //krunchList.setOnItemClickListener(itemClickListener);
        krunchList.setAdapter(listViewCursorAdapter);
    }

    private void setAlarmsIfNecessary(){
        boolean alarmSet = (PendingIntent.getBroadcast(this,
                0,
                new Intent(this, KrunchAlarmReceiver.class).setAction(KrunchAlarmReceiver.REPEATING_ALARM_CODE),
                PendingIntent.FLAG_NO_CREATE) != null);
        if(!alarmSet){
            KrunchAlarmReceiver.setTheRepeatingAlarm(this);
        }
        else
            KrunchAlarmReceiver.setTheRepeatingAlarm(this);
    }

    private void justForFun(){
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmMgr.cancel(PendingIntent.getBroadcast(this,
                0,
                new Intent(this, KrunchAlarmReceiver.class).setAction(KrunchAlarmReceiver.REPEATING_ALARM_CODE),
                0));

        alarmMgr.cancel(PendingIntent.getBroadcast(this,
                0,
                new Intent(this, KrunchAlarmReceiver.class).setAction(KrunchAlarmReceiver.ONE_TIME_ALARM_CODE),
                0));
    }

}
