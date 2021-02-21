package io.markzhang.stride;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class LocAlarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (pm == null) return;

        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "stride:wakelock");
        wl.acquire(60 * 1000L /*1 minute*/);

        Toast.makeText(context, "Alarm !!!! " + System.currentTimeMillis(), Toast.LENGTH_SHORT).show();
        Log.e("DEBUG", "Acquiring Location @ " + System.currentTimeMillis());
        setAlarm(context);
        wl.release();
    }

    public static void setAlarm(Context context) {
        AlarmManager am =( AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        Intent i = new Intent("io.markzhang.stride.START_ALARM");
        i.setComponent(new ComponentName(context, LocAlarm.class));
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000L * 5, pi); // Millisec * Second * Minute
    }

    public static void cancelAlarm(Context context) {
        Intent intent = new Intent("io.markzhang.stride.START_ALARM");
        intent.setComponent(new ComponentName(context, LocAlarm.class));
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;
        alarmManager.cancel(sender);
    }
}