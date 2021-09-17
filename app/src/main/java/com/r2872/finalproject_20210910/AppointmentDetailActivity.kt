package com.r2872.finalproject_20210910

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.odsay.odsayandroidsdk.API
import com.odsay.odsayandroidsdk.ODsayData
import com.odsay.odsayandroidsdk.ODsayService
import com.odsay.odsayandroidsdk.OnResultCallbackListener
import com.r2872.finalproject_20210910.databinding.ActivityAppointmentDetailBinding
import com.r2872.finalproject_20210910.datas.AppointmentData
import com.r2872.finalproject_20210910.datas.UserData
import com.r2872.finalproject_20210910.utils.Request
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat

class AppointmentDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityAppointmentDetailBinding
    private val mInvitedFriendsList = ArrayList<UserData>()
    private lateinit var mAppointmentData: AppointmentData
    private lateinit var mNaverMap: NaverMap
    private val mMarker = Marker()
    private val startMarker = Marker()
    private lateinit var mLocationSource: FusedLocationSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_appointment_detail)

        setupEvents()
        setValues()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setupEvents() {

        binding.scrollStop.setOnTouchListener { _, _ ->

            binding.mainScrollView.requestDisallowInterceptTouchEvent(true)

            return@setOnTouchListener false
        }
    }

    override fun setValues() {

        titleTxt.text = "일정 상세정보"

        mAppointmentData = intent.getSerializableExtra("appointment") as AppointmentData

        mInvitedFriendsList.addAll(mAppointmentData.invitedFriends)

        binding.titleTxt.text = mAppointmentData.title
        binding.placeTxt.text = mAppointmentData.place

//        1) 참여인원 수 => "(참여인원 : ? 명)" => 본인 빼고 초대된 사람들 수만.
        val invitedFriendsCount = mAppointmentData.invitedFriends.size - 1
        binding.invitedFriendsCount.text = "(참여인원 : ${invitedFriendsCount}명)"

//        2) 약속시간 => 9/3 오후 6:30 양식으로 가공.
        val sdf = SimpleDateFormat(" M/d a h:mm")
        val dateTime = sdf.format(mAppointmentData.datetime.time).toString()
        binding.timeTxt.text = dateTime

//        네이버 맵 설정 함수
        setNaverMap()

//        4) 응용 1 - 친구목록 => 레이아웃에 xml inflate 해서 하나씩 addView
        val inflater = LayoutInflater.from(mContext)

        for (i in 0 until mInvitedFriendsList.size) {

            val friendsList = mInvitedFriendsList[i]
            val invitedFriend = inflater.inflate(
                R.layout.invited_friends_list_item,
                binding.invitedFriendsLayout,
                false
            )
            val nickname = invitedFriend.findViewById<TextView>(R.id.friendNickname_Txt)
            val statusTxt = invitedFriend.findViewById<TextView>(R.id.status_Txt)
            val friendProfileImg = invitedFriend.findViewById<ImageView>(R.id.friendProfile_Img)

            nickname.text = friendsList.nickName
            Glide.with(mContext)
                .load(friendsList.profileImg)
                .into(friendProfileImg)

            binding.invitedFriendsLayout.addView(invitedFriend)
        }

    }

    private fun setNaverMap() {

//        지도 관련 코드.
//         - 마커를 하나 생성 -> 도착지 좌표에 찍어주기
//         - 카메라 이동 -> 도착지 좌표로 카메라 이동
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.naverMapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.naverMapView, it).commit()
            }

        mapFragment.getMapAsync { naverMap ->
            mNaverMap = naverMap

            mLocationSource = FusedLocationSource(this, Request.LOCATION_PERMISSION_REQUEST_CODE)

            mNaverMap.locationSource = mLocationSource

            val startLat = mAppointmentData.latitude
            val startLng = mAppointmentData.longitude

            val currentAppointment = LatLng(startLat, startLng)
//            val cameraUpdate = CameraUpdate.scrollTo(currentAppointment)
//            naverMap.moveCamera(cameraUpdate)

            val uiSettings = naverMap.uiSettings
            uiSettings.isCompassEnabled = true
            uiSettings.isScaleBarEnabled = false
            uiSettings.isLocationButtonEnabled = true

            mMarker.icon = OverlayImage.fromResource(R.drawable.arrival_marker)

            mMarker.position = currentAppointment
            mMarker.map = naverMap

            val endLat = mAppointmentData.startLatitude
            val endLng = mAppointmentData.startLongitude

            val startLatLng =
                LatLng(endLat, endLng)
            startMarker.icon = OverlayImage.fromResource(R.drawable.map_marker_red)
            startMarker.position = startLatLng
            startMarker.map = naverMap

//            시작점, 도착점 중간으로 카메라 이동?
            val centerOfStartAndDest = LatLng(
                (startLat + endLat) / 2,
                (startLng + endLng) / 2
            )

            val cameraUpdate = CameraUpdate.scrollTo(centerOfStartAndDest)
            naverMap.moveCamera(cameraUpdate)

//            거리에 따른 줌 레벨 변경 (도전과제)
            val zoomLevel = 11.0  // 두 좌표의 직선거리에 따라 어느 줌 레벨이 적당한지 계산해줘야함.
            mNaverMap.moveCamera(CameraUpdate.zoomTo(zoomLevel))

//            기본 정보창 띄우기

            val infoWindow = InfoWindow()

            infoWindow.open(mMarker)

//            지도의 아무데나 찍으면 열려있는 마커 닫아주기.
            mNaverMap.setOnMapClickListener { _, _ ->

                infoWindow.close()
            }
            mMarker.setOnClickListener {

                val clickedMarker = it as Marker

                if (clickedMarker.infoWindow == null) {

//                    마커에 연결된 정보창 없을때 (닫혀있을때)
                    infoWindow.open(clickedMarker)
                } else {

                    infoWindow.close()
                }
                return@setOnClickListener true
            }

            val url =
                HttpUrl.parse("https://api.odsay.com/v1/api/searchPubTransPath")!!.newBuilder()
            url.addEncodedQueryParameter("apikey", "JdJCDd5mWQLx6RMfBFXCYV0S/Kw3CU0YMt4WrfwXhTg")
            url.addEncodedQueryParameter("SX", startLng.toString())
            url.addEncodedQueryParameter("SY", startLat.toString())
            url.addEncodedQueryParameter("EX", endLng.toString())
            url.addEncodedQueryParameter("EY", endLat.toString())

            val request = Request.Builder()

            val client = OkHttpClient()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {

                }

                override fun onResponse(call: Call, response: Response) {

                    val bodyString = response.body()!!
                    val jsonObj = JSONObject(bodyString)
                }
            })

            //        5) 응용 2 - 출발지 좌표도 지도에 설정.
//         - 마커 찍기.
//         - 출발지 / 도착지 일직선 PathOverlay 그어주기
//         - 대중교통 API 활용 => 1. 도착 예상시간 표시 (infoWindow), 2. 실제 경유지로 PathOverlay 그어주기.
            val myOdsayService =
                ODsayService.init(mContext, "JdJCDd5mWQLx6RMfBFXCYV0S/Kw3CU0YMt4WrfwXhTg")

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode != Request.LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }

        if (mLocationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!mLocationSource.isActivated) {
                mNaverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }

    }
}