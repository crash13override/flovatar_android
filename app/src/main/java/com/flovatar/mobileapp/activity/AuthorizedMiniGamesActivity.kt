package com.flovatar.mobileapp.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.flovatar.mobileapp.R
import com.flovatar.mobileapp.databinding.ActivityMiniGamesAuthorizedBinding
import com.flovatar.mobileapp.utils.PrefUtils
import com.flovatar.mobileapp.viewmodel.LeaderboardViewModel

class AuthorizedMiniGamesActivity :
    BaseActivity<ActivityMiniGamesAuthorizedBinding>(ActivityMiniGamesAuthorizedBinding::inflate) {

    val viewModel: LeaderboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBtns()
        showProgress()
        viewModel.highestWaldoScore.observe(this, Observer {
            binding.scoreWaldo.text = it.toString()
        })
        viewModel.totalScore.observe(this, Observer {
            setScore()
        })
        val list = PrefUtils.with(this).getAvatarList()
        if (list == null || list.size == 0) {
            val handler = Handler()
            handler.postDelayed(Runnable {
                hideProgress()
            }, 5000)
        } else {
            hideProgress()
        }
    }

    override fun onStart() {
        super.onStart()
        val address = PrefUtils.with(this).getUserToken()
        if (address != null) {
            viewModel.getLeaderBoardByUser(address)
        }
    }

    override fun onStop() {
        super.onStop()
        hideProgress()
    }

    fun setupBtns() {
        binding.btnHome.setOnClickListener {
            startLoginActivity()
        }
        binding.btnPlayRunner.setOnClickListener {
            showToast(R.string.coming_soon)
        }
        binding.btnPlayWhack.setOnClickListener {
            showToast(R.string.coming_soon)
        }

        binding.btnHome.setOnClickListener {
            startLoginActivity()
        }
        binding.btnPlayWaldo.setOnClickListener {
            startWaldoGameActivity()
        }
    }

    fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun setScore() {
        binding.layotScore.removeAllViews()
        val scoreText = viewModel.totalScore.value.toString()
        val scoreArray = scoreText.toCharArray()
        for (num: Char in scoreArray) {
            val view: View =
                LayoutInflater.from(this).inflate(R.layout.item_score_violet, null)
            val textview: TextView = view.findViewById(R.id.score)
            val width = resources.getDimension(R.dimen.score_width)
            val height = resources.getDimension(R.dimen.score_height)
            val params = LinearLayout.LayoutParams(width.toInt(), height.toInt())
            params.setMargins(10, 0, 0, 0)
            view.layoutParams = params
            textview.text = num.toString()
            binding.layotScore.addView(view)
        }

        binding.scoreRunner.text = "0"
        binding.scoreWaldo.text = viewModel.highestWaldoScore.value.toString()
        binding.scoreWhack.text = "0"
    }

    fun startWaldoGameActivity() {
        val intent = Intent(this, WaldoGameActivity::class.java)
        startActivity(intent)
    }


    fun startChoosePlayerActivity() {
        val intent = Intent(this, ChoosePlayerActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        setScore()
        binding.scoreWaldo.text = viewModel.waldoScore.value.toString()
        binding.scoreRunner.text = "0"
        binding.scoreWhack.text = "0"
    }

    override fun getLayoutRes(): Int {
        return R.layout.activity_mini_games_authorized
    }

    override fun getViewBinding(): ActivityMiniGamesAuthorizedBinding {
        return ActivityMiniGamesAuthorizedBinding.inflate(layoutInflater)
    }
}