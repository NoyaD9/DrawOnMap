package noya.it.drawonmap.converter

import com.google.android.gms.maps.model.LatLng

interface PointToLatLngConverter {
  fun toLatLng(point: Pair<Int, Int>): LatLng
}
