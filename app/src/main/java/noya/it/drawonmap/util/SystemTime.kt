package noya.it.drawonmap.util

class SystemTime : TimeWrapper {
  override fun currentTimeMillis(): Long {
    return System.currentTimeMillis()
  }
}
