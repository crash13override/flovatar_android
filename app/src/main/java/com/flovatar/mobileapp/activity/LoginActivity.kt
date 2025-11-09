package com.flovatar.mobileapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.flovatar.mobileapp.R
import com.flovatar.mobileapp.databinding.ActivityLoginBinding
import com.flovatar.mobileapp.dialogs.LogoutDialog
import com.flovatar.mobileapp.eventbus.LogoutEvent
import com.flovatar.mobileapp.utils.AvatarUtils
import com.flovatar.mobileapp.utils.PrefUtils
import com.flovatar.mobileapp.viewmodel.AvatarsListViewModel
import org.greenrobot.eventbus.Subscribe
import org.onflow.fcl.android.auth.AppInfo
import org.onflow.fcl.android.auth.DefaultProvider
import org.onflow.fcl.android.auth.FCL

import java.net.URL


class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {
    private var fcl: FCL = FCL(
        AppInfo(
            title = "Flovatar",
            icon = URL("https://flovatar.com/images/logo-square.png"),
        )
    )
    private val viewModel: AvatarsListViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val list = PrefUtils.with(this).getAvatarList()
        if (list == null || list.size <100) {
            viewModel.getRandomAvatars()
        }
        initBloctoLogin()
        initGameInfo()
        initBTNs()
    }

    private fun initBTNs() {
        binding.saveSwitch.isChecked = PrefUtils.with(this).isNeedToSendScore()
        binding.btnBrowse.setOnClickListener {
            startMainActivity(CASE_PUBLIC)
        }
        binding.saveSwitch.setOnCheckedChangeListener { compoundButton, b ->
            PrefUtils.with(this).setIsNeedToSendScore(b)
        }
        binding.btnPlayGames.setOnClickListener {
            startPlayGamesActivity()
        }

        binding.btnLogout.setOnClickListener {
            val dialog = LogoutDialog()
            dialog.show(supportFragmentManager, "")
        }
        binding.btnLoginBlocto.setOnClickListener {
            if (PrefUtils.with(this).getUserToken() == null) {
                fcl.authenticate(this, DefaultProvider.BLOCTO) { response ->
                    response.address?.let { it1 ->
                        PrefUtils.with(this).setUserToken(it1)
                        runOnUiThread {
                            val game = PrefUtils.with(this).getGameToOpen()
                            if (game == null) {
                                startMainActivity(CASE_USER)
                            } else {
                                startWaldoGameActivity()
                            }
                        }
                    }
                }
            } else {
                startMainActivity(CASE_USER)
            }
        }

    }

    private fun initGameInfo() {
        val avatarModel = PrefUtils.with(this).getSelectedAvatar()
        if (avatarModel != null) {
            val imageString = AvatarUtils.stringToBitMap(avatarModel.image)
            Glide.with(this)
                .load(imageString)
                .placeholder(R.drawable.ic_waldo)
                .into(binding.avatar)
        }
        viewModel.totalScore.observe(this, Observer {
            binding.totalScore.text = it.toString()
        })
    }

    private fun initBloctoLogin() {
        fcl.providers.add(DefaultProvider.BLOCTO)
    }

    fun startMainActivity(case: Int) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(Companion.KEY_CASE, case)
        startActivity(intent)
    }

    fun startPlayGamesActivity() {
        val intent = Intent(this, PlayGamesActivity::class.java)
        startActivity(intent)
    }

    fun startWaldoGameActivity() {
        val intent = Intent(this, WaldoGameActivity::class.java)
        startActivity(intent)
    }

    @Subscribe
    fun logoutEvent(event: LogoutEvent) {
        PrefUtils.with(this).clear()
        binding.btnLogout.isVisible = false
        binding.layoutUserInfo.isVisible = false
    }

    override fun onStart() {
        super.onStart()
        if (PrefUtils.with(this).getUserToken() == null) {
            binding.btnLogout.isVisible = false
            binding.layoutUserInfo.isVisible = false
        } else {
            viewModel.getLeaderBoardByUser(PrefUtils.with(this).getUserToken()!!)
            binding.btnLogout.isVisible = true
            binding.layoutUserInfo.isVisible = true
        }
        initGameInfo()
    }

    override fun getLayoutRes(): Int {
        return R.layout.activity_login
    }

    override fun getViewBinding(): ActivityLoginBinding {
        return ActivityLoginBinding.inflate(layoutInflater)
    }

    companion object {
        const val KEY_CASE: String = "case"
        const val CASE_PUBLIC: Int = 111
        const val CASE_USER: Int = 112
    }
}