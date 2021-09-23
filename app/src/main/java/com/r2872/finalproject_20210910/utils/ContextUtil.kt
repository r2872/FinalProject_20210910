package com.r2872.finalproject_20210910.utils

import android.content.Context

class ContextUtil {

    companion object {

        private val prefName = "FinalProjectPref"
        private val AUTO_LOGIN = "AUTO_LOGIN"
        private val USER_ID = "USER_ID"
        private val USER_PW = "USER_PW"
        private val TOKEN = "TOKEN"
        private val MY_READY_MINUTE = "MY_READY_MINUTE"

        fun setAutoLogIn(context: Context, isAutoLogIn: Boolean) {
            val pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)

            pref.edit().putBoolean(AUTO_LOGIN, isAutoLogIn).apply()
        }

        fun getAutoLogIn(context: Context): Boolean {

            val pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
            return pref.getBoolean(AUTO_LOGIN, false)
        }

        fun setToken(context: Context, token: String) {
            val pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)

            pref.edit().putString(TOKEN, token).apply()
        }

        fun getToken(context: Context): String {
            val pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
            return pref.getString(TOKEN, "")!!
        }

        fun setMyReadyMinute(context: Context, minute: Int) {
            val pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)

            pref.edit().putInt(MY_READY_MINUTE, minute).apply()
        }

        fun getMyReadyMinute(context: Context): Int {
            val pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
            return pref.getInt(MY_READY_MINUTE, 0)
        }

        fun setUserId(context: Context, inputId: String) {
            val pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)

            pref.edit().putString(USER_ID, inputId).apply()
        }

        fun getUserId(context: Context): String {
            val pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)

            return pref.getString(USER_ID, "")!!
        }

        fun setUserPw(context: Context, inputPw: String) {
            val pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)

            pref.edit().putString(USER_PW, inputPw).apply()
        }

        fun getUserPw(context: Context): String {
            val pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)

            return pref.getString(USER_PW, "")!!
        }
    }
}