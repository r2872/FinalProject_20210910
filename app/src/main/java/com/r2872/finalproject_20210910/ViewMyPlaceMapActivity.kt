package com.r2872.finalproject_20210910

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.r2872.finalproject_20210910.databinding.ActivityViewMyPlaceMapBinding
import com.r2872.finalproject_20210910.datas.PlaceListData
import com.r2872.finalproject_20210910.utils.Request

class ViewMyPlaceMapActivity : BaseActivity() {

    private lateinit var binding: ActivityViewMyPlaceMapBinding
    private lateinit var mMyPlaceData: PlaceListData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_my_place_map)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {

    }

    override fun setValues() {

        titleTxt.text = "상세 장소"

        mMyPlaceData = intent.getSerializableExtra("myPlace") as PlaceListData

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.naverMapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.naverMapView, it).commit()
            }

        mapFragment.getMapAsync { naverMap ->

            val lat = mMyPlaceData.latitude
            val lng = mMyPlaceData.longitude

            val currentAppointment = LatLng(lat, lng)
            val cameraUpdate = CameraUpdate.scrollTo(currentAppointment)
            naverMap.moveCamera(cameraUpdate)

            val uiSettings = naverMap.uiSettings
            uiSettings.isScaleBarEnabled = false

            val marker = Marker()
            marker.icon = OverlayImage.fromResource(R.drawable.map_marker_red)

            marker.position = currentAppointment
            marker.map = naverMap

        }
    }
}