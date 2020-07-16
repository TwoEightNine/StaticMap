package global.msnthrp.staticmap.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap
import global.msnthrp.staticmap.R
import global.msnthrp.staticmap.loader.StaticMapLoader
import global.msnthrp.staticmap.model.LatLng
import global.msnthrp.staticmap.tile.TileLoader
import global.msnthrp.staticmap.tile.TileProvider

class StaticMapView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attributeSet, defStyleAttr) {

    var pinIcon: Drawable? = null
        set(value) {
            field = value
            invalidate()
        }

    var latLng: LatLng? = null
        set(value) {
            field = value
            tryLoadMap()
        }

    var zoom: Int = ZOOM_DEFAULT
        set(value) {
            if (value in ZOOM_MIN..ZOOM_MAX) {
                field = value
                tryLoadMap()
            }
        }

    private var loader: StaticMapLoader? = null

    private var widthInternal = 0
    private var heightInternal = 0

    init {
        scaleType = ScaleType.CENTER_CROP
        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.StaticMapView,
            0, 0).apply {

            try {
                pinIcon = getDrawable(R.styleable.StaticMapView_pinIcon)
            } finally {
                recycle()
            }
        }
    }

    fun setConfig(config: Config) {
        loader = StaticMapLoader(
            config.tileLoader,
            config.tileProvider,
            config.tileCacheSize,
            config.mapCacheSize
        )
        tryLoadMap()
    }

    fun zoomIn() {
        zoom++
    }

    fun zoomOut() {
        zoom--
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        widthInternal = w
        heightInternal = h
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        latLng ?: return

        pinIcon?.toBitmap()?.also { pin ->
            canvas?.drawBitmap(pin, (widthInternal - pin.width) / 2f, heightInternal / 2f - pin.height, null)
        }
    }

    private fun tryLoadMap() {
        loader?.cancelAll()
        loader?.load(latLng ?: return, zoom, OnMapLoaded())
    }

    companion object {

        const val ZOOM_MIN = 0
        const val ZOOM_DEFAULT = 16
        const val ZOOM_MAX = 22
    }

    data class Config(
        val tileProvider: TileProvider,
        val tileLoader: TileLoader,
        val tileCacheSize: Int = 100,
        val mapCacheSize: Int = tileCacheSize / 4
    )

    private inner class OnMapLoaded : StaticMapLoader.Callback {
        override fun onMapLoaded(bitmap: Bitmap) {
            setImageBitmap(bitmap)
        }

        override fun onMapFailed(errorMessage: String) {
        }
    }
}