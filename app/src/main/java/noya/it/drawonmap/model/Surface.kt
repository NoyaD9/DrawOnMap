package noya.it.drawonmap.model

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng

data class Surface(internal val outline: MutableCollection<LatLng> = mutableSetOf<LatLng>()) : Parcelable {

  fun addPoint(point: LatLng) {
    outline.add(point)
  }

  override fun writeToParcel(dest: Parcel, flags: Int) {
    dest.writeTypedList(outline.toList())
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<Surface> {
    override fun newArray(size: Int) = arrayOfNulls<Surface>(size)

    override fun createFromParcel(source: Parcel): Surface {
      val outline = mutableListOf<LatLng>()
      source.readTypedList(outline, LatLng.CREATOR)
      return Surface(outline.toMutableSet())
    }
  }
}
