package com.r2872.finalproject_20210910.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.r2872.finalproject_20210910.utils.FontChanger
import com.r2872.finalproject_20210910.web.ServerAPI
import com.r2872.finalproject_20210910.web.ServerAPIService
import retrofit2.Retrofit

abstract class BaseFragment : Fragment() {

    lateinit var mContext: Context
    private lateinit var retrofit: Retrofit
    lateinit var apiService: ServerAPIService

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mContext = requireContext()
        retrofit = ServerAPI.getRetrofit(mContext)
        apiService = retrofit.create(ServerAPIService::class.java)

//        모든 텍스트뷰 폰트 변경
//        FontChanger.setGlobalFont(mContext, requireView()) // requireView : 프래그먼트의 최상위 레이아웃
    }

    abstract fun setupEvents()
    abstract fun setValues()
}