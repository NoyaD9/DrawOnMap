package noya.it.drawonmap

import noya.it.drawonmap.model.Surface

internal interface DrawOnMapView {
  fun setEditMode(editMode: Boolean)
  fun drawPolygonsOnMap(polygons: List<Surface>)
  fun drawPolylineOnMap(polygons: List<Surface>)
}
