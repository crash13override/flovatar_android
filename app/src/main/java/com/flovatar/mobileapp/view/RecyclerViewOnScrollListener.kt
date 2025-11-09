package com.flovatar.mobileapp.view

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flovatar.mobileapp.utils.PrefUtils
import com.flovatar.mobileapp.viewmodel.AvatarsListViewModel
import com.jackandphantom.carouselrecyclerview.CarouselLayoutManager
import java.lang.Exception

class RecyclerViewOnScrollListener(
    var paginationArgs: PaginationArgs,
    val context: Context,
    val viewModel: AvatarsListViewModel
) : RecyclerView.OnScrollListener() {
    private var previousTotalItemCount = 0
    private var loading = false
    private var isPublic = false

    fun setIsPublic(value: Boolean) {
        isPublic = value
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
        val mTotalItemCount = recyclerView.layoutManager!!.itemCount - 1
        if (loading && mTotalItemCount > previousTotalItemCount) {
            loading = false
        }
        if (!loading && layoutManager!!.findLastVisibleItemPosition() > -1 && layoutManager.findLastVisibleItemPosition() >= mTotalItemCount - VISIBLE_THRESHOLD) {
            loading = true
            previousTotalItemCount = mTotalItemCount
            val nextPage: Int = paginationArgs.currentPage + 1
            if (paginationArgs.totalPage > nextPage) {
                getMoreData(nextPage)
                paginationArgs.currentPage = nextPage
            } else {
                paginationArgs.isHideLoading = true
            }
        }
    }

    private fun getMoreData(nextPage: Int) {
        if (isPublic) {
            viewModel.getAvatarList(nextPage)
        } else {
            if (PrefUtils.with(context).getUserToken() != null) {
                viewModel.getAvatarListByAddress(nextPage, PrefUtils.with(context).getUserToken()!!)
            }
        }
    }

    fun resetListener() {
        previousTotalItemCount = 0
        loading = false
        paginationArgs.currentPage = 1
    }

    companion object {
        const val CONNECTIONS = 1
        private const val VISIBLE_THRESHOLD = 4
    }
}