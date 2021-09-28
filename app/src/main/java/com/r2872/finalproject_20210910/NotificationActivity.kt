package com.r2872.finalproject_20210910

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.r2872.finalproject_20210910.adapters.NotificationAdapter
import com.r2872.finalproject_20210910.databinding.ActivityNotificationBinding
import com.r2872.finalproject_20210910.datas.BasicResponse
import com.r2872.finalproject_20210910.datas.NotificationData
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationActivity : BaseActivity() {

    private lateinit var binding: ActivityNotificationBinding
    private val mNotiList = ArrayList<NotificationData>()
    private lateinit var mAdapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {

        readAllNoti.setOnClickListener {

            if (mNotiList.isEmpty()) {
                Toast.makeText(mContext, "알람이 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            getReadNotificationFromServer()
        }
    }

    override fun setValues() {

        titleTxt.text = "알람목록"
        notiImg.visibility = View.GONE
        readAllNoti.visibility = View.VISIBLE
        mAdapter = NotificationAdapter(mContext, mNotiList)
        binding.notiList.apply {
            adapter = mAdapter
            addItemDecoration(DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL))
        }

        getNotificationsFromServer()
    }

    private fun getNotificationsFromServer() {
        apiService.getRequestNotifications(true).enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                if (response.isSuccessful) {
                    val basicResponse = response.body()!!
                    mNotiList.clear()

                    for (i in basicResponse.data.notifications.indices) {
                        if (!basicResponse.data.notifications[i].isRead) {
                            mNotiList.add(basicResponse.data.notifications[i])
                        }
                    }

                    if (mNotiList.isNotEmpty()) {
                        notiCount.visibility = View.VISIBLE
                        notiCount.text = mNotiList.size.toString()
                    } else {
                        notiCount.visibility = View.GONE
                    }
                    mAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

            }
        })
    }

    private fun getReadNotificationFromServer() {
        apiService.postRequestNotifications(mNotiList[0].id)
            .enqueue(object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {
                    if (response.isSuccessful) {
                        val basicResponse = response.body()!!

                        Toast.makeText(mContext, basicResponse.message, Toast.LENGTH_SHORT).show()
                    } else {
                        val errorResponse = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                            mContext,
                            errorResponse.getString("message"),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    mAdapter.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                }
            })
    }
}