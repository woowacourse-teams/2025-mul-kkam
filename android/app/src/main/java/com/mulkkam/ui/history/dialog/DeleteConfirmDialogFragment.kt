package com.mulkkam.ui.history.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.mulkkam.databinding.FragmentDeleteConfirmDialogBinding

class DeleteConfirmDialogFragment : DialogFragment() {
    private var _binding: FragmentDeleteConfirmDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDeleteConfirmDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnDeleteConfirm.setOnClickListener {
            setFragmentResult("REQUEST_KEY", bundleOf("BUNDLE_KEY_CONFIRM" to true))
            dismiss()
        }
        binding.btnDeleteCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        setWindowWidth()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun setWindowWidth() {
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * DIALOG_WIDTH_RATIO).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val DIALOG_WIDTH_RATIO: Float = 0.85f
        const val TAG = "DeleteConfirmDialogFragment"
        const val REQUEST_KEY = "delete_request"
        const val BUNDLE_KEY_CONFIRM = "is_confirmed"
    }
}
