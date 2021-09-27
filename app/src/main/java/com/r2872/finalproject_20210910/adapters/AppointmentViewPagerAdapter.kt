package com.r2872.finalproject_20210910.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.r2872.finalproject_20210910.fragments.*

class AppointmentViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "내가만든 일정"
            1 -> "초대받은 일정"
            else -> "내 정보"
        }
    }

    override fun getCount() = 3

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> MyAppointmentFragment.getFrag()
            1 -> InvitedAppointmentFragment.getFrag()
            else -> UserInfoFragment.getFrag()
        }
    }
}