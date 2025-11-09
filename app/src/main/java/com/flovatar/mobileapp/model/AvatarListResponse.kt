package com.flovatar.mobileapp.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AvatarListResponse(
    @SerializedName("current_page")
    val currentPage: Int,
    @SerializedName("last_page")
    val lastPage: Int,
    @SerializedName("data")
    val data: List<AvatarModel>
) : Serializable
