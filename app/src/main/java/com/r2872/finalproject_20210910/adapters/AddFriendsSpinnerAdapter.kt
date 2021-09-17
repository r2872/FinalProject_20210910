package com.r2872.finalproject_20210910.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.r2872.finalproject_20210910.R
import com.r2872.finalproject_20210910.datas.UserData

class AddFriendsSpinnerAdapter(
    private val mContext: Context,
    resId: Int,
    private val mList: List<UserData>
) : ArrayAdapter<UserData>(mContext, resId, mList) {

    private val mInflater = LayoutInflater.from(mContext)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var row = convertView
        if (row == null) {
            row = mInflater.inflate(R.layout.friend_list_item, null)
        }
        row!!

        val data = mList[position]

        val friendNicknameTxt = row.findViewById<TextView>(R.id.friendNickname_Txt)
        val friendProfileImg = row.findViewById<ImageView>(R.id.friendProfile_Img)
        val socialLoginImg = row.findViewById<ImageView>(R.id.socialLogin_Img)
        val addFriendBtn = row.findViewById<ImageView>(R.id.addFriend_Btn)
        addFriendBtn.visibility = View.GONE
        socialLoginImg.visibility = View.GONE

        friendNicknameTxt.text = data.nickName

        Glide.with(mContext)
            .load(data.profileImg)
            .into(friendProfileImg)

        return row
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }
}