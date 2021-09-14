package com.r2872.finalproject_20210910

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.r2872.finalproject_20210910.databinding.ActivitySplashBinding
import com.r2872.finalproject_20210910.datas.BasicResponse
import com.r2872.finalproject_20210910.utils.ContextUtil
import com.r2872.finalproject_20210910.utils.GlobalData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        setValues()
        setupEvents()
    }

    override fun setupEvents() {

    }

    override fun setValues() {

        Glide.with(mContext)
            .load(R.raw.icon_16)
            .into(binding.splashImg)

        apiService.getRequestMyInfo().enqueue(object : Callback<BasicResponse> {
            override fun onResponse(
                call: Call<BasicResponse>,
                response: Response<BasicResponse>
            ) {
                if (response.isSuccessful) {

                    val basicResponse = response.body()!!
                    val user = basicResponse.data.user
                    Log.d("유저정보테스트성공", user.toString())

                    GlobalData.loginUser = user
                } else {
                    Log.d("유저정보테스트실패", GlobalData.loginUser.toString())
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

            }
        })

        val myHandler = Handler(Looper.getMainLooper())
        myHandler.postDelayed({

            val myIntent: Intent

            if (GlobalData.loginUser != null) {

                Log.d("유저정보", GlobalData.loginUser.toString())

                myIntent = Intent(mContext, MainActivity::class.java)

            } else {

                myIntent = Intent(mContext, LoginActivity::class.java)
            }
            startActivity(myIntent)
            finish()
        }, 2500)
    }
}