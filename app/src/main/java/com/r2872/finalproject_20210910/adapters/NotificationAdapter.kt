package com.r2872.finalproject_20210910.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.r2872.finalproject_20210910.AppointmentDetailActivity
import com.r2872.finalproject_20210910.InvitedAppointmentDetailActivity
import com.r2872.finalproject_20210910.NotificationActivity
import com.r2872.finalproject_20210910.R
import com.r2872.finalproject_20210910.datas.AppointmentData
import com.r2872.finalproject_20210910.datas.BasicResponse
import com.r2872.finalproject_20210910.datas.NotificationData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationAdapter(
    private val mContext: Context,
    private val mList: List<NotificationData>
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    private lateinit var AppoData: AppointmentData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(mContext).inflate(R.layout.noti_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(mContext, mList[position])
    }

    override fun getItemCount() = mList.size

    inner class ViewHolder(view: View) : BaseViewHolder(mContext, view) {

        private val type = view.findViewById<TextView>(R.id.type_Txt)
        private val message = view.findViewById<TextView>(R.id.message_Txt)
        private val background = view.findViewById<LinearLayout>(R.id.backgroundLayout)

        fun bind(context: Context, item: NotificationData) {

            type.text = item.type
            message.text = item.message

            background.setOnClickListener {

                if (item.actUserId == item.receiveUserId) {
                    getAppointmentFromServer(context, item.focusId, true)
                } else {
                    getAppointmentFromServer(context, item.focusId, false)
                }
            }
        }

        private fun getAppointmentFromServer(context: Context, id: Int, my: Boolean) {
            (context as NotificationActivity).apiService.getRequestAppointmentDetail(id)
                .enqueue(object : Callback<BasicResponse> {
                    override fun onResponse(
                        call: Call<BasicResponse>,
                        response: Response<BasicResponse>
                    ) {
                        if (response.isSuccessful) {
                            val basicResponse = response.body()!!
                            AppoData = basicResponse.data.appointment

                            val intent: Intent
                            if (my) {
                                intent = Intent(context, AppointmentDetailActivity::class.java)
                                intent.putExtra("appointment", AppoData)
                                context.startActivity(intent)
                            } else {
                                intent =
                                    Intent(context, InvitedAppointmentDetailActivity::class.java)
                                intent.putExtra("appointment", AppoData)
                                context.startActivity(intent)
                            }

                        }
                    }

                    override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                    }
                })
        }
    }
}