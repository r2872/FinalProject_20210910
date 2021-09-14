package com.r2872.finalproject_20210910

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.r2872.finalproject_20210910.databinding.ActivityUserInfoEditBinding
import com.r2872.finalproject_20210910.datas.BasicResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserInfoEditActivity : BaseActivity() {

    private lateinit var binding: ActivityUserInfoEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_info_edit)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {

        binding.saveBtn.setOnClickListener {

            val inputCurrentPw = binding.currentPwEdt.text.toString()
            val inputNewPw = binding.newPwEdt.text.toString()
            val inputNickname = binding.nicknameEdtEdt.text.toString()

            apiService.patchRequestEditUser(inputCurrentPw, inputNewPw, inputNickname)
                .enqueue(object : Callback<BasicResponse> {
                    override fun onResponse(
                        call: Call<BasicResponse>,
                        response: Response<BasicResponse>
                    ) {
                        if (response.isSuccessful) {

                            val basicResponse = response.body()!!

                            val userNickName = basicResponse.data.user.nickName

                            Log.d("사용자 닉네임", userNickName)
                            Toast.makeText(mContext, "수정 완료", Toast.LENGTH_SHORT)
                                .show()
                            finish()
                        } else {

                            Toast.makeText(
                                mContext,
                                JSONObject(response.errorBody()!!.string()).getString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                        Log.d("오류", t.toString())
                    }
                })
        }
    }

    override fun setValues() {

        titleTxt.text = "정보 수정"
    }
}