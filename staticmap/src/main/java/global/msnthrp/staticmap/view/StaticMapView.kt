package global.msnthrp.staticmap.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import global.msnthrp.staticmap.R
import global.msnthrp.staticmap.model.LatLng

class StaticMapView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attributeSet, defStyleAttr) {

    private val pinIcon: Drawable?

    private var latitude: Double
    private var longitude: Double

    init {
        val attrsArray = intArrayOf(
            R.attr.latitude,
            R.attr.longitude,
            R.attr.pinIcon
        )
        context.obtainStyledAttributes(attributeSet, attrsArray).apply {
            latitude = getFloat(R.styleable.StaticMapView_latitude, 0f).toDouble()
            longitude = getFloat(R.styleable.StaticMapView_longitude, 0f).toDouble()
            pinIcon = getDrawable(R.styleable.StaticMapView_pinIcon)
                ?: ContextCompat.getDrawable(context, R.drawable.ic_default_pin)
            recycle()
        }
    }

    fun setLatLng(latLng: LatLng) {
        latitude = latLng.latitude
        longitude = latLng.longitude
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

    }
}