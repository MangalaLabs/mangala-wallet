package com.mangala.wallet.ui.imageloader

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import com.seiko.imageloader.ImageLoaderConfigBuilder
//import com.seiko.imageloader.intercept.BlurInterceptor
import com.seiko.imageloader.intercept.Interceptor
import com.seiko.imageloader.model.ImageResult
import com.seiko.imageloader.model.NullRequestData
import com.seiko.imageloader.util.LogPriority
import com.seiko.imageloader.util.Logger

fun ImageLoaderConfigBuilder.commonConfig() {
    logger = object : Logger {
        override fun log(
            priority: LogPriority,
            tag: String,
            data: Any?,
            throwable: Throwable?,
            message: String,
        ) {
            println("ImageLoader $message throwable? $throwable data? $data")
        }

        override fun isLoggable(priority: LogPriority) = priority >= LogPriority.DEBUG
    }
    interceptor {
//        addInterceptor(BlurInterceptor()) // TODO: Do we need this?
    }
}

/**
 * return empty painter if request is null or empty
 */
object NullDataInterceptor : Interceptor {

    override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
        val data = chain.request.data
        if (data === NullRequestData || data is String && data.isEmpty()) {
            return ImageResult.OfPainter(
                painter = EmptyPainter
            )
        }
        return chain.proceed(chain.request)
    }

    private object EmptyPainter : Painter() {
        override val intrinsicSize: Size get() = Size.Unspecified
        override fun DrawScope.onDraw() {}
    }
}