package com.mulkkam.ui.settingcups.model

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.mulkkam.ui.settingcups.adapter.SettingCupsItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch

class SettingCupsReorderState(
    private val lazyListState: LazyListState,
    private val coroutineScope: CoroutineScope,
    private val autoScrollThresholdPx: Float,
    private val autoScrollStepPx: Float,
    private val defaultItemHeightPx: Float,
) {
    var draggingItemKey by mutableStateOf<Any?>(null)
        private set
    var draggingItemIndex by mutableStateOf<Int?>(null)
        private set
    var dragOffset by mutableFloatStateOf(0f)
        private set

    private var initialCupOrder by mutableStateOf(emptyList<Long>())
    private var initialSnapshot by mutableStateOf<List<SettingCupsItem>>(emptyList())
    private var autoScrollJob: Job? = null

    fun beginDrag(
        items: SnapshotStateList<SettingCupsItem>,
        fallbackIndex: Int,
        key: Any,
    ) {
        draggingItemKey = key
        draggingItemIndex =
            items
                .indexOfFirst {
                    it is SettingCupsItem.CupItem && it.value.id == key
                }.let { if (it >= 0) it else fallbackIndex }
        dragOffset = 0f
        initialCupOrder = items.cupItems().map { it.value.id }
        initialSnapshot = items.toList()
    }

    fun updateDrag(
        items: SnapshotStateList<SettingCupsItem>,
        dragDeltaY: Float,
        onAutoScroll: (Float) -> Unit,
    ) {
        val key = draggingItemKey ?: return
        val currentIndex = draggingItemIndex ?: return
        dragOffset += dragDeltaY
        calculateTargetIndex(
            lazyListState = lazyListState,
            items = items,
            currentKey = key,
            dragOffset = dragOffset,
        )?.let { target ->
            if (target != currentIndex && items.getOrNull(target) is SettingCupsItem.CupItem) {
                val targetInfo: LazyListItemInfo? =
                    lazyListState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == target }
                items.move(currentIndex, target)
                draggingItemIndex = target
                val direction = if (target > currentIndex) 1 else -1
                val adjust = targetInfo?.size?.toFloat() ?: defaultItemHeightPx
                dragOffset -= direction * adjust
            }
        }
        autoScrollJob?.cancel()
        autoScrollJob =
            autoScrollIfNeeded(
                lazyListState = lazyListState,
                draggedKey = key,
                dragOffset = dragOffset,
                threshold = autoScrollThresholdPx,
                scrollAmount = autoScrollStepPx,
                coroutineScope = coroutineScope,
                onAutoScroll = onAutoScroll,
            )
    }

    fun adjustDragOffset(delta: Float) {
        dragOffset += delta
    }

    suspend fun endDrag(
        items: SnapshotStateList<SettingCupsItem>,
        onReorderCups: (List<CupUiModel>) -> Unit,
    ) {
        autoScrollJob?.cancelAndJoin()
        autoScrollJob = null
        if (initialCupOrder.isNotEmpty()) {
            val changed = !items.isSameOrderAs(initialCupOrder)
            if (changed) onReorderCups(items.cupItems().map { it.value })
        }
        resetDrag()
    }

    suspend fun cancelDrag(items: SnapshotStateList<SettingCupsItem>) {
        autoScrollJob?.cancelAndJoin()
        autoScrollJob = null
        if (initialSnapshot.isNotEmpty()) {
            items.replaceAll(initialSnapshot)
        }
        resetDrag()
    }

    private fun resetDrag() {
        draggingItemKey = null
        draggingItemIndex = null
        dragOffset = 0f
        initialCupOrder = emptyList()
        initialSnapshot = emptyList()
    }
}

@Composable
fun rememberSettingCupsReorderState(
    lazyListState: LazyListState,
    coroutineScope: CoroutineScope,
    cupHeight: Dp,
    autoScrollThreshold: Dp,
    autoScrollStep: Dp,
): SettingCupsReorderState {
    val density = LocalDensity.current
    val thresholdPx = with(density) { autoScrollThreshold.toPx() }
    val stepPx = with(density) { autoScrollStep.toPx() }
    val defaultItemHeightPx = with(density) { cupHeight.toPx() }
    return remember(lazyListState, coroutineScope, thresholdPx, stepPx, defaultItemHeightPx) {
        SettingCupsReorderState(
            lazyListState = lazyListState,
            coroutineScope = coroutineScope,
            autoScrollThresholdPx = thresholdPx,
            autoScrollStepPx = stepPx,
            defaultItemHeightPx = defaultItemHeightPx,
        )
    }
}

private fun calculateTargetIndex(
    lazyListState: LazyListState,
    items: List<SettingCupsItem>,
    currentKey: Any,
    dragOffset: Float,
): Int? {
    val layoutInfo = lazyListState.layoutInfo
    val draggedInfo = layoutInfo.visibleItemsInfo.firstOrNull { it.key == currentKey } ?: return null
    val draggedMiddle = draggedInfo.offset + dragOffset + draggedInfo.size / 2f
    return when {
        dragOffset > 0f -> {
            layoutInfo.visibleItemsInfo
                .asSequence()
                .filter { it.offset > draggedInfo.offset }
                .firstOrNull { draggedMiddle > it.offset + it.size / 2f && items.getOrNull(it.index) is SettingCupsItem.CupItem }
                ?.index
        }

        dragOffset < 0f -> {
            layoutInfo.visibleItemsInfo
                .asSequence()
                .filter { it.offset < draggedInfo.offset }
                .lastOrNull { draggedMiddle < it.offset + it.size / 2f && items.getOrNull(it.index) is SettingCupsItem.CupItem }
                ?.index
        }

        else -> {
            null
        }
    }
}

private fun autoScrollIfNeeded(
    lazyListState: LazyListState,
    draggedKey: Any,
    dragOffset: Float,
    threshold: Float,
    scrollAmount: Float,
    coroutineScope: CoroutineScope,
    onAutoScroll: (Float) -> Unit,
): Job? {
    val layoutInfo = lazyListState.layoutInfo
    val draggedInfo = layoutInfo.visibleItemsInfo.firstOrNull { it.key == draggedKey } ?: return null
    val viewportStart = layoutInfo.viewportStartOffset
    val viewportEnd = layoutInfo.viewportEndOffset
    val draggedTop = draggedInfo.offset + dragOffset
    val draggedBottom = draggedTop + draggedInfo.size
    return when {
        draggedTop < viewportStart + threshold -> {
            coroutineScope.launch {
                val consumed = lazyListState.scrollBy(-scrollAmount)
                if (consumed != 0f) onAutoScroll(consumed)
            }
        }

        draggedBottom > viewportEnd - threshold -> {
            coroutineScope.launch {
                val consumed = lazyListState.scrollBy(scrollAmount)
                if (consumed != 0f) onAutoScroll(consumed)
            }
        }

        else -> {
            null
        }
    }
}

private fun SnapshotStateList<SettingCupsItem>.move(
    fromIndex: Int,
    toIndex: Int,
) {
    if (fromIndex == toIndex) return
    val item = removeAt(fromIndex)
    add(toIndex, item)
}

private fun SnapshotStateList<SettingCupsItem>.replaceAll(newItems: List<SettingCupsItem>) {
    clear()
    addAll(newItems)
}

private fun SnapshotStateList<SettingCupsItem>.isSameOrderAs(order: List<Long>): Boolean {
    val current = cupItems().map { it.value.id }
    return current == order
}

private fun SnapshotStateList<SettingCupsItem>.cupItems(): List<SettingCupsItem.CupItem> = filterIsInstance<SettingCupsItem.CupItem>()
