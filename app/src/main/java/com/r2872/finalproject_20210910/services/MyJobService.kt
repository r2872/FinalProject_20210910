package com.r2872.finalproject_20210910.services

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log

class MyJobService : JobService() {

    override fun onStartJob(p0: JobParameters?): Boolean {

        Log.d("예약작업시작", p0!!.jobId.toString())
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