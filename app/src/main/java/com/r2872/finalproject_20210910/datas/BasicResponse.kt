package com.r2872.finalproject_20210910.datas

// 서버가 주는 기본 형태의 응답을 담는 클래스. (파싱 결과로 활용)

data class BasicResponse(var code: Int, var message:String) {
}