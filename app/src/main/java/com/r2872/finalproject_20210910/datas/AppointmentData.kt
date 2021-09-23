package com.r2872.finalproject_20210910.datas

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

data class AppointmentData(
    var id: Int,
    @SerializedName("user_id")
    var userId: Int,
    var title: String,
    var datetime: Date, // 일단 String -> 파싱 기능 수정 => Date 형태로 받자. (Calendar 와 엮어서 사용)
    @SerializedName("start_place")
    var startPlace: String,
    @SerializedName("start_latitude")
    var startLatitude: Double,
    @SerializedName("start_longitude")
    var startLongitude: Double,
    var place: String,
    var latitude: Double,
    var longitude: Double,
    @SerializedName("created_at")
    var createdAt: Date,
    var user: UserData,
    @SerializedName("invited_friends")
    var invitedFriends: List<UserData>
) : Serializable {


    //    함수 추가 => 현재 시간 ~ 약속시간 남은 시간에 따라 다른 문구를 리턴.
    fun getFormattedDateTime(): String {

//        현재 시간
        val currentTime = Calendar.getInstance()

//        약속시간 (utc) => 폰 설정 타임존 변환 - 현재시간 (폰 설정 타임존) : 몇시간?

        val dateTimeToTimeZone = this.datetime.time + currentTime.timeZone.rawOffset

        val diff = dateTimeToTimeZone - currentTime.timeInMillis

//        몇시간 차이인가?
        val diffHour = diff / 1000 / 60 / 60
        val diffMinute = diff / 1000 / 60

        if (diffHour < 1) {

            return "${diffMinute}분 남음"
        } else if (diffHour < 5) {

            val hour = diffMinute / 60
            val minute = diffMinute % 60
            return "${hour}시간 ${minute}분 남음"
        } else {
            val dateTimeSdf = SimpleDateFormat("M/d a h:mm")
            return dateTimeSdf.format(dateTimeToTimeZone)
        }
    }
}
