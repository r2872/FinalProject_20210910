package com.r2872.finalproject_20210910

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.r2872.finalproject_20210910.adapters.AppointmentAdapter
import com.r2872.finalproject_20210910.databinding.ActivityMainBinding
import com.r2872.finalproject_20210910.datas.AppointmentData
import com.r2872.finalproject_20210910.datas.BasicResponse
import com.r2872.finalproject_20210910.utils.GlobalData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mAdapter: AppointmentAdapter
    private val mAppointmentList = ArrayList<AppointmentData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setValues()
        setupEvents()
    }

    override fun onResume() {
        super.onResume()

        getAppointmentListFromServer()
    }

    override fun setupEvents() {

        binding.refreshLayout.setOnRefreshListener {
            getAppointmentListFromServer()

            Toast.makeText(mContext, "새로고침 완료", Toast.LENGTH_SHORT).show()

            binding.refreshLayout.isRefreshing = false
        }

        binding.addAppoinmentBtn.setOnClickListener {

            val myIntent = Intent(mContext, EditAppointmentActivity::class.java)
            startActivity(myIntent)
        }

        profileImg.setOnClickListener {
            val myIntent = Intent(mContext, UserInfoActivity::class.java)
            startActivity(myIntent)
        }
    }

    override fun setValues() {

        profileImg.visibility = View.VISIBLE
        titleTxt.text = "일정 목록"

        Toast.makeText(mContext, "${GlobalData.loginUser!!.nickName} 님 환영합니다.", Toast.LENGTH_SHORT)
            .show()

        mAdapter = AppointmentAdapter(mContext, mAppointmentList)
        binding.scheduleList.adapter = mAdapter
        binding.scheduleList.addItemDecoration(
            DividerItemDecoration(
                mContext,
                LinearLayoutManager.VERTICAL
            )
        )
    }

    private fun getAppointmentListFromServer() {

        apiService.getRequestAppointmentList().enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {

                if (response.isSuccessful) {

                    mAppointmentList.clear()
                    val basicResponse = response.body()!!
                    Log.d("리스트", basicResponse.data.appointments.toString())

//                    약속목록변수에 => 서버가 알려준 약속목록을 전부 추가.
                    mAppointmentList.addAll(basicResponse.data.appointments)

//                    for (apData in basicResponse.data.appoinments) {
//                        Log.d("약속리스트", apData.title)
//                    }
                } else {
                    Toast.makeText(mContext, response.errorBody()!!.string(), Toast.LENGTH_SHORT)
                        .show()

                }
                mAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

            }
        })
    }
}