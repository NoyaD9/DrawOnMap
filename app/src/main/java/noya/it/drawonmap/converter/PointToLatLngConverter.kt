package noya.it.drawonmap.converter

import com.google.android.gms.maps.model.LatLng
import de.lighti.clipper.Point

/**
 * implementation of this interface must provide a way to convert to GoogleMaps LatLng and to Clipper LongPoint
 * @see MapProjectionPointConverter
 */
internal interface PointToLatLngConverter {
  fun toLatLng(point: Pair<Int, Int>): LatLng
  fun toLongPoint(latLng: LatLng): Point.LongPoint
}
