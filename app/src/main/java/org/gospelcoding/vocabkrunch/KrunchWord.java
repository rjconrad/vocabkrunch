package org.gospelcoding.vocabkrunch;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Rick on 11/13/2016.
 */

public class KrunchWord implements BaseColumns {
    public static final String TABLE_NAME = "krunchword";
    public static final String WORD_COLUMN_NAME = "word";
    public static final String CREATED_DATE_COLUMN_NAME = "created_date";
    public static final String LAST_REVIEWED_DATE_COLUMN_NAME = "last_reviewed_date";
    public static final String REVIEWS_COUNT_COLUMN_NAME = "reviews_count";
    public static final String PART_OF_SPEECH_COLUMN_NAME = "part_of_speech";
    public static final String LEARNED_COLUMN_NAME = "learned";

    public static final int LEARNED_TRUE = 1;
    public static final int LEARNED_FALSE = 0;
    public static final int PART_OF_SPEECH_NOUN = 0;
    public static final int PART_OF_SPEECH_VERB = 1;
    public static final int PART_OF_SPEECH_ADJECTIVE = 2;
    public static final int PART_OF_SPEECH_ADVERB = 3;
    public static final int PART_OF_SPEECH_OTHER = 4;
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    private long id;
    private String word;
    private Date createdDate;
    private Date lastReviewedDate;
    private int reviews;
    private int partOfSpeech;
    private boolean learned;

    public KrunchWord(String s){
        word = s;
        createdDate = new Date();
        lastReviewedDate = new Date();
        reviews = 0;
        partOfSpeech = PART_OF_SPEECH_OTHER;  //TODO currently hardcoded - add part of speech feature later
        learned = false;
    }

    public void addToDatabase(SQLiteDatabase db){
        if(!word.isEmpty()){
            ContentValues values = makeNewContentValues();
            id = db.insert(TABLE_NAME, null, values);
       }
    }

    public static Cursor getAllKrunchWords(SQLiteDatabase db){
        String sortOrder = LEARNED_COLUMN_NAME + " ASC, " + _ID + " DESC";
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, sortOrder);
        return c;
    }

    public static void updateLearned(SQLiteDatabase db, long id, boolean isLearned){
        ContentValues values = new ContentValues();
        values.put(LEARNED_COLUMN_NAME, isLearned);
        String whereClause = _ID + "=?";
        String[] whereArgs = {Long.toString(id)};
        db.update(TABLE_NAME, values, whereClause, whereArgs);
    }

    public static void updateWordJustReviewed(SQLiteDatabase db, long id, int newNumReviews){
        ContentValues values = new ContentValues();
        values.put(LAST_REVIEWED_DATE_COLUMN_NAME, (new SimpleDateFormat(DATE_FORMAT)).format(new Date()));
        values.put(REVIEWS_COUNT_COLUMN_NAME, newNumReviews);
        String whereClause = _ID + "=?";
        String[] whereArgs = {Long.toString(id)};
        db.update(TABLE_NAME, values, whereClause, whereArgs);
    }

    /*
    Formula for weighting: 1 + review_num_factor + last_review_factor
     review_num_factor =    {  0: 15,
                              >0: round(10/reviews, 0) }
     last_review_factor = 2 * (today - last_review)

     */
    private static int getWeight(Cursor cursor){
        int weight = 1;
        try{
            SimpleDateFormat ft = new SimpleDateFormat(DATE_FORMAT);
            int numReviews = cursor.getInt(cursor.getColumnIndex(REVIEWS_COUNT_COLUMN_NAME));
            long lastReview = ft.parse(cursor.getString(cursor.getColumnIndex(LAST_REVIEWED_DATE_COLUMN_NAME))).getTime();  //gets the string lastReview date and converts to millisecond Time
            int daysSinceReview = new Long((((new Date()).getTime() - lastReview) / (1000*60*60*24))).intValue();
            if (numReviews == 0)
                weight += 15;
            else
                weight += 10 / numReviews;
            weight += 2 * daysSinceReview;

        } catch(ParseException e){
            //I'm pretty sure I didn't mess up...
        }
        return weight;
    }

    private static int getCursorPositionOfRandSelectedKrunchWord(Cursor cursor){
        int count = cursor.getCount();
        int[] weights = new int[count];
        int weightSum = 0;
        cursor.moveToFirst();
        for(int i=0; i<count; ++i){
            weights[i] = getWeight(cursor);
            weightSum += weights[i];
            cursor.moveToNext();
        }
        int rand = 1 + (int) (Math.random() * weightSum);  //from 1 to weightSum
        int i = 0;
        int compareToRand = weights[i];
        while(rand > compareToRand){
            ++i;
            compareToRand += weights[i];
        }
        return i;
    }

    public static String getKrunchWordForReview(KrunchDBHelper dbHelper){
        SQLiteDatabase readDB = dbHelper.getReadableDatabase();
        SQLiteDatabase writeDB = dbHelper.getWritableDatabase();
        String selection = LEARNED_COLUMN_NAME + "=?";
        String[] selectionArgs = {Integer.toString(LEARNED_FALSE)};
        Cursor cursor = readDB.query(TABLE_NAME, null, selection, selectionArgs, null, null ,null);
        cursor.moveToPosition(getCursorPositionOfRandSelectedKrunchWord(cursor));
        updateWordJustReviewed(writeDB, cursor.getLong(cursor.getColumnIndex(_ID)), 1 + cursor.getInt(cursor.getColumnIndex(REVIEWS_COUNT_COLUMN_NAME)));
        return cursor.getString(cursor.getColumnIndex(WORD_COLUMN_NAME));
    }

    private ContentValues makeNewContentValues(){
        SimpleDateFormat ft = new SimpleDateFormat(DATE_FORMAT);
        ContentValues values = new ContentValues();
        values.put(WORD_COLUMN_NAME, word);
        values.put(CREATED_DATE_COLUMN_NAME, ft.format(createdDate));
        values.put(LAST_REVIEWED_DATE_COLUMN_NAME, ft.format(lastReviewedDate));
        values.put(REVIEWS_COUNT_COLUMN_NAME, reviews);
        values.put(PART_OF_SPEECH_COLUMN_NAME, partOfSpeech);
        values.put(LEARNED_COLUMN_NAME, learned);
        return values;
    }

    public long getId(){
        return id;
    }

    public String getWord(){
        return word;
    }

    public Date getCreatedDate(){
        return createdDate;
    }

    public Date getLastReviewedDate(){
        return lastReviewedDate;
    }

    public int getReviews(){
        return reviews;
    }

    public int getPartOfSpeech(){
        return partOfSpeech;
    }

    public boolean getLearned(){
        return learned;
    }
}
