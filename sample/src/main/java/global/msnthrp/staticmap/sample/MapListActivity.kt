package global.msnthrp.staticmap.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import global.msnthrp.staticmap.model.LatLngZoom
import global.msnthrp.staticmap.model.Tile
import global.msnthrp.staticmap.tile.TileEssential
import global.msnthrp.staticmap.tile.TileLoader
import global.msnthrp.staticmap.tile.TileProvider
import kotlinx.android.synthetic.main.activity_map_list.*
import kotlin.random.Random

class MapListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_list)

        val tileEssential = TileEssential(CustomTileProvider(), CustomTileLoader())
        val adapter = MapPreviewAdapter(this, tileEssential)
        rvMaps.layoutManager = LinearLayoutManager(this)
        rvMaps.adapter = adapter
        adapter.addAll(createPoints())
    }

    private fun createPoints() = arrayListOf<LatLngZoom>().apply {
        for (i in 1..100) {
            add(
                LatLngZoom(
                    latitude = Random.nextDouble(45.7, 53.0),
                    longitude = Random.nextDouble(4.9, 29.0),
                    zoom = 14
                )
            )
        }
    }

    private inner class CustomTileLoader : TileLoader {
        override fun loadTile(tileUrl: String, callback: TileLoader.Callback) {
            try {
                val bitmap = Glide.with(this@MapListActivity)
                    .asBitmap()
                    .load(tileUrl)
                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get()
                callback.onLoaded(bitmap)
            } catch (e: Exception) {
                callback.onFailed(e)
            }
        }
    }

    private inner class CustomTileProvider : TileProvider {
        override fun getTileUrl(tile: Tile): String =
            "https://c.tile.openstreetmap.org/${tile.z}/${tile.x}/${tile.y}.png"
    }
}