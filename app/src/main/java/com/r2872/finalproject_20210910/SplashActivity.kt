package com.r2872.finalproject_20210910

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.r2872.finalproject_20210910.databinding.ActivitySplashBinding
import com.r2872.finalproject_20210910.utils.ContextUtil

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

        val myHandler = Handler(Looper.getMainLooper())

        Glide.with(mContext)
            .load(R.raw.icon_16)
            .into(binding.splashImg)

        myHandler.postDelayed({

//        1. 자동 로그인 여부 판단 -> 상황에 따라 다른 화면으로 넘어가게.
//        다른 화면: Intent 의 목적지만 달라진다.
            val myIntent: Intent

//        자동 로그인 여부: 사용자가 자동로그인 하겠다 + 저장 된 토큰이 유효(들어있다)하다.
            if (ContextUtil.getAutoLogIn(mContext) && ContextUtil.getToken(mContext) != "") {

//            둘다 만족: 자동 로그인 O -> 메인화면으로 이동.
                myIntent = Intent(mContext, MainActivity::class.java)


            } else {
//            하나라도 만족 안됨: 자동 로그인 실패 -> 로그인 화면으로 이동
                myIntent = Intent(mContext, LoginActivity::class.java)

//                내가 누구인지 받아오지 않겠다. (코드 작성 X)
            }
            startActivity(myIntent)
            finish()
        }, 2000)
    }
}