package global.msnthrp.staticmap.loader

import android.graphics.drawable.Drawable
import android.util.Log
import global.msnthrp.staticmap.model.LatLngZoom
import global.msnthrp.staticmap.model.TileQuadruple
import global.msnthrp.staticmap.tile.TileCache
import global.msnthrp.staticmap.tile.TileEssential

/**
 * converts [LatLngZoom] into [TileQuadruple], loads tiles,
 * caches them, creates map with centered [LatLngZoom]
 */
internal object StaticMapLoader {

    private const val TAG = "StaticMap"

    /**
     * stores cached tiles
     */
    internal val tileCache = TileCache(100)

    /**
     * stores cached concatenated maps
     */
    internal val readyMapCache = TileCache(25)

    /**
     * list of running thread
     */
    private val threads = arrayListOf<Thread>()

    /**
     * starts loading of static map by [latLngZoom]
     * @param callback callback to notify about finishing
     */
    fun load(
        tileEssential: TileEssential,
        latLngZoom: LatLngZoom,
        pinIcon: Drawable? = null,
        callback: StaticMapLoaderCallback
    ) {
        LoaderThread(tileEssential, latLngZoom, pinIcon, callback)
            .apply {
                threads.add(this)
                start()
            }
    }

    /**
     * cancel all current jobs
     */
    fun cancelAll() {
        threads.forEach { it.interrupt() }
    }

    internal fun log(s: String, t: Throwable? = null) {
        if (t == null) {
            Log.i(TAG, s)
        } else {
            Log.wtf(TAG, s)
            t.printStackTrace()
        }
    }
}