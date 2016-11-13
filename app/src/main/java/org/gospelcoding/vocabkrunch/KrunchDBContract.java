package org.gospelcoding.vocabkrunch;

import android.provider.BaseColumns;

/**
 * Created by Rick on 11/11/2016.
 */

public class KrunchDBContract {

    //Do not instantiate
    private KrunchDBContract(){}

    public static class KrunchWord implements BaseColumns {
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
    }
}
