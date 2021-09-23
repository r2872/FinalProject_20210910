package com.r2872.finalproject_20210910

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.setMargins
import androidx.databinding.DataBindingUtil
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
import com.r2872.finalproject_20210910.adapters.AddFriendsSpinnerAdapter
import com.r2872.finalproject_20210910.adapters.StartPlaceSpinnerAdapter
import com.r2872.finalproject_20210910.databinding.ActivityEditAppoinmentBinding
import com.r2872.finalproject_20210910.datas.BasicResponse
import com.r2872.finalproject_20210910.datas.PlaceListData
import com.r2872.finalproject_20210910.datas.UserData
import com.r2872.finalproject_20210910.utils.Request
import com.r2872.finalproject_20210910.utils.SizeUtil.Companion.dbToPx
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@SuppressLint("ClickableViewAccessibility", "SimpleDateFormat")
class EditAppointmentActivity : BaseActivity() {

    private lateinit var binding: ActivityEditAppoinmentBinding

    //    선택한 약속 일시를 저장할 변수.
    private val mSelectedDateTime = Calendar.getInstance()

    //    선택한 약속장소를 저장할 변수.
    private var mSelectedLat = 0.0 // Double 을 넣을것임.
    private var mSelectedLng = 0.0

    //    출발지 목록을 담아둘 리스트.
    val mStartPlaceList = ArrayList<PlaceListData>()
    private lateinit var mSpinnerAdapter: StartPlaceSpinnerAdapter

    //    내 친구 목록을 담아둘 리스트.
    private val mFriendList = ArrayList<UserData>()
    private lateinit var mAddFriendsSpinnerAdapter: AddFriendsSpinnerAdapter

    //    약속에 참가시킬 친구 리스트.
    private val mSelectedFriendList = ArrayList<UserData>()

    //    선택된 출발지를 담아줄 변수
    private lateinit var mSelectedStartPlace: PlaceListData

    //    선택된 출발지를 보여줄 마커
    private val mStartPlaceMarker = Marker()

    //    화면에 그려질 출발~도착지 연결 선
    private val mPath = PathOverlay()

    //        선택된 도착지를 보여줄 마커 하나만 생성.
    private val selectedPointMaker = Marker()

    //    도착지에 보여줄 정보창
    private val mInfoWindow = InfoWindow()

    //    네이버 지도를 멤버변수로 담자.
    private var mNaverMap: NaverMap? = null

