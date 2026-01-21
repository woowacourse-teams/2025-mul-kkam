package com.mulkkam.ui.onboarding.cups.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.mulkkam.domain.model.IntakeType
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.onboarding.cups.model.rememberCupsReorderState
import com.mulkkam.ui.setting.cups.component.SettingCupsAdd
import com.mulkkam.ui.setting.cups.component.SettingCupsCup
import com.mulkkam.ui.settingcups.adapter.SettingCupsItem
import com.mulkkam.ui.settingcups.model.CupEmojiUiModel
import com.mulkkam.ui.settingcups.model.CupUiModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.roundToInt

val SETTING_CUPS_CUP_HEIGHT: Dp = 64.dp
private val SETTING_CUPS_AUTO_SCROLL_THRESHOLD: Dp = 60.dp
private val SETTING_CUPS_AUTO_SCROLL_STEP: Dp = 24.dp
private const val ADD_CUP_KEY: String = "ADD_CUP_KEY"

@Composable
fun CupsEditor(
    items: SnapshotStateList<SettingCupsItem>,
    onEditCup: (cup: CupUiModel) -> Unit,
    onAddCup: () -> Unit,
    onReorderCups: (reorderCups: List<CupUiModel>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val reorderState =
        rememberCupsReorderState(
            lazyListState = lazyListState,
            coroutineScope = scope,
            cupHeight = SETTING_CUPS_CUP_HEIGHT,
            autoScrollThreshold = SETTING_CUPS_AUTO_SCROLL_THRESHOLD,
            autoScrollStep = SETTING_CUPS_AUTO_SCROLL_STEP,
        )

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
                    val isDragging = reorderState.draggingItemKey == itemKey
                    val itemModifier =
                        Modifier
                            .fillMaxWidth()
                            .zIndex(if (isDragging) 1f else 0f)
                            .offset {
                                IntOffset(
                                    x = 0,
                                    y = if (isDragging) reorderState.dragOffset.roundToInt() else 0,
                                )
                            }
                    val dragHandleModifier =
                        Modifier.pointerInput(itemKey) {
                            detectDragGestures(
                                onDragStart = {
                                    reorderState.beginDrag(
                                        items,
                                        fallbackIndex = index,
                                        key = itemKey,
                                    )
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    reorderState.updateDrag(
                                        items = items,
                                        dragDeltaY = dragAmount.y,
                                        onAutoScroll = { scrolled -> reorderState.apply { dragOffset += scrolled } },
                                    )
                                },
                                onDragEnd = {
                                    scope.launch { reorderState.endDrag(items, onReorderCups) }
                                },
                                onDragCancel = {
                                    scope.launch { reorderState.cancelDrag(items) }
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

                is SettingCupsItem.AddItem -> {
                    Spacer(modifier = Modifier.size(18.dp))
                    SettingCupsAdd(
                        onClick = onAddCup,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    )
                }
            }
        }
    }
}

private fun SettingCupsItem.toStableKey(): Any =
    when (this) {
        is SettingCupsItem.CupItem -> this.hashCode()
        is SettingCupsItem.AddItem -> ADD_CUP_KEY
    }

@Preview(showBackground = true)
@Composable
private fun CupsEditorPreview() {
    MulKkamTheme {
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
        CupsEditor(
            items = previewItems,
            onEditCup = {},
            onAddCup = {},
            onReorderCups = {},
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        )
    }
}
