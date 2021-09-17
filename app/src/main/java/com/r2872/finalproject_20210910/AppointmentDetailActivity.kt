package com.r2872.finalproject_20210910

import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.r2872.finalproject_20210910.databinding.ActivityAppointmentDetailBinding
import com.r2872.finalproject_20210910.datas.AppointmentData
import com.r2872.finalproject_20210910.datas.UserData

class AppointmentDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityAppointmentDetailBinding
    private val invitedFriendsList = ArrayList<UserData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_appointment_detail)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {

    }

    override fun setValues() {

        titleTxt.text = "일정 상세정보"

        val data = intent.getSerializableExtra("appointment") as AppointmentData
        invitedFriendsList.addAll(data.invitedFriends)
        Log.d("값", invitedFriendsList.toString())
    }
}