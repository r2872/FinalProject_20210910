package com.r2872.finalproject_20210910.datas

data class DataResponse(
    var user: UserData,
    var token: String,
    var appoinments: List<AppointmentData>
)
