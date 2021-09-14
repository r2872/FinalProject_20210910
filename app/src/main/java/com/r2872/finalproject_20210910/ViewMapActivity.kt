package com.r2872.finalproject_20210910

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.odsay.odsayandroidsdk.API
import com.odsay.odsayandroidsdk.ODsayData
import com.odsay.odsayandroidsdk.ODsayService
import com.odsay.odsayandroidsdk.OnResultCallbackListener
import com.r2872.finalproject_20210910.databinding.ActivityViewMapBinding
import com.r2872.finalproject_20210910.datas.AppointmentData

class ViewMapActivity : BaseActivity() {

    private lateinit var binding: ActivityViewMapBinding
    private lateinit var mAppointmentData: AppointmentData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_map)

        setValues()
        setupEvents()
    }

    override fun setupEvents() {

    }

    override fun setValues() {

        mAppointmentData = intent.getSerializableExtra("appointment") as AppointmentData

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.naverMapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.naverMapView, it).commit()
            }

        mapFragment.getMapAsync { naverMap ->

            val lat = mAppointmentData.latitude
            val lng = mAppointmentData.longitude

            val currentAppointment = LatLng(lat, lng)
            val cameraUpdate = CameraUpdate.scrollTo(currentAppointment)
            naverMap.moveCamera(cameraUpdate)

            val uiSettings = naverMap.uiSettings
            uiSettings.isCompassEnabled = true
            uiSettings.isScaleBarEnabled = false
            uiSettings.isLocationButtonEnabled = true

            val marker = Marker()
            marker.icon = OverlayImage.fromResource(R.drawable.map_marker_red)

            marker.position = currentAppointment
            marker.map = naverMap

//            기본 정보창 띄우기

            val infoWindow = InfoWindow()

            val myOdsayService =
                ODsayService.init(mContext, "JdJCDd5mWQLx6RMfBFXCYV0S/Kw3CU0YMt4WrfwXhTg")
            myOdsayService.requestSearchPubTransPath(
                126.9075.toString(),
                37.5674.toString(),
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

//                                for (i in 0 until pathArr.length()) {
//                                    val pathObj = pathArr.getJSONObject(i)
//                                    Log.d("API 응답", pathObj.toString(4))
//                                }
                        val firstPath = pathArr.getJSONObject(0)
                        val infoObj = firstPath.getJSONObject("info")
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

            infoWindow.open(marker)

//            지도의 아무데나 찍으면 열려있는 마커 닫아주기.
            naverMap.setOnMapClickListener { _, _ ->

                infoWindow.close()
            }
            marker.setOnClickListener {

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