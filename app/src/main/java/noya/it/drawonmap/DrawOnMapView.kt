package noya.it.drawonmap

import noya.it.drawonmap.model.Surface

internal interface DrawOnMapView {
  fun setEditMode(isEditMode: Boolean)
  fun setUndoAndDeleteButtonVisibility(isVisible: Boolean)
  fun drawPolygonsOnMap(polygons: List<Surface>)
  fun drawPolylineOnMap(surface: Surface)
}
