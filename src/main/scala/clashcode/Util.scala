package clashcode

object TopName {

  def name(map: Map[String, Int]): String = {
    def sorting(a: (String, Int), b: (String, Int)): Boolean = a._2 > b._2
    val hold = map.values.max.toDouble * 0.5
    val filtered = (map.toList).filter { case (k, v) => v > hold }
    val sorted = filtered.sortWith(sorting)
    sorted.map { case (k, v) => s"[$k]" }.mkString("")
  }

}

object UniqueID {

  import java.io.File
  import java.io.ObjectOutputStream
  import java.io.ObjectInputStream
  import java.io.FileInputStream
  import java.io.FileOutputStream

  def provideID: Int = {

    def using[A <: { def close(): Unit }, B](param: A)(f: A => B): B =
      try { f(param) } finally { param.close() }

    val home = new File(System.getProperty("user.home"))
    val idfile = new File(home, ".idfile")

    def writeIdfile(nextId: Int): Unit = {
      using(new FileOutputStream(idfile)) { fout =>
        val oout = new ObjectOutputStream(fout)
        oout.writeObject(nextId)
      }
    }

    def readIdfile: Int = {
      using(new FileInputStream(idfile)) { fin =>
        val oin = new ObjectInputStream(fin)
        oin.readObject.asInstanceOf[Int];
      }
    }
    
    val re = if (!idfile.exists()) 0
    else readIdfile
    writeIdfile(re + 1)
    re
  }

}
