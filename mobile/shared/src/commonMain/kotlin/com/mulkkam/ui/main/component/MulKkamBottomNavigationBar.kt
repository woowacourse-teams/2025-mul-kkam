package com.mulkkam.ui.main.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.Gray100
import com.mulkkam.ui.designsystem.Gray200
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.main.model.MainTab
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun MulKkamBottomNavigationBar(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        HorizontalDivider(
            thickness = 1.dp,
            color = Gray100,
        )
        NavigationBar(
            containerColor = White,
        ) {
            MainTab.entries.forEach { tab ->
                MulKkamNavigationBarItem(
                    tab = tab,
                    selected = selectedTab == tab,
                    onClick = { onTabSelected(tab) },
                )
            }
        }
    }
}

@Composable
private fun RowScope.MulKkamNavigationBarItem(
    tab: MainTab,
    selected: Boolean,
    onClick: () -> Unit,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                painter = painterResource(tab.iconRes),
                contentDescription = stringResource(tab.labelRes),
            )
        },
        label = {
            Text(
                text = stringResource(tab.labelRes),
                style = MulKkamTheme.typography.label2,
            )
        },
        colors =
            NavigationBarItemDefaults.colors(
                selectedIconColor = Black,
                selectedTextColor = Black,
                unselectedIconColor = Gray200,
                unselectedTextColor = Gray200,
                indicatorColor = Color.Transparent,
            ),
        interactionSource = remember { MutableInteractionSource() },
    )
}
