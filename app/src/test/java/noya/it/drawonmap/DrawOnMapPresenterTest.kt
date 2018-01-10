package noya.it.drawonmap

import com.google.android.gms.maps.model.LatLng
import noya.it.drawonmap.converter.PointToLatLngConverter
import noya.it.drawonmap.model.Surface
import noya.it.drawonmap.util.TimeWrapper
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Before
import org.junit.Test



@Suppress("IllegalIdentifier")
class DrawOnMapPresenterTest {

  private lateinit var presenter: DrawOnMapPresenter
  private val converter = SimplePointToLatLngConverter()
  private val view = TestView()
  private val timeWrapper = SimpleTimeWrapper(System.currentTimeMillis())

  @Before
  fun setUp() {
    presenter = DrawOnMapPresenter(converter, timeWrapper)
    presenter.bind(view, null)
  }

  @Test
  fun `draw a polygon`() {
    presenter.onStartDrawing()
    presenter.onDrawPoint(Pair(1, 2))

    presenter.onFinishDrawing()

    assertThat(view.polygons).isEqualTo(mutableListOf(Surface(mutableSetOf(LatLng(1.0, 2.0)))))
  }

  @Test
  fun `first point must always be added`() {
    presenter.onStartDrawing()
    timeWrapper.currentTime = 10

    presenter.onDrawPoint(Pair(1, 2))

    assertThat(view.polygons).isEqualTo(mutableListOf(Surface(mutableSetOf(LatLng(1.0, 2.0)))))
  }

  @Test
  fun `don't add a second point if last was added too recently`() {
    presenter.onStartDrawing()
    presenter.onDrawPoint(Pair(1, 2))
    timeWrapper.currentTime = 10

    presenter.onDrawPoint(Pair(3, 4))

    assertThat(view.polygons).isEqualTo(mutableListOf(Surface(mutableSetOf(LatLng(1.0, 2.0)))))
  }

  @Test
  fun `add a second point if above threshold`() {
    presenter.onStartDrawing()
    presenter.onDrawPoint(Pair(1, 2))
    timeWrapper.currentTime = 101

    presenter.onDrawPoint(Pair(3, 4))

    assertThat(view.polygons).isEqualTo(mutableListOf(Surface(mutableSetOf(LatLng(1.0, 2.0), LatLng(3.0, 4.0)))))
  }


  @Test(expected = NoSuchElementException::class)
  fun `throws exception if trying to draw without calling startDrawing`() {
    presenter.onDrawPoint(Pair(1, 2))
  }

  @Test
  fun `toggle edit mode`() {
    presenter.toggleEditMode()

    assertThat(view.edit).isTrue()
  }

  @Test
  fun `finish edit mode when drawing is finished`() {
    presenter.toggleEditMode()
    presenter.onStartDrawing()
    presenter.onDrawPoint(Pair(1, 2))

    presenter.onFinishDrawing()

    assertThat(view.edit).isFalse()
  }

  @Test
  fun `undo remove last added polygon`() {
    presenter.onStartDrawing()
    presenter.onDrawPoint(Pair(1, 2))
    presenter.onDrawPoint(Pair(3, 4))
    presenter.onFinishDrawing()
    presenter.onStartDrawing()
    presenter.onDrawPoint(Pair(7, 8))
    presenter.onDrawPoint(Pair(8, 9))
    presenter.onFinishDrawing()

    presenter.undo()

    assertThat(view.polygons).isEqualTo(mutableListOf(Surface(mutableSetOf(LatLng(1.0, 2.0), LatLng(3.0, 4.0)))))
  }

  @Test
  fun `delete all removes all polygons`() {
    presenter.onStartDrawing()
    presenter.onDrawPoint(Pair(1, 2))
    presenter.onDrawPoint(Pair(3, 4))
    presenter.onFinishDrawing()
    presenter.onStartDrawing()
    presenter.onDrawPoint(Pair(7, 8))
    presenter.onDrawPoint(Pair(8, 9))
    presenter.onFinishDrawing()

    presenter.deleteAll()

    assertThat(view.polygons).isEmpty()
  }

  @Test
  fun `undo and delete buttons are visible when there's at least one polygon`() {
    presenter.onStartDrawing()
    presenter.onDrawPoint(Pair(1, 2))
    presenter.onDrawPoint(Pair(3, 4))

    presenter.onFinishDrawing()

    assertThat(view.undoAndDeleteButtonVisible).isTrue()
  }


  @Test
  fun `hide undo and delete buttons when delete all`() {
    view.undoAndDeleteButtonVisible = true
    presenter.onStartDrawing()
    presenter.onDrawPoint(Pair(1, 2))
    presenter.onDrawPoint(Pair(3, 4))
    presenter.onFinishDrawing()

    presenter.deleteAll()

    assertThat(view.undoAndDeleteButtonVisible).isFalse()
  }

  @Test
  fun `hide undo and delete buttons when there's no polygon`() {
    view.undoAndDeleteButtonVisible = true
    presenter.onStartDrawing()
    presenter.onDrawPoint(Pair(1, 2))
    presenter.onDrawPoint(Pair(3, 4))
    presenter.onFinishDrawing()

    presenter.undo()

    assertThat(view.undoAndDeleteButtonVisible).isFalse()
  }

  @Test
  fun `draw polygon if provided during binding`() {
    val poly = Surface(mutableSetOf(LatLng(1.0, 2.0), LatLng(2.0, 3.0)))

    presenter.bind(view, listOf(poly))

    assertThat(view.polygons).isEqualTo(mutableListOf(poly))
  }

  @Test
  fun `enable undo and delete buttons if binding a polygon`() {
    val poly = Surface(mutableSetOf(LatLng(1.0, 2.0), LatLng(2.0, 3.0)))

    presenter.bind(view, listOf(poly))

    assertThat(view.undoAndDeleteButtonVisible).isTrue()
  }

  private class SimplePointToLatLngConverter : PointToLatLngConverter {
    override fun toLatLng(point: Pair<Int, Int>): LatLng {
      return LatLng(point.first.toDouble(), point.second.toDouble())
    }
  }

  private class TestView : DrawOnMapView {
    var edit = false
    var undoAndDeleteButtonVisible = false
    var polygons: List<Surface> = listOf()


    override fun setEditMode(isEditMode: Boolean) {
      edit = isEditMode
    }

    override fun setUndoAndDeleteButtonVisibility(isVisible: Boolean) {
      undoAndDeleteButtonVisible = isVisible
    }

    override fun drawPolygonsOnMap(polygons: List<Surface>) {
      this.polygons = polygons
    }

    override fun drawPolylineOnMap(surface: Surface) {
      polygons = listOf(surface)
    }
  }

  private class SimpleTimeWrapper(var currentTime: Long) : TimeWrapper {
    override fun currentTimeMillis(): Long {
      return currentTime
    }
  }
}
