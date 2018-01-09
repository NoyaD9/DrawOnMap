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
    presenter.bind(view)
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
    timeWrapper.currentTime = 10
    presenter.onStartDrawing()
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

  private class SimplePointToLatLngConverter : PointToLatLngConverter {
    override fun toLatLng(point: Pair<Int, Int>): LatLng {
      return LatLng(point.first.toDouble(), point.second.toDouble())
    }
  }

  private class TestView : DrawOnMapView {
    var edit = false
    var polygons: MutableList<Surface> = mutableListOf()

    override fun setEditMode(editMode: Boolean) {
      edit = editMode
    }

    override fun drawPolygonsOnMap(polygons: List<Surface>) {
      this.polygons = polygons.toMutableList()
    }

    override fun drawPolylineOnMap(polygons: List<Surface>) {
      this.polygons = polygons.toMutableList()
    }
  }

  private class SimpleTimeWrapper(var currentTime: Long) : TimeWrapper {
    override fun currentTimeMillis(): Long {
      return currentTime
    }
  }
}
