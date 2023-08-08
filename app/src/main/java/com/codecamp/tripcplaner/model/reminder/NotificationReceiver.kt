package com.codecamp.tripcplaner.model.reminder

import android.Manifest
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.codecamp.tripcplaner.view.updatedList
import java.time.LocalDateTime


class NotificationReceiver: BroadcastReceiver()  {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationId = intent?.getIntExtra("notificationId", 0)
        val notification =
            intent?.getParcelableExtra<Notification>("notification") // Retrieve the Notification object
        val dateTime=intent?.getStringExtra("dateTime")
       val dateTimeSplit= dateTime?.split(".")



        val notificationManager = NotificationManagerCompat.from(context!!)
        Log.d("notif", "onReceive: notif" )
        if (notification != null) {
            Log.d("notif", "onReceive: notif" )
            if (notificationId != null) {
                Log.d("notif", "onReceive: $notificationId")
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {

                    // here to request the missing permissions, and then overriding
                    Log.d("notif", "onReceive: " + notificationId.toString() +" "+LocalDateTime.of(dateTimeSplit!![0].toInt(),dateTimeSplit!![1].toInt(),dateTimeSplit!![2].toInt(),0,0,0,0).toString()+" "+notificationManager.activeNotifications.size)
                    notificationManager.notify(notificationId, notification)

                }
//                if(LocalDateTime.of(2023,7,28,5,3,0)> LocalDateTime.now()){
//                    scheduleNotification(context,2,4000,"button2")
//                }
//                else

            }
        }

    }


}
