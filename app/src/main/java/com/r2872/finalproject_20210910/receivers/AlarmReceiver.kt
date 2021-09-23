package com.r2872.finalproject_20210910.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        Log.d("알람울림", "test")
    }

    companion object {
        val ALARM_ID = 1001
    }
}