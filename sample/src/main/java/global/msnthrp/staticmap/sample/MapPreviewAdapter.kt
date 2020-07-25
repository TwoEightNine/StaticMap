package global.msnthrp.staticmap.sample

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import global.msnthrp.staticmap.core.StaticMap
import global.msnthrp.staticmap.model.LatLngZoom
import global.msnthrp.staticmap.tile.TileEssential
import kotlinx.android.synthetic.main.item_map_preview.view.*

class MapPreviewAdapter(
    context: Context,
    private val tileEssential: TileEssential
) : RecyclerView.Adapter<MapPreviewAdapter.MapPreviewViewHolder>() {

    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
            as LayoutInflater

    private val items = arrayListOf<LatLngZoom>()

    fun addAll(items: Collection<LatLngZoom>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = MapPreviewViewHolder(inflater.inflate(R.layout.item_map_preview, parent, false))

    override fun onBindViewHolder(holder: MapPreviewViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class MapPreviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(latLng: LatLngZoom) {
            with(itemView) {
                StaticMap.with(tileEssential)
                    .load(latLng)
                    .clearBeforeLoading(true)
                    .into(ivStaticMapPreview)
            }
        }

    }
}