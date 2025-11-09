package com.flovatar.mobileapp.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.flovatar.mobileapp.R
import com.flovatar.mobileapp.databinding.DialogLogoutBinding
import com.flovatar.mobileapp.eventbus.LogoutEvent
import org.greenrobot.eventbus.EventBus
import android.view.ViewGroup




class LogoutDialog() : DialogFragment(R.layout.dialog_logout) {

    private lateinit var binding: DialogLogoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
          }
    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams = dialog!!.window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent);

        dialog!!.window!!.attributes = params as WindowManager.LayoutParams
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogLogoutBinding.inflate(layoutInflater)
        return super.onCreateDialog(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = binding.root
        dialog?.setCancelable(false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogout.setOnClickListener {
            EventBus.getDefault().post(LogoutEvent())
            dismiss()
        }
        binding.btnNo.setOnClickListener {
            dismiss()
        }
    }
}