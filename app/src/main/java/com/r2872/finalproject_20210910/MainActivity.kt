package com.r2872.finalproject_20210910

import android.content.Intent
import android.os.Bundle

import androidx.databinding.DataBindingUtil
import com.r2872.finalproject_20210910.databinding.ActivityMainBinding
import com.r2872.finalproject_20210910.utils.ContextUtil

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setValues()
        setupEvents()
    }

    override fun setupEvents() {

        binding.signOutBtn.setOnClickListener {
            ContextUtil.setToken(mContext, "")
            val myIntent = Intent(mContext, LoginActivity::class.java)
            startActivity(myIntent)
            finish()
        }

        binding.userInfoChangeBtn.setOnClickListener {
            val myIntent = Intent(mContext, UserInfoEditActivity::class.java)
            startActivity(myIntent)
        }
    }

    override fun setValues() {

    }
}