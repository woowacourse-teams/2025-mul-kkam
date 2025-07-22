package com.mulkkam.ui.record

import com.mulkkam.databinding.FragmentRecordBinding
import com.mulkkam.ui.binding.BindingFragment
import com.mulkkam.ui.main.Refreshable

class RecordFragment :
    BindingFragment<FragmentRecordBinding>(
        FragmentRecordBinding::inflate,
    ),
    Refreshable {
    override fun onSelected() {
        // TODO: 화면 전환 시 필요한 작업을 구현합니다.
    }
}
