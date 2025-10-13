package com.mulkkam.ui.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.TypedValue
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.mulkkam.R
import com.mulkkam.di.CheckerInjection.achievementHeatmapChecker
import com.mulkkam.domain.checker.AchievementHeatmapChecker
import com.mulkkam.domain.model.intake.AchievementLevel
import com.mulkkam.ui.main.MainActivity
import java.util.UUID
import kotlin.math.sqrt

class AchievementHeatmapWidget : AppWidgetProvider() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        super.onReceive(context, intent)
        val appWidgetManager = AppWidgetManager.getInstance(context.applicationContext)
        val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, AchievementHeatmapWidget::class.java))
        ids.forEach { id -> updateWidget(context.applicationContext, appWidgetManager, id) }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context.applicationContext, appWidgetManager, appWidgetId)
        }
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
    ) {
        val requestId: UUID = achievementHeatmapChecker.fetchAchievementHeatmap()
        observeWorker(context, appWidgetManager, appWidgetId, requestId)
    }

    private fun observeWorker(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        requestId: UUID,
    ) {
        val workManager = WorkManager.getInstance(context.applicationContext)
        val liveData = workManager.getWorkInfoByIdLiveData(requestId)

        val observer =
            object : Observer<WorkInfo?> {
                override fun onChanged(value: WorkInfo?) {
                    if (value?.state?.isFinished != true) return
                    showAchievementHeatmap(context, appWidgetManager, appWidgetId, value)
                    liveData.removeObserver(this)
                }
            }

        liveData.observeForever(observer)
    }

    private fun showAchievementHeatmap(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        workInfo: WorkInfo,
    ) {
        val outputData = workInfo.outputData
        val rates = outputData.getFloatArray(AchievementHeatmapChecker.KEY_RATES) ?: FloatArray(CELL_VIEW_IDS.size)
        val views = RemoteViews(context.packageName, R.layout.layout_achievement_heatmap_widget)

        val cellSizePx = calculateCellSizePx(context, appWidgetManager, appWidgetId)

        if (Build.VERSION.SDK_INT >= 31) {
            for (id in CELL_VIEW_IDS) {
                views.setViewLayoutWidth(id, cellSizePx, TypedValue.COMPLEX_UNIT_PX)
                views.setViewLayoutHeight(id, cellSizePx, TypedValue.COMPLEX_UNIT_PX)
            }
        }

        colorHeatmapCells(context, views, rates)

        views.setOnClickPendingIntent(R.id.main, MainActivity.newPendingIntent(context))

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun calculateCellSizePx(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
    ): Float {
        val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
        val minWidthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
        val paddingDp = 12 + 12
        val cellMarginDp = 2f
        val columns = sqrt(CELL_VIEW_IDS.size.toFloat())

        val totalWidthDp = (minWidthDp - paddingDp).coerceAtLeast(0)
        val totalMarginsDp = cellMarginDp * 2 * columns
        val cellSizeDp = ((totalWidthDp - totalMarginsDp) / columns).coerceAtLeast(0f)

        val dm = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, cellSizeDp, dm)
    }

    private fun colorHeatmapCells(
        context: Context,
        views: RemoteViews,
        rates: FloatArray,
    ) {
        for (index in CELL_VIEW_IDS.indices) {
            val viewId = CELL_VIEW_IDS[index]
            val rate = rates.getOrNull(index) ?: 0f
            val color = getAchievementColor(context, rate)
            views.setInt(viewId, "setColorFilter", color)
        }
    }

    private fun getAchievementColor(
        context: Context,
        rate: Float,
    ): Int {
        val level = AchievementLevel.from(rate)

        val colorResId =
            when (level) {
                AchievementLevel.LEVEL_1 -> R.color.gray_10
                AchievementLevel.LEVEL_2 -> R.color.primary_10
                AchievementLevel.LEVEL_3 -> R.color.primary_50
                AchievementLevel.LEVEL_4 -> R.color.primary_100
                AchievementLevel.LEVEL_5 -> R.color.primary_200
                AchievementLevel.LEVEL_6 -> R.color.primary_300
                AchievementLevel.LEVEL_7 -> R.color.primary_400
            }

        return ContextCompat.getColor(context, colorResId)
    }

    companion object {
        private val CELL_VIEW_IDS =
            intArrayOf(
                R.id.cell_0,
                R.id.cell_1,
                R.id.cell_2,
                R.id.cell_3,
                R.id.cell_4,
                R.id.cell_5,
                R.id.cell_6,
                R.id.cell_7,
                R.id.cell_8,
                R.id.cell_9,
                R.id.cell_10,
                R.id.cell_11,
                R.id.cell_12,
                R.id.cell_13,
                R.id.cell_14,
                R.id.cell_15,
                R.id.cell_16,
                R.id.cell_17,
                R.id.cell_18,
                R.id.cell_19,
                R.id.cell_20,
                R.id.cell_21,
                R.id.cell_22,
                R.id.cell_23,
                R.id.cell_24,
                R.id.cell_25,
                R.id.cell_26,
                R.id.cell_27,
                R.id.cell_28,
                R.id.cell_29,
                R.id.cell_30,
                R.id.cell_31,
                R.id.cell_32,
                R.id.cell_33,
                R.id.cell_34,
                R.id.cell_35,
                R.id.cell_36,
                R.id.cell_37,
                R.id.cell_38,
                R.id.cell_39,
                R.id.cell_40,
                R.id.cell_41,
                R.id.cell_42,
                R.id.cell_43,
                R.id.cell_44,
                R.id.cell_45,
                R.id.cell_46,
                R.id.cell_47,
                R.id.cell_48,
            )

        fun refresh(context: Context) {
            val intent = Intent(context, AchievementHeatmapWidget::class.java)
            context.sendBroadcast(intent)
        }
    }
}
