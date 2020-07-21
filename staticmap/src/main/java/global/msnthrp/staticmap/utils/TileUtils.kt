package global.msnthrp.staticmap.utils

import global.msnthrp.staticmap.model.*
import global.msnthrp.staticmap.model.Coords
import global.msnthrp.staticmap.model.TileQuadruple
import kotlin.math.*

/**
 * using latitude, longitude and zoom calculates 4 the most nearest tiles
 * @param latLngZoom coordinates and zoom of place
 * @return object that contains 4 tiles and an offset of given place
 */
internal fun getNeededTiles(latLngZoom: LatLngZoom): TileQuadruple {
    val (x, y) = getXY(latLngZoom)
    val zoom = latLngZoom.zoom
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

/**
 * converts zoom, latitude and longitude into x and y for tiling
 * @param latLngZoom coordinates and zoom of place
 * @return pair of x and y, both float for calculating offsets
 */
private fun getXY(latLngZoom: LatLngZoom): Coords {
    val zoomPower = 1 shl latLngZoom.zoom
    val x = (latLngZoom.longitude + 180) / 360 * zoomPower
    val latTan = tan(latLngZoom.latitude.toRadian())
    val latInvCos = 1 / cos(latLngZoom.latitude.toRadian())
    val y = (1 - log(latTan + latInvCos, E) / PI) / 2 * zoomPower
    return Coords(x, y)
}

private fun Double.toRadian() = this / 180 * PI