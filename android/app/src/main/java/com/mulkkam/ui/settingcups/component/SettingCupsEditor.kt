package com.mulkkam.ui.settingcups.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.mulkkam.domain.model.intake.IntakeType
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.settingcups.adapter.SettingCupsItem
import com.mulkkam.ui.settingcups.model.CupEmojiUiModel
import com.mulkkam.ui.settingcups.model.CupUiModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

val SETTING_CUPS_CUP_HEIGHT: Dp = 64.dp
private val SETTING_CUPS_AUTO_SCROLL_THRESHOLD: Dp = 60.dp
private val SETTING_CUPS_AUTO_SCROLL_STEP: Dp = 24.dp

@Composable
fun SettingCupsEditor(
    items: SnapshotStateList<SettingCupsItem>,
    onEditCup: (CupUiModel) -> Unit,
    onAddCup: () -> Unit,
    onReorderCups: (List<CupUiModel>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyListState: LazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val autoScrollThresholdPx: Float = with(density) { SETTING_CUPS_AUTO_SCROLL_THRESHOLD.toPx() }
    val autoScrollStepPx: Float = with(density) { SETTING_CUPS_AUTO_SCROLL_STEP.toPx() }
    val defaultItemHeightPx: Float = with(density) { SETTING_CUPS_CUP_HEIGHT.toPx() }

    var draggingItemKey by remember { mutableStateOf<Any?>(null) }
    var draggingItemIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    var initialCupOrder by remember { mutableStateOf(emptyList<Long>()) }
    var initialSnapshot by remember { mutableStateOf(emptyList<SettingCupsItem>()) }
    var autoScrollJob by remember { mutableStateOf<Job?>(null) }

    LazyColumn(
        state = lazyListState,
        modifier = modifier.background(White),
    ) {
        itemsIndexed(
            items = items,
            key = { _, item -> item.toStableKey() },
        ) { index, item ->
            when (item) {
                is SettingCupsItem.CupItem -> {
                    val itemKey = item.toStableKey()
                    val isDragging = draggingItemKey == itemKey
                    val itemModifier =
                        Modifier
                            .fillMaxWidth()
                            .zIndex(if (isDragging) 1f else 0f)
                            .offset { IntOffset(x = 0, y = if (isDragging) dragOffset.roundToInt() else 0) }
                    val dragHandleModifier: Modifier =
                        Modifier.pointerInput(itemKey) {
                            detectDragGestures(
                                onDragStart = {
                                    draggingItemKey = itemKey
                                    val currentIndex: Int =
                                        items
                                            .indexOfFirst { listItem -> listItem.toStableKey() == itemKey }
                                            .takeIf { position -> position >= 0 }
                                            ?: index
                                    draggingItemIndex = currentIndex
                                    dragOffset = 0f
                                    initialCupOrder = items.cupItems().map { cupItem -> cupItem.value.id }
                                    initialSnapshot = items.toList()
                                },
                                onDrag = { change, dragAmount ->
                                    if (draggingItemKey != itemKey) return@detectDragGestures
                                    change.consume()
                                    val currentIndex: Int = draggingItemIndex ?: return@detectDragGestures
                                    dragOffset += dragAmount.y
                                    val targetIndex: Int? =
                                        calculateTargetIndex(
                                            lazyListState = lazyListState,
                                            items = items,
                                            currentKey = itemKey,
                                            dragOffset = dragOffset,
                                        )
                                    if (targetIndex != null &&
                                        targetIndex != currentIndex &&
                                        items.getOrNull(targetIndex) is SettingCupsItem.CupItem
                                    ) {
                                        val targetItemInfo: LazyListItemInfo? =
                                            lazyListState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == targetIndex }
                                        items.move(currentIndex, targetIndex)
                                        draggingItemIndex = targetIndex
                                        val direction = if (targetIndex > currentIndex) 1 else -1
                                        val adjustment = targetItemInfo?.size?.toFloat() ?: defaultItemHeightPx
                                        dragOffset -= direction * adjustment
                                    }

                                    autoScrollJob?.cancel()
                                    autoScrollJob =
                                        autoScrollIfNeeded(
                                            lazyListState = lazyListState,
                                            draggedKey = itemKey,
                                            dragOffset = dragOffset,
                                            threshold = autoScrollThresholdPx,
                                            scrollAmount = autoScrollStepPx,
                                            coroutineScope = coroutineScope,
                                            onAutoScroll = { scrolled -> dragOffset += scrolled },
                                        )
                                },
                                onDragEnd = {
                                    if (draggingItemKey != itemKey) return@detectDragGestures
                                    autoScrollJob?.cancel()
                                    autoScrollJob = null
                                    finishDrag(
                                        items = items,
                                        initialCupOrder = initialCupOrder,
                                        onReorderCups = onReorderCups,
                                    )
                                    draggingItemKey = null
                                    draggingItemIndex = null
                                    dragOffset = 0f
                                    initialCupOrder = emptyList()
                                    initialSnapshot = emptyList()
                                },
                                onDragCancel = {
                                    if (draggingItemKey != itemKey) return@detectDragGestures
                                    autoScrollJob?.cancel()
                                    autoScrollJob = null
                                    if (initialSnapshot.isNotEmpty()) {
                                        items.replaceAll(initialSnapshot)
                                    }
                                    draggingItemKey = null
                                    draggingItemIndex = null
                                    dragOffset = 0f
                                    initialCupOrder = emptyList()
                                    initialSnapshot = emptyList()
                                },
                            )
                        }
                    SettingCupsCup(
                        item = item,
                        onEdit = { onEditCup(item.value) },
                        dragHandleModifier = dragHandleModifier,
                        modifier = itemModifier,
                    )
                }

                SettingCupsItem.AddItem -> {
                    Spacer(modifier = Modifier.size(18.dp))
                    SettingCupsAdd(
                        onClick = onAddCup,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                    )
                }
            }
        }
    }
}

