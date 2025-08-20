package com.mulkkam.ui.splash.dialog

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import com.mulkkam.R
import com.mulkkam.databinding.DialogFragmentAppUpdateBinding
import com.mulkkam.ui.util.binding.BindingDialogFragment
import com.mulkkam.ui.util.extensions.getColoredSpannable
import com.mulkkam.ui.util.extensions.setSingleClickListener

class AppUpdateDialogFragment :
    BindingDialogFragment<DialogFragmentAppUpdateBinding>(
        DialogFragmentAppUpdateBinding::inflate,
    ) {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setWindowOptions()
        initClickListeners()
        binding.tvTitle.text =
            getString(R.string.app_update_title).getColoredSpannable(
                requireContext(),
                R.color.primary_200,
                getString(R.string.app_update_title_highlighted),
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
            openPlayStoreAndExit()
        }
    }

    private fun openPlayStoreAndExit() {
        val appPackageName: String = requireContext().packageName

        val playStoreUri: Uri = getString(R.string.play_store_app, appPackageName).toUri()
        val webUri: Uri = getString(R.string.play_store_web, appPackageName).toUri()

        val playStoreIntent =
            Intent(Intent.ACTION_VIEW, playStoreUri).apply {
                setPackage(getString(R.string.play_store))
                addFlags(
                    Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK,
                )
            }
        val webIntent = Intent(Intent.ACTION_VIEW, webUri)

        runCatching { startActivity(playStoreIntent) }
            .recoverCatching { startActivity(webIntent) }

        requireActivity().finishAffinity()
        requireActivity().finishAndRemoveTask()
    }

    companion object {
        const val TAG: String = "APP_UPDATE_DIALOG_FRAGMENT"
        private const val DIALOG_WIDTH_RATIO: Float = 0.85f

        fun newInstance(): AppUpdateDialogFragment = AppUpdateDialogFragment()
    }
}
