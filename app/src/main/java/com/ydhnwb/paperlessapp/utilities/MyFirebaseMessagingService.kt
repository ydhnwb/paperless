package com.ydhnwb.paperlessapp.utilities

import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "FirebaseServ"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.notification != null) {
            val i : MutableMap<String, String> = remoteMessage.data
            val title = remoteMessage.notification!!.title
            val body = remoteMessage.notification!!.body
            NotificationHelper.displayNotification(applicationContext, title!!, body!!, i)
        }

        Log.d(TAG, "From: " + remoteMessage.from)
//
//        if (remoteMessage.data.size() > 0) {
//            Log.d(TAG, "Message data payload: " + remoteMessage.data)
//            startActivity(Intent(this, NotifikasiActivity::class.java))
//        }
//
//        // Check if message contains a notification payload.
//
//        // Check if message contains a notification payload.
//        if (remoteMessage.notification != null) {
//            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body)
//        }

    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }
}