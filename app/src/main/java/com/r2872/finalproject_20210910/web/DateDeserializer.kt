package com.r2872.finalproject_20210910.web

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class DateDeserializer : JsonDeserializer<Date> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Date {

        val dateStr = json?.asString

//        서버가 주는 양식으로 Date 형태로 파싱
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

//        서버가 준 String -> Date 형식으로 변환 => 서버 기본값: UTC 시간대 설정.
        val resultDate = sdf.parse(dateStr)

//        UTC -> 내 핸드폰의 시차 값 만큼 더해서 리턴.
        val now = Calendar.getInstance()

//        결과로 나갈 시간값의 밀리초 단위로 => 시차값 밀리초 단위 덧셈
        resultDate.time += now.timeZone.rawOffset

//        시차만큼 더해진 시간값 파싱 결과로 선택.
        return resultDate
    }
}