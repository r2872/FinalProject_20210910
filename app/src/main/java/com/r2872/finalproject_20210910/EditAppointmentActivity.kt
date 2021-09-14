package com.r2872.finalproject_20210910

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.r2872.finalproject_20210910.databinding.ActivityEditAppoinmentBinding
import com.r2872.finalproject_20210910.datas.BasicResponse
import net.daum.mf.map.api.MapView
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class EditAppointmentActivity : BaseActivity() {

    private lateinit var binding: ActivityEditAppoinmentBinding

    //    선택한 약속 일시를 저장할 변수.
    private val mSelectedDateTime = Calendar.getInstance()

    //    선택한 약속장소를 저장할 변수.
    private var mSelectedLat = 0.0 // Double 을 넣을것임.
    private var mSelectedLng = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_appoinment)

        setValues()
        setupEvents()
    }

    override fun setupEvents() {

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

            apiService.postRequestAppointment(
                inputTitle,
                finalDateTime,
                inputPlaceName,
                mSelectedLat, mSelectedLng
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

            finish()

        }

        binding.dateTxt.setOnClickListener {

            showDatePicker()
        }
        binding.timeTxt.setOnClickListener {

            showTimePicker()
        }
    }

    override fun setValues() {

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.naverMapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.naverMapView, it).commit()
            }
        mapFragment.getMapAsync { naverMap ->
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

//            선택된 위치를 보여줄 마커 하나만 생성.
            val selectedPointMaker = Marker()
            selectedPointMaker.icon = OverlayImage.fromResource(R.drawable.map_marker_red)

            naverMap.setOnMapClickListener { _, latLng ->

                Toast.makeText(
                    mContext,
                    "위도: ${latLng.latitude}, 경도: ${latLng.longitude}",
                    Toast.LENGTH_SHORT
                ).show()
                mSelectedLat = latLng.latitude
                mSelectedLng = latLng.longitude

//                좌표를 받아서 -> 미리 만들어둔 마커의 좌표로 연결. 맵에 띄우자.
                selectedPointMaker.position = LatLng(mSelectedLat, mSelectedLng)
                selectedPointMaker.map = naverMap
            }
        }
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


}