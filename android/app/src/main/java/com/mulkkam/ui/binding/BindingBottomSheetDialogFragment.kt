package com.mulkkam.ui.binding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class BindingBottomSheetDialogFragment<BINDING : ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> BINDING,
) : BottomSheetDialogFragment() {
    private var _binding: BINDING? = null
    val binding: BINDING get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = bindingInflater(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
