package com.r2872.finalproject_20210910.datas

data class SubPathData(
    var trafficType: Int,
    var sectionTime: Int,
) {
    var pathName: String = ""
    var stationCount: Int = 0
    var startName: String = ""
    var endName: String = ""

    constructor(
        trafficType: Int,
        sectionTime: Int,
        pathName: String,
        stationCount: Int,
        startName: String,
        endName: String
    ) : this(trafficType, sectionTime) {
        this.pathName = pathName
        this.stationCount = stationCount
        this.startName = startName
        this.endName = endName
    }

}
