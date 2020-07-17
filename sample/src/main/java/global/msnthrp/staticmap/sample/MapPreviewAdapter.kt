package global.msnthrp.staticmap.sample

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import global.msnthrp.staticmap.model.LatLng
import global.msnthrp.staticmap.view.StaticMapView
import kotlinx.android.synthetic.main.item_map_preview.view.*

class MapPreviewAdapter(
    context: Context,
    private val config: StaticMapView.Config
) : RecyclerView.Adapter<MapPreviewAdapter.MapPreviewViewHolder>() {

    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
            as LayoutInflater

    private val items = arrayListOf<LatLng>()

    fun addAll(items: Collection<LatLng>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = MapPreviewViewHolder(inflater.inflate(R.layout.item_map_preview, parent, false))
        .apply { init() }

    override fun onBindViewHolder(holder: MapPreviewViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class MapPreviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun init() {
            with(itemView) {
                staticMapPreview.setConfig(config)
                staticMapPreview.zoom = 14
            }
        }

        fun bind(latLng: LatLng) {
            with(itemView) {
                staticMapPreview.latLng = latLng
            }
        }

    }
}