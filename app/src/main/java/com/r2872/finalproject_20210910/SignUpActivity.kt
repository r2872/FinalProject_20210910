package com.r2872.finalproject_20210910

import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.r2872.finalproject_20210910.databinding.ActivitySignUpBinding
import com.r2872.finalproject_20210910.datas.BasicResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
                .enqueue(object : Callback<BasicResponse> {
                    override fun onResponse(
                        call: Call<BasicResponse>,
                        response: Response<BasicResponse>
                    ) {

                        Log.d("retrofit", response.body().toString())
                    }

                    override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                        Log.e("retrofit", t.toString())
                    }
                })
        }
    }

    override fun setValues() {


    }
}