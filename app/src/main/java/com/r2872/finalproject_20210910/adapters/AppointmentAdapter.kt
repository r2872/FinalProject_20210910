package com.r2872.finalproject_20210910.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.r2872.finalproject_20210910.R
import com.r2872.finalproject_20210910.datas.AppointmentData

class AppointmentAdapter(
    private val mContext: Context,
    resId: Int,
    private val mList: List<AppointmentData>
) : ArrayAdapter<AppointmentData>(mContext, resId, mList) {

    private val mInflater = LayoutInflater.from(mContext)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var row = convertView
        if (row == null) {
            row = mInflater.inflate(R.layout.appointment_list_item, null)
        }
        row!!

        val data = mList[position]

        val scheduleTitle = row.findViewById<TextView>(R.id.title_Txt)
        val scheduleDateTime = row.findViewById<TextView>(R.id.date_Txt)
        val schedulePlace = row.findViewById<TextView>(R.id.place_Txt)

        scheduleTitle.text = data.title
        scheduleDateTime.text = data.datetime
        schedulePlace.text = data.place

        return row
    }
}