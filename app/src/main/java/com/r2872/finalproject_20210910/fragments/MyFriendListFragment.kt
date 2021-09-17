package com.r2872.finalproject_20210910.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.r2872.finalproject_20210910.R
import com.r2872.finalproject_20210910.adapters.FriendListAdapter
import com.r2872.finalproject_20210910.databinding.FragmentMyFriendListBinding
import com.r2872.finalproject_20210910.datas.BasicResponse
import com.r2872.finalproject_20210910.datas.UserData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyFriendListFragment : BaseFragment() {

    companion object {

        private var frag: MyFriendListFragment? = null
        fun getFrag(): MyFriendListFragment {
            if (frag == null) {
                frag = MyFriendListFragment()
            }
            return frag!!
        }
    }

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

        getFriendListFromServer()
    }

    fun getFriendListFromServer() {

        apiService.getRequestFriendList("my").enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {

                if (response.isSuccessful) {
                    mList.clear()

                    val basicResponse = response.body()!!
                    val friendsArr = basicResponse.data.friends

                    mList.addAll(friendsArr)
                }

                mAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

            }
        })
    }
}