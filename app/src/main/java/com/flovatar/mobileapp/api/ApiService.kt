package com.flovatar.mobileapp.api

import com.flovatar.mobileapp.model.AvatarListResponse
import com.flovatar.mobileapp.model.LeaderboardItem
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import okhttp3.ResponseBody
import retrofit2.http.*

interface ApiService {

    @GET("collection/api")
    fun getAvatars(@Query("page") page: Int): Single<AvatarListResponse>

    @GET
    fun getAvatarsByAddress(@Url url: String): Single<AvatarListResponse>

    @GET("api/leaderboard/game/{game}")
    fun getLeaderboardByGame(@Path("game") game: Int): Observable<List<LeaderboardItem>>

    @GET("api/leaderboard/address/{address}")
    fun getLeaderboardByUser(@Path("address") address: String): Observable<List<LeaderboardItem>>

    @POST("api/leaderboard")
    fun setScore(
        @Query("game") game: Int, @Query("name") name: String,
        @Query("flow_address") address: String, @Query("score") score: Int
    ): Single<LeaderboardItem>

    @Headers("Content-Type: image/svg+xml")
    @GET
    fun getRandomAvatar(@Url url: String): Single<ResponseBody>
}