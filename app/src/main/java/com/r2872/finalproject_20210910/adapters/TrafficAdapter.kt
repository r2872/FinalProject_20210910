package com.r2872.finalproject_20210910.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.r2872.finalproject_20210910.AppointmentDetailActivity
import com.r2872.finalproject_20210910.R
import com.r2872.finalproject_20210910.ViewMapActivity
import com.r2872.finalproject_20210910.datas.AppointmentData
import com.r2872.finalproject_20210910.datas.SubPathData
import java.text.SimpleDateFormat
import java.util.*

class TrafficAdapter(
    private val mContext: Context,
    private val mList: List<SubPathData>
) : RecyclerView.Adapter<TrafficAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(mContext).inflate(R.layout.path_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(mList[position])
    }

    override fun getItemCount() = mList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val trafficType = view.findViewById<TextView>(R.id.trafficType)
        private val sectionTime = view.findViewById<TextView>(R.id.sectionTime)
        private val lane = view.findViewById<TextView>(R.id.lane)

        fun bind(item: SubPathData) {

            when (item.trafficType) {
                1 -> {
                    trafficType.text = "지하철"
                    lane.visibility = View.VISIBLE
                    lane.text = "${item.pathName}호선 ${item.startName} -> ${item.endName}"
                }
                2 -> {
                    trafficType.text = "버스"
                    lane.visibility = View.VISIBLE
                    lane.text = "${item.pathName}번 ${item.startName} -> ${item.endName}"
                }

                3 -> {
                    trafficType.text = "도보"
                    lane.visibility = View.GONE
                }
            }
            sectionTime.text = "${item.sectionTime}분 소요"

        }
    }
}