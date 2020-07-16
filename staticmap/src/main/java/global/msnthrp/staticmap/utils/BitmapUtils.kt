package global.msnthrp.staticmap.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import global.msnthrp.staticmap.model.Coords

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

internal fun cropByOffset(bitmap: Bitmap, offset: Coords): Bitmap {
    val xCenter = (offset.x * bitmap.width).toInt()
    val yCenter = (offset.y * bitmap.height).toInt()
    return Bitmap.createBitmap(
        bitmap,
        xCenter - bitmap.width / 4,
        yCenter - bitmap.height / 4,
        bitmap.width / 2,
        bitmap.height / 2
    )
}