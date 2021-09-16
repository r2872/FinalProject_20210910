package com.r2872.finalproject_20210910.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.r2872.finalproject_20210910.R
import com.r2872.finalproject_20210910.datas.UserData

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
            addFriendBtn.apply {
                setImageResource(R.drawable.ic_baseline_check_24)
                visibility = View.VISIBLE
                setOnClickListener {

                    val alert = AlertDialog.Builder(context)
                        .setTitle("${data.nickName}님의 친구요청을 수락하시겠습니까?")
                        .setPositiveButton("확인", null)
                        .setNegativeButton("취소", null)
                    alert.show()
                }
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