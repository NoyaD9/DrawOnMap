package noya.it.drawonmap.model

import com.google.android.gms.maps.model.LatLng

data class Surface(internal val outline: MutableCollection<LatLng> = mutableSetOf<LatLng>()) {

  fun addPoint(point: LatLng) {
    outline.add(point)
  }
}
