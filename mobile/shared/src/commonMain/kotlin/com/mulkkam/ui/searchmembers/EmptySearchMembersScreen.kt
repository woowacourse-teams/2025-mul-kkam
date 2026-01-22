package com.mulkkam.ui.searchmembers

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.img_crying_character
import mulkkam.shared.generated.resources.search_friends_empty
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EmptySearchMembersScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(Res.drawable.img_crying_character),
            modifier = Modifier.size(240.dp),
            contentDescription = null,
        )
        Text(
            text = stringResource(Res.string.search_friends_empty),
            style = MulKkamTheme.typography.body2,
            color = Gray400,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptySearchMembersScreenPreview() {
    MulKkamTheme {
        EmptySearchMembersScreen(modifier = Modifier.fillMaxSize())
    }
}
