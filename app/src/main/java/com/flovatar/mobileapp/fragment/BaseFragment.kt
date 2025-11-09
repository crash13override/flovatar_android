package com.flovatar.mobileapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.flovatar.mobileapp.activity.BaseActivity

abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    lateinit var binding: VB
    protected abstract fun getLayoutRes(): Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getViewBinding()
        val view: View = binding.root
        return view
    }

    abstract fun getViewBinding(): VB

    protected fun showToast(@StringRes stringRes: Int) {
        Toast.makeText(context, stringRes, Toast.LENGTH_LONG).show()
    }

    protected fun showToast(stringRes: String?) {
        Toast.makeText(context, stringRes, Toast.LENGTH_LONG).show()
    }

    protected fun showProgress() {
        val activity: BaseActivity<*>? = activity as BaseActivity<*>?
        if (activity != null) activity.showProgress()
    }

    protected fun hideProgress() {
        val activity: BaseActivity<*>? = activity as BaseActivity<*>?
        if (activity != null) activity.hideProgress()
    }
}
