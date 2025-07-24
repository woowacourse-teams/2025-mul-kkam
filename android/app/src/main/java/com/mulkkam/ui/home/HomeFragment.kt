package com.mulkkam.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.mulkkam.databinding.FragmentHomeBinding
import com.mulkkam.ui.binding.BindingFragment
import com.mulkkam.ui.main.Refreshable

class HomeFragment :
    BindingFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate),
    Refreshable {
    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.currentWaterIntake.observe(viewLifecycleOwner) { currentWaterIntake ->
            binding.pbHomeWaterProgress.progress = currentWaterIntake
        }

        binding.fabHomeDrink.setOnClickListener {
            viewModel.addWaterIntake()
        }
    }
}
