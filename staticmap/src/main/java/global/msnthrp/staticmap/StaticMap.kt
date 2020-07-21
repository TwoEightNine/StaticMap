package global.msnthrp.staticmap

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import global.msnthrp.staticmap.loader.StaticMapLoader
import global.msnthrp.staticmap.loader.StaticMapLoaderCallback
import global.msnthrp.staticmap.model.LatLngZoom
import global.msnthrp.staticmap.tile.TileEssential
import global.msnthrp.staticmap.tile.TileLoader
import global.msnthrp.staticmap.tile.TileProvider

class StaticMap(private val tileEssential: TileEssential) {

    private var latLngZoom: LatLngZoom? = null
    private var pinIcon: Drawable? = null

    fun load(latLngZoom: LatLngZoom) = apply {
        this.latLngZoom = latLngZoom
    }

    fun pin(pinIcon: Drawable?) = apply {
        this.pinIcon = pinIcon
    }

    fun into(callback: StaticMapLoaderCallback) {
        val latLngZoom = latLngZoom
        check(latLngZoom != null) {
            "LatLngZoom must be specified. Use load(LatLngZoom)"
        }
        StaticMapLoader.load(tileEssential, latLngZoom, pinIcon, callback)
    }

    fun into(imageView: ImageView) {
        into(object : StaticMapLoaderCallback {
            override fun onMapLoaded(bitmap: Bitmap) {
                imageView.setImageBitmap(bitmap)
            }

            override fun onMapFailed(errorMessage: String) {
                // noop
            }
        })
    }

    companion object {

        fun with(tileEssential: TileEssential) =
            StaticMap(tileEssential)

        fun clearCache() {
            StaticMapLoader.tileCache.evictAll()
            StaticMapLoader.readyMapCache.evictAll()
        }
    }


}