package com.flovatar.mobileapp.view

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.flovatar.mobileapp.utils.PrefUtils
import com.flovatar.mobileapp.viewmodel.AvatarsListViewModel
import com.jackandphantom.carouselrecyclerview.CarouselLayoutManager

class CarouselRecyclerViewOnScrollListener(
    var paginationArgs: PaginationArgs, val context: Context,
    val viewModel: AvatarsListViewModel
) : RecyclerView.OnScrollListener() {
    private var previousTotalItemCount = 0
    private var loading = false
    private var isPublic: Boolean = false

    fun setPublic(value: Boolean) {
        isPublic = value
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        var carouselLayoutManager: CarouselLayoutManager? = null
        carouselLayoutManager = recyclerView.layoutManager as CarouselLayoutManager?
        val mTotalItemCount = recyclerView.layoutManager!!.itemCount - 1
        if (loading && mTotalItemCount > previousTotalItemCount) {
            loading = false
        }

        if (carouselLayoutManager != null) {
            val jdsh = carouselLayoutManager.getLastVisiblePosition()
            if (!loading && carouselLayoutManager.getLastVisiblePosition() > -1 && carouselLayoutManager.getLastVisiblePosition() >= mTotalItemCount - VISIBLE_THRESHOLD) {
                loading = true
                previousTotalItemCount = mTotalItemCount
                val nextPage: Int = paginationArgs.currentPage + 1
                if (paginationArgs.totalPage > nextPage) {
                    getMoreData(nextPage + 1)
                    paginationArgs.currentPage = nextPage
                } else {
                    paginationArgs.isHideLoading = true
                }
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