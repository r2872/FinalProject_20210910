package com.r2872.finalproject_20210910.web

import android.content.Context
import android.util.Log
import com.r2872.finalproject_20210910.utils.ContextUtil
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ServerAPI {

    companion object {

        //        서버 주소
        private val HOST_URL = "http://3.36.146.152"

        //        Retrofit 형태의 변수가 => OKHttpClient 처럼 실제 호출 담당.
//        레트로핏 객체는 -> 하나만 만들어두고 -> 여러 화면에서 고유해서 사용.
//        객체를 하나로 유지하자. => SingleTon 패턴 사용.
        private var retrofit: Retrofit? = null

        fun getRetrofit(context: Context): Retrofit {

            if (retrofit == null) {

//                API 요청이 발생하면 => 가로채서 => Header 를 추가해주자. => API 요청을 이어가게.
//                자동으로 헤더를 달아주는 효과 발생.
                val interceptor = Interceptor {
                    with(it) {
                        val newRequest =
                            request().newBuilder()
                                .addHeader("X-Http-Token", ContextUtil.getToken(context))
                                .build()

                        proceed(newRequest)
                    }
                }

//                retrofit : okhttp 의 확장판. => retrofit 도 OkHttpClient 형태의 클라이언트 활용.
//                이 클라이언트에게 -> 만들어둔 언터셉터를 달아주자.
//                클라이언트를 가공해주자.
                val myClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

                retrofit = Retrofit.Builder()
                    .baseUrl(HOST_URL)
                    .client(myClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }

            return retrofit!!
        }
    }
}