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
import com.r2872.finalproject_20210910.adapters.InvitedAppointmentAdapter
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
    private lateinit var mInvitedAdapter: InvitedAppointmentAdapter
    private val mAppointmentList = ArrayList<AppointmentData>()
    private val mInvitedAppointmentList = ArrayList<AppointmentData>()
    private var waitTime = 0L

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

    override fun onBackPressed() {
        if (System.currentTimeMillis() - waitTime >= 1500) {
            waitTime = System.currentTimeMillis()
            Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else {
            finish() // 액티비티 종료
        }

    }

    override fun setupEvents() {

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

        mInvitedAdapter = InvitedAppointmentAdapter(mContext, mInvitedAppointmentList)
        binding.invitedList.adapter = mInvitedAdapter
        binding.invitedList.addItemDecoration(
            DividerItemDecoration(
                mContext,
                LinearLayoutManager.VERTICAL
            )
        )
    }

    fun getAppointmentListFromServer() {

        apiService.getRequestAppointmentList().enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {

                if (response.isSuccessful) {

                    mAppointmentList.clear()
                    mInvitedAppointmentList.clear()
                    val basicResponse = response.body()!!
                    Log.d("리스트", basicResponse.data.appointments.toString())

//                    약속목록변수에 => 서버가 알려준 약속목록을 전부 추가.
                    mAppointmentList.addAll(basicResponse.data.appointments)
                    mInvitedAppointmentList.addAll(basicResponse.data.invited_appointments)

//                    for (apData in basicResponse.data.appoinments) {
//                        Log.d("약속리스트", apData.title)
//                    }
                } else {
                    Toast.makeText(mContext, response.errorBody()!!.string(), Toast.LENGTH_SHORT)
                        .show()

                }
                if (mAppointmentList.isEmpty()) {
                    binding.createdListMenu.text = "아직 만든 일정이 없습니다."
                } else {
                    binding.createdListMenu.text = "내가 만든 일정목록"
                }
                if (mInvitedAppointmentList.isEmpty()) {
                    binding.invitedListMenu.text = "초대 받은 일정이 없습니다."
                } else {
                    binding.invitedListMenu.text = "초대 받은 일정목록"

                }
                mAdapter.notifyDataSetChanged()
                mInvitedAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

            }
        })
    }
}