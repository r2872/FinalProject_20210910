package com.r2872.finalproject_20210910.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.r2872.finalproject_20210910.R
import com.r2872.finalproject_20210910.datas.PlaceListData

class MyPlaceListAdapter(
    val mContext: Context
) : RecyclerView.Adapter<MyPlaceListAdapter.ViewHolder>() {

    var datas = mutableListOf<PlaceListData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(mContext).inflate(R.layout.my_place_list_item, null)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val placeNameTxt = itemView.findViewById<TextView>(R.id.placeName_Txt)
        private val latitudeTxt = itemView.findViewById<TextView>(R.id.latitude_Txt)
        private val longitudeTxt = itemView.findViewById<TextView>(R.id.longitude_Txt)

        fun bind(item: PlaceListData) {

            placeNameTxt.text = item.name
            latitudeTxt.text = item.latitude.toString()
            longitudeTxt.text = item.longitude.toString()
        }

    }
}