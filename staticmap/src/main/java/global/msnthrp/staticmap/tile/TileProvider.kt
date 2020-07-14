package global.msnthrp.staticmap.tile

import global.msnthrp.staticmap.model.Tile

interface TileProvider {

    fun getTileUrl(tile: Tile): String
}