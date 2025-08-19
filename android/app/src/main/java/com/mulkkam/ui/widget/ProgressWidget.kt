package com.mulkkam.ui.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.graphics.Bitmap
import android.widget.RemoteViews
import androidx.annotation.ColorRes
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.mulkkam.R
import com.mulkkam.data.local.work.ProgressWidgetWorker
import com.mulkkam.data.local.work.ProgressWidgetWorker.Companion.KEY_OUTPUT_ACHIEVEMENT_RATE
import com.mulkkam.ui.custom.progress.GradientDonutChartView
import com.mulkkam.ui.main.MainActivity
import com.mulkkam.ui.util.ViewBitmapCapture
import java.util.UUID

class ProgressWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        for (appWidgetId in appWidgetIds) {
            updateProgressWidgetWithWorker(context, appWidgetId)
        }
    }
}

fun updateProgressWidgetWithWorker(
    context: Context,
    appWidgetId: Int,
) {
    val workManager = WorkManager.getInstance(context.applicationContext)
    val workRequest = OneTimeWorkRequestBuilder<ProgressWidgetWorker>().build()

    workManager.enqueue(workRequest)

    observeWorker(context, appWidgetId, workRequest.id)
}

private fun observeWorker(
    context: Context,
    appWidgetId: Int,
    workId: UUID,
) {
    val workManager = WorkManager.getInstance(context.applicationContext)
    val liveData = workManager.getWorkInfoByIdLiveData(workId)

    val observer =
        object : Observer<WorkInfo?> {
            override fun onChanged(workInfo: WorkInfo?) {
                workInfo?.takeIf { it.state.isFinished }?.let {
                    val achievementRate =
                        it.outputData.getFloat(KEY_OUTPUT_ACHIEVEMENT_RATE, 0f)

                    val appWidgetManager = AppWidgetManager.getInstance(context.applicationContext)
                    updateProgressWidget(context.applicationContext, appWidgetManager, appWidgetId, achievementRate)

                    liveData.removeObserver(this)
                }
            }
        }

    liveData.observeForever(observer)
}

private fun updateProgressWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    progress: Float = 0f,
) {
    val views = RemoteViews(context.packageName, R.layout.layout_progress_widget)

    val donutBitmap = createDonutBitmap(context, progress = progress)

    val progressText =
        context.getString(R.string.progress_widget_achievement_rate, progress.toInt())
    views.setTextViewText(R.id.tv_achievement_rate, progressText)

    views.setImageViewBitmap(R.id.iv_donut_chart, donutBitmap)
    views.setOnClickPendingIntent(R.id.main, MainActivity.newPendingIntent(context))

    appWidgetManager.updateAppWidget(appWidgetId, views)
}

private fun createDonutBitmap(
    context: Context,
    width: Int = 300,
    height: Int = 300,
    stroke: Float = 10f,
    progress: Float,
    @ColorRes backgroundColor: Int = R.color.gray_10,
    @ColorRes paintColor: Int = R.color.primary_50,
): Bitmap {
    val donutView =
        GradientDonutChartView(context).apply {
            layoutParams = android.view.ViewGroup.LayoutParams(width, height)
            setStroke(stroke)
            setBackgroundPaintColor(backgroundColor)
            setPaintColor(paintColor)
            setProgress(progress)
            invalidate()
        }

    donutView.measure(
        android.view.View.MeasureSpec
            .makeMeasureSpec(width, android.view.View.MeasureSpec.EXACTLY),
        android.view.View.MeasureSpec
            .makeMeasureSpec(height, android.view.View.MeasureSpec.EXACTLY),
    )
    donutView.layout(0, 0, donutView.measuredWidth, donutView.measuredHeight)

    return ViewBitmapCapture.snapshot(donutView)
}
