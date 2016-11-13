package org.gospelcoding.vocabkrunch;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by Rick on 11/11/2016.
 */

public class KrunchDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "krunch.db";


    public KrunchDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db){
        /* CREATE TABLE krunchword(
                id INTEGER PRIMARY KEY,
                ...) */
        String sql = "CREATE TABLE " + KrunchDBContract.KrunchWord.TABLE_NAME + "("
                + KrunchDBContract.KrunchWord._ID + " INTEGER PRIMARY KEY, "
                + KrunchDBContract.KrunchWord.WORD_COLUMN_NAME + " TEXT,"
                + KrunchDBContract.KrunchWord.CREATED_DATE_COLUMN_NAME + " INT"
                + KrunchDBContract.KrunchWord.LAST_REVIEWED_DATE_COLUMN_NAME + " INT"
                + KrunchDBContract.KrunchWord.REVIEWS_COUNT_COLUMN_NAME + " INT,"
                + KrunchDBContract.KrunchWord.PART_OF_SPEECH_COLUMN_NAME + " INT,"
                + KrunchDBContract.KrunchWord.LEARNED_COLUMN_NAME + " INT)";


    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
