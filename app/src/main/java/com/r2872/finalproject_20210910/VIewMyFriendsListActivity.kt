package com.r2872.finalproject_20210910

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.r2872.finalproject_20210910.adapters.FriendsViewPagerAdapter
import com.r2872.finalproject_20210910.databinding.ActivityViewMyFriendsListBinding

class VIewMyFriendsListActivity : BaseActivity() {

    private lateinit var binding: ActivityViewMyFriendsListBinding
    private lateinit var friendsViewPagerAdapter: FriendsViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_my_friends_list)

        setValues()
        setupEvents()
    }

    override fun setupEvents() {

    }

    override fun setValues() {

        titleTxt.text = "친구 관리"
        friendsViewPagerAdapter = FriendsViewPagerAdapter(supportFragmentManager)
        binding.friendsViewPager.adapter = friendsViewPagerAdapter
        binding.friendsTabLayout.setupWithViewPager(binding.friendsViewPager)
    }
}