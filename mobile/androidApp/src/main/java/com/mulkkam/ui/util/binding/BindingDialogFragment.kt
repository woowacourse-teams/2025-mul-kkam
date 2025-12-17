package com.mulkkam.ui.util.binding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding

open class BindingDialogFragment<BINDING : ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> BINDING,
) : DialogFragment() {
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
