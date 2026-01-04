package com.mangala.wallet.features.addressbook.presentation.avatar


import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.intercept.bitmapMemoryCacheConfig
import com.seiko.imageloader.intercept.imageMemoryCacheConfig
import com.seiko.imageloader.intercept.painterMemoryCacheConfig
import com.seiko.imageloader.model.ImageResult
import com.seiko.imageloader.intercept.Interceptor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.geometry.Size
import okio.Path.Companion.toOkioPath

/**
 * Android-specific implementation của ProvideImageLoader
 */
@Composable
actual fun ProvideImageLoader(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val imageLoader = remember(context) {
        createImageLoader(context)
    }

    CompositionLocalProvider(LocalImageLoader provides imageLoader) {
        content()
    }
}

/**
 * Tạo ImageLoader cho Android
 */
private fun createImageLoader(context: Context): ImageLoader {
    return ImageLoader {
        components {
            setupDefaultComponents()
        }
        interceptor {
            // Cấu hình bộ memory cache
            bitmapMemoryCacheConfig {
                // Cache bitmaps có kích thước tối đa 32MB
                maxSize(32 * 1024 * 1024) // 32MB
            }
            // Cache 50 images trong memory
            imageMemoryCacheConfig {
                maxSize(50)
            }
            // Cache 50 painters trong memory
            painterMemoryCacheConfig {
                maxSize(50)
            }
            // Cấu hình disk cache
            diskCacheConfig {
                directory(context.cacheDir.resolve("image_cache").toOkioPath())
                maxSizeBytes(512L * 1024 * 1024) // 512MB
            }
            
            // Thêm Interceptor để xử lý file path
            addInterceptor(object : Interceptor {
                override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
                    val request = chain.request
                    val data = request.data
                    if (data is String && data.startsWith("file:///")) {
                        val filePath = data.substringAfter("file://")
                        val file = java.io.File(filePath)
                        if (!file.exists() || file.length() == 0L) {
                            println("LeonardImageLoader: File không tồn tại hoặc rỗng: $filePath")
                            return ImageResult.OfPainter(
                                painter = object : Painter() {
                                    override val intrinsicSize = Size.Unspecified
                                    override fun androidx.compose.ui.graphics.drawscope.DrawScope.onDraw() {}
                                }
                            )
                        }
                    }
                    return chain.proceed(request)
                }
            })
            
            // Log chi tiết hơn để debug
            logger = object : com.seiko.imageloader.util.Logger {
                override fun log(
                    priority: com.seiko.imageloader.util.LogPriority,
                    tag: String,
                    data: Any?,
                    throwable: Throwable?,
                    message: String,
                ) {
                    println("LeonardImageLoader: [$priority][$tag] $message, data=$data, throwable=$throwable")
                }
                
                override fun isLoggable(priority: com.seiko.imageloader.util.LogPriority): Boolean {
                    return true // Log tất cả để debug
                }
            }
        }
    }
}