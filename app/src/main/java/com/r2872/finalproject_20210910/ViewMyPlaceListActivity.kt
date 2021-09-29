package com.r2872.finalproject_20210910

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
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
    private val mList = ArrayList<PlaceListData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_my_place_list)

        setValues()
        setupEvents()
    }

    override fun onResume() {
        super.onResume()

        getMyPlaceListFromServer()
    }

    override fun setupEvents() {

        profileImg.setOnClickListener {

            startActivity(Intent(mContext, EditMyPlaceActivity::class.java))
        }
    }

    override fun setValues() {

        titleTxt.text = "내가 자주 쓰는 출발장소들"
        profileImg.setImageResource(R.drawable.ic_baseline_post_add_24)
        profileImg.visibility = View.VISIBLE
        notiImg.visibility = View.GONE

        mAdapter = MyPlaceListAdapter(mContext, mList)
        binding.placeListRecyclerView.adapter = mAdapter
        binding.placeListRecyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )
    }

    fun getMyPlaceListFromServer() {

        apiService.getRequestMyAppointmentList().enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {

                if (response.isSuccessful) {

                    val basicResponse = response.body()!!

                    mList.clear()
                    mList.addAll(basicResponse.data.places)
                    Log.d("서버응답", basicResponse.toString())
                }

                mAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

            }
        })
    }
}