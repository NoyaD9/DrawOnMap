package noya.it.drawonmap

import de.lighti.clipper.Clipper
import de.lighti.clipper.DefaultClipper
import de.lighti.clipper.Path
import de.lighti.clipper.Paths
import noya.it.drawonmap.converter.PointToLatLngConverter
import noya.it.drawonmap.model.Surface
import noya.it.drawonmap.util.TimeWrapper

internal class DrawOnMapPresenter(private val converter: PointToLatLngConverter, private val timeWrapper: TimeWrapper) : PolygonCaptorView.Listener {

  private lateinit var view: DrawOnMapView
  private val polygons = mutableListOf<Surface>()
  private var lastAdded: Long = 0
  private var editState = EditState.IDLE
    set(state) {
      field = state
      when (state) {
        EditState.IDLE -> {
          view.highlightRemovePathButtons(false)
          view.highlightAddPathButtons(false)
        }
        EditState.ADD_PATH -> {
          view.highlightRemovePathButtons(false)
          view.highlightAddPathButtons(true)
        }
        EditState.REMOVE_PATH -> {
          view.highlightRemovePathButtons(true)
          view.highlightAddPathButtons(false)
        }
      }
      view.setEditMode(state != EditState.IDLE)
    }

  override fun onStartDrawing() {
    polygons.add(Surface())
  }

  override fun onFinishDrawing() {
    toPolygons(combinePaths())
    view.drawPolygonsOnMap(polygons)
    editState = EditState.IDLE
    view.setEditModeButtonsVisibility(polygons.isNotEmpty())
  }

  private fun combinePaths(): Paths {
    val allPaths = toPaths()
    val clip = allPaths.removeAt(allPaths.lastIndex)
    val clipper = DefaultClipper()
    val result = Paths()
    clipper.addPaths(allPaths, Clipper.PolyType.SUBJECT, true)
    clipper.addPath(clip, Clipper.PolyType.CLIP, true)
    when (editState) {
      EditState.ADD_PATH -> clipper.execute(Clipper.ClipType.UNION, result)
      EditState.REMOVE_PATH -> clipper.execute(Clipper.ClipType.DIFFERENCE, result)
      else -> throw IllegalStateException("trying to combine paths in idle mode")
    }
    return result
  }

  private fun toPaths(): Paths {
    val paths = Paths()
    polygons.forEach { surface ->
      val path = Path()
      path.addAll(surface.outline.map { converter.toLongPoint(it) })
      paths.add(path)
      surface.holes.forEach { latLngList ->
        if (latLngList.isNotEmpty()) {
          val hole = Path()
          hole.addAll(latLngList.map { converter.toLongPoint(it) })
          paths.add(hole)
        }
      }
    }
    polygons.clear()
    return paths
  }

  private fun toPolygons(paths: Paths) {
    paths.forEach { path ->
      if (path.orientation()) {
        val surface = Surface()
        path.forEach {
          val latLng = converter.toLatLng(Pair(it.x.toInt(), it.y.toInt()))
          surface.addPoint(latLng)
        }
        polygons.add(surface)
      } else {
        val hole = path.map { converter.toLatLng(Pair(it.x.toInt(), it.y.toInt())) }
        polygons.last().holes.add(hole)
      }
    }
  }

  override fun onDrawPoint(point: Pair<Int, Int>) {
    if (addPoint(point)) {
      view.drawPolylineOnMap(polygons.last())
    }
  }

  private fun addPoint(point: Pair<Int, Int>): Boolean {
    if (!polygons.last().outline.isEmpty() && lastItemWasAddedTooRecently()) {
      return false
    }
    val newPoint = converter.toLatLng(point)
    polygons.last().addPoint(newPoint)
    return true
  }

  fun bind(view: DrawOnMapView, retainedPolygons: List<Surface>?) {
    this.view = view
    retainedPolygons?.let {
      polygons.addAll(retainedPolygons)
      view.drawPolygonsOnMap(polygons)
      view.setEditModeButtonsVisibility(polygons.isNotEmpty())
    }
  }

  fun addPath() {
    editState = when (editState) {
      EditState.IDLE -> {
        EditState.ADD_PATH
      }
      EditState.ADD_PATH -> {
        EditState.IDLE
      }
      EditState.REMOVE_PATH -> {
        EditState.ADD_PATH
      }
    }
  }

  fun removePath() {
    editState = when (editState) {
      EditState.IDLE -> {
        EditState.REMOVE_PATH
      }
      EditState.ADD_PATH -> {
        EditState.REMOVE_PATH
      }
      EditState.REMOVE_PATH -> {
        EditState.IDLE
      }
    }
  }

  fun deleteAll() {
    polygons.clear()
    view.drawPolygonsOnMap(polygons)
    view.setEditModeButtonsVisibility(false)
  }

  fun copyOfPolygons() = polygons.toList()

  private fun lastItemWasAddedTooRecently(): Boolean {
    val now = timeWrapper.currentTimeMillis()
    if (now - lastAdded <= ADD_ELEMENT_THRESHOLD) {
      return true
    }
    lastAdded = now
    return false
  }

  companion object {
    const val ADD_ELEMENT_THRESHOLD = 100

    enum class EditState {
      IDLE, ADD_PATH, REMOVE_PATH
    }
  }

}
