package com.mulkkam.ui.util.binding

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewbinding.ViewBinding

open class BindingActivity<BINDING : ViewBinding>(
    private val bindingInflater: (LayoutInflater) -> BINDING,
) : AppCompatActivity() {
    private var _binding: BINDING? = null
    val binding get() = _binding!!

    open val needBottomPadding: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = bindingInflater(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        initViewPadding()
    }

    private fun initViewPadding() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val bottomPadding = if (needBottomPadding) systemBars.bottom else 0
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding)
            insets
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
