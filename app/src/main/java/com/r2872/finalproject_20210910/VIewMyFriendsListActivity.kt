package com.r2872.finalproject_20210910

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.r2872.finalproject_20210910.databinding.ActivityViewMyFriendsListBinding

class VIewMyFriendsListActivity : BaseActivity() {

    private lateinit var binding: ActivityViewMyFriendsListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_my_friends_list)
    }

    override fun setupEvents() {

    }

    override fun setValues() {

        titleTxt.text = "친구 관리"
    }
}