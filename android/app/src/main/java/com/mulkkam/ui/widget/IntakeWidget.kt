package com.mulkkam.ui.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.widget.RemoteViews
import androidx.annotation.ColorRes
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.mulkkam.R
import com.mulkkam.data.local.work.IntakeWidgetWorker
import com.mulkkam.ui.custom.progress.GradientDonutChartView
import com.mulkkam.ui.splash.SplashActivity
import com.mulkkam.ui.util.extensions.snapshot
import java.time.LocalDate

class IntakeWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        appWidgetIds.forEach { id -> updateIntakeWidgetWithWorker(context, id) }
    }
}

fun updateIntakeWidgetWithWorker(
    context: Context,
    appWidgetId: Int,
) {
    val workManager = WorkManager.getInstance(context)
    val request = OneTimeWorkRequestBuilder<IntakeWidgetWorker>().build()
    workManager.enqueue(request)

    workManager.getWorkInfoByIdLiveData(request.id).observeForever { info ->
        if (info?.state?.isFinished != true) return@observeForever
        val rate = info.outputData.getFloat(IntakeWidgetWorker.KEY_OUTPUT_ACHIEVEMENT_RATE, 0f)
        val target = info.outputData.getInt(IntakeWidgetWorker.KEY_OUTPUT_TARGET, 0)
        val total = info.outputData.getInt(IntakeWidgetWorker.KEY_OUTPUT_TOTAL, 0)
        updateIntakeWidget(context, AppWidgetManager.getInstance(context), appWidgetId, rate, target, total)
    }
}

private fun updateIntakeWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    achievementRate: Float,
    targetAmount: Int,
    totalAmount: Int,
) {
    val views = RemoteViews(context.packageName, R.layout.layout_intake_widget)

    val donut =
        createDonutBitmap(
            context,
            width = 74.dpToPx(context),
            height = 74.dpToPx(context),
            stroke = 8f,
            progress = achievementRate,
        )
    views.setImageViewBitmap(R.id.iv_donut_chart, donut)

    val title = formatTodayTitle(context)
    views.setTextViewText(R.id.tv_title_date, title)

    views.setTextViewText(
        R.id.tv_summary,
        context.getString(R.string.home_daily_intake_summary, totalAmount, targetAmount),
    )

    views.setOnClickPendingIntent(R.id.main, SplashActivity.newPendingIntent(context))
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

private fun Int.dpToPx(context: Context): Int = (this * context.resources.displayMetrics.density).toInt()

private fun formatTodayTitle(context: Context): String {
    val now = LocalDate.now()
    val title = context.getString(R.string.intake_widget_home_target, now.monthValue, now.dayOfMonth)
    return title
}

private fun createDonutBitmap(
    context: Context,
    width: Int,
    height: Int,
    stroke: Float,
    progress: Float,
    @ColorRes backgroundColor: Int = R.color.gray_10,
    @ColorRes paintColor: Int = R.color.primary_50,
): Bitmap {
    val view =
        GradientDonutChartView(context).apply {
            layoutParams = ViewGroup.LayoutParams(width, height)
            setStroke(stroke)
            setBackgroundPaintColor(backgroundColor)
            setPaintColor(paintColor)
            setProgress(progress)
        }
    view.measure(
        View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY),
    )
    view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    return view.snapshot()
}
