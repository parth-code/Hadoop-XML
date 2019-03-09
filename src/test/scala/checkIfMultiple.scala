import org.scalatest.FlatSpec

class checkIfMultiple extends FlatSpec{
  "Multiple items" must "be generated if multiple items are present" in {
    val a = "a,b,2"
    val b = "a,b\na,b\n"
    val num = a.split(",").toList.last.toInt
    val s = a.split(",").toList.dropRight(1).mkString(",")
    val s1 = s + "\n"
    val st = new StringBuilder
    (0 to num - 1).foreach(x => st.append(s1))
    assert(st.toString() == b)
  }
}
