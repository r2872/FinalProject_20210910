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
    var place: String,
    var latitude: Double,
    var longitude: Double,
    @SerializedName("created_at")
    var createdAt: Date,
    var user: UserData
) : Serializable {

    //    함수 추가 => 현재 시간 ~ 약속시간 남은 시간에 따라 다른 문구를 리턴.
    fun getFormattedDateTime(): String {

//        현재 시간
        val currentTime = Calendar.getInstance()

//        약속시간 - 현재시간 : 몇시간?
        val diff = this.datetime.time - currentTime.timeInMillis

//        몇시간 차이인가?
        val diffHour = diff / 1000 / 60 / 60

        if (diffHour < 1) {

            //        몇분 남음
            val diffMinute = diff / 1000 / 60
            return "${diffMinute}분 남음"
        } else if (diffHour < 5) {

            return "${diffHour}시간 남음"
        } else {
            val dateTimeSdf = SimpleDateFormat("M/d a h:mm")
            return dateTimeSdf.format(datetime)
        }
    }
}
