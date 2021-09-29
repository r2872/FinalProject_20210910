package com.r2872.finalproject_20210910.adapters

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.r2872.finalproject_20210910.*
import com.r2872.finalproject_20210910.datas.AppointmentData
import com.r2872.finalproject_20210910.datas.BasicResponse
import com.r2872.finalproject_20210910.datas.SearchPlaceData
import com.r2872.finalproject_20210910.web.ServerAPIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.*

class DialogEditRecyclerAdapter(
    private val mContext: Context,
    private val mList: List<SearchPlaceData>
) : RecyclerView.Adapter<DialogEditRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(mContext).inflate(R.layout.searchplace_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(mContext, mList[position])
    }

    override fun getItemCount() = mList.size

    inner class ViewHolder(view: View) : BaseViewHolder(mContext, view) {

        private val placeName = view.findViewById<TextView>(R.id.placeName_Txt)
        private val addressName = view.findViewById<TextView>(R.id.addressName_Txt)
        private val backgroundLayout = view.findViewById<LinearLayout>(R.id.backgroundLayout)

        fun bind(context: Context, item: SearchPlaceData) {

            placeName.text = item.placeName
            addressName.text = item.addressName

            backgroundLayout.setOnClickListener {

                (context as FixAppointmentActivity).apply {
                    intent.putExtra("placeName", item.placeName)
                    mSelectedLat = item.lat.toDouble()
                    mSelectedLng = item.lng.toDouble()
                    dialog.dismiss()
                    placeSearchEvents()
                }

            }
        }
    }
}