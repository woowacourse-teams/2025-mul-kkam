package com.mulkkam.ui.settingcups

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.settingcups.model.CupUiModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingCupsActivity : AppCompatActivity() {
    private val viewModel: SettingCupsViewModel by viewModel()
    private val debounceHandler: Handler = Handler(Looper.getMainLooper())
    private var debounceRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MulKkamTheme {
                SettingCupsRoute(
                    onBackClick = ::finish,
                    onReorderCups = ::reorderCups,
                    onNavigateToCoffeeEncyclopedia = ::navigateToCoffeeEncyclopedia,
                    viewModel = viewModel,
                )
            }
        }
    }

    private fun navigateToCoffeeEncyclopedia() {
        // coffee encyclopedia activity migration completed
    }

    private fun reorderCups(newCups: List<CupUiModel>) {
        viewModel.applyOptimisticCupOrder(newCups)

        saveOrderResult(newCups)
        scheduleCupOrderUpdate(newCups)
    }

    private fun saveOrderResult(cups: List<CupUiModel>) {
        /* TODO: 화면 마이그레이션 작업 중 참고
        val data =
            Intent().apply {
                putParcelableArrayListExtra(EXTRA_KEY_LATEST_CUPS_ORDER, ArrayList(cups))
            }
         */
        setResult(RESULT_OK)
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
