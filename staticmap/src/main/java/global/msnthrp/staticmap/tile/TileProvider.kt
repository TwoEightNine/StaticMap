package global.msnthrp.staticmap.tile

import global.msnthrp.staticmap.model.Tile

/**
 * converts [Tile] into tile url
 */
interface TileProvider {

    fun getTileUrl(tile: Tile): String
}