package com.r2872.finalproject_20210910.adapters

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.r2872.finalproject_20210910.utils.FontChanger

abstract class BaseViewHolder(context: Context, itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    init {
        FontChanger.setGlobalFont(context, itemView)
    }
}