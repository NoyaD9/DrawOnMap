package noya.it.drawonmap.converter

import com.google.android.gms.maps.model.LatLng
import de.lighti.clipper.Point

internal interface PointToLatLngConverter {
  fun toLatLng(point: Pair<Int, Int>): LatLng
  fun toLongPoint(latLng: LatLng): Point.LongPoint
}
