package com.mulkkam.ui.settingaccountinfo

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.activityViewModels
import com.mulkkam.R
import com.mulkkam.databinding.FragmentAccountDeleteDialogBinding
import com.mulkkam.ui.util.binding.BindingDialogFragment
import com.mulkkam.ui.util.extensions.getColoredSpannable
import com.mulkkam.ui.util.extensions.setSingleClickListener

class AccountDeleteDialogFragment :
    BindingDialogFragment<FragmentAccountDeleteDialogBinding>(
        FragmentAccountDeleteDialogBinding::inflate,
    ) {
    private val parentViewModel: SettingAccountInfoViewModel by activityViewModels()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initGreetingHighlight()
        setWindowOptions()
        initClickListeners()
    }

    private fun initGreetingHighlight() {
        binding.tvWarning.text =
            getString(R.string.setting_account_info_delete_description).getColoredSpannable(
                requireContext(),
                R.color.secondary_200,
                getString(R.string.setting_account_info_delete_description_highlight),
            )
    }

    private fun setWindowOptions() {
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * DIALOG_WIDTH_RATIO).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )

        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
    }

    private fun initClickListeners() {
        binding.tvConfirm.setSingleClickListener {
            parentViewModel.deleteAccount()
        }

        binding.tvCancel.setSingleClickListener {
            dismiss()
        }
    }

    companion object {
        private const val DIALOG_WIDTH_RATIO: Float = 0.85f
    }
}
