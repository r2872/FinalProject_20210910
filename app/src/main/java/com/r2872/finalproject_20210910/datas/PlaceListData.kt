package com.r2872.finalproject_20210910.datas

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PlaceListData(
    var id: Int,
    @SerializedName("user_id")
    var userId: Int,
    var name: String,
    var latitude: Double,
    var longitude: Double,
    @SerializedName("is_primary")
    var isPrimary: Boolean
) : Serializable