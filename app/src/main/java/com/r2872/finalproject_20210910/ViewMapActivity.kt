package com.r2872.finalproject_20210910

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
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
            infoWindow.adapter = object : InfoWindow.DefaultViewAdapter(mContext) {
                override fun getContentView(p0: InfoWindow): View {

                    val myView =
                        LayoutInflater.from(mContext).inflate(R.layout.my_custom_info_window, null)

                    return myView
                }
            }
            infoWindow.open(marker)
        }
    }
}