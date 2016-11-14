package org.gospelcoding.vocabkrunch;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

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
