package global.msnthrp.staticmap.tile

import android.graphics.Bitmap
import android.util.LruCache
import global.msnthrp.staticmap.model.Tile

class TileCache(maxTiles: Int = 100) : LruCache<Tile, Bitmap>(maxTiles) {

    override fun entryRemoved(evicted: Boolean, key: Tile?, oldValue: Bitmap?, newValue: Bitmap?) {
        super.entryRemoved(evicted, key, oldValue, newValue)
        oldValue?.recycle()
    }
}