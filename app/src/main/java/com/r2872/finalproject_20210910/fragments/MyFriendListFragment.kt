package com.r2872.finalproject_20210910.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.r2872.finalproject_20210910.R
import com.r2872.finalproject_20210910.adapters.FriendListAdapter
import com.r2872.finalproject_20210910.databinding.FragmentMyFriendListBinding
import com.r2872.finalproject_20210910.datas.UserData

class MyFriendListFragment : BaseFragment() {

    private lateinit var binding: FragmentMyFriendListBinding
    private lateinit var mAdapter: FriendListAdapter
    private val mList = ArrayList<UserData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_my_friend_list, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {

    }

    override fun setValues() {

        mAdapter = FriendListAdapter(mContext, mList)
        binding.myFriendsRecyclerView.adapter = mAdapter
        binding.myFriendsRecyclerView.addItemDecoration(
            DividerItemDecoration(
                mContext,
                LinearLayoutManager.VERTICAL
            )
        )
    }
}