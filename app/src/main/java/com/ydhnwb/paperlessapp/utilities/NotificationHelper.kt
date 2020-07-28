package com.ydhnwb.paperlessapp.utilities

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.ui.main.MainActivity

object NotificationHelper {

    fun displayNotification(context: Context, title: String, body: String, i : Map<String, String>) {
        if(PaperlessUtil.getToken(context) != null){
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 100, intent, PendingIntent.FLAG_CANCEL_CURRENT)

            val mBuilder = NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(title)
//            .setContentText(body)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            val mNotificationMgr = NotificationManagerCompat.from(context)
            mNotificationMgr.notify(1, mBuilder.build())
        }
    }

}