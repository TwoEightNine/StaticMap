package global.msnthrp.staticmap.loader

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import global.msnthrp.staticmap.model.LatLng
import global.msnthrp.staticmap.model.Tile
import global.msnthrp.staticmap.model.TileQuadruple
import global.msnthrp.staticmap.tile.TileCache
import global.msnthrp.staticmap.tile.TileLoader
import global.msnthrp.staticmap.tile.TileProvider
import global.msnthrp.staticmap.utils.combine4Tiles
import global.msnthrp.staticmap.utils.cropByOffset
import global.msnthrp.staticmap.utils.getNeededTiles
import java.lang.Exception

/**
 * converts [LatLng] into [TileQuadruple], loads tiles,
 * caches them, creates map with centered [LatLng]
 *
 * @param tileLoader loads bitmap by tile url
 * @param tileProvider converts [Tile] into tile url
 * @param tileCacheSize how many tiles are cached
 * @param mapCacheSize how many concatenated maps are cached
 */
internal class StaticMapLoader(
    private val tileLoader: TileLoader,
    private val tileProvider: TileProvider,
    tileCacheSize: Int = 100,
    mapCacheSize: Int = tileCacheSize / 4
) {

    /**
     * used to send callbacks to ui thread
     */
    private val handler = Handler(Looper.getMainLooper())

    /**
     * stores cached tiles
     */
    private val tileCache = TileCache(tileCacheSize)

    /**
     * stores cached concatenated maps
     */
    private val readyMapCache = TileCache(mapCacheSize)

    /**
     * list of running thread
     */
    private val threads = arrayListOf<Thread>()

    /**
     * starts loading of static map by [latLng] and [zoom]
     * @param callback callback to notify about finishing
     */
    fun load(latLng: LatLng, zoom: Int, callback: Callback) {
        LoaderThread(latLng, zoom, callback)
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

    /**
     * the way to notify about finishing of loading static map
     */
    interface Callback {

        /**
         * static map is successfully loaded
         * @param bitmap static map
         */
        fun onMapLoaded(bitmap: Bitmap)

        /**
         * error occurred
         * @param errorMessage a cause of error
         */
        fun onMapFailed(errorMessage: String)
    }

    private inner class LoaderThread(
        private val latLng: LatLng,
        private val zoom: Int,
        private val callback: Callback
    ) : Thread() {

        override fun run() {
            super.run()
            try {
                val tileQuadruple = getNeededTiles(latLng, zoom)

                val map = readyMapCache[tileQuadruple.topLeft] ?: createMap(tileQuadruple)
                val cropped = cropByOffset(map, tileQuadruple.centerOffset)
                handler.post {
                    callback.onMapLoaded(cropped)
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
                callback.onMapFailed(e.message.toString())
            }
        }

        private fun createMap(tileQuadruple: TileQuadruple): Bitmap {
            var topLeft: Bitmap? = null
            var topRight: Bitmap? = null
            var bottomLeft: Bitmap? = null
            var bottomRight: Bitmap? = null

            processTile(tileQuadruple.topLeft) { topLeft = it }
            processTile(tileQuadruple.topRight) { topRight = it }
            processTile(tileQuadruple.bottomLeft) { bottomLeft = it }
            processTile(tileQuadruple.bottomRight) { bottomRight = it }

            while (
                topLeft == null
                || topRight == null
                || bottomLeft == null
                || bottomRight == null
            ) {
                sleep(100L)
            }
            val result = combine4Tiles(
                topLeft!!,
                topRight!!,
                bottomLeft!!,
                bottomRight!!
            )
            readyMapCache.put(tileQuadruple.topLeft, result)
            return result
        }

        private fun processTile(tile: Tile, onReady: (Bitmap) -> Unit) {
            if (tileCache[tile] == null) {
                tileLoader.loadTile(
                    tileProvider.getTileUrl(tile),
                    object : TileLoader.Callback {
                        override fun onLoaded(tileBitmap: Bitmap) {
                            tileCache.put(tile, tileBitmap)
                            onReady(tileBitmap)
                        }
                    })
            } else {
                onReady(tileCache[tile])
            }
        }
    }
}