package com.r2872.finalproject_20210910.utils

import com.r2872.finalproject_20210910.datas.UserData

class GlobalData {

    companion object {

//        로그인 한 사용자를 담아둘 변수

//        앱이 처음 켜졌을때는 로그인 한 사용자가 없다.
        var loginUser : UserData? = null
    }
}