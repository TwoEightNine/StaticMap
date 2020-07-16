package global.msnthrp.staticmap.utils

import global.msnthrp.staticmap.model.Coords
import global.msnthrp.staticmap.model.LatLng
import global.msnthrp.staticmap.model.Tile
import global.msnthrp.staticmap.model.TileQuadruple
import kotlin.math.*

internal fun getNeededTiles(latLng: LatLng, zoom: Int): TileQuadruple {
    val (x, y) = getXY(latLng, zoom)
    val mainX = x.toInt()
    val mainY = y.toInt()

    val xOffset: Double
    val yOffset: Double

    if (x - mainX < 0.5) {
        xOffset = (x - mainX + 1) / 2
        if (y - mainY < 0.5) {
            yOffset = (y - mainY + 1) / 2
            return TileQuadruple(
                topLeft = Tile(mainX - 1, mainY - 1, zoom),
                topRight = Tile(mainX, mainY - 1, zoom),
                bottomLeft = Tile(mainX - 1, mainY, zoom),
                bottomRight = Tile(mainX, mainY, zoom),
                centerOffset = Coords(xOffset, yOffset)
            )
        } else {
            yOffset = (y - mainY) / 2
            return TileQuadruple(
                topLeft = Tile(mainX - 1, mainY, zoom),
                topRight = Tile(mainX, mainY, zoom),
                bottomLeft = Tile(mainX - 1, mainY + 1, zoom),
                bottomRight = Tile(mainX, mainY + 1, zoom),
                centerOffset = Coords(xOffset, yOffset)
            )
        }
    } else {
        xOffset = (x - mainX) / 2
        if (y - mainY < 0.5) {
            yOffset = (y - mainY + 1) / 2
            return TileQuadruple(
                topLeft = Tile(mainX, mainY - 1, zoom),
                topRight = Tile(mainX + 1, mainY - 1, zoom),
                bottomLeft = Tile(mainX, mainY, zoom),
                bottomRight = Tile(mainX + 1, mainY, zoom),
                centerOffset = Coords(xOffset, yOffset)
            )
        } else {
            yOffset = (y - mainY) / 2
            return TileQuadruple(
                topLeft = Tile(mainX, mainY, zoom),
                topRight = Tile(mainX + 1, mainY, zoom),
                bottomLeft = Tile(mainX, mainY + 1, zoom),
                bottomRight = Tile(mainX + 1, mainY + 1, zoom),
                centerOffset = Coords(xOffset, yOffset)
            )
        }
    }
}

private fun getXY(latLng: LatLng, zoom: Int): Coords {
    val zoomPower = 1 shl zoom
    val x = (latLng.longitude + 180) / 360 * zoomPower
    val latTan = tan(latLng.latitude.toRadian())
    val latInvCos = 1 / cos(latLng.latitude.toRadian())
    val y = (1 - log(latTan + latInvCos, E) / PI) / 2 * zoomPower
    return Coords(x, y)
}

private fun Double.toRadian() = this / 180 * PI