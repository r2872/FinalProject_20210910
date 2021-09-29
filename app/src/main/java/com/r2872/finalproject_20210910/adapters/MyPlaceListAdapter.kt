package com.r2872.finalproject_20210910.adapters

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.r2872.finalproject_20210910.R
import com.r2872.finalproject_20210910.ViewMyPlaceListActivity
import com.r2872.finalproject_20210910.ViewMyPlaceMapActivity
import com.r2872.finalproject_20210910.datas.BasicResponse
import com.r2872.finalproject_20210910.datas.PlaceListData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPlaceListAdapter(
    val mContext: Context,
    private val mList: List<PlaceListData>
) : RecyclerView.Adapter<MyPlaceListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(mContext).inflate(R.layout.my_place_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mContext, mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val placeNameTxt = itemView.findViewById<TextView>(R.id.placeName_Txt)
        private val isPrimary = itemView.findViewById<TextView>(R.id.isPrimary)
        private val viewPlaceMapBtn = itemView.findViewById<ImageView>(R.id.viewPlaceMap_Btn)
        private val backgroundLayout = itemView.findViewById<LinearLayout>(R.id.backgroundLayout)

        fun bind(context: Context, item: PlaceListData) {

            placeNameTxt.text = item.name
            if (item.isPrimary) {
                isPrimary.visibility = View.VISIBLE
            }
            viewPlaceMapBtn.setOnClickListener {
                val myIntent = Intent(mContext, ViewMyPlaceMapActivity::class.java)
                myIntent.putExtra("myPlace", item)
                mContext.startActivity(myIntent)
            }
            backgroundLayout.setOnClickListener {
                Toast.makeText(mContext, item.name, Toast.LENGTH_SHORT).show()
            }

            backgroundLayout.setOnLongClickListener {

                val alert = AlertDialog.Builder(context)
                    .setTitle("해당 장소를 삭제하시겠습니까?")
                    .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                        (context as ViewMyPlaceListActivity).apiService.deleteRequestUserPlace(item.id)
                            .enqueue(object : Callback<BasicResponse> {
                                override fun onResponse(
                                    call: Call<BasicResponse>,
                                    response: Response<BasicResponse>
                                ) {
                                    if (response.isSuccessful) {
                                        val basicResponse = response.body()!!
                                        Toast.makeText(
                                            context,
                                            "삭제되었습니다.",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        context.getMyPlaceListFromServer()
                                    }
                                }

                                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                                }
                            })
                    })
                    .setNegativeButton("취소", null)
                    .show()

                return@setOnLongClickListener true
            }
        }

    }
}