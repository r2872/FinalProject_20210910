package com.r2872.finalproject_20210910

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Point
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.view.setMargins
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.r2872.finalproject_20210910.adapters.AddFriendsSpinnerAdapter
import com.r2872.finalproject_20210910.adapters.DialogEditRecyclerAdapter
import com.r2872.finalproject_20210910.adapters.StartPlaceSpinnerAdapter
import com.r2872.finalproject_20210910.databinding.ActivityEditAppoinmentBinding
import com.r2872.finalproject_20210910.datas.*
import com.r2872.finalproject_20210910.utils.GlobalData
import com.r2872.finalproject_20210910.utils.Request
import com.r2872.finalproject_20210910.utils.SizeUtil.Companion.dbToPx
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("ClickableViewAccessibility", "SimpleDateFormat")
class FixAppointmentActivity : BaseActivity() {

    private lateinit var binding: ActivityEditAppoinmentBinding

    //    ????????? ?????? ????????? ????????? ??????.
    private val mSelectedDateTime = Calendar.getInstance()

    //    ????????? ??????????????? ????????? ??????.
    var mSelectedLat = 0.0 // Double ??? ????????????.
    var mSelectedLng = 0.0

    //    ????????? ????????? ????????? ?????????.
    val mStartPlaceList = ArrayList<PlaceListData>()
    private lateinit var mSpinnerAdapter: StartPlaceSpinnerAdapter

    //    ??? ?????? ????????? ????????? ?????????.
    private val mFriendList = ArrayList<UserData>()
    private lateinit var mAddFriendsSpinnerAdapter: AddFriendsSpinnerAdapter

    //    ????????? ???????????? ?????? ?????????.
    private val mSelectedFriendList = ArrayList<UserData>()

    //    ????????? ???????????? ????????? ??????
    private lateinit var mSelectedStartPlace: PlaceListData

    //    ????????? ???????????? ????????? ??????
    private val mStartPlaceMarker = Marker()

    //    ????????? ????????? ??????~????????? ?????? ???
    private var mPath = PathOverlay()

    //        ????????? ???????????? ????????? ?????? ????????? ??????.
    private val selectedPointMaker = Marker()

    //    ???????????? ????????? ?????????
    private val mInfoWindow = InfoWindow()

    //    ????????? ????????? ??????????????? ??????.
    private lateinit var mNaverMap: NaverMap

    private lateinit var mLocationSource: FusedLocationSource

    private var needLocationFromServer = true

    private val mSearchPlaceList = ArrayList<SearchPlaceData>()
    lateinit var dialog: Dialog

