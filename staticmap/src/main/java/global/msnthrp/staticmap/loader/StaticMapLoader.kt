package global.msnthrp.staticmap.loader

import android.graphics.drawable.Drawable
import android.util.Log
import global.msnthrp.staticmap.core.StaticMap
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
    internal val tileCache = TileCache(StaticMap.tileCacheSize)

    /**
     * stores cached concatenated maps
     */
    internal val readyMapCache = TileCache(StaticMap.mapCacheSize)

    /**
     * list of running thread
     */
    private val threads = arrayListOf<Thread>()

    /**
     * list of queued threads
     */
    private val threadQueue = arrayListOf<Thread>()

    /**
     * starts loading of static map by [latLngZoom]
     * @param tileEssential essential part of StaticMap, contains loader and provider
     * @param latLngZoom represents exact point of map
     * @param pinIcon pin to draw over bitmap
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
                onThreadDone = { removeThread(this) }
                if (threads.size < StaticMap.maxThreads) {
                    startThread(this)
                } else {
                    threadQueue.add(this)
                }
            }
    }

    /**
     * cancel all current jobs
     */
    fun cancelAll() {
        threads.forEach { it.interrupt() }
    }

    /**
     * starts thread and adds it to [threads]
     */
    private fun startThread(thread: Thread) {
        threads.add(thread)
        thread.start()
    }

    /**
     * removes [thread] from [threads] list
     * checks for next thread in [threadQueue]
     */
    private fun removeThread(thread: Thread) {
        if (thread in threads) {
            threads.remove(thread)
        }
        if (threadQueue.isNotEmpty()) {
            val nextThread = threadQueue[0]
            threadQueue.removeAt(0)
            startThread(nextThread)
        } else {
            log("Queue is empty")
        }
    }

    internal fun log(s: String, t: Throwable? = null) {
        if (StaticMap.verbose) {
            if (t == null) {
                Log.i(TAG, s)
            } else {
                Log.wtf(TAG, s)
                t.printStackTrace()
            }
        }
    }
}