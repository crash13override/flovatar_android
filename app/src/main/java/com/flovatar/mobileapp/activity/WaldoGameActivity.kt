package com.flovatar.mobileapp.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.flovatar.mobileapp.R
import com.flovatar.mobileapp.adapter.WaldoGameAvatarAdapter
import com.flovatar.mobileapp.databinding.ActivityWaldoGameBinding
import com.flovatar.mobileapp.eventbus.SaveListEvent
import com.flovatar.mobileapp.model.AvatarModel
import com.flovatar.mobileapp.utils.PrefUtils
import com.flovatar.mobileapp.view.AvatarClickedListener
import com.flovatar.mobileapp.viewmodel.AvatarsListViewModel
import com.flovatar.mobileapp.viewmodel.WaldoViewModel
import org.greenrobot.eventbus.Subscribe
import java.util.*
import kotlin.random.Random

class WaldoGameActivity :
    BaseActivity<ActivityWaldoGameBinding>(ActivityWaldoGameBinding::inflate) {
    private var score: Int = 0
    val viewModel: WaldoViewModel by viewModels()
    val avatarViewModel: AvatarsListViewModel by viewModels()
    private var loadingCounter: CountDownTimer? = null
    private var resultList: MutableList<AvatarModel> = mutableListOf()
    private var timeCounter: CountDownTimer? = null
    private var adapter: WaldoGameAvatarAdapter? = null
    private var totalCount = 0
    private var spanCount = 7
    private var timeCount: Long = 0
    private var currentLevel = 1
    private var currentGameScore: Long = 0
    private var lastClickTime: Long = 0
    private var stoppedTime: Long = 0
    private var isLoadingTimerCounting: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupData()
        val list = PrefUtils.with(this).getAvatarList()
        val listSize = if (list == null) 0 else list.size
        val maxSize = 13 * 14
        if (listSize < maxSize) {
            if (listSize > 0) {
                val times = maxSize / listSize
                for (i in 1..times + 1) {
                    resultList.addAll(list as MutableList<AvatarModel>)
                }
            }
        } else {
            resultList.addAll(list as MutableList<AvatarModel>)
        }
        setupWaldoPosition()
        setupButtons()
        startLoadingCounter()
    }

    @Subscribe
    fun saveListAvatars(event: SaveListEvent) {
        val list = event.list
        val listSize = if (list == null) 0 else list.size
        val maxSize = 13 * 14
        if (listSize < maxSize) {
            if (listSize > 0) {
                val times = maxSize / listSize
                for (i in 1..times + 1) {
                    resultList.addAll(list as MutableList<AvatarModel>)
                }
            }
        } else {
            resultList.addAll(list as MutableList<AvatarModel>)
        }
        setupWaldoPosition()
        PrefUtils.with(this).saveAvatarsList(event.list as MutableList<AvatarModel>)
    }

    private fun calculateScore(seconds_remaining: Float): Int {
        val timeTotal = timeCount / 1000
        val timeCoeficcient: Float = seconds_remaining / timeTotal.toFloat()
        val levelCoeficcient: Float = currentLevel.toFloat() / 5
        val score: Int =
            100 + (100 * timeCoeficcient * levelCoeficcient).toInt()
        return score
    }

    private fun setupWaldoPosition() {
        try {
            val listToAdd: MutableList<AvatarModel> =
                resultList.take(totalCount - 1) as MutableList<AvatarModel>
            Collections.shuffle(listToAdd)
            val waldo = AvatarModel(
                4548,
                "0x2a0eccae942667be",
                "",
                R.drawable.ic_waldo.toString(),
                1,
                0,
                0,
                null
            )
            val position = Random.nextInt(0, totalCount)
            listToAdd.add(position, waldo)
            adapter?.submitList(currentLevel, listToAdd)
        } catch (e: Exception) {

        }
    }

    fun startLoadingCounter() {
        isLoadingTimerCounting = true
        binding.layoutLoading.isVisible = true
        binding.circularProgressbarLoading.setProgress(0)
        binding.secondsLoading.setText("3")
        loadingCounter = object : CountDownTimer(4000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.secondsLoading.setText((millisUntilFinished / 1000).toString())
                binding.circularProgressbarLoading.setProgress(4000 - millisUntilFinished.toInt())
            }

            override fun onFinish() {
                binding.layoutLoading.isVisible = false
                setupCounter()
                cancel()
                isLoadingTimerCounting = false
            }
        }.start()
    }

    private fun setupData() {
        when (currentLevel) {
            1, 2 -> setupDataForLevel(7, 7 * 8, 61000)
            3, 4 -> setupDataForLevel(8, 8 * 9, 46000)
            5, 6 -> setupDataForLevel(9, 9 * 10, 31000)
            7, 8 -> setupDataForLevel(10, 10 * 11, 21000)
            9, 10 -> setupDataForLevel(11, 11 * 12, 16000)
            11, 12 -> setupDataForLevel(12, 12 * 13, 11000)
            else -> {
                setupDataForLevel(13, 13 * 14, 11000)
            }
        }
    }


    private fun setupUI() {
        binding.recyclerview.setHasFixedSize(true)
        binding.circularProgressbar.max = timeCount.toInt()
        binding.circularProgressbar.secondaryProgress = timeCount.toInt()

        adapter = WaldoGameAvatarAdapter(object : AvatarClickedListener {
            override fun onAvatarClicked(flowId: Int) {
                if (isLoadingTimerCounting) {
                    return
                }
                if (SystemClock.elapsedRealtime() - lastClickTime < 500) {
                    return
                }
                lastClickTime = SystemClock.elapsedRealtime()
                if (flowId.equals(4548)) {
                    updatelevel()
                    timeCounter?.cancel()
                    binding.layoutWin.isVisible = true
                    score = calculateScore(binding.seconds.text.toString().toFloat())
                    currentGameScore += score
                    binding.gameScore.text = currentGameScore.toString()
                    setScore(true)
                } else {
                    binding.layoutTimesUp.isVisible = true
                    setScore(false)
                    timeCounter?.cancel()
                }
            }
        })
        binding.recyclerview.layoutManager = GridLayoutManager(this, spanCount)
        binding.recyclerview.adapter = adapter
    }

    private fun setupDataForLevel(spanCount: Int, totalCount: Int, timeCount: Long) {
        this.spanCount = spanCount
        this.totalCount = totalCount
        this.timeCount = timeCount
    }

    fun setupCounter() {
        binding.circularProgressbar.max = timeCount.toInt()
        binding.circularProgressbar.secondaryProgress = timeCount.toInt()
        var time: Long = 0;
        if (stoppedTime == 0L) {
            time = timeCount
        } else {
            time = stoppedTime
        }
        timeCounter = object : CountDownTimer(time, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.seconds.setText((millisUntilFinished / 1000).toString())
                binding.circularProgressbar.setProgress(timeCount.toInt() - millisUntilFinished.toInt())
            }

            override fun onFinish() {
                binding.layoutTimesUp.isVisible = true
                setScore(false)
                cancel()
            }
        }.start()
    }

    private fun setupButtons() {
        binding.btnHome.setOnClickListener {
            hideKeyboard()
            startLoadingCounter()
        }
        binding.btnHomeEnd.setOnClickListener {
            hideKeyboard()
            startLoginActivity()
        }

        binding.btnHomeWin.setOnClickListener {
            hideKeyboard()
            startLoginActivity()
        }
        binding.btnSaveScore.setOnClickListener {
            val name = binding.etName.text.toString()
            if (TextUtils.isEmpty(name)) {
                showToast(R.string.error_enter_name)
            } else {
                onBackPressed()
                viewModel.saveScore(
                    0,
                    name,
                    PrefUtils.with(this@WaldoGameActivity).getUserToken()!!,
                    currentGameScore.toInt()
                )
            }
        }

        binding.btnPlayAgainWin.setOnClickListener {
            hideKeyboard()
            clearViews()
            startLoadingCounter()
        }

        binding.btnPlayAgain.setOnClickListener {
            hideKeyboard()
            restartGame()
            clearViews()
            startLoadingCounter()
        }

        binding.btnPause.setOnClickListener {
            binding.layoutPaused.isVisible = true
            stoppedTime = binding.seconds.text.toString().toLong() * 1000
            timeCounter?.cancel()
        }

        binding.btnResume.setOnClickListener {
            binding.layoutPaused.isVisible = false
            setupCounter()
        }

        binding.btnQuit.setOnClickListener {
            onBackPressed()
        }

        binding.btnRestart.setOnClickListener {
            setupWaldoPosition()
            clearViews()
            startLoadingCounter()
        }
    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    fun restartGame() {
        currentLevel = 1
        currentGameScore = 0
        score = 0
        binding.gameScore.setText("0")
        setupData()
        binding.recyclerview.layoutManager = GridLayoutManager(this, spanCount)
        binding.recyclerview.adapter = adapter
        setupWaldoPosition()
    }

    private fun updatelevel() {
        currentLevel++
        setupData()
        binding.recyclerview.layoutManager = GridLayoutManager(this, spanCount)
        binding.recyclerview.adapter = adapter
        setupWaldoPosition()
    }

    private fun clearViews() {
        stoppedTime = 0
        binding.layotScoreWin.removeAllViews()
        binding.layotScore.removeAllViews()
        binding.layoutLoading.isVisible = true
        binding.layoutWin.isVisible = false
        binding.layoutPaused.isVisible = false
        binding.layoutTimesUp.isVisible = false
    }

    private fun setScore(isWin: Boolean) {
        val scoreText = currentGameScore.toString()
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
            if (isWin) {
                binding.layotScoreWin.addView(view)
            } else {
                binding.layotScore.addView(view)
            }
        }

    }


    override fun getLayoutRes(): Int {
        return R.layout.activity_waldo_game
    }

    override fun getViewBinding(): ActivityWaldoGameBinding {
        return ActivityWaldoGameBinding.inflate(layoutInflater)
    }
}