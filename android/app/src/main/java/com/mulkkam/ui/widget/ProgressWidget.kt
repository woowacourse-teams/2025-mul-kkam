package com.mulkkam.ui.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.graphics.Bitmap
import android.widget.RemoteViews
import androidx.annotation.ColorRes
import com.mulkkam.R
import com.mulkkam.ui.custom.progress.GradientDonutChartView
import com.mulkkam.ui.main.MainActivity
import com.mulkkam.ui.util.ViewBitmapCapture

class ProgressWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
) {
    val views = RemoteViews(context.packageName, R.layout.progress_widget)

    val donutBitmap = createDonutBitmap(context, progress = 90f)

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
