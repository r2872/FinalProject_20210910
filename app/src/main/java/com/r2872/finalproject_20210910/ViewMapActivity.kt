package com.r2872.finalproject_20210910

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.odsay.odsayandroidsdk.API
import com.odsay.odsayandroidsdk.ODsayData
import com.odsay.odsayandroidsdk.ODsayService
import com.odsay.odsayandroidsdk.OnResultCallbackListener
import com.r2872.finalproject_20210910.adapters.TrafficAdapter
import com.r2872.finalproject_20210910.databinding.ActivityViewMapBinding
import com.r2872.finalproject_20210910.datas.AppointmentData
import com.r2872.finalproject_20210910.datas.SubPathData
import com.r2872.finalproject_20210910.utils.Request

class ViewMapActivity : BaseActivity() {

    private lateinit var binding: ActivityViewMapBinding
    private lateinit var mAppointmentData: AppointmentData
    private lateinit var mLocationSource: FusedLocationSource
    private lateinit var mAdapter: TrafficAdapter
    private val mList = ArrayList<SubPathData>()

    //    선택된 출발지를 보여줄 마커
    private val mStartPlaceMarker = Marker()

    //        선택된 도착지를 보여줄 마커 하나만 생성.
    private val selectedPointMaker = Marker()

    //    네이버 지도를 멤버변수로 담자.
    private lateinit var mNaverMap: NaverMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_map)

        setValues()
        setupEvents()
    }

    override fun setupEvents() {

    }

    override fun setValues() {

        titleTxt.text = "상세 장소"
        mAdapter = TrafficAdapter(mContext, mList)
        binding.pathInfoList.adapter = mAdapter
        binding.pathInfoList.layoutManager = LinearLayoutManager(mContext)
        binding.pathInfoList.addItemDecoration(
            DividerItemDecoration(
                mContext,
                LinearLayoutManager.VERTICAL
            )
        )

        mAppointmentData = intent.getSerializableExtra("appointment") as AppointmentData

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.naverMapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.naverMapView, it).commit()
            }

        mapFragment.getMapAsync { naverMap ->
            mNaverMap = naverMap

            mLocationSource = FusedLocationSource(this, Request.LOCATION_PERMISSION_REQUEST_CODE)

            mNaverMap.locationSource = mLocationSource

            val lat = mAppointmentData.latitude
            val lng = mAppointmentData.longitude

            val currentAppointment = LatLng(lat, lng)
            val cameraUpdate = CameraUpdate.scrollTo(currentAppointment)
            naverMap.moveCamera(cameraUpdate)

            val uiSettings = naverMap.uiSettings
            uiSettings.isCompassEnabled = true
            uiSettings.isScaleBarEnabled = false
            uiSettings.isLocationButtonEnabled = true

            selectedPointMaker.icon = OverlayImage.fromResource(R.drawable.arrival_marker)

            selectedPointMaker.position = currentAppointment
            selectedPointMaker.map = naverMap

            mStartPlaceMarker.icon = OverlayImage.fromResource(R.drawable.map_marker_red)
            mStartPlaceMarker.position =
                LatLng(mAppointmentData.startLatitude, mAppointmentData.startLongitude)
            mStartPlaceMarker.map = naverMap

//            기본 정보창 띄우기

            val infoWindow = InfoWindow()

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

                        Log.d("API 통신", "성공")
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
                            val trafficType = subPathObj.getInt("trafficType")
                            val sectionTime = subPathObj.getInt("sectionTime")
                            Log.d("trafficType", trafficType.toString())
                            if (trafficType == 3) {
                                mList.add(SubPathData(trafficType, sectionTime))
                            }

                            if (!subPathObj.isNull("passStopList")) {

                                val pathName: String

                                if (trafficType != 3) {
                                    val stationCount = subPathObj.getInt("stationCount")
                                    Log.d("stationCount", stationCount.toString())
                                    val startName = subPathObj.getString("startName")
                                    Log.d("startName", startName.toString())
                                    val endName = subPathObj.getString("endName")
                                    Log.d("endName", endName.toString())
                                    val laneArr = subPathObj.getJSONArray("lane")
                                    Log.d("laneArr", laneArr.toString())
                                    val laneObj = laneArr.getJSONObject(0)

                                    when (trafficType) {
                                        1 -> {
                                            pathName = laneObj.getString("name")
                                            mList.add(
                                                (SubPathData(
                                                    trafficType,
                                                    sectionTime,
                                                    pathName,
                                                    stationCount,
                                                    startName,
                                                    endName
                                                ))
                                            )
                                        }
                                        2 -> {
                                            pathName = laneObj.getString("busNo")
                                            mList.add(
                                                (SubPathData(
                                                    trafficType,
                                                    sectionTime,
                                                    pathName,
                                                    stationCount,
                                                    startName,
                                                    endName
                                                ))
                                            )
                                        }
                                    }
                                }
                                Log.d("리스트", mList.toString())
                                mAdapter.notifyDataSetChanged()

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

            infoWindow.open(selectedPointMaker)

//            지도의 아무데나 찍으면 열려있는 마커 닫아주기.
            naverMap.setOnMapClickListener { _, _ ->

                infoWindow.close()
            }
            selectedPointMaker.setOnClickListener {

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