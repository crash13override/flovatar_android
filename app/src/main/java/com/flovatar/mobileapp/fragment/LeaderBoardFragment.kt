package com.flovatar.mobileapp.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.flovatar.mobileapp.R
import com.flovatar.mobileapp.adapter.LeaderboardAdapter
import com.flovatar.mobileapp.databinding.FragmentLeaderboardBinding
import com.flovatar.mobileapp.viewmodel.LeaderboardViewModel

class LeaderBoardFragment : BaseFragment<FragmentLeaderboardBinding>() {
    val viewModel: LeaderboardViewModel by activityViewModels()
    val adapter: LeaderboardAdapter by lazy {
        LeaderboardAdapter()
    }
    var gameType: Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.getInt("KEY_GAME")?.let {
            gameType = it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (gameType) {
            0 -> viewModel.waldoLeaderBoardList.observe(this, Observer {
                adapter.submitList(it)
                hideProgress()
            })
            1 -> viewModel.whackLeaderBoardList.observe(this, Observer {
                adapter.submitList(it)
                hideProgress()
            })
            2 -> viewModel.runnerLeaderBoardList.observe(this, Observer {
                adapter.submitList(it)
                hideProgress()
            })
        }

        binding.recyclerview.layoutManager = LinearLayoutManager(context)
        binding.recyclerview.adapter = adapter
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_leaderboard
    }

    override fun getViewBinding(): FragmentLeaderboardBinding {
        return FragmentLeaderboardBinding.inflate(layoutInflater)
    }

    companion object {
        @JvmStatic
        fun newInstance(gameType: Int) = LeaderBoardFragment().apply {
            arguments = Bundle().apply {
                putInt("KEY_GAME", gameType)
            }
        }
    }

}