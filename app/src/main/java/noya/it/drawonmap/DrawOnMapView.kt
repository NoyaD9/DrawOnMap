package noya.it.drawonmap

import noya.it.drawonmap.model.Surface

internal interface DrawOnMapView {
  fun setEditMode(isEditMode: Boolean)
  fun setEditModeButtonsVisibility(isVisible: Boolean)
  fun drawPolygonsOnMap(polygons: List<Surface>)
  fun drawPolylineOnMap(surface: Surface)
  fun highlightAddPathButtons(isHighlighted: Boolean)
  fun highlightRemovePathButtons(isHighlighted: Boolean)
}
