package com.r2872.finalproject_20210910.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.r2872.finalproject_20210910.R
import com.r2872.finalproject_20210910.ViewMapActivity
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

        val title = row.findViewById<TextView>(R.id.title_Txt)
        val dateTime = row.findViewById<TextView>(R.id.date_Txt)
        val place = row.findViewById<TextView>(R.id.place_Txt)
        val viewPlaceMap = row.findViewById<ImageView>(R.id.viewPlaceMap_Btn)

        title.text = data.title
        dateTime.text = data.datetime
        place.text = data.place

        viewPlaceMap.setOnClickListener {
            val myIntent = Intent(mContext, ViewMapActivity::class.java)
            myIntent.putExtra("appointment", data)
            mContext.startActivity(myIntent)
        }

        return row
    }
}