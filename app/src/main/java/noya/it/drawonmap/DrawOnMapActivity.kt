package noya.it.drawonmap

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.PolylineOptions
import noya.it.drawonmap.converter.MapProjectionPointConverter
import noya.it.drawonmap.model.Surface
import noya.it.drawonmap.util.SystemTime


class DrawOnMapActivity : AppCompatActivity(), DrawOnMapView {

  private lateinit var map: GoogleMap
  private lateinit var captorView: PolygonCaptorView
  private lateinit var addPathButton: View
  private lateinit var addPathPlus: View
  private lateinit var removePathButton: View
  private lateinit var removePathMinus: View
  private lateinit var removePathLayout: View
  private lateinit var deleteButton: View
  private lateinit var presenter: DrawOnMapPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_maps)
    val mapFragment = supportFragmentManager
        .findFragmentById(R.id.map) as SupportMapFragment
    mapFragment.getMapAsync { map ->
      this.map = map
      presenter = DrawOnMapPresenter(MapProjectionPointConverter(map), SystemTime())
      val retainedPolygons = savedInstanceState?.getParcelableArrayList<Surface>(BUNDLE_PARCELABLE)
      presenter.bind(this, retainedPolygons)
      captorView.listener = presenter
    }
    captorView = findViewById(R.id.polygonCaptor)
    initAddPathButton()
    initRemovePathButton()
    initDeleteButton()
  }


  private fun initAddPathButton() {
    addPathPlus = findViewById(R.id.ic_plus)
    addPathButton = findViewById(R.id.button_addPath)
    addPathButton.setOnClickListener {
      presenter.addPath()
    }
  }

  private fun initRemovePathButton() {
    removePathLayout = findViewById(R.id.layout_removePath)
    removePathMinus = findViewById(R.id.ic_minus)
    removePathButton = findViewById(R.id.button_removePath)
    removePathButton.setOnClickListener {
      presenter.removePath()
    }
  }

  private fun initDeleteButton() {
    deleteButton = findViewById(R.id.button_delete)
    deleteButton.setOnClickListener {
      presenter.deleteAll()
    }

  }

  override fun onSaveInstanceState(outState: Bundle) {
    if (::presenter.isInitialized) outState.putParcelableArrayList(BUNDLE_PARCELABLE, ArrayList(presenter.copyOfPolygons()))
    super.onSaveInstanceState(outState)
  }

  override fun setEditMode(isEditMode: Boolean) {
    captorView.isEditMode = isEditMode
  }

  override fun setEditModeButtonsVisibility(isVisible: Boolean) {
    removePathLayout.visibility = if (isVisible) View.VISIBLE else View.GONE
    deleteButton.visibility = if (isVisible) View.VISIBLE else View.GONE
  }

  override fun drawPolygonsOnMap(polygons: List<Surface>) {
    map.clear()
    polygons.forEach {
      val polygonOptions = PolygonOptions()
          .strokeColor(R.color.colorPrimary)
          .strokeWidth(4f)
          .fillColor(R.color.colorPolygonShape)
      it.outline.forEach {
        polygonOptions.add(it)
      }
      it.holes.forEach {
        if (it.isNotEmpty()) polygonOptions.addHole(it)
      }
      map.addPolygon(polygonOptions)
    }
  }

  override fun drawPolylineOnMap(surface: Surface) {
    val polylineOptions = PolylineOptions()
        .color(R.color.colorPrimary)
        .width(4f)
    surface.outline.forEach {
      polylineOptions.add(it)
    }
    map.addPolyline(polylineOptions)
  }

  companion object {
    const val BUNDLE_PARCELABLE = "BUNDLE_PARCELABLE"
  }
}
