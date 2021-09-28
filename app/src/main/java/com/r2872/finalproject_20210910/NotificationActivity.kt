package com.r2872.finalproject_20210910

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.r2872.finalproject_20210910.adapters.NotificationAdapter
import com.r2872.finalproject_20210910.databinding.ActivityNotificationBinding
import com.r2872.finalproject_20210910.datas.BasicResponse
import com.r2872.finalproject_20210910.datas.NotificationData
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

    }

    override fun setValues() {

        getNotificationsFromServer()

        mAdapter = NotificationAdapter(mContext, mNotiList)
        binding.notiList.apply {
            adapter = mAdapter
            addItemDecoration(DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL))
        }
    }

    private fun getNotificationsFromServer() {
        apiService.getRequestNotifications(true).enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                if (response.isSuccessful) {
                    val basicResponse = response.body()!!
                    mNotiList.clear()

                    mNotiList.addAll(basicResponse.data.notifications)

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
}