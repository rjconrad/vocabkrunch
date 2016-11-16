package org.gospelcoding.vocabkrunch;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SimpleCursorAdapter;

/**
 * Created by Rick on 11/16/2016.
 */

public class KrunchCursorAdapter extends SimpleCursorAdapter {

    public KrunchCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags){
        super(context, layout, c, from, to, flags);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        //String firstWord = cursor.getString(cursor.getColumnIndex(KrunchWord.WORD_COLUMN_NAME));
        Cursor c = getCursor();
        int learnedVal = c.getInt(c.getColumnIndex(KrunchWord.LEARNED_COLUMN_NAME));
        boolean checkTheBox = (learnedVal==KrunchWord.LEARNED_TRUE);
        CheckBox newCheckBox = new CheckBox(parent.getContext());
        newCheckBox.setText(R.string.learned_check);
        newCheckBox.setChecked(checkTheBox);
        return newCheckBox;
    }
}
