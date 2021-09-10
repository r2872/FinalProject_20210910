package com.r2872.finalproject_20210910

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.r2872.finalproject_20210910.databinding.ActivitySignUpBinding

class SignUpActivity : BaseActivity() {

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {

        binding.signUpBtn.setOnClickListener {

            val inputId = binding.emailEdt.text.toString()
            val inputPassword = binding.passwordEdt.text.toString()
            val inputNickname = binding.nicknameEdt.text.toString()

            apiService.putRequestSignUp(inputId, inputPassword, inputNickname)
        }
    }

    override fun setValues() {


    }
}