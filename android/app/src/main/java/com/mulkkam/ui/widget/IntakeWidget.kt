package com.mulkkam.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.View
import android.widget.RemoteViews
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.mulkkam.R
import com.mulkkam.di.CheckerInjection.intakeChecker
import com.mulkkam.di.LoggingInjection.mulKkamLogger
import com.mulkkam.domain.checker.IntakeChecker.Companion.KEY_INTAKE_CHECKER_ACHIEVEMENT_RATE
import com.mulkkam.domain.checker.IntakeChecker.Companion.KEY_INTAKE_CHECKER_CUP_ID
import com.mulkkam.domain.checker.IntakeChecker.Companion.KEY_INTAKE_CHECKER_EMOJI_BYTES
import com.mulkkam.domain.checker.IntakeChecker.Companion.KEY_INTAKE_CHECKER_PERFORM_SUCCESS
import com.mulkkam.domain.checker.IntakeChecker.Companion.KEY_INTAKE_CHECKER_TARGET_AMOUNT
import com.mulkkam.domain.checker.IntakeChecker.Companion.KEY_INTAKE_CHECKER_TOTAL_AMOUNT
import com.mulkkam.domain.model.logger.LogEvent
import com.mulkkam.ui.custom.progress.GradientDonutChartView
import com.mulkkam.ui.main.MainActivity
import com.mulkkam.ui.util.extensions.dpToPx
import com.mulkkam.ui.widget.IntakeWidgetAction.ACTION_DRINK
import com.mulkkam.ui.widget.IntakeWidgetAction.ACTION_REFRESH
import java.time.LocalDate
import java.util.UUID

class IntakeWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        appWidgetIds.forEach { id -> updateIntakeWidgetInfo(context, id) }
    }

    private fun updateIntakeWidgetInfo(
        context: Context,
        appWidgetId: Int,
    ) {
        val requestId = intakeChecker.checkWidgetInfo()

        val workManager = WorkManager.getInstance(context.applicationContext)
        val live = workManager.getWorkInfoByIdLiveData(requestId)

        val observer =
            object : Observer<WorkInfo?> {
                override fun onChanged(value: WorkInfo?) {
                    if (value?.state?.isFinished != true) return

                    val rate = value.outputData.getFloat(KEY_INTAKE_CHECKER_ACHIEVEMENT_RATE, 0f)
                    val target = value.outputData.getInt(KEY_INTAKE_CHECKER_TARGET_AMOUNT, 0)
                    val total = value.outputData.getInt(KEY_INTAKE_CHECKER_TOTAL_AMOUNT, 0)
                    val cupId = value.outputData.getLong(KEY_INTAKE_CHECKER_CUP_ID, 0L)
                    val emojiBytes = value.outputData.getByteArray(KEY_INTAKE_CHECKER_EMOJI_BYTES) ?: byteArrayOf()

                    val appWidgetManager = AppWidgetManager.getInstance(context.applicationContext)
                    showIntakeWidgetInfo(
                        context = context.applicationContext,
                        appWidgetManager = appWidgetManager,
                        appWidgetId = appWidgetId,
                        achievementRate = rate,
                        targetAmount = target,
                        totalAmount = total,
                        cupId = cupId,
                        emojiBytes = emojiBytes,
                    )

                    live.removeObserver(this)
                }
            }
        live.observeForever(observer)
    }

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        super.onReceive(context, intent)
        when (IntakeWidgetAction.from(intent.action)) {
            ACTION_DRINK -> performDrink(intent, context)
            ACTION_REFRESH -> refreshWidget(context)
            null -> return
        }
    }

    private fun performDrink(
        intent: Intent,
        context: Context,
    ) {
        val cupId = intent.getLongExtra(KEY_EXTRA_CUP_ID, 0L)
        mulKkamLogger.info(LogEvent.WIDGET, "${IntakeWidget::class.simpleName} - Drink cupId: $cupId")
        val requestId = intakeChecker.drink(cupId)
        observeDrinkWorker(context, requestId)
    }

    private fun observeDrinkWorker(
        context: Context,
        workId: UUID,
    ) {
        val workManager = WorkManager.getInstance(context.applicationContext)
        val live = workManager.getWorkInfoByIdLiveData(workId)

        val observer =
            object : Observer<WorkInfo?> {
                override fun onChanged(value: WorkInfo?) {
                    if (value?.state?.isFinished == true) {
                        val success = value.outputData.getBoolean(KEY_INTAKE_CHECKER_PERFORM_SUCCESS, false)
                        if (success) refreshWidget(context)
                        live.removeObserver(this)
                    }
                }
            }
        live.observeForever(observer)
    }

    private fun refreshWidget(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context.applicationContext)
        val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, IntakeWidget::class.java))
        ids.forEach { id -> updateIntakeWidgetInfo(context, id) }
    }

    private fun showIntakeWidgetInfo(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        achievementRate: Float,
        targetAmount: Int,
        totalAmount: Int,
        cupId: Long,
        emojiBytes: ByteArray,
    ) {
        val views = RemoteViews(context.packageName, R.layout.layout_intake_widget)

        val donut =
            GradientDonutChartView.createBitmap(
                context,
                width = 74.dpToPx(context),
                height = 74.dpToPx(context),
                stroke = 6f,
                progress = achievementRate,
            )
        views.setImageViewBitmap(R.id.iv_donut_chart, donut)

        views.setTextViewText(
            R.id.tv_title_date,
            context.getString(
                R.string.intake_widget_home_target,
                LocalDate.now().monthValue,
                LocalDate.now().dayOfMonth,
            ),
        )

        views.setTextViewText(
            R.id.tv_summary,
            context.getString(R.string.home_daily_intake_summary, totalAmount, targetAmount),
        )

        views.setOnClickPendingIntent(
            R.id.layout_intake_widget,
            MainActivity.newPendingIntent(context),
        )

        views.setOnClickPendingIntent(
            R.id.ll_drink,
            newDrinkPendingIntent(context, appWidgetId, cupId),
        )

        updateEmojiIcon(emojiBytes, views)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun updateEmojiIcon(
        emojiBytes: ByteArray,
        views: RemoteViews,
    ) {
        val bitmap =
            emojiBytes.takeIf { it.isNotEmpty() }?.let { bytes ->
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }
        if (bitmap != null) {
            views.setImageViewBitmap(R.id.iv_drink_icon, bitmap)
        } else {
            views.setViewVisibility(R.id.iv_drink_icon, View.GONE)
        }
    }

    companion object {
        private const val REQUEST_CODE_DRINK: Int = 20_100
        private const val KEY_EXTRA_CUP_ID: String = "EXTRA_CUP_ID"
        private const val KEY_EXTRA_WIDGET_ID: String = "EXTRA_WIDGET_ID"

        private fun newDrinkPendingIntent(
            context: Context,
            appWidgetId: Int,
            cupId: Long,
        ): PendingIntent {
            val intent =
                Intent(context, IntakeWidget::class.java).apply {
                    action = ACTION_DRINK.name
                    putExtra(KEY_EXTRA_CUP_ID, cupId)
                    putExtra(KEY_EXTRA_WIDGET_ID, appWidgetId)
                }

            val requestCode = REQUEST_CODE_DRINK + appWidgetId
            return PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }

        fun refresh(context: Context) {
            val intent =
                Intent(context, IntakeWidget::class.java).apply {
                    action = ACTION_REFRESH.name
                }
            context.sendBroadcast(intent)
        }
    }
}
