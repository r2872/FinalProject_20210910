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
import com.r2872.finalproject_20210910.datas.BasicResponse
import com.r2872.finalproject_20210910.datas.SubPathData
import com.r2872.finalproject_20210910.datas.UserData
import com.r2872.finalproject_20210910.utils.Request
import okhttp3.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class InvitedAppointmentDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityAppointmentDetailBinding
    private val mInvitedFriendsList = ArrayList<UserData>()
    private lateinit var mAppointmentData: AppointmentData
    private lateinit var mNaverMap: NaverMap
    private val mMarker = Marker()
    private val startMarker = Marker()
    private lateinit var mLocationSource: FusedLocationSource
    private lateinit var mPath: PathOverlay
    private lateinit var mInfoWindow: InfoWindow
    private var needLocationFromServer = true
    private var mSelectedStartPlaceLat = 0.0
    private var mSelectedStartPlaceLng = 0.0

    //    ????????? ????????? => API ?????????????????? ?????? flag
    var needLocationSendServer = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_appointment_detail)

        setupEvents()
        setValues()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setupEvents() {

        binding.searchTraffic.setOnClickListener {

            needLocationFromServer = true
            getLocation()
        }

        binding.scrollStop.setOnTouchListener { _, _ ->

            binding.mainScrollView.requestDisallowInterceptTouchEvent(true)

            return@setOnTouchListener false
        }

        binding.arrivalBtn.setOnClickListener {

//            ????????? ????????? ?????????????????? flag ?????? true
            needLocationSendServer = true

//           ??? ?????? ??????. (???????????? ?????? / ?????? ??????)

            val pl = object : PermissionListener {
                override fun onPermissionGranted() {

//                   ?????? ?????? ???????????? (??????????????? ?????????)

                    if (ActivityCompat.checkSelfPermission(
                            mContext,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            mContext,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {

//                        ????????? ???????????? ????????? ?????? ?????? ?????? X
                        return
                    }

//                   ?????? ??????????????? ????????????.
                    val locationManger = getSystemService(LOCATION_SERVICE) as LocationManager

                    locationManger.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0L, 0f, object : LocationListener {
                            override fun onLocationChanged(p0: Location) {

                                if (needLocationSendServer) {

//                                    ????????? ????????? ??? ????????????.
                                    Log.d("??????", p0.latitude.toString())
                                    Log.d("??????", p0.longitude.toString())

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
                                                    "???????????????????????????.",
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()
//                                              ????????? ??????????????? ???????????? => ????????? ????????????.
                                                needLocationSendServer = false
                                            } else {

//                                                ????????? ???????????? ?????? ?????? ?????? ??????
                                                val jsonObj =
                                                    JSONObject(response.errorBody()!!.string())
                                                val message = jsonObj.getString("message")
                                                Toast.makeText(
                                                    mContext,
                                                    message,
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()
                                                needLocationSendServer = false
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<BasicResponse>,
                                            t: Throwable
                                        ) {

                                        }
                                    })
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
                }
            }
            TedPermission.create()
                .setPermissionListener(pl)
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check()
        }

        binding.refreshBtn.setOnClickListener {
            getAppointmentFromServer()
            Toast.makeText(mContext, "???????????? ??????", Toast.LENGTH_SHORT).show()
        }

    }


    override fun setValues() {

        titleTxt.text = "???????????? ????????????"
        binding.searchTraffic.visibility = View.VISIBLE

        mAppointmentData = intent.getSerializableExtra("appointment") as AppointmentData

        mInvitedFriendsList.addAll(mAppointmentData.invitedFriends)

        binding.titleTxt.text = mAppointmentData.title
        binding.placeTxt.text = mAppointmentData.place

//        1) ???????????? ??? => "(???????????? : ? ???)" => ?????? ?????? ????????? ????????? ??????.
        val invitedFriendsCount = mAppointmentData.invitedFriends.size
        binding.invitedFriendsCount.text = "(???????????? : ${invitedFriendsCount}???)"

//        2) ???????????? => 9/3 ?????? 6:30 ???????????? ??????.
        val sdf = SimpleDateFormat(" M/d a h:mm")
        val now = Calendar.getInstance()
        mAppointmentData.datetime.time += now.timeZone.rawOffset
        val dateTime = sdf.format(mAppointmentData.datetime.time).toString()
        binding.timeTxt.text = dateTime

//        ????????? ??? ?????? ??????
        setNaverMap()

//        4) ?????? 1 - ???????????? => ??????????????? xml inflate ?????? ????????? addView
        getAppointmentFromServer()

    }

    private fun getAppointmentFromServer() {

//        ?????? ???????????? ????????? ???????????? ?????? ??????.
        apiService.getRequestAppointmentDetail(mAppointmentData.id)
            .enqueue(object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {
                    if (response.isSuccessful) {

                        val basicResponse = response.body()!!

                        mAppointmentData = basicResponse.data.appointment

                        //        ?????? ?????? API ?????? ????????? ?????? ?????? ????????????.

//                        ????????? ???????????? ???????????? View ?????? ?????? ?????? => ??? ????????? ?????? ????????? ?????? ??????.
                        binding.invitedFriendsLayout.removeAllViews()

                        val inflater = LayoutInflater.from(mContext)
                        val sdf = SimpleDateFormat("H:mm ??????")

                        for (i in 0 until mInvitedFriendsList.size) {

                            val friendsList = mInvitedFriendsList[i]
                            val invitedFriend = inflater.inflate(
                                R.layout.invited_friends_list_item,
                                binding.invitedFriendsLayout,
                                false
                            )
                            val nickname =
                                invitedFriend.findViewById<TextView>(R.id.friendNickname_Txt)
                            val statusTxt = invitedFriend.findViewById<TextView>(R.id.status_Txt)
                            val friendProfileImg =
                                invitedFriend.findViewById<ImageView>(R.id.friendProfile_Img)

                            nickname.text = friendsList.nickName
                            if (friendsList.arrivedAt != null) {

                                val arrivedTime =
                                    sdf.format(friendsList.arrivedAt!!.time).toString()
                                statusTxt.text = arrivedTime
                            } else {
                                statusTxt.text = "?????? ???"
                            }
                            Glide.with(mContext)
                                .load(friendsList.profileImg)
                                .into(friendProfileImg)

                            binding.invitedFriendsLayout.addView(invitedFriend)
                        }
                    } else {
                        val jsonObj = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(mContext, jsonObj.getString("message"), Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                }
            })

    }


    private fun setNaverMap() {

//        ?????? ?????? ??????.
//         - ????????? ?????? ?????? -> ????????? ????????? ????????????
//         - ????????? ?????? -> ????????? ????????? ????????? ??????
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.naverMapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.naverMapView, it).commit()
            }

        mapFragment.getMapAsync { naverMap ->
            mNaverMap = naverMap
            mLocationSource = FusedLocationSource(this, Request.LOCATION_PERMISSION_REQUEST_CODE)

            mNaverMap.locationSource = mLocationSource

            val endLat = mAppointmentData.latitude
            val endLng = mAppointmentData.longitude

            val currentAppointment = LatLng(endLat, endLng)
            val cameraUpdate = CameraUpdate.scrollTo(currentAppointment)
            naverMap.moveCamera(cameraUpdate)

            val uiSettings = mNaverMap.uiSettings
            uiSettings.isCompassEnabled = true
            uiSettings.isScaleBarEnabled = false
            uiSettings.isLocationButtonEnabled = true

            mMarker.icon = OverlayImage.fromResource(R.drawable.arrival_marker)

            mMarker.position = currentAppointment
            mMarker.map = mNaverMap

//            ?????? ????????? ?????????

            mInfoWindow = InfoWindow()

//            ????????? ???????????? ????????? ???????????? ?????? ????????????.
            naverMap.setOnMapClickListener { _, _ ->

                mInfoWindow.close()
            }
            mMarker.setOnClickListener {

                val clickedMarker = it as Marker

                if (clickedMarker.infoWindow == null) {

//                    ????????? ????????? ????????? ????????? (???????????????)
                    mInfoWindow.open(clickedMarker)
                } else {

                    mInfoWindow.close()
                }
                return@setOnClickListener true
            }
        }

    }

    private fun getPathAPI(latLng: LatLng) {

        val myOdsayService =
            ODsayService.init(mContext, "JdJCDd5mWQLx6RMfBFXCYV0S/Kw3CU0YMt4WrfwXhTg")
        myOdsayService.requestSearchPubTransPath(
            latLng.longitude.toString(),
            latLng.latitude.toString(),
            mAppointmentData.longitude.toString(),
            mAppointmentData.latitude.toString(),
            null,
            null,
            null,
            object : OnResultCallbackListener {
                override fun onSuccess(p0: ODsayData?, p1: API?) {

                    Log.d("API ??????", "??????")
                    val jsonObj = p0!!.json
                    val resultObj = jsonObj.getJSONObject("result")
                    val pathArr = resultObj.getJSONArray("path")
                    val firstPathObj = pathArr.getJSONObject(0)

//                        ????????? ~ ????????? ?????? ~ ???????????? ???????????? Path ????????? ??????.
                    val points = ArrayList<LatLng>()

                    points.add(
                        LatLng(
                            latLng.latitude,
                            latLng.longitude
                        )
                    ) // ?????????

                    val subPathArr = firstPathObj.getJSONArray("subPath")
                    for (i in 0 until subPathArr.length()) {
                        val subPathObj = subPathArr.getJSONObject(i)

                        if (!subPathObj.isNull("passStopList")) {

//                            ????????? ????????? ???????????????.
                            val passStopListObj = subPathObj.getJSONObject("passStopList")
                            val stationArr = passStopListObj.getJSONArray("stations")
                            for (j in 0 until stationArr.length()) {

                                val stationObj = stationArr.getJSONObject(j)

//                                    ??? ???????????? GPS ?????? ?????? -> ?????????????????? ??????????????? ??????.
                                val latlng = LatLng(
                                    stationObj.getString("y").toDouble(),
                                    stationObj.getString("x").toDouble()
                                )
//                                points ArrayList ??? ???????????? ??????
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
                    //    ????????? ????????? ??????~????????? ?????? ???
                    mPath = PathOverlay()

                    mPath.coords = points
                    mPath.map = mNaverMap

                    val infoObj = firstPathObj.getJSONObject("info")
                    val totalTime = infoObj.getInt("totalTime")
//                                Log.d("??? ????????????", totalTime.toString())

//                                ?????? / ??? ?????? ??????. 92 => 1?????? 32???
//                                ?????? : ?????? ??? / 60
//                                ??? : ?????? ??? % 60
                    val hour = totalTime / 60
                    val minute = totalTime % 60
                    Log.d("????????????", hour.toString())
                    Log.d("?????????", minute.toString())

                    mInfoWindow.adapter = object : InfoWindow.DefaultViewAdapter(mContext) {
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
                                    "${minute}??? ?????? ??????"
                                } else {
                                    "${hour}?????? ${minute}??? ?????? ??????"
                                }

                            return myView
                        }
                    }
                    mInfoWindow.open(mMarker)
                }

                override fun onError(p0: Int, p1: String?, p2: API?) {
                    Log.d("error", p0.toString())
                    if (p0 == -101) {
                        mInfoWindow.adapter = object : InfoWindow.DefaultViewAdapter(mContext) {
                            override fun getContentView(p0: InfoWindow): View {
                                val myView =
                                    LayoutInflater.from(mContext)
                                        .inflate(R.layout.my_custom_info_window, null)

                                val placeName =
                                    myView.findViewById<TextView>(R.id.placeName_Txt)
                                val arrivalTimeTxt =
                                    myView.findViewById<TextView>(R.id.arrivalTime_Txt)
                                placeName.text = "?????? ??????"
                                arrivalTimeTxt.visibility = View.GONE
                                return myView
                            }
                        }
                        mInfoWindow.open(mMarker)
                    }
                }
            })
    }

    private fun getLocation() {

        val pl = object : PermissionListener {
            override fun onPermissionGranted() {

                if (ActivityCompat.checkSelfPermission(
                        mContext,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        mContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }

                val locationManger = getSystemService(LOCATION_SERVICE) as LocationManager

                locationManger.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0L, 0f, object : LocationListener {
                        override fun onLocationChanged(p0: Location) {
                            if (needLocationFromServer) {
                                mSelectedStartPlaceLat = p0.latitude
                                mSelectedStartPlaceLng = p0.longitude
                                val LatLng = LatLng(
                                    mSelectedStartPlaceLat, mSelectedStartPlaceLng
                                )
                                val cameraUpdate = CameraUpdate.scrollTo(LatLng)
                                mNaverMap.moveCamera(cameraUpdate)
                                startMarker.icon =
                                    OverlayImage.fromResource(R.drawable.map_marker_red)
                                startMarker.position = LatLng
                                startMarker.map = mNaverMap
                                needLocationFromServer = false
                                getPathAPI(LatLng)
                            }
                        }

                        override fun onStatusChanged(
                            provider: String?,
                            status: Int,
                            extras: Bundle?
                        ) {
                        }

                        override fun onProviderEnabled(provider: String) {}
                        override fun onProviderDisabled(provider: String) {}
                    })
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {}
        }
        TedPermission.create()
            .setPermissionListener(pl)
            .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
            .check()
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