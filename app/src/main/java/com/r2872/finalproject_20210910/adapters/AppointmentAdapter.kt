package com.r2872.finalproject_20210910.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.r2872.finalproject_20210910.R
import com.r2872.finalproject_20210910.ViewMapActivity
import com.r2872.finalproject_20210910.datas.AppointmentData
import java.text.SimpleDateFormat

class AppointmentAdapter(
    private val mContext: Context,
    private val mList: List<AppointmentData>
) : RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(mContext).inflate(R.layout.appointment_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(mList[position])
    }

    override fun getItemCount() = mList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val titleTxt = view.findViewById<TextView>(R.id.title_Txt)
        private val dateTxt = view.findViewById<TextView>(R.id.date_Txt)
        private val placeTxt = view.findViewById<TextView>(R.id.place_Txt)
        private val viewPlaceMapBtn = view.findViewById<ImageView>(R.id.viewPlaceMap_Btn)
        private val dateTimeSdf = SimpleDateFormat("M/d a h:mm")

        fun bind(item: AppointmentData) {

            titleTxt.text = item.title

//            약속일시 : Date 형태로 파싱됨. => String 으로 가공. SimpleDateFormat 사용.
            dateTxt.text = dateTimeSdf.format(item.datetime)
            placeTxt.text = item.place

            viewPlaceMapBtn.setOnClickListener {

                val myIntent = Intent(mContext, ViewMapActivity::class.java)
                myIntent.putExtra("appointment", item)
                mContext.startActivity(myIntent)
            }
        }
    }
}