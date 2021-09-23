package com.r2872.finalproject_20210910.receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {

    //    알림을 관리하는 클래스를 멤버변수로 선언.
    private lateinit var mNotificationManager: NotificationManager

    override fun onReceive(context: Context, intent: Intent?) {

        Log.d("알람울림", "test")
        mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

//        알림 - Oreo 버전 이후로는 채널을 설정해야함. 그 이전 버전에선 하면 안됨.
        createNotificationChannel()
    }

    private fun createNotificationChannel() {

//        폰에 깔린 OS 가 Oreo 버전 이상인가?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

//            맞을때만 알림 채널 설정.
            val notiChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "Primary Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notiChannel.enableLights(true)
            notiChannel.lightColor = Color.RED
            notiChannel.enableVibration(true)
            notiChannel.description = "알람을 통한 Notification TEST"

//            알림(Notification) 매니저를 통해 채널 등록
            mNotificationManager.createNotificationChannel(notiChannel)

        }
    }

    companion object {
        val ALARM_ID = 1001
        val PRIMARY_CHANNEL_ID = "PRIMARY_CHANNEL_ID"
    }
}