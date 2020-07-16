package global.msnthrp.staticmap.model

/**
 * object that holds the 4 nearest tiles to a place
 * the place is represented by [centerOffset] (in [0..1, 0..1], relative placement)
 */
internal data class TileQuadruple(

    val topLeft: Tile,

    val topRight: Tile,

    val bottomLeft: Tile,

    val bottomRight: Tile,

    val centerOffset: Coords
)