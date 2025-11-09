package com.flovatar.mobileapp.model

import com.google.gson.annotations.SerializedName

data class LeaderboardItem(
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("flow_address")
    val flowAddress: String,
    @SerializedName("score")
    val score: Long,
    @SerializedName("game")
    val game: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updateAt: String
)