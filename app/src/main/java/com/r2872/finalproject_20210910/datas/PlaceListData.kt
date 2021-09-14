package com.r2872.finalproject_20210910.datas

import java.io.Serializable

data class PlaceListData(
    var id: Int,
    var user_id: Int,
    var name: String,
    var latitude: Double,
    var longitude: Double,
    var is_primary: Boolean
) : Serializable