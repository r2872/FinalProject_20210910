package com.r2872.finalproject_20210910

import android.content.Intent
import android.os.Bundle
import android.view.View
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

        profileImg.setOnClickListener {

            val myIntent = Intent(mContext, AddFriendActivity::class.java)
            startActivity(myIntent)
        }
    }

    override fun setValues() {

        titleTxt.text = "친구 관리"
        profileImg.setImageResource(R.drawable.ic_baseline_person_add_alt_1_24)
        profileImg.visibility = View.VISIBLE
        friendsViewPagerAdapter = FriendsViewPagerAdapter(supportFragmentManager)
        binding.friendsViewPager.adapter = friendsViewPagerAdapter
        binding.friendsTabLayout.setupWithViewPager(binding.friendsViewPager)
    }
}