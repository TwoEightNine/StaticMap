package global.msnthrp.staticmap.view

import android.R.attr.radius
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
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

    /**
     * pin drawable that appears in the center
     */
    var pinIcon: Drawable? = null
        set(value) {
            field = value
            invalidate()
        }

    /**
     * latitude and longitude to show
     * see [LatLng]
     */
    var latLng: LatLng? = null
        set(value) {
            field = value
            tryLoadMap()
        }

    /**
     * zoom to show
     * should be in [ZOOM_MIN]..[ZOOM_MAX]
     */
    var zoom: Int = ZOOM_DEFAULT
        set(value) {
            if (value in ZOOM_MIN..ZOOM_MAX) {
                field = value
                tryLoadMap()
            }
        }

    /**
     * tile and map loader
     * loads tiles by [LatLng], concatenates them into [Bitmap], provides caching
     */
    private var loader: StaticMapLoader? = null

    /**
     * view's size after [onSizeChanged]
     */
    private var widthInternal = 0
    private var heightInternal = 0

    /**
     * used for adding corner radii
     */
    private var cornerRadii = FloatArray(8)
    private val path = Path()
    private var rect = RectF(0f, 0f, 0f, 0f)

    init {
        scaleType = ScaleType.CENTER_CROP
        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.StaticMapView,
            0, 0
        ).apply {

            try {
                pinIcon = getDrawable(R.styleable.StaticMapView_pinIcon)
                val cornerRadius = getDimension(R.styleable.StaticMapView_cornerRadius, 0f)
                cornerRadii.fill(cornerRadius)
            } finally {
                recycle()
            }
        }
    }

    /**
     * sets configurations of [StaticMapView], then reloads map
     * @param config see [Config]
     */
    fun setConfig(config: Config) {
        loader = StaticMapLoader(
            config.tileLoader,
            config.tileProvider,
            config.tileCacheSize,
            config.mapCacheSize
        )
        tryLoadMap()
    }

    /**
     * increases zoom by one
     */
    fun zoomIn() {
        zoom++
    }

    /**
     * decreases zoom by one
     */
    fun zoomOut() {
        zoom--
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rect.bottom = h.toFloat()
        rect.right = w.toFloat()
        widthInternal = w
        heightInternal = h
    }

    override fun onDraw(canvas: Canvas?) {
        path.addRoundRect(rect, cornerRadii, Path.Direction.CW)
        canvas?.clipPath(path)
        super.onDraw(canvas)

        latLng ?: return

        pinIcon?.toBitmap()?.also { pin ->
            canvas?.drawBitmap(
                pin,
                (widthInternal - pin.width) / 2f,
                heightInternal / 2f - pin.height,
                null
            )
        }
    }

    /**
     * cancels previous loadings, checks for non-null [latLng], loads new map
     */
    private fun tryLoadMap() {
        loader?.cancelAll()
        loader?.load(latLng ?: return, zoom, OnMapLoaded())
    }

    companion object {

        const val ZOOM_MIN = 0
        const val ZOOM_DEFAULT = 16
        const val ZOOM_MAX = 22
    }

    /**
     * configuration of [StaticMapView]
     * @param tileProvider converts x, y, z into tile url
     * @param tileLoader loads tile by url into bitmap
     * @param tileCacheSize how many tiles are cached in LRU, default 100
     * @param mapCacheSize how many concatenated maps are cached in LRU,
     *                      default [tileCacheSize] * 0.25
     */
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