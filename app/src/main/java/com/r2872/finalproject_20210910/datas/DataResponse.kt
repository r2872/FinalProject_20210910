package com.r2872.finalproject_20210910.datas

data class DataResponse(
//    로그인 성공 시 파싱용 변수.
    var user: UserData,
    var token: String,
//    이 밑으로는 약속 목록 파싱용 변수.
    var appoinments: List<AppointmentData>
)
