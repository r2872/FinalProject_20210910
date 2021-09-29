package com.r2872.finalproject_20210910

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.r2872.finalproject_20210910.databinding.ActivitySignUpBinding
import com.r2872.finalproject_20210910.datas.BasicResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : BaseActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private var emailCheck = false
    private var nicknameCheck = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {

        binding.idCheckBtn.setOnClickListener {
            if (binding.emailEdt.text.isEmpty()) {
                toastMessage("이메일을 입력해주세요.")
                return@setOnClickListener
            }
            userCheckFromServer("EMAIL", binding.emailEdt.text.toString())
        }
        binding.emailEdt.addTextChangedListener {
            emailCheck = false
            buttonEnabled()
        }

        binding.nickCheckBtn.setOnClickListener {
            if (binding.nicknameEdt.text.isEmpty()) {
                toastMessage("닉네임을 입력해주세요")
                return@setOnClickListener
            }
            userCheckFromServer("NICK_NAME", binding.nicknameEdt.text.toString())
        }
        binding.nicknameEdt.addTextChangedListener {
            nicknameCheck = false
            buttonEnabled()
        }


        binding.passwordEdt.addTextChangedListener {
            buttonEnabled()
        }

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

//                        response.body() -> 응답 코드 200번이어야 들어있다. (성공)
//                        가입실패 / 로그인 실패 -> 응답 코드 400 -> errorBody 에서 따로 찾아야함. (실패)

                        if (response.isSuccessful) {
                            val basicResponse = response.body()!!
                            Log.d("서버 메세지", basicResponse.message)
                            Toast.makeText(mContext, basicResponse.message, Toast.LENGTH_SHORT)
                                .show()
                            finish()
                        } else {
//                            어떤 이유던 성공이 아닌 상황.
                            val errorBodyStr = response.errorBody()!!.string()

//                            단순 JSON 형태의 String 으로 내려옴. => JSONObject 형태로 가공 필요.
                            Log.d("에러 경우", errorBodyStr)
                            val jsonObj = JSONObject(errorBodyStr)
                            val message = jsonObj.getString("message")

//                            runOnUiThread 를 해주지 않아도 UI 접근 가능.
                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
                        }

                    }

                    override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                        Log.e("retrofit", t.toString())
                    }
                })
        }
    }

    override fun setValues() {

    }

    private fun userCheckFromServer(type: String, inputString: String) {
        apiService.getRequestUserCheck(type, inputString).enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                if (response.isSuccessful) {
                    when (type) {
                        "EMAIL" -> {
                            toastMessage("사용가능한 이메일입니다.")
                            emailCheck = true
                        }
                        "NICK_NAME" -> {
                            toastMessage("사용가능한 닉네임입니다.")
                            nicknameCheck = true
                        }
                    }
                } else {
                    toastMessage(JSONObject(response.errorBody()!!.string()).getString("message"))
                    when (type) {
                        "EMAIL" -> emailCheck = false
                        "NICK_NAME" -> nicknameCheck = false
                    }
                }
                buttonEnabled()
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

            }
        })
    }

    private fun toastMessage(inputString: String) {
        Toast.makeText(mContext, inputString, Toast.LENGTH_SHORT).show()
    }

    private fun buttonEnabled() {
        binding.signUpBtn.isEnabled =
            emailCheck && nicknameCheck && binding.passwordEdt.text.isNotEmpty()
    }
}