package noya.it.drawonmap

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageButton
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.PolylineOptions
import noya.it.drawonmap.converter.MapProjectionPointConverter
import noya.it.drawonmap.model.Surface
import noya.it.drawonmap.util.SystemTime


class DrawOnMapActivity : AppCompatActivity(), OnMapReadyCallback, DrawOnMapView {

  private lateinit var map: GoogleMap
  private lateinit var captorView: PolygonCaptorView
  private lateinit var editButton: ImageButton
  private lateinit var undoButton: View
  private lateinit var presenter: DrawOnMapPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_maps)
    val mapFragment = supportFragmentManager
        .findFragmentById(R.id.map) as SupportMapFragment
    mapFragment.getMapAsync(this)
    captorView = findViewById(R.id.polygonCaptor)
    initEditButton()
    initUndoButton()
  }

  private fun initEditButton() {
    editButton = findViewById(R.id.button_edit)
    editButton.setOnClickListener {
      presenter.toggleEditMode()
    }
  }

  private fun initUndoButton() {
    undoButton = findViewById(R.id.button_delete)
    undoButton.setOnClickListener {
      presenter.undo()
    }
  }

  override fun onMapReady(googleMap: GoogleMap) {
    map = googleMap
    presenter = DrawOnMapPresenter(MapProjectionPointConverter(map), SystemTime())
    presenter.bind(this)
    captorView.listener = presenter
  }

  override fun setEditMode(editMode: Boolean) {
    captorView.isEditMode = editMode
    changeEditButtonColor(editMode)
    undoButton.visibility = if (editMode) View.VISIBLE else View.GONE
  }

  private fun changeEditButtonColor(editMode: Boolean) {
    editButton.setImageResource(if (editMode) R.drawable.ic_check else R.drawable.ic_edit)
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
}
