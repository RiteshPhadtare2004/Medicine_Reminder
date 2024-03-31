package com.example.medicinereminder;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        // You can handle the alarm event here
        // For example, you can show a notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Alarm")
                .setContentText("Time to take your medication")
                .setAutoCancel(true);

        if (notificationManager != null) {
            notificationManager.notify(0, builder.build());
        }
    }
}

