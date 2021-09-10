package com.r2872.finalproject_20210910

import android.os.Bundle

import androidx.databinding.DataBindingUtil
import com.r2872.finalproject_20210910.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setValues()
        setupEvents()
    }

    override fun setupEvents() {

    }

    override fun setValues() {

    }
}