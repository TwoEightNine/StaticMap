package global.msnthrp.staticmap.tile

import android.graphics.Bitmap

interface TileLoader {

    fun loadTile(tileUrl: String, callback: Callback)

    interface Callback {

        fun onLoaded(tileBitmap: Bitmap)

    }
}