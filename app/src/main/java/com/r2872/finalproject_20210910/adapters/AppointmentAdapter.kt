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
import com.r2872.finalproject_20210910.fragments.FriendRequestFragment
import com.r2872.finalproject_20210910.fragments.MyAppointmentFragment
import com.r2872.finalproject_20210910.web.ServerAPIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.*

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

        holder.bind(mContext, mList[position])
    }

    override fun getItemCount() = mList.size

    inner class ViewHolder(view: View) : BaseViewHolder(mContext, view) {

        private val titleTxt = view.findViewById<TextView>(R.id.title_Txt)
        private val dateTxt = view.findViewById<TextView>(R.id.date_Txt)
        private val placeTxt = view.findViewById<TextView>(R.id.place_Txt)
        private val viewPlaceMapBtn = view.findViewById<ImageView>(R.id.viewPlaceMap_Btn)
        private val backgroundLayout = view.findViewById<LinearLayout>(R.id.backgroundLayout)


        fun bind(context: Context, item: AppointmentData) {

            titleTxt.text = item.title

//            ???????????? : Date ????????? ?????????. => String ?????? ??????. SimpleDateFormat ??????.
            dateTxt.text = item.getFormattedDateTime()
            placeTxt.text = item.place

            viewPlaceMapBtn.setOnClickListener {

                val myIntent = Intent(context, ViewMapActivity::class.java)
                myIntent.putExtra("appointment", item)
                context.startActivity(myIntent)
            }

            backgroundLayout.setOnClickListener {

                val myIntent = Intent(context, AppointmentDetailActivity::class.java)
                myIntent.putExtra("appointment", item)
                context.startActivity(myIntent)
            }

            backgroundLayout.setOnLongClickListener {
                val alert = AlertDialog.Builder(mContext)
                    .setTitle("?????? ??????")
                    .setPositiveButton("??????", DialogInterface.OnClickListener { _, _ ->
                        val deleteAlert = AlertDialog.Builder(context)
                            .setTitle("?????? ????????? ?????????????????????????")
                            .setPositiveButton("??????", DialogInterface.OnClickListener { _, _ ->
                                (context as MainActivity).apiService.deleteRequestAppointment(item.id)
                                    .enqueue(object : Callback<BasicResponse> {
                                        override fun onResponse(
                                            call: Call<BasicResponse>,
                                            response: Response<BasicResponse>
                                        ) {
                                            if (response.isSuccessful) {
                                                val basicResponse = response.body()!!
                                                Toast.makeText(
                                                    context,
                                                    "?????? ???????????????.",
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()
                                                (context.appointmentViewPagerAdapter.getItem(0) as MyAppointmentFragment).getAppointmentListFromServer()
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<BasicResponse>,
                                            t: Throwable
                                        ) {

                                        }
                                    })
                            })
                            .setNegativeButton("??????", null)
                        deleteAlert.show()
                    })
                    .setNegativeButton("??????", DialogInterface.OnClickListener { _, _ ->
                        val intent = Intent(context, FixAppointmentActivity::class.java)
                        intent.putExtra("appointment", item)
                        context.startActivity(intent)
                    })
                    .show()


                return@setOnLongClickListener true
            }

        }
    }
}