package com.mulkkam.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.mulkkam.R
import com.mulkkam.di.CheckerInjection.intakeChecker
import com.mulkkam.domain.checker.IntakeChecker.Companion.KEY_INTAKE_CHECKER_ACHIEVEMENT_RATE
import com.mulkkam.domain.checker.IntakeChecker.Companion.KEY_INTAKE_CHECKER_CUP_AMOUNT
import com.mulkkam.domain.checker.IntakeChecker.Companion.KEY_INTAKE_CHECKER_PERFORM_SUCCESS
import com.mulkkam.domain.checker.IntakeChecker.Companion.KEY_INTAKE_CHECKER_TARGET_AMOUNT
import com.mulkkam.domain.checker.IntakeChecker.Companion.KEY_INTAKE_CHECKER_TOTAL_AMOUNT
import com.mulkkam.ui.custom.progress.GradientDonutChartView
import com.mulkkam.ui.main.MainActivity
import com.mulkkam.ui.util.extensions.dpToPx
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
                    val amount = value.outputData.getInt(KEY_INTAKE_CHECKER_CUP_AMOUNT, 0)

                    val appWidgetManager = AppWidgetManager.getInstance(context.applicationContext)
                    showIntakeWidgetInfo(
                        context = context.applicationContext,
                        appWidgetManager = appWidgetManager,
                        appWidgetId = appWidgetId,
                        achievementRate = rate,
                        targetAmount = target,
                        totalAmount = total,
                        primaryCupAmount = amount,
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
            IntakeWidgetAction.ACTION_DRINK -> performDrink(intent, context)
            IntakeWidgetAction.ACTION_REFRESH -> refreshWidget(context)
            null -> return
        }
    }

    private fun performDrink(
        intent: Intent,
        context: Context,
    ) {
        val amount = intent.getIntExtra(KEY_EXTRA_AMOUNT, 0)
        val requestId = intakeChecker.drink(amount)
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
        primaryCupAmount: Int,
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
            newDrinkPendingIntent(context, appWidgetId, primaryCupAmount),
        )

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    companion object {
        private const val REQUEST_CODE_DRINK: Int = 20_100
        private const val KEY_EXTRA_AMOUNT: String = "EXTRA_AMOUNT"
        private const val KEY_EXTRA_WIDGET_ID: String = "EXTRA_WIDGET_ID"

        fun newDrinkPendingIntent(
            context: Context,
            appWidgetId: Int,
            amount: Int,
        ): PendingIntent {
            val intent =
                Intent(context, IntakeWidget::class.java).apply {
                    action = IntakeWidgetAction.ACTION_DRINK.name
                    putExtra(KEY_EXTRA_AMOUNT, amount)
                    putExtra(KEY_EXTRA_WIDGET_ID, appWidgetId)
                    data = "mulkkam://widget/drink?id=$appWidgetId&amount=$amount".toUri()
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
                    action = IntakeWidgetAction.ACTION_REFRESH.name
                }
            context.sendBroadcast(intent)
        }
    }
}
