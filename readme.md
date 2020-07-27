## StaticMap

StaticMap is a library for showing a static piece of map without having to use powerful but hard-weight maps engines.
StaticMap API allows you to load a map right into `ImageView`.

![](https://github.com/TwoEightNine/StaticMap/raw/master/staticmap_sample.gif)

### usage

add this dependency to your app-level `build.gradle`:

```groovy
implementation 'com.twoeightnine.staticmap:staticmap:0.1'
```

after sync is done create preferred `TileLoader` (loads image by url into bitmap) and 
`TileProvider` (converts tile coordinates into tile url)

```kotlin
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
```

then load a map

```kotlin
val tileEssential = TileEssential(CustomTileProvider(), CustomTileLoader())
val pinIcon = ContextCompat.getDrawable(this, R.drawable.ic_pin)
var latLngZoom = LatLngZoom(0.0, 0.0, 16)

StaticMap.with(tileEssential)
        .load(latLngZoom)
        .pin(pinIcon)
        .into(yourImageView)
```

##### issues and contribution are welcome!

#### twoeightnine, 2020 