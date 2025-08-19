package com.mulkkam.ui.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.mulkkam.R
import com.mulkkam.data.local.work.ProgressWidgetWorker
import com.mulkkam.ui.custom.progress.GradientDonutChartView
import com.mulkkam.ui.main.MainActivity
import com.mulkkam.ui.util.extensions.dpToPx

class ProgressWidget : AppWidgetProvider() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        super.onReceive(context, intent)

        if (intent.action == ACTION_UPDATE_PROGRESS) {
            val rate = intent.getFloatExtra(KEY_EXTRA_ACHIEVEMENT_RATE, 0f)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, javaClass))
            appWidgetIds.forEach { id ->
                updateProgressWidget(context, appWidgetManager, id, rate)
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        appWidgetIds.forEach { id ->
            updateProgressWidgetWithWorker(context)
        }
    }

    companion object {
        private const val KEY_EXTRA_ACHIEVEMENT_RATE = "ACHIEVEMENT_RATE"
        private const val ACTION_UPDATE_PROGRESS = "com.example.ACTION_UPDATE_PROGRESS"

        fun newIntent(
            context: Context,
            rate: Float,
        ): Intent =
            Intent(context, ProgressWidget::class.java).apply {
                action = ACTION_UPDATE_PROGRESS
                putExtra(KEY_EXTRA_ACHIEVEMENT_RATE, rate)
            }
    }
}

fun updateProgressWidgetWithWorker(context: Context) {
    val workManager = WorkManager.getInstance(context.applicationContext)
    val workRequest = OneTimeWorkRequestBuilder<ProgressWidgetWorker>().build()

    workManager.enqueue(workRequest)
}

private fun updateProgressWidget(
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
