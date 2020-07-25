package global.msnthrp.staticmap.core

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import global.msnthrp.staticmap.loader.StaticMapLoader
import global.msnthrp.staticmap.loader.StaticMapLoaderCallback
import global.msnthrp.staticmap.model.LatLngZoom
import global.msnthrp.staticmap.tile.TileEssential

class StaticMapRequest internal constructor(
    private val tileEssential: TileEssential
) {

    private var latLngZoom: LatLngZoom? = null
    private var pinIcon: Drawable? = null

    private var clearBeforeLoading = false

    /**
     * to specify point to show
     * @param latLngZoom point to show
     * @return [StaticMap]
     */
    fun load(latLngZoom: LatLngZoom) = apply {
        this.latLngZoom = latLngZoom
    }

    /**
     * to specify pin drawable (ot null if none)
     * @param pinIcon drawable pin icon
     * @return [StaticMapRequest]
     */
    fun pin(pinIcon: Drawable?) = apply {
        this.pinIcon = pinIcon
    }

    /**
     * to specify if view is needed to be cleared
     * @param clear true if clear view before loading
     * @return [StaticMapRequest]
     */
    fun clearBeforeLoading(clear: Boolean) = apply {
        clearBeforeLoading = clear
    }

    /**
     * launches loading a static map into [callback]
     * @param callback what will handle the loaded map
     */
    fun into(callback: StaticMapLoaderCallback) {
        val latLngZoom = latLngZoom
        check(latLngZoom != null) {
            "LatLngZoom must be specified. Use load(LatLngZoom)"
        }
        StaticMapLoader.load(tileEssential, latLngZoom, pinIcon, callback)
    }

    /**
     * launches loading a static map into [ImageView]
     * @param imageView where to load the map
     */
    fun into(imageView: ImageView) {
        if (clearBeforeLoading) {
            imageView.setImageDrawable(null)
        }
        into(object : StaticMapLoaderCallback {
            override fun onMapLoaded(bitmap: Bitmap) {
                imageView.setImageBitmap(bitmap)
            }

            override fun onMapFailed(errorMessage: String) {
                // noop
            }
        })
    }
}