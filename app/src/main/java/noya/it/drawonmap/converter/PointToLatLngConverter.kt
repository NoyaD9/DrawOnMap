package noya.it.drawonmap.converter

import com.google.android.gms.maps.model.LatLng

internal interface PointToLatLngConverter {
  fun toLatLng(point: Pair<Int, Int>): LatLng
}
