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
import com.r2872.finalproject_20210910.VIewMyFriendsListActivity
import com.r2872.finalproject_20210910.datas.BasicResponse
import com.r2872.finalproject_20210910.datas.UserData
import com.r2872.finalproject_20210910.fragments.MyFriendListFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendListAdapter(
    val mContext: Context,
    private val mList: List<UserData>
) : RecyclerView.Adapter<FriendListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : BaseViewHolder(mContext, view) {

        private val friendProfileImg = view.findViewById<ImageView>(R.id.friendProfile_Img)
        private val friendNicknameTxt = view.findViewById<TextView>(R.id.friendNickname_Txt)
        private val socialLoginImg = view.findViewById<ImageView>(R.id.socialLogin_Img)
        private val addFriendBtn = view.findViewById<ImageView>(R.id.addFriend_Btn)

        fun bind(context: Context, data: UserData) {
            friendNicknameTxt.text = data.nickName

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
                "naver" -> {
                    socialLoginImg.setImageResource(R.drawable.naver_icon)
                }
                else -> socialLoginImg.setImageResource(R.drawable.ic_baseline_person_24)
            }

            addFriendBtn.setImageResource(R.drawable.ic_baseline_edit_24)
            addFriendBtn.setOnClickListener {

                val alert = AlertDialog.Builder(context)
                    .setTitle(data.nickName)
                    .setPositiveButton(
                        "삭제", DialogInterface.OnClickListener { dialogInterface, i ->
                            val alert = AlertDialog.Builder(context)
                                .setTitle("정말로 삭제하시겠습니까?")
                                .setPositiveButton(
                                    "확인",
                                    DialogInterface.OnClickListener { dialog, which ->
                                        (context as VIewMyFriendsListActivity).apiService.deleteRequestFriend(
                                            data.id
                                        ).enqueue(object : Callback<BasicResponse> {
                                            override fun onResponse(
                                                call: Call<BasicResponse>,
                                                response: Response<BasicResponse>
                                            ) {
                                                if (response.isSuccessful) {
                                                    Toast.makeText(
                                                        context,
                                                        "삭제하였습니다.",
                                                        Toast.LENGTH_SHORT
                                                    )
                                                        .show()
                                                }
                                                (context.friendsViewPagerAdapter.getItem(0) as MyFriendListFragment).getFriendListFromServer()
                                            }

                                            override fun onFailure(
                                                call: Call<BasicResponse>,
                                                t: Throwable
                                            ) {

                                            }
                                        })
                                    })
                                .setNegativeButton("취소", null)
                                .show()
                        }
                    )
                    .setNegativeButton("취소", null)
                alert.show()

            }

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