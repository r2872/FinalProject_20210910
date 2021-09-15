package com.r2872.finalproject_20210910.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.r2872.finalproject_20210910.R
import com.r2872.finalproject_20210910.datas.PlaceListData

class MyPlaceListAdapter(
    val mContext: Context,
    private val mList: List<PlaceListData>
) : RecyclerView.Adapter<MyPlaceListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(mContext).inflate(R.layout.my_place_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val placeNameTxt = itemView.findViewById<TextView>(R.id.placeName_Txt)
        private val isPrimary = itemView.findViewById<TextView>(R.id.isPrimary)
        private val viewPlaceMapBtn = itemView.findViewById<ImageView>(R.id.viewPlaceMap_Btn)
        private val backgroundLayout = itemView.findViewById<LinearLayout>(R.id.backgroundLayout)

        fun bind(item: PlaceListData) {

            placeNameTxt.text = item.name
            if (item.isPrimary) {
                isPrimary.visibility = View.VISIBLE
            }
            viewPlaceMapBtn.setOnClickListener {
                Toast.makeText(mContext, "지도 버튼 클릭", Toast.LENGTH_SHORT).show()
            }
            backgroundLayout.setOnClickListener {
                Toast.makeText(mContext, item.name, Toast.LENGTH_SHORT).show()
            }
        }

    }
}