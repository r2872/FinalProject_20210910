package com.r2872.finalproject_20210910

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.r2872.finalproject_20210910.databinding.ActivityAppointmentDetailBinding

class AppointmentDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityAppointmentDetailBinding

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
    }
}