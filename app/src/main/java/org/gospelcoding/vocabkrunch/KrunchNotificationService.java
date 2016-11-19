package org.gospelcoding.vocabkrunch;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class KrunchNotificationService extends Service {
    public KrunchNotificationService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        KrunchPromptManagerService.logAlarmRang(KrunchPromptManagerService.LOG_ONE_TIME);
        int alarmNumber = intent.getIntExtra(KrunchPromptManagerService.CURRENT_ALARM_TAG, 1);
        postNotification();
        KrunchPromptManagerService.setTheNextOneTimeAlarm(this, alarmNumber);
        stopSelf();
        return START_NOT_STICKY; //maybe?
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void postNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(getString(R.string.notification_title));
        builder.setContentText(getString(R.string.notification_text));
        builder.setAutoCancel(true);

        Intent resultIntent = new Intent(this, ReviewWordActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ListActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(0, builder.build());
    }
}

