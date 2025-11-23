package com.mulkkam.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.main.Refreshable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment :
    Fragment(),
    Refreshable {
    private val viewModel: HistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val composeView =
            ComposeView(requireContext()).apply {
                setContent {
                    MulkkamTheme {
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
