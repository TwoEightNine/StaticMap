package global.msnthrp.staticmap.sample

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import global.msnthrp.staticmap.loader.StaticMapLoader
import global.msnthrp.staticmap.model.LatLng
import global.msnthrp.staticmap.model.Tile
import global.msnthrp.staticmap.tile.TileLoader
import global.msnthrp.staticmap.tile.TileProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val loader by lazy {
        StaticMapLoader(CustomTileLoader(), CustomTileProvider())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnLoad.setOnClickListener {
            val latLng = LatLng(
                latitude = etLatitude.text.toString().toDouble(),
                longitude = etLongitude.text.toString().toDouble()
            )
            loader.load(latLng, 14, OnStaticMapLoaded())
        }
    }

    private inner class OnStaticMapLoaded : StaticMapLoader.Callback {
        override fun onMapLoaded(bitmap: Bitmap) {
            ivTest.setImageBitmap(bitmap)
        }

        override fun onMapFailed(errorMessage: String) {
            Log.wtf("qwer", "error: $errorMessage")
        }
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
                e.printStackTrace()
            }
        }
    }

    private inner class CustomTileProvider : TileProvider {
        override fun getTileUrl(tile: Tile): String =
            "https://c.tile.openstreetmap.org/${tile.z}/${tile.x}/${tile.y}.png"
    }
}