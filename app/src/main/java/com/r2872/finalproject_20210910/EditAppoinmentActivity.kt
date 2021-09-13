package com.r2872.finalproject_20210910

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.r2872.finalproject_20210910.databinding.ActivityEditAppoinmentBinding
import java.util.*

class EditAppoinmentActivity : BaseActivity() {

    private lateinit var binding: ActivityEditAppoinmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_appoinment)

        setValues()
        setupEvents()
    }

    override fun setupEvents() {

        binding.datePickBtn.setOnClickListener {

            showDatePicker()
        }
        binding.timePickBtn.setOnClickListener {

            showTimePicker()
        }
    }

    override fun setValues() {

    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(mContext, DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
            binding.dateTxt.text = "$y - ${m+1} - $d"
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showTimePicker() {
        val cal = Calendar.getInstance()
        TimePickerDialog(mContext, TimePickerDialog.OnTimeSetListener { timePicker, h, m ->
            binding.timeTxt.text = "$h - $m"
        }, cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE), true).show()
    }


}