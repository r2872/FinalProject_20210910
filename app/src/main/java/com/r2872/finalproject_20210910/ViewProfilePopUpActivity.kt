package com.r2872.finalproject_20210910

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.r2872.finalproject_20210910.databinding.ActivityViewProfilePopUpBinding
import com.r2872.finalproject_20210910.datas.BasicResponse
import com.r2872.finalproject_20210910.utils.GlobalData
import com.r2872.finalproject_20210910.utils.Request
import com.r2872.finalproject_20210910.utils.URIPathHelper
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ViewProfilePopUpActivity : BaseActivity() {

    private lateinit var binding: ActivityViewProfilePopUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_profile_pop_up)

        setValues()
        setupEvents()
    }

    override fun setupEvents() {

        binding.changeBtn.setOnClickListener {

            //            갤러리를 개발자가 이용: 허락 받아야 볼 수 있다. => 권한 세팅 필요.
//            TedPermission 라이브러리
            val permissionListener = object : PermissionListener {
                override fun onPermissionGranted() {

//                    권한이 OK 일때.
//                    갤러리로 사진을 가지러 이동. (추가 작업)
                    val myIntent = Intent()
                    myIntent.action = Intent.ACTION_PICK
                    myIntent.type = "image/*"
                    myIntent.type = android.provider.MediaStore.Images.Media.CONTENT_TYPE
                    startActivityForResult(myIntent, Request.READ_STORAGE_PERMISSION_REQUEST_CODE)
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

        binding.deleteBtn.setOnClickListener {

            val alertDialog = AlertDialog.Builder(mContext)
                .setTitle("프로필 삭제")
                .setPositiveButton("확인", DialogInterface.OnClickListener { dialogInterface, i ->
                    apiService.deleteRequestUserImage().enqueue(object : Callback<BasicResponse> {
                        override fun onResponse(
                            call: Call<BasicResponse>,
                            response: Response<BasicResponse>
                        ) {
                            if (response.isSuccessful) {
                                val basicResponse = response.body()!!

                                GlobalData.loginUser = basicResponse.data.user
                                Toast.makeText(mContext, "삭제하였습니다.", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }

                        override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                        }
                    })
                })
                .setNegativeButton("취소", null)
                .show()
        }
    }

    override fun setValues() {

        Glide.with(mContext)
            .load(GlobalData.loginUser!!.profileImg)
            .into(binding.profileImg)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

//        갤러리에서 사진 가져온 경우?
        if (requestCode == Request.READ_STORAGE_PERMISSION_REQUEST_CODE) {

//            실제로 이미지를 선택한건지?
            if (resultCode == RESULT_OK) {

//              어떤 사진을 골랐는지? 파악해보자
//                임시: 고른 사진을 profileImg 에 바로 적용만. (서버 전송 X)

//              data? => 이전 화면이 넘겨준 Intent
//              data?.data => 선택한 사진이 들어있는 경로 정보 (Uri)
                val dataUri = data?.data

//                Uri -> 이미지뷰의 사진으로. (Glide)
//                Glide.with(mContext).load(dataUri).into(binding.profileImg)

//                API 서버에 사진을 전송. => PUT - /user/image 로 API 활용.
//                파일을 같이 첨부해야한다. => Multipart 형식의 데이터 첨부 활용. (기존 FormData 와는 다르다)

//                Uri -> File 형태로 변환. -> 그 파일의 실제 경로? 얻어낼 필요가 있다.

                val file = File(URIPathHelper().getPath(mContext, dataUri!!))

//                파일을 Retrofit 에 첨부할 수 있는 => RequestBody => MultipartBody 형태로 변환.
                val fileRequestBody = RequestBody.create(MediaType.get("image/*"), file)
                val body = MultipartBody.Part.createFormData(
                    "profile_image",
                    "myFile.jpg",
                    fileRequestBody
                )
                apiService.putRequestProfileImage(body).enqueue(object : Callback<BasicResponse> {
                    override fun onResponse(
                        call: Call<BasicResponse>,
                        response: Response<BasicResponse>
                    ) {
                        if (response.isSuccessful) {

                            val basicResponse = response.body()!!

                            GlobalData.loginUser = basicResponse.data.user

                            Toast.makeText(mContext, "변경 완료", Toast.LENGTH_SHORT).show()
                            finish()
                        }

                    }

                    override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                    }
                })
            }
        }
    }
}