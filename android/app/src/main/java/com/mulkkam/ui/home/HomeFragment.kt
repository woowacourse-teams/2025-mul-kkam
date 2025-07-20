package com.mulkkam.ui.home

import com.mulkkam.databinding.FragmentHomeBinding
import com.mulkkam.ui.binding.BindingFragment
import com.mulkkam.ui.main.MainActivity

class HomeFragment :
    BindingFragment<FragmentHomeBinding>(
        FragmentHomeBinding::inflate,
    ),
    MainActivity.Refreshable {
    override fun onSelected() {
    }
}
