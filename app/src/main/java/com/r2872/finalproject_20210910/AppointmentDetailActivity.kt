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
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.odsay.odsayandroidsdk.API
import com.odsay.odsayandroidsdk.ODsayData
import com.odsay.odsayandroidsdk.ODsayService
import com.odsay.odsayandroidsdk.OnResultCallbackListener
import com.r2872.finalproject_20210910.databinding.ActivityAppointmentDetailBinding
import com.r2872.finalproject_20210910.datas.AppointmentData
import com.r2872.finalproject_20210910.datas.UserData
import java.text.SimpleDateFormat

class AppointmentDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityAppointmentDetailBinding
    private val mInvitedFriendsList = ArrayList<UserData>()
    private lateinit var mAppointmentData: AppointmentData
    private lateinit var mNaverMap: NaverMap
    private val mMarker = Marker()
    private val startMarker = Marker()

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

            val lat = mAppointmentData.latitude
            val lng = mAppointmentData.longitude

            val currentAppointment = LatLng(lat, lng)
            val cameraUpdate = CameraUpdate.scrollTo(currentAppointment)
            naverMap.moveCamera(cameraUpdate)

            val uiSettings = naverMap.uiSettings
            uiSettings.isCompassEnabled = true
            uiSettings.isScaleBarEnabled = false
            uiSettings.isLocationButtonEnabled = true

            mMarker.icon = OverlayImage.fromResource(R.drawable.arrival_marker)

            mMarker.position = currentAppointment
            mMarker.map = naverMap

            val startLatLng =
                LatLng(mAppointmentData.startLatitude, mAppointmentData.startLongitude)
            startMarker.icon = OverlayImage.fromResource(R.drawable.map_marker_red)
            startMarker.position = startLatLng
            startMarker.map = naverMap

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

            //        5) 응용 2 - 출발지 좌표도 지도에 설정.
//         - 마커 찍기.
//         - 출발지 / 도착지 일직선 PathOverlay 그어주기
//         - 대중교통 API 활용 => 1. 도착 예상시간 표시 (infoWindow), 2. 실제 경유지로 PathOverlay 그어주기.
            val myOdsayService =
                ODsayService.init(mContext, "JdJCDd5mWQLx6RMfBFXCYV0S/Kw3CU0YMt4WrfwXhTg")

            myOdsayService.requestSearchPubTransPath(
                mAppointmentData.startLongitude.toString(),
                mAppointmentData.startLatitude.toString(),
                lng.toString(),
                lat.toString(),
                null,
                null,
                null,
                object : OnResultCallbackListener {
                    override fun onSuccess(p0: ODsayData?, p1: API?) {

                        val jsonObj = p0!!.json
                        val resultObj = jsonObj.getJSONObject("result")
                        val pathArr = resultObj.getJSONArray("path")
                        val firstPathObj = pathArr.getJSONObject(0)

//                        출발점 ~ 경유지 목록 ~ 도착지를 이어주는 Path 객체를 추가.
                        val points = ArrayList<LatLng>()

                        points.add(
                            LatLng(
                                mAppointmentData.startLatitude,
                                mAppointmentData.startLongitude
                            )
                        )
                        val subPathArr = firstPathObj.getJSONArray("subPath")
                        for (i in 0 until subPathArr.length()) {
                            val subPathObj = subPathArr.getJSONObject(i)

                            if (!subPathObj.isNull("passStopList")) {

//                            정거장 목록을 불러내보자.
                                val passStopListObj = subPathObj.getJSONObject("passStopList")
                                val stationArr = passStopListObj.getJSONArray("stations")
                                for (j in 0 until stationArr.length()) {

                                    val stationObj = stationArr.getJSONObject(j)

//                                    각 정거장의 GPS 좌표 추출 -> 네이버지도의 위치객체로 변환.
                                    val latlng = LatLng(
                                        stationObj.getString("y").toDouble(),
                                        stationObj.getString("x").toDouble()
                                    )
//                                points ArrayList 에 경유지로 추가
                                    points.add(latlng)
                                }
                            }
                        }
                        points.add(
                            LatLng(
                                mAppointmentData.latitude,
                                mAppointmentData.longitude
                            )
                        )
                        //    화면에 그려질 출발~도착지 연결 선
                        val mPath = PathOverlay()

                        mPath.coords = points
                        mPath.map = naverMap

                        val infoObj = firstPathObj.getJSONObject("info")
                        val totalTime = infoObj.getInt("totalTime")
//                                Log.d("총 소요시간", totalTime.toString())

//                                시간 / 분 으로 분리. 92 => 1시간 32분
//                                시간 : 전체 분 / 60
//                                분 : 전체 분 % 60
                        val hour = totalTime / 60
                        val minute = totalTime % 60
                        Log.d("예상시간", hour.toString())
                        Log.d("예상분", minute.toString())

                        infoWindow.adapter = object : InfoWindow.DefaultViewAdapter(mContext) {
                            override fun getContentView(p0: InfoWindow): View {
                                val myView =
                                    LayoutInflater.from(mContext)
                                        .inflate(R.layout.my_custom_info_window, null)

                                val placeName = myView.findViewById<TextView>(R.id.placeName_Txt)
                                val arrivalTime =
                                    myView.findViewById<TextView>(R.id.arrivalTime_Txt)

                                placeName.text = mAppointmentData.place
                                arrivalTime.text =
                                    if (hour == 0) {
                                        "${minute}분 소요 예상"
                                    } else {
                                        "${hour}시간 ${minute}분 소요 예상"
                                    }

                                return myView
                            }
                        }
                    }

                    override fun onError(p0: Int, p1: String?, p2: API?) {

                    }
                })
        }

    }
}