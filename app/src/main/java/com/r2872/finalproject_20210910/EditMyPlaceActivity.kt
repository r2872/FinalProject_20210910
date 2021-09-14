package com.r2872.finalproject_20210910

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.r2872.finalproject_20210910.databinding.ActivityEditMyPlaceBinding
import com.r2872.finalproject_20210910.datas.BasicResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditMyPlaceActivity : BaseActivity() {

    private lateinit var binding: ActivityEditMyPlaceBinding
    private var mSelectedLat = 0.0
    private var mSelectedLng = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_my_place)

        setValues()
        setupEvents()
    }

    override fun setupEvents() {

        binding.addBtn.setOnClickListener {

            val inputTitle = binding.placeTxt.text.toString()

            if (inputTitle == "") {
                Toast.makeText(mContext, "이름을 지정해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (mSelectedLat == 0.0 && mSelectedLng == 0.0) {
                Toast.makeText(mContext, "장소를 지정해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Log.d("입력값", inputTitle)
            Log.d("입력값", mSelectedLat.toString())
            Log.d("입력값", mSelectedLng.toString())

            apiService.postRequestMyPlaceList(inputTitle, mSelectedLat, mSelectedLng, true)
                .enqueue(object : Callback<BasicResponse> {
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
    }

    override fun setValues() {

        titleTxt.text = "장소 추가"

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.naverMapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.naverMapView, it).commit()
            }
        mapFragment.getMapAsync { naverMap ->

            val myHome = LatLng(37.5674, 126.9075)
            val cameraUpdate = CameraUpdate.scrollTo(myHome)
            naverMap.moveCamera(cameraUpdate)

            val uiSettings = naverMap.uiSettings
            uiSettings.isCompassEnabled = true
            uiSettings.isScaleBarEnabled = false
            uiSettings.isLocationButtonEnabled = true

            val selectedPointMaker = Marker()
            selectedPointMaker.icon = OverlayImage.fromResource(R.drawable.map_marker_red)

            naverMap.setOnMapClickListener { _, latLng ->

                mSelectedLat = latLng.latitude
                mSelectedLng = latLng.longitude

                Toast.makeText(
                    mContext,
                    "$mSelectedLat $mSelectedLng",
                    Toast.LENGTH_SHORT
                ).show()

                selectedPointMaker.position = LatLng(mSelectedLat, mSelectedLng)
                selectedPointMaker.map = naverMap
            }
        }
    }
}