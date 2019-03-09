import org.scalatest.FlatSpec


class ReduceTest extends FlatSpec {
  "reduce" must "return sum" in {
    val map = Map("a"-> 1,"b" -> 2)

    var sum = map.foldLeft(0)(_ + _._2)
    assert(sum == 3)
  }
}