    private lateinit var mLocationSource: FusedLocationSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_appoinment)

        setValues()
        setupEvents()
    }

    override fun setupEvents() {

//        친구추가 버튼 이벤트
        binding.addFriendToListBtn.setOnClickListener {

//            고른 친구가 누구인지? => 스피너에서 선택되어있는 친구를 찾아내자.
            val selectedFriend = mFriendList[binding.myFriendsSpinner.selectedItemPosition]

//            이미 선택한 친구인지 검사
            if (mSelectedFriendList.contains(selectedFriend)) {
                Toast.makeText(mContext, "이미 추가한 친구입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

//            텍스트뷰 하나를 코틀린에서 생성
            val textView = TextView(mContext)
            textView.text = selectedFriend.nickName
            textView.setBackgroundResource(R.drawable.selected_friend_box)
            textView.setPadding(
                dbToPx(mContext, 5f).toInt(),
                dbToPx(mContext, 5f).toInt(),
                dbToPx(mContext, 5f).toInt(),
                dbToPx(mContext, 5f).toInt()
            )
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(dbToPx(mContext, 5f).toInt())
            textView.layoutParams = params

//            만들어낸 텍스트뷰에 이벤트 처리
            textView.setOnClickListener {
                binding.friendListLayout.removeView(textView)
                mSelectedFriendList.remove(selectedFriend)
            }

//            레이아웃에 추가 + 친구목록으로도 추가.
            binding.friendListLayout.addView(textView)
            mSelectedFriendList.add(selectedFriend)

        }

//        스피너의 선택 이벤트.
        binding.startPlaceSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {

//                    화면이 뜨면 자동으로 0번 아이템이 선택된다.
                    Log.d("선택된위치", position.toString())

//                    스피너의 위치에 맞는 장소를 선택된 출발지점으로 선정.
                    mSelectedStartPlace = mStartPlaceList[position]

                    mNaverMap?.let {
                        drawStartPlaceToDestination(it)
                    }

                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }

//        확인 버튼이 눌리면?
        binding.addBtn.setOnClickListener {

//            입력한 값들 받아오기
//            1. 일정 제목
            val inputTitle = binding.titleEdt.text.toString()
            if (inputTitle == "") {
                Toast.makeText(mContext, "약속 제목을 적어주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

//            2. 약속 일시? -> "2021-09-13 11:11" String 변환까지.
//            => 날짜 / 시간중 선택 안한게 있다면? 선택하라고 토스트, 함수 강제 종료. (validation)
            if (binding.dateTxt.text == "일자 설정") {
                Toast.makeText(mContext, "일자를 설정하지 않았습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (binding.timeTxt.text == "시간 설정") {
                Toast.makeText(mContext, "시간을 설정하지 않았습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

//            여기 코드 실행된다 : 일자 / 시간 모두 설정했다.

//            약속일시를 => UTC 시간대로 변경해주자. 서버가 사용하는 시간대는 UTC 라서,
//            앱에서 폰의 시간대를 찾아서, 보정해주자.
            val myTimeZone = mSelectedDateTime.timeZone

//            내 시간대가 시차가 UTC 로 부터 얼마나 나는지? 밀리초 ~ 시간 으로 변환.
            val myTimeOffset = myTimeZone.rawOffset / 1000 / 60 / 60

//            선택된 시간을 보정. (더해져 있는 시차를 빼 주자)
            mSelectedDateTime.add(Calendar.HOUR_OF_DAY, -myTimeOffset)

//            선택된 약속일시를 -> "yyyy-MM-dd HH:mm" 양식으로 가공. => 최종 서버에 파라미터로 첨부
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val finalDateTime = sdf.format(mSelectedDateTime.time)
            Log.d("서버에 보낼 약속일시", finalDateTime)

//            3. 약속 장소?
//            - 장소 이름
            val inputPlaceName = binding.placeSearchEdt.text.toString()

//            - 장소 위도 / 경도 ?
//            val lat = 37.4972
//            val lng = 127.0271

            if (mSelectedLat == 0.0 && mSelectedLng == 0.0) {
                Toast.makeText(mContext, "약속 장소를 지정해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val strBuilder = StringBuilder()
            for (i in 0 until mSelectedFriendList.size) {
                strBuilder.append(mSelectedFriendList[i].id.toString())
                strBuilder.append(",")
            }
            var friendListStr = ""
            if (strBuilder.isNotEmpty()) {
                friendListStr = strBuilder.substring(0, strBuilder.length - 1)
            }

            Log.d("친구리스트", friendListStr)

            apiService.postRequestAppointment(
                inputTitle,
                finalDateTime,
                mSelectedStartPlace.name,
                mSelectedStartPlace.latitude,
                mSelectedStartPlace.longitude,
                inputPlaceName,
                mSelectedLat, mSelectedLng,
                friendListStr
            ).enqueue(object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {
                    if (response.isSuccessful) {

                        Toast.makeText(mContext, "등록 완료", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(
                            mContext,
                            JSONObject(response.errorBody()!!.string()).getString("message"),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                }
            })

        }

        binding.dateTxt.setOnClickListener {

            showDatePicker()
        }
        binding.timeTxt.setOnClickListener {

            showTimePicker()
        }

//        지도 영역에 손을 대면 => 스크롤뷰를 정지.
        binding.scrollStop.setOnTouchListener { _, _ ->

            binding.mainScrollView.requestDisallowInterceptTouchEvent(true)

            return@setOnTouchListener false
        }
    }

    override fun setValues() {

        titleTxt.text = "일정 생성"

        mSpinnerAdapter =
            StartPlaceSpinnerAdapter(mContext, R.layout.my_place_list_item, mStartPlaceList)
        binding.startPlaceSpinner.adapter = mSpinnerAdapter

        mAddFriendsSpinnerAdapter =
            AddFriendsSpinnerAdapter(mContext, R.layout.friend_list_item, mFriendList)
        binding.myFriendsSpinner.adapter = mAddFriendsSpinnerAdapter

//        내 친구 목록 담아주기
        apiService.getRequestFriendList("my").enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                if (response.isSuccessful) {

                    val basicResponse = response.body()!!
                    mFriendList.clear()
                    mFriendList.addAll(basicResponse.data.friends)
                }
                mAddFriendsSpinnerAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

            }
        })

//        내 출발장소 목록 담아주기
        apiService.getRequestMyAppointmentList().enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {

                if (response.isSuccessful) {
                    mStartPlaceList.clear()

                    val basicResponse = response.body()!!
                    mStartPlaceList.addAll(basicResponse.data.places)
                }
                mSpinnerAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

            }
        })

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.naverMapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.naverMapView, it).commit()
            }
        mapFragment.getMapAsync { naverMap ->

            mNaverMap = naverMap

            mLocationSource = FusedLocationSource(this, Request.LOCATION_PERMISSION_REQUEST_CODE)

            mNaverMap!!.locationSource = mLocationSource
            Log.d("지도바로할일", naverMap.toString())
//            집 좌표를 지도 시작점으로. (예제)
//            it.mapType = NaverMap.MapType.Hybrid

//            좌표를 다루는 변수 - LatLng 클래스 활용
            val myHome = LatLng(37.5674, 126.9075)
            val cameraUpdate = CameraUpdate.scrollTo(myHome)
            naverMap.moveCamera(cameraUpdate)

            val uiSettings = naverMap.uiSettings
            uiSettings.isCompassEnabled = true
            uiSettings.isScaleBarEnabled = false
            uiSettings.isLocationButtonEnabled = true

            selectedPointMaker.icon = OverlayImage.fromResource(R.drawable.arrival_marker)

            naverMap.setOnMapClickListener { _, latLng ->

//                Toast.makeText(
//                    mContext,
//                    "위도: ${latLng.latitude}, 경도: ${latLng.longitude}",
//                    Toast.LENGTH_SHORT
//                ).show()
                mSelectedLat = latLng.latitude
                mSelectedLng = latLng.longitude

//                좌표를 받아서 -> 미리 만들어둔 마커의 좌표로 연결. 맵에 띄우자.
                selectedPointMaker.position = LatLng(mSelectedLat, mSelectedLng)
                selectedPointMaker.map = naverMap

                drawStartPlaceToDestination(naverMap)
            }
        }
    }

    private fun drawStartPlaceToDestination(naverMap: NaverMap) {

//        시작지점의 위경도
//        mSelectedStartPlace 위경도 활용.

//        시작지점에 좌표 마커 찍어주기.
        mStartPlaceMarker.position =
            LatLng(mSelectedStartPlace.latitude, mSelectedStartPlace.longitude)
        mStartPlaceMarker.map = naverMap
        mStartPlaceMarker.icon = OverlayImage.fromResource(R.drawable.map_marker_red)

//        도착지점의 위경도
//        mSelectedLat,Lng 변수 활용

//        예제. 시작지점 -> 도착지점으로 연결 선 그어주기.

//        좌표 목록을 ArrayList 로 담자.
        val points = ArrayList<LatLng>()

        points.add(LatLng(mSelectedStartPlace.latitude, mSelectedStartPlace.longitude)) // 시작점

//        대중교통 길찾기 API => 들리는 좌표들을 제공 => 목록을 담아주자.
        val odsay = ODsayService.init(mContext, "JdJCDd5mWQLx6RMfBFXCYV0S/Kw3CU0YMt4WrfwXhTg")
        odsay.requestSearchPubTransPath(
            mSelectedStartPlace.longitude.toString(),
            mSelectedStartPlace.latitude.toString(),
            mSelectedLng.toString(),
            mSelectedLat.toString(),
            null, null, null, object : OnResultCallbackListener {
                override fun onSuccess(p0: ODsayData?, p1: API?) {

                    val jsonObject = p0!!.json
                    val resultObj = jsonObject.getJSONObject("result")
                    val pathArr = resultObj.getJSONArray("path")
                    val firstPathObj = pathArr.getJSONObject(0)

//                    총 소요시간이 얼마나 걸리나?'
                    val infoObj = firstPathObj.getJSONObject("info")
                    val totalTime = infoObj.getInt("totalTime")
                    val hour = totalTime / 60
                    val minute = totalTime % 60

                    Log.d("총 소요시간", totalTime.toString())

//                    멤버변수로 만들어둔 정보창의 내용 설정, 열어주기
                    mInfoWindow.adapter = object : InfoWindow.DefaultTextAdapter(mContext) {
                        override fun getText(p0: InfoWindow): CharSequence {

                            return if (hour == 0) {
                                "${minute}분 소요 예상"
                            } else {
                                "${hour}시간 ${minute}분 소요 예상"
                            }
                        }
                    }
                    mInfoWindow.open(selectedPointMaker)

//                  경유지들 좌표를 목록에 추가 (결과가 어떻게 되어있는지 분석, Parsing)

                    val subPathArr = firstPathObj.getJSONArray("subPath")
                    for (i in 0 until subPathArr.length()) {
                        val subPathObj = subPathArr.getJSONObject(i)

                        if (!subPathObj.isNull("passStopList")) {

//                            정거장 목록을 불러내보자.
                            val passStopListObj = subPathObj.getJSONObject("passStopList")
                            val stationArr = passStopListObj.getJSONArray("stations")
                            for (j in 0 until stationArr.length()) {

                                val stationObj = stationArr.getJSONObject(j)
                                Log.d("길찾기 응답", stationObj.toString())

                                val latlng = LatLng(
                                    stationObj.getString("y").toDouble(),
                                    stationObj.getString("x").toDouble()
                                )
//                                points ArrayList 에 경유지로 추가
                                points.add(latlng)
                            }
                        }
                    }

//                    최종 목적지 좌표도 추가
                    points.add(LatLng(mSelectedLat, mSelectedLng)) // 도착점

//        매번 새로 PolyLine 을 그리면, 선이 하나씩 계속 추가됨.
//        멤버 변수로 선을 하나 지정해두고, 위치값만 변경하면서 사용.
//        val polyline = PolylineOverlay()
                    mPath.coords = points
                    mPath.map = naverMap
                }

                override fun onError(p0: Int, p1: String?, p2: API?) {}
            })
    }

    private fun showDatePicker() {
        DatePickerDialog(
            mContext,
            DatePickerDialog.OnDateSetListener { _, y, m, d ->
                mSelectedDateTime.apply {
                    set(Calendar.YEAR, y)
                    set(Calendar.MONTH, m)
                    set(Calendar.DAY_OF_MONTH, d)
                }
                val sdf = SimpleDateFormat("yyyy-MM-dd (E)")

                binding.dateTxt.text = sdf.format(mSelectedDateTime.time).toString()
            },
            mSelectedDateTime.get(Calendar.YEAR),
            mSelectedDateTime.get(Calendar.MONTH),
            mSelectedDateTime.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker() {
        TimePickerDialog(
            mContext,
            TimePickerDialog.OnTimeSetListener { _, h, m ->
                mSelectedDateTime.apply {
                    set(Calendar.HOUR_OF_DAY, h)
                    set(Calendar.MINUTE, m)
                }
                val sdf = SimpleDateFormat("a h:mm")

                binding.timeTxt.text = sdf.format(mSelectedDateTime.time).toString()
            },
            mSelectedDateTime.get(Calendar.HOUR_OF_DAY),
            mSelectedDateTime.get(Calendar.MINUTE),
            false
        ).show()
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
                mNaverMap!!.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }

    }


}