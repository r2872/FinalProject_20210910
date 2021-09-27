package com.r2872.finalproject_20210910.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.r2872.finalproject_20210910.EditAppointmentActivity
import com.r2872.finalproject_20210910.MainActivity
import com.r2872.finalproject_20210910.R
import com.r2872.finalproject_20210910.adapters.AppointmentAdapter
import com.r2872.finalproject_20210910.adapters.AppointmentViewPagerAdapter
import com.r2872.finalproject_20210910.adapters.FriendRequestListAdapter
import com.r2872.finalproject_20210910.databinding.FragmentAppointmentListBinding
import com.r2872.finalproject_20210910.databinding.FragmentFriendRequestBinding
import com.r2872.finalproject_20210910.datas.AppointmentData
import com.r2872.finalproject_20210910.datas.BasicResponse
import com.r2872.finalproject_20210910.datas.UserData
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyAppointmentFragment : BaseFragment() {

    companion object {

        private var frag: MyAppointmentFragment? = null
        fun getFrag(): MyAppointmentFragment {
            if (frag == null) {
                frag = MyAppointmentFragment()
            }
            return frag!!
        }
    }

    private lateinit var binding: FragmentAppointmentListBinding
    private lateinit var mAdapter: AppointmentAdapter
    private val mList = ArrayList<AppointmentData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_appointment_list, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {

        binding.addBtn.setOnClickListener {

            startActivity(Intent(mContext, EditAppointmentActivity::class.java))
        }
    }

    override fun setValues() {

        mAdapter = AppointmentAdapter(mContext, mList)
        binding.appointmentList.apply {
            adapter = mAdapter
            addItemDecoration(DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL))
        }
        binding.addBtn.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        getAppointmentListFromServer()
    }

    fun getAppointmentListFromServer() {

        apiService.getRequestAppointmentList().enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {

                if (response.isSuccessful) {

                    mList.clear()
                    val basicResponse = response.body()!!

                    mList.addAll(basicResponse.data.appointments)

                } else {
                    val errorResponse = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(mContext, errorResponse.getString("message"), Toast.LENGTH_SHORT)
                        .show()
                }
                if (mList.isEmpty()) {
                    binding.emptyTxt.visibility = View.VISIBLE
                } else {
                    binding.emptyTxt.visibility = View.GONE
                }
                mAdapter.notifyDataSetChanged()

            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

            }
        })
    }

}