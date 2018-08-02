package noya.it.drawonmap

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

internal class PolygonCaptorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

  var isEditMode = false
  var listener: Listener? = null

  override fun onFinishInflate() {
    super.onFinishInflate()
    setOnTouchListener { _, event ->
      if (isEditMode) {
        val x = event.x.toInt()
        val y = event.y.toInt()

        when (event.action) {
          MotionEvent.ACTION_DOWN -> {
            listener?.onStartDrawing()
            true
          }

          MotionEvent.ACTION_MOVE -> {
            listener?.onDrawPoint(Pair(x, y))
            true
          }
          MotionEvent.ACTION_UP -> {
            listener?.onFinishDrawing()
            true
          }
          else -> true
        }
      } else {
        false
      }
    }
  }

  interface Listener {
    fun onStartDrawing()
    fun onFinishDrawing()
    fun onDrawPoint(point: Pair<Int, Int>)
  }
}
