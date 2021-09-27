package com.r2872.finalproject_20210910.fragments

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.r2872.finalproject_20210910.*
import com.r2872.finalproject_20210910.databinding.ActivityUserInfoBinding
import com.r2872.finalproject_20210910.datas.BasicResponse
import com.r2872.finalproject_20210910.utils.ContextUtil
import com.r2872.finalproject_20210910.utils.GlobalData
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserInfoFragment : BaseFragment() {

    companion object {

        private var frag: UserInfoFragment? = null
        fun getFrag(): UserInfoFragment {
            if (frag == null) {
                frag = UserInfoFragment()
            }
            return frag!!
        }
    }

    private lateinit var binding: ActivityUserInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.activity_user_info, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupEvents()
        setValues()
    }


    override fun setupEvents() {

        binding.pwChangeLayout.setOnClickListener {
            val customView =
                LayoutInflater.from(mContext).inflate(R.layout.my_custom_alert_edt_password, null)

            val myAlert = AlertDialog.Builder(mContext)
                .setTitle("비밀번호 변경")
                .setView(customView)
                .setPositiveButton("입력", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {

                        val currentPwEdt = customView.findViewById<EditText>(R.id.currentPw_Edt)
                        val newPwEdt = customView.findViewById<EditText>(R.id.newPw_Edt)

                        if (newPwEdt.text.isEmpty() || currentPwEdt.text.isEmpty()) {
                            Toast.makeText(mContext, "내용을 입력하세요", Toast.LENGTH_SHORT).show()
                            return
                        }
                        apiService.patchRequestUserPassword(
                            currentPwEdt.text.toString(),
                            newPwEdt.text.toString()
                        ).enqueue(object : Callback<BasicResponse> {
                            override fun onResponse(
                                call: Call<BasicResponse>,
                                response: Response<BasicResponse>
                            ) {
                                if (response.isSuccessful) {
                                    val basicResponse = response.body()!!
                                    Toast.makeText(mContext, "변경에 성공하였습니다.", Toast.LENGTH_SHORT)
                                        .show()
                                    ContextUtil.setToken(mContext, basicResponse.data.token)
                                } else {
                                    Toast.makeText(
                                        mContext,
                                        JSONObject(
                                            response.errorBody()!!.string()
                                        ).getString("message"),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                            }
                        })

                    }
                })
                .setNegativeButton("취소", null)
            myAlert.show()
        }

        binding.myFriendsLayout.setOnClickListener {
            val myIntent = Intent(mContext, VIewMyFriendsListActivity::class.java)
            startActivity(myIntent)
        }

//        프로필사진 누르면 => 프사 변경의 의미로 활용. => 갤러리로 프사 선택하러 진입.
//        안드로이드가 제공하는 갤러리 화면 활용. Intent (4) 추가 항목
//        어떤사진? 결과를 얻기 위해 화면을 이동. Intent (3) 활용.
        binding.profileImg.setOnClickListener {

            startActivity(Intent(mContext, ViewProfilePopUpActivity::class.java))
        }

        binding.readyTimeLayout.setOnClickListener {

            val customView =
                LayoutInflater.from(mContext).inflate(R.layout.my_custom_alert_edt_readytime, null)

            val myAlert = AlertDialog.Builder(mContext)
                .setTitle("나의 외출 준비시간")
                .setView(customView)
                .setPositiveButton("입력", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {

                        val readyTimeEdt = customView.findViewById<EditText>(R.id.readyTime_Edt)

                        if (readyTimeEdt.text.toString() == "") {
                            Toast.makeText(mContext, "내용을 입력하세요", Toast.LENGTH_SHORT).show()
                            return
                        }

                        apiService.patchRequestEditUser(
                            "ready_minute",
                            readyTimeEdt.text.toString()
                        ).enqueue(object : Callback<BasicResponse> {
                            override fun onResponse(
                                call: Call<BasicResponse>,
                                response: Response<BasicResponse>
                            ) {
                                if (response.isSuccessful) {

//                                    내 수정된 정보 파싱. => 로그인한 사용자의 정보로 갱신.
                                    val basicResponse = response.body()!!

                                    GlobalData.loginUser = basicResponse.data.user

                                    Toast.makeText(mContext, "변경 완료", Toast.LENGTH_SHORT).show()
                                    setUserInfo()
                                }

                            }

                            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                            }
                        })
                    }
                })
                .setNegativeButton("취소", null)
            myAlert.show()
        }

        binding.editNickNameBtn.setOnClickListener {

            val customView =
                LayoutInflater.from(mContext).inflate(R.layout.my_custom_alert_edt_nickname, null)

            val myAlert = AlertDialog.Builder(mContext)
                .setTitle("닉네임 변경")
                .setView(customView)
                .setPositiveButton("입력", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {

                        val nicknameEdt = customView.findViewById<EditText>(R.id.nickname_Edt)

                        if (nicknameEdt.text.toString() == "") {
                            Toast.makeText(mContext, "내용을 입력하세요", Toast.LENGTH_SHORT).show()
                            return
                        }

                        apiService.patchRequestEditUser(
                            "nickname",
                            nicknameEdt.text.toString()
                        ).enqueue(object : Callback<BasicResponse> {
                            override fun onResponse(
                                call: Call<BasicResponse>,
                                response: Response<BasicResponse>
                            ) {
                                if (response.isSuccessful) {

//                                    내 수정된 정보 파싱. => 로그인한 사용자의 정보로 갱신.
                                    val basicResponse = response.body()!!

                                    GlobalData.loginUser = basicResponse.data.user

                                    Toast.makeText(mContext, "변경 완료", Toast.LENGTH_SHORT).show()
                                    setUserInfo()
                                }

                            }

                            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                            }
                        })
                    }
                })
                .setNegativeButton("취소", null)
            myAlert.show()
        }

        binding.logoutLayout.setOnClickListener {

            val alert = AlertDialog.Builder(mContext)
                .setMessage("정말 로그아웃 하시겠습니까?")
                .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->

                    ContextUtil.setToken(mContext, "")
                    GlobalData.loginUser = null

                    val myIntent = Intent(mContext, LoginActivity::class.java)
                    myIntent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(myIntent)
                })
                .setNegativeButton("취소", null)
            alert.show()
        }

        binding.myPlaceLayout.setOnClickListener {

            startActivity(Intent(mContext, ViewMyPlaceListActivity::class.java))
        }
    }

    override fun setValues() {

        when (GlobalData.loginUser!!.provider) {
            "facebook" -> {
                binding.providerTxt.setImageResource(R.drawable.facebook)
                binding.pwEditBtn.visibility = View.GONE
            }
            "kakao" -> {
                binding.providerTxt.setImageResource(R.drawable.kakao_icon)
                binding.pwEditBtn.visibility = View.GONE
            }
        }
        setUserInfo()
    }

    private fun setUserInfo() {
        val loginUser = GlobalData.loginUser!!

        binding.nicknameTxt.text = loginUser.nickName

        Glide.with(mContext)
            .load(GlobalData.loginUser!!.profileImg)
            .into(binding.profileImg)

        if (loginUser.readyMinute >= 60) {
            val hour = loginUser.readyMinute / 60
            val minute = loginUser.readyMinute % 60

            binding.readyTimeTxt.text = "${hour}시간 ${minute}분"
        } else {

            binding.readyTimeTxt.text = "${loginUser.readyMinute}분"
        }
    }

    override fun onResume() {
        super.onResume()
        setUserInfo()
    }

}