package global.msnthrp.staticmap.loader

import android.graphics.Bitmap
import android.graphics.DrawFilter
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import global.msnthrp.staticmap.model.LatLngZoom
import global.msnthrp.staticmap.model.Tile
import global.msnthrp.staticmap.model.TileQuadruple
import global.msnthrp.staticmap.tile.TileEssential
import global.msnthrp.staticmap.tile.TileLoader
import global.msnthrp.staticmap.utils.combine4Tiles
import global.msnthrp.staticmap.utils.cropByOffset
import global.msnthrp.staticmap.utils.drawPinIconOnBitmap
import global.msnthrp.staticmap.utils.getNeededTiles

internal class LoaderThread(
    private val tileEssential: TileEssential,
    private val latLngZoom: LatLngZoom,
    private val pinIcon: Drawable? = null,
    private val callback: StaticMapLoaderCallback
) : Thread() {

    /**
     * used to send callbacks to ui thread
     */
    private val handler = Handler(Looper.getMainLooper())

    /**
     * callback that is being invoked after the job is done
     */
    internal var onThreadDone: (() -> Unit)? = null

    override fun run() {
        super.run()
        try {
            val start = System.currentTimeMillis()
            val tileQuadruple = getNeededTiles(latLngZoom)

            val map = StaticMapLoader.readyMapCache[tileQuadruple.topLeft] ?: createMap(tileQuadruple)
            val cropped = cropByOffset(map, tileQuadruple.centerOffset)
            pinIcon?.also {
                drawPinIconOnBitmap(cropped, it)
            }
            handler.post {
                StaticMapLoader.log("loaded for ${System.currentTimeMillis() - start} ms")
                callback.onMapLoaded(cropped)
            }
        } catch (e: InterruptedException) {
            StaticMapLoader.log("cancelled")
        } catch (e: Exception) {
            StaticMapLoader.log("map loading failed", e)
            handler.post {
                callback.onMapFailed(e.message.toString())
            }
        } finally {
            onThreadDone?.invoke()
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
            sleep(50L)
        }
        val result = combine4Tiles(
            topLeft!!,
            topRight!!,
            bottomLeft!!,
            bottomRight!!
        )
        StaticMapLoader.readyMapCache.put(tileQuadruple.topLeft, result)
        return result
    }

    private fun processTile(tile: Tile, onReady: (Bitmap) -> Unit) {
        if (StaticMapLoader.tileCache[tile] == null) {
            tileEssential.tileLoader.loadTile(
                tileEssential.tileProvider.getTileUrl(tile),
                object : TileLoader.Callback {
                    override fun onLoaded(tileBitmap: Bitmap) {
                        StaticMapLoader.tileCache.put(tile, tileBitmap)
                        onReady(tileBitmap)
                    }

                    override fun onFailed(e: Exception) {
                        handler.post {
                            StaticMapLoader.log("tile loading failed", e)
                            callback.onMapFailed(e.message.toString())
                        }
                    }
                })
        } else {
            onReady(StaticMapLoader.tileCache[tile])
        }
    }
}