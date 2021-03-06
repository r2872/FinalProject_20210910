package com.r2872.finalproject_20210910

import android.content.Context
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.r2872.finalproject_20210910.utils.FontChanger
import com.r2872.finalproject_20210910.web.ServerAPI
import com.r2872.finalproject_20210910.web.ServerAPIService
import retrofit2.Retrofit

abstract class BaseActivity : AppCompatActivity() {

    lateinit var mContext: Context

    //    모든 화면에 레트로핏 / API 서비스를 미리 만들어서 물려주자.
//    각 화면에서는 변수를 불러내서 사용만 하면 되도록.
    private lateinit var retrofit: Retrofit
    lateinit var apiService: ServerAPIService

    //    액션바에 있는 UI 요소들을 상속시켜주자.
    lateinit var notiImg: FrameLayout
    lateinit var titleTxt: TextView
    lateinit var logoImg: ImageView
    lateinit var profileImg: ImageView
    lateinit var notiCount: TextView
    lateinit var readAllNoti: TextView

    override fun onStart() {
        super.onStart()

        //        (액티비티의 최상위 태그) rootView 받아와서 폰트변경기에 의뢰.
        val rootView = window.decorView.rootView
        FontChanger.setGlobalFont(mContext, rootView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this

        retrofit = ServerAPI.getRetrofit(mContext)
        apiService = retrofit.create(ServerAPIService::class.java)

        supportActionBar?.let {
            setCustomActionBar()
        }
    }

    abstract fun setupEvents()
    abstract fun setValues()

    private fun setCustomActionBar() {

        val defActionBar = supportActionBar!!

        defActionBar.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        defActionBar.setCustomView(R.layout.my_custom_action_bar)

        val toolBar = defActionBar.customView.parent as Toolbar
        toolBar.setContentInsetsAbsolute(0, 0)

        titleTxt = defActionBar.customView.findViewById(R.id.title_Txt)
        logoImg = defActionBar.customView.findViewById(R.id.logo_Img)
        notiImg = defActionBar.customView.findViewById(R.id.noti_Img)
        profileImg = defActionBar.customView.findViewById(R.id.profile_Img)
        notiCount = defActionBar.customView.findViewById(R.id.noticount_Txt)
        readAllNoti = defActionBar.customView.findViewById(R.id.readAllNoti_Txt)
    }
}