package com.r2872.finalproject_20210910.web

import com.r2872.finalproject_20210910.datas.BasicResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ServerAPIService {

    @FormUrlEncoded
    @PUT("/user")
    fun putRequestSignUp(
        @Field("email") email: String,
        @Field("password") pw: String,
        @Field("nick_name") nickname: String,
        @Field("app_maker") maker: String
    ): Call<BasicResponse>

    @FormUrlEncoded
    @POST("/user")
    fun postRequestSignIn(
        @Field("email") email: String,
        @Field("password") pw: String
    ): Call<BasicResponse>

    @FormUrlEncoded
    @POST("/user/password")
    fun postRequestUserPw(
        @Field("email") email: String,
        @Field("nick_name") nickname: String
    ): Call<BasicResponse>

    @FormUrlEncoded
    @POST("/user/social")
    fun postRequestSocialSignIn(
        @Field("provider") provider: String,
        @Field("uid") uid: String,
        @Field("nick_name") nick_name: String,
        @Field("app_maker") maker: String
    ): Call<BasicResponse>

    @FormUrlEncoded
    @PATCH("/user")
    fun patchRequestEditUser(
        @Field("field") field: String,
        @Field("value") value: String,
    ): Call<BasicResponse>

    @FormUrlEncoded
    @POST("/appointment")
    fun postRequestAppointment(
        @Field("title") title: String,
        @Field("datetime") datetime: String,
        @Field("start_place") startPlace: String,
        @Field("start_latitude") startLatitude: Double,
        @Field("start_longitude") startLongitude: Double,
        @Field("place") place: String,
        @Field("latitude") latitude: Double,
        @Field("longitude") longitude: Double,
        @Field("friend_list") friendList: String
    ): Call<BasicResponse>

    @FormUrlEncoded
    @PUT("/appointment")
    fun putRequestAppointment(
        @Field("appointment_id") appointmentId: Int,
        @Field("title") title: String,
        @Field("datetime") datetime: String,
        @Field("start_place") start_place: String,
        @Field("start_latitude") start_latitude: Double,
        @Field("start_longitude") start_longitude: Double,
        @Field("place") place: String,
        @Field("latitude") latitude: Double,
        @Field("longitude") longitude: Double,
        @Field("friend_list") friend_list: String
    ): Call<BasicResponse>

    @GET("/appointment")
    fun getRequestAppointmentList(): Call<BasicResponse>

    @GET("/user")
    fun getRequestMyInfo(): Call<BasicResponse>

    @FormUrlEncoded
    @POST("/user/place")
    fun postRequestMyPlaceList(
        @Field("name") name: String,
        @Field("latitude") latitude: Double,
        @Field("longitude") longitude: Double,
        @Field("is_primary") is_primary: Boolean
    ): Call<BasicResponse>

    @GET("/user/place")
    fun getRequestMyAppointmentList(): Call<BasicResponse>

    //    ????????? ?????? ?????? => Multipart ??????
//    Multipart ????????? ??????????????? Field ??? ?????? ??????, MultipartBody.Part ???????????? (??????) ????????? ??????.
//    ?????? ?????? ???????????? ???????????????, ????????? ???????????? RequestBody ????????? ?????????.
    @Multipart
    @PUT("/user/image")
    fun putRequestProfileImage(@Part profileImg: MultipartBody.Part): Call<BasicResponse>

    //    ???????????? ????????????
//    ?????? ??????????????? ????????? ????????????.
    @GET("/user/friend")
    fun getRequestFriendList(
        @Query("type") type: String
    ): Call<BasicResponse>

    //    ??????????????? ????????? ????????????
    @GET("/search/user")
    fun getRequestUserSearch(
        @Query("nickname") keyword: String
    ): Call<BasicResponse>

    @FormUrlEncoded
    @POST("/user/friend")
    fun postRequestAddFriend(
        @Field("user_id") userId: Int
    ): Call<BasicResponse>

    @FormUrlEncoded
    @PUT("/user/friend")
    fun putRequestAddFriend(
        @Field("user_id") userId: Int,
        @Field("type") type: String
    ): Call<BasicResponse>

    @FormUrlEncoded
    @POST("/appointment/arrival")
    fun postRequestArrival(
        @Field("appointment_id") appointmentId: Int,
        @Field("latitude") latitude: Double,
        @Field("longitude") longitude: Double
    ): Call<BasicResponse>

    @GET("/appointment/{appointment_id}")
    fun getRequestAppointmentDetail(
        @Path("appointment_id") id: Int
    ): Call<BasicResponse>

    @DELETE("/appointment")
    fun deleteRequestAppointment(
        @Query("appointment_id") appointmentId: Int
    ): Call<BasicResponse>

    @GET("/user/check")
    fun getRequestUserCheck(
        @Query("type") type: String,
        @Query("value") value: String
    ): Call<BasicResponse>

    @FormUrlEncoded
    @PATCH("user/password")
    fun patchRequestUserPassword(
        @Field("current_password") currentPW: String,
        @Field("new_password") newPW: String
    ): Call<BasicResponse>

    @DELETE("user/image")
    fun deleteRequestUserImage(): Call<BasicResponse>

    @GET("notifications")
    fun getRequestNotifications(
        @Query("need_all_notis") needAll: Boolean
    ): Call<BasicResponse>

    @FormUrlEncoded
    @POST("notifications")
    fun postRequestNotifications(
        @Field("noti_id") notiId: Int
    ): Call<BasicResponse>

    @DELETE("user/place")
    fun deleteRequestUserPlace(
        @Query("place_id") placeId: Int
    ): Call<BasicResponse>

    @DELETE("user/friend")
    fun deleteRequestFriend(
        @Query("user_id") userId: Int
    ): Call<BasicResponse>

    @DELETE("user")
    fun deleteRequestUser(
        @Query("text") text: String
    ): Call<BasicResponse>

    @FormUrlEncoded
    @PATCH("user/place")
    fun patchRequestUserPlace(
        @Field("place_id") placeId: Int,
    ): Call<BasicResponse>
}