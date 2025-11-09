package com.flovatar.mobileapp.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import coil.util.CoilUtils
import com.flovatar.mobileapp.api.RetrofitClient
import com.flovatar.mobileapp.eventbus.HideProgressEvent
import com.flovatar.mobileapp.eventbus.SaveListEvent
import com.flovatar.mobileapp.model.AvatarListResponse
import com.flovatar.mobileapp.model.AvatarModel
import com.flovatar.mobileapp.model.LeaderboardItem
import com.flovatar.mobileapp.utils.PrefUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import org.greenrobot.eventbus.EventBus
import kotlin.random.Random

class AvatarsListViewModel : ViewModel() {
    val avatarsList: MutableLiveData<MutableList<AvatarModel>> =
        MutableLiveData<MutableList<AvatarModel>>().apply { postValue(mutableListOf()) }
    val totalAvatarsList: MutableLiveData<MutableList<AvatarModel>> =
        MutableLiveData<MutableList<AvatarModel>>().apply { postValue(mutableListOf()) }
    val myAvatarsList: MutableLiveData<MutableList<AvatarModel>> =
        MutableLiveData<MutableList<AvatarModel>>().apply { postValue(mutableListOf()) }
    val totalPage: MutableLiveData<Int> = MutableLiveData<Int>().apply { postValue(1) }
    val currentPage: MutableLiveData<Int> = MutableLiveData<Int>().apply { postValue(1) }

    val randomAvatarsList: MutableLiveData<MutableList<AvatarModel>> =
        MutableLiveData<MutableList<AvatarModel>>().apply { postValue(mutableListOf()) }
    val totalScore: MutableLiveData<Long> = MutableLiveData<Long>().apply { postValue(0) }

    val leaderBoardListForUser: MutableLiveData<List<LeaderboardItem>> =
        MutableLiveData<List<LeaderboardItem>>().apply { postValue(mutableListOf()) }

    fun getAvatarList(page: Int) {
        val runBlocking = runBlocking {
            async {
                RetrofitClient.buildService().getAvatars(page)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribeWith(object : SingleObserver<AvatarListResponse> {
                        override fun onSubscribe(d: Disposable) {

                        }

                        override fun onSuccess(t: AvatarListResponse) {
                            totalAvatarsList.postValue(t.data as MutableList<AvatarModel>?)
                            updateList(t, false)
                        }

                        override fun onError(e: Throwable) {
                            EventBus.getDefault().post(HideProgressEvent())
                        }

                    })
            }
        }
    }

    fun getRandomAvatars() {
        val listToCreate = mutableListOf<AvatarModel>()
        viewModelScope.executeAsyncTask(onPreExecute = {

        }, doInBackground = {
            for (i in 1..200) {
                val url = "https://flovatar.com/api/image/nobg/".plus(i.toString())
                RetrofitClient.buildService().getRandomAvatar(url)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ r ->
                        val model = AvatarModel(0, "", "", r.string(), 0, 0, 0, "")
                        listToCreate.add(model)
                        if (i == 100) {
                            createBitmaps(listToCreate)
                        }
                    }, { t ->

                    })
            }
        }, onPostExecute = {
        })
    }

    fun createBitmaps(list: List<AvatarModel>) {
        viewModelScope.executeAsyncTask(onPreExecute = {
        }, doInBackground = {
            try {
                for (model: AvatarModel in list) {
                    model.createDrawable()
                }
            } catch (e: Exception) {

            }
        }, onPostExecute = {
            if (list.size > 0) {
                EventBus.getDefault().post(SaveListEvent(list))
            }
        })
    }

    fun <R> CoroutineScope.executeAsyncTask(
        onPreExecute: () -> Unit,
        doInBackground: () -> R,
        onPostExecute: (R) -> Unit
    ) = launch {
        onPreExecute()
        val result =
            withContext(Dispatchers.IO) { // runs in background thread without blocking the Main Thread
                doInBackground()
            }
        onPostExecute(result)
    }

    fun getAvatarListByAddress(page: Int, token: String) {
        val url: String =
            "https://flovatar.com/collection/api/".plus(token).plus("?page=").plus(page)
        RetrofitClient.buildService().getAvatarsByAddress(url)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeWith(object : SingleObserver<AvatarListResponse> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onSuccess(t: AvatarListResponse) {
                    myAvatarsList.postValue(t.data as MutableList<AvatarModel>?)
                    updateList(t, true)
                }

                override fun onError(e: Throwable) {
                    EventBus.getDefault().post(HideProgressEvent())
                }

            })
    }

    fun updateList(apiResponse: AvatarListResponse, isMine: Boolean) {
        if (apiResponse.data.size == 0) {
            EventBus.getDefault().post(HideProgressEvent())
        }
        totalPage.postValue(apiResponse.lastPage)
        currentPage.postValue(apiResponse.currentPage)
        val existingList: MutableList<AvatarModel>? = avatarsList.value
        if (existingList != null) {
            if (existingList.size == 0) {
                existingList.addAll(apiResponse.data)
            } else {
                for (model: AvatarModel in apiResponse.data) {
                    if (!isAvatarExistInList(model)) {
                        existingList.add(model)
                    }
                }
            }
            avatarsList.postValue(existingList)
        } else {
            avatarsList.postValue(apiResponse.data as MutableList<AvatarModel>?)
        }
        //totalAvatarsList.value?.let { createBitmaps(it.take(50)) }
    }

    fun isAvatarExistInList(model: AvatarModel): Boolean {
        val list: List<AvatarModel>? = avatarsList.value
        if (list != null) {
            for (avatar: AvatarModel in list) {
                if (avatar.flowId.equals(model.flowId)) {
                    return true
                }
            }
        }
        return false
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
        if (list != null) {
            for (item: LeaderboardItem in list) {
                total += item.score
            }
            totalScore.postValue(total)
        }
    }

}