package com.mulkkam.ui.encyclopedia

import android.os.Bundle
import com.mulkkam.databinding.ActivityCoffeeEncyclopediaBinding
import com.mulkkam.ui.util.binding.BindingActivity
import com.mulkkam.ui.util.extensions.setSingleClickListener

class CoffeeEncyclopediaActivity :
    BindingActivity<ActivityCoffeeEncyclopediaBinding>(
        ActivityCoffeeEncyclopediaBinding::inflate,
    ) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.ivBack.setSingleClickListener {
            finish()
        }
    }
}
