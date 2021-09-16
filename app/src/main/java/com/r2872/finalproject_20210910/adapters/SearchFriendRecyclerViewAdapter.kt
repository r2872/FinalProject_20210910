package com.r2872.finalproject_20210910.adapters

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.r2872.finalproject_20210910.AddFriendActivity
import com.r2872.finalproject_20210910.R
import com.r2872.finalproject_20210910.datas.BasicResponse
import com.r2872.finalproject_20210910.datas.UserData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFriendRecyclerViewAdapter(
    val mContext: Context,
    private val mList: List<UserData>
) : RecyclerView.Adapter<SearchFriendRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.friend_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mContext, mList[position])
    }

    override fun getItemCount() = mList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val friendProfileImg = view.findViewById<ImageView>(R.id.friendProfile_Img)
        private val friendNicknameTxt = view.findViewById<TextView>(R.id.friendNickname_Txt)
        private val socialLoginImg = view.findViewById<ImageView>(R.id.socialLogin_Img)
        private val addFriendBtn = view.findViewById<ImageView>(R.id.addFriend_Btn)

        fun bind(context: Context, data: UserData) {
            friendNicknameTxt.text = data.nickName
            addFriendBtn.visibility = View.VISIBLE

            Glide.with(context)
                .load(data.profileImg)
                .into(friendProfileImg)

            when (data.provider) {
                "facebook" -> {
                    socialLoginImg.setImageResource(R.drawable.facebook)
                }
                "kakao" -> {
                    socialLoginImg.setImageResource(R.drawable.kakao_icon)
                }
            }
//            친구추가 버튼 눌림 이벤트 처리
            addFriendBtn.setOnClickListener {

//                ~~ 님에게 친구 요청을 보내시겠습니까?
                val alert = AlertDialog.Builder(context)
                    .setTitle("${data.nickName}님에게 친구 요청을 보내시겠습니까?")
                    .setPositiveButton("확인", DialogInterface.OnClickListener { dialogInterface, i ->

                        (context as AddFriendActivity).apiService.postRequestAddFriend(data.id)
                            .enqueue(object : Callback<BasicResponse> {
                                override fun onResponse(
                                    call: Call<BasicResponse>,
                                    response: Response<BasicResponse>
                                ) {
                                    if (response.isSuccessful) {
                                        val basicResponse = response.body()!!

                                        Toast.makeText(
                                            context,
                                            "${data.nickName}님에게 친구 추가 요청을 보냈습니다.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                                }
                            })
                    })
                    .setNegativeButton("취소", null)
                alert.show()
            }

        }
    }
}