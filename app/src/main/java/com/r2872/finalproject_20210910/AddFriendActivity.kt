package com.r2872.finalproject_20210910

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.r2872.finalproject_20210910.adapters.SearchFriendRecyclerViewAdapter
import com.r2872.finalproject_20210910.databinding.ActivityAddFriendBinding
import com.r2872.finalproject_20210910.datas.BasicResponse
import com.r2872.finalproject_20210910.datas.UserData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddFriendActivity : BaseActivity() {

    private lateinit var binding: ActivityAddFriendBinding
    private lateinit var mAdapter: SearchFriendRecyclerViewAdapter
    private val mList = ArrayList<UserData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_friend)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {

        binding.searchBtn.setOnClickListener {

            val inputKeyword = binding.searchEdt.text.toString()
            if (inputKeyword.length < 2) {
                Toast.makeText(mContext, "키워드를 2자리 이상 입력 해 주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            getSearchFriendFromServer(inputKeyword)
        }
    }

    override fun setValues() {

        titleTxt.text = "친구 찾기"
        mAdapter = SearchFriendRecyclerViewAdapter(mContext, mList)
        binding.searchUserRecyclerView.adapter = mAdapter
    }

    private fun getSearchFriendFromServer(inputKeyword: String) {

        apiService.getRequestUserSearch(inputKeyword).enqueue(object : Callback<BasicResponse> {

            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {

                if (response.isSuccessful) {

                    val basicResponse = response.body()!!
                    val usersArr = basicResponse.data.users

                    if (usersArr.isEmpty()) {
                        Toast.makeText(mContext, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                        closeKeyboard()
                    } else {
                        mList.clear()
                        mList.addAll(usersArr)
                        Log.d("서버응답", usersArr.toString())
                        closeKeyboard()
                    }
                }
                mAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

            }
        })
    }

    private fun closeKeyboard() {
        val view = this.currentFocus

        if (view != null) {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}