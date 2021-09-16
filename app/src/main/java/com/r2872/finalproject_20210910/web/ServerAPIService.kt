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
        @Field("longitude") longitude: Double
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

    //    프로필 사진 첨부 => Multipart 활용
//    Multipart 방식의 통신에서는 Field 를 담지 않고, MultipartBody.Part 양식으로 (모든) 데이터 첨부.
//    사진 외의 데이터도 첨부할때는, 나머지 항목들은 RequestBody 형태로 첨부함.
    @Multipart
    @PUT("/user/image")
    fun putRequestProfileImage(@Part profileImg: MultipartBody.Part): Call<BasicResponse>

    //    친구목록 불러오기
//    쿼리 파라미터를 넣어서 불러오기.
    @GET("/user/friend")
    fun getRequestFriendList(
        @Query("type") type: String
    ): Call<BasicResponse>

    //    닉네임으로 사용자 검색하기
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
}