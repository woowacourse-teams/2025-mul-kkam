package com.mulkkam.ui.record

import com.mulkkam.databinding.FragmentRecordBinding
import com.mulkkam.ui.binding.BindingFragment
import com.mulkkam.ui.main.MainActivity

class RecordFragment :
    BindingFragment<FragmentRecordBinding>(
        FragmentRecordBinding::inflate,
    ),
    MainActivity.Refreshable {
    override fun onSelected() {
    }
}
