package com.flovatar.mobileapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.flovatar.mobileapp.R
import com.flovatar.mobileapp.adapter.ChoseAvatarAdapter
import com.flovatar.mobileapp.adapter.SmallAvatarAdapter
import com.flovatar.mobileapp.databinding.ActivityChoseAvatarBinding
import com.flovatar.mobileapp.eventbus.HideProgressEvent
import com.flovatar.mobileapp.model.AvatarModel
import com.flovatar.mobileapp.utils.PrefUtils
import com.flovatar.mobileapp.utils.PrefUtils.Companion.GAME_WALDO
import com.flovatar.mobileapp.view.*
import com.flovatar.mobileapp.viewmodel.AvatarsListViewModel
import org.greenrobot.eventbus.Subscribe
import java.util.*

class ChoosePlayerActivity :
    BaseActivity<ActivityChoseAvatarBinding>(ActivityChoseAvatarBinding::inflate) {
    private val viewModel: AvatarsListViewModel by viewModels()
    private var currentPage = 1
    private val paginationargs: PaginationArgs by lazy {
        PaginationArgs()
    }

    private val bigAvatarsAdapter: ChoseAvatarAdapter by lazy {
        ChoseAvatarAdapter(paginationargs)
    }
    private val bigAvatarLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
    }

    private val bigRecyclerViewOnScrollListener: RecyclerViewOnScrollListener by lazy {
        RecyclerViewOnScrollListener(paginationargs, this, viewModel)
    }

    private val smallRecyclerViewOnScrollListener: RecyclerViewOnScrollListener by lazy {
        RecyclerViewOnScrollListener(paginationargs, this, viewModel)
    }

    private val smallAvatarsAdapter: SmallAvatarAdapter by lazy {
        SmallAvatarAdapter(paginationargs, object : AvatarClickedListener {
            override fun onAvatarClicked(position: Int) {
                bigAvatarLayoutManager.scrollToPosition(position)
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showProgress()
        viewModel.getAvatarListByAddress(currentPage, PrefUtils.with(this).getUserToken()!!)
        viewModel.getAvatarList(currentPage)
        setupUI()
        setupData()
    }

    private fun setupUI() {
        setupBigAvatars()
        setupCarouselRecyclerView()
        binding.btnHome.setOnClickListener {
            startLoginActivity()
        }
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnPlay.setOnClickListener {
            val game = PrefUtils.with(this).getGameToOpen()
            if (game != null) {
                if (game.equals(GAME_WALDO)) {
                    val position = bigAvatarLayoutManager.findFirstVisibleItemPosition()
                    if (position >= 0) {
                        PrefUtils.with(this)
                            .setSelectedAvatar(viewModel.myAvatarsList.value?.get(position))
                        startWaldoGameActivity()
                    }
                }
            }
        }
    }

    fun startWaldoGameActivity() {
        val intent = Intent(this, WaldoGameActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun setupBigAvatars() {
        binding.recyclerViewBigAvatar.layoutManager = bigAvatarLayoutManager
        binding.recyclerViewBigAvatar.adapter = bigAvatarsAdapter
        binding.recyclerViewBigAvatar.setOnFlingListener(null)
        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerViewBigAvatar)
        binding.recyclerViewBigAvatar.addOnScrollListener(bigRecyclerViewOnScrollListener)
    }

    private fun setupCarouselRecyclerView() {
        binding.recyclerCarousel.adapter = smallAvatarsAdapter
        binding.recyclerCarousel.set3DItem(true)
        binding.recyclerCarousel.setIntervalRatio(0.6f)
        binding.recyclerCarousel.setAlpha(true)
        binding.recyclerCarousel.addOnScrollListener(smallRecyclerViewOnScrollListener)
    }

    private fun setupData() {
        viewModel.myAvatarsList.observe(this, Observer {
            bigAvatarsAdapter.submitList(it)
            smallAvatarsAdapter.submitList(it)
        })
        viewModel.totalAvatarsList.observe(this, Observer {
            if (it.size > 0) {
                for (model: AvatarModel in it) {
                    model.createDrawable()
                }
                if (viewModel.myAvatarsList.value?.isNullOrEmpty() == true) {
                    val list = it
                    Collections.shuffle(list)
                    viewModel.myAvatarsList.postValue(list.take(10) as MutableList<AvatarModel>?)
                }
              //  PrefUtils.with(this).saveAvatarsList(it)
                hideProgress()
            }
        })
        viewModel.totalPage.observe(this, {
            paginationargs.totalPage = it
        })
        viewModel.currentPage.observe(this, {
            paginationargs.currentPage = it
        })
    }

    @Subscribe
    fun hideProgressEvent(event: HideProgressEvent) {
        //  hideProgress()
    }

    override fun getLayoutRes(): Int {
        return R.layout.activity_chose_avatar
    }

    override fun getViewBinding(): ActivityChoseAvatarBinding {
        return ActivityChoseAvatarBinding.inflate(layoutInflater)
    }
}