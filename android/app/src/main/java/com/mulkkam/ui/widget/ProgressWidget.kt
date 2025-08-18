package com.mulkkam.ui.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.mulkkam.R
import com.mulkkam.ui.custom.progress.GradientDonutChartView
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
    // RemoteViews 생성
    val views = RemoteViews(context.packageName, R.layout.progress_widget)

    // 도넛 차트 뷰 생성
    val donutView =
        GradientDonutChartView(context).apply {
            // 위젯 크기에 맞게 사이즈 지정 (고정 값 or 동적으로 계산)
            layoutParams = android.view.ViewGroup.LayoutParams(300, 300)
            setStroke(10f)
            setBackgroundPaintColor(R.color.gray_10)
            setPaintColor(R.color.primary_50)
            setProgress(90f)
            invalidate()
        }

    // View → Bitmap 변환
    donutView.measure(
        android.view.View.MeasureSpec
            .makeMeasureSpec(300, android.view.View.MeasureSpec.EXACTLY),
        android.view.View.MeasureSpec
            .makeMeasureSpec(300, android.view.View.MeasureSpec.EXACTLY),
    )
    donutView.layout(0, 0, donutView.measuredWidth, donutView.measuredHeight)

    val bitmap = ViewBitmapCapture.snapshot(donutView)

    // RemoteViews 에 Bitmap 반영
    views.setImageViewBitmap(R.id.iv_donut_chart, bitmap)

    // 위젯 갱신
    appWidgetManager.updateAppWidget(appWidgetId, views)
}
