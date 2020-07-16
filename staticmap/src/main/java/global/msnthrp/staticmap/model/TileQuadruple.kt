package global.msnthrp.staticmap.model

internal data class TileQuadruple(

    val topLeft: Tile,

    val topRight: Tile,

    val bottomLeft: Tile,

    val bottomRight: Tile,

    val centerOffset: Coords
)