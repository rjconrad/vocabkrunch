package org.gospelcoding.vocabkrunch;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class KrunchPromptManagerService extends Service {
    public static final int REPEATING_ALARM_CODE = 0;
    public static final int ONE_TIME_ALARM_CODE = 1;
    public static final int ALARMS_PER_DAY = 4;
    public static final int OPENING_TIME = 7;
    public static final int CLOSING_TIME = 20;  //8pm
    public static final String CURRENT_ALARM_TAG = "org.gospelcoding.vocabkrunch.current_alarm";
    public static final int LOG_REPEATING = 0;
    public static final int LOG_ONE_TIME = 1;
    public static final String LOG_TAG = "krunchword.Alarms";

    public KrunchPromptManagerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        logAlarmRang(LOG_REPEATING);
        Intent notifyIntent = new Intent(this, KrunchNotificationService.class);
        notifyIntent.putExtra(CURRENT_ALARM_TAG, 1);
        startService(notifyIntent);
        stopSelf();
        return START_NOT_STICKY;  //I think...didn't take the time to figure out what this is all about
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //do not call with alarmsDoneToday=0 or >=ALARMS_PER_DAY
    public static void setTheNextOneTimeAlarm(Context context, int alarmsDoneToday) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (alarmsDoneToday < 1 || alarmsDoneToday >= ALARMS_PER_DAY || calendar.get(Calendar.HOUR_OF_DAY) > CLOSING_TIME)
            return;
        long timeOfNextAlarm = calculateTimeOfNextAlarm(alarmsDoneToday);
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, KrunchNotificationService.class);
        intent.putExtra(CURRENT_ALARM_TAG, alarmsDoneToday + 1);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, timeOfNextAlarm, alarmIntent);
        logAlarmSet(timeOfNextAlarm, LOG_ONE_TIME);
    }

    public static void setTheRepeatingAlarm(Context context) {
        boolean setOneTimeAlarmToo = false;
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, KrunchPromptManagerService.class);
        //intent.putExtra(CURRENT_ALARM_TAG, 1);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        // Set the alarm to start at approximately 7am
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (calendar.get(Calendar.HOUR_OF_DAY) > OPENING_TIME) {
            calendar.add(Calendar.DATE, 1);
            if (calendar.get(Calendar.HOUR_OF_DAY) < CLOSING_TIME)
                setOneTimeAlarmToo = true;
        }
        calendar.set(Calendar.HOUR_OF_DAY, OPENING_TIME);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
        logAlarmSet(calendar.getTimeInMillis(), LOG_REPEATING);
        if (setOneTimeAlarmToo)
            setTheNextOneTimeAlarm(context, 1);
    }

    //do not call with alarmsDoneToday=0 or >=ALARMS_PER_DAY
    //do not call after CLOSING_TIME
    private static long calculateTimeOfNextAlarm(int alarmsDoneToday) {
        int alarmsToGo = ALARMS_PER_DAY - alarmsDoneToday;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, CLOSING_TIME);
        long diff = calendar.getTimeInMillis() - System.currentTimeMillis();
        long fromNow = diff / (alarmsToGo + 1);
        return System.currentTimeMillis() + fromNow;
    }

    public static void logAlarmSet(long time, int type){
        SimpleDateFormat ft = new SimpleDateFormat("hh:mm MM-dd-yy");
        String log = ft.format(new Date(time));
        if(type==LOG_REPEATING)
            log = "Repeating alarm set for " + log;
        else
            log = "One time alarm set for " + log;
         Log.d(LOG_TAG, log);
    }

    public static void logAlarmRang(int type){
        SimpleDateFormat ft = new SimpleDateFormat("hh:mm MM-dd-yy");
        String log = ft.format(new Date());
        if(type==LOG_REPEATING)
            log = "Repeating alarm rang at " + log;
        else
            log = "One time alarm rang at " + log;
        Log.d(LOG_TAG, log);

    }
}


