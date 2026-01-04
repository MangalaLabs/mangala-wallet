package com.mangala.wallet.features.addressbook.presentation.avatar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.component.setupDefaultComponents
import okio.Path.Companion.toPath

/**
 * Cung cấp ImageLoader cho toàn bộ ứng dụng
 */
@Composable
expect fun ProvideImageLoader(content: @Composable () -> Unit)