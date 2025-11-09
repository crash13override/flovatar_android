package com.flovatar.mobileapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import com.flovatar.mobileapp.R
import com.flovatar.mobileapp.databinding.ActivityMiniGamesUnauthorizedBinding
import com.flovatar.mobileapp.utils.PrefUtils
import com.flovatar.mobileapp.utils.PrefUtils.Companion.GAME_WALDO

class UnAuthorizedMiniGamesActivity :
    BaseActivity<ActivityMiniGamesUnauthorizedBinding>(ActivityMiniGamesUnauthorizedBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (PrefUtils.with(this).getUserToken() != null) {
            binding.layoutLogin.isVisible = false
        } else {
            binding.layoutLogin.isVisible = true
        }
        binding.btnClose.setOnClickListener {
            onBackPressed()
        }

        binding.btnPlayWaldo.setOnClickListener {
            PrefUtils.with(this).setGameToOpen(GAME_WALDO)
            startLoginActivity()
        }

        binding.btnPlayRunner.setOnClickListener {
            showToast(R.string.coming_soon)
        }

        binding.btnPlayWhack.setOnClickListener {
            showToast(R.string.coming_soon)
        }

        binding.layoutLogin.setOnClickListener {
            startLoginActivity()
        }
    }

    fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }


    override fun getLayoutRes(): Int {
        return R.layout.activity_mini_games_unauthorized
    }

    override fun getViewBinding(): ActivityMiniGamesUnauthorizedBinding {
        return ActivityMiniGamesUnauthorizedBinding.inflate(layoutInflater)
    }
}