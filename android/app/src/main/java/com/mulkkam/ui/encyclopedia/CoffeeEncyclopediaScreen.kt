package com.mulkkam.ui.encyclopedia

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.mulkkam.R
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.Primary300
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.encyclopedia.component.CoffeeEncyclopediaTopAppBar
import com.mulkkam.ui.util.extensions.noRippleClickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoffeeEncyclopediaScreen(
    navigateToBack: () -> Unit,
    navigateToInformationSource: (Uri) -> Unit,
) {
    val url = stringResource(R.string.coffee_encyclopedia_source)

    Scaffold(
        topBar = { CoffeeEncyclopediaTopAppBar(onBackClick = navigateToBack) },
        containerColor = White,
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .padding(20.dp),
        ) {
            Text(
                text = stringResource(R.string.coffee_encyclopedia_title),
                color = Black,
                style = MulKkamTheme.typography.title3,
            )

            Text(
                modifier = Modifier.padding(top = 15.dp),
                text = stringResource(R.string.coffee_encyclopedia_subtitle1),
                color = Black,
                style = MulKkamTheme.typography.body4,
            )

            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = stringResource(R.string.coffee_encyclopedia_content1),
                color = Black,
                style = MulKkamTheme.typography.body5,
            )

            Text(
                modifier =
                    Modifier
                        .padding(top = 15.dp),
                text = stringResource(R.string.coffee_encyclopedia_source_title),
                style = MulKkamTheme.typography.body5,
                color = Black,
            )

            Text(
                modifier =
                    Modifier
                        .noRippleClickable(
                            onClick = { navigateToInformationSource(url.toUri()) },
                        ),
                text = stringResource(R.string.coffee_encyclopedia_source),
                style = MulKkamTheme.typography.body5,
                color = Primary300,
                textDecoration = TextDecoration.Underline,
            )

            Text(
                modifier = Modifier.padding(top = 15.dp),
                text = stringResource(R.string.coffee_encyclopedia_subtitle2),
                color = Black,
                style = MulKkamTheme.typography.body4,
            )

            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = stringResource(R.string.coffee_encyclopedia_content2),
                color = Black,
                style = MulKkamTheme.typography.body5,
            )

            Text(
                modifier = Modifier.padding(top = 15.dp),
                text = stringResource(R.string.coffee_encyclopedia_subtitle3),
                color = Black,
                style = MulKkamTheme.typography.body4,
            )

            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = stringResource(R.string.coffee_encyclopedia_content3),
                color = Black,
                style = MulKkamTheme.typography.body5,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CoffeeEncyclopediaScreenPreview() {
    MulkkamTheme {
        CoffeeEncyclopediaScreen(navigateToBack = {}, navigateToInformationSource = {})
    }
}
