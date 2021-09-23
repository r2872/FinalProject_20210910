package com.r2872.finalproject_20210910.datas

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class UserData(
    var id: Int,
    var provider: String,
    var email: String,
    @SerializedName("nick_name")
    var nickName: String,
    @SerializedName("ready_minute")
    var readyMinute: Int,
    @SerializedName("profile_img")
    var profileImg: String,
    @SerializedName("arrived_at")
    var arrivedAt: Date?
) : Serializable
