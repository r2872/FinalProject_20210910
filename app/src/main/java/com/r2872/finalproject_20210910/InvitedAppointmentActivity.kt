package com.r2872.finalproject_20210910

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.r2872.finalproject_20210910.adapters.AppointmentAdapter
import com.r2872.finalproject_20210910.databinding.ActivityInvitedAppointmentBinding
import com.r2872.finalproject_20210910.datas.AppointmentData
import com.r2872.finalproject_20210910.datas.BasicResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InvitedAppointmentActivity : BaseActivity() {

    private lateinit var binding: ActivityInvitedAppointmentBinding
    private val mAppointmentList = ArrayList<AppointmentData>()
    private lateinit var mAdapter: AppointmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_invited_appointment)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {

        binding.refreshLayout.setOnRefreshListener {
            getAppointmentListFromServer()

            Toast.makeText(mContext, "새로고침 완료", Toast.LENGTH_SHORT).show()

            binding.refreshLayout.isRefreshing = false
        }
    }

    override fun setValues() {

        titleTxt.text = "초대받은 일정목록"

        mAdapter = AppointmentAdapter(mContext, mAppointmentList)
        binding.invitedList.adapter = mAdapter
        binding.invitedList.addItemDecoration(
            DividerItemDecoration(
                mContext,
                LinearLayoutManager.VERTICAL
            )
        )
    }

    override fun onResume() {
        super.onResume()
        getAppointmentListFromServer()
    }

    private fun getAppointmentListFromServer() {

        apiService.getRequestAppointmentList().enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {

                if (response.isSuccessful) {

                    mAppointmentList.clear()
                    val basicResponse = response.body()!!
                    Log.d("리스트", basicResponse.data.invited_appointments.toString())

//                    약속목록변수에 => 서버가 알려준 약속목록을 전부 추가.
                    mAppointmentList.addAll(basicResponse.data.invited_appointments)

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