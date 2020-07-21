package global.msnthrp.staticmap.tile

import android.graphics.Bitmap
import android.util.LruCache
import global.msnthrp.staticmap.model.Tile

internal class TileCache(maxTiles: Int = 100) : LruCache<Tile, Bitmap>(maxTiles) {

    override fun entryRemoved(evicted: Boolean, key: Tile?, oldValue: Bitmap?, newValue: Bitmap?) {
        super.entryRemoved(evicted, key, oldValue, newValue)
//        oldValue?.recycle()
        // TODO fix this issue: if recycle bitmaps, static maps in lists won't work due to
        //              bitmap is already recycle()'d
    }
}