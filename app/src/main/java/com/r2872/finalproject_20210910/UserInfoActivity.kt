package com.r2872.finalproject_20210910

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.r2872.finalproject_20210910.databinding.ActivityUserInfoBinding
import com.r2872.finalproject_20210910.datas.BasicResponse
import com.r2872.finalproject_20210910.utils.ContextUtil
import com.r2872.finalproject_20210910.utils.GlobalData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserInfoActivity : BaseActivity() {

    private lateinit var binding: ActivityUserInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_info)

        setValues()
        setupEvents()
    }

    override fun setupEvents() {

//        프로필사진 누르면 => 프사 변경의 의미로 활용. => 갤러리로 프사 선택하러 진입.
//        안드로이드가 제공하는 갤러리 화면 활용. Intent (4) 추가 항목
//        어떤사진? 결과를 얻기 위해 화면을 이동. Intent (3) 활용.
        binding.profileImg.setOnClickListener {

//            갤러리를 개발자가 이용: 허락 받아야 볼 수 있다. => 권한 세팅 필요.
//            TedPermission 라이브러리
            val permissionListener = object : PermissionListener {
                override fun onPermissionGranted() {

//                    권한이 OK 일때.
//                    갤러리로 사진을 가지러 이동. (추가 작업)
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {

//                    권한이 거절되었을떄. => 토스트로 안내만.
                    Toast.makeText(mContext, "권한이 거부되어 갤러리 접근이 불가능합니다.", Toast.LENGTH_SHORT).show()
                }
            }

//            실제로 권한 체크.
//            1) Manifest 에 권한 등록
//            2) 실제로 라이브러리로 질문.
            TedPermission.create()
                .setPermissionListener(permissionListener)
                .setDeniedMessage("이 기능을 사용하기 위해서는 권한 승인이 필요합니다.")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .check()
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

        binding.signOutBtn.setOnClickListener {

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
                Glide.with(mContext)
                    .load(R.drawable.facebook)
                    .into(binding.providerTxt)
                binding.pwEditBtn.visibility = View.GONE
            }
            "kakao" -> {
                Glide.with(mContext)
                    .load(R.drawable.kakao_icon)
                    .into(binding.providerTxt)
                binding.pwEditBtn.visibility = View.GONE
            }
        }

        titleTxt.text = "내 정보"
        setUserInfo()

        Glide.with(mContext)
            .load(GlobalData.loginUser!!.profileImg)
            .into(binding.profileImg)
    }

    private fun setUserInfo() {
        val loginUser = GlobalData.loginUser!!

        binding.nicknameTxt.text = loginUser.nickName

        if (loginUser.readyMinute >= 60) {
            val hour = loginUser.readyMinute / 60
            val minute = loginUser.readyMinute % 60

            binding.readyTimeTxt.text = "${hour}시간 ${minute}분"
        } else {

            binding.readyTimeTxt.text = "${loginUser.readyMinute}분"
        }
    }
}