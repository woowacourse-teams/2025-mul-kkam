package com.mulkkam.ui.util

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.annotation.ExperimentalCoilApi
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade

@OptIn(ExperimentalCoilApi::class)
fun ImageLoader(context: PlatformContext): ImageLoader =
    ImageLoader
        .Builder(context)
        .components {
            add(KtorNetworkFetcherFactory())
        }.memoryCache {
            MemoryCache
                .Builder()
                .maxSizePercent(context, percent = 0.25)
                .build()
        }.crossfade(true)
        .build()
