package com.flovatar.mobileapp.view

class PaginationArgs {
    var currentPage = 0
    var totalPage = 0

    var isHideLoading = false
    val isShowProgress: Boolean
        get() = totalPage > 1 && currentPage + 1 <= totalPage && !isHideLoading

}