private fun calculateTargetIndex(
    lazyListState: LazyListState,
    items: List<SettingCupsItem>,
    currentKey: Any,
    dragOffset: Float,
): Int? {
    val layoutInfo = lazyListState.layoutInfo
    val draggedItemInfo: LazyListItemInfo =
        layoutInfo.visibleItemsInfo.firstOrNull { it.key == currentKey } ?: return null
    val draggedMiddle: Float = draggedItemInfo.offset + dragOffset + draggedItemInfo.size / 2f

    return when {
        dragOffset > 0f ->
            layoutInfo.visibleItemsInfo
                .filter { it.offset > draggedItemInfo.offset }
                .firstOrNull { draggedMiddle > it.offset + it.size / 2f && items.getOrNull(it.index) is SettingCupsItem.CupItem }
                ?.index

        dragOffset < 0f ->
            layoutInfo.visibleItemsInfo
                .filter { it.offset < draggedItemInfo.offset }
                .lastOrNull { draggedMiddle < it.offset + it.size / 2f && items.getOrNull(it.index) is SettingCupsItem.CupItem }
                ?.index

        else -> null
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
    val draggedItemInfo: LazyListItemInfo =
        layoutInfo.visibleItemsInfo.firstOrNull { it.key == draggedKey } ?: return null
    val viewportStart = layoutInfo.viewportStartOffset
    val viewportEnd = layoutInfo.viewportEndOffset
    val draggedTop = draggedItemInfo.offset + dragOffset
    val draggedBottom = draggedTop + draggedItemInfo.size

    return when {
        draggedTop < viewportStart + threshold ->
            coroutineScope.launch {
                val consumed = lazyListState.scrollBy(-scrollAmount)
                if (consumed != 0f) {
                    onAutoScroll(consumed)
                }
            }

        draggedBottom > viewportEnd - threshold ->
            coroutineScope.launch {
                val consumed = lazyListState.scrollBy(scrollAmount)
                if (consumed != 0f) {
                    onAutoScroll(consumed)
                }
            }

        else -> null
    }
}

private fun finishDrag(
    items: SnapshotStateList<SettingCupsItem>,
    initialCupOrder: List<Long>,
    onReorderCups: (List<CupUiModel>) -> Unit,
) {
    if (initialCupOrder.isEmpty()) return
    val currentOrder: List<Long> = items.cupItems().map { item -> item.value.id }
    if (currentOrder == initialCupOrder) return
    onReorderCups(items.cupItems().map { item -> item.value })
}

private fun SettingCupsItem.toStableKey(): Any =
    when (this) {
        is SettingCupsItem.CupItem -> value.id
        SettingCupsItem.AddItem -> "add_item"
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

private fun SnapshotStateList<SettingCupsItem>.cupItems(): List<SettingCupsItem.CupItem> = filterIsInstance<SettingCupsItem.CupItem>()

@Preview(showBackground = true)
@Composable
private fun SettingCupsEditorPreview() {
    MulkkamTheme {
        val previewItems =
            remember {
                mutableStateListOf<SettingCupsItem>().apply {
                    addAll(
                        listOf(
                            CupUiModel(
                                id = 1L,
                                name = "대표 컵",
                                amount = 300,
                                rank = 1,
                                intakeType = IntakeType.WATER,
                                emoji = CupEmojiUiModel(id = 1L, cupEmojiUrl = ""),
                                isRepresentative = true,
                            ),
                            CupUiModel(
                                id = 2L,
                                name = "보조 컵",
                                amount = 250,
                                rank = 2,
                                intakeType = IntakeType.COFFEE,
                                emoji = CupEmojiUiModel(id = 2L, cupEmojiUrl = ""),
                                isRepresentative = false,
                            ),
                        ).map { cup -> SettingCupsItem.CupItem(cup) },
                    )
                    add(SettingCupsItem.AddItem)
                }
            }
        SettingCupsEditor(
            items = previewItems,
            onEditCup = {},
            onAddCup = {},
            onReorderCups = {},
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
        )
    }
}
