package com.flovatar.mobileapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.text.TextUtils
import com.flovatar.mobileapp.model.AvatarModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

class PrefUtils {


    companion object {
        private var singleton: PrefUtils? = null
        private lateinit var preferences: SharedPreferences
        private lateinit var editor: SharedPreferences.Editor
        private var gsonConverter: Gson? = null
        const val KEY_USER_TOKEN: String = "user token"
        const val KEY_USER_TOTAL_SCORE: String = "user score"
        const val KEY_AVATAR_LIST: String = "avatar list"
        const val KEY_WALDO_SCORE: String = "waldo score"
        const val KEY_WHACK_SCORE: String = "whack score"
        const val KEY_RUNNER_SCORE: String = "runner score"
        const val KEY_GAME_TO_OPEN: String = "game to open"
        const val GAME_WALDO: String = "Waldo"
        const val KEY_SELECTED_AVATAR: String = "selected avatar"
        const val KEY_IS_NEED_TO_SAVE_SCORE: String = "is need to save score"
        fun with(context: Context): PrefUtils {
            if (null == singleton)
                singleton = Builder(context).build()
            return singleton as PrefUtils
        }

    }

    private class Builder(val context: Context) {

        fun build(): PrefUtils {
            return PrefUtils(context)
        }
    }

    constructor()

    @SuppressLint("CommitPrefEdits")
    constructor(context: Context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
        editor = preferences.edit()
        gsonConverter = GsonBuilder().create()
    }

    fun setUserToken(token: String) {
        save(KEY_USER_TOKEN, token)
    }

    fun getUserToken(): String? {
        return getString(KEY_USER_TOKEN, null)
    }

    fun saveScore(scoreType: String, score: Long) {
        save(scoreType, score)
    }

    fun saveWaldoScore(score: Long) {
        val result = getWaldoScore() + score
        saveScore(KEY_WALDO_SCORE, result)
        saveTotalScore(score)
    }

    fun saveWaldoScoreWithoutAdding(score: Long) {
        saveScore(KEY_WALDO_SCORE, score)
    }

    fun saveTotalScore(score: Long) {
        val result = getTotalScore() + score
        saveScore(KEY_USER_TOTAL_SCORE, result)
    }

    fun saveTotalScoreWithoutAdding(score: Long) {
        saveScore(KEY_USER_TOTAL_SCORE, score)
    }

    fun saveWhackScore(score: Long) {
        val result = getWhackScore() + score
        saveScore(KEY_WHACK_SCORE, result)
        saveTotalScore(score)
    }

    fun saveRunnerScore(score: Long) {
        val result = getRunnerScore() + score
        saveScore(KEY_RUNNER_SCORE, result)
        saveTotalScore(score)
    }

    fun saveAvatarsList(list: MutableList<AvatarModel>) {
        for (model: AvatarModel in list) {
            model.createDrawable()
        }
        save(KEY_AVATAR_LIST, gsonConverter?.toJson(list))
    }

    fun addToAvatarsList(model: AvatarModel) {
        var list = getAvatarList()
        if (list == null) {
            list = mutableListOf()
        }
        list.add(model)
        save(KEY_AVATAR_LIST, gsonConverter?.toJson(list))
    }

    fun getAvatarList(): MutableList<AvatarModel>? {
        val tags = preferences.getString(KEY_AVATAR_LIST, null)
        if (TextUtils.isEmpty(tags)) {
            return mutableListOf()
        }

        val type = object : TypeToken<MutableList<AvatarModel?>?>() {}.type
        return gsonConverter?.fromJson<MutableList<AvatarModel>>(tags, type)
    }

    fun setSelectedAvatar(user: AvatarModel?) {
        preferences.edit()
            .putString(KEY_SELECTED_AVATAR, Gson().toJson(user)).apply()
    }

    fun getSelectedAvatar(): AvatarModel? {
        val userStr = preferences.getString(KEY_SELECTED_AVATAR, null)
        return if (userStr != null) Gson().fromJson(userStr, AvatarModel::class.java) else null
    }

    fun getTotalScore(): Long {
        return getLong(KEY_USER_TOTAL_SCORE, 0)
    }

    fun getWaldoScore(): Long {
        return getLong(KEY_WALDO_SCORE, 0)
    }

    fun getWhackScore(): Long {
        return getLong(KEY_WHACK_SCORE, 0)
    }

    fun getRunnerScore(): Long {
        return getLong(KEY_RUNNER_SCORE, 0)
    }

    fun getGameToOpen(): String? {
        return getString(KEY_GAME_TO_OPEN, null)
    }

    fun setGameToOpen(game: String) {
        save(KEY_GAME_TO_OPEN, game)
    }

    fun setIsNeedToSendScore(value: Boolean) {
        save(KEY_IS_NEED_TO_SAVE_SCORE, value)
    }

    fun isNeedToSendScore(): Boolean {
        return getBoolean(KEY_IS_NEED_TO_SAVE_SCORE, false)
    }

    fun save(key: String, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }

    fun save(key: String, value: Float) {
        editor.putFloat(key, value).apply()
    }

    fun save(key: String, value: Int) {
        editor.putInt(key, value).apply()
    }

    fun save(key: String, value: Long) {
        editor.putLong(key, value).apply()
    }

    fun save(key: String, value: String?) {
        editor.putString(key, value).apply()
    }

    fun save(key: String, value: Set<String>) {
        editor.putStringSet(key, value).apply()
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return preferences.getBoolean(key, defValue)
    }

    fun getFloat(key: String, defValue: Float): Float {
        return try {
            preferences.getFloat(key, defValue)
        } catch (ex: ClassCastException) {
            preferences.getString(key, defValue.toString())!!.toFloat()
        }
    }

    fun getInt(key: String, defValue: Int): Int {
        return try {
            preferences.getInt(key, defValue)
        } catch (ex: ClassCastException) {
            preferences.getString(key, defValue.toString())!!.toInt()
        }
    }

    fun getLong(key: String, defValue: Long): Long {
        return try {
            preferences.getLong(key, defValue)
        } catch (ex: ClassCastException) {
            preferences.getString(key, defValue.toString())!!.toLong()
        }
    }

    fun getString(key: String, defValue: String?): String? {
        return preferences.getString(key, defValue)
    }

    fun getStringSet(key: String, defValue: Set<String>): Set<String>? {
        return preferences.getStringSet(key, defValue)
    }

    fun getAll(): MutableMap<String, *>? {
        return preferences.all
    }

    fun remove(key: String) {
        editor.remove(key).apply()
    }

    fun clear() {
        if (editor != null) {
            val list = getAvatarList()
            preferences.edit().clear().apply()
            if (list != null) {
                saveAvatarsList(list)
            }
        }
    }


}