package com.r2872.finalproject_20210910.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import com.r2872.finalproject_20210910.receivers.AlarmReceiver
import java.util.*

class MyJobService : JobService() {

    override fun onStartJob(p0: JobParameters?): Boolean {

        Log.d("예약작업시작", p0!!.jobId.toString())

//        임시 : 1분 후에 알람 설정.
//        실제 : 약속시간 - (API 에서 알려준)교통 소용시간 - 내 준비시간 계산된 시간에 알람.

//        알람을 울리게 도와주는 도구. => Broadcast 송신.
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

//        실제로 알람이 울리면 실행 할 코드 => BroadcastReceiver 에 작업 해둘 필요 있다.
        val myIntent = Intent(this, AlarmReceiver::class.java)

//        할 일을 가지고 대기(Pending) 해주는 Intent.
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            AlarmReceiver.ALARM_ID,
            myIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

//        알람이 울릴 시간 설정 (임시: 1분 후)
        val triggerTime = SystemClock.elapsedRealtime() + 30 * 1000

//        실제 알람 기능 설정
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pendingIntent)

        return false
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return false
    }

    companion object {
        //        어떤 작업인지 구별하기 쉽게 숫자를 변수로 담자
        val JOB_TIME_SET = 1000
    }
}