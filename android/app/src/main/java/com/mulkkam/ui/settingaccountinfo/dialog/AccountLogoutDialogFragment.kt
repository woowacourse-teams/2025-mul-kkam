package com.mulkkam.ui.settingaccountinfo.dialog

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.activityViewModels
import com.mulkkam.databinding.FragmentAccountLogoutDialogBinding
import com.mulkkam.ui.settingaccountinfo.SettingAccountInfoViewModel
import com.mulkkam.ui.util.binding.BindingDialogFragment
import com.mulkkam.ui.util.extensions.setSingleClickListener

class AccountLogoutDialogFragment :
    BindingDialogFragment<FragmentAccountLogoutDialogBinding>(
        FragmentAccountLogoutDialogBinding::inflate,
    ) {
    private val parentViewModel: SettingAccountInfoViewModel by activityViewModels()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setWindowOptions()
        initClickListeners()
    }

    private fun setWindowOptions() {
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * DIALOG_WIDTH_RATIO).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )

        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
    }

    private fun initClickListeners() {
        binding.tvLogoutConfirm.setSingleClickListener {
            parentViewModel.logoutAccount()
        }

        binding.tvLogoutCancel.setSingleClickListener {
            dismiss()
        }
    }

    companion object {
        private const val DIALOG_WIDTH_RATIO: Float = 0.85f
    }
}
