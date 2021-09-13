package com.r2872.finalproject_20210910.web

import com.r2872.finalproject_20210910.datas.AppointmentData
import com.r2872.finalproject_20210910.datas.BasicResponse
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

    @FormUrlEncoded
    @PATCH("/user")
    fun patchRequestEditUser(
        @Field("current_password") current_password: String,
        @Field("new_password") new_password: String,
        @Field("nick_name") nick_name: String
    ): Call<BasicResponse>

    @FormUrlEncoded
    @POST("/appointment")
    fun postRequestAppointment(
        @Field("title") title: String,
        @Field("datetime") datetime: String,
        @Field("place") place: String,
        @Field("latitude") latitude: Double,
        @Field("longitude") longitude: Double
    ): Call<BasicResponse>

    @GET("/appointment")
    fun getRequestAppointmentList(): Call<BasicResponse>

    @GET("/user")
    fun getRequestMyInfo(): Call<BasicResponse>

}