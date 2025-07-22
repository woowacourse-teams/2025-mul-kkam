package com.mulkkam.ui.home

import android.os.Bundle
import android.view.View
import com.mulkkam.databinding.FragmentHomeBinding
import com.mulkkam.ui.binding.BindingFragment
import com.mulkkam.ui.main.Refreshable

class HomeFragment :
    BindingFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate),
    Refreshable {
    override fun onSelected() {
        // TODO: 화면 전환 시 필요한 작업을 구현합니다.
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
    }
}
