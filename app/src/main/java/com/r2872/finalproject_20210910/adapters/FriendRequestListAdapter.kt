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
import com.r2872.finalproject_20210910.R
import com.r2872.finalproject_20210910.UserInfoActivity
import com.r2872.finalproject_20210910.VIewMyFriendsListActivity
import com.r2872.finalproject_20210910.datas.BasicResponse
import com.r2872.finalproject_20210910.datas.UserData
import com.r2872.finalproject_20210910.fragments.FriendRequestFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendRequestListAdapter(
    val mContext: Context,
    private val mList: List<UserData>
) : RecyclerView.Adapter<FriendRequestListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val friendProfileImg = view.findViewById<ImageView>(R.id.friendProfile_Img)
        private val friendNicknameTxt = view.findViewById<TextView>(R.id.friendNickname_Txt)
        private val socialLoginImg = view.findViewById<ImageView>(R.id.socialLogin_Img)
        private val addFriendBtn = view.findViewById<ImageView>(R.id.addFriend_Btn)

        fun bind(context: Context, data: UserData) {
            friendNicknameTxt.text = data.nickName
            addFriendBtn.setImageResource(R.drawable.ic_baseline_check_24)
            addFriendBtn.visibility = View.VISIBLE
            addFriendBtn.setOnClickListener {

                val alert = AlertDialog.Builder(context)
                    .setTitle("${data.nickName}님의 친구요청을 수락하시겠습니까?")
                    .setPositiveButton(
                        "확인", DialogInterface.OnClickListener { dialogInterface, i ->
                            putRequestAddFriend(context, data, "수락")
                        }
                    )
                    .setNegativeButton(
                        "취소", DialogInterface.OnClickListener { dialogInterface, i ->
                            putRequestAddFriend(context, data, "거절")
                        }
                    )
                alert.show()

            }

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
                else -> socialLoginImg.setImageResource(R.drawable.ic_baseline_person_24)
            }

        }

        private fun putRequestAddFriend(context: Context, data: UserData, type: String) {
            (context as VIewMyFriendsListActivity).apiService.putRequestAddFriend(data.id, type)
                .enqueue(object : Callback<BasicResponse> {
                    override fun onResponse(
                        call: Call<BasicResponse>,
                        response: Response<BasicResponse>
                    ) {
                        if (response.isSuccessful) {
                            val basicResponse = response.body()

                            Toast.makeText(
                                context,
                                "${data.nickName}님의 친구 요청을 ${type}하셨습니다.",
                                Toast.LENGTH_SHORT
                            ).show()

//                            프래그먼트의 요청목록 새로 가져오기 함수를 실행?
//                            어댑터 -> 액비티티 기능 : context 변수 활용.

//                            어댑터 -> 액티비티 -> ViewPager 어댑터 -> 1번쨰 Fragment -> 요청목록 Frag 로 변신 -> 기능활용
                            (context.friendsViewPagerAdapter.getItem(1) as FriendRequestFragment).getRequestedFriendFromUser()
                        }
                    }

                    override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                    }
                })
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(mContext).inflate(R.layout.friend_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(mContext, mList[position])
    }

    override fun getItemCount() = mList.size
}