package com.mulkkam.ui.setting.cups.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mulkkam.domain.model.IntakeType
import com.mulkkam.ui.component.NetworkImage
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.Gray200
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.setting.cups.adapter.SettingCupsItem
import com.mulkkam.ui.setting.cups.model.CupEmojiUiModel
import com.mulkkam.ui.setting.cups.model.CupUiModel
import com.mulkkam.ui.util.ImageShape
import com.mulkkam.ui.util.extensions.noRippleClickable
import com.mulkkam.ui.util.extensions.toColorInt
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.btn_setting_cup_move
import mulkkam.shared.generated.resources.btn_setting_cups_edit
import mulkkam.shared.generated.resources.img_cup_placeholder
import mulkkam.shared.generated.resources.setting_cup_representative
import mulkkam.shared.generated.resources.setting_cups_increment
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CupItem(
    item: SettingCupsItem.CupItem,
    onEdit: () -> Unit,
    dragHandleModifier: Modifier = Modifier,
    modifier: Modifier = Modifier,
) {
    val intakeTypeColor = item.value.intakeType.toColorOrDefault()
    val intakeTypeLabel = item.value.intakeType.toLabel()

    Row(
        modifier =
            modifier
                .noRippleClickable(onEdit)
                .background(White)
                .padding(vertical = 12.dp, horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OrderDragHandle(dragHandleModifier)
        Spacer(modifier = Modifier.width(6.dp))

        CupBody(
            name = item.value.name,
            emojiUrl = item.value.emoji.cupEmojiUrl,
            isRepresentative = item.value.isRepresentative,
            intakeTypeLabel = intakeTypeLabel,
            intakeTypeColor = intakeTypeColor,
            modifier = Modifier.weight(1f),
        )

        Spacer(modifier = Modifier.width(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            CupAmount(item.value.amount, intakeTypeColor)
            Spacer(modifier = Modifier.width(6.dp))
            CupEditButton()
        }
    }
}

@Composable
private fun OrderDragHandle(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(Res.drawable.btn_setting_cup_move),
        contentDescription = null,
        modifier =
            Modifier
                .size(40.dp)
                .padding(8.dp)
                .then(modifier),
    )
}

@Composable
private fun CupBody(
    name: String,
    emojiUrl: String,
    isRepresentative: Boolean,
    intakeTypeLabel: String,
    intakeTypeColor: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NetworkImage(
            url = emojiUrl,
            placeholderRes = Res.drawable.img_cup_placeholder,
            modifier =
                Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(12.dp)),
            shape = ImageShape.Rounded(12),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isRepresentative) {
                    Text(
                        text = stringResource(Res.string.setting_cup_representative),
                        style = MulKkamTheme.typography.label2,
                        color = White,
                        modifier =
                            Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Black)
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                if (intakeTypeLabel.isNotBlank()) {
                    Text(
                        text = intakeTypeLabel,
                        style = MulKkamTheme.typography.label2,
                        color = White,
                        modifier =
                            Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(intakeTypeColor)
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                    )
                }
            }
            Text(
                text = name,
                style = MulKkamTheme.typography.body4,
                color = Black,
                modifier = Modifier.padding(top = 2.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private fun IntakeType.toColorOrDefault(): Color {
    val colorHex = toColorHex()
    return if (colorHex.isBlank()) Gray200 else Color(colorHex.toColorInt())
}

@Composable
private fun CupAmount(
    amount: Int,
    textColor: Color,
) {
    Text(
        text = stringResource(Res.string.setting_cups_increment, amount),
        style = MulKkamTheme.typography.body4,
        color = textColor,
    )
}

@Composable
private fun CupEditButton() {
    Image(
        painter = painterResource(Res.drawable.btn_setting_cups_edit),
        contentDescription = null,
        modifier =
            Modifier
                .size(40.dp)
                .padding(6.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun CupItemPreview() {
    MulKkamTheme {
        CupItem(
            item =
                SettingCupsItem.CupItem(
                    CupUiModel(
                        id = 1L,
                        name = "종이컵 1컵",
                        amount = 1200,
                        rank = 1,
                        intakeType = IntakeType.WATER,
                        emoji = CupEmojiUiModel(id = 1L, cupEmojiUrl = ""),
                        isRepresentative = true,
                    ),
                ),
            onEdit = {},
            dragHandleModifier = Modifier,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
        )
    }
}
