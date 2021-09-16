package com.r2872.finalproject_20210910.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.r2872.finalproject_20210910.fragments.FriendRequestFragment
import com.r2872.finalproject_20210910.fragments.MyFriendListFragment

class FriendsViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "내 친구"
            else -> "친구 요청"
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> MyFriendListFragment()
            else -> FriendRequestFragment()
        }
    }
}