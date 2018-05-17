package com.firelotto.stats.utils

object JsonHelper {
  import org.json4s.DefaultFormats
  import org.json4s.native.Serialization

  implicit val formats = DefaultFormats

  def deserializeFromJson[T](value: String)(implicit mf: Manifest[T]): T = {
    Serialization.read[T](value)
  }

  def serializeToJson[T <: AnyRef](value: T): String = {
    Serialization.writePretty[T](value)
  }

  def saveToFile(filePath: String, value: String): Unit = {
    try {
      val pw = new java.io.PrintWriter(filePath)
      pw.print(value)
      pw.flush()
      pw.close()
    } catch {
      case ex: Exception ⇒ ex.printStackTrace()
    }
  }

  def readFile(filePath: java.io.File, codec: String = "UTF-8"): String = {
    scala.io.Source.fromFile(filePath, codec).getLines().mkString("")
  }

}
