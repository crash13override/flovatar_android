package com.flovatar.mobileapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.flovatar.mobileapp.api.RetrofitClient
import com.flovatar.mobileapp.model.LeaderboardItem
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class LeaderboardViewModel : ViewModel() {
    val leaderBoardListForUser: MutableLiveData<List<LeaderboardItem>> =
        MutableLiveData<List<LeaderboardItem>>().apply { postValue(mutableListOf()) }

    val waldoLeaderBoardList: MutableLiveData<List<LeaderboardItem>> =
        MutableLiveData<List<LeaderboardItem>>().apply { postValue(mutableListOf()) }
    val whackLeaderBoardList: MutableLiveData<List<LeaderboardItem>> =
        MutableLiveData<List<LeaderboardItem>>().apply { postValue(mutableListOf()) }
    val runnerLeaderBoardList: MutableLiveData<List<LeaderboardItem>> =
        MutableLiveData<List<LeaderboardItem>>().apply { postValue(mutableListOf()) }

    val totalScore: MutableLiveData<Long> = MutableLiveData<Long>().apply { postValue(0) }
    val waldoScore: MutableLiveData<Long> = MutableLiveData<Long>().apply { postValue(0) }
    val highestWaldoScore: MutableLiveData<Long> = MutableLiveData<Long>().apply { postValue(0) }

    fun getLeaderBoardForGame(game: Int) {
        RetrofitClient.buildService().getLeaderboardByGame(game)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ r ->
                when (game) {
                    0 -> waldoLeaderBoardList.postValue(r)
                    1 -> whackLeaderBoardList.postValue(r)
                    2 -> runnerLeaderBoardList.postValue(r)
                }
            }, { t ->

            })

    }

    fun getLeaderBoardByUser(address: String) {
        RetrofitClient.buildService().getLeaderboardByUser(address)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ r ->
                leaderBoardListForUser.postValue(r)
                calculateTotalScore(r)
            }, { t ->

            })

    }

    fun calculateTotalScore(list: List<LeaderboardItem>) {
        var total: Long = 0
        var waldoscore: Long = 0
        var highestScore: Long = 0
        if (list != null) {
            for (item: LeaderboardItem in list) {
                total += item.score
                if (item.game.equals(0)) {
                    waldoscore += item.score
                    if (item.score > highestScore) {
                        highestScore = item.score
                    }
                }
            }
            waldoScore.postValue(waldoscore)
            highestWaldoScore.postValue(highestScore)
            totalScore.postValue(total)
        }
    }
}