package com.mulkkam.ui.setting.cups.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.component.NetworkImage
import com.mulkkam.ui.designsystem.Primary100
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.setting.cups.model.CupEmojiUiModel
import com.mulkkam.ui.setting.cups.model.CupEmojisUiModel
import com.mulkkam.ui.util.ImageShape
import com.mulkkam.ui.util.extensions.noRippleClickable
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.img_cup_placeholder

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EmojiSection(
    cupEmojisUiState: MulKkamUiState<CupEmojisUiModel>,
    onSelect: (emojiId: Long) -> Unit,
) {
    when (cupEmojisUiState) {
        is MulKkamUiState.Success -> {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                cupEmojisUiState.data.cupEmojis.forEach { emoji ->
                    EmojiItem(
                        emojiUrl = emoji.cupEmojiUrl,
                        isSelected = emoji.isSelected,
                        onClick = { onSelect(emoji.id) },
                    )
                }
            }
        }

        else -> {
            Unit
        }
    }
}

@Composable
private fun EmojiItem(
    emojiUrl: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color = if (isSelected) Primary100 else Transparent)
                .noRippleClickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        NetworkImage(
            url = emojiUrl,
            shape = ImageShape.Circle,
            placeholderRes = Res.drawable.img_cup_placeholder,
            modifier =
                Modifier
                    .size(36.dp)
                    .background(color = White, shape = CircleShape),
        )

        if (isSelected) {
            Box(
                modifier =
                    Modifier
                        .matchParentSize()
                        .background(color = Transparent, shape = CircleShape),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmojiSectionPreview() {
    EmojiSection(
        cupEmojisUiState =
            MulKkamUiState.Success(
                CupEmojisUiModel(
                    cupEmojis =
                        listOf(
                            CupEmojiUiModel(
                                id = 1L,
                                cupEmojiUrl = "https://example.com/emoji1.png",
                                isSelected = true,
                            ),
                            CupEmojiUiModel(
                                id = 2L,
                                cupEmojiUrl = "https://example.com/emoji2.png",
                            ),
                        ),
                ),
            ),
        onSelect = {},
    )
}
