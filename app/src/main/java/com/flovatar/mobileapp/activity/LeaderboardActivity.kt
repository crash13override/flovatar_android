package com.flovatar.mobileapp.activity

import android.os.Bundle
import androidx.activity.viewModels
import com.flovatar.mobileapp.R
import com.flovatar.mobileapp.adapter.TabAdapter
import com.flovatar.mobileapp.databinding.ActivityLeaderboardBinding
import com.flovatar.mobileapp.fragment.LeaderBoardFragment
import com.flovatar.mobileapp.viewmodel.LeaderboardViewModel

class LeaderboardActivity :
    BaseActivity<ActivityLeaderboardBinding>(ActivityLeaderboardBinding::inflate) {

    val viewModel: LeaderboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showProgress()
        viewModel.getLeaderBoardForGame(0)
        viewModel.getLeaderBoardForGame(1)
        viewModel.getLeaderBoardForGame(2)
        binding.btnClose.setOnClickListener {
            onBackPressed()
        }

        val adapter = TabAdapter(supportFragmentManager)
        adapter.addFragment(LeaderBoardFragment.newInstance(0), getString(R.string.where_waldo))
      //  adapter.addFragment(LeaderBoardFragment.newInstance(1), getString(R.string.whack_avatar))
        //adapter.addFragment(LeaderBoardFragment.newInstance(2), getString(R.string.flowatar_runner))

        binding.viewPager.setAdapter(adapter)
        binding.tabLayout.setupWithViewPager(binding.viewPager)

    }

    override fun getLayoutRes(): Int {
        return R.layout.activity_leaderboard
    }

    override fun getViewBinding(): ActivityLeaderboardBinding {
        return ActivityLeaderboardBinding.inflate(layoutInflater)
    }
}