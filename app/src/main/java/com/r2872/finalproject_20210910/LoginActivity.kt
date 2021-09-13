package com.r2872.finalproject_20210910

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.kakao.sdk.user.UserApiClient
import com.r2872.finalproject_20210910.databinding.ActivityLoginBinding
import com.r2872.finalproject_20210910.datas.BasicResponse
import com.r2872.finalproject_20210910.datas.DataResponse
import com.r2872.finalproject_20210910.utils.ContextUtil
import com.r2872.finalproject_20210910.web.ServerAPIService
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        setValues()
        setupEvents()
    }

    override fun setupEvents() {

        binding.autoLoginCheckBox.setOnCheckedChangeListener { _, isChecked ->

            ContextUtil.setAutoLogIn(mContext, isChecked)
        }

        callbackManager = CallbackManager.Factory.create()

        binding.loginButton.setReadPermissions("email")

        binding.facebookLoginBtn.setOnClickListener {

//            우리가 붙인 버튼 기능 활용

//            커스텀 버튼에, 로그인 하고 돌아온 Callback 을 따로 설정.
            LoginManager.getInstance()
                .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult?) {

                        Log.d("로그인 성공", "커스텀버튼으로 성공")
//                    페이스북에 접근할 수 있는 토큰이 생겨 있다. => 활용.
//                    나의 (로그인한사람) 정보 (GraphRequest) 를 받아오는데 활용.
                        val graphRequest = GraphRequest.newMeRequest(
                            result?.accessToken,
                            object : GraphRequest.GraphJSONObjectCallback {
                                override fun onCompleted(
                                    jsonObj: JSONObject?,
                                    response: GraphResponse?
                                ) {

                                    Log.d("내 정보 내용", jsonObj.toString())
                                    val name = jsonObj!!.getString("name")
                                    val id = jsonObj.getString("id")

//                                    가입한 회원 이름 => 우리 서버에 사용자 이름으로 (닉네임으로) 저장
                                    Log.d("이름", name)
//                                    페이스북에서 사용자를 구별하는 고유번호. => 우리 서버에 같이 저장. 회원가입 or 로그인 근거자료로 활용
                                    Log.d("id값", id)

                                    apiService.postRequestSocialSignIn("facebook", id, name)
                                        .enqueue(object : Callback<BasicResponse> {
                                            override fun onResponse(
                                                call: Call<BasicResponse>,
                                                response: Response<BasicResponse>
                                            ) {
                                                if (response.isSuccessful) {

                                                    Log.d("성공", response.body()?.message.toString())
                                                    Toast.makeText(
                                                        mContext,
                                                        response.body()?.message,
                                                        Toast.LENGTH_SHORT
                                                    )
                                                        .show()

                                                    val basicResponse = response.body()!!

                                                    ContextUtil.setToken(mContext, basicResponse.data.token)

                                                    val myIntent =
                                                        Intent(mContext, MainActivity::class.java)
                                                    startActivity(myIntent)
                                                    finish()
                                                } else {

                                                    val errorBodyStr =
                                                        response.errorBody()!!.string()
                                                    val jsonObj = JSONObject(errorBodyStr)
                                                    val message = jsonObj.getString("message")

                                                    Log.d("실패", message)

                                                    Toast.makeText(
                                                        mContext,
                                                        message,
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }

                                            override fun onFailure(
                                                call: Call<BasicResponse>,
                                                t: Throwable
                                            ) {

                                            }
                                        })
                                }
                            })
//                        위에서 정리한 내용을 들고, 내 정보를 실제로 요청.
                        graphRequest.executeAsync()
                    }

                    override fun onCancel() {


                    }

                    override fun onError(error: FacebookException?) {


                    }
                })

            LoginManager.getInstance()
                .logInWithReadPermissions(this, listOf("public_profile"))
        }

        binding.kakaoLoginBtn.setOnClickListener {
            UserApiClient.instance.loginWithKakaoAccount(mContext) { token, error ->
                if (error != null) {
                    Log.e(TAG, "로그인 실패", error)
                } else if (token != null) {
                    Log.i(TAG, "로그인 성공 ${token.accessToken}")

                    UserApiClient.instance.me { user, error ->
                        if (error != null) {
                            Log.e(TAG, "사용자 정보 요청 실패", error)
                        } else if (user != null) {
                            Log.i(
                                TAG, "사용자 정보 요청 성공" +
                                        "\n회원번호: ${user.id}" +
                                        "\n이메일: ${user.kakaoAccount?.email}" +
                                        "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                                        "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}"
                            )
                            val userId = user.id.toString()
                            val userNickname = user.kakaoAccount?.profile?.nickname.toString()

                            apiService.postRequestSocialSignIn("kakao", userId, userNickname)
                                .enqueue(object : Callback<BasicResponse> {
                                    override fun onResponse(
                                        call: Call<BasicResponse>,
                                        response: Response<BasicResponse>
                                    ) {
                                        if (response.isSuccessful) {

                                            Log.d("성공", response.body()?.message.toString())
                                            Toast.makeText(
                                                mContext,
                                                response.body()?.message,
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()

                                            val basicResponse = response.body()!!

                                            ContextUtil.setToken(mContext, basicResponse.data.token)

                                            val myIntent =
                                                Intent(mContext, MainActivity::class.java)
                                            startActivity(myIntent)
                                            finish()
                                        } else {

                                            val errorBodyStr =
                                                response.errorBody()!!.string()
                                            val jsonObj = JSONObject(errorBodyStr)
                                            val message = jsonObj.getString("message")

                                            Log.d("실패", message)

                                            Toast.makeText(
                                                mContext,
                                                message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<BasicResponse>,
                                        t: Throwable
                                    ) {

                                    }
                                })
                        }
                    }
                }
            }
        }

        binding.signInBtn.setOnClickListener {

            val inputId = binding.emailEdt.text.toString()
            val inputPw = binding.pwEdt.text.toString()

            apiService.postRequestSignIn(inputId, inputPw)
                .enqueue(object : Callback<BasicResponse> {
                    override fun onResponse(
                        call: Call<BasicResponse>,
                        response: Response<BasicResponse>
                    ) {
                        if (response.isSuccessful) {

                            val basicResponse = response.body()!!

                            val userNickName = basicResponse.data.user.nick_name

                            ContextUtil.setToken(mContext, basicResponse.data.token)
                            Log.d("토큰값", basicResponse.data.token)
                            Log.d("사용자 닉네임", userNickName)
                            Toast.makeText(mContext, "${userNickName} 님 환영합니다.", Toast.LENGTH_SHORT)
                                .show()

                            val myIntent = Intent(mContext, MainActivity::class.java)
                            startActivity(myIntent)
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

                    }
                })

        }

        binding.signUpBtn.setOnClickListener {
            val myIntent = Intent(mContext, SignUpActivity::class.java)
            startActivity(myIntent)
        }

    }

    override fun setValues() {

//        var keyHash = Utility.getKeyHash(this)
//        Log.d("해시코드", keyHash)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

    }

    companion object {
        const val TAG = "MainActivity"
    }
}