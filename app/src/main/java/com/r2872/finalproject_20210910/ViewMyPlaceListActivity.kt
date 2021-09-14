package com.r2872.finalproject_20210910

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.r2872.finalproject_20210910.databinding.ActivityViewMyPlaceListBinding

class ViewMyPlaceListActivity : BaseActivity() {

    private lateinit var binding: ActivityViewMyPlaceListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_my_place_list)

        setValues()
        setupEvents()
    }

    override fun setupEvents() {

        profileImg.setOnClickListener {

            startActivity(Intent(mContext, EditMyPlaceActivity::class.java))
        }
    }

    override fun setValues() {

        titleTxt.text = "내가 자주 쓰는 출발장소들"
        Glide.with(mContext)
            .load(R.drawable.ic_baseline_post_add_24)
            .into(profileImg)
        profileImg.visibility = View.VISIBLE
    }
}