package com.mulkkam.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.main.Refreshable
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryFragment :
    Fragment(),
    Refreshable {
    private val viewModel: HistoryViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val composeView =
            ComposeView(requireContext()).apply {
                setContent {
                    MulKkamTheme {
                        HistoryScreen(viewModel = viewModel)
                    }
                }
            }
        return composeView
    }

    override fun onReselected() {
        viewModel.loadIntakeHistories()
    }
}
