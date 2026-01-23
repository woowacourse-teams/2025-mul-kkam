package com.mulkkam.ui.home.encyclopedia

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Primary300
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.home.encyclopedia.component.CoffeeEncyclopediaTopAppBar
import com.mulkkam.ui.util.extensions.noRippleClickable
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.coffee_encyclopedia_content1
import mulkkam.shared.generated.resources.coffee_encyclopedia_content2
import mulkkam.shared.generated.resources.coffee_encyclopedia_content3
import mulkkam.shared.generated.resources.coffee_encyclopedia_source
import mulkkam.shared.generated.resources.coffee_encyclopedia_source_title
import mulkkam.shared.generated.resources.coffee_encyclopedia_subtitle1
import mulkkam.shared.generated.resources.coffee_encyclopedia_subtitle2
import mulkkam.shared.generated.resources.coffee_encyclopedia_subtitle3
import mulkkam.shared.generated.resources.coffee_encyclopedia_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun EncyclopediaScreen(
    padding: PaddingValues,
    navigateToBack: () -> Unit,
    navigateToInformationSource: (uri: String) -> Unit,
) {
    val coffeeSource = stringResource(Res.string.coffee_encyclopedia_source)

    Scaffold(
        topBar = { CoffeeEncyclopediaTopAppBar { navigateToBack() } },
        containerColor = White,
        modifier = Modifier.background(White).padding(padding),
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .padding(20.dp),
        ) {
            Text(
                text = stringResource(resource = Res.string.coffee_encyclopedia_title),
                color = Black,
                style = MulKkamTheme.typography.title3,
            )

            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = stringResource(resource = Res.string.coffee_encyclopedia_subtitle1),
                color = Black,
                style = MulKkamTheme.typography.body4,
            )

            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = stringResource(resource = Res.string.coffee_encyclopedia_content1),
                color = Black,
                style = MulKkamTheme.typography.body5,
            )

            Text(
                modifier =
                    Modifier
                        .padding(top = 16.dp),
                text = stringResource(resource = Res.string.coffee_encyclopedia_source_title),
                style = MulKkamTheme.typography.body5,
                color = Black,
            )

            Text(
                modifier =
                    Modifier
                        .noRippleClickable(
                            onClick = { navigateToInformationSource(coffeeSource) },
                        ),
                text = stringResource(resource = Res.string.coffee_encyclopedia_source),
                style = MulKkamTheme.typography.body5,
                color = Primary300,
                textDecoration = TextDecoration.Underline,
            )

            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = stringResource(resource = Res.string.coffee_encyclopedia_subtitle2),
                color = Black,
                style = MulKkamTheme.typography.body4,
            )

            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = stringResource(resource = Res.string.coffee_encyclopedia_content2),
                color = Black,
                style = MulKkamTheme.typography.body5,
            )

            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = stringResource(resource = Res.string.coffee_encyclopedia_subtitle3),
                color = Black,
                style = MulKkamTheme.typography.body4,
            )

            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = stringResource(resource = Res.string.coffee_encyclopedia_content3),
                color = Black,
                style = MulKkamTheme.typography.body5,
            )
        }
    }
}
