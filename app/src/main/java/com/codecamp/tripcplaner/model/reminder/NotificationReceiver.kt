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
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin


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
                                //if less than 30 km then send notification
                                if(stringToLoc< LocalDateTime.now()||calculateDistance(givenLat,givenLng,lat,lng)<30f){

                                    notificationManager.notify(notificationId, notification)
                                    Log.d("notif", "onReceive7: Notif in stringToLoc< LocalDateTime.now() ${calculateDistance(givenLat,givenLng,lat,lng)}")
                                }
                                else{
                                    //or schedule it again
                                    scheduleNotification(context,notificationId,dateTime,"PackAlert",cityMessage,itemName!!)

                                    Log.d("notif", "onReceive8: nothing matches for  ${calculateDistance(givenLat,givenLng,lat,lng)} and rescheduled $notificationId")
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
                    //if target time is not passed yet then schedule it again
                    scheduleNotification(context,notificationId,dateTime,"PackAlert",cityMessage,itemName!!)
                    Log.d("notif", "onReceive10: Notif rescheduled $stringToLoc")
                }
                else{
                    //if target time is passed then send notification
                   notificationManager.notify(notificationId, notification)
                    Log.d("notif", "onReceive11: Notif sent $stringToLoc")
                }
            }



                }
        else{
            Log.d("notif", "onReceive: no notif")
        }


            }

   //calculating distance between two points
    private fun calculateDistance(givenLat: Double, givenLng:Double, currentLat:Double, currentLng:Double): Double {
        val theta = currentLng - givenLng
        var dist = sin(deg2rad(currentLat)) * sin(deg2rad(givenLat)) +
                cos(deg2rad(currentLat)) * cos(deg2rad(givenLat)) *
                cos(deg2rad(theta))
        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515
        dist *= 1.609344 // Convert to kilometers
        return dist
    }

    fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }
}

