package com.r2872.finalproject_20210910

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.r2872.finalproject_20210910.adapters.FriendsViewPagerAdapter
import com.r2872.finalproject_20210910.databinding.ActivityViewMyFriendsListBinding
import com.r2872.finalproject_20210910.fragments.FriendRequestFragment
import com.r2872.finalproject_20210910.fragments.MyFriendListFragment

class VIewMyFriendsListActivity : BaseActivity() {

    private lateinit var binding: ActivityViewMyFriendsListBinding
    lateinit var friendsViewPagerAdapter: FriendsViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_my_friends_list)

        setValues()
        setupEvents()
    }

    override fun setupEvents() {

        binding.friendsViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {

//                각 페이지에 맞는 프래그먼트의 새로고침 실행.
                when (position) {
                    0 -> (friendsViewPagerAdapter.getItem(position) as MyFriendListFragment).getFriendListFromServer()
                    else -> (friendsViewPagerAdapter.getItem(position) as FriendRequestFragment).getRequestedFriendFromUser()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        profileImg.setOnClickListener {

            val myIntent = Intent(mContext, AddFriendActivity::class.java)
            startActivity(myIntent)
        }
    }

    override fun setValues() {

        titleTxt.text = "친구 관리"
        profileImg.setImageResource(R.drawable.ic_baseline_person_add_alt_1_24)
        profileImg.visibility = View.VISIBLE
        notiImg.visibility = View.GONE
        friendsViewPagerAdapter = FriendsViewPagerAdapter(supportFragmentManager)
        binding.friendsViewPager.adapter = friendsViewPagerAdapter
        binding.friendsTabLayout.setupWithViewPager(binding.friendsViewPager)
    }
}