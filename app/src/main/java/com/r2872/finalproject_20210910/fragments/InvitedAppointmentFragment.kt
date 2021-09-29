package com.r2872.finalproject_20210910.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.r2872.finalproject_20210910.R
import com.r2872.finalproject_20210910.adapters.InvitedAppointmentAdapter
import com.r2872.finalproject_20210910.databinding.FragmentAppointmentListBinding
import com.r2872.finalproject_20210910.datas.AppointmentData
import com.r2872.finalproject_20210910.datas.BasicResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InvitedAppointmentFragment : BaseFragment() {

    companion object {

        private var frag: InvitedAppointmentFragment? = null
        fun getFrag(): InvitedAppointmentFragment {
            if (frag == null) {
                frag = InvitedAppointmentFragment()
            }
            return frag!!
        }
    }

    private lateinit var binding: FragmentAppointmentListBinding
    private lateinit var mAdapter: InvitedAppointmentAdapter
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

    }

    override fun setValues() {

        mAdapter = InvitedAppointmentAdapter(mContext, mList)
        binding.appointmentList.apply {
            adapter = mAdapter
            LinearLayoutManager.VERTICAL
            addItemDecoration(DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL))
        }
        binding.addBtn.visibility = View.GONE
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

                    mList.addAll(basicResponse.data.invited_appointments)

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