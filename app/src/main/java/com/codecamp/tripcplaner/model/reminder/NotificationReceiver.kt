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


class NotificationReceiver: BroadcastReceiver()  {

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onReceive(context: Context?, intent: Intent?) {
            val notificationId = intent?.getIntExtra("notificationId", 0)
            val notification =
                intent?.getParcelableExtra<Notification>("notification") // Retrieve the Notification object

            val notificationManager = NotificationManagerCompat.from(context!!)
            if (notification != null) {
                Log.d("notif", "onReceive: notif" )
                if (notificationId != null) {
                    Log.d("notif", "onReceive: " + notificationId.toString())
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {

                        notificationManager.notify(notificationId, notification)
                    }
                    else{
                        Log.d("post permission", " post permission not granted !Go to settings and enable it")
                    }

                }
            }

        }


    }
