package com.mulkkam.ui.core

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
): com.mulkkam.ui.core.NavEntry<T> =
    _root_ide_package_.com.mulkkam.ui.core
        .NavEntry(key = key, content = content)
