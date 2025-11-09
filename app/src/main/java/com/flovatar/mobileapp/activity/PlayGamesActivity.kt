package com.flovatar.mobileapp.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.flovatar.mobileapp.R
import com.flovatar.mobileapp.databinding.ActivityPlayGamesBinding
import com.flovatar.mobileapp.utils.PrefUtils
import com.flovatar.mobileapp.viewmodel.LeaderboardViewModel


class PlayGamesActivity :
    BaseActivity<ActivityPlayGamesBinding>(ActivityPlayGamesBinding::inflate) {

    val viewModel: LeaderboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showProgress()
        viewModel.totalScore.observe(this, Observer {
            setScore()
        })
        val content = SpannableString(getString(R.string.show_leaderboard))
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        binding.btnShowLeaderbord.setText(content)

        binding.btnPlayGames.setOnClickListener {
            startMiniGamesActivity()
        }
        binding.btnShowLeaderbord.setOnClickListener {
            startLeaderboardActivity()
        }
        val list = PrefUtils.with(this).getAvatarList()
        if (list == null || list.size == 0) {
            val handler = Handler()
            handler.postDelayed(Runnable {
                hideProgress()
            }, 3000)
        }else{
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

    private fun setScore() {
        binding.layotScore.removeAllViews()
        val scoreText = viewModel.totalScore.value.toString()
        val scoreArray = scoreText.toCharArray()
        for (num: Char in scoreArray) {
            val view: View =
                LayoutInflater.from(this).inflate(R.layout.item_score_number, null)
            val textview: TextView = view.findViewById(R.id.score)
            val width = resources.getDimension(R.dimen.score_width)
            val height = resources.getDimension(R.dimen.score_height)
            val params = LinearLayout.LayoutParams(width.toInt(), height.toInt())
            params.setMargins(10, 0, 0, 0)
            view.layoutParams = params
            textview.text = num.toString()
            binding.layotScore.addView(view)
        }

    }

    fun startLoginActivity() {
        PrefUtils.with(this).clear()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    fun startMiniGamesActivity() {
        var intent: Intent? = null
        if (PrefUtils.with(this).getUserToken() == null) {
            intent = Intent(this, UnAuthorizedMiniGamesActivity::class.java)
        } else {
            intent = Intent(this, AuthorizedMiniGamesActivity::class.java)
        }
        startActivity(intent)
    }

    fun startLeaderboardActivity() {
        val intent = Intent(this, LeaderboardActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        setScore()
    }

    override fun getLayoutRes(): Int {
        return R.layout.activity_play_games
    }

    override fun getViewBinding(): ActivityPlayGamesBinding {
        return ActivityPlayGamesBinding.inflate(layoutInflater)
    }
}