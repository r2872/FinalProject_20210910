package com.r2872.finalproject_20210910.web

import com.r2872.finalproject_20210910.datas.BasicResponse
import com.r2872.finalproject_20210910.utils.ContextUtil
import retrofit2.Call
import retrofit2.http.*

interface ServerAPIService {

    @FormUrlEncoded
    @PUT("/user")
    fun putRequestSignUp(
        @Field("email") email: String,
        @Field("password") pw: String,
        @Field("nick_name") nickname: String
    ): Call<BasicResponse>

    @FormUrlEncoded
    @POST("/user")
    fun postRequestSignIn(
        @Field("email") email: String,
        @Field("password") pw: String
    ): Call<BasicResponse>

    @FormUrlEncoded
    @POST("/user/social")
    fun postRequestSocialSignIn(
        @Field("provider") provider: String,
        @Field("uid") uid: String,
        @Field("nick_name") nick_name: String
    ): Call<BasicResponse>
//
//    @FormUrlEncoded
//    @PATCH("/user")
//    @Header(ContextUtil.getToken())
//    fun postRequestEditUser(
//        @Field("current_password") current_password: String,
//        @Field("new_password") new_password: String,
//        @Field("nick_name") nick_name: String
//    ): Call<BasicResponse>
}