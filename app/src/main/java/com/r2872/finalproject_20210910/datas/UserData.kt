package com.r2872.finalproject_20210910.datas

import com.google.gson.annotations.SerializedName

data class UserData(
    var id: Int,
    var provider: String,
    var email: String,
    @SerializedName("nick_name")
    var nickName: String
)
