package org.gospelcoding.vocabkrunch;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class KrunchAlarmReceiver extends BroadcastReceiver {
    public static final String REPEATING_ALARM_CODE = "org.gospelcoding.vocabkrunch.repeating_alarm";
    public static final String ONE_TIME_ALARM_CODE = "org.gospelcoding.vocabkrunch.one_time_alarm";
    public static final int ALARMS_PER_DAY = 4;
    public static final int OPENING_TIME = 7;
    public static final int CLOSING_TIME = 20;  //8pm
    public static final String CURRENT_ALARM_TAG = "org.gospelcoding.vocabkrunch.current_alarm";
    public static final int LOG_REPEATING = 0;
    public static final int LOG_ONE_TIME = 1;
    public static final String LOG_TAG = "krunchword.Alarms";

    public KrunchAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.contentEquals(REPEATING_ALARM_CODE)){
            logAlarmRang(LOG_REPEATING);
            setTheNextOneTimeAlarmIfNecessary(context);
        }
        else{
            logAlarmRang(LOG_ONE_TIME);
            int alarmNumber = intent.getIntExtra(CURRENT_ALARM_TAG, 1);
            if(alarmNumber<ALARMS_PER_DAY)
                setTheNextOneTimeAlarm(context, alarmNumber);
            postNotification(context);
        }
    }

    public static void setTheNextOneTimeAlarmIfNecessary(Context context){
        boolean alarmSet = (PendingIntent.getBroadcast(context,
                0,
                new Intent(context, KrunchAlarmReceiver.class).setAction(KrunchAlarmReceiver.ONE_TIME_ALARM_CODE),
                PendingIntent.FLAG_NO_CREATE) != null);
        if(!alarmSet){
            setTheNextOneTimeAlarm(context, 1);
        }
    }

    public static void setTheNextOneTimeAlarm(Context context, int alarmsDoneToday) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, KrunchAlarmReceiver.class);
        intent.setAction(ONE_TIME_ALARM_CODE);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        Calendar calendar = getRightNow();
        long timeOfNextAlarm;
        if (calendar.get(Calendar.HOUR_OF_DAY) > CLOSING_TIME || alarmsDoneToday>=ALARMS_PER_DAY) {
            timeOfNextAlarm = getNextOpeningTime().getTimeInMillis();
            intent.putExtra(CURRENT_ALARM_TAG, 1);
        }
        else {
            timeOfNextAlarm = calculateTimeOfNextAlarm(alarmsDoneToday);
            intent.putExtra(CURRENT_ALARM_TAG, alarmsDoneToday + 1);
        }
        alarmMgr.set(AlarmManager.RTC_WAKEUP, timeOfNextAlarm, alarmIntent);
        logAlarmSet(timeOfNextAlarm, LOG_ONE_TIME);
    }

    public static void setTheRepeatingAlarm(Context context) {
        boolean setOneTimeAlarmToo = false;
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, KrunchAlarmReceiver.class);
        intent.setAction(REPEATING_ALARM_CODE);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        //DEBUG
        //calendar.setTimeInMillis(System.currentTimeMillis()+6000);

        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);

        logAlarmSet(System.currentTimeMillis(), LOG_REPEATING);
        if (setOneTimeAlarmToo)
            setTheNextOneTimeAlarm(context, 1);
    }

    //do not call with alarmsDoneToday>=ALARMS_PER_DAY
    //do not call after CLOSING_TIME
    private static long calculateTimeOfNextAlarm(int alarmsDoneToday) {
        int alarmsToGo = ALARMS_PER_DAY - alarmsDoneToday;
        Calendar calendar = getRightNow();
        calendar.set(Calendar.HOUR_OF_DAY, CLOSING_TIME);
        calendar.set(Calendar.MINUTE, 0);
        long diff = calendar.getTimeInMillis() - System.currentTimeMillis();
        long fromNow = diff / (alarmsToGo + 1);

        //DEBUG
        //SimpleDateFormat ft = new SimpleDateFormat("HH:mm MM-dd-yy");
        //Log.d(LOG_TAG, "alarmsToGo="+Integer.toString(alarmsToGo));
        //Log.d(LOG_TAG, "Closing Time = " + ft.format(new Date(calendar.getTimeInMillis())));
        //Log.d(LOG_TAG, "Right now is " + ft.format(new Date()));
        //Log.d(LOG_TAG, "diff is " + Long.toString(diff/1000/60) + " min");
        //Log.d(LOG_TAG, "fromNow is " + Long.toString(fromNow/1000/60) + " min");


        return System.currentTimeMillis() + fromNow;

        //DEBUG
        //return System.currentTimeMillis() + (1000*60*3);
    }

    private static Calendar getNextOpeningTime(){
        Calendar c = getRightNow();
        if (c.get(Calendar.HOUR_OF_DAY) > OPENING_TIME) {
            c.add(Calendar.DATE, 1);
        }
        c.set(Calendar.HOUR_OF_DAY, OPENING_TIME);
        c.set(Calendar.MINUTE, 0);
        return c;
    }

    private static Calendar getRightNow(){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        return c;
    }

    public static void logAlarmSet(long time, int type){
        SimpleDateFormat ft = new SimpleDateFormat("HH:mm MM-dd-yy");
        String log = ft.format(new Date(time));
        if(type==LOG_REPEATING)
            log = "Repeating alarm set for " + log;
        else
            log = "One time alarm set for " + log;
        Log.d(LOG_TAG, log);
    }

    public static void logAlarmRang(int type) {
        SimpleDateFormat ft = new SimpleDateFormat("HH:mm MM-dd-yy");
        String log = ft.format(new Date());
        if (type == LOG_REPEATING)
            log = "Repeating alarm rang at " + log;
        else
            log = "One time alarm rang at " + log;
        Log.d(LOG_TAG, log);
    }

    private void postNotification(Context context){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(context.getString(R.string.notification_title));
        builder.setContentText(context.getString(R.string.notification_text));
        builder.setAutoCancel(true);

        Intent resultIntent = new Intent(context, ReviewWordActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(ListActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(0, builder.build());
    }
}
