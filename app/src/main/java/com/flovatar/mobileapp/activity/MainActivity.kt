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
import com.flovatar.mobileapp.activity.LoginActivity.Companion.CASE_USER
import com.flovatar.mobileapp.activity.LoginActivity.Companion.KEY_CASE
import com.flovatar.mobileapp.adapter.BigAvatarAdapter
import com.flovatar.mobileapp.adapter.SmallAvatarAdapter
import com.flovatar.mobileapp.databinding.ActivityMainBinding
import com.flovatar.mobileapp.eventbus.HideProgressEvent
import com.flovatar.mobileapp.utils.PrefUtils
import com.flovatar.mobileapp.view.AvatarClickedListener
import com.flovatar.mobileapp.view.CarouselRecyclerViewOnScrollListener
import com.flovatar.mobileapp.view.PaginationArgs
import com.flovatar.mobileapp.view.RecyclerViewOnScrollListener
import com.flovatar.mobileapp.viewmodel.AvatarsListViewModel
import org.greenrobot.eventbus.Subscribe


class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private var case: Int = 0
    private var isMenuOpen = false
    private val viewModel: AvatarsListViewModel by viewModels()
    private val paginationargs: PaginationArgs by lazy {
        PaginationArgs()
    }
    private val smallRecyclerViewOnScrollListener: CarouselRecyclerViewOnScrollListener by lazy {
        CarouselRecyclerViewOnScrollListener(paginationargs, this, viewModel)
    }
    private val bigRecyclerViewOnScrollListener: RecyclerViewOnScrollListener by lazy {
        RecyclerViewOnScrollListener(paginationargs, this, viewModel)
    }
    private val bigAvatarsAdapter: BigAvatarAdapter by lazy {
        BigAvatarAdapter(paginationargs)
    }
    private val bigAvatarLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
    }
    private val smallAvatarsAdapter: SmallAvatarAdapter by lazy {
        SmallAvatarAdapter(paginationargs, object : AvatarClickedListener {
            override fun onAvatarClicked(position: Int) {
                bigAvatarLayoutManager.scrollToPosition(position)
            }
        })
    }
    private val smallAvatarLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
    }


    private var currentPage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // viewModel.getRandomAvatars()
        if (intent.extras != null) {
            val extrasCase = intent.extras?.getInt(KEY_CASE)
            case =
                if (extrasCase != null) extrasCase else 0
        }
        showProgress()
        if (case == CASE_USER) {
            bigRecyclerViewOnScrollListener.setIsPublic(false)
            smallRecyclerViewOnScrollListener.setPublic(false)
            viewModel.getAvatarListByAddress(currentPage, PrefUtils.with(this).getUserToken()!!)
        } else {
            bigRecyclerViewOnScrollListener.setIsPublic(true)
            smallRecyclerViewOnScrollListener.setPublic(true)
            viewModel.getAvatarList(currentPage)
        }
        setupUI()
        setupData()
    }

    private fun setupData() {
        viewModel.avatarsList.observe(this, Observer {
            bigAvatarsAdapter.submitList(it)
            smallAvatarsAdapter.submitList(it)
        })
        viewModel.totalPage.observe(this, {
            paginationargs.totalPage = it
        })
        viewModel.currentPage.observe(this, {
           // paginationargs.currentPage = it
        })
    }

    private fun setupUI() {
        setupBigAvatars()
        setupSmallAvatars()
        binding.shimmerViewContainer.startShimmer()
        binding.btnClose.setOnClickListener {
            onBackPressed()
        }
    }

    fun startWaldoGameActivity() {
        val intent = Intent(this, WaldoGameActivity::class.java)
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

    private fun setupSmallAvatars() {
        binding.recyclerViewSmallAvatar.adapter = smallAvatarsAdapter
        binding.recyclerViewSmallAvatar.set3DItem(true)
        binding.recyclerViewSmallAvatar.setIntervalRatio(0.6f)
        binding.recyclerViewSmallAvatar.setAlpha(true)
        binding.recyclerViewSmallAvatar.addOnScrollListener(smallRecyclerViewOnScrollListener)
    }

    fun startLoginActivity() {
        PrefUtils.with(this).clear()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    @Subscribe
    fun hideProgressEvent(event: HideProgressEvent) {
        hideProgress()
    }

    override fun getLayoutRes(): Int {
        return R.layout.activity_main
    }

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }
}