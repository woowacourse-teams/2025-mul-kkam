package com.mulkkam.ui.main.dialog

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.activityViewModels
import androidx.health.connect.client.PermissionController
import com.mulkkam.R
import com.mulkkam.databinding.FragmentMainPermissionBinding
import com.mulkkam.ui.custom.toast.CustomToast
import com.mulkkam.ui.main.MainActivity.Companion.PERMISSION_ACTIVE_CALORIES_BURNED
import com.mulkkam.ui.main.MainActivity.Companion.PERMISSION_HEALTH_DATA_IN_BACKGROUND
import com.mulkkam.ui.main.MainActivity.Companion.TOAST_BOTTOM_NAV_OFFSET
import com.mulkkam.ui.main.MainViewModel
import com.mulkkam.ui.util.binding.BindingDialogFragment
import com.mulkkam.ui.util.extensions.setSingleClickListener

class MainPermissionDialogFragment :
    BindingDialogFragment<FragmentMainPermissionBinding>(
        FragmentMainPermissionBinding::inflate,
    ) {
    private val parentViewModel: MainViewModel by activityViewModels()

    private val requestHealthConnectLauncher =
        registerForActivityResult(PermissionController.createRequestPermissionResultContract()) { results ->
            handleHealthPermissionResult(results.contains(PERMISSION_HEALTH_DATA_IN_BACKGROUND))
            requestNotificationPermission()
        }

    private val requestNotificationLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            handleNotificationPermissionResult(granted)
        }

    private fun handleHealthPermissionResult(granted: Boolean) {
        val messageResId =
            if (granted) {
                parentViewModel.scheduleCalorieCheck()
                R.string.main_health_permission_granted
            } else {
                R.string.main_health_permission_denied
            }

        CustomToast
            .makeText(requireContext(), getString(messageResId), R.drawable.ic_info_circle)
            .apply {
                setGravityY(TOAST_BOTTOM_NAV_OFFSET)
            }.show()
    }

    private fun handleNotificationPermissionResult(granted: Boolean) {
        val messageResId =
            if (granted) {
                R.string.main_alarm_permission_granted
            } else {
                R.string.main_alarm_permission_denied
            }

        CustomToast
            .makeText(requireContext(), getString(messageResId), R.drawable.ic_info_circle)
            .apply {
                setGravityY(TOAST_BOTTOM_NAV_OFFSET)
            }.show()
        dismiss()
    }

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
        binding.tvConfirm.setSingleClickListener {
            requestHealthConnectLauncher.launch(setOf(PERMISSION_ACTIVE_CALORIES_BURNED, PERMISSION_HEALTH_DATA_IN_BACKGROUND))
            dialog?.hide()
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || hasNotificationPermission()) {
            dismiss()
            return
        }

        requestNotificationLauncher.launch(POST_NOTIFICATIONS)
    }

    private fun hasNotificationPermission(): Boolean =
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU -> true
            else ->
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED
        }

    companion object {
        const val TAG: String = "MAIN_PERMISSION_DIALOG_FRAGMENT"
        private const val DIALOG_WIDTH_RATIO: Float = 0.85f

        fun newInstance(): MainPermissionDialogFragment = MainPermissionDialogFragment()
    }
}
