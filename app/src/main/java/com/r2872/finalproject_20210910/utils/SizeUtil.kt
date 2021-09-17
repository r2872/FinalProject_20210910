package com.r2872.finalproject_20210910.utils

import android.content.Context
import android.util.TypedValue

class SizeUtil {

    companion object {

        fun dbToPx(context: Context, dp: Float): Float {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.resources.displayMetrics
            )
        }
    }
}