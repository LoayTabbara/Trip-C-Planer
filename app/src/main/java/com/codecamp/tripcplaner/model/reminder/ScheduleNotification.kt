package com.codecamp.tripcplaner.model.reminder

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.codecamp.tripcplaner.model.util.Converters
import java.time.LocalDateTime

fun scheduleNotification(context: Context,id:Int,dateTime:String,channelId:String,city:String,itemName:String) {
    val stepTime=10000

    Log.d("notif", "scheduleNotification: ")
    val notificationManager =context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Create a notification channel if not already created (Android 8.0 and above)
    val channel = NotificationChannel(
        channelId,
        "My Channel",
        NotificationManager.IMPORTANCE_HIGH
    )
    channel.description = "A channel used to send water reminders $channelId"
    notificationManager.createNotificationChannel(channel)

    // Build the notification content
    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(androidx.core.R.drawable.notification_bg)
        .setContentTitle(itemName)
        .setContentText("You want to pick it up today from $city. Don't forget!")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    // Set the time to display the notification (in this case, 10 seconds from now)
    val futureTimeInMillis = System.currentTimeMillis() + stepTime // 10 seconds from now

    // Schedule the notification
    val notificationIntent = Intent(context, NotificationReceiver::class.java)
    notificationIntent.putExtra("notificationId", id)
    notificationIntent.putExtra("notification", notification)
    notificationIntent.putExtra("dateTime",dateTime)
    val pendingIntent = PendingIntent.getBroadcast(context, id, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, futureTimeInMillis, pendingIntent)

}