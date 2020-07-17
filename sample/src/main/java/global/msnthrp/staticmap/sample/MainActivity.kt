package global.msnthrp.staticmap.sample

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import global.msnthrp.staticmap.model.LatLng
import global.msnthrp.staticmap.model.Tile
import global.msnthrp.staticmap.tile.TileLoader
import global.msnthrp.staticmap.tile.TileProvider
import global.msnthrp.staticmap.view.StaticMapView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        staticMap.viewState = StaticMapViewState()
        staticMap.setConfig(StaticMapView.Config(
            CustomTileProvider(),
            CustomTileLoader()
        ))

        btnLoad.setOnClickListener {
            staticMap.latLng = LatLng(
                latitude = etLatitude.text.toString().toDouble(),
                longitude = etLongitude.text.toString().toDouble()
            )
        }
        btnZoomIn.setOnClickListener {
            staticMap.zoomIn()
        }
        btnZoomOut.setOnClickListener {
            staticMap.zoomOut()
        }
    }

    private inner class StaticMapViewState : StaticMapView.ViewState {
        override fun onLoadingStateChanged(isLoading: Boolean) {
            rlLoader.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        override fun onErrorOccurred(errorMessage: String) {
            AlertDialog.Builder(this@MainActivity)
                .setTitle("static map error")
                .setMessage(errorMessage)
                .setPositiveButton("ok", null)
                .show()
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
                callback.onFailed(e)
            }
        }
    }

    private inner class CustomTileProvider : TileProvider {
        override fun getTileUrl(tile: Tile): String =
            "https://c.tile.openstreetmap.org/${tile.z}/${tile.x}/${tile.y}.png"
    }
}