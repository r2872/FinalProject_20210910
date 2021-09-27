package com.r2872.finalproject_20210910

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.r2872.finalproject_20210910.adapters.AppointmentViewPagerAdapter
import com.r2872.finalproject_20210910.databinding.ActivityMainBinding
import com.r2872.finalproject_20210910.fragments.InvitedAppointmentFragment
import com.r2872.finalproject_20210910.fragments.MyAppointmentFragment
import com.r2872.finalproject_20210910.utils.GlobalData


class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var appointmentViewPagerAdapter: AppointmentViewPagerAdapter
    private var waitTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setValues()
        setupEvents()
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() - waitTime >= 1500) {
            waitTime = System.currentTimeMillis()
            Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else {
            finish() // 액티비티 종료
        }

    }

    override fun setupEvents() {

        binding.appointmentsViewPager.addOnPageChangeListener(object :
            ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {

//                각 페이지에 맞는 프래그먼트의 새로고침 실행.
                when (position) {
                    0 -> {
                        (appointmentViewPagerAdapter.getItem(position) as MyAppointmentFragment).getAppointmentListFromServer()
                        titleTxt.text = "내가만든 일정"
                    }
                    1 -> {
                        (appointmentViewPagerAdapter.getItem(position) as InvitedAppointmentFragment).getAppointmentListFromServer()
                        titleTxt.text = "초대받은 일정"
                    }
                    else -> titleTxt.text = "내 정보"
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    override fun setValues() {

        titleTxt.text = "일정 목록"

        Toast.makeText(mContext, "${GlobalData.loginUser!!.nickName} 님 환영합니다.", Toast.LENGTH_SHORT)
            .show()

        appointmentViewPagerAdapter = AppointmentViewPagerAdapter(supportFragmentManager)
        binding.appointmentsViewPager.adapter = appointmentViewPagerAdapter
        binding.appointmentsTabLayout.setupWithViewPager(binding.appointmentsViewPager)
        binding.appointmentsTabLayout.apply {
            getTabAt(0)?.setIcon(R.drawable.calendar)
            getTabAt(1)?.setIcon(R.drawable.invitation)
            getTabAt(2)?.setIcon(R.drawable.ic_baseline_person_24)
        }

        val root: View = binding.appointmentsTabLayout.getChildAt(0)
        if (root is LinearLayout) {
            (root).showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
            val drawable = GradientDrawable()
            drawable.setColor(resources.getColor(R.color.gray))
            drawable.setSize(2, 1)
            (root).dividerPadding = 10
            (root).dividerDrawable = drawable
        }
    }
}