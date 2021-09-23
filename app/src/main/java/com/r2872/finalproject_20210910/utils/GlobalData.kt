package com.r2872.finalproject_20210910.utils

import android.content.Context
import com.r2872.finalproject_20210910.datas.UserData

class GlobalData {

    companion object {

        var context: Context? = null

        var loginUser: UserData? = null
            set(value) {
//            부가적으로 할 행동
                value?.let {
//                    UserData 가 null 이 아님. => 로그인 등의 이유로 사용자 기록.
//                    내 준비 시간을 => ContextUtil 에 기록해두자.
                    ContextUtil.setMyReadyMinute(context!!, it.readyMinute)
                }
                if (value == null) {
//                    로그아웃 등의 이유로 데이터 파기.
//                    내 준비시간을 0 (기본값) 으로 되돌리기.
                    ContextUtil.setMyReadyMinute(context!!, 0)
                }
//            실제 변수에 입력값 대입
                field = value
            }
    }
}