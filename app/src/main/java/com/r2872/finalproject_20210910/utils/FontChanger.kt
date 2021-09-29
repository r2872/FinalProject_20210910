package com.r2872.finalproject_20210910.utils

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class FontChanger {

    companion object {

        //        폰트를 모든 텍스트뷰를 찾아서 적용해주는 함수
        fun setGlobalFont(context: Context, view: View) {

//            뷰 하나를 받아서 -> 모든 하위 뷰를 검색
//            그릇 (layout) 으로써 하위 뷰들을 들고있나?
            if (view is ViewGroup) {

//                담겨있는 자녀들을 for 문으로 뽑아보자
                for (i in 0 until view.childCount) {

                    val childView = view.getChildAt(i)

//                    뽑이낸 하위 view 가 TextView 라면 -> 폰트 적용
                    if (childView is TextView) {

//                        asset 에 추가한 폰트파일 적용
                        childView.typeface = Typeface.createFromAsset(context.assets, "font.ttf")
                    }

//                    자녀뷰에도 또 하위 View 가 있는지 확인해서 -> 폰트 적용
                    setGlobalFont(context, childView) // 함수 안에서 자신을 다시 호출 : 재귀 함수
                }
            }
        }
    }
}