package com.r2872.finalproject_20210910.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.util.Log
import com.odsay.odsayandroidsdk.API
import com.odsay.odsayandroidsdk.ODsayData
import com.odsay.odsayandroidsdk.ODsayService
import com.odsay.odsayandroidsdk.OnResultCallbackListener
import com.r2872.finalproject_20210910.datas.BasicResponse
import com.r2872.finalproject_20210910.receivers.AlarmReceiver
import com.r2872.finalproject_20210910.utils.GlobalData
import com.r2872.finalproject_20210910.web.ServerAPI
import com.r2872.finalproject_20210910.web.ServerAPIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyJobService : JobService() {

    override fun onStartJob(p0: JobParameters?): Boolean {

        Log.d("예약작업시작", p0!!.jobId.toString())

//        임시 : 1분 후에 알람 설정.
//        실제 : 약속시간 - (API 에서 알려준)교통 소용시간 - 내 준비시간 계산된 시간에 알람.
//        jobId => (출발 / 도착 좌표) => 상세정보 확인 API 통해 좌표 정보 받아오자.
        val retrofit = ServerAPI.getRetrofit(applicationContext)
        val apiService = retrofit.create(ServerAPIService::class.java)
        apiService.getRequestAppointmentDetail(p0.jobId).enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {

//                예약된 작업에서 약속 정보를 요청.
                if (response.isSuccessful) {

                    val basicResponse = response.body()!!

//                    서버가 알려준 약속 데이터 => 교통정보 확인 => 알람 설정.
                    val appointmentData = basicResponse.data.appointment
                    val myOdsayService =
                        ODsayService.init(
                            applicationContext,
                            "JdJCDd5mWQLx6RMfBFXCYV0S/Kw3CU0YMt4WrfwXhTg"
                        )
                    myOdsayService.requestSearchPubTransPath(
                        appointmentData.startLongitude.toString(),
                        appointmentData.startLatitude.toString(),
                        appointmentData.longitude.toString(),
                        appointmentData.latitude.toString(),
                        null,
                        null,
                        null,
                        object : OnResultCallbackListener {
                            override fun onSuccess(p0: ODsayData?, p1: API?) {

                                val jsonObj = p0!!.json
                                val resultObj = jsonObj.getJSONObject("result")
                                val pathArr = resultObj.getJSONArray("path")
                                val firstPathObj = pathArr.getJSONObject(0)
                                val infoObj = firstPathObj.getJSONObject("info")
                                val totalTime = infoObj.getInt("totalTime")

//                                예상 시간이 몇분이나 걸리는지 파악 완료. => 알람 띄우는데 활용

//                                알람 시간 : 약속시간 - 교통소요시간 - 내 준비시간 (밀리초 단위)
                                val alarmTime =
                                    appointmentData.datetime.time - totalTime * 60 * 1000 - GlobalData.loginUser!!.readyMinute * 60 * 1000
                                setAlarmByMilliSecond(alarmTime)

                            }

                            override fun onError(p0: Int, p1: String?, p2: API?) {

                                Log.d("예상시간실패", p1!!)
                            }
                        })
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

            }
        })

        return false
    }

//    언제 알람을 울릴지 계산해서 넘겨주면, 단순히 울리기만 하는 함수.

    fun setAlarmByMilliSecond(timeInMillis: Long) {
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

//        알람이 울릴 시간 설정 (임시: 30초 후)
//        val triggerTime = SystemClock.elapsedRealtime() + 30 * 1000

//        실제 알람시간 : 위의 함수에서 계산해서 넘겨준다.
        val triggerTime = timeInMillis

//        실제 알람 시간: 교통 소요시간 (API), 내 준비시간 고려
//        출발지 좌표 / 약속장소 좌표 필요

//        실제 알람 기능 설정
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeInMillis, pendingIntent)
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return false
    }
}