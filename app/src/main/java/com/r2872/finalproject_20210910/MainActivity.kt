package com.r2872.finalproject_20210910

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.r2872.finalproject_20210910.adapters.ScheduleAdapter
import com.r2872.finalproject_20210910.databinding.ActivityMainBinding
import com.r2872.finalproject_20210910.datas.AppointmentData

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mAdapter: ScheduleAdapter
    private val mScheduleList = ArrayList<AppointmentData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setValues()
        setupEvents()
    }

    override fun setupEvents() {

        binding.addAppoinmentBtn.setOnClickListener {

            val myIntent = Intent(mContext, EditAppointmentActivity::class.java)
            startActivity(myIntent)
        }
    }

    override fun setValues() {

        mAdapter = ScheduleAdapter(mContext, R.layout.appointment_list_item, mScheduleList)
        binding.scheduleList.adapter = mAdapter
    }
}