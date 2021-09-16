package com.r2872.finalproject_20210910.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.r2872.finalproject_20210910.R
import com.r2872.finalproject_20210910.datas.UserData
import com.r2872.finalproject_20210910.utils.GlobalData

class FriendListAdapter(
    val mContext: Context,
    private val mList: List<UserData>
) : RecyclerView.Adapter<FriendListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val friendProfileImg = view.findViewById<ImageView>(R.id.friendProfile_Img)
        private val friendNicknameTxt = view.findViewById<TextView>(R.id.friendNickname_Txt)
        private val socialLoginImg = view.findViewById<ImageView>(R.id.socialLogin_Img)

        fun bind(context: Context, data: UserData) {
            friendNicknameTxt.text = data.nickName

            Glide.with(mContext)
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