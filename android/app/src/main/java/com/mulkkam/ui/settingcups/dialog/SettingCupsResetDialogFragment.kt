package com.mulkkam.ui.settingcups.dialog

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.activityViewModels
import com.mulkkam.databinding.FragmentSettingCupResetBinding
import com.mulkkam.ui.settingcups.SettingCupsViewModel
import com.mulkkam.ui.util.binding.BindingDialogFragment
import com.mulkkam.ui.util.extensions.setSingleClickListener

class SettingCupsResetDialogFragment :
    BindingDialogFragment<FragmentSettingCupResetBinding>(
        FragmentSettingCupResetBinding::inflate,
    ) {
    private val parentViewModel: SettingCupsViewModel by activityViewModels()

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
        with(binding) {
            tvConfirm.setSingleClickListener {
                parentViewModel.resetCups()
                dismiss()
            }
            tvCancel.setSingleClickListener {
                dismiss()
            }
        }
    }

    companion object {
        const val TAG: String = "SETTING_CUPS_RESET_DIALOG_FRAGMENT"
        private const val DIALOG_WIDTH_RATIO: Float = 0.85f

        fun newInstance(): SettingCupsResetDialogFragment = SettingCupsResetDialogFragment()
    }
}