    private lateinit var mAppointmentData: AppointmentData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_appoinment)

        setValues()
        setupEvents()
    }

    override fun setupEvents() {

        binding.placeSearchBtn.setOnClickListener {

            val inputPlaceName = binding.placeSearchEdt.text.toString()

            if (inputPlaceName.length < 2) {
                Toast.makeText(mContext, "?????? 2?????? ?????? ??????????????????", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val url =
                HttpUrl.parse("https://dapi.kakao.com/v2/local/search/keyword.json")!!.newBuilder()
            url.addQueryParameter("query", inputPlaceName)

            val urlString = url.toString()

            val request = okhttp3.Request.Builder()
                .url(urlString)
                .get()
                .header("Authorization", "KakaoAK bd3146605605fae442c5a9ab55cc5259")
                .build()

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {

                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {

                    val jsonObj = JSONObject(response.body()!!.string())
                    val documentsArr = jsonObj.getJSONArray("documents")
                    mSearchPlaceList.clear()

                    for (i in 0 until documentsArr.length()) {
                        val docu = documentsArr.getJSONObject(i)

                        Log.d("???????????????", docu.toString())

                        val addressName = docu.getString("address_name")
                        val placeName = docu.getString("place_name")
                        val lat = docu.getString("y")
                        val lng = docu.getString("x")
                        mSearchPlaceList.add(SearchPlaceData(placeName, addressName, lat, lng))
                    }
                    runOnUiThread {
                        if (mSearchPlaceList.isEmpty()) {
                            Toast.makeText(mContext, "?????? ????????? ????????????.", Toast.LENGTH_SHORT).show()
                            return@runOnUiThread
                        }
                        showAlertDialogSearchPlaceList()
                    }
                }
            })
        }

//        ???????????? ?????? ?????????
        binding.addFriendToListBtn.setOnClickListener {

//            ?????? ????????? ????????????? => ??????????????? ?????????????????? ????????? ????????????.
            val selectedFriend = mFriendList[binding.myFriendsSpinner.selectedItemPosition]

//            ?????? ????????? ???????????? ??????
            if (mSelectedFriendList.contains(selectedFriend)) {
                Toast.makeText(mContext, "?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            addFriendsTxt(selectedFriend)
            mSelectedFriendList.add(selectedFriend)

        }

//        ???????????? ?????? ?????????.
        binding.startPlaceSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {

//                    ????????? ?????? ???????????? 0??? ???????????? ????????????.
                    Log.d("???????????????", position.toString())

//                    ???????????? ????????? ?????? ????????? ????????? ?????????????????? ??????.
                    mSelectedStartPlace = mStartPlaceList[position]
                    drawStartPlaceToDestination(mNaverMap)
                    val cameraUpdate = CameraUpdate.scrollTo(
                        LatLng(
                            mSelectedStartPlace.latitude,
                            mSelectedStartPlace.longitude
                        )
                    )
                    mNaverMap.moveCamera(cameraUpdate)

                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }

//        ?????? ????????? ??????????
        binding.addBtn.setOnClickListener {

//            ????????? ?????? ????????????
//            1. ?????? ??????
            val inputTitle = binding.titleEdt.text.toString()
            val placeSearchEdt = binding.placeSearchEdt.text.toString()
            if (inputTitle.isEmpty()) {
                Toast.makeText(mContext, "?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

//            2. ?????? ??????? -> "2021-09-13 11:11" String ????????????.
//            => ?????? / ????????? ?????? ????????? ?????????? ??????????????? ?????????, ?????? ?????? ??????. (validation)
            if (binding.dateTxt.text == "?????? ??????") {
                Toast.makeText(mContext, "????????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (binding.timeTxt.text == "?????? ??????") {
                Toast.makeText(mContext, "????????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (placeSearchEdt.isEmpty()) {
                Toast.makeText(mContext, "???????????? ????????? ???????????????.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

//            ?????? ?????? ???????????? : ?????? / ?????? ?????? ????????????.

//            ??????????????? => UTC ???????????? ???????????????. ????????? ???????????? ???????????? UTC ??????,
//            ????????? ?????? ???????????? ?????????, ???????????????.
//            val myTimeZone = mSelectedDateTime.timeZone
//
////            ??? ???????????? ????????? UTC ??? ?????? ????????? ?????????? ????????? ~ ?????? ?????? ??????.
//            val myTimeOffset = myTimeZone.rawOffset / 1000 / 60 / 60
//
////            ????????? ????????? ??????. (????????? ?????? ????????? ??? ??????)
//            mSelectedDateTime.add(Calendar.HOUR_OF_DAY, -myTimeOffset)

//            ????????? ??????????????? -> "yyyy-MM-dd HH:mm" ???????????? ??????. => ?????? ????????? ??????????????? ??????
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val finalDateTime = sdf.format(mSelectedDateTime.time)
            Log.d("????????? ?????? ????????????", finalDateTime)

//            3. ?????? ???????
//            - ?????? ??????
            val inputPlaceName = binding.placeSearchEdt.text.toString()

//            - ?????? ?????? / ?????? ?
//            val lat = 37.4972
//            val lng = 127.0271

            if (mSelectedLat == 0.0 && mSelectedLng == 0.0) {
                Toast.makeText(mContext, "?????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show()
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

            Log.d("???????????????", friendListStr)

            apiService.putRequestAppointment(
                mAppointmentData.id,
                inputTitle,
                finalDateTime,
                mSelectedStartPlace.name,
                mSelectedStartPlace.latitude,
                mSelectedStartPlace.longitude,
                inputPlaceName,
                mSelectedLat,
                mSelectedLng,
                friendListStr
            ).enqueue(object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(mContext, "????????????", Toast.LENGTH_SHORT).show()
                        finish()
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

//        ?????? ????????? ?????? ?????? => ??????????????? ??????.
        binding.scrollStop.setOnTouchListener { _, _ ->

            binding.mainScrollView.requestDisallowInterceptTouchEvent(true)

            return@setOnTouchListener false
        }

        binding.currentLatLng.setOnClickListener {
            val customView =
                LayoutInflater.from(mContext).inflate(R.layout.my_custom_alert_edt_place, null)

            val alertDialog = AlertDialog.Builder(mContext)
                .setTitle("??????????????? ??????")
                .setView(customView)
                .setPositiveButton("??????", DialogInterface.OnClickListener { dialogInterface, i ->
                    needLocationFromServer = true
                    getLocation()
                    binding.startPlaceSpinner.visibility = View.GONE
                    val placeNameEdt = customView.findViewById<EditText>(R.id.placeName_Edt)
                    binding.currentLatLng.text = placeNameEdt.text.toString()
                    mSelectedStartPlace.name = placeNameEdt.text.toString()
                    mSelectedStartPlace.name = placeNameEdt.text.toString()
                })
                .setNegativeButton("??????", null)
                .show()
        }

        binding.startPlaceSelectBtn.setOnClickListener {
            binding.startPlaceSpinner.visibility = View.VISIBLE
            binding.currentLatLng.text = "??????????????? ????????????"
        }
    }

    override fun setValues() {

        titleTxt.text = "?????? ??????"
        binding.addBtn.text = "????????????"
        getAppointmentData()

        mSpinnerAdapter =
            StartPlaceSpinnerAdapter(mContext, R.layout.my_place_list_item, mStartPlaceList)
        binding.startPlaceSpinner.adapter = mSpinnerAdapter

        mAddFriendsSpinnerAdapter =
            AddFriendsSpinnerAdapter(mContext, R.layout.friend_list_item, mFriendList)
        binding.myFriendsSpinner.adapter = mAddFriendsSpinnerAdapter

//        ??? ?????? ?????? ????????????
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

//        ??? ???????????? ?????? ????????????
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
        setNaverMap()
    }

    private fun setNaverMap() {
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.naverMapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.naverMapView, it).commit()
            }
        mapFragment.getMapAsync { naverMap ->

            mNaverMap = naverMap

            binding.startPlaceSpinner.isClickable = true

            mLocationSource =
                FusedLocationSource(this, Request.LOCATION_PERMISSION_REQUEST_CODE)

            mNaverMap.locationSource = mLocationSource
            Log.d("??????????????????", mNaverMap.toString())
//            ??? ????????? ?????? ???????????????. (??????)
//            it.mapType = NaverMap.MapType.Hybrid

//            ????????? ????????? ?????? - LatLng ????????? ??????
            val myHome = LatLng(37.5674, 126.9075)
            val cameraUpdate = CameraUpdate.scrollTo(myHome)
            mNaverMap.moveCamera(cameraUpdate)

            val uiSettings = mNaverMap.uiSettings
            uiSettings.isCompassEnabled = true
            uiSettings.isScaleBarEnabled = false
            uiSettings.isLocationButtonEnabled = true

            selectedPointMaker.icon = OverlayImage.fromResource(R.drawable.arrival_marker)
            selectedPointMaker.position =
                LatLng(mAppointmentData.latitude, mAppointmentData.longitude)
            selectedPointMaker.map = mNaverMap

            mNaverMap.setOnMapClickListener { _, latLng ->

//                Toast.makeText(
//                    mContext,
//                    "??????: ${latLng.latitude}, ??????: ${latLng.longitude}",
//                    Toast.LENGTH_SHORT
//                ).show()
                mSelectedLat = latLng.latitude
                mSelectedLng = latLng.longitude

//                ????????? ????????? -> ?????? ???????????? ????????? ????????? ??????. ?????? ?????????.
                selectedPointMaker.position = LatLng(mSelectedLat, mSelectedLng)

                drawStartPlaceToDestination(mNaverMap)
            }
        }
    }

    private fun drawStartPlaceToDestination(naverMap: NaverMap) {

        //        ??????????????? ?????????
//        mSelectedStartPlace ????????? ??????.

//        ??????????????? ?????? ?????? ????????????.
        mStartPlaceMarker.position =
            LatLng(mSelectedStartPlace.latitude, mSelectedStartPlace.longitude)
        mStartPlaceMarker.map = mNaverMap
        mStartPlaceMarker.icon = OverlayImage.fromResource(R.drawable.map_marker_red)

        if (mSelectedLat != 0.0 && mSelectedLng != 0.0) {

//        ??????. ???????????? -> ?????????????????? ?????? ??? ????????????.

//        ?????? ????????? ArrayList ??? ??????.
            val points = ArrayList<LatLng>()

            points.add(LatLng(mSelectedStartPlace.latitude, mSelectedStartPlace.longitude)) // ?????????

//        ???????????? ????????? API => ????????? ???????????? ?????? => ????????? ????????????.
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

//                    ??? ??????????????? ????????? ??????????'
                        val infoObj = firstPathObj.getJSONObject("info")
                        val totalTime = infoObj.getInt("totalTime")
                        val hour = totalTime / 60
                        val minute = totalTime % 60

                        Log.d("??? ????????????", totalTime.toString())

//                    ??????????????? ???????????? ???????????? ?????? ??????, ????????????
                        mInfoWindow.adapter = object : InfoWindow.DefaultTextAdapter(mContext) {
                            override fun getText(p0: InfoWindow): CharSequence {

                                return if (hour == 0) {
                                    "${minute}??? ?????? ??????"
                                } else {
                                    "${hour}?????? ${minute}??? ?????? ??????"
                                }
                            }
                        }
                        mInfoWindow.open(selectedPointMaker)

//                  ???????????? ????????? ????????? ?????? (????????? ????????? ??????????????? ??????, Parsing)

                        val subPathArr = firstPathObj.getJSONArray("subPath")
                        for (i in 0 until subPathArr.length()) {
                            val subPathObj = subPathArr.getJSONObject(i)

                            if (!subPathObj.isNull("passStopList")) {

//                            ????????? ????????? ???????????????.
                                val passStopListObj = subPathObj.getJSONObject("passStopList")
                                val stationArr = passStopListObj.getJSONArray("stations")
                                for (j in 0 until stationArr.length()) {

                                    val stationObj = stationArr.getJSONObject(j)
                                    Log.d("????????? ??????", stationObj.toString())

                                    val latlng = LatLng(
                                        stationObj.getString("y").toDouble(),
                                        stationObj.getString("x").toDouble()
                                    )
//                                points ArrayList ??? ???????????? ??????
                                    points.add(latlng)
                                }
                            }
                        }

//                    ?????? ????????? ????????? ??????
                        points.add(LatLng(mSelectedLat, mSelectedLng)) // ?????????

//        ?????? ?????? PolyLine ??? ?????????, ?????? ????????? ?????? ?????????.
//        ?????? ????????? ?????? ?????? ???????????????, ???????????? ??????????????? ??????.
//        val polyline = PolylineOverlay()
                        mPath.coords = points
                        mPath.map = mNaverMap
                    }

                    override fun onError(p0: Int, p1: String?, p2: API?) {
                        Log.d("error", p0.toString())
                        if (p0 == -101) {
                            Toast.makeText(mContext, "???????????? ????????? ????????????.", Toast.LENGTH_SHORT).show()
                            mPath.map = null
                            mInfoWindow.close()
                        }
                    }
                })
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

    fun placeSearchEvents() {
        binding.placeSearchEdt.setText(intent.getStringExtra("placeName"))
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(mSelectedLat, mSelectedLng))
        mNaverMap.moveCamera(cameraUpdate)
        selectedPointMaker.position = LatLng(mSelectedLat, mSelectedLng)
        selectedPointMaker.map = mNaverMap
        drawStartPlaceToDestination(mNaverMap)
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

    private fun showAlertDialogSearchPlaceList() {
        val display = windowManager.defaultDisplay
        val size = Point()
        dialog = Dialog(mContext)

        display.getRealSize(size)
        val lp = WindowManager.LayoutParams()

        val dialogView: View = LayoutInflater.from(mContext).inflate(R.layout.dialog_layout, null)

        lp.copyFrom(dialog.window!!.attributes)
        val width = size.x
        lp.width = width * 80 / 100 // ????????? ????????? 80%

        val height = size.y
        lp.height = height * 50 / 100

        dialog.setContentView(dialogView) // Dialog ??? ???????????? layout ??????

        dialog.setCanceledOnTouchOutside(true) // ?????? touch ??? Dialog ??????

        dialog.window!!.attributes = lp // ????????? ??????, ?????? ??? Dialog ??? ??????
        val dialogRecyclerView = dialogView.findViewById<RecyclerView>(R.id.place_list)
        dialogRecyclerView.layoutManager = LinearLayoutManager(mContext)
        dialogRecyclerView.adapter = DialogEditRecyclerAdapter(mContext, mSearchPlaceList)
        dialogRecyclerView.addItemDecoration(
            DividerItemDecoration(
                mContext,
                LinearLayoutManager.VERTICAL
            )
        )
        dialog.show()
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
                                mSelectedStartPlace.latitude = p0.latitude
                                mSelectedStartPlace.longitude = p0.longitude
                                val LatLng = LatLng(
                                    mSelectedStartPlace.latitude,
                                    mSelectedStartPlace.longitude
                                )
                                val cameraUpdate = CameraUpdate.scrollTo(LatLng)
                                mNaverMap.moveCamera(cameraUpdate)
                                drawStartPlaceToDestination(mNaverMap)
                                needLocationFromServer = false
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

    private fun getAppointmentData() {
        mAppointmentData = intent.getSerializableExtra("appointment") as AppointmentData
        mSelectedLat = mAppointmentData.latitude
        mSelectedLng = mAppointmentData.longitude
        binding.titleEdt.setText(mAppointmentData.title)
        binding.placeSearchEdt.setText(mAppointmentData.place)
        mSelectedFriendList.addAll(mAppointmentData.invitedFriends)
        for (i in 0 until mAppointmentData.invitedFriends.size) {
            if (mAppointmentData.invitedFriends[i].id != GlobalData.loginUser!!.id) {
                addFriendsTxt(mAppointmentData.invitedFriends[i])
            }
        }
        mSelectedDateTime.timeInMillis = mAppointmentData.datetime.time
//        val myTimeZone = mSelectedDateTime.timeZone
//        val myTimeOffset = myTimeZone.rawOffset / 1000 / 60 / 60
//        mSelectedDateTime.add(Calendar.HOUR_OF_DAY, myTimeOffset)
        val sdfDate = SimpleDateFormat("yyyy-MM-dd (E)")
        val sdfTime = SimpleDateFormat("a h:mm")
        binding.dateTxt.text = sdfDate.format(mSelectedDateTime.time)
        binding.timeTxt.text = sdfTime.format(mSelectedDateTime.time)
    }

    private fun addFriendsTxt(selectedFriend: UserData) {
        //            ???????????? ????????? ??????????????? ??????
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

//            ???????????? ??????????????? ????????? ??????
        textView.setOnClickListener {
            binding.friendListLayout.removeView(textView)
            mSelectedFriendList.remove(selectedFriend)
        }

//            ??????????????? ?????? + ????????????????????? ??????.
        binding.friendListLayout.addView(textView)
    }
}