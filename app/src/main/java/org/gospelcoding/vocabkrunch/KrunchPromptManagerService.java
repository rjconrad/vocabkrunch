package org.gospelcoding.vocabkrunch;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.Calendar;

public class KrunchPromptManagerService extends Service {
    public static final int REPEATING_ALARM_CODE = 0;
    public static final int ONE_TIME_ALARM_CODE = 1;
    public static final int ALARMS_PER_DAY = 4;
    public static final int CLOSING_TIME = 20;  //8pm
    public static final String CURRENT_ALARM_TAG = "org.gospelcoding.vocabkrunch.current_alarm";

    public KrunchPromptManagerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        int alarmNumber = intent.getIntExtra(CURRENT_ALARM_TAG, 0);
        postNotification();
        setTheNextOneTimeAlarm(this, alarmNumber);
        return START_REDELIVER_INTENT;  //I think...didn't take the time to figure out what this is all about
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //do not call with alarmsDoneToday=0 or >=ALARMS_PER_DAY
    public static void setTheNextOneTimeAlarm(Context context, int alarmsDoneToday){
        if(alarmsDoneToday<1 || alarmsDoneToday>=ALARMS_PER_DAY)
            return;
        long timeOfNextAlarm = calculateTimeOfNextAlarm(alarmsDoneToday);
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, KrunchPromptManagerService.class);
        intent.putExtra(CURRENT_ALARM_TAG, alarmsDoneToday+1);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, ONE_TIME_ALARM_CODE, intent, 0);

        alarmMgr.set(AlarmManager.RTC_WAKEUP, timeOfNextAlarm, alarmIntent);

    }

    public static void setTheRepeatingAlarm(Context context){
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, KrunchPromptManagerService.class);
        intent.putExtra(CURRENT_ALARM_TAG, 1);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, REPEATING_ALARM_CODE, intent, 0);

        // Set the alarm to start at approximately 7am
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if(calendar.get(Calendar.HOUR_OF_DAY) > 7)
            calendar.add(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    //do not call with alarmsDoneToday=0 or >=ALARMS_PER_DAY
    //do not call after CLOSING_TIME
    private static long calculateTimeOfNextAlarm(int alarmsDoneToday){
        int alarmsToGo = ALARMS_PER_DAY - alarmsDoneToday;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, CLOSING_TIME);
        long diff = calendar.getTimeInMillis() - System.currentTimeMillis();
        long fromNow = diff / (alarmsToGo + 1);
        return System.currentTimeMillis() + fromNow;
    }
}