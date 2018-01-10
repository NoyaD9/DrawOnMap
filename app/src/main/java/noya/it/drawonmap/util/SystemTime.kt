package noya.it.drawonmap.util

internal class SystemTime : TimeWrapper {
  override fun currentTimeMillis(): Long {
    return System.currentTimeMillis()
  }
}
