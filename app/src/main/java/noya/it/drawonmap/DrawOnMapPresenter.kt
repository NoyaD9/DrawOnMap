package noya.it.drawonmap

import de.lighti.clipper.Clipper
import de.lighti.clipper.DefaultClipper
import de.lighti.clipper.Path
import de.lighti.clipper.Paths
import noya.it.drawonmap.converter.PointToLatLngConverter
import noya.it.drawonmap.model.Surface
import noya.it.drawonmap.util.TimeWrapper

internal class DrawOnMapPresenter(private val converter: PointToLatLngConverter, private val timeWrapper: TimeWrapper) : PolygonCaptorView.Listener {

  lateinit private var view: DrawOnMapView
  private val polygons = mutableListOf<Surface>()
  private var lastAdded: Long = 0
  private var editState = EditState.IDLE

  override fun onStartDrawing() {
    polygons.add(Surface())
  }

  override fun onFinishDrawing() {
    toPolygons(combinePaths())
    view.drawPolygonsOnMap(polygons)
    setEditState(EditState.IDLE)
    view.setEditModeButtonsVisibility(polygons.isNotEmpty())
  }

  private fun combinePaths(): Paths {
    val allPaths = toPaths()
    val clip = allPaths.removeAt(allPaths.lastIndex)
    val clipper = DefaultClipper()
    val result = Paths()
    clipper.addPaths(allPaths, Clipper.PolyType.SUBJECT, true)
    clipper.addPath(clip, Clipper.PolyType.CLIP, true)
    clipper.execute(if (editState == EditState.ADD_PATH) Clipper.ClipType.UNION else Clipper.ClipType.DIFFERENCE, result)
    return result
  }

  private fun toPaths(): Paths {
    val paths = Paths()
    polygons.forEach { surface ->
      val path = Path()
      path.addAll(surface.outline.map { converter.toLongPoint(it) })
      paths.add(path)
      surface.holes.forEach {
        if (it.isNotEmpty()) {
          val hole = Path()
          hole.addAll(it.map { converter.toLongPoint(it) })
          paths.add(hole)
        }
      }
    }
    polygons.clear()
    return paths
  }

  private fun toPolygons(paths: Paths) {
    paths.forEach {
      if (it.orientation()) {
        val surface = Surface()
        it.forEach {
          val latLng = converter.toLatLng(Pair(it.x.toInt(), it.y.toInt()))
          surface.addPoint(latLng)
        }
        polygons.add(surface)
      } else {
        val hole = it.map { converter.toLatLng(Pair(it.x.toInt(), it.y.toInt())) }
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
    val newPoint = converter.toLatLng(point)
    if (!polygons.last().outline.isEmpty() && lastItemWasAddedTooRecently()) {
      return false
    }
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

  private fun setEditState(state: EditState) {
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

  fun addPath() {
    when (editState) {
      EditState.IDLE -> {
        editState = EditState.ADD_PATH
      }
      EditState.ADD_PATH -> {
        editState = EditState.IDLE
      }
      EditState.REMOVE_PATH -> {
        editState = EditState.ADD_PATH
      }
    }
    setEditState(editState)
  }

  fun removePath() {
    when (editState) {
      EditState.IDLE -> {
        editState = EditState.REMOVE_PATH
      }
      EditState.ADD_PATH -> {
        editState = EditState.REMOVE_PATH
      }
      EditState.REMOVE_PATH -> {
        editState = EditState.IDLE
      }
    }
    setEditState(editState)
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
      lastAdded = now
      return true
    }
    return false
  }

  companion object {
    const val ADD_ELEMENT_THRESHOLD = 100

    enum class EditState {
      IDLE, ADD_PATH, REMOVE_PATH
    }
  }

}
