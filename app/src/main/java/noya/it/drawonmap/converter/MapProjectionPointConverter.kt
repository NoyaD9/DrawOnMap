package noya.it.drawonmap.converter

import android.graphics.Point
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.model.LatLng

internal class MapProjectionPointConverter(private val map: GoogleMap) : PointToLatLngConverter {

  override fun toLatLng(point: Pair<Int, Int>): LatLng {
    return map.projection.fromScreenLocation(point)
  }

  private fun Projection.fromScreenLocation(pair: Pair<Int, Int>): LatLng {
    return this.fromScreenLocation(Point(pair.first, pair.second))
  }
}
