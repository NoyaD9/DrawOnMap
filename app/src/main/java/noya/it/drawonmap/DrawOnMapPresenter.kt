package noya.it.drawonmap

import noya.it.drawonmap.converter.PointToLatLngConverter
import noya.it.drawonmap.model.Surface
import noya.it.drawonmap.util.TimeWrapper

internal class DrawOnMapPresenter(private val converter: PointToLatLngConverter, private val timeWrapper: TimeWrapper) : PolygonCaptorView.Listener {

  lateinit private var view: DrawOnMapView
  private var isEditMode = false
  private val polygons = mutableListOf<Surface>()
  private var lastAdded: Long = 0

  override fun onStartDrawing() {
    polygons.add(Surface())
  }

  override fun onFinishDrawing() {
    view.drawPolygonsOnMap(polygons)
    toggleEditMode()
  }

  override fun onDrawPoint(point: Pair<Int, Int>) {
    if (addPoint(point)) {
      view.drawPolylineOnMap(polygons.last())
    }
  }

  private fun addPoint(point: Pair<Int, Int>): Boolean {
    val newPoint = converter.toLatLng(point)
    if (!polygons.last().outline.isEmpty() && lastItemWasAddedTooRecently()) {
      return false
    }
    polygons.last().addPoint(newPoint)
    return true
  }

  fun bind(view: DrawOnMapView) {
    this.view = view
  }

  fun toggleEditMode() {
    isEditMode = !isEditMode
    view.setEditMode(isEditMode)
  }

  fun undo() {
    if (polygons.isNotEmpty()) {
      polygons.removeAt(polygons.lastIndex)
      view.drawPolygonsOnMap(polygons)
    }
  }


  private fun lastItemWasAddedTooRecently(): Boolean {
    val now = timeWrapper.currentTimeMillis()
    if (now - lastAdded <= ADD_ELEMENT_THRESHOLD) {
      lastAdded = now
      return true
    }
    return false
  }

  companion object {
    const val ADD_ELEMENT_THRESHOLD = 100
  }
}
