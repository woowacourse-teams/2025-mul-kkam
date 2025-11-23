package com.mulkkam.ui.settingcups

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.settingcups.dialog.SettingCupFragment
import com.mulkkam.ui.settingcups.model.CupUiModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingCupsActivity : AppCompatActivity() {
    private val viewModel: SettingCupsViewModel by viewModels()
    private val debounceHandler: Handler = Handler(Looper.getMainLooper())
    private var debounceRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MulkkamTheme {
                SettingCupsRoute(
                    viewModel = viewModel,
                    onBackClick = ::finish,
                    onConfirmReset = { viewModel.resetCups() },
                    onEditCup = ::showEditBottomSheetDialog,
                    onAddCup = { showEditBottomSheetDialog(null) },
                    onReorderCups = ::reorderCups,
                )
            }
        }
    }

    private fun showEditBottomSheetDialog(cup: CupUiModel?) {
        if (supportFragmentManager.findFragmentByTag(SettingCupFragment.TAG) != null) return
        SettingCupFragment
            .newInstance(cup)
            .show(supportFragmentManager, SettingCupFragment.TAG)
    }

    private fun reorderCups(newCups: List<CupUiModel>) {
        viewModel.applyOptimisticCupOrder(newCups)

        saveOrderResult(newCups)
        scheduleCupOrderUpdate(newCups)
    }

    private fun saveOrderResult(cups: List<CupUiModel>) {
        val data: Intent =
            Intent().apply {
                putParcelableArrayListExtra(EXTRA_KEY_LATEST_CUPS_ORDER, ArrayList(cups))
            }
        setResult(RESULT_OK, data)
    }

    private fun scheduleCupOrderUpdate(newCups: List<CupUiModel>) {
        debounceRunnable?.let(debounceHandler::removeCallbacks)

        debounceRunnable =
            Runnable {
                viewModel.updateCupOrder(newCups)

                setResult(RESULT_CANCELED)
            }.also { runnable ->
                debounceHandler.postDelayed(runnable, REORDER_RANK_DELAY)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        debounceRunnable?.let(debounceHandler::removeCallbacks)
        debounceRunnable = null
    }

    companion object {
        const val EXTRA_KEY_LATEST_CUPS_ORDER: String = "EXTRA_KEY_LATEST_CUPS_ORDER"
        private const val REORDER_RANK_DELAY: Long = 2000L

        fun newIntent(context: Context): Intent = Intent(context, SettingCupsActivity::class.java)
    }
}
