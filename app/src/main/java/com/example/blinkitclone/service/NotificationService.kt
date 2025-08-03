package com.example.blinkitclone.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.blinkitclone.R
import com.example.blinkitclone.activity.UsersMainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotificationService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val title: String?
        val message: String?

        // Handle notification payload
        if (remoteMessage.notification != null) {
            title = remoteMessage.notification?.title ?: "Order Update"
            message = remoteMessage.notification?.body ?: "Product added successfully!"
        } else {
            // Handle data payload
            title = remoteMessage.data["title"] ?: "Order Update"
            message = remoteMessage.data["body"] ?: "Product added successfully!"
        }

        Log.d("FirebaseMsg", "Message received: title=$title, body=$message")
        showNotification(title, message)
    }

    private fun showNotification(title: String?, message: String?) {
        val channelId = "product_channel"
        val notificationId = System.currentTimeMillis().toInt()

        val intent = Intent(this, UsersMainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.baseline_circle_notifications_24)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // For Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Product Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for product notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token generated: $token")
        // Save or update this token in your database for sending notifications
    }
}
