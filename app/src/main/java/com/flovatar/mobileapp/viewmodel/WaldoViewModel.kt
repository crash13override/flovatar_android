package com.flovatar.mobileapp.viewmodel

import androidx.lifecycle.ViewModel
import com.flovatar.mobileapp.api.RetrofitClient
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class WaldoViewModel : ViewModel() {

    fun saveScore(game: Int, name: String, address: String, score: Int) {
        RetrofitClient.buildService().setScore(game, name, address, score)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ r ->

            }, { t ->

            })
    }
}