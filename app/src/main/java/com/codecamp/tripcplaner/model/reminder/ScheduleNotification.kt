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
import java.time.LocalDateTime


fun scheduleNotification(context: Context,id:Int,dateTime:String,channelId:String,city:String,itemName:String,trip: Trip) {
    val stepTime=10000
    var cityMessage=""
    var contentMessage=""
    //Reminder will be automatically set for 20 days if the date was not selected.
    val currentDate= LocalDateTime.now().plusDays(20)
    val currentDateMessage=if (dateTime=="")"${currentDate.year}.${currentDate.monthValue}.${currentDate.dayOfMonth}" else dateTime
    if (city!=""){
        cityMessage=trip.cities[city]!!.first.toString()+"-"+city

        contentMessage = "you wanted to pick $itemName up from $city or on $currentDateMessage. Don't forget! "

    }
    else {
        cityMessage="No place specified"

        contentMessage="you want to pick it up on $currentDateMessage. Don't forget! "
    }

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
        .setContentText("$contentMessage $id")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    // Set the time to display the notification (in this case, 10 seconds from now)
    val futureTimeInMillis = System.currentTimeMillis() + stepTime // 10 seconds from now

    // Schedule the notification
    val notificationIntent = Intent(context, NotificationReceiver::class.java)
    notificationIntent.putExtra("notificationId", id)
    notificationIntent.putExtra("notification", notification)
    notificationIntent.putExtra("dateTime", currentDateMessage)
    notificationIntent.putExtra("city",cityMessage)
    val pendingIntent = PendingIntent.getBroadcast(context, id, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, futureTimeInMillis, pendingIntent)

}