package com.r2872.finalproject_20210910

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.r2872.finalproject_20210910.databinding.ActivityUserInfoBinding
import com.r2872.finalproject_20210910.utils.GlobalData

class UserInfoActivity : BaseActivity() {

    private lateinit var binding: ActivityUserInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_info)

        setValues()
        setupEvents()
    }

    override fun setupEvents() {

        binding.readyTimeLayout.setOnClickListener {

            val customView =
                LayoutInflater.from(mContext).inflate(R.layout.my_custom_alert_edt, null)

            val myAlert = AlertDialog.Builder(mContext)
                .setTitle("나의 외출 준비시간")
                .setView(customView)
                .setPositiveButton("입력", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {

                        val readyTimeEdt = customView.findViewById<EditText>(R.id.readyTime_Edt)

                        if (readyTimeEdt.text.toString() == "") {
                            Toast.makeText(mContext, "값을 입력해주세요", Toast.LENGTH_SHORT).show()
                            return
                        }

                        binding.readyTimeTxt.text = readyTimeEdt.text
                    }
                })
                .setNegativeButton("취소", null).show()
        }
    }

    override fun setValues() {

        titleTxt.text = "내 정보"

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