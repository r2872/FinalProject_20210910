package com.r2872.finalproject_20210910

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
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
import com.r2872.finalproject_20210910.datas.BasicResponse
import com.r2872.finalproject_20210910.datas.UserData
import okhttp3.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class AppointmentDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityAppointmentDetailBinding
    private val mInvitedFriendsList = ArrayList<UserData>()
    private lateinit var mAppointmentData: AppointmentData
    private lateinit var mNaverMap: NaverMap
    private val mMarker = Marker()
    private val startMarker = Marker()

    //    버튼이 눌리면 => API 전송해달라고 표시 flag
    var needLocationSendServer = false

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

        binding.arrivalBtn.setOnClickListener {

//            서버에 위치를 보내야한다고 flag 값을 true
            needLocationSendServer = true

//           내 위치 파악. (현재위치 위도 / 경도 추출)

            val pl = object : PermissionListener {
                override fun onPermissionGranted() {

//                   실제 위치 물어보기 (안드로이드 폰에게)

                    if (ActivityCompat.checkSelfPermission(
                            mContext,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            mContext,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {

//                        권한이 하나라도 없다면 밑의 코드 실행 X
                        return
                    }

//                   위치 관리자부터 가져오자.
                    val locationManger = getSystemService(LOCATION_SERVICE) as LocationManager

                    locationManger.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0L, 0f, object : LocationListener {
                            override fun onLocationChanged(p0: Location) {

                                if (needLocationSendServer) {

//                                    서버에 위경도 값 보내주기.
                                    Log.d("위도", p0.latitude.toString())
                                    Log.d("경도", p0.longitude.toString())

                                    apiService.postRequestArrival(
                                        mAppointmentData.id,
                                        p0.latitude,
                                        p0.longitude
                                    ).enqueue(object : Callback<BasicResponse> {
                                        override fun onResponse(
                                            call: Call<BasicResponse>,
                                            response: Response<BasicResponse>
                                        ) {
                                            if (response.isSuccessful) {
                                                val basicResponse = response.body()!!

                                                Toast.makeText(
                                                    mContext,
                                                    "도착인증하였습니다.",
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()
                                            } else {

//                                                서버가 알려주는 인증 실패 사유 출력
                                                val jsonObj =
                                                    JSONObject(response.errorBody()!!.string())
                                                val message = jsonObj.getString("message")
                                                Toast.makeText(
                                                    mContext,
                                                    message,
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<BasicResponse>,
                                            t: Throwable
                                        ) {

                                        }
                                    })

//                                    응답이 성공적으로 돌아오면 => 서버에 안보내기.
                                    needLocationSendServer = false
                                }
                            }

                            override fun onStatusChanged(
                                provider: String?,
                                status: Int,
                                extras: Bundle?
                            ) {

                            }

                            override fun onProviderEnabled(provider: String) {

                            }

                            override fun onProviderDisabled(provider: String) {

                            }


                        })
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {

                    Toast.makeText(mContext, "현재 위치 정볼르 파악해야 약속 도착 시간을", Toast.LENGTH_SHORT).show()
                }
            }
            TedPermission.create()
                .setPermissionListener(pl)
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check()
        }

    }


    override fun setValues() {

        titleTxt.text = "일정 상세정보"

        mAppointmentData = intent.getSerializableExtra("appointment") as AppointmentData

        mInvitedFriendsList.addAll(mAppointmentData.invitedFriends)

        binding.titleTxt.text = mAppointmentData.title
        binding.placeTxt.text = mAppointmentData.place

//        1) 참여인원 수 => "(참여인원 : ? 명)" => 본인 빼고 초대된 사람들 수만.
        val invitedFriendsCount = mAppointmentData.invitedFriends.size
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

            val endLat = mAppointmentData.latitude
            val endLng = mAppointmentData.longitude

            val currentAppointment = LatLng(endLat, endLng)
//            val cameraUpdate = CameraUpdate.scrollTo(currentAppointment)
//            naverMap.moveCamera(cameraUpdate)

            val uiSettings = naverMap.uiSettings
            uiSettings.isCompassEnabled = true
            uiSettings.isScaleBarEnabled = false
            uiSettings.isLocationButtonEnabled = true

            mMarker.icon = OverlayImage.fromResource(R.drawable.arrival_marker)

            mMarker.position = currentAppointment
            mMarker.map = naverMap

            val startLat = mAppointmentData.startLatitude
            val startLng = mAppointmentData.startLongitude

            val startLatLng =
                LatLng(startLat, startLng)
            startMarker.icon = OverlayImage.fromResource(R.drawable.map_marker_red)
            startMarker.position = startLatLng
            startMarker.map = naverMap

//            시작점, 도착점 중간으로 카메라 이동?
            val centerOfStartAndDest = LatLng(
                (startLat + startLat) / 2,
                (startLng + startLng) / 2
            )

            val cameraUpdate = CameraUpdate.scrollTo(centerOfStartAndDest)
            naverMap.moveCamera(cameraUpdate)

//            거리에 따른 줌 레벨 변경 (도전과제)
            val zoomLevel = 11.0  // 두 좌표의 직선거리에 따라 어느 줌 레벨이 적당한지 계산해줘야함.
            mNaverMap.moveCamera(CameraUpdate.zoomTo(zoomLevel))

//            기본 정보창 띄우기

            val infoWindow = InfoWindow()

            val myOdsayService =
                ODsayService.init(mContext, "JdJCDd5mWQLx6RMfBFXCYV0S/Kw3CU0YMt4WrfwXhTg")
            myOdsayService.requestSearchPubTransPath(
                mAppointmentData.startLongitude.toString(),
                mAppointmentData.startLatitude.toString(),
                endLng.toString(),
                endLat.toString(),
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
                        ) // 시작점

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

                        Log.d("예상시간실패", p1!!)
                    }
                })

            infoWindow.open(mMarker)

//            지도의 아무데나 찍으면 열려있는 마커 닫아주기.
            naverMap.setOnMapClickListener { _, _ ->

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
        }

    }

}