package com.r2872.finalproject_20210910

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler
import com.r2872.finalproject_20210910.databinding.ActivityLoginBinding
import com.r2872.finalproject_20210910.datas.BasicResponse
import com.r2872.finalproject_20210910.utils.ContextUtil
import com.r2872.finalproject_20210910.utils.GlobalData
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var callbackManager: CallbackManager
    private lateinit var mNaverLoginModule: OAuthLogin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        setValues()
        setupEvents()
    }

    override fun setupEvents() {

        callbackManager = CallbackManager.Factory.create()

        binding.loginButton.setReadPermissions("email")

        binding.naverLoginBtn.setOnClickListener {

            mNaverLoginModule.startOauthLoginActivity(this, @SuppressLint("HandlerLeak")
            object : OAuthLoginHandler() {
                override fun run(success: Boolean) {

                    if (success) {
//                        네이버 로그인에 성공하면 그 계정의 토큰값 추출.
                        val accessToken = mNaverLoginModule.getAccessToken(mContext)
                        Log.d("네이버토큰", accessToken)

//                        별개의 통신용 Thread 생성 -> 내 정보 요청
                        Thread {
//                            이 내부의 코드를 백그라운드 실행
                            val url = "https://openapi.naver.com/v1/nid/me"
                            val jsonObj = JSONObject(
                                mNaverLoginModule.requestApi(
                                    mContext,
                                    accessToken,
                                    url
                                )
                            )
                            Log.d("네이버내정보응답", jsonObj.toString())
                            val response = jsonObj.getJSONObject("response")

//                            정보 추출
                            val uid = response.getString("id")
                            val name = response.getString("name")

//                            우리 서버로 전달.
                            apiService.postRequestSocialSignIn("naver", uid, name)
                                .enqueue(object : retrofit2.Callback<BasicResponse> {
                                    override fun onResponse(
                                        call: Call<BasicResponse>,
                                        response: Response<BasicResponse>
                                    ) {
                                        if (response.isSuccessful) {
                                            val basicResponse = response.body()!!
                                            ContextUtil.setToken(
                                                mContext,
                                                basicResponse.data.token
                                            )
                                            GlobalData.loginUser = basicResponse.data.user
                                            clearContextUtilIdPw()

                                            moveToMain()
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<BasicResponse>,
                                        t: Throwable
                                    ) {

                                    }
                                })
                        }.start()
                    } else {

                        Toast.makeText(mContext, "네이버 로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }

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

                                                    ContextUtil.setToken(
                                                        mContext,
                                                        basicResponse.data.token
                                                    )
                                                    GlobalData.loginUser = basicResponse.data.user
                                                    clearContextUtilIdPw()

                                                    moveToMain()
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
                                            GlobalData.loginUser = basicResponse.data.user
                                            clearContextUtilIdPw()

                                            moveToMain()
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
            val saveInfo = binding.autoLoginCheckBox.isChecked

            if (saveInfo) {
                ContextUtil.setUserId(mContext, inputId)
                ContextUtil.setUserPw(mContext, inputPw)
            } else {
                clearContextUtilIdPw()
            }

            apiService.postRequestSignIn(inputId, inputPw)
                .enqueue(object : Callback<BasicResponse> {
                    override fun onResponse(
                        call: Call<BasicResponse>,
                        response: Response<BasicResponse>
                    ) {
                        if (response.isSuccessful) {

                            val basicResponse = response.body()!!

                            ContextUtil.setToken(mContext, basicResponse.data.token)
                            GlobalData.loginUser = basicResponse.data.user
                            Log.d("토큰값", basicResponse.data.token)

                            moveToMain()
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

//        네이버 로그인 모듈 세팅
        mNaverLoginModule = OAuthLogin.getInstance()
        mNaverLoginModule.init(
            mContext,
            getString(R.string.naver_client_id),
            getString(R.string.naver_secret_key),
            getString(R.string.naver_client_name)
        )

        titleTxt.visibility = View.GONE
        logoImg.visibility = View.VISIBLE

        val keyHash = Utility.getKeyHash(this)
        Log.d("해시코드", keyHash)
        if (ContextUtil.getUserId(mContext) != "") {
            binding.emailEdt.setText(ContextUtil.getUserId(mContext))
            binding.pwEdt.setText(ContextUtil.getUserPw(mContext))
            binding.autoLoginCheckBox.isChecked = true
            Toast.makeText(mContext, "기존 로그인 정보를 불러옵니다.", Toast.LENGTH_SHORT).show()
        } else {
            binding.autoLoginCheckBox.isChecked = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

    }

    private fun moveToMain() {
        val myIntent = Intent(mContext, MainActivity::class.java)
        startActivity(myIntent)
        finish()
    }

    private fun clearContextUtilIdPw() {
        ContextUtil.setUserId(mContext, "")
        ContextUtil.setUserPw(mContext, "")
    }

    companion object {
        const val TAG = "MainActivity"
    }
}