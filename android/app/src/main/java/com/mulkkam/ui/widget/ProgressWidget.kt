package com.mulkkam.ui.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.mulkkam.R
import com.mulkkam.di.CheckerInjection.progressChecker
import com.mulkkam.domain.checker.ProgressChecker.Companion.KEY_OUTPUT_ACHIEVEMENT_RATE
import com.mulkkam.ui.custom.progress.GradientDonutChartView
import com.mulkkam.ui.main.MainActivity
import com.mulkkam.ui.util.extensions.dpToPx
import java.util.UUID

class ProgressWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        for (appWidgetId in appWidgetIds) {
            updateProgressWidget(context, appWidgetId)
        }
    }

    companion object {
        fun updateProgressWidget(
            context: Context,
            appWidgetId: Int,
        ) {
            val requestId = progressChecker.checkCurrentAchievementRate()

            observeWorker(context, appWidgetId, requestId)
        }

        private fun observeWorker(
            context: Context,
            appWidgetId: Int,
            requestId: UUID,
        ) {
            val workManager = WorkManager.getInstance(context.applicationContext)
            val liveData = workManager.getWorkInfoByIdLiveData(requestId)

            val observer =
                object : Observer<WorkInfo?> {
                    override fun onChanged(value: WorkInfo?) {
                        value?.takeIf { it.state.isFinished }?.let {
                            val achievementRate =
                                it.outputData.getFloat(KEY_OUTPUT_ACHIEVEMENT_RATE, 0f)

                            val appWidgetManager =
                                AppWidgetManager.getInstance(context.applicationContext)
                            updateProgressWidgetViews(
                                context.applicationContext,
                                appWidgetManager,
                                appWidgetId,
                                achievementRate,
                            )

                            liveData.removeObserver(this)
                        }
                    }
                }

            liveData.observeForever(observer)
        }

        private fun updateProgressWidgetViews(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            progress: Float = 0f,
        ) {
            val views = RemoteViews(context.packageName, R.layout.layout_progress_widget)

            val donutBitmap =
                GradientDonutChartView.createBitmap(
                    context,
                    width = 78.dpToPx(context),
                    height = 78.dpToPx(context),
                    stroke = 10f,
                    progress = progress,
                )

            val progressText =
                context.getString(R.string.progress_widget_achievement_rate, progress.toInt())
            views.setTextViewText(R.id.tv_achievement_rate, progressText)

            views.setImageViewBitmap(R.id.iv_donut_chart, donutBitmap)
            views.setOnClickPendingIntent(R.id.main, MainActivity.newPendingIntent(context))

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
