package com.r2872.finalproject_20210910

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.r2872.finalproject_20210910.adapters.MyPlaceListAdapter
import com.r2872.finalproject_20210910.databinding.ActivityViewMyPlaceListBinding
import com.r2872.finalproject_20210910.datas.BasicResponse
import com.r2872.finalproject_20210910.datas.PlaceListData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewMyPlaceListActivity : BaseActivity() {

    private lateinit var binding: ActivityViewMyPlaceListBinding
    private lateinit var mAdapter: MyPlaceListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_my_place_list)

        setValues()
        setupEvents()
    }

    override fun onResume() {
        super.onResume()

        getMyAppointmentListFromServer()
    }

    override fun setupEvents() {

        profileImg.setOnClickListener {

            startActivity(Intent(mContext, EditMyPlaceActivity::class.java))
        }
    }

    override fun setValues() {

        titleTxt.text = "내가 자주 쓰는 출발장소들"
        Glide.with(mContext)
            .load(R.drawable.ic_baseline_post_add_24)
            .into(profileImg)
        profileImg.visibility = View.VISIBLE
    }

    private fun getMyAppointmentListFromServer() {

        apiService.getRequestMyAppointmentList().enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {

                if (response.isSuccessful) {

                    val basicResponse = response.body()!!

                    Log.d("서버응답", basicResponse.toString())
                    mAdapter = MyPlaceListAdapter(mContext)
                    mAdapter.datas = basicResponse.data.places as MutableList<PlaceListData>
                    binding.placeListRecyclerView.adapter = mAdapter
                }

                mAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

            }
        })
    }
}