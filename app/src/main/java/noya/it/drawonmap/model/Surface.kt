package noya.it.drawonmap.model

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng

data class Surface(
    internal val outline: MutableList<LatLng> = mutableListOf(),
    internal val holes: MutableList<List<LatLng>> = mutableListOf(listOf())) : Parcelable {

  fun addPoint(point: LatLng) {
    outline.add(point)
  }

  override fun writeToParcel(dest: Parcel, flags: Int) {
    dest.writeTypedList(outline.toList())
    dest.writeInt(holes.size)
    holes.forEach {
      dest.writeTypedList(it.toList())
    }
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<Surface> {
    override fun newArray(size: Int) = arrayOfNulls<Surface>(size)

    override fun createFromParcel(source: Parcel): Surface {
      val outline = mutableListOf<LatLng>()
      source.readTypedList(outline, LatLng.CREATOR)
      val holesSize = source.readInt()
      val holes: MutableList<List<LatLng>> = mutableListOf()
      for (i in 0 until holesSize) {
        val hole = mutableListOf<LatLng>()
        source.readTypedList(hole, LatLng.CREATOR)
        holes.add(hole)
      }
      return Surface(outline, holes)
    }
  }
}
