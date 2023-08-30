package com.codecamp.tripcplaner.model.reminder

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.core.app.NotificationCompat
import com.codecamp.tripcplaner.model.data.Trip
import com.codecamp.tripcplaner.viewModel.TravelInfoViewModel
import java.time.LocalDateTime


fun scheduleNotification (context: Context,id:Int,dateTime:String,channelId:String,city:String,itemName:String) {

// time step for notification is 10 seconds and each time it tests if the time is passed or not and also location in notification receiver
    val stepTime=10000

    var contentMessage=""


    contentMessage = if (city!="No place specified"){


        "you wanted to pick $itemName up from $city or on $dateTime. Don't forget! "

    }
    else {


        "you want to pick it up on $dateTime. Don't forget! "
    }

    Log.d("notif", "scheduleNotification: $dateTime")
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
        .setContentText("$contentMessage $id")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    // Set the time to display the notification (in this case, 10 seconds from now)
    val futureTimeInMillis = System.currentTimeMillis() + stepTime // 10 seconds from now

    // Schedule the notification
    val notificationIntent = Intent(context, NotificationReceiver::class.java)
    notificationIntent.putExtra("notificationId", id)
    notificationIntent.putExtra("notification", notification)
    notificationIntent.putExtra("dateTime", dateTime)
    notificationIntent.putExtra("city",city)
    notificationIntent.putExtra("itemName", itemName)
    val pendingIntent = PendingIntent.getBroadcast(context, id, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, futureTimeInMillis, pendingIntent)

}