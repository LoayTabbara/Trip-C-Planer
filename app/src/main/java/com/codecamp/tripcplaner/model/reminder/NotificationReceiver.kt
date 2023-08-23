package com.codecamp.tripcplaner.model.reminder

import android.Manifest
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.codecamp.tripcplaner.view.updatedList
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.internal.notify
import java.time.LocalDateTime
import kotlin.math.roundToInt


class NotificationReceiver: BroadcastReceiver()  {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationId = intent?.getIntExtra("notificationId", 0)
        val notification =
            intent?.getParcelableExtra<Notification>("notification") // Retrieve the Notification object
//processing datetime message
        val dateTime=intent?.getStringExtra("dateTime")
        Log.d("notif", "onReceive0: $dateTime")
       val dateTimeSplit= dateTime?.split(".")
        val stringToLoc=LocalDateTime.of(dateTimeSplit!![0].toInt(),dateTimeSplit!![1].toInt(),dateTimeSplit!![2].toInt(),0,0,0,0)

//processing city message
        val cityMessage=intent?.getStringExtra("city")
        val regex = """\((-?\d+\.\d+),(-?\d+\.\d+)\)""".toRegex()
        var givenLat=0.0
        var givenLng=0.0
        if (cityMessage!="No place specified"){
           val matchResult=regex.find(cityMessage!!)
            if (matchResult != null && matchResult.groupValues.size == 3) {
                givenLat = (matchResult.groupValues[1].toDouble()*100000).roundToInt()/100000.0
                givenLng = (matchResult.groupValues[2].toDouble()*100000).roundToInt()/100000.0
            }
            else{
                Log.d("notif", "onReceive1: no match")
            }
        }
        Log.d("notif", "onReceive2: $givenLat $givenLng")




        val itemName=intent?.getStringExtra("itemName")

        val notificationManager = NotificationManagerCompat.from(context!!)
        //val activeNotificationSize=notificationManager.activeNotifications.size
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        Log.d("notif", "onReceive3: notif" )
        if (notification != null&&notificationId != null&&ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED) {
            Log.d("notif", "onReceive4: notif" )

            if (cityMessage!="No place specified"){
                    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        // Launch a coroutine to handle the location update
                        GlobalScope.launch(Dispatchers.Main) {
                            try {
                                // Obtain location data asynchronously
                                val location = fusedLocationClient.lastLocation.await()

                                // Save the new location data
                                val lat = ((location?.latitude ?: 0.0)*100000).roundToInt()/100000.0
                                val lng = ((location?.longitude ?: 0.0)*100000).roundToInt()/100000.0
                                Log.d("notif", "onReceive5: $lat $lng")
                                Log.d("notif", "onReceive6: $givenLat $givenLng")
                                if(stringToLoc< LocalDateTime.now()||(lat==givenLat&&lng==givenLng)){

                                    //notificationManager.notify(notificationId, notification)
                                    Log.d("notif", "onReceive7: Notif in stringToLoc< LocalDateTime.now()")
                                }
                                else{
                                    //scheduleNotification(context,notificationId,dateTime,"PackAlert",cityMessage,itemName!!)

                                    Log.d("notif", "onReceive8: nothing matches")
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    else{
                        Log.d("notif", "onReceive9: no permission")
                    }

            }
            else{
                if(stringToLoc> LocalDateTime.now()){

                    scheduleNotification(context,notificationId,dateTime,"PackAlert",cityMessage,itemName!!)
                    Log.d("notif", "onReceive10: Notif rescheduled $stringToLoc")
                }
                else{
                   notificationManager.notify(notificationId, notification)
                    Log.d("notif", "onReceive11: Notif sent $stringToLoc")
                }
            }



                }
        else{
            Log.d("notif", "onReceive: no notif")
        }
//

            }
        }

