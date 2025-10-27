package com.mulkkam.ui.searchmembers.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.domain.model.members.Nickname
import com.mulkkam.ui.designsystem.Gray300
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme

@Composable
fun SearchMembersTextField(
    name: String,
    onValueChanged: (String) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    BasicTextField(
        value = name,
        onValueChange = {
            if (it.length <= Nickname.NICKNAME_LENGTH_MAX) {
                onValueChanged(it)
            }
        },
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .height(44.dp)
                .border(width = 1.dp, color = Gray400, shape = RoundedCornerShape(8.dp)),
        textStyle = MulKkamTheme.typography.body2.copy(color = Gray400),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions =
            KeyboardActions(onDone = {
                keyboardController?.hide()
            }),
        decorationBox = { innerTextField ->
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
                    painter = painterResource(R.drawable.ic_search_friends_search),
                    contentDescription = null,
                    tint = Gray300,
                )
                innerTextField()
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
fun SearchMembersTextFieldPreview() {
    MulkkamTheme {
        SearchMembersTextField(name = "", onValueChanged = {})
    }
}
