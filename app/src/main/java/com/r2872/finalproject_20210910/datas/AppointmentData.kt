package com.r2872.finalproject_20210910.datas

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AppointmentData(
    var id: Int,
    @SerializedName("user_id")
    var userId: Int,
    var title: String,
    var datetime: String, // 일단 String -> 파싱 기능 수정 => Date 형태로 받자. (Calendar 와 엮어서 사용)
    var place: String,
    var latitude: Double,
    var longitude: Double,
    @SerializedName("created_at")
    var createdAt: String,
    var user: UserData
): Serializable
