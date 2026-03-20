package com.mulkkam.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

@Stable
class NavEntry<T : Any>(
    val key: T,
    val content: @Composable () -> Unit,
)

@Composable
fun <T : Any> entry(
    key: T,
    content: @Composable () -> Unit,
): NavEntry<T> = NavEntry(key = key, content = content)
