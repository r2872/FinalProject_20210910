package com.r2872.finalproject_20210910

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.r2872.finalproject_20210910.databinding.ActivityAddFriendBinding

class AddFriendActivity : BaseActivity() {

    private lateinit var binding: ActivityAddFriendBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_friend)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {

    }

    override fun setValues() {

    }
}