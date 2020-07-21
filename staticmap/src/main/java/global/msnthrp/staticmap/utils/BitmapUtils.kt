package global.msnthrp.staticmap.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import global.msnthrp.staticmap.model.Coords

/**
 * concatenates 4 tiles into one large bitmap
 *
 * +------+------+
 * |  top |  top |
 * | left | right|
 * |      |      |
 * +------+------+
 * |bottom|bottom|
 * | left | right|
 * |      |      |
 * +------+------+
 */
internal fun combine4Tiles(topLeft: Bitmap, topRight: Bitmap, bottomLeft: Bitmap, bottomRight: Bitmap): Bitmap {
    val width = topLeft.width
    val height = topLeft.height
    val result = Bitmap.createBitmap(width * 2, height * 2, Bitmap.Config.ARGB_8888)
    Canvas(result).apply {
        drawBitmap(topLeft, 0f, 0f, null)
        drawBitmap(topRight, width.toFloat(), 0f, null)
        drawBitmap(bottomLeft, 0f, height.toFloat(), null)
        drawBitmap(bottomRight, width.toFloat(), height.toFloat(), null)
    }
    return result
}

/**
 * crop [bitmap] according to absolute [point] to make [bitmap] 2 times smaller
 * and obtain point at center
 * @param bitmap what to crop
 * @param point point in [0..1, 0..1], represents place on bitmap
 * @return cropped bitmap with centered [point]
 */
internal fun cropByOffset(bitmap: Bitmap, point: Coords): Bitmap {
    val xCenter = (point.x * bitmap.width).toInt()
    val yCenter = (point.y * bitmap.height).toInt()
    return Bitmap.createBitmap(
        bitmap,
        xCenter - bitmap.width / 4,
        yCenter - bitmap.height / 4,
        bitmap.width / 2,
        bitmap.height / 2
    )
}

/**
 * draws pin to point center of bitmap
 * @param bitmap bitmap to draw a pin on
 * @param pinIcon what to draw
 */
internal fun drawPinIconOnBitmap(bitmap: Bitmap, pinIcon: Drawable) {
    val pinBitmap = pinIcon.toBitmap()
    Canvas(bitmap).apply {
        drawBitmap(
            pinBitmap,
            (bitmap.width - pinBitmap.width) / 2f,
            bitmap.height / 2f - pinBitmap.height,
            null
        )
    }
}