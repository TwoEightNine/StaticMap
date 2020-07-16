package global.msnthrp.staticmap.tile

import android.graphics.Bitmap

/**
 * helps to load image by url into bitmap
 */
interface TileLoader {

    /**
     * @param tileUrl url to load
     * @param callback the way to return loaded bitmap
     */
    fun loadTile(tileUrl: String, callback: Callback)

    /**
     * callback for loading image by url into bitmap
     */
    interface Callback {

        /**
         * should be invoked when bitmap is successfully loaded
         * @param tileBitmap loaded bitmap tile
         */
        fun onLoaded(tileBitmap: Bitmap)

    }
}