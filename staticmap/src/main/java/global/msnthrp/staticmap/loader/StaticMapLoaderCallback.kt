package global.msnthrp.staticmap.loader

import android.graphics.Bitmap

/**
 * the way to notify about finishing of loading static map
 */
interface StaticMapLoaderCallback {

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