package com.r2872.finalproject_20210910

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.r2872.finalproject_20210910.databinding.ActivityLoginBinding
import java.util.*


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

        callbackManager = CallbackManager.Factory.create()

        binding.loginButton.setReadPermissions("email")

        binding.facebookLoginBtn.setOnClickListener {

//            우리가 붙인 버튼 기능 활용
            LoginManager.getInstance()
                .logInWithReadPermissions(this, listOf("public_profile"))
        }

        // Callback registration
        binding.loginButton.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    // App code

                    Log.d("확인용", loginResult.toString())
                    val accessToken = AccessToken.getCurrentAccessToken()
                    Log.d("페북토큰", accessToken.token.toString())
                }

                override fun onCancel() {
                    // App code
                }

                override fun onError(exception: FacebookException) {
                    // App code
                }
            })
    }

    override fun setValues() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

    }
}