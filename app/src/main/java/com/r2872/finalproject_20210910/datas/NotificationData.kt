package com.r2872.finalproject_20210910.datas

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class NotificationData(
    var id: Int,
    @SerializedName("receive_user_id")
    var receiveUserId: Int,
    @SerializedName("act_user_id")
    var actUserId: Int,
    var title: String,
    var type: String,
    var message: String,
    @SerializedName("is_read")
    var isRead: Boolean
) : Serializable
