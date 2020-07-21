package global.msnthrp.staticmap.sample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import global.msnthrp.staticmap.core.StaticMap
import global.msnthrp.staticmap.model.LatLngZoom
import global.msnthrp.staticmap.model.Tile
import global.msnthrp.staticmap.tile.TileEssential
import global.msnthrp.staticmap.tile.TileLoader
import global.msnthrp.staticmap.tile.TileProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tileEssential = TileEssential(CustomTileProvider(), CustomTileLoader())
        val pinIcon = ContextCompat.getDrawable(this, R.drawable.ic_pin)
        var latLngZoom = LatLngZoom(0.0, 0.0, 16)

        btnLoad.setOnClickListener {
            latLngZoom = latLngZoom.copy(
                latitude = etLatitude.text.toString().toDouble(),
                longitude = etLongitude.text.toString().toDouble()
            )
            StaticMap.with(tileEssential)
                .load(latLngZoom)
                .pin(if (cbWithPin.isChecked) pinIcon else null)
                .into(ivStaticMap)
        }
        btnZoomIn.setOnClickListener {
            latLngZoom = latLngZoom.copy(zoom = latLngZoom.zoom + 1)
            btnLoad.callOnClick()
        }
        btnZoomOut.setOnClickListener {
            latLngZoom = latLngZoom.copy(zoom = latLngZoom.zoom - 1)
            btnLoad.callOnClick()
        }
        cbWithPin.setOnCheckedChangeListener { _, _ ->
            btnLoad.callOnClick()
        }
        btnList.setOnClickListener {
            startActivity(Intent(this, MapListActivity::class.java))
        }
        btnLoad.callOnClick()
    }

    private inner class CustomTileLoader : TileLoader {
        override fun loadTile(tileUrl: String, callback: TileLoader.Callback) {
            try {
                val bitmap = Glide.with(this@MainActivity)
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