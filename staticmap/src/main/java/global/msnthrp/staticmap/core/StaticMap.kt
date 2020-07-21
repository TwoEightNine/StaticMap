package global.msnthrp.staticmap.core

import android.os.Build
import global.msnthrp.staticmap.loader.StaticMapLoader
import global.msnthrp.staticmap.tile.TileEssential
import global.msnthrp.staticmap.tile.TileLoader
import global.msnthrp.staticmap.tile.TileProvider

object StaticMap {

    /**
     * flag to print logs into logcat
     */
    var verbose = false

    /**
     * maximum threads that are loading map simultaneously
     */
    var maxThreads = 8

    /**
     * maximum tiles count that can be cached
     */
    var tileCacheSize = 100
        set(value) {
            field = value
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                StaticMapLoader.tileCache.resize(value)
            }
        }

    /**
     * maximum maps count that can be cached
     */
    var mapCacheSize = 25
        set(value) {
            field = value
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                StaticMapLoader.readyMapCache.resize(value)
            }
        }

    /**
     * creates [StaticMapRequest] with given [TileLoader] and [TileProvider]
     */
    fun with(tileEssential: TileEssential) =
        StaticMapRequest(tileEssential)

    /**
     * clears tile cache
     */
    fun clearCache() {
        StaticMapLoader.tileCache.evictAll()
        StaticMapLoader.readyMapCache.evictAll()
    }
}