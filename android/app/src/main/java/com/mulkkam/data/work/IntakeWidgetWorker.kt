package com.mulkkam.data.work

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import coil3.BitmapImage
import coil3.DrawableImage
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.svg.SvgDecoder
import com.mulkkam.domain.checker.IntakeChecker.Companion.KEY_INTAKE_CHECKER_ACHIEVEMENT_RATE
import com.mulkkam.domain.checker.IntakeChecker.Companion.KEY_INTAKE_CHECKER_CUP_ID
import com.mulkkam.domain.checker.IntakeChecker.Companion.KEY_INTAKE_CHECKER_EMOJI_BYTES
import com.mulkkam.domain.checker.IntakeChecker.Companion.KEY_INTAKE_CHECKER_TARGET_AMOUNT
import com.mulkkam.domain.checker.IntakeChecker.Companion.KEY_INTAKE_CHECKER_TOTAL_AMOUNT
import com.mulkkam.domain.repository.CupsRepository
import com.mulkkam.domain.repository.MembersRepository
import java.io.ByteArrayOutputStream
import java.time.LocalDate

class IntakeWidgetWorker(
    appContext: Context,
    params: WorkerParameters,
    private val membersRepository: MembersRepository,
    private val cupsRepository: CupsRepository,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result =
        runCatching {
            val todayProgress = membersRepository.getMembersProgressInfo(LocalDate.now()).getOrError()
            val cups = cupsRepository.getCups().getOrError()
            val representativeCup = cups.representativeCup
            val cupId = representativeCup?.id ?: 0L
            val emojiUrl = representativeCup?.emoji?.cupEmojiUrl.orEmpty()

            val targetPixelSize = 52f.toPixel(applicationContext).toInt().coerceAtLeast(16)
            val rawBitmap = loadBitmapFromUrl(applicationContext, emojiUrl, targetPixelSize, targetPixelSize)
            val scaledBitmap = rawBitmap?.scaleDown(targetPixelSize)
            val emojiBytes = scaledBitmap?.toBytes(Bitmap.CompressFormat.PNG) ?: ByteArray(0)

            workDataOf(
                KEY_INTAKE_CHECKER_ACHIEVEMENT_RATE to todayProgress.achievementRate,
                KEY_INTAKE_CHECKER_TARGET_AMOUNT to todayProgress.targetAmount,
                KEY_INTAKE_CHECKER_TOTAL_AMOUNT to todayProgress.totalAmount,
                KEY_INTAKE_CHECKER_CUP_ID to cupId,
                KEY_INTAKE_CHECKER_EMOJI_BYTES to emojiBytes,
            )
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure() },
        )
}

fun Float.toPixel(context: Context): Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics)

private suspend fun loadBitmapFromUrl(
    context: Context,
    url: String,
    width: Int,
    height: Int,
): Bitmap? {
    if (url.isBlank()) return null
    val loader =
        ImageLoader
            .Builder(context)
            .components { add(SvgDecoder.Factory()) }
            .build()

    val request =
        ImageRequest
            .Builder(context)
            .data(url)
            .allowHardware(false)
            .size(width, height)
            .build()

    return when (val image = loader.execute(request).image) {
        is BitmapImage -> image.bitmap
        is DrawableImage -> image.drawable.toBitmap(width, height)
        else -> null
    }
}

fun Drawable.toBitmap(
    width: Int,
    height: Int,
): Bitmap {
    val bitmap = createBitmap(width, height)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, width, height)
    draw(canvas)
    return bitmap
}

fun Bitmap.scaleDown(targetPixelSize: Int): Bitmap {
    if (width <= targetPixelSize && height <= targetPixelSize) return this
    val scaleRatio = targetPixelSize.toFloat() / width.toFloat()
    val newWidth = targetPixelSize
    val newHeight = (height * scaleRatio).toInt().coerceAtLeast(1)
    return scale(newWidth, newHeight, filter = true)
}

fun Bitmap.toBytes(format: CompressFormat): ByteArray =
    ByteArrayOutputStream().use { outputStream ->
        compress(format, 100, outputStream)
        outputStream.toByteArray()
    }
