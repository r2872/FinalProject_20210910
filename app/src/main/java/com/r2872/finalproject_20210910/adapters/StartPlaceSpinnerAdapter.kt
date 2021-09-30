package com.r2872.finalproject_20210910.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.r2872.finalproject_20210910.R
import com.r2872.finalproject_20210910.datas.PlaceListData
import com.r2872.finalproject_20210910.utils.FontChanger

class StartPlaceSpinnerAdapter(
    private val mContext: Context,
    resId: Int,
    private val mList: List<PlaceListData>
) : ArrayAdapter<PlaceListData>(mContext, resId, mList) {

    private val mInflater = LayoutInflater.from(mContext)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var row = convertView
        if (row == null) {
            row = mInflater.inflate(R.layout.my_place_list_item, null)
        }
        row!!

        val data = mList[position]

        val placeNameTxt = row.findViewById<TextView>(R.id.placeName_Txt)
        val isPrimaryTxt = row.findViewById<TextView>(R.id.isPrimary)

        placeNameTxt.text = data.name

        if (data.isPrimary) {
            isPrimaryTxt.visibility = View.VISIBLE
        } else {
            isPrimaryTxt.visibility = View.GONE
        }

        FontChanger.setGlobalFont(mContext, row)

        return row
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {

        var row = convertView
        if (row == null) {
            row = mInflater.inflate(R.layout.my_place_list_item, null)
        }
        row!!

        val data = mList[position]

        val placeNameTxt = row.findViewById<TextView>(R.id.placeName_Txt)
        val isPrimaryTxt = row.findViewById<TextView>(R.id.isPrimary)

        placeNameTxt.text = data.name

        if (data.isPrimary) {
            isPrimaryTxt.visibility = View.VISIBLE
        } else {
            isPrimaryTxt.visibility = View.GONE
        }

        FontChanger.setGlobalFont(mContext, row)

        return row
    }
}